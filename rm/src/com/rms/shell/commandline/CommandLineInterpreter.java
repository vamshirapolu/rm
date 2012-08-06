// RMShell
// Copyright (C) 2000 Jack A. Orenstein
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of
// the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.
// 
// Jack A. Orenstein  jao@mediaone.net

package com.rms.shell.commandline;

import java.util.*;

import com.rms.shell.*;
import com.rms.shell.util.*;

import java_cup.runtime.*;

// About the threading model:
// 
// There are five classes involved in the actual execution of threads,
// (i.e., you'd see them in a stack trace between com.maxis.rms.shell.RMShell
// and a command class such as com.rms.shell.command.ls):
// 1) commandline.Command
// 2) commandline.CommandLine
// 3) commandline.CommandLineInterpreter
// 4) commandline.JobThread
// 5) commandline.Pipeline
// 
// Command, Pipeline, and CommandLine are created during the parse
// of a command line, and the reflect the structure of RMShell syntax.
// E.g. consider this command line:
// 
//     ls ; javac *.java | save errs.txt
// 
// The entire command line is represented by a CommandLine. The CommandLine
// has two Pipelines -- ls is one, javac *.java | save errs.txt is the other.
// The first pipeline has one Command (ls), the second has two (javac and
// save).
// 
// The execution of the entire CommandLine is initiated by 
// CommandLineInterpreter. Each Command is a thread (an instance
// of PooledThread) so that they can be run in parallel. I.e.,
// they really do form a pipeline.
// 
// CommandLineInterpreter is also a PooledThread. By putting each
// CommandLineInterpreter in its own thread, it is possible to support
// the parallel execution of multiple background threads (or "jobs").
// 
// The main thread is responsible for reading the command line and
// firing off execution of the command line. If the command
// is to run in the foreground, the next prompt is delayed until
// execution completes. Otherwise, there is no such delay.
// 
// JobThread is needed for proper handling of background jobs.
// The main thread always starts a JobThread which in turn
// starts a CommandLineInterpreter. JobThread.execute blocks
// until the CommandLineInterpreter it started completes.
// It then handles printing of the error message, if any.
// JobThread is needed because the main thread can't wait
// for background threads to complete, (otherwise they
// wouldn't be background threads).

public class CommandLineInterpreter extends PooledThread
{
    // PooledThread interface

    public void preExecute()
        throws Exception
    {
        super.preExecute();
        _id = _id_counter++;
        parse();
    }

    public void execute()
        throws Exception
    {
        Jobs jobs = Jobs.only();
        jobs.add(this);
        _command_line.execute();
        Vector exceptions = _command_line.exceptions();
        jobs.remove(this);
        if (exceptions.size() > 0)
            throw new RMShellException(exceptions);
    }


    // CommandLineInterpreter interface

    public static CommandLineInterpreter create(String text)
    {
        CommandLineInterpreter interpreter =
            (CommandLineInterpreter) _interpreters.take();
        Assertion.check(interpreter.isAlive());
        interpreter.prepare(text);
        return interpreter;
    }

    public int id()
    {
        return _id;
    }

    public String commandLine()
    {
        return _text;
    }

    public boolean backgroundJob()
    {
        return _background_job;
    }

    public void kill()
    {
        _command_line.kill();
    }


    // For use by this class

    private void parse()
        throws Exception
    {
        // Not sure the parser is thread-safe. 
        // Synchronize on this class just to be safe.
        synchronized (getClass())
        {
            parser p = new parser
                (new JavaCUPScanner(new Lexer(_text)));
            Symbol symbol = p.parse();
            _command_line = (CommandLine) symbol.value;
            if (_command_line.containsCommandReferences())
            {
                _text = _command_line.expandCommandReferences();
                p = new parser
                    (new JavaCUPScanner(new Lexer(_text)));
                symbol = p.parse();
                _command_line = (CommandLine) symbol.value;
            }
            History.only().record(_text);
            _background_job = _command_line.backgroundJob();
        }
    }

    private void prepare(String text)
    {
        _text = text;
        description("CommandLineInterpreter: "+text);
    }

    private static Pool _interpreters = 
        new Pool("CommandLineInterpreters",
                 new Pool.ElementFactory()
                 {
                     public Pool.Element create()
                     {
                         CommandLineInterpreter interpreter =
                             new CommandLineInterpreter();
                         interpreter.start();
                         return interpreter;
                     }
                 });

    private static int _id_counter = 0;

    private int _id;
    private String _text;
    private CommandLine _command_line;
    private boolean _background_job;
}

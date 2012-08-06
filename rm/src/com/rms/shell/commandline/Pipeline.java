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

public class Pipeline
{
    public Pipeline addCommand(AbstractCommand command)
    {
        _commands.addElement(command);
        return this;
    }

    public void redirectIn(CommandElement source)
    {
        Vector v = source.expand();
        if (v.size() != 1)
            System.err.println
                ("Can't redirect input from more "+
                 "than one file. "+
                 source.token()+" expands to "+v);
        String file_name = (String) v.firstElement();

        // Create 'cat' command
        Command cat = Command.create();
        cat.addElement(UnquotedString.create("cat"));
        cat.addElement(source);

        // Add to the beginning of the pipeline
        _commands.insertElementAt(cat, 0);
    }

    public void redirectOut
    (CommandElement destination, String redirection_type)
    {
        Vector v = destination.expand();
        if (v.size() != 1)
            System.err.println
                ("Can't redirect output to more "+
                 "than one file. "+
                 destination.token()+" expands to "+v);
        String file_name = (String) v.firstElement();

        // Create 'save' command
        Command save = Command.create();
        save.addElement(UnquotedString.create("save"));
        save.addElement(destination);
        if (redirection_type.equals(">>"))
            save.addElement
                (UnquotedString.create
                 (Util.systemProperty(RMShell.RMSHELL_FLAG)+"a"));

        // Add to the end of the pipeline
        _commands.addElement(save);
    }

    public void execute()
        throws Exception
    {
        // Set up threads for command execution
        boolean prepared = true;
        for (Enumeration command_scan = _commands.elements();
             command_scan.hasMoreElements();)
        {
            Command command =
                (Command) command_scan.nextElement();
            try
            { command.prepareToExecute(); }
            catch (Throwable t)
            {
                _exceptions.addElement(t);
                prepared = false;
            }
        }

        if (prepared)
        {
            // Pipe output of one command to input of next
            Command previous_command = null;
            for (Enumeration command_scan = _commands.elements();
                 command_scan.hasMoreElements();)
            {
                Command command =
                    (Command) command_scan.nextElement();
                if (previous_command != null)
                    Command.connectOutputToInput
                        (previous_command, command);
                previous_command = command;
            }
    
            // Run everything and collect outcomes
            Vector outcomes = new Vector();
            for (Enumeration command_scan = _commands.elements();
                 command_scan.hasMoreElements();)
            {
                Command command =
                    (Command) command_scan.nextElement();
                outcomes.addElement(command.startWork());
            }
    
            // Gather exceptions. terminationReason blocks until
            // execution is complete.
            for (Enumeration outcome_scan = outcomes.elements();
                 outcome_scan.hasMoreElements();)
            {
                PooledThread.Outcome outcome =
                    (PooledThread.Outcome) outcome_scan.nextElement();
                Throwable exception = outcome.terminationReason();
                if (exception != null)
                    _exceptions.addElement(exception);
            }
        }
    }

    public void kill()
    {
        for (Enumeration command_scan = _commands.elements();
             command_scan.hasMoreElements();)
        {
            Command command =
                (Command) command_scan.nextElement();
            command.interrupt();
        }
    }

    public Vector exceptions()
    {
        return _exceptions;
    }
    
    boolean containsCommandReferences()
    {
        for (Enumeration command_scan = _commands.elements();
             command_scan.hasMoreElements();)
        {
            if (command_scan.nextElement() instanceof
                CommandReference)
                return true;
        }
        return false;
    }

    String expandCommandReferences()
    {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (Enumeration command_scan = _commands.elements();
             command_scan.hasMoreElements();)
        {
            if (first)
                first = false;
            else
                buffer.append(" ^ ");
            AbstractCommand command =
                (AbstractCommand) command_scan.nextElement();
            buffer.append(command.expandCommandReference());
        }
        return buffer.toString();
    }

    private Vector _commands = new Vector();
    private Vector _exceptions = new Vector();
}

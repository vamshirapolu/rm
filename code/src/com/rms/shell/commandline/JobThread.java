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

import com.rms.shell.*;
import com.rms.shell.util.*;

public class JobThread extends PooledThread
{
    // PooledThread interface

    public void preExecute()
        throws Exception
    {
        _job_outcome = _command_line_interpreter.startWork();
    }

    public void execute()
        throws Exception
    {
        Throwable termination_reason = _job_outcome.terminationReason();
        if (termination_reason instanceof RMShellException)
            ((RMShellException)termination_reason).describe(System.out);
        else
        {
            System.out.println("Unexpected "+termination_reason+": "+
                               termination_reason.getMessage());
            termination_reason.printStackTrace(System.out);
        }
        _command_line_interpreter = null;
        _job_outcome = null;
    }


    // JobThread interface

    public static JobThread create(String command_line)
    {
        JobThread job_thread = (JobThread) _job_threads.take();
        Assertion.check(job_thread.isAlive());
        job_thread.prepare(command_line);
        return job_thread;
    }

    public void waitIfForegroundJob()
    {
        if (!_command_line_interpreter.backgroundJob())
            _job_outcome.waitForCompletion();
    }


    // For use by this class

    private void prepare(String command_line)
    {
        _command_line_interpreter =
            CommandLineInterpreter.create(command_line);
        description("JobThread: "+command_line);
    }



    // Representation

    private static Pool _job_threads = 
        new Pool("JobThreads",
                 new Pool.ElementFactory()
                 {
                     public Pool.Element create()
                     {
                         JobThread job_thread = new JobThread();
                         job_thread.start();
                         return job_thread;
                     }
                 });

    private CommandLineInterpreter _command_line_interpreter;
    private PooledThread.Outcome _job_outcome;
}

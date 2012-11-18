
package com.rms.shell;

import java.io.*;
import java.util.*;

import com.rms.shell.commandline.*;
import com.rms.shell.util.*;

public class Jobs
{
    public static Jobs only()
    {
        return _only;
    }

    public void add(CommandLineInterpreter job)
    {
        _jobs.addElement(job);
    }

    public CommandLineInterpreter job(int id)
    {
        for (Enumeration job_scan = _jobs.elements();
             job_scan.hasMoreElements();)
        {
            CommandLineInterpreter job =
                (CommandLineInterpreter) job_scan.nextElement();
            if (job.id() == id)
                return job;
        }
        return null;
    }

    public void remove(CommandLineInterpreter job)
    {
        if (job != null)
            _jobs.removeElement(job);
    }

    public void print(PrintStream out)
    {
        for (Enumeration job_scan = _jobs.elements();
             job_scan.hasMoreElements();)
        {
            CommandLineInterpreter job =
                (CommandLineInterpreter) job_scan.nextElement();
            out.print(job.id());
            out.print(": ");
            out.println(job.commandLine());
        }
    }

    public void kill(int[] job_ids)
    {
        for (int i = 0; i < job_ids.length; i++)
        {
            int job_id = job_ids[i];
            CommandLineInterpreter job = find(job_id);
            if (job != null)
            {
                remove(job);
                job.kill();
            }
        }
    }

    private CommandLineInterpreter find(int job_id)
    {
        for (Enumeration job_scan = _jobs.elements();
             job_scan.hasMoreElements();)
        {
            CommandLineInterpreter job =
                (CommandLineInterpreter) job_scan.nextElement();
            if (job.id() == job_id)
                return job;
        }
        return null;
    }

    private static Jobs _only = new Jobs();

    private Vector _jobs = new Vector();
}

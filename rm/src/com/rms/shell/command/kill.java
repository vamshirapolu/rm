package com.rms.shell.command;

import com.rms.shell.*;

public class kill extends Command
{
    public void execute(String[] args)
         throws Exception
    {
        int[] job_ids = new int[args.length];
        for (int i = 0; i < args.length; i++)
            job_ids[i] = Integer.parseInt(args[i]);
        killJobs(job_ids);
    }

    public void usage()
    {
        out().println("kill job ...");
        out().println("    Terminates execution of the specified jobs.\n"+
                      "    The job numbers are obtained by running the\n"+
                      "    jobs command.");
    }
}

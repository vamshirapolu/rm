
package com.rms.shell.command;

import java.io.*;

import com.rms.shell.*;

public class jobs extends Command
{
    public void execute(String[] args)
    {
        printJobs();
    }

    public void usage()
    {
        out().println("jobs");
        out().println("    Prints currently running background jobs.");
    }
}

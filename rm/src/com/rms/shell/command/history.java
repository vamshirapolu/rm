
package com.rms.shell.command;

import java.io.*;

import com.rms.shell.*;

public class history extends Command
{
    public void execute(String[] args)
    {
        if (args.length == 0)
            printHistory(0);  // prints everything
        else
            printHistory(Integer.parseInt(args[0]));
    }

    public void usage()
    {
        out().println("history [n]");
        out().println("    Prints the last n commands. If n is omitted,\n"+
                      "    all recorded commands are printed. In both cases,\n"+
                      "    the number of commands printed is limited by the\n"+
                      "    value of com.maxis.rms.shell.history_size.");
    }
}

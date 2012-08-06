
package com.rms.shell.command;

import java.io.*;

import com.rms.shell.*;

public class echo extends Command
{
    public void execute(String[] args)
        throws IOException
    {
        if (args.length == 0)
            echo_input();
        else
            echo_command_line_args(args);
    }

    public void usage()
    {
        out().println("echo [expression ...]");
        out().println("    With no arguments, echo copies standard\n"+
                      "    input to System.out and standard output.\n"+
                      "    This is useful for examining data as it\n"+
                      "    flows from one command to another.\n"+
                      "    If command line arguments are specified,\n"+
                      "    echo simply evaluates and prints the\n"+
                      "    expressions.");
    }

    private void echo_input()
         throws IOException
    {
        // Print input and pass it on
        BufferedReader in = 
            new BufferedReader(new InputStreamReader(in()));
        String line;
        while ((line = in.readLine()) != null)
        {
            System.out.println(line);
            out().println(line);
        }
    }

    private void echo_command_line_args(String[] args)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (i > 0)
                out().print(' ');
            out().print(args[i]);
        }
        out().println();
    }
}


package com.rms.shell.command;

import java.io.*;

import com.rms.shell.*;

public class pwd extends Command
{
    public void execute(String[] args)
    {
        out().println(property("com.maxis.rms.shell.dir"));
    }

    public void usage()
    {
        out().println("pwd");
        out().println("    Prints the current directory, (the \"working\n"+
                      "    directory\"), which is the value of com.maxis.rms.shell.dir.");
    }
}

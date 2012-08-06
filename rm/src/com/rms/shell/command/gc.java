
package com.rms.shell.command;

import java.io.*;

import com.rms.shell.*;
import com.rms.shell.commandline.Variable;

public class gc extends Command
{
    public void execute(String[] args)
    {
        System.gc();
    }

    public void usage()
    {
        out().println("gc");
        out().println("    Run the garbage collector.");
    }
}

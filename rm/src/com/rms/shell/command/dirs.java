
package com.rms.shell.command;

import java.io.*;

import com.rms.shell.*;

public class dirs extends Command
{
    public void execute(String[] args)
    {
        String dir_stack = property("com.maxis.rms.shell.dir_stack");
        if (dir_stack == null)
            out().println("Directory stack is empty.");
        else
            out().println(dir_stack);
    }

    public void usage()
    {
        out().println("dirs");
        out().println("    Prints the directory stack, starting with the\n"+
                      "    most recently visited directory. The stack is\n"+
                      "    recorded in the variable com.maxis.rms.shell.dir_stack.\n"+
                      "    The stack is modified using the commands pushd\n"+
                      "    and popd.");
    }
}

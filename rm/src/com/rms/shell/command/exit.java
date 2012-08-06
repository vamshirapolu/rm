
package com.rms.shell.command;

import com.rms.shell.*;

public class exit extends Command
{
    public void execute(String[] args)
    {
        throw new ReallyExit();
    }

    public void usage()
    {
        out().println("exit");
        out().println("    Exit RMShell immediately.");
    }

    //------------------------------------------------------------

    public static class ReallyExit extends RMShellException
    {
        public ReallyExit()
        {
            super("");
        }
    }
}

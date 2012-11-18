
package com.rms.shell.command;

import java.io.IOException;
import java.util.*;

import com.rms.shell.*;

public class cd extends Command
{
    public void execute(String[] args)
        throws IOException
    {
        if (args.length != 1)
            err().println("cd must have exactly one argument.");
        else
        {
            File dir = File.create(args[0]);
            if (!dir.exists())
                err().println("No such directory.");
            else if (!dir.isDirectory())
                err().println(dir.getPath()+" is not a directory.");
            else
            {
                String new_dir = dir.getCanonicalPath();
                property("com.maxis.rms.shell.dir", new_dir);
                out().println(new_dir);
            }
        }
    }

    public void usage()
    {
        out().println("cd directory");
        out().println("    Go to the specified directory. Modifies the value\n"+
                      "    of com.maxis.rms.shell.dir.");
    }
}

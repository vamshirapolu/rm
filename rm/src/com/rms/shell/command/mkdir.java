
package com.rms.shell.command;

import java.io.IOException;
import java.util.*;

import com.rms.shell.*;

public class mkdir extends Command
{
    public void execute(String[] args)
        throws IOException
    {
        if (args.length != 1)
            err().println("mkdir must have exactly one argument.");
        else
        {
            String path = args[0];
            File file = File.create(path);
            boolean success = file.mkdirs();
            if (success)
                out().println(file.getPath());
            else
                err().println("Unable to create directory "+path);
        }
    }

    public void usage()
    {
        out().println("mkdir directory");
        out().println("    Creates the specified directory.");
    }
}

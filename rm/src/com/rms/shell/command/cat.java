
package com.rms.shell.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.rms.shell.Command;
import com.rms.shell.File;

// Break ties

public class cat extends Command
{
    public void execute(String[] args)
        throws IOException, InterruptedException
    {
        Vector files = new Vector();
        for (int i = 0; i < args.length; i++)
        {
            File file = File.create(args[i]);
            if (file.exists() && !file.isDirectory())
                files.addElement(file);
            else
            {
                out().print(file.getPath());
                if (file.exists())
                    out().println(": directory");
                else
                    out().println(": does not exist");
            }
        }
        for (Enumeration file_scan = files.elements();
             file_scan.hasMoreElements();)
        {
            File file = (File) file_scan.nextElement();
            type(file);
        }
    }

    public void usage()
    {
        out().println("cat file ...");
        out().println
            ("    Prints the files specified on the command line with no\n"+
             "    header or separator.");
    }

    private void type(File file)
        throws IOException, InterruptedException
    {
        BufferedReader in =
            new BufferedReader(file.fileReader());
        String line;
        while ((line = in.readLine()) != null)
        {
            checkForInterruption();
            out().println(line);
        }
        in.close();
    }
}

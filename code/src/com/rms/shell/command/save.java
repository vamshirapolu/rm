
package com.rms.shell.command;

import java.io.FileOutputStream;
import java.io.IOException;

import com.rms.shell.Command;
import com.rms.shell.File;

// Break ties

public class save extends Command
{
    public void execute(String[] args)
        throws IOException
    {
        process_args(args);
        File file = File.create(args[0]);
        if (file.isDirectory())
            err().println(file.getPath()+" is a directory");
        else
        {
            FileOutputStream out = file.outputStream(_append);
            byte[] buffer = new byte[BUFFER_SIZE];
            int n_read;
            while ((n_read = in().read(buffer)) != -1)
                out.write(buffer, 0, n_read);
            out.close();
        }
    }

    public void usage()
    {
        out().println("save "+flag("a")+" file");
        out().println("    The input stream is saved in the specified file.\n"+
                      "    By default, the file is overwritten if it exists.\n"+
                      "    The a flag causes the input stream to be appended\n"+
                      "    instead. The save command is not normally needed\n"+
                      "    because file redirection (using > or >>) can be\n"+
                      "    used instead.");
    }

    private void process_args(String[] args)
    {
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            if (isFlag(arg))
            {
                if (arg.charAt(1) == 'a')
                    _append = true;
                else
                    err().println
                        ("Ignoring unrecognized flag for save: "+
                         arg);
            }
            else
            {
                if (_file_name == null)
                    _file_name = arg;
                else
                    err().println
                        ("Extra filename to save ignored: "+
                         arg+
                         ". Saving to "+_file_name+".");
            }
        }
    }

    private static final int BUFFER_SIZE = 1024;

    private String _file_name = null;
    private boolean _append = false;
}

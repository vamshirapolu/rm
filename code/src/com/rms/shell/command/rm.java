
package com.rms.shell.command;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.rms.shell.Command;
import com.rms.shell.File;
import com.rms.shell.RMShellException;

// Break ties

public class rm extends Command
{
    public void execute(String[] args)
        throws IOException
    {
        process_args(args);
        remove_files(_files_to_remove);
    }

    public void usage()
    {
        out().println("rm ["+flag("R")+"] file ...");
        out().println("    Removes the specified files. Directories are not\n"+
                      "    removed unless the directory is empty, or the R\n"+
                      "    (recursive) flag is specified.");
    }

    private void process_args(String[] args)
    {
        _files_to_remove = new Vector();
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            if (isFlag(arg))
                process_flag(arg);
            else
                _files_to_remove.addElement(File.create(arg));
        }
    }

    private void remove_files(Vector files)
        throws IOException
    {
        for (Enumeration file_scan = files.elements();
             file_scan.hasMoreElements();)
        {
            File file = (File) file_scan.nextElement();
            if (file.exists())
                remove_file(file);
        }
    }

    private void remove_file(File file)
        throws IOException
    {
        if (file.isDirectory())
        {
            String[] file_names = file.list();
            if (_recursive)
            {
                String directory_name = file.getCanonicalPath();
                Vector directory_contents = new Vector();
                for (int i = 0; i < file_names.length; i++)
                {
                    String file_name = file_names[i];
                    File file_in_directory =
                        File.create(directory_name, file_name);
                    directory_contents.addElement
                        (file_in_directory);
                }
                remove_files(directory_contents);
                file.delete();
            }
            else
            {
                if (file_names.length == 0)
                    file.delete();
                else
                    throw new RMShellException
                        (file.getCanonicalPath()+" is not "+
                         "empty. It cannot be deleted without "+
                         "the R (recursive) flag, or until "+
                         "it is empty.");
            }
        }
        else
            file.delete();
    }

    private void process_flag(String arg)
    {
        if (arg.charAt(1) == 'R')
            _recursive = true;
        else
            throw new RMShellException
                ("Unrecognized flag to rm: "+arg);
    }

    private Vector _files_to_remove;
    private boolean _recursive = false;
}


package com.rms.shell.command;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.rms.shell.Command;
import com.rms.shell.File;
import com.rms.shell.Path;
import com.rms.shell.RMShellException;
import com.rms.shell.util.Assertion;

// Break ties

public class cp extends Command
{
    public void execute(String[] args)
        throws IOException, InterruptedException
    {
        process_args(args);
        copy_files();
    }

    public void usage()
    {
        out().println("cp source ... destination");
        out().println("    Copies source files to destination.\n"+
                      "    - If there are multiple sources, the destination\n"+
                      "      must be a directory.\n"+
                      "    - Directories are not copied by default.\n"+
                      "    - Use the "+flag("R")+" flag to copy recursively.");
    }

    private void process_args(String[] args)
    {
        Vector source_names = new Vector();
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            if (isFlag(arg))
                process_flag(arg);
            else
                source_names.addElement(arg);
        }
        _destination_name =
            (String) source_names.lastElement();
        int n_sources = source_names.size() - 1;
        if (n_sources == 0)
             throw new RMShellException
                 ("The arguments to cp must include a "+
                  "destination file or directory, and at "+
                  "least one source file or directory.");
        _sources = new Vector(n_sources);
        for (int i = 0; i < n_sources; i++)
        {
            String source_name =
                (String) source_names.elementAt(i);
            _sources.addElement(File.create(source_name));
        }
    }

    private void copy_files()
        throws IOException, InterruptedException
    {
        File destination = File.create(_destination_name);
        boolean destination_exists = destination.exists();
        boolean destination_is_file =
            destination_exists &&
            destination.isFile();

        if (destination_is_file || !destination_exists)
        {
            // Source should be a single file.
            int n_sources = _sources.size();
            boolean ok = n_sources == 1;
            if (ok)
            {
                File source = (File)_sources.firstElement();
                if (source.isFile())
                    file_to_file(source, destination);
                else
                    ok = false;
            }
            if (!ok)
                throw new RMShellException
                    ("If the destination is a file or does "+
                     "not exist, then the source must be "+
                     "a single file.");
        }
        else
            to_directory(_sources, destination);
    }

    private void process_flag(String arg)
    {
        if (arg.charAt(1) == 'R')
            _recursive = true;
        else
            throw new RMShellException
                ("Unrecognized flag to cp: "+arg);
    }

    private void file_to_file(File in_file, File out_file)
        throws IOException, InterruptedException
    {
        FileInputStream in = in_file.inputStream();
        FileOutputStream out = out_file.outputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int n_read;
        while ((n_read = in.read(buffer)) != -1)
        {
            checkForInterruption();
            out.write(buffer, 0, n_read);
        }
        in.close();
        out.close();
    }

    private void to_directory
    (Vector sources, File destination_directory)
        throws IOException, InterruptedException
    {
        Assertion.check(destination_directory.exists());
        for (Enumeration source_scan = sources.elements();
             source_scan.hasMoreElements();)
        {
            File source = (File) source_scan.nextElement();
            if (source.isFile())
            {
                File destination = 
                    File.create(destination_directory.getPath(), 
                                source.getName());
                file_to_file(source, destination);
            }
            else if (_recursive)
            {
                String source_base = source.getParent();
                if (source_base == null)
                    source_base = ".";
                String destination_base = 
                     destination_directory.getCanonicalPath();
                directory_to_directory
                    (source_base,
                     destination_base,
                     source.getName());
            }
        }
    }

    private void directory_to_directory
    (String source_base_name,
     String destination_base_name,
     String directory_name)
        throws IOException, InterruptedException
    {
        // E.g.
        // source_base_name:        /foo/bar
        // destination_base_name:   /x/y
        // directory_name:     abc
        //
        // - Create /x/y/abc
        // - Copy /foo/bar/abc/* to /x/y/abc/*

        // Get source directory.
        String source_directory_name = 
            Path.concatenate(source_base_name, 
                             directory_name);
        File source_directory =
            File.create(source_directory_name);
        Assertion.check(source_directory.isDirectory());

        // Get destination directory, creating it if 
        // necessary.
        String destination_directory_name =
            Path.concatenate(destination_base_name, 
                             directory_name);
        File destination_directory =
            File.create(destination_directory_name);
        destination_directory.mkdirs();

        // Get files in source directory
        String[] source_file_names =
            source_directory.list();
        Vector source_files = new Vector();
        for (int i = 0; i < source_file_names.length; i++)
            source_files.addElement
                (File.create(source_directory_name,
                             source_file_names[i]));

        // Copy
        to_directory(source_files, destination_directory);
    }


    private static final int BUFFER_SIZE = 10000;

    private Vector _sources;
    private String _destination_name;
    private boolean _recursive = false;
}

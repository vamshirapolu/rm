
package com.rms.shell;

import java.io.*;
import java.util.*;

import com.rms.shell.util.*;

public abstract class File extends java.io.File
{
    // Object interface

    public String toString()
    {
        return getAbsolutePath();
    }

    public int hashCode()
    {
        return getAbsolutePath().hashCode();
    }


    // File interface

    public String getAbsolutePath()
    {
        return _path.absolutePath();
    }

    public String getCanonicalPath() 
        throws IOException
    {
        return _path.canonicalPath();
    }

    public String getPath() 
    {
        return _path.statedPath();
    }
    
    public boolean isAbsolute() 
    {
        return _path.absolute();
    }

    public String getParent()
    {
        String absolute_path = _path.absolutePath();
        String parent = null;
        int last_slash = absolute_path.lastIndexOf("/");
        if (last_slash >= 0)
            parent = absolute_path.substring(0, last_slash);
        return parent;
    }


    // Creation methods

    public static File create(String path)
    {
        return RMShell.os().createFile(path);
    }

    public static File create(String directory, String file_name)
    {
        return RMShell.os().createFile(directory, file_name);
    }


    // com.maxis.rms.shell.File interface

    public FileInputStream inputStream()
        throws IOException
    {
        String absolute_rmshell_path = _path.absolutePath();
        String absolute_os_path = Path.rmsShellToOs(absolute_rmshell_path);
        return new FileInputStream(absolute_os_path);
    }

    public FileOutputStream outputStream()
        throws IOException
    {
        return outputStream(false);
    }

    public FileOutputStream outputStream(boolean append)
        throws IOException
    {
        String absolute_rmshelll_path = _path.absolutePath();
        String absolute_os_path = Path.rmsShellToOs(absolute_rmshelll_path);
        return new FileOutputStream(absolute_os_path, append);
    }

    public FileReader fileReader()
        throws IOException
    {
        String absolute_rmshelll_path = _path.absolutePath();
        String absolute_os_path = Path.rmsShellToOs(absolute_rmshelll_path);
        return new FileReader(absolute_os_path);
    }



    // For use by subclasses

    protected File(String path)
    {
        // Why Path.create is called twice: The invocation to
        // super has to be the first statement in the constructor, 
        // and _path can't be assigned inside the super() invocation.
        super(Path.rmsShellToOs(Path.create(path).absolutePath()));
        _path = Path.create(path);
    }

    protected File(String directory, String file_name)
    {
        this(Path.concatenate(directory, file_name));
    }


    // Representation

    protected Path _path;
}

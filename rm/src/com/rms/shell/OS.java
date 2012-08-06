
package com.rms.shell;

import java.io.*;

import com.rms.shell.os.*;

public abstract class OS
{
    public static OS create()
    {
        String os_name = System.getProperty("os.name");
        OS os = null;
        if (os_name.toLowerCase().indexOf("windows") >= 0)
            os = new DOS();
        else if (os_name.toLowerCase().indexOf("epoc") >= 0)
            os = new EPOC();
        else
            os = new UNIX();
        return os;
    }

    public abstract boolean caseSensitive();

    public abstract String rmShellToOsPath(String rmshell_path);

    public abstract String osToRMShellPath(String os_path);

    public abstract String canonicalizePath(String path)
        throws IOException;

    public abstract com.rms.shell.File createFile(String path);

    public abstract com.rms.shell.File createFile(String directory, 
                                           String file_name);
}

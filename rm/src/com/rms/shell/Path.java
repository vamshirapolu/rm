
package com.rms.shell;

import java.io.IOException;

import com.rms.shell.util.*;

// Used by com.maxis.rms.shell.File to keep track of stated paths (i.e. from the
// command line) and absolute paths, parse paths, etc. Also 
// provides useful path functions for general use, (these methods
// are static).

public class Path
{
    // For general use

    public static boolean absolute(String path)
    {
        return path != null && path.charAt(0) == '/';
    }

    public static boolean relative(String path)
    {
        return !absolute(path);
    }

    public static String rmsShellToOs(String RMSHELL_path)
    {
        return RMShell.os().rmShellToOsPath(RMSHELL_path);
    }

    public static String osToRMSHELL(String os_path)
    {
        return RMShell.os().osToRMShellPath(os_path);
    }

    public static synchronized String concatenate
    (String prefix, String suffix) 
    {
        boolean no_prefix = prefix == null || prefix.trim().equals("");
        boolean no_suffix = suffix == null || suffix.trim().equals("");
        Assertion.check(!(no_prefix && no_suffix));
        _buffer.setLength(0);
        if (no_prefix)
            _buffer.append(suffix);
        else if (no_suffix)
            _buffer.append(prefix);
        else
        {
            _buffer.append(prefix);
            if (!prefix.endsWith("/"))
                _buffer.append('/');
            _buffer.append(suffix);
        }
        return _buffer.toString();
    }

    public static String simplify(String path)
    {
        // Get rid of /.. and /. in path. Do /.. first, since /.
        // is a prefix.
        String simplified_path = path;
        do
        {
            path = simplified_path;
            simplified_path = simplify_slash_dot_dot(path);
        }
        while (path != simplified_path);
        do
        {
            path = simplified_path;
            simplified_path = simplify_slash_dot(path);
        }
        while (path != simplified_path);
        if (simplified_path.length() == 0)
            simplified_path = "/";
        else if (simplified_path.endsWith("/") && 
                 simplified_path.length() > 1)
            simplified_path = 
                simplified_path.substring(0, path.length() - 1);
        return simplified_path;
    }

    public static Path create(String path)
    {
        return new Path(path);
    }

    public static Path create(String directory_name, String file_name)
    {
        return new Path(concatenate(directory_name, file_name));
    }

    public String absolutePath()
    {
        return simplify(concatenate(_absolute_directory, _file_name));
    }

    public String canonicalPath()
        throws IOException
    {
        return RMShell.os().canonicalizePath
            (concatenate(_absolute_directory, _file_name));
    }

    public String statedPath()
    {
        return concatenate(_stated_directory, _file_name);
    }
    
    public boolean absolute()
    {
        return absolute(_stated_directory);
    }

    private Path(String path)
    {
        int last_slash = path.lastIndexOf("/");
        if (last_slash < 0)
        {
            _stated_directory = null;
            _absolute_directory =
                System.getProperty(RMShell.RMSHELL_DIR);
            _file_name = path;
        }
        else if (last_slash == 0)
        {
            _stated_directory = "/";
            _absolute_directory = "/";
            _file_name = path.substring(1);
        }
        else
        {
            _stated_directory = path.substring(0, last_slash);
            _file_name = path.substring(last_slash + 1);
            _absolute_directory =
                absolute(path)
                ? _stated_directory
                : concatenate(System.getProperty(RMShell.RMSHELL_DIR),
                              _stated_directory);
        }
    }

    private static String simplify_slash_dot(String path)
    {
        // Caller assumes that input is identical to output
        // iff the string cannot be simplified.
        int slash_dot;
        while ((slash_dot = path.indexOf("/./")) != -1)
            path = 
                path.substring(0, slash_dot) + 
                path.substring(slash_dot + 2);
        if (path.endsWith("/."))
            path = path.substring(0, path.length() - 2);
        return path;
    }

    private static String simplify_slash_dot_dot(String path)
    {
        // Caller assumes that input is identical to output
        // iff the string cannot be simplified.
        int slash_dot_dot;
        while ((slash_dot_dot = path.indexOf("/../")) != -1)
        {
            int previous_slash =
                path.lastIndexOf("/", slash_dot_dot - 1);
            Assertion.check(previous_slash != -1);
            path = 
                path.substring(0, previous_slash) + 
                path.substring(slash_dot_dot + 3);
        }
        if (path.endsWith("/.."))
        {
            int previous_slash = 
                path.lastIndexOf("/", path.lastIndexOf("/..") - 1);
            Assertion.check(previous_slash != -1);
            path = path.substring(0, previous_slash);
        }
        return path;
    }

    protected static StringBuffer _buffer = new StringBuffer();

    protected String _stated_directory;
    protected String _absolute_directory;
    protected String _file_name;
}

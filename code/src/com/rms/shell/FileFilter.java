
package com.rms.shell;

import java.io.*;

import com.rms.shell.util.*;

public class FileFilter implements FilenameFilter
{
    public String toString()
    {
        return _pattern.toString();
    }

    public FileFilter(String filter)
    {
        int slash = filter.lastIndexOf("/");
        if (slash >= 0)
            filter = filter.substring(slash + 1);
        _pattern = GlobPattern.create(filter);
    }

    public boolean accept(java.io.File dir, String file_name)
    {
        return _pattern.match(file_name);
    }

    public boolean hasWildcards()
    {
        return _pattern.hasWildcards();
    }

    private GlobPattern _pattern;
}

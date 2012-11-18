package com.rms.shell;

import java.io.IOException;
import java.util.*;

import com.rms.shell.File;
import com.rms.shell.util.*;

public abstract class FileSelector
{
    public static FileSelector create(String path)
    {
        // path is a sequence of glob patterns separated 
        // by slashes. E.g. "[a-z]*/*/../foo/*.java".
        FileSelector first = null;
        FileSelector last = null;
        boolean any_wildcards = false;
        for (StringTokenizer path_scanner =
                 new StringTokenizer(path, "/");
             path_scanner.hasMoreTokens();)
        {
            String path_element = path_scanner.nextToken();
            FileSelector selector;
            if (path_element.equals("."))
                selector = new DotSelector();
            else if (path_element.equals(".."))
                selector = new DotDotSelector();
            else
            {
                PatternSelector pattern_selector =
                    new PatternSelector(path_element);
                if (first == null && path.startsWith("/"))
                    pattern_selector.markRoot();
                any_wildcards = 
                    any_wildcards || 
                    pattern_selector.hasWildcards();
                selector = pattern_selector;
            }
            if (first == null)
                first = selector;
            else
                last._next = selector;
            last = selector;
        }
        if (first == null)
        {
            PatternSelector pattern_selector =
                new PatternSelector("/");
            pattern_selector.markRoot();
            first = pattern_selector;
        }
        // If path has no wildcards in it, then the user may be
        // trying to identify a file so that it can be created,
        // e.g. "cp existing_file new_place". So for paths with
        // no wildcards, return a File object for that path.
        if (!any_wildcards)
            first.requestedPath(path);
        return first;
    }

    public final Vector files()
    {
        Vector files = new Vector();
        if (_requested_path != null)
            // Just create a file for the requested path,
            // whether it exists or not.
            files.addElement(File.create(_requested_path));
        else
            // The requested path had wildcards, so 
            // _requested_path was not set. Recursively
            // visit each step of path and
            // evaluate filter.
            add_files(null, files);
        return files;
    }

    private void requestedPath(String path)
    {
        _requested_path = Util.removeEscapes(path);
    }

    abstract void add_files(String path_so_far, Vector files);

    protected FileSelector _next = null;
    private String _requested_path;

    //------------------------------------------------------------

    static final class DotSelector extends FileSelector
    {
        void add_files(String path_so_far, Vector files)
        {
            if (_next == null)
                files.addElement(File.create(path_so_far));
            else
                _next.add_files
                    (Path.concatenate(path_so_far, "."), files);
        }
    }

    //------------------------------------------------------------

    static final class DotDotSelector extends FileSelector
    {
        void add_files(String path_so_far, Vector files)
        {
            if (_next == null)
                files.addElement(File.create(path_so_far, ".."));
            else
                _next.add_files(Path.concatenate(path_so_far, ".."), files);
        }
    }

    //------------------------------------------------------------

    static final class PatternSelector extends FileSelector
    {
        PatternSelector(String pattern)
        {
            _filter = new FileFilter(pattern);
            _root = false;
        }

        void markRoot()
        {
            _root = true;
        }

        void add_files(String path_so_far, Vector files)
        {
            if (_root)
                path_so_far = "/";
            File directory =
                path_so_far == null
                ? File.create(".")
                : File.create(path_so_far);
            String[] file_names = directory.list(_filter);
            if (file_names != null)
                for (int i = 0; i < file_names.length; i++)
                {
                    if (_next == null)
                    {
                        File file =
                            path_so_far == null
                            ? File.create(file_names[i])
                            : File.create(path_so_far, 
                                          file_names[i]);
                        files.addElement(file);
                    }
                    else
                    {
                        _next.add_files
                            (Path.concatenate(path_so_far,
                                              file_names[i]), 
                                        files);
                    }
                }
        }

        boolean hasWildcards()
        {
            return _filter.hasWildcards();
        }

        private final FileFilter _filter;
        private boolean _root;
    }
}

// RMShell
// Copyright (C) 2000 Jack A. Orenstein
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of
// the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.
// 
// Jack A. Orenstein  jao@mediaone.net

package com.rms.shell.commandline;

import java.io.*;
import java.util.*;

// Use java.io.File, not com.maxis.rms.shell.File
import java.io.File;

final class RMShellClassLoader extends ClassLoader
{
    public RMShellClassLoader()
    {
        setup_classpath();
    }
    
    public synchronized Class loadClass(String class_name,
                                        boolean resolve)
        throws ClassNotFoundException
    {
        Class c = null;
        if (class_name.startsWith("java.") ||
            class_name.startsWith("sun.") ||
            class_name.startsWith("com.maxis.rms.shell."))
            c = findSystemClass(class_name);
        else
        {
            try
            {
                c = (Class) _cache.get(class_name);
                if (c == null) 
                {
                    byte data[] = class_file_contents(class_name);
                    c = defineClass(class_name, data, 0, data.length);
                    _cache.put(class_name, c);
                }
                if (resolve)
                    resolveClass(c);
            }
            catch (Exception e)
            {
                throw new ClassNotFoundException
                    ("Caught "+e.getClass().getName()+
                     " while loading "+class_name+": "+
                     e.getMessage());
            }
        }
        return c;
    }

    private byte[] class_file_contents(String class_name)
        throws IOException
    {
        String file_name = file_name(class_name);
        for (int i = 0; i < _classpath.length; i++)
        {
            File classpath_entry = new File(_classpath[i]);
            if (classpath_entry.isDirectory())
            {
                String path = _classpath[i] + File.separatorChar + file_name;
                File file = new File(path);
                if (file.exists())
                    return read_class_file(file);
            }
            // TBD: Search in zip and jar files
        }
        return null;
    }

    private void setup_classpath()
    {
        StringTokenizer tokenizer = 
            new StringTokenizer
            (System.getProperty("java.class.path"), File.pathSeparator);
        Vector tokens = new Vector();
        while (tokenizer.hasMoreTokens())
            tokens.addElement(tokenizer.nextToken());
        _classpath = new String[tokens.size()];
        tokens.copyInto(_classpath);
    }

    private String file_name(String class_name)
    {
        return class_name.replace('.', File.separatorChar) + ".class";
    }

    private byte[] read_class_file(File file)
        throws IOException
    {
        Vector arrays = new Vector();
        FileInputStream input = new FileInputStream(file);

        // Read file contents
        byte[] array = new byte[BYTES_PER_READ];
        int n_read;
        int last_array_size = -1;
        while ((n_read = input.read(array)) != -1)
        {
            last_array_size = n_read;
            arrays.addElement(array);
            array = new byte[BYTES_PER_READ];
        }

        // Assemble into a single array
        int size = BYTES_PER_READ * (arrays.size() - 1) + last_array_size;
        byte[] output = new byte[size];
        int p = 0;
        Enumeration array_scan = arrays.elements();
        for (int i = 0; i < arrays.size() - 1; i++)
        {
            array = (byte[]) array_scan.nextElement();
            System.arraycopy(array, 0, output, p, BYTES_PER_READ);
            p += BYTES_PER_READ;
        }
        array = (byte[]) array_scan.nextElement();
        System.arraycopy(array, 0, output, p, last_array_size);

        return output;
    }

    private static final int BYTES_PER_READ = 1000;

    private Hashtable _cache = new Hashtable();
    private String[] _classpath;
}

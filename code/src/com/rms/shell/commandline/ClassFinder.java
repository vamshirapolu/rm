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

import java.util.*;

import com.rms.shell.util.*;

class ClassFinder
{
    ClassFinder()
    {
        _stable_classes = new Hashtable();
        int i = 0;
        while (i < _stable_class_names.length)
        {
            String command = _stable_class_names[i++];
            String class_name = _stable_class_names[i++];
            try
            {
                Class klass = Class.forName(class_name);
                _stable_classes.put(command, klass);
            }
            catch (ClassNotFoundException e)
            {
                System.err.println("Couldn't find "+class_name);
                Assertion.check(false);
            }
        }
    }

    Class find(String class_name)
        throws ClassNotFoundException
    {
        Class klass = (Class) _stable_classes.get(class_name);
        if (klass == null)
        {
            RMShellClassLoader loader = new RMShellClassLoader();
            klass = loader.loadClass(class_name);
        }
        return klass;
    }

    private static String[] _stable_class_names =
    {
        // Aliases
        "cat",                        "com.rms.shell.command.cat",
        "cd",                         "com.rms.shell.command.cd",
        "cp",                         "com.rms.shell.command.cp",
        "dirs",                       "com.rms.shell.command.dirs",
        "echo",                       "com.rms.shell.command.echo",
        "env",                        "com.rms.shell.command.env",
        "exit",                       "com.rms.shell.command.exit",
        "gc",                         "com.rms.shell.command.gc",
        "help",                       "com.rms.shell.command.help",
        "history",                    "com.rms.shell.command.history",
        "jobs",                       "com.rms.shell.command.jobs",
        "kill",                       "com.rms.shell.command.kill",
        "ls",                         "com.rms.shell.command.ls",
        "mkdir",                      "com.rms.shell.command.mkdir",
        "popd",                       "com.rms.shell.command.popd",
        "pushd",                      "com.rms.shell.command.pushd",
        "pwd",                        "com.rms.shell.command.pwd",
        "quit",                       "com.rms.shell.command.exit",
        "rm",                         "com.rms.shell.command.rm",
        "save",                       "com.rms.shell.command.save",
        "set",                        "com.rms.shell.command.set",
    };

    private Hashtable _stable_classes;
}

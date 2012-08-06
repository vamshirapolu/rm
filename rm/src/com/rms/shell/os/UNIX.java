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

package com.rms.shell.os;

import java.io.*;

import com.rms.shell.*;
import com.rms.shell.util.*;

public class UNIX extends OS
{
    public boolean caseSensitive()
    {
        return true;
    }

    public String rmShellToOsPath(String rmshell_path)
    {
        return rmshell_path;
    }

    public String osToRMShellPath(String os_path)
    {
        return os_path;
    }

    public String canonicalizePath(String rmshell_path)
        throws IOException
    {
        // Let OS do canonicalization, so that links are handled
        // correctly.
        java.io.File os_file = new java.io.File(rmshell_path);
        return os_file.getCanonicalPath();
    }

    public com.rms.shell.File createFile(String path)
    {
        return new UNIXFile(path);
    }

    public com.rms.shell.File createFile(String directory, String file_name)
    {
        return new UNIXFile(directory, file_name);
    }
}

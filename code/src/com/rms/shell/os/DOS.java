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

import com.rms.shell.File;
import com.rms.shell.InvalidRMShellPathException;
import com.rms.shell.OS;
import com.rms.shell.Path;
import com.rms.shell.RMShellException;
import com.rms.shell.util.Assertion;

// Break ties

public class DOS extends OS
{
    public boolean caseSensitive()
    {
        return false;
    }

    public String rmShellToOsPath(String rmshell_path)
    {
        String os_path = null;
        switch (rmshell_path.length())
        {
        case 0:
            Assertion.check(false);
            break;

        case 1:
            os_path = "\\";
            break;

        case 2:
            if (rmshell_path.charAt(0) == '/')
                os_path = rmshell_path.charAt(1) + ":\\";
            else
                throw new InvalidRMShellPathException
                    (rmshell_path+" is not a valid com.maxis.rms.shell path.");
            break;
            
        default:
            if (rmshell_path.charAt(0) == '/' && 
                rmshell_path.charAt(2) == '/')
                os_path = 
                    rmshell_path.charAt(1) + 
                    ":" + 
                    rmshell_path.substring(2);
            else
                throw new InvalidRMShellPathException
                    (rmshell_path+" is not a valid "+
                     "com.maxis.rms.shell path.");
        }
        os_path = os_path.replace('/', File.separatorChar);
        return os_path;
    }

    public String osToRMShellPath(String os_path)
    {
        String rmshell_path = null;
        if (os_path.charAt(1) == ':' &&
            os_path.charAt(2) == File.separatorChar)
            rmshell_path =
                "/" +
                os_path.charAt(0) +
                os_path.substring(2);
        else
            throw new RMShellException(os_path+" is not a valid OS path.");
        rmshell_path = rmshell_path.replace(File.separatorChar, '/');
        if (rmshell_path.endsWith("/") && rmshell_path.length() > 1)
            rmshell_path = 
                rmshell_path.substring(0, rmshell_path.length() - 1);
        return rmshell_path;
    }

    public String canonicalizePath(String path)
    {
        return Path.simplify(path);
    }

    public com.rms.shell.File createFile(String path)
    {
        return new DOSFile(path);
    }

    public com.rms.shell.File createFile(String directory, String file_name)
    {
        return new DOSFile(directory, file_name);
    }
}

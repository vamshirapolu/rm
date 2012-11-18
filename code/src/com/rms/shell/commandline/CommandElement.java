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

import com.rms.shell.*;

abstract class CommandElement
{
    public abstract Vector expand();

    public final String token()
    {
        return _token;
    }

    static Vector globExpand(String token)
    {
        Vector expanded;
        FileSelector file_selector = 
            FileSelector.create(token);
        Vector files = null;
        try
        {
            files = file_selector.files();
        }
        catch (InvalidRMShellPathException e)
        {
            // Could happen in expanding the command name
            // while in the root directory on a DOS-like
            // file-system. ("/ls" isn't a valid DOS path.)
        }
        if (files == null || files.size() == 0)
        {
            expanded = new Vector(1);
            expanded.addElement(token);
        }
        else
        {
            expanded = new Vector(files.size());
            for (Enumeration file_scan = files.elements();
                 file_scan.hasMoreElements();)
            {
                File file = (File) file_scan.nextElement();
                expanded.addElement(file.getPath());
            }
        }
        return expanded;
    }

    protected CommandElement(String token)
    {
        token = _token;
    }

    protected CommandElement()
    {}

    protected String _token;
}

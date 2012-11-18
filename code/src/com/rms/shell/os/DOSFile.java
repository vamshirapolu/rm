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

import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;

import com.rms.shell.File;
import com.rms.shell.util.Assertion;

// Break ties

public class DOSFile extends com.rms.shell.File
{
    // com.maxis.rms.shell.File interface

    public String[] list()
    {
        String[] list = null;
        try
        {
            if (_path.canonicalPath().equals("/"))
                list = list(new FilenameFilter()
                            {
                                public boolean accept(java.io.File dir, 
                                                      String file_name)
                                {
                                    return true;
                                }
                            });
            else
                list = super.list();
        }
        catch (IOException e)
        {
            // For DOS, canonicalPath shouldn't ever throw IOException
            Assertion.check(false);
        }
        return list;
    }

    public String[] list(FilenameFilter filter)
    {
        String[] list = null;
        try
        {
            if (_path.canonicalPath().equals("/"))
            {
                File root = File.create("/");
                Vector files = new Vector();
                String[] logical_drives = logical_drives();
                for (int i = 0; i < logical_drives.length; i++)
                {
                    if (filter.accept(root, logical_drives[i]))
                        files.addElement(logical_drives[i]);
                }
                list = new String[files.size()];
                files.copyInto(list);
            }
            else
                list = super.list(filter);
        }
        catch (IOException e)
        {
            // canonicalPath shouldn't throw an exception
            Assertion.check(false);
        }
        return list;
    }


    // For use by this package

    DOSFile(String path)
    {
        super(path);
    }

    DOSFile(String directory, String file_name)
    {
        super(directory, file_name);
    }


    // For use by this class and subclasses

    protected String[] logical_drives()
    {
        if (_logical_drives == null)
        {
            boolean[] drives = new boolean[26];
            for (char drive_letter = 'c'; 
                 drive_letter <= 'z'; 
                 drive_letter++)
                drives[drive_letter - 'c'] = false;
            int n_drives = 0;
            for (char drive_letter = 'c';
                 drive_letter <= 'z';
                 drive_letter++)
            {
                java.io.File logical_drive = 
                    new java.io.File(drive_letter + ":\\");
                if (logical_drive.exists())
                {
                    drives[drive_letter - 'c'] = true;
                    n_drives++;
                }
            }
            _logical_drives = new String[n_drives];
            int i = 0;
            for (char drive_letter = 'c'; 
                 drive_letter <= 'z';
                 drive_letter++)
                if (drives[drive_letter - 'c'])
                    _logical_drives[i++] = 
                        new String(new char[] { drive_letter });
        }
        return _logical_drives;
    }


    // Representation

    protected static String[] _logical_drives;
}

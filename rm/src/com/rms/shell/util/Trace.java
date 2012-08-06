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

package com.rms.shell.util;

public class Trace
{
    public static synchronized void print(String s)
    {
        print(s, false);
    }

    public static synchronized void print(int i)
    {
        print(Integer.toString(i), false);
    }

    public static synchronized void println(String s)
    {
        print(s, true);
    }

    public static synchronized void println(int i)
    {
        print(Integer.toString(i), true);
    }

    public static synchronized void println()
    {
        print("", true);
    }

    public static synchronized void printTimestamp(boolean ts)
    {
        _print_timestamp = ts;
    }

    public static synchronized void printThread(boolean th)
    {
        _print_thread = th;
    }

    private static void print(String s, boolean new_line_at_end)
    {
        if (_new_line)
        {
            if (_print_timestamp)
            {
                System.out.print(System.currentTimeMillis());
                System.out.print(' ');
            }
            if (_print_thread)
            {
                System.out.print(Thread.currentThread());
                System.out.print(": ");
            }
        }
        if (new_line_at_end)
            System.out.println(s);
        else
            System.out.print(s);
        _new_line = new_line_at_end;
    }

    private static boolean _print_timestamp = true;
    private static boolean _print_thread = true;
    private static boolean _new_line = true;
}

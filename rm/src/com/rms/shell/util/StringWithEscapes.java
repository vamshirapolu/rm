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

public class StringWithEscapes
{
    public StringWithEscapes(String s)
    {
        // Count "logical" characters. A logical character is 
        // an unescaped character or an escape combined with 
        // the following character.
        char[] chars = s.toCharArray();
        int n_chars = chars.length;
        int n_logical_chars = 0;
        int i = 0;
        while (i < n_chars)
        {
            char c = chars[i++];
            if (c == '\\')
                i++;
            n_logical_chars++;
        }
        
        // Remove escapes and note their positions.
        _string = new char[n_logical_chars];
        _escaped = new boolean[n_logical_chars];
        int old_position = 0;
        int new_position = 0;
        while (old_position < n_chars)
        {
            char c = chars[old_position++];
            if (c == '\\')
            {
                c = chars[old_position++];
                _escaped[new_position] = true;
            }
            else
                _escaped[new_position] = false;
            _string[new_position++] = c;
        }
    }
    
    public char charAt(int position)
    {
        return _string[position];
    }
    
    public boolean escaped(int position)
    {
        return _escaped[position];
    }
    
    public int length()
    {
        return _string.length;
    }
    
    private char[] _string;
    private boolean[] _escaped;
}

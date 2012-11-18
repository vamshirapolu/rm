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

public abstract class QuotedString extends CommandElement
{
    // Object interface

    public final String toString()
    {
        return quote() + _value + quote();
    }

    
    // CommandElement interface

    public Vector expand()
    {
        Vector expanded = new Vector(1);
        expanded.addElement(_value);
        return expanded;
    }


    // QuotedString interface

    public abstract char quote();

    public static QuotedString create(String token)
    {
        QuotedString s = null;
        switch (token.charAt(0))
        {
            case '\'':
                s = new SingleQuotedString(token);
                break;

            case '"':
                s = new DoubleQuotedString(token);
                break;

            default:
                Assertion.check(false);
                break;
        }
        return s;
    }

    protected QuotedString(String token)
    {
        super(token);
        int length = token.length();
        Assertion.check(token.charAt(0) == 
                        token.charAt(token.length() - 1));
        _value = token.substring(1, length - 1);
    }

    
    protected String _value;
}

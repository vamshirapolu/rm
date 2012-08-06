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
import com.rms.shell.util.*;

public class Variable extends CommandElement
{
    public String toString()
    {
        return '$' + _variable_name;
    }

    public Vector expand()
    {
        String value = System.getProperty(_variable_name);
        Vector expanded =
            value == null
            ? new Vector(0)
            : tokenize(value);
        return expanded;
    }

    public static Variable create(String token)
    {
        return new Variable(token);
    }

    private Variable(String token)
    {
        super(token);
        Assertion.check(token.charAt(0) == '$');
        _variable_name = token.substring(1);
        validate_variable_name();
    }

    private Vector tokenize(String string)
    {
        Vector tokens = new Vector();
        for (StringTokenizer tokenizer =
                 new StringTokenizer(string, " \t\n\r");
             tokenizer.hasMoreTokens();)
            Util.insert(tokens, 
                        globExpand(tokenizer.nextToken()));
        return tokens;
    }

    private void validate_variable_name()
    {
        boolean ok = true;
        char c = _variable_name.charAt(0);
        if (is_letter(c))
        {
            for (int i = 1; 
                 ok && i < _variable_name.length(); 
                 i++)
            {
                c = _variable_name.charAt(i);
                ok = 
                    is_letter(c) ||
                    is_digit(c) ||
                    c == '.';
            }
        }
        else
            ok = false;
        if (!ok)
            throw new RMShellException
                ("'"+_variable_name+
                 "' is not a valid variable name.");
    }

    private boolean is_letter(char c)
    {
        return 
            'a' <= c && c <= 'z' ||
            'A' <= c && c <= 'Z' ||
            c == '_' ||
            c == '$';
    }

    private boolean is_digit(char c)
    {
        return '0' <= c && c <= '9';
    }

    private String _variable_name;
}

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

import com.rms.shell.util.*;


// Expected usage:
//
// Lexer lexer = new Lexer(command_line);
// while (lexer.next())
// {
//     int token_type = lexer.tokenType();
//     String token = lexer.token();
//     switch (token_type)
//     { ... }
// }
//
// The token contains all delimiters, e.g. $ for a variable, quote marks
// for a string.


class Lexer
{
    public static final int NONE              = 0;
    public static final int PIPE              = 1;   // ^
    public static final int REDIRECT_IN       = 2;   // <
    public static final int REDIRECT_OUT      = 3;   // >
    public static final int APPEND            = 4;   // >>
    public static final int SEPARATOR         = 5;   // ;
    public static final int AMPERSAND         = 6;   // &
    public static final int VARIABLE          = 7;   // $var_name
    public static final int QUOTED_STRING     = 8;   // '...' or "..."
    public static final int STRING            = 9;   // simple_string,
                                                     // foo*.{cc,hh}
    public static final int LAST_COMMAND      = 10;  // !!
    public static final int EARLIER_COMMAND   = 11;  // !419    

    public Lexer(String line)
    {
        _input = new PushbackReader(new StringReader(line));
    }

    public boolean next()
    {
        _automaton.reset();
        char c = (char) -1;
        while (!_automaton.inEndState())
        {
            c = next_char();
            _automaton.accept(c);
        }
        unread_char(c);
        return _automaton.tokenType() != NONE;
    }

    public int tokenType()
    {
        return _automaton.tokenType();
    }

    public String token()
    {
        return _automaton.token();
    }

    public static String tokenTypeName(int token_type)
    {
        String name = null;
        switch (token_type)
        {
            case PIPE:
                name = "PIPE";
                break;
            case REDIRECT_IN:
                name = "REDIRECT_IN";
                break;
            case REDIRECT_OUT:
                name = "REDIRECT_OUT";
                break;
            case APPEND:
                name = "APPEND";
                break;
            case SEPARATOR:
                name = "SEPARATOR";
                break;
            case AMPERSAND:
                name = "AMPERSAND";
                break;
            case VARIABLE:
                name = "VARIABLE";
                break;
            case QUOTED_STRING:
                name = "QUOTED_STRING";
                break;
            case STRING:
                name = "STRING";
                break;
            default: 
                Assertion.check(false); 
                break;
        }
        return name;
    }

    private char next_char()
    {
        int c = 0;
        try
        {
            c = _input.read();
        }
        catch (IOException e)
        {
            Assertion.check(false);
        }
        return (char) c;
    }

    private void unread_char(char c)
    {
        try
        {
            _input.unread(c);
        }
        catch (IOException e)
        {
            Assertion.check(false);
        }
    }


    // Representation

    private static LexerAutomaton _automaton = new LexerAutomaton();

    private PushbackReader _input;
}

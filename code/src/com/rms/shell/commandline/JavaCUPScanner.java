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

import java_cup.runtime.*;

class JavaCUPScanner implements Scanner
{
    public JavaCUPScanner(Lexer lexer)
    {
        _lexer = lexer;
    }

    public void init()
    {}

    public Symbol next_token() throws java.lang.Exception
    {
        Symbol symbol;
        if (_lexer.next())
        {
            switch (_lexer.tokenType())
            {
                case Lexer.VARIABLE:
                    symbol = new Symbol(sym.VARIABLE, _lexer.token());
                    break;

                case Lexer.STRING:
                    symbol = new Symbol(sym.STRING, _lexer.token());
                    break;

                case Lexer.QUOTED_STRING:
                    symbol = new Symbol(sym.QUOTED_STRING, _lexer.token());
                    break;

                case Lexer.PIPE:
                    symbol = new Symbol(sym.PIPE, _lexer.token());
                    break;

                case Lexer.REDIRECT_IN:
                    symbol = new Symbol(sym.REDIRECT_IN, _lexer.token());
                    break;

                case Lexer.REDIRECT_OUT:
                    symbol = new Symbol(sym.REDIRECT_OUT, _lexer.token());
                    break;

                case Lexer.APPEND:
                    symbol = new Symbol(sym.APPEND, _lexer.token());
                    break;

                case Lexer.SEPARATOR:
                    symbol = new Symbol(sym.SEPARATOR, _lexer.token());
                    break;

                case Lexer.AMPERSAND:
                    symbol = new Symbol(sym.AMPERSAND, _lexer.token());
                    break;

                case Lexer.LAST_COMMAND:
                    symbol = new Symbol(sym.LAST_COMMAND, _lexer.token());
                    break;

                case Lexer.EARLIER_COMMAND:
                    symbol = new Symbol(sym.EARLIER_COMMAND, _lexer.token());
                    break;

                default:
                    System.err.println("Unexpected token type "+_lexer.tokenType());
                    symbol = null;
                    break;
            }
        }
        else
            symbol = new Symbol(sym.EOF);
        return symbol;
    }

    private Lexer _lexer;
}

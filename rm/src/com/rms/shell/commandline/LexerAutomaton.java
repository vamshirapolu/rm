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

import com.rms.shell.*;
import com.rms.shell.util.*;

class LexerAutomaton
{
    public LexerAutomaton()
    {
        setup_transitions();
        _token = new StringBuffer();
        reset();
    }

    public void reset()
    {
        if (TRACE)
            System.out.println
                ("Resetting LexerAutomaton to S_UNKNOWN");

        _token.setLength(0);
        _state = S_UNKNOWN;
        _token_type = Lexer.NONE;
    }

    public void accept(char c)
    {
        _char = c;
        classify();
        int original_state = _state;
        Transition transition =
            _transition[_state][_char_classification];
        if (transition == null)
            transition = _transition[_state][I_DEFAULT];
        transition.apply();

        if (TRACE)
        {
            System.out.println
                (state_name(original_state)+
                 " + '"+
                 _char+"' ("+
                 char_classification_name(_char_classification)+
                 ") -> "+
                 state_name(_state));
        }
    }

    public boolean inEndState()
    {
        return _state == S_DONE;
    }

    public int tokenType()
    {
        Assertion.check(inEndState());
        return _token_type;
    }

    public String token()
    {
        Assertion.check(inEndState());
        return _token.toString();
    }

    private void classify()
    {
        switch (_char)
        {
            case '^':
                _char_classification = I_HAT;
                break;

            case '<':
                _char_classification = I_LT;
                break;

            case '>':
                _char_classification = I_GT;
                break;

            case ';':
                _char_classification = I_SEMI;
                break;

            case '$':
                _char_classification = I_DOLLAR;
                break;

            case ' ':
            case '\t':
            case '\n':
            case '\r':
                _char_classification = I_WHITESPACE;
                break;

            case '\'':
                _char_classification = I_SINGLE_QUOTE;
                break;

            case '"':
                _char_classification = I_DOUBLE_QUOTE;
                break;

            case '\\':
                _char_classification = I_ESCAPE;
                break;

            case '&':
                _char_classification = I_AMPERSAND;
                break;

            case '!':
                _char_classification = I_BANG;
                break;

            case (char) -1:
                _char_classification = I_EOL;
                break;

            default:
                _char_classification = I_OTHER;
                break;
        }
    }

    private void setup_transitions()
    {
        _transition = new Transition[N_STATES][];
        Transition[] state_transition;

        // S_UNKNOWN
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_HAT]          = new Transition_UNKNOWN_HAT();
        state_transition[I_LT]           = new Transition_UNKNOWN_LT();
        state_transition[I_GT]           = new Transition_UNKNOWN_GT();
        state_transition[I_SEMI]         = new Transition_UNKNOWN_SEMI();
        state_transition[I_DOLLAR]       = new Transition_UNKNOWN_DOLLAR();
        state_transition[I_SINGLE_QUOTE] = new Transition_UNKNOWN_QUOTE_1();
        state_transition[I_DOUBLE_QUOTE] = new Transition_UNKNOWN_QUOTE_2();
        state_transition[I_WHITESPACE]   = new Transition_UNKNOWN_WHITESPACE();
        state_transition[I_AMPERSAND]    = new Transition_UNKNOWN_AMPERSAND();
        state_transition[I_ESCAPE   ]    = new Transition_UNKNOWN_ESCAPE();
        state_transition[I_EOL]          = new Transition_UNKNOWN_EOL();
        state_transition[I_BANG]         = new Transition_UNKNOWN_BANG();
        state_transition[I_DEFAULT]      = new Transition_UNKNOWN_DEFAULT();
        _transition[S_UNKNOWN] = state_transition;

        // S_PIPE
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]      = new Transition_PIPE_DEFAULT();
        _transition[S_PIPE] = state_transition;

        // S_REDIRECT_IN
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]      = new Transition_REDIRECT_IN_DEFAULT();
        _transition[S_REDIRECT_IN] = state_transition;

        // S_REDIRECT_OUT
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_GT]           = new Transition_REDIRECT_OUT_GT();
        state_transition[I_DEFAULT]      = new Transition_REDIRECT_OUT_DEFAULT();
        _transition[S_REDIRECT_OUT] = state_transition;

        // S_SEPARATOR
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_AMPERSAND]    = new Transition_SEPARATOR_AMPERSAND();
        state_transition[I_DEFAULT]      = new Transition_SEPARATOR_DEFAULT();
        _transition[S_SEPARATOR] = state_transition;

        // S_VARIABLE
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_OTHER]        = new Transition_VARIABLE_OTHER();
        state_transition[I_DEFAULT]      = new Transition_VARIABLE_DEFAULT();
        _transition[S_VARIABLE] = state_transition;

        // S_STRING
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_ESCAPE]       = new Transition_STRING_ESCAPE();
        state_transition[I_AMPERSAND]    = new Transition_STRING_AMPERSAND();
        state_transition[I_OTHER]        = new Transition_STRING_OTHER();
        state_transition[I_DEFAULT]      = new Transition_STRING_DEFAULT();
        _transition[S_STRING] = state_transition;

        // S_QUOTE_1
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]      = new Transition_QUOTE_1_DEFAULT();
        _transition[S_QUOTE_1] = state_transition;

        // S_QUOTE_2
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]      = new Transition_QUOTE_2_DEFAULT();
        _transition[S_QUOTE_2] = state_transition;

        // S_APPEND
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]      = new Transition_APPEND_DEFAULT();
        _transition[S_APPEND] = state_transition;

        // S_VARIABLE_NAME
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_AMPERSAND]    = new Transition_VARIABLE_NAME_AMPERSAND();
        state_transition[I_OTHER]        = new Transition_VARIABLE_NAME_OTHER();
        state_transition[I_DEFAULT]      = new Transition_VARIABLE_NAME_DEFAULT();
        _transition[S_VARIABLE_NAME] = state_transition;

        // S_QUOTED_1
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_SINGLE_QUOTE] = new Transition_QUOTED_1_QUOTE_1();
        state_transition[I_EOL]          = new Transition_QUOTED_1_EOL();
        state_transition[I_DEFAULT]      = new Transition_QUOTED_1_DEFAULT();
        _transition[S_QUOTED_1] = state_transition;

        // S_QUOTED_2
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DOUBLE_QUOTE] = new Transition_QUOTED_2_QUOTE_2();
        state_transition[I_EOL]          = new Transition_QUOTED_2_EOL();
        state_transition[I_DEFAULT]      = new Transition_QUOTED_2_DEFAULT();
        _transition[S_QUOTED_2] = state_transition;

        // S_QUOTE_1_DONE
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_AMPERSAND]    = new Transition_QUOTE_1_DONE_AMPERSAND();
        state_transition[I_DEFAULT]      = new Transition_QUOTE_1_DONE_DEFAULT();
        _transition[S_QUOTE_1_DONE] = state_transition;

        // S_QUOTE_2_DONE
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_AMPERSAND]    = new Transition_QUOTE_2_DONE_AMPERSAND();
        state_transition[I_DEFAULT]      = new Transition_QUOTE_2_DONE_DEFAULT();
        _transition[S_QUOTE_2_DONE] = state_transition;

        // S_ESCAPED
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]      = new Transition_ESCAPED_DEFAULT();
        _transition[S_ESCAPED] = state_transition;

        // S_BACKGROUND
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]      = new Transition_BACKGROUND_DEFAULT();
        _transition[S_BACKGROUND] = state_transition;

        // S_BANG
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_BANG]         = new Transition_BANG_BANG();
        state_transition[I_OTHER]        = new Transition_BANG_OTHER();
        state_transition[I_DEFAULT]      = new Transition_BANG_DEFAULT();
        _transition[S_BANG] = state_transition;

        // S_LAST_COMMAND
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]      = new Transition_LAST_COMMAND_DEFAULT();
        _transition[S_LAST_COMMAND] = state_transition;

        // S_EARLIER_COMMAND
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_OTHER]        = new Transition_EARLIER_COMMAND_OTHER();
        state_transition[I_DEFAULT]      = new Transition_EARLIER_COMMAND_DEFAULT();
        _transition[S_EARLIER_COMMAND] = state_transition;

        // S_DONE
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]      = new Transition_DONE_DEFAULT();
        _transition[S_DONE] = state_transition;
    }

    private void error()
    {
        throw new RMShellException("Invalid token in command line");
    }

    private String state_name(int state)
    {
        String name;
        switch (state)
        {
            case S_PIPE:
                name = "S_PIPE";
                break;

            case S_REDIRECT_IN:
                name = "S_REDIRECT_IN";
                break;

            case S_REDIRECT_OUT:
                name = "S_REDIRECT_OUT";
                break;

            case S_APPEND:
                name = "S_APPEND";
                break;
                
            case S_SEPARATOR:
                name = "S_SEPARATOR";
                break;
                
            case S_VARIABLE:
                name = "S_VARIABLE";
                break;
                
            case S_QUOTE_1:
                name = "S_QUOTE_1";
                break;
                
            case S_QUOTE_2:
                name = "S_QUOTE_2";
                break;
                
            case S_STRING:
                name = "S_STRING";
                break;
                
            case S_UNKNOWN:
                name = "S_UNKNOWN";
                break;
                
            case S_DONE:
                name = "S_DONE";
                break;
                
            case S_VARIABLE_NAME:
                name = "S_VARIABLE_NAME";
                break;
                
            case S_QUOTED_1:
                name = "S_QUOTED_1";
                break;
                
            case S_QUOTED_2:
                name = "S_QUOTED_2";
                break;

            case S_QUOTE_1_DONE:
                name = "S_QUOTE_1_DONE";
                break;
                
            case S_QUOTE_2_DONE:
                name = "S_QUOTE_2_DONE";
                break;
                
            case S_ESCAPED:
                name = "S_ESCAPED";
                break;
                
            case S_BACKGROUND:
                name = "S_BACKGROUND";
                break;
                
            case S_BANG:
                name = "S_BANG";
                break;
                
            case S_LAST_COMMAND:
                name = "S_LAST_COMMAND";
                break;
                
            case S_EARLIER_COMMAND:
                name = "S_EARLIER_COMMAND";
                break;
                
            default:
                name = "Unrecognized state: "+state;
                break;
        }
        return name;
    }

    private String char_classification_name(int classification)
    {
        String name;
        switch (classification)
        {
            case I_DEFAULT:
                name = "I_DEFAULT";
                break;

            case I_WHITESPACE:
                name = "I_WHITESPACE";
                break;

            case I_END_OF_INPUT:
                name = "I_END_OF_INPUT";
                break;

            case I_HAT:
                name = "I_HAT";
                break;

            case I_LT:
                name = "I_LT";
                break;

            case I_GT:
                name = "I_GT";
                break;

            case I_SEMI:
                name = "I_SEMI";
                break;

            case I_DOLLAR:
                name = "I_DOLLAR";
                break;

            case I_SINGLE_QUOTE:
                name = "I_SINGLE_QUOTE";
                break;

            case I_DOUBLE_QUOTE:
                name = "I_DOUBLE_QUOTE";
                break;

            case I_ESCAPE:
                name = "I_ESCAPE";
                break;

            case I_AMPERSAND:
                name = "I_AMPERSAND";
                break;

            case I_BANG:
                name = "I_BANG";
                break;

            case I_OTHER:
                name = "I_OTHER";
                break;

            case I_EOL:
                name = "I_EOL";
                break;

            default:
                name = "Unrecognized char classification: "+classification;
                break;
        }
        return name;
    }
        

    private static final boolean TRACE = false;

    // States
    private static final int S_PIPE            = 0;   // ^
    private static final int S_REDIRECT_IN     = 1;   // <
    private static final int S_REDIRECT_OUT    = 2;   // >
    private static final int S_APPEND          = 3;   // >>
    private static final int S_SEPARATOR       = 4;   // ;
    private static final int S_VARIABLE        = 5;   // $
    private static final int S_QUOTE_1         = 6;   // '
    private static final int S_QUOTE_2         = 7;   // "
    private static final int S_STRING          = 8;   // simple_string,
    private static final int S_LAST_COMMAND    = 9;   // !!
    private static final int S_EARLIER_COMMAND = 10;  // !419
    private static final int S_UNKNOWN         = 11;
    private static final int S_DONE            = 12;
    private static final int S_VARIABLE_NAME   = 13;  // var_name following $
    private static final int S_QUOTED_1        = 14;  // Stuff in '...' 
    private static final int S_QUOTED_2        = 15;  // Stuff in "..."
    private static final int S_QUOTE_1_DONE    = 16;
    private static final int S_QUOTE_2_DONE    = 17;
    private static final int S_ESCAPED         = 18;
    private static final int S_BACKGROUND      = 19;
    private static final int S_BANG            = 20;
    private static final int N_STATES          = 21;

    // Input classifications
    private static final int I_DEFAULT         = 0;
    private static final int I_WHITESPACE      = 1;
    private static final int I_END_OF_INPUT    = 2;
    private static final int I_HAT             = 3;
    private static final int I_LT              = 4;
    private static final int I_GT              = 5; 
    private static final int I_SEMI            = 6; 
    private static final int I_DOLLAR          = 7;
    private static final int I_SINGLE_QUOTE    = 8;
    private static final int I_DOUBLE_QUOTE    = 9;
    private static final int I_ESCAPE          = 10;
    private static final int I_OTHER           = 11;
    private static final int I_EOL             = 12;
    private static final int I_AMPERSAND       = 13;
    private static final int I_BANG            = 14;
    private static final int N_INPUT_CLASSES   = 15;

    private Transition[][] _transition;
    private int _state;
    private char _char;
    private int _char_classification;
    private int _token_type;
    private StringBuffer _token;

    //------------------------------------------------------------

    abstract class Transition
    {
        abstract void apply();
    }

    //------------------------------------------------------------

    class Transition_UNKNOWN_HAT extends Transition
    {
        void apply()
        {
            _state = S_PIPE;
            _token_type = Lexer.PIPE;
            _token.append(_char);
        }
    }

    class Transition_UNKNOWN_LT extends Transition
    {
        void apply()
        {
            _state = S_REDIRECT_IN;
            _token_type = Lexer.REDIRECT_IN;
            _token.append(_char);
        }
    }

    class Transition_UNKNOWN_GT extends Transition
    {
        void apply()
        {
            _state = S_REDIRECT_OUT;
            _token_type = Lexer.REDIRECT_OUT;
            _token.append(_char);
        }
    }

    class Transition_UNKNOWN_SEMI extends Transition
    {
        void apply()
        {
            _state = S_SEPARATOR;
            _token_type = Lexer.SEPARATOR;
            _token.append(_char);
        }
    }

    class Transition_UNKNOWN_DOLLAR extends Transition
    {
        void apply()
        {
            _state = S_VARIABLE;
            _token_type = Lexer.VARIABLE;
            _token.append(_char);
        }
    }

    class Transition_UNKNOWN_QUOTE_1 extends Transition
    {
        void apply()
        {
            _state = S_QUOTE_1;
            _token_type = Lexer.QUOTED_STRING;
            _token.append(_char);
        }
    }

    class Transition_UNKNOWN_QUOTE_2 extends Transition
    {
        void apply()
        {
            _state = S_QUOTE_2;
            _token_type = Lexer.QUOTED_STRING;
            _token.append(_char);
        }
    }

    class Transition_UNKNOWN_WHITESPACE extends Transition
    {
        void apply()
        {
            // Stay in S_UNKNOWN. Don't add whitespace to token.
        }
    }

    class Transition_UNKNOWN_AMPERSAND extends Transition
    {
        void apply()
        {
            _state = S_BACKGROUND;
            _token_type = Lexer.AMPERSAND;
            _token.append(_char);
        }
    }

    class Transition_UNKNOWN_ESCAPE extends Transition
    {
        void apply()
        {
            _state = S_ESCAPED;
            _token_type = Lexer.STRING;
        }
    }

    class Transition_UNKNOWN_BANG extends Transition
    {
        void apply()
        {
            _state = S_BANG;
            _token.append(_char);
        }
    }

    class Transition_UNKNOWN_EOL extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_UNKNOWN_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_STRING;
            _token_type = Lexer.STRING;
            _token.append(_char);
        }
    }

    class Transition_PIPE_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_REDIRECT_IN_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_REDIRECT_OUT_GT extends Transition
    {
        void apply()
        {
            _state = S_APPEND;
            _token_type = Lexer.APPEND;
            _token.append(_char);
        }
    }

    class Transition_REDIRECT_OUT_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_SEPARATOR_AMPERSAND extends Transition
    {
        void apply()
        {
            _state = S_BACKGROUND;
            _token_type = Lexer.AMPERSAND;
            _token.append(_char); 
       }
    }

    class Transition_SEPARATOR_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_VARIABLE_OTHER extends Transition
    {
        void apply()
        {
            _state = S_VARIABLE_NAME;
            _token.append(_char);
        }
    }

    class Transition_VARIABLE_DEFAULT extends Transition
    {
        void apply()
        {
            error();
        }
    }

    class Transition_STRING_ESCAPE extends Transition
    {
        void apply()
        {
            _state = S_ESCAPED;
            _token.append(_char);
        }
    }

    class Transition_STRING_AMPERSAND extends Transition
    {
        void apply()
        {
            _state = S_BACKGROUND;
            _token_type = Lexer.AMPERSAND;
            _token.append(_char);
        }
    }

    class Transition_STRING_OTHER extends Transition
    {
        void apply()
        {
            // Stay in S_STRING
            _token.append(_char);
        }
    }

    class Transition_STRING_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_QUOTE_1_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_QUOTED_1;
            _token.append(_char);
        }
    }

    class Transition_QUOTE_2_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_QUOTED_2;
            _token.append(_char);
        }
    }

    class Transition_APPEND_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_VARIABLE_NAME_AMPERSAND extends Transition
    {
        void apply()
        {
            _state = S_BACKGROUND;
            _token_type = Lexer.AMPERSAND;
            _token.append(_char);
        }
    }

    class Transition_VARIABLE_NAME_OTHER extends Transition
    {
        void apply()
        {
            // Stay in S_VARIABLE_NAME
            _token.append(_char);
        }
    }

    class Transition_VARIABLE_NAME_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_QUOTED_1_QUOTE_1 extends Transition
    {
        void apply()
        {
            _state = S_QUOTE_1_DONE;
            _token.append(_char);
        }
    }

    class Transition_QUOTED_1_EOL extends Transition
    {
        void apply()
        {
            error();
        }
    }

    class Transition_QUOTED_1_DEFAULT extends Transition
    {
        void apply()
        {
            // Stay in S_QUOTED_1
            _token.append(_char);
        }
    }

    class Transition_QUOTED_2_QUOTE_2 extends Transition
    {
        void apply()
        {
            _state = S_QUOTE_2_DONE;
            _token.append(_char);
        }
    }

    class Transition_QUOTED_2_EOL extends Transition
    {
        void apply()
        {
            error();
        }
    }

    class Transition_QUOTED_2_DEFAULT extends Transition
    {
        void apply()
        {
            // Stay in S_QUOTED_2
            _token.append(_char);
        }
    }

    class Transition_QUOTE_1_DONE_AMPERSAND extends Transition
    {
        void apply()
        {
            _state = S_BACKGROUND;
            _token_type = Lexer.AMPERSAND;
            _token.append(_char);
        }
    }

    class Transition_QUOTE_1_DONE_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_QUOTE_2_DONE_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_QUOTE_2_DONE_AMPERSAND extends Transition
    {
        void apply()
        {
            _state = S_BACKGROUND;
            _token_type = Lexer.AMPERSAND;
            _token.append(_char);
        }
    }

    class Transition_ESCAPED_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_STRING;
            _token.append(_char);
        }
    }

    class Transition_BANG_BANG extends Transition
    {
        void apply()
        {
            _state = S_LAST_COMMAND;
            _token_type = Lexer.LAST_COMMAND;
            _token.append(_char);
        }
    }

    class Transition_BANG_OTHER extends Transition
    {
        void apply()
        {
            _state = S_DONE;
            _token_type = Lexer.EARLIER_COMMAND;
            _token.append(_char);
        }
    }

    class Transition_BANG_DEFAULT extends Transition
    {
        void apply()
        {
            error();
        }
    }

    class Transition_LAST_COMMAND_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_EARLIER_COMMAND_OTHER extends Transition
    {
        void apply()
        {
            // Stay in S_EARLIER_COMMAND
            _token.append(_char);
        }
    }

    class Transition_EARLIER_COMMAND_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_BACKGROUND_DEFAULT extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_DONE_DEFAULT extends Transition
    {
        void apply()
        {
            error();
        }
    }

    class Transition_ERROR_DEFAULT extends Transition
    {
        void apply()
        {
            error();
        }
    }
}

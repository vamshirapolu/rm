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

class CommandElementExpander
{
    public static final int HANDLE_ESCAPES      = 1;
    public static final int DONT_HANDLE_ESCAPES = 0;
    public static final int GLOB_EXPAND         = 2;
    public static final int DONT_GLOB_EXPAND    = 0;

    public CommandElementExpander(int options)
    {
        _handle_escapes = (options & HANDLE_ESCAPES) != 0;
        _glob_expand = (options & GLOB_EXPAND) != 0;
        setup_transitions();
        reset();
    }

    public synchronized Vector expand(String string)
    {
        reset();
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++)
            process_char(chars[i]);
        // EOL
        process_char((char)-1);
        return _out;
    }

    private CommandElementExpander()
    {}

    private void reset()
    {
        if (TRACE)
        {
            System.out.println("Resetting to S_TEXT");
            System.out.println("   Handle escapes: "+_handle_escapes);
            System.out.println("   Expand globs: "+_glob_expand);
        }

        _token.setLength(0);
        _state = S_TEXT;
        _out = new Vector();
    }

    private void process_char(char c)
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
                 " + "+c+"("+
                 char_classification_name(_char_classification)+
                 ") -> "+
                 state_name(_state));
        }
    }

    private void classify()
    {
        switch (_char)
        {
            case '$':
                _char_classification = I_DOLLAR;
                break;

            case ' ':
            case '\t':
            case '\n':
            case '\r':
                _char_classification = I_WHITESPACE;
                break;

            case '\\':
                _char_classification = I_ESCAPE;
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

        // S_TEXT
        state_transition = new Transition[N_INPUT_CLASSES];
        if (_handle_escapes)
            state_transition[I_ESCAPE]      = new Transition_TEXT_ESCAPE();
        state_transition[I_DOLLAR]      = new Transition_TEXT_DOLLAR();
        state_transition[I_EOL]         = new Transition_TEXT_EOL();
        state_transition[I_WHITESPACE]  = new Transition_TEXT_WHITESPACE();
        state_transition[I_DEFAULT]     = new Transition_TEXT_DEFAULT();
        _transition[S_TEXT] = state_transition;

        // S_WHITESPACE
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DOLLAR]      = new Transition_WHITESPACE_DOLLAR();
        state_transition[I_EOL]         = new Transition_WHITESPACE_EOL();
        state_transition[I_WHITESPACE]  = new Transition_WHITESPACE_WHITESPACE();
        state_transition[I_DEFAULT]     = new Transition_WHITESPACE_DEFAULT();
        _transition[S_WHITESPACE] = state_transition;

        // S_VAR
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_WHITESPACE]  = new Transition_VAR_WHITESPACE();
        state_transition[I_EOL]         = new Transition_VAR_EOL();
        state_transition[I_DOLLAR]      = new Transition_VAR_DOLLAR();
        state_transition[I_DEFAULT]     = new Transition_VAR_DEFAULT();
        _transition[S_VAR] = state_transition;

        // S_VAR_NAME
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_WHITESPACE]  = new Transition_VAR_NAME_WHITESPACE();
        state_transition[I_DOLLAR]      = new Transition_VAR_NAME_DOLLAR();
        state_transition[I_EOL]         = new Transition_VAR_NAME_EOL();
        state_transition[I_DEFAULT]     = new Transition_VAR_NAME_DEFAULT();
        _transition[S_VAR_NAME] = state_transition;

        // S_ESCAPED
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]     = new Transition_ESCAPED_DEFAULT();
        _transition[S_ESCAPED] = state_transition;

        // S_DONE
        state_transition = new Transition[N_INPUT_CLASSES];
        state_transition[I_DEFAULT]     = new Transition_DONE_DEFAULT();
        _transition[S_DONE] = state_transition;
    }

    private void record_token()
    {
        String t = _token.toString();
        if (t.length() > 0)
        {
            if (_glob_expand)
                Util.insert(_out, CommandElement.globExpand(t));
            else
                _out.addElement(t);
            _token.setLength(0);
        }
    }

    private void reset_token()
    {
        _token.setLength(0);
    }

    private void process_variable()
    {
        // _token contains variable name
        String var = _token.toString();
        if (var.length() > 0)
        {
            String value = Util.systemProperty(var);
            if (value != null)
            {
                for (StringTokenizer tokenizer =
                         new StringTokenizer(value, " \t\n\r");
                     tokenizer.hasMoreTokens();)
                {
                    String token = tokenizer.nextToken();
                    if (_glob_expand)
                        Util.insert
                            (_out, 
                             CommandElement.globExpand(token));
                    else
                        _out.addElement(token);
                }
            }
            _token.setLength(0);
        }
    }

    private void error()
    {
        throw new RMShellException("Incorrect syntax");
    }

    private String state_name(int state)
    {
        String name;
        switch (state)
        {
            case S_TEXT:
                name = "S_TEXT";
                break;

            case S_WHITESPACE:
                name = "S_WHITESPACE";
                break;

            case S_VAR:
                name = "S_VAR";
                break;

            case S_VAR_NAME:
                name = "S_VAR_NAME";
                break;

            case S_ESCAPED:
                name = "S_ESCAPED";
                break;

            case S_DONE:
                name = "S_DONE";
                break;

            default:
                name = "Unrecognized state "+state;
                break;
        }
        return name;
    }

    private String char_classification_name(int classification)
    {
        String name;
        switch (classification)
        {
            case I_ESCAPE:
                name = "I_ESCAPE";
                break;

            case I_DOLLAR:
                name = "I_DOLLAR";
                break;

            case I_EOL:
                name = "I_EOL";
                break;

            case I_OTHER:
                name = "I_OTHER";
                break;

            case I_WHITESPACE:
                name = "I_WHITESPACE";
                break;

            default:
                name = "Unrecognized classification: "+classification;
                break;
        }
        return name;
    }

    private static final int S_TEXT        = 0;
    private static final int S_WHITESPACE  = 1;
    private static final int S_VAR         = 2;
    private static final int S_VAR_NAME    = 3;
    private static final int S_ESCAPED     = 4;
    private static final int S_DONE        = 5;
    private static final int N_STATES      = 6;

    private static final int I_ESCAPE          = 0;
    private static final int I_DOLLAR          = 1;
    private static final int I_EOL             = 2;
    private static final int I_OTHER           = 3;
    private static final int I_WHITESPACE      = 4;
    private static final int I_DEFAULT         = 5;
    private static final int N_INPUT_CLASSES   = 6;

    private static final boolean TRACE = false;

    private boolean _handle_escapes;
    private boolean _glob_expand;
    private Transition[][] _transition;
    private char _char;
    private int _char_classification;
    private Vector _out;
    private int _state;
    private StringBuffer _token = new StringBuffer();

    //------------------------------------------------------------

    abstract class Transition
    {
        abstract void apply();
    }

    //------------------------------------------------------------

    class Transition_TEXT_ESCAPE extends Transition
    {
        void apply()
        {
            _token.append(_char);
            _state = S_ESCAPED;
        }
    }

    class Transition_TEXT_DOLLAR extends Transition
    {
        void apply()
        {
            record_token();
            reset_token();
            _state = S_VAR;
        }
    }

    class Transition_TEXT_EOL extends Transition
    {
        void apply()
        {
            record_token();
            _state = S_DONE;
        }
    }

    class Transition_TEXT_WHITESPACE extends Transition
    {
        void apply()
        {
            record_token();
            _state = S_WHITESPACE;
        }
    }

    class Transition_TEXT_DEFAULT extends Transition
    {
        void apply()
        {
            _token.append(_char);
            // Stay in S_TEXT
        }
    }

    class Transition_WHITESPACE_DOLLAR extends Transition
    {
        void apply()
        {
            reset_token();
            _state = S_VAR;
        }
    }

    class Transition_WHITESPACE_EOL extends Transition
    {
        void apply()
        {
            _state = S_DONE;
        }
    }

    class Transition_WHITESPACE_WHITESPACE extends Transition
    {
        void apply()
        {
            // Stay in S_WHITESPACE
        }
    }

    class Transition_WHITESPACE_DEFAULT extends Transition
    {
        void apply()
        {
            _token.append(_char);
            _state = S_TEXT;
        }
    }

    class Transition_SLASH_EOL extends Transition
    {
        void apply()
        {
            error();
        }
    }

    class Transition_SLASH_DEFAULT extends Transition
    {
        void apply()
        {
            _token.append(_char);
            _state = S_TEXT;
        }
    }

    class Transition_VAR_WHITESPACE extends Transition
    {
        void apply()
        {
            error();
        }
    }

    class Transition_VAR_EOL extends Transition
    {
        void apply()
        {
            error();
        }
    }

    class Transition_VAR_DOLLAR extends Transition
    {
        void apply()
        {
            error();
        }
    }

    class Transition_VAR_DEFAULT extends Transition
    {
        void apply()
        {
            _token.append(_char);
            _state = S_VAR_NAME;
        }
    }

    class Transition_VAR_NAME_WHITESPACE extends Transition
    {
        void apply()
        {
            process_variable();
            _state = S_WHITESPACE;
        }
    }

    class Transition_VAR_NAME_DOLLAR extends Transition
    {
        void apply()
        {
            process_variable();
            reset_token();
            _state = S_VAR;
        }
    }

    class Transition_VAR_NAME_EOL extends Transition
    {
        void apply()
        {
            process_variable();
            _state = S_DONE;
        }
    }

    class Transition_VAR_NAME_DEFAULT extends Transition
    {
        void apply()
        {
            _token.append(_char);
            // Stay in VAR_NAME
        }
    }

    class Transition_ESCAPED_DEFAULT extends Transition
    {
        void apply()
        {
            _token.append(_char);
            _state = S_TEXT;
        }
    }

    class Transition_DONE_DEFAULT extends Transition
    {
        void apply()
        {
            error();
        }
    }
}

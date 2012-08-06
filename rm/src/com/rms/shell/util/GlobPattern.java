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

import java.util.*;

import com.rms.shell.*;

public abstract class GlobPattern
{
    public final static int CASE_SENSITIVE   = 1;
    public final static int CASE_INSENSITIVE = 2;

    public static GlobPattern create(String pattern_string)
    {
        int case_sensitivity =
            RMShell.os().caseSensitive()
            ? CASE_SENSITIVE
            : CASE_INSENSITIVE;
        return create(pattern_string, case_sensitivity);
    }

    public static GlobPattern create(String pattern_string,
                                     int case_sensitivity)
    {
        int first_star = pattern_string.indexOf("*");
        int last_star = pattern_string.lastIndexOf("*");
        GlobPattern pattern;
        if (no_glob_characters(pattern_string))
            pattern = 
                new StringPattern(pattern_string, 
                                  case_sensitivity);
        else if (pattern_string.equals("*"))
            pattern = new EverythingPattern();
        else if (first_star == 0 && 
                 last_star == 0 &&
                 no_glob_characters(pattern_string.substring(1)))
            pattern = 
                new SuffixPattern(pattern_string, 
                                  case_sensitivity);
        else if (first_star == pattern_string.length() - 1 &&
                 last_star == first_star &&
                 no_glob_characters(pattern_string.substring
                                    (0, pattern_string.length() - 1)))
        {
            if (pattern_string.charAt
                (pattern_string.length() - 2) == '\\')
                pattern = new StringPattern(pattern_string, 
                                            case_sensitivity);
            else
                pattern = new PrefixPattern(pattern_string, 
                                            case_sensitivity);
        }
        else
            pattern = new GeneralPattern(pattern_string);
        return pattern;
    }

    public abstract boolean match(String string);

    public boolean hasWildcards()
    {
        return true;
    }

    private static boolean no_glob_characters(String s)
    {
        char[] chars = s.toCharArray();
        int i = 0;
        while (i < chars.length)
        {
            char c = chars[i++];
            if (c == '\\')
                i++;
            else if (glob_character(c))
                return false;
        }
        return true;
    }

    private static boolean glob_character(char c)
    {
        boolean glob;
        switch (c)
        {
        case '*':
        case '?':
        case '-':
        case ',':
        case '{':
        case '}':
        case '[':
        case ']':
            glob = true;
            break;
            
        default:
            glob = false;
            break;
        }
        return glob;
    }

    private static boolean glob_start_character(char c)
    {
        boolean glob;
        switch (c)
        {
        case '*':
        case '?':
        case '{':
        case '}':
        case '[':
        case ']':
            glob = true;
            break;
            
        default:
            glob = false;
            break;
        }
        return glob;
    }

    //------------------------------------------------------------

    static final class StringPattern extends GlobPattern
    {
        public String toString()
        {
            return "StringPattern "+_string;
        }

        public StringPattern(String string, int case_sensitivity)
        {
            _string = Util.removeEscapes(string);
            _case_sensitivity = case_sensitivity;
        }

        public boolean match(String string)
        {
            return 
                _case_sensitivity == CASE_SENSITIVE
                ? string.equals(_string)
                : string.equalsIgnoreCase(_string);
        }

        public boolean hasWildcards()
        {
            return false;
        }

        private String _string;
        private int _case_sensitivity;
    }

    //------------------------------------------------------------

    static final class EverythingPattern extends GlobPattern
    {
        public String toString()
        {
            return "EverythingPattern";
        }

        public boolean match(String string)
        {
            return true;
        }
    }

    //------------------------------------------------------------

    static final class SuffixPattern extends GlobPattern
    {
        public String toString()
        {
            return "SuffixPattern: *"+_suffix;
        }

        SuffixPattern(String pattern, int case_sensitivity)
        {
            _suffix = pattern.substring(1);
            if (case_sensitivity == CASE_INSENSITIVE)
                _suffix = _suffix.toLowerCase();
            _case_sensitivity = case_sensitivity;
        }

        public boolean match(String string)
        {
            return 
                _case_sensitivity == CASE_SENSITIVE
                ? string.endsWith(_suffix)
                : string.toLowerCase().endsWith(_suffix);
        }

        private String _suffix;
        private int _case_sensitivity;
    }

    //------------------------------------------------------------

    static final class PrefixPattern extends GlobPattern
    {
        public String toString()
        {
            return "PrefixPattern: "+_prefix+"*";
        }

        PrefixPattern(String pattern, int case_sensitivity)
        {
            _prefix = pattern.substring(0, pattern.length() - 1);
            if (case_sensitivity == CASE_INSENSITIVE)
                _prefix = _prefix.toLowerCase();
            _case_sensitivity = case_sensitivity;
        }

        public boolean match(String string)
        {
            return 
                _case_sensitivity == CASE_SENSITIVE
                ? string.startsWith(_prefix)
                : string.toLowerCase().startsWith(_prefix);
        }

        private String _prefix;
        private int _case_sensitivity;
    }

    //------------------------------------------------------------

    static final class GeneralPattern extends GlobPattern
    {
        public String toString()
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("GeneralPattern:");
            for (Enumeration element_scan = _elements.elements();
                 element_scan.hasMoreElements();)
            {
                buffer.append('\n');
                PatternElement element =
                    (PatternElement) element_scan.nextElement();
                buffer.append("   ");
                buffer.append(element.toString());
            }
            return buffer.toString();
        }

        GeneralPattern(String pattern)
        {
            _pattern = new StringWithEscapes(pattern.toLowerCase());
            _elements = new Vector();
            parse();
        }

        public boolean match(String string)
        {
            _string = string.toLowerCase().toCharArray();
            _string_end = _string.length;
            PatternElement first_pattern_element = 
                (PatternElement)
                _elements.elementAt(0);
            return first_pattern_element.match(0);
        }


        //............................................................

        // Parse pattern -> Vector of PatternElements

        private void parse()
        {
            _pattern_position = 0;
            while (_pattern_position < _pattern.length())
            {
                boolean b =
                    star() ||
                    bracket() ||
                    brace() ||
                    question_mark() ||
                    text();
            }
            pattern_end();
        }

        private boolean star()
        {
            boolean consumed;
            if (_pattern.charAt(_pattern_position) == '*' &&
                !_pattern.escaped(_pattern_position))
            {
                _elements.addElement(new ZeroOrMoreChars());
                _pattern_position++;
                consumed = true;
            }
            else
                consumed = false;
            return consumed;
        }

        private boolean bracket()
        {
            boolean consumed;
            if (_pattern.charAt(_pattern_position) == '[' &&
                !_pattern.escaped(_pattern_position))
            {
                char c;
                OneChar one_char = new OneChar();
                while (!((c = _pattern.charAt(++_pattern_position)) 
                         == ']' && 
                         !_pattern.escaped(_pattern_position)))
                {
                    if (c == '-' &&
                        !_pattern.escaped(_pattern_position))
                    {
                        c = _pattern.charAt(++_pattern_position);
                        one_char.defineRange(c);
                    }
                    else
                        one_char.addChar(c);
                }
                // Get past ]
                _pattern_position++;

                _elements.addElement(one_char);
                consumed = true;
            }
            else
                consumed = false;
            return consumed;
        }

        private boolean brace()
        {
            boolean consumed;
            if (_pattern.charAt(_pattern_position) == '{' &&
                !_pattern.escaped(_pattern_position))
            {
                char c;
                Alternatives alternatives = new Alternatives();
                StringBuffer buffer = new StringBuffer();
                while (!((c = _pattern.charAt(++_pattern_position))
                         == '}' &&
                         !_pattern.escaped(_pattern_position)))
                {
                    if (c == ',')
                    {
                        alternatives.add(buffer.toString());
                        buffer.setLength(0);
                    }
                    else
                        buffer.append(c);
                }
                // Get past }
                _pattern_position++;

                alternatives.add(buffer.toString());
                _elements.addElement(alternatives);
                consumed = true;
            }
            else
                consumed = false;
            return consumed;
        }

        private boolean question_mark()
        {
            boolean consumed;
            if (_pattern.charAt(_pattern_position) == '?' &&
                !_pattern.escaped(_pattern_position))
            {
                _elements.addElement(new AnyChar());
                _pattern_position++;
                consumed = true;
            }
            else
                consumed = false;
            return consumed;
        }

        private boolean text()
        {
            char c;
            StringBuffer buffer = new StringBuffer();
            while (_pattern_position < _pattern.length() &&
                   !GlobPattern.glob_start_character
                   (c = _pattern.charAt(_pattern_position)))
            {
                _pattern_position++;
                buffer.append(c);
            }
            _elements.addElement(new Text(buffer.toString()));
            return true;
        }

        private void pattern_end()
        {
            _elements.addElement(new PatternEnd());
        }

        //............................................................

        private StringWithEscapes _pattern;
        private int _pattern_position;
        private Vector _elements;
        private int _n_elements = 0;
        private char[] _string;
        private int _string_end;

        //------------------------------------------------------------

        abstract class PatternElement
        {
            protected PatternElement()
            {
                _element_id = _n_elements++;
            }

            protected PatternElement next_element()
            {
                int next_id = _element_id + 1;
                if (_next_element == null && next_id < _n_elements)
                    _next_element = 
                        (PatternElement) _elements.elementAt(next_id);
                return _next_element;
            }

            public abstract boolean match(int string_position);

            protected int _element_id;
            protected PatternElement _next_element;
        }

        //............................................................

        class ZeroOrMoreChars extends PatternElement
        {
            public String toString()
            {
                return "ZeroOrMoreChars: *";
            }

            public boolean match(int string_position)
            {
                PatternElement next_element = next_element();
                if (next_element instanceof PatternEnd)
                    return true;
                while (string_position < _string_end)
                {
                    if (next_element.match(string_position))
                        return true;
                    string_position++;
                }
                return false;
            }
        }

        //............................................................

        class OneChar extends PatternElement
        {
            public String toString()
            {
                StringBuffer buffer = new StringBuffer();
                buffer.append("OneChar: [");
                int i = 0;
                while (i < lo().length)
                {
                    char lo = lo()[i];
                    char hi = hi()[i];
                    buffer.append(lo);
                    if (lo != hi)
                    {
                        buffer.append('-');
                        buffer.append(hi);
                    }
                    i++;
                }
                buffer.append(']');
                return buffer.toString();
            }

            public boolean match(int string_position)
            {
                char c = _string[string_position++];
                char[] lo = lo();
                char[] hi = hi();
                for (int i = 0; i < lo.length; i++)
                    if (lo[i] <= c && c <= hi[i] &&
                        next_element().match(string_position))
                        return true;
                return false;
            }

            public void addChar(char c)
            {
                Character character = new Character(c);
                _lo.addElement(character);
                _hi.addElement(character);
            }

            public void defineRange(char c)
            {
                Character character = new Character(c);
                _hi.setElementAt(character, _hi.size() - 1);
            }

            private char[] lo()
            {
                if (_lo_array == null)
                {
                    _lo_array = new char[_lo.size()];
                    for (int i = 0; i < _lo_array.length; i++)
                        _lo_array[i] =
                            ((Character)_lo.elementAt(i)).charValue();
                }
                return _lo_array;
            }

            private char[] hi()
            {
                if (_hi_array == null)
                {
                    _hi_array = new char[_hi.size()];
                    for (int i = 0; i < _hi_array.length; i++)
                        _hi_array[i] =
                            ((Character)_hi.elementAt(i)).charValue();
                }
                return _hi_array;
            }

            private Vector _lo = new Vector();
            private Vector _hi = new Vector();
            private char[] _lo_array;
            private char[] _hi_array;
        }

        //............................................................

        class Alternatives extends PatternElement
        {
            public String toString()
            {
                StringBuffer buffer = new StringBuffer();
                buffer.append("Alternatives: {");
                char[][] alternatives = alternatives();
                for (int i = 0; i < alternatives.length; i++)
                {
                    if (i > 0)
                        buffer.append(',');
                    buffer.append(alternatives[i]);
                }
                buffer.append('}');
                return buffer.toString();
            }
            
            public boolean match(int string_position)
            {
                char[][] alternatives = alternatives();
                for (int i = 0; i < alternatives.length; i++)
                {
                    char[] alternative = alternatives[i];
                    int length = alternative.length;
                    if (string_position + length <= _string_end)
                    {
                        int s = string_position;
                        int a = 0;
                        boolean eq = true;
                        while (a < length && eq)
                            eq = _string[s++] == alternative[a++];
                        if (eq && 
                            next_element().match
                            (string_position + length))
                            return true;
                    }
                }
                return false;
            }

            public void add(String alternative)
            {
                _alternatives.addElement(alternative);
            }

            private char[][] alternatives()
            {
                if (_alternatives_array == null)
                {
                    int n = _alternatives.size();
                    _alternatives_array = new char[n][];
                    for (int i = 0; i < n; i++)
                    {
                        String alternative =
                            (String) _alternatives.elementAt(i);
                        int length = alternative.length();
                        _alternatives_array[i] =
                            alternative.toCharArray();
                    }
                }
                return _alternatives_array;
            }
             
            private Vector _alternatives = new Vector();
            private char[][] _alternatives_array;
        }

        //............................................................

        class AnyChar extends PatternElement
        {
            public String toString()
            {
                return "AnyChar: ?";
            }

            public boolean match(int string_position)
            {
                if (++string_position <= _string_end)
                    return next_element().match(string_position);
                return false;
            }
        }

        //............................................................

        class Text extends Alternatives
        {
            public String toString()
            {
                return "Text: '" + _text + "'";
            }

            public Text(String text)
            {
                add(text);
                _text = text;
            }

            private String _text;
        }

        //............................................................

        class PatternEnd extends PatternElement
        {
            public String toString()
            {
                return "PatternEnd";
            }

            public boolean match(int string_position)
            {
                return string_position == _string_end;
            }
        }
    }
}

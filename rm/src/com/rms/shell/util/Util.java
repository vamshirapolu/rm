
package com.rms.shell.util;

import java.io.File;
import java.util.*;

import com.rms.shell.RMShellException;

public class Util
{
    public static void insert(Vector to, Vector from)
    {
        for (Enumeration from_scan = from.elements();
             from_scan.hasMoreElements();)
            to.addElement(from_scan.nextElement());
    }

    public static String systemProperty(String property)
    {
        return System.getProperty(property);
    }

    public static void systemProperty(String property, String value)
    {
        System.getProperties().put(property, value);
    }

    public static String removeEscapes(String s)
    {
        char[] chars = s.toCharArray();
        int from = 0;
        int to = 0;
        while (from < chars.length)
        {
            char c = chars[from++];
            if (c == '\\')
            {
                if (from == chars.length)
                    throw new RMShellException
                        ("Malformed command-line argument: "+s);
                c = chars[from++];
            }
            chars[to++] = c;
        }
        return new String(chars, 0, to);
    }
}

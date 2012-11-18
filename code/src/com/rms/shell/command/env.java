package com.rms.shell.command;

import java.io.*;
import java.util.*;

import com.rms.shell.*;
import com.rms.shell.util.*;

public class env extends Command
{
    public void execute(String[] args)
    {
        switch (args.length)
        {
            case 0:
                print_all();
                break;
                
            case 1:
                print_filtered(args[0]);
                break;
                
            default:
                break;
        }
    }

    public void usage()
    {
        out().println("env [filter]");
        out().println("    Prints environment values and their values.\n"+
                      "    With no arguments, all variables are printed.\n"+
                      "    Otherwise, print the variables whose names match\n"+
                      "    the filter argument. Environment variables\n"+
                      "    include the contents of System.getProperties().");
    }

    private void print_all()
    {
        print_filtered(null);
    }

    private void print_filtered(String filter)
    {
        String[] env = properties_as_array(filter);
        sort(env);
        print(env);
    }

    private String[] properties_as_array(String filter)
    {
        GlobPattern pattern_matcher = 
            filter == null
            ? null
            : GlobPattern.create(filter);
        Vector qualifying_env = new Vector();
        for (Enumeration scan = properties().keys(); scan.hasMoreElements();)
        {
            String var = (String) scan.nextElement();
            if (filter == null || pattern_matcher.match(var))
            {
                String value = property(var);
                qualifying_env.addElement(var + '=' + value);
            }
        }
        String[] result = new String[qualifying_env.size()];
        qualifying_env.copyInto(result);
        return result;
    }

    private void sort(String[] env)
    {
        Sorter sorter = new Sorter
            (new Sorter.Comparer()
             {
                 public int compare(Object x, Object y)
                 {
                     return ((String)x).compareTo((String)y);
                 }
             });
        sorter.sort(env);
    }

    private void print(String[] env)
    {
        for (int i = 0; i < env.length; i++)
            out().println(env[i]);
    }
}

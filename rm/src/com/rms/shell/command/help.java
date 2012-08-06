
package com.rms.shell.command;

import java.lang.reflect.*;

import com.rms.shell.*;
import com.rms.shell.util.*;

public class help extends Command
{
    public void execute(String[] args)
        throws
        ClassNotFoundException,
        IllegalAccessException
    {
        process_args(args);
        print_command_usage();
    }

    public void usage()
    {
        out().println("help command");
        out().println("    Provides usage information for builtin RMShell\n"+
                      "    commands.");
    }

    private void process_args(String[] args)
    {
        _command =
            args.length == 0
            ? "help"
            : args[0];
    }

    private void print_command_usage()
    {
        try
        {
            String class_name = "com.rms.shell.command." + _command;
            Class klass = Class.forName(class_name);
            Command command = (Command) klass.newInstance();
            command.usage();
        }
        catch (ClassNotFoundException e)
        {
            System.out.println(_command+" is not a RMShell command.");
        }
        catch (InstantiationException e)
        { Assertion.check(false); }
        catch (IllegalAccessException e)
        { Assertion.check(false); }
    }

    private String _command;
}

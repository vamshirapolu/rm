
package com.rms.shell.command;

import java.io.*;

import com.rms.shell.*;
import com.rms.shell.commandline.Variable;

public class set extends Command
{
    public void execute(String[] args)
    {
        String var = args[0];
        // Try creating the variable to validate the variable
        // name.
        Variable.create('$' + var);
        if (args.length == 1)
            removeProperty(var);
        else
        {
            String value = args[1];
            property(var, value);
            out().println(var+"="+value);
        }
    }

    public void usage()
    {
        out().println("set variable [value]");
        out().println("    If a value is specified, then the variable's\n"+
                      "    value is set to the specified value. Otherwise,\n"+
                      "    the variable is removed.");
    }
}

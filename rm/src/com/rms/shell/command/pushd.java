
package com.rms.shell.command;

import java.io.*;

import com.rms.shell.*;

public class pushd extends Command
{
    public void execute(String[] args)
        throws IOException
    {
        if (args.length == 0)
            swap_current_and_top();
        else
        {
            push_current_directory();
            go_to_new_directory(args);
        }
    }

    public void usage()
    {
        out().println("pushd [directory]");
        out().println("    If no directory is specified, then the current\n"+
                      "    directory and the top of the directory stack\n"+
                      "    are swapped. Otherwise, the current directory is\n"+
                      "    pushed onto the directory stack, and the current\n"+
                      "    directory is then changed to the specified\n"+
                      "    directory. In both cases, the environment\n"+
                      "    variables com.maxis.rms.shell.dir and com.maxis.rms.shell.dir_stack\n"+
                      "    are modified.");
    }

    private void swap_current_and_top()
    {
        String dir_stack = property("com.maxis.rms.shell.dir_stack");
        String current_dir = property("com.maxis.rms.shell.dir");
        if (dir_stack == null)
            out().println(current_dir);
        else
        {
            int space = dir_stack.indexOf(" ");
            if (space > 0)
            {
                String top = dir_stack.substring(0, space);
                String remainder = dir_stack.substring(space);
                property("com.maxis.rms.shell.dir", top);
                property("com.maxis.rms.shell.dir_stack", current_dir + remainder);
            }
            else
            {
                property("com.maxis.rms.shell.dir", dir_stack);
                property("com.maxis.rms.shell.dir_stack", current_dir);
            }
            out().println(property("com.maxis.rms.shell.dir"));
        }
    }

    private void push_current_directory()
    {
        String dir_stack = property("com.maxis.rms.shell.dir_stack");
        String current_dir = property("com.maxis.rms.shell.dir");
        dir_stack =
            dir_stack == null
            ? current_dir
            : current_dir + ' ' + dir_stack;
        property("com.maxis.rms.shell.dir_stack", dir_stack);
    }

    private void go_to_new_directory(String[] args)
        throws IOException
    {
        cd cd_command = new cd();
        cd_command.execute(args);
    }
}

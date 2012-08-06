
package com.rms.shell.command;

import java.io.*;

import com.rms.shell.*;

public class popd extends Command
{
    public void execute(String[] args)
        throws IOException
    {
        String new_dir = pop_dir_stack();
        if (new_dir != null)
            go_to_new_directory(new_dir);
        else
            out().println(property("com.maxis.rms.shell.dir"));
    }

    public void usage()
    {
        out().println("popd");
        out().println("    Pops the directory stack. The current directory\n"+
                      "    is changed to the directory that is removed from\n"+
                      "    the stack, and the directory stack, stored in\n"+
                      "    com.maxis.rms.shell.dir, is modified.");
    }

    private String pop_dir_stack()
    {
        String dir_stack = property("com.maxis.rms.shell.dir_stack");
        String dir = null;
        if (dir_stack != null)
        {
            int colon = dir_stack.indexOf(" ");
            if (colon > 0)
            {
                dir = dir_stack.substring(0, colon);
                dir_stack = dir_stack.substring(colon + 1);
                property("com.maxis.rms.shell.dir_stack", dir_stack);
            }
            else
            {
                dir = dir_stack;
                removeProperty("com.maxis.rms.shell.dir_stack");
            }
        }
        return dir;
    }

    private void go_to_new_directory(String new_dir)
        throws IOException
    {
        cd cd_command = new cd();
        cd_command.execute(new String[] { new_dir });
    }
}

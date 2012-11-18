
package com.rms.shell;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.rms.shell.commandline.*;
import com.rms.shell.util.*;

public class RMShell
{
    public static final String RMSHELL_BUFFER         = "com.maxis.rms.shell.buffer";
    public static final String RMSHELL_PAGE           = "com.maxis.rms.shell.page";
    public static final String RMSHELL_PROMPT         = "com.maxis.rms.shell.prompt";
    public static final String RMSHELL_COLUMNS        = "com.maxis.rms.shell.columns";
    public static final String RMSHELL_LINES          = "com.maxis.rms.shell.lines";
    public static final String RMSHELL_TIME_COMMANDS  = "com.maxis.rms.shell.time_commands";
    public static final String RMSHELL_DIR            = "com.maxis.rms.shell.dir";
    public static final String RMSHELL_HISTORY_SIZE   = "com.maxis.rms.shell.history_size";
    public static final String RMSHELL_FLAG           = "com.maxis.rms.shell.flag";

    
    // Interface for interactive usage

    public static void main(String[] args)
        throws Exception
    {
        new RMShell(true /* interactive */);
    }


    // Interface for programmatic usage

    public static RMShell create()
        throws Exception
    {
        return new RMShell(false /* not interactive */);
    }

    public void runCommand(String command)
        throws Exception
    {
        process_command_line(command);
    }


    // For use by RMShell

    public static OS os()
    {
        return _os;
    }

    public static void okToExit()
    {
        _security_manager.okToExit();
    }


    // For use by this class

    private RMShell(boolean interactive)
        throws Exception
    {
        initialize(interactive);
        // The command com.rms.shell.command.exit calls System.exit.
        if (_interactive)
            while (true)
                process_command_line();
    }

    private void initialize(boolean interactive)
        throws Exception
    {
        _security_manager = new RMShellSecurityManager();
        System.setSecurityManager(_security_manager);

        _interactive = interactive;

        // Set up object for dealing with platform dependencies.
        _os = OS.create();

        // Set up shell input
        _shell_input = new BufferedReader
            (new InputStreamReader(System.in));

        // Set up environment
        initialize_environment();
    }

    private void initialize_environment()
    {
        Util.systemProperty(RMSHELL_PROMPT, "mrms»");
        Util.systemProperty(RMSHELL_PAGE, "true");
        Util.systemProperty(RMSHELL_COLUMNS, "60");
        Util.systemProperty(RMSHELL_LINES, "19");
        Util.systemProperty(RMSHELL_BUFFER, "false");
        Util.systemProperty(RMSHELL_FLAG, ":");
        Util.systemProperty
            (RMSHELL_DIR,
             Path.osToRMSHELL(System.getProperty("user.dir")));
        Util.systemProperty(RMSHELL_HISTORY_SIZE, "100");
    }
        
    private void process_command_line()
        throws Exception
    {
        String text = read_command_line();
        try
        { process_command_line(text); }
        catch (Exception e)
        { System.err.println("Illegal syntax."); }
    }

    private void process_command_line(String command_line)
        throws Exception
    {
        JobThread job_thread = null;
        job_thread = JobThread.create(command_line);
        job_thread.startWork();
        job_thread.waitIfForegroundJob();
    }

    private String read_command_line()
        throws IOException
    {
        String line = null;
        do
        {
            //System.out.print((History.only().lastCommandId() + 1)+ System.getProperty("com.maxis.rms.shell.prompt")+" ");
        	System.out.print(System.getProperty("com.maxis.rms.shell.prompt")+" ");
            System.out.flush();
            line = readLine();
        }
        while (line == null || line.trim().length() == 0);
        // Trim to get rid of cr and or lf at end of line.
        return line.trim();
    }

    // Needed because BufferedReader.readLine doesn't work
    // on Psion 5mx
    public String readLine() 
        throws java.io.IOException
    {
        StringBuffer s = new StringBuffer();
        char c;
        while ((c = (char)System.in.read())!= '\n')
        {
            if (c == '\b' && s.length() > 0)
                s.setLength(s.length() - 1);
            else
                s.append(c);
        }
        return s.toString();
    }

    private static OS _os;
    private static RMShellSecurityManager _security_manager;

    private BufferedReader _shell_input;
    private boolean _interactive;
}

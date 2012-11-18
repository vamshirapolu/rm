
package com.rms.shell;

public class RMShellCommandExit extends SecurityException
{
    public RMShellCommandExit(int exit_code)
    {
        super("System.exit("+exit_code+")");
        _exit_code = exit_code;
    }

    public int exitCode()
    {
        return _exit_code;
    }

    private int _exit_code;
}

package com.rms.shell;

import java.io.*;
import java.util.*;

public class RMShellException extends RuntimeException
{
    public RMShellException(String message)
    {
        super(message);
    }

    public RMShellException(Vector exceptions)
    {
        super("Command execution failed.");
        _exceptions = exceptions;
    }

    public void describe(PrintStream out)
    {
        if (_exceptions == null)
            printStackTrace(out);
        else
        {
            for (Enumeration exception_scan = _exceptions.elements();
                 exception_scan.hasMoreElements();)
            {
                Throwable exception = (Throwable) exception_scan.nextElement();
                if (exception instanceof com.rms.shell.command.exit.ReallyExit)
                {
                    RMShell.okToExit();
                    System.exit(0);
                }
                else
                    exception.printStackTrace(out);
            }
        }
    }

    private Vector _exceptions;
}

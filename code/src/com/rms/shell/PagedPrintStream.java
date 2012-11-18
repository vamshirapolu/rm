
package com.rms.shell;

import java.io.*;

class PagedPrintStream extends PrintStream
{
    public PagedPrintStream(PrintStream stream)
    {
        super(stream);
    }

    public void println()
    {
        super.println();
        check_page_end();
    }

    public void println(boolean x)
    {
        super.println(x);
        check_page_end();
    }
    
    public void println(char x)
    {
        super.println(x);
        check_page_end();
    }
    
    public void println(char[] x)
    {
        super.println(x);
        check_page_end();
    }
    
    public void println(double x)
    {
        super.println(x);
        check_page_end();
    }
    
    public void println(float x)
    {
        super.println(x);
        check_page_end();
    }
    
    public void println(int x)
    {
        super.println(x);
        check_page_end();
    }
    
    public void println(long x)
    {
        super.println(x);
        check_page_end();
    }
    
    public void println(Object x)
    {
        super.println(x);
        check_page_end();
    }
    
    public void println(String x)
    {
        super.println(x);
        check_page_end();
    }

    private void check_page_end()
    {
        String page_size_string = System.getProperty(RMShell.RMSHELL_LINES);
        int page_size =
            page_size_string == null
            ? Integer.MAX_VALUE
            : Integer.parseInt(page_size_string);
        if (++_lines_printed == page_size)
        {
            super.print("Press Enter for more.");
            super.flush();
            try
            {
                System.in.read();
            }
            catch (IOException e) {}
            super.println();
            _lines_printed = 0;
        }
    }

    private int _lines_printed;
    private boolean _done = false;
}

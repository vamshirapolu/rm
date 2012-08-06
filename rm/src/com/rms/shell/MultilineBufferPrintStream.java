package com.rms.shell;

import java.io.*;

import com.rms.shell.util.*;

public class MultilineBufferPrintStream extends PrintStream
{
    public MultilineBufferPrintStream(PrintStream stream)
    {
        super(stream);
        _buffer = new StringBuffer(BUFFER_SIZE);
    }

    public void flush()
    {
        flush_to_stream();
        super.flush();
    }

    public void close()
    {
        flush();
        super.close();
    }

    public boolean checkError()
    {
        flush_to_stream();
        return super.checkError();
    }

    public void print(boolean x)
    {
        String s = x ? "true" : "false";
        if (_buffer.length() + s.length() >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
    }

    public void print(char x)
    {
        if (_buffer.length() + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(x);
    }

    public void print(int x)
    {
        String s = Integer.toString(x);
        if (_buffer.length() + s.length() >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
    }

    public void print(long x)
    {
        String s = Long.toString(x);
        if (_buffer.length() + s.length() >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
    }

    public void print(float x)
    {
        String s = Float.toString(x);
        if (_buffer.length() + s.length() >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
    }

    public void print(double x)
    {
        String s = Double.toString(x);
        if (_buffer.length() + s.length() >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
    }

    public void print(char[] x)
    {
        if (_buffer.length() + x.length >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(x);
    }

    public void print(String x)
    {
        if (_buffer.length() + x.length() >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(x);
    }

    public void print(Object x)
    {
        String s = x.toString();
        if (_buffer.length() + s.length() >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
    }

    public void println()
    {
        if (_buffer.length() + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append('\n');
    }

    public void println(boolean x)
    {
        String s = x ? "true" : "false";
        if (_buffer.length() + s.length() + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
        _buffer.append('\n');
    }

    public void println(char x)
    {
        if (_buffer.length() + 2 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(x);
        _buffer.append('\n');
    }

    public void println(int x)
    {
        String s = Integer.toString(x);
        if (_buffer.length() + s.length() + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
        _buffer.append('\n');
    }

    public void println(long x)
    {
        String s = Long.toString(x);
        if (_buffer.length() + s.length() + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
        _buffer.append('\n');
    }

    public void println(float x)
    {
        String s = Float.toString(x);
        if (_buffer.length() + s.length() + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
        _buffer.append('\n');
    }

    public void println(double x)
    {
        String s = Double.toString(x);
        if (_buffer.length() + s.length() + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
        _buffer.append('\n');
    }

    public void println(char[] x)
    {
        if (_buffer.length() + x.length + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(x);
        _buffer.append('\n');
    }

    public void println(String x)
    {
        if (_buffer.length() + x.length() + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(x);
        _buffer.append('\n');
    }

    public void println(Object x)
    {
        String s = x.toString();
        if (_buffer.length() + s.length() + 1 >= BUFFER_SIZE)
            flush_to_stream();
        _buffer.append(s);
        _buffer.append('\n');
    }

    private void flush_to_stream()
    {
        super.print(_buffer.toString());
        _buffer.setLength(0);
    }


    private static final int BUFFER_SIZE = 500;

    private StringBuffer _buffer;
}

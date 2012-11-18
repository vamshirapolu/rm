// RMShell
// Copyright (C) 2000 Jack A. Orenstein
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of
// the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.
// 
// Jack A. Orenstein  jao@mediaone.net

package com.rms.shell.commandline;

import java.lang.reflect.*;
import java.util.*;

import com.rms.shell.*;
import com.rms.shell.util.*;

public class Command extends AbstractCommand
{
    // Object interface

    public String toString()
    {
        return to_string();
    }


    // PooledThread interface

    public void execute()
        throws Exception
    {
        // Try running the execute method
        try
        {
            _command_type = COMMAND_TYPE_EXECUTE;
            execute_method(_command_class)
                .invoke(_command_object, 
                        new Object[] { _args });
        }
        catch (NoSuchMethodException no_execute)
        {
            // No execute method; try main.
            _command_type = COMMAND_TYPE_MAIN;
            main_method(_command_class)
                .invoke(null, new Object[] { _args });
        }
        finally
        {
            finish();
            _command_object = null;
            _command_elements = null;
            _command_class = null;
            _command = null;
            _args = null;
        }
    }


    // Command interface

    public static Command create()
    {
        Command command = (Command) _command_threads.take();
        Assertion.check(command.isAlive());
        command.initialize();
        return command;
    }
    
    public Command addElement(CommandElement element)
    {
        _command_elements.addElement(element);
        return this;
    }

    public static void connectOutputToInput
    (Command producer, Command consumer)
    {
        if (producer._command_type == COMMAND_TYPE_MAIN)
            throw new RMShellException
                ("Can't pipe output of "+producer._command+
                 " because it is invoked using "+
                 "main(String[], not execute(String[]).");
        if (consumer._command_type == COMMAND_TYPE_MAIN)
            throw new RMShellException
                ("Can't pipe output of "+consumer._command+
                 " because it is invoked using "+
                 "main(String[], not execute(String[]).");
        try
        {
            connect_output_to_input_method
                (producer._command_class)
                .invoke(null, 
                        new Object[] 
                        { 
                            producer._command_object, 
                            consumer._command_object
                        });
        }
        catch (IllegalAccessException e)
        { Assertion.check(false); }
        catch (InvocationTargetException e)
        { Assertion.check(false); }
        catch (NoSuchMethodException e)
        { Assertion.check(false); }
    }

    public void prepareToExecute()
    {
        expand_command_elements();
        try
        {
            _command_class = _class_finder.find(_command);
        }
        catch (ClassNotFoundException e)
        {
            throw new RMShellException
                ("Couldn't find class implementing "+_command);
        }
        Exception failure = null;
        try
        {
            _command_object = _command_class.newInstance();
        }
        catch (InstantiationException e)
        { failure = e; }
        catch (IllegalAccessException e)
        { failure = e; }
        if (failure != null)
            throw new RMShellException
                (_command+" must be a non-abstract, "+
                 "non-interface class "+
                 "providing a 'public void execute"+
                 "(String[]) throws Exception' "+
                 "method, or a 'public static void main"+
                 "(String[])' method. "+
                 "Caught "+failure.getClass().getName()+
                 " while creating "+
                 "instance of "+_command_class.getName()+".");
    }


    // For use by this package
    
    String expandCommandReference()
    {
        return to_string();
    }


    // For use by this class

    private void initialize()
    {
        _command_elements = new Vector();
        _command_class = null;
        _command_object = null;
        _command = null;
        _args = null;
        _something_to_do = false;
        _command_type = COMMAND_TYPE_UNKNOWN;
    }

    private void expand_command_elements()
    {
        Vector expanded = new Vector();
        for (Enumeration element_scan =
                 _command_elements.elements();
             element_scan.hasMoreElements();)
        {
            CommandElement element =
                (CommandElement) element_scan.nextElement();
            Vector expanded_element = element.expand();
            Util.insert(expanded, expanded_element);
        }
        _command = (String) expanded.elementAt(0);
        expanded.removeElementAt(0);
        _args = new String[expanded.size()];
        expanded.copyInto(_args);
        description("Command: "+to_string());
    }

    private void finish()
    {
        if (_command_type == COMMAND_TYPE_EXECUTE)
        {
            try
            {
                finish_method(_command_class)
                    .invoke(_command_object, new Object[0]);
            }
            catch (NoSuchMethodException e)
            { Assertion.check(false); }
            catch (IllegalAccessException e)
            { Assertion.check(false); }
            catch (InvocationTargetException e)
            { Assertion.check(false); }
        }
    }

    private static Method execute_method(Class klass)
        throws Exception
    {
        return klass.getMethod("execute", _execute_signature);
    }

    private static Method main_method(Class klass)
        throws NoSuchMethodException
    {
        return klass.getMethod("main", _main_signature);
    }

    private static Method finish_method(Class klass)
        throws NoSuchMethodException
    {
        return klass.getMethod("finish", _finish_signature);
    }

    private static Method connect_output_to_input_method
    (Class klass)
        throws NoSuchMethodException
    {
        return klass.getMethod
            ("connectOutputToInput", _connect_signature);
    }

    private String to_string()
    {
        if (_command == null)
            return super.toString();
        StringBuffer buffer = new StringBuffer();
        buffer.append(_command);
        for (int i = 0; i < _args.length; i++)
        {
            buffer.append(' ');
            buffer.append(_args[i]);
        }
        return buffer.toString();
    }

    // Don't let Commands be created by constructor
    private Command(){}

    private static Pool _command_threads = 
        new Pool("Commands",
                 new Pool.ElementFactory()
                 {
                     public Pool.Element create()
                     {
                         Command command = new Command();
                         command.start();
                         return command;
                     }
                 });

    private static final int COMMAND_TYPE_UNKNOWN = 0;
    private static final int COMMAND_TYPE_EXECUTE = 1;
    private static final int COMMAND_TYPE_MAIN    = 2;

    private static ClassFinder _class_finder =
        new ClassFinder();
    private static Class[] _execute_signature =
        new Class[] { String[].class };
    private static Class[] _main_signature = _execute_signature;
    private static Class[] _connect_signature =
        new Class[] { Object.class, Object.class };
    private static Class[] _finish_signature = new Class[] {};

    private Vector _command_elements = new Vector();
    private Class _command_class;
    private Object _command_object;
    private String _command;
    private String[] _args;
    private boolean _something_to_do = false;
    private int _command_type = COMMAND_TYPE_UNKNOWN;
}

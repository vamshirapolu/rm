
package com.rms.shell;

public class RMShellSecurityManager extends SecurityManager
{
    // SecurityManager interface

    public void checkCreateClassLoader()
    {}

    public void checkAccess(Thread thread)
    {}

    public void checkAccess(ThreadGroup thread_group)
    {}

    public void checkExit(int exit_code)
    {
        if (!_ok_to_exit)
            throw new RMShellCommandExit(exit_code);
    }

    public void checkExec(String s)
    {}

    public void checkLink(String s)
    {}

    public void checkRead(java.io.FileDescriptor fd)
    {}

    public void checkRead(String s)
    {}

    public void checkRead(String s, Object o)
    {}

    public void checkWrite(java.io.FileDescriptor fd)
    {}

    public void checkWrite(String s)
    {}

    public void checkDelete(String s)
    {}

    public void checkConnect(String s, int i)
    {}

    public void checkConnect(String s, int i, Object o)
    {}

    public void checkListen(int i)
    {}

    public void checkAccept(String s, int i)
    {}

    public void checkMulticast(java.net.InetAddress a)
    {}

    public void checkMulticast(java.net.InetAddress a, byte b)
    {}

    public void checkPropertiesAccess()
    {}

    public void checkPropertyAccess(String s)
    {}

    public boolean checkTopLevelWindow(Object o)
    {
        return true;
    }

    public void checkPrintJobAccess()
    {}

    public void checkSystemClipboardAccess()
    {}

    public void checkAwtEventQueueAccess()
    {}

    public void checkPackageAccess(String s)
    {}

    public void checkPackageDefinition(String s)
    {}

    public void checkSetFactory()
    {}

    public void checkMemberAccess(Class c, int i)
    {}

    public void checkSecurityAccess(String s)
    {}


    // RMShellSecurityManager interface

    public void okToExit()
    {
        _ok_to_exit = true;
    }


    private boolean _ok_to_exit = false;
}

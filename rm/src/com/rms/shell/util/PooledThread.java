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

package com.rms.shell.util;

import java.lang.reflect.*;

public abstract class PooledThread 
    extends Thread
    implements Pool.Element
{
    // Thread interface

    public final void run()
    {
        while (true)
        {
            wait_for_something_to_do();
            try 
            { execute(); }
            catch (Throwable t)
            { _outcome.terminationReason(t); }
            finally
            { cleanup(); }
        }
    }

    //............................................................

    // Pool.Element interface

    // okToLeavePool and okToEnterPool are implemented
    // by subclasses to guarantee the integrity of 
    // the thread pool. These methods are called by
    // Pool. pool() and pool(Pool) are called by users
    // of PooledThread to associate the PooledThread 
    // with a Pool, and to get a PooledThread's Pool.

    public boolean okToLeavePool()
    {
        return _outcome == null;
    }

    public boolean okToEnterPool()
    {
        return _outcome == null;
    }

    public final Pool pool()
    {
        return _pool;
    }

    public final void pool(Pool pool)
    {
        _pool = pool;
    }

    //............................................................

    // PooledThread interface

    // User of a PooledThread calls startWork and eventually
    // waitForCompletion. terminationReason may be called
    // any time after startWork (it calls waitForCompletion).
    // Sometime after completion (guaranteed by calling
    // waitForCompletion or terminationReason), cleanup
    // must be called (or the PooledThread never goes back
    // to the pool).

    // preExecute and execute are called by PooledThread. 
    // preExecute may be, and execute must be defined
    // by subclasses. cleanup is called by the user
    // of the PooledThread and may be overridden by
    // a subclass. If cleanup is overridden, the
    // subclass' cleanup method must invoke
    // super.cleanup as its last action.

    public PooledThread()
    {
        setDaemon(true);
    }

    public final synchronized Outcome startWork()
        throws Exception
    {
        preExecute();
        _outcome = new Outcome();
        notifyAll();
        return _outcome;
    }

    public final String description()
    {
        return _description;
    }

    public final void description(String description)
    {
        _description = description;
    }

    protected abstract void execute()
        throws Exception;

    protected void preExecute()
        throws Exception
    {}

    //............................................................

    // For use by this class

    private synchronized void wait_for_something_to_do()
    {
        while (_outcome == null)
        {
            try { wait(WAIT_TIME); }
            catch (InterruptedException e) {}
        }
    }

    private void cleanup()
    {
        // I think it's OK to reuse a thread that has been interrupted.
        // If not, then check _outcome.terminationReason() for instanceof
        // InterruptedException before calling putBack.
        _outcome.running(false);
        _outcome = null;
        _pool.putBack(this);
    }

    //............................................................

    // Representation

    private static final int WAIT_TIME = 100;
    private static final boolean TRACE = true;

    private Pool _pool;
    private String _description;
    private Outcome _outcome;

    //------------------------------------------------------------

    public static class Outcome
    {
        public Throwable terminationReason()
        {
            wait_for_completion();
            return _termination_reason;
        }

        public void waitForCompletion()
        {
            wait_for_completion();
        }

        void running(boolean running)
        {
            _running = running;
        }

        void terminationReason(Throwable termination_reason)
        {
            if (_termination_reason == null)
                _termination_reason = unwrap(termination_reason);
        }

        public synchronized void wait_for_completion()
        {
            while (_running)
                try { wait(WAIT_TIME); }
                catch (InterruptedException e) {}
        }

        private Throwable unwrap(Throwable t)
        {
            if (t instanceof InvocationTargetException)
            {
                InvocationTargetException ite =
                    (InvocationTargetException) t;
                t = ite.getTargetException();
            }
            return t;
        }

        private boolean _running = true;
        private Throwable _termination_reason;
    }
}

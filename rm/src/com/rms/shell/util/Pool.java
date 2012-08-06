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

import java.util.*;

/*
 * This implementation does not check that an object passed to putBack
 * was originally handed out by take. It would be nice to fix this.
 */

public class Pool
{
    public Pool(String name,
                ElementFactory factory)
    {
        _name = name;
        _factory = factory;
    }

    public synchronized Element take()
    {
        if (_available.size() == 0)
        {
            Element element = _factory.create();
            element.pool(this);
            _available.addElement(element);
        }
        Element element = (Element) _available.lastElement();
        Assertion.check(element.pool() == this);
        Assertion.check(element.okToLeavePool());
        _available.removeElementAt(_available.size() - 1);
        return element;
    }

    public synchronized void putBack(Element element)
    {
        Assertion.check(element.pool() == this);
        Assertion.check(element.okToEnterPool());
        _available.addElement(element);
    }

    private String _name;
    private ElementFactory _factory;
    private Vector _available = new Vector();

    //------------------------------------------------------------

    public abstract static class ElementFactory
    {
        public abstract Element create();
    }

    //------------------------------------------------------------

    public interface Element
    {
        boolean okToLeavePool();
        boolean okToEnterPool();
        Pool pool();
        void pool(Pool pool);
    }
}

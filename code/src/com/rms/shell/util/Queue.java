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

import com.rms.shell.RMShellException;

public class Queue
{
    public Queue()
    {
        _contents = new Object[INITIAL_SIZE + 1];
        _front = 0;
        _back = 0;
    }

    public void add(Object o)
    {
        check_size();
        _contents[_back] = o;
        _back = next(_back);
    }

    public Object remove()
    {
        if (empty())
            throw new RMShellException
                ("Can't apply remove to empty Queue");
        Object o = _contents[_front];
        _contents[_front] = null;
        _front = next(_front);
        return o;
    }

    public boolean empty()
    {
        return _front == _back;
    }

    private int next(int p)
    {
        if (++p == _contents.length)
            p = 0;
        return p;
    }

    private void check_size()
    {
        if (next(_back) == _front)
        {
            int new_length = _contents.length * GROWTH_FACTOR;
            Object[] new_contents = new Object[new_length];
            int from = _front;
            int to = 0;
            int n = _contents.length - 1;
            for (int i = 0; i < n; i++)
            {
                new_contents[to++] = _contents[from];
                from = next(from);
            }
            _contents = new_contents;
            _front = 0;
            _back = n;
        }
    }

    private static final int INITIAL_SIZE = 20;
    private static final int GROWTH_FACTOR = 2;

    private Object[] _contents;
    private int _front;
    private int _back;
}

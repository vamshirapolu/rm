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

import com.rms.shell.util.*;

public abstract class AbstractCommand extends PooledThread
{
    // Command interface

    public Command addElement(CommandElement element)
    {
        Assertion.check(false);
        return null;
    }

    public void prepareToExecute()
    {
        Assertion.check(false);
    }

    public void execute()
        throws Exception
    {
        Assertion.check(false);
    }

    abstract String expandCommandReference();
}

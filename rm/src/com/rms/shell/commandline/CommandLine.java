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

import java.util.*;

import com.rms.shell.util.*;

public class CommandLine
{
    public CommandLine addPipeline(Pipeline pipeline)
    {
        _pipelines.addElement(pipeline);
        return this;
    }

    public void executeInBackground()
    {
        _background_job = true;
    }

    public boolean backgroundJob()
    {
        return _background_job;
    }

    public void execute()
        throws Exception
    {
        for (Enumeration pipeline_scan = _pipelines.elements();
             pipeline_scan.hasMoreElements();)
        {
            Pipeline pipeline =
                (Pipeline) pipeline_scan.nextElement();
            pipeline.execute();
            Util.insert(_exceptions, pipeline.exceptions());
        }
    }

    public void kill()
    {
        for (Enumeration pipeline_scan = _pipelines.elements();
             pipeline_scan.hasMoreElements();)
        {
            Pipeline pipeline =
                (Pipeline) pipeline_scan.nextElement();
            pipeline.kill();
        }
    }
    
    public boolean containsCommandReferences()
    {
        Vector new_pipelines = new Vector();
        for (Enumeration pipeline_scan = _pipelines.elements();
             pipeline_scan.hasMoreElements();)
        {
            Pipeline pipeline =
                (Pipeline) pipeline_scan.nextElement();
            if (pipeline.containsCommandReferences())
                return true;
        }
        return false;
    }

    public String expandCommandReferences()
    {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (Enumeration pipeline_scan = _pipelines.elements();
             pipeline_scan.hasMoreElements();)
        {
            if (first)
                first = false;
            else
                buffer.append("; ");
            Pipeline pipeline =
                (Pipeline) pipeline_scan.nextElement();
            buffer.append(pipeline.expandCommandReferences());
        }
        return buffer.toString();
    }

    public Vector exceptions()
    {
        return _exceptions;
    }

    private Vector _pipelines = new Vector();
    private boolean _background_job = false;
    private Vector _exceptions = new Vector();
}

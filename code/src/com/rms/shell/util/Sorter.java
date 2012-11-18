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

public class Sorter
{
    public Sorter(Comparer comparer)
    {
        _comparer = comparer;
    }

    public synchronized void sort(Object[] objects)
    {
        int n = objects.length;
        _objects = objects;
        _objects_temp = new Object[n];
        sort_partition(0, n);
    }

    private void sort_partition(int from, int to)
    {
        int n = to - from;
        if (n < 2)
            return;
        if (n == 2)
            sort_two(from, from + 1);
        else if (n == 3)
        {
            sort_two(from, from + 1);
            sort_two(from + 1, from + 2);
            sort_two(from, from + 1);
        }
        else if (n == 4)
        {
            sort_two(from, from + 1);
            sort_two(from + 1, from + 2);
            sort_two(from + 2, from + 3);
            sort_two(from, from + 1);
            sort_two(from + 1, from + 2);
            sort_two(from, from + 1);
        }
        else
        {
            int half = n / 2;
            sort_partition(from, from + half);
            sort_partition(from + half, to);
            merge(from, from + half, to);
        }
    }

    private void merge(int from, int mid, int to)
    {
        int i = from;
        int j = mid;
        int m = from;
        while (i < mid && j < to)
        {
            Object ki = _objects[i];
            Object kj = _objects[j];
            int c = _comparer.compare(ki, kj);
            if (c < 0)
                copy_to_temp(i++, m++);
            else if (c > 0)
                copy_to_temp(j++, m++);
            else
            {
                // Copy all the objects in the low (i) partition,
                // then all the objects in the high (j) partition.
                while (i < mid && _comparer.compare(_objects[i], ki) == 0)
                    copy_to_temp(i++, m++);
                while (j < to && _comparer.compare(_objects[j], kj) == 0)
                    copy_to_temp(j++, m++);
            }
        }
        while (i < mid)
            copy_to_temp(i++, m++);
        while (j < to)
            copy_to_temp(j++, m++);
        System.arraycopy(_objects_temp, from, _objects, from, to - from);
    }

    private void sort_two(int i, int j)
    {
        Object ki = _objects[i];
        Object kj = _objects[j];
        if (_comparer.compare(ki, kj) > 0)
        {
            Object o = _objects[i];
            _objects[i] = _objects[j];
            _objects[j] = o;
        }
    }

    private void copy_to_temp(int i, int j)
    {
        _objects_temp[j] = _objects[i];
    }

    private Comparer _comparer;
    private Object[] _objects;
    private Object[] _objects_temp;

    public static interface Comparer
    {
        public abstract int compare(Object x, Object y);
    }
}

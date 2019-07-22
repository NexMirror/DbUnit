/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.dbunit.util.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

/**
 * @author Felipe Leme (dbunit@felipeal.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Aug 25, 2005
 */
public abstract class AbstractSearchTestCase extends TestCase
{
    protected static final String A = "A";
    protected static final String B = "B";
    protected static final String C = "C";
    protected static final String D = "D";
    protected static final String E = "E";
    protected static final String F = "F";

    // fixtures
    protected final Map fEdgesPerNodeMap = new HashMap();

    protected final Set fAllEdgesSet = new HashSet();

    protected final Set fExpectedOutput = new LinkedHashSet();

    protected final Set fInput = new HashSet();

    protected final DepthFirstSearch fSearch = new DepthFirstSearch();

    protected void doIt() throws Exception
    {
        final Set actualOutput = fSearch.search(this.fInput, getCallback());
        assertEquals(
                "Input and output sets do not have the same number of members",
                this.fExpectedOutput.size(), actualOutput.size());
        assertEquals("Sets do not contain the same members",
                this.fExpectedOutput, actualOutput);
    }

    protected void setInput(final String[] nodes)
    {
        for (int i = 0; i < nodes.length; i++)
        {
            this.fInput.add(nodes[i]);
        }
    }

    protected void setOutput(final String[] nodes)
    {
        for (int i = 0; i < nodes.length; i++)
        {
            this.fExpectedOutput.add(nodes[i]);
        }
    }

    protected void addEdges(final String from, final String[] tos)
    {
        final Set tmpEdges = new TreeSet();
        for (int i = 0; i < tos.length; i++)
        {
            final Edge edge = new Edge(from, tos[i]);
            this.fAllEdgesSet.add(edge);
            tmpEdges.add(edge);
        }
        this.fEdgesPerNodeMap.put(from, tmpEdges);
    }

    protected ISearchCallback getCallback()
    {
        return new ISearchCallback()
        {
            public SortedSet getEdges(final Object fromNode)
            {
                return getEdgesFromNode(fromNode);
            };

            public void nodeAdded(final Object fromNode)
            {
            }

            public boolean searchNode(final Object node)
            {
                return true;
            }
        };
    }

    protected SortedSet getEdgesFromNode(final Object fromNode)
    {
        return (SortedSet) fEdgesPerNodeMap.get(fromNode);
    }

    protected SortedSet getEdgesToNode(final Object toNode)
    {
        final TreeSet set = new TreeSet();
        final Iterator iterator = this.fAllEdgesSet.iterator();
        while (iterator.hasNext())
        {
            final Edge edge = (Edge) iterator.next();
            if (edge.getTo().equals(toNode))
            {
                set.add(edge);
            }
        }
        return set;
    };
}

/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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
package org.dbunit.dataset;

import org.dbunit.dataset.DefaultDataSetSource;
import org.dbunit.dataset.ForwardOnlyDataSetTest;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.IDataSetSource;
import org.dbunit.dataset.StreamingDataSet;
import org.dbunit.dataset.xml.FlatXmlSource;

import org.xml.sax.InputSource;

import java.io.FileReader;

/**
 * @author Manuel Laflamme
 * @since Apr 18, 2003
 * @version $Revision$
 */
public class StreamingDataSetTest extends ForwardOnlyDataSetTest
{
    public StreamingDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        IDataSetSource source = new FlatXmlSource(
                new InputSource(new FileReader("src/xml/flatXmlDataSetTest.xml")));
        return new StreamingDataSet(source);
//        return new StreamingDataSet(
//                new DefaultDataSetSource(super.createDataSet()));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new StreamingDataSet(
                new DefaultDataSetSource(super.createDuplicateDataSet()));
    }
}

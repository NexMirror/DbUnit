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
package org.dbunit.dataset.xml;

import org.dbunit.dataset.AbstractProducerTest;
import org.dbunit.dataset.IDataSetProducer;
import org.dbunit.dataset.Column;

import org.xml.sax.InputSource;

import java.io.File;

/**
 * @author Manuel Laflamme
 * @since Apr 30, 2003
 * @version $Revision$
 */
public class XmlProducerTest extends AbstractProducerTest
{
    private static final File DATASET_FILE =
            new File("src/xml/xmlProducerTest.xml");

    public XmlProducerTest(String s)
    {
        super(s);
    }

    protected IDataSetProducer createProducer() throws Exception
    {
        String uri = DATASET_FILE.getAbsoluteFile().toURL().toString();
        InputSource source = new InputSource(uri);

        return new XmlProducer(source);
    }

    protected Column[] createExpectedColumns(Column.Nullable nullable) throws Exception
    {
        return super.createExpectedColumns(Column.NULLABLE_UNKNOWN);
    }
}

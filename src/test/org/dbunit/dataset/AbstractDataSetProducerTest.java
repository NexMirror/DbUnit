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

import org.dbunit.dataset.datatype.DataType;

import junit.framework.TestCase;

/**
 * @author Manuel Laflamme
 * @since Apr 29, 2003
 * @version $Revision$
 */
public abstract class AbstractDataSetProducerTest extends TestCase
{
    private static final String[] TABLE_NAMES = {
        "DUPLICATE_TABLE",
        "SECOND_TABLE",
        "TEST_TABLE",
        "DUPLICATE_TABLE",
        "EMPTY_TABLE",
    };

    public AbstractDataSetProducerTest(String s)
    {
        super(s);
    }

    protected String[] getExpectedNames() throws Exception
    {
        return (String[])TABLE_NAMES.clone();
    }

    protected Column[] createExpectedColumns(boolean nullable) throws Exception
    {
        Column[] columns = new Column[4];
        for (int i = 0; i < columns.length; i++)
        {
            columns[i] = new Column("COLUMN" + i, DataType.UNKNOWN,
                    Column.nullableValue(nullable));
        }
        return columns;
    }

    public abstract void testProduce() throws Exception;
    public abstract void testProduceWithoutConsumer() throws Exception;

}

/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
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

import java.io.FileReader;

import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.dataset.xml.FlatXmlDataSetTest;
import org.dbunit.testutil.TestUtils;

/**
 * @author Manuel Laflamme
 * @since Mar 17, 2003
 * @version $Revision$
 */
public class ReplacementDataSetTest extends AbstractDataSetDecoratorTest
{
    public ReplacementDataSetTest(String s)
    {
        super(s);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSetTest class

    protected IDataSet createDataSet() throws Exception
    {
        return new ReplacementDataSet(new FlatXmlDataSetBuilder().build(new FileReader(
                FlatXmlDataSetTest.DATASET_FILE)));
    }

    public void testConstructor_DataSetHasCaseSensitive_ReplacementSetHasCaseSensitive()
            throws Exception
    {
        FileReader xmlReader = new FileReader(FlatXmlDataSetTest.DATASET_FILE);
        FlatXmlDataSet flatDataSet = new FlatXmlDataSetBuilder()
                .setCaseSensitiveTableNames(true).build(xmlReader);
        ReplacementDataSet dataSet = new ReplacementDataSet(flatDataSet);

        assertTrue(dataSet.isCaseSensitiveTableNames());
    }

    public void testConstructor_DifferentCaseTableNames_CaseSensitiveMatch()
            throws Exception
    {
        FileReader fileReader = TestUtils
                .getFileReader("/xml/replacementDataSetCaseSensitive.xml");
        IDataSet originalDataSet = new FlatXmlDataSetBuilder()
                .setCaseSensitiveTableNames(true).build(fileReader);
        assertCaseSensitiveTables(originalDataSet);

        IDataSet replacementDataSet = new ReplacementDataSet(originalDataSet);
        assertCaseSensitiveTables(replacementDataSet);
    }

    private void assertCaseSensitiveTables(IDataSet dataSet) throws DataSetException
    {
        ITable[] tables = dataSet.getTables();
        assertEquals(
                "Should be 2 tables with case-sensitive table names; 1 without.",
                2, tables.length);

        String tableName0 = tables[0].getTableMetaData().getTableName();
        String tableName1 = tables[1].getTableMetaData().getTableName();

        assertEquals("TEST_TABLE", tableName0);
        assertEquals("test_table", tableName1);
        assertEquals("row 0 col 0", tables[0].getValue(0, "COLUMN0"));
        assertEquals("row 1 col 0", tables[1].getValue(0, "COLUMN0"));
    }
}

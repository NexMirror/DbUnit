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

import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.testutil.TestUtils;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.0 (Mar 27, 2002)
 */
public class CaseInsensitiveDataSetTest extends AbstractDataSetTest
{
    public CaseInsensitiveDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new CaseInsensitiveDataSet(new XmlDataSet(TestUtils.getFileReader(
                "xml/caseInsensitiveDataSetTest.xml")));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        throw new UnsupportedOperationException();
    }

    protected IDataSet createMultipleCaseDuplicateDataSet() throws Exception
    {
        throw new UnsupportedOperationException();
    }

    protected void assertEqualsTableName(String message, String expected,
            String actual)
    {
        assertEqualsIgnoreCase(message, expected, actual);
    }

    public void testCreateDuplicateDataSet() throws Exception
    {
        // No op. This dataSet is only a wrapper for another dataSet which is why duplicates cannot occur.
    }

    public void testCreateMultipleCaseDuplicateDataSet() throws Exception
    {
        // No op. This dataSet is only a wrapper for another dataSet which is why duplicates cannot occur.
    }

    public void testAddTable() throws Exception {
        try {
            createDataSet().addTable((ITable) null);
            fail("A \"org.dbunit.dataset.DataSetException: Not implemented.\" is expected.");
        } catch (UnsupportedOperationException exception) {
           assertThat(exception.getMessage(), equalTo("Not implemented.  Never will since this class is deprecated."));
        }
    }

    public void testAddTablesWithCollection() throws Exception {
        try {
            createDataSet().addTables((Collection<ITable>) null);
            fail("A \"org.dbunit.dataset.DataSetException: Not implemented.\" is expected.");
        } catch (UnsupportedOperationException exception) {
           assertThat(exception.getMessage(), equalTo("Not implemented.  Never will since this class is deprecated."));
        }
    }

    public void testAddTablesWithDataset() throws Exception {
        try {
            createDataSet().addTables((IDataSet) null);
            fail("A \"org.dbunit.dataset.DataSetException: Not implemented.\" is expected.");
        } catch (UnsupportedOperationException exception) {
           assertThat(exception.getMessage(), equalTo("Not implemented.  Never will since this class is deprecated."));
        }
    }
}



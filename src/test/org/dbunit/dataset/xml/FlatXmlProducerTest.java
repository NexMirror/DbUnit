package org.dbunit.dataset.xml;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;

/**
 *
 * <p> Copyright (c) 2002 OZ.COM.  All Rights Reserved. </p>
 * @author manuel.laflamme$
 * @since Apr 28, 2003$
 * @version $Revision$
 */
public class FlatXmlProducerTest extends FlatXmlDataSetTest
{
    public FlatXmlProducerTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        InputSource source = new InputSource(new FileInputStream(DATASET_FILE));
        return new CachedDataSet(new FlatXmlProducer(source));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        InputSource source = new InputSource(new FileInputStream(DUPLICATE_DATASET_FILE));
        return new CachedDataSet(new FlatXmlProducer(source));
    }

    public void testMissingColumnAndEnableDtdMetadata() throws Exception
    {
        File file = new File("src/xml/flatXmlTableTest.xml");
        InputSource source = new InputSource(file.toURL().toString());
        IDataSet dataSet = new CachedDataSet(new FlatXmlProducer(source));

        ITable table = dataSet.getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", 3, columns.length);
    }

    public void testMissingColumnAndDisableDtdMetadata() throws Exception
    {
        File file = new File("src/xml/flatXmlTableTest.xml");
        InputSource source = new InputSource(file.toURL().toString());
        FlatXmlProducer provider = new FlatXmlProducer(source, true);
        IDataSet dataSet = new CachedDataSet(provider);

        ITable table = dataSet.getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", 2, columns.length);
    }

    public void testProduce() throws Exception
    {
    }
}

package org.dbunit.dataset.xml;

import java.io.InputStream;
import java.net.MalformedURLException;

import org.dbunit.dataset.DataSetException;
import org.junit.Test;

public class FlatXmlDataSetBuilderTest
{
    private final FlatXmlDataSetBuilder sut = new FlatXmlDataSetBuilder();

    @Test(expected = DataSetException.class)
    public void testBuild_File_$InTableName_Fails()
            throws MalformedURLException, DataSetException
    {
        String fileName = "/xml/flatXmlDataSet$Test.xml";
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        sut.build(inputStream);
    }
}

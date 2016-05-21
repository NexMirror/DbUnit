package org.dbunit.dataset.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.junit.Test;

public class XlsDataSetWriterTest
{
    private static final File OUTPUT_DIR = new File("target", "excel");

    private static final String INPUT_EXCEL_FILE =
            "/excel/XlsDataSetWriterCellStyleCaching.xlsx";

    private static final File OUTPUT_EXCEL_FILE = new File(OUTPUT_DIR,
            "XlsDataSetWriterCellStyleCachingTestOutput.xls");

    /**
     * Test for issue 377. Without 377's changes, test fails with:
     * java.lang.IllegalStateException: The maximum number of cell styles was
     * exceeded. You can define up to 4000 styles in a .xls workbook
     */
    @Test
    public void testTimestampTzOffsets()
            throws URISyntaxException, DataSetException, IOException
    {
        OUTPUT_DIR.mkdir();

        URL excelFileUrl = getClass().getResource(INPUT_EXCEL_FILE);
        URI excelFileUri = excelFileUrl.toURI();
        File file = new File(excelFileUri);
        IDataSet dataSet = new XlsDataSet(file);
        FileOutputStream outputStream = new FileOutputStream(OUTPUT_EXCEL_FILE);
        XlsDataSet.write(dataSet, outputStream);
    }
}

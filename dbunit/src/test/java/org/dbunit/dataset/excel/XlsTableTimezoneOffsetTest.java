package org.dbunit.dataset.excel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

public class XlsTableTimezoneOffsetTest
{
    private static final String EXCEL_SPREADSHEET =
            "/excel/XlsTableTimezoneOffset.xlsx";
    private static final String TABLE_NAME = "xxx_tz_offset_test";
    private static final String FAIL_MSG =
            "row '%d' column '%s' doesn't match.";

    private static final DateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    private ITable table;

    /**
     * Simple test that loads the specified excel file and makes sure that the
     * date is correct if the JVM's time zone is different from GMT/UTC This
     * test only makes sense when the default time zone is NOT UTC (i.e. has an
     * offset different from 0), otherwise the test would succeed even if the
     * bug is still present. Thus, the precondition check makes sure that the
     * default time zone offset is not 0 and aborts the test if it is.
     *
     * To change the time zone offset to something other than 0, either change
     * your machine's system time zone setting, or use JVM parameter
     * '-Duser.timezone="Europe/Berlin"' (or any other valid timezone ID)
     *
     * @throws Exception
     */
    @Ignore("Remove the following two lines in XlsTable for this test to pass")
    // long tzOffset = TimeZone.getDefault().getOffset(date.getTime());
    // date = new Date(date.getTime() + tzOffset);
    @Test
    public void testTimestampTzOffsets() throws Exception
    {
        // uncomment to see available timezones
        // System.err.println(Arrays.toString(TimeZone.getAvailableIDs()));

        assertNotEquals(
                "Precondition failed: default time zone must not have offset 0!"
                        + " Use JVM parameter '-Duser.timezone=\"Europe/Berlin\"' or some other value, if you have to...",
                0, TimeZone.getDefault().getRawOffset());

        URL spreadsheetUrl = getClass().getResource(EXCEL_SPREADSHEET);
        URI spreadsheetUri = spreadsheetUrl.toURI();
        File spreadsheetFile = new File(spreadsheetUri);
        XlsDataSet xlsDataSet = new XlsDataSet(spreadsheetFile);

        table = xlsDataSet.getTable(TABLE_NAME);

        // The values returned by ITable.getValue() should match the content of
        // the actual file, regardless of the JVM's default timezone

        checkStringValue(0, "id", "1");
        checkDateValue(0, "ts", "2015-03-14 00:00:00");
        checkStringValue(1, "id", "2");
        checkDateValue(1, "ts", "2015-03-18 02:00:00");
        checkStringValue(2, "id", "3");
        checkDateValue(2, "ts", "2015-12-19 23:00:00");
    }

    private void checkStringValue(int row, String column, String expected)
            throws DataSetException
    {
        String failMsg = String.format(FAIL_MSG, row, column);
        String value = getValueAsString(row, column);
        collector.checkThat(failMsg, value, equalTo(expected));
    }

    private void checkDateValue(int row, String column, String expected)
            throws DataSetException
    {
        String failMsg = String.format(FAIL_MSG, row, column);
        String value = getValueAsDate(row, column);
        collector.checkThat(failMsg, value, equalTo(expected));
    }

    private String getValueAsString(int row, String column)
            throws DataSetException
    {
        Object value = table.getValue(row, column);
        return value.toString();
    }

    private String getValueAsDate(int row, String column)
            throws DataSetException
    {
        return dateFormat.format(table.getValue(row, column));
    }
}

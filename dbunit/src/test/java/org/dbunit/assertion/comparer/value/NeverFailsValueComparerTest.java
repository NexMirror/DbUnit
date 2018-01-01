package org.dbunit.assertion.comparer.value;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Test;

public class NeverFailsValueComparerTest
{
    private final NeverFailsValueComparer sut = new NeverFailsValueComparer();

    @Test
    public void testIsExpected_AllNulls_True() throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = null;
        final Object expectedValue = null;
        final Object actualValue = null;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("All null should have been equal.", actual, equalTo(true));
    }

    @Test
    public void testIsExpected_DifferenceValues_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = null;
        final Object expectedValue = "expected value";
        final Object actualValue = "actual value";

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Unequal values should have been equal.", actual,
                equalTo(true));
    }

    @Test
    public void testGetFailPhrase()
    {
        final String actual = sut.getFailPhrase();

        assertThat(
                "Fail phrase is not empty String"
                        + " and must be empty for never fails.",
                actual, equalTo(""));
    }
}

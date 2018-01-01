package org.dbunit.assertion.comparer.value;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Test;

public class IsActualEqualToExpectedValueComparerTest
{
    private final IsActualEqualToExpectedValueComparer sut =
            new IsActualEqualToExpectedValueComparer();

    @Test
    public void testIsExpected_AllNulls_True() throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.BIGINT;
        final Object expectedValue = null;
        final Object actualValue = null;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("All null should have been true.", actual, equalTo(true));
    }

    @Test
    public void testIsExpected_ActualEqualToExpected_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.BIGINT;
        final Object expectedValue = 4;
        final Object actualValue = 4;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual is equal to expected, should have been true.",
                actual, equalTo(true));
    }

    @Test
    public void testIsExpected_ActualGreaterThanExpected_False()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.BIGINT;
        final Object expectedValue = 4;
        final Object actualValue = 8;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat(
                "Actual is greater than expected, should not have been true.",
                actual, equalTo(false));
    }

    @Test
    public void testIsExpected_ActualLessThanExpected_False()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.BIGINT;
        final Object expectedValue = 4;
        final Object actualValue = 2;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual is less than expected, should not have been true.",
                actual, equalTo(false));
    }

    @Test
    public void testGetFailPhrase() throws Exception
    {
        final String actual = sut.getFailPhrase();

        assertThat("Should have fail phrase.", actual, not(nullValue()));
    }
}

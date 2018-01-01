package org.dbunit.assertion.comparer.value;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Test;

public class IsActualNotEqualToExpectedValueComparerTest
{
    private final IsActualNotEqualToExpectedValueComparer sut =
            new IsActualNotEqualToExpectedValueComparer();

    @Test
    public void testIsExpected_AllNulls_False() throws DatabaseUnitException
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
        assertThat("All null should not have been equal.", actual,
                equalTo(false));
    }

    @Test
    public void testIsExpected_NotEqualNumbers_True()
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
        assertThat("Unequal numbers should not have been equal.", actual,
                equalTo(true));
    }

    @Test
    public void testGetFailPhrase() throws Exception
    {
        final String actual = sut.getFailPhrase();

        assertThat("Should have fail phrase.", actual, not(nullValue()));
    }
}

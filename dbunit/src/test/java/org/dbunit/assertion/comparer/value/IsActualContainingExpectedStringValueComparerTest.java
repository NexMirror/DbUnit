package org.dbunit.assertion.comparer.value;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Test;

public class IsActualContainingExpectedStringValueComparerTest
{
    final IsActualContainingExpectedStringValueComparer sut =
            new IsActualContainingExpectedStringValueComparer();

    @Test
    public void testIsExpected_AllNull_True() throws DatabaseUnitException
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
    public void testIsExpected_ActualNullExpectedNotNull_False()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = null;
        final Object expectedValue = "expected string";
        final Object actualValue = null;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);

        assertThat("Actual null, expected not null should not have been equal.",
                actual, equalTo(false));
    }

    @Test
    public void testIsExpected_ActualNotNullExpectedNull_False()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = null;
        final Object expectedValue = null;
        final Object actualValue = "expected string";

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);

        assertThat(
                "Actual not null, expected null, should not have been equal.",
                actual, equalTo(false));
    }

    @Test
    public void testIsExpected_ActualEqualToExpected_BigInt_True()
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
    public void testIsExpected_ActualEqualToExpected_Double_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.DOUBLE;
        final Object expectedValue = 4.8;
        final Object actualValue = 4.8;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual is equal to expected, should have been true.",
                actual, equalTo(true));
    }

    @Test
    public void testIsExpected_ActualEqualToExpected_Varchar_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.VARCHAR;
        final Object expectedValue = "the value";
        final Object actualValue = "the value";

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual is equal to expected, should have been true.",
                actual, equalTo(true));
    }

    @Test
    public void testIsExpected_ActualContainsExpected_BigInt_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.BIGINT;
        final Object expectedValue = 4;
        final Object actualValue = 4444;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual contains expected, should have been true.", actual,
                equalTo(true));
    }

    @Test
    public void testIsExpected_ActualContainsExpected_Double_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.DOUBLE;
        final Object expectedValue = 4.8;
        final Object actualValue = 4444.8888;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual contains expected, should have been true.", actual,
                equalTo(true));
    }

    @Test
    public void testIsExpected_ActualContainsExpected_Varchar_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.VARCHAR;
        final Object expectedValue = "the value";
        final Object actualValue = "prefix the value suffix";

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual contains expected, should have been true.", actual,
                equalTo(true));
    }

    @Test
    public void testIsExpected_ActualNotContainsExpected_BigInt_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.BIGINT;
        final Object expectedValue = 88;
        final Object actualValue = 4444;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual does not contain expected, should have been false.",
                actual, equalTo(false));
    }

    @Test
    public void testIsExpected_ActualNotContainsExpected_Double_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.DOUBLE;
        final Object expectedValue = 24.82;
        final Object actualValue = 4444.8888;

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual does not contain expected, should have been false.",
                actual, equalTo(false));
    }

    @Test
    public void testIsExpected_ActualNotContainsExpected_Varchar_True()
            throws DatabaseUnitException
    {
        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.VARCHAR;
        final Object expectedValue = "not the value";
        final Object actualValue = "prefix the value suffix";

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);
        assertThat("Actual does not contain expected, should have been false.",
                actual, equalTo(false));
    }

    @Test
    public void testGetFailPhrase() throws Exception
    {
        final String actual = sut.getFailPhrase();

        assertThat("Should have fail phrase.", actual, not(nullValue()));
    }
}

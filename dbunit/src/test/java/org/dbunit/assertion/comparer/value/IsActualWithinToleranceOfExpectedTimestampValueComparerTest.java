package org.dbunit.assertion.comparer.value;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Test;

public class IsActualWithinToleranceOfExpectedTimestampValueComparerTest
{
    @Test
    public void testIsExpected_AllNull_True() throws DatabaseUnitException
    {
        final long lowToleranceValueInMillis = 0;
        final long highToleranceValueInMillis = 0;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);

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
        final long lowToleranceValueInMillis = 0;
        final long highToleranceValueInMillis = 0;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);

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
    public void testIsExpected_WithinToleranceMiddle_True()
            throws DatabaseUnitException
    {
        final long lowToleranceValueInMillis = 500;
        final long highToleranceValueInMillis = 1500;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);
        final long expectedMillis = 1000;
        final long actualMillis = 2000;

        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = null;
        final Object expectedValue = new Timestamp(expectedMillis);
        final Object actualValue = new Timestamp(actualMillis);

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);

        assertThat("Within tolerance, should have been equal.", actual,
                equalTo(true));
    }

    @Test
    public void testIsExpected_WithinToleranceMatchLow_True()
            throws DatabaseUnitException
    {
        final long lowToleranceValueInMillis = 500;
        final long highToleranceValueInMillis = 1500;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);
        final long expectedMillis = 1000;
        final long actualMillis = expectedMillis + lowToleranceValueInMillis;

        final long diff = Math.abs(expectedMillis - actualMillis);
        assertThat("Test setup problem, diff does not match low tolerance",
                diff, equalTo(lowToleranceValueInMillis));

        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = null;
        final Object expectedValue = new Timestamp(expectedMillis);
        final Object actualValue = new Timestamp(actualMillis);

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);

        assertThat("Diff matches low tolerance, should have been equal.",
                actual, equalTo(true));
    }

    @Test
    public void testIsExpected_WithinToleranceMatchHigh_True()
            throws DatabaseUnitException
    {
        final long lowToleranceValueInMillis = 500;
        final long highToleranceValueInMillis = 1500;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);
        final long expectedMillis = 1000;
        final long actualMillis = expectedMillis + highToleranceValueInMillis;

        final long diff = Math.abs(expectedMillis - actualMillis);
        assertThat("Test setup problem, diff does not match high tolerance",
                diff, equalTo(highToleranceValueInMillis));

        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = null;
        final Object expectedValue = new Timestamp(expectedMillis);
        final Object actualValue = new Timestamp(actualMillis);

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);

        assertThat("Diff matches high tolerance, should have been equal.",
                actual, equalTo(true));
    }

    @Test
    public void testIsTolerant_DiffTimeNotInRangeLow_False()
    {
        final long lowToleranceValueInMillis = 200;
        final long highToleranceValueInMillis = 400;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);

        final long diffTime = lowToleranceValueInMillis - 100;
        final boolean actual = sut.isTolerant(diffTime);

        assertThat("Diff value is low of tolerant range but passed.", actual,
                equalTo(false));
    }

    @Test
    public void testIsTolerant_DiffTimeNotInRangeHigh_False()
    {
        final long lowToleranceValueInMillis = 200;
        final long highToleranceValueInMillis = 400;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);

        final long diffTime = highToleranceValueInMillis + 100;
        final boolean actual = sut.isTolerant(diffTime);

        assertThat("Diff value is high of tolerant range but passed.", actual,
                equalTo(false));
    }

    @Test
    public void testIsTolerant_DiffTimeInRange_True()
    {
        final long lowToleranceValueInMillis = 200;
        final long highToleranceValueInMillis = 400;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);

        final long diffTime =
                lowToleranceValueInMillis + highToleranceValueInMillis / 2;
        final boolean actual = sut.isTolerant(diffTime);

        assertThat("Diff value is in tolerant range but did not pass.", actual,
                equalTo(true));
    }

    @Test
    public void testGetFailPhrase() throws Exception
    {
        final long lowToleranceValueInMillis = 500;
        final long highToleranceValueInMillis = 1500;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);

        final String actual = sut.getFailPhrase();

        assertThat("Should have fail phrase.", actual, not(nullValue()));
    }

    @Test
    public void testStringExpectedTimestampActual() throws DatabaseUnitException
    {
        final long lowToleranceValueInMillis = 500;
        final long highToleranceValueInMillis = 1500;
        final IsActualWithinToleranceOfExpectedTimestampValueComparer sut =
                new IsActualWithinToleranceOfExpectedTimestampValueComparer(
                        lowToleranceValueInMillis, highToleranceValueInMillis);

        final long expectedMillis = 1000;
        final long actualMillis = expectedMillis + highToleranceValueInMillis;

        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.TIMESTAMP;
        final Object expectedValue = new Timestamp(expectedMillis).toString();
        final Object actualValue = new Timestamp(actualMillis);

        final boolean actual = sut.isExpected(expectedTable, actualTable,
                rowNum, columnName, dataType, expectedValue, actualValue);

        assertThat("Should have been equal.", actual, equalTo(true));
    }
}

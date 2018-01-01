package org.dbunit.assertion.comparer.value;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Before;
import org.junit.Test;

public class ConditionalSelectorMultiValueComparerTest
{
    private static final Long VALUE_COMPARER_KEY = 1L;

    private final ValueComparerSelector valueComparerSelector1 =
            (expectedTable, actualTable, rowNum, columnName, dataType,
                    expectedValue, actualValue,
                    valueComparers) -> valueComparers.get(VALUE_COMPARER_KEY);

    private final Map<Object, ValueComparer> valueComparersEmpty =
            new HashMap<>();
    private final Map<Object, ValueComparer> valueComparers1 = new HashMap<>();

    @Before
    public void setupValueComparerMap()
    {
        valueComparers1.put(VALUE_COMPARER_KEY,
                ValueComparers.isActualEqualToExpected);
    }

    @Test(expected = IllegalStateException.class)
    public void testDoCompare_NoValueComparers_IllegalState()
            throws DatabaseUnitException
    {
        final Map<Object, ValueComparer> valueComparers = valueComparersEmpty;
        final ValueComparerSelector valueComparerSelector =
                valueComparerSelector1;
        final ConditionalSelectorMultiValueComparer sut =
                new ConditionalSelectorMultiValueComparer(valueComparers,
                        valueComparerSelector);

        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.BIGINT;
        final Object expectedValue = null;
        final Object actualValue = null;
        sut.doCompare(expectedTable, actualTable, rowNum, columnName, dataType,
                expectedValue, actualValue);
        fail("Expected exception for no ValueComparers found in map.");
    }

    @Test
    public void testDoCompare_AllNull_NoFailMessage()
            throws DatabaseUnitException
    {
        final Map<Object, ValueComparer> valueComparers = valueComparers1;
        final ValueComparerSelector valueComparerSelector =
                valueComparerSelector1;
        final ConditionalSelectorMultiValueComparer sut =
                new ConditionalSelectorMultiValueComparer(valueComparers,
                        valueComparerSelector);

        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.BIGINT;
        final Object expectedValue = null;
        final Object actualValue = null;
        final String actual = sut.doCompare(expectedTable, actualTable, rowNum,
                columnName, dataType, expectedValue, actualValue);

        assertThat("Actual and expected both null,"
                + " should have no fail message.", actual, nullValue());
    }

    @Test
    public void testDoCompare_ActualAndExpectedNotEqual_FailMessage()
            throws DatabaseUnitException
    {
        final Map<Object, ValueComparer> valueComparers = valueComparers1;
        final ValueComparerSelector valueComparerSelector =
                valueComparerSelector1;
        final ConditionalSelectorMultiValueComparer sut =
                new ConditionalSelectorMultiValueComparer(valueComparers,
                        valueComparerSelector);

        final ITable expectedTable = null;
        final ITable actualTable = null;
        final int rowNum = 0;
        final String columnName = null;
        final DataType dataType = DataType.BIGINT;
        final Object expectedValue = 1;
        final Object actualValue = 2;
        final String actual = sut.doCompare(expectedTable, actualTable, rowNum,
                columnName, dataType, expectedValue, actualValue);

        assertThat("Actual and expected not equal, should have fail message.",
                actual, not(nullValue()));
    }
}

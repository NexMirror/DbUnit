package org.dbunit.assertion;

import java.util.Arrays;

import org.dbunit.assertion.DbUnitAssert.ComparisonColumn;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.Columns;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for DbUnit assert classes containing common methods.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public class DbUnitAssertBase
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private FailureFactory junitFailureFactory = getJUnitFailureFactory();

    /**
     * @return The default failure handler
     * @since 2.4
     */
    protected FailureHandler getDefaultFailureHandler()
    {
        return getDefaultFailureHandler(null);
    }

    /**
     * @return The default failure handler
     * @since 2.4
     */
    protected FailureHandler getDefaultFailureHandler(
            final Column[] additionalColumnInfo)
    {
        final DefaultFailureHandler failureHandler =
                new DefaultFailureHandler(additionalColumnInfo);
        if (junitFailureFactory != null)
        {
            failureHandler.setFailureFactory(junitFailureFactory);
        }
        return failureHandler;
    }

    /**
     * @return the JUnitFailureFactory if JUnit is on the classpath or
     *         <code>null</code> if JUnit is not on the classpath.
     */
    private FailureFactory getJUnitFailureFactory()
    {
        try
        {
            Class.forName("junit.framework.Assert");
            // JUnit available
            return new JUnitFailureFactory();
        } catch (final ClassNotFoundException e)
        {
            // JUnit not available on the classpath return null
            log.debug("JUnit does not seem to be on the classpath. " + e);
        }
        return null;
    }

    /**
     * @param expectedTableName
     * @param expectedColumns
     * @param actualColumns
     * @param failureHandler
     *            The {@link FailureHandler} to be used when no datatype can be
     *            determined
     * @return The columns to be used for the assertion, including the correct
     *         datatype
     * @since 2.4
     */
    protected ComparisonColumn[] getComparisonColumns(
            final String expectedTableName, final Column[] expectedColumns,
            final Column[] actualColumns, final FailureHandler failureHandler)
    {
        final ComparisonColumn[] result =
                new ComparisonColumn[expectedColumns.length];

        for (int j = 0; j < expectedColumns.length; j++)
        {
            final Column expectedColumn = expectedColumns[j];
            final Column actualColumn = actualColumns[j];
            result[j] = new ComparisonColumn(expectedTableName, expectedColumn,
                    actualColumn, failureHandler);
        }
        return result;
    }

    /**
     * Method to last-minute intercept the comparison of a single expected and
     * actual value. Designed to be overridden in order to skip cell comparison
     * by specific cell values.
     *
     * @param columnName
     *            The column being compared
     * @param expectedValue
     *            The expected value to be compared
     * @param actualValue
     *            The actual value to be compared
     * @return <code>false</code> always so that the comparison is never skipped
     * @since 2.4
     */
    protected boolean skipCompare(final String columnName,
            final Object expectedValue, final Object actualValue)
    {
        return false;
    }

    protected FailureHandler determineFailureHandler(
            final FailureHandler failureHandler)
    {
        final FailureHandler validFailureHandler;

        if (failureHandler == null)
        {
            log.debug("FailureHandler is null. Using default implementation");
            validFailureHandler = getDefaultFailureHandler();
        } else
        {
            validFailureHandler = failureHandler;
        }

        return validFailureHandler;
    }

    protected boolean compareRowCounts(final ITable expectedTable,
            final ITable actualTable, final FailureHandler failureHandler,
            final String expectedTableName) throws Error
    {
        boolean isTablesEmpty;

        final int expectedRowsCount = expectedTable.getRowCount();
        int actualRowsCount = 0;
        boolean skipRowComparison = false;
        try
        {
            actualRowsCount = actualTable.getRowCount();
        } catch (final UnsupportedOperationException exception)
        {
            skipRowComparison = true;
        }

        if (skipRowComparison)
        {
            isTablesEmpty = false;
        } else
        {
            if (expectedRowsCount != actualRowsCount)
            {
                final String msg =
                        "row count (table=" + expectedTableName + ")";
                final Error error = failureHandler.createFailure(msg,
                        String.valueOf(expectedRowsCount),
                        String.valueOf(actualRowsCount));
                log.error(error.toString());
                throw error;
            }

            // if both tables are empty, it is not necessary to compare columns,
            // as such comparison can fail if column metadata is different
            // (which could occurs when comparing empty tables)
            if (expectedRowsCount == 0 && actualRowsCount == 0)
            {
                log.debug("Tables are empty, hence equals.");
                isTablesEmpty = true;
            } else
            {
                isTablesEmpty = false;
            }
        }

        return isTablesEmpty;
    }

    protected void compareColumns(final Column[] expectedColumns,
            final Column[] actualColumns, final ITableMetaData expectedMetaData,
            final ITableMetaData actualMetaData,
            final FailureHandler failureHandler) throws DataSetException, Error
    {
        final Columns.ColumnDiff columnDiff =
                Columns.getColumnDiff(expectedMetaData, actualMetaData);
        if (columnDiff.hasDifference())
        {
            final String message = columnDiff.getMessage();
            final Error error = failureHandler.createFailure(message,
                    Columns.getColumnNamesAsString(expectedColumns),
                    Columns.getColumnNamesAsString(actualColumns));
            log.error(error.toString());
            throw error;
        }
    }

    protected void compareTableCounts(final String[] expectedNames,
            final String[] actualNames, final FailureHandler failureHandler)
            throws Error
    {
        if (expectedNames.length != actualNames.length)
        {
            throw failureHandler.createFailure("table count",
                    String.valueOf(expectedNames.length),
                    String.valueOf(actualNames.length));
        }
    }

    protected void compareTableNames(final String[] expectedNames,
            final String[] actualNames, final FailureHandler failureHandler)
            throws Error
    {
        for (int i = 0; i < expectedNames.length; i++)
        {
            if (!actualNames[i].equals(expectedNames[i]))
            {
                throw failureHandler.createFailure("tables",
                        Arrays.asList(expectedNames).toString(),
                        Arrays.asList(actualNames).toString());
            }
        }
    }

    protected String[] getSortedTableNames(final IDataSet dataSet)
            throws DataSetException
    {
        log.debug("getSortedTableNames(dataSet={}) - start", dataSet);

        final String[] names = dataSet.getTableNames();
        if (!dataSet.isCaseSensitiveTableNames())
        {
            for (int i = 0; i < names.length; i++)
            {
                names[i] = names[i].toUpperCase();
            }
        }
        Arrays.sort(names);
        return names;
    }
}

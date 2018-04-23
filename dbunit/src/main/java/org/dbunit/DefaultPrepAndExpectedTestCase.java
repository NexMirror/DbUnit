/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dbunit.assertion.comparer.value.ValueComparer;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.DataFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test case base class supporting prep data and expected data. Prep data is the
 * data needed for the test to run. Expected data is the data needed to compare
 * if the test ran successfully.
 *
 * @see org.dbunit.DefaultPrepAndExpectedTestCaseDiIT
 * @see org.dbunit.DefaultPrepAndExpectedTestCaseExtIT
 *
 * @author Jeff Jensen jeffjensen AT users.sourceforge.net
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.8
 */
public class DefaultPrepAndExpectedTestCase extends DBTestCase
        implements PrepAndExpectedTestCase
{
    private final Logger log =
            LoggerFactory.getLogger(DefaultPrepAndExpectedTestCase.class);

    private static final String DATABASE_TESTER_IS_NULL_MSG =
            "databaseTester is null; must configure or set it first";

    public static final String TEST_ERROR_MSG = "DbUnit test error.";

    private IDatabaseTester databaseTester;
    private DataFileLoader dataFileLoader;

    // per test data
    private IDataSet prepDataSet = new DefaultDataSet();
    private IDataSet expectedDataSet = new DefaultDataSet();
    private VerifyTableDefinition[] verifyTableDefs = {};

    private ExpectedDataSetAndVerifyTableDefinitionVerifier expectedDataSetAndVerifyTableDefinitionVerifier =
            new DefaultExpectedDataSetAndVerifyTableDefinitionVerifier();

    /** Create new instance. */
    public DefaultPrepAndExpectedTestCase()
    {
    }

    /**
     * Create new instance with specified dataFileLoader and databaseTester.
     *
     * @param dataFileLoader
     *            Load to use for loading the data files.
     * @param databaseTester
     *            Tester to use for database manipulation.
     */
    public DefaultPrepAndExpectedTestCase(final DataFileLoader dataFileLoader,
            final IDatabaseTester databaseTester)
    {
        this.dataFileLoader = dataFileLoader;
        this.databaseTester = databaseTester;
    }

    /**
     * Create new instance with specified test case name.
     *
     * @param name
     *            The test case name.
     */
    public DefaultPrepAndExpectedTestCase(final String name)
    {
        super(name);
    }

    /**
     * {@inheritDoc} This implementation returns the databaseTester set by the
     * test.
     */
    @Override
    public IDatabaseTester newDatabaseTester() throws Exception
    {
        // questionable, but there is not a "setter" for any parent...
        return databaseTester;
    }

    /**
     * {@inheritDoc} Returns the prep dataset.
     */
    @Override
    public IDataSet getDataSet() throws Exception
    {
        return prepDataSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureTest(
            final VerifyTableDefinition[] verifyTableDefinitions,
            final String[] prepDataFiles, final String[] expectedDataFiles)
            throws Exception
    {
        log.info("configureTest: saving instance variables");
        this.prepDataSet = makeCompositeDataSet(prepDataFiles, "prep");
        this.expectedDataSet =
                makeCompositeDataSet(expectedDataFiles, "expected");
        this.verifyTableDefs = verifyTableDefinitions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preTest() throws Exception
    {
        setupData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preTest(final VerifyTableDefinition[] tables,
            final String[] prepDataFiles, final String[] expectedDataFiles)
            throws Exception
    {
        configureTest(tables, prepDataFiles, expectedDataFiles);
        preTest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object runTest(final VerifyTableDefinition[] verifyTables,
            final String[] prepDataFiles, final String[] expectedDataFiles,
            final PrepAndExpectedTestCaseSteps testSteps) throws Exception
    {
        final Object result;

        try
        {
            preTest(verifyTables, prepDataFiles, expectedDataFiles);
            log.info("runTest: running test steps");
            result = testSteps.run();
        } catch (final Exception e)
        {
            log.error(TEST_ERROR_MSG, e);
            // don't verify table data when test execution has errors as:
            // * a verify data failure masks the test error exception
            // * tables in unknown state and therefore probably not accurate
            postTest(false);
            throw e;
        }

        postTest();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postTest() throws Exception
    {
        postTest(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postTest(final boolean verifyData) throws Exception
    {
        try
        {
            if (verifyData)
            {
                verifyData();
            }
        } finally
        {
            // it is deliberate to have cleanup exceptions shadow verify
            // failures so user knows db is probably in unknown state (for
            // those not using an in-memory db or transaction rollback),
            // otherwise would mask probable cause of subsequent test failures
            cleanupData();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanupData() throws Exception
    {
        try
        {
            final IDataSet dataset =
                    new CompositeDataSet(prepDataSet, expectedDataSet);
            final String[] tableNames = dataset.getTableNames();
            final int count = tableNames.length;
            log.info("cleanupData: about to clean up {} tables={}", count,
                    tableNames);

            if (databaseTester == null)
            {
                throw new IllegalStateException(DATABASE_TESTER_IS_NULL_MSG);
            }

            databaseTester.setTearDownOperation(getTearDownOperation());
            databaseTester.setDataSet(dataset);
            databaseTester.setOperationListener(getOperationListener());
            databaseTester.onTearDown();
            log.debug("cleanupData: Clean up done");
        } catch (final Exception e)
        {
            log.error("cleanupData: Exception:", e);
            throw e;
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
        // parent tearDown() only cleans up prep data
        cleanupData();
        super.tearDown();
    }

    /**
     * Use the provided databaseTester to prep the database with the provided
     * prep dataset. See {@link org.dbunit.IDatabaseTester#onSetup()}.
     *
     * @throws Exception
     */
    public void setupData() throws Exception
    {
        log.info("setupData: setting prep dataset and inserting rows");
        if (databaseTester == null)
        {
            throw new IllegalStateException(DATABASE_TESTER_IS_NULL_MSG);
        }

        try
        {
            super.setUp();
        } catch (final Exception e)
        {
            log.error("setupData: Exception with setting up data:", e);
            throw e;
        }
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception
    {
        assertNotNull(DATABASE_TESTER_IS_NULL_MSG, databaseTester);
        return databaseTester.getSetUpOperation();
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception
    {
        assertNotNull(DATABASE_TESTER_IS_NULL_MSG, databaseTester);
        return databaseTester.getTearDownOperation();
    }

    /**
     * {@inheritDoc} Uses the connection from the provided databaseTester.
     */
    @Override
    public void verifyData() throws Exception
    {
        if (databaseTester == null)
        {
            throw new IllegalStateException(DATABASE_TESTER_IS_NULL_MSG);
        }

        final IDatabaseConnection connection = getConnection();

        final DatabaseConfig config = connection.getConfig();
        expectedDataSetAndVerifyTableDefinitionVerifier.verify(verifyTableDefs,
                expectedDataSet, config);

        try
        {
            final int tableDefsCount = verifyTableDefs.length;
            log.info(
                    "verifyData: about to verify {} tables"
                            + " using verifyTableDefinitions={}",
                    tableDefsCount, verifyTableDefs);
            if (tableDefsCount == 0)
            {
                log.warn("verifyData: No tables to verify as"
                        + " no VerifyTableDefinitions specified");
            }

            for (int i = 0; i < tableDefsCount; i++)
            {
                final VerifyTableDefinition td = verifyTableDefs[i];
                verifyData(connection, td);
            }
        } catch (final Exception e)
        {
            log.error("verifyData: Exception:", e);
            throw e;
        } finally
        {
            log.debug("verifyData: Verification done, closing connection");
            connection.close();
        }
    }

    protected void verifyData(final IDatabaseConnection connection,
            final VerifyTableDefinition verifyTableDefinition) throws Exception
    {
        final String tableName = verifyTableDefinition.getTableName();
        log.info("verifyData: Verifying table '{}'", tableName);

        final String[] excludeColumns =
                verifyTableDefinition.getColumnExclusionFilters();
        final String[] includeColumns =
                verifyTableDefinition.getColumnInclusionFilters();
        final Map<String, ValueComparer> columnValueComparers =
                verifyTableDefinition.getColumnValueComparers();
        final ValueComparer defaultValueComparer =
                verifyTableDefinition.getDefaultValueComparer();

        final ITable expectedTable = loadTableDataFromDataSet(tableName);
        final ITable actualTable =
                loadTableDataFromDatabase(tableName, connection);

        verifyData(expectedTable, actualTable, excludeColumns, includeColumns,
                defaultValueComparer, columnValueComparers);
    }

    public ITable loadTableDataFromDataSet(final String tableName)
            throws DataSetException
    {
        ITable table = null;

        final String methodName = "loadTableDataFromDataSet";

        log.debug("{}: Loading table {} from expected dataset", methodName,
                tableName);
        try
        {
            table = expectedDataSet.getTable(tableName);
        } catch (final Exception e)
        {
            final String msg = methodName + ": Problem obtaining table '"
                    + tableName + "' from expected dataset";
            log.error(msg, e);
            throw new DataSetException(msg, e);
        }
        return table;
    }

    public ITable loadTableDataFromDatabase(final String tableName,
            final IDatabaseConnection connection) throws Exception
    {
        ITable table = null;

        final String methodName = "loadTableDataFromDatabase";

        log.debug("{}: Loading table {} from database", methodName, tableName);
        try
        {
            table = connection.createTable(tableName);
        } catch (final Exception e)
        {
            final String msg = methodName + ": Problem obtaining table '"
                    + tableName + "' from database";
            log.error(msg, e);
            throw new DataSetException(msg, e);
        }
        return table;
    }

    /**
     * For the specified expected and actual tables (and excluding and including
     * the specified columns), verify the actual data is as expected.
     *
     * @param expectedTable
     *            The expected table to compare the actual table to.
     * @param actualTable
     *            The actual table to compare to the expected table.
     * @param excludeColumns
     *            The column names to exclude from comparison. See
     *            {@link org.dbunit.dataset.filter.DefaultColumnFilter#excludeColumn(String)}
     *            .
     * @param includeColumns
     *            The column names to only include in comparison. See
     *            {@link org.dbunit.dataset.filter.DefaultColumnFilter#includeColumn(String)}
     *            .
     * @param defaultValueComparer
     *            {@link ValueComparer} to use with column value comparisons
     *            when the column name for the table is not in the
     *            columnValueComparers {@link Map}. Can be <code>null</code> and
     *            will default.
     * @param columnValueComparers
     *            {@link Map} of {@link ValueComparer}s to use for specific
     *            columns. Key is column name, value is the
     *            {@link ValueComparer}. Can be <code>null</code> and will
     *            default to defaultValueComparer for all columns in all tables.
     * @throws DatabaseUnitException
     */
    protected void verifyData(final ITable expectedTable,
            final ITable actualTable, final String[] excludeColumns,
            final String[] includeColumns,
            final ValueComparer defaultValueComparer,
            final Map<String, ValueComparer> columnValueComparers)
            throws DatabaseUnitException
    {
        final String methodName = "verifyData";
        // Filter out the columns from the expected and actual results
        log.debug("{}: Applying filters to expected table", methodName);
        final ITable expectedFilteredTable = applyColumnFilters(expectedTable,
                excludeColumns, includeColumns);
        log.debug("{}: Applying filters to actual table", methodName);
        final ITable actualFilteredTable =
                applyColumnFilters(actualTable, excludeColumns, includeColumns);

        log.debug("{}: Sorting expected table", methodName);
        final SortedTable expectedSortedTable =
                new SortedTable(expectedFilteredTable);
        log.debug("{}: Sorted expected table={}", methodName,
                expectedSortedTable);

        log.debug("{}: Sorting actual table", methodName);
        final SortedTable actualSortedTable = new SortedTable(
                actualFilteredTable, expectedFilteredTable.getTableMetaData());
        log.debug("{}: Sorted actual table={}", methodName, actualSortedTable);

        log.debug("{}: Creating additionalColumnInfo", methodName);
        final Column[] additionalColumnInfo =
                makeAdditionalColumnInfo(expectedTable, excludeColumns);
        log.debug("{}: additionalColumnInfo={}", methodName,
                additionalColumnInfo);

        log.debug("{}: Comparing expected table to actual table", methodName);
        compareData(expectedSortedTable, actualSortedTable,
                additionalColumnInfo, defaultValueComparer,
                columnValueComparers);
    }

    /** Compare the tables, enables easy overriding. */
    protected void compareData(final SortedTable expectedSortedTable,
            final SortedTable actualSortedTable,
            final Column[] additionalColumnInfo,
            final ValueComparer defaultValueComparer,
            final Map<String, ValueComparer> columnValueComparers)
            throws DatabaseUnitException
    {
        Assertion.assertWithValueComparer(expectedSortedTable,
                actualSortedTable, additionalColumnInfo, defaultValueComparer,
                columnValueComparers);
    }

    /**
     * Don't add excluded columns to additionalColumnInfo as they are not found
     * and generate a not found message in the fail message.
     */
    protected Column[] makeAdditionalColumnInfo(final ITable expectedTable,
            final String[] excludeColumns) throws DataSetException
    {
        final List<Column> keepColumnsList = new ArrayList<Column>();
        final List<String> excludeColumnsList = Arrays.asList(excludeColumns);

        final Column[] allColumns =
                expectedTable.getTableMetaData().getColumns();
        for (final Column column : allColumns)
        {
            final String columnName = column.getColumnName();
            if (!excludeColumnsList.contains(columnName))
            {
                keepColumnsList.add(column);
            }
        }

        return keepColumnsList.toArray(new Column[keepColumnsList.size()]);
    }

    /**
     * Make a <code>IDataSet</code> from the specified files.
     *
     * @param dataFiles
     *            Represents the array of dbUnit data files.
     * @return The composite dataset.
     * @throws DataSetException
     *             On dbUnit errors.
     */
    public IDataSet makeCompositeDataSet(final String[] dataFiles,
            final String dataFilesName) throws DataSetException
    {
        if (dataFileLoader == null)
        {
            throw new IllegalStateException(
                    "dataFileLoader is null; must configure or set it first");
        }

        final int count = dataFiles.length;
        log.debug("makeCompositeDataSet: {} dataFiles count={}", dataFilesName,
                count);
        if (count == 0)
        {
            log.info("makeCompositeDataSet: Specified zero {} data files",
                    dataFilesName);
        }

        final List list = new ArrayList();
        for (int i = 0; i < count; i++)
        {
            final IDataSet ds = dataFileLoader.load(dataFiles[i]);
            list.add(ds);
        }

        final IDataSet[] dataSet = (IDataSet[]) list.toArray(new IDataSet[] {});
        final IDataSet compositeDS = new CompositeDataSet(dataSet);
        return compositeDS;
    }

    /**
     * Apply the specified exclude and include column filters to the specified
     * table.
     *
     * @param table
     *            The table to apply the filters to.
     * @param excludeColumns
     *            The exclude filters; use null or empty array to mean exclude
     *            none.
     * @param includeColumns
     *            The include filters; use null to mean include all.
     * @return The filtered table.
     * @throws DataSetException
     */
    public ITable applyColumnFilters(final ITable table,
            final String[] excludeColumns, final String[] includeColumns)
            throws DataSetException
    {
        ITable filteredTable = table;

        if (table == null)
        {
            throw new IllegalArgumentException("table is null");
        }

        // note: dbunit interprets an empty inclusion filter array as one
        // not wanting to compare anything!
        if (includeColumns == null)
        {
            log.debug("applyColumnFilters: including columns=(all)");
        } else
        {
            log.debug("applyColumnFilters: including columns='{}'",
                    new Object[] {includeColumns});
            filteredTable = DefaultColumnFilter
                    .includedColumnsTable(filteredTable, includeColumns);
        }

        if (excludeColumns == null || excludeColumns.length == 0)
        {
            log.debug("applyColumnFilters: excluding columns=(none)");
        } else
        {
            log.debug("applyColumnFilters: excluding columns='{}'",
                    new Object[] {excludeColumns});
            filteredTable = DefaultColumnFilter
                    .excludedColumnsTable(filteredTable, excludeColumns);
        }

        return filteredTable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDataSet getPrepDataset()
    {
        return prepDataSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDataSet getExpectedDataset()
    {
        return expectedDataSet;
    }

    /**
     * Get the databaseTester.
     *
     * @see {@link #databaseTester}.
     *
     * @return The databaseTester.
     */
    @Override
    public IDatabaseTester getDatabaseTester()
    {
        return databaseTester;
    }

    /**
     * Set the databaseTester.
     *
     * @see {@link #databaseTester}.
     *
     * @param databaseTester
     *            The databaseTester to set.
     */
    public void setDatabaseTester(final IDatabaseTester databaseTester)
    {
        this.databaseTester = databaseTester;
    }

    /**
     * Get the dataFileLoader.
     *
     * @see {@link #dataFileLoader}.
     *
     * @return The dataFileLoader.
     */
    public DataFileLoader getDataFileLoader()
    {
        return dataFileLoader;
    }

    /**
     * Set the dataFileLoader.
     *
     * @see {@link #dataFileLoader}.
     *
     * @param dataFileLoader
     *            The dataFileLoader to set.
     */
    public void setDataFileLoader(final DataFileLoader dataFileLoader)
    {
        this.dataFileLoader = dataFileLoader;
    }

    /**
     * Set the prepDs.
     *
     * @see {@link #prepDataSet}.
     *
     * @param prepDataSet
     *            The prepDs to set.
     */
    public void setPrepDs(final IDataSet prepDataSet)
    {
        this.prepDataSet = prepDataSet;
    }

    /**
     * Set the expectedDs.
     *
     * @see {@link #expectedDataSet}.
     *
     * @param expectedDataSet
     *            The expectedDs to set.
     */
    public void setExpectedDs(final IDataSet expectedDataSet)
    {
        this.expectedDataSet = expectedDataSet;
    }

    /**
     * Get the tableDefs.
     *
     * @see {@link #verifyTableDefs}.
     *
     * @return The tableDefs.
     */
    public VerifyTableDefinition[] getTableDefs()
    {
        return verifyTableDefs;
    }

    /**
     * Set the tableDefs.
     *
     * @see {@link #verifyTableDefs}.
     *
     * @param verifyTableDefs
     *            The tableDefs to set.
     */
    public void setTableDefs(final VerifyTableDefinition[] verifyTableDefs)
    {
        this.verifyTableDefs = verifyTableDefs;
    }

    public ExpectedDataSetAndVerifyTableDefinitionVerifier getExpectedDataSetAndVerifyTableDefinitionVerifier()
    {
        return expectedDataSetAndVerifyTableDefinitionVerifier;
    }

    public void setExpectedDataSetAndVerifyTableDefinitionVerifier(
            final ExpectedDataSetAndVerifyTableDefinitionVerifier expectedDataSetAndVerifyTableDefinitionVerifier)
    {
        this.expectedDataSetAndVerifyTableDefinitionVerifier =
                expectedDataSetAndVerifyTableDefinitionVerifier;
    }
}

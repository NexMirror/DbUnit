package org.dbunit;

import java.util.HashSet;
import java.util.Set;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for
 * {@link ExpectedDataSetAndVerifyTableDefinitionVerifier} which logs the
 * mismatches and fails the test when an expected table does not have a
 * VerifyTableDefinition.
 *
 * Can disable failing the test on mismatch with property
 * {@link DatabaseConfig#PROPERTY_ALLOW_VERIFYTABLEDEFINITION_EXPECTEDTABLE_COUNT_MISMATCH}
 * , setting it to false.
 *
 * Can change the implementation by extending this class or implementing
 * {@link ExpectedDataSetAndVerifyTableDefinitionVerifier} and calling
 * {@link DefaultPrepAndExpectedTestCase#setExpectedDataSetAndVerifyTableDefinitionVerifier}
 * .
 *
 * @author Jeff Jensen
 */
public class DefaultExpectedDataSetAndVerifyTableDefinitionVerifier
        implements ExpectedDataSetAndVerifyTableDefinitionVerifier
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void verify(final VerifyTableDefinition[] verifyTableDefinitions,
            final IDataSet expectedDataSet, final DatabaseConfig config)
            throws DataSetException
    {
        final String methodName = "verify";

        final int verifyTableDefsCount = verifyTableDefinitions.length;

        final String[] expectedTableNames = expectedDataSet.getTableNames();
        final int expectedTablesCount = expectedTableNames.length;

        log.debug(
                "{}: expectedTables count={}, verifyTableDefinitions count={}",
                methodName, expectedTablesCount, verifyTableDefsCount);

        if (expectedTablesCount > verifyTableDefsCount)
        {
            handleCountMismatch(verifyTableDefinitions, expectedTableNames,
                    config);
        }
    }

    protected void handleCountMismatch(
            final VerifyTableDefinition[] verifyTableDefinitions,
            final String[] expectedTableNames, final DatabaseConfig config)
            throws DataSetException
    {
        final String methodName = "handleCountMismatch";

        final int verifyTableDefsCount = verifyTableDefinitions.length;
        final int expectedTablesCount = expectedTableNames.length;

        final String msg = "{}: Test specified {} expected tables"
                + " and {} VerifyTableDefinitions;"
                + " usually these numbers should match as an expected table"
                + " is not verified without a VerifyTableDefinition";
        log.warn(msg, methodName, expectedTablesCount, verifyTableDefsCount);

        final Set<String> mismatchedTableNames = makeMismatchedTableNamesList(
                verifyTableDefinitions, expectedTableNames);
        failOnMismatch(config, mismatchedTableNames);
    }

    protected Set<String> makeMismatchedTableNamesList(
            final VerifyTableDefinition[] verifyTableDefinitions,
            final String[] expectedTableNames)
    {
        final Set<String> tables = new HashSet<String>();

        final String methodName = "makeMismatchedTableNamesList";

        final int expectedTablesCount = expectedTableNames.length;
        for (int i = 0; i < expectedTablesCount; i++)
        {
            final String expectedTableName = expectedTableNames[i];
            final boolean isExpectedTableExist =
                    isVerifyTableDefinitionsHasTable(verifyTableDefinitions,
                            expectedTableName);

            if (!isExpectedTableExist)
            {
                final String msg = "{}: expected table name={} does not have"
                        + " a corresponding VerifyTableDefinition";
                log.warn(msg, methodName, expectedTableName);
                tables.add(expectedTableName);
            }
        }
        return tables;
    }

    protected boolean isVerifyTableDefinitionsHasTable(
            final VerifyTableDefinition[] verifyTableDefinitions,
            final String expectedTableName)
    {
        boolean isExpectedTableFound = false;
        for (int j = 0; j < verifyTableDefinitions.length
                && !isExpectedTableFound; j++)
        {
            final VerifyTableDefinition verifyTableDefinition =
                    verifyTableDefinitions[j];
            final String definitionTableName =
                    verifyTableDefinition.getTableName();
            isExpectedTableFound =
                    expectedTableName.equals(definitionTableName);
        }
        return isExpectedTableFound;
    }

    protected void failOnMismatch(final DatabaseConfig config,
            final Set<String> mismatchCountTables) throws DataSetException
    {
        final String methodName = "failOnMismatch";

        final boolean allowCountMismatch = (Boolean) config.getProperty(
                DatabaseConfig.PROPERTY_ALLOW_VERIFYTABLEDEFINITION_EXPECTEDTABLE_COUNT_MISMATCH);
        final String willFailTestWord = allowCountMismatch ? " not" : "";
        log.info("{}: Property {} is set to {} so will{} fail test", methodName,
                DatabaseConfig.PROPERTY_ALLOW_VERIFYTABLEDEFINITION_EXPECTEDTABLE_COUNT_MISMATCH,
                allowCountMismatch, willFailTestWord);
        if (!allowCountMismatch)
        {
            final int mismatchCount = mismatchCountTables.size();
            final String msg = "The following " + mismatchCount
                    + " expected tables do not have"
                    + " corresponding VerifyTableDefinitions: "
                    + mismatchCountTables + "\nSet property '"
                    + DatabaseConfig.PROPERTY_ALLOW_VERIFYTABLEDEFINITION_EXPECTEDTABLE_COUNT_MISMATCH
                    + "' to true to suppress test fail.";
            throw new DataSetException(msg);
        }
    }
}

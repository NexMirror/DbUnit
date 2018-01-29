package org.dbunit;

import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.database.statement.IBatchStatement;
import org.dbunit.database.statement.MockBatchStatement;
import org.dbunit.database.statement.MockStatementFactory;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;

import com.mockobjects.sql.MockConnection;

import junit.framework.TestCase;

public class DefaultPrepAndExpectedTestCaseTest extends TestCase
{
    private static final String PREP_DATA_FILE_NAME = "/xml/flatXmlDataSetTest.xml";
    private static final String EXP_DATA_FILE_NAME = "/xml/flatXmlDataSetTest.xml";

    private final DataFileLoader dataFileLoader = new FlatXmlDataFileLoader();
    // private final IDatabaseTester databaseTester = new
    // JdbcDatabaseTester(driverClass, connectionUrl);

    private final DefaultPrepAndExpectedTestCase tc = new DefaultPrepAndExpectedTestCase();

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        // tc.setDatabaseTester(databaseTester);
        tc.setDataFileLoader(dataFileLoader);
    }

    public void testConfigureTest() throws Exception
    {
        String[] prepDataFiles = {PREP_DATA_FILE_NAME};
        String[] expectedDataFiles = {EXP_DATA_FILE_NAME};
        VerifyTableDefinition[] tables = {};

        tc.configureTest(tables, prepDataFiles, expectedDataFiles);

        assertEquals("Configured tables do not match expected.", tables,
                tc.getVerifyTableDefs());

        IDataSet expPrepDs = dataFileLoader.load(PREP_DATA_FILE_NAME);
        Assertion.assertEquals(expPrepDs, tc.getPrepDataset());

        IDataSet expExpDs = dataFileLoader.load(EXP_DATA_FILE_NAME);
        Assertion.assertEquals(expExpDs, tc.getExpectedDataset());
    }

    public void testPreTest() throws Exception
    {
        // TODO implement test
    }

    public void testRunTest() throws Exception
    {
        VerifyTableDefinition[] tables = {};
        String[] prepDataFiles = {};
        String[] expectedDataFiles = {};
        PrepAndExpectedTestCaseSteps testSteps = new PrepAndExpectedTestCaseSteps()
        {
            public Object run() throws Exception
            {
                System.out.println("This message represents the test steps.");
                return Boolean.TRUE;
            }
        };

        MockDatabaseConnection mockDbConnection = makeMockDatabaseConnection();
        IDatabaseTester databaseTester = new DefaultDatabaseTester(mockDbConnection);

        tc.setDatabaseTester(databaseTester);
        Boolean actual = (Boolean) tc.runTest(tables, prepDataFiles, expectedDataFiles, testSteps);

        assertTrue("Did not receive expected value from runTest().", actual);
    }

    public void testPostTest()
    {
        // TODO implement test
    }

    public void testPostTest_false()
    {
        // TODO implement test
    }

    public void testSetupData()
    {
        // TODO implement test
    }

    public void testVerifyData()
    {
        // TODO implement test
    }

    public void testVerifyDataITableITableStringArrayStringArray()
    {
        // TODO implement test
    }

    public void testCleanupData()
    {
        // TODO implement test
    }

    public void testMakeCompositeDataSet()
    {
        // TODO implement test
    }

    // TODO implement test - doesn't test anything yet
    public void testApplyColumnFiltersBothNull() throws DataSetException
    {
        final ITable table = new DefaultTable("test_table");
        final String[] excludeColumns = null;
        final String[] includeColumns = null;
        tc.applyColumnFilters(table, excludeColumns, includeColumns);
    }

    // TODO implement test - doesn't test anything yet
    public void testApplyColumnFiltersBothNotNull() throws DataSetException
    {
        final ITable table = new DefaultTable("test_table");
        final String[] excludeColumns = {"COL1"};
        final String[] includeColumns = {"COL2"};
        tc.applyColumnFilters(table, excludeColumns, includeColumns);
    }

    private MockDatabaseConnection makeMockDatabaseConnection()
    {
        MockConnection mockConnection = new MockConnection();

        MockStatementFactory mockStatementFactory = new MockStatementFactory();
        IBatchStatement mockBatchStatement = new MockBatchStatement();
        mockStatementFactory.setupStatement(mockBatchStatement);

        MockDatabaseConnection mockDbConnection = new MockDatabaseConnection();
        mockDbConnection.setupConnection(mockConnection);
        mockDbConnection.setupStatementFactory(mockStatementFactory);
        return mockDbConnection;
    }
}

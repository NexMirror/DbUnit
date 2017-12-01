package org.dbunit.dataset;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;

import org.dbunit.AbstractDatabaseIT;
import org.dbunit.DdlExecutor;
import org.dbunit.HypersonicEnvironment;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.testutil.TestUtils;
import org.junit.Test;

public class ColumnMetaDataTest extends AbstractDatabaseIT
{
    public ColumnMetaDataTest(String s)
    {
        super(s);
    }

    @Test
    public void testAllColumns() throws Exception
    {
        Connection jdbcConnection =
                HypersonicEnvironment.createJdbcConnection("tempdb");
        DdlExecutor.executeDdlFile(
                TestUtils.getFile("sql/hypersonic_dataset_column_metadata.sql"),
                jdbcConnection);
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        try
        {
            DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();

            DatabaseConfig config = connection.getConfig();

            IMetadataHandler metadataHandler = (IMetadataHandler) config
                    .getProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER);
            ResultSet colMetaRS =
                    metadataHandler.getColumns(databaseMetaData, "PUBLIC", "%");
            // PK_A INTEGER
            colMetaRS.next();
            ColumnMetaData pkaMetadata = new ColumnMetaData(colMetaRS);
            assertEquals("PK_A.columnName", "PK_A",
                    pkaMetadata.getColumnName());
            assertEquals("PK_A.sqlType", Types.INTEGER,
                    pkaMetadata.getSQLType().intValue());
            assertEquals("PK_A.sqlTypeName", "INTEGER",
                    pkaMetadata.getSqlTypeName());
            // DEC_COL_A2 DECIMAL(9, 2)
            colMetaRS.next();
            ColumnMetaData colA2Metadata = new ColumnMetaData(colMetaRS);
            assertEquals("DEC_COL_A2.columnName", "DEC_COL_A2",
                    colA2Metadata.getColumnName());
            assertEquals("DEC_COL_A2.sqlType", Types.DECIMAL,
                    colA2Metadata.getSQLType().intValue());
            assertEquals("DEC_COL_A2.sqlTypeName", "DECIMAL",
                    colA2Metadata.getSqlTypeName());
            assertEquals("DEC_COL_A2.decimalDigits", 2,
                    colA2Metadata.getDecimalDigits().intValue());
            // CHAR_COL_A3 CHAR(30)
            colMetaRS.next();
            ColumnMetaData colA3Metadata = new ColumnMetaData(colMetaRS);
            assertEquals("CHAR_COL_A3.columnName", "CHAR_COL_A3",
                    colA3Metadata.getColumnName());
            assertEquals("CHAR_COL_A3.sqlType", Types.CHAR,
                    colA3Metadata.getSQLType().intValue());
            assertEquals("CHAR_COL_A3.sqlTypeName", "CHAR",
                    colA3Metadata.getSqlTypeName());
            assertEquals("CHAR_COL_A3.columnSize", 30,
                    colA3Metadata.getColumnSize().intValue());
            // DATE_COL_A4 DATE
            colMetaRS.next();
            ColumnMetaData colA4Metadata = new ColumnMetaData(colMetaRS);
            assertEquals("DATE_COL_A4.columnName", "DATE_COL_A4",
                    colA4Metadata.getColumnName());
            assertEquals("DATE_COL_A4.sqlType", Types.DATE,
                    colA4Metadata.getSQLType().intValue());
            assertEquals("DATE_COL_A4.sqlTypeName", "DATE",
                    colA4Metadata.getSqlTypeName());
        } finally
        {
            HypersonicEnvironment.shutdown(jdbcConnection);
            jdbcConnection.close();
            HypersonicEnvironment.deleteFiles("tempdb");
        }
    }
}

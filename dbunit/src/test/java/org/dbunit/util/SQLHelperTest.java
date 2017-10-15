/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
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

package org.dbunit.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbunit.AbstractHSQLTestCase;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.ColumnMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import com.mockobjects.sql.MockDatabaseMetaData;
import com.mockobjects.sql.MockResultSetMetaData;
import com.mockobjects.sql.MockSingleRowResultSet;

/**
 * @author Felipe Leme (dbunit@felipeal.net)
 * @version $Revision$
 * @since Nov 5, 2005
 */
public class SQLHelperTest extends AbstractHSQLTestCase {
  
  public SQLHelperTest( String name ) {
    super( name, "hypersonic_dataset.sql" );
  }  
  
  public void testGetPrimaryKeyColumn() throws SQLException {
    String[] tables = { "A", "B", "C", "D", "E", "F", "G", "H" };
    Connection conn = getConnection().getConnection();
    assertNotNull( "didn't get a connection", conn );
    for (int i = 0; i < tables.length; i++) {
      String table = tables[i];
      String expectedPK = "PK" + table;
      String actualPK = SQLHelper.getPrimaryKeyColumn( conn, table );
      assertNotNull( actualPK );
      assertEquals( "primary key column for table " + table + " does not match", expectedPK, actualPK );
    }
  }
  
  public void testGetDatabaseInfoWithException() throws Exception{
      final String productName="Some product";
      final String exceptionText="Dummy exception to simulate unimplemented operation exception as occurs " +
      "in sybase 'getDatabaseMajorVersion()' (com.sybase.jdbc3.utils.UnimplementedOperationException)";
      
      DatabaseMetaData metaData = new MockDatabaseMetaData(){
          public String getDatabaseProductName() throws SQLException {
              return productName;
          }
          public String getDatabaseProductVersion() throws SQLException{
              return null;
          }
          public int getDriverMajorVersion() {
              return -1;
          }
          public int getDriverMinorVersion() {
              return -1;
          }
          public String getDriverName() throws SQLException {
              return null;
          }
          public String getDriverVersion() throws SQLException {
              return null;
          }
          public int getDatabaseMajorVersion() throws SQLException {
              throw new SQLException(exceptionText);
          }
          public int getDatabaseMinorVersion() throws SQLException {
              return -1;
          }
      };
      String info = SQLHelper.getDatabaseInfo(metaData);
      assertNotNull(info);
      assertTrue(info.indexOf(productName)>-1);
      assertTrue(info.indexOf(SQLHelper.ExceptionWrapper.NOT_AVAILABLE_TEXT)>-1);
  }
  
  /**
   * Regression test for changes to SQLHelper.createColumn.
   * Also covers org.dbunit.dataset.ColumnMetaData
   */
  public void testCreateColumn() {
	  try {
		  _testCreateColumn(new Object[]{"CAT", "SCHEMA", "table_1", "col01", 12, 
			  "VARCHAR", 30, null, 2, 2, 
			  0, "Test data", null, null, null, 
			  30, 4, "YES", null, null,
			  null, null, "NO", "NO"});
		  _testCreateColumn(new Object[]{"CAT", "SCHEMA", "other_table", "some_id", 3, 
				  "DECIMAL", 10, null, 2, 2, 
				  0, "Primary key", "0.00", null, null, 
				  4, 1, "NO", null, null,
				  null, null, "YES", "YES"});
		  _testCreateColumn(new Object[]{"CAT", "SCHEMA", "same_table", "some_ud_ref", -3, 
				  "VARBINARY", 8, null, 0, 0, 
				  1, "User defined ref", null, null, null, 
				  null, 7, "YES", "Scope cat", "Scope schema",
				  "Scope table", (short)12, "YES", "YES"});
	  } catch (Exception e) {
		  e.printStackTrace();
		  fail(e.getMessage());
	  }
  }
  
  private void _testCreateColumn(Object[] metadataValues) throws Exception {
	  MockSingleRowResultSet resultSet = new MockSingleRowResultSet();
	  resultSet.addExpectedIndexedValues(metadataValues);
	  MockResultSetMetaData mockMeta = new MockResultSetMetaData();
	  mockMeta.setupGetColumnCount(metadataValues.length);
	  resultSet.setupMetaData(mockMeta);
	  IDataTypeFactory dtFactory = new DefaultDataTypeFactory();
	  _testCreateColumn(resultSet, dtFactory);
  }
  
  private void _testCreateColumn(ResultSet resultSet, IDataTypeFactory dtFactory) throws Exception {
	  Column oldCol = oldCreateColumn(resultSet, dtFactory, true);
	  ColumnMetaData colmd = new ColumnMetaData(resultSet);
	  // Check that the new method creates a column equal to the on returned by the legacy method:
	  Column newCol = SQLHelper.createColumn(colmd, dtFactory, true);
	  assertEquals(oldCol, newCol);
	  // Check that the backwards-compatibility method also produces a congruent column: 
	  Column newOldCol = SQLHelper.createColumn(resultSet, dtFactory, true);
	  assertEquals(oldCol, newOldCol);
  }
  
  /*
   * This is a direct lift of the previous SQLHelper.createColumn, for comparison 
   * with the new implementation in regression testing.   
   */
  private static final Column oldCreateColumn(ResultSet resultSet,
          IDataTypeFactory dataTypeFactory, boolean datatypeWarning)
                  throws SQLException, DataTypeException
                  {
      String tableName = resultSet.getString(3);
      String columnName = resultSet.getString(4);
      int sqlType = resultSet.getInt(5);
      //If Types.DISTINCT like SQL DOMAIN, then get Source Date Type of SQL-DOMAIN
      if(sqlType == java.sql.Types.DISTINCT)
      {
          sqlType = resultSet.getInt("SOURCE_DATA_TYPE");
      }

      String sqlTypeName = resultSet.getString(6);
      //        int columnSize = resultSet.getInt(7);
      int nullable = resultSet.getInt(11);
      String remarks = resultSet.getString(12);
      String columnDefaultValue = resultSet.getString(13);
      // This is only available since Java 5 - so we can try it and if it does not work default it
      String isAutoIncrement = Column.AutoIncrement.NO.getKey();
      try {
          isAutoIncrement = resultSet.getString(23);
      }
      catch (Exception e)
      {
          // Ignore this one here
          final String msg =
                  "Could not retrieve the 'isAutoIncrement' property"
                          + " because not yet running on Java 1.5 -"
                          + " defaulting to NO. Table={}, Column={}";
          System.err.println(msg);
          e.printStackTrace();
      }

      // Convert SQL type to DataType
      DataType dataType =
              dataTypeFactory.createDataType(sqlType, sqlTypeName, tableName, columnName);
      if (dataType != DataType.UNKNOWN)
      {
          Column column = new Column(columnName, dataType,
                  sqlTypeName, Column.nullableValue(nullable), columnDefaultValue, remarks,
                  Column.AutoIncrement.autoIncrementValue(isAutoIncrement));
          return column;
      }
      else
      {
          if (datatypeWarning)
              System.err.println(
                      tableName + "." + columnName +
                      " data type (" + sqlType + ", '" + sqlTypeName +
                      "') not recognized and will be ignored. See FAQ for more information.");

          // datatype unknown - column not created
          return null;
      }
                  }

  
}

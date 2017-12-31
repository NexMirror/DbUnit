/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Callable;
import java.util.Properties;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.testutil.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 18, 2002
 */
public class DatabaseEnvironment
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseEnvironment.class);

    private static DatabaseEnvironment INSTANCE = null;

    private DatabaseProfile _profile = null;
    private IDatabaseConnection _connection = null;
    private IDataSet _dataSet = null;
    private IDatabaseTester _databaseTester = null;

    /**
     * Optional "dbunit.properties" is loaded if present and
     * merged with System properties and the whole set is returned.
     * <p>
     * If absent (which is the normal scenario), only System
     * properties are returned.
     * <p>
     * "dbunit.properties" is useful for environment which make
     * it difficult if not impossible to use Maven's profiles.
     * Example is IntelliJ IDEA which when calling Junit tests,
     * bypass Maven completely.  Since profiles are not used,
     * database configuration is not properly set and tests fail.
     * "dbunit.properties" contains the missing properties which
     * a profile would set.
     * <p>
     * Following is an example of the content of "dbunit.properties":
     * <p>
     * DATABASE_PROFILE=h2
     * dbunit.profile.driverClass=org.hsqldb.jdbcDriver
     * dbunit.profile.url=jdbc:hsqldb:mem:.
     * <p>
     * Simply create "dbunit.properties" under "src/test/resources".
     *
     * @return Merged DbUnit and System properties.
     * @throws IOException Thrown if an error occurs when attempting to read "dbunit.properties".
     */
    protected static Properties getProperties() throws IOException {
        Properties dbUnitProperties = new Properties();
        InputStream inputStream = DatabaseEnvironment.class.getClassLoader()
          .getResourceAsStream("dbunit.properties");

        if (inputStream == null) {
            // No DbUnit properties.  Sending back only System properties together/
            return System.getProperties();
        }

        logger.info("Properties from file 'dbunit.properties' loaded");
        dbUnitProperties.load(inputStream);
        inputStream.close();

        // Merging DbUnit properties and System properties together.
        dbUnitProperties.putAll(System.getProperties());
        return dbUnitProperties;
    }

    public static DatabaseEnvironment getInstance() throws Exception
    {
        if (INSTANCE == null)
        {
            DatabaseProfile profile = new DatabaseProfile(getProperties());

            String profileName = profile.getActiveProfile();
            if (profileName == null || profileName.equals("hsqldb"))
            {
                INSTANCE = new HypersonicEnvironment(profile);
            }
            else if (profileName.equals("oracle"))
            {
                INSTANCE = new OracleEnvironment(profile);
            }
            else if (profileName.equals("oracle10"))
            {
                INSTANCE = new Oracle10Environment(profile);
            }
            else if (profileName.equals("postgresql"))
            {
                INSTANCE = new PostgresqlEnvironment(profile);
            }
            else if (profileName.equals("mysql"))
            {
                INSTANCE = new MySqlEnvironment(profile);
            }
            else if (profileName.equals("derby"))
            {
                INSTANCE = new DerbyEnvironment(profile);
            }
            else if (profileName.equals("h2"))
            {
                INSTANCE = new H2Environment(profile);
            }
            else if (profileName.equals("mssql"))
            {
                INSTANCE = new MsSqlEnvironment(profile);
            }
            else
            {
                INSTANCE = new DatabaseEnvironment(profile);
            }
        }

        return INSTANCE;
    }

    public DatabaseEnvironment(DatabaseProfile profile,
                               Callable<Void> preDdlFunction) throws Exception
    {
        if (null != preDdlFunction) {
            preDdlFunction.call();
        }

        _profile = profile;
        File file = TestUtils.getFile("xml/dataSetTest.xml");
        _dataSet = new XmlDataSet(new FileReader(file));
        _databaseTester = new JdbcDatabaseTester( _profile.getDriverClass(),
                _profile.getConnectionUrl(), _profile.getUser(), _profile.getPassword(), _profile.getSchema() );

        DdlExecutor.execute("sql/" + _profile.getProfileDdl(), getConnection().getConnection(),
                profile.getProfileMultilineSupport(), true);
    }

    public DatabaseEnvironment(DatabaseProfile profile) throws Exception
    {
        this(profile, null);
    }

    public IDatabaseConnection getConnection() throws Exception
    {
        // First check if the current connection is still valid and open
        // The connection may have been closed by a consumer
        if(_connection != null && _connection.getConnection().isClosed()){
            // Reset the member so that a new connection will be created
            _connection = null;
        }

        if (_connection == null)
        {
            String name = _profile.getDriverClass();
            Class.forName(name);
            Connection connection = DriverManager.getConnection(
                    _profile.getConnectionUrl(), _profile.getUser(),
                    _profile.getPassword());
            _connection = new DatabaseConnection(connection,
                    _profile.getSchema());
        }
        return _connection;
    }

    protected void setupDatabaseConfig(DatabaseConfig config)
    {
        // Override in subclasses as necessary.
    }

    public IDatabaseTester getDatabaseTester()
    {
        return _databaseTester;
    }

    public void closeConnection() throws Exception
    {
        if (_connection != null)
        {
            _connection.close();
            _connection = null;
        }
    }

    public IDataSet getInitDataSet() throws Exception
    {
        return _dataSet;
    }

    public DatabaseProfile getProfile() throws Exception
    {
        return _profile;
    }

    public boolean support(TestFeature feature)
    {
        String[] unsupportedFeatures = _profile.getUnsupportedFeatures();
        for (int i = 0; i < unsupportedFeatures.length; i++)
        {
            String unsupportedFeature = unsupportedFeatures[i];
            if (feature.toString().equals(unsupportedFeature))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the string converted as an identifier according to the metadata rules of the database environment.
     * Most databases convert all metadata identifiers to uppercase.
     * PostgreSQL converts identifiers to lowercase.
     * MySQL preserves case.
     * @param str The identifier.
     * @return The identifier converted according to database rules.
     */
    public String convertString(String str)
    {
        return str == null ? null : str.toUpperCase();
    }

    public String toString()
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getClass().getName()).append("[");
    	sb.append("_profile=").append(_profile);
    	sb.append(", _connection=").append(_connection);
    	sb.append(", _dataSet=").append(_dataSet);
    	sb.append(", _databaseTester=").append(_databaseTester);
    	sb.append("]");
    	return sb.toString();
    }
}

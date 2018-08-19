/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2017, DbUnit.org
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

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.db2.Db2DataTypeFactory;

/**
 * @author Andrew Landsverk
 * @since DbUnit 2.6.0
 */
public class Db2Environment extends DatabaseEnvironment
{

    public Db2Environment(final DatabaseProfile profile) throws Exception
    {
        super(profile);
    }

    @Override
    protected void setupDatabaseConfig(DatabaseConfig config)
    {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                new Db2DataTypeFactory());
    }

}

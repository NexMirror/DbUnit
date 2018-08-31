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
package org.dbunit.dataset.filter;

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.DataSetException;

import java.util.Collection;


/**
 * This filter hides specified tables from the filtered dataset. This
 * implementation do not modify the original table order from the filtered
 * dataset and support duplicate table names.
 *
 * @author Manuel Laflamme
 * @since Mar 7, 2003
 * @version $Revision$
 */
public class ExcludeTableFilter extends AbstractTableFilter implements ITableFilter
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ExcludeTableFilter.class);

    private final PatternMatcher _patternMatcher = new PatternMatcher();

    /**
     * Create a new empty ExcludeTableFilter. Use {@link #excludeTable} to hide
     * some tables.
     */
    public ExcludeTableFilter()
    {
    }

    /**
     * Create a new ExcludeTableFilter which prevent access to specified tables.
     */
    public ExcludeTableFilter(String[] tableNames)
    {
        for (int i = 0; i < tableNames.length; i++)
        {
            String tableName = tableNames[i];
            excludeTable(tableName);
        }
    }

    /**
     * Add a new refused table pattern name.
     * The following wildcard characters are supported:
     * '*' matches zero or more characters,
     * '?' matches one character.
     */
    public void excludeTable(String patternName)
    {
        logger.debug("excludeTable(patternName=" + patternName + ") - start");

        _patternMatcher.addPattern(patternName);
    }

    public boolean isEmpty()
    {
        logger.debug("isEmpty() - start");

        return _patternMatcher.isEmpty();
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableFilter interface

    public boolean isValidName(String tableName) throws DataSetException
    {
        logger.debug("isValidName(tableName=" + tableName + ") - start");

        return !_patternMatcher.accept(tableName);
    }

    /**
     * {@inheritDoc}
     *
     * This is similar to {@link #excludeTable}, but
     *
     * @param table {@inheritDoc}
     * @throws AmbiguousTableNameException
     */
    public void addTable(ITable table) throws AmbiguousTableNameException {
        logger.debug("addTable() - start");
        excludeTable(table.getTableMetaData().getTableName());
    }

    /**
     * {@inheritDoc}
     *
     * @param tables {@inheritDoc}
     * @throws AmbiguousTableNameException
     */
    public void addTables(Collection<ITable> tables) throws AmbiguousTableNameException {
        logger.debug("addTables(Collection) - start");
        for(ITable table: tables) {
            addTable(table);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param dataSet {@inheritDoc}
     * @throws AmbiguousTableNameException
     */
    public void addTables(IDataSet dataSet) throws DataSetException {
        logger.debug("addTables(IDataSet) - start");
        ITableIterator iterator = dataSet.iterator();
        while(iterator.next())
            addTable(iterator.getTable());
    }
}

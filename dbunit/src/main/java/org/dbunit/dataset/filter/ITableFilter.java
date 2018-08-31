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
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;

import java.util.Collection;

/**
 * Represents a strategy used by {@link org.dbunit.dataset.FilteredDataSet} to
 * exposes only some tables from a dataset.
 *
 * @author Manuel Laflamme
 * @since Mar 7, 2003
 * @version $Revision$
 */
public interface ITableFilter extends ITableFilterSimple
{
    /**
     * Returns the table names allowed by this filter from the specified dataset.
     *
     * @param dataSet the filtered dataset
     */
    public String[] getTableNames(IDataSet dataSet) throws DataSetException;

    /**
     * Returns iterator of tables allowed by this filter from the specified dataset.
     *
     * @param dataSet the filtered dataset
     */
    public ITableIterator iterator(IDataSet dataSet, boolean reversed)
            throws DataSetException;

    /**
     * Add a {@link org.dbunit.dataset.ITable} to the filter.
     *
     * @param table {@link ITable} to add to the filter.
     * @throws AmbiguousTableNameException
     */
    public void addTable(ITable table) throws AmbiguousTableNameException;

    /**
     * Add all {@link ITable} stored in a {@link java.util.Collection} to the filter.
     *
     * @param tables Collection of @link(org.dbunit.dataset.ITable) to add to the filter.
     * @throws AmbiguousTableNameException
     */
    public void addTables(Collection<ITable> tables) throws AmbiguousTableNameException;

    /**
     * Add all {@link org.dbunit.dataset.ITable} stored in a {@link org.dbunit.dataset.IDataSet} to the filter.
     *
     * @param dataSet {@link org.dbunit.dataset.IDataSet} from which all tables will be added to the filter.
     * @throws AmbiguousTableNameException
     */
    public void addTables(IDataSet dataSet) throws DataSetException;
}

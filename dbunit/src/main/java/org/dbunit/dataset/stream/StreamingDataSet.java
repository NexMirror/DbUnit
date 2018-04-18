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
package org.dbunit.dataset.stream;

import org.dbunit.dataset.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Dataset that consumes producer asynchronously.
 *
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Apr 18, 2003
 */
public class StreamingDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(StreamingDataSet.class);

    private IDataSetProducer _source;
    private int _iteratorCount;

    public StreamingDataSet(IDataSetProducer source)
    {
        _source = source;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        logger.debug("createIterator(reversed={}) - start", Boolean.valueOf(reversed));

        if (reversed)
        {
            throw new UnsupportedOperationException(
                    "Reverse iterator not supported!");
        }

        if (_iteratorCount > 0)
        {
            throw new UnsupportedOperationException(
                    "Only one iterator allowed!");
        }

        _iteratorCount++;
        return new StreamingIterator(_source);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    /**
     * Not supported.
     * @throws UnsupportedOperationException
     */
    public String[] getTableNames() throws DataSetException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     * @throws UnsupportedOperationException
     */
    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        logger.debug("getTableMetaData(tableName={}) - start", tableName);

        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     * @throws UnsupportedOperationException
     */
    public ITable getTable(String tableName) throws DataSetException
    {
        logger.debug("getTable(tableName={}) - start", tableName);

        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented since it does not make any sense to add a table to a
     * stream.  Beside, if the AbstractDataSet implementation is left to
     * execute, eventually the function getTable() of this class will be
     * called, and it is not implemented.
     *
     * @param table Ignored, not used.
     * @throws UnsupportedOperationException Reminder about the function not being implemented.
     */
    public void addTable(ITable table) throws UnsupportedOperationException
    {
        logger.debug("addTable() - start");
        throw new UnsupportedOperationException("Impossible to implement; does not make sense to add a table to a stream.");
    }

    /**
     * Not implemented since it does not make any sense to add a table to a
     * stream.  Beside, if the AbstractDataSet implementation is left to
     * execute, eventually the function getTable() of this class will be
     * called, and it is not implemented.
     *
     * @param tables Ignored, not used.
     * @throws UnsupportedOperationException Reminder about the function not being implemented.
     */
    public void addTables(Collection<ITable> tables) throws UnsupportedOperationException
    {
        logger.debug("addTables(Collection) - start");
        throw new UnsupportedOperationException("Impossible to implement; does not make sense to add a table to a stream.");
    }

    /**
     * Not implemented since it does not make any sense to add a table to a
     * stream.  Beside, if the AbstractDataSet implementation is left to
     * execute, eventually the function getTable() of this class will be
     * called, and it is not implemented.
     *
     * @param dataSet Ignored, not used.
     * @throws UnsupportedOperationException Reminder about the function not being implemented.
     */
    public void addTables(IDataSet dataSet) throws UnsupportedOperationException
    {
        logger.debug("addTables(IDataSet) - start");
        throw new UnsupportedOperationException("Impossible to implement; does not make sense to add a table to a stream.");
    }
}

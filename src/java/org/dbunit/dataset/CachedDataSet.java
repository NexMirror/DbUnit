/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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
package org.dbunit.dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @since Apr 18, 2003
 * @version $Revision$
 */
public class CachedDataSet extends AbstractDataSet
{
    private ITable[] _tables;

    public CachedDataSet(IDataSet dataSet) throws DataSetException
    {
        List tableList = new ArrayList();
        ITableIterator iterator = dataSet.iterator();
        while(iterator.next())
        {
            tableList.add(new CachedTable(iterator.getTable()));
        }
        _tables = (ITable[])tableList.toArray(new ITable[0]);
    }

    public CachedDataSet(IDataSetSource source) throws DataSetException
    {
        source.process(new Listener());
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        return new DefaultTableIterator(_tables, reversed);
    }

    private class Listener implements IDataSetListener
    {
        List _tableList = new ArrayList();
        ITableMetaData _activeMetaData;
        List _activeRowList;

        ////////////////////////////////////////////////////////////////////////
        // IDataSetListener interface

        public void startDataSet() throws DataSetException
        {
        }

        public void endDataSet() throws DataSetException
        {
            _tables = (ITable[])_tableList.toArray(new ITable[0]);
        }

        public void startTable(ITableMetaData metaData) throws DataSetException
        {
            _activeMetaData = metaData;
            _activeRowList = new ArrayList();
//            System.out.println("START " + _activeMetaData.getTableName());
        }

        public void endTable() throws DataSetException
        {
//            System.out.println("END " + _activeMetaData.getTableName());
            _tableList.add(new DefaultTable(_activeMetaData, _activeRowList));
            _activeRowList = null;
            _activeMetaData = null;
        }

        public void row(Object[] values) throws DataSetException
        {
            _activeRowList.add(values);
        }
    }
}

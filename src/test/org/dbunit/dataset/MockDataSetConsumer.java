package org.dbunit.dataset;

import com.mockobjects.Verifiable;
import com.mockobjects.ExpectationList;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;

/**
 *
 * <p> Copyright (c) 2002 OZ.COM.  All Rights Reserved. </p>
 * @author manuel.laflamme$
 * @since Apr 29, 2003$
 */
public class MockDataSetConsumer implements Verifiable, IDataSetConsumer
{
    private static final Item START_DATASET = new Item("startDataSet()");
    private static final Item END_DATASET = new Item("endDataSet()");

    private final ExpectationList _expectedList = new ExpectationList("");
    private String _actualTableName;
    private int _actualTableRow = 0;

    public void addExpectedStartDataSet() throws Exception
    {
        _expectedList.addExpected(START_DATASET);
    }

    public void addExpectedEndDataSet() throws Exception
    {
        _expectedList.addExpected(END_DATASET);
    }

    public void addExpectedStartTable(ITableMetaData metaData) throws Exception
    {
        _expectedList.addExpected(new StartTableItem(metaData));
    }

    public void addExpectedStartTable(String tableName, Column[] columns) throws Exception
    {
        addExpectedStartTable(new DefaultTableMetaData(tableName, columns));
    }

    public void addExpectedEndTable(String tableName) throws Exception
    {
        _expectedList.addExpected(new EndTableItem(tableName));
    }

    public void addExpectedRow(String tableName, int row, Object[] values) throws Exception
    {
        _expectedList.addExpected(new RowItem(tableName, row, values));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Verifiable interface

    public void verify()
    {
        _expectedList.verify();
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSetConsumer interface

    public void startDataSet() throws DataSetException
    {
        _expectedList.addActual(START_DATASET);
    }

    public void endDataSet() throws DataSetException
    {
        _expectedList.addActual(END_DATASET);
    }

    public void startTable(ITableMetaData metaData) throws DataSetException
    {
        _expectedList.addActual(new StartTableItem(metaData));
        _actualTableName = metaData.getTableName();
        _actualTableRow = 0;
    }

    public void endTable() throws DataSetException
    {
        _expectedList.addActual(new EndTableItem(_actualTableName));
        _actualTableName = null;
        _actualTableRow = 0;
    }

    public void row(Object[] values) throws DataSetException
    {
        _expectedList.addActual(
                new RowItem(_actualTableName, _actualTableRow, values));
        _actualTableRow++;
    }

    ////////////////////////////////////////////////////////////////////////////
    //

    private static class Item
    {
        protected final String _name;

        public Item(String name)
        {
            _name = name;
        }

        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Item)) return false;

            final Item item = (Item)o;

            if (!_name.equals(item._name)) return false;

            return true;
        }

        public int hashCode()
        {
            return _name.hashCode();
        }

        public String toString()
        {
            return _name;
        }
    }

    private static class StartTableItem extends Item
    {
        private final String _tableName;
        private final Column[] _columns;

        public StartTableItem(ITableMetaData metaData) throws DataSetException
        {
            super("startTable()");
            _tableName = metaData.getTableName();
            _columns = metaData.getColumns();
        }

        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof StartTableItem)) return false;
            if (!super.equals(o)) return false;

            final StartTableItem startTableItem = (StartTableItem)o;

            if (!Arrays.equals(_columns, startTableItem._columns)) return false;
            if (!_tableName.equals(startTableItem._tableName)) return false;

            return true;
        }

        public int hashCode()
        {
            int result = super.hashCode();
            result = 29 * result + _tableName.hashCode();
            return result;
        }

        public String toString()
        {
            return _name + ": table=" + _tableName + ", columns=" + Arrays.asList(_columns);
        }
    }

    private static class EndTableItem extends Item
    {
        private final String _tableName;

        public EndTableItem(String tableName)
        {
            super("endTable()");
            _tableName = tableName;
        }

        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof EndTableItem)) return false;
            if (!super.equals(o)) return false;

            final EndTableItem endTableItem = (EndTableItem)o;

            if (!_tableName.equals(endTableItem._tableName)) return false;

            return true;
        }

        public int hashCode()
        {
            int result = super.hashCode();
            result = 29 * result + _tableName.hashCode();
            return result;
        }

        public String toString()
        {
            return _name + ": table=" + _tableName;
        }
    }

    private static class RowItem extends Item
    {
        private final String _tableName;
        private final int _row;
        private final Object[] _values;

        public RowItem(String tableName, int row, Object[] values)
        {
            super("row()");
            _tableName = tableName;
            _row = row;
            _values = values;
        }

        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof RowItem)) return false;
            if (!super.equals(o)) return false;

            final RowItem rowItem = (RowItem)o;

            if (_row != rowItem._row) return false;
            if (!_tableName.equals(rowItem._tableName)) return false;
// Probably incorrect - comparing Object[] arrays with Arrays.equals
            if (!Arrays.equals(_values, rowItem._values)) return false;

            return true;
        }

        public int hashCode()
        {
            int result = super.hashCode();
            result = 29 * result + _tableName.hashCode();
            result = 29 * result + _row;
            return result;
        }

        public String toString()
        {
            return _name + ": table=" + _tableName + ", row=" + _row +
                    ", values=" + Arrays.asList(_values);
        }

    }
}

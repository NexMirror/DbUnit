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
package org.dbunit.dataset.xml;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.DefaultDataSetProvider;
import org.dbunit.dataset.IDataSetConsumer;

import org.dbunit.util.xml.DataWriter;
import org.dbunit.util.xml.XMLWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.Writer;

/**
 * @author Manuel Laflamme
 * @since Apr 19, 2003
 * @version $Revision$
 */
public class FlatXmlWriter
{
    private static final String CDATA = "CDATA";
    private static final String DATASET = "dataset";

    public void write(IDataSet dataSet, Writer writer) throws DataSetException
    {
        DataWriter dataWriter = new DataWriter(writer);
        dataWriter.setIndentStep(1);
        DefaultDataSetProvider provider = new DefaultDataSetProvider(dataSet);
        provider.setConsumer(new Consumer(dataWriter));
        provider.process();
    }

    private class Consumer implements IDataSetConsumer
    {
        private final XMLWriter _xmlWriter;
        private ITableMetaData _activeMetaData;

        public Consumer(XMLWriter xmlWriter)
        {
            _xmlWriter = xmlWriter;
        }

        ////////////////////////////////////////////////////////////////////////////
        // IDataSetConsumer interface

        public void startDataSet() throws DataSetException
        {
            try
            {
                _xmlWriter.startDocument();
                _xmlWriter.startElement("", DATASET, DATASET, new AttributesImpl());
            }
            catch (SAXException e)
            {
                throw new DataSetException(e.getException() == null ? e : e.getException());
            }
        }

        public void endDataSet() throws DataSetException
        {
            try
            {
                _xmlWriter.endDocument();
                _xmlWriter.endElement("", DATASET, DATASET);
            }
            catch (SAXException e)
            {
                throw new DataSetException(e.getException() == null ? e : e.getException());
            }
        }

        public void startTable(ITableMetaData metaData) throws DataSetException
        {
            _activeMetaData = metaData;
        }

        public void endTable() throws DataSetException
        {
            _activeMetaData = null;
        }

        public void row(Object[] values) throws DataSetException
        {
            try
            {
                AttributesImpl attributes = new AttributesImpl();
                Column[] columns = _activeMetaData.getColumns();
                for (int i = 0; i < columns.length; i++)
                {
                    String columnName = columns[i].getColumnName();
                    Object value = values[i];
                    attributes.addAttribute("", columnName, columnName, CDATA,
                            DataType.asString(value));
                }
                String tableName = _activeMetaData.getTableName();
                _xmlWriter.emptyElement("", tableName, tableName, attributes);
            }
            catch (SAXException e)
            {
                throw new DataSetException(e.getException() == null ? e : e.getException());
            }
        }
    }
}

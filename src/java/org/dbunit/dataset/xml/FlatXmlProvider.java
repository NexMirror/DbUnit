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
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.IDataSetConsumer;
import org.dbunit.dataset.IDataSetProvider;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * @author Manuel Laflamme
 * @since Apr 18, 2003
 * @version $Revision$
 */
public class FlatXmlProvider implements IDataSetProvider
{
    private final InputSource _inputSource;
    private XMLReader _xmlReader;
    private IDataSetConsumer _consumer;

    public FlatXmlProvider(InputSource inputSource)
    {
        _inputSource = inputSource;
    }

//    public FlatXmlProvider(InputSource inputSource, SAXParser parser)
//    {
//        _inputSource = inputSource;
//        _xmlReader = parser;
//    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSetProvider interface

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException
    {
        _consumer = consumer;
    }

    public void process() throws DataSetException
    {
        try
        {
            if (_xmlReader == null)
            {
                SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
                _xmlReader = saxParser.getXMLReader();
            }

            FlatXmlHandler handler = new FlatXmlHandler(_consumer);
            _xmlReader.setContentHandler(handler);
            _xmlReader.parse(_inputSource);
        }
        catch (ParserConfigurationException e)
        {
            throw new DataSetException(e);
        }
        catch (SAXException e)
        {
            throw new DataSetException(e.getException());
        }
        catch (IOException e)
        {
            throw new DataSetException(e);
        }
    }

    private class FlatXmlHandler extends DefaultHandler
    {
        private static final String DATASET = "dataset";

        private final IDataSetConsumer _listener;
        private ITableMetaData _activeMetaData;

        public FlatXmlHandler(IDataSetConsumer listener)
        {
            _listener = listener;
        }

        ////////////////////////////////////////////////////////////////////////
        // ContentHandler interface

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException
        {
            try
            {
                // Start of dataset
                if (_activeMetaData == null && qName.equals(DATASET))
                {
                    _listener.startDataSet();
                    return;
                }

                // New table
                if (_activeMetaData == null ||
                        !_activeMetaData.getTableName().equals(qName))
                {
                    // If not first table, notify end of previous table to listener
                    if (_activeMetaData != null)
                    {
                        _listener.endTable();
                    }

                    // Create metadata from attributes
                    Column[] columns = new Column[attributes.getLength()];
                    for (int i = 0; i < attributes.getLength(); i++)
                    {
                        columns[i] = new Column(attributes.getQName(i),
                                DataType.UNKNOWN);
                    }

                    // Notify start of new table to listener
                    _activeMetaData = new DefaultTableMetaData(qName, columns);
                    _listener.startTable(_activeMetaData);
                }

                // Row notification
                if (attributes.getLength() > 0)
                {
                    Column[] columns = _activeMetaData.getColumns();
                    Object[] rowValues = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++)
                    {
                        Column column = columns[i];
                        rowValues[i] = attributes.getValue(column.getColumnName());
                    }
                    _listener.row(rowValues);
                }
            }
            catch (DataSetException e)
            {
                throw new SAXException(e);
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            // End of dataset
            if (qName.equals(DATASET))
            {
                try
                {
                    // Notify end of active table to listener
                    if (_activeMetaData != null)
                    {
                        _listener.endTable();
                    }

                    // End of dataset to listener
                    _listener.endDataSet();
                }
                catch (DataSetException e)
                {
                    throw new SAXException(e);
                }
            }
        }
    }
}

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

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.IDataSetConsumer;
import org.dbunit.dataset.IDataSetProducer;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Manuel Laflamme
 * @since Apr 18, 2003
 * @version $Revision$
 */
public class FlatXmlProducer extends DefaultHandler
        implements IDataSetProducer, ContentHandler
{
    private static final String DATASET = "dataset";

    private final InputSource _inputSource;
    private XMLReader _xmlReader;
    private IDataSet _metaDataSet;
    boolean _ignoreDtd = false;

    private IDataSetConsumer _consumer;
    private ITableMetaData _activeMetaData;

    public FlatXmlProducer(InputSource xmlSource)
    {
        _inputSource = xmlSource;
    }

    public FlatXmlProducer(InputSource xmlSource, boolean ignoreDtd)
    {
        _inputSource = xmlSource;
        _ignoreDtd = ignoreDtd;
    }

    public FlatXmlProducer(InputSource xmlSource, IDataSet metaDataSet)
    {
        _inputSource = xmlSource;
        _metaDataSet = metaDataSet;
        _ignoreDtd = true;
    }

//    public FlatXmlProducer(InputSource inputSource, XMLReader xmlReader)
//    {
//        _inputSource = inputSource;
//        _xmlReader = xmlReader;
//    }

    private ITableMetaData createTableMetaData(String tableName,
            Attributes attributes) throws DataSetException
    {
        if (_metaDataSet != null)
        {
            return _metaDataSet.getTableMetaData(tableName);
        }

        // Create metadata from attributes
        Column[] columns = new Column[attributes.getLength()];
        for (int i = 0; i < attributes.getLength(); i++)
        {
            columns[i] = new Column(attributes.getQName(i),
                    DataType.UNKNOWN);
        }

        return new DefaultTableMetaData(tableName, columns);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSetProducer interface

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException
    {
        _consumer = consumer;
    }

    public void produce() throws DataSetException
    {
        try
        {
            if (_xmlReader == null)
            {
                SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
                _xmlReader = saxParser.getXMLReader();
            }

            if (!_ignoreDtd)
            {
                FlatDtdHandler dtdHandler = new FlatDtdHandler();
                FlatDtdHandler.setLexicalHandler(_xmlReader, dtdHandler);
                FlatDtdHandler.setDeclHandler(_xmlReader, dtdHandler);
            }

            _xmlReader.setContentHandler(this);
            _xmlReader.setEntityResolver(this);
            _xmlReader.parse(_inputSource);
        }
        catch (ParserConfigurationException e)
        {
            throw new DataSetException(e);
        }
        catch (SAXException e)
        {
            Exception exception = e.getException() == null ? e : e.getException();
            throw new DataSetException(exception);
        }
        catch (IOException e)
        {
            throw new DataSetException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // EntityResolver interface

    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException
    {
        if (_ignoreDtd)
        {
            return new InputSource(new StringReader(""));
        }
        return null;
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
                _consumer.startDataSet();
                return;
            }

            // New table
            if (_activeMetaData == null ||
                    !_activeMetaData.getTableName().equals(qName))
            {
                // If not first table, notify end of previous table to consumer
                if (_activeMetaData != null)
                {
                    _consumer.endTable();
                }

                // Notify start of new table to consumer
                _activeMetaData = createTableMetaData(qName, attributes);
                _consumer.startTable(_activeMetaData);
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
                _consumer.row(rowValues);
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
                // Notify end of active table to consumer
                if (_activeMetaData != null)
                {
                    _consumer.endTable();
                }

                // Notify end of dataset to consumer
                _consumer.endDataSet();
            }
            catch (DataSetException e)
            {
                throw new SAXException(e);
            }
        }
    }

    private class FlatDtdHandler extends FlatDtdProducer
    {
        public FlatDtdHandler()
        {
        }

        ////////////////////////////////////////////////////////////////////////////
        // LexicalHandler interface

        public void startDTD(String name, String publicId, String systemId)
                throws SAXException
        {
            try
            {
                // Cache the DTD content to use it as metadata
                CachedDataSet metaDataSet = new CachedDataSet();
                this.setConsumer(metaDataSet);
                _metaDataSet = metaDataSet;

                super.startDTD(name, publicId, systemId);
            }
            catch (DataSetException e)
            {
                throw new SAXException(e);
            }
        }
    }

}

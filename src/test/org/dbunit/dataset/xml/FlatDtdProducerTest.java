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

import org.dbunit.dataset.AbstractDataSetProducerTest;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSetProducer;
import org.dbunit.dataset.MockDataSetConsumer;

import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

/**
 * @author Manuel Laflamme
 * @since Apr 29, 2003
 * @version $Revision$
 */
public class FlatDtdProducerTest extends AbstractDataSetProducerTest
{
    private static final File DTD_FILE =
            new File("src/dtd/flatDtdProducerTest.dtd");

    public FlatDtdProducerTest(String s)
    {
        super(s);
    }

    protected IDataSetProducer createProducer() throws Exception
    {
        InputSource source = new InputSource(new FileInputStream(DTD_FILE));
        return new FlatDtdProducer(source);
    }

    public void testProduce() throws Exception
    {
        // Setup consumer
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        String[] expectedNames = getExpectedNames();
        for (int i = 0; i < expectedNames.length; i++)
        {
            // All tables except the one have nullable columns
            boolean nullable = (i + 1 < expectedNames.length);
            Column[] expectedColumns = createExpectedColumns(nullable);

            String expectedName = expectedNames[i];
            consumer.addExpectedStartTable(expectedName,
                    expectedColumns);
            consumer.addExpectedEndTable(expectedName);
        }
        consumer.addExpectedEndDataSet();

        // Setup producer
        IDataSetProducer producer = createProducer();
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

    public void testProduceWithoutConsumer() throws Exception
    {
        IDataSetProducer producer = createProducer();
        producer.produce();
    }

    public void testSequenceModel() throws Exception
    {
        // Setup consumer
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        consumer.addExpectedEmptyTableIgnoreColumns("DUPLICATE_TABLE");
        consumer.addExpectedEmptyTableIgnoreColumns("TEST_TABLE");
        consumer.addExpectedEmptyTableIgnoreColumns("DUPLICATE_TABLE");
        consumer.addExpectedEndDataSet();

        // Setup producer
        String content =
                "<!ELEMENT dataset (DUPLICATE_TABLE*,TEST_TABLE*,DUPLICATE_TABLE*)>" +
                "<!ELEMENT TEST_TABLE EMPTY>" +
                "<!ELEMENT DUPLICATE_TABLE EMPTY>";
        InputSource source = new InputSource(new StringReader(content));
        FlatDtdProducer producer = new FlatDtdProducer(source);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

    public void testChoicesModel() throws Exception
    {
        // Setup consumer
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        consumer.addExpectedEmptyTableIgnoreColumns("TEST_TABLE");
        consumer.addExpectedEmptyTableIgnoreColumns("SECOND_TABLE");
        consumer.addExpectedEndDataSet();

        // Setup producer
        String content =
                "<!ELEMENT dataset (TEST_TABLE|SECOND_TABLE)>" +
                "<!ELEMENT TEST_TABLE EMPTY>" +
                "<!ELEMENT SECOND_TABLE EMPTY>";
        InputSource source = new InputSource(new StringReader(content));
        FlatDtdProducer producer = new FlatDtdProducer(source);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

}

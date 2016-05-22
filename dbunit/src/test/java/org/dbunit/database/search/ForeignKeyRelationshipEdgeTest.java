/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
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
package org.dbunit.database.search;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

import junit.framework.TestCase;

/**
 * @author gommma
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
public class ForeignKeyRelationshipEdgeTest extends TestCase
{
    private final ForeignKeyRelationshipEdge e1 =
            new ForeignKeyRelationshipEdge("table1", "table2", "fk_col",
                    "pk_col");
    private final ForeignKeyRelationshipEdge equal =
            new ForeignKeyRelationshipEdge("table1", "table2", "fk_col",
                    "pk_col");
    private final ForeignKeyRelationshipEdge notEqual1 =
            new ForeignKeyRelationshipEdge("table1", "tableOther", "fk_col",
                    "pk_col");
    private final ForeignKeyRelationshipEdge notEqual2 =
            new ForeignKeyRelationshipEdge("table1", "table2", "fk_col_other",
                    "pk_col");

    private final ForeignKeyRelationshipEdge equalSubclass =
            new ForeignKeyRelationshipEdge("table1", "table2", "fk_col",
                    "pk_col")
            {
            };

    public void testEqualsHashCode()
    {
        // Use gsbase "EqualsTester" library for this - easier and less code for
        // equals/hashCode test
        new EqualsTester(e1, equal, notEqual1, equalSubclass);
        new EqualsTester(e1, equal, notEqual2, equalSubclass);
    }

    @Test
    public void testCompareTo()
    {
        assertThat("Equal instances have different compareTo.",
                e1.compareTo(equal), equalTo(0));

        assertThat(
                "Unequal parent values with equal child values:"
                        + " first compared after second.",
                e1.compareTo(notEqual1), lessThan(0));
        assertThat(
                "Unequal parent values with equal child values:"
                        + " first compared before second.",
                notEqual1.compareTo(e1), greaterThan(0));

        assertThat(
                "Equal parent values with unequal child values:"
                        + " first compared after second.",
                e1.compareTo(notEqual2), lessThan(0));
        assertThat(
                "Equal parent values with unequal child values:"
                        + " first compared before second.",
                notEqual2.compareTo(e1), greaterThan(0));
    }
}

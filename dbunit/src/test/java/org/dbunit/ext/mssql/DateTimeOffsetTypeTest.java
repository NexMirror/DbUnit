/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2019, DbUnit.org
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
package org.dbunit.ext.mssql;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.dbunit.dataset.datatype.TypeCastException;

import junit.framework.TestCase;

public class DateTimeOffsetTypeTest extends TestCase
{
    private DateTimeOffsetType type;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        type = new DateTimeOffsetType();
    }

    public void testTypeCastWithNull() throws TypeCastException
    {
        final Object result = type.typeCast(null);
        assertNull(result);
    }

    public void testTypeCastWithOffsetDateTime() throws TypeCastException
    {
        final Object result = type.typeCast(OffsetDateTime.MIN);
        assertSame(OffsetDateTime.MIN, result);
    }

    public void testTypeCastWithValidTemporalAccessor() throws TypeCastException
    {
        final ZonedDateTime now = ZonedDateTime.now();
        final Object result = type.typeCast(now);
        assertEquals(now.toOffsetDateTime(), result);
    }

    public void testTypeCastWithInvalidTemporalAccessor()
            throws TypeCastException
    {
        try
        {
            type.typeCast(LocalDateTime.now());
            fail("Should not be possible to convert due to insufficient information");
        } catch (final TypeCastException e)
        {
        }
    }

    public void testTypeCastWithISO_8601_String() throws TypeCastException
    {
        final Object result = type.typeCast("2000-01-01T01:00:00Z");
        assertEquals(OffsetDateTime.of(2000, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC),
                result);
    }

    public void testTypeCastWithSqlServerStringWithoutNanos()
            throws TypeCastException
    {
        final Object result = type.typeCast("2000-01-01 01:00:00 +00:00");
        assertEquals(OffsetDateTime.of(2000, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC),
                result);
    }

    public void testTypeCastWithSqlServerStringWithNanos()
            throws TypeCastException
    {
        final Object result =
                type.typeCast("2000-01-01 01:00:00.123000 +00:00");
        assertEquals(
                OffsetDateTime.of(2000, 1, 1, 1, 0, 0, 123000, ZoneOffset.UTC),
                result);
    }

    public void testTypeCastWithInvalidObject() throws TypeCastException
    {
        try
        {
            type.typeCast(new Object());
            fail("Should not be possible to convert due to invalid string format");
        } catch (final TypeCastException e)
        {
        }
    }
}

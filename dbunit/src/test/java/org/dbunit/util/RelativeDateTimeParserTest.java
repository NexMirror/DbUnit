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

package org.dbunit.util;

import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

public class RelativeDateTimeParserTest
{
    private static Clock CLOCK =
            Clock.fixed(Instant.now(), ZoneId.systemDefault());
    private static RelativeDateTimeParser parser =
            new RelativeDateTimeParser(CLOCK);

    @Test
    public void testNullInput() throws Exception
    {
        try
        {
            parser.parse(null);
            fail("IllegalArgumentException must be thrown when input is null.");
        } catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().contains(
                    "Relative datetime input must not be null or empty."));
        }
    }

    @Test
    public void testEmptyInput() throws Exception
    {
        try
        {
            parser.parse("");
            fail("IllegalArgumentException must be thrown when input is empty.");
        } catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().contains(
                    "Relative datetime input must not be null or empty."));
        }
    }

    @Test
    public void testInvalidInputs() throws Exception
    {
        // @formatter:off
        String[] inputs = {
                "[+1d]", // missing 'now' prefix
                "[NOW+1d", // missing closing bracket
                "[NOW+1x]", // invalid unit
                "[now+1d3y]", // missing +- sign
        };
        // @formatter:on
        for (String input : inputs)
        {
            verifyPatternMismatchError(input);
        }
    }

    private void verifyPatternMismatchError(String input)
    {
        try
        {
            parser.parse(input);
            fail("IllegalArgumentException must be thrown for input '" + input
                    + "'");
        } catch (IllegalArgumentException e)
        {
            assertEquals("'" + input
                    + "' does not match the expected pattern [now{diff}{time}]. "
                    + "Please see the data types documentation for the details. "
                    + "http://dbunit.sourceforge.net/datatypes.html#relativedatetime",
                    e.getMessage());
        }
    }

    @Test
    public void testInvalidFormat_UnparsableTime() throws Exception
    {
        try
        {
            parser.parse("[now 1:23]");
            fail("DateTimeParseException should be thrown.");
        } catch (DateTimeParseException e)
        {
            assertEquals("Text '1:23' could not be parsed at index 0",
                    e.getMessage());
        }
    }

    @Test
    public void testUnitResolution() throws Exception
    {
        LocalDateTime actual = parser.parse("[NOW+1y-2M+3d-4h+5m-6s]");
        LocalDateTime expected = LocalDateTime.now(CLOCK)
                .plus(1, ChronoUnit.YEARS).plus(-2, ChronoUnit.MONTHS)
                .plus(3, ChronoUnit.DAYS).plus(-4, ChronoUnit.HOURS)
                .plus(5, ChronoUnit.MINUTES).plus(-6, ChronoUnit.SECONDS);
        assertEquals(actual, expected);
    }

    @Test
    public void testOrderInsensitivity() throws Exception
    {
        LocalDateTime actual = parser.parse("[NOW+1s-2m+3h-4d+5M-6y]");
        LocalDateTime expected = LocalDateTime.now(CLOCK)
                .plus(1, ChronoUnit.SECONDS).plus(-2, ChronoUnit.MINUTES)
                .plus(3, ChronoUnit.HOURS).plus(-4, ChronoUnit.DAYS)
                .plus(5, ChronoUnit.MONTHS).plus(-6, ChronoUnit.YEARS);
        assertEquals(actual, expected);
    }

    @Test
    public void testWhitespaces() throws Exception
    {
        LocalDateTime actual = parser.parse("[NOW\t \r\n+1y  -2M\t\t+3d]");
        LocalDateTime expected =
                LocalDateTime.now(CLOCK).plus(1, ChronoUnit.YEARS)
                        .plus(-2, ChronoUnit.MONTHS).plus(3, ChronoUnit.DAYS);
        assertEquals(actual, expected);
    }

    @Test
    public void testNow() throws Exception
    {
        LocalDateTime actual = parser.parse("[NOW]");
        LocalDateTime expected = LocalDateTime.now(CLOCK);
        assertEquals(actual, expected);
    }

    @Test
    public void testHoursMinutes() throws Exception
    {
        LocalDateTime actual = parser.parse("[now 12:34]");
        LocalDateTime expected =
                LocalDateTime.of(LocalDate.now(CLOCK), LocalTime.of(12, 34));
        assertEquals(actual, expected);
    }

    @Test
    public void testHoursMinutesSeconds() throws Exception
    {
        LocalDateTime actual = parser.parse("[Now02:34:56]");
        LocalDateTime expected =
                LocalDateTime.of(LocalDate.now(CLOCK), LocalTime.of(2, 34, 56));
        assertEquals(actual, expected);
    }
}

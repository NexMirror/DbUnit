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

import junit.framework.TestCase;

/**
 * @author Manuel Laflamme
 * @since Apr 6, 2003
 * @version $Revision$
 */
public class AbstractTest extends TestCase
{
    private static final String[] TABLE_NAMES = {
        "TEST_TABLE",
        "SECOND_TABLE",
        "EMPTY_TABLE",
        "PK_TABLE",
        "ONLY_PK_TABLE",
        "EMPTY_MULTITYPE_TABLE",
    };
    private static final String[] DUPLICATE_TABLE_NAMES = {
        "DUPLICATE_TABLE",
        "EMPTY_TABLE",
        "DUPLICATE_TABLE",
    };
    private static final String EXTRA_TABLE_NAME = "EXTRA_TABLE";

    public AbstractTest(String s)
    {
        super(s);
    }

    protected String[] getExpectedNames() throws Exception
    {
        return (String[])AbstractTest.TABLE_NAMES.clone();
    }

    protected String[] getExpectedLowerNames() throws Exception
    {
        String[] names = (String[])AbstractTest.TABLE_NAMES.clone();
        for (int i = 0; i < names.length; i++)
        {
            names[i] = names[i].toLowerCase();
        }

        return names;
    }

    protected String[] getExpectedDuplicateNames()
    {
        return (String[])AbstractTest.DUPLICATE_TABLE_NAMES.clone();
    }

    protected String getDuplicateTableName()
    {
        return "DUPLICATE_TABLE";
    }

    public String getExtraTableName()
    {
        return AbstractTest.EXTRA_TABLE_NAME;
    }
}

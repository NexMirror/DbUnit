/*
 * Main.java   Mar 14, 2002
 *
 * DbUnit Database Testing Framework
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

package org.dbunit;

import java.io.FileOutputStream;
import java.io.File;
import java.util.Arrays;

import electric.xml.*;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
//        IDatabaseConnection connection =
//                DatabaseEnvironment.getInstance().getConnection();
//
//        String[] tableNames = connection.createDataSet().getTableNames();
//        Arrays.sort(tableNames);
//        FlatXmlDataSet.writeDtd(new FilteredDataSet(tableNames,
//                connection.createDataSet()),
//                new FileOutputStream("test.dtd"));
//
//
//        FlatXmlDataSet.write(new FilteredDataSet(tableNames,
//                connection.createDataSet()),
//                new FileOutputStream("test.xml"));


        ////////////////////////////////
        Document document = new Document(new File("test.xml"));
        DocType docType = document.getDocType();
        System.out.println(docType);

        // display children of DocType
        for (Children decls = docType.getChildren(); decls.hasMoreElements();)
        {
            Child decl = decls.next();
            String type = decl.getClass().getName();
            System.out.println("decl = " + decl + ", class: " + type);
        }

//
//        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
//        DatabaseOperation.DELETE.execute(connection, dataSet);
//        DatabaseOperation.INSERT.execute(connection, dataSet);
//        DatabaseOperation.UPDATE.execute(connection, dataSet);
//        DatabaseOperation.REFRESH.execute(connection, dataSet);
//        DatabaseOperation.DELETE_ALL.execute(connection, dataSet);
//        DatabaseOperation.REFRESH.execute(connection, dataSet);


//        FlatXmlDataSet.write(new XmlDataSet(
//                new FileInputStream("src/xml/refreshOperationTest.xml")),
//                new FileOutputStream("src/xml/refreshOperationTestSetup.xml"));
}

}










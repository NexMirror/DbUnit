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
package org.dbunit.util.xml;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;

/**
 * @author Manuel Laflamme
 * @since Apr 27, 2003
 * @version $Revision$
 */
public class ExtendedDefaultHandler extends DefaultHandler
        implements DeclHandler, LexicalHandler
{
    ////////////////////////////////////////////////////////////////////////////
    // DeclHandler interface

    public void elementDecl(String name, String model)
            throws SAXException
    {
    }

    public void attributeDecl(String eName,
            String aName,
            String type,
            String valueDefault,
            String value)
            throws SAXException
    {
    }

    public void internalEntityDecl(String name, String value)
            throws SAXException
    {
    }

    public void externalEntityDecl(String name, String publicId,
            String systemId)
            throws SAXException
    {
    }

    ////////////////////////////////////////////////////////////////////////////
    // LexicalHandler interface

    public void startDTD(String name, String publicId,
            String systemId)
            throws SAXException
    {
    }

    public void endDTD()
            throws SAXException
    {
    }

    public void startEntity(String name)
            throws SAXException
    {
    }

    public void endEntity(String name)
            throws SAXException
    {
    }

    public void startCDATA()
            throws SAXException
    {
    }

    public void endCDATA()
            throws SAXException
    {
    }

    public void comment(char ch[], int start, int length)
            throws SAXException
    {
    }
}

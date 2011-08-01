/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugin.migration.artifactory.util;

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class DomUtil
{

    public static String getValue( Xpp3Dom dom, String nodeName )
    {
        Xpp3Dom node = dom.getChild( nodeName );
        if ( node == null )
        {
            return null;
        }
        return node.getValue();
    }
    
    public static Xpp3Dom findReference( Xpp3Dom dom )
    {
        String ref = dom.getAttribute( "reference" );

        Xpp3Dom currentDom = dom;

        String[] tokens = ref.split( "/" );

        for ( String token : tokens )
        {
            if ( token.equals( ".." ) )
            {
                currentDom = currentDom.getParent();
            }
            else if ( token.contains( "[" ) && token.contains( "]" ) )
            {
                int squareStart = token.indexOf( '[' );

                int squareEnd = token.indexOf( ']' );

                String childGroup = token.substring( 0, squareStart );

                String childIndex = token.substring( squareStart + 1, squareEnd );

                currentDom = currentDom.getChildren( childGroup )[Integer.parseInt( childIndex ) - 1];
            }
            else
            {
                currentDom = currentDom.getChild( token );
            }
        }
        return currentDom;
    }
}

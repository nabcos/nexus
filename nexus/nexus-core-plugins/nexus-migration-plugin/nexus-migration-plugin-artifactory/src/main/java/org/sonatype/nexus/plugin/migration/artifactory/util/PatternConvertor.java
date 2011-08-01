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

import java.util.List;

/**
 * Convert artifactory style pattern to nexus style pattern (which is regular expression)
 * 
 * @author Juven Xu
 */
public class PatternConvertor
{
    public static String convert125Pattern( String path )
    {
        if ( path.equals( "ANY" ) )
        {
            return ".*";
        }
        else
        {
            if ( !path.equals( "/" ) )
            {
                path = path + "/";
            }
            return path + ".*";
        }
    }

    /**
     * Ant style (*, **, ?)
     * 
     * @param includes
     * @param excludes
     * @return
     */
    public static String convert130Pattern( List<String> includes, List<String> excludes )
    {
        // default is all
        if ( includes.isEmpty() )
        {
            includes.add( "**" );
        }

        StringBuffer regx = new StringBuffer();

        for ( int i = 0; i < includes.size(); i++ )
        {
            if ( i > 0 )
            {
                regx.append( "|" );
            }

            regx.append( "(" + convertAntStylePattern( includes.get( i ) ) + ")" );
        }

        // TODO: how to append excludes?

        return regx.toString();
    }

    /**
     * ? -> .{1} </br> ** -> .* </br> * -> [^/]*
     * 
     * @param pattern
     * @return
     */
    public static String convertAntStylePattern( String pattern )
    {
        StringBuffer regx = new StringBuffer();

        for ( int i = 0; i < pattern.length(); )
        {

            if ( pattern.charAt( i ) == '?' )
            {
                regx.append( ".{1}" );

                i++;
            }
            else if ( pattern.charAt( i ) == '*' && ( i + 1 ) != pattern.length() && pattern.charAt( i + 1 ) == '*' )
            {
                regx.append( ".*" );

                i += 2;
            }
            else if ( pattern.charAt( i ) == '*' )
            {
                regx.append( "[^/]*" );

                i++;
            }
            else if ( pattern.charAt( i ) == '.' )
            {
                regx.append( "\\." );
                
                i++;
            }
            else
            {
                regx.append( pattern.charAt( i ) );

                i++;
            }
        }

        return regx.toString();
    }
}

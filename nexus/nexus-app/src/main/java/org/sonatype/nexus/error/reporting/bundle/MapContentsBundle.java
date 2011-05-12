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
package org.sonatype.nexus.error.reporting.bundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.sonatype.sisu.pr.bundle.AbstractBundle;

public class MapContentsBundle
    extends AbstractBundle
{

    private static final String LINE_SEPERATOR = System.getProperty( "line.separator" );

    private byte[] content;

    public MapContentsBundle( Map<String, Object> context )
        throws UnsupportedEncodingException
    {
        super( "contextListing.txt", "text/plain" );

        StringBuilder sb = new StringBuilder();

        for ( String key : context.keySet() )
        {
            sb.append( "key: " + key );
            sb.append( LINE_SEPERATOR );

            Object o = context.get( key );
            sb.append( "value: " + o == null ? "null" : o.toString() );
            sb.append( LINE_SEPERATOR );
            sb.append( LINE_SEPERATOR );
        }

        this.content = sb.toString().getBytes( "utf-8" );
    }

    @Override
    protected InputStream openStream()
        throws IOException
    {
        return new ByteArrayInputStream( content );
    }

    @Override
    public long getContentLength()
    {
        return content.length;
    }

}

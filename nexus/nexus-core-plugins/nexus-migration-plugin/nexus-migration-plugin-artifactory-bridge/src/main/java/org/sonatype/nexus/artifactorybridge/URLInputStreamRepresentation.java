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
package org.sonatype.nexus.artifactorybridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.codehaus.plexus.util.IOUtil;
import org.restlet.data.MediaType;
import org.restlet.resource.OutputRepresentation;

public class URLInputStreamRepresentation
    extends OutputRepresentation
{

    private InputStream input;

    private HttpURLConnection urlConn;

    public URLInputStreamRepresentation( String type, InputStream input, HttpURLConnection urlConn )
    {
        super( MediaType.valueOf( type ) );
        if ( input == null )
        {
            throw new NullPointerException( "input" );
        }
        if ( urlConn == null )
        {
            throw new NullPointerException( "urlConn" );
        }
        this.input = input;
        this.urlConn = urlConn;
    }

    @Override
    public void write( OutputStream out )
        throws IOException
    {
        IOUtil.copy( input, out );
        out.flush();
    }

    @Override
    public void release()
    {
        IOUtil.close( input );
        urlConn.disconnect();

        input = null;
        urlConn = null;

        super.release();
    }
}

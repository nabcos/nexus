/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.gwt.client.request;

import java.util.Map;

import org.sonatype.gwt.client.resource.Resource;
import org.sonatype.gwt.client.resource.Variant;

import com.google.gwt.http.client.RequestBuilder;

public class DefaultRESTRequestBuilder
    implements RESTRequestBuilder
{

    private String scheme = "http";

    private String hostname = "localhost";

    private String port = "8081";

    protected class FullRequestBuilder
        extends RequestBuilder
    {
        public FullRequestBuilder( String httpMethod, String url )
        {
            super( httpMethod, url );
        }
    }

    public String getHostname()
    {
        return hostname;
    }

    public void setHostname( String hostname )
    {
        this.hostname = hostname;
    }

    public String getScheme()
    {
        return scheme;
    }

    public void setScheme( String scheme )
    {
        this.scheme = scheme;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort( String port )
    {
        this.port = port;
    }

    protected String getUrl( String path )
    {
        String url = getScheme() + "://" + getHostname();

        if ( getPort() != null )
        {
            url = url + ":" + getPort();
        }

        if ( path.startsWith( "/" ) )
        {
            url = url + path;
        }
        else
        {
            url = url + "/" + path;
        }

        return url;
    }

    public RequestBuilder buildDelete( Resource resource )
    {
        return build( "DELETE", resource, null );
    }

    public RequestBuilder buildGet( Resource resource, Variant variant )
    {
        return build( "GET", resource, variant );
    }

    public RequestBuilder buildHead( Resource resource, Variant variant )
    {
        return build( "HEAD", resource, variant );
    }

    public RequestBuilder buildPost( Resource resource, Variant variant )
    {
        return build( "POST", resource, variant );
    }

    public RequestBuilder buildPut( Resource resource, Variant variant )
    {
        return build( "PUT", resource, variant );
    }

    protected RequestBuilder build( String method, Resource resource, Variant variant )
    {
        RequestBuilder result = new FullRequestBuilder( method, getUrl( resource.getPath() ) );

        if ( variant != null )
        {
            result.setHeader( "Accept", variant.getMediaType() );

            result.setHeader( "Content-Type", variant.getMediaType() );
        }

        for ( Map.Entry<String, String> header : resource.getHeaders().entrySet() )
        {
            result.setHeader( header.getKey(), header.getValue() );
        }

        return result;
    }

}

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
package org.sonatype.nexus.plugins.lvo.strategy;

import java.io.IOException;
import java.util.Properties;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.plugins.lvo.DiscoveryRequest;
import org.sonatype.nexus.plugins.lvo.DiscoveryResponse;
import org.sonatype.nexus.plugins.lvo.DiscoveryStrategy;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;

/**
 * This is a "remote" strategy, uses HTTP GET to get a Java properties file and get filtered keys from there. It extends
 * the HttpGetDiscoveryStrategy, and assumes that a GETted content is a java.util.Properties file.
 * 
 * @author cstamas
 */
@Component( role = DiscoveryStrategy.class, hint = "http-get-properties" )
public class HttpGetPropertiesDiscoveryStrategy
    extends AbstractRemoteDiscoveryStrategy
{
    public DiscoveryResponse discoverLatestVersion( DiscoveryRequest request )
        throws NoSuchRepositoryException,
            IOException
    {
        DiscoveryResponse dr = new DiscoveryResponse( request );

        // handle
        RequestResult response = handleRequest( getRemoteUrl( request ) );

        if ( response != null )
        {
            Properties properties = new Properties();

            try
            {
                properties.load( response.getInputStream() );
            }
            finally
            {
                response.close();
            }

            String keyPrefix = request.getKey() + ".";

            // repack it into response
            for ( Object key : properties.keySet() )
            {
                String keyString = key.toString();

                if ( keyString.startsWith( keyPrefix ) )
                {
                    dr.getResponse().put( key.toString().substring( keyPrefix.length() ), properties.get( key ) );

                    dr.setSuccessful( true );
                }
            }
        }
        else
        {
            dr.setSuccessful( false );
        }

        return dr;
    }
}

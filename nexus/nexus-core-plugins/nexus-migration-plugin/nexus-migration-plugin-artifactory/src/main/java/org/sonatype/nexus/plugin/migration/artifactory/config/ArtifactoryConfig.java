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
package org.sonatype.nexus.plugin.migration.artifactory.config;

import static org.sonatype.nexus.plugin.migration.artifactory.util.DomUtil.getValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class ArtifactoryConfig
{
    private final Xpp3Dom dom;

    private Map<String, ArtifactoryRepository> localRepositories;

    private Map<String, ArtifactoryRepository> remoteRepositories;

    private Map<String, ArtifactoryVirtualRepository> virtualRepositories;

    public ArtifactoryConfig( Xpp3Dom dom )
    {
        this.dom = dom;
    }

    @SuppressWarnings( "deprecation" )
    public static ArtifactoryConfig read( File file )
        throws IOException, XmlPullParserException
    {
        XmlStreamReader reader = ReaderFactory.newXmlReader( file );
        try
        {
            return new ArtifactoryConfig( Xpp3DomBuilder.build( reader ) );
        }
        finally
        {
            reader.close();
        }
    }

    @SuppressWarnings( "deprecation" )
    public static ArtifactoryConfig read( InputStream input )
        throws IOException, XmlPullParserException
    {
        XmlStreamReader reader = ReaderFactory.newXmlReader( input );
        try
        {
            return new ArtifactoryConfig( Xpp3DomBuilder.build( reader ) );
        }
        finally
        {
            reader.close();
        }
    }

    public Map<String, ArtifactoryRepository> getLocalRepositories()
    {
        if ( localRepositories == null )
        {
            localRepositories = load( "localRepositories" );
        }
        return localRepositories;
    }

    private Map<String, ArtifactoryRepository> load( String nodeName )
    {
        Xpp3Dom repositoriesDom = dom.getChild( nodeName );
        final Map<String, ArtifactoryRepository> repos;
        if ( repositoriesDom == null )
        {
            repos = Collections.emptyMap();
        }
        else
        {
            repos = new HashMap<String, ArtifactoryRepository>();
            for ( Xpp3Dom repoDom : repositoriesDom.getChildren() )
            {
                ArtifactoryRepository repo = new ArtifactoryRepository( repoDom );
                repos.put( repo.getKey(), repo );
            }
        }

        return Collections.unmodifiableMap( repos );
    }

    public Map<String, ArtifactoryRepository> getRemoteRepositories()
    {
        if ( remoteRepositories == null )
        {
            remoteRepositories = load( "remoteRepositories" );
        }
        return remoteRepositories;
    }

    public Map<String, ArtifactoryVirtualRepository> getVirtualRepositories()
    {
        if ( virtualRepositories == null )
        {
            Xpp3Dom repositoriesDom = dom.getChild( "virtualRepositories" );
            if ( repositoriesDom == null )
            {
                virtualRepositories = new HashMap<String, ArtifactoryVirtualRepository>();
            }
            else
            {
                virtualRepositories = new LinkedHashMap<String, ArtifactoryVirtualRepository>();
                for ( Xpp3Dom repoDom : repositoriesDom.getChildren( "virtualRepository" ) )
                {
                    ArtifactoryVirtualRepository virtualRepo = new ArtifactoryVirtualRepository( repoDom );
                    virtualRepositories.put( virtualRepo.getKey(), virtualRepo );
                }
            }

            List<String> repos = new ArrayList<String>();
            repos.addAll( getLocalRepositories().keySet() );
            repos.addAll( getRemoteRepositories().keySet() );

            ArtifactoryDefaultVirtualRepository repo = new ArtifactoryDefaultVirtualRepository( repos );
            virtualRepositories.put( repo.getKey(), repo );

            virtualRepositories = Collections.unmodifiableMap( virtualRepositories );
        }
        return virtualRepositories;
    }

    public Map<String, ArtifactoryProxy> getProxies()
    {
        Xpp3Dom proxiesDom = dom.getChild( "proxies" );
        if ( proxiesDom == null )
        {
            return Collections.emptyMap();
        }

        Map<String, ArtifactoryProxy> proxies = new LinkedHashMap<String, ArtifactoryProxy>();
        for ( Xpp3Dom proxyDom : proxiesDom.getChildren( "proxy" ) )
        {
            proxies.put( getValue( proxyDom, "key" ), new ArtifactoryProxy( proxyDom ) );
        }
        proxies = Collections.unmodifiableMap( proxies );
        return proxies;
    }

    public Map<String, ArtifactoryRepository> getRepositories()
    {
        Map<String, ArtifactoryRepository> repositories = new HashMap<String, ArtifactoryRepository>();
        repositories.putAll( getLocalRepositories() );
        repositories.putAll( getRemoteRepositories() );
        repositories = Collections.unmodifiableMap( repositories );
        return repositories;
    }

}

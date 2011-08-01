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

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.sonatype.nexus.plugin.migration.artifactory.persist.MappingConfiguration;
import org.sonatype.nexus.plugin.migration.artifactory.persist.model.CMapping;

@Component( role = UrlConverter.class )
public class DefaultUrlConverter
    extends AbstractLogEnabled
    implements UrlConverter
{

    private static final String GROUP = "/content/groups/";

    private static final String REPOSITORY = "/content/repositories/";

    @Requirement( role = MappingConfiguration.class, hint = "default" )
    private MappingConfiguration mappingConfiguration;

    public String convertDownload( String servletPath )
    {
        return convert( servletPath, false );
    }

    private String convert( String servletPath, boolean resolveRepository )
    {
        // servletPath: /artifactory/main-local/nxcm259/released/1.0/released-1.0.pom
        if ( servletPath == null || servletPath.length() < 12 )
        {
            return null;
        }

        // cut /artifactory
        servletPath = servletPath.substring( 12 );

        // repository: main-local
        int artifactPathIndex = servletPath.indexOf( "/", 1 );
        if ( artifactPathIndex == -1 )
        {
            getLogger().error( "Unexpected servletPath: " + servletPath );
            return null;
        }

        String nexusContext = mappingConfiguration.getNexusContext();
        if(nexusContext == null)
        {
            nexusContext = "/nexus";
        }

        String repository = servletPath.substring( 1, artifactPathIndex );

        CMapping map = mappingConfiguration.getMapping( repository );
        if ( map == null )
        {
            getLogger().error( "Mapping not found to: " + repository );
            return null;
        }

        // path: /nxcm259/released/1.0/released-1.0.pom
        String artifactPath = servletPath.substring( artifactPathIndex );

        if ( map.getNexusGroupId() != null )
        {
            if ( resolveRepository )
            {
                int lastSlash = artifactPath.lastIndexOf( "/" );
                int previousSlash = 0;
                do
                {
                    int slash = artifactPath.indexOf( "/", previousSlash + 1 );
                    if ( slash == lastSlash )
                    {
                        break;
                    }

                    previousSlash = slash;
                }
                while ( true );

                String version = artifactPath.substring( previousSlash, lastSlash );
                if ( version.endsWith( "-SNAPSHOT" ) )
                {
                    return nexusContext + REPOSITORY + map.getSnapshotsRepositoryId() + artifactPath;
                }
                else
                {
                    return nexusContext + REPOSITORY + map.getReleasesRepositoryId() + artifactPath;
                }
            }
            else
            {
                return nexusContext + GROUP + map.getNexusGroupId() + artifactPath;
            }
        }
        else
        {
            return nexusContext + REPOSITORY + map.getNexusRepositoryId() + artifactPath;
        }
    }

    public String convertDeploy( String servletPath )
    {
        return convert( servletPath, true );
    }

}

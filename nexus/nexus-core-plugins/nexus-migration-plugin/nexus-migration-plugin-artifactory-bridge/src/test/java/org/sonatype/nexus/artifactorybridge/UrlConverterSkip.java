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

import org.codehaus.plexus.PlexusTestCase;
import org.sonatype.nexus.plugin.migration.artifactory.persist.MappingConfiguration;
import org.sonatype.nexus.plugin.migration.artifactory.persist.model.CMapping;

public class UrlConverterSkip
    extends PlexusTestCase
{

    private UrlConverter urlConverter;

    @Override
    protected void setUp()
        throws Exception
    {
        urlConverter = (UrlConverter) lookup( UrlConverter.class );
        MappingConfiguration cfg = (MappingConfiguration) lookup( MappingConfiguration.class );
        cfg.addMapping( new CMapping( "repo1", "central" ) );
        cfg.addMapping( new CMapping( "libs-local", "libs-local", "libs-local-releases", "libs-local-snapshots" ) );
    }

    public void testDownload()
        throws Exception
    {
        String url;

        url = urlConverter.convertDownload( "/repo1/org/apache/maven/2.0.9/maven-2.0.9.zip" );
        assertEquals( "/content/repositories/central/org/apache/maven/2.0.9/maven-2.0.9.zip", url );

        url = urlConverter.convertDownload( "/libs-local/local/lib/1.0-SNAPSHOT/lib-1.0-SNAPSHOT.jar" );
        assertEquals( "/content/groups/libs-local/local/lib/1.0-SNAPSHOT/lib-1.0-SNAPSHOT.jar", url );

        assertNull( urlConverter.convertDownload( "/" ) );
        assertNull( urlConverter.convertDownload( null ) );
        assertNull( urlConverter.convertDownload( "dummy" ) );
    }

}

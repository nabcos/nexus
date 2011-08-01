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
package org.sonatype.nexus.plugin.migration.artifactory.config.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sonatype.nexus.plugin.migration.artifactory.config.ArtifactoryConfig;
import org.sonatype.nexus.plugin.migration.artifactory.config.ArtifactoryVirtualRepository;
import org.sonatype.nexus.plugin.migration.artifactory.util.VirtualRepositoryUtil;

public class ResolveVirtualRepositoriesTest
{

    @Test
    public void resolveVirtualRepos()
        throws Exception
    {
        InputStream input = getClass().getResourceAsStream( "/config-virtual-repos.xml" );
        ArtifactoryConfig config = ArtifactoryConfig.read( input );

        Map<String, ArtifactoryVirtualRepository> virtualRepos = config.getVirtualRepositories();
        VirtualRepositoryUtil.resolveRepositories( virtualRepos );

        List<String> resolvedRemoteRepos = virtualRepos.get( "remote-repos" ).getResolvedRepositories();
        assertNotNull( resolvedRemoteRepos );
        assertEquals( 3, resolvedRemoteRepos.size() );
        assertTrue( resolvedRemoteRepos.contains( "java.net.m2" ) );
        assertTrue( resolvedRemoteRepos.contains( "java.net.m1" ) );
        assertTrue( resolvedRemoteRepos.contains( "repo1" ) );

        List<String> resolvedSnapshots = virtualRepos.get( "plugins-snapshots" ).getResolvedRepositories();
        assertNotNull( resolvedSnapshots );
        assertEquals( 5, resolvedSnapshots.size() );
        assertTrue( resolvedSnapshots.contains( "java.net.m2" ) );
        assertTrue( resolvedSnapshots.contains( "java.net.m1" ) );
        assertTrue( resolvedSnapshots.contains( "repo1" ) );
        assertTrue( resolvedSnapshots.contains( "plugins-snapshots-local" ) );
        assertTrue( resolvedSnapshots.contains( "ext-snapshots-local" ) );

    }
}

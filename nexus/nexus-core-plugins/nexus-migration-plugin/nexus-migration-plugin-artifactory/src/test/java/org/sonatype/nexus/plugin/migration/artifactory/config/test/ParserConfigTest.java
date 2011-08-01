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

import java.io.InputStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sonatype.nexus.plugin.migration.artifactory.config.ArtifactoryConfig;
import org.sonatype.nexus.plugin.migration.artifactory.config.ArtifactoryProxy;
import org.sonatype.nexus.plugin.migration.artifactory.config.ArtifactoryRepository;
import org.sonatype.nexus.plugin.migration.artifactory.config.ArtifactoryVirtualRepository;

public class ParserConfigTest
{

    @Test
    public void parseConfig125()
        throws Exception
    {

        InputStream input = getClass().getResourceAsStream( "/config.1.2.5.xml" );
        ArtifactoryConfig config = ArtifactoryConfig.read( input );

        // validate local repos
        Map<String, ArtifactoryRepository> localRepositories = config.getLocalRepositories();
        Assert.assertNotNull( localRepositories );
        Assert.assertEquals( 6, localRepositories.size() );

        ArtifactoryRepository libsReleases = localRepositories.get( "libs-releases" );
        Assert.assertEquals( "libs-releases", libsReleases.getKey() );
        Assert.assertEquals( "Local repository for in-house libraries", libsReleases.getDescription() );
        Assert.assertTrue( libsReleases.getHandleReleases() );
        Assert.assertFalse( libsReleases.getHandleSnapshots() );
        Assert.assertNull( libsReleases.getUrl() );

        ArtifactoryRepository extSnapshots = localRepositories.get( "ext-snapshots" );
        Assert.assertEquals( "ext-snapshots", extSnapshots.getKey() );
        Assert.assertEquals( "Local repository for third party snapshots", extSnapshots.getDescription() );
        Assert.assertFalse( extSnapshots.getHandleReleases() );
        Assert.assertTrue( extSnapshots.getHandleSnapshots() );
        Assert.assertNull( extSnapshots.getUrl() );

        // validate remote repos
        Map<String, ArtifactoryRepository> remoteRepositories = config.getRemoteRepositories();
        Assert.assertNotNull( remoteRepositories );
        Assert.assertEquals( 3, remoteRepositories.size() );

        ArtifactoryRepository repo1 = remoteRepositories.get( "repo1" );
        Assert.assertEquals( "repo1", repo1.getKey() );
        Assert.assertNull( repo1.getDescription() );
        Assert.assertTrue( repo1.getHandleReleases() );
        Assert.assertFalse( repo1.getHandleSnapshots() );
        Assert.assertEquals( "http://repo1.maven.org/maven2", repo1.getUrl() );

        ArtifactoryRepository codehausSnapshots = remoteRepositories.get( "codehaus-snapshots" );
        Assert.assertEquals( "codehaus-snapshots", codehausSnapshots.getKey() );
        Assert.assertNull( codehausSnapshots.getDescription() );
        Assert.assertFalse( codehausSnapshots.getHandleReleases() );
        Assert.assertTrue( codehausSnapshots.getHandleSnapshots() );
        Assert.assertEquals( "http://snapshots.repository.codehaus.org", codehausSnapshots.getUrl() );

        // validate virtual repos
        Map<String, ArtifactoryVirtualRepository> virtualRepositories = config.getVirtualRepositories();
        Assert.assertNotNull( virtualRepositories );
        Assert.assertEquals( 2, virtualRepositories.size() );

        ArtifactoryVirtualRepository snapshotsOnly = virtualRepositories.get( "snapshots-only" );
        Assert.assertEquals( "snapshots-only", snapshotsOnly.getKey() );
        Assert.assertEquals( 4, snapshotsOnly.getRepositories().size() );
        Assert.assertEquals( "plugins-snapshots", snapshotsOnly.getRepositories().get( 1 ) );

        // validate Default Virtual Repository
        ArtifactoryVirtualRepository repo = virtualRepositories.get( "repo" );
        Assert.assertEquals( "repo", repo.getKey() );
        Assert.assertEquals( config.getRepositories().size(), repo.getRepositories().size() );

        // validate proxies
        Map<String, ArtifactoryProxy> proxies = config.getProxies();
        Assert.assertNotNull( proxies );
        Assert.assertEquals( 1, proxies.size() );

        ArtifactoryProxy unsuedProxy = proxies.values().iterator().next();
        Assert.assertEquals( "unused-proxy", unsuedProxy.getKey() );
        Assert.assertEquals( "host", unsuedProxy.getHost() );
        Assert.assertEquals( 8080, unsuedProxy.getPort() );
        Assert.assertEquals( "un", unsuedProxy.getUsername() );
        Assert.assertEquals( "pw", unsuedProxy.getPassword() );
        Assert.assertEquals( "mydomain", unsuedProxy.getDomain() );
    }
}

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
package org.sonatype.nexus.plugin.migration.artifactory.security;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.sonatype.nexus.plugin.migration.artifactory.security.builder.ArtifactorySecurityConfigBuilder;

public class ParseSecurityConfig213Test
{
    protected ArtifactorySecurityConfig securityConfig;

    @Before
    public void parseSecurityConfig()
        throws Exception
    {
        // Note that the whole test case is based on this configuration file
        securityConfig = ArtifactorySecurityConfigBuilder.read( getClass().getResourceAsStream(
            "/security-config-2.1.3.xml" ) );
    }

    @Test
    public void assertGroup()
    {
        ArtifactoryGroup group = new ArtifactoryGroup( "readers", "A group for read-only users" );
        List<ArtifactoryGroup> groups = new ArrayList<ArtifactoryGroup>();
        groups.add( group );

        Assert.assertEquals( groups, securityConfig.getGroups() );
    }

    @Test
    public void assertUser()
    {
        ArtifactoryUser admin = new ArtifactoryUser( "admin", "5f4dcc3b5aa765d61d8327deb882cf99" );
        admin.setAdmin( true );
        ArtifactoryUser anonymous = new ArtifactoryUser( "anonymous", "d41d8cd98f00b204e9800998ecf8427e" );

        List<ArtifactoryUser> users = new ArrayList<ArtifactoryUser>();
        users.add( admin );
        users.add( anonymous );

        Assert.assertEquals( users, securityConfig.getUsers() );
    }

    @Test
    public void assertPermissionTarget()
    {
        ArtifactoryPermissionTarget target1 = new ArtifactoryPermissionTarget( "some repo" );
        target1.addRepoKey( "libs-releases-local" );
        target1.addRepoKey( "codehaus-cache" );
        target1.addInclude( ".*" );
        target1.addInclude( "com/acme/.*" );
        ArtifactoryPermissionTarget target2 = new ArtifactoryPermissionTarget( "Any Local" );
        target2.addRepoKey( "ANY LOCAL" );
        target2.addInclude( ".*" );
        ArtifactoryPermissionTarget target3 = new ArtifactoryPermissionTarget( "Any Remote" );
        target3.addRepoKey( "ANY REMOTE" );
        target3.addInclude( ".*" );
        target3.addInclude( ".*/[^/]*-sources\\.[^/]*" );
        ArtifactoryPermissionTarget target4 = new ArtifactoryPermissionTarget( "Anything" );
        target4.addRepoKey( "ANY" );
        target4.addInclude( ".*" );

        Assert.assertEquals( target1, securityConfig.getPermissionTargets().get( 0 ) );
        Assert.assertEquals( target2, securityConfig.getPermissionTargets().get( 1 ) );
        Assert.assertEquals( target3, securityConfig.getPermissionTargets().get( 2 ) );
    }
}

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

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonatype.nexus.plugin.migration.artifactory.security.builder.ArtifactorySecurityConfigBuilder;

public class ParseSecurityConfig125Test
{

    protected static ArtifactorySecurityConfig securityConfig;

    @BeforeClass
    public static void parseSecurityConfig()
        throws Exception
    {
        // Note that the whole test case is based on this configuration file
        securityConfig = ArtifactorySecurityConfigBuilder.read( ParseSecurityConfig125Test.class
            .getResourceAsStream( "/security-config-1.2.5.xml" ) );
    }

    @Test
    public void assertUser()
    {
        ArtifactoryUser admin = new ArtifactoryUser( "admin", "5f4dcc3b5aa765d61d8327deb882cf99" );
        admin.setAdmin( true );
        ArtifactoryUser admin1 = new ArtifactoryUser( "admin1", "5f4dcc3b5aa765d61d8327deb882cf99" );
        admin1.setAdmin( true );
        ArtifactoryUser user = new ArtifactoryUser( "user", "5f4dcc3b5aa765d61d8327deb882cf99" );
        ArtifactoryUser user1 = new ArtifactoryUser( "user1", "5f4dcc3b5aa765d61d8327deb882cf99" );

        List<ArtifactoryUser> users = new ArrayList<ArtifactoryUser>();

        users.add( admin );
        users.add( admin1 );
        users.add( user );
        users.add( user1 );

        Assert.assertEquals( users, securityConfig.getUsers() );
    }

    @Test
    public void assertPermissionTarget()
    {
        ArtifactoryPermissionTarget target1 = new ArtifactoryPermissionTarget();
        target1.addRepoKey( "ANY" );
        target1.addInclude( ".*" );
        ArtifactoryPermissionTarget target2 = new ArtifactoryPermissionTarget();
        target2.addRepoKey( "libs-releases" );
        target2.addInclude( "org/apache/.*" );
        ArtifactoryPermissionTarget target3 = new ArtifactoryPermissionTarget();
        target3.addRepoKey( "java.net-cache" );
        target3.addInclude( ".*" );

        assertPermissionTargetContent( target1, securityConfig.getPermissionTargets().get( 0 ) );
        assertPermissionTargetContent( target2, securityConfig.getPermissionTargets().get( 1 ) );
        assertPermissionTargetContent( target3, securityConfig.getPermissionTargets().get( 2 ) );
    }

    @Test
    public void assertAcl()
    {
        ArtifactoryUser user = new ArtifactoryUser( "user", "5f4dcc3b5aa765d61d8327deb882cf99" );
        ArtifactoryUser user1 = new ArtifactoryUser( "user1", "5f4dcc3b5aa765d61d8327deb882cf99" );

        ArtifactoryPermissionTarget target2 = securityConfig.getPermissionTargets().get( 1 );
        ArtifactoryPermissionTarget target3 = securityConfig.getPermissionTargets().get( 2 );

        ArtifactoryAcl acl1 = new ArtifactoryAcl( target2, user1 );
        acl1.addPermission( ArtifactoryPermission.DEPLOYER );
        acl1.addPermission( ArtifactoryPermission.READER );

        ArtifactoryAcl acl2 = new ArtifactoryAcl( target3, user );
        acl2.addPermission( ArtifactoryPermission.ADMIN );
        acl2.addPermission( ArtifactoryPermission.DEPLOYER );
        acl2.addPermission( ArtifactoryPermission.READER );

        List<ArtifactoryAcl> acls = new ArrayList<ArtifactoryAcl>();
        acls.add( acl1 );
        acls.add( acl2 );

        Assert.assertEquals( acls, securityConfig.getAcls() );
    }

    private void assertPermissionTargetContent( ArtifactoryPermissionTarget expected, ArtifactoryPermissionTarget actual )
    {
        Assert.assertEquals( expected.getRepoKeys(), actual.getRepoKeys() );
        Assert.assertEquals( expected.getIncludes(), actual.getIncludes() );
        Assert.assertEquals( expected.getExcludes(), actual.getExcludes() );
    }
}

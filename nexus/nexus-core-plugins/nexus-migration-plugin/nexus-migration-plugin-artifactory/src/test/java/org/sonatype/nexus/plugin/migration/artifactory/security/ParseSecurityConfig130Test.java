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

public class ParseSecurityConfig130Test
{
    protected ArtifactorySecurityConfig securityConfig;

    @Before
    public void parseSecurityConfig()
        throws Exception
    {
        // Note that the whole test case is based on this configuration file
        securityConfig = ArtifactorySecurityConfigBuilder.read( getClass().getResourceAsStream(
            "/security-config-1.3.0.xml" ) );
    }

    @Test
    public void assertGroup()
    {
        ArtifactoryGroup group = new ArtifactoryGroup( "group", "A test user group" );
        List<ArtifactoryGroup> groups = new ArrayList<ArtifactoryGroup>();
        groups.add( group );

        Assert.assertEquals( groups, securityConfig.getGroups() );
    }

    @Test
    public void assertUser()
    {
        ArtifactoryUser anonymous = new ArtifactoryUser( "anonymous", "d41d8cd98f00b204e9800998ecf8427e" );
        ArtifactoryUser admin = new ArtifactoryUser( "admin", "5f4dcc3b5aa765d61d8327deb882cf99" );
        admin.setAdmin( true );
        ArtifactoryUser user = new ArtifactoryUser( "user", "5f4dcc3b5aa765d61d8327deb882cf99", "user@artifactory.org" );
        user.getGroups().add( securityConfig.getGroups().get( 0 ) );
        ArtifactoryUser user1 = new ArtifactoryUser(
            "user1",
            "5f4dcc3b5aa765d61d8327deb882cf99",
            "user1@artifactory.org" );

        List<ArtifactoryUser> users = new ArrayList<ArtifactoryUser>();

        users.add( anonymous );
        users.add( admin );
        users.add( user );
        users.add( user1 );

        Assert.assertEquals( users, securityConfig.getUsers() );
    }

    @Test
    public void assertPermissionTarget()
    {
        ArtifactoryPermissionTarget target1 = new ArtifactoryPermissionTarget( "Anything", "ANY" );
        target1.addInclude( ".*" );
        ArtifactoryPermissionTarget target2 = new ArtifactoryPermissionTarget( "permTarget", "repo1-cache" );
        target2.addInclude( ".*" );
        target2.addInclude( ".*/[^/]*-sources\\.[^/]*" );

        ArtifactoryPermissionTarget target3 = new ArtifactoryPermissionTarget( "permTarget1", "repo1-cache" );
        target3.addInclude( ".*" );
        target3.addInclude( "com/acme/.*" );
        target3.addInclude( ".*/[^/]*-sources\\.[^/]*" );

        Assert.assertEquals( target1, securityConfig.getPermissionTargets().get( 0 ) );
        Assert.assertEquals( target2, securityConfig.getPermissionTargets().get( 1 ) );
        Assert.assertEquals( target3, securityConfig.getPermissionTargets().get( 2 ) );
    }

    @Test
    public void assertAcl()
    {
        ArtifactoryPermissionTarget tAny = securityConfig.getPermissionTargets().get( 0 );
        ArtifactoryUser anonymous = securityConfig.getUsers().get( 0 );
        ArtifactoryAcl acl1 = new ArtifactoryAcl( tAny, anonymous );
        acl1.addPermission( ArtifactoryPermission.READER );

        ArtifactoryPermissionTarget permTarget = securityConfig.getPermissionTargets().get( 1 );
        ArtifactoryGroup group = securityConfig.getGroups().get( 0 );
        ArtifactoryAcl acl2 = new ArtifactoryAcl( permTarget, group );
        acl2.addPermission( ArtifactoryPermission.READER );
        acl2.addPermission( ArtifactoryPermission.ADMIN );
        acl2.addPermission( ArtifactoryPermission.DEPLOYER );
        acl2.addPermission( ArtifactoryPermission.DELETE );

        ArtifactoryPermissionTarget permTarget1 = securityConfig.getPermissionTargets().get( 2 );
        ArtifactoryUser user1 = securityConfig.getUsers().get( 3 );
        ArtifactoryAcl acl3 = new ArtifactoryAcl( permTarget1, user1 );
        acl3.addPermission( ArtifactoryPermission.DELETE );

        List<ArtifactoryAcl> acls = new ArrayList<ArtifactoryAcl>();
        acls.add( acl1 );
        acls.add( acl2 );
        acls.add( acl3 );

        Assert.assertEquals( acls, securityConfig.getAcls() );
    }
}

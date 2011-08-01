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

import junit.framework.Assert;

import org.junit.Test;

public class ArtifactoryPermissionTest
{
    @Test
    public void buildPermission125()
    {
        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 7 ).contains( ArtifactoryPermission.ADMIN ) );
        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 7 ).contains( ArtifactoryPermission.DEPLOYER ) );
        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 7 ).contains( ArtifactoryPermission.READER ) );

        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 6 ).contains( ArtifactoryPermission.ADMIN ) );
        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 6 ).contains( ArtifactoryPermission.DEPLOYER ) );
        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 6 ).contains( ArtifactoryPermission.READER ) );

        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 5 ).contains( ArtifactoryPermission.ADMIN ) );
        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 5 ).contains( ArtifactoryPermission.DEPLOYER ) );
        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 5 ).contains( ArtifactoryPermission.READER ) );

        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 4 ).contains( ArtifactoryPermission.ADMIN ) );
        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 4 ).contains( ArtifactoryPermission.DEPLOYER ) );
        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 4 ).contains( ArtifactoryPermission.READER ) );

        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 3 ).contains( ArtifactoryPermission.ADMIN ) );
        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 3 ).contains( ArtifactoryPermission.DEPLOYER ) );
        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 3 ).contains( ArtifactoryPermission.READER ) );

        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 2 ).contains( ArtifactoryPermission.ADMIN ) );
        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 2 ).contains( ArtifactoryPermission.DEPLOYER ) );
        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 2 ).contains( ArtifactoryPermission.READER ) );

        Assert.assertTrue( ArtifactoryPermission.buildPermission125( 1 ).contains( ArtifactoryPermission.ADMIN ) );
        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 1 ).contains( ArtifactoryPermission.DEPLOYER ) );
        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 1 ).contains( ArtifactoryPermission.READER ) );

        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 0 ).contains( ArtifactoryPermission.ADMIN ) );
        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 0 ).contains( ArtifactoryPermission.DEPLOYER ) );
        Assert.assertFalse( ArtifactoryPermission.buildPermission125( 0 ).contains( ArtifactoryPermission.READER ) );
    }
}

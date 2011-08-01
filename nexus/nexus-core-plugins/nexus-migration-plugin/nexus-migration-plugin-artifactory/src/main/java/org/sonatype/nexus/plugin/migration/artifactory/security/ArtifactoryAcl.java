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

import java.util.HashSet;
import java.util.Set;

public class ArtifactoryAcl
{
    private ArtifactoryPermissionTarget permissionTarget;

    private ArtifactoryUser user;

    private ArtifactoryGroup group;

    private Set<ArtifactoryPermission> permissions = new HashSet<ArtifactoryPermission>();

    public ArtifactoryAcl( ArtifactoryPermissionTarget permissionTarget, ArtifactoryUser user )
    {
        this.permissionTarget = permissionTarget;

        this.user = user;
    }

    public ArtifactoryAcl( ArtifactoryPermissionTarget permissionTarget, ArtifactoryGroup group )
    {
        this.permissionTarget = permissionTarget;

        this.group = group;
    }

    public ArtifactoryPermissionTarget getPermissionTarget()
    {
        return permissionTarget;
    }

    public ArtifactoryUser getUser()
    {
        return user;
    }

    public ArtifactoryGroup getGroup()
    {
        return group;
    }

    public Set<ArtifactoryPermission> getPermissions()
    {
        return permissions;
    }

    public void addPermission( ArtifactoryPermission permission )
    {
        permissions.add( permission );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( !( obj instanceof ArtifactoryAcl ) )
        {
            return false;
        }

        ArtifactoryAcl acl = (ArtifactoryAcl) obj;

        return

        ( ( user == null && acl.user == null ) || ( user != null && acl.user != null && user.equals( acl.user ) ) )
            && ( group == null && acl.group == null || ( group != null && acl.group != null && group.equals( acl.group ) ) )
            && this.permissionTarget.equals( acl.permissionTarget ) && this.permissions.equals( acl.permissions );

    }

}

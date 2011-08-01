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

public class ArtifactorySecurityConfig
{
    private List<ArtifactoryUser> users = new ArrayList<ArtifactoryUser>();

    private List<ArtifactoryGroup> groups = new ArrayList<ArtifactoryGroup>();

    private List<ArtifactoryPermissionTarget> permissionTargets = new ArrayList<ArtifactoryPermissionTarget>();

    private List<ArtifactoryAcl> acls = new ArrayList<ArtifactoryAcl>();

    public List<ArtifactoryUser> getUsers()
    {
        return users;
    }

    public List<ArtifactoryGroup> getGroups()
    {
        return groups;
    }

    public List<ArtifactoryPermissionTarget> getPermissionTargets()
    {
        return permissionTargets;
    }

    public List<ArtifactoryAcl> getAcls()
    {
        return acls;
    }

    public void addUser( ArtifactoryUser user )
    {
        users.add( user );
    }

    public void addGroup( ArtifactoryGroup group )
    {
        groups.add( group );
    }

    public void addPermissionTarget( ArtifactoryPermissionTarget repoPath )
    {
        permissionTargets.add( repoPath );
    }

    public void addAcl( ArtifactoryAcl acl )
    {
        acls.add( acl );
    }

    public ArtifactoryUser getUserByUsername( String username )
    {
        for ( ArtifactoryUser user : users )
        {
            if ( user.getUsername().equals( username ) )
            {
                return user;
            }
        }

        if ( username.endsWith( "-artifactory" ) )
        {
            username = username.replace( "-artifactory", "" );
            for ( ArtifactoryUser user : users )
            {
                if ( user.getUsername().equals( username ) )
                {
                    return user;
                }
            }
        }

        return null;
    }

    public ArtifactoryGroup getGroupByName( String name )
    {
        for ( ArtifactoryGroup group : groups )
        {
            if ( group.getName().equals( name ) )
            {
                return group;
            }
        }
        return null;
    }

    // this works for 1.2.5, there only one include path exists
    public ArtifactoryPermissionTarget getArtifactoryRepoTarget( String repoKey, String path )
    {
        for ( ArtifactoryPermissionTarget target : permissionTargets )
        {
            if ( target.getRepoKeys().size() == 1 && target.getRepoKeys().contains( repoKey )
                && target.getIncludes().size() == 1 && target.getExcludes().isEmpty()
                && target.getIncludes().get( 0 ).equals( path ) )
            {
                return target;
            }
        }
        return null;
    }

    public ArtifactoryPermissionTarget getPermissionTarget( String name )
    {
        for ( ArtifactoryPermissionTarget target : permissionTargets )
        {
            if ( target.getId().equals( name ) )
            {
                return target;
            }
        }
        return null;
    }

}

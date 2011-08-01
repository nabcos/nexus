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
package org.sonatype.nexus.plugin.migration.artifactory.dto;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( "migrationSummaryDTO" )
public class MigrationSummaryDTO
{
    private String id;

    private String backupLocation;

    private List<UserResolutionDTO> usersResolution;

    private boolean resolvePermission;

    private List<RepositoryResolutionDTO> repositoriesResolution;

    private List<GroupResolutionDTO> groupsResolution;

    private String nexusContext;

    public MigrationSummaryDTO()
    {
        super();
    }

    public List<RepositoryResolutionDTO> getRepositoriesResolution()
    {
        if ( repositoriesResolution == null )
        {
            repositoriesResolution = new ArrayList<RepositoryResolutionDTO>();
        }
        return repositoriesResolution;
    }

    public void setRepositoriesResolution( List<RepositoryResolutionDTO> repositoriesResolution )
    {
        this.repositoriesResolution = repositoriesResolution;
    }

    public String getBackupLocation()
    {
        return backupLocation;
    }

    public void setBackupLocation( String backupLocation )
    {
        this.backupLocation = backupLocation;
    }

    public List<UserResolutionDTO> getUsersResolution()
    {
        if ( usersResolution == null )
        {
            usersResolution = new ArrayList<UserResolutionDTO>();
        }
        return usersResolution;
    }

    public void setUsersResolution( List<UserResolutionDTO> userResolution )
    {
        this.usersResolution = userResolution;
    }

    public boolean isResolvePermission()
    {
        return resolvePermission;
    }

    public void setResolvePermission( boolean resolvePermission )
    {
        this.resolvePermission = resolvePermission;
    }

    public void setGroupsResolution( List<GroupResolutionDTO> groupsResolution )
    {
        this.groupsResolution = groupsResolution;
    }

    public List<GroupResolutionDTO> getGroupsResolution()
    {
        if ( groupsResolution == null )
        {
            groupsResolution = new ArrayList<GroupResolutionDTO>();
        }
        return groupsResolution;
    }

    public RepositoryResolutionDTO getRepositoryResolution( String repoId )
    {
        if ( repoId == null )
        {
            return null;
        }

        for ( RepositoryResolutionDTO resolution : getRepositoriesResolution() )
        {
            if ( repoId.equals( resolution.getRepositoryId() ) )
            {
                return resolution;
            }
        }
        return null;
    }

    public GroupResolutionDTO getGroupResolution( String groupId )
    {
        if ( groupId == null )
        {
            return null;
        }

        for ( GroupResolutionDTO resolution : getGroupsResolution() )
        {
            if ( groupId.equals( resolution.getGroupId() ) )
            {
                return resolution;
            }
        }
        return null;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getNexusContext()
    {
        return nexusContext;
    }

    public void setNexusContext( String nexusContext )
    {
        this.nexusContext = nexusContext;
    }

}

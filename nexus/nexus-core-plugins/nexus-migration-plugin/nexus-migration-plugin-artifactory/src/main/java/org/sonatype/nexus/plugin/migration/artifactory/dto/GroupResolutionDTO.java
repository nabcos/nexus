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

import org.codehaus.plexus.util.StringUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( "groupResolution" )
public class GroupResolutionDTO
{
    private String groupId;

    private boolean isMixed = false;

    private String repositoryTypeResolution = ERepositoryTypeResolution.MAVEN_2_ONLY.name();

    public GroupResolutionDTO()
    {
        super();
    }

    public GroupResolutionDTO( String groupId, boolean isMixed )
    {
        super();
        this.groupId = groupId;
        this.isMixed = isMixed;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public ERepositoryTypeResolution getRepositoryTypeResolution()
    {
        if ( StringUtils.isEmpty( repositoryTypeResolution ) )
        {
            return null;
        }
        return ERepositoryTypeResolution.valueOf( repositoryTypeResolution );
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public void setRepositoryTypeResolution( ERepositoryTypeResolution repositoryTypeResolution )
    {
        this.repositoryTypeResolution = repositoryTypeResolution.name();
    }

    public boolean isMixed()
    {
        return isMixed;
    }

    public void setMixed( boolean isMixed )
    {
        this.isMixed = isMixed;
    }

}

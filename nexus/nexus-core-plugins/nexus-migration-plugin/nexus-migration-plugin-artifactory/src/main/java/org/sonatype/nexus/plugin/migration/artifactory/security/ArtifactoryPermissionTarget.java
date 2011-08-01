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

public class ArtifactoryPermissionTarget
{
    private static int defaultIdCount = 1001;

    private String id;

    private List<String> repoKeys = new ArrayList<String>();

    private List<String> includes = new ArrayList<String>();

    private List<String> excludes = new ArrayList<String>();

    public ArtifactoryPermissionTarget()
    {
        this.id = "arti-perm-target-" + defaultIdCount;

        defaultIdCount++;
    }

    public ArtifactoryPermissionTarget( String id )
    {
        this.id = id;
    }

    public ArtifactoryPermissionTarget( String id, String repoKey )
    {
        this.id = id;

        this.repoKeys.add( repoKey );
    }

    public List<String> getRepoKeys()
    {
        return repoKeys;
    }

    public void setRepoKeys( List<String> repoKeys )
    {
        this.repoKeys = repoKeys;
    }

    public void addRepoKey( String repoKey )
    {
        if ( !this.repoKeys.contains( repoKey ) )
        {
            repoKeys.add( repoKey );
        }
    }

    public String getId()
    {
        return id;
    }

    public List<String> getIncludes()
    {
        return includes;
    }

    public List<String> getExcludes()
    {
        return excludes;
    }

    public void addInclude( String include )
    {
        includes.add( include );
    }

    public void addExclude( String exclude )
    {
        excludes.add( exclude );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( !( obj instanceof ArtifactoryPermissionTarget ) )
        {
            return false;
        }

        ArtifactoryPermissionTarget repoTarget = (ArtifactoryPermissionTarget) obj;

        return id.equals( repoTarget.id ) && repoKeys.equals( repoTarget.repoKeys )
            && includes.equals( repoTarget.includes ) && excludes.equals( repoTarget.excludes );
    }

}

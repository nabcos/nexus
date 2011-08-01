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
package org.sonatype.nexus.plugin.migration.artifactory.persist.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( "mapping" )
public class CMapping
{

    private String artifactoryRepositoryId;

    private String nexusGroupId;

    private String nexusRepositoryId;

    private String releasesRepositoryId;

    private String snapshotsRepositoryId;

    public CMapping()
    {
        super();
    }

    public CMapping( String artifactoryRepositoryId, String nexusRepositoryId )
    {
        this();
        this.artifactoryRepositoryId = artifactoryRepositoryId;
        this.nexusRepositoryId = nexusRepositoryId;
    }

    public CMapping( String artifactoryRepositoryId, String nexusGroupId, String releasesRepositoryId,
                     String snapshotsRepositoryId )
    {
        this();
        this.artifactoryRepositoryId = artifactoryRepositoryId;
        this.nexusGroupId = nexusGroupId;
        this.releasesRepositoryId = releasesRepositoryId;
        this.snapshotsRepositoryId = snapshotsRepositoryId;
    }

    public String getArtifactoryRepositoryId()
    {
        return artifactoryRepositoryId;
    }

    public String getNexusGroupId()
    {
        return nexusGroupId;
    }

    public String getNexusRepositoryId()
    {
        return nexusRepositoryId;
    }

    public void setArtifactoryRepositoryId( String artifactoryRepositoryId )
    {
        this.artifactoryRepositoryId = artifactoryRepositoryId;
    }

    public void setNexusGroupId( String nexusGroupId )
    {
        this.nexusGroupId = nexusGroupId;
    }

    public void setNexusRepositoryId( String nexusRepositoryId )
    {
        this.nexusRepositoryId = nexusRepositoryId;
    }

    public String getReleasesRepositoryId()
    {
        return releasesRepositoryId;
    }

    public void setReleasesRepositoryId( String releasesRepositoryId )
    {
        this.releasesRepositoryId = releasesRepositoryId;
    }

    public String getSnapshotsRepositoryId()
    {
        return snapshotsRepositoryId;
    }

    public void setSnapshotsRepositoryId( String snapshotsRepositoryId )
    {
        this.snapshotsRepositoryId = snapshotsRepositoryId;
    }

}

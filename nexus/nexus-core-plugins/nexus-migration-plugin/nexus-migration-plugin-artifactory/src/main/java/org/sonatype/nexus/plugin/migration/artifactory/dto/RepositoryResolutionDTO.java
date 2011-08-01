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

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( "repositoryResolution" )
public class RepositoryResolutionDTO
{
    private boolean alreadyExists;

    private boolean copyCachedArtifacts = true;

    private boolean isImport = true;

    private boolean isMixed = false;

    private boolean mapUrls = true;

    private boolean mergeSimilarRepository = false;

    private String mixResolution = EMixResolution.BOTH.name();

    private String repositoryId;

    private String similarRepositoryId;

    private String type;

    public RepositoryResolutionDTO()
    {
        super();
    }

    public EMixResolution getMixResolution()
    {
        return EMixResolution.valueOf( mixResolution );
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public String getSimilarRepositoryId()
    {
        return similarRepositoryId;
    }

    public ERepositoryType getType()
    {
        return ERepositoryType.valueOf( this.type );
    }

    public boolean isAlreadyExists()
    {
        return alreadyExists;
    }

    public boolean isCopyCachedArtifacts()
    {
        return copyCachedArtifacts;
    }

    public boolean isImport()
    {
        return isImport;
    }

    public boolean isMapUrls()
    {
        return mapUrls;
    }

    public boolean isMergeSimilarRepository()
    {
        return mergeSimilarRepository;
    }

    public boolean isMixed()
    {
        return isMixed;
    }

    public void setAlreadyExists( boolean alreadyExists )
    {
        this.alreadyExists = alreadyExists;
    }

    public void setCopyCachedArtifacts( boolean copyCachedArtifacts )
    {
        this.copyCachedArtifacts = copyCachedArtifacts;
    }

    public void setImport( boolean isImport )
    {
        this.isImport = isImport;
    }

    public void setMapUrls( boolean mapUrls )
    {
        this.mapUrls = mapUrls;
    }

    public void setMergeSimilarRepository( boolean mergeSimilarRepository )
    {
        this.mergeSimilarRepository = mergeSimilarRepository;
    }

    public void setMixed( boolean isMixed )
    {
        this.isMixed = isMixed;
    }

    public void setMixResolution( EMixResolution mixResolution )
    {
        this.mixResolution = mixResolution.name();
    }

    public void setRepositoryId( String repositoryId )
    {
        this.repositoryId = repositoryId;
    }

    public void setSimilarRepositoryId( String similarRepositoryId )
    {
        this.similarRepositoryId = similarRepositoryId;
    }

    public void setType( ERepositoryType type )
    {
        this.type = type.name();
    }

}

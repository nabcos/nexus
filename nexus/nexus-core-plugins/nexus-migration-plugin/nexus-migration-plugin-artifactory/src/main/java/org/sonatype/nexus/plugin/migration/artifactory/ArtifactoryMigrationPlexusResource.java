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
package org.sonatype.nexus.plugin.migration.artifactory;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;
import org.sonatype.nexus.plugin.migration.artifactory.dto.MigrationSummaryDTO;
import org.sonatype.nexus.plugin.migration.artifactory.dto.MigrationSummaryRequestDTO;
import org.sonatype.nexus.plugin.migration.artifactory.task.ArtifactoryMigrationTask;
import org.sonatype.nexus.scheduling.NexusScheduler;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

@Component( role = PlexusResource.class, hint = "artifactoryMigration" )
public class ArtifactoryMigrationPlexusResource
    extends AbstractArtifactoryMigrationPlexusResource
{
    @Requirement
    private NexusScheduler nexusScheduler;

    public ArtifactoryMigrationPlexusResource()
    {
        this.setReadable( false );
        this.setModifiable( true );
    }

    @Override
    public Object getPayloadInstance()
    {
        return new MigrationSummaryRequestDTO();
    }

    @Override
    public String getResourceUri()
    {
        return "/migration/artifactory/content";
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        return new PathProtectionDescriptor( getResourceUri(), "authcBasic,perms[nexus:artifactorymigrate]" );
    }

    @Override
    public Object post( Context context, Request request, Response response, Object payload )
        throws ResourceException
    {
        MigrationSummaryDTO migrationSummary = ( (MigrationSummaryRequestDTO) payload ).getData();

        // need to resolve that on posts
        this.validateBackupFileLocation( migrationSummary.getBackupLocation() );

        String nexusContext = getContextRoot( request ).getPath();
        migrationSummary.setNexusContext( nexusContext );

        // lookup task and run it
        ArtifactoryMigrationTask migrationTask = nexusScheduler.createTaskInstance( ArtifactoryMigrationTask.class );
        migrationTask.setMigrationSummary( migrationSummary );
        nexusScheduler.submit( "Importing Artifactory Backup.", migrationTask );

        return null;
    }

}

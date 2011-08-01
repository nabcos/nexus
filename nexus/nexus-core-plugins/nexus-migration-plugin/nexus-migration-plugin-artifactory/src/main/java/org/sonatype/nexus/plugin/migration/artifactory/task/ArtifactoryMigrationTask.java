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
package org.sonatype.nexus.plugin.migration.artifactory.task;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.nexus.plugin.migration.artifactory.ArtifactoryMigrator;
import org.sonatype.nexus.plugin.migration.artifactory.dto.MigrationSummaryDTO;
import org.sonatype.nexus.scheduling.AbstractNexusRepositoriesTask;
import org.sonatype.scheduling.SchedulerTask;

@Component( role = SchedulerTask.class, hint = ArtifactoryMigrationTaskDescriptor.ID, instantiationStrategy = "per-lookup" )
public class ArtifactoryMigrationTask
    extends AbstractNexusRepositoriesTask<Object>
{

    private static final String ACTION = "ARTIFACTORY_MIGRATION";

    @Requirement
    private ArtifactoryMigrator artifactoryMigrator;

    private MigrationSummaryDTO migrationSummary;

    @Override
    protected Object doRun()
        throws Exception
    {
        // run the migration
        /*MigrationResult result =*/ this.artifactoryMigrator.migrate( this.migrationSummary );

//        this.getTaskActivityDescriptor().
        return null;
    }

    @Override
    protected String getAction()
    {
        return ACTION;
    }

    @Override
    protected String getMessage()
    {
        return "Importing Artifactory Backup.";
    }

    public MigrationSummaryDTO getMigrationSummary()
    {
        return migrationSummary;
    }

    public void setMigrationSummary( MigrationSummaryDTO migrationSummary )
    {
        this.migrationSummary = migrationSummary;
    }

}

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
package org.sonatype.nexus.plugin.migration.artifactory.util;

import org.sonatype.nexus.plugin.migration.artifactory.dto.FileLocationRequestDTO;
import org.sonatype.nexus.plugin.migration.artifactory.dto.FileLocationResource;
import org.sonatype.nexus.plugin.migration.artifactory.dto.GroupResolutionDTO;
import org.sonatype.nexus.plugin.migration.artifactory.dto.MigrationSummaryDTO;
import org.sonatype.nexus.plugin.migration.artifactory.dto.MigrationSummaryRequestDTO;
import org.sonatype.nexus.plugin.migration.artifactory.dto.MigrationSummaryResponseDTO;
import org.sonatype.nexus.plugin.migration.artifactory.dto.RepositoryResolutionDTO;
import org.sonatype.nexus.plugin.migration.artifactory.dto.UserResolutionDTO;
import org.sonatype.plexus.rest.xstream.AliasingListConverter;

import com.thoughtworks.xstream.XStream;

public class XStreamUtil
{

    public static void configureMigration( XStream xstream )
    {
        xstream.processAnnotations( MigrationSummaryDTO.class );
        xstream.processAnnotations( MigrationSummaryResponseDTO.class );
        xstream.processAnnotations( MigrationSummaryRequestDTO.class );
        xstream.processAnnotations( RepositoryResolutionDTO.class );
        xstream.processAnnotations( GroupResolutionDTO.class );
        xstream.processAnnotations( UserResolutionDTO.class );
        xstream.processAnnotations( FileLocationRequestDTO.class );
        xstream.processAnnotations( FileLocationResource.class );

        xstream.registerLocalConverter( MigrationSummaryDTO.class, "usersResolution",
                                        new AliasingListConverter( UserResolutionDTO.class, "userResolution" ) );

        xstream.registerLocalConverter( MigrationSummaryDTO.class, "repositoriesResolution",
                                        new AliasingListConverter( RepositoryResolutionDTO.class,
                                                                   "repositoryResolution" ) );

        xstream.registerLocalConverter( MigrationSummaryDTO.class, "groupsResolution",
                                        new AliasingListConverter( GroupResolutionDTO.class, "groupResolution" ) );
    }

}

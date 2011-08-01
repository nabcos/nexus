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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sonatype.nexus.plugin.migration.artifactory.config.ArtifactoryVirtualRepository;

public class VirtualRepositoryUtil
{

    public static void resolveRepositories( Map<String, ArtifactoryVirtualRepository> virtualRepositories )
    {
        for ( String repoId : virtualRepositories.keySet() )
        {
            ArtifactoryVirtualRepository repo = virtualRepositories.get( repoId );
            List<String> resolvedChilds = getRepositories( repo, virtualRepositories );
            repo.setResolvedRepositories( resolvedChilds );
        }

    }

    private static List<String> getRepositories( ArtifactoryVirtualRepository repo,
                                                 Map<String, ArtifactoryVirtualRepository> virtualRepositories )
    {
        List<String> resolvedRepos = new ArrayList<String>();
        List<String> repositories = repo.getRepositories();
        for ( String childId : repositories )
        {
            ArtifactoryVirtualRepository virtualRepo = virtualRepositories.get( childId );
            if ( virtualRepo != null )
            {
                resolvedRepos.addAll( getRepositories( virtualRepo, virtualRepositories ) );
            }
            else
            {
                resolvedRepos.add( childId );
            }
        }

        return resolvedRepos;
    }

}

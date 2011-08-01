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
package org.sonatype.nexus.plugin.migration.artifactory.config;

import static org.sonatype.nexus.plugin.migration.artifactory.util.DomUtil.getValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ArtifactoryVirtualRepository
{
    private Xpp3Dom dom;

    private List<String> resolvedRepositories;

    private List<String> repositories;

    public ArtifactoryVirtualRepository( Xpp3Dom dom )
    {
        this.dom = dom;
    }

    public String getKey()
    {
        return getValue( dom, "key" );
    }

    public List<String> getRepositories()
    {
        if ( repositories == null )
        {
            Xpp3Dom repositoriesDom = dom.getChild( "repositories" );
            if ( repositoriesDom == null )
            {
                repositories = Collections.emptyList();
            }
            else
            {
                repositories = new ArrayList<String>();
                for ( Xpp3Dom repoDom : repositoriesDom.getChildren( "repositoryRef" ) )
                {
                    repositories.add( repoDom.getValue() );
                }
                repositories = Collections.unmodifiableList( repositories );
            }
        }
        return repositories;
    }

    public List<String> getResolvedRepositories()
    {
        return resolvedRepositories;
    }

    public void setResolvedRepositories( List<String> resolvedRepositories )
    {
        this.resolvedRepositories = resolvedRepositories;
    }

}

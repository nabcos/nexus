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

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ArtifactoryProxy
{
    private Xpp3Dom dom;

    public ArtifactoryProxy( Xpp3Dom dom )
    {
        this.dom = dom;
    }

    public String getKey()
    {
        return getValue( dom, "key" );
    }

    public String getHost()
    {
        return getValue( dom, "host" );
    }

    public int getPort()
    {
        return Integer.parseInt( getValue( dom, "port" ) );
    }

    public String getUsername()
    {
        return getValue( dom, "username" );
    }

    public String getPassword()
    {
        return getValue( dom, "password" );
    }

    public String getDomain()
    {
        return getValue( dom, "domain" );
    }

}

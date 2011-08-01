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
package org.sonatype.nexus.plugin.migration.artifactory.security.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.nexus.plugin.migration.artifactory.security.ArtifactorySecurityConfig;

public class ArtifactorySecurityConfigBuilder
{
    public static final String VERSION_125 = "1.2.5";

    public static final String VERSION_130 = "1.3.0";

    public static final String VERSION_UNKNOWN = "unknown";

    public static ArtifactorySecurityConfig read( File file )
        throws IOException,
            XmlPullParserException
    {
        XmlStreamReader reader = ReaderFactory.newXmlReader( file );

        try
        {
            return build( Xpp3DomBuilder.build( reader ) );
        }

        finally
        {
            IOUtil.close( reader );
        }
    }

    public static ArtifactorySecurityConfig read( InputStream inputStream )
        throws IOException,
            XmlPullParserException
    {
        XmlStreamReader reader = ReaderFactory.newXmlReader( inputStream );

        try
        {
            return build( Xpp3DomBuilder.build( reader ) );
        }
        finally
        {
            IOUtil.close( reader );
        }
    }

    /**
     * @param dom
     * @return version of the security config dom
     */
    public static String validate( Xpp3Dom dom )
    {
        if ( dom.getChild( "users" ) == null )
        {
            return VERSION_UNKNOWN;
        }

        if ( dom.getChild( "users" ).getChildren( "org.artifactory.security.SimpleUser" ).length > 0 )
        {
            return VERSION_125;
        }

        if ( dom.getChild( "users" ).getChildren( "user" ).length > 0 )
        {
            return VERSION_130;
        }

        return VERSION_UNKNOWN;
    }

    public static ArtifactorySecurityConfig build( Xpp3Dom dom )
    {
        ArtifactorySecurityConfig securityConfig = new ArtifactorySecurityConfig();

        if ( validate( dom ).equals( VERSION_125 ) )
        {
            new SecurityConfig125Parser( dom, securityConfig ).parse();
        }
        else if ( validate( dom ).equals( VERSION_130 ) )
        {
            new SecurityConfig130Parser( dom, securityConfig ).parse();
        }

        return securityConfig;

    }

}

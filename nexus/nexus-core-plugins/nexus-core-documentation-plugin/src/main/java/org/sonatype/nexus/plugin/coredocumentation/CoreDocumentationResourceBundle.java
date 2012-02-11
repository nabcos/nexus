/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugin.coredocumentation;

import java.io.IOException;
import java.util.zip.ZipFile;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.plugins.rest.AbstractDocumentationNexusResourceBundle;
import org.sonatype.nexus.plugins.rest.NexusResourceBundle;
import org.sonatype.nexus.rest.NexusApplication;

@Component( role = NexusResourceBundle.class, hint = "CoreDocumentationResourceBundle" )
public class CoreDocumentationResourceBundle
    extends AbstractDocumentationNexusResourceBundle
{

    @Override
    public String getPluginId()
    {
        return "nexus-core-documentation-plugin";
    }

    @Override
    protected ZipFile getZipFile()
        throws IOException
    {
        return getZipFile( NexusApplication.class );
    }

    @Override
    public String getDescription()
    {
        return "Core API";
    }

    @Override
    protected String getUrlSnippet()
    {
        return "core";
    }
}

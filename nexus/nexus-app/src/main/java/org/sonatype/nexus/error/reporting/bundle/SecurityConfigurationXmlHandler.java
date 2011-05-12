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
package org.sonatype.nexus.error.reporting.bundle;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.swizzle.IssueSubmissionException;
import org.codehaus.plexus.swizzle.IssueSubmissionRequest;
import org.codehaus.plexus.util.IOUtil;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.security.configuration.model.SecurityConfiguration;
import org.sonatype.security.configuration.model.io.xpp3.SecurityConfigurationXpp3Writer;
import org.sonatype.security.configuration.source.SecurityConfigurationSource;
import org.sonatype.sisu.pr.bundle.Bundle;
import org.sonatype.sisu.pr.bundle.BundleAssembler;
import org.sonatype.sisu.pr.bundle.ManagedBundle;
import org.sonatype.sisu.pr.bundle.StorageManager;

@Component(role = BundleAssembler.class, hint = "security-configuration.xml")
public class SecurityConfigurationXmlHandler
    extends AbstractXmlHandler
    implements BundleAssembler
{
    @Requirement
    SecurityConfigurationSource source;
    
    @Requirement
    NexusConfiguration nexusConfig;
    
    @Requirement
    StorageManager storageManager;
    
    @Override
    public boolean isParticipating( IssueSubmissionRequest request )
    {
        return source.getConfiguration() != null;
    }

    @Override
    public Bundle assemble( IssueSubmissionRequest request )
        throws IssueSubmissionException
    {
        SecurityConfiguration configuration = ( SecurityConfiguration )cloneViaXml( source.getConfiguration() );
        
        configuration.setAnonymousPassword( PASSWORD_MASK );
        
        SecurityConfigurationXpp3Writer xppWriter = new SecurityConfigurationXpp3Writer();
        
        Writer writer = null;
        
        try
        {
            ManagedBundle bundle = storageManager.createBundle( "security-configuration.xml", "application/xml" );
            OutputStream out = bundle.getOutputStream();
            writer = new OutputStreamWriter( out );
            xppWriter.write( writer, configuration );
            writer.close();
            return bundle;
        }
        catch (IOException e)
        {
            throw new IssueSubmissionException( "Could not assemble security-configuration.xml-bundle", e );
        }
        finally
        {
            IOUtil.close(writer);
        }
        
    }
}

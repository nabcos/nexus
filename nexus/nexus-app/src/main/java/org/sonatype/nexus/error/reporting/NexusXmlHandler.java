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
package org.sonatype.nexus.error.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.swizzle.IssueSubmissionException;
import org.codehaus.plexus.swizzle.IssueSubmissionRequest;
import org.codehaus.plexus.util.IOUtil;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.nexus.configuration.model.Configuration;
import org.sonatype.nexus.configuration.model.ConfigurationHelper;
import org.sonatype.nexus.configuration.model.io.xpp3.NexusConfigurationXpp3Writer;
import org.sonatype.sisu.pr.bundle.Bundle;
import org.sonatype.sisu.pr.bundle.BundleAssembler;
import org.sonatype.sisu.pr.bundle.ManagedBundle;
import org.sonatype.sisu.pr.bundle.StorageManager;

@Component(role = BundleAssembler.class, hint = "nexus.xml")
public class NexusXmlHandler
    extends AbstractXmlHandler
    implements BundleAssembler
{
    @Requirement
    private ConfigurationHelper configHelper;
    
    @Requirement
    private NexusConfiguration nexusConfig;
    
    @Requirement
    private StorageManager storageManager;
    
    public File getFile( ConfigurationHelper configHelper, NexusConfiguration nexusConfig )
        throws IOException
    {
        Configuration configuration = configHelper.clone( nexusConfig.getConfigurationModel() );
        
        // No config ?
        if ( configuration == null )
        {
            return null;
        }
        
        configHelper.maskPasswords( configuration );
        
        NexusConfigurationXpp3Writer writer = new NexusConfigurationXpp3Writer();
        FileWriter fWriter = null;
        File tempFile = null;
        
        try
        {
            tempFile = new File( nexusConfig.getTemporaryDirectory(), "nexus.xml." + System.currentTimeMillis() );
            fWriter = new FileWriter( tempFile );
            writer.write( fWriter, configuration );
        }
        finally
        {
            if ( fWriter != null )
            {
                fWriter.close();
            }
        }
        
        return tempFile;
    }

    @Override
    public boolean isParticipating( IssueSubmissionRequest request )
    {
        return nexusConfig.getConfigurationModel() != null;
    }

    @Override
    public Bundle assemble( IssueSubmissionRequest request )
        throws IssueSubmissionException
    {
        OutputStream out = null;
        try
        {
            ManagedBundle bundle = storageManager.createBundle( "nexus.xml", "application/xml" );
            Configuration configuration = configHelper.clone( nexusConfig.getConfigurationModel() );
            configHelper.maskPasswords( configuration );
            NexusConfigurationXpp3Writer writer = new NexusConfigurationXpp3Writer();
            
            out = bundle.getOutputStream();
            writer.write( out, configuration );
            out.close();
            
	        return bundle;
        }
        catch ( IOException e )
        {
            IOUtil.close( out );
            throw new IssueSubmissionException( "Could not assemble nexus.xml: " + e.getMessage(), e );
        }
    }
}

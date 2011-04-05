package org.sonatype.nexus.error.reporting.bundle;

import java.io.File;
import java.io.FileFilter;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.swizzle.IssueSubmissionException;
import org.codehaus.plexus.swizzle.IssueSubmissionRequest;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.sisu.pr.bundle.Bundle;
import org.sonatype.sisu.pr.bundle.BundleAssembler;
import org.sonatype.sisu.pr.bundle.internal.FileBundle;

@Component( role = BundleAssembler.class, hint = "conf-dir" )
public class ConfigFilesBundleAssembler
    implements BundleAssembler
{

    @Requirement
    private NexusConfiguration nexusConfig;

    @Override
    public boolean isParticipating( IssueSubmissionRequest request )
    {
        return true;
    }

    @Override
    public Bundle assemble( IssueSubmissionRequest request )
        throws IssueSubmissionException
    {
        File confDir = nexusConfig.getWorkingDirectory( "conf" );

        FileFilter filter = new FileFilter()
        {
            @Override
            public boolean accept( File pathname )
            {
                return !pathname.getName().endsWith( ".bak" ) && !pathname.getName().endsWith( "nexus.xml" )
                    && !pathname.getName().endsWith( "security.xml" )
                    && !pathname.getName().endsWith( "security-configuration.xml" );
            }
        };

        FileBundle bundle = new FileBundle( confDir );
        bundle.setFilter( filter );
        return bundle;
    }

}

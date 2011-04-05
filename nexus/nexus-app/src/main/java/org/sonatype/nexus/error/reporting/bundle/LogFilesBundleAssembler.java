package org.sonatype.nexus.error.reporting.bundle;

import java.io.File;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.swizzle.IssueSubmissionException;
import org.codehaus.plexus.swizzle.IssueSubmissionRequest;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.sisu.pr.bundle.Bundle;
import org.sonatype.sisu.pr.bundle.BundleAssembler;
import org.sonatype.sisu.pr.bundle.internal.FileBundle;

@Component( role = BundleAssembler.class, hint = "logfile" )
public class LogFilesBundleAssembler
    implements BundleAssembler
{

    @Requirement
    private NexusConfiguration nexusConfig;

    @Override
    public boolean isParticipating( IssueSubmissionRequest request )
    {
        return new File(nexusConfig.getWorkingDirectory( "logs" ), "nexus.log").exists();
    }

    @Override
    public Bundle assemble( IssueSubmissionRequest request )
        throws IssueSubmissionException
    {
        return new FileBundle( new File(nexusConfig.getWorkingDirectory( "logs" ), "nexus.log") );
    }

}

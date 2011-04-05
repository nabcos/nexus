package org.sonatype.nexus.error.reporting;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.nexus.ApplicationStatusSource;
import org.sonatype.nexus.util.StringDigester;
import org.sonatype.sisu.pr.SystemEnvironmentContributor;

@Component( role = SystemEnvironmentContributor.class, hint = "nexus")
public class NexusSystemEnvironment
    implements SystemEnvironmentContributor
{
    @Requirement( role = ApplicationStatusSource.class )
    ApplicationStatusSource applicationStatus;
    
    @Override
    public String asDiagnosticsFormat()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "Nexus Version: " );
        sb.append( applicationStatus.getSystemStatus().getVersion() );
        sb.append( StringDigester.LINE_SEPERATOR );

        sb.append( "Nexus Edition: " );
        sb.append( applicationStatus.getSystemStatus().getEditionLong() );
        sb.append( StringDigester.LINE_SEPERATOR );
        
        return sb.toString();
    }

}

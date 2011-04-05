package org.sonatype.nexus.error.reporting;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.swizzle.IssueSubmissionRequest;
import org.sonatype.sisu.pr.Modifier;

@Component(role=Modifier.class)
public class TriggerTypeModifier
implements Modifier
{

    @Override
    public IssueSubmissionRequest modify( IssueSubmissionRequest request )
    {
        if ( request.getError() != null )
        {
            request.setSummary( "APR: " + request.getSummary() );
        }
        return request;
    }

    @Override
    public int getPriority()
    {
        return Priority.MODIFIER.priority();
    }
    

}

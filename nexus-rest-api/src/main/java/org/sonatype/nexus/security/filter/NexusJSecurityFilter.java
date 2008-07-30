package org.sonatype.nexus.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.nexus.Nexus;
import org.sonatype.plexus.jsecurity.web.filter.PlexusJSecurityFilter;

/**
 * This filter simply behaves according Nexus configuration.
 * 
 * @author cstamas
 */
public class NexusJSecurityFilter
    extends PlexusJSecurityFilter
{
    public static final String REQUEST_IS_AUTHZ_REJECTED = "request.is.authz.rejected";

    private Nexus nexus;

    public NexusJSecurityFilter()
    {
        this.configClassName = NexusFilterConfiguration.class.getName();
    }

    protected final Nexus getNexus()
    {
        if ( nexus == null )
        {
            PlexusContainer plexus = (PlexusContainer) getAttribute( PlexusConstants.PLEXUS_KEY );

            try
            {
                nexus = (Nexus) plexus.lookup( Nexus.class );
            }
            catch ( ComponentLookupException e )
            {
                log.error( "Cannot lookup Nexus!", e );

                throw new IllegalStateException( "Cannot lookup Nexus!", e );
            }
        }

        return nexus;
    }

    @Override
    protected boolean shouldNotFilter( ServletRequest request )
        throws ServletException
    {
        log.info( "SECURITY IS ENABLED: " + getNexus().isSecurityEnabled() );
        return !getNexus().isSecurityEnabled();
    }

    @Override
    protected void doFilterInternal( ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain origChain )
        throws ServletException,
            IOException
    {
        servletRequest.setAttribute( Nexus.class.getName(), getNexus() );

        super.doFilterInternal( servletRequest, servletResponse, origChain );
    }
}

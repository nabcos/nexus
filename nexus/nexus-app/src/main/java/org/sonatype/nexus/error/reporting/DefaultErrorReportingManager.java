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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.swizzle.IssueSubmissionException;
import org.codehaus.plexus.swizzle.IssueSubmissionRequest;
import org.codehaus.plexus.swizzle.IssueSubmissionResult;
import org.codehaus.plexus.swizzle.IssueSubmitter;
import org.codehaus.plexus.swizzle.jira.authentication.AuthenticationSource;
import org.codehaus.plexus.swizzle.jira.authentication.DefaultAuthenticationSource;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.swizzle.jira.Issue;
import org.codehaus.swizzle.jira.Project;
import org.sonatype.configuration.ConfigurationException;
import org.sonatype.inject.Parameters;
import org.sonatype.nexus.configuration.AbstractConfigurable;
import org.sonatype.nexus.configuration.Configurator;
import org.sonatype.nexus.configuration.CoreConfiguration;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.nexus.configuration.model.CErrorReporting;
import org.sonatype.nexus.configuration.model.CErrorReportingCoreConfiguration;
import org.sonatype.nexus.error.reporting.bundle.MapContentsBundle;
import org.sonatype.nexus.util.DigesterUtils;
import org.sonatype.sisu.issue.IssueRetriever;
import org.sonatype.sisu.pr.ProjectManager;
import org.sonatype.sisu.pr.bundle.Archiver;
import org.sonatype.sisu.pr.bundle.Bundle;
import org.sonatype.sisu.pr.bundle.BundleManager;

@Component( role = ErrorReportingManager.class )
public class DefaultErrorReportingManager
    extends AbstractConfigurable
    implements ErrorReportingManager
{
    @Requirement
    private Logger logger;

    @Requirement
    private NexusConfiguration nexusConfig;

    @Requirement
    private IssueSubmitter issueSubmitter;
    
    @Requirement
    private IssueRetriever issueRetriever;
    
    @Requirement
    private ProjectManager projectManager;
    
    @Requirement
    private Archiver archiver;

    @Requirement
    private BundleManager assembler;

    @Requirement
    @Parameters
    private Map<String, String> parameters;

    /* UT */ static final String ERROR_REPORT_DIR = "error-report-bundles";

    private Set<String> errorHashSet = new HashSet<String>();


    // ==

    protected Logger getLogger()
    {
        return logger;
    }

    // ==

    @SuppressWarnings( "deprecation" )
    @Override
    protected void initializeConfiguration()
        throws ConfigurationException
    {
        if ( getApplicationConfiguration().getConfigurationModel() != null )
        {
            configure( getApplicationConfiguration() );
        }

        CErrorReporting config = getCurrentConfiguration( false );
        issueSubmitter.setServerUrl( config.getJiraUrl() );
        issueRetriever.setServerUrl( config.getJiraUrl() );

        AuthenticationSource credentials =
            new DefaultAuthenticationSource( config.getJiraUsername(), config.getJiraPassword() );
        issueSubmitter.setCredentials( credentials );
        issueRetriever.setCredentials( credentials );
    }

    @Override
    protected ApplicationConfiguration getApplicationConfiguration()
    {
        return nexusConfig;
    }

    @Override
    protected Configurator getConfigurator()
    {
        return null;
    }

    @Override
    protected CErrorReporting getCurrentConfiguration( boolean forWrite )
    {
        return ( (CErrorReportingCoreConfiguration) getCurrentCoreConfiguration() ).getConfiguration( forWrite );
    }

    @Override
    protected CoreConfiguration wrapConfiguration( Object configuration )
        throws ConfigurationException
    {
        if ( configuration instanceof ApplicationConfiguration )
        {
            return new CErrorReportingCoreConfiguration( getApplicationConfiguration() );
        }
        else
        {
            throw new ConfigurationException( "The passed configuration object is of class \""
                + configuration.getClass().getName() + "\" and not the required \""
                + ApplicationConfiguration.class.getName() + "\"!" );
        }
    }

    // ==

    @Override
    public boolean isEnabled()
    {
        return getCurrentConfiguration( false ).isEnabled();
    }

    @Override
    public void setEnabled( boolean value )
    {
        getCurrentConfiguration( true ).setEnabled( value );
    }

    // ==

    /* UT */File writeArchive( Collection<Bundle> bundles )
        throws IOException, FileNotFoundException
    {
        Bundle bundle = archiver.createArchive( bundles );

        File zipFile = getZipFile( "nexus-error-bundle", "zip" );
        OutputStream output = null;
        InputStream input = null;

        try
        {
            output = new FileOutputStream( zipFile );
            input = bundle.getInputStream();
            IOUtil.copy( input, output );
        }
        finally
        {
            IOUtil.close( input );
            IOUtil.close( output );
        }

        return zipFile;
    }

    protected void renameFile( File bundle, String jiraTicket )
        throws IOException
    {
        if ( StringUtils.isNotEmpty( jiraTicket ) )
        {
            String filename = bundle.getAbsolutePath();

            String newfilename = filename.replace( "nexus-error-bundle", "nexus-error-bundle-" + jiraTicket );

            FileUtils.rename( bundle, new File( newfilename ) );
        }
    }

    @Override
    public ErrorReportResponse handleError( ErrorReportRequest request )
        throws IssueSubmissionException, IOException, GeneralSecurityException
    {
        AuthenticationSource auth = null;

        String username = getJIRAUsername();
        String password = getJIRAPassword();
        if ( username != null && password != null )
        {
            auth = new DefaultAuthenticationSource( username, password );
        }

        return handleError( request, auth );
    }

    @Override
    public ErrorReportResponse handleError( ErrorReportRequest request, String jiraUsername, String jiraPassword )
        throws IssueSubmissionException, IOException, GeneralSecurityException
    {
        getLogger().error( "Detected Error in Nexus", request.getThrowable() );
        DefaultAuthenticationSource credentials = new DefaultAuthenticationSource( jiraUsername, jiraPassword );
        
        return handleError( request, credentials );
    }

    @Override
    public ErrorReportResponse handleError( ErrorReportRequest genReq, String jiraUsername, String jiraPassword,
                                            boolean useGlobalProxy )
        throws IssueSubmissionException, IOException, GeneralSecurityException
    {
        // FIXME do something
        throw new UnsupportedOperationException();
    }

    private ErrorReportResponse handleError( ErrorReportRequest request, AuthenticationSource credentials )
        throws IOException, FileNotFoundException, IssueSubmissionException
    {
        ErrorReportResponse response = new ErrorReportResponse();
        
        IssueSubmissionRequest subRequest = prepareRequest( request, response );
        
        if ( subRequest != null && ! subRequest.isSkipSubmission() )
        {
            submitIssue( response, subRequest, credentials );
        }
        response.setSuccess( true );
        
        return response;
    }

    private IssueSubmissionRequest prepareRequest( ErrorReportRequest request, ErrorReportResponse response )
        throws IOException, FileNotFoundException
    {
        IssueSubmissionRequest subRequest = null;
        // if title is not null, this is a manual report, so we will generate regardless
        // of other checks
        if ( request.getTitle() != null
            || ( isEnabled() && shouldHandleReport( request ) && !shouldIgnore( request.getThrowable() ) ) )
        {
            subRequest = buildRequest( request );
        
            if ( request.getTitle() == null )
            {
                List<Issue> existingIssues = retrieveIssues( subRequest.getSummary() );
        
                if ( existingIssues != null ) {
                    subRequest.setSkipSubmission( true );
                    response.setJiraUrl( existingIssues.get( 0 ).getLink() );
        	        renameFile( writeArchive( subRequest.getBundles() ), existingIssues.iterator().next().getKey() );
                    getLogger().info(
                        "Not reporting problem as it already exists in database: "
                            + existingIssues.iterator().next().getLink() );
                }
            }
        }
        return subRequest;
    }

    private void submitIssue( ErrorReportResponse response, IssueSubmissionRequest subRequest,
                              AuthenticationSource credentials )
        throws IssueSubmissionException, IOException, FileNotFoundException
    {
        List<Bundle> bundles = assembler.assemble( subRequest );
        subRequest.setBundles( bundles );
        File file = writeArchive( bundles );
        
        IssueSubmissionResult result = credentials == null ? issueSubmitter.submit( subRequest ) : issueSubmitter.submit( subRequest, credentials );
        response.setCreated( true );
        response.setJiraUrl( result.getIssueUrl() );

        renameFile( file, result.getKey() );
            
        getLogger().info( "Generated problem report, ticket " + result.getIssueUrl() + " was created." );
    }

    protected boolean shouldHandleReport( ErrorReportRequest request )
    {
        // if there is a title, we are talking about user generated, simply use it
        if ( request.getTitle() != null )
        {
            return true;
        }

        if ( request.getThrowable() != null && StringUtils.isNotEmpty( request.getThrowable().getMessage() ) )
        {
            String hash = DigesterUtils.getSha1Digest( request.getThrowable().getMessage() );

            if ( errorHashSet.contains( hash ) )
            {
                getLogger().debug( "Received an exception we already processed, ignoring." );
                return false;
            }
            else
            {
                errorHashSet.add( hash );
                return true;
            }
        }
        else
        {
            getLogger().debug( "Received an empty message in exception, will not handle" );
        }

        return false;
    }

    protected List<Issue> retrieveIssues( String description )
    {
        try
        {
            Project project = issueRetriever.getProject( projectManager.getProject( null ) );
			List<Issue> issues = issueRetriever.getIssues( "\"" + description + "\"", project );
            if ( !issues.isEmpty() )
            {
                return issues;
            }
        }
        catch ( Exception e )
        {
            getLogger().error( "Unable to query JIRA server to find if error report already exists", e );
        }

        return null;
    }

    protected IssueSubmissionRequest buildRequest( ErrorReportRequest request )
        throws IOException
    {

        IssueSubmissionRequest subRequest = new IssueSubmissionRequest(request.getThrowable());
        
        if ( request.getTitle() != null )
        {
	        if ( request.getDescription() != null )
	        {
	            subRequest.setDescription( request.getDescription() );
	        }
	        subRequest.setSummary( "MPR: " + request.getTitle() );
        }

        assembler.addBundle( subRequest, new MapContentsBundle( request.getContext() ) );

        return subRequest;
    }

    private File getZipFile( String prefix, String suffix )
    {
        File zipDir = nexusConfig.getWorkingDirectory( ERROR_REPORT_DIR );

        if ( !zipDir.exists() )
        {
            zipDir.mkdirs();
        }

        return new File( zipDir, prefix + "." + System.currentTimeMillis() + "." + suffix );
    }

    @Override
    public String getName()
    {
        return "Error Report Settings";
    }

    protected boolean shouldIgnore( Throwable throwable )
    {
        if ( throwable != null )
        {
            if ( "org.mortbay.jetty.EofException".equals( throwable.getClass().getName() ) )
            {
                return true;
            }
            else if ( throwable.getMessage() != null
                && ( throwable.getMessage().contains( "An exception occured writing the response entity" ) || throwable.getMessage().contains(
                    "Error while handling an HTTP server call" ) ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setJIRAUsername( String username )
    {
        getCurrentConfiguration( true ).setJiraUsername( username );
    }

    @Override
    public void setJIRAPassword( String password )
    {
        getCurrentConfiguration( true ).setJiraPassword( password );
    }

    @Override
    public String getJIRAUsername()
    {
        return getCurrentConfiguration( false ).getJiraUsername();
    }

    @Override
    public String getJIRAPassword()
    {
        return getCurrentConfiguration( false ).getJiraPassword();
    }

    @Override
    public boolean isUseGlobalProxy()
    {
        // FIXME do something
        return false;
    }

    @Override
    public void setUseGlobalProxy( boolean useGlobalProxy )
    {
        // FIXME do something
    }

    @Override
    public File assembleBundle( ErrorReportRequest request )
        throws IOException, IssueSubmissionException
    {
        return writeArchive( assembler.assemble( buildRequest( request ) ) );
    }

}

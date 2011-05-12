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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.swizzle.jira.Attachment;
import org.codehaus.swizzle.jira.Component;
import org.codehaus.swizzle.jira.Issue;
import org.junit.Assert;
import org.junit.Test;
import org.sonatype.configuration.ConfigurationException;
import org.sonatype.jira.AttachmentHandler;
import org.sonatype.jira.mock.MockAttachmentHandler;
import org.sonatype.jira.mock.StubJira;
import org.sonatype.jira.test.JiraXmlRpcTestServlet;
import org.sonatype.nexus.AbstractNexusTestCase;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.nexus.proxy.repository.RemoteProxySettings;
import org.sonatype.nexus.proxy.repository.UsernamePasswordRemoteAuthenticationSettings;
import org.sonatype.nexus.scheduling.NexusTask;
import org.sonatype.plexus.encryptor.PlexusEncryptor;
import org.sonatype.scheduling.SchedulerTask;
import org.sonatype.sisu.issue.IssueRetriever;
import org.sonatype.sisu.pr.bundle.Bundle;
import org.sonatype.sisu.pr.bundle.internal.ByteArrayBundle;
import org.sonatype.tests.http.server.jetty.impl.JettyServerProvider;

public class DefaultErrorReportingManagerTest
    extends AbstractNexusTestCase
{
    private static final String PR_PASS = "_____";

    private static final String PR_USER = "sonatype_problem_reporting";

    private DefaultErrorReportingManager manager;

    private NexusConfiguration nexusConfig;

    private File unzipHomeDir = null;

    private JettyServerProvider provider;

    private StubJira mock;

    private PlexusEncryptor encryptor;

    @SuppressWarnings( "deprecation" )
    @Override
    protected void setUp()
        throws Exception
    {
        setupJiraMock( "src/test/resources/jira-mock.db" );
        
        super.setUp();

        unzipHomeDir = new File( getPlexusHomeDir(), "unzip" );
        unzipHomeDir.mkdirs();

        nexusConfig = lookup( NexusConfiguration.class );
        nexusConfig.getConfigurationModel().getErrorReporting().setJiraUrl( provider.getUrl().toString() );
        nexusConfig.getConfigurationModel().getErrorReporting().setJiraUsername( PR_USER );
        nexusConfig.getConfigurationModel().getErrorReporting().setJiraPassword( PR_PASS );

        manager = (DefaultErrorReportingManager) lookup( ErrorReportingManager.class );
        
        encryptor = lookup(PlexusEncryptor.class);
    }

    private void setupJiraMock( String dbPath )
        throws FileNotFoundException, IOException, Exception, MalformedURLException
    {
        mock = new StubJira();
        FileInputStream in = null;
        try
        {
            in = new FileInputStream( dbPath );
            mock.setDatabase( IOUtil.toString( in ) );
        }
        finally
        {
            IOUtil.close( in );
        }

        MockAttachmentHandler mockHandler = new MockAttachmentHandler();
        mockHandler.setMock( mock );

        List<AttachmentHandler> handlers = Arrays.<AttachmentHandler> asList( mockHandler );
        provider = new JettyServerProvider();
        provider.addServlet( new JiraXmlRpcTestServlet( mock, provider.getUrl(), handlers ) );
        provider.start();
    }

    @Override
    protected void customizeContext( final Context ctx )
    {
        try
        {
            ctx.put( "pr.serverUrl", provider.getUrl().toString() );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
            ctx.put( "pr.serverUrl", "https://issues.sonatype.org" );
        }
        ctx.put( "pr.auth.login", PR_USER );
        ctx.put( "pr.auth.password", PR_PASS );
        ctx.put( "pr.project", "SBOX" );
        ctx.put( "pr.component", "Nexus" );
        ctx.put( "pr.issuetype.default", "1" );
        ctx.put( "pr.encryptor.publicKeyPath", "/apr/public-key.txt" );
        super.customizeContext( ctx );
    }

    @Override
    protected void customizeContainerConfiguration( final ContainerConfiguration configuration )
    {
        configuration.setClassPathScanning( "ON" );
        configuration.setAutoWiring( true );
        super.customizeContainerConfiguration( configuration );
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        cleanDir( unzipHomeDir );
        provider.stop();
    }

    private void enableErrorReports( boolean useProxy )
        throws Exception
    {
        manager.setEnabled( true );
        nexusConfig.saveConfiguration();
    }

    private void enableProxy()
        throws ConfigurationException, IOException
    {
        RemoteProxySettings proxy = nexusConfig.getGlobalRemoteStorageContext().getRemoteProxySettings();
        proxy.setHostname( "localhost" );
        proxy.setPort( 8111 );
        proxy.setProxyAuthentication( new UsernamePasswordRemoteAuthenticationSettings( "*****", "*****" ) );

        nexusConfig.saveConfiguration();
    }

    @Test
    public void testJiraAccess()
        throws Exception
    {
        // enableProxy();
        enableErrorReports( false );

        ErrorReportRequest request = new ErrorReportRequest();

        try
        {
            throw new Exception( "Test exception " + Long.toHexString( System.currentTimeMillis() ) );
        }
        catch ( Exception e )
        {
            request.setThrowable( e );
        }

        // First make sure item doesn't already exist
        List<Issue> issues = manager.retrieveIssues( "APR: " + request.getThrowable().getMessage() );

        Assert.assertNull( issues );

        manager.handleError( request );

        issues = manager.retrieveIssues( "APR: " + request.getThrowable().getMessage() );

        Assert.assertEquals( 1, issues.size() );

        manager.handleError( request );

        issues = manager.retrieveIssues( "APR: " + request.getThrowable().getMessage() );

        Assert.assertEquals( 1, issues.size() );
        System.err.println( issues.get( 0 ).getLink() );
    }

    private void addBackupFiles( File dir )
        throws Exception
    {
        new File( dir, "nexus.xml.bak" ).createNewFile();
        new File( dir, "security.xml.bak" ).createNewFile();
    }

    private void addDirectory( String path, String[] filenames )
        throws Exception
    {
        File confDir = new File( getConfHomeDir(), path );
        // File unzipDir = new File( unzipHomeDir, path );
        confDir.mkdirs();
        // unzipDir.mkdirs();

        for ( String filename : filenames )
        {
            new File( confDir, filename ).createNewFile();
        }
    }

    private void extractZipFile( File zipFile, File outputDirectory )
        throws Exception
    {
        FileInputStream fis = new FileInputStream( zipFile );
        ZipInputStream zin = null;
        
        outputDirectory.mkdirs();

        try
        {
            zin = new ZipInputStream( new BufferedInputStream( fis ) );

            ZipEntry entry;
            while ( ( entry = zin.getNextEntry() ) != null )
            {
                File file = new File( outputDirectory, entry.getName() );
                file.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream( file );
                BufferedOutputStream bos = null;

                try
                {
                    byte[] buffer = new byte[2048];
                    bos = new BufferedOutputStream( fos, 2048 );

                    int count;
                    while ( ( count = zin.read( buffer, 0, buffer.length ) ) != -1 )
                    {
                        bos.write( buffer, 0, count );
                    }

                }
                finally
                {
                    if ( bos != null )
                    {
                        bos.close();
                    }
                }
            }
        }
        finally
        {
            if ( zin != null )
            {
                zin.close();
            }
        }
    }

    @Test
    public void testTaskFailure()
        throws Exception
    {
        enableErrorReports( false );

        String msg = "Runtime exception " + Long.toHexString( System.currentTimeMillis() );
        ExceptionTask task = (ExceptionTask) lookup( SchedulerTask.class, "ExceptionTask" );
        task.setMessage( msg );

        String aprMessage = "APR: " + new RuntimeException( msg ).getMessage();

        // First make sure item doesn't already exist
        List<Issue> issues = manager.retrieveIssues( aprMessage );

        Assert.assertNull( issues );

        doCall( task );

        issues = manager.retrieveIssues( aprMessage );

        Assert.assertEquals( 1, issues.size() );

        doCall( task );

        issues = manager.retrieveIssues( aprMessage );

        Assert.assertEquals( 1, issues.size() );
    }

    private void doCall( NexusTask<?> task )
    {
        try
        {
            task.call();
            Thread.sleep( 100 );
        }
        catch ( Throwable t )
        {
        }
    }
    
    public void testIssueContents() throws Exception
    {
        IssueRetriever retriever = lookup(IssueRetriever.class);
        
        
        addBackupFiles( getConfHomeDir() );
        addDirectory( "test-directory", new String[] {"filename1.file", "filename2.file", "filename3.file"} );
        addDirectory( "nested-test-directory/more-nested-test-directory", new String[] { "filename1.file", "filename2.file", "filename3.file" } );
        nexusConfiguration.saveConfiguration();
        
        // enableProxy();
        enableErrorReports( false );

        ErrorReportRequest request = new ErrorReportRequest();

        String msg = "Test exception " + Long.toHexString( System.currentTimeMillis() );
        Exception e = new Exception( msg );
        request.setThrowable( e );

        // First make sure item doesn't already exist
        List<Issue> issues =
            manager.retrieveIssues( "APR: " + request.getThrowable().getMessage() );

        Assert.assertNull( issues );
        
        manager.handleError( request );
        
        issues = retriever.getIssues( msg );
        assertNotNull( issues );
        assertFalse(issues.isEmpty());
        assertEquals( 1, issues.size() );
        
        Issue issue = issues.get( 0 );
        assertEquals("APR: " + msg, issue.getSummary());
        
        String environment = issue.getEnvironment();
        assertFalse(environment.isEmpty());
        assertTrue(environment.contains( "Nexus" ));
        
        assertEquals("sonatype_problem_reporting", issue.getReporter().getName());
        assertEquals("sonatype_problem_reporting", issue.getAssignee().getName());
        
        assertEquals("SBOX", issue.getProject().getKey());
        
        assertNotNull( issue.getDescription() );
        assertTrue( issue.getDescription().contains( e.getStackTrace()[0].toString() ) );
        
        Component component = retriever.getComponent( issue.getProject(), "Nexus" );
        assertTrue(issue.getComponents().contains( component ));
        
        Map<Attachment, byte[]> attachments = mock.getAttachments( issue.getKey() );
        assertEquals(1, attachments.size());
        Entry<Attachment, byte[]> entry = attachments.entrySet().iterator().next();
        Attachment att = entry.getKey();
        
        assertTrue(att.getFileName().startsWith("ProblemReportBundle"));
        
        File zipfile = File.createTempFile( "DefaultErrorReportingManagerTest", ".zip");
        FileOutputStream out = null;
        try {
	        out = new FileOutputStream( zipfile );
	        encryptor.decrypt( new ByteArrayInputStream( entry.getValue() ), out, getClass().getResourceAsStream( "/apr/private-key.txt" ) );
        } finally {
            IOUtil.close( out );
        }
        
        extractZipFile( zipfile, unzipHomeDir );
        
        zipfile.delete();

        assertTrue( unzipHomeDir.exists() );

        File[] files = unzipHomeDir.listFiles();

        assertNotNull( files );
         // TODO: was seven with the directory listing, but that was removed, as it OOM'd
         // TODO: was six but assembling changed
        assertEquals( 4, files.length );
        System.err.println(Arrays.toString( files ));
        
        File unzippedConfDir = new File(unzipHomeDir, "conf");
        
        files = unzippedConfDir.listFiles( new FileFilter(){
            public boolean accept( File pathname )
            {
                if ( pathname.isDirectory()
                    && pathname.getName().equals( "test-directory" ) )
                {
                    return true;
                }
                
                return false;
            }
        });
        
        assertEquals( 1, files.length );
        
        files = files[0].listFiles();
        
        boolean file1found = false;
        boolean file2found = false;
        boolean file3found = false;
        for ( File file : files )
        {
            if ( file.getName().equals( "filename1.file" ) )
            {
                file1found = true;
            }
            else if ( file.getName().equals( "filename2.file" ) )
            {
                file2found = true;
            }
            else if ( file.getName().equals( "filename3.file" ) )
            {
                file3found = true;
            }
        }
        
        assertTrue( file1found && file2found && file3found );
        
        files = unzippedConfDir.listFiles( new FileFilter(){
            public boolean accept( File pathname )
            {
                if ( pathname.isDirectory()
                    && pathname.getName().equals( "nested-test-directory" ) )
                {
                    return true;
                }
                
                return false;
            }
        });
        
        files = files[0].listFiles( new FileFilter(){
           public boolean accept( File pathname )
            {
               if ( pathname.isDirectory()
                   && pathname.getName().equals( "more-nested-test-directory" ) )
               {
                   return true;
               }
               
               return false;
            } 
        });
        
        files = files[0].listFiles();
        
        file1found = false;
        file2found = false;
        file3found = false;
        for ( File file : files )
        {
            if ( file.getName().equals( "filename1.file" ) )
            {
                file1found = true;
            }
            else if ( file.getName().equals( "filename2.file" ) )
            {
                file2found = true;
            }
            else if ( file.getName().equals( "filename3.file" ) )
            {
                file3found = true;
            }
        }
        
        assertTrue( file1found && file2found && file3found );
    }

    @Test
    public void testWriteIntermediateArchive()
        throws FileNotFoundException, IOException
    {
        Bundle bundle = new ByteArrayBundle( "TestBundle".getBytes( "utf-8" ), "testname.txt", "text/plain" );
        File file = manager.writeArchive( Collections.singleton( bundle ) );

        assertThat( file.exists(), is( true ) );
        assertThat( file.getAbsolutePath(), containsString( DefaultErrorReportingManager.ERROR_REPORT_DIR ) );
    }

}

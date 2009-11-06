/**
 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General Public License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License Version 3 for more details.
 * You should have received a copy of the GNU General Public License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.integrationtests;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.restlet.data.Response;
import org.sonatype.jettytestsuite.ServletServer;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.proxy.repository.LocalStatus;
import org.sonatype.nexus.proxy.repository.ProxyMode;
import org.sonatype.nexus.proxy.repository.RemoteStatus;
import org.sonatype.nexus.rest.model.RepositoryStatusResource;
import org.sonatype.nexus.test.utils.FileTestingUtils;
import org.sonatype.nexus.test.utils.JettyInstaceFactory;
import org.sonatype.nexus.test.utils.RepositoryStatusMessageUtil;
import org.sonatype.nexus.test.utils.TestProperties;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;

public abstract class AbstractNexusProxyIntegrationTest
    extends AbstractNexusIntegrationTest
{

    protected String localStorageDir = null;

    protected ServletServer server;

    protected AbstractNexusProxyIntegrationTest()
    {
        this( "release-proxy-repo-1" );
    }

    protected AbstractNexusProxyIntegrationTest( String testRepositoryId )
    {
        super( testRepositoryId );

        this.localStorageDir = TestProperties.getString( "proxy.repo.base.dir" );
    }

    @Override
    protected void startExtraServices()
        throws Exception
    {
        server = createProxyServer();
        startProxy();
    }

    public void startProxy()
        throws StartingException
    {
        server.start();
    }

    protected ServletServer createProxyServer()
        throws Exception
    {
        return JettyInstaceFactory.getDefaultFileServer( proxyServerPort );
    }

    @AfterClass
    public void stopProxy()
        throws Exception
    {
        server.stop();
    }

    protected File getLocalFile( String repositoryId, Gav gav )
    {
        return this.getLocalFile( repositoryId, gav.getGroupId(), gav.getArtifactId(), gav.getVersion(),
                                  gav.getExtension() );
    }

    protected File getLocalFile( String repositoryId, String groupId, String artifact, String version, String type )
    {
        File result =
            new File( this.localStorageDir, repositoryId + "/" + groupId.replace( '.', '/' ) + "/" + artifact + "/"
                + version + "/" + artifact + "-" + version + "." + type );
        log.debug( "Returning file: " + result );
        return result;
    }

    // TODO: Refactor this into the AbstractNexusIntegrationTest or some util class, to make more generic

    protected void setBlockProxy( String nexusBaseUrl, String repoId, boolean block )
        throws IOException
    {
        RepositoryStatusResource status = new RepositoryStatusResource();
        status.setId( repoId );
        status.setRepoType( "proxy" );
        status.setLocalStatus( LocalStatus.IN_SERVICE.name() );
        if ( block )
        {
            status.setRemoteStatus( RemoteStatus.AVAILABLE.name() );
            status.setProxyMode( ProxyMode.BLOCKED_MANUAL.name() );
        }
        else
        {
            status.setRemoteStatus( RemoteStatus.UNAVAILABLE.name() );
            status.setProxyMode( ProxyMode.ALLOW.name() );
        }
        Response response = RepositoryStatusMessageUtil.changeStatus( status );

        if ( !response.getStatus().isSuccess() )
        {
            AssertJUnit.fail( "Could not unblock proxy: " + repoId + ", status: " + response.getStatus().getName()
                + " (" + response.getStatus().getCode() + ") - " + response.getStatus().getDescription() );
        }
    }

    protected void setOutOfServiceProxy( String nexusBaseUrl, String repoId, boolean outOfService )
        throws IOException
    {

        RepositoryStatusResource status = new RepositoryStatusResource();
        status.setId( repoId );
        status.setRepoType( "proxy" );
        if ( outOfService )
        {
            status.setLocalStatus( LocalStatus.OUT_OF_SERVICE.name() );
        }
        else
        {
            status.setLocalStatus( LocalStatus.IN_SERVICE.name() );
        }
        Response response = RepositoryStatusMessageUtil.changeStatus( status );

        if ( !response.getStatus().isSuccess() )
        {
            AssertJUnit.fail( "Could not set proxy out of service status (Status: " + response.getStatus() + ": "
                + repoId + "\n" + response.getEntity().getText() );
        }
    }

    @Override
    protected void copyTestResources()
        throws IOException
    {
        super.copyTestResources();

        File source = new File( TestProperties.getString( "test.resources.source.folder" ), "proxyRepo" );
        if ( !source.exists() )
        {
            return;
        }

        FileTestingUtils.interpolationDirectoryCopy( source, new File( localStorageDir ), TestProperties.getAll() );

    }
}

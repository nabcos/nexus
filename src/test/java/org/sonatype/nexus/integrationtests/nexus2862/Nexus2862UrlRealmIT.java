package org.sonatype.nexus.integrationtests.nexus2862;

import java.io.IOException;

import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.rest.model.GlobalConfigurationResource;
import org.sonatype.nexus.test.utils.SettingsMessageUtil;
import org.sonatype.nexus.test.utils.UserCreationUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class Nexus2862UrlRealmIT
    extends AbstractNexusIntegrationTest
{

    private AuthenticationServer server;

    @Override
    public boolean isSecureTest()
    {
        return true;
    }

    @Override
    protected void startExtraServices()
        throws Exception
    {
        super.startExtraServices();

        System.setProperty( "plexus.authentication-url", "http://localhost:" + proxyServerPort );
        System.setProperty( "plexus.url-authentication-default-role", "admin" );
        System.setProperty( "plexus.url-authentication-email-domain", "sonatype.com" );

        server = new AuthenticationServer( proxyServerPort );
        server.addUser( "juka", "juk@", "admin" );
        server.start();
    }

    @AfterClass
    public void stopServer()
        throws Exception
    {
        server.stop();
    }

    @Override
    protected void runOnce()
        throws Exception
    {
        super.runOnce();

        TestContainer.getInstance().getTestContext().useAdminForRequests();
        GlobalConfigurationResource resource = SettingsMessageUtil.getCurrentSettings();
        resource.getSecurityRealms().clear();
        resource.getSecurityRealms().add( "url" );
        Status status = SettingsMessageUtil.save( resource );
        AssertJUnit.assertTrue( status.isSuccess() );
    }

    @Test
    public void loginUrlRealm()
        throws IOException
    {
        Status login = UserCreationUtil.login( "juka", "juk@" );
        AssertJUnit.assertTrue( login + "", login.isSuccess() );

        AssertJUnit.assertTrue( UserCreationUtil.logout().isSuccess() );
    }

    @Test
    public void wrongPassword()
        throws IOException
    {
        Status status = UserCreationUtil.login( "juka", "juka" );
        AssertJUnit.assertFalse( status + "", status.isSuccess() );

        AssertJUnit.assertTrue( UserCreationUtil.logout().isSuccess() );
    }

    @Test
    public void wrongUsername()
        throws IOException
    {
        Status status = UserCreationUtil.login( "anuser", "juka" );
        AssertJUnit.assertFalse( status + "", status.isSuccess() );

        AssertJUnit.assertTrue( UserCreationUtil.logout().isSuccess() );
    }

}

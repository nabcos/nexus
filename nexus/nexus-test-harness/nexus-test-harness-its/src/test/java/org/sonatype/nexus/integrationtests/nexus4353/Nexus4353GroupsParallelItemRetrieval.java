package org.sonatype.nexus.integrationtests.nexus4353;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.sonatype.nexus.integrationtests.AbstractNexusProxyIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class Nexus4353GroupsParallelItemRetrieval
    extends AbstractNexusProxyIntegrationTest
{

    private static final long TIMEOUT = 5000;

    private Server server;

    public Nexus4353GroupsParallelItemRetrieval()
    {
        super( "nexus4353" );
    }

    @Test
    public void testMetadataRetrieval()
        throws Exception
    {
        replaceProxy();

        long start = System.currentTimeMillis();
        String tmpFile = System.getProperty( "java.io.tmpdir" ) + "/" + "Nexus4353-" + start + ".tmp";
        URL url = new URL( nexusBaseUrl + "content/groups/nexus-4353-group/ant/ant/maven-metadata.xml" );

        try
        {
            RequestFacade.downloadFile( url, tmpFile );
        }
        catch ( IOException e )
        {
            // expected, only 404s
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();

        long duration = end - start;

        // group contains 3 repos, parallel retrieval means we will should not run into that timeout 3 times
        assertThat( duration, allOf( greaterThanOrEqualTo( TIMEOUT ), lessThan( 2 * TIMEOUT ) ) );
    }

    private void replaceProxy()
        throws Exception
    {
        // final Handler handler = proxyServer.getServer().getHandler();
        proxyServer.stop();

        server = new Server( proxyPort );

        HandlerList coll = new HandlerList();
        AbstractHandler handler = new AbstractHandler()
        {

            @Override
            public void handle( String target, HttpServletRequest request, final HttpServletResponse response,
                                int dispatch )
                throws IOException, ServletException
            {
                ( (Request) request ).setHandled( true );
                try
                {
                    Thread.sleep( TIMEOUT );
                    try
                    {
                        response.sendError( 404 );
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                    }
                }
                catch ( InterruptedException e )
                {
                    // not interested
                }
                // for ( String path : P2Constants.METADATA_FILE_PATHS )
                // {
                // if ( target.endsWith( path ) )
                // {
                // response.sendError( 503 );
                // return;
                // }
                // }
                // handler.handle( target, request, response, dispatch );
            }
        };

        coll.addHandler( handler );
        server.setHandler( coll );
        server.start();
    }

    @AfterMethod( alwaysRun = true )
    @Override
    public void stopProxy()
        throws Exception
    {
        if ( server != null )
        {
            server.stop();
        }
        super.stopProxy();
    }
}

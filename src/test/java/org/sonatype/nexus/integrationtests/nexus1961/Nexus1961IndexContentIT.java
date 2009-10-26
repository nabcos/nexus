package org.sonatype.nexus.integrationtests.nexus1961;

import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.rest.model.ContentListResource;
import org.sonatype.nexus.rest.model.ContentListResourceResponse;
import org.sonatype.nexus.test.utils.RepositoryMessageUtil;
import org.sonatype.nexus.test.utils.XStreamFactory;
import org.sonatype.plexus.rest.representation.XStreamRepresentation;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class Nexus1961IndexContentIT
    extends AbstractNexusIntegrationTest
{

    @BeforeClass
    public static void init()
        throws Exception
    {
        cleanWorkDir();
    }

    @Override
    protected void runOnce()
        throws Exception
    {
        super.runOnce();

        RepositoryMessageUtil.updateIndexes( REPO_TEST_HARNESS_REPO );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void getIndexContent()
        throws Exception
    {
        String serviceURI = "service/local/repositories/" + REPO_TEST_HARNESS_REPO + "/index_content/";

        Response response = RequestFacade.doGetRequest( serviceURI );
        String responseText = response.getEntity().getText();
        Status status = response.getStatus();
        AssertJUnit.assertTrue( responseText + status, status.isSuccess() );

        XStreamRepresentation re =
            new XStreamRepresentation( XStreamFactory.getXmlXStream(), responseText, MediaType.APPLICATION_XML );
        ContentListResourceResponse resourceResponse =
            (ContentListResourceResponse) re.getPayload( new ContentListResourceResponse() );

        List<ContentListResource> content = resourceResponse.getData();
        for ( ContentListResource contentListResource : content )
        {
            AssertJUnit.assertEquals( "nexus1961", contentListResource.getText() );
        }

    }
}

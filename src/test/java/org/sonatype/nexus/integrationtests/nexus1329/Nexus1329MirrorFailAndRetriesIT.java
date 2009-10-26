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
package org.sonatype.nexus.integrationtests.nexus1329;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.rest.model.MirrorStatusResource;
import org.sonatype.nexus.rest.model.MirrorStatusResourceListResponse;
import org.sonatype.nexus.test.utils.FileTestingUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class Nexus1329MirrorFailAndRetriesIT
    extends AbstractMirrorIT
{

    /**
     * 3. download from mirror fails, retry fails, download succeeds from repository the mirror should be blacklisted
     */
    @Test
    public void downloadFromMirrorTest()
        throws Exception
    {
        File content = getTestFile( "basic" );

        server.addServer( "repository", content );
        List<String> mirror1Urls = server.addServer( "mirror1", HttpServletResponse.SC_REQUEST_TIMEOUT );
        List<String> mirror2Urls = server.addServer( "mirror2", HttpServletResponse.SC_NOT_FOUND );

        server.start();

        Gav gav =
            new Gav( "nexus1329", "sample", "1.0.0", null, "xml", null, null, null, false, false, null, false, null );

        File artifactFile = this.downloadArtifactFromRepository( REPO, gav, "./target/downloads/nexus1329" );

        File originalFile = this.getTestFile( "basic/nexus1329/sample/1.0.0/sample-1.0.0.xml" );
        AssertJUnit.assertTrue( FileTestingUtils.compareFileSHA1s( originalFile, artifactFile ) );

        AssertJUnit.assertFalse( "Nexus should access first mirror " + mirror1Urls, mirror1Urls.isEmpty() );
        AssertJUnit.assertEquals( "Nexus should retry mirror " + mirror1Urls, 3, mirror1Urls.size() );
        AssertJUnit.assertTrue( "Nexus should not access second mirror " + mirror2Urls, mirror2Urls.isEmpty() );

        MirrorStatusResourceListResponse response = this.messageUtil.getMirrorsStatus( REPO );

        MirrorStatusResource one = response.getData().get( 0 );

        AssertJUnit.assertEquals( "http://localhost:" + webProxyPort + "/mirror1", one.getUrl() );
        AssertJUnit.assertEquals( "Blacklisted", one.getStatus() );
    }
}

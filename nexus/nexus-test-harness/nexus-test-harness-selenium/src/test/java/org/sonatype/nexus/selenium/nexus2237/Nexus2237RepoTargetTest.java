/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.selenium.nexus2237;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.hasItems;

import static org.hamcrest.MatcherAssert.assertThat;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.mock.MockEvent;
import org.sonatype.nexus.mock.MockListener;
import org.sonatype.nexus.mock.SeleniumTest;
import org.sonatype.nexus.mock.pages.RepoTargetForm;
import org.sonatype.nexus.mock.pages.RepoTargetTab;
import org.sonatype.nexus.mock.rest.MockHelper;
import org.sonatype.nexus.rest.model.RepositoryTargetResourceResponse;
import org.sonatype.nexus.selenium.util.NxAssert;
import org.testng.Assert;
import org.testng.annotations.Test;

@Component( role = Nexus2237RepoTargetTest.class )
public class Nexus2237RepoTargetTest
    extends SeleniumTest
{

    @Test
    public void errorMessages()
        throws InterruptedException
    {
        doLogin();

        RepoTargetForm target = main.openRepoTargets().addRepoTarget();

        NxAssert.requiredField( target.getName(), "target" );
        NxAssert.requiredField( target.getRepositoryType(), "maven2" );

        target.save();
        NxAssert.hasErrorText( target.getPattern(), "The target should have at least one pattern." );

        target.addPattern( ".*" );
        NxAssert.noErrorText( target.getPattern() );

        target.cancel();
    }

    @Test
    public void repoTargetCRUD()
        throws InterruptedException
    {
        doLogin();

        RepoTargetTab targets = main.openRepoTargets();

        // create
        String name = "seleniumTarget";
        String repoType = "maven2";
        final String pattern = ".*";
        final String pattern2 = ".*maven-metadata\\.xml.*";

        MockListener ml = MockHelper.listen( "/repo_targets", new MockListener()
        {
            @Override
            public void onPayload( Object payload, MockEvent evt )
            {
                assertThat( payload, not( nullValue() ) );
                RepositoryTargetResourceResponse result = (RepositoryTargetResourceResponse) payload;

                assertThat( result.getData().getPatterns(), hasItems( pattern, pattern2 ) );
            }
        } );

        targets.addRepoTarget().populate( name, repoType, pattern, pattern2 ).save();

        RepositoryTargetResourceResponse result = (RepositoryTargetResourceResponse) ml.getResult();
        String targetId = nexusBaseURL + "service/local/repo_targets/" + result.getData().getId();

        MockHelper.checkAssertions();
        MockHelper.clearMocks();

        targets.refresh();

        Assert.assertTrue( targets.getGrid().contains( targetId ) );
        targets.refresh();

        // read
        RepoTargetForm target = targets.select( targetId );
        NxAssert.valueEqualsTo( target.getName(), name );
        NxAssert.valueEqualsTo( target.getRepositoryType(), repoType );

        targets.refresh();

        // update
        String newName = "new selenium repository target";

        target = targets.select( targetId );
        target.getName().type( newName );
        target.save();

        targets.refresh();
        target = targets.select( targetId );
        NxAssert.valueEqualsTo( target.getName(), newName );

        targets.refresh();

        // delete
        targets.select( targetId );
        targets.delete().clickYes();
        targets.refresh();

        Assert.assertFalse( targets.getGrid().contains( targetId ) );
    }
}

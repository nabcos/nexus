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
package org.sonatype.nexus.selenium.nexus1962;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

import java.util.Arrays;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.restlet.data.Status;
import org.sonatype.nexus.mock.MockListener;
import org.sonatype.nexus.mock.MockResponse;
import org.sonatype.nexus.mock.SeleniumTest;
import org.sonatype.nexus.mock.pages.MessageBox;
import org.sonatype.nexus.mock.pages.ScheduleGrid;
import org.sonatype.nexus.mock.pages.SchedulesConfigTab;
import org.sonatype.nexus.mock.rest.MockHelper;
import org.sonatype.nexus.scheduling.NexusScheduler;
import org.sonatype.nexus.tasks.EmptyTrashTask;
import org.sonatype.scheduling.NoSuchTaskException;
import org.sonatype.scheduling.ScheduledTask;
import org.sonatype.scheduling.schedules.ManualRunSchedule;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Component( role = Nexus1962TaskContextMenuTest.class )
public class Nexus1962TaskContextMenuTest
    extends SeleniumTest
{
    @Requirement
    private NexusScheduler nexusScheduler;

    private ScheduledTask<Object> task;

    @BeforeMethod
    public void createTask()
    {
        task =
            nexusScheduler.schedule( "selenium-context-task",
                nexusScheduler.createTaskInstance( EmptyTrashTask.class ), new ManualRunSchedule() );
    }

    @AfterMethod
    public void deleteTask()
    {
        if ( task != null )
        {
            try
            {
                task.get();
            }
            catch ( Exception e )
            {
                log.error( e.getMessage(), e );
            }
            task.cancel();
            task = null;
        }
    }

    @Test
    public void contextMenuRefresh()
        throws InterruptedException
    {
        doLogin();

        SchedulesConfigTab tasks = main.openTasks();
        ScheduleGrid scheduleGrid = tasks.getScheduleGrid();

        String uiTaskId = nexusBaseURL + "service/local/schedules/" + task.getId();

        // refresh
        tasks.contextMenuRefresh( 0 );
        assertThat( Arrays.asList( scheduleGrid.getKeys() ), hasItems( uiTaskId ) );
    }

    @Test
    public void contextMenuRun()
        throws InterruptedException
    {
        doLogin();
        SchedulesConfigTab tasks = main.openTasks();

        String uiTaskId = nexusBaseURL + "service/local/schedules/" + task.getId();

        // run error
        MockHelper.expect( "/schedule_run/{scheduledServiceId}", new MockResponse( Status.CLIENT_ERROR_BAD_REQUEST,
            null ) );
        tasks.contextMenuRun( uiTaskId );
        new MessageBox( selenium ).clickOk();

        MockHelper.checkExecutions();
        MockHelper.checkAssertions();
        MockHelper.clearMocks();

        // run
        MockHelper.listen( "/schedule_run/{scheduledServiceId}", new MockListener() );
        tasks.contextMenuRun( uiTaskId );

        MockHelper.checkExecutions();
        MockHelper.checkAssertions();
        MockHelper.clearMocks();
    }

    @Test
    public void contextMenuDelete()
        throws InterruptedException
    {
        doLogin();

        SchedulesConfigTab tasks = main.openTasks();

        String uiTaskId = nexusBaseURL + "service/local/schedules/" + task.getId();

        // delete
        tasks.contextMenuDelete( uiTaskId );

        MockHelper.checkExecutions();
        MockHelper.checkAssertions();
        MockHelper.clearMocks();

        try
        {
            nexusScheduler.getTaskById( task.getId() );
            Assert.fail( "Task shouldn't exists! " + task.getId() );
        }
        catch ( NoSuchTaskException e )
        {
            // expected
        }

    }
}

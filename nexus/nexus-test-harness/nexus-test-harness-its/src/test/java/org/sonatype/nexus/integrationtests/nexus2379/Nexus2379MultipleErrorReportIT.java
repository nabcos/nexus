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
package org.sonatype.nexus.integrationtests.nexus2379;

import org.restlet.data.Method;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.test.utils.ErrorReportUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Nexus2379MultipleErrorReportIT
    extends AbstractNexusIntegrationTest
{
    @BeforeMethod
    public void cleanDirs()
        throws Exception
    {
        ErrorReportUtil.cleanErrorBundleDir( nexusWorkDir );
    }
    
    @Test
    public void validateMultipleErrors()
        throws Exception
    {        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateZipContents( nexusWorkDir, true );
        
        ErrorReportUtil.cleanErrorBundleDir( nexusWorkDir );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=500", Method.GET, null );
        
        ErrorReportUtil.validateNoZip( nexusWorkDir );
        
        RequestFacade.sendMessage( "service/local/exception?status=501", Method.GET, null );
        
        ErrorReportUtil.validateZipContents( nexusWorkDir, true );
    }
}
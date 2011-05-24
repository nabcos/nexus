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
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.codehaus.plexus.swizzle.IssueSubmissionException;
import org.sonatype.nexus.configuration.Configurable;

public interface ErrorReportingManager
    extends Configurable
{
    boolean isEnabled();

    void setEnabled( boolean value );

    // ==

    ErrorReportResponse handleError( ErrorReportRequest request )
        throws IssueSubmissionException, IOException, GeneralSecurityException;
    
    ErrorReportResponse handleError( ErrorReportRequest request, String jiraUsername, String jiraPassword)
        throws IssueSubmissionException, IOException, GeneralSecurityException;

    ErrorReportResponse handleError( org.sonatype.nexus.error.reporting.ErrorReportRequest genReq, String jiraUsername,
                                     String jiraPassword, boolean useGlobalProxy )
        throws IssueSubmissionException, IOException, GeneralSecurityException;

    void setJIRAUsername( String username );

    void setJIRAPassword( String password );

    String getJIRAUsername();

    String getJIRAPassword();

    boolean isUseGlobalProxy();

    void setUseGlobalProxy( boolean useGlobalProxy );

    File assembleBundle( ErrorReportRequest request )
        throws IssueSubmissionException, IOException;

}

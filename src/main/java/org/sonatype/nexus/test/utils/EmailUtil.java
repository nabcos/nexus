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
package org.sonatype.nexus.test.utils;

import org.apache.log4j.Logger;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class EmailUtil
{
    private static final Logger log = Logger.getLogger( EmailUtil.class );

    public static final String USER_USERNAME = "smtp-username";

    public static final String USER_PASSWORD = "smtp-password";

    public static final String USER_EMAIL = "system@nexus.org";

    public static synchronized GreenMail startEmailServer( int serverPort )
    {
        // ServerSetup smtp = new ServerSetup( 1234, null, ServerSetup.PROTOCOL_SMTP );
        ServerSetup smtp = new ServerSetup( serverPort, null, ServerSetup.PROTOCOL_SMTP );

        GreenMail server = new GreenMail( smtp );
        server.setUser( USER_EMAIL, USER_USERNAME, USER_PASSWORD );
        log.debug( "Starting e-mail server" );
        server.start();
        return server;
    }

}

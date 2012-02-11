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
package org.sonatype.nexus.integrationtests.report;

import java.util.List;

/**
 * The default implementation used to print Confluence wiki markup to the console. I am sick of needed to update <a
 * href='https://docs.sonatype.com/display/Nexus/Automated+Integration+Tests'>this</a> manually.
 */
public class ConsoleWikiReport
    implements TestReport
{

    private static final String NEXUS_BUG_URL_BASE = "http://issues.sonatype.org/browse/";

    private static final String NEXUS_SCM_URL_BASE =
        "http://svn.sonatype.org/nexus/trunk/nexus/nexus-test-harness/nexus-test-harness-launcher/src/test/java/";

    public void writeReport( List<ReportBean> beans )
    {

        System.out.println( "|| Test Name || Description || JIRA  || Implemented ||" );
        for ( ReportBean bean : beans )
        {
            String scmUrl =
                "[" + bean.getJavaClass().getName() + "|" + NEXUS_SCM_URL_BASE
                    + bean.getJavaClass().getPackage().replaceAll( "\\.", "/" ) + "/" + bean.getJavaClass().getName()
                    + ".java]";

            String jiraUrl = "[" + bean.getTestId() + "|" + NEXUS_BUG_URL_BASE + bean.getTestId() + "]";
            
            // get the description
            String comment = bean.getJavaClass().getComment();
            comment = (comment == null) ? "MISSING DESCRIPTION" : convertAnchor( comment );
            
            String row =
                " | " + scmUrl + " | " + comment + " | " + jiraUrl
                    + " | (/) |";

            System.out.println( row );
        }
        System.out.flush();
    }

    /**
     * If this was actually going to be used, this would need a better solution.
     * 
     * @param stringWithAnchor
     * @return
     */
    private static String convertAnchor( String stringWithAnchor )
    {
        if ( stringWithAnchor.contains( "<a href='" ) )
        {

            int start = stringWithAnchor.indexOf( "<a href='" ) + 9;
            int end = stringWithAnchor.lastIndexOf( "</a>" );

            String url = stringWithAnchor.substring( start, end ).split( "'>" )[0];
            String text = stringWithAnchor.substring( start, end ).split( "'>" )[1];

            String wikiLink = "[" + text + "|" + url + "]";

            String result = stringWithAnchor.replaceAll( "<a href='.*'>.*</a>", wikiLink );
            return result;

        }
        return stringWithAnchor;

    }

    public static void main( String[] args )
    {
        String data =
            "asdfa <a href='https://docs.sonatype.com/display/NX/Nexus+Test-Harness'>Nexus Test-Harness</a> asdf.";

        System.out.println( convertAnchor( data ) );
    }

}

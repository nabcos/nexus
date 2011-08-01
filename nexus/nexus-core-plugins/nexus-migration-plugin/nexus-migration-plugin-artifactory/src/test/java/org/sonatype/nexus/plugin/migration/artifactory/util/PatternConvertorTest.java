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
package org.sonatype.nexus.plugin.migration.artifactory.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

public class PatternConvertorTest
{
    @Test
    public void convert125Pattern()
        throws Exception
    {
        Pattern pattern = Pattern.compile( PatternConvertor.convert125Pattern( "ANY" ) );

        assertMatch(pattern, "org/apache");
        assertMatch(pattern, "com/");
        
        pattern = Pattern.compile( PatternConvertor.convert125Pattern( "org/apache" ) );
        assertMatch(pattern, "org/apache/");
        assertMatch(pattern, "org/apache/maven");
        assertMatch(pattern, "org/apache/maven/artifact.jar");
        assertNotMatch(pattern, "org/sonatype");
        assertNotMatch(pattern, "org");
        assertNotMatch(pattern, "foo/org/apache");
    }

    @Test
    public void convert130Pattern()
    {
        List<String> includes = new ArrayList<String>();
        List<String> excludes = new ArrayList<String>();

        includes.add( "org/apache/**" );
        includes.add( "com/acme/**" );
        includes.add( "org/?aven/**" );
        includes.add( "**/*-sources.*" );
        excludes.add( "com/acme/exp-project/**" );
        excludes.add( "**/*-sources.*" );
        excludes.add( "**/*-SNAPSHOT/**" );

        Pattern pattern = Pattern.compile( PatternConvertor.convert130Pattern( includes, excludes ) );
        
        assertMatch(pattern, "org/apache/maven");
        assertMatch(pattern, "com/acme/");
        assertMatch(pattern, "com/acme/artifactory");
        assertMatch(pattern, "org/maven/");
        assertMatch(pattern, "org/raven/");
        assertMatch(pattern, "com/sonatype/my/artifact-1.0-sources.jar");
        assertNotMatch(pattern, "com/sonatype/my/artifact-1.0-sourcesjar");
        //TODO: the asserts below should be passed
/*        assertNotMatch(pattern, "org/");
        assertNotMatch(pattern, "org/ntaven/");
        assertNotMatch(pattern, "com/acme/exp-project/");
        assertNotMatch(pattern, "org/apache/maven/artifact-sources.jar");
        assertNotMatch(pattern, "org/apache/artifact-sources.jar.md5");
        assertNotMatch(pattern, "org/apache/maven/1.0-SNAPSHOT/");
        assertNotMatch(pattern, "org/acme/0.1-SNAPSHOT/artifact-0.1-SNAPSHOT.jar");*/
    }

    protected void assertMatch( Pattern pattern, String text )
    {
        Assert.assertTrue( text + " doesn't match pattern " + pattern.pattern(), pattern.matcher( text ).matches() );
    }

    protected void assertNotMatch( Pattern pattern, String text )
    {
        Assert.assertFalse( text + " matches pattern " + pattern.pattern(), pattern.matcher( text ).matches() );
    }
    
    public static void main(String[] args)
    {
        System.out.println(PatternConvertor.convertAntStylePattern("**/*-sources.*"));
    }
}

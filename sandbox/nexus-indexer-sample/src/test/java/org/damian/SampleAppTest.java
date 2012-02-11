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
package org.damian;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.sonatype.nexus.configuration.PlexusTestCaseSupport;
import org.codehaus.plexus.context.Context;
import org.damian.creators.SampleIndexCreator;
import org.sonatype.nexus.index.ArtifactInfo;
import org.sonatype.nexus.index.ArtifactInfoGroup;
import org.sonatype.nexus.index.search.grouping.AbstractGrouping;

public class SampleAppTest
    extends PlexusTestCaseSupport
{
    private SampleApp app;

    @Override
    protected void customizeContext( Context context )
    {
        super.customizeContext( context );

        context.put( "repository.path", "src/test/resources/repo" );
        context.put( "index.path", "target/indexOutput" );
    }

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        app = lookup( SampleApp.class );
    }

    public void testAddIndexContext()
        throws Exception
    {
        app.index();

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( ArtifactInfo.ARTIFACT_ID, "*" );

        assertNotNull( "returned artifacts is null", artifacts );
        assertFalse( "returned artifacts is empty", artifacts.isEmpty() );
    }

    public void testSearch()
        throws Exception
    {
        app.index();

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( ArtifactInfo.ARTIFACT_ID, "hivedoc-plugin" );

        assertNotNull( "returned artifacts is null", artifacts );
        assertFalse( "returned artifacts is empty", artifacts.isEmpty() );
        assertEquals( "returned artifacts has more than 1 entry", 1, artifacts.size() );
        assertEquals( "returned artifact not correct item", "hivedoc-plugin", artifacts.iterator().next().artifactId );
    }

    public void testSampleSearch()
        throws Exception
    {
        app.index();

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( SampleIndexCreator.MY_FIELD, "value" );

        assertNotNull( "returned artifacts is null", artifacts );
        assertFalse( "returned artifacts is empty", artifacts.isEmpty() );

        for ( ArtifactInfo ai : artifacts )
        {
            assertEquals( "returned artifact has invalid data", "value", ai.getAttributes().get( SampleIndexCreator.MY_FIELD ) );
        }
    }

    public void testNegativeSampleSearch()
        throws Exception
    {
        app.index();

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( SampleIndexCreator.MY_FIELD, "invalid" );

        assertNotNull( "returned artifacts is null", artifacts );
        assertTrue( "returned artifacts should be empty", artifacts.isEmpty() );
    }

    public void testSampleSearchWithTermQuery()
        throws Exception
    {
        app.index();

        // This type of query will be totally built outside of nexus indexer, and will not
        // be tied to constraints defined in
        // http://svn.sonatype.org/nexus/trunk/nexus-indexer/src/main/java/org/sonatype/nexus/index/DefaultQueryCreator.java

        // A TermQuery matches equal strings
        Query q = new TermQuery( new Term( SampleIndexCreator.MY_FIELD, "value" ) );

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertFalse( "returned artifacts is empty", artifacts.isEmpty() );

        for ( ArtifactInfo ai : artifacts )
        {
            assertEquals( "returned artifact has invalid data", "value", ai.getAttributes().get( SampleIndexCreator.MY_FIELD ) );
        }
    }

    public void testNegativeSampleSearchWithTermQuery()
        throws Exception
    {
        app.index();

        Query q = new TermQuery( new Term( SampleIndexCreator.MY_FIELD, "invalid" ) );

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertTrue( "returned artifacts should be empty", artifacts.isEmpty() );
    }

    public void testSampleSearchWithPrefixQuery()
        throws Exception
    {
        app.index();

        // This type of query will be totally built outside of nexus indexer, and will not
        // be tied to constraints defined in
        // http://svn.sonatype.org/nexus/trunk/nexus-indexer/src/main/java/org/sonatype/nexus/index/DefaultQueryCreator.java

        // A PrefixQuery will look for any documents containing the MY_FIELD term that starts with val
        Query q = new PrefixQuery( new Term( SampleIndexCreator.MY_FIELD, "val" ) );

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertFalse( "returned artifacts is empty", artifacts.isEmpty() );

        for ( ArtifactInfo ai : artifacts )
        {
            assertEquals( "returned artifact has invalid data", "value", ai.getAttributes().get( SampleIndexCreator.MY_FIELD ) );
        }
    }

    public void testNegativeSampleSearchWithPrefixQuery()
        throws Exception
    {
        app.index();

        Query q = new PrefixQuery( new Term( SampleIndexCreator.MY_FIELD, "vrz" ) );

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertTrue( "returned artifacts should be empty", artifacts.isEmpty() );
    }

    public void testSampleSearchWithWildcardQuery()
        throws Exception
    {
        app.index();

        // This type of query will be totally built outside of nexus indexer, and will not
        // be tied to constraints defined in
        // http://svn.sonatype.org/nexus/trunk/nexus-indexer/src/main/java/org/sonatype/nexus/index/DefaultQueryCreator.java

        // A WildcardQuery supports the * and ? wildcard characters
        Query q = new WildcardQuery( new Term( SampleIndexCreator.MY_FIELD, "*alue" ) );

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertFalse( "returned artifacts is empty", artifacts.isEmpty() );

        for ( ArtifactInfo ai : artifacts )
        {
            assertEquals( "returned artifact has invalid data", "value", ai.getAttributes().get( SampleIndexCreator.MY_FIELD ) );
        }

        // A WildcardQuery supports the * and ? wildcard characters
        q = new WildcardQuery( new Term( SampleIndexCreator.MY_FIELD, "v?lue" ) );

        artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertFalse( "returned artifacts is empty", artifacts.isEmpty() );

        for ( ArtifactInfo ai : artifacts )
        {
            assertEquals( "returned artifact has invalid data", "value", ai.getAttributes().get( SampleIndexCreator.MY_FIELD ) );
        }

        // A WildcardQuery supports the * and ? wildcard characters
        q = new WildcardQuery( new Term( SampleIndexCreator.MY_FIELD, "val*" ) );

        artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertFalse( "returned artifacts is empty", artifacts.isEmpty() );

        for ( ArtifactInfo ai : artifacts )
        {
            assertEquals( "returned artifact has invalid data", "value", ai.getAttributes().get( SampleIndexCreator.MY_FIELD ) );
        }
    }

    public void testNegativeSampleSearchWithWildcardQuery()
        throws Exception
    {
        app.index();

        Query q = new WildcardQuery( new Term( SampleIndexCreator.MY_FIELD, "*invalid" ) );

        Set<ArtifactInfo> artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertTrue( "returned artifacts should be empty", artifacts.isEmpty() );

        q = new WildcardQuery( new Term( SampleIndexCreator.MY_FIELD, "vaj?e" ) );

        artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertTrue( "returned artifacts should be empty", artifacts.isEmpty() );

        q = new WildcardQuery( new Term( SampleIndexCreator.MY_FIELD, "vaj*" ) );

        artifacts = app.searchIndexFlat( q );

        assertNotNull( "returned artifacts is null", artifacts );
        assertTrue( "returned artifacts should be empty", artifacts.isEmpty() );
    }

    public void testSampleSearchGroup()
        throws Exception
    {
        app.index();

        Map<String,ArtifactInfoGroup> groupedArtifacts = app.searchIndexGrouped( SampleIndexCreator.MY_FIELD, "value" );

        assertNotNull( "returned groupedArtifacts is null", groupedArtifacts );
        assertFalse( "returned groupedArtifacts should not be empty", groupedArtifacts.isEmpty() );

        for ( ArtifactInfoGroup artifactGroup : groupedArtifacts.values() )
        {
            String[] parts = artifactGroup.getGroupKey().split( ":" );
            //1st part groupId
            //2nd part artifactId
            //3rd part version
            //4th part classifier
            assertEquals( "should be 4 parts to the group key", 4, parts.length );
            assertFalse( "each group should contain at least 1 artifact", artifactGroup.getArtifactInfos().isEmpty() );
        }
    }

    public void testSampleSearchGroupNewGrouping()
        throws Exception
    {
        app.index();

        // Search using my own grouping, which will group based upon the MY_FIELD parameter
        Map<String, ArtifactInfoGroup> groupedArtifacts = app.searchIndexGrouped(
            SampleIndexCreator.MY_FIELD,
            "value",
            new AbstractGrouping()
            {
                @Override
                protected String getGroupKey( ArtifactInfo artifactInfo )
                {
                    return artifactInfo.getAttributes().get( SampleIndexCreator.MY_FIELD );
                }
            } );

        assertNotNull( "returned groupedArtifacts is null", groupedArtifacts );
        assertEquals( "returned groupedArtifacts should have 1 entry", 1, groupedArtifacts.size() );
        assertEquals( "group key should be value", "value", groupedArtifacts.values().iterator().next().getGroupKey() );
    }

    public void testIndexPacking()
        throws Exception
    {
        app.index();

        File publishDir = new File( getBasedir(), "target/publish/");

        app.publishIndex( publishDir );

        assertTrue( publishDir.exists() );

        // Legacy index format
        assertTrue( new File( publishDir, "nexus-maven-repository-index.zip" ).exists() );
        assertTrue( new File( publishDir, "nexus-maven-repository-index.zip.sha1" ).exists() );
        assertTrue( new File( publishDir, "nexus-maven-repository-index.zip.md5" ).exists() );

        // Current index format
        assertTrue( new File( publishDir, "nexus-maven-repository-index.gz" ).exists() );
        assertTrue( new File( publishDir, "nexus-maven-repository-index.gz.sha1" ).exists() );
        assertTrue( new File( publishDir, "nexus-maven-repository-index.gz.md5" ).exists() );

        // properties file
        assertTrue( new File( publishDir, "nexus-maven-repository-index.properties" ).exists() );
        assertTrue( new File( publishDir, "nexus-maven-repository-index.properties.sha1" ).exists() );
        assertTrue( new File( publishDir, "nexus-maven-repository-index.properties.md5" ).exists() );
    }
}

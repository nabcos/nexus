package org.sonatype.nexus.test.utils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.codehaus.plexus.util.FileUtils;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.testng.AssertJUnit;

public class ErrorReportUtil
{
    public static void cleanErrorBundleDir( String directory )
        throws IOException
    {
        File errorBundleDir = new File( directory + "/error-report-bundles" );
        
        if ( errorBundleDir.exists() )
        {
            FileUtils.deleteDirectory( errorBundleDir );
        }
    }
    
    public static void validateNoZip( String directory )
    {
        File errorBundleDir = new File( directory + "/error-report-bundles" );
        
        AssertJUnit.assertFalse( errorBundleDir.exists() );
    }
    
    public static void validateZipContents( String directory )
        throws IOException
    {
        File errorBundleDir = new File( directory + "/error-report-bundles" );
        
        File[] files = errorBundleDir.listFiles();
        
        AssertJUnit.assertTrue( files != null );
        AssertJUnit.assertEquals( 1, files.length );
        AssertJUnit.assertTrue( files[0].getName().startsWith( "nexus-error-bundle." ) );
        AssertJUnit.assertTrue( files[0].getName().endsWith( ".zip" ) );
        
        validateZipContents( files[0] );
    }
    
    public static void validateZipContents( File file )
        throws IOException
    {
        boolean foundException = false;
        boolean foundFileList = false;
        boolean foundContextList = false;
        boolean foundLog4j = false;
        boolean foundNexusXml = false;
        boolean foundSecurityXml = false;
        boolean foundSecurityConfigXml = false;
        boolean foundOthers = false;

        ZipFile zipFile = new ZipFile( file );

        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

        while ( enumeration.hasMoreElements() )
        {
            ZipEntry entry = enumeration.nextElement();

            if ( entry.getName().equals( "exception.txt" ) )
            {
                foundException = true;
            }
            else if ( entry.getName().equals( "fileListing.txt" ) )
            {
                foundFileList = true;
            }
            else if ( entry.getName().equals( "contextListing.txt" ) )
            {
                foundContextList = true;
            }
            else if ( entry.getName().equals( "log4j.properties" ) )
            {
                foundLog4j = true;
            }
            else if ( entry.getName().equals( "nexus.xml" ) )
            {
                foundNexusXml = true;
            }
            else if ( entry.getName().equals( "security.xml" ) )
            {
                foundSecurityXml = true;
            }
            else if ( entry.getName().equals( "security-configuration.xml" ) )
            {
                foundSecurityConfigXml = true;
            }
            else
            {   
                String confDir = AbstractNexusIntegrationTest.WORK_CONF_DIR;
                
                // any extra plugin config goes in the zip, so if we find something from the conf dir that is ok.
                if(! new File( confDir, entry.getName()).exists())
                {
                    foundOthers = true;
                }
            }
        }

        AssertJUnit.assertTrue( foundException );
        AssertJUnit.assertTrue( foundFileList );
        AssertJUnit.assertTrue( foundContextList );
        AssertJUnit.assertTrue( foundLog4j );
        AssertJUnit.assertTrue( foundNexusXml );
        AssertJUnit.assertTrue( foundSecurityXml );
        AssertJUnit.assertTrue( foundSecurityConfigXml );
        AssertJUnit.assertFalse( foundOthers );
    }
}

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
package org.sonatype.nexus.plugin.migration.artifactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.sonatype.nexus.plugin.migration.artifactory.util.XStreamUtil;
import org.sonatype.nexus.rest.AbstractNexusPlexusResource;

import com.thoughtworks.xstream.XStream;

public abstract class AbstractArtifactoryMigrationPlexusResource
    extends AbstractNexusPlexusResource
{
    public AbstractArtifactoryMigrationPlexusResource()
    {
        super();
    }

    @Override
    public void configureXStream( XStream xstream )
    {
        super.configureXStream( xstream );

        XStreamUtil.configureMigration(xstream);
    }

    protected File validateBackupFileLocation( String fileLocation )
        throws ResourceException
    {
        File file = new File( fileLocation );

        if ( !file.exists() )
        {
            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, "Invalid File Location: "
                + file.getAbsolutePath() );
        }

        if ( file.isFile() )
        {
            ZipFile zip;
            try
            {
                zip = new ZipFile( file );
                zip.close();
            }
            catch ( ZipException e )
            {
                throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, "Invalid file format. Is not a Zip. "
                    + e.getMessage() );
            }
            catch ( IOException e )
            {
                throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, "Unable to read file. " + e.getMessage() );
            }
        }

        return file;
    }

    protected InputStream getConfigurationFile( File backup, String filename )
        throws IOException, ZipException, FileNotFoundException, ResourceException
    {
        ZipFile zipFile = null;

        if ( backup.isFile() )
        {
            zipFile = new ZipFile( backup );
        }

        final InputStream cfg;
        if ( zipFile != null )
        {
            ZipEntry cfgEntry = zipFile.getEntry( filename );
            if ( cfgEntry == null )
            {
                cfg = null;
            }
            else
            {
                cfg = zipFile.getInputStream( cfgEntry );
            }
        }
        else
        {
            File cfgFile = new File( backup, filename );
            if ( !cfgFile.exists() )
            {
                cfg = null;
            }
            else
            {
                cfg = new FileInputStream( cfgFile );
            }
        }

        if ( cfg == null )
        {
            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, "Artifactory backup is invalid, missing: '"
                + filename + "'" );
        }
        return cfg;

    }

}

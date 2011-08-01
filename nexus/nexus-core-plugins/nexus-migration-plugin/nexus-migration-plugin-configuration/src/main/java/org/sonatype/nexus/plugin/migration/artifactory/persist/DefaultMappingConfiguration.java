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
package org.sonatype.nexus.plugin.migration.artifactory.persist;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.sonatype.nexus.plugin.migration.artifactory.persist.model.CMapping;
import org.sonatype.nexus.plugin.migration.artifactory.persist.model.Configuration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

@Component( role = MappingConfiguration.class, hint = "default" )
public class DefaultMappingConfiguration
    extends AbstractLogEnabled
    implements MappingConfiguration
{

    @org.codehaus.plexus.component.annotations.Configuration( value = "${nexus-work}/conf/mapping.xml" )
    private File configurationFile;

    private ReentrantLock lock = new ReentrantLock();

    private Configuration configuration;

    private long lastModified;

    private XStream xstream;

    public void addMapping( CMapping map )
        throws IOException
    {
        lock.lock();
        try
        {
            getConfiguration().addUrlMapping( map );

            save();
        }
        finally
        {
            lock.unlock();
        }
    }

    public void save()
        throws IOException
    {
        lock.lock();

        Configuration cfg = getConfiguration();

        FileLock fileLock = null;
        FileChannel channel = null;
        FileOutputStream out = null;
        try
        {
            XStream xs = getXStream();

            if ( !configurationFile.exists() )
            {
                configurationFile.getParentFile().mkdirs();
                configurationFile.createNewFile();
            }

            out = new FileOutputStream( configurationFile );
            channel = out.getChannel();
            fileLock = channel.lock( 0, Long.MAX_VALUE, false ); // exclusive lock on save

            xs.toXML( cfg, out );
        }
        catch ( IOException e )
        {
            getLogger().error( "Unable to save mapping configuration", e );
            throw e;
        }
        finally
        {
            release( fileLock, channel, out );

            lock.unlock();
        }
    }

    private XStream getXStream()
    {
        if ( xstream == null )
        {
            xstream = new XStream();
            xstream.setClassLoader( getClass().getClassLoader() );
            xstream.processAnnotations( Configuration.class );
            xstream.processAnnotations( CMapping.class );
        }
        return xstream;
    }

    private Configuration getConfiguration()
    {
        if ( this.configuration != null && lastModified == configurationFile.lastModified() )
        {
            return this.configuration;
        }

        lock.lock();
        FileInputStream in = null;
        FileChannel channel = null;
        FileLock fileLock = null;
        try
        {
            lastModified = configurationFile.lastModified();

            in = new FileInputStream( configurationFile );
            channel = in.getChannel();
            fileLock = channel.lock( 0, Long.MAX_VALUE, true );

            XStream xs = getXStream();
            Object config = xs.fromXML( in );

            if ( config != null )
            {
                this.configuration = (Configuration) config;
            }
            else
            {
                this.configuration = new Configuration();
            }

        }
        catch ( FileNotFoundException e )
        {
            this.configuration = new Configuration();
        }
        catch ( XStreamException e )
        {
            getLogger().error( "Invalid configuration XML", e );
        }
        catch ( IOException e )
        {
            getLogger().error( "Error reading configuration", e );
        }
        finally
        {
            release( fileLock, channel, in );

            lock.unlock();
        }

        return this.configuration;
    }

    private void release( FileLock fileLock, Closeable channel, Closeable stream )
    {
        if ( fileLock != null )
        {
            try
            {
                fileLock.release();
            }
            catch ( IOException e )
            {
                // just releasing lock
            }
        }

        if ( channel != null )
        {
            try
            {
                channel.close();
            }
            catch ( IOException e )
            {
                // just closing channel
            }
        }

        if ( stream != null )
        {
            try
            {
                stream.close();
            }
            catch ( IOException e )
            {
                // just closing file
            }
        }
    }

    public CMapping getMapping( String repositoryId )
    {
        if ( repositoryId == null )
        {
            return null;
        }

        List<CMapping> urls = getConfiguration().getUrlsMapping();
        for ( CMapping mapping : urls )
        {
            if ( repositoryId.equals( mapping.getArtifactoryRepositoryId() ) )
            {
                return mapping;
            }
        }

        return null;
    }

    public String getNexusContext()
    {
        return getConfiguration().getNexusContext();
    }

    public void setNexusContext( String nexusContext )
        throws IOException
    {
        lock.lock();
        try
        {
            getConfiguration().setNexusContext( nexusContext );

            save();
        }
        finally
        {
            lock.unlock();
        }
    }

    public List<CMapping> listMappings()
    {
        return getConfiguration().getUrlsMapping();
    }

}

package org.sonatype.nexus.test.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.sonatype.appbooter.DefaultForkedAppBooter;
import org.sonatype.appbooter.ForkedAppBooter;
import org.sonatype.appbooter.ctl.ControllerClient;
import org.sonatype.nexus.test.utils.NexusIllegalStateException;
import org.sonatype.nexus.test.utils.NexusStatusUtil;
import org.sonatype.nexus.test.utils.TestProperties;

public class NexusInstancesPool
{

    private static final List<NexusContext> contexts = new ArrayList<NexusContext>();

    private PlexusContainer container;

    public NexusInstancesPool( PlexusContainer container )
    {
        this.container = container;
    }

    private void addChild( PlexusConfiguration cfg, String name, String value )
    {
        PlexusConfiguration child = cfg.getChild( name );
        if ( child != null )
        {
            child.setValue( value );
        }
        else
        {
            cfg.addChild( name, value );
        }
    }

    private void sleep( int i )
    {
        try
        {
            Thread.sleep( i );
        }
        catch ( InterruptedException e )
        {
            // ok
        }
    }

    private Integer getRandomPort()
        throws IOException
    {
        ServerSocket ss = new ServerSocket( 0 );
        try
        {
            return ss.getLocalPort();
        }
        finally
        {
            try
            {
                ss.close();
            }
            catch ( IOException e )
            {
                // no problem
            }
        }
    }

    public NexusContext borrowObject()
        throws Exception, NoSuchElementException, IllegalStateException
    {
        System.out.println( "=========================================================================" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=  make object                                                          =" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=========================================================================" );

        Integer nexusPort = getRandomPort();
        Integer controllerPort = getRandomPort();
        String nexusWorkDir = TestProperties.getPath( "nexus.work.dir" ) + nexusPort;
        String nexusBaseDir = TestProperties.getPath( "nexus.base.dir" );
        String nexusApp = nexusBaseDir + "/runtime/apps/nexus";

        Properties plexusProps = new Properties();
        plexusProps.setProperty( "application-port", nexusPort.toString() );
        plexusProps.setProperty( "application-host", "0.0.0.0" );
        plexusProps.setProperty( "runtime", nexusBaseDir + "/runtime" );
        plexusProps.setProperty( "apps", nexusBaseDir + "/runtime/apps" );
        plexusProps.setProperty( "nexus-work", nexusWorkDir );
        plexusProps.setProperty( "nexus-app", nexusApp );
        plexusProps.setProperty( "webapp", nexusBaseDir + "/runtime/apps/nexus/webapp" );
        plexusProps.setProperty( "webapp-context-path", "/nexus" );
        plexusProps.setProperty( "application-conf", nexusWorkDir + "/conf" );
        plexusProps.setProperty( "log4j-prop-file", TestProperties.getPath( "default-configs" ) + "/log4j.properties" );
        plexusProps.setProperty( "jetty.xml", nexusBaseDir + "/conf/jetty.xml" );
        plexusProps.setProperty( "index.template.file", "templates/index-debug.vm" );
        plexusProps.setProperty( "security-xml-file", nexusWorkDir + "/conf/security.xml" );
        File containerProperties = new File( nexusWorkDir, "plexus.properties" );
        containerProperties.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream( containerProperties );
        plexusProps.store( out, "Generated by NexusInstancesPool" );
        out.close();

        String forkedAppBooterHint = "TestForkedAppBooter" + nexusPort;
        ForkedAppBooter appBooter;
        synchronized ( container )
        {
            ComponentDescriptor<ForkedAppBooter> baseComp =
                container.getComponentDescriptor( ForkedAppBooter.class, ForkedAppBooter.class.getName(),
                                                  "DefaultForkedAppBooter" );

            PlexusConfiguration cfg = baseComp.getConfiguration();
            addChild( cfg, "disable-blocking", Boolean.TRUE.toString() );
            addChild( cfg, "debug", Boolean.FALSE.toString() );
            addChild( cfg, "java-cmd", "java" );
            addChild( cfg, "debug-port", TestProperties.getInteger( "debug-port", 5006 ).toString() );
            addChild( cfg, "debug-suspend", Boolean.TRUE.toString() );
            addChild(
                      cfg,
                      "debug-java-cmd",
                      "java -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=@DEBUG_SUSPEND@,address=@DEBUG_PORT@ -Djava.compiler=NONE" );
            addChild( cfg, "launcher-class", "org.sonatype.appbooter.PlexusContainerHost" );
            addChild( cfg, "configuration", nexusBaseDir + "/conf/plexus.xml" );
            addChild( cfg, "basedir", nexusBaseDir );
            addChild( cfg, "temp-dir", TestProperties.getPath( "project.build.directory" ) + "/appbooter.tmp."
                + nexusPort );
            addChild( cfg, "classworldsJar", nexusBaseDir + "/lib/plexus-classworlds-1.4.jar" );
            addChild( cfg, "classworldsConf", nexusBaseDir + "/conf/classworlds.conf" );
            addChild( cfg, "class-path-elements", nexusApp + "/conf, " + nexusApp + "/lib/*.jar" );
            addChild( cfg, "sleep-after-start", "5000" );
            addChild( cfg, "control-port", controllerPort.toString() );
            addChild( cfg, "container-properties", containerProperties.getAbsolutePath() );

            ComponentDescriptor<StreamConsumer> consumer = new ComponentDescriptor<StreamConsumer>();
            consumer.setRoleClass( StreamConsumer.class );
            consumer.setRoleHint( "FileConsumer" + nexusPort );
            consumer.setImplementationClass( FileStreamConsumer.class );
            XmlPlexusConfiguration consumeCfg = new XmlPlexusConfiguration();
            consumeCfg.addChild( "destination", nexusWorkDir + "/nexus.log" );
            consumer.setConfiguration( consumeCfg );
            container.addComponentDescriptor( consumer );

            ComponentDescriptor<ForkedAppBooter> comp = new ComponentDescriptor<ForkedAppBooter>();
            BeanUtils.copyProperties( comp, baseComp );
            comp.setRoleClass( ForkedAppBooter.class );
            comp.setRoleHint( forkedAppBooterHint );
            comp.setImplementationClass( DefaultForkedAppBooter.class );

            ComponentRequirement requirement = new ComponentRequirement();
            requirement.setFieldName( "streamConsumer" );
            requirement.setRole( StreamConsumer.class.getName() );
            requirement.setRoleHint( "FileConsumer" + nexusPort );
            comp.addRequirement( requirement );

            container.addComponentDescriptor( comp );

            appBooter = container.lookup( ForkedAppBooter.class, forkedAppBooterHint );
        }

        appBooter.start();
        NexusContext context = new NexusContext( appBooter, nexusPort, new File( nexusWorkDir ) );

        ForkedAppBooter forkedAppBooter = context.getForkedAppBooter();

        ControllerClient client = forkedAppBooter.getControllerClient();
        for ( int i = 0; i < 50; i++ )
        {
            if ( client.ping() )
            {
                break;
            }
            sleep( 200 );
        }

        if ( !NexusStatusUtil.getNexusStatus( context.getPort() ).getData().getState().equals( "STARTED" ) )
        {
            throw new NexusIllegalStateException( "Failed to start nexus" );
        }

        synchronized ( contexts )
        {
            contexts.add( context );
        }

        return context;
    }

    public void close()
        throws Exception
    {
        synchronized ( contexts )
        {
            Iterator<NexusContext> it = contexts.iterator();
            while ( it.hasNext() )
            {
                NexusContext context = it.next();
                try
                {
                    returnObject( context );
                }
                catch ( Throwable t )
                {
                    t.printStackTrace();
                }
                finally
                {
                    it.remove();
                }
            }
        }
    }

    public void returnObject( NexusContext context )
        throws Exception
    {
        System.out.println( "=========================================================================" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=  destroy object                                                       =" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=========================================================================" );

        synchronized ( contexts )
        {
            contexts.remove( context );
        }

        ForkedAppBooter appBooter = context.getForkedAppBooter();
        appBooter.shutdown();
        context.release();

        synchronized ( container )
        {
            container.release( appBooter );
        }
    }

}

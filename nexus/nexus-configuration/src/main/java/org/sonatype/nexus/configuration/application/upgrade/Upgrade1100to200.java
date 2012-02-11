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
package org.sonatype.nexus.configuration.application.upgrade;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.configuration.upgrade.ConfigurationIsCorruptedException;
import org.sonatype.configuration.upgrade.SingleVersionUpgrader;
import org.sonatype.configuration.upgrade.UpgradeMessage;
import org.sonatype.nexus.configuration.model.CRepository;
import org.sonatype.nexus.configuration.model.v2_0_0.upgrade.BasicVersionUpgrade;

/**
 * Upgrades configuration model from version 1.10.0 to 2.0.<BR>
 * 
 * @author velo
 */
@Component( role = SingleVersionUpgrader.class, hint = "1.10.0" )
public class Upgrade1100to200
    extends AbstractLogEnabled
    implements SingleVersionUpgrader
{

    public Object loadConfiguration( File file )
        throws IOException, ConfigurationIsCorruptedException
    {
        FileReader fr = null;

        org.sonatype.nexus.configuration.model.v1_10_0.Configuration conf = null;

        try
        {
            // reading without interpolation to preserve user settings as variables
            fr = new FileReader( file );

            org.sonatype.nexus.configuration.model.v1_10_0.io.xpp3.NexusConfigurationXpp3Reader reader =
                new org.sonatype.nexus.configuration.model.v1_10_0.io.xpp3.NexusConfigurationXpp3Reader();

            conf = reader.read( fr );
        }
        catch ( XmlPullParserException e )
        {
            throw new ConfigurationIsCorruptedException( file.getAbsolutePath(), e );
        }
        finally
        {
            if ( fr != null )
            {
                fr.close();
            }
        }

        return conf;
    }

    public void upgrade( UpgradeMessage message )
        throws ConfigurationIsCorruptedException
    {
        org.sonatype.nexus.configuration.model.v1_10_0.Configuration oldc =
            (org.sonatype.nexus.configuration.model.v1_10_0.Configuration) message.getConfiguration();

        BasicVersionUpgrade versionConverter = new BasicVersionUpgrade()
        {
            @Override
            public CRepository upgradeCRepository( org.sonatype.nexus.configuration.model.v1_10_0.CRepository cRepository )
            {
                CRepository newRepo = super.upgradeCRepository( cRepository );

                // shut down NFC on any non-proxy repository
                final boolean isProxyRepository =
                    cRepository.isNotFoundCacheActive() &&
                    newRepo.getRemoteStorage() != null &&
                    newRepo.getRemoteStorage().getUrl() != null;
                newRepo.setNotFoundCacheActive( isProxyRepository );

                return newRepo;
            }
        };

        org.sonatype.nexus.configuration.model.Configuration newc = versionConverter.upgradeConfiguration( oldc );

        newc.setVersion( org.sonatype.nexus.configuration.model.Configuration.MODEL_VERSION );
        message.setModelVersion( org.sonatype.nexus.configuration.model.Configuration.MODEL_VERSION );
        message.setConfiguration( newc );
    }
}

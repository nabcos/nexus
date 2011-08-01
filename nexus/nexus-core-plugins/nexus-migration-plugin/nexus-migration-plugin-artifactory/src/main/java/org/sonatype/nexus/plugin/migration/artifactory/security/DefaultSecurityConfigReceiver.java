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
package org.sonatype.nexus.plugin.migration.artifactory.security;

import java.util.LinkedHashSet;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.configuration.ConfigurationException;
import org.sonatype.configuration.validation.InvalidConfigurationException;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.nexus.configuration.model.CRepositoryTarget;
import org.sonatype.nexus.plugin.migration.artifactory.ArtifactoryMigrationException;
import org.sonatype.nexus.proxy.registry.ContentClass;
import org.sonatype.nexus.proxy.registry.RepositoryTypeRegistry;
import org.sonatype.nexus.proxy.target.Target;
import org.sonatype.nexus.proxy.target.TargetRegistry;
import org.sonatype.security.model.CPrivilege;
import org.sonatype.security.model.CRole;
import org.sonatype.security.model.CUser;
import org.sonatype.security.model.CUserRoleMapping;
import org.sonatype.security.realms.tools.ConfigurationManager;

@Component( role = SecurityConfigReceiver.class )
public class DefaultSecurityConfigReceiver
    implements SecurityConfigReceiver
{

    @Requirement
    private TargetRegistry targetRegistry;

    @Requirement
    private NexusConfiguration nexusConfiguration;

    @Requirement
    private RepositoryTypeRegistry repositoryTypeRegistry;

    @Requirement( role = ConfigurationManager.class, hint = "resourceMerging" )
    private ConfigurationManager manager;

    public void receiveRepositoryTarget( CRepositoryTarget repoTarget )
        throws ArtifactoryMigrationException
    {
        try
        {
            ContentClass cc = repositoryTypeRegistry.getContentClasses().get( repoTarget.getContentClass() );

            if ( cc == null )
            {
                throw new ConfigurationException( "Content class with ID=\"" + repoTarget.getContentClass()
                    + "\" does not exists!" );
            }

            Target target = new Target( repoTarget.getId(), repoTarget.getName(), cc, repoTarget.getPatterns() );
            targetRegistry.addRepositoryTarget( target );
            nexusConfiguration.saveConfiguration();
        }
        catch ( Exception e )
        {
            throw new ArtifactoryMigrationException( "Cannot create repository target with id " + repoTarget.getId(), e );
        }
    }

    public void receiveSecurityPrivilege( CPrivilege privilege )
        throws ArtifactoryMigrationException
    {
        try
        {
            // nexusSecurity.createPrivilege( privilege );

            manager.createPrivilege( privilege );

            manager.save();
        }
        catch ( InvalidConfigurationException e )
        {
            throw new ArtifactoryMigrationException( "Cannot create privilege with name " + privilege.getName(), e );
        }

    }

    public void receiveSecurityRole( CRole role )
        throws ArtifactoryMigrationException
    {
        try
        {
            manager.createRole( role );

            manager.save();
        }
        catch ( InvalidConfigurationException e )
        {
            throw new ArtifactoryMigrationException( "Cannot create role with id " + role.getId(), e );
        }

    }

    public void receiveSecurityUser( CUser user, CUserRoleMapping map )
        throws ArtifactoryMigrationException
    {
        try
        {
            manager.createUser( user, new LinkedHashSet<String>( map.getRoles() ) );

            manager.save();
        }
        catch ( InvalidConfigurationException e )
        {
            throw new ArtifactoryMigrationException( "Cannot create user with id " + user.getId(), e );
        }

    }

}

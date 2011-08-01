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
package org.sonatype.nexus.plugin.migration.artifactory.security.builder;

import java.util.Set;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.nexus.plugin.migration.artifactory.security.ArtifactoryAcl;
import org.sonatype.nexus.plugin.migration.artifactory.security.ArtifactoryGroup;
import org.sonatype.nexus.plugin.migration.artifactory.security.ArtifactoryPermission;
import org.sonatype.nexus.plugin.migration.artifactory.security.ArtifactoryPermissionTarget;
import org.sonatype.nexus.plugin.migration.artifactory.security.ArtifactorySecurityConfig;
import org.sonatype.nexus.plugin.migration.artifactory.security.ArtifactoryUser;
import org.sonatype.nexus.plugin.migration.artifactory.util.DomUtil;
import org.sonatype.nexus.plugin.migration.artifactory.util.PatternConvertor;

public class SecurityConfig130Parser
    extends AbstractSecurityConfigParser
{

    public SecurityConfig130Parser( Xpp3Dom dom, ArtifactorySecurityConfig config )
    {
        super( dom, config );
    }

    @Override
    protected void parseAcls()
    {
        Xpp3Dom aclsDom = getDom().getChild( "acls" );

        if ( aclsDom.getChildCount() == 0 )
        {
            return;
        }

        for ( Xpp3Dom aclDom : aclsDom.getChildren() )
        {

            String permTargetName = aclDom.getChild( "permissionTarget" ).getChild( "name" ).getValue();

            ArtifactoryPermissionTarget permTarget = getConfig().getPermissionTarget( permTargetName );

            Xpp3Dom aces = aclDom.getChild( "aces" );

            if ( aces.getChildCount() == 0 )
            {
                continue;
            }

            for ( Xpp3Dom ace : aces.getChildren() )
            {
                String mask = ace.getChild( "mask" ).getValue();

                if ( mask.equals( "0" ) )
                {
                    continue;
                }

                String principal = ace.getChild( "principal" ).getValue();

                boolean isGroup = ace.getChild( "group" ).getValue().equals( "true" );

                ArtifactoryAcl acl;

                if ( isGroup )
                {
                    ArtifactoryGroup group = getConfig().getGroupByName( principal );

                    acl = new ArtifactoryAcl( permTarget, group );
                }
                else
                {
                    ArtifactoryUser user = getConfig().getUserByUsername( principal );

                    acl = new ArtifactoryAcl( permTarget, user );
                }

                Set<ArtifactoryPermission> permissions = ArtifactoryPermission.buildPermission130( Integer
                    .parseInt( mask ) );

                acl.getPermissions().addAll( permissions );

                getConfig().addAcl( acl );
            }

        }

    }

    @Override
    protected void parsePermissionTargets()
    {
        Xpp3Dom aclsDom = getDom().getChild( "acls" );

        if ( aclsDom.getChildCount() == 0 )
        {
            return;
        }

        for ( Xpp3Dom acl : aclsDom.getChildren() )
        {
            Xpp3Dom targetDom = acl.getChild( "permissionTarget" );

            String name = targetDom.getChild( "name" ).getValue();

            ArtifactoryPermissionTarget target = new ArtifactoryPermissionTarget( name );

            // Artifactory 1.3.0 - 2.0.x
            if ( targetDom.getChild( "repoKey" ) != null )
            {
                String repoKey = targetDom.getChild( "repoKey" ).getValue();

                target.addRepoKey( repoKey );
            }
            // Artifactory 2.1.x
            else if ( targetDom.getChild( "repoKeys" ) != null )
            {
                for ( Xpp3Dom repoKeyDom : targetDom.getChild( "repoKeys" ).getChildren() )
                {
                    target.addRepoKey( repoKeyDom.getValue() );
                }
            }

            Xpp3Dom includes = targetDom.getChild( "includes" );

            if ( includes.getChildCount() > 0 )
            {
                for ( Xpp3Dom include : includes.getChildren() )
                {
                    target.addInclude( PatternConvertor.convertAntStylePattern( include.getValue() ) );
                }
            }

            getConfig().addPermissionTarget( target );
        }

    }

    @Override
    public void parseUsers()
    {
        Xpp3Dom usersDom = getDom().getChild( "users" );

        for ( Xpp3Dom userDom : usersDom.getChildren() )
        {
            String username = userDom.getChild( "username" ).getValue();

            String password = userDom.getChild( "password" ).getValue();

            ArtifactoryUser user = new ArtifactoryUser( username, password );

            if ( userDom.getChild( "admin" ) != null && userDom.getChild( "admin" ).getValue().equals( "true" ) )
            {
                user.setAdmin( true );
            }

            if ( userDom.getChild( "email" ) != null )
            {
                user.setEmail( userDom.getChild( "email" ).getValue() );
            }

            Xpp3Dom groupsDom = userDom.getChild( "groups" );

            if ( groupsDom != null && groupsDom.getChildCount() != 0 )
            {
                for ( Xpp3Dom groupDom : groupsDom.getChildren() )
                {
                    user.getGroups().add( getConfig().getGroupByName( groupDom.getValue() ) );
                }
            }

            getConfig().addUser( user );
        }
    }

    @Override
    protected void parseGroups()
    {
        Xpp3Dom groupsDom = getDom().getChild( "groups" );

        if ( groupsDom.getChildCount() == 0 )
        {
            return;
        }

        for ( Xpp3Dom groupDom : groupsDom.getChildren() )
        {
            String name = DomUtil.getValue( groupDom, "groupName" );

            String description = DomUtil.getValue( groupDom, "description" );

            getConfig().addGroup( new ArtifactoryGroup( name, description ) );
        }
    }

}

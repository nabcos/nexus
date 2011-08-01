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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.logging.Logger;
import org.sonatype.nexus.plugin.migration.artifactory.dto.MigrationSummaryDTO;

public class MigrationResult
{
    private final Logger logger;

    private final MigrationSummaryDTO migrationSummary;

    private final ArrayList<String> errorMessages = new ArrayList<String>();

    private final ArrayList<String> warningMessages = new ArrayList<String>();

    private final HashSet<String> migratedRepositoryIds = new HashSet<String>();

    private boolean successful = false;

    public MigrationResult( Logger logger, MigrationSummaryDTO migrationSummary )
    {
        this.logger = logger;

        this.migrationSummary = migrationSummary;
    }

    protected Logger getLogger()
    {
        return logger;
    }

    public String getId()
    {
        return migrationSummary.getId();
    }

    public MigrationSummaryDTO getMigrationSummary()
    {
        return migrationSummary;
    }

    public List<String> getErrorMessages()
    {
        return errorMessages;
    }

    public List<String> getWarningMessages()
    {
        return warningMessages;
    }

    public void addErrorMessage( String message )
    {
        addErrorMessage( message, null );
    }

    public void addErrorMessage( String message, Throwable cause )
    {
        this.errorMessages.add( message );

        getLogger().error( message, cause );
    }

    public void addWarningMessage( String message )
    {
        addWarningMessage( message, null );
    }

    public void addWarningMessage( String message, Throwable cause )
    {
        this.warningMessages.add( message );

        getLogger().warn( message, cause );
    }

    public void addInfoMessage( String message )
    {
        addInfoMessage( message, null );
    }

    public void addInfoMessage( String message, Throwable cause )
    {
        if ( getLogger().isInfoEnabled() )
        {
            getLogger().info( message, cause );
        }
    }

    public void addDebugMessage( String message )
    {
        addDebugMessage( message, null );
    }

    public void addDebugMessage( String message, Throwable cause )
    {
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( message, cause );
        }
    }

    public void mergeResult( MigrationResult migrationResult )
    {
        this.errorMessages.addAll( migrationResult.getErrorMessages() );

        this.warningMessages.addAll( migrationResult.getWarningMessages() );
    }

    public boolean isSuccessful()
    {
        return successful;
    }

    public void setSuccessful( boolean successful )
    {
        this.successful = successful;
    }

    public Set<String> getMigratedRepositoryIds()
    {
        return migratedRepositoryIds;
    }

    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();

        if ( this.errorMessages.size() > 0 )
        {
            sw.append( "\nMigration errors follows:\n" );

            for ( String error : this.errorMessages )
            {
                sw.append( error ).append( "\n" );
            }
        }

        if ( this.warningMessages.size() > 0 )
        {
            sw.append( "\nMigration warnings follows:\n" );

            for ( String warning : this.warningMessages )
            {
                sw.append( warning ).append( "\n" );
            }
        }

        return sw.toString();
    }

    public void clear()
    {
        this.errorMessages.clear();

        this.warningMessages.clear();
    }
}

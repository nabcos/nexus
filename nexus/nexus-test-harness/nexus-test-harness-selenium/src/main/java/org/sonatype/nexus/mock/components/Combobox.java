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
package org.sonatype.nexus.mock.components;

import com.thoughtworks.selenium.Selenium;

public class Combobox
    extends TextField
{

    public Combobox( Component parent, String expression )
    {
        super( parent, expression );
    }

    public Combobox( Selenium selenium, String expression )
    {
        super( selenium, expression );
    }

    public void click()
    {
        selenium.click( getXPath() );
    }

    public void setValue( String value )
    {
        /*
         * var cb =
         * window.Ext.getCmp('security-privileges').cardPanel.getLayout().activeItem.getLayout().activeItem.find('name',
         * 'repositoryOrGroup')[0]; var value = 'repo_central'; cb.setValue( value ); cb.fireEvent('select', cb,
         * cb.store.getById(value), cb.store.indexOfId(value));
         * window.Ext.getCmp('security-privileges').cardPanel.getLayout().activeItem.getLayout().activeItem.find('name',
         * 'repositoryTargetId')[0].store.getCount();
         */
        focus();
        evalTrue( ".setValue( '" + value + "' )" );
        runScript( ".fireEvent( 'select', " + expression + ", " + expression + ".store.getById('" + value + "'), "
            + expression + ".store.indexOfId('" + value + "') )" );
        blur();
    }

    public void select( int i )
    {
        // workaround to select an item on combobox
        /*
         * var cb =
         * window.Ext.getCmp('security-privileges').cardPanel.getLayout().activeItem.getLayout().activeItem.find('name',
         * 'repositoryOrGroup')[0]; var i = 0; cb.setValue( cb.store.getAt(i).id ); cb.fireEvent('select', cb,
         * cb.store.getAt(i), i);
         * window.Ext.getCmp('security-privileges').cardPanel.getLayout().activeItem.getLayout().activeItem.find('name',
         * 'repositoryTargetId')[0].store.getCount();
         */
        focus();
        runScript( ".setValue(" + expression + ".store.getAt(" + i + ").id )" );
        runScript( ".fireEvent( 'select', " + expression + ", " + expression + ".store.getAt(" + i + "), " + i + " )" );
        blur();
    }

    public Integer getCount()
    {
        String eval = getEval( ".store.getCount()" );
        if ( eval == null || "null".equals( eval ) )
        {
            return null;
        }

        return new Integer( eval );
    }

}

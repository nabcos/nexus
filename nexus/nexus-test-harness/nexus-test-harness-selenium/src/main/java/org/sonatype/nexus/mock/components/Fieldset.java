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

public class Fieldset
    extends Component
{

    private Checkbox checkbox;

    public Fieldset( Selenium selenium, String expression )
    {
        super( selenium, expression );

        checkbox = new Checkbox(selenium, expression+".checkbox");
        checkbox.idFunction=".id";
    }

    public Fieldset check( boolean enable )
    {
        checkbox.check( enable );

        return this;
    }

}

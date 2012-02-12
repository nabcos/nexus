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
package org.sonatype.nexus.error.reporting;

import org.apache.commons.httpclient.HttpClient;
import org.slf4j.Logger;
import org.sonatype.nexus.proxy.storage.remote.RemoteStorageContext;
import org.sonatype.nexus.proxy.storage.remote.commonshttpclient.HttpClientProxyUtil;

/**
 * FIXME totally wrong package, is also used by lvo plugin, but not by PR anymore
 */
public class NexusProxyServerConfigurator
{
    private Logger logger;
    private RemoteStorageContext ctx;
    
    public NexusProxyServerConfigurator( RemoteStorageContext ctx, Logger logger )
    {
        this.ctx = ctx;
        this.logger = logger;
    }
    
    public void applyToClient( HttpClient client )
    {   
        HttpClientProxyUtil.applyProxyToHttpClient( client, ctx, logger );
    }
}

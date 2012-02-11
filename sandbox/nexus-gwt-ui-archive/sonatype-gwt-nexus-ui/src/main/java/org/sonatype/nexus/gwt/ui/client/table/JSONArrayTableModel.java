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
package org.sonatype.nexus.gwt.ui.client.table;

import org.sonatype.nexus.gwt.ui.client.JSONUtil;
import org.sonatype.nexus.gwt.ui.client.data.DataStore;
import org.sonatype.nexus.gwt.ui.client.data.DataStoreListener;
import org.sonatype.nexus.gwt.ui.client.data.JSONArrayDataStore;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 *
 * @author barath
 */
public class JSONArrayTableModel extends AbstractTableModel implements DataStoreListener {
    
    private String[] props;
    
    private JSONArrayDataStore dataStore;
    
    public JSONArrayTableModel(JSONArrayDataStore dataStore, String[] props) {
        if (props == null) {
            throw new NullPointerException("props is null");
        }
        if (dataStore == null) {
            throw new NullPointerException("dataStore is null");
        }
        
        this.props = props;
        this.dataStore = dataStore;
        
        dataStore.addDataStoreListener(this);
    }
    
    public int getColumnCount() {
        return props.length;
    }

    public Object getRow(int rowIndex) {
        return dataStore.getElements().get(rowIndex);
    }

    public int getRowCount() {
        return dataStore.getElements().size();
    }
    
    public Object getCell(int rowIndex, int colIndex) {
        JSONObject obj = (JSONObject) getRow(rowIndex);
        
        JSONValue val = JSONUtil.getValue(obj, props[colIndex]);
        
        if (val != null) {
            if (val.isString() != null) {
                return val.isString().stringValue();
            } else if (val.isBoolean() != null) {
                return Boolean.valueOf(val.isBoolean().booleanValue());
            } else if (val.isNumber() != null) {
                return new Double(val.isNumber().doubleValue());
            } else if (val.isNull() != null) {
                return null;
            } else {
                return val;
            }
        }
        
        return null;
    }

    public void refreshed(DataStore sender) {
        fireTableModelListeners();
    }
    
}

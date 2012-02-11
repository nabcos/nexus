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
package org.sonatype.nexus.ext.gwt.ui.client.reposerver.model;

import org.sonatype.nexus.ext.gwt.ui.client.data.Entity;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class AuthenticationClientPermissions extends BaseModelData implements Entity {

    public String getType() {
        return "org.sonatype.nexus.rest.model.AuthenticationClientPermissions";
    }

    public Class getFieldType(String fieldName) {
        return Integer.class;
    }

    public Entity createEntity(String fieldName) {
        return null;
    }

    public Integer getViewSearch() {
        return get("viewSearch");
    }

    public void setViewSearch(Integer viewSearch) {
        set("viewSearch", viewSearch);
    }

    public Integer getViewUpdatedArtifacts() {
        return get("viewUpdatedArtifacts");
    }

    public void setViewUpdatedArtifacts(Integer viewUpdatedArtifacts) {
        set("viewUpdatedArtifacts", viewUpdatedArtifacts);
    }

    public Integer getViewCachedArtifacts() {
        return get("viewCachedArtifacts");
    }

    public void setViewCachedArtifacts(Integer viewCachedArtifacts) {
        set("viewCachedArtifacts", viewCachedArtifacts);
    }

    public Integer getViewDeployedArtifacts() {
        return get("viewDeployedArtifacts");
    }

    public void setViewDeployedArtifacts(Integer viewDeployedArtifacts) {
        set("viewDeployedArtifacts", viewDeployedArtifacts);
    }

    public Integer getViewSystemChanges() {
        return get("viewSystemChanges");
    }

    public void setViewSystemChanges(Integer viewSystemChanges) {
        set("viewSystemChanges", viewSystemChanges);
    }

    public Integer getMaintRepos() {
        return get("maintRepos");
    }

    public void setMaintRepos(Integer maintRepos) {
        set("maintRepos", maintRepos);
    }

    public Integer getMaintLogs() {
        return get("maintLogs");
    }

    public void setMaintLogs(Integer maintLogs) {
        set("maintLogs", maintLogs);
    }

    public Integer getMaintConfig() {
        return get("maintConfig");
    }

    public void setMaintConfig(Integer maintConfig) {
        set("maintConfig", maintConfig);
    }

    public Integer getConfigServer() {
        return get("configServer");
    }

    public void setConfigServer(Integer configServer) {
        set("configServer", configServer);
    }

    public Integer getConfigGroups() {
        return get("configGroups");
    }

    public void setConfigGroups(Integer configGroups) {
        set("configGroups", configGroups);
    }

    public Integer getConfigRules() {
        return get("configRules");
    }

    public void setConfigRules(Integer configRules) {
        set("configRules", configRules);
    }

    public Integer getConfigRepos() {
        return get("configRepos");
    }

    public void setConfigRepos(Integer configRepos) {
        set("configRepos", configRepos);
    }

    static public AuthenticationClientPermissions getAnonymousUserPermissions() {
        AuthenticationClientPermissions permissions
                = new AuthenticationClientPermissions();

        permissions.setViewSearch(Permissions.READ);
        permissions.setViewUpdatedArtifacts(Permissions.NONE);
        permissions.setViewCachedArtifacts(Permissions.READ);
        permissions.setViewDeployedArtifacts(Permissions.READ);
        permissions.setViewSystemChanges(Permissions.READ);
        permissions.setMaintRepos(Permissions.READ);
        permissions.setMaintLogs(Permissions.NONE);
        permissions.setMaintConfig(Permissions.NONE);
        permissions.setConfigServer(Permissions.NONE);
        permissions.setConfigGroups(Permissions.NONE);
        permissions.setConfigRules(Permissions.NONE);
        permissions.setConfigRepos(Permissions.NONE);

        return permissions;
    }

    public interface Permissions {

        static public Integer NONE = 0;
        static public Integer READ = 1;
        static public Integer EDIT = 2;
        static public Integer DELETE = 4;

    }

}

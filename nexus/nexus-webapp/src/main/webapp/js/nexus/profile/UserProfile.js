/*
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

/**
 * The user profile tab.
 *
 * @constructor
 */
Nexus.profile.UserProfile = function(config) {
  // this may happen on opening a bookmark
  if (!Sonatype.user.curr || !Sonatype.user.curr.isLoggedIn) {
    Sonatype.repoServer.RepoServer.loginHandler();
    return
  }

  var config = config || {};
  var defaultConfig = {
    autoScroll : true,
    minWidth : 270,
    layout : 'absolute'
  };
  Ext.apply(this, config, defaultConfig);

  var views = [];

  Sonatype.Events.fireEvent('userProfileInit', views);

  this.content = new Nexus.profile.UserProfile.Content({
    cls : 'user-profile-dynamic-content',
    border : false,
    x : 20,
    y : 20,
    anchor : '-20 -20'
  });

  this.selector = new Ext.form.ComboBox({
    id: 'user-profile-selector',
    x : 30,
    y : 11,
    editable : false,
    triggerAction : 'all',
    listeners : {
      'select' : {
        fn : function(combo, record, index) {
          this.bookmark = record.get('text');
          this.content.display(record.get('value'), this);
          Sonatype.utils.updateHistory(this);
        },
        scope : this
      },
      'render' : {
        fn : function(combo) {
          if ( this.bookmark !== null ) {
            return;
          }
          var rec = combo.store.getAt(0);
          this.bookmark = rec.get('text');

          combo.setValue(rec.get('text'));
          this.content.display(rec.get('value'), this);
        },
        scope : this
      }
    },
    store : (function() {
      // [ (v.item,v.name) for v in views]
      var viewArray = [];
      Ext.each(views, function(v) {
        var content = new v.item({username : Sonatype.user.curr.username, border : false, frame : false});
        if (!content.shouldShow || content.shouldShow()) {
          viewArray.push([content, v.name])
        }
      });
      return viewArray;
    })()
  });

  this.refreshContent = function() {
    var tab = this.content.getActiveTab();
    if (tab.refreshContent) {
      tab.refreshContent();
      this.content.doLayout();
    }
  }

  this.refreshButton = new Ext.Button({
    tooltip : 'Refresh',
    style : 'position: absolute; right:25px; top:25px;',
    icon : Sonatype.config.resourcePath + '/images/icons/arrow_refresh.png',
    cls : 'x-btn-icon',
    scope : this,
    handler : this.refreshContent,
    noExtraClass : true
  });

  Nexus.profile.UserProfile.superclass.constructor.call(this, {
    title : 'Profile',
    items : [
      this.content,
      this.selector,
      this.refreshButton
    ]
  });

  this.bookmark = null;

  this.getBookmark = function() {
    return this.bookmark;
  }

  this.applyBookmark = function(token) {
    var rec;
    var combo = this.selector;

    token = decodeURIComponent(token);

    var idx = combo.store.find('text', token);

    if (idx != -1) {
      this.bookmark = token;
      rec = combo.store.getAt(idx);
      combo.setValue(rec.get('text'));
      this.content.display(rec.get('value'), this);
    }
  }

}
Ext.extend(Nexus.profile.UserProfile, Ext.Panel);

/**
 * The inner content panel of the user profile tab.
 * This is what changes on combo box selection.
 *
 * @constructor
 */
Nexus.profile.UserProfile.Content = function(config)
{
  Ext.apply(this, config || {}, {
    plain : true,
    autoScroll : true,
    border : true,
    layoutOnTabChange: true,
    listeners : {
      'tabchange' : function() {
        // hide tabStrip
        if (!this.headerAlreadyHidden) {
          this.header.hide();
          this.headerAlreadyHidden = true;
        }
      },
      scope : this
    }
  });

  Nexus.profile.UserProfile.Content.superclass.constructor.call(this);

  this.display = function(panel, profile)
  {
    this.add(panel);
    this.setActiveTab(panel);
    profile.refreshButton.setVisible(panel.refreshContent !== undefined);
  }
};
Ext.extend(Nexus.profile.UserProfile.Content, Ext.TabPanel);

/**
 * Weirdo hack to get bookmarking to work. navigation-* is usually the link in the left nav panel,
 * but the user profile menu does not have that.
 */
Nexus.profile.OpenAction = function(config) {
  config.initialConfigNavigation = {
    tabId : 'profile',
    tabCode : Nexus.profile.UserProfile,
    title : 'Profile'
  };
  Nexus.profile.OpenAction.superclass.constructor.call(this, config);
};
Ext.extend(Nexus.profile.OpenAction, Ext.Panel);

Nexus.profile.OpenAction.registered = new Nexus.profile.OpenAction({
  id : 'navigation-profile'
});

/**
 * @param {string} name The name displayed in the combo box selector.
 * @param {Object} panelCls The class definition of the panel to show as content. The constructor will be called with {username:$currentUsername} and may override frame and border settings.
 * @param {Array} views (optional) List of profile views to add the panel to. Currently 'user' and 'admin' are support. If omitted, the panel will be added to all views.
 *
 * @static
 * @member Nexus.profile
 */
Nexus.profile.register = function(name, panelCls, views) {
  if (views === undefined || views.indexOf('user') !== -1) {
    Sonatype.Events.addListener('userProfileInit', function(views) {
      views.push({
        name : name,
        item : panelCls
      });
    });
  }

  if (views === undefined || views.indexOf('admin') !== -1) {
    Sonatype.Events.addListener('userAdminViewInit', function(views) {
      views.push({
        name : name,
        item : panelCls
      });
    });
  }
}



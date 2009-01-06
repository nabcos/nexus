/*
 * Nexus: Maven Repository Manager
 * Copyright (C) 2008 Sonatype Inc.                                                                                                                          
 * 
 * This file is part of Nexus.                                                                                                                                  
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 */
/*
 * User Edit/Create panel layout and controller
 */

Sonatype.repoServer.UserEditPanel = function( config ) {
  var config = config || {};
  var defaultConfig = {
    title: 'Users'
  };
  Ext.apply( this, config, defaultConfig );

  this.sp = Sonatype.lib.Permissions;
  
  this.actions = {
    resetPasswordAction: {
      text: 'Reset Password',
      scope: this,
      handler: this.resetPasswordHandler
    },
    changePasswordAction: {
      text: 'Set Password',
      scope: this,
      handler: this.changePasswordHandler
    }
  };
  
  this.displaySelector = new Ext.Button( {
    text: 'Default Realm Users',
    icon: Sonatype.config.resourcePath + '/images/icons/page_white_stack.png',
    cls: 'x-btn-text-icon',
    value: 'default',
    menu: {
      items: [
        {
          text: 'Default Realm Users',
          checked: true,
          handler: this.showUsers,
          value: 'default',
          group: 'user-realm-selector',
          scope: this
        },
        {
          text: 'All Users With Nexus Roles',
          checked: false,
          handler: this.showUsers,
          group: 'user-realm-selector',
          value: 'allConfigured',
          scope: this
        }
      ]
    }
  } );
  
  this.searchField = new Ext.app.SearchField( { 
    searchPanel: this,
    width: 240,
    emptyText: 'Display All',

    onTrigger2Click : function(){
      var v = this.getRawValue();
      this.searchPanel.startSearch( this.searchPanel );
    }
  } );

  Sonatype.Events.on( 'userAddMenuInit', this.onAddMenuInit, this );
  Sonatype.Events.on( 'userMenuInit', this.onUserMenuInit, this );

  Sonatype.repoServer.UserEditPanel.superclass.constructor.call( this, {
    addMenuInitEvent: 'userAddMenuInit',
    deleteButton: this.sp.checkPermission( 'nexus:users', this.sp.DELETE ),
    rowClickEvent: 'userViewInit',
    rowContextClickEvent: 'userMenuInit',
    url: Sonatype.config.repos.urls.plexusUsersDefault,
    dataAutoLoad: true,
    dataId: 'userId',
    columns: [
      { 
        name: 'resourceURI',
        mapping: 'userId',
        convert: function( value, parent ) {
          return parent.source == 'default' ? 
            ( Sonatype.config.repos.urls.users + '/' + value ) :
            ( Sonatype.config.repos.urls.plexusUser + '/' + value );
        }
      },
      { 
        name: 'userId', 
        sortType: Ext.data.SortTypes.asUCString,
        header: 'User ID',
        width: 100
      },
      { 
        name: 'source',
        header: 'Realm',
        width: 50
      },
      { 
        name: 'name',
        header: 'Name',
        width: 175
      },
      { 
        name: 'email',
        header: 'Email',
        width: 175
      },
      { name: 'roles' },
      { 
        name: 'displayRoles', 
        mapping: 'roles', 
        convert: this.combineRoles.createDelegate( this ),
        header: 'Roles',
        autoExpand: true
      }
    ],
    listeners: {
      beforedestroy: {
        fn: function(){
          Sonatype.Events.un( 'userAddMenuInit', this.onAddMenuInit, this );
          Sonatype.Events.un( 'userMenuInit', this.onUserMenuInit, this );
        },
        scope: this
      }
    },
    tbar: [
      ' ',
      this.displaySelector,
      this.searchField
    ]
  } );
};


Ext.extend( Sonatype.repoServer.UserEditPanel, Sonatype.panels.GridViewer, {
  combineRoles: function( val, parent ) {
    var s = '';
    if ( val ) {
      for ( var i = 0; i < val.length; i++ ) {
        var roleName = val[i].name;
        if ( s ) {
          s += ', ';
        }
        s += roleName;
      }
    }

    return s;
  },

  changePasswordHandler : function( rec ) {
    var userId = rec.get( 'userId' );

    var w = new Ext.Window({
      title: 'Set Password',
      closable: true,
      autoWidth: false,
      width: 350,
      autoHeight: true,
      modal:true,
      constrain: true,
      resizable: false,
      draggable: false,
      items: [
        {
          xtype: 'form',
          labelAlign: 'right',
          labelWidth:110,
          frame:true,  
          defaultType:'textfield',
          monitorValid:true,
          items:[
            {
              xtype: 'panel',
              style: 'padding-left: 70px; padding-bottom: 10px',
              html: 'Enter a new password for user ' + userId
            },
            { 
              fieldLabel: 'New Password', 
              inputType: 'password',
              name: 'newPassword',
              width: 200,
              allowBlank: false 
            },
            { 
              fieldLabel: 'Confirm Password', 
              inputType: 'password',
              name: 'confirmPassword',
              width: 200,
              allowBlank: false,
              validator: function( s ) {
                var firstField = this.ownerCt.find( 'name', 'newPassword' )[0];
                if ( firstField && firstField.getRawValue() != s ) {
                  return "Passwords don't match";
                }
                return true;
              }
            }
          ],
          buttons: [
            {
              text: 'Set Password',
              formBind: true,
              scope: this,
              handler: function(){
                var newPassword = w.find('name', 'newPassword')[0].getValue();

                Ext.Ajax.request({
                  scope: this,
                  method: 'POST',
                  jsonData: {
                    data: {
                      userId: userId,
                      newPassword: newPassword
                    }
                  },
                  url: Sonatype.config.repos.urls.usersSetPassword,
                  success: function(response, options){
                    w.close();
                    Sonatype.MessageBox.show( {
                      title: 'Password Changed',
                      msg: 'Password change request completed successfully.',
                      buttons: Sonatype.MessageBox.OK,
                      icon: Sonatype.MessageBox.INFO,
                      animEl: 'mb3'
                    } );
                  },
                  failure: function(response, options){
                    Sonatype.utils.connectionError( response, 'There is a problem changing the password.' )
                  }
                });
              }
            },
            {
              text: 'Cancel',
              formBind: false,
              scope: this,
              handler: function(){
                w.close();
              }
            }
          ]
        }
      ]
    });

    w.show();
  },

  refreshHandler: function( button, e ) {
    this.clearCards();
    if ( this.lastUrl ) {
      this.searchByUrl();
    }
    else {
      this.dataStore.reload();
    }
  },

  resetPasswordHandler : function( rec ) {
    if ( rec.data.resourceURI != 'new' ) {
      Sonatype.utils.defaultToNo();
        
      Sonatype.MessageBox.show({
        animEl: this.gridPanel.getEl(),
        title : 'Reset user password?',
        msg : 'Reset the ' + rec.get('userId') + ' user password?',
        buttons: Sonatype.MessageBox.YESNO,
        scope: this,
        icon: Sonatype.MessageBox.QUESTION,
        fn: function(btnName){
          if (btnName == 'yes' || btnName == 'ok') {
            Ext.Ajax.request({
              callback: this.resetPasswordCallback,
              cbPassThru: {
                resourceId: rec.id
              },
              scope: this,
              method: 'DELETE',
              url: Sonatype.config.repos.urls.usersReset + '/' + rec.data.userId
            });
          }
        }
      });
    } 
  },
  
  resetPasswordCallback : function(options, isSuccess, response){
    if(isSuccess){
      Sonatype.MessageBox.alert('The password has been reset.');
    }
    else {
      Sonatype.MessageBox.alert('The server did not reset the password.');
    }
  },
  
  searchByUrl: function() {
    this.gridPanel.loadMask.show();
    Ext.Ajax.request( {
      scope: this,
      url: this.lastUrl,
      callback: function( options, success, response ) {
        this.gridPanel.loadMask.hide();
        if ( success ) {
          var r = Ext.decode( response.responseText );
          if ( r.data ) {
            this.dataStore.loadData( r );
          }
          else {
            this.clearAll();
          }
        }
      }
    } );
  },

  showUsers: function( button, e ) {
    this.displaySelector.setText( button.text );
    this.displaySelector.value = button.value;
    this.calculateSearchUrl( this );
  },
  
  startSearch: function( panel ) {
    panel.calculateSearchUrl( panel );
    panel.searchByUrl();
  },

  calculateSearchUrl: function( panel ) {
    var v = panel.searchField.getValue();
    var prefix = '/' + panel.displaySelector.value; 
    if ( v.length > 0 ) {
      panel.lastUrl = Sonatype.config.repos.urls.searchUsers + prefix + '/' + panel.searchField.getValue();
    }
    else {
      panel.lastUrl = Sonatype.config.repos.urls.plexusUsers + prefix;
    }
  },
  
  stopSearch: function( panel ) {
    panel.searchField.setValue( null );
    panel.searchField.triggers[0].hide();
  },
  
  onAddMenuInit: function( menu ) {
    menu.add( '-' );
    if ( this.sp.checkPermission( 'nexus:users', this.sp.CREATE ) ) {
      menu.add( {
        text: 'Nexus User',
        autoCreateNewRecord: true,
        handler: function( container, rec, item, e ) {
          rec.beginEdit();
          rec.set( 'source', 'default' );
          rec.commit();
          rec.endEdit();
        },
        scope: this
      } );
    }
    menu.add( {
      text: 'User Role Mapping',
      handler: this.mapRolesHandler,
      scope: this
    } );
  },

  onUserMenuInit: function( menu, userRecord ) {
    if ( userRecord.data.source == 'default' // && userRecord.data.userManaged == true
        ) {

      if ( userRecord.data.resourceURI.substring( 0, 4 ) != 'new_' ) {
        if ( this.sp.checkPermission( 'nexus:usersreset', this.sp.DELETE ) ) {
          menu.add(this.actions.resetPasswordAction);
        }

        if ( this.sp.checkPermission( 'nexus:users', this.sp.EDIT ) ) {
          menu.add( this.actions.changePasswordAction );
        }
      }
    }
  },
  
  mapRolesHandler: function( button, e ) {
    this.createChildPanel( { 
      id: 'new_mapping',
      hostPanel: this,
      data: {
        name: 'User Role Mapping'
      }
    } );
  },

  deleteRecord: function( rec ) {
    if ( rec.data.source == 'default' ) {
      return Sonatype.repoServer.UserEditPanel.superclass.deleteRecord.call( this, rec );
    }
    else {
      Ext.Ajax.request( {
        callback: function( options, success, response ) {
          if ( success ) {
            this.dataStore.remove( rec );
          }
          else {
            Sonatype.utils.connectionError( response, 'Delete Failed!' );
          }
        },
        scope: this,
        suppressStatus: 404,
        method: 'DELETE',
        url: Sonatype.config.repos.urls.userToRoles + '/' +
          rec.data.source + '/' + rec.data.userId
      } );
    }
  },
  
  deleteActionHandler: function( button, e ) {
    if ( this.gridPanel.getSelectionModel().hasSelection() ) {
      var rec = this.gridPanel.getSelectionModel().getSelected();
      if ( rec.data.source == 'default' ) {
        return Sonatype.repoServer.UserEditPanel.superclass.deleteActionHandler.call( this, button, e );
      }
      else {
        var roles = rec.data.roles;
        if ( roles ) for ( var i = 0; i < roles.length; i++ ) {
          if ( roles[i].source == 'default' ) {
            Sonatype.utils.defaultToNo();
            
            Sonatype.MessageBox.show({
              animEl: this.gridPanel.getEl(),
              title: 'Delete',
              msg: 'Delete Nexus role mapping for ' + rec.data[this.titleColumn] + '?',
              buttons: Sonatype.MessageBox.YESNO,
              scope: this,
              icon: Sonatype.MessageBox.QUESTION,
              fn: function( btnName ) {
                if ( btnName == 'yes' || btnName == 'ok' ) {
                  this.deleteRecord( rec );
                }
              }
            } );
            return;
          }
        }
        Sonatype.MessageBox.show( {
          animEl: this.gridPanel.getEl(),
          title: 'Delete',
          msg: 'This user does not have any Nexus roles mapped.',
          buttons: Sonatype.MessageBox.OK,
          scope: this,
          icon: Sonatype.MessageBox.WARNING
        } );
      }
    }
  }
} );

Sonatype.repoServer.DefaultUserEditor = function( config ) {
  var config = config || {};
  var defaultConfig = {
    uri: Sonatype.config.repos.urls.users,
    labelWidth: 100,
    dataModifiers: {
      load: {
        roles: function( arr, srcObj, fpanel ) {
          fpanel.find( 'name', 'roles' )[0].setValue( arr );
          return arr;
        }
      },
      submit: { 
        roles: function( value, fpanel ) {
          return fpanel.find( 'name', 'roles' )[0].getValue();
        }
      }
    }
  };
  Ext.apply( this, config, defaultConfig );
  
  //List of user statuses
  this.statusStore = new Ext.data.SimpleStore( { fields: ['value', 'display'],
    data: [['active', 'Active'], ['disabled', 'Disabled']] } );

  this.roleDataStore = new Ext.data.JsonStore( {
    root: 'data',
    id: 'id',
    url: Sonatype.config.repos.urls.roles,
    sortInfo: { field: 'name', direction: 'ASC' },
    fields: [
      { name: 'id' },
      { name: 'name', sortType:Ext.data.SortTypes.asUCString }
    ]
  } );

  var ht = Sonatype.repoServer.resources.help.users;
  
  this.COMBO_WIDTH = 300;

  this.checkPayload();

  var items = [
    {
      xtype: 'textfield',
      fieldLabel: 'User ID',
      itemCls: 'required-field',
      labelStyle: 'margin-left: 15px; width: 185px;',
      helpText: ht.userId,
      name: 'userId',
      disabled: ! this.isNew,
      allowBlank: false,
      width: this.COMBO_WIDTH
    },
    {
      xtype: 'textfield',
      fieldLabel: 'Name',
      itemCls: 'required-field',
      labelStyle: 'margin-left: 15px; width: 185px;',
      helpText: ht.name,
      name: 'name',
      allowBlank: false,
      width: this.COMBO_WIDTH
    },
    {
      xtype: 'textfield',
      fieldLabel: 'Email',
      itemCls: 'required-field',
      labelStyle: 'margin-left: 15px; width: 185px;',
      helpText: ht.email,
      name: 'email',
      allowBlank: false,
      width: this.COMBO_WIDTH
    },
    {
      xtype: 'combo',
      fieldLabel: 'Status',
      labelStyle: 'margin-left: 15px; width: 185px;',
      itemCls: 'required-field',
      helpText: ht.status,
      name: 'status',
      store: this.statusStore,
      displayField:'display',
      valueField:'value',
      editable: false,
      forceSelection: true,
      mode: 'local',
      triggerAction: 'all',
      emptyText:'Select...',
      selectOnFocus:true,
      allowBlank: false,
      width: this.COMBO_WIDTH
    }
  ];
  
  if ( this.isNew ) {
    items.push( {
      xtype: 'textfield',
      fieldLabel: 'New Password (optional)',
      inputType: 'password',
      labelStyle: 'margin-left: 15px; width: 185px;',
      helpText: ht.password,
      name: 'password',
      allowBlank: true,
      width: this.COMBO_WIDTH
    } );
    items.push( {
      xtype: 'textfield',
      fieldLabel: 'Confirm Password',
      inputType: 'password',
      labelStyle: 'margin-left: 15px; width: 185px;',
      helpText: ht.reenterPassword,
      name: 'confirmPassword',
      allowBlank: true,
      width: this.COMBO_WIDTH,
      validator: function( s ) {
        var firstField = this.ownerCt.find( 'name', 'password' )[0];
        if ( firstField && firstField.getRawValue() != s ) {
          return "Passwords don't match";
        }
        return true;
      }
    } );
  }

  items.push( {
    xtype: 'twinpanelchooser',
    titleLeft: 'Selected Roles',
    titleRight: 'Available Roles',
    name: 'roles',
    valueField: 'id',
    store: this.roleDataStore,
    required: true
  } );

  Sonatype.repoServer.DefaultUserEditor.superclass.constructor.call( this, {
    items: items,
    dataStores: [this.roleDataStore],
    listeners: {
      submit: {
        fn: this.submitHandler,
        scope: this
      }
    }
  } );
};

Ext.extend( Sonatype.repoServer.DefaultUserEditor, Sonatype.ext.FormPanel, {
  combineRoles: function( val ) {
    var s = '';
    if ( val ) {
      for ( var i = 0; i < val.length; i++ ) {
        var roleName = val[i];
        var rec = this.roleDataStore.getAt( this.roleDataStore.find( 'id', roleName ) );
        if ( rec ) {
          roleName = rec.data.name;
        }
        if ( s ) {
          s += ', ';
        }
        s += roleName;
      }
    }

    return s;
  },

  isValid: function() {
    return this.form.isValid() && this.find( 'name', 'roles' )[0].validate();
  },
  
  saveHandler: function( button, event ) {
    var password = this.form.getValues().password;
    this.referenceData = ( this.isNew && password ) ?
      Sonatype.repoServer.referenceData.userNew : Sonatype.repoServer.referenceData.users;
    
    return Sonatype.repoServer.DefaultUserEditor.superclass.saveHandler.call( this, button, event );
  },

  submitHandler: function( form, action, receivedData ) {
    if ( this.isNew ) {
      receivedData.source = 'default';
      receivedData.displayRoles = this.combineRoles( receivedData.roles );
      return;
    }

    var rec = this.payload;
    rec.beginEdit();
    rec.set( 'name', receivedData.name );
    rec.set( 'email', receivedData.email );
    rec.set( 'displayRoles', this.combineRoles( receivedData.roles ) );
    rec.commit();
    rec.endEdit();
  }
} );

Sonatype.repoServer.UserMappingEditor = function( config ) {
  var config = config || {};
  var defaultConfig = {
    uri: Sonatype.config.repos.urls.plexusUser,
    dataModifiers: {
      load: {
        roles: function( arr, srcObj, fpanel ) {
          var arr2 = [];
          var externalRoles = 0;
          for ( var i = 0; i < arr.length; i++ ) {
            var a = arr[i];
            var readOnly = false;
            if ( a.source != 'default' ) {
              readOnly = true;
              externalRoles++;
            }
            arr2.push( {
              id: a.roleId,
              name: a.name,
              readOnly: readOnly
            } );
          }
          var roleBox = fpanel.find( 'name', 'roles' )[0];
          roleBox.setValue( arr2 );
          roleBox.nexusRolesEmptyOnLoad = ( arr.length == externalRoles );

          return arr;
        }
      },
      submit: { 
        roles: function( value, fpanel ) {
          return fpanel.find( 'name', 'roles' )[0].getValue();
        }
      }
    },
    referenceData: {
      userId: '',
      source: '',
      roles: []
    }
  };
  Ext.apply( this, config, defaultConfig );

  this.roleDataStore = new Ext.data.JsonStore( {
    root: 'data',
    id: 'id',
    url: Sonatype.config.repos.urls.roles,
    sortInfo: { field: 'name', direction: 'ASC' },
    fields: [
      { name: 'id' },
      { name: 'name', sortType:Ext.data.SortTypes.asUCString }
    ]
  } );

  var ht = Sonatype.repoServer.resources.help.users;
  
  this.COMBO_WIDTH = 300;
  
  var useridField = this.payload.id == 'new_mapping' ? 
    {
      xtype: 'trigger',
      triggerClass: 'x-form-search-trigger',
      fieldLabel: 'Enter a User ID',
      itemCls: 'required-field',
      labelStyle: 'margin-left: 15px; width: 185px;',
      name: 'userId',
      allowBlank: false,
      width: this.COMBO_WIDTH,
      listeners: {
        specialkey: {
          fn: function(f, e){
            if(e.getKey() == e.ENTER){
              this.loadUserId.createDelegate( this );
            }
          }
        }
      },
      onTriggerClick: this.loadUserId.createDelegate( this ),
      listeners: {
        change: {
          fn: function( control, newValue, oldValue ) {
            if ( newValue != this.lastLoadedId ) {
              this.loadUserId();
            }
          },
          scope: this
        }
      }
    } :
    {
      xtype: 'textfield',
      fieldLabel: 'User ID',
      itemCls: 'required-field',
      labelStyle: 'margin-left: 15px; width: 185px;',
      name: 'userId',
      disabled: true,
      allowBlank: false,
      width: this.COMBO_WIDTH,
      userFound: true
    };
    

  Sonatype.repoServer.UserMappingEditor.superclass.constructor.call( this, {
    dataStores: [this.roleDataStore],
    items: [
      useridField,
      {
        xtype: 'textfield',
        fieldLabel: 'Realm',
        itemCls: 'required-field',
        labelStyle: 'margin-left: 15px; width: 185px;',
        name: 'source',
        disabled: true,
        allowBlank: false,
        width: this.COMBO_WIDTH
      },
      {
        xtype: 'textfield',
        fieldLabel: 'Name',
        itemCls: 'required-field',
        labelStyle: 'margin-left: 15px; width: 185px;',
        name: 'name',
        disabled: true,
        allowBlank: false,
        width: this.COMBO_WIDTH
      },
      {
        xtype: 'textfield',
        fieldLabel: 'Email',
        itemCls: 'required-field',
        labelStyle: 'margin-left: 15px; width: 185px;',
        name: 'email',
        disabled: true,
        allowBlank: false,
        width: this.COMBO_WIDTH
      },
      {
        xtype: 'twinpanelchooser',
        titleLeft: 'Selected Roles',
        titleRight: 'Available Roles',
        name: 'roles',
        valueField: 'id',
        store: this.roleDataStore,
        required: true
      }
    ],
    listeners: {
      submit: {
        fn: this.submitHandler,
        scope: this
      }
    }
  } );
};

Ext.extend( Sonatype.repoServer.UserMappingEditor, Sonatype.ext.FormPanel, {
  saveHandler : function( button, event ){
    if ( this.isValid() ) {
      var method = 'PUT';
      var roleBox = this.find( 'name', 'roles' )[0];
      var roles = roleBox.getValue();
      if ( roles.length == 0 ) {
        if ( roleBox.nexusRolesEmptyOnLoad ) {
          // if there weren't any nexus roles on load, and we're not saving any - do nothing
          return;
        }
        else {
          method = 'DELETE';
          roleBox.nexusRolesEmptyOnLoad = true;
        }
      }
      else {
        roleBox.nexusRolesEmptyOnLoad = false;
      }

      var url = Sonatype.config.repos.urls.userToRoles + '/' +
        this.form.findField( 'source').getValue() + '/' +
        this.form.findField( 'userId' ).getValue();

      this.form.doAction( 'sonatypeSubmit', {
        method: method,
        url: url,
        waitMsg: 'Updating records...',
        fpanel: this,
        dataModifiers: this.dataModifiers.submit,
        serviceDataObj: this.referenceData,
        isNew: this.isNew //extra option to send to callback, instead of conditioning on method
      } );
    }
  },

  // update roles if the user record with the same id is displayed in the grid
  // (auto-update doesn't work since the mapping resource does not return anything)
  submitHandler: function( form, action, receivedData ) {
    var store;
    if ( this.payload.id == 'new_mapping' && this.payload.hostPanel ) {
      store = this.payload.hostPanel.dataStore;
    }
    else if ( this.payload.store ) {
      store = this.payload.store;
    }
    
    if ( store ) {
      var rec = store.getById( action.output.data.userId );
      if ( rec ) {
        var s = '';
        var roles = [];
        var sentRoles = action.output.data.roles;
        for ( var i = 0; i < sentRoles.length; i++ ) {
          var roleName = sentRoles[i];
          var roleRec = this.roleDataStore.getAt( this.roleDataStore.find( 'id', roleName ) );
          if ( roleRec ) {
            roles.push( roleRec.data );
            roleName = roleRec.data.name;
          }
          if ( s ) {
            s += ', ';
          }
          s += roleName;
        }
        
        rec.beginEdit();
        rec.set( 'roles', roles );
        rec.set( 'displayRoles', s );
        rec.commit();
        rec.endEdit();
      }
    }
  },
  
  isValid: function() {
    return this.form.findField( 'userId' ).userFound &&
      this.form.findField( 'source' ).getValue() && 
      this.find( 'name', 'roles' )[0].validate();
  },

  loadUserId: function() {
    var testField = this.form.findField( 'userId' );
    testField.clearInvalid();
    testField.userFound = true;
    this.lastLoadedId = testField.getValue();

    this.form.doAction( 'sonatypeLoad', {
      url: this.uri + '/' + testField.getValue(),
      method: 'GET',
      fpanel: this,
      testField: testField,
      suppressStatus: 404,
      dataModifiers: this.dataModifiers.load,
      scope: this
    } );
  },

  actionFailedHandler: function( form, action ) {
    if ( action.response.status == 404 && action.options.testField ) {
      action.options.testField.markInvalid( 'User record not found.' );
      action.options.testField.userFound = false;
    }
    else {
      return Sonatype.repoServer.UserMappingEditor.superclass.actionFailedHandler.call(
        this, form, action );
    }
  }
} );

Sonatype.Events.addListener( 'userViewInit', function( cardPanel, rec ) {
  var config = { payload: rec };
  cardPanel.add( rec.data.source == 'default' ?
    new Sonatype.repoServer.DefaultUserEditor( config ) :
    new Sonatype.repoServer.UserMappingEditor( config )
  );
} );

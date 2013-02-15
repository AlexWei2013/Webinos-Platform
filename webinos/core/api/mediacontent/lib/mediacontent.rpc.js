/*******************************************************************************
*  Code contributed to the webinos project
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*  
*     http://www.apache.org/licenses/LICENSE-2.0
*  
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Copyright 2012 Habib Virji, Samsung Electronics (UK) Ltd
 ******************************************************************************/
(function () {
  "use strict";
  // implFile should be determined dynamically, like for Device Orientation
		var	implFile = 'android';
		 
  var MediaType = require("./media_types.js"),
    os = require("os"),
    path = require("path"),
    child_process = require("child_process").exec,
    fs = require("fs"),
    WebinosMediaContentModule,
    RPCWebinosService = require('webinos-jsonrpc2').RPCWebinosService;

  
   function WebinosMediaContentModule(rpcHandler, params) {
    
	var implModule = require('./mediacontent.' + implFile + '.js');
	implModule.setRPCHandler(rpcHandler);
    
    // inherit from RPCWebinosService
    this.base = RPCWebinosService;
    this.base(implModule.serviceDesc);
    
    /**
   * This functionality is not implemented as not supported in media info
   * @param item
   */
  this.updateItem = function (params, successCB, errorCB, item) {

  };
  /**
   * This functionality is not implemented as not supported in media info
   * @param items
   * @successCB
   * @errorCB
   */
  this.updateItemsBatch = function (params, successCB, errorCB, items) {
  };

  this.fetchMediaInfo = function(directoryPath, id, callback) {
    implModule.fetchMediaInfo(directoryPath, id, callback);
  }

  this.getLocalFolders = function (params, successCB, errorCB) {
				implModule.getLocalFolders(params, successCB, errorCB);
  };

  // sortMode and filter not implemented
  this.findItem = function (params, successCB, errorCB) {
    implModule.findItem(params, successCB, errorCB);
  };

  this.getContents = function (params, successCB, errorCB, objectRef) {
    implModule.getContents(params, successCB, errorCB, objectRef);
  };
    
  }


  WebinosMediaContentModule.prototype = new RPCWebinosService;
  
  // export our object
  exports.Service = WebinosMediaContentModule;
})();

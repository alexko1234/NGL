"use strict";

angular.module('ngl-sq.containerSupportsServices', []).
factory('containerSupportsSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
	var tags = [];
	var getColumns = function(){
		var columns = [];
		columns.push({
			"header":Messages("containerSupports.table.code"),
			"property":"code",
			"position":1,
			"order":true,
			"type":"text"
		});
		columns.push({
			"header":Messages("containerSupports.table.categoryCode"),
			"property":"categoryCode",
			"filter":"codes:'container_support_cat'",
			"position":2,
			"order":true,
			"type":"text"
		});
		
		columns.push({
			"header":Messages("containers.table.fromTransformationTypeCodes"),
			"property":"fromTransformationTypeCodes",
			"order":false,
			"hide":true,
			"position":2.5,
			"type":"text",
			"render":"<div list-resize='cellValue | unique' list-resize-min-size='3'>",
			"filter":"unique | codes:\"type\"",
			"groupMethod":"collect"
		});	
		
		columns.push({
			"header":Messages("containerSupports.table.sampleCodes.length"),
			"property":"sampleCodes.length",
			"position":4,
			"order":true,
			"hide":true,
			"type":"text"
		});
		columns.push({
			"header":Messages("containerSupports.table.sampleCodes"),
			"property":"sampleCodes",
			"position":5,
			"order":false,
			"type":"text",
			"render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>",
		});
		columns.push({
			"header":Messages("containerSupports.table.projectCodes"),
			"property":"projectCodes",
			"position":6,
			"render":"<div list-resize='value.data.projectCodes | unique' list-resize-min-size='3'>",
			"order":false,
			"type":"text"
		});		
		columns.push({
			"header":Messages("containerSupports.table.creationDate"),
			"property":"traceInformation.creationDate",
			"position":8,
			"order":true,
			"type":"date"
		});
		columns.push({
			"header":Messages("containers.table.createUser"),
			"property":"traceInformation.createUser",
			"position":9,
			"order":true,
			"type":"text"
		});
		columns.push({
			"header":Messages("containers.table.storageCode"),
			"property":"storageCode",
			"order":true,
			"hide":true,
			"type":"text",
			"edit":false,
			"position":9.5,
			"groupMethod":"unique"
				
		});
		if(mainService.getHomePage() === 'state'){
			columns.push({
				"header":Messages("containerSupports.table.state.code"),
				"property":"state.code",
				"position":3,
				"order":true,
				"type":"text",
				"edit":true,
				"choiceInList": true,
				"listStyle":"bt-select",
				"possibleValues":"searchService.lists.getStates()", 
				"filter":"codes:'state'"
			});
			columns.push({
				"header":Messages("containerSupports.table.valid"),
				"property":"valuation.valid",
				"position":7,
				"order":true,
				"type":"text",
				"edit":false,
				"choiceInList": true,
				"possibleValues":"searchService.lists.getValuations()", 
				"filter":"codes:'valuation'",
			});
		}else{
			columns.push({
				"header":Messages("containerSupports.table.state.code"),
				"property":"state.code",
				"position":3,
				"order":true,
				"type":"text",
				"edit":false,
				"choiceInList": true,
				"possibleValues":"searchService.lists.getStates()", 
				"filter":"codes:'state'"
			});
			columns.push({
				"header":Messages("containerSupports.table.valid"),
				"property":"valuation.valid",
				"position":7,
				"order":true,
				"type":"text",
				"edit":true,
				"choiceInList": true,
				"possibleValues":"searchService.lists.getValuations()", 
				"filter":"codes:'valuation'",
			});
		}
		
		return columns;
	};


	var isInit = false;

	var initListService = function(){
		if(!isInit){
			lists.refresh.containerSupportCategories();
			lists.refresh.containerCategories();
			lists.refresh.experimentTypes({categoryCodes:["transformation"], withoutOneToVoid:false});
			lists.refresh.containerSupports();
			lists.refresh.projects();
			lists.refresh.users();
			lists.refresh.processCategories();
			lists.refresh.states({objectTypeCode:"Container"});
			isInit=true;
		}
	};

	var searchService = {
			getColumns:getColumns,
			datatable:undefined,
			isRouteParam:false,
			lists : lists,
			form:undefined, 
			selectedAddColumns:[],
			setRouteParams:function($routeParams){
				var count = 0;
				for(var p in $routeParams){
					count++;
					break;
				}
				if(count > 0){
					this.isRouteParam = true;
					this.form = $routeParams;
				}
			},

			updateForm : function(){
				//this.form.includes = [];
				/*
				this.form.includes = ["default"];
				for(var i = 0 ; i < this.selectedAddColumns.length ; i++){
					//remove .value if present to manage correctly properties (single, list, etc.)
					if(this.selectedAddColumns[i].queryIncludeKeys && this.selectedAddColumns[i].queryIncludeKeys.length > 0){
						this.form.includes = this.form.includes.concat(this.selectedAddColumns[i].queryIncludeKeys);
					}else{
						this.form.includes.push(this.selectedAddColumns[i].property.replace('.value',''));	
					}
					
				}
				*/
			},
			convertForm : function(){
				var _form = angular.copy(this.form);
				if(_form.fromDate)_form.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
				if(_form.toDate)_form.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();		
				return _form
			},

			resetForm : function(){
				this.form = {};									
			},

			resetSampleCodes : function(){
				this.form.sampleCodes = [];									
			},
			
			search : function(){
				this.updateForm();
				mainService.setForm(this.form);
				var jsonSearch = this.convertForm();
				if(jsonSearch != undefined){
					this.datatable.search(jsonSearch);
				}
			},
			refreshSamples : function(){
				if(this.form.projectCodes && this.form.projectCodes.length>0){
					lists.refresh.samples({projectCodes:this.form.projectCodes});
				}
			},
			changeProject : function(){
				if(this.form.project){
					lists.refresh.samples({projectCode:this.form.project.code});
				}else{
					lists.clear("samples");
				}

				if(this.form.type){
					this.search();
				}
			},

			changeProcessType : function(){
				if(this.form.processCategory){
					this.search();
				}else{
					this.form.processType = undefined;	
				}
			},

			changeProcessCategory : function(){
				this.form.processTypeCode = undefined;
				if(this.form.processCategory){
					lists.refresh.processTypes({"categoryCode":this.form.processCategory});
				}
			},
			/**
			 * initialise the service
			 */
			init : function($routeParams, datatableConfig){
				initListService();

				datatableConfig.messages = {
						transformKey: function(key, args) {
	                        return Messages(key, args);
	                    }
				};
				// to avoid to lost the previous search
				if(datatableConfig && angular.isUndefined(mainService.getDatatable())){				
					this.datatable = datatable(datatableConfig);
					mainService.setDatatable(this.datatable);
					this.datatable.setColumnsConfig(getColumns());
				}else if(angular.isDefined(mainService.getDatatable()) && angular.isDefined(mainService.getDatatable().getConfig().search.url)){
					if(mainService.getDatatable().getConfig().search.url.includes("supports"))
						this.datatable = mainService.getDatatable();
				}else{	// to release udt 
					this.datatable = datatable(datatableConfig);
					mainService.setDatatable(this.datatable);
					this.datatable.setColumnsConfig(getColumns());
				}
				
				if(angular.isDefined(mainService.getForm())){
					searchService.form = mainService.getForm();
				}else{
					searchService.resetForm();						
				}

				if(angular.isDefined($routeParams)){
					this.setRouteParams($routeParams);
				}
			}
	};

	return searchService;				
}]);

"use strict";
angular.module('ngl-sq.containerSupportsServices').
factory('containerSupportsDetailsSearchService', ['mainService','lists','datatable', function(mainService,lists,datatable){
	var getColumnsDefault = function(){
		var columns = [
		               	{
		               		"header":Messages("containerSupports.table.yx"),
		               		"property":"support.line+support.column*1",
		               		"type":"text",
		               		"order":true,
		               		"hide":true,
		               		"position":1,
		               		"edit":false
		               	},
						{
							"header":Messages("containers.table.code"),
							"property":"code",
							"order":true,
							"hide":true,
							"position":2,
							"type":"text",
							"render":"<div list-resize='cellValue | stringToArray | unique' ' list-resize-min-size='2'>",
							"groupMethod":"collect"							
						},
						{
							"header":Messages("containers.table.fromTransformationTypeCodes"),
							"property":"fromTransformationTypeCodes",
					  	  	"position":3,
					  	  	"hide":true,
					  	  	"order":false,
					  	  	"type":"text",
					  	  	"render":"<div list-resize='cellValue | unique' list-resize-min-size='3'>",
					  	  	"filter":"unique | codes:\"type\"",
					  	  	"groupMethod":"collect"	
						},
						{
							"header":Messages("containerSupports.table.projectCodes"),
							"property":"projectCodes",
							"position":4,
							"render":"<div list-resize='value.data.projectCodes | unique' list-resize-min-size='3'>",
							"order":true,
							"type":"text",
							"hide":true,
						},
						{
							"header":Messages("containerSupports.table.sampleCodes"),
							"property":"sampleCodes",
							"position":5,
							"order":false,
							"hide":true,
							"type":"text",
							"render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>",			
						},
						{
							"header":Messages("containers.table.support.line"),
							"property":"support.line",
							"order":true,
							"hide":true,
							"position":6,
							"type":"text"
						},
						{
							"header":Messages("containers.table.support.column"),
							"property":"support.column*1",
							"order":true,
							"hide":true,
							"position":7,
							"type":"number"							
						}
					 ];
		return columns;
	};
	
	var isInit = false;	
	var initListService = function(){
		if(!isInit){
			lists.refresh.containerSupportCategories();
			lists.refresh.experimentTypes({categoryCodes:["transformation"], withoutOneToVoid:false});
		}
	};
	
	var detailsSearchService = {
			getColumns:getColumnsDefault,
			getDefaultColumns:getColumnsDefault,
			datatable:undefined,
			form:undefined,
			isRouteParam:false,
			lists : lists,
			
			setRouteParams:function($routeParams){
				var count = 0;
				for(var p in $routeParams){
					count++;
					break;
				}
				if(count > 0){
					this.isRouteParam = true;
					this.form.supportCodeRegex = $routeParams.code;
				}
			},
			convertForm : function(){
				var _form = angular.copy(this.form);
				if(_form.fromDate)_form.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
				if(_form.toDate)_form.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();		
				return _form

			},
			resetForm : function(){
				this.form = {};									
			},
			search : function(){			
				this.datatable.search(this.convertForm());
			},
			
			/**
			 * initialise the service
			 */
			init : function($routeParams, datatableConfig){
				initListService();
				
				datatableConfig.messages = {
						transformKey: function(key, args) {
	                        return Messages(key, args);
	                    }
				};

				this.datatable = datatable(datatableConfig);
				mainService.setDatatable(this.datatable);
				this.datatable.setColumnsConfig(getColumnsDefault());		

				if(angular.isDefined(mainService.getForm())){
					this.form = mainService.getForm();
				}else{
					this.resetForm();						
				}

				if(angular.isDefined($routeParams)){
					this.resetForm();
					this.setRouteParams($routeParams);
				}
			}			
	};
	
	return detailsSearchService;
}]);
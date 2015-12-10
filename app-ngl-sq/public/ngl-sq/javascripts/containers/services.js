"use strict";

angular.module('ngl-sq.containersServices', []).
factory('containersSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
	//var tags = [];
	var getColumnsDefault = function(){
		var columns = [];
		columns.push({
			"header":Messages("containers.table.supportCode"),
			"property":"support.code",
			"order":true,
			"hide":true,
			"position":1,
			"type":"text",
			"group":true
		});
		columns.push({
			"header":Messages("containers.table.supportCategoryCode"),
			"property":"support.categoryCode",
			"filter":"codes:'container_support_cat'",
			"order":true,
			"position":2,
			"hide":true,
			"type":"text",
			"groupMethod":"unique"
		});
		columns.push({
			"header":Messages("containers.table.support.line"),
			"property":"support.line",
			"order":true,
			"hide":true,
			"position":3,
			"type":"text"
		});
		columns.push({
			"header":Messages("containers.table.support.column"),
			"property":"support.column",
			"order":true,
			"hide":true,
			"position":4,
			"type":"text"
		});		
		columns.push({
			"header":Messages("containers.table.code"),
			"property":"code",
			"order":true,
			"position":5,
			"type":"text",
			"render":"<div list-resize='cellValue | stringToArray | unique' ' list-resize-min-size='2'>",
			"groupMethod":"collect"
		});
		columns.push({
			"header":Messages("containers.table.projectCodes"),
			"property":"projectCodes",
			"order":false,
			"hide":true,
			"position":6,					
			"render":"<div list-resize='cellValue' ' list-resize-min-size='2'>",
			"filter":"unique",
			"type":"text",
			"groupMethod":"collect"
		});			
		columns.push({
			"header":Messages("containers.table.sampleCodes.length"),
			"property":"sampleCodes.length",
			"order":true,
			"hide":true,
			"position":7,
			"type":"number",
			"groupMethod":"sum"
		});
		columns.push({
			"header":Messages("containers.table.sampleCodes"),
			"property":"sampleCodes",
			"order":false,
			"hide":true,
			"position":8,
			"type":"text",
			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			"filter":"unique",
			"groupMethod":"collect"
			
		});
		columns.push({
			"header":Messages("containers.table.contents.length"),
			"property":"contents.length",
			"order":true,
			"hide":true,
			"position":9,
			"type":"number",
			"groupMethod":"sum"
				
		});
		columns.push({
			"header":Messages("containers.table.tags"),
			"property": "contents",
			"order":false,
			"hide":true,
			"type":"text",
			"position":10,
			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			"filter":"getArray:'properties.tag.value' | unique",
			"groupMethod":"collect"
			
		});
		columns.push({
			"header":Messages("containers.table.fromExperimentTypeCodes"),
			"property":"fromExperimentTypeCodes",
			"order":false,
			"hide":true,
			"position":11,
			"type":"text",
			"render":"<div list-resize='cellValue | unique' list-resize-min-size='3'>",
			"filter":"unique | codes:\"type\"",
			"groupMethod":"collect"
		});	
		columns.push({
			"header":Messages("containers.table.mesuredConcentration.value"),
			"property":"mesuredConcentration.value",
			"order":true,
			"hide":true,
			"position":11.6,
			"format":2,
			"type":"number",
			"groupMethod":"unique"
		});	
		columns.push({
			"header":Messages("containers.table.mesuredConcentration.unit"),
			"property":"mesuredConcentration.unit",
			"order":true,
			"hide":true,
			"position":11.7,
			"type":"text",
			"groupMethod":"unique"
		});
		columns.push({
			"header":Messages("containers.table.creationDate"),
			"property":"traceInformation.creationDate",
			"order":true,
			"hide":true,
			"position":14,			
			"type":"date",
			"groupMethod":"unique"
				});
		columns.push({
			"header":Messages("containers.table.createUser"),
			"property":"traceInformation.createUser",
			"order":true,
			"hide":true,
			"position":15,
			"type":"text",
			"groupMethod":"unique"
		});
		columns.push({
			"header":Messages("containers.table.storageCode"),
			"property":"support.storageCode",
			"order":true,
			"hide":true,
			"type":"text",
			"edit":false,
			"position":15.5,
			"groupMethod":"unique"
				
		});
		columns.push({
				"header":Messages("containers.table.inputProcessCodes"),
				"property":"inputProcessCodes",
				"order":false,
				"hide":true,
				"type":"text",
				"position":16,
				"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				"groupMethod":"collect"
			});
		columns.push({
			"header":Messages("containers.table.state.code"),
			"property":"state.code",
			"order":true,
			"hide":true,
			"type":"text",
			"edit":true,
			"position":12,
			"choiceInList": true,
			"listStyle":"bt-select",
			"possibleValues":"searchService.lists.getStates()", 
			"filter":"codes:'state'",
			"groupMethod":"unique"					
		});
		columns.push({
			"header":Messages("containers.table.valid"),
			"property":"valuation.valid",
			"order":true,
			"type":"text",
			"edit":false,
			"hide":true,
			"position":13,
			"choiceInList": true,
			"listStyle":"bt-select",
			"possibleValues":"searchService.lists.getValuations()", 
			"filter":"codes:'valuation'"
		});
		
		return columns;
	};
	
	var isInit = false;

	var initListService = function(){
		if(!isInit){
			lists.refresh.containerSupportCategories();
			lists.refresh.containerCategories();
			lists.refresh.experimentTypes({categoryCodes:["transformation", "voidProcess"], withoutOneToVoid:false});
			lists.refresh.containerSupports();
			lists.refresh.projects();
			lists.refresh.processCategories();
			lists.refresh.states({objectTypeCode:"Container"});
			lists.refresh.users();
			lists.refresh.reportConfigs({pageCodes:["containers"+"-"+mainService.getHomePage()]});
			lists.refresh.reportConfigs({pageCodes:["containers-addcolumns"]}, "containers-addcolumns");
			lists.refresh.filterConfigs({pageCodes:["containers-search-addfilters"]}, "containers-search-addfilters");
			isInit=true;
		}
	};

	var searchService = {
			getColumns:getColumnsDefault,
			getDefaultColumns:getColumnsDefault,
			datatable:undefined,
			isRouteParam:false,
			lists : lists,
			additionalFilters:[],
			additionalColumns:[],
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
				this.form.includes = [];
				if(this.reportingConfiguration){
					for(var i = 0 ; i < this.reportingConfiguration.columns.length ; i++){
						if(this.reportingConfiguration.columns[i].queryIncludeKeys && this.reportingConfiguration.columns[i].queryIncludeKeys.length > 0){
							this.form.includes = this.form.includes.concat(this.reportingConfiguration.columns[i].queryIncludeKeys);
						}else{
							this.form.includes.push(this.reportingConfiguration.columns[i].property.replace('.value','').replace(".unit", ''));
						}
					}
				}else{
					this.form.includes = ["default"];
				}
				
				
				//this.form.includes = ["default"];
				for(var i = 0 ; i < this.selectedAddColumns.length ; i++){
					//remove .value if present to manage correctly properties (single, list, etc.)
					if(this.selectedAddColumns[i].queryIncludeKeys && this.selectedAddColumns[i].queryIncludeKeys.length > 0){
						this.form.includes = this.form.includes.concat(this.selectedAddColumns[i].queryIncludeKeys);
					}else{
						this.form.includes.push(this.selectedAddColumns[i].property.replace('.value','').replace(".unit", ''));
					}
					
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
			
			resetSampleCodes : function(){
				this.form.sampleCodes = [];									
			},

			search : function(){
				this.updateForm();
				mainService.setForm(this.form);				
				this.datatable.search(this.convertForm());
				
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
			initAdditionalColumns : function(){
				this.additionalColumns=[];
				this.selectedAddColumns=[];
				
				if(lists.get("containers-addcolumns") && lists.get("containers-addcolumns").length === 1){
					var formColumns = [];
					var allColumns = angular.copy(lists.get("containers-addcolumns")[0].columns);
					var nbElementByColumn = Math.ceil(allColumns.length / 5); //5 columns
					for(var i = 0; i  < 5 && allColumns.length > 0 ; i++){
						formColumns.push(allColumns.splice(0, nbElementByColumn));	    								
					}
					//complete to 5 five element to have a great design 
					while(formColumns.length < 5){
						formColumns.push([]);
					}
					this.additionalColumns = formColumns;
				}
			},
			getAddColumnsToForm : function(){
				if(this.additionalColumns.length === 0){
					this.initAdditionalColumns();
				}
				return this.additionalColumns;									
			},
			addColumnsToDatatable:function(){
				//this.reportingConfiguration = undefined;
				//this.reportingConfigurationCode = undefined;
				
				this.selectedAddColumns = [];
				for(var i = 0 ; i < this.additionalColumns.length ; i++){
					for(var j = 0; j < this.additionalColumns[i].length; j++){
						if(this.additionalColumns[i][j].select){
							this.selectedAddColumns.push(this.additionalColumns[i][j]);
						}
					}
				}
				if(this.reportingConfigurationCode){
					this.datatable.setColumnsConfig(this.reportingConfiguration.columns.concat(this.selectedAddColumns));
				}else{
					this.datatable.setColumnsConfig(this.getDefaultColumns().concat(this.selectedAddColumns));						
				}
				this.search();
			},	
			resetDatatableColumns:function(){
				this.initAdditionalColumns();
				this.datatable.setColumnsConfig(this.getDefaultColumns());
				this.search();
			},
			/**
			 * Update column when change reportingConfiguration
			 */
			updateColumn : function(){
				this.initAdditionalColumns();
				if(this.reportingConfigurationCode){
					$http.get(jsRoutes.controllers.reporting.api.ReportingConfigurations.get(this.reportingConfigurationCode).url,{searchService:this, datatable:this.datatable})
							.success(function(data, status, headers, config) {
								config.searchService.reportingConfiguration = data;
								//config.searchService.search();
								config.datatable.setColumnsConfig(data.columns);																								
					});
				}else{
					this.reportingConfiguration = undefined;
					this.datatable.setColumnsConfig(this.getDefaultColumns());
					//this.search();
				}
				
			},
			initAdditionalFilters:function(){
				this.additionalFilters=[];
				
				if(lists.get("containers-search-addfilters") && lists.get("containers-search-addfilters").length === 1){
					var formFilters = [];
					var allFilters = angular.copy(lists.get("containers-search-addfilters")[0].filters);
					var nbElementByColumn = Math.ceil(allFilters.length / 5); //5 columns
					for(var i = 0; i  < 5 && allFilters.length > 0 ; i++){
						formFilters.push(allFilters.splice(0, nbElementByColumn));	    								
					}
					//complete to 5 five element to have a great design 
					while(formFilters.length < 5){
						formFilters.push([]);
					}
						
					this.additionalFilters = formFilters;
				}
			},
			
			getAddFiltersToForm : function(){
				if(this.additionalFilters.length === 0){
					this.initAdditionalFilters();
				}
				return this.additionalFilters;									
			},	
			
			
			/**
			 * initialise the service
			 */
			init : function($routeParams, datatableConfig){
				initListService();

				// to avoid to lost the previous search
				if(datatableConfig && angular.isUndefined(mainService.getDatatable())){
					searchService.datatable = datatable(datatableConfig);
					mainService.setDatatable(searchService.datatable);
					searchService.datatable.setColumnsConfig(getColumnsDefault());		
				}else if(angular.isDefined(mainService.getDatatable())){
					searchService.datatable = mainService.getDatatable();			
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
}
]);
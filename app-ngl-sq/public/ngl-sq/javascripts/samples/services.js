"use strict";

angular.module('ngl-sq.samplesServices', []).
factory('samplesSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
	//var tags = [];
	var getColumnsDefault = function(){
		var columns = [];
		
		columns.push({
			"header":Messages("samples.table.projectCodes"),
			"property":"projectCodes",
			"order":false,
			"hide":true,
			"group":true,
			"position":1,					
			"render":"<div list-resize='cellValue | unique' ' list-resize-min-size='2'>",
			"type":"text",
			"groupMethod":"collect"
		});	
		
		columns.push({
			"header":Messages("samples.table.code"),
			"property":"code",
			"order":true,
			"hide":true,
			"position":2,
			"type":"text",
			"group":true,
			"groupMethod":"collect"
		});
		columns.push({
			"header":Messages("samples.table.typeCode"),
			"property":"typeCode",
			"filter":"codes:'type'",
			"order":true,
			"hide":true,
			"position":3,
			"type":"text",			
			"groupMethod":"collect"
		});
		columns.push({
			"header":Messages("samples.table.referenceCollab"),
			"property":"referenceCollab",
			"order":true,
			"hide":true,
			"position":4,
			"type":"text",			
			"groupMethod":"collect"
		});	
		columns.push({
			"header":Messages("samples.table.taxonCode"),
			"property":"taxonCode",
			"order":true,
			"hide":true,
			"position":5,
			"type":"text",			
			"groupMethod":"collect"
		});	
		columns.push({
			"header":Messages("samples.table.ncbiScientifiName"),
			"property":"ncbiScientificName",
			"order":true,
			"hide":true,
			"position":6,
			"type":"text",			
			"groupMethod":"collect"
		});	
		
		columns.push({
			"header":Messages("samples.table.creationDate"),
			"property":"traceInformation.creationDate",
			"order":true,
			"hide":true,
			"position":14,			
			"type":"date",
			"groupMethod":"unique"
				});
		columns.push({
			"header":Messages("samples.table.createUser"),
			"property":"traceInformation.createUser",
			"order":true,
			"hide":true,
			"position":15,
			"type":"text",
			"groupMethod":"unique"
		});
		
		
		return columns;
	};
	
	var isInit = false;

	var initListService = function(){
		if(!isInit){
			lists.refresh.projects();
			lists.refresh.processCategories();
			lists.refresh.states({objectTypeCode:"Sample"});
			lists.refresh.users();
			lists.refresh.reportConfigs({pageCodes:["samples"+"-"+mainService.getHomePage()]});
			lists.refresh.reportConfigs({pageCodes:["samples-addcolumns"]}, "samples-addcolumns");
			lists.refresh.filterConfigs({pageCodes:["samples-search-addfilters"]}, "samples-search-addfilters");
			lists.refresh.resolutions({"objectTypeCode":"Sample"}, "sampleResolutions");
			isInit=true;
		}
	};

	var searchService = {
			getColumns:getColumnsDefault,
			getDefaultColumns:getColumnsDefault,
			datatable:undefined,
			isRouteParam:false,
			lists : lists,
			form:undefined,
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
			getStates : function(value){
				/*
				this.initAuthorizedStates();
				if(value && value.data){
					return this.authorizedStates[value.data.state.code];
				}else{
					return this.lists.getStates();
				}
				*/
				return this.lists.getStates();
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
				
				if(this.form.reportingQuery){
					this.form.reportingQuery.trim();
					if(this.form.reportingQuery.length > 0){
						this.form.reporting=true;
					}else{
						this.form.reporting=false;
					}
				}else{
					this.form.reporting=false;
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
					//we used the order in the document to order column in display and not the position value !!!!
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
								if(config.searchService.lists.get('reportConfigs').length > 1){
									config.searchService.search();
								}
								config.datatable.setColumnsConfig(data.columns);																								
					});
				}else{
					this.reportingConfiguration = undefined;
					this.datatable.setColumnsConfig(this.getDefaultColumns());
					this.search();
				}
				
			},
			initAdditionalFilters:function(){
				this.additionalFilters=[];
				
				if(lists.get("containers-search-addfilters") && lists.get("containers-search-addfilters").length === 1){
					var formFilters = [];
					var allFilters = angular.copy(lists.get("containers-search-addfilters")[0].filters);
					
					/* add static filters here*/
					allFilters.push({property:"comments.comment",html:"<textarea class='form-control' ng-model='searchService.form.commentRegex' placeholder='"+Messages("search.placeholder.commentRegex")+"' title='"+Messages("search.placeholder.commentRegex")+"'></textarea>",position:allFilters.length+1});
					
					
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

				datatableConfig.messages = {
						transformKey: function(key, args) {
	                        return Messages(key, args);
	                    }
				};
				
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
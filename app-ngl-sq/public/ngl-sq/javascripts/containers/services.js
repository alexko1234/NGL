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
			"position":1,
			"type":"text"
		});
		columns.push({
			"header":Messages("containers.table.supportCategoryCode"),
			"property":"support.categoryCode",
			"order":true,
			"position":2,
			"type":"text"
		});
		columns.push({
			"header":Messages("containers.table.support.column"),
			"property":"support.column",
			"order":true,
			"position":3,
			"type":"text"
		});
		columns.push({
			"header":Messages("containers.table.support.line"),
			"property":"support.line",
			"order":true,
			"position":4,
			"type":"text"
		});
		columns.push({
			"header":Messages("containers.table.code"),
			"property":"code",
			"order":true,
			"position":5,
			"type":"text"
		});
		columns.push({
			"header":Messages("containers.table.fromExperimentTypeCodes"),
			"property":"fromExperimentTypeCodes",
			"order":true,
			"position":6,
			"type":"text"
		});		
		columns.push({
			"header":Messages("containers.table.sampleCodes.length"),
			"property":"sampleCodes.length",
			"order":true,
			"position":8,
			"type":"number"
		});
		columns.push({
			"header":Messages("containers.table.sampleCodes"),
			"property":"sampleCodes",
			"order":true,
			"hide":true,
			"position":9,
			"type":"text",
			"render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>",
		});	
		columns.push({
			"header":Messages("containers.table.tags"),
			"property": "contents",
			"order":true,
			"hide":true,
			"type":"text",
			"position":9.1,
			"render":"<div list-resize='value.data.contents | getArray:\"properties.tag.value\"' ' list-resize-min-size='3'>",
		});
		columns.push({
					"header":Messages("containers.table.projectCodes"),
					"property":"projectCodes",
					"order":true,
					"position":10,
					"type":"text"
				});
		columns.push({
					"header":Messages("containers.table.valid"),
					"property":"valuation.valid",
					"order":true,
					"type":"text",
					"edit":true,
					"position":11,
					"choiceInList": true,
					"possibleValues":"searchService.lists.getValuations()", 
					"filter":"codes:'valuation'",
				});
		columns.push({
					"header":Messages("containers.table.creationDate"),
					"property":"traceInformation.creationDate",
					"order":true,
					"position":12,
					"type":"date"
				});
		columns.push({
			"header":Messages("containers.table.createUser"),
			"property":"traceInformation.createUser",
			"order":true,
			"position":13,
			"type":"text"
		});
		columns.push({
					"header":Messages("containers.table.inputProcessCodes"),
					"property":"inputProcessCodes",
					"order":true,
					"type":"text",
					"position":14,
					"render":"<div list-resize='value.data.inputProcessCodes | unique' list-resize-min-size='3'>",
				});
		
		if(mainService.getHomePage() === 'state'){
			columns.push({
				"header":Messages("containers.table.state.code"),
				"property":"state.code",
				"order":true,
				"type":"text",
				"edit":true,
				"position":7,
				"choiceInList": true,
				"possibleValues":"searchService.lists.getStates()", 
				"filter":"codes:'state'"
			});
		}else{
			columns.push({
				"header":Messages("containers.table.state.code"),
				"property":"state.code",
				"order":true,
				"type":"text",
				"edit":false,
				"position":7,
				"choiceInList": true,
				"possibleValues":"searchService.lists.getStates()", 
				"filter":"codes:'state'"
			});
		}
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
			lists.refresh.reportConfigs({pageCodes:["containers-addcolumns"]}, "containers-addcolumns");
			isInit=true;
		}
	};

	var searchService = {
			getColumns:getColumnsDefault,
			getDefaultColumns:getColumnsDefault,
			datatable:undefined,
			isRouteParam:false,
			lists : lists,
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
				//this.form.includes = [];
				this.form.includes = ["default"];
				for(var i = 0 ; i < this.selectedAddColumns.length ; i++){
					//remove .value if present to manage correctly properties (single, list, etc.)
					if(this.selectedAddColumns[i].queryIncludeKeys && this.selectedAddColumns[i].queryIncludeKeys.length > 0){
						this.form.includes = this.form.includes.concat(this.selectedAddColumns[i].queryIncludeKeys);
					}else{
						this.form.includes.push(this.selectedAddColumns[i].property.replace('.value',''));	
					}
					
				}
			},
			convertForm : function(){
				var _form = angular.copy(this.form);
				if(_form.projectCodes || _form.sampleCodes || (_form.fromExperimentTypeCodes && _form.fromExperimentTypeCodes.length > 0) || _form.containerCategory || _form.processType || _form.createUser
						|| _form.processCategory || _form.containerSupportCategory || _form.state || _form.states || _form.containerSupportCode  || _form.valuations || _form.fromDate || _form.toDate){	

					var jsonSearch = {};

					if(_form.projectCodes){
						jsonSearch.projectCodes = _form.projectCodes;
					}			
					if(_form.sampleCodes){
						jsonSearch.sampleCodes = _form.sampleCodes;
					}		

					if(_form.valuations){
						jsonSearch.valuations = _form.valuations;
					}

					if(_form.fromExperimentTypeCodes){
						jsonSearch.fromExperimentTypeCodes = _form.fromExperimentTypeCodes;
					}

					if(_form.containerCategory){
						jsonSearch.categoryCode = _form.containerCategory;
					}

					if(_form.processType){
						jsonSearch.processTypeCode = _form.processType;
					}							

					if(_form.containerSupportCategory){
						jsonSearch.containerSupportCategory = _form.containerSupportCategory;
					}	


					if(_form.state){
						jsonSearch.stateCode = _form.state;
					}

					if(_form.states){
						jsonSearch.stateCodes = _form.states;
					}

					if(_form.containerSupportCode){
						jsonSearch.supportCode = _form.containerSupportCode;
					}
					
					if(_form.createUser){
						jsonSearch.createUser = _form.createUser;
					}

					if(_form.fromDate)jsonSearch.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
					if(_form.toDate)jsonSearch.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();

					mainService.setForm(_form);

					return jsonSearch;
				}else{
					this.datatable.setData({},0);
					return undefined;

				}

			},

			resetForm : function(){
				this.form = {};									
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
				this.form.processType = undefined;
				if(this.form.processCategory){
					lists.refresh.processTypes({"processCategoryCode":this.form.processCategory});
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
			updateColumn : function(){
				this.initAdditionalColumns();				
				this.reportingConfiguration = undefined;
				this.datatable.setColumnsConfig(this.getDefaultColumns());
				this.search();		
				
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
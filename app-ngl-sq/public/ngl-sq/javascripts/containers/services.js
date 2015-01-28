 "use strict";
 
 angular.module('ngl-sq.containersServices', []).
	factory('containersSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		var getColumns = function(){
			var columns = [];
			columns.push({
				"header":Messages("containers.table.supportCode"),
				"property":"support.code",
				"order":true,
				"type":"text"
			});
			columns.push({
				"header":Messages("containers.table.supportCategoryCode"),
				"property":"support.categoryCode",
				"order":true,
				"type":"text"
			});
			columns.push({
				"header":Messages("containers.table.support.column"),
				"property":"support.column",
				"order":true,
				"type":"text"
			});
			columns.push({
				"header":Messages("containers.table.support.line"),
				"property":"support.line",
				"order":true,
				"type":"text"
			});
			columns.push({
				"header":Messages("containers.table.code"),
				"property":"code",
				"order":true,
				"type":"text"
			});
			columns.push({
				"header":Messages("containers.table.fromExperimentTypeCodes"),
				"property":"fromExperimentTypeCodes",
				"order":true,
				"type":"text"
			});
			columns.push({
				"header":Messages("containers.table.state.code"),
				"property":"state.code",
				"order":true,
				"type":"text",
				"edit":true,
				"choiceInList": true,
				"possibleValues":"searchService.lists.getStates()", 
				"filter":"codes:'state'"
			});
			columns.push({
				"header":Messages("containers.table.sampleCodes.length"),
				"property":"sampleCodes.length",
				"order":true,
				"hide":true,
				"type":"text"
			});
			columns.push({
				"header":Messages("containers.table.sampleCodes"),
				"property":"sampleCodes",
				"order":true,
				"type":"text",
				"render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>",
			});	
			columns.push({
				"header":Messages("containers.table.projectCodes"),
				"property":"projectCodes",
				"order":true,
				"type":"text"
			});					
			columns.push({
				"header":Messages("containers.table.valid"),
				"property":"valuation.valid",
				"order":true,
				"type":"text",
				"edit":true,
				"choiceInList": true,
				"possibleValues":"searchService.lists.getValuations()", 
				"filter":"codes:'valuation'",
			});
			columns.push({
				"header":Messages("containers.table.creationDate"),
				"property":"traceInformation.creationDate",
				"order":true,
				"type":"date"
			});
			columns.push({
				"header":Messages("containers.table.inputProcessCodes"),
				"property":"inputProcessCodes",
				"order":true,
				"type":"text",
				"render":"<div list-resize='value.data.inputProcessCodes | unique' list-resize-min-size='3'>",
			});
			
			
			return columns;
		};
		
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				lists.refresh.containerSupportCategories();
				lists.refresh.containerCategories();
				lists.refresh.experimentTypes({categoryCode:"transformation", withoutOneToVoid:true});
				lists.refresh.supports();
				lists.refresh.projects();
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
					
				},
				convertForm : function(){
					var _form = angular.copy(this.form);
					if(_form.projectCodes || _form.sampleCodes || (_form.fromExperimentTypeCodes && _form.fromExperimentTypeCodes.length > 0) || _form.containerCategory || _form.processType
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
				/**
				 * initialise the service
				 */
				init : function($routeParams, datatableConfig){
					initListService();
					
					//to avoid to lost the previous search
					if(datatableConfig && angular.isUndefined(mainService.getDatatable())){
						searchService.datatable = datatable(datatableConfig);
						mainService.setDatatable(searchService.datatable);
						searchService.datatable.setColumnsConfig(getColumns());		
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
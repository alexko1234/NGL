 "use strict";
 
 angular.module('ngl-sq.experimentsServices', []).
	factory('experimentsSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		var getColumns = function(){
			var columns = [
						{
							"header":Messages("experiments.table.code"),
							"property":"code",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("experiments.table.categoryCode"),
							"property":"categoryCode",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("experiments.table.typeCode"),
							"property":"typeCode",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("experiments.table.state.code"),
							"property":"state.code",
							"order":true,
							"type":"text",
							"hide":true,
							"filter":"codes:'state'"
						},
						{
							"header":Messages("experiments.table.resolutionCodes"),
							"property":"state.resolutionCodes",
							"order":true,
							"hide":true,
							"type":"date"
						},
						{
							"header":Messages("experiments.table.creationDate"),
							"property":"traceInformation.creationDate",
							"order":true,
							"hide":true,
							"type":"date"
						},
						{
							"header":Messages("experiments.table.createUser"),
							"property":"traceInformation.createUser",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("experiments.table.projectCodes"),
							"property":"projectCodes",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("containers.table.sampleCodes"),
							"property":"sampleCodes",
							"order":true,
							"hide":true,
							"type":"text"
						}
						];
			
			return columns;
		};
		
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				lists.refresh.types({objectTypeCode:"Process"}, true);
				lists.refresh.processCategories();
				lists.refresh.experimentCategories();
				lists.refresh.projects();
				lists.refresh.users();
				lists.refresh.states({objectTypeCode:"Experiment"});
				lists.refresh.experimentTypes({categoryCode:"purification"}, "purifications");
				lists.refresh.experimentTypes({categoryCode:"qualitycontrol"}, "qualitycontrols");
				lists.refresh.experimentTypes({categoryCode:"transfert"}, "transferts");
				lists.refresh.experimentTypes({categoryCode:"transformation"}, "transformations");
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
					if(_form.processType || _form.experimentType || _form.projectCodes || _form.sampleCodes || _form.type || _form.fromDate || _form.toDate || _form.state || _form.user || _form.containerSupportCode){
						var jsonSearch = {};			

						if(_form.projectCodes){
							jsonSearch.projectCodes = _form.projectCodes;
						}			
						if(_form.sampleCodes){
							jsonSearch.sampleCodes = _form.sampleCodes;
						}			
						if(_form.processType){
							jsonSearch.processTypeCode = _form.processType;
						}		
						
						if(_form.containerSupportCode){
							jsonSearch.containerSupportCode = _form.containerSupportCode;
						}
						
						if(_form.type){
							jsonSearch.typeCode = _form.type;
						}
						
						if(_form.state){
							jsonSearch.stateCode = _form.state;
						}

						if(_form.user){
							jsonSearch.users = _form.user;
						}
						
						if(_form.experimentType){
							jsonSearch.typeCode = _form.experimentType;
						}
						
						if(_form.fromDate)jsonSearch.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
						if(_form.toDate)jsonSearch.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();
						
						return jsonSearch;	
					}
					
					return undefined;
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
					if(this.form.projectCodes && this.form.projectCodes.length > 0){
						this.lists.refresh.samples({projectCodes:this.form.projectCodes});
					}
				},
				changeTypeCode : function(){
					this.search();
				},
				
				changeExperimentType : function(){
					this.search();
				},
				
				changeProcessCategory : function(){
					this.form.experimentType = undefined;
					this.form.experimentCategory = undefined;
					if(this.form.processCategory){
						lists.refresh.processTypes({processCategoryCode:this.form.processCategory});
					}
					this.form.processType = undefined;
				},
				
				changeProcessType : function(){
					this.form.experimentType = undefined;
					this.form.experimentCategory = undefined;
				},
				
				changeExperimentCategory : function(){
					this.form.experimentType = undefined;
					if(this.form.processType && this.form.experimentCategory){
						lists.refresh.experimentTypes({categoryCode:this.form.experimentCategory, processTypeCode:this.form.processType});
					}else if(this.form.experimentCategory){
						lists.refresh.experimentTypes({categoryCode:this.form.experimentCategory});
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
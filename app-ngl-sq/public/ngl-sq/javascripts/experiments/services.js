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
							"position":1,
							"type":"text"
						},
						{
							"header":Messages("experiments.intrument"),
							"property":"instrument.code",
							"order":true,
							"hide":true,
							"position":2,
							"type":"text",
							"filter":"codes:'instrument'"
						},						
						{
							"header":Messages("experiments.table.categoryCode"),
							"property":"categoryCode",
							"order":true,
							"hide":true,
							"position":3,
							"type":"text"
						},
						{
							"header":Messages("experiments.table.typeCode"),
							"property":"typeCode",
							"filter":"codes:'type'",
							"order":true,
							"hide":true,
							"position":4,
							"type":"text"
						},
						{
							"header":Messages("experiments.table.state.code"),
							"property":"state.code",
							"order":true,
							"type":"text",
							"position":5,
							"hide":true,
							"filter":"codes:'state'"
						},
						{
							"header":Messages("experiments.table.resolutionCodes"),
							"property":"state.resolutionCodes",
							//"filter":"codes:'Experiment.resolution'",
							"render":"<div bt-select ng-model='value.data.state.resolutionCodes' bt-options='valid.code as valid.name for valid in searchService.lists.getResolutions()'  ng-edit=\"false\"></div>",
							"order":true,
							"hide":true,
							"position":6,
							"type":"date"
						},
						{
							"header":Messages("containers.table.sampleCodes.length"),
							"property":"sampleCodes.length",
							"order":true,
							"hide":true,
							"position":7,
							"type":"text"
						},
						{
							"header":Messages("containers.table.sampleCodes"),
							"property":"sampleCodes",
							"order":true,
							"hide":true,
							"position":8,
							"type":"text",
							"render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>",
						},
						{
							"header":Messages("experiments.table.projectCodes"),
							"property":"projectCodes",
							"order":true,
							"hide":true,
							"position":9,
							"type":"text"
						},					
						{
							"header":Messages("experiments.table.creationDate"),
							"property":"traceInformation.creationDate",
							"order":true,
							"hide":true,
							"position":10,
							"type":"date"
						},
						{
							"header":Messages("experiments.table.createUser"),
							"property":"traceInformation.createUser",
							"order":true,
							"hide":true,
							"position":11,
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
				lists.refresh.experiments();
				lists.refresh.states({objectTypeCode:"Experiment"});
				lists.refresh.experimentTypes({categoryCode:"purification"}, "purifications");
				lists.refresh.experimentTypes({categoryCode:"qualitycontrol"}, "qualitycontrols");
				lists.refresh.experimentTypes({categoryCode:"transfert"}, "transferts");
				lists.refresh.experimentTypes({categoryCode:"transformation"}, "transformations");
				lists.refresh.reportConfigs({pageCodes:["experiments-addcolumns"]}, "experiments-addcolumns");
				//lists.refresh.instruments();
				lists.refresh.resolutions({objectTypeCode:"Experiment",distinct:true});
				isInit=true;
			}
		};
		
		var searchService = {
				getColumns:getColumns,
				getDefaultColumns:getColumns,
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
					
					if(_form.states){
						jsonSearch.stateCodes = _form.states;
					}
					
					if(_form.reagentOrBoxCode){
						jsonSearch.reagentOrBoxCode = _form.reagentOrBoxCode;
					}

					if(_form.user){
						jsonSearch.users = _form.user;
					}
					
					if(_form.code){
						jsonSearch.code = _form.code;
					}
					
					if(_form.experimentType){
						jsonSearch.typeCode = _form.experimentType;
					}
					
					if(_form.instrument){
						jsonSearch.instrument = _form.instrument;
					}
					
					if(_form.fromDate)jsonSearch.fromDate = this.useMoment(_form.fromDate, Messages("date.format").toUpperCase());
					if(_form.toDate)jsonSearch.toDate = this.useMoment(_form.toDate, Messages("date.format").toUpperCase());
					
					return jsonSearch;
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
				initAdditionalColumns : function(){
					this.additionalColumns=[];
					this.selectedAddColumns=[];
					
					if(lists.get("experiments-addcolumns") && lists.get("experiments-addcolumns").length === 1){
						var formColumns = [];
						var allColumns = angular.copy(lists.get("experiments-addcolumns")[0].columns);
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
				refreshSamples : function(){
					if(this.form.projectCodes && this.form.projectCodes.length > 0){
						this.lists.refresh.samples({projectCodes:this.form.projectCodes});
					}
				},
				changeTypeCode : function(){
					//this.search();					
				},
				useMoment: function(date, format){
					//ex: 2014-10-02
					var patt = /[0-9]{4}-[0-9]{2}-[0-9]{2}/;
					
					//chrome browser always return input with type=date value as AAAA-MM-DD
					if(date.search(patt) != -1){
						return moment(date).valueOf();
					}
					//fifox browser return the specified format
					return moment(date, Messages("date.format").toUpperCase()).valueOf();
				},
				changeExperimentType : function(){					
						lists.refresh.instruments({"experimentTypes":this.form.experimentType});					
				},
				
				changeProcessCategory : function(){
					//this.form.experimentType = undefined;
					//this.form.experimentCategory = undefined;
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
				changeContainerSupportCode: function(val){
					
					console.log(val);
					return $http.get(jsRoutes.controllers.containers.api.ContainerSupports.list().url,{params:{"codeRegex":val}}).success(function(data, status, headers, config) {
						console.log(data);
						
						return [data];				
	    			});
					
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
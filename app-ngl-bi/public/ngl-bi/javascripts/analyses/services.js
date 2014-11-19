 "use strict";
 
 angular.module('ngl-bi.AnalysesServices', []).
	factory('analysisSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		
		var getColumns = function(){
			var columns = [];
			columns.push({	property:"code",
				    	  	header: "analyses.code",
				    	  	type :"text",		    	  	
				    	  	order:true});
			columns.push({	property:"typeCode",
							filter:"codes:'type'",
							header: "analyses.typeCode",
							type :"text",
							order:true});
			columns.push({	property:"masterReadSetCodes",
							header: "analyses.masterReadSetCodes",
							type :"text",
							});					
			columns.push({	property:"projectCodes",
							header: "analyses.projectCodes",
							type :"text",
							});
			columns.push({	property:"sampleCodes",
							header: "analyses.sampleCodes",
							type :"text",
							});
			if(!mainService.isHomePage('state')){
				columns.push({	property:"state.code",
								filter:"codes:'state'",
								header: "analyses.state.code",
								type :"text",
								order:true});
			}else{
				columns.push({	property:"state.code",
								filter:"codes:'state'",
								header: "analyses.state.code",
								type :"text",
								edit:true,
								order:true,
						    	choiceInList:true,
						    	listStyle:'bt-select',
						    	possibleValues:'searchService.lists.getStates()'});
			}
			if(!mainService.isHomePage('valuation')){
				columns.push({	property:"valuation.valid",
								filter:"codes:'valuation'",
								header: "analyses.valuation.valid",
								type :"text",
						    	order:true});
				columns.push({	property:"valuation.resolutionCodes",
								header: "analyses.valuation.resolutions",
								render:'<div bt-select ng-model="value.data.valuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
								type :"text",
								hide:true});
			}else{
				columns.push({	property:"valuation.valid",
								filter:"codes:'valuation'",
								header: "analyses.valuation.valid",
								type :"text",
						    	order:true,
						    	edit:true,
						    	choiceInList:true,
						    	listStyle:'bt-select',
						    	possibleValues:'searchService.lists.getValuations()'});
				columns.push({	property:"valuation.criteriaCode",
								filter:"codes:'valuation_criteria'",
								header: "analyses.valuation.criteria",
								type :"text",
						    	edit:true,
						    	choiceInList:true,
						    	listStyle:'bt-select',
						    	possibleValues:'searchService.lists.getValuationCriterias()'});
				columns.push({	property:"valuation.resolutionCodes",
								header: "analyses.valuation.resolutions",
								render:'<div bt-select ng-model="value.data.valuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
								type :"text",
						    	edit:true,
						    	choiceInList:true,
						    	listStyle:'bt-select-multiple',
						    	possibleValues:'searchService.lists.getResolutions()',
						    	groupBy:'category.name'});
			}					
			return columns;
		};
		
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				searchService.lists.refresh.projects();
				searchService.lists.refresh.states({objectTypeCode:"Analysis", display:true},'statetrue');
				searchService.lists.refresh.states({objectTypeCode:"Analysis"});
				searchService.lists.refresh.types({objectTypeCode:"Analysis"});
				searchService.lists.refresh.resolutions({objectTypeCode:"Analysis"});
				
				lists.refresh.valuationCriterias({objectTypeCode:"Analysis"});
				
				searchService.lists.refresh.reportConfigs({pageCodes:["analysis"+"-"+mainService.getHomePage()]});
				searchService.lists.refresh.users();
				isInit=true;
			}
		};
		
		
		var searchService = {
				getColumns:getColumns,
				datatable:undefined,
				isRouteParam : false,
				lists : lists,
				form : undefined,
				reportingConfigurationCode:undefined,
				reportingConfiguration:undefined,
				//additionalsColumns:[],
				//selectedAddColumns:[],
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
					if (mainService.isHomePage('valuation')) {
						if(!this.isRouteParam && (this.form.stateCodes === undefined || this.form.stateCodes.length === 0)) {
							//No stateCodes selected, the filter by default (on the only two possible states for the valuation) is applied
							this.form.stateCodes = ["IW-V"];
						}		
					}
					
					if(this.reportingConfiguration && this.reportingConfiguration.queryConfiguration){
						var queryParams = this.reportingConfiguration.queryConfiguration;
						if(queryParams && queryParams.includeKeys && queryParams.includeKeys.length > 0){
							this.form.includes = queryParams.includeKeys;
						}else if(queryParams && queryParams.excludeKeys && queryParams.excludeKeys.length > 0) {
							this.form.excludes = queryParams.excludeKeys;
						}
					}
				},
				
				resetForm : function(){
					this.form = {};									
				},
				
				search : function(){
					this.updateForm();
					mainService.setForm(this.form);
					this.datatable.search(this.form);
				},
				
				refreshSamples : function(){
					if(this.form.projectCodes && this.form.projectCodes.length > 0){
						this.lists.refresh.samples({projectCodes:this.form.projectCodes});
					}
				},
				
				valuationStates : [{code:"IW-V",name:Codes("state.IW-V")}],
				states : function(){
					if (mainService.isHomePage('valuation')) {
						return this.valuationStates;
					}else{
						return this.lists.get('statetrue');
					}
				},
				
				/**
				 * Update column when change reportingConfiguration
				 */
				updateColumn : function(){
					if(this.reportingConfigurationCode){
						$http.get(jsRoutes.controllers.reporting.api.ReportingConfigurations.get(this.reportingConfigurationCode).url,{searchService:this, datatable:this.datatable})
								.success(function(data, status, headers, config) {
									config.searchService.reportingConfiguration = data;
									config.searchService.search();
									config.datatable.setColumnsConfig(data.columns);																								
						});
					}else{
						this.reportingConfiguration = undefined;
						this.datatable.setColumnsConfig(this.getColumns());
						this.search();
					}
					
				},
				
				/*
				initAdditionalColumns:function(){
					if(lists.get("readsets-addcolumns") && lists.get("readsets-addcolumns").length === 1){
						var formColumns = [];
						var allColumns = angular.copy(lists.get("readsets-addcolumns")[0].columns);
						var nbElementByColumn = Math.ceil(allColumns.length / 5); //5 columns
						for(var i = 0; i  < 5 && allColumns.length > 0 ; i++){
							formColumns.push(allColumns.splice(0, nbElementByColumn));	    								
						}
						this.additionalsColumns = formColumns;
					}
				},
				
				getAddColumnsToForm : function(){
					if(this.additionalsColumns.length === 0){
						this.initAdditionalColumns();
					}
					return this.additionalsColumns;									
				},				
				addColumnsToDatatable:function(){
					this.reportingConfiguration = undefined;
					this.reportingConfigurationCode = undefined;
					this.selectedAddColumns = [];
					for(var i = 0 ; i < this.additionalsColumns.length ; i++){
						for(var j = 0; j < this.additionalsColumns[i].length; j++){
							if(this.additionalsColumns[i][j].select){
								this.selectedAddColumns.push(this.additionalsColumns[i][j]);
							}
						}
					}
					this.datatable.setColumnsConfig(this.getColumns().concat(this.selectedAddColumns));
					this.search();
					
				},	
				resetDatatableColumns:function(){
					this.additionalsColumns=[];
					this.selectedAddColumns=[];
					this.initAdditionalColumns();
					this.datatable.setColumnsConfig(this.getColumns());
					this.search();
				},
				 */
				
				resetDatatableColumns:function(){
					this.datatable.setColumnsConfig(this.getColumns());
					this.search();
				},
				
				
				/**
				 * initialization of the service
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
 
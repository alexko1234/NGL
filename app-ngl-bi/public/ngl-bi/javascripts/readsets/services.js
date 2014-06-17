 "use strict";
 
 angular.module('ngl-bi.ReadSetsServices', []).
	factory('readSetsSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		
		var getColumns = function(){
			var columns = [];
			columns.push({	property:"code",
				    	  	header: "readsets.code",
				    	  	type :"text",		    	  	
				    	  	order:true,
				    	  	position:1});
			columns.push({	property:"runCode",
							header: "readsets.runCode",
							type :"text",
							order:true,
				    	  	position:2});
			columns.push({	property:"laneNumber",
							header: "readsets.laneNumber",
							type :"text",
							order:true,
				    	  	position:3});
			columns.push({	property:"projectCode",
							header: "readsets.projectCode",
							type :"text",
							order:true,
				    	  	position:4});
			columns.push({	property:"sampleCode",
							header: "readsets.sampleCode",
							type :"text",
							order:true,
				    	  	position:5});
			columns.push({	property:"runSequencingStartDate",
							header: "runs.sequencingStartDate",
							type :"date",
							order:true,
				    	  	position:6});
			if(mainService.getHomePage() == 'search'){
					columns.push({	property:"state.code",
									filter:"codes:'state'",
									header: "readsets.stateCode",
									type :"text",
									order:true,
						    	  	position:7});
					
					columns.push({	property:"productionValuation.valid",
									filter:"codes:'valuation'",
									header: "readsets.productionValuation.valid",
									type :"text",
							    	order:true,
						    	  	position:70});
					
					columns.push({	property:"productionValuation.resolutionCodes",
									header: "readsets.productionValuation.resolutions",
									render:'<div bt-select ng-model="value.data.productionValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
									type :"text",
									hide:true,
						    	  	position:72});
					
					columns.push({	property:"bioinformaticValuation.valid",
									filter:"codes:'valuation'",
									header: "readsets.bioinformaticValuation.valid",
									type :"text",
							    	order:true,
						    	  	position:80});
					
					columns.push({	property:"bioinformaticValuation.resolutionCodes",
									header: "readsets.bioinformaticValuation.resolutions",
									render:'<div bt-select ng-model="value.data.bioinformaticValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
									type :"text",
									hide:true,
						    	  	position:82});
			}else if(mainService.getHomePage() == 'valuation'){
					columns.push({	property:"state.code",
									filter:"codes:'state'",
									header: "readsets.stateCode",
									type :"text",
									order:true,
						    	  	position:7});
					
					columns.push({	property:"productionValuation.valid",
									filter:"codes:'valuation'",
									header: "readsets.productionValuation.valid",
									type :"text",
							    	order:true,
							    	edit:true,
							    	choiceInList:true,
							    	listStyle:'bt-select',
							    	possibleValues:'searchService.lists.getValuations()',
						    	  	position:70
							    	});
					
					columns.push({	property:"productionValuation.criteriaCode",
									filter:"codes:'valuation_criteria'",
									header: "readsets.productionValuation.criteria",
									type :"text",
							    	edit:true,
							    	choiceInList:true,
							    	listStyle:'bt-select',
							    	possibleValues:'searchService.lists.getValuationCriterias()',
						    	  	position:71
				    });
					
					columns.push({	property:"productionValuation.resolutionCodes",
									header: "readsets.productionValuation.resolutions",
									render:'<div bt-select ng-model="value.data.productionValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
									type :"text",
							    	edit:true,
							    	choiceInList:true,
							    	listStyle:'bt-select-multiple',
							    	possibleValues:'searchService.lists.getResolutions()',
							    	groupBy:'category.name',
						    	  	position:72
							    		
					});
					
					columns.push({	property:"bioinformaticValuation.valid",
									filter:"codes:'valuation'",
									header: "readsets.bioinformaticValuation.valid",
									type :"text",
									order:true,
							    	edit:true,
							    	choiceInList:true,
							    	listStyle:'bt-select',
							    	possibleValues:'searchService.lists.getValuations()',
						    	  	position:80
							    	});	
					
			}else if(mainService.getHomePage() == 'state'){
					columns.push({	property:"state.code",
									filter:"codes:'state'",
									header: "readsets.stateCode",
									type :"text",
									edit:true,
									order:true,
							    	choiceInList:true,
							    	listStyle:'bt-select',
							    	possibleValues:'searchService.lists.getStates()',
						    	  	position:7});
					
					columns.push({	property:"productionValuation.valid",
									filter:"codes:'valuation'",
									header: "readsets.productionValuation.valid",
									type :"text",
							    	order:true,
						    	  	position:70    	
					});
					
					columns.push({	property:"bioinformaticValuation.valid",
									filter:"codes:'valuation'",
									header: "readsets.bioinformaticValuation.valid",
									type :"text",
									order:true,
						    	  	position:80
					});
					
			}else if(mainService.getHomePage() == 'batch'){
					columns.push({	property:"productionValuation.valid",
									filter:"codes:'valuation'",
									header: "readsets.productionValuation.valid",
									type :"text",
							    	order:true,
						    	  	position:70    	
							    	});
					columns.push({	property:"bioinformaticValuation.valid",
									filter:"codes:'valuation'",
									header: "readsets.bioinformaticValuation.valid",
									type :"text",
									order:true,
						    	  	position:80
				    });
					
					columns.push({	property:"properties.isSentCCRT.value",
									header: "readsets.properties.isSentCCRT",
									type :"boolean",
									edit:true,
						    	  	position:90
				    });
					columns.push({	property:"properties.isSentCollaborator.value",
									header: "readsets.properties.isSentCollaborator",
									type :"boolean",
									edit:true,
						    	  	position:91
					});
			}
			
			return columns;
		};
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				lists.refresh.projects();
				lists.refresh.states({objectTypeCode:"ReadSet", display:true},'statetrue');
				lists.refresh.states({objectTypeCode:"ReadSet"});			
				lists.refresh.resolutions({objectTypeCode:"ReadSet"});
				lists.refresh.valuationCriterias({objectTypeCode:"ReadSet"});
				lists.refresh.types({objectTypeCode:"Run"});
				lists.refresh.runs();
				lists.refresh.instruments({categoryCode:"seq-illumina"});
				//TODO Warn if pass to one application page
				lists.refresh.reportConfigs({pageCodes:["readsets"+"-"+mainService.getHomePage()]});
				lists.refresh.reportConfigs({pageCodes:["readsets-addcolumns"]}, "readsets-addcolumns");
				
				lists.refresh.resolutions({objectTypeCode:"ReadSet"});
				lists.refresh.users();
				isInit=true;
			}
		};
		
		var searchService = {
				getColumns:getColumns,
				datatable:undefined,
				isRouteParam:false,
				lists : lists,
				form : undefined,
				reportingConfigurationCode:undefined,
				reportingConfiguration:undefined,
				additionalsColumns:[],
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
					if (mainService.isHomePage('valuation')) {
						if(!this.isRouteParam && (this.form.stateCodes === undefined || this.form.stateCodes.length === 0)) {
							//No stateCodes selected, the filter by default (on the only two possible states for the valuation) is applied
							this.form.stateCodes = ["IW-VQC", "IP-VQC", "IW-VBA"];
						}		
					}
					
					if(this.reportingConfiguration && this.reportingConfiguration.queryConfiguration){
						var queryParams = this.reportingConfiguration.queryConfiguration;
						if(queryParams && queryParams.includeKeys && queryParams.includeKeys.length > 0){
							this.form.includes = queryParams.includeKeys;
						}else if(queryParams && queryParams.excludeKeys && queryParams.excludeKeys.length > 0){
							this.form.excludes = queryParams.excludeKeys;
						}else{
							this.form.includes = ["default"];
						}
					}else{
						this.form.includes = ["default"];
					}
					
					for(var i = 0 ; i < this.selectedAddColumns.length ; i++){
						this.form.includes.push(this.selectedAddColumns[i].property);
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
					this.updateForm();
					mainService.setForm(this.form);
					this.datatable.search(this.convertForm());
				},
				
				refreshSamples : function(){
					if(this.form.projectCodes){
						this.lists.refresh.samples({projectCodes:this.form.projectCodes});
					}
				},
				valuationStates : [{code:"IW-VQC",name:Codes("state.IW-VQC")},{code:"IP-VQC",name:Codes("state.IP-VQC")},{code:"IW-VBA",name:Codes("state.IW-VBA")}],
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
					this.resetDatatableColumns();
					if(this.reportingConfigurationCode){
						$http.get(jsRoutes.controllers.reporting.api.ReportingConfigurations.get(this.reportingConfigurationCode).url,{searchService:this, datatable:this.datatable})
								.success(function(data, status, headers, config) {
									config.searchService.reportingConfiguration = data;
									config.datatable.setColumnsConfig(data.columns);
									config.searchService.search();
															
						});
					}else{
						this.reportingConfiguration = undefined;
						this.datatable.setColumnsConfig(this.getColumns());
						this.search();
					}
					
				},
				
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
					this.reportingConfiguration, this.reportingConfigurationCode = undefined;
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
 
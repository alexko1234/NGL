 "use strict";
 
 angular.module('ngl-bi.ReadSetsServices', []).
	factory('searchService', ['$http', 'mainService', 'lists', function($http, mainService, lists){
		
		var searchService = {
				getColumns:function(){
					var columns = [];
					columns.push({	property:"code",
						    	  	header: "readsets.code",
						    	  	type :"text",		    	  	
						    	  	order:true});
					columns.push({	property:"runCode",
									header: "readsets.runCode",
									type :"text",
									order:true});
					columns.push({	property:"laneNumber",
									header: "readsets.laneNumber",
									type :"text",
									order:true});
					columns.push({	property:"projectCode",
									header: "readsets.projectCode",
									type :"text",
									order:true});
					columns.push({	property:"sampleCode",
									header: "readsets.sampleCode",
									type :"text",
									order:true});
					columns.push({	property:"runSequencingStartDate",
									header: "runs.sequencingStartDate",
									type :"date",
									order:true});
					if(mainService.getHomePage() == 'search'){
							columns.push({	property:"state.code",
											filter:"codes:'state'",
											header: "readsets.stateCode",
											type :"text",
											order:true});
							
							columns.push({	property:"productionValuation.valid",
											filter:"codes:'valuation'",
											header: "readsets.productionValuation.valid",
											type :"text",
									    	order:true});
							
							columns.push({	property:"productionValuation.resolutionCodes",
											header: "readsets.productionValuation.resolutions",
											render:'<div bt-select ng-model="value.data.productionValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
											type :"text",
											hide:true});
							
							columns.push({	property:"bioinformaticValuation.valid",
											filter:"codes:'valuation'",
											header: "readsets.bioinformaticValuation.valid",
											type :"text",
									    	order:true});
							
							columns.push({	property:"bioinformaticValuation.resolutionCodes",
											header: "readsets.bioinformaticValuation.resolutions",
											render:'<div bt-select ng-model="value.data.bioinformaticValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
											type :"text",
											hide:true});
					}else if(mainService.getHomePage() == 'valuation'){
							columns.push({	property:"state.code",
											filter:"codes:'state'",
											header: "readsets.stateCode",
											type :"text",
											order:true});
							
							columns.push({	property:"productionValuation.valid",
											filter:"codes:'valuation'",
											header: "readsets.productionValuation.valid",
											type :"text",
									    	order:true,
									    	edit:true,
									    	choiceInList:true,
									    	listStyle:'bt-select',
									    	possibleValues:'searchService.lists.getValuations()'
									    	});
							
							columns.push({	property:"productionValuation.criteriaCode",
											filter:"codes:'valuation_criteria'",
											header: "readsets.productionValuation.criteria",
											type :"text",
									    	edit:true,
									    	choiceInList:true,
									    	listStyle:'bt-select',
									    	possibleValues:'searchService.lists.getValuationCriterias()'
						    });
							
							columns.push({	property:"productionValuation.resolutionCodes",
											header: "readsets.productionValuation.resolutions",
											render:'<div bt-select ng-model="value.data.productionValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
											type :"text",
									    	edit:true,
									    	choiceInList:true,
									    	listStyle:'bt-select-multiple',
									    	possibleValues:'searchService.lists.getResolutions()',
									    	groupBy:'category.name'
									    		
							});
							
							columns.push({	property:"bioinformaticValuation.valid",
											filter:"codes:'valuation'",
											header: "readsets.bioinformaticValuation.valid",
											type :"text",
											order:true,
									    	edit:true,
									    	choiceInList:true,
									    	listStyle:'bt-select',
									    	possibleValues:'searchService.lists.getValuations()'
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
									    	possibleValues:'searchService.lists.getStates()'});
							
							columns.push({	property:"productionValuation.valid",
											filter:"codes:'valuation'",
											header: "readsets.productionValuation.valid",
											type :"text",
									    	order:true    	
							});
							
							columns.push({	property:"bioinformaticValuation.valid",
											filter:"codes:'valuation'",
											header: "readsets.bioinformaticValuation.valid",
											type :"text",
											order:true
							});
							
					}else if(mainService.getHomePage() == 'batch'){
							columns.push({	property:"productionValuation.valid",
											filter:"codes:'valuation'",
											header: "readsets.productionValuation.valid",
											type :"text",
									    	order:true    	
									    	});
							columns.push({	property:"bioinformaticValuation.valid",
											filter:"codes:'valuation'",
											header: "readsets.bioinformaticValuation.valid",
											type :"text",
											order:true
						    });
							
							columns.push({	property:"properties.isSentCCRT.value",
											header: "readsets.properties.isSentCCRT",
											type :"boolean",
											edit:true
						    });
							columns.push({	property:"properties.isSentCollaborator.value",
											header: "readsets.properties.isSentCollaborator",
											type :"boolean",
											edit:true
							});
					}
					
					return columns;
				},
				
				lists : lists,
				form : undefined,
				reportingConfigurationCode:undefined,
				reportingConfiguration:undefined,
				setRouteParams:function($routeParams){
					var count = 0;
					for(var p in $routeParams){
						count++;
						break;
					}
					if(count > 0){
						this.form = $routeParams;
					}
				},
				
				updateForm : function(){
					if (mainService.isHomePage('valuation')) {
						if(this.form.stateCodes === undefined || this.form.stateCodes.length === 0) {
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
							this.form.excludes = ["files", "treatments"];
						}
					}else{
						this.form.excludes = ["files", "treatments"];
					}
				},
				
				convertForm : function(){
					var _form = angular.copy(this.form);
					if(_form.fromDate)_form.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
					if(_form.toDate)_form.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();		
					return _form
				},
				
				refreshSamples : function(){
					if(this.form.projectCode){
						this.lists.refresh.samples({projectCode:this.form.projectCode});
					}
				},
				
				search : function(datatable){
					this.updateForm();
					mainService.setForm(this.form);
					datatable.search(this.convertForm());
				},
				
				updateColumn : function(datatable){
					if(this.reportingConfigurationCode){
						$http.get(jsRoutes.controllers.reporting.api.ReportingConfigurations.get(this.reportingConfigurationCode).url,{searchService:this, datatable:datatable})
								.success(function(data, status, headers, config) {
									config.searchService.reportingConfiguration = data;
									config.searchService.search(datatable);
									config.datatable.setColumnsConfig(data.columns);						
						});
					}else{
						this.reportingConfiguration = undefined;
						datatable.setColumnsConfig(this.getColumns());
						this.search(datatable);
					}
					
				},
				reset : function(){
					this.form = {};
				},
				
				states : function(){
					if (mainService.isHomePage('valuation')) {
						return [{code:"IW-VQC",name:Codes("state.IW-VQC")},{code:"IP-VQC",name:Codes("state.IP-VQC")},{code:"IW-VBA",name:Codes("state.IW-VBA")}];
					}else{
						return this.lists.get('statetrue');
					}
				}
		};
		
		return function(){
			
			searchService.lists.refresh.projects();
			searchService.lists.refresh.states({objectTypeCode:"ReadSet", display:true},'statetrue');
			searchService.lists.refresh.states({objectTypeCode:"ReadSet"});			
			searchService.lists.refresh.resolutions({objectTypeCode:"ReadSet"});
			searchService.lists.refresh.valuationCriterias({objectTypeCode:"ReadSet"});
			searchService.lists.refresh.types({objectTypeCode:"Run"});
			searchService.lists.refresh.runs();
			searchService.lists.refresh.instruments({categoryCode:"seq-illumina"});
			searchService.lists.refresh.reportConfigs({pageCodes:["readsets"+"-"+mainService.getHomePage()]});
			searchService.lists.refresh.resolutions({objectTypeCode:"ReadSet"});
			searchService.lists.refresh.users();
			
			if(angular.isDefined(mainService.getForm())){
				searchService.form = mainService.getForm();
			}else{
				searchService.reset();
			}
			
			return searchService;		
		}
	}
]);
 
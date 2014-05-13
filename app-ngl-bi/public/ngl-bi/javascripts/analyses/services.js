 "use strict";
 
 angular.module('ngl-bi.AnalysesServices', []).
	factory('searchService', ['$http', 'mainService', 'lists', function($http, mainService, lists){
		
		var searchService = {
				getColumns:function(){
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
										header: "analyses.stateCode",
										type :"text",
										order:true});
					}else{
						columns.push({	property:"state.code",
										filter:"codes:'state'",
										header: "analyses.stateCode",
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
				},
				isRouteParam : false,
				lists : lists,
				form : undefined,
				
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
					this.form.excludes = ["files", "treatments"];					
				},
				
				refreshSamples : function(){
					if(this.form.projectCode){
						this.lists.refresh.samples({projectCode:this.form.projectCode});
					}
				},
				
				search : function(datatable){
					this.updateForm();
					mainService.setForm(this.form);
					datatable.search(this.form);
				},
				
				reset : function(){
					this.form = {};
				},
				
				states : function(){
					if (mainService.isHomePage('valuation')) {
						return [{code:"IW-V",name:Codes("state.IW-V")}];
					}else{
						return this.lists.get('statetrue');
					}
				}
		};
		
		return function(){
			
			
			searchService.lists.refresh.projects();
			searchService.lists.refresh.states({objectTypeCode:"Analysis", display:true},'statetrue');
			searchService.lists.refresh.states({objectTypeCode:"Analysis"});
			searchService.lists.refresh.types({objectTypeCode:"Analysis"});
			searchService.lists.refresh.resolutions({objectTypeCode:"Analysis"});
			
			if(angular.isDefined(mainService.getForm())){
				searchService.form = mainService.getForm();
			}else{
				searchService.reset();
			}
			
			return searchService;		
		}
	}
]);
 
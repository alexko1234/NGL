 "use strict";
 
 angular.module('ngl-bi.RunsServices', []).
	factory('searchService', ['$http', 'mainService', 'lists', function($http, mainService, lists){
		
		var searchService = {
				additionalFilters:[],
				getColumns:function(){
					var columns = [
								    {  	property:"code",
								    	header: "runs.code",
								    	type :"String",
								    	order:true
									},
									{	property:"typeCode",
										header: "runs.typeCode",
										type :"String",
								    	order:true
									},
									{	property:"sequencingStartDate",
										header: "runs.sequencingStartDate",
										type :"Date",
								    	order:true
									},
									{	property:"state.historical|filter:'F-RG'|get:'date'",
										header: "runs.endOfRG",
										type :"date",
								    	order:true
									},
									{	property:"state.code",
										filter:"codes:'state'",					
										header: "runs.stateCode",
										type :"String",
										edit:true,
										order:true,
										choiceInList:true,
								    	listStyle:'bt-select',
								    	possibleValues:'searchService.lists.getStates()'	
									},
									{	property:"valuation.valid",
										filter:"codes:'valuation'",					
										header: "runs.valuation.valid",
										type :"String",
								    	order:true
									},
									{	property:"valuation.resolutionCodes",
										header: "runs.valuation.resolutions",
										render:'<div bt-select ng-model="value.data.valuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
										type :"text",
										hide:true
									} 
								];						
					return columns;
				},
				isRouteParam:false,
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
							this.form.stateCodes = ["IW-V","IP-V"];
						}		
					}
					this.form.excludes =  ["treatments","lanes"];				
				},
				convertForm : function(){
					var _form = angular.copy(this.form);
					if(_form.fromDate)_form.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
					if(_form.toDate)_form.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();		
					return _form
				},
				refreshSamples : function(){
					if(this.form.projectCodes && this.form.projectCodes.length > 0){
						this.lists.refresh.samples({projectCodes:this.form.projectCodes});
					}
				},
				
				search : function(datatable){
					this.updateForm();
					mainService.setForm(this.form);
					datatable.search(this.convertForm());
				},
				
				reset : function(){
					this.form = {};
				},
				
				valuationStates : [{code:"IW-V",name:Codes("state.IW-V")}],
				states : function(){
					if (mainService.isHomePage('valuation')) {
						return this.valuationStates;
					}else{
						return this.lists.get('statetrue');
					}
				},
				
				initAdditionalFilters:function(){
					this.additionalFilters=[];
					if(lists.get("runs-addfilters") && lists.get("runs-addfilters").length === 1){
						var formFilters = [];
						var allFilters = angular.copy(lists.get("runs-addfilters")[0].filters);
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
		};
		
		return function(){
			
			searchService.lists.refresh.projects();
			searchService.lists.refresh.states({objectTypeCode:"Run", display:true},'statetrue');				
			searchService.lists.refresh.states({objectTypeCode:"Run"});							
			searchService.lists.refresh.types({objectTypeCode:"Run"});
			searchService.lists.refresh.resolutions({objectTypeCode:"Run"});
			searchService.lists.refresh.runs();
			searchService.lists.refresh.instruments({categoryCode:"illumina-sequencer"});
			searchService.lists.refresh.users();
			searchService.lists.refresh.filterConfigs({pageCodes:["runs-addfilters"]}, "runs-addfilters");
			
			searchService.lists.refresh.valuationCriterias({objectTypeCode:"Run",orderBy:'name'});
			
			
			
			if(angular.isDefined(mainService.getForm())){
				searchService.form = mainService.getForm();
			}else{
				searchService.reset();
			}
			
			return searchService;		
		}
	}
]);
 
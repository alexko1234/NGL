 "use strict";
 
 angular.module('ngl-sq.processesServices', []).
	factory('processesSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		var getColumns = function(){
			var typeCode = "";
			if(this.form.typeCode){
				typeCode = this.form.typeCode;
			}
			
			$http.get(jsRoutes.controllers.processes.tpl.Processes.searchColumns().url,{params:{"typeCode":typeCode}})
			.success(function(data, status, headers, config) {
				if(data!=null){
					searchService.datatable.setColumnsConfig(data);
				}
			})
			.error(function(data, status, headers, config) {
			
			});
		};
		
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				lists.refresh.containerSupportCategories();
				lists.refresh.projects();
				lists.refresh.processCategories();
				lists.refresh.containerSupports();
				lists.refresh.users();
				lists.refresh.states({objectTypeCode:"Process"});
				lists.refresh.processTypes();
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
					if(_form.fromDate)_form.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
					if(_form.toDate)_form.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();
					return _form;
				},
				
				resetForm : function(){
					this.form = {};									
				},
				
				initAdditionalFilters:function(){
					this.additionalFilters=[];
					if(this.form.typeCode !== undefined && lists.get("process-"+this.form.typeCode) && lists.get("process-"+this.form.typeCode).length === 1){
						var formFilters = [];
						var allFilters = angular.copy(lists.get("process-"+this.form.typeCode)[0].filters);
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
					if(this.additionalFilters !== undefined && this.additionalFilters.length === 0){
						this.initAdditionalFilters();
					}
					return this.additionalFilters;									
				},
				
				search : function(){
					this.updateForm();
					mainService.setForm(this.form);
					var jsonSearch = this.convertForm();
					if(jsonSearch != undefined){
						searchService.getColumns();
						this.datatable.search(jsonSearch);
					}
				},
				
				refreshSamples : function(){
					if(this.form.projectCodes && this.form.projectCodes.length > 0){
						this.lists.refresh.samples({projectCodes:this.form.projectCodes});
					}
				},
				
				changeProcessCategory : function(){
					this.additionalFilters=[];
					this.form.typeCode = undefined;
					this.lists.clear("processTypes");
					
					if(this.form.categoryCode){
						this.lists.refresh.processTypes({processCategoryCode:this.form.categoryCode});
					}
				},
				
				changeProcessTypeCode : function(){
					if(this.form.categoryCode){
						//searchService.search();
						lists.refresh.filterConfigs({pageCodes:["process-"+this.form.typeCode]}, "process-"+this.form.typeCode);
						this.initAdditionalFilters();
					}else{
						this.form.typeCode = undefined;	
					}
				},
		/*		changeProcessesSupportCode : function(val){

					console.log(val);
					return $http.get(jsRoutes.controllers.containerSupports.api.ContainerSupports.list().url,{params:{"codeRegex":val}}).success(function(data, status, headers, config) {
						console.log(data);

						return [data];				
					});

				}, */
				/**
				 * initialise the service
				 */
				init : function($routeParams, datatableConfig){
					initListService();
					
					//to avoid to lost the previous search
					if(datatableConfig && angular.isUndefined(mainService.getDatatable())){
						searchService.datatable = datatable(datatableConfig);
						mainService.setDatatable(searchService.datatable);
						//searchService.datatable.setColumnsConfig(getColumns());		
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
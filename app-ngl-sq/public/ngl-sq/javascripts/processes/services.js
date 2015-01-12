 "use strict";
 
 angular.module('ngl-sq.processesServices', []).
	factory('processesSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		var getColumns = function(){
			var typeCode = "";
			if(this.form.processType){
				typeCode = this.form.processType;
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
				lists.refresh.supports();
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
					if(_form.projectCodes || _form.sampleCodes || _form.processType 
							|| _form.processCategory || _form.processesSupportCode || _form.state || _form.states || _form.user
							|| _form.fromDate || _form.toDate || _form.experimentCode){
						var jsonSearch = {};
						if(_form.projectCodes){
							jsonSearch.projectCodes = _form.projectCodes;
						}			
						
						if(_form.sampleCodes){
							jsonSearch.sampleCodes = _form.sampleCodes;
						}				
						
						if(_form.experimentCode)
						{
							jsonSearch.experimentCode = _form.experimentCode;
						}
						
						if(_form.processType){
							jsonSearch.typeCode = _form.processType;
						}
						
						if(_form.processCategory){
							jsonSearch.categoryCode = _form.processCategory;
						}
						
						if(_form.processesSupportCode){
							jsonSearch.supportCode = _form.processesSupportCode;
						}
						
						if(_form.state){
							jsonSearch.stateCode = _form.state;
						}
						
						if(_form.states){
							jsonSearch.stateCodes = _form.states;
						}
						
						if(_form.user){
							jsonSearch.users = _form.user;
						}
						
						if(_form.containerSupportCategory){
							jsonSearch.containerSupportCategory = _form.containerSupportCategory;
						}
						
						if(_form.fromDate)jsonSearch.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
						if(_form.toDate)jsonSearch.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();
						
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
					if(this.form.projectCodes && this.form.projectCodes.length > 0){
						this.lists.refresh.samples({projectCodes:this.form.projectCodes});
					}
				},
				changeProcessCategory : function(){
					this.lists.clear("processTypes");
					if(this.form.processCategory){
						this.lists.refresh.processTypes({processCategoryCode:this.form.processCategory});
					}
				},
				changeProcessTypeCode : function(){
					if(this.form.processCategory){
						searchService.getColumns();
						searchService.search();
					}else{
						this.form.processType = undefined;	
					}
				},
		/*		changeProcessesSupportCode : function(val){

					console.log(val);
					return $http.get(jsRoutes.controllers.supports.api.Supports.list().url,{params:{"codeRegex":val}}).success(function(data, status, headers, config) {
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
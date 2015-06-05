 "use strict";
 
 angular.module('ngl-reagent.kitDeclarationsService', []).
	factory('kitsSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		var getColumns = function(){
			var columns = [];
			columns.push({
				"header":Messages("reagents.table.catalogName"),
				"property":"catalogCode",
				"order":true,
				"type":"text",
				"filter":"codes:'kitCatalogs'"
			});
			if(mainService.getHomePage() === 'boxes.new'){
				columns.push({
					"header":Messages("reagents.table.barCode"),
					"property":"barCode",
					"order":true,
					"type":"text"
				});
				columns.push({
					"header":Messages("reagents.table.expirationDate"),
					"property":"expirationDate",
					"order":true,
					"type":"date"
				});
				columns.push({
					"header":Messages("reagents.table.startToUseDate"),
					"property":"startToUseDate",
					"order":true,
					"type":"date"
				});
				columns.push({
					"header":Messages("reagents.table.stopToUseDate"),
					"property":"stopToUseDate",
					"order":true,
					"type":"date"
				});
			}
			columns.push({
				"header":Messages("reagents.table.orderCode"),
				"property":"orderCode",
				"order":true,
				"type":"text"
			});
			columns.push({
				"header":Messages("reagents.table.possibleUseNumber"),
				"property":"possibleUseNumber",
				"order":true,
				"type":"text"
			});
			columns.push({
				"header":Messages("reagents.table.stateCode"),
				"property":"state.code",
				"order":true,
				"type":"text",
				"filter":"codes:'state'"
			});
			
			return columns;
		};
		
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				lists.refresh.kitCatalogs();
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
					
					mainService.setForm(_form);
						
					return _form;	
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
]).
factory('boxesSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
	var getColumns = function(){
		var columns = [];
		columns.push({
			"header":Messages("reagents.table.catalogName"),
			"property":"catalogCode",
			"order":true,
			"type":"text",
			"filter":"codes:'kitCatalogs'"
		});
		columns.push({
			"header":Messages("reagents.table.barCode"),
			"property":"barCode",
			"order":true,
			"type":"text"
		});
		columns.push({
			"header":Messages("reagents.table.bundleBarCode"),
			"property":"bundleBarCode",
			"order":true,
			"type":"date"
		});
		columns.push({
			"header":Messages("reagents.table.catalogRefCode"),
			"property":"catalogRefCode",
			"order":true,
			"type":"date"
		});
		columns.push({
			"header":Messages("reagents.table.expirationDate"),
			"property":"expirationDate",
			"order":true,
			"type":"date"
		});
		columns.push({
			"header":Messages("reagents.table.startToUseDate"),
			"property":"startToUseDate",
			"order":true,
			"type":"date"
		});
		columns.push({
			"header":Messages("reagents.table.stopToUseDate"),
			"property":"stopToUseDate",
			"order":true,
			"type":"date"
		});
		columns.push({
			"header":Messages("reagents.table.orderCode"),
			"property":"orderCode",
			"order":true,
			"type":"text"
		});
		columns.push({
			"header":Messages("reagents.table.possibleUseNumber"),
			"property":"possibleUseNumber",
			"order":true,
			"type":"text"
		});
		columns.push({
			"header":Messages("reagents.table.stateCode"),
			"property":"state.code",
			"order":true,
			"type":"text",
			"filter":"codes:'state'"
		});
		
		return columns;
	};
	
	
	var isInit = false;
	
	var initListService = function(){
		if(!isInit){
			lists.refresh.kitCatalogs();
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
				
				mainService.setForm(_form);
					
				return _form;	
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
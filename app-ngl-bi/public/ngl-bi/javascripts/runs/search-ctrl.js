"use strict";

angular.module('home').controller('SearchCtrl', ['$scope', '$routeParams', 'datatable', 'mainService', 'tabService', 'searchService', 
                                                 function($scope, $routeParams, datatable, mainService, tabService, searchService) {
	var datatableConfig = {
			order :{mode:'local', by:'sequencingStartDate', reverse:true},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			pagination:{
				mode:'local'
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.runs.tpl.Runs.get(line.code).url,remove:true});
				}
			},
			hide:{active:true}
	};
	
	$scope.search = function(){
		$scope.searchService.search($scope.datatable);
	};
	
	$scope.reset = function(){
		$scope.searchService.reset();
	};
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("search").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.searchService = searchService();	
	$scope.searchService.setRouteParams($routeParams);
	
	//to avoid to lost the previous search
	if(angular.isUndefined(mainService.getDatatable())){
		$scope.datatable = datatable(datatableConfig);			
		mainService.setDatatable($scope.datatable);
		$scope.datatable.setColumnsConfig($scope.searchService.getColumns());
	}else{
		$scope.datatable = mainService.getDatatable();
	}
	
	if($scope.searchService.isRouteParam){
		$scope.search();
	}
}]);


angular.module('home').controller('SearchValuationCtrl', ['$scope', '$routeParams', 'datatable', 'mainService', 'tabService', 'searchService',
                                                          function($scope, $routeParams, datatable, mainService, tabService, searchService) {
	var datatableConfig = {
			order :{mode:'local', by:'sequencingStartDate', reverse:true},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			pagination:{
				mode:'local'
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.runs.tpl.Runs.valuation(line.code).url,remove:true});
				}
			},
			hide:{active:true}
	};
	
	$scope.search = function(){
		$scope.searchService.search($scope.datatable);
	};
	
	$scope.reset = function(){
		$scope.searchService.reset();
	};
	
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('valuation');
		tabService.addTabs({label:Messages('runs.page.tab.validate'),href:jsRoutes.controllers.runs.tpl.Runs.home("valuation").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.searchService = searchService();	
	$scope.searchService.setRouteParams($routeParams);
	
	
	//to avoid to lost the previous search
	if(angular.isUndefined(mainService.getDatatable())){
		$scope.datatable = datatable(datatableConfig);			
		mainService.setDatatable($scope.datatable);
		$scope.datatable.setColumnsConfig($scope.searchService.getColumns());
	}else{
		$scope.datatable = mainService.getDatatable();
	}
	$scope.search();
	
}]);

angular.module('home').controller('SearchStateCtrl', ['$scope', '$routeParams', 'datatable', 'mainService', 'tabService', 'searchService',
                                                      function($scope, $routeParams, datatable, mainService, tabService, searchService) {

	var datatableConfig = {
			order :{mode:'local', by:'sequencingStartDate', reverse:true},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			pagination:{
				mode:'local'
			},
			edit : {
				active:true,
				columnMode:true		    	
			},
			save : {
				active:true,
				url: jsRoutes.controllers.runs.api.State.updateBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code,state:line.state};}				
			},
			
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.runs.tpl.Runs.get(line.code).url,remove:true});
				}
			},
			hide:{active:true},
			messages : {active:true}
	};
	
	$scope.search = function(){
		$scope.searchService.search($scope.datatable);
	};
	
	$scope.reset = function(){
		$scope.searchService.reset();
	};
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('state');
		tabService.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("state").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.searchService = searchService();	
	$scope.searchService.setRouteParams($routeParams);
	
	//to avoid to lost the previous search
	if(angular.isUndefined(mainService.getDatatable())){
		$scope.datatable = datatable(datatableConfig);			
		mainService.setDatatable($scope.datatable);
		$scope.datatable.setColumnsConfig($scope.searchService.getColumns());
	}else{
		$scope.datatable = mainService.getDatatable();
	}
		
}]);



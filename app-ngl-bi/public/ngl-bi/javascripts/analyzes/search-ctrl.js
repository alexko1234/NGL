"use strict";

angular.module('home').controller('SearchCtrl',['$scope', '$routeParams', 'datatable', 'mainService', 'tabService', 'analysisSearchService',  'valuationService',
                                                function($scope, $routeParams, datatable, mainService, tabService, analysisSearchService, valuationService) {
	
	var datatableConfig = {
			pagination:{mode:'local'},			
			order :{mode:'local', by:'code', reverse : true},
			search:{
				url:jsRoutes.controllers.analyzes.api.Analyzes.list()
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.analyzes.tpl.Analyzes.get(line.code).url,remove:true});
				}
			},
			hide:{
				active:true
			}
	};

	$scope.search = function(){
		$scope.searchService.search();
	};
	
	$scope.reset = function(){
		$scope.searchService.resetForm();
	};
		
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('analyzes.menu.search'),href:jsRoutes.controllers.analyzes.tpl.Analyzes.home("search").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.searchService = analysisSearchService;	
	$scope.searchService.init($routeParams, datatableConfig);
	
	$scope.valuationService = valuationService();
	
	if($scope.searchService.isRouteParam){
		$scope.search();
	}

	
}]);


angular.module('home').controller('SearchValuationCtrl', ['$scope', '$routeParams', 'datatable', 'mainService', 'tabService', 'analysisSearchService', 'valuationService',
                                                          function($scope, $routeParams, datatable, mainService, tabService, analysisSearchService, valuationService) {
	
	var datatableConfig = {
			pagination:{mode:'local'},
			order :{mode:'local', by:'code', reverse : true},
			search:{
				url:jsRoutes.controllers.analyzes.api.Analyzes.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},			
			save : {
				active:true,
				url: jsRoutes.controllers.analyzes.api.Analyzes.valuationBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code,valuation:line.valuation};}				
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.analyzes.tpl.Analyzes.get(line.code).url,remove:true});
				}
			},
			hide:{
				active:true
			},
			messages : {active:true}
	};

	$scope.search = function(){
		$scope.searchService.search();
	};
	
	$scope.reset = function(){
		$scope.searchService.reset();
	};
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('valuation');
		tabService.addTabs({label:Messages('analyzes.page.tab.validate'),href:jsRoutes.controllers.analyzes.tpl.Analyzes.home("valuation").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.searchService = analysisSearchService;	
	$scope.searchService.init($routeParams, datatableConfig);
	
	$scope.valuationService = valuationService();
	
	$scope.search();
	
}]);


angular.module('home').controller('SearchStateCtrl', ['$scope', '$routeParams', 'datatable', 'mainService', 'tabService', 'analysisSearchService', 
                                                      function($scope, $routeParams, datatable, mainService, tabService, analysisSearchService) {
	
	var datatableConfig = {
			pagination:{mode:'local'},
			order :{mode:'local', by:'code', reverse : true},
			search:{
				url:jsRoutes.controllers.analyzes.api.Analyzes.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},			
			save : {
				active:true,
				url: jsRoutes.controllers.analyzes.api.Analyzes.stateBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code,state:line.state};}				
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.analyzes.tpl.Analyzes.get(line.code).url,remove:true});
				}
			},
			hide:{
				active:true
			},
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
		tabService.addTabs({label:Messages('analyzes.menu.search'),href:jsRoutes.controllers.analyzes.tpl.Analyzes.home("state").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}

	$scope.searchService = analysisSearchService;	
	$scope.searchService.init($routeParams, datatableConfig);
	
	if($scope.searchService.isRouteParam){
		$scope.search();
	}
	
}]);






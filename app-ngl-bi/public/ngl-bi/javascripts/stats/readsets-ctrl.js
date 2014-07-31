"use strict";

angular.module('home').controller('StatsSearchReadSetsCtrl',['$scope', '$routeParams', 'mainService', 'tabService','readSetsSearchService', 'valuationService','queriesConfigReadSetsService',
                                                              function($scope, $routeParams, mainService, tabService, readSetsSearchService, valuationService, queriesConfigReadSetsService) { 

	var datatableConfig = {
			pagination:{mode:'local'},
			order :{mode:'local',by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			hide:{
				active:true
			},
			otherButtons:{
				active:true,
				template:'<button class="btn btn-default" ng-click="addToBasket()" data-toggle="tooltip" title="'+Messages("button.query.addbasket")+'"><i class="fa fa-shopping-cart"></i> (<span ng-bind="queriesConfigService.queries.length"/>)</button>'
			}
	};

	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('stats.page.tab.readsets.select'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets").url});
		tabService.addTabs({label:Messages('stats.page.tab.readsets.config'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-config").url});		
		tabService.addTabs({label:Messages('stats.page.tab.show'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-show").url});		
		
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.search = function(){
		$scope.searchService.search();
	};
	
	$scope.reset = function(){
		$scope.searchService.resetForm();
	};

		
	$scope.addToBasket = function(){
		var query = {form : angular.copy($scope.searchService.convertForm())};
		query.form.includes = undefined;
		query.form.excludes = undefined;
		$scope.queriesConfigService.addQuery(query);			
	};
	
	$scope.searchService = readSetsSearchService;	
	$scope.searchService.init($routeParams, datatableConfig);
	$scope.valuationService = valuationService();
		
	$scope.queriesConfigService = queriesConfigReadSetsService;
	mainService.put('queriesConfigReadSetsService', $scope.queriesConfigService);
	
}]);

angular.module('home').controller('StatsConfigReadSetsCtrl',['$scope', 'mainService', 'tabService', 'basket', 'statsConfigReadSetsService','queriesConfigReadSetsService',
                                                              function($scope, mainService, tabService, basket, statsConfigReadSetsService, queriesConfigReadSetsService) { 
	
	if(angular.isUndefined(mainService.getBasket())){
		mainService.setBasket(basket());
	}
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('stats.page.tab.readsets.select'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets").url});
		tabService.addTabs({label:Messages('stats.page.tab.readsets.config'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-config").url});		
		tabService.addTabs({label:Messages('stats.page.tab.show'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-show").url});		
		tabService.activeTab(1); // desactive le lien !
	}
	
	if(angular.isUndefined(mainService.get('statsConfigReadSetsService'))){
		$scope.statsConfigService = statsConfigReadSetsService;
		$scope.statsConfigService.init();
		mainService.put('statsConfigReadSetsService', $scope.statsConfigService);
	}else{
		$scope.statsConfigService = mainService.get('statsConfigReadSetsService');
	}
	
	
	if(angular.isUndefined(mainService.get('queriesConfigReadSetsService'))){
		$scope.queriesConfigService = queriesConfigReadSetsService;
		mainService.put('queriesConfigReadSetsService', $scope.queriesConfigService);
	}else{
		$scope.queriesConfigService = mainService.get('queriesConfigReadSetsService');
	}	
	$scope.queriesConfigService.loadDatatable();	
}]);

angular.module('home').controller('StatsShowReadSetsCtrl',['$scope',  'mainService', 'tabService', 'chartsReadSetsService', 'queriesConfigReadSetsService',
                                                              function($scope,mainService, tabService, chartsReadSetsService, queriesConfigReadSetsService) { 
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('stats.page.tab.readsets.select'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets").url});
		tabService.addTabs({label:Messages('stats.page.tab.readsets.config'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-config").url});		
		tabService.addTabs({label:Messages('stats.page.tab.show'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-show").url});		
		
		tabService.activeTab(2); // desactive le lien !
	}
	
	$scope.queriesConfigService = queriesConfigReadSetsService;
	
	$scope.chartsReadSetsService = chartsReadSetsService;
	$scope.chartsReadSetsService.init();
}]);

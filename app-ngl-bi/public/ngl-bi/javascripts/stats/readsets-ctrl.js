"use strict";

angular.module('home').controller('StatsSearchReadSetsCtrl',['$scope', '$routeParams', 'mainService', 'tabService','readSetsSearchService', 'basket',
                                                              function($scope, $routeParams, mainService, tabService, readSetsSearchService, basket) { 

	var datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			hide:{
				active:true
			},
			otherButtons:{
				active:true,
				template:'<button class="btn btn-default" ng-click="addToBasket()" data-toggle="tooltip" title="'+Messages("button.query.addbasket")+'"><i class="fa fa-shopping-cart"></i> (<span ng-bind="basket.length()"/>)</button>'
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
		$scope.basket.reset();
		$scope.basket.add(query);		
	};
	
	$scope.searchService = readSetsSearchService;	
	$scope.searchService.init($routeParams, datatableConfig);
	
	if(angular.isUndefined(mainService.getBasket())){
		$scope.basket = basket();			
		mainService.setBasket($scope.basket);
	}else{
		$scope.basket = mainService.getBasket();
	}
	
	$scope.search();
	
	
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
	
	$scope.queriesConfigService = queriesConfigReadSetsService;
	$scope.queriesConfigService.init(mainService.getBasket().get());
	mainService.put('queriesConfigReadSetsService', $scope.queriesConfigService);
}]);

angular.module('home').controller('StatsShowReadSetsCtrl',['$scope',  'mainService', 'tabService', 'chartsReadSetsService',
                                                              function($scope,mainService, tabService, chartsReadSetsService) { 
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('stats.page.tab.readsets.select'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets").url});
		tabService.addTabs({label:Messages('stats.page.tab.readsets.config'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-config").url});		
		tabService.addTabs({label:Messages('stats.page.tab.show'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-show").url});		
		
		tabService.activeTab(2); // desactive le lien !
	}
	
	$scope.chartsReadSetsService = chartsReadSetsService;
	$scope.chartsReadSetsService.init();
}]);

"use strict";

angular.module('home').controller('StatsSearchLanesCtrl',['$scope', '$routeParams', '$location','$modal', 'mainService', '$http','tabService','runSearchService', 'valuationService','queriesConfigReadSetsService',
                                                             function($scope, $routeParams, $location, $modal, mainService, $http, tabService, runSearchService, valuationService, queriesConfigReadSetsService) { 

	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('stats.page.tab.lanes.select'),href:jsRoutes.controllers.stats.tpl.Stats.home("lanes").url});
		tabService.addTabs({label:Messages('stats.page.tab.lanes.config'),href:jsRoutes.controllers.stats.tpl.Stats.home("lanes-config").url});		
		tabService.addTabs({label:Messages('stats.page.tab.show'),href:jsRoutes.controllers.stats.tpl.Stats.home("lanes-show").url});		
		
		tabService.activeTab(0); // desactive le lien !
	}
	
	var datatableConfig = {
			group:{active:true},
			order :{mode:'local', by:'sequencingStartDate', reverse:true},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			pagination:{
				mode:'local'
			},
			hide:{active:true},
			exportCSV:{
				active:true
			},
			otherButtons:{
				active:true,
				template:'<button class="btn btn-default" ng-click="addToBasket()" data-toggle="tooltip" title="'+Messages("button.query.addbasket")+'"><i class="fa fa-shopping-cart"></i> (<span ng-bind="queriesConfigService.queries.length"/>)</button>'
			},
			name:"Runs"
	};
	
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
		
	
	$scope.searchService = runSearchService;	
	$scope.searchService.init($routeParams, datatableConfig)
	$scope.valuationService = valuationService();
	$scope.queriesConfigService = queriesConfigReadSetsService;
	
}]);

angular.module('home').controller('StatsConfigLanesCtrl',['$scope', 'mainService', 'tabService', 'basket', 'statsConfigLanesService','queriesConfigReadSetsService',
															function($scope, mainService, tabService, basket, statsConfigLanesService, queriesConfigReadSetsService) { 

	if(angular.isUndefined(mainService.getBasket())){
		mainService.setBasket(basket());
	}

	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('stats.page.tab.lanes.select'),href:jsRoutes.controllers.stats.tpl.Stats.home("lanes").url});
		tabService.addTabs({label:Messages('stats.page.tab.lanes.config'),href:jsRoutes.controllers.stats.tpl.Stats.home("lanes-config").url});		
		tabService.addTabs({label:Messages('stats.page.tab.show'),href:jsRoutes.controllers.stats.tpl.Stats.home("lanes-show").url});		
		
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.statsConfigService = statsConfigLanesService;
	$scope.queriesConfigService = queriesConfigReadSetsService;

	$scope.queriesConfigService.loadDatatable();	
}]);

angular.module('home').controller('StatsShowLanesCtrl',['$scope', '$routeParams', 'mainService', 'tabService', 'chartsLanesService',
                                                              function($scope, $routeParams, mainService, tabService, chartsLanesService) { 
	
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
			tabService.addTabs({label:Messages('stats.page.tab.lanes.select'),href:jsRoutes.controllers.stats.tpl.Stats.home("lanes").url});
			tabService.addTabs({label:Messages('stats.page.tab.lanes.config'),href:jsRoutes.controllers.stats.tpl.Stats.home("lanes-config").url});		
			tabService.addTabs({label:Messages('stats.page.tab.show'),href:jsRoutes.controllers.stats.tpl.Stats.home("lanes-show").url});			
			tabService.activeTab(0); // desactive le lien !
		}
	
	
	
	$scope.chartsLanesService = chartsLanesService;
	$scope.chartsLanesService.init();
	
}]);

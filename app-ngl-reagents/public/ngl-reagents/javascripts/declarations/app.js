"use strict";
 
angular.module('home', ['commonsServices','ngRoute','datatableServices','ngl-reagent.kitDeclarationsService'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/reagent-declarations/create/home', {
		templateUrl : jsRoutes.controllers.reagents.tpl.Kits.createOrEdit().url,
		controller : 'CreationKitsCtrl'
	});
	
	$routeProvider.when('/reagent-declarations/search/home', {
		templateUrl : jsRoutes.controllers.reagents.tpl.Kits.search().url,
		controller : 'SearchKitsCtrl'
	});
	
	$routeProvider.when('/reagent-declarations/kits/:kitCode', {
		templateUrl : jsRoutes.controllers.reagents.tpl.Kits.createOrEdit().url,
		controller : 'CreationKitsCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/reagent-declarations/create/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});
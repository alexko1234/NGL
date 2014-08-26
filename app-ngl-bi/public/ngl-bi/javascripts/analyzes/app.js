"use strict";
angular.module('home', ['ngRoute', 'datatableServices','commonsServices','biCommonsServices', 'ui.bootstrap','ngl-bi.AnalyzesServices'], function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/analyzes/search/home', {
		templateUrl : '/tpl/analyzes/search/default',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/analyzes/valuation/home', {
		templateUrl : '/tpl/analyzes/search/valuation',
		controller : 'SearchValuationCtrl'
	});
	
	$routeProvider.when('/analyzes/state/home', {
		templateUrl : '/tpl/analyzes/search/state',
		controller : 'SearchStateCtrl'
	});
		
	$routeProvider.when('/analyzes/:code', {
		templateUrl : '/tpl/analyzes/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.when('/analyzes/:code/:page', {
		templateUrl : '/tpl/analyzes/details',
		controller : 'DetailsCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/analyzes/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});


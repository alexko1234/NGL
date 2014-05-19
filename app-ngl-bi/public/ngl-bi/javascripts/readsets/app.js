"use strict";
angular.module('home', ['ngRoute', 'datatableServices','commonsServices','biCommonsServices', 'ui.bootstrap','ngl-bi.ReadSetsServices'], function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/readsets/search/home', {
		templateUrl : '/tpl/readsets/search',
		controller : 'SearchCtrl'
	});
	
	$routeProvider.when('/readsets/valuation/home', {
		templateUrl : '/tpl/readsets/search',
		controller : 'SearchValuationCtrl'
	});
	
	$routeProvider.when('/readsets/state/home', {
		templateUrl : '/tpl/readsets/search',
		controller : 'SearchStateCtrl'
	});
	
	$routeProvider.when('/readsets/batch/home', {
		templateUrl : '/tpl/readsets/search',
		controller : 'SearchBatchCtrl'
	});
	
	$routeProvider.when('/readsets/:code', {
		templateUrl : '/tpl/readsets/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.when('/readsets/:code/:page', {
		templateUrl : '/tpl/readsets/details',
		controller : 'DetailsCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/readsets/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});


"use strict";
angular.module('home', ['ngRoute','datatableServices','commonsServices','ui.bootstrap'], 
 function($routeProvider, $locationProvider) {
	$routeProvider.when('/projects/search/home', {
		templateUrl : '/tpl/projects/search/default',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/projects/valuation/home', {
		templateUrl : '/tpl/projects/search/valuation',
		controller : 'SearchValuationCtrl'
	});
	$routeProvider.when('/projects/:code', {
		templateUrl : '/tpl/projects/details',
		controller : 'DetailsCtrl'
	});

	$routeProvider.otherwise({redirectTo: '/projects/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
"use strict";

angular.module('home', ['commonsServices','ngRoute','ultimateDataTableServices','ui.bootstrap','ngl-sq.containerSupportsServices'], function($routeProvider, $locationProvider) {

	$routeProvider.when('/supports/search/home', {
		templateUrl : '/tpl/supports/search',
		controller : 'SearchCtrl'
	});
	
	$routeProvider.when('/supports/state/home', {
		templateUrl : '/tpl/supports/search',
		controller : 'SearchStateCtrl'
	});
	
	$routeProvider.when('/supports/:code', {
		templateUrl : '/tpl/supports/details',
		controller : 'DetailsCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/supports/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});
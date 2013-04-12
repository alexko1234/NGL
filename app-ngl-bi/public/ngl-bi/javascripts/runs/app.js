"use strict";
 
angular.module('home', ['datatableServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/runs/home', {
		templateUrl : '/tpl/runs/search',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/runs/:code', {
		templateUrl : '/tpl/runs/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/runs/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});

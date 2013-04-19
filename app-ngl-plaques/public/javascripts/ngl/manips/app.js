"use strict";
 
angular.module('home', ['datatableServices','basketServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/manips/home', {
		templateUrl : '/tpl/manips/search',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/manips/list', {
		templateUrl : '/tpl/manips/list',
		controller : 'ListCtrl'
	});
	
	$routeProvider.when('/manips/plaque', {
		templateUrl : '/tpl/manips/plaque',
		controller : 'PlaqueCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/manips/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});

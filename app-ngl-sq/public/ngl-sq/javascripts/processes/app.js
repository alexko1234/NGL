"use strict";
 
angular.module('home', ['datatableServices','basketServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/processes/home', {
		templateUrl : '/tpl/processes/search',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/processes/list', {
		templateUrl : '/tpl/processes/list',
		controller : 'ListCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/processes/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
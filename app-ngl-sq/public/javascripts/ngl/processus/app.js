"use strict";
 
angular.module('home', ['datatableServices','basketServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/processus/home', {
		templateUrl : '/tpl/processus/search',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/processus/list', {
		templateUrl : '/tpl/processus/list',
		controller : 'ListCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/processus/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
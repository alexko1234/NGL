"use strict";
 
angular.module('home', ['datatableServices','basketsServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/processus/home', {
		templateUrl : '/tpl/processus/search',
		controller : 'SearchCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/processus/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
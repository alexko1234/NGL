"use strict";
 
angular.module('home', ['datatableServices','basketsServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/containers/home', {
		templateUrl : '/tpl/containers/search',
		controller : 'SearchCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/containers/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
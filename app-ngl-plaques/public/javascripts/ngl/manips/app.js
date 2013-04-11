"use strict";
 
angular.module('home', ['datatableServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/manips/home', {
		templateUrl : '/tpl/manips/search',
		controller : 'SearchCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/manips/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});

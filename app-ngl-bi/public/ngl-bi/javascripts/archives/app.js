"use strict";
 
angular.module('home', ['datatableServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/archives/search/home', {
		templateUrl : '/tpl/archives/search',
		controller : 'SearchCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/archives/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});

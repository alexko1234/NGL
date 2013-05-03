"use strict";
 
angular.module('home', ['datatableServices','basketServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/plaques/search/home', {
		templateUrl : '/tpl/plaques/search',
		controller : 'SearchCtrl'
	});	
	
	$routeProvider.when('/plaques/new/home', {
		templateUrl : '/tpl/plaques/new/search',
		controller : 'SearchManipsCtrl'
	});
	
	$routeProvider.when('/plaques/:code', {
		templateUrl : '/tpl/plaques/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/plaques/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});

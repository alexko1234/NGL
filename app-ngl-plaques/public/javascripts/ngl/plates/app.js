"use strict";
 
angular.module('home', ['datatableServices','basketServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/plaques/search', {
		templateUrl : '/tpl/plaques/search',
		controller : 'SearchCtrl'
	});	
	$routeProvider.when('/plaques/new', {
		templateUrl : '/tpl/plaques/new/search',
		controller : 'SearchManipsCtrl'
	});
	$routeProvider.when('/plaques/:code', {
		templateUrl : '/tpl/plaques/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/plaques/search'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});

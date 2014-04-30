"use strict";
angular.module('home', ['ngRoute','datatableServices','commonsServices','ui.bootstrap', 'commonsProjectServices'], 
 function($routeProvider, $locationProvider) {
	$routeProvider.when('/umbrellaprojects/search/home', {
		templateUrl : '/tpl/umbrellaprojects/search/default',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/umbrellaprojects/:code', {
		templateUrl : '/tpl/umbrellaprojects/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.when('/umbrellaprojects/add/home', {
		templateUrl : '/tpl/umbrellaprojects/add',
		controller : 'AddCtrl'
	});

	$routeProvider.otherwise({redirectTo: '/umbrellaprojects/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
}
);

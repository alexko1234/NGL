"use strict";
angular.module('home', ['ngRoute','datatableServices','commonsServices','ui.bootstrap'], 
 function($routeProvider, $locationProvider) {
	$routeProvider.when('/projects/search/home', {
		templateUrl : '/tpl/projects/search/default',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/projects/:code', {
		templateUrl : '/tpl/projects/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.when('/projects/add/home', {
		templateUrl : '/tpl/projects/add',
		controller : 'SearchAddCtrl'
	});

	$routeProvider.otherwise({redirectTo: '/projects/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
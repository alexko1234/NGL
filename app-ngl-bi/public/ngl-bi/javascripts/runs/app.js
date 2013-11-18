"use strict";
angular.module('home', ['datatableServices','messagesServices','$strap.directives'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/runs/search/home', {
		templateUrl : '/tpl/runs/search/default',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/runs/validation/home', {
		templateUrl : '/tpl/runs/search/validation',
		controller : 'SearchValidationCtrl'
	});
	$routeProvider.when('/runs/state/home', {
		templateUrl : '/tpl/runs/search/state',
		controller : 'SearchStateCtrl'
	});
	$routeProvider.when('/runs/:code', {
		templateUrl : '/tpl/runs/details/default',
		controller : 'DetailsCtrl'
	});
	$routeProvider.when('/runs/:code/validation', {
		templateUrl : '/tpl/runs/details/validation',
		controller : 'ValidationDetailsCtrl'
	});

	$routeProvider.otherwise({redirectTo: '/runs/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});


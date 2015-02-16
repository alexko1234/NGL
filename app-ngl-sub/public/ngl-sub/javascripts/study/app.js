"use strict";
angular.module('home', ['ngRoute', 'commonsServices', 'datatableServices','ui.bootstrap'], 
	function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/study/create/home', {
		templateUrl : '/tpl/study/create',
		controller : 'CreateCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/study/create/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});


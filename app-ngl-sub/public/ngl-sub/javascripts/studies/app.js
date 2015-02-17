"use strict";
angular.module('home', ['ngRoute', 'commonsServices', 'datatableServices','ui.bootstrap', 'ngl-sub.StudiesServices'], 
	function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/studies/create/home', {
		templateUrl : '/tpl/studies/create',
		controller : 'CreateCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/studies/create/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});


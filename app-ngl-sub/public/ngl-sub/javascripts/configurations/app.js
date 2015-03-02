"use strict";
angular.module('home', ['ngRoute', 'commonsServices', 'datatableServices','ui.bootstrap', 'ngl-sub.ConfigurationsServices'], 
	function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/configurations/create/home', {
		// url qui va appeler controler java de type tpl
		templateUrl : '/tpl/configurations/create',
		controller : 'CreateCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/configurations/create/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});


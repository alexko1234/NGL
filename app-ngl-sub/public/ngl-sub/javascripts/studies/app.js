"use strict";
angular.module('home', ['ngRoute', 'commonsServices', 'datatableServices','ui.bootstrap', 'ngl-sub.StudiesServices'], 
	function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/sra/studies/create/home', {
		// url qui va appeler controler java de type tpl
		templateUrl : '/tpl/sra/studies/create',
		controller : 'CreateCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/sra/studies/create/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});


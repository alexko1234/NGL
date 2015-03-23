"use strict";
angular.module('home', ['ngRoute', 'commonsServices', 'datatableServices','ui.bootstrap', 'ngl-sub.SubmissionsServices'], 
	function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/submissions/start/home', {
		// url qui va appeler controler java de type tpl
		templateUrl : '/tpl/submissions/create',
		controller : 'CreateCtrl'
	});
	
	$routeProvider.when('/submissions/:code', {
		// url qui va appeler controler java de type tpl
		templateUrl : '/tpl/submissions/details',
		controller : 'DetailsCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/submissions/start/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});


"use strict";
angular.module('home', ['ngRoute', 'datatableServices','commonsServices','biCommonsServices', 'ui.bootstrap', 
                        'ngl-bi.StatsServices','ngl-bi.ReadSetsServices', 'basketServices'], function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/stats/readsets/home', {
		templateUrl : '/tpl/readsets/search',
		controller : 'StatsSearchReadSetsCtrl'
	});
	
	$routeProvider.when('/stats/readsets-config/home', {
		templateUrl : '/tpl/stats/config/readsets',
		controller : 'StatsConfigReadSetsCtrl'
	});
	
	$routeProvider.when('/stats/readsets-show/home', {
		templateUrl : '/tpl/stats/show',
		controller : 'StatsShowReadSetsCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/stats/readsets/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});


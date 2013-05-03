"use strict";
 
angular.module('home', ['datatableServices','basketServices'], function($routeProvider, $locationProvider) {
		
	$routeProvider.when('/plates/search/home', {
		templateUrl : '/tpl/plates/search',
		controller : 'SearchCtrl'
	});	
	
	$routeProvider.when('/plates/new/home', {
		templateUrl : '/tpl/plates/new/search',
		controller : 'SearchManipsCtrl'
	});
	
	$routeProvider.when('/plates/:code', {
		templateUrl : '/tpl/plates/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/plates/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});

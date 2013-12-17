"use strict";
 
angular.module('home', ['commonsServices','ngRoute','datatableServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/containers/search/home', {
		templateUrl : jsRoutes.controllers.containers.tpl.Containers.search().url,
		controller : 'SearchCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/containers/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
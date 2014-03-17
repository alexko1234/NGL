"use strict";
 
angular.module('home', ['commonsServices','ngRoute','datatableServices','ui.bootstrap'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/containers/search/home', {
		templateUrl : jsRoutes.controllers.containers.tpl.Containers.search().url,
		controller : 'SearchCtrl'
	});
	$routeProvider.otherwise({redirectTo: jsRoutes.controllers.containers.tpl.Containers.home("search").url});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
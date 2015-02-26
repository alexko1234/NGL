"use strict";

angular.module('home', ['commonsServices','ngRoute','datatableServices','ui.bootstrap','ngl-sq.containerSupportsServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/supports/search/home', {
		templateUrl : jsRoutes.controllers.containerSupports.tpl.ContainerSupports.search().url,
		controller : 'SearchCtrl'
	});
	$routeProvider.otherwise({redirectTo: jsRoutes.controllers.containerSupports.tpl.ContainerSupports.home("search").url});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});
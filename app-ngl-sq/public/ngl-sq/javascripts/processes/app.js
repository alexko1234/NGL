"use strict";
 
angular.module('home', ['datatableServices','basketServices', 'comboListsServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/processes/new/home', {
		templateUrl : jsRoutes.controllers.processes.tpl.Processes.searchContainers().url,
		controller : 'SearchContainerCtrl'
	});
	$routeProvider.when('/processes/new/:processTypeCode', {
		templateUrl : function(params){return jsRoutes.controllers.processes.tpl.Processes.newProcesses(params.processTypeCode).url},
		controller : 'ListNewCtrl'
	});
	$routeProvider.when('/processes/search/home', {
		templateUrl : function(params){return jsRoutes.controllers.processes.tpl.Processes.search("home").url},
		controller : 'SearchCtrl'
	});
	
	$routeProvider.when('/processes/search/:processTypeCode', {
		templateUrl : function(params){return jsRoutes.controllers.processes.tpl.Processes.search(params.processTypeCode).url},
		controller : 'SearchCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/processes/new/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
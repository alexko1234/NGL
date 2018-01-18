"use strict";
 
angular.module('home', ['commonsServices','ngRoute','ultimateDataTableServices','basketServices','ui.bootstrap','ngl-sq.processesServices','ngl-sq.samplesServices','ngl-sq.containersServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/processes/new-from-containers/home', {
		templateUrl : jsRoutes.controllers.processes.tpl.Processes.searchContainers().url,
		controller : 'SearchContainersCtrl'
	});
	
	$routeProvider.when('/processes/new-from-samples/home', {
		templateUrl : jsRoutes.controllers.processes.tpl.Processes.searchSamples().url,
		controller : 'SearchSamplesCtrl'
	});
	
	$routeProvider.when('/processes/assign-process-to-container/home', {
		templateUrl : jsRoutes.controllers.processes.tpl.Processes.searchContainers().url,
		controller : 'SearchContainersForProcessesAssignationCtrl'
	});
	
	$routeProvider.when('/processes/state/home', {
		templateUrl : jsRoutes.controllers.processes.tpl.Processes.search("home").url,
		controller : 'SearchStateCtrl'
	});
	$routeProvider.when('/processes/remove/home', {
		templateUrl : jsRoutes.controllers.processes.tpl.Processes.search("home").url,
		controller : 'SearchRemoveCtrl'
	});
	$routeProvider.when('/processes/new-from-containers/:processTypeCode', {
		templateUrl : function(params){return jsRoutes.controllers.processes.tpl.Processes.newProcesses(params.processTypeCode).url},
		controller : 'NewFromContainersCtrl'
	});
	$routeProvider.when('/processes/new-from-samples/:processTypeCode', {
		templateUrl : function(params){return jsRoutes.controllers.processes.tpl.Processes.newProcesses(params.processTypeCode).url},
		controller : 'NewFromSamplesCtrl'
	});
	$routeProvider.when('/processes/search/home', {
		templateUrl : function(params){return jsRoutes.controllers.processes.tpl.Processes.search("home").url},
		controller : 'SearchCtrl'
	});
	
	$routeProvider.when('/processes/search/:processTypeCode', {
		templateUrl : function(params){return jsRoutes.controllers.processes.tpl.Processes.search(params.processTypeCode).url},
		controller : 'SearchCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/processes/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({enabled: true, requireBase: false});
});
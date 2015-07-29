"use strict";
 
angular.module('home', ['ngRoute','ultimateDataTableServices','basketServices', 'commonsServices','ui.bootstrap','atomicTransfereServices','dragndropServices','ngl-sq.experimentsServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/experiments/new/home', {
		templateUrl : jsRoutes.controllers.experiments.tpl.Experiments.searchSupports().url,
		controller : 'SearchContainerCtrl'
	});
	$routeProvider.when('/experiments/new/:experimentTypeCode', {
		templateUrl : function(params){return jsRoutes.controllers.experiments.tpl.Experiments.newExperiments(params.experimentTypeCode).url},
		controller : 'ListNewCtrl'
	});
	$routeProvider.when('/experiments/search/home', {
		templateUrl : function(params){return jsRoutes.controllers.experiments.tpl.Experiments.search("home").url},
		controller : 'SearchCtrl'
	});
	
	$routeProvider.when('/experiments/search/:experimentTypeCode', {
		templateUrl : function(params){return jsRoutes.controllers.experiments.tpl.Experiments.search(params.experimentTypeCode).url},
		controller : 'SearchCtrl'
	});
	
	$routeProvider.when('/experiments/create/:experimentTypeCode', {
		templateUrl : function(params){return jsRoutes.controllers.experiments.tpl.Experiments.createOrEditExperiment().url},
		controller : 'CreateNewCtrl'
	});
	
	$routeProvider.when('/experiments/:experimentCode', {
		templateUrl : function(params){return jsRoutes.controllers.experiments.tpl.Experiments.createOrEditExperiment().url},
		controller : 'CreateNewCtrl'
	});
	
	$routeProvider.when('/experiments/:newExperiment/home', {
		templateUrl : function(params){return jsRoutes.controllers.experiments.tpl.Experiments.searchSupports(params.newExperiment).url},
		controller : 'SearchContainerCtrl'
	});
	
	
	$routeProvider.otherwise({redirectTo: '/experiments/new/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode({
		  enabled: true,
		  requireBase: false
		});
}).config(function ( $compileProvider) {

    $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|mailto|data):/);

});
"use strict";
 
angular.module('home', ['datatableServices','basketServices', 'comboListsServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/experiments/new/home', {
		templateUrl : jsRoutes.controllers.experiments.tpl.Experiments.searchContainers().url,
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
	
	$routeProvider.when('/experiments/edit/:experimentTypeCode', {
		templateUrl : function(params){return jsRoutes.controllers.experiments.tpl.Experiments.editExperiment(params.experimentTypeCode).url},
		controller : 'CreateNewCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/experiments/new/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
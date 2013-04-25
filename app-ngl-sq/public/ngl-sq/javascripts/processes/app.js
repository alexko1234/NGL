"use strict";
 
angular.module('home', ['datatableServices','basketServices', 'comboListsServices'], function($routeProvider, $locationProvider) {
	$routeProvider.when('/processes/new', {
		templateUrl : '/tpl/processes/new/search',
		controller : 'SearchContainerCtrl'
	});
	$routeProvider.when('/processes/new/:processTypeCode', {
		templateUrl : function(params){return '/tpl/processes/new/'+params.processTypeCode},
		controller : 'ListNewCtrl'
	});
	$routeProvider.when('/processes/search', {
		templateUrl : function(params){return '/tpl/processes/search'},
		controller : 'SearchCtrl'
	});
	$routeProvider.otherwise({redirectTo: '/processes/new'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});
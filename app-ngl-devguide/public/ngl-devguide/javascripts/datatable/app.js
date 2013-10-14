"use strict";
 
angular.module('home', ['directives','datatableServices'], function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/datatable', {
		templateUrl : '/assets/ngl-devguide/html/datatable/documentation.html',
		controller : 'DemoCtrl'
	});	
	
	$routeProvider.otherwise({redirectTo: '/'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});

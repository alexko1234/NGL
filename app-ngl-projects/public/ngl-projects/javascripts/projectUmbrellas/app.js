"use strict";
angular.module('home', ['ngRoute','datatableServices','commonsServices','ui.bootstrap', 'commonsProjectServices'], 
 function($routeProvider, $locationProvider) {
	$routeProvider.when('/projectUmbrellas/search/home', {
		templateUrl : '/tpl/projectUmbrellas/search/default',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/projectUmbrellas/:code', {
		templateUrl : '/tpl/projectUmbrellas/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.when('/projectUmbrellas/add/home', {
		templateUrl : '/tpl/projectUmbrellas/add',
		controller : 'AddCtrl'
	});

	$routeProvider.otherwise({redirectTo: '/projectUmbrellas/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
}
);

"use strict";
 
var containerApp = angular.module('containerApp', ['nglServices','basketsServices'])
.config(['$routeProvider', function($routeProvider) {
$routeProvider.when('/', {templateUrl: 'tpl/containers',controller: 'MainCtrl'})
.otherwise({redirectTo: '/test'});
}]);
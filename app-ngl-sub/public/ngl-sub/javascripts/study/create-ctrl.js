"use strict";

angular.module('home').controller('CreateCtrl',[ '$scope', '$routeParams' , 'mainService', 'lists', 'tabService', 
                                                 function($scope, $routeParams, mainService, lists, tabService) { 
  
	$scope.form={};
	$scope.lists=lists;
	console.log("Lists "+$scope.lists);
	
	$scope.save = function(){
		console.log($scope.form.studyTitle);
	};
}]);



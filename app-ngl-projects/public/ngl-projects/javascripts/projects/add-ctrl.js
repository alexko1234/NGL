"use strict";

angular.module('home').controller('AddCtrl', ['$scope', '$http', '$routeParams', 'messages',   
                                                  
  function($scope, $http, $routeParams, messages) {
		
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	
	/* buttons section */
	$scope.save = function(){
		//get data
		var objProj = angular.copy($scope.project);
		//to not save empty comment in Mongo
		if (objProj.comments[0].comment == "") {
			delete objProj.comments; 
		}
		//update database
		$http.post(jsRoutes.controllers.projects.api.Projects.save().url, objProj).success(function(data) {
			$scope.messages.setSuccess("save");
		});
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		updateData(true);				
	};
	
	var updateData = function(isCancel){
		$http.get(jsRoutes.controllers.projects.api.Projects.get($routeParams.code).url).success(function(data) {
			$scope.project = data;	
			$scope.stopEditMode();
		});
	};
	
	

	/* main section  */
	var init = function(){
		
		alert("inside");
		
		$scope.messages = messages();
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
		
		$scope.startEditMode();
		
	};
	
	init();
	
	
}]);
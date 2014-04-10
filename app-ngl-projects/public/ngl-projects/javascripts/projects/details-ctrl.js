"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'messages',   
                                                  
  function($scope, $http, $routeParams, messages) {
		
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	
	/* buttons section */
	$scope.update = function(){
		//get data
		var objProj = angular.copy($scope.project);
		//to not save empty comment in Mongo
		if (objProj.comments[0].comment == "") {
			delete objProj.comments; 
		}
		//update database
		$http.put(jsRoutes.controllers.projects.api.Projects.update($routeParams.code).url, objProj).success(function(data) {
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
		
		$scope.messages = messages();
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
		
		$scope.stopEditMode();

		$http.get(jsRoutes.controllers.projects.api.Projects.get($routeParams.code).url).success(function(data) {

			var data2 = data;
			
			if (data2.comments == null || data2.comments.length > 0) {
				var comments = new Array();
				var comment = new Object();
				comment.comment = "";
				comments[0] = comment;
				data2.comments = comments;				
			}
			
			$scope.project = data2;
			
			if($scope.getTabs().length == 0){
				$scope.addTabs({label:Messages('projects.menu.search'), href:jsRoutes.controllers.projects.tpl.Projects.home("search").url, remove:true});
				$scope.addTabs({label:$scope.project.code, href:jsRoutes.controllers.projects.tpl.Projects.get($scope.project.code).url, remove:true})									

				$scope.activeTab($scope.getTabs(1));
			}
			
		});
		
		
	};
	
	init();
	
	
}]);
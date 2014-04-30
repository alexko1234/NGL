"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', '$filter', 'messages', 'lists', 
                                                                                                    
  function($scope, $http, $routeParams, $filter, messages, lists) {
		
	$scope.reset = function(){
		$scope.form = {				
		}
	};
	
	
	/* buttons section */
	$scope.update = function(){
		var objProj = angular.copy($scope.umbrellaProject);
		objProj.projectCodes = $scope.form.selectedProjects;
		
		$http.put(jsRoutes.controllers.umbrellaprojects.api.UmbrellaProjects.update($routeParams.code).url, objProj).success(function(data) {
			$scope.messages.setSuccess("save");
		});
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		updateData(true);				
	};
	
	var updateData = function(isCancel){
		$http.get(jsRoutes.controllers.umbrellaprojects.api.UmbrellaProjects.get($routeParams.code).url).success(function(data) {
			$scope.umbrellaProject = data;	
			$scope.stopEditMode();
		});
	};
	
	
	$scope.addItem = function() {
		for (var i=0; i<$scope.form.allProjects.length; i++) {
			$scope.form.selectedProjects.push($scope.form.allProjects[i]);
		}
		
	};
	
	$scope.removeItem = function() {
		var itemSelected, idxItemSelected;
		for (var i=0; i<$scope.umbrellaProject.projectCodes.length; i++) {
			itemSelected = $scope.umbrellaProject.projectCodes[i];
			idxItemSelected = $scope.form.selectedProjects.indexOf(itemSelected);
			$scope.form.selectedProjects.splice(idxItemSelected,1);
		}
	};
	
		
	/* main section  */
	var init = function() {
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.lists.refresh.projects();
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
		
		$scope.stopEditMode();	

		$http.get(jsRoutes.controllers.umbrellaprojects.api.UmbrellaProjects.get($routeParams.code).url).success(function(data) {
			$scope.umbrellaProject = data;		
			
			$scope.form.allProjects = lists.getProjects(); 		
			if ($scope.umbrellaProject.projectCodes != null) {
				$scope.form.selectedProjects = angular.copy($scope.umbrellaProject.projectCodes);
			}
			else {
				$scope.form.selectedProjects = [];
			}
			if ($scope.form.allProjects == undefined) {
				$scope.form.allProjects = [];
			}
		
			if($scope.getTabs().length == 0){
				$scope.addTabs({label:Messages('projects.menu.search'), href:jsRoutes.controllers.umbrellaprojects.tpl.UmbrellaProjects.home("search").url, remove:true});
				$scope.addTabs({label:$scope.umbrellaProject.code, href:jsRoutes.controllers.umbrellaprojects.tpl.UmbrellaProjects.get($scope.umbrellaProject.code).url, remove:true});
				$scope.activeTab($scope.getTabs(1));
			}
			
		});
		
	};
	
	init();
	
}]);
"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', '$filter', 'messages', 'lists', 'mainService', 'tabService', 
                                                                                                    
  function($scope, $http, $routeParams, $filter, messages, lists, mainService, tabService) {
		
	$scope.form = {				
	}
	
	
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
		
		$scope.mainService = mainService;
		$scope.mainService.stopEditMode();

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
		
			if(tabService.getTabs().length == 0){
				tabService.addTabs({label:Messages('projects.menu.search'), href:jsRoutes.controllers.umbrellaprojects.tpl.UmbrellaProjects.home("search").url, remove:true});
				tabService.addTabs({label:$scope.umbrellaProject.code, href:jsRoutes.controllers.umbrellaprojects.tpl.UmbrellaProjects.get($scope.umbrellaProject.code).url, remove:true});
				tabService.activeTab(tabService.getTabs(1));
			}
			
		});
		
	};
	
	init();
	
}]);
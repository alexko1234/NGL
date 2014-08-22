"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists', 'mainService', 'tabService',    
                                                  
  function($scope, $http, $routeParams, messages, lists, mainService, tabService) {
		
	$scope.form = {	}
	
	
	/* buttons section */
	$scope.update = function(){
		var objProj = angular.copy($scope.project);
		
		$http.put(jsRoutes.controllers.projects.api.Projects.update($routeParams.code).url, objProj).success(function(data) {
			$scope.messages.isDetails = false;
			$scope.messages.showDetails=false;
			$scope.messages.setSuccess("save");
			mainService.stopEditMode();
		}).error(function(data, status, headers, config){
			$scope.messages.setError("save");
			$scope.messages.details = data;
			$scope.messages.isDetails = true;
			$scope.messages.showDetails=false;
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
		$scope.lists = lists;
		$scope.lists.refresh.states({objectTypeCode:"Project"});
		$scope.lists.refresh.projectTypes();
		$scope.lists.refresh.projectCategories();
		$scope.lists.refresh.umbrellaProjects();
		
		$scope.mainService = mainService;
		$scope.mainService.stopEditMode();
		
		$http.get(jsRoutes.controllers.projects.api.Projects.get($routeParams.code).url).success(function(data) {
			$scope.project = data;	
			
			if(tabService.getTabs().length == 0){
				tabService.addTabs({label:Messages('projects.menu.search'), href:jsRoutes.controllers.projects.tpl.Projects.home("search").url, remove:true});
				tabService.addTabs({label:$scope.project.code, href:jsRoutes.controllers.projects.tpl.Projects.get($scope.project.code).url, remove:true});							
				tabService.activeTab(tabService.getTabs(1));
			}
			
		});
		
	};
	
	init();
	
	
}]);


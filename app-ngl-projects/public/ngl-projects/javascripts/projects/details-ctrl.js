"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists', 'mainService', 'tabService',    
                                                  
  function($scope, $http, $routeParams, messages, lists, mainService, tabService) {
		
	$scope.form = {				
	}
	
	
	/* buttons section */
	$scope.update = function(){
		var objProj = angular.copy($scope.project);
		//to not save empty comment in Mongo
		if (objProj.comments[0].comment == null || objProj.comments[0].comment == "") {
			delete objProj.comments; 
		}
		else {
			objProj.comments[0].createUser = "ngsrg";
		}
		objProj.state = {code:objProj.state.code, user:"ngsrg"};		
		objProj.umbrellaProjectCodes = $scope.form.selectedProjects;
		
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
	
	$scope.addItem = function() {
		for (var i=0; i<$scope.form.allProjects.length; i++) {
			$scope.form.selectedProjects.push($scope.form.allProjects[i]);
		}
		
	};
	
	$scope.removeItem = function() {
		var itemSelected, idxItemSelected;
		for (var i=0; i<$scope.project.umbrellaProjectCodes.length; i++) {
			itemSelected = $scope.project.umbrellaProjectCodes[i];
			idxItemSelected = $scope.form.selectedProjects.indexOf(itemSelected);
			$scope.form.selectedProjects.splice(idxItemSelected,1);
		}
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

			var data2 = data;
			if (data2.comments == null || data2.comments.length == 0) {
				data2.comments = [{comment:""}];
			}
			$scope.project = data2;	
			
			$scope.form.allProjects = lists.getUmbrellaProjects(); 		
			if ($scope.project.umbrellaProjectCodes != null) {
				$scope.form.selectedProjects = angular.copy($scope.project.umbrellaProjectCodes);
			}
			else {
				$scope.form.selectedProjects = [];
			}
			if ($scope.form.allProjects == undefined) {
				$scope.form.allProjects = [];
			}
			
			if(tabService.getTabs().length == 0){
				tabService.addTabs({label:Messages('projects.menu.search'), href:jsRoutes.controllers.projects.tpl.Projects.home("search").url, remove:true});
				tabService.addTabs({label:$scope.project.code, href:jsRoutes.controllers.projects.tpl.Projects.get($scope.project.code).url, remove:true});							
				tabService.activeTab(tabService.getTabs(1));
			}
			
		});
		
	};
	
	init();
	
	
}]);
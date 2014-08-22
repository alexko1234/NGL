"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists', 'mainService', 'tabService',    
                                                  
  function($scope, $http, $routeParams, messages, lists, mainService, tabService) {
		
	$scope.form = {				
	}
	
	$scope.clearMessages  = function(){
		$scope.message = {clazz : undefined, text : undefined, showDetails : false, isDetails : false, details : []};
	};
	
	
	/* buttons section */
	$scope.update = function(){
		var objProj = angular.copy($scope.project);
		
		$http.put(jsRoutes.controllers.projects.api.Projects.update($routeParams.code).url, objProj).success(function(data) {
			$scope.message.clazz="alert alert-success";
			$scope.message.text=Messages('projects.msg.save.sucess');
			$scope.setTab(1,{label:$scope.project.code,href:jsRoutes.controllers.projects.tpl.Projects.get($scope.project.code).url,remove:false});
			$scope.activeTab(1);
			mainService.stopEditMode();
		}).error(function(data, status, headers, config){
			$scope.message.clazz="alert alert-danger";
			$scope.message.text=Messages('projects.msg.save.error');
			$scope.message.details = data;
			$scope.message.isDetails = true;
			$scope.message.showDetails=false;
		});
	};
	
	$scope.cancel = function(){
		$scope.clearMessages();	
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
		
		$scope.clearMessages();		
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


"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'datatable', 'messages', 'lists', 
                                                  
  function($scope, $http, $routeParams, datatable, messages, lists) {
		
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	
	/* main section  */
	var init = function(){
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}

		
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.stopEditMode();
		
		$http.get(jsRoutes.controllers.projects.api.Projects.get($routeParams.code).url).success(function(data) {
			$scope.project = data;	
			
			
			if($scope.getTabs().length == 0){
				$scope.addTabs({label:Messages('projects.menu.search'), href:jsRoutes.controllers.projects.tpl.Projects.home("search").url, remove:true});
				$scope.addTabs({label:$scope.project.code, href:jsRoutes.controllers.projects.tpl.Projects.get($scope.project.code).url, remove:true})									

				$scope.activeTab($scope.getTabs(1));
			}
			
		});
		
		
	};
	
	init();
	
	
	
}]);
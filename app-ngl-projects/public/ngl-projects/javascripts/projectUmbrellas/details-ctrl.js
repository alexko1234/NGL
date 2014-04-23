"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', '$filter', 'messages', 'lists', 
                                                                                                    
  function($scope, $http, $routeParams, $filter, messages, lists) {
		
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	
	/* buttons section */
	$scope.update = function(){
		var objProj = angular.copy($scope.projectUmbrella);
		
		objProj.projectCodes = $scope.form.selectedProjects;
		
		$http.put(jsRoutes.controllers.projectUmbrellas.api.ProjectUmbrellas.update($routeParams.code).url, objProj).success(function(data) {
			$scope.messages.setSuccess("save");
		});
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		updateData(true);				
	};
	
	var updateData = function(isCancel){
		$http.get(jsRoutes.controllers.projectUmbrellas.api.ProjectUmbrellas.get($routeParams.code).url).success(function(data) {
			$scope.projectUmbrella = data;	
			$scope.stopEditMode();
		});
	};
	
	
	$scope.addItem = function() {
		for (var i=0; i<$scope.form.allProjects.length; i++) {
			$scope.form.selectedProjects.push($scope.form.allProjects[i]);
		}
		
	};
	
	$scope.removeItem = function() {
		var itemSelected;
		var idx;
		for (var i=0; i<$scope.projectUmbrella.projectCodes.length; i++) {
			itemSelected = $scope.projectUmbrella.projectCodes[i];
			idx = $scope.form.selectedProjects.indexOf(itemSelected);
			$scope.form.selectedProjects.splice(idx,1);
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
		

		$http.get(jsRoutes.controllers.projectUmbrellas.api.ProjectUmbrellas.get($routeParams.code).url).success(function(data) {
			$scope.projectUmbrella = data;		
			
			$scope.form.allProjects = lists.getProjects(); 		
			if ($scope.projectUmbrella.projectCodes != null) {
				$scope.form.selectedProjects = angular.copy($scope.projectUmbrella.projectCodes);
			}
			else {
				$scope.form.selectedProjects = [];
			}
		
			if($scope.getTabs().length == 0){
				$scope.addTabs({label:Messages('projects.menu.search'), href:jsRoutes.controllers.projectUmbrellas.tpl.ProjectUmbrellas.home("search").url, remove:true});
				$scope.addTabs({label:$scope.projectUmbrella.code, href:jsRoutes.controllers.projectUmbrellas.tpl.ProjectUmbrellas.get($scope.projectUmbrella.code).url, remove:true});
				$scope.activeTab($scope.getTabs(1));
			}
			
		});
		
	};
	
	init();
	
	
}]);
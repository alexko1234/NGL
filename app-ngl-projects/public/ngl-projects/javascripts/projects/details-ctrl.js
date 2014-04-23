"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists',   
                                                  
  function($scope, $http, $routeParams, messages, lists) {
		
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	
	/* buttons section */
	$scope.update = function(){
		//copy data
		var objProj = angular.copy($scope.project);
		//to not save empty comment in Mongo
		if (objProj.comments[0].comment == null || objProj.comments[0].comment == "") {
			delete objProj.comments; 
		}
		else {
			objProj.comments[0].createUser = "ngsrg";
		}
		objProj.state = {code:objProj.state.code, user:"ngsrg"};	
		
		objProj.projectUmbrellaCodes = $scope.form.selectedProjects;
		
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
	
	$scope.addItem = function() {
		for (var i=0; i<$scope.form.allProjects.length; i++) {
			$scope.form.selectedProjects.push($scope.form.allProjects[i]);
		}
		
	};
	
	$scope.removeItem = function() {
		var itemSelected;
		var idx;
		for (var i=0; i<$scope.project.projectUmbrellaCodes.length; i++) {
			itemSelected = $scope.project.projectUmbrellaCodes[i];
			idx = $scope.form.selectedProjects.indexOf(itemSelected);
			$scope.form.selectedProjects.splice(idx,1);
		}
	};
	

	/* main section  */
	var init = function(){
		
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.lists.refresh.states({objectTypeCode:"Project"});
		$scope.lists.refresh.projectTypes();
		$scope.lists.refresh.projectCategories();
		$scope.lists.refresh.projectUmbrellas();
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
		
		$scope.stopEditMode();

		$http.get(jsRoutes.controllers.projects.api.Projects.get($routeParams.code).url).success(function(data) {

			var data2 = data;
			if (data2.comments == null || data2.comments.length == 0) {
				data2.comments = [{comment:""}];
			}
			$scope.project = data2;	
			
			$scope.form.allProjects = lists.getProjectUmbrellas(); 		
			if ($scope.project.projectUmbrellaCodes != null) {
				$scope.form.selectedProjects = angular.copy($scope.project.projectUmbrellaCodes);
			}
			else {
				$scope.form.selectedProjects = [];
			}
			
			if($scope.getTabs().length == 0){
				$scope.addTabs({label:Messages('projects.menu.search'), href:jsRoutes.controllers.projects.tpl.Projects.home("search").url, remove:true});
				$scope.addTabs({label:$scope.project.code, href:jsRoutes.controllers.projects.tpl.Projects.get($scope.project.code).url, remove:true});							
				$scope.activeTab($scope.getTabs(1));
			}
			
		});
		
	};
	
	init();
	
	
}]);
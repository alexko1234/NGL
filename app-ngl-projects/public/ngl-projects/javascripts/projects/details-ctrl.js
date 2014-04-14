"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists',   
                                                  
  function($scope, $http, $routeParams, messages, lists) {
		
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
		var typeCodes = objProj.typeCodes; 
		var catgCodes = objProj.categoryCodes;
		var stateCodes = objProj.state.codes;
		delete objProj.typeCodes;
		delete objProj.categoryCodes;
		delete objProj.state;
		objProj.typeCode = typeCodes[0];
		objProj.categoryCode = catgCodes[0];
		objProj.state = new Object();
		objProj.state.code = stateCodes[0];
		objProj.state.user = "ngsrg";
		objProj.state.date = new Date();
		
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
		$scope.lists = lists;
		
		$scope.lists.refresh.states({objectTypeCode:"Project"});
		$scope.lists.refresh.projectTypes();
		$scope.lists.refresh.projectCategories();
		
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
		
		$scope.stopEditMode();

		$http.get(jsRoutes.controllers.projects.api.Projects.get($routeParams.code).url).success(function(data) {

			var data2 = data;
			
			if (data2.comments == null || data2.comments.length == 0) {
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
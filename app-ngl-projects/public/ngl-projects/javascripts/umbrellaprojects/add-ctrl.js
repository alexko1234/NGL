angular.module('home').controller('AddCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists', 
  function($scope, $http, $routeParams, messages, lists) {
	
	/* buttons section */
	$scope.save = function(){
		var umbrellaProject = {code:$scope.form.code, name:$scope.form.name, projectCodes:$scope.form.selectedProjects};			
		$http.post(jsRoutes.controllers.umbrellaprojects.api.UmbrellaProjects.save().url, umbrellaProject).success(function(data) {
			$scope.messages.setSuccess("save");
		});
	};

	$scope.reset = function(){
		$scope.form = {
				
		}
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
	
	
	var init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.lists.refresh.projects();
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
		
		$scope.form.allProjects = lists.getProjects(); 	
		if ($scope.form.allProjects == undefined) {
			$scope.form.allProjects = [];
		}
		$scope.form.selectedProjects = [];
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('add');
		}
	};
	
	init();
}]);

angular.module('home').controller('AddCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists', 
  function($scope, $http, $routeParams, messages, lists) {
	
	/* buttons section */
	$scope.save = function(){
		var projectUmbrella = {code:$scope.form.code, name:$scope.form.name, projectCodes:$scope.form.selectedProjects};			
		$http.post(jsRoutes.controllers.projectUmbrellas.api.ProjectUmbrellas.save().url, projectUmbrella).success(function(data) {
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
		var itemSelected;
		var idx;
		for (var i=0; i<$scope.projectUmbrella.projectCodes.length; i++) {
			itemSelected = $scope.projectUmbrella.projectCodes[i];
			idx = $scope.form.selectedProjects.indexOf(itemSelected);
			$scope.form.selectedProjects.splice(idx,1);
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
		$scope.form.selectedProjects = [];
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('add');
		}
	};
	
	init();
}]);

angular.module('home').controller('AddCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists', function($scope, $http, $routeParams, messages, lists) {
	

	/* buttons section */
	$scope.save = function(){
		var endProcess = false;
		
		//to not save empty comment in Mongo
		var comments = null;
		var txt = $scope.form.comment;
		if (txt != null && txt != "") {
			comments =[{comment:txt, creationDate:new Date(), createUser:"ngsrg"}];
		}
		//end
		
		var project = new Object();			
		project.code = $scope.form.code;
		project.name = $scope.form.name;
		project.typeCode = $scope.form.projectType;
		project.categoryCode =  $scope.form.projectCategory;
		project.comments = comments;		
		project.state = {code:$scope.form.state.code, user:"ngsrg"};
		project.projectUmbrellaCodes = $scope.form.selectedProjects;
		
		endProcess = true;
		
		if (endProcess) {
			$http.post(jsRoutes.controllers.projects.api.Projects.save().url, project).success(function(data) {
				$scope.messages.setSuccess("save");
			});
		}
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
		for (var i=0; i<$scope.project.projectUmbrellaCodes.length; i++) {
			itemSelected = $scope.project.projectUmbrellaCodes[i];
			idx = $scope.form.selectedProjects.indexOf(itemSelected);
			$scope.form.selectedProjects.splice(idx,1);
		}
	};
	
	
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
		
		$scope.form.allProjects = lists.getProjectUmbrellas(); 		
		$scope.form.selectedProjects = [];
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('add');
		}
	};
	
	init();
}]);

angular.module('home').controller('AddCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists', function($scope, $http, $routeParams, messages, lists) {
	

	/* buttons section */
	$scope.save = function(){

		//to not save empty comment in Mongo
		var txt = $scope.form.comment;
		var endProcess = false;
		if (txt != "") {
			b = true;
			var comments = new Array();
			var comment = new Object();
			comment.comment = txt;
			comment.date = new Date();
			comment.user = "ngsrg"; // dynamically ?
			comments[0] = new Object();
			comments[0].comment = comment;
		}	
		
		var state = new Object();
		state.code = $scope.form.state.codes[0];
		//alert('state.code=' + state.code);
		state.user = "ngsrg";
		state.date = new Date();
			
		var typeCode = $scope.form.projectTypes[0];
		var categoryCode = $scope.form.projectCategories[0];
		
		var name = $scope.form.name;
		
		var traceInformation = new Object();
		traceInformation.creationDate = new Date();
		traceInformation.createUser = "ngsrg";
		
		var project = new Object();
			
		project.code = $scope.form.code;
		project.name = $scope.form.name;
		project.typeCode = typeCode;
		project.categoryCode = categoryCode;
		project.comments = comments;		
		project.traceInformation = traceInformation;
		
		endProcess = true;
		
		if (endProcess) {
			//save database
			$http.post(jsRoutes.controllers.projects.api.Projects.save().url, project).success(function(data) {
				$scope.messages.setSuccess("save");
			});
		}
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
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	
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

		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('add');
		}
	};
	
	init();
}]);

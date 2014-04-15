angular.module('home').controller('AddCtrl', ['$scope', '$http', '$routeParams', 'messages', 'lists', function($scope, $http, $routeParams, messages, lists) {
	

	/* buttons section */
	$scope.save = function(){

		//to not save empty comment in Mongo
		var endProcess = false;
		var comments = null;
		var txt = $scope.form.comment;
		if (txt != null && txt != "") {
			comments =[{comment:txt, creationDate:new Date(), createUser:"ngsrg"}];
		}	
		
		var state = {code:$scope.form.state.code, user:"ngsrg"};
			
		var traceInformation = new Object();
		traceInformation.creationDate = new Date();
		traceInformation.createUser = "ngsrg";
		
		var project = new Object();			
		project.code = $scope.form.code;
		project.name = $scope.form.name;
		project.typeCode = $scope.form.projectType;
		project.categoryCode =  $scope.form.projectCategory;
		project.comments = comments;		
		project.state = state;
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

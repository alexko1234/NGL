"use strict";

angular.module('home').controller('CreateCtrl',[ '$http', '$scope', '$routeParams' , 'mainService', 'lists', 'tabService','submissionsCreateService','messages',
                                                 function($http, $scope, $routeParams, mainService, lists, tabService, submissionsCreateService, messages) { 
  
	
	$scope.messages = messages();
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('create');
		tabService.addTabs({label:Messages('submissionss.menu.create'),href:jsRoutes.controllers.submissions.tpl.Submissions.home("create").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.createService = submissionsCreateService;	
	$scope.createService.init($routeParams);
	
	$scope.save = function(){
		mainService.setForm($scope.createService.form);
			$http.post(jsRoutes.controllers.submissions.api.Submissions.save().url, mainService.getForm()).success(function(data) {
				$scope.messages.setSuccess("save");
				$scope.codeSubmission=data;
			/*			
				$http.get(jsRoutes.controllers.experiments.api.Experiments.get($scope.codeSubmission).url, ).success(function(data)){
					$scope.experiments=data;

				}*/
			}).error(function(data){
				$scope.messages.setError("save");
			});
	};
	
	$scope.reset = function(){
		$scope.createService.resetForm();
		$scope.messages.clear();
	};
		
	
}]);



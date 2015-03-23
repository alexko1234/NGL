"use strict";

angular.module('home').controller('CreateCtrl',[ '$http', '$scope', '$routeParams' , 'mainService', 'lists', 'tabService','submissionsCreateService','messages',
                                                 function($http, $scope, $routeParams, mainService, lists, tabService, submissionsCreateService, messages) { 
  
	var datatableConfig = {
			pagination:{mode:'local'},			
			order :{mode:'local', by:'code', reverse : true},
			search:{
				url:jsRoutes.controllers.submissions.api.Submissions.list()
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.submissions.tpl.Submissions.get(line.code).url,remove:true});
				}
			},
			hide:{
				active:true
			},
			exportCSV:{
				active:true
			},
			name:"Analyses"
	};
	
	$scope.messages = messages();
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('create');
		tabService.addTabs({label:Messages('submissionss.menu.create'),href:jsRoutes.controllers.submissions.tpl.Submissions.home("create").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.createService = submissionsCreateService;	
	$scope.createService.init($routeParams, datatableConfig);
	
	$scope.save = function(){
		mainService.setForm($scope.createService.form);
		//$scope.createService.search();
			$http.post(jsRoutes.controllers.submissions.api.Submissions.save().url, mainService.getForm()).success(function(data) {
				$scope.messages.setSuccess("save");
				$scope.codeSubmission=data;
				$scope.createService.search();
			}).error(function(data){
				$scope.messages.setError("save");
			});
	};
	
	$scope.reset = function(){
		$scope.createService.resetForm();
		$scope.messages.clear();
	};
		
	
}]);



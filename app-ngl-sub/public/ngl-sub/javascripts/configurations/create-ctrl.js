"use strict";

angular.module('home').controller('CreateCtrl',[ '$http', '$scope', '$routeParams' , 'mainService', 'lists', 'tabService','configurationsCreateService','messages',
                                                 function($http, $scope, $routeParams, mainService, lists, tabService, configurationsCreateService, messages) { 
  
	
	$scope.messages = messages();
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('create');
		tabService.addTabs({label:Messages('configurations.menu.create'),href:jsRoutes.controllers.configurations.tpl.Configurations.home("create").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.createService = configurationsCreateService;	
	$scope.createService.init($routeParams);
	
	$scope.save = function(){
		mainService.setForm($scope.createService.form);
			$http.post(jsRoutes.controllers.configurations.api.Configurations.save().url, mainService.getForm()).success(function(data) {
				$scope.messages.setSuccess("save");
			}).error(function(data){
				$scope.messages.setError("save");
			});
	};
	
	$scope.reset = function(){
		$scope.createService.resetForm();
		$scope.messages.clear();
	};
		
	
}]);



"use strict";

angular.module('home').controller('CreateCtrl',[ '$http', '$scope', '$routeParams' , 'mainService', 'lists', 'tabService','configurationsCreateService','messages',
                                                 function($http, $scope, $routeParams, mainService, lists, tabService, configurationsCreateService, messages) { 
  
	
	$scope.messages = messages();
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('create');
		tabService.addTabs({label:Messages('configurations.menu.create'),href:jsRoutes.controllers.sra.configurations.tpl.Configurations.home("create").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.createService = configurationsCreateService;	
	$scope.createService.init($routeParams);
	
	$scope.save = function(){
		//get file
		$scope.file = document.getElementById("file").files[0];
		console.log("File "+$scope.file);
		mainService.setForm($scope.createService.form);
		var fd = new FormData();
        fd.append('file', $scope.file);
        $http.post(jsRoutes.controllers.sra.configurations.api.Configurations.save().url, fd,{
        	params: $scope.createService.form, 
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).success(function(data) {
			$scope.messages.clazz="alert alert-success";
			$scope.messages.text=Messages('configurations.msg.save.success')+" : "+data;
			$scope.messages.open();
		}).error(function(data){
			$scope.messages.setDetails(data);
			$scope.messages.setError("save");
		});
	};
	
	
/*	$scope.upload = function ($modalInstance) {
		console.log("Upload file");
		$scope.file = document.getElementById("file").files[0];
		console.log($scope.file);
		
		return $http({
		    method: 'POST',
		    url: "/administrations/upload",
		    headers: {'Content-Type': undefined},
		    data : {
		    	file:$scope.file,
		    },
		    transformRequest: formDataObject
		  }).success(function(data) {
				$scope.isError=false;
		  }).error(function(data){
				$scope.isError=true;
				$scope.errorMessage=data;
		  });
	};
*/
	
	$scope.reset = function(){
		$scope.createService.resetForm();
		$scope.messages.clear();
	};
		
	
}]);



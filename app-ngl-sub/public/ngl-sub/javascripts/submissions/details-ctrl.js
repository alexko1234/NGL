"use strict";

angular.module('home').controller('DetailsCtrl',[ '$http', '$scope', '$routeParams' , 'mainService', 'lists', 'tabService','messages',
                                                  function($http, $scope, $routeParams, mainService, lists, tabService, messages) { 


	var init = function(){
		$scope.messages = messages();
		// Attention appel de get du controller api.submissions qui est herite
		$http.get(jsRoutes.controllers.submissions.api.Submissions.get($routeParams.code).url).success(function(data) {
			$scope.submission = data;	
			console.log("Submission "+$scope.submission.code);
			console.log("Submission "+$scope.submission.sampleCodes);
			//Get samples
			$http.get(jsRoutes.controllers.samples.api.Samples.list().url, {listSampleCodes:$scope.submission.sampleCodes}).success(function(data)
					{
				$scope.samples = data;
					});
			//Get experiments
			//Get runs

		});
	};

	init();

	$scope.save = function(){
		//Call service to save samples
		var error = false;
		console.log("Samples "+$scope.samples);
		for(var i=0; i<$scope.samples.length; i++){
			var sample = $scope.samples[i];
			console.log("Sample "+sample);
			$http.put(jsRoutes.controllers.samples.api.Samples.update(sample.code).url, sample).error(function(data)
			{
				error=true;
			});
		}
		
		if(error){
			$scope.messages.setError("save");
			updateData();
		}else{
			$scope.messages.setSuccess("save");
			mainService.stopEditMode();
			updateData();
		}				
	};

	$scope.cancel = function(){
		mainService.stopEditMode();
		updateData();				
	};

	$scope.activeEditMode = function(){
		mainService.startEditMode();			
	};

	var updateData = function(){
		$http.get(jsRoutes.controllers.samples.api.Samples.list().url, {listSampleCodes:$scope.submission.sampleCodes}).success(function(data)
		{
			$scope.samples = data;
		});
	}



}]);



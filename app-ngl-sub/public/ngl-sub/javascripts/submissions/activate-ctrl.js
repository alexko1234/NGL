"use strict";

angular.module('home').controller('ActivateCtrl',[ '$http', '$scope', '$routeParams' , '$q', 'mainService', 'lists', 'tabService','messages','submissionsActivateService',
	                                                  function($http, $scope, $routeParams, $q, mainService, lists, tabService, messages, submissionsActivateService) { 

	var submissionDTConfig = {
			pagination:{mode:'local'},			
			order :{mode:'local', by:'code', reverse : true},
			search:{
				//url:jsRoutes.controllers.submissions.api.Submissions.list().url+'?state=userValidate'
				url:jsRoutes.controllers.submissions.api.Submissions.list()
			},
			otherButtons:{
				active:true,
				template:'<button class="btn" title=\'@Messages("button.save")\' ng-click="activate()" data-toggle="tooltip" >'
						+'<i class="fa fa-save"></i></button>'
			},
			name:"Submissions"
	};	
	$scope.messages = messages();	

	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('activate');
		tabService.addTabs({label:Messages('submissions.menu.activate'),href:jsRoutes.controllers.submissions.tpl.Submissions.home("activate").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	// si on declare dans services => var sraVariables = {};
	// si on declare dans le controlleur :

	$scope.activateService = submissionsActivateService;	
	$scope.activateService.init($routeParams, submissionDTConfig);

	$scope.search = function(){
		if($scope.activateService.form.projCode!=null){
			$scope.activateService.search();
		}
		else{
			console.log("Cancel datatable");
			$scope.activateService.cancel();
		}
			
	};
	$scope.activate = function(){
		console.log("activate ");
		var queries = [];

		//Get data du datable
		console.log("Get data ");
		var tab_submissions = $scope.activateService.datatable.getData();
		//boucle data
		for(var i = 0; i < tab_submissions.length ; i++){
			console.log("submissionCode = " + tab_submissions[i].code + " state = "+ tab_submissions[i].state.code);
			queries.push($http.put(jsRoutes.controllers.submissions.api.Submissions.activate(tab_submissions[i].code).url));
		}	
		
		$q.all(queries).then(function(results){
			console.log("Check error");
			var error = false;
			for(var i = 0; i  < results.length; i++){
				var result = results[i];
				if(result.status !== 200){
					error = true;
				}
			}
			
			if(error){
				$scope.messages.setError("save");	
			}else{
				$scope.messages.setSuccess("save");
				$scope.activateService.search();
			}
		});	
		
		
	};
	
}]);



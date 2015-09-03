"use strict";

angular.module('home').controller('ConsultationCtrl',[ '$http', '$scope', '$routeParams' , '$q', 'mainService', 'lists', 'tabService','messages','submissionsConsultationService',
	                                                  function($http, $scope, $routeParams, $q, mainService, lists, tabService, messages, submissionsConsultationService) { 


	var submissionDTConfig = {
			pagination:{mode:'local'},			
			order :{mode:'local', by:'code', reverse : true},
			search:{
				url:jsRoutes.controllers.sra.submissions.api.Submissions.list()
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.sra.submissions.tpl.Submissions.get(line.code).url,remove:true});
				}
			},
			hide:{
				active:true
			},
			exportCSV:{
				active:true
			}, 
			name:"Submissions"
	};
	
	$scope.messages = messages();	

	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('consultation');
		tabService.addTabs({label:Messages('submissions.menu.consultation'),href:jsRoutes.controllers.sra.submissions.tpl.Submissions.home("consultation").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	// si on declare dans services => var sraVariables = {};
	// si on declare dans le controlleur :

	$scope.consultationService = submissionsConsultationService;	
	$scope.consultationService.init($routeParams, submissionDTConfig);

	$scope.search = function(){
		if($scope.consultationService.form.projCode!=null){
			$scope.consultationService.search();
		} else {
			console.log("Cancel datatable");
			$scope.consultationService.cancel();
		}	
	};

}]);

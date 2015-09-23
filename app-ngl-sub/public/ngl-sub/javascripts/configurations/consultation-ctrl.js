"use strict";

angular.module('home').controller('ConsultationCtrl',[ '$http', '$scope', '$routeParams' , '$q', 'mainService', 'lists', 'tabService','messages','configurationsConsultationService',
	                                                  function($http, $scope, $routeParams, $q, mainService, lists, tabService, messages, configurationsConsultationService) { 


	
	var configurationDTConfig = {
			name:'configurationDT',
			order :{by:'code',mode:'local', reverse:true},
			search:{
				url:jsRoutes.controllers.sra.configurations.api.Configurations.list()
			},
			pagination:{active:false},
			select:{active:true},
			showTotalNumberRecords:false,
			edit : {
				active:true,       // permettre edition des champs editables
				showButton : true, // bouton d'edition visible
				withoutSelect : true,
				columnMode : true
			},
			cancel : {
				showButton:true
			},
			hide:{
				active:true,
				showButton:true
			},
			exportCSV:{
				active:false
			},
			/*show:{                   // bouton pour epingler si on passe par details-ctrl.js 
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.sra.configurations.tpl.Configurations.get(line.code).url,remove:true});
				}
			},*/
			save : {
				active:true,
				showButton : true,
				changeClass : false,
				url:function(line){
					return jsRoutes.controllers.sra.configurations.api.Configurations.update(line.code).url; // jamais utilisé en mode local
				},
				method:'put',
				value:function(line){
					return line;
				},
			},
			columns : [
			        {property:"code",
			        	header: "configuration.code",
			        	type :"text",		    	  	
			        	order:true
			        },	
			        {property:"projectCode",
			        	header: "configuration.projectCode",
			        	type :"text",		    	  	
			        	order:false,
			        	edit:false,
			        	choiceInList:false  
			        },

			        {property:"librarySelection",
						header: "configuration.librarySelection",
						type :"String",
			        	hide:true,
			        	edit:true,
						order:false,
				    	choiceInList:true,
				    	listStyle:'bt-select-multiple',
				    	possibleValues:'sraVariables.librarySelection',
				    },
				    {property:"libraryStrategy",
						header: "configuration.libraryStrategy",
						type :"String",
						hide:true,
						edit:true,
						order:false,
						choiceInList:true,
						listStyle:'bt-select-multiple',
						possibleValues:'sraVariables.libraryStrategy',
				    },
					{property:"librarySource",
						header: "configuration.librarySource",
						type :"String",
						hide:true,
						edit:true,
						order:false,
						choiceInList:true,
						listStyle:'bt-select-multiple',
						possibleValues:'sraVariables.librarySource',
					},
					{property:"libraryConstructionProtocol",
						 header: "configuration.libraryConstructionProtocol",
						 type :"String",		    	  	
						 hide:true,
						 edit:true,
					},

					 {property:"state.code",
			        	  header: "configuration.state",
			        	  type :"text",		    	  	
			        	  order:false,
			        	  edit:false,
			        	  choiceInList:false
			        }
			 ],	
	};
	

	
	$scope.messages = messages();	

	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('consultation');
		tabService.addTabs({label:Messages('configurations.menu.consultation'),href:jsRoutes.controllers.sra.configurations.tpl.Configurations.home("consultation").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	// si on declare dans services => var sraVariables = {};
	// si on declare dans le controlleur : $scope.sraVariables = {};

	$scope.consultationService = configurationsConsultationService;	
	$scope.consultationService.init($routeParams, configurationDTConfig);

	$scope.search = function(){
		if($scope.consultationService.form.projCode!=null){
			$scope.consultationService.search();
		} else {
			console.log("Cancel datatable");
			$scope.consultationService.cancel();
		}	
	};

}]);

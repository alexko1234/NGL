"use strict";
 
 angular.module('ngl-sub.ConfigurationsServices', []).
	factory('configurationsCreateService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				createService.lists.refresh.projects();
				$http.get(jsRoutes.controllers.sra.api.Variables.get('strategySample').url)
				.success(function(data) {
					// initialisation de la variable createService.sraVariables.strategySample utilisée dans create.scala.html
					createService.sraVariables.strategySample = data;																					
				});
				$http.get(jsRoutes.controllers.sra.api.Variables.get('librarySelection').url)
				.success(function(data) {
					createService.sraVariables.librarySelection = data;																					
				});
				$http.get(jsRoutes.controllers.sra.api.Variables.get('libraryStrategy').url)
				.success(function(data) {
					createService.sraVariables.libraryStrategy = data;																					
				});
				$http.get(jsRoutes.controllers.sra.api.Variables.get('librarySource').url)
				.success(function(data) {
					createService.sraVariables.librarySource = data;																					
				});
				isInit=true;
			}
		};
		
		
		var createService = {
				isRouteParam : false,
				lists : lists,
				form : undefined,
				sraVariables : {},
				setRouteParams:function($routeParams){
					var count = 0;
					for(var p in $routeParams){
						count++;
						break;
					}
					if(count > 0){
						this.isRouteParam = true;
						this.form = $routeParams;
					}
				},
				
				
				resetForm : function(){
					this.form = {};	
				},
				
				
				
				
				/**
				 * initialization of the service
				 */
				init : function($routeParams){
					initListService();
					
					//to avoid to lost the previous search
					if(angular.isDefined(mainService.getForm())){
						createService.form = mainService.getForm();
					}else{
						createService.resetForm();						
					}
					
					if(angular.isDefined($routeParams)){
						this.setRouteParams($routeParams);
					}
				}
		};
		
		
		return createService;

	}	
]).factory('configurationsConsultationService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){

/*	
methode utilisée pour definir les colonnes du datatable mais dans notre cas elles sont directement definies
dans consultation-ctrl.js.
var getColumns = function(){
		var columns = [];
		columns.push({	property:"code",
			    	  	header: "configurations.code",
			    	  	type :"text",		    	  	
			    	  	order:true});
		
		return columns;
	};
*/	
	var isInit = false;
	
	var initListService = function(){
		if(!isInit){
			consultationService.lists.refresh.projects();
			isInit=true;
		}
	};
	
		
	var consultationService = {
			isRouteParam : false,
			lists : lists,
			form : undefined,
			datatable : undefined,
			
			// methode appelee pour remplir le tableau des configurations 
			// Recherche toutes les configurations pour projCode indiqué :
			search : function(){
				this.datatable.search({projCode:this.form.projCode});
				console.log("consultationService: " + this.form);
			},
			
			cancel : function(){
				this.datatable.setData([],0);
			},
			
			
			resetForm : function(){
				this.form = {};	
			},
			
			// important pour avoir le menu permettant d'epingler : 
			setRouteParams:function($routeParams){
					var count = 0;
					for(var p in $routeParams){
						count++;
						break;
					}
					if(count > 0){
						this.isRouteParam = true;
						this.form = $routeParams;
					}
				},
				
			//
			// initialization of the service
			 //
			init : function($routeParams, configurationDTConfig){
				initListService();
				
			
				//to avoid to lost the previous search
				if(configurationDTConfig && angular.isUndefined(mainService.getDatatable())){
					consultationService.datatable = datatable(configurationDTConfig);
					mainService.setDatatable(consultationService.datatable);
					// On definit la config du tableau configurationDT dans consultation-ctrl.js et les colonnes à afficher dans
					// consultation-ctrl.js ou bien dans services.js (dernier cas qui permet de reutiliser la definition des colonnes => factorisation du code)
					// Dans notre cas definition des colonnes dans consultation-ctrl.js d'ou ligne suivante en commentaire.
					//consultationService.datatable.setColumnsConfig(getColumns());	
						
				}else if(angular.isDefined(mainService.getDatatable())){
					consultationService.datatable = mainService.getDatatable();			
				}			
				//to avoid to lost the previous search
				if(angular.isDefined(mainService.getForm())){
					consultationService.form = mainService.getForm();	
				}else{
					consultationService.resetForm();						
				}
				
				if(angular.isDefined($routeParams)){
					this.setRouteParams($routeParams);
				}
			}
	};
	return consultationService;
}
]);
 
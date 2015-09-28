"use strict";
 
 angular.module('ngl-sub.StudiesServices', []).
	factory('studiesCreateService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				createService.lists.refresh.projects();
				$http.get(jsRoutes.controllers.sra.api.Variables.get('existingStudyType').url)
				.success(function(data) {
				// initialisation de la variable createService.sraVariables.exitingStudyType utilisée dans create.scala.html
				createService.sraVariables.existingStudyType = data;																					
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
]).factory('studiesConsultationService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){



//methode utilisée pour definir les colonnes 
var getColumns = function(){
		var columns = [];
			columns.push({property:"code",
			        	header: "study.code",
			        	type :"text",		    	  	
			        	order:true
			        	});		
			 columns.push({property:"projectCode",
			        	header: "study.projectCode",
			        	type :"text",		    	  	
			        	order:false,
			        	edit:false,
			        	choiceInList:false  
			        	});	
			columns.push({property:"accession",
			        	header: "study.accession",
			        	type :"text",		    	  	
			        	order:false,
			        	edit:false,
			        	choiceInList:false  
			        	});	
			/*columns.push({property:"releaseDate",
			        	header: "study.releaseDate",
			        	type :Date,		    	  	
			        	order:false,
			        	edit:false,
			        	choiceInList:false  
			        	});	*/
			 columns.push({property:"state.code",
			        	header: "study.state.code",
			        	type :"text",		    	  	
			        	order:true
			        	});	
			 columns.push({property:"centerName",
			        	header: "study.centerName",
			        	type :"text",		    	  	
			        	order:false,
			        	edit:false,
			        	choiceInList:false  
			        	});	
			  columns.push({property:"centerProjectName",
			        	header: "study.centerProjectName",
			        	type :"text",		    	  	
			        	order:false,
			        	edit:false,
			        	choiceInList:false  
			        	});			        			        			        
			  columns.push({property:"title",
						header: "study.title",
						type :"String",
			        	hide:true,
			        	edit:true,
						order:false,
				    	choiceInList:false
				    	});	
				    
			  columns.push({property:"studyAbstract",
						header: "study.studyAbstract",
						type :"String",
			        	hide:true,
			        	edit:true,
						order:false,
				    	choiceInList:false
				    	});	
			   columns.push({property:"description",
						header: "study.description",
						type :"String",
			        	hide:true,
			        	edit:true,
						order:false,
				    	choiceInList:false
				    	});	
			      
				columns.push({property:"existingStudyType",
						header: "study.existingStudyType",
						type :"String",
						hide:true,
						edit:true,
						order:false,
						choiceInList:true,
						listStyle:'bt-select-multiple',
						possibleValues:'consultationService.sraVariables.existingStudyType',
				    	});
		return columns;
	};
	
	
	var isInit = false;
	
	var initListService = function(){
		if(!isInit){
			consultationService.lists.refresh.projects();
			$http.get(jsRoutes.controllers.sra.api.Variables.get('existingStudyType').url)
				.success(function(data) {
					// initialisation de la variable sraVariables.existingStudyType 
					consultationService.sraVariables.existingStudyType = data;																					
			});
			isInit=true;
		}
	};
	var consultationService = {
			isRouteParam : false,
			lists : lists,
			form : undefined,
			datatable : undefined,
			sraVariables : {},
			
			//console.log("sraVariables :" + sraVariables); 
			// Recherche l'ensemble de projCode :
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
			init : function($routeParams, studiesDTConfig){
				initListService();
				
			
				//to avoid to lost the previous search
				if(studiesDTConfig && angular.isUndefined(mainService.getDatatable())){
					consultationService.datatable = datatable(studiesDTConfig);
					mainService.setDatatable(consultationService.datatable);
					// On definit la config du tableau studiesDTConfig dans consultation-ctrl.js et les colonnes à afficher dans
					// consultation-ctrl.js ou bien dans services.js (dernier cas qui permet de reutiliser la definition des colonnes => factorisation du code)
					// Dans notre cas definition des colonnes dans consultationService.js d'ou ligne suivante 
					consultationService.datatable.setColumnsConfig(getColumns());	
						
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
 		

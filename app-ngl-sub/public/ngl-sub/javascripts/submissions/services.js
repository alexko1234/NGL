"use strict";
 
 angular.module('ngl-sub.SubmissionsServices', []).
	factory('submissionsCreateService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		
		var getColumns = function(){
			var columns = [];
			columns.push({	property:"code",
				    	  	header: "submissions.code",
				    	  	type :"text",		    	  	
				    	  	order:true});
			columns.push({	property:"state.code",
							header: "submissions.state",
							type :"text",
							order:true});	
			return columns;
		};
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				createService.lists.refresh.projects();
				isInit=true;
			}
		};
		
		
		var createService = {
				isRouteParam : false,
				lists : lists,
				form : undefined,
				datatable : undefined,
				internalStudies : true,
				externalStudies : false,
				
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
				
				refreshSubmissions : function(){
					if(this.form.projCode!==null && this.form.projCode !== undefined){
						this.datatable.search({projCode:this.form.projCode, state:'new'});
					}
				},
				
				refreshSraStudies : function(){
					if(this.form.projCode!==null && this.form.projCode !== undefined){
						// appel de refresh.sraStudies dans lists de common.js
						this.lists.refresh.sraStudies({projCode:this.form.projCode});
					}
				},
				
				refreshSraConfigurations : function(){
					if(this.form.projCode!==null && this.form.projCode !== undefined){
						// appel de refresh.sraConfigurations dans lists de common.js
						this.lists.refresh.sraConfigurations({projCode:this.form.projCode});
					}
				},
				refreshReadSets : function(){
					if(this.form.projCode!==null && this.form.projCode !== undefined){
						// appel de refresh.ReadSets dans lists de common.js
						this.lists.refresh.readSets({projectCode:this.form.projCode});
					}
				},
				// fonction qui recupere objet configuration dont le code est saisi par utilisateur et qui en fonction
				// de config.strategy_internal_study determine si la variable internal_studies est à true ou false.
				displayStudies : function(){
					if(this.form.configurationCode !== null && this.form.configurationCode !== undefined){
						//get configuration
						$http.get(jsRoutes.controllers.sra.configurations.api.Configurations.get(this.form.configurationCode).url).success(function(data) {
							if(data.strategyStudy == 'strategy_internal_study'){
								createService.internalStudies=true;
								createService.externalStudies=false;
							}else{
								createService.internalStudies=false;
								createService.externalStudies=true;	
							}
						});
					}
				},
				

				// methode appelee pour remplir le tableau des soumissions 
				search : function(){
					this.datatable.search({projCode:this.form.projCode, state:'new'});
				},
				/**
				 * initialization of the service
				 */
				init : function($routeParams, submissionDTConfig){
					initListService();
					
					//to avoid to lost the previous search
					if(submissionDTConfig && angular.isUndefined(mainService.getDatatable())){
						createService.datatable = datatable(submissionDTConfig);
						mainService.setDatatable(createService.datatable);
						createService.datatable.setColumnsConfig(getColumns());		
					}else if(angular.isDefined(mainService.getDatatable())){
						createService.datatable = mainService.getDatatable();		
						if(this.form.projCode!==null && this.form.projCode !== undefined){
							this.datatable.search({projCode:this.form.projCode, state:'new'});
						}
					}	
					
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
	
]).factory('submissionsActivateService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
	var getColumns = function(){
		var columns = [];
		columns.push({	property:"code",
			    	  	header: "submissions.code",
			    	  	type :"text",		    	  	
			    	  	order:true});
		columns.push({	property:"state.code",
						header: "submissions.state",
						type :"text",
						order:true});	
		return columns;
	};
		
	var isInit = false;
	
	var initListService = function(){
		if(!isInit){
			activateService.lists.refresh.projects();
			isInit=true;
		}
	};
	
	var activateService = {
			isRouteParam : false,
			lists : lists,
			form : undefined,
			datatable : undefined,
			
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
			

			// methode appelee pour remplir le tableau des soumissions 
			search : function(){
				this.datatable.search({projCode:this.form.projCode, state:'userValidate'});
			},
			cancel : function(){
				this.datatable.setData([],0);
			},
			//
			// initialization of the service
			 //
			init : function($routeParams, submissionDTConfig){
				initListService();
				
				//to avoid to lost the previous search
				if(submissionDTConfig && angular.isUndefined(mainService.getDatatable())){
					activateService.datatable = datatable(submissionDTConfig);
					mainService.setDatatable(activateService.datatable);
					activateService.datatable.setColumnsConfig(getColumns());		
				}else if(angular.isDefined(mainService.getDatatable())){
					activateService.datatable = mainService.getDatatable();			
				}	
				
				//to avoid to lost the previous search
				if(angular.isDefined(mainService.getForm())){
					activateService.form = mainService.getForm();
				}else{
					activateService.resetForm();						
				}
				
				if(angular.isDefined($routeParams)){
					this.setRouteParams($routeParams);
				}
			}
	};
	return activateService;
	}
]).factory('submissionsConsultationService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
	var getColumns = function(){
		var columns = [];
		columns.push({	property:"code",
			    	  	header: "submissions.code",
			    	  	type :"text",		    	  	
			    	  	order:true});
		columns.push({	property:"state.code",
						header: "submissions.state",
						type :"text",
						order:true});	
		return columns;
	};
	
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
			
			// methode appelee pour remplir le tableau des submissions
			// Recherche toutes les submissions pour projCode indiqué :
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
			init : function($routeParams, submissionDTConfig){
				initListService();
				
				//to avoid to lost the previous search
				if(submissionDTConfig && angular.isUndefined(mainService.getDatatable())){
					consultationService.datatable = datatable(submissionDTConfig);
					mainService.setDatatable(consultationService.datatable);
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
])/*.factory('submissionsValidateService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){

	var validateService = {
			isRouteParam : false,
			lists : lists,
			form : undefined,
			datatable : undefined,
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
			
			// methode appelee pour remplir le tableau des soumissions 
			search : function(){
				this.datatable.search({projCode:this.form.projCode, state:'userValidate'});
			},
			cancel : function(){
				this.datatable.setData([],0);
			},
			init : function($routeParams, submissionDTConfig){
				initListService();
				
				//to avoid to lost the previous search
				if(submissionDTConfig && angular.isUndefined(mainService.getDatatable())){
					validateService.datatable = datatable(submissionDTConfig);
					mainService.setDatatable(validateService.datatable);
					validateService.datatable.setColumnsConfig(getColumns());		
				}else if(angular.isDefined(mainService.getDatatable())){
					validateService.datatable = mainService.getDatatable();			
				}	
				
				//to avoid to lost the previous search
				if(angular.isDefined(mainService.getForm())){
					validateService.form = mainService.getForm();
				}else{
					validateService.resetForm();						
				}
				
				if(angular.isDefined($routeParams)){
					this.setRouteParams($routeParams);
				}
			}
	};
	return validateService;
])*/;

 
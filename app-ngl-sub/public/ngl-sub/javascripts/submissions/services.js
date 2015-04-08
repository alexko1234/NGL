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
]);
 
"use strict"

angular.module('home').controller('SwitchIndexSearchCtrl', ['$scope', '$filter', '$http', 'lists','mainService','tabService', 'messages','datatable',
                                                 function($scope, $filter, $http, lists,mainService,tabService, messages, datatable) {
	
	
	
	var datatableConfig = {
			name:"swichIndex",
			columns:[
			  		 {
			        	 "header":"Code",
			        	 "property":"code",
			        	 "order":true,
						 "edit":false,
						 "type":"text",
			        	 "position":1
			         },
			         {
			        	 "header":"Collection",
			        	 "property":"collectionName",
			        	 "order":true,
						 "edit":false,
						 "type":"text",
			        	 "position":2
			         },
			         {
			        	 "header":"Type",
			        	 "property":"typeCode",
			        	 "filter":"codes:'type'",
			        	 "order":true,
						 "edit":false,
						 "type":"text",
			        	 "position":3
			         },
			         {
			        	 "header":"Projet",
			        	 "property":"projectCode",
			        	"order":true,
						 "edit":false,
						 "type":"text",
			        	 "position":4
			         },
			         {
			        	 "header":"Echantillon",
			        	 "property":"sampleCode",
			        	"order":true,
						 "edit":false,
						 "type":"text",
			        	 "position":5
			         },
			         {
			        	 "header":"Type",
			        	 "property":"typeCode",
			        	 "filter":"codes:'type'",
			        	 "order":true,
						 "edit":false,
						 "type":"text",
			        	 "position":6
			         },
			         {
			        	 "header":"Propriété mise à jour",
			        	 "property":"contentPropertyNameUpdated",
			        	 "order":true,
						 "edit":false,
						 "type":"text",
			        	 "position":7
			         },
			         {
			        	 "header":"Valeur courante",
			        	 "property":"currentValue",
			        	 "order":true,
						 "edit":false,
						 "type":"text",
			        	 "position":8
			         },
			         {
			        	 "header":"Valeur nouvelle",
			        	 "property":"newValue",
			        	 "order":true,
						 "edit":true,
						 "choiceInList" : true, 
						 "possibleValues":"searchService.lists.getTags()",
						 "type":"text",
			        	 "position":9
			         }
			        
			         ],
			compact:true,
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local',  
				active:true,
				by:'code'
			},
			remove:{
				active: false
			},
			save:{
				active:true,
	        	mode:'remote'				
			},
			edit:{
				active: true,
				columnMode:true
			},
			messages:{
				active:true,
				columnMode:true
			},
			exportCSV:{
				active:true
			},			
			showTotalNumberRecords:false
	};
	
	$scope.searchService = {
			
			form : {},
			lists : lists,
			
			datatable : undefined,
			
			resetSampleCodes : function(){
				this.form.sampleCode = undefined;									
			},
		
			refreshSamples : function(){
				if(this.form.projectCode){
					lists.refresh.samples({projectCodes:[this.form.projectCode]});
				}
			},
			
			reset : function(){
				this.form = {};									
			},
			
			updateForm : function(){
				if(!this.form.collectionNames){
					this.form.collectionNames = ["ngl_sq.Container","ngl_sq.Process","ngl_sq.Experiment","ngl_bi.ReadSetIllumina"];
					this.form.contentPropertyNameUpdated = "tag";
				}
			},
						
			search : function(){
				$scope.messages.clear();
				this.updateForm();
				 $http.get(jsRoutes.controllers.admin.supports.api.NGLObjects.list().url,{params:this.form})
				 	.success(function(result){
				 		$scope.searchService.datatable.setData(result);
				 	}).error(function(data){
				 		$scope.messages.setError("get");
				 		$scope.messages.setDetails(data);
				 		$scope.messages.showDetails=true;
				 	});
				
			}
			
	};
	
	//init
	if(angular.isUndefined($scope.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('admin.supports.switch-index.tabs.search'),href:jsRoutes.controllers.admin.supports.tpl.Supports.home('switch-index').url,remove:false});
		tabService.activeTab(0);
	}
	$scope.messages = messages();
	$scope.searchService.datatable =  datatable(datatableConfig);
	
}]);
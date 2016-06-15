"use strict";

angular.module('home').controller('NewFromFileCtrl', ['$scope', 'lists', 'mainService', 'tabService','datatable', 'messages',
                                                  function($scope,lists,mainService,tabService,datatable, messages){

	
	/*
	 Object Structure
	 
	  {
	  	sample : {
	  		projectCode: undefined
	  		code : undefined,
	  		sampleTypeCode: undefined,
	  		importTypeCode : undefined
	  	},
	  	support : {
	  		code:undefined,
	  		categoryCode:undefined,
	  		storageCode : undefined,
	  		storage : {
	  			location : undefined,
	  			freezer : undefined,
	  			rack:undefined,
	  			box:undefined,
	  			boxPosition:undefined
	  		},
	  		comment:undefined
	  	}
	  	container : {} ????
	  
	  
	  }
	 
	 */
	
	
	var datatableConfig = {
			name:"supports",
				columns:[
			         {
			        	 "header":Messages("samples.table.projectCode"),
			        	 "property":"sample.projectCodes",
			        	 "type":"text",
			        	 "edit":true,
			        	 "editTemplate":"<div bt-select class='form-control' #ng-model	multiple=true filter=true bt-options='project.code as project.code+\" (\"+project.name+\")\" for project in lists.get(\"projects\")'></div>",
			        	 "extraHeaders":{0:Messages("samples.table.sample")}
			         },
			         {
			        	 "header":Messages("samples.table.typeCode"),
			        	 "property":"sample.typeCode",
			        	 "type":"text",
			        	 "edit":true,
			        	 "editTemplate":"<div bt-select class='form-control' #ng-model	bt-options='type.code as type.name for type in lists.get(\"sampleTypes\")'></div>",
			        	 "extraHeaders":{0:Messages("samples.table.sample")}
			         },
			         {
			        	 "header":Messages("samples.table.importTypeCode"),
			        	 "property":"sample.importTypeCode",
			        	 "type":"text",
			        	 "edit":true,
			        	 "editTemplate":"<div bt-select class='form-control' #ng-model	bt-options='type.code as type.name for type in lists.get(\"importTypes\")'></div>",
			        	 "extraHeaders":{0:Messages("samples.table.sample")}
			         },
			         {
			        	 "header":Messages("samples.table.code"),
			        	 "property":"sample.code",
			        	 "type":"text",
			        	 "edit":true,
			        	 "extraHeaders":{0:Messages("samples.table.sample")}
			         },
			         
			         {
			        	 "header":Messages("containerSupports.table.codeSupport"),
			        	 "property":"support.code",
			        	 "type":"text",
			        	 "edit":true,
			        	 "extraHeaders":{0:Messages("containerSupports.table.support")}
			         },
			         {
			        	 "header":Messages("containerSupports.table.categoryCode"),
			        	 "property":"support.categoryCode",
			        	 "type":"text",
			        	 "edit":true,
			        	 "editTemplate":"<div bt-select class='form-control' #ng-model	bt-options='cat.code as cat.name for cat in lists.get(\"containerSupportCategories\")'></div>",			        	 
			        	 "extraHeaders":{0:Messages("containerSupports.table.support")}
			         },
			         
			         
			         ],
			 compact:true,
	         pagination:{
	        	 mode:'local',
	        	 active:false
	         },	
	         order:{
	        	 active:false
	         },
	         search:{
	        	 active:false
	         },	        
	         remove:{
	        	 active:false,
	        	 //active:Permissions.check("writing")?true:false,	    	        	 
	        	 mode:'local',
	        	 withEdit:true	        	 
	         },
	         hide:{
	        	 active:false
	         },
	         edit:{
	        	 active:Permissions.check("writing")?true:false,
	        	 columnMode:true,
	        	 showButton : false,
	        	 withoutSelect:false,
	        	 byDefault : false
	         },
	         save:{
	        	 active:Permissions.check("writing")?true:false,
	        	 showButton:false,
	        	 withoutEdit:true,
	        	 mode:'local'
	         },
	         messages:{
	        	 active:false,
	        	 columnMode:true
	         },
	         exportCSV:{
	        	 active:false,
	        	 showButton:true,
	        	 delimiter:";"
	         },
	         add:{
	        	 active:Permissions.check("writing")?true:false,
	         },
	         cancel : {
	        	 active:false
	         },
	         select : {
	        	 showButton:false
	         },
	         extraHeaders:{
					number:1,
					dynamic:true,
			 },
	         showTotalNumberRecords: false
	         
		};
	
	
	
	$scope.save = function(){
		$scope.datatable.save();
		var results = $scope.datatable.getData();
		
		console.log("result = "+results.length);
		
	}
	
	/*
	 * init()
	 */
	var init = function(){
		
		lists.refresh.containerSupportCategories();
		lists.refresh.projects();
		lists.refresh.types({objectTypeCode:'Sample'} ,true, 'sampleTypes');
		lists.refresh.types({objectTypeCode:'Import'} ,true, 'importTypes');
		lists.refresh.containerSupportCategories();
		
		$scope.lists = lists;
		
		if(angular.isUndefined($scope.getHomePage())){
			mainService.setHomePage('new');
			tabService.addTabs({label:Messages('containerSupports.tabs.new'),href:jsRoutes.controllers.containers.tpl.ContainerSupports.home("new").url,remove:true});
			tabService.activeTab(0);
		}
	};

	init();
	
}]);
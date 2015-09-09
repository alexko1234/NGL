"use strict";


angular.module('home').controller('SearchContainerCtrl', ['$scope', 'datatable','basket','lists','$filter','$http','mainService','tabService','$parse', 
                                                          function($scope, datatable,basket, lists,$filter,$http,mainService, tabService, $parse) {
	$scope.lists = lists;	
	$scope.searchService = {};
	$scope.searchService.lists = lists;

	var datatableConfig = {
			columns:[
			         {
			        	 "header":Messages("containers.table.supportCode"),
			        	 "property":"support.code",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			 			 "group":true
			         },
			         {
			        	 "header":Messages("containers.table.support.categoryCode"),
			        	 "property":"support.categoryCode",
			        	 "filter":"codes:'container_support_cat'",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			 			 "groupMethod":"unique"
			         },
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"code",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text",
			        	 "position":3,
			 			 "render":"<div list-resize='cellValue | stringToArray | unique' ' list-resize-min-size='2'>",
			 			 "groupMethod":"collect"
			         },
			         {
			        	 "header":Messages("processes.table.projectCode"),
			        	 "property":"projectCodes",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text",
			        	 "position":4,					
			 			 "render":"<div list-resize='cellValue | unique' ' list-resize-min-size='2'>",
			 			 "groupMethod":"collect"
			         },
			         {
				 			"header":Messages("containers.table.sampleCodes.length"),
				 			"property":"sampleCodes.length",
				 			"order":true,
				 			"hide":true,
				 			"type":"text",
				        	"position":5,
				 			"groupMethod":"sum"
				 	},
			 		{
						"header":Messages("containers.table.sampleCodes"),
						"property":"sampleCodes",
						"order":false,
						"hide":true,
						"type":"text",
						"render":"<div list-resize='cellValue | unique' list-resize-min-size='3'>",
						"groupMethod":"collect",
			        	"position":6
					},
			 		{
						"header":Messages("containers.table.contents.length"),
						"property":"contents.length",
						"order":true,
						"hide":true,
						"type":"number",
			        	 "position":7,
			 			"groupMethod":"sum"
					},
					{
						"header":Messages("containers.table.tags"),
						"property": "contents",
						"order":false,
						"hide":true,
						"type":"text",
						"render":"<div list-resize='cellValue | getArray:\"properties.tag.value\" | unique' ' list-resize-min-size='3'>",
						"groupMethod":"collect",
			        	"position":8
					},
			        {
			        	 "header":Messages("containers.table.fromExperimentTypeCodes"),
			        	 "property":"fromExperimentTypeCodes",			        	 
			        	 "order":false,
			        	 "hide":true,
			        	 "type":"text",
			        	 "position":9,
			 			"render":"<div list-resize='cellValue | unique | codes:\"type\"' list-resize-min-size='3'>",
						"groupMethod":"collect"
			        },
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"state.code",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text",
			        	 "filter":"codes:'state'",
			        	 "position":10,
			 			"groupMethod":"unique"
			         },
			        {
			        	 "header":Messages("containers.table.valid"),
			        	 "property":"valuation.valid",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text",
			        	 "filter":"codes:'valuation'",
			        	 "position":11
			         },
			         {
						"header":Messages("containers.table.creationDate"),
						"property":"traceInformation.creationDate",
						"order":true,
						"hide":true,
						"type":"date",
			        	 "position":12
					 },
					 {
						"header":Messages("containers.table.createUser"),
						"property":"traceInformation.createUser",
						"order":true,
						"hide":true,
						"type":"text",
			        	 "position":13
					 }
			         ],
			         search:{
			        	 url:jsRoutes.controllers.containers.api.Containers.list()
			         },
			 		group:{
						active:true,
						showOnlyGroups:true,
						enableLineSelection:true,
						showButton:true
					},
			         pagination:{
			 			mode:'local'
				 	 },
				 	 order:{
				 		active:true,
				 		by:'code',
			 			mode:'local'
			 		 },
			         otherButtons :{
			        	 active:true,
			        	 template:'<button class="btn" ng-disabled="!datatable.isSelect()" ng-click="addToBasket(datatable.getSelection(true))" data-toggle="tooltip" title="'+Messages("button.addbasket")+'">'
			        	 +'<i class="fa fa-shopping-cart fa-lg"></i> ({{basket.length()}})'
			         }
	};


	$scope.changeProcessCategory = function(){
		$scope.searchService.form.nextProcessTypeCode = undefined;
		$scope.lists.clear("processTypes");

		if($scope.searchService.form.processCategory !== undefined && $scope.searchService.form.processCategory !== null){
			$scope.lists.refresh.processTypes({categoryCode:$scope.searchService.form.processCategory});
		}
	};

	$scope.changeProcessType = function(){
		$scope.removeTab(1);
		$scope.basket.reset();
	};
	
	$scope.selectDefaultFromExperimentType = function(){
		var selectionList = {};	
		$scope.searchService.form.fromExperimentTypeCodes=[];
			
		if(angular.isDefined($scope.searchService.form.nextProcessTypeCode)){
			selectionList = angular.copy($scope.lists.getExperimentTypesWithNone());
			$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.getDefaultFirstExperiments($scope.searchService.form.nextProcessTypeCode).url)
			.success(function(data, status, headers, config) {
				var defaultFirstExperimentTypes = data;
				console.log("defaultFirstExperimentTypes= "+defaultFirstExperimentTypes);
				angular.forEach(defaultFirstExperimentTypes, function(experimentType, key){
					angular.forEach(selectionList, function(item, index){
						if(experimentType.code==item.code){
							console.log("experimentType.code= "+experimentType.code);
							console.log("item.code= "+item.code);
							console.log("index= "+index);								
							$scope.searchService.form.fromExperimentTypeCodes.push(item.code);							
							console.log("form.fromExperimentTypeCodes= "+$scope.searchService.form.fromExperimentTypeCodes);
						}
					});
				});
				
				$scope.search();
			});
		}		
	};

	$scope.reset = function(){
		$scope.searchService.form = {};
	};

	$scope.resetSampleCodes = function(){
		$scope.searchService.form.sampleCodes = [];									
	};
	
	$scope.refreshSamples = function(){
		if($scope.searchService.form.projectCodes && $scope.searchService.form.projectCodes.length>0){
			lists.refresh.samples({projectCodes:$scope.searchService.form.projectCodes});
		}
	};

	$scope.search = function(){	
		$scope.searchService.updateForm();
		var _form = angular.copy($scope.searchService.form);
		$scope.errors.processCategory = {};
		$scope.errors.processType = {};
		
		
		
		
		if((_form.processCategory && _form.nextProcessTypeCode) || _form.createUser){			
			_form.stateCode = 'IW-P';
			
			if(_form.fromExperimentTypeCodes){			
				_form.isEmptyFromExperimentTypeCodes = false;
			}


			if(_form.fromDate)_form.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
			if(_form.toDate)_form.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();

			$scope.datatable.search(_form);
			mainService.setForm($scope.searchService.form);
		}else{
			if(_form.processCategory === null || _form.processCategory === undefined || _form.processCategory === "" ){
				$scope.errors.processCategory = "has-error";
				$scope.errors.processType = "has-error";
				$scope.searchService.form.nextProcessTypeCode = undefined;
			}
			if(_form.nextProcessTypeCode === null || _form.nextProcessTypeCode === undefined || _form.nextProcessTypeCode === "" ){
				$scope.errors.processType = "has-error";
			}
			$scope.datatable.setData([],0);
			$scope.basket.reset();

		}
	};
	
	
	$scope.searchService.initAdditionalColumns = function(){
		$scope.searchService.additionalColumns=[];
		$scope.searchService.selectedAddColumns=[];
		
		if($scope.searchService.lists.get("containers-addcolumns-processes-creation") && $scope.searchService.lists.get("containers-addcolumns-processes-creation").length === 1){
			var formColumns = [];
			var allColumns = angular.copy($scope.searchService.lists.get("containers-addcolumns-processes-creation")[0].columns);
			var nbElementByColumn = Math.ceil(allColumns.length / 5); //5 columns
			for(var i = 0; i  < 5 && allColumns.length > 0 ; i++){
				formColumns.push(allColumns.splice(0, nbElementByColumn));	    								
			}
			//complete to 5 five element to have a great design 
			while(formColumns.length < 5){
				formColumns.push([]);
			}
			$scope.searchService.additionalColumns = formColumns;
		}
	};
	
	$scope.searchService.updateForm = function(){
		$scope.searchService.form.includes = [];
		if($scope.searchService.reportingConfiguration){
			for(var i = 0 ; i < $scope.searchService.reportingConfiguration.columns.length ; i++){
				if($scope.searchService.reportingConfiguration.columns[i].queryIncludeKeys && $scope.searchService.reportingConfiguration.columns[i].queryIncludeKeys.length > 0){
					$scope.searchService.form.includes = $scope.searchService.form.includes.concat($scope.searchService.reportingConfiguration.columns[i].queryIncludeKeys);
				}else{
					$scope.searchService.form.includes.push($scope.searchService.reportingConfiguration.columns[i].property.replace('.value','').replace(".unit", ''));
				}
			}
		}else{
			$scope.searchService.form.includes = ["default"];
		}
		
		
		//this.form.includes = ["default"];
		for(var i = 0 ; i < $scope.searchService.selectedAddColumns.length ; i++){
			//remove .value if present to manage correctly properties (single, list, etc.)
			if($scope.searchService.selectedAddColumns[i].queryIncludeKeys && $scope.searchService.selectedAddColumns[i].queryIncludeKeys.length > 0){
				$scope.searchService.form.includes = $scope.searchService.form.includes.concat($scope.searchService.selectedAddColumns[i].queryIncludeKeys);
			}else{
				$scope.searchService.form.includes.push($scope.searchService.selectedAddColumns[i].property.replace('.value','').replace(".unit", ''));
			}
			
		}
	};
	
	$scope.searchService.getAddColumnsToForm = function(){
		if($scope.searchService.additionalColumns !== undefined && $scope.searchService.additionalColumns.length === 0){
			$scope.searchService.initAdditionalColumns();
		}
		return $scope.searchService.additionalColumns;									
	};
	
	$scope.searchService.addColumnsToDatatable=function(){
		//this.reportingConfiguration = undefined;
		//this.reportingConfigurationCode = undefined;
		
		$scope.searchService.selectedAddColumns = [];
		for(var i = 0 ; i < $scope.searchService.additionalColumns.length ; i++){
			for(var j = 0; j < $scope.searchService.additionalColumns[i].length; j++){
				if($scope.searchService.additionalColumns[i][j].select){
					$scope.searchService.selectedAddColumns.push($scope.searchService.additionalColumns[i][j]);
				}
			}
		}
		if($scope.searchService.reportingConfigurationCode){
			$scope.datatable.setColumnsConfig($scope.searchService.reportingConfiguration.columns.concat($scope.searchService.selectedAddColumns));
		}else{
			$scope.datatable.setColumnsConfig($scope.searchService.getDefaultColumns().concat($scope.searchService.selectedAddColumns));						
		}
		$scope.search();
	};	
	$scope.searchService.resetDatatableColumns = function(){
		$scope.searchService.initAdditionalColumns();
		$scope.datatable.setColumnsConfig($scope.searchService.getDefaultColumns());
		$scope.search();
	};
	/**
	 * Update column when change reportingConfiguration
	 */
	$scope.searchService.updateColumn = function(){
		$scope.searchService.initAdditionalColumns();
		if($scope.searchService.reportingConfigurationCode){
			$http.get(jsRoutes.controllers.reporting.api.ReportingConfigurations.get($scope.searchService.reportingConfigurationCode).url,{searchService:$scope.searchService, datatable:$scope.datatable})
					.success(function(data, status, headers, config) {
						config.searchService.reportingConfiguration = data;
						config.searchService.search();
						config.datatable.setColumnsConfig(data.columns);																								
			});
		}else{
			$scope.searchService.reportingConfiguration = undefined;
			$scope.datatable.setColumnsConfig($scope.searchService.getDefaultColumns());
			$scope.search();
		}
		
	};
	
	$scope.searchService.initAdditionalFilters = function(){
		var additionalFilters = $scope.searchService.additionalFilters = [];
		
		if($scope.searchService.lists.get("containers-search-addfilters") && $scope.searchService.lists.get("containers-search-addfilters").length === 1){
			var formFilters = [];
			var allFilters = angular.copy($scope.searchService.lists.get("containers-search-addfilters")[0].filters);
			var nbElementByColumn = Math.ceil(allFilters.length / 5); //5 columns
			for(var i = 0; i  < 5 && allFilters.length > 0 ; i++){
				formFilters.push(allFilters.splice(0, nbElementByColumn));	    								
			}
			//complete to 5 five element to have a great design 
			while(formFilters.length < 5){
				formFilters.push([]);
			}
				
			$scope.searchService.additionalFilters = additionalFilters = formFilters;
		}
	};
	
	$scope.searchService.getAddFiltersToForm = function(){
		if($scope.searchService.additionalFilters.length === 0){
			$scope.searchService.initAdditionalFilters();
		}
		return $scope.searchService.additionalFilters;									
	};	
	

	$scope.addToBasket = function(containers){
		$scope.errors.processType = {};
		$scope.errors.processCategory = {};
		if($scope.searchService.form.nextProcessTypeCode){
			for(var i = 0; i < containers.length; i++){
				var alreadyOnBasket = false;
				for(var j=0;j<this.basket.get().length;j++){
					if(this.basket.get()[j].code === containers[i].code){
						alreadyOnBasket = true;
					}
				}
				if(!alreadyOnBasket){
					this.basket.add(containers[i]);
				}
			}
			tabService.addTabs({label:$filter('codes')($scope.searchService.form.nextProcessTypeCode,"type"),href:$scope.searchService.form.nextProcessTypeCode,remove:false});
		}else{
			if(!$scope.searchService.form.processCategory){
				$scope.errors.processCategory = "has-error";
			}

			$scope.errors.processType = "has-error";
		}
	};	
	
	$scope.searchService.getDefaultColumns = function(){ return datatableConfig.columns;};

	//init
	$scope.errors = {};
	if(angular.isUndefined($scope.getDatatable())){
		$scope.datatable = datatable(datatableConfig);			
		mainService.setDatatable($scope.datatable);	
	}else{
		$scope.datatable = mainService.getDatatable();
	}

	if(angular.isUndefined($scope.getHomePage())){
		mainService.setHomePage('new');
		tabService.addTabs({label:Messages('processes.tabs.create'),href:jsRoutes.controllers.processes.tpl.Processes.home("new").url,remove:false});
		tabService.activeTab(0);
	}

	if(angular.isUndefined($scope.getBasket())){
		$scope.basket = basket();			
		mainService.setBasket($scope.basket);
	}else{
		$scope.basket = mainService.getBasket();
	}
	
	

	if(angular.isUndefined(mainService.getForm())){
		$scope.searchService.form = {};
		mainService.setForm($scope.searchService.form);
		$scope.lists.refresh.projects();
		$scope.lists.refresh.processCategories();
		$scope.lists.refresh.containerSupports();
		$scope.lists.refresh.containerSupportCategories();
		$scope.lists.refresh.users();
		lists.refresh.experimentTypes({categoryCode:"transformation", withoutOneToVoid:true});
		$scope.lists.refresh.reportConfigs({pageCodes:["containers-addcolumns-processes-creation"]}, "containers-addcolumns-processes-creation");
		$scope.lists.refresh.filterConfigs({pageCodes:["containers-search-addfilters"]}, "containers-search-addfilters");
		$scope.searchService.additionalFilters=[];
		$scope.searchService.additionalColumns=[];
		$scope.searchService.selectedAddColumns=[];
		$scope.searchService.getColumns=datatableConfig.columns;
		$scope.searchService.lists = lists;


	}else{
		$scope.searchService.form = mainService.getForm();			
	}
	
	
	
}]);

angular.module('home').controller('ListNewCtrl', ['$scope', 'datatable','$http','mainService','$q', function($scope, datatable,$http,mainService,$q) {

var	datatableConfig = {
			columns:[
			         {
			        	 "header":Messages("processes.table.supportCode"),
			        	 "property":"support.code",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":1,
			        	 "type":"text"
			         },
			 /*        {
			        	 "header":Messages("processes.table.line"),
			        	 "property":"support.line",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":2,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.columns"),
			        	 "property":"support.column",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":3,
			        	 "type":"text"
			         },  */
			         {
			        	 "header":Messages("processes.table.projectCode"),
			        	 "property":"projectCodes",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":4,
			        	 "render":"<div list-resize='value.data.projectCodes | unique' list-resize-min-size='3'>",
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.sampleCode"),
			        	 "property":"sampleCodes",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":5,
			        	 "render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>",
			        	 "type":"text"
			         },
			         {
			 			"header":Messages("containers.table.contents.length"),
			 			"property":"contents.length",
			 			"order":true,
			 			"hide":true,
			 			"position":5.01,
			 			"type":"number"
			 		},
			         {
			        	 "header": function(){
			        		 if($scope.supportView){
			        			 return Messages("containers.table.stateCode");
			        		 }else{
			        			 return  Messages("containers.table.state.code");	
			        		 }
			        	 },
			        	 "property":"state.code",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":6,
			        	 "filter": "codes:'state'",
			        	 "type":"text"
			         },
			         {
			        	 "header" : Messages("processes.table.comments"),
						"property" : "comments[0].comment",
						"position" : 500,
						"order" : false,
						"edit" : true,
						"hide" : true,
						"type" : "text"
			         }
			         ],
			         pagination:{
			        	 active:false
			         },		
			         search:{
			        	 active:false
			         },
			         order:{
			        	 mode:'local',
			        	 active:true,
			        	 by:'containerInputCode'
			         },
			         edit:{  			        	
			        	 columnMode:true
			         },
			         save:{
			        	 active: !$scope.doneAndRecorded,
			        	 withoutEdit:true,
			        	 showButton : false,
			        	 mode:"local",
			        	 changeClass : false
			         },
			         remove:{			        	 
			        	 mode:'local',
			        	 callback : function(datatable){
			        		 $scope.basket.reset();
			        		 $scope.basket.add(datatable.allResult);
			        	 }
			         },
			         messages:{
			        	 active:false
			         },
			         otherButtons :{
			        	 active:true,
			        	 template:'<button ng-if="doneAndRecorded==false" class="btn" ng-click="save()"><i class="fa fa-save"></i></button><button ng-if="doneAndRecorded==false" ng-click="swithView()" ng-disabled="loadView"  class="btn btn-info" ng-switch="supportView">'+Messages("baskets.switchView")+
			        	 ' '+'<b ng-switch-when="true" class="switchLabel">'+
			        	 Messages("baskets.switchView.containers")+'</b>'+
			        	 '<b ng-switch-when="false" class="switchLabel">'+Messages("baskets.switchView.supports")+'</b></button></button>'
			         }
	};

	var getProcessesColumns = function(){
		var columns = [
		         {
		        	 "header":Messages("processes.table.containerInputCode"),
		        	 "property":"containerInputCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":1,
		        	 "type":"text"
		         },
		         {
			 			"header":Messages("containers.table.contents.length"),
			 			"property":"contents.length",
			 			"url":"'/api/containers/'+containerInputCode",
			 			"order":true,
			 			"hide":true,
			 			"position":2,
			 			"type":"number"
			 	},
		         {
		        	 "header":Messages("processes.table.sampleCode"),
		        	 "property":"sampleCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":2.01,
		        	 "type":"text"
		         },
	/*	         {
		        	 "header":Messages("processes.table.columns"),
		        	 "property":"support.column",
		        	 "url":"'/api/containers/'+containerInputCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":3.5,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.line"),
		        	 "property":"support.line",
		        	 "url":"'/api/containers/'+containerInputCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":3,
		        	 "type":"text"
		         },   */
		         {
		        	 "header":Messages("processes.table.sampleOnInputContainer.properties.tag"),
		        	 "property":"sampleOnInputContainer.properties.tag.value",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":4,
		        	 "type":"text"
		         },		         
		         {
		        	 "header":Messages("processes.table.typeCode"),
		        	 "property":"typeCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":9,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.stateCode"),
		        	 "property":"state.code",
		        	 "order":true,
		        	 "hide":true,
		        	 "filter": "codes:'state'",
		        	 "position":30,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.code"),
		        	 "property":"code",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":33,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.creationDate"),
		        	 "property":"traceInformation.creationDate",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":34,
		        	 "type":"date"
		         },
		         {
		        	 "header":Messages("processes.table.projectCode"),
		        	 "property":"projectCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":37,
		        	 "type":"text"
		         },
		         {
		         "header" : Messages("processes.table.comments"),
					"property" : "comments[0].comment",
					"position" : 500,
					"order" : false,
					"edit" : true,
					"hide" : true,
					"type" : "text"
			        }
		 ];
		
		columns = columns.concat($scope.processPropertyColumns);
	
		return columns;
	};
	
	$scope.swithView = function(){		
		if($scope.supportView){
			$scope.supportView = false;
			$scope.swithToContainerView();
		}else{
			$scope.supportView = true;
			$scope.swithToSupportView()
		}
	};

	$scope.swithToContainerView = function(){
		if($scope.basket.length() != 0){
			$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
		}else{
			$scope.datatable.setData($scope.processes);
		}


		$scope.datatable.config.columns[0].header = "containers.table.code";
	};

	$scope.save = function (){
		$scope.message.details = {};
		$scope.message.isDetails = false;
		$scope.datatable.save();
		$scope.processesToSave = [];
		var data = $scope.datatable.getData();
		var url = "";
		var processes = [];
		$scope.promises = [];
		if(!$scope.supportView){
			url =  jsRoutes.controllers.processes.api.Processes.saveBatch().url;
			$scope.datatable.config.spinner.start = true;
			for(var i=0;i<data.length;i++){
				if($scope.lineClasses[i] != "success"){
					for(var j = 0; j < data[i].contents.length; j++){ //one process by sample
						var processData = data[i];
						processData.properties.limsCode = undefined;
						processData.properties.receptionDate = undefined;
						var process = {index:i, data:{
								projectCode: data[i].contents[j].projectCode,
								sampleCode: data[i].contents[j].sampleCode,
								containerInputCode: processData.code,
								typeCode:$scope.form.nextProcessTypeCode,
								categoryCode:$scope.form.processCategory,
								properties:processData.properties,
								comments:data[i].comments,
								sampleOnInputContainer:{sampleCode:processData.contents[j].sampleCode,
														sampleCategoryCode:processData.contents[j].sampleCategoryCode,
														sampleTypeCode:processData.contents[j].sampleTypeCode,
														percentage:processData.contents[j].percentage,
														properties:processData.contents[j].properties,
														containerCode:data[i].code,
														containerSupportCode:data[i].support.code,
														mesuredVolume:data[i].mesuredVolume,
														mesuredQuantity:data[i].mesuredQuantity,
														mesuredConcentration:data[i].mesuredConcentration}
						}};
						processes.push(process);
					}
				}
			}
			var nbElementByBatch = Math.ceil(processes.length / 6); //6 because 6 request max in parrallel with firefox and chrome
			for(var i = 0; i  < 6 && processes.length > 0 ; i++){
				$scope.promises.push($scope.getSaveRemoteRequest(url, processes.splice(0, nbElementByBatch),$scope.form.nextProcessTypeCode)); 								
			}
			
		}else{
			$scope.datatable.config.spinner.start = true;
			var i=0;
			for(i=0;i<data.length;i++){
				url =  jsRoutes.controllers.processes.api.Processes.save().url;
				var processData = data[i];
				processData.properties.limsCode = undefined;
				processData.properties.receptionDate = undefined;
				var process = {
						projectCode: processData.projectCodes[0],
						typeCode:$scope.form.nextProcessTypeCode,
						categoryCode:$scope.form.processCategory,
						comments:data[i].comments,
						properties:processData.properties
				};
				
				if($scope.lineClasses[i] != "success"){
					$scope.promises.push($scope.saveSupport(url,i,process,data[i].support.code));
				}
			}
		}

		$q.all($scope.promises).then(function (res) {
			$scope.message.clazz="alert alert-success";
			$scope.message.text=Messages('experiments.msg.save.sucess');
			$scope.doneAndRecorded = true;			

			$scope.basket.reset();
			$scope.datatable.config.edit.active = false;
			$scope.datatable.config.remove.active = false;
			$scope.datatable.config.select.active = false;
			$scope.datatable.config.cancel.active = false;
			$scope.datatable.setColumnsConfig(getProcessesColumns());
			$scope.datatable.setData($scope.processes);
			var displayResult = $scope.datatable.displayResult;
			for(var i=0;i<displayResult.length;i++){
				$scope.datatable.displayResult[i].line.trClass = "success";
			}
			$scope.changeConfigFunc(false);
			$scope.datatable.config.spinner.start = false;
		}, function(res){
			$scope.datatable.config.spinner.start = false;
			$scope.message.clazz = 'alert alert-danger';
			$scope.message.text = Messages('experiments.msg.save.error');
			
			/*var displayResult = $scope.datatable.displayResult;
			for(var i=0;i<displayResult.length;i++){
				$scope.datatable.displayResult[i].line.trClass = $scope.lineClasses[i];
			}*/
			
			$scope.doneAndRecorded = false;
			$scope.changeConfigFunc(true);
		});	
	};

	
	$scope.getSaveRemoteRequest = function(url, processes,processTypeCode){
		return $http.post(url, processes, {params:{"processTypeCode": processTypeCode}})
		.success(function(data, status, headers, config) {
			if(data!=null){
				for(var i=0;i<data.length;i++){
					//$scope.datatable.displayResult[data[i].index].line.trClass = "success";
					$scope.lineClasses[data[i].index] = "success";
					$scope.processes = $scope.processes.concat(data[i].data);
				}
				var displayResult = $scope.datatable.displayResult;
				for(var i=0;i<displayResult.length;i++){
					$scope.datatable.displayResult[i].line.trClass = $scope.lineClasses[i];
				}
			}
		})
		.error(function(data, status, headers, config) {
			for(var i=0;i<data.length;i++){
				$scope.lineClasses[data[i].index] = "danger";
				$scope.datatable.addErrors(data[i].index,data[i].data);
				$scope.message.details = data[i].data;
				$scope.message.isDetails = true;
			}
			var displayResult = $scope.datatable.displayResult;
			for(var i=0;i<displayResult.length;i++){
				$scope.datatable.displayResult[i].line.trClass = $scope.lineClasses[i];
			}
		});
	};
	
	$scope.saveSupport = function(url, index, process, supportCode){
		return $http.post(url,process, {params:{"fromSupportContainerCode": supportCode}})
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.lineClasses[index] = "success";
				$scope.processes = $scope.processes.concat(data);
				$scope.datatable.config.remove.active = false;
				var displayResult = $scope.datatable.displayResult;
				for(var j=0;j<displayResult.length;j++){
					$scope.datatable.displayResult[j].line.trClass = $scope.lineClasses[j];
				}
			}
		}).error(function(data, status, headers, config) {
			$scope.lineClasses[index] = "danger";
			$scope.message.details = data;
			$scope.message.isDetails = true;
			var displayResult = $scope.datatable.displayResult;
			for(var j=0;j<displayResult.length;j++){
				$scope.datatable.displayResult[j].line.trClass = $scope.lineClasses[j];
			}
		});
	};
	
	$scope.swithToSupportView = function(){

		if($scope.basket.length() == 0){
			$scope.datatable.setData($scope.processes);
		}else{
			var processes =  mainService.getBasket().basket;
			$scope.processesSupports = [];
			angular.forEach(processes,function(process){
				var alreadyExist = false;
				angular.forEach($scope.processesSupports,function(processesSupport){
					if(processesSupport.support.code == process.support.code){
						alreadyExist = true;
					}
				});
				if(!alreadyExist){
					$scope.processesSupports.push(process);
				}
			});
			$scope.datatable.setData($scope.processesSupports,$scope.processesSupports.length);
		}


		console.log($scope.datatable.config);
		if($scope.datatable.config.columns.length>0)
			$scope.datatable.config.columns[0].header = "containers.table.supportCode";
	};

	$scope.getPropertyColumnType = function(type){
		if(type === "java.lang.String"){
			return "text";
		}else if(type === "java.lang.Double"){
			return "number";
		}else if(type === "java.util.Date"){
			return "date";
		}
		
		return type;
	};
	
	$scope.addNewProcessColumns = function(){		
		var typeCode = "";		
		var column = "";
		if($scope.form.nextProcessTypeCode){
			typeCode = $scope.form.nextProcessTypeCode;
		}
		$scope.processPropertyColumns = [];
		return $http.get(jsRoutes.controllers.processes.tpl.Processes.getPropertiesDefinitions(typeCode).url)
		.success(function(data, status, headers, config) {
			if(data!=null){
				if(data.length>0 && !$scope.doneAndRecorded){
					 $scope.editableFunc(true);					
				}else{
					 $scope.editableFunc(false);				
				} 
				
				console.log(data);				
				angular.forEach(data, function(property){
					var unit = "";
					if(angular.isDefined(property.displayMeasureValue) && property.displayMeasureValue != null){
						unit = "("+property.displayMeasureValue.value+")";
					}				
						
					column = $scope.datatable.newColumn(property.name, "properties."+property.code+".value",property.editable,false,true, $scope.getPropertyColumnType(property.valueType),property.choiceInList, property.possibleValues,{});
					
					column.listStyle = "bt-select";
					column.defaultValues = property.defaultValue;
					if(property.displayMeasureValue != undefined && property.displayMeasureValue != null){
						column.convertValue = {"active":true, "displayMeasureValue":property.displayMeasureValue.value, "saveMeasureValue":property.saveMeasureValue.value};
					}
					column.position = (9+(property.displayOrder/1000));
					$scope.processPropertyColumns.push(column);
					$scope.datatable.addColumn(9+(property.displayOrder/1000),column);	
				});				
			}

		})
		.error(function(data, status, headers, config) {
			console.log(data);
		});		

	};	
	
/*	$scope.recorded = function(){
		 if($scope.doneAndRecorded==false){
			 return true;
		 }
		 return false;
		 
	 };*/
	 
	 $scope.editableFunc= function(bool){
		 var conf = $scope.datatable.getConfig();
		 conf.edit.active = bool;		 
		 $scope.datatable.setConfig(conf);		 
	 };	 
	 
	 $scope.removableFunc= function(bool){
		 var conf = $scope.datatable.getConfig();
		 conf.remove.active = bool;		 
		 $scope.datatable.setConfig(conf);		 
	 };
	 
	 $scope.changeConfigFunc = function(bool){
		 var conf = $scope.datatable.getConfig();
		 conf.edit.active = bool;
		 conf.remove.active = bool;
		 $scope.datatable.setConfig(conf);
	 };
	 
	//init
	$scope.form = mainService.getForm();
	$scope.message = {};
	$scope.supportView = false;
	$scope.containers = [];
	$scope.lineClasses = [];
	$scope.processes = [];
	
	$scope.datatable = datatable(datatableConfig);	
	$scope.basket = mainService.getBasket();
	$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
	$scope.addNewProcessColumns();
	$scope.datatable.selectAll(false);
	if($scope.basket.length() != 0){
		$scope.doneAndRecorded = false;
		$scope.changeConfigFunc(true);
	}else{
		$scope.doneAndRecorded = true;
		$scope.changeConfigFunc(false);
	}
	$scope.swithView();
}]);
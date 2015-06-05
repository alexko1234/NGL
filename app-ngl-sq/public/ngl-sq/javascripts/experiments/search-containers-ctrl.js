"use strict";

angular.module('home').controller('SearchContainerCtrl', ['$scope','$routeParams', '$filter','datatable','basket','lists','$http','mainService','tabService', function ($scope,$routeParams, $filter, datatable,basket, lists, $http,mainService,tabService) {
	$scope.searchService = {};
	$scope.searchService.lists = lists;
	
	$scope.datatableConfig = {
		columns:[{
			"header":Messages("containers.table.supportCode"),
			"property":"support.code",
			"order":true,
			"position":1,
			"type":"text",
			"group":true
		},
		{
			"header":Messages("containers.table.supportCategoryCode"),
			"property":"support.categoryCode",
			"filter":"codes:'container_support_cat'",
			"order":true,
			"position":2,
			"type":"text",
			"groupMethod":"unique"
		},
		{
			"header":Messages("containers.table.support.column"),
			"property":"support.column",
			"order":false,
			"position":3,
			"type":"text"
		},
		{
			"header":Messages("containers.table.support.line"),
			"property":"support.line",
			"order":false,
			"position":4,
			"type":"text"
		},
		{
			"header":Messages("containers.table.code"),
			"property":"code",
			"order":true,
			"position":5,
			"type":"text",
			"groupMethod":"collect"
		},
		{
			"header":Messages("containers.table.fromExperimentTypeCodes"),
			"property":"fromExperimentTypeCodes",
			//"filter":"codes:'type'",
			"order":false,
			"position":6,
			"type":"text",
			"render":"<div list-resize='cellValue | unique | codes:\"type\"' list-resize-min-size='3'>",
			"groupMethod":"collect"
		},
		{
			"header":Messages("containers.table.sampleCodes.length"),
			"property":"sampleCodes.length",
			"order":true,
			"position":8,
			"type":"number",
			"groupMethod":"sum"
		},
		{
			"header":Messages("containers.table.sampleCodes"),
			"property":"sampleCodes",
			"order":false,
			"hide":true,
			"position":9,
			"type":"text",
			"render":"<div list-resize='cellValue | unique' list-resize-min-size='3'>",
			"groupMethod":"collect"
			
		},
		{
			"header":Messages("containers.table.contents.length"),
			"property":"contents.length",
			"order":true,
			"hide":true,
			"position":9.01,
			"type":"number",
			"groupMethod":"sum"
				
		},
		{
			"header":Messages("containers.table.tags"),
			"property": "contents",
			"order":false,
			"hide":true,
			"type":"text",
			"position":9.1,
			"render":"<div list-resize='cellValue | getArray:\"properties.tag.value\" | unique' ' list-resize-min-size='3'>",
			"groupMethod":"collect"
			
		},
		{
			"header":Messages("containers.table.projectCodes"),
			"property":"projectCodes",
			"order":false,
			"position":10,					
			"render":"<div list-resize='cellValue | unique' ' list-resize-min-size='2'>",
			"type":"text",
			"groupMethod":"collect"
		},
		{
			"header":Messages("containers.table.creationDate"),
			"property":"traceInformation.creationDate",
			"order":true,
			"position":12,
			"type":"date"
		},
		{
			"header":Messages("containers.table.createUser"),
			"property":"traceInformation.createUser",
			"order":true,
			"position":13,
			"type":"text"
		},
		{
			"header":Messages("containers.table.inputProcessCodes"),
			"property":"inputProcessCodes",
			"order":false,
			"type":"text",
			"position":14,
			"render":"<div list-resize='value.data.inputProcessCodes | unique' list-resize-min-size='3'>",
			"groupMethod":"collect"
		},
		{
			"header":Messages("containers.table.state.code"),
			"property":"state.code",
			"order":true,
			"type":"text",
			"edit":false,
			"position":7,
			"choiceInList": true,
			"possibleValues":"searchService.lists.getStates()", 
			"filter":"codes:'state'",
			"groupMethod":"unique"
				
		},
		{
			"header":Messages("containers.table.valid"),
			"property":"valuation.valid",
			"order":true,
			"type":"text",
			"edit":true,
			"position":11,
			"choiceInList": true,
			"possibleValues":"searchService.lists.getValuations()", 
			"filter":"codes:'valuation'"	
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
			template:'<button class="btn" ng-disabled="!datatable.isSelect() && !datatable.isSelectGroup()" ng-click="addToBasket(datatable.getSelection(true))" data-toggle="tooltip" title="'+Messages("button.addbasket")+'">'
					+'<i class="fa fa-shopping-cart fa-lg"></i> ({{basket.length()}})</button>'
		}
	};
	
	$scope.changeExperimentType = function(experimentCategory){
		tabService.removeTab(2);
		tabService.removeTab(1);

		$scope.basket.reset();
		$scope.searchService.form.containerSupportCategory = undefined;
		$scope.searchService.lists.clear("containerSupportCategories");
		if($scope.searchService.form.nextExperimentTypeCode){
			$scope.searchService.lists.refresh.containerSupportCategories({experimentTypeCode:$scope.searchService.form.nextExperimentTypeCode});
		}
		$scope.experimentCategory = experimentCategory;
		//this.search();
	};
	
	$scope.changeProcessCategory = function(){
		$scope.additionalFilters=[];
		$scope.searchService.form.processTypeCode = undefined;
		if($scope.searchService.form.processCategory !== undefined)
			$scope.searchService.lists.refresh.processTypes({"processCategoryCode":$scope.searchService.form.processCategory});
	};
	
	$scope.changeProcessType = function(){
		lists.refresh.filterConfigs({pageCodes:["process-"+$scope.searchService.form.processTypeCode]}, "process-"+$scope.searchService.form.processTypeCode);
		$scope.initAdditionalFilters();
	};
	
	$scope.reset = function(){
		$scope.searchService.form = {}
	};
	
	$scope.loadExperimentTypesLists = function(){
		$scope.searchService.lists.refresh.experimentTypes({categoryCode:"purification"}, "purification");
		$scope.searchService.lists.refresh.experimentTypes({categoryCode:"qualitycontrol"}, "qualitycontrol");
		$scope.searchService.lists.refresh.experimentTypes({categoryCode:"transfert"}, "transfert");
		$scope.searchService.lists.refresh.experimentTypes({categoryCode:"transformation", withoutOneToVoid:true});
	};
	
	$scope.refreshSamples = function(){
		if($scope.searchService.form.projectCodes && $scope.searchService.form.projectCodes.length>0){
			lists.refresh.samples({projectCodes:$scope.searchService.form.projectCodes});
		}
	};
	
	$scope.getContainerStateCode = function(experimentCategory){
		var stateCode = "A";
		console.log(experimentCategory);
		switch(experimentCategory){
			case "qualitycontrol": stateCode = 'A-QC';
								   break;
			case "transfert": 	   stateCode = 'A-TF';
							       break;
			case "purification":   stateCode = 'A-PF';
								   break;
			case "transformation":   stateCode = 'A-TM';
			   						break;								   
			default:               stateCode = 'A';
		}
		
		return stateCode;
	};
	
	$scope.search = function(){
		var _form = angular.copy($scope.searchService.form);
		$scope.errors.experimentType = {};
		$scope.errors.containerSupportCategory = {};
		
		
		if(_form.nextExperimentTypeCode){
			_form.stateCode = $scope.getContainerStateCode($scope.experimentCategory);	 
		
			var formTemp = angular.copy(_form.nextExperimentTypeCode);
			if($scope.experimentCategory!='transformation') _form.nextExperimentTypeCode=undefined;
			if(_form.fromDate)_form.fromDate = moment($scope.searchService.form.fromDate, Messages("date.format").toUpperCase()).valueOf();
			if(_form.toDate)_form.toDate = moment($scope.searchService.form.toDate, Messages("date.format").toUpperCase()).valueOf();
			_form.processCategory = undefined;
			
			$scope.datatable.search(_form);
			
			if(angular.isDefined(formTemp) && angular.isUndefined(_form.nextExperimentTypeCode)){
				_form.nextExperimentTypeCode=angular.copy(formTemp);				
			}
			
			mainService.setForm($scope.searchService.form);
		}else{
			if(!_form.nextExperimentTypeCode){
				$scope.errors.experimentType = "has-error";
			}
			$scope.datatable.setData([],0);
			$scope.basket.reset();
		}						
	};
	
	$scope.initAdditionalFilters = function(){
		$scope.additionalFilters=[];
		if($scope.searchService.form.processTypeCode !== undefined && lists.get("process-"+$scope.searchService.form.processTypeCode) && lists.get("process-"+$scope.searchService.form.processTypeCode).length === 1){
			var formFilters = [];
			var allFilters = angular.copy(lists.get("process-"+$scope.searchService.form.processTypeCode)[0].filters);
			var nbElementByColumn = Math.ceil(allFilters.length / 5); //5 columns
			for(var i = 0; i  < 5 && allFilters.length > 0 ; i++){
				formFilters.push(allFilters.splice(0, nbElementByColumn));	    								
			}
			//complete to 5 five element to have a great design 
			while(formFilters.length < 5){
				formFilters.push([]);
			}
				
			this.additionalFilters = formFilters;
		}
	},
	
	$scope.getAddFiltersToForm = function(){
		if($scope.additionalFilters !== undefined && $scope.additionalFilters.length === 0){
			$scope.initAdditionalFilters();
		}
		return $scope.additionalFilters;									
	},
	
	$scope.addToBasket = function(containers){
		for(var i = 0; i < containers.length; i++){
			var alreadyOnBasket = false;
			for(var j=0;j<this.basket.get().length;j++){
				if(containers[i].group === undefined){
					if(this.basket.get()[j].code === containers[i].code){
						alreadyOnBasket = true;
					}
				}else{
					var test = $scope.datatable.getGroupColumnValue(containers[i], "code");
					if($scope.datatable.getGroupColumnValue(containers[i], "code") === this.basket.get()[j].code){
						alreadyOnBasket = true;
					}
				}
			}
			if(!alreadyOnBasket){
				if(containers[i].group === undefined){
					this.basket.add(containers[i]);
					if(($scope.searchService.form.nextExperimentTypeCode) && this.basket.length() > 0 && tabService.getTabs().length === 1){
						tabService.addTabs({label:$filter('codes')($scope.searchService.form.nextExperimentTypeCode,'type'),href:"/experiments/new/"+$scope.searchService.form.nextExperimentTypeCode,remove:false});
					}
				}else{
					var basket = this.basket;
					var supportCode = $scope.datatable.getGroupColumnValue(containers[i], "support.code");
					$http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{"supportCode":supportCode}})
					.success(function(data, status, headers, config) {
						if(data!=null){
							angular.forEach(data, function(container){
								basket.add(container);
							});
							if(($scope.searchService.form.nextExperimentTypeCode) && basket.length() > 0 && tabService.getTabs().length === 1){
								tabService.addTabs({label:$filter('codes')($scope.searchService.form.nextExperimentTypeCode,'type'),href:"/experiments/new/"+$scope.searchService.form.nextExperimentTypeCode,remove:false});
							}
						}
					})
					.error(function(data, status, headers, config) {
						alert("error");
					});
					//var container = {"code": $scope.datatable.getGroupColumnValue(containers[i], "support.code"), "projectCodes": $scope.datatable.getGroupColumnValue(containers[i], "projectCodes"), "sampleCodes": $scope.datatable.getGroupColumnValue(containers[i], "sampleCodes")}
				}
			}
		}
	};
	
	//init
	$scope.errors = {};
	$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.list().url).success(function(data, status, headers, config) {
		$scope.experimentTypeList=data;    				
	});
	if(angular.isUndefined(mainService.getDatatable())){
		$scope.datatable = datatable($scope.datatableConfig);
		mainService.setDatatable($scope.datatable);	
	} else {
		$scope.datatable = mainService.getDatatable();
	}
	if($routeParams.newExperiment === undefined){
		$scope.newExperiment = "new";
	}
	
	if(angular.isUndefined($scope.getHomePage())){
		mainService.setHomePage('new');
		tabService.addTabs({label:Messages('experiments.tabs.create'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("new").url,remove:false});
		tabService.activeTab(0);
	}
	
	if(angular.isUndefined($scope.getBasket())){
		$scope.basket = basket();			
		mainService.setBasket($scope.basket);
	} else {
		$scope.basket = mainService.getBasket();
	}
	
	//$scope.searchService.lists.clear("processTypes");
	$scope.searchService.lists.refresh.projects();
	$scope.searchService.lists.refresh.types({objectTypeCode:"Process"}, true);
	$scope.searchService.lists.refresh.experimentTypes({categoryCode:"transformation"},"transformation");
	$scope.searchService.lists.refresh.processCategories();
	$scope.searchService.lists.refresh.experimentCategories();
	$scope.searchService.lists.refresh.users();
	$scope.searchService.lists.refresh.states({objectTypeCode:"Container"});
	$scope.form = {};
	$scope.loadExperimentTypesLists();
	if(angular.isUndefined(mainService.getForm())){
		$scope.searchService.form = {};
		mainService.setForm($scope.searchService.form);
		
	} else {
		$scope.searchService.form = {};
		$scope.searchService.form =  mainService.getForm();
		if($scope.experimentCategory === undefined){
			$scope.experimentCategory = $scope.searchService.form.experimentCategoryCode;
		}
		$scope.searchService.form.experimentCategoryCode = undefined;
		$scope.searchService.lists.refresh.containerSupportCategories({experimentTypeCode:$scope.searchService.form.nextExperimentTypeCode});
		$scope.changeProcessCategory();
		//$scope.search();
	}
}]);
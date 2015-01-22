"use strict";

angular.module('home').controller('SearchContainerCtrl', ['$scope','$routeParams', '$filter','datatable','basket','lists','$http','mainService','tabService', function ($scope,$routeParams, $filter, datatable,basket, lists, $http,mainService,tabService) {
	$scope.searchService = {};
	$scope.searchService.lists = lists;
	
	$scope.datatableConfig = {
		columns:[{
			"header":Messages("supports.table.code"),
			"property":"code",
			"order":true,
			"type":"text"
		},
		{
			"header":Messages("containers.table.categoryCode"),
			"property":"categoryCode",
			"order":true,
			"type":"text"
		},
		{
			"header":Messages("containers.table.fromExperimentTypeCodes"),
			"property":"fromExperimentTypeCodes",
			"order":true,
			"type":"text"
		},
		{
			"header":Messages("containers.table.stateCode"), 
			"property":"state.code", 
			"order":true,
			"type":"text",
			"filter":"codes:'state'"
		},
		{
			"header":Messages("containers.table.sampleCodes.length"),
			"property":"sampleCodes.length",
			"order":true,
			"hide":true,
			"type":"text"
		},
		{
			"header":Messages("containers.table.sampleCodes"),
			"property":"sampleCodes",
			"order":true,
			"type":"text",
			"render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>",
		},
		{
			"header":Messages("containers.table.projectCodes"),
			"property":"projectCodes",
			"order":true,
			"type":"text"
		},		
		{
			"header":Messages("containers.table.valid"),
			"property":"valuation.valid",
			"order":true,
			"type":"text",
			"filter":"codes:'valuation'",
		},
		{
			"header":Messages("containers.table.creationDate"),
			"property":"traceInformation.creationDate",
			"order":true,
			"type":"date"
		},
		{
			"header":Messages("containers.table.createUser"),
			"property":"traceInformation.createUser",
			"order":true,
			"type":"text"
		},{
			"header":Messages("containers.table.inputProcessCodes"),
			"property":"inputProcessCodes",
			"order":true,
			"type":"text",
			"render":"<div list-resize='value.data.inputProcessCodes | unique' list-resize-min-size='3'>",
		}],	
		search:{
			url:jsRoutes.controllers.supports.api.Supports.list()
		},
		order:{
			active:true,
			by:'code'
		},
		otherButtons :{
			active:true,
			template:'<button class="btn" ng-disabled="!datatable.isSelect()" ng-click="addToBasket(datatable.getSelection(true))" data-toggle="tooltip" title="'+Messages("button.addbasket")+'">'
					+'<i class="fa fa-shopping-cart fa-lg"></i> ({{basket.length()}})</button>'
		}
	};
	
	$scope.changeExperimentType = function(experimentCategory){
		tabService.removeTab(2);
		tabService.removeTab(1);

		$scope.basket.reset();
		$scope.searchService.form.categoryCode = undefined;
		$scope.searchService.lists.clear("containerSupportCategories");
		if($scope.searchService.form.nextExperimentTypeCode){
			$scope.searchService.lists.refresh.containerSupportCategories({experimentTypeCode:$scope.searchService.form.nextExperimentTypeCode});
		}
		//$scope.searchService.form.experimentCategory = experimentCategory;
		//this.search();
	};
	
	$scope.changeProcessCategory = function(){
		$scope.additionalFilters=[];
		$scope.searchService.form.processTypeCode = undefined;
		if($scope.form.processCategoryCode !== undefined)
			$scope.searchService.lists.refresh.processTypes({"processCategoryCode":$scope.form.processCategoryCode});
	};
	
	$scope.changeProcessType = function(){
		lists.refresh.filterConfigs({pageCodes:["process-"+$scope.searchService.form.processTypeCode]}, "process-"+$scope.searchService.form.processTypeCode);
		$scope.initAdditionalFilters();
	};
	
	$scope.reset = function(){
		$scope.searchService.form = {}
	};
	
	$scope.loadExperimentTypesLists = function(){
		$scope.searchService.lists.refresh.experimentTypes({categoryCode:"purification"}, "purifications");
		$scope.searchService.lists.refresh.experimentTypes({categoryCode:"qualitycontrol"}, "qualitycontrols");
		$scope.searchService.lists.refresh.experimentTypes({categoryCode:"transfert"}, "transferts");
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
			case "qualitycontrols": stateCode = 'A-QC';
								   break;
			case "transferts": 	   stateCode = 'A-TF';
							       break;
			case "purifications":   stateCode = 'A-PF';
								   break;
			default:               stateCode = 'A';
		}
		
		return stateCode;
	};
	
	$scope.search = function(){
		var _form = $scope.searchService.form;
		$scope.errors.experimentType = {};
		$scope.errors.containerSupportCategory = {};
		
		
		if(_form.nextExperimentTypeCode && _form.categoryCode){
			_form.stateCode = $scope.getContainerStateCode($scope.searchService.form.experimentCategory);	 
		
			if($scope.searchService.form.fromDate)_form.fromDate = moment($scope.searchService.form.fromDate, Messages("date.format").toUpperCase()).valueOf();
			if($scope.searchService.form.toDate)_form.toDate = moment($scope.searchService.form.toDate, Messages("date.format").toUpperCase()).valueOf();
			
			mainService.setForm($scope.searchService.form);
			
			$scope.datatable.search(_form);
		}else{
			if(!$scope.searchService.form.nextExperimentTypeCode){
				$scope.errors.experimentType = "has-error";
			}else{
				$scope.errors.containerSupportCategory = "has-error";
			}
			$scope.datatable.setData({},0);
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
			this.basket.add(containers[i]);
			console.log(containers[i]);
		}
		
		if(($scope.searchService.form.nextExperimentTypeCode) && this.basket.length() > 0 && tabService.getTabs().length === 1){
			tabService.addTabs({label:$filter('codes')($scope.searchService.form.nextExperimentTypeCode,'type'),href:"/experiments/new/"+$scope.searchService.form.nextExperimentTypeCode,remove:false});
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
	
	$scope.searchService.lists.refresh.projects();
	$scope.searchService.lists.refresh.types({objectTypeCode:"Process"}, true);
	$scope.searchService.lists.refresh.experimentTypes({categoryCode:"transformation"},"transformations");
	$scope.searchService.lists.refresh.processCategories();
	$scope.searchService.lists.refresh.experimentCategories();
	$scope.searchService.lists.refresh.users();
	$scope.searchService.lists.refresh.states({objectTypeCode:"Container"});
	$scope.form = {};
	if(angular.isUndefined(mainService.getForm())){
		$scope.searchService.form = {};
		mainService.setForm($scope.searchService.form);
		$scope.loadExperimentTypesLists();
		
	} else {
		$scope.searchService.form = {};
		$scope.searchService.form.nextExperimentTypeCode =  mainService.getForm().experimentType;
		$scope.searchService.lists.refresh.containerSupportCategories({experimentTypeCode:$scope.searchService.form.nextExperimentTypeCode});
		//$scope.search();
	}
}]);
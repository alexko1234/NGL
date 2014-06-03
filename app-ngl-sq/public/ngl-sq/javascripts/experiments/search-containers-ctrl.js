"use strict";

angular.module('home').controller('SearchContainerCtrl', ['$scope','$routeParams', '$filter','datatable','basket','lists','$http', function ($scope,$routeParams, $filter, datatable,basket, lists, $http) {
	$scope.lists = lists;
	
	$scope.datatableConfig = {
		columns:[{
			"header":Messages("containers.table.code"),
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
			"header":Messages("containers.table.creationDate"),
			"property":"traceInformation.creationDate",
			"order":true,
			"type":"date"
		},
		{
			"header":Messages("containers.table.sampleCodes"),
			"property":"sampleCodes",
			"order":true,
			"type":"text"
		},
		{
			"header":Messages("containers.table.projectCodes"),
			"property":"projectCodes",
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
			"header":Messages("containers.table.valid"),
			"property":"valuation.valid",
			"order":true,
			"type":"text",
			"filter":"codes:'valuation'",
		},
		{
			"header":Messages("containers.table.createUser"),
			"property":"traceInformation.createUser",
			"order":true,
			"type":"text"
		},
		{
			"header":Messages("containers.table.stateCode"), 
			"property":"state.code", 
			"order":true,
			"type":"text",
			"filter":"codes:'state'"
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
			template:'<button class="btn" ng-disabled="!datatable.isSelect()" ng-click="addToBasket(datatable.getSelection(true))" data-toggle="tooltip" title="Messages("button.addbasket")">'
					+'<i class="fa fa-shopping-cart fa-lg"></i> ({{basket.length()}})</button>'
		}
	};
	
	$scope.changeExperimentType = function(){
		$scope.removeTab(1);
		$scope.basket.reset();
		$scope.form.containerSupportCategory = undefined;
		$scope.lists.clear("containerSupportCategories");
		if($scope.form.experimentType){
			$scope.lists.refresh.containerSupportCategories({experimentTypeCode:$scope.form.experimentType.code});
		}
		this.search();
	};
	
	$scope.changeProcessCategory = function(){
		$scope.form.processType = undefined;
		$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
	};
	
	$scope.reset = function(){
		$scope.form = {}
	};
	
	$scope.loadExperimentTypesLists = function(){
		$scope.lists.refresh.experimentTypes({categoryCode:"purification"}, "purifications");
		$scope.lists.refresh.experimentTypes({categoryCode:"qualitycontrol"}, "qualitycontrols");
		$scope.lists.refresh.experimentTypes({categoryCode:"transfert"}, "transferts");
		$scope.lists.refresh.experimentTypes({categoryCode:"transformation"}, "transformations");
	};
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
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
			default:               stateCode = 'A';
		}
		
		return stateCode;
	};
	
	$scope.search = function(){
		$scope.errors.experimentType = {};
		$scope.errors.containerSupportCategory = {};
		
		
		if($scope.form.experimentType && $scope.form.containerSupportCategory){ 		
			var jsonSearch = {};
			
			jsonSearch.stateCode = $scope.getContainerStateCode($scope.form.experimentCategory.code);	 
			
			if($scope.form.projectCode){
				jsonSearch.projectCodes = $scope.form.projectCode;
			}			
			if($scope.form.sampleCode){
				jsonSearch.sampleCodes = $scope.form.sampleCode;
			}			
			if($scope.form.processType){
				jsonSearch.processTypeCode = $scope.form.processType.code;
			}		
			

			if($scope.form.containerSupportCategory){
				jsonSearch.categoryCode = $scope.form.containerSupportCategory;
			}
			
			if($scope.form.experimentType){
				jsonSearch.experimentTypeCode = $scope.form.experimentType.code;
			}
			
			
			if($scope.form.fromExperimentTypeCodes){
				jsonSearch.fromExperimentTypeCodes = $scope.form.fromExperimentTypeCodes;
			}
			
			if($scope.form.user){
				jsonSearch.users = $scope.form.user;
			}
			
			if($scope.form.fromDate)jsonSearch.fromDate = moment($scope.form.fromDate, Messages("date.format").toUpperCase()).valueOf();
			if($scope.form.toDate)jsonSearch.toDate = moment($scope.form.toDate, Messages("date.format").toUpperCase()).valueOf();
			
			$scope.datatable.search(jsonSearch);
		}else{
			if(!$scope.form.experimentType){
				$scope.errors.experimentType = "alert-danger";
			}else{
				$scope.errors.containerSupportCategory = "alert-danger";
			}
			$scope.datatable.setData({},0);
			$scope.basket.reset();
		}						
	};
	
	$scope.addToBasket = function(containers){
		for(var i = 0; i < containers.length; i++){
			this.basket.add(containers[i]);
		}
		
		if(($scope.form.experimentType) && this.basket.length() > 0 && $scope.getTabs().length === 1){
			$scope.addTabs({label:$scope.form.experimentType.name,href:"/experiments/new/"+$scope.form.experimentType.code,remove:false});
		}
	};
	
	//init
	$scope.errors = {};
	$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.list().url).success(function(data, status, headers, config) {
		$scope.experimentTypeList=data;    				
	});
	if(angular.isUndefined($scope.getDatatable())){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.setDatatable($scope.datatable);	
	} else {
		$scope.datatable = $scope.getDatatable();
	}
	if($routeParams.newExperiment === undefined){
		$scope.newExperiment = "new";
	}
	
	if(angular.isUndefined($scope.getHomePage())){
		$scope.setHomePage('new');
		$scope.addTabs({label:Messages('experiments.tabs.create'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("new").url,remove:false});
		$scope.activeTab(0);
	}
	
	if(angular.isUndefined($scope.getBasket())){
		$scope.basket = basket();			
		$scope.setBasket($scope.basket);
	} else {
		$scope.basket = $scope.getBasket();
	}
	
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {experimentCategory:{}};
		$scope.setForm($scope.form);
		$scope.lists.refresh.projects();
		$scope.lists.refresh.types({objectTypeCode:"Process"}, true);
		$scope.lists.refresh.processCategories();
		$scope.lists.refresh.experimentCategories();
		$scope.lists.refresh.users();
		$scope.loadExperimentTypesLists();
		
	} else {
		$scope.form = $scope.getForm();		
	}
}]);
"use strict";

function SearchContainerCtrl($scope,$routeParams, $filter, datatable,basket, lists) {
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
			"header":Messages("containers.table.createUser"),
			"property":"traceInformation.createUser",
			"order":true,
			"type":"text"
		},
		{
			"header":Messages("containers.table.stateCode"), 
			"property":"state.code", 
			"order":true,
			"type":"text"
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
		this.search();
	};
	
	$scope.changeProcessCategory = function(){
		$scope.form.experimentType = undefined;
		$scope.form.experimentCategory = undefined;
		$scope.form.processType = undefined;
		$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
	};
	
	$scope.changeProcessType = function(){
		$scope.form.experimentType = undefined;
		$scope.form.experimentCategory = undefined;
	};
	
	$scope.reset = function(){
		$scope.form = {}
	};

	$scope.changeExperimentCategory = function(){
		$scope.removeTab(1);
		
		$scope.basket.reset();
		
		$scope.form.experimentType = undefined;
		if($scope.form.processType && $scope.form.experimentCategory){
			$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory.code, processTypeCode:$scope.form.processType.code}, true);
		}else if($scope.form.experimentCategory){
			$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory.code}, true);
		}
		
		this.search();
	};
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	};
	
	
	$scope.search = function(){
		if($scope.form.experimentType || $scope.newExperiment != "new"){ 		
			var jsonSearch = {};			
			jsonSearch.stateCode = 'A';	//default state code for containers		
			if($scope.form.projectCode){
				jsonSearch.projectCodes = $scope.form.projectCode;
			}			
			if($scope.form.sampleCode){
				jsonSearch.sampleCodes = $scope.form.sampleCode;
			}			
			if($scope.form.processType){
				jsonSearch.processTypeCode = $scope.form.processType.code;
			}		
			
			if($scope.form.experimentType){
				jsonSearch.experimentTypeCode = $scope.form.experimentType.code;
			}
			
			if($scope.form.user){
				jsonSearch.users = $scope.form.user;
			}
			
			if($scope.form.fromDate)jsonSearch.fromDate = moment($scope.form.fromDate).valueOf();
			if($scope.form.toDate)jsonSearch.toDate = moment($scope.form.toDate).valueOf();	
			
			$scope.datatable.search(jsonSearch);
		}							
	};
	
	$scope.addToBasket = function(containers){
		for(var i = 0; i < containers.length; i++){
			this.basket.add(containers[i]);
		}
		
		if(($scope.form.experimentType || $scope.newExperiment != "new") && this.basket.length() > 0 && $scope.getTabs().length === 1){
			$scope.addTabs({label:$scope.form.experimentType.name,href:"/experiments/new/"+$scope.form.experimentType.code,remove:false});
		}
	};
	
	//init
	if(angular.isUndefined($scope.getDatatable())){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.setDatatable($scope.datatable);	
	} else {
		$scope.datatable = $scope.getDatatable();
	}
	if($routeParams.newExperiment === undefined){
		$scope.newExperiment = "new";
	}else{
		$scope.newExperiment = $routeParams.newExperiment;
	}
	
	if(angular.isUndefined($scope.getHomePage())){
		$scope.setHomePage('new');
		$scope.addTabs({label:Messages('experiments.tabs.create'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("new").url,remove:false});
		$scope.activeTab(0);
	}
	
	if(angular.isUndefined($scope.getBasket())){
		$scope.basket = basket($scope);			
		$scope.setBasket($scope.basket);
	} else {
		$scope.basket = $scope.getBasket();
	}
	
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		$scope.setForm($scope.form);
		$scope.lists.refresh.projects();
		$scope.lists.refresh.types({objectTypeCode:"Process"}, true);
		$scope.lists.refresh.processCategories();
		$scope.lists.refresh.experimentCategories();
		$scope.lists.refresh.users();
	} else {
		$scope.form = $scope.getForm();		
	}
}
SearchContainerCtrl.$inject = ['$scope','$routeParams', '$filter','datatable','basket','lists'];
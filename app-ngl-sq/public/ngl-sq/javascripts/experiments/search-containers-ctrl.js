"use strict";

function SearchContainerCtrl($scope,$routeParams, $filter, datatable,basket, lists) {
	 
	$scope.datatableConfig = {
		search:{
			url:jsRoutes.controllers.supports.api.Supports.list()
		},
		order:{
			active:true,
			by:'code'
		},
		otherButtons :{
			active:true
		}
	};
		
	$scope.lists = lists;
	
	var search = function(values, query){
		var queryElts = query.split(',');
		
		var lastQueryElt = queryElts.pop();
		
		var output = [];
		angular.forEach($filter('filter')(values, lastQueryElt), function(value, key){
			if(queryElts.length > 0){
				this.push(queryElts.join(',')+','+value.code);
			}else{
				this.push(value.code);
			}
		}, output);
		
		return output;
	}
	
	$scope.init = function(){
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
			$scope.addTabs({label:Messages('experiments.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("new").url,remove:false});
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
		
		/*if($scope.newExperiment == "newp"){
			$scope.form.experimentCategories.selected = {"code":"purification","name":"purification"};
			$scope.form.experimentTypes.options = $scope.comboLists.getExperimentTypesByCategory($scope.form.experimentCategories.selected.code).query();
		}else if($scope.newExperiment == "newqc"){
			$scope.form.experimentCategories.selected = {"code":"qualitycontrol","name":"qualitycontrol"};
			$scope.form.experimentTypes.options = $scope.comboLists.getExperimentTypesByCategory($scope.form.experimentCategories.selected.code).query();
		}*/
	}
	
	$scope.changeExperimentType = function(){
		/*$scope.removeTab(1);
		
		$scope.basket.reset();*/
		this.search();
	}
	
	$scope.changeProcessCategory = function(){
		/*$scope.removeTab(1);
		
		$scope.basket.reset();*/
		
		$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
	}
	
	$scope.searchProjects = function(query){
		return search(lists.getProjects(), query);
	}
	
	$scope.searchUsers = function(query){
		return search(lists.getUsers(), query);
	}
	
	$scope.searchSamples = function(query){
		return search(lists.getSamples(), query);
	}
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	}
	
	$scope.changeProcessType = function(){
		/*$scope.removeTab(1);
		
		$scope.basket.reset();*/
		
		if($scope.form.processType != null){
			$scope.lists.refresh.experimentCategories({processTypeCode:$scope.form.processType.code});
		}else{
			$scope.lists.refresh.experimentCategories();
		}
	}
	
	$scope.changeExperimentCategory = function(){
		$scope.removeTab(1);
		
		$scope.basket.reset();
		//$scope.form.experimentTypes.options = $scope.comboLists.getExperimentTypesByCategory($scope.form.experimentCategories.selected.code).query();
		$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory.code}, true);
		//this.search();
	}
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCodes){
			lists.refresh.samples({projectCodes:$scope.form.projectCodes.split(',')});
		}
	}
	
	
	$scope.search = function(){
		if($scope.form.experimentType || $scope.newExperiment != "new"){ 		
			var jsonSearch = {};			
			jsonSearch.stateCode = 'A';	//default state code for containers		
			if($scope.form.projectCodes){
				jsonSearch.projectCodes = $scope.form.projectCodes.split(",");
			}			
			if($scope.form.sampleCodes){
				jsonSearch.sampleCodes = $scope.form.sampleCodes.split(",");
			}			
			if($scope.form.processType){
				jsonSearch.processTypeCode = $scope.form.processType.code;
			}		
			
			if($scope.form.experimentType){
				jsonSearch.experimentTypeCode = $scope.form.experimentType.code;
			}
			
			if($scope.form.users){
				jsonSearch.users = $scope.form.users.split(",");
			}
			
			if($scope.form.fromDate)jsonSearch.fromDate = moment($scope.form.fromDate).valueOf();
			if($scope.form.toDate)jsonSearch.toDate = moment($scope.form.toDate).valueOf();	
			
			$scope.datatable.search(jsonSearch);
		}							
	}
	
	$scope.addToBasket = function(containers){
		for(var i = 0; i < containers.length; i++){
			this.basket.add(containers[i]);
		}
		
		if(($scope.form.experimentType || $scope.newExperiment != "new") && this.basket.length() > 0 && $scope.getTabs().length === 1){
			$scope.addTabs({label:$scope.form.experimentType.name,href:"/experiments/new/"+$scope.form.experimentType.code,remove:false});
		}
	}
}
SearchContainerCtrl.$inject = ['$scope','$routeParams', '$filter','datatable','basket','lists'];
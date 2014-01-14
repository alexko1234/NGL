"use strict";

function SearchContainerCtrl($scope,$routeParams, datatable,basket, lists) {
	 
	$scope.datatableConfig = {
		search:{
			url:jsRoutes.controllers.containers.api.Containers.list()
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
			
			$scope.lists.refresh.experimentCategories();
			$scope.lists.refresh.projects();
			$scope.lists.refresh.types({objectTypeCode:"Process"}, true);
			
		} else {
			$scope.form = $scope.getForm();		
		}
		
		if($scope.newExperiment == "newp"){
			$scope.form.experimentCategories.selected = {"code":"purification","name":"purification"};
			$scope.form.experimentTypes.options = $scope.comboLists.getExperimentTypesByCategory($scope.form.experimentCategories.selected.code).query();
		}else if($scope.newExperiment == "newqc"){
			$scope.form.experimentCategories.selected = {"code":"qualitycontrol","name":"qualitycontrol"};
			$scope.form.experimentTypes.options = $scope.comboLists.getExperimentTypesByCategory($scope.form.experimentCategories.selected.code).query();
		}
	}
	
	$scope.changeExperimentType = function(){
		/*$scope.removeTab(1);
		
		$scope.basket.reset();*/
		this.search();
	}
	
	$scope.changeExperimentCategory = function(){
		$scope.removeTab(1);
		
		$scope.basket.reset();
		//$scope.form.experimentTypes.options = $scope.comboLists.getExperimentTypesByCategory($scope.form.experimentCategories.selected.code).query();
		$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory.code}, true);
		//this.search();
	}
	
	$scope.changeProject = function(){
		if($scope.form.project){
			$scope.lists.refresh.samples({projectCode:$scope.form.project.code});
		}else{
			$scope.lists.clear("samples");
		}
		
		$scope.search();
	}
	
	
	$scope.search = function(){
		if($scope.form.experimentType || $scope.newExperiment != "new"){ 		
			var jsonSearch = {};			
			jsonSearch.stateCode = 'A';	//default state code for containers		
			if($scope.form.project){
				jsonSearch.projectCode = $scope.form.project.code;
			}			
			if($scope.form.sample){
				jsonSearch.sampleCode = $scope.form.sample.code;
			}			
			if($scope.form.processType){
				jsonSearch.processTypeCode = $scope.form.processType.code;
			}		
			
			if($scope.form.experimentType){
				jsonSearch.experimentTypeCode = $scope.form.experimentType.code;
			}		
			
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
SearchContainerCtrl.$inject = ['$scope','$routeParams', 'datatable','basket','lists'];
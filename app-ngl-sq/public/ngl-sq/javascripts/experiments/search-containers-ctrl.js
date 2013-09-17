"use strict";

function SearchContainerCtrl($scope,$routeParams, datatable,basket, comboLists) {
	 
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
		
	$scope.comboLists = comboLists;
	
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
			$scope.form = {
					experimentTypes:{},
					experimentCategories:{},
					processTypes:{},
					projects:{},
					samples:{}
			};
			$scope.setForm($scope.form);
			$scope.form.experimentCategories.options = $scope.comboLists.getExperimentCategories().query();
			$scope.form.processTypes.options = $scope.comboLists.getProcessTypes().query();
			$scope.form.projects.options = $scope.comboLists.getProjects().query();
			
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
		$scope.form.experimentTypes.options = $scope.comboLists.getExperimentTypesByCategory($scope.form.experimentCategories.selected.code).query();
		
		//this.search();
	}
	
	$scope.changeProject = function(){
		if($scope.form.projects.selected){
			$scope.form.samples.options =  $scope.comboLists.getSamples($scope.form.projects.selected.code).query();			
		} else {
			$scope.form.samples.options = [];
		}	
		$scope.search();
	}
	
	
	$scope.search = function(){
		if($scope.form.experimentTypes.selected || $scope.newExperiment != "new"){ 		
			var jsonSearch = {};			
			jsonSearch.stateCode = 'A';	//default state code for containers		
			if($scope.form.projects.selected){
				jsonSearch.projectCode = $scope.form.projects.selected.code;
			}			
			if($scope.form.samples.selected){
				jsonSearch.sampleCode = $scope.form.samples.selected.code;
			}			
			if($scope.form.processTypes.selected){
				jsonSearch.processTypeCode = $scope.form.processTypes.selected.code;
			}		
			
			if($scope.form.experimentTypes.selected){
				jsonSearch.experimentTypeCode = $scope.form.experimentTypes.selected.code;
			}		
			
			$scope.datatable.search(jsonSearch);
		}							
	}
	
	$scope.addToBasket = function(containers){
		for(var i = 0; i < containers.length; i++){
			this.basket.add(containers[i]);
		}
		
		if(($scope.form.experimentTypes.selected || $scope.newExperiment != "new") && this.basket.length() > 0 && $scope.getTabs().length === 1){
			$scope.addTabs({label:$scope.form.experimentTypes.selected.name,href:"/experiments/new/"+$scope.form.experimentTypes.selected.code,remove:false});
		}
	}
}
SearchContainerCtrl.$inject = ['$scope','$routeParams', 'datatable','basket','comboLists'];
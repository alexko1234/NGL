"use strict";

function SearchContainerCtrl($scope, datatable,basket, comboLists) {
	 
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
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		

		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('new');
			$scope.addTabs({label:Messages('experiments.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("new").url,remove:false});
			$scope.activeTab(0);
		}
		
		if(angular.isUndefined($scope.getBasket())){
			$scope.basket = basket($scope);			
			$scope.setBasket($scope.basket);
		}else{
			$scope.basket = $scope.getBasket();
		}
		
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {
					experimentTypes:{},
					processTypes:{},
					projects:{},
					samples:{}
			};
			$scope.setForm($scope.form);
			$scope.form.experimentTypes.options = $scope.comboLists.getExperimentTypes().query();
			$scope.form.processTypes.options = $scope.comboLists.getProcessTypes().query();
			$scope.form.projects.options = $scope.comboLists.getProjects().query();
			
		}else{
			$scope.form = $scope.getForm();		
		}
	}
	
	$scope.changeExperimentType = function(){
		$scope.removeTab(1);
		$scope.basket.reset();
		this.search();
	}
	
	$scope.changeProject = function(){
		if($scope.form.projects.selected){
			$scope.form.samples.options =  $scope.comboLists.getSamples($scope.form.projects.selected.code).query();			
		}else{
			$scope.form.samples.options = [];
		}	
		if($scope.form.experimentTypes.selected){
			$scope.search();
		}
	}
	
	
	$scope.search = function(){
		if($scope.form.experimentTypes.selected){ 		
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
		if($scope.form.experimentTypes.selected){
			$scope.addTabs({label:$scope.form.experimentTypes.selected.name,href:"/experiments/new/"+$scope.form.experimentTypes.selected.code,remove:false});
		}
	}
}
SearchContainerCtrl.$inject = ['$scope', 'datatable','basket','comboLists'];
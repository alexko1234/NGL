"use strict"

function SearchCtrl($scope,$location,$routeParams,$filter, datatable, lists) {
	
	$scope.datatableConfig = {	
			columns:[
			{
				"header":Messages("experiments.table.code"),
				"property":"code",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("experiments.table.projectCodes"),
				"property":"projectCodes",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.sampleCodes"),
				"property":"sampleCodes",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.valid"),
				"property":"valuation.valid",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.state.code"),
				"property":"state.code",
				"order":true,
				"type":"text",
				"hide":true,
				"filter":"codes:'state'"
			},
			{
				"header":Messages("experiments.table.categoryCode"),
				"property":"categoryCode",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.fromExperimentTypeCodes"),
				"property":"typeCode",
				"order":true,
				"hide":true,
				"type":"text"
			}
			],
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:"/experiments/edit/"+line.code,remove:true});
				}
			},
			search:{
				url:jsRoutes.controllers.experiments.api.Experiments.list()
				
			},
			order:{
				by:'code'
			},
			edit:{
				active:true
			}
	};
	
	$scope.lists = lists;
	
	$scope.changeTypeCode = function(){
		$scope.search();
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
	
	$scope.changeExperimentCategory = function(){
		$scope.form.experimentType = undefined;
		if($scope.form.processType && $scope.form.experimentCategory){
			$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory.code, processTypeCode:$scope.form.processType.code}, true);
		}else if($scope.form.experimentCategory){
			$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory.code}, true);
		}
	};
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	};
	
	$scope.search = function(){		
		if($scope.form.projectCode || $scope.form.sampleCode || $scope.form.type || ($scope.form.fromDate && $scope.form.toDate)){
			var jsonSearch = {};			

			if($scope.form.projectCode){
				jsonSearch.projectCodes = $scope.form.projectCode;
			}			
			if($scope.form.sampleCode){
				jsonSearch.sampleCodes = $scope.form.sampleCode;
			}			
			if($scope.form.processType){
				jsonSearch.processTypeCode = $scope.form.processType.code;
			}		
			
			if($scope.form.type){
				jsonSearch.typeCode = $scope.form.type.code;
			}
			
			if($scope.form.fromDate)jsonSearch.fromDate = moment($scope.form.fromDate, Messages("date.format").toUpperCase()).valueOf();
			if($scope.form.toDate)jsonSearch.toDate = moment($scope.form.toDate, Messages("date.format").toUpperCase()).valueOf();
			
			$scope.datatable.search(jsonSearch);	
		}
	};
	
	//init
	if(angular.isUndefined($scope.getHomePage())){
		$scope.setHomePage('search');
		$scope.addTabs({label:Messages('experiments.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("search").url,remove:false});
		$scope.activeTab(0);
	}
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		$scope.setForm($scope.form);
		$scope.lists.refresh.types({objectTypeCode:"Process"}, true);
		$scope.lists.refresh.processCategories();
		$scope.lists.refresh.experimentCategories();
		$scope.lists.refresh.projects();
		
	}else{
		$scope.form = $scope.getForm();			
	}
	$scope.datatable = datatable($scope, $scope.datatableConfig);
	if($scope.form.type){
		$scope.search();
	}
}

SearchCtrl.$inject = ['$scope','$location','$routeParams','$filter', 'datatable','lists'];
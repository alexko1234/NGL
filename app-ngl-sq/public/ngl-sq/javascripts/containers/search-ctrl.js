"use strict"

angular.module('home').controller('SearchCtrl', ['$scope', 'datatable','lists','$filter', function($scope, datatable, lists,$filter) {
	$scope.lists = lists;
	
	$scope.datatableConfig = {	
			columns:[
				{
					"header":Messages("containers.table.supportCode"),
					"property":"support.code",
					"order":true,
					"type":"text"
			},
			{
				"header":Messages("containers.table.support.position"),
				"property":"support.position",
				"order":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.supportCategoryCode"),
				"property":"support.categoryCode",
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
				"header":Messages("containers.table.sampleCodes"),
				"property":"sampleCodes",
				"order":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.state.code"),
				"property":"state.code",
				"order":true,
				"type":"text",
				"edit":true,
				"choiceInList": true,
				"possibleValues":'lists.getStates()',
				"filter":"codes:'state'"
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
				"header":Messages("containers.table.fromExperimentTypeCodes"),
				"property":"fromExperimentTypeCodes",
				"order":true,
				"type":"text"
			}
		],
		search:{
			url:jsRoutes.controllers.containers.api.Containers.list()
		},
		order:{
			by:'traceInformation.creationDate',
			reverse:true
		},
		edit:{
			active:true,
			columnMode:true
		},
		save:{
			active:true,
			url:jsRoutes.controllers.containers.api.Containers.updateBatch().url,
			batch:true,
			method:'put'
		}
	};
	
	$scope.changeProject = function(){
		if($scope.form.project){
				$scope.lists.refresh.samples({projectCode:$scope.form.project.code});
			}else{
				$scope.lists.clear("samples");
			}
		
		if($scope.form.type){
			$scope.search();
		}
	};
	
	$scope.changeProcessType = function(){
		if($scope.form.processCategory){
			$scope.search();
		}else{
			$scope.form.processType = undefined;	
		}
	};
	
	$scope.changeProcessCategory = function(){
		$scope.form.processType = undefined;
		if($scope.form.processCategory){
		$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
		}
	};
	
	$scope.reset = function(){
		$scope.form = {};
	};
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCodes && $scope.form.projectCodes.length>0){
			lists.refresh.samples({projectCodes:$scope.form.projectCodes});
		}
	};

	$scope.search = function(){		
		if($scope.form.projectCodes || $scope.form.sampleCodes || ($scope.form.fromExperimentTypeCodes && $scope.form.fromExperimentTypeCodes.length > 0) || $scope.form.containerCategory 
			|| $scope.form.containerSupportCategory || $scope.form.state || $scope.form.containerSupportCode  || $scope.form.valuations){	
			
			var jsonSearch = {};
			
			if($scope.form.projectCodes){
				jsonSearch.projectCodes = $scope.form.projectCodes;
			}			
			if($scope.form.sampleCodes){
				jsonSearch.sampleCodes = $scope.form.sampleCodes;
			}		
			
			if($scope.form.valuations){
				jsonSearch.valuations = $scope.form.valuations;
			}
			
			if($scope.form.fromExperimentTypeCodes){
				jsonSearch.fromExperimentTypeCodes = $scope.form.fromExperimentTypeCodes;
			}
			
			if($scope.form.containerCategory){
				jsonSearch.categoryCode = $scope.form.containerCategory;
			}
			
			if($scope.form.containerSupportCategory){
				jsonSearch.containerSupportCategory = $scope.form.containerSupportCategory;
			}	
			
			
			if($scope.form.state){
				jsonSearch.stateCode = $scope.form.state.code;
			}	
			
			if($scope.form.containerSupportCode){
				jsonSearch.supportCode = $scope.form.containerSupportCode;
			}	
			
			$scope.datatable.search(jsonSearch);
		}
	};
	
	//init
	$scope.datatable = datatable($scope.datatableConfig);		
	if(angular.isUndefined($scope.getHomePage())){
		$scope.setHomePage('new');
		$scope.addTabs({label:Messages('containers.tabs.search'),href:jsRoutes.controllers.containers.tpl.Containers.home("new").url,remove:false});
		$scope.activeTab(0);
	}
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		$scope.setForm($scope.form);
		$scope.lists.refresh.containerSupportCategories();
		
		$scope.lists.refresh.containerCategories();
		$scope.lists.refresh.experimentTypes();
		$scope.lists.refresh.supports();
		$scope.lists.refresh.projects();
		$scope.lists.refresh.processCategories();
		$scope.lists.refresh.states({objectTypeCode:"Container"});
	}
}]);
"use strict"

function SearchCtrl($scope, datatable, lists,$filter) {
	$scope.lists = lists;
	
	$scope.datatableConfig = {	
			columns:[
				{
					"header":Messages("containers.table.supportCode"),
					"property":"support.supportCode",
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
				"type":"text"
			},
			{
				"header":Messages("containers.table.state.code"),
				"property":"state.code",
				"order":true,
				"type":"text",
				"filter":"codes:'state'"
			},
			{
				"header":Messages("containers.table.creationDate"),
				"property":"traceInformation.creationDate",
				"order":true,
				"type":"date"
			},
		],
		search:{
			url:jsRoutes.controllers.containers.api.Containers.list()
		},
		order:{
			by:'code'
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
	
	$scope.reset = function(){
		$scope.form = {};
	};
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	};

	$scope.search = function(){		
		if($scope.form.projectCode || $scope.form.sampleCode || ($scope.form.fromExperimentTypeCodes && $scope.form.fromExperimentTypeCodes.length > 0) || $scope.form.containerCategory 
			|| $scope.form.containerSupportCategory || $scope.form.state || $scope.form.containerSupportCode){	
			
			var jsonSearch = {};
			
			if($scope.form.projectCode){
				jsonSearch.projectCodes = $scope.form.projectCode;
			}			
			if($scope.form.sampleCode){
				jsonSearch.sampleCodes = $scope.form.sampleCode;
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
	$scope.datatable = datatable($scope, $scope.datatableConfig);		
	if(angular.isUndefined($scope.getHomePage())){
		$scope.setHomePage('new');
		$scope.addTabs({label:Messages('containers.tabs.search'),href:jsRoutes.controllers.containers.tpl.Containers.home("new").url,remove:false});
		$scope.activeTab(0);
	}
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		$scope.setForm($scope.form);
		$scope.lists.refresh.containerSupportCategories();
		$scope.lists.refresh.projects();
		$scope.lists.refresh.containerCategories();
		$scope.lists.refresh.experimentTypes();
		$scope.lists.refresh.supports();
		$scope.lists.refresh.states({objectTypeCode:"Container"});
	}
}

SearchCtrl.$inject = ['$scope', 'datatable','lists','$filter'];
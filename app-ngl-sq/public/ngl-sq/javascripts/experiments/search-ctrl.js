"use strict"
angular.module('home').controller('SearchCtrl', ['$scope','$location','$routeParams','$filter', 'datatable','lists', function($scope,$location,$routeParams,$filter, datatable, lists) {
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
				"header":Messages("experiments.table.categoryCode"),
				"property":"categoryCode",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("experiments.table.typeCode"),
				"property":"typeCode",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("experiments.table.state.code"),
				"property":"state.code",
				"order":true,
				"type":"text",
				"hide":true,
				"filter":"codes:'state'"
			},
			{
				"header":Messages("experiments.table.resolutionCodes"),
				"property":"state.resolutionCodes",
				"order":true,
				"hide":true,
				"type":"date"
			},
			{
				"header":Messages("experiments.table.creationDate"),
				"property":"traceInformation.creationDate",
				"order":true,
				"hide":true,
				"type":"date"
			},
			{
				"header":Messages("experiments.table.createUser"),
				"property":"traceInformation.createUser",
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
				by:'traceInformation.creationDate',
				reverse :true
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
		if($scope.form.processCategory){
			$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory});
		}
		$scope.form.processType = undefined;
	};
	
	$scope.changeProcessType = function(){
		$scope.form.experimentType = undefined;
		$scope.form.experimentCategory = undefined;
	};
	
	$scope.changeExperimentCategory = function(){
		$scope.form.experimentType = undefined;
		if($scope.form.processType && $scope.form.experimentCategory){
			$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory, processTypeCode:$scope.form.processType});
		}else if($scope.form.experimentCategory){
			$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory});
		}
	};
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCodes && $scope.form.projectCodes.length >0){
			lists.refresh.samples({projectCodes:$scope.form.projectCodes});
		}
	};
	
	$scope.loadExperimentTypesLists = function(){
		$scope.lists.refresh.experimentTypes({categoryCode:"purification"}, "purifications");
		$scope.lists.refresh.experimentTypes({categoryCode:"qualitycontrol"}, "qualitycontrols");
		$scope.lists.refresh.experimentTypes({categoryCode:"transfert"}, "transferts");
		$scope.lists.refresh.experimentTypes({categoryCode:"transformation"}, "transformations");
	};
	
	$scope.search = function(){		
		if($scope.form.processType || $scope.form.experimentType || $scope.form.projectCodes || $scope.form.sampleCodes || $scope.form.type || $scope.form.fromDate || $scope.form.toDate || $scope.form.state || $scope.form.user || $scope.form.containerSupportCode){
			var jsonSearch = {};			

			if($scope.form.projectCodes){
				jsonSearch.projectCodes = $scope.form.projectCodes;
			}			
			if($scope.form.sampleCodes){
				jsonSearch.sampleCodes = $scope.form.sampleCodes;
			}			
			if($scope.form.processType){
				jsonSearch.processTypeCode = $scope.form.processType;
			}		
			
			if($scope.form.containerSupportCode){
				jsonSearch.containerSupportCode = $scope.form.containerSupportCode;
			}
			
			if($scope.form.type){
				jsonSearch.typeCode = $scope.form.type;
			}
			
			if($scope.form.state){
				jsonSearch.stateCode = $scope.form.state;
			}

			if($scope.form.user){
				jsonSearch.users = $scope.form.user;
			}
			
			if($scope.form.experimentType){
				jsonSearch.typeCode = $scope.form.experimentType;
			}
			
			if($scope.form.fromDate)jsonSearch.fromDate = moment($scope.form.fromDate, Messages("date.format").toUpperCase()).valueOf();
			if($scope.form.toDate)jsonSearch.toDate = moment($scope.form.toDate, Messages("date.format").toUpperCase()).valueOf();
			
			$scope.datatable.search(jsonSearch);	
		}
	};
	
	//init
	if(angular.isUndefined($scope.getHomePage())){
		$scope.setHomePage('search');
		$scope.addTabs({label:Messages('experiments.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("search").url,remove:true});
		$scope.activeTab(0);
	}
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		$scope.setForm($scope.form);
		$scope.lists.refresh.types({objectTypeCode:"Process"}, true);
		$scope.lists.refresh.processCategories();
		$scope.lists.refresh.experimentCategories();
		$scope.lists.refresh.projects();
		$scope.lists.refresh.users();
		$scope.lists.refresh.states({objectTypeCode:"Experiment"});
		$scope.loadExperimentTypesLists();
	}else{
		$scope.form = $scope.getForm();			
	}
	$scope.datatable = datatable($scope.datatableConfig);
	if($scope.form.type){
		$scope.search();
	}
}]);
"use strict"
angular.module('home').controller('SearchCtrl', ['$scope','$location','$routeParams','$filter', 'datatable','lists','mainService','tabService','experimentsSearchService', function($scope,$location,$routeParams,$filter, datatable, lists, mainService,tabService,experimentsSearchService) {
	$scope.datatableConfig = {	
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.experiments.tpl.Experiments.edit(line.code).url,remove:true});
				}
			},
			search:{
				url:jsRoutes.controllers.experiments.api.Experiments.list()
				
			},
			pagination:{
				mode:'local'
			},
			order:{
				by:'traceInformation.creationDate',
				reverse :true,
				mode:'local'
			},
			hide:{
		 		 active:true
		 	},
			edit:{
				active:false
			},
			exportCSV:{
				active:true
			}
	};
	
	$scope.search = function(){		
		$scope.searchService.search();
	};
	
	$scope.reset = function(){
		$scope.searchService.resetForm();		
	};
	
	
	$scope.changeContainerSupportCode = function(val){
		console.log(val);
		return $scope.searchService.changeContainerSupportCode(val);		 
	};

	$scope.changeExperimentType = function(){
		$scope.searchService.changeExperimentType();
	};
	
	$scope.changeProcessType = function(){
		$scope.searchService.changeProcessType();
	};
	
	$scope.changeProcessCategory = function(){
		$scope.searchService.changeProcessCategory();
	};
	
	//init
	if(angular.isUndefined($scope.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('experiments.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("search").url,remove:true});
		tabService.activeTab(0);
	}
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		mainService.setForm($scope.form);
	}else{
		$scope.form = mainService.getForm();		
	}
	
	$scope.searchService = experimentsSearchService;
	$scope.searchService.init($routeParams, $scope.datatableConfig);	
	
	
	if($scope.form.experimentType){
		//$scope.search();
	}
}]);
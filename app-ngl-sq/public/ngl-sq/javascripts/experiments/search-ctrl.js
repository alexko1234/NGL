"use strict"
angular.module('home').controller('SearchCtrl', ['$scope','$location','$routeParams','$filter', 'datatable','lists','mainService','tabService','experimentsSearchService', function($scope,$location,$routeParams,$filter, datatable, lists, mainService,tabService,experimentsSearchService) {
	$scope.datatableConfig = {	
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:"/experiments/edit/"+line.code,remove:true});
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
	
	$scope.search = function(){		
		$scope.searchService.search();
	};
	
	$scope.reset = function(){
		$scope.searchService.resetForm();		
	};
	
	
	$scope.changeContainerSupportCode = function(val){
		console.log(val);
		return $scope.searchService.changeContainerSupportCode(val);		 
	}

	
	
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
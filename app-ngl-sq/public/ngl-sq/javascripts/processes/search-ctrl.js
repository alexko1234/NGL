"use strict"
angular.module('home').controller('SearchCtrl', ['$scope','$location','$routeParams', 'datatable','lists','$filter','$http','mainService','tabService','processesSearchService', function($scope,$location,$routeParams, datatable, lists,$filter,$http,mainService,tabService,processesSearchService) {
	$scope.datatableConfig = {
			search:{
				url:jsRoutes.controllers.processes.api.Processes.list()
				
			},
			order:{
				by:'code'
			},
			columnsUrl:jsRoutes.controllers.processes.tpl.Processes.searchColumns().url,
			edit:{
				active:true,
				columnMode:true
			},
			save:{
				active:true,
				url:function(line){return jsRoutes.controllers.processes.api.Processes.update(line.code).url;},
				mode:'remote',
				method:'put',
			}
	};

	$scope.reset = function(){
		$scope.searchService.resetForm();
		//$scope.searchService.getColumn();
	};
	
	$scope.search = function(){	
		$scope.searchService.search();
	};
	
	//init
	if(angular.isUndefined($scope.getHomePage())){
		mainService.setHomePage('new');
		tabService.addTabs({label:Messages('processes.tabs.search'),href:jsRoutes.controllers.processes.tpl.Processes.home("new").url,remove:false});
		tabService.activeTab(0);
	}
	
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		mainService.setForm($scope.form);
	}else{
		$scope.form = mainService.getForm();			
	}
	
	$scope.searchService = processesSearchService;
	$scope.searchService.init($routeParams, $scope.datatableConfig)
	
	if($scope.form.project || $scope.form.type){
		$scope.search();
	}
	
	
}]);
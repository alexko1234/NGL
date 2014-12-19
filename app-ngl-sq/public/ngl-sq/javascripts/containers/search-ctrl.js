"use strict"

angular.module('home').controller('SearchCtrl', ['$scope', 'datatable','lists','$filter','mainService','tabService','containersSearchService','$routeParams', function($scope, datatable, lists,$filter,mainService,tabService,containersSearchService,$routeParams) {
	$scope.datatableConfig = {
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
			method:'put',
			callback: function(reason, error){
				
				console.log("callback reason=" + reason);
				console.log("callback error=" + error);
			}
		}
	};

	$scope.search = function(){		
		$scope.searchService.search();
	};
	
	$scope.reset = function(){
		$scope.searchService.resetForm();		
	};
	
	//init
	$scope.datatable = datatable($scope.datatableConfig);		
	if(angular.isUndefined($scope.getHomePage())){
		mainService.setHomePage('new');
		tabService.addTabs({label:Messages('containers.tabs.search'),href:jsRoutes.controllers.containers.tpl.Containers.home("new").url,remove:false});
		tabService.activeTab(0);
	}
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		mainService.setForm($scope.form);
	}
	
	$scope.searchService = containersSearchService;
	$scope.searchService.init($routeParams, $scope.datatableConfig)
}]);
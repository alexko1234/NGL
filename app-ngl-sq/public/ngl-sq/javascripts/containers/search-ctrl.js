"use strict"

angular.module('home').controller('SearchCtrl', ['$scope', 'datatable','lists','$filter','mainService','tabService','containersSearchService','$routeParams', function($scope, datatable, lists,$filter,mainService,tabService,containersSearchService,$routeParams) {
	var datatableConfig = {
		group:{active:true},
		search:{
			url:jsRoutes.controllers.containers.api.Containers.list()
		},
		pagination:{
			mode:'local'
		},
		order:{
			//by:"['support.code','support.column']",
			by:'traceInformation.creationDate',
			reverse:true,
			mode:'local'
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
		
	};

	
	
	$scope.search = function(){		
		$scope.searchService.search();
	};
	
	$scope.reset = function(){
		$scope.searchService.resetForm();		
	};
	
	//init
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
	$scope.searchService.init($routeParams, datatableConfig)
}]);


"use strict"
angular.module('home').controller('SearchStateCtrl', ['$scope','$location','$routeParams', 'datatable','lists','$filter','$http','mainService','tabService','containersSearchService', function($scope,$location,$routeParams, datatable, lists,$filter,$http,mainService,tabService,containersSearchService) {
	var datatableConfig = {
			search:{
				url:jsRoutes.controllers.containers.api.Containers.list()
				
			},
			order:{
				by:'code',
				mode:'local'
			},
			edit:{
				active:true,
				columnMode:true
			},
			pagination:{
				mode:'local'
			},
			save:{
				active:true,
				url:function(line){return jsRoutes.controllers.containers.api.Containers.updateStateCode(line.code).url;},
				mode:'remote',
				method:'put',
				value:function(container){
					var updateForm = {};
					updateForm.stateCode = container.state.code;
					return updateForm;
				}
			}
	};

	$scope.reset = function(){
		$scope.searchService.resetForm();
	};
	
	$scope.search = function(){	
		$scope.searchService.search();
	};
	
	//init
	if(angular.isUndefined($scope.getHomePage())){
		mainService.setHomePage('state');
		tabService.addTabs({label:Messages('containers.tabs.state'),href:jsRoutes.controllers.containers.tpl.Containers.home("new").url,remove:false});
		tabService.activeTab(0);
	}
	
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		mainService.setForm($scope.form);
	}else{
		$scope.form = mainService.getForm();			
	}
	
	$scope.searchService = containersSearchService;
	$scope.searchService.init($routeParams, datatableConfig)
	
	if($scope.form.project || $scope.form.type){
		$scope.search();
	}
	
	
}]);
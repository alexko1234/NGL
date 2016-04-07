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
		group:{
			active:true,
			showOnlyGroups:true,
			enableLineSelection:true,
			showButton:true
		},
		hide:{
			active:true
		},
		order:{
			//by:"['support.code','support.column']",
			by:'traceInformation.creationDate',
			reverse:true,
			mode:'local'
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
	
	//init
	if(angular.isUndefined($scope.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('containers.tabs.search'),href:jsRoutes.controllers.containers.tpl.Containers.home("search").url,remove:false});
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
				by:'traceInformation.creationDate',
				reverse : true,
				mode:'local'
			},
			edit:{
				active:Permissions.check("writing")?true:false,
				columnMode:true
			},
			pagination:{
				mode:'local'
			},
			save:{
				active:Permissions.check("writing")?true:false,
				url:function(line){return jsRoutes.controllers.containers.api.Containers.updateStateBatch().url;},
				mode:'remote',
				method:'put',
				batch:true,
				value:function(line){return {code:line.code,state:line.state};}
			},
			hide:{
				active:true
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
		tabService.addTabs({label:Messages('containers.tabs.state'),href:jsRoutes.controllers.containers.tpl.Containers.home("state").url,remove:false});
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
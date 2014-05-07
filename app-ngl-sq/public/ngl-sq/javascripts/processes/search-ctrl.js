"use strict"
angular.module('home').controller('SearchCtrl', ['$scope','$location','$routeParams', 'datatable','lists','$filter','$http', function($scope,$location,$routeParams, datatable, lists,$filter,$http) {

	$scope.lists = lists;
	
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
	
	$scope.changeProcessTypeCode = function(){
		if($scope.form.processCategory){
			$scope.getColumns();
			$scope.search();
		}else{
			$scope.form.processType = undefined;	
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
		$scope.getColumns();
	};
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	};
	
	$scope.changeProcessCategory = function(){
		if($scope.form.processCategory){
			$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
		}else{
			$scope.lists.clear("processTypes");
		}
	};
	
	$scope.search = function(){	
		if($scope.form.projectCode || $scope.form.sampleCode || $scope.form.processType 
				|| $scope.form.processCategory || $scope.form.processesSupportCode || $scope.form.state || $scope.form.user
				|| $scope.form.fromDate || $scope.form.toDate){
			var jsonSearch = {};
			if($scope.form.projectCode){
				jsonSearch.projectCode = $scope.form.projectCode;
			}			
			
			if($scope.form.sampleCode){
				jsonSearch.sampleCode = $scope.form.sampleCode;
			}				
			
			if($scope.form.processType){
				jsonSearch.typeCode = $scope.form.processType.code;
			}
			
			if($scope.form.processCategory){
				jsonSearch.categoryCode = $scope.form.processCategory.code;
			}
			
			if($scope.form.processesSupportCode){
				jsonSearch.supportCode = $scope.form.processesSupportCode;
			}
			
			if($scope.form.state){
				jsonSearch.stateCode = $scope.form.state.code;
			}
			
			if($scope.form.user){
				jsonSearch.users = $scope.form.user;
			}
			
			if($scope.form.fromDate)jsonSearch.fromDate = moment($scope.form.fromDate, Messages("date.format").toUpperCase()).valueOf();
			if($scope.form.toDate)jsonSearch.toDate = moment($scope.form.toDate, Messages("date.format").toUpperCase()).valueOf();
			
			
			$scope.datatable.search(jsonSearch);						
		}else{
			$scope.datatable.setData({},0);
		}
	};
	
	$scope.getColumns = function(){
		var typeCode = "";
		if($scope.form.processType){
			typeCode = $scope.form.processType.code;
		}
		
		$http.get(jsRoutes.controllers.processes.tpl.Processes.searchColumns().url,{params:{"typeCode":typeCode}})
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.datatable.setColumnsConfig(data);
			}
		})
		.error(function(data, status, headers, config) {
		
		});
	};
	
	//init
	if(angular.isUndefined($scope.getHomePage())){
		$scope.setHomePage('new');
		$scope.addTabs({label:Messages('processes.tabs.search'),href:jsRoutes.controllers.processes.tpl.Processes.home("new").url,remove:false});
		$scope.activeTab(0);
	}
	
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		$scope.setForm($scope.form);
		$scope.lists.refresh.projects();
		$scope.lists.refresh.processCategories();
		$scope.lists.refresh.supports();
		$scope.lists.refresh.users();
		$scope.lists.refresh.states({objectTypeCode:"Process"});
	}else{
		$scope.form = $scope.getForm();			
	}
	
	$scope.datatable = datatable($scope, $scope.datatableConfig);
	if($scope.form.project || $scope.form.type){
		$scope.search();
	}
}]);
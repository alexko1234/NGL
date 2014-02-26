"use strict"

function SearchCtrl($scope,$location,$routeParams, datatable, lists,$filter,$http) {
	
	$scope.form = {
			type:{
				code:""
			}
	};
	
	$scope.lists = lists;
	
	$scope.datatableConfig = {
			search:{
				url:jsRoutes.controllers.processes.api.Processes.list()
				
			},
			order:{
				by:'code'
			}
		};
	
	$scope.changeTypeCode = function(){
		if($scope.form.type){
			//$location.path('/processes/search/'+$scope.form.type.code);
		}
		
		$scope.getColumns();
		$scope.search();
	}
	
	var search = function(values, query){
		var queryElts = query.split(',');
		
		var lastQueryElt = queryElts.pop();
		
		var output = [];
		angular.forEach($filter('filter')(values, lastQueryElt), function(value, key){
			if(queryElts.length > 0){
				this.push(queryElts.join(',')+','+value.code);
			}else{
				this.push(value.code);
			}
		}, output);
		
		return output;
	}
	$scope.changeProject = function(){
		if($scope.form.project){
				$scope.lists.refresh.samples({projectCode:$scope.form.project.code});
			}else{
				$scope.lists.clear("samples");
			}
		
		if($scope.form.type){
			$scope.search();
		}
	}
	
	
	$scope.searchProjects = function(query){
		return search(lists.getProjects(), query);
	}
	

	$scope.reset = function(){
		$scope.form = {
				
		}
	}
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	};
	
	
	$scope.searchSamples = function(query){
		return search(lists.getSamples(), query);
	}
	
	
	$scope.changeProcessCategory = function(){
		/*$scope.removeTab(1);
		
		$scope.basket.reset();*/
		
		$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
	}
	
	$scope.init = function(){
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('new');
			$scope.addTabs({label:Messages('processes.tabs.search'),href:jsRoutes.controllers.processes.tpl.Processes.home("new").url,remove:false});
			$scope.activeTab(0);
		}
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {type:{code:""}};
			$scope.setForm($scope.form);
			//$scope.form.typeCodes.options = $scope.comboLists.getProcessTypes().query();
			//$scope.form.projects.options = $scope.comboLists.getProjects().query();
			
			$scope.lists.refresh.projects();
			$scope.lists.refresh.processCategories();
			
			
		}else{
			$scope.form = $scope.getForm();			
		}
		
		$scope.datatable = datatable($scope, $scope.datatableConfig);

		
		if($scope.form.project || $scope.form.type){
			$scope.search();
		}
	}
	
	$scope.search = function(){		
			var jsonSearch = {};			

			if($scope.form.projectCode){
				jsonSearch.projectCode = $scope.form.projectCode;
			}			
			if($scope.form.sampleCode){
				jsonSearch.sampleCode = $scope.form.sampleCode;
			}				
			if($scope.form.type){
				jsonSearch.typeCode = $scope.form.type.code;
			}			
			$scope.datatable.search(jsonSearch);						
	}
	
	$scope.getColumns = function(){
		$http.get(jsRoutes.controllers.processes.tpl.Processes.searchColumns($scope.form.type.code).url)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.datatable.setColumnsConfig(data);
			}
		})
		.error(function(data, status, headers, config) {
		
		});
	};
}

SearchCtrl.$inject = ['$scope','$location','$routeParams', 'datatable','lists','$filter','$http'];
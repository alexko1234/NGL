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
					"header":Messages("containers.table.categoryCode"),
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
					"property":"valid",
					"order":true,
					"type":"text"
				},
				{
					"header":Messages("containers.table.stateCode"),
					"property":"stateCode",
					"order":true,
					"type":"text"
				},
				{
					"header":Messages("containers.table.creationDate"),
					"property":"traceInformation.creationDate",
					"order":true,
					"type":"text"
				},
			],
			search:{
				url:jsRoutes.controllers.containers.api.Containers.list()
			},
			order:{
				by:'code'
			}
		};
	
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
		if($scope.form.projectCodes){
			lists.refresh.samples({projectCodes:$scope.form.projectCodes.split(',')});
		}
	}
	
	$scope.searchSamples = function(query){
		return search(lists.getSamples(), query);
	}
	
	$scope.init = function(){
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
		$scope.lists.refresh.states({objectTypeCode:"Container"});
		}
	}
	
	$scope.search = function(){		
			var jsonSearch = {};			

			if($scope.form.projectCodes){
				jsonSearch.projectCodes = $scope.form.projectCodes.split(",");
			}			
			if($scope.form.sampleCodes){
				jsonSearch.sampleCodes = $scope.form.sampleCodes.split(",");
			}		
			
			if($scope.form.fromExperimentTypeCodes){
				jsonSearch.fromExperimentTypeCodes = $scope.form.fromExperimentTypeCodes;
			}
			
			if($scope.form.containerCategory){
				jsonSearch.categoryCode = $scope.form.containerCategory.code;
			}	
			
			if($scope.form.containerSupportCategory){
				jsonSearch.containerSupportCategory = $scope.form.containerSupportCategory.code;
			}	
			
			if($scope.form.state){
				jsonSearch.stateCode = $scope.form.state.code;
			}	
			$scope.datatable.search(jsonSearch);							
	}
}

SearchCtrl.$inject = ['$scope', 'datatable','lists','$filter'];
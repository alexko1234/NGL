"use strict"

function SearchCtrl($scope,$location,$routeParams, datatable, comboLists) {
	
	$scope.datatableConfig = {	
			search:{
				url:jsRoutes.controllers.processes.api.Processes.list()
				
			},
			order:{
				by:'code'
			}
		};
	
	$scope.comboLists = comboLists;
	
	$scope.changeTypeCode = function(){
		if($scope.form.typeCodes.selected){
			$location.path('/processes/search/'+$scope.form.typeCodes.selected.code);
		}
	}
	
	$scope.changeProject = function(){
		if($scope.form.projects.selected){
			$scope.form.samples.options =  $scope.comboLists.getSamples($scope.form.projects.selected.code).query();			
		}else{
			$scope.form.samples.options = [];
		}	
		
		$scope.search();
	}
	
	$scope.init = function(){
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {
					typeCodes:{},
					projects:{},
					samples:{}
			};
			$scope.setForm($scope.form);
			$scope.form.typeCodes.options = $scope.comboLists.getProcessTypes().query();
			$scope.form.projects.options = $scope.comboLists.getProjects().query();
			
		}else{
			$scope.form = $scope.getForm();			
		}
		
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		if($scope.form.projects.selected || $scope.form.typeCodes.selected){
			$scope.search();
		}
	}
	
	$scope.search = function(){		
			var jsonSearch = {};			

			if($scope.form.projects.selected){
				jsonSearch.projectCode = $scope.form.projects.selected.code;
			}			
			if($scope.form.samples.selected){
				jsonSearch.sampleCode = $scope.form.samples.selected.code;
			}			
			if($scope.form.typeCodes.selected){
				jsonSearch.typeCode = $scope.form.typeCodes.selected.code;
			}			
			$scope.datatable.search(jsonSearch);							
	}
}

SearchCtrl.$inject = ['$scope','$location','$routeParams', 'datatable','comboLists'];
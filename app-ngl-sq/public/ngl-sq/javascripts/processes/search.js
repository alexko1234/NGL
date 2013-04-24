"use strict"

function SearchCtrl($scope, datatable, comboLists) {
	
	$scope.datatableConfig = {	
			search:{
				url:jsRoutes.controllers.processes.api.Processes.list()
				
			},
			order:{
				mode:'local', //or 
				active:true,
				by:'containerInputCode'
			}
		};
	
	$scope.comboLists = comboLists;
	
	$scope.changeTypeCode = function(){
		if($scope.form.typeCodes.selected){
			this.search();
		}
	}
	
	$scope.changeProject = function(){
		if($scope.form.projects.selected){
			$scope.form.samples.options =  $scope.comboLists.getSamples($scope.form.projects.selected.code).query();			
		}else{
			$scope.form.samples.options = [];
		}	
		if($scope.form.typeCodes.selected){
			$scope.search();
		}
	}
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);		
		$scope.form = {
					typeCodes:{},
					projects:{},
					samples:{}
		};
		$scope.form.typeCodes.options = $scope.comboLists.getProcessTypes().query();
		$scope.form.projects.options = $scope.comboLists.getProjects().query();
	}
	
	$scope.search = function(){		
			var jsonSearch = {};			
			if($scope.form.typeCodes.selected){
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
}

SearchCtrl.$inject = ['$scope', 'datatable','comboLists'];
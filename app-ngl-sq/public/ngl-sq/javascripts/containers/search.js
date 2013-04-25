"use strict"

function SearchCtrl($scope, datatable, comboLists) {
	
	$scope.datatableConfig = {	
			search:{
				url:jsRoutes.controllers.containers.api.Containers.list()
			},
			order:{
				by:'code'
			}
		};
	
	$scope.comboLists = comboLists;
	
	$scope.changeProject = function(){
		if($scope.form.projects.selected){
			$scope.form.samples.options =  $scope.comboLists.getSamples($scope.form.projects.selected.code).query();			
		}else{
			$scope.form.samples.options = [];
		}	
		
		$scope.search();
	}
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);		
		$scope.form = {
					categoryCodes:{},
					projects:{},
					samples:{},
					stateCodes:{}
		};
		$scope.form.categoryCodes.options = $scope.comboLists.getContainerCategoryCodes().query();
		$scope.form.projects.options = $scope.comboLists.getProjects().query();
		$scope.form.stateCodes.options  = $scope.comboLists.getContainerStateCodes().query();
	}
	
	$scope.search = function(){		
			var jsonSearch = {};			

			if($scope.form.projects.selected){
				jsonSearch.projectCode = $scope.form.projects.selected.code;
			}			
			if($scope.form.samples.selected){
				jsonSearch.sampleCode = $scope.form.samples.selected.code;
			}			
			if($scope.form.categoryCodes.selected){
				jsonSearch.categoryCode = $scope.form.categoryCodes.selected.code;
			}	
			
			if($scope.form.stateCodes.selected){
				jsonSearch.stateCode = $scope.form.stateCodes.selected.code;
			}	
			$scope.datatable.search(jsonSearch);							
	}
}

SearchCtrl.$inject = ['$scope', 'datatable','comboLists'];
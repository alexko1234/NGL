"use strict"

function SearchCtrl($scope,$location,$routeParams, datatable, comboLists) {
	
	$scope.datatableConfig = {	
			search:{
				url:jsRoutes.controllers.experiments.api.Experiments.list()
				
			},
			order:{
				by:'code'
			},
			edit:{
				active:true
			}
		};
	
	$scope.comboLists = comboLists;
	
	$scope.changeTypeCode = function(){
		$scope.search();
	}
	
	$scope.init = function(){
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('experiment.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("search").url,remove:false});
			$scope.activeTab(0);
		}
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {
					typeCodes:{}
			};
			$scope.setForm($scope.form);
			$scope.form.typeCodes.options = $scope.comboLists.getExperimentTypes().query();
			
		}else{
			$scope.form = $scope.getForm();			
		}
		
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		if($scope.form.typeCodes.selected){
			$scope.search();
		}
	}
	
	$scope.search = function(){		
			var jsonSearch = {};			

			if($scope.form.typeCodes.selected){
				jsonSearch.typeCode = $scope.form.typeCodes.selected.code;
			}			
			
			$scope.datatable.search(jsonSearch);							
	}
}

SearchCtrl.$inject = ['$scope','$location','$routeParams', 'datatable','comboLists'];
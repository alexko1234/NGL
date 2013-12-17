"use strict"

function SearchCtrl($scope,$location,$routeParams, datatable, lists) {
	
	$scope.datatableConfig = {	
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:"/experiments/edit/"+line.code,remove:true});
				}
			},
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
	
	$scope.lists = lists;
	
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
			$scope.form = {};
			$scope.setForm($scope.form);
			//$scope.form.typeCodes.options = $scope.comboLists.getExperimentTypes().query();
			
			$scope.lists.refresh.types({objectTypeCode:"Experiment"});
			
		}else{
			$scope.form = $scope.getForm();			
		}
		
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		if($scope.form.type){
			$scope.search();
		}
	}
	
	$scope.search = function(){		
			var jsonSearch = {};			

			if($scope.form.type){
				jsonSearch.typeCode = $scope.form.type.code;
			}			
			
			$scope.datatable.search(jsonSearch);							
	}
}

SearchCtrl.$inject = ['$scope','$location','$routeParams', 'datatable','lists'];
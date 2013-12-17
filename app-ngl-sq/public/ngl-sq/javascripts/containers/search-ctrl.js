"use strict"

function SearchCtrl($scope, datatable, lists) {
	$scope.lists = lists;
	
	$scope.datatableConfig = {	
			search:{
				url:jsRoutes.controllers.containers.api.Containers.list()
			},
			order:{
				by:'code'
			}
		};
	
	$scope.changeProject = function(){
		if($scope.form.project){
			$scope.lists.refresh.samples({projectCode:$scope.form.project.code});
		}else{
			$scope.lists.clear("samples");
		}
		
		$scope.search();
	}
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);		
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('new');
			$scope.addTabs({label:Messages('containers.tabs.search'),href:jsRoutes.controllers.containers.tpl.Containers.home("new").url,remove:false});
			$scope.activeTab(0);
		}
		
		$scope.lists.refresh.projects();
		$scope.lists.refresh.containerCategories();
		$scope.lists.refresh.states({objectTypeCode:"Container"});
	}
	
	$scope.search = function(){		
			var jsonSearch = {};			

			if($scope.form.project){
				jsonSearch.projectCode = $scope.form.project.code;
			}			
			if($scope.form.sample){
				jsonSearch.sampleCode = $scope.form.sample.code;
			}			
			if($scope.form.containerCategory){
				jsonSearch.categoryCode = $scope.form.containerCategory.code;
			}	
			
			if($scope.form.stateCode){
				jsonSearch.stateCode = $scope.form.state.code;
			}	
			$scope.datatable.search(jsonSearch);							
	}
}

SearchCtrl.$inject = ['$scope', 'datatable','lists'];
"use strict"

function SearchCtrl($scope,$location,$routeParams, datatable, lists) {
	
	$scope.lists = lists;
	
	$scope.datatableConfig = {
			search:{
				url:jsRoutes.controllers.processes.api.Processes.list()
				
			},
			order:{
				by:'code'
			},
			save:{
				active:true,
				withoutEdit:false,
				url:jsRoutes.controllers.processes.api.Processes.save(),
				callback : function(datatable){
				}
			},
			edit:{
				active:true
			}
		};
	
	$scope.changeTypeCode = function(){
		if($scope.form.type){
			$location.path('/processes/search/'+$scope.form.type.code);
		}
	}
	
	$scope.changeProject = function(){
		if($scope.form.project){
			$scope.lists.refresh.samples({projectCode:$scope.form.project.code});
		}else{
			$scope.lists.clear("samples");
		}
		
		$scope.search();
	}
	
	$scope.init = function(){
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('new');
			$scope.addTabs({label:Messages('processes.tabs.search'),href:jsRoutes.controllers.processes.tpl.Processes.home("new").url,remove:false});
			$scope.activeTab(0);
		}
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {};
			$scope.setForm($scope.form);
			//$scope.form.typeCodes.options = $scope.comboLists.getProcessTypes().query();
			//$scope.form.projects.options = $scope.comboLists.getProjects().query();
			
			$scope.lists.refresh.projects();
			$scope.lists.refresh.types({objectTypeCode:"Process"});
			
			
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

			if($scope.form.project){
				jsonSearch.projectCode = $scope.form.project.code;			
			}			
			if($scope.form.sample){
				jsonSearch.sampleCode = $scope.form.sample.code;
			}			
			if($scope.form.type){
				jsonSearch.typeCode = $scope.form.type.code;
			}			
			$scope.datatable.search(jsonSearch);						
	}
}

SearchCtrl.$inject = ['$scope','$location','$routeParams', 'datatable','lists'];
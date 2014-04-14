"use strict";

function getColumns() {
var columns = [
			    {  	property:"code",
			    	header: "projects.code",
			    	type :"String",
			    	order:true,
			    	edit:false
				},
			    {  	property:"name",
			    	header: "projects.name",
			    	type :"String",
			    	order:false,
			    	edit:false
				},
				{	property:"typeCode",
					header: "projects.typeCode",
					type :"String",
			    	order:false,
			    	edit:false
				},
				{	property:"state.code",
					filter:"codes:'state'",					
					header: "projects.stateCode",
					type :"String",
					order:false,
					edit:false,
					choiceInList:false,
			    	listStyle:'bt-select',
			    	possibleValues:'listsTable.getStates()'	
				}
			];						
	return columns;
};

function convertForm(iform){
	var form = angular.copy(iform);
	return form
};


angular.module('home').controller('SearchCtrl', ['$scope', '$routeParams', 'datatable', function($scope, $routeParams, datatable) {
	
	var datatableConfig = {
			order :{by:'code', reverse:false},
			search:{
				url:jsRoutes.controllers.projects.api.Projects.list()
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.projects.tpl.Projects.get(line.code).url, remove:true});
				}
			},
			columns : getColumns()
	};
	
	
	
	var init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, datatableConfig);			
			$scope.datatable.search(convertForm($routeParams),'search');
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('projects.menu.search'), href:jsRoutes.controllers.projects.tpl.Projects.home("search").url, remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
	};
	
	init();
}]);





angular.module('home').controller('SearchFormCtrl', ['$scope', '$filter', 'lists', function($scope, $filter, lists){
	$scope.lists = lists;
	
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	var init = function(){
		$scope.lists.refresh.states({objectTypeCode:"Project"});
		$scope.lists.refresh.projectTypes();
		$scope.lists.refresh.projectCategories();
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
	};
	init();
	
}]);








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


angular.module('home').controller('SearchFormCtrl', ['$scope', 'lists', function($scope, lists) {
	
	$scope.lists = lists;
	
	$scope.search = function(){
		$scope.setForm($scope.form);		
		$scope.datatable.search(convertForm($scope.form));
	};
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	var init = function(){
		$scope.lists.refresh.projects();
		$scope.lists.refresh.types({objectTypeCode:"Project"});
		$scope.lists.refresh.states({objectTypeCode:"Project"});
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
	};
	init();
	
}]);


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





angular.module('home').controller('SearchAddCtrl', ['$scope', '$http', '$routeParams', 'messages', 'datatable', function($scope, $http, $routeParams, messages, datatable) {
	
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	
	var init = function(){
		
		$scope.messages = messages();
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
		
		$scope.startEditMode();

		$http.get(jsRoutes.controllers.projects.api.Projects.list().url).success(function(data) {

			var data2 = data[0];
			
			if (data2.comments == null || data2.comments.length == 0) {
				var comments = new Array();
				var comment = new Object();
				comment.comment = "";
				comments[0] = comment;
				data2.comments = comments;				
			}
			
			$scope.project = data2;
					
		
			if(angular.isUndefined($scope.getHomePage())){
				$scope.setHomePage('add');
			}
		});
	};
	
	init();
}]);




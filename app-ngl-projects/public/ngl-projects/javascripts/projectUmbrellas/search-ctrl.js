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
				}
			];						
	return columns;
};

function convertForm(iform){
	var form = angular.copy(iform);
	return form;
};


angular.module('home').controller('SearchCtrl', ['$scope', '$routeParams', 'datatable', function($scope, $routeParams, datatable) {
	
	var datatableConfig = {
			order :{by:'code', reverse:false},
			search:{
				url:jsRoutes.controllers.projectUmbrellas.api.ProjectUmbrellas.list()
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.projectUmbrellas.tpl.ProjectUmbrellas.get(line.code).url, remove:true});
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
			$scope.addTabs({label:Messages('projectUmbrellas.menu.search'), href:jsRoutes.controllers.projectUmbrellas.tpl.ProjectUmbrellas.home("search").url, remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
	};
	
	init();
}]);





angular.module('home').controller('SearchFormCtrl', ['$scope', 'lists', function($scope, lists){
	
	$scope.lists = lists;

	$scope.search = function(){
		$scope.setForm($scope.form);
		$scope.datatable.search(convertForm($scope.form));
	};
	
	$scope.reset = function() {
		$scope.form = {
				
		}
	};
	
	var init = function(){
		$scope.lists.refresh.projectUmbrellas();	
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
	};
	init();
	
}]);








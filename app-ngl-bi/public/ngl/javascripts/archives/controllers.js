"use strict";

function SearchCtrl($scope, datatable) {
	$scope.archive = 2; //default only need archive
	
	$scope.datatableConfig = {
			orderBy:'date',			
			url:{
				search:jsRoutes.controllers.archives.api.ReadSets.list()
			}
	};
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.search();
	}
	
	$scope.search = function(){
		$scope.datatable.search({archive:$scope.archive});
	}
	
};

SearchCtrl.$inject = ['$scope', 'datatable'];

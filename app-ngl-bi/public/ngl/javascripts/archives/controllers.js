"use strict";

function SearchCtrl($scope, datatable) {
	$scope.archive = 2; //default only need archive
	
	$scope.datatableConfig = {
			search : { 
				url:jsRoutes.controllers.archives.api.ReadSets.list()
			},
			pagination : {
				mode : 'local'
			},
			order : {
				mode : 'local',
				by:'date'
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

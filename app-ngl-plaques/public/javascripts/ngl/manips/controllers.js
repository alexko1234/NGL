"use strict";

function SearchCtrl($scope, $http, datatable) {

	$scope.datatableConfig = {
			order :{by:'matmanom'},
			search:{
				url:jsRoutes.controllers.manips.api.Manips.list()
			},
			show:{
				active:true
			}
	};

	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		
		$http.get('/tpl/projects/list').
		success(function(data, status, headers, config){
			$scope.projects = data;
		});
		
		$http.get('/tpl/etmanips/list').
		success(function(data, status, headers, config){
			$scope.etmanips = data;
		});

		
//		$scope.search();
	}
	
	$scope.search = function(){
		
		var jsonSearch = {};
		
		jsonSearch.emateriel = '2';
		
		if($scope.project != undefined){
			jsonSearch.project = $scope.project.code;
		}
		
		if($scope.etmanip != undefined){
			jsonSearch.etmanip = $scope.etmanip.code;
		}

		$scope.datatable.search(jsonSearch);
	}
		
};

SearchCtrl.$inject = ['$scope', '$http','datatable'];
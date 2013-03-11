"use strict";
 containerApp.controller('MainCtrl', function($scope, $http, datatable, baskets) {
	
	$scope.datatableConfig = {
			edit: false,
			orderReverse:false,
			orderBy:undefined,
			editColumn: {
				all:undefined,
				code:undefined,
				categoryCode:undefined,
				sampleCodes:undefined,
				fromExperimentTypeCodes:undefined,
				stateCode:undefined,
				support:{name:undefined}
				
			},
			updateColumn: {
				code:undefined,
				categoryCode:undefined,
				sampleCodes:undefined,
				fromExperimentTypeCodes:undefined,
				stateCode:undefined,
				support:{name:undefined}
			},
			hideColumn: {
				code:undefined,
				categoryCode:undefined,
				sampleCodes:undefined,
				fromExperimentTypeCodes:undefined,
				stateCode:undefined,
				support:{name:undefined}
			},
			orderColumn:{
				code:undefined,
				categoryCode:undefined,
				sampleCodes:undefined,
				fromExperimentTypeCodes:undefined,
				stateCode:undefined,
				support:{name:undefined}
			},
			url:{
				//save:"/admin/types?format=json",
				remove:"",
				search:'/api/containers'
			},
	};
	
	$scope.basketsConfig = {
		idBtnModal:"Add",
		titleModal:"Add to basket",
		textModal:"",
		modalId:"newBasketModal",
		textCancelModal:"Cancel",
		manualModal:false,
		url:"/baskets",
		urlList:"/basket/experimenttype"
	};
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.baskets = baskets($scope, $scope.basketsConfig, $scope.datatable);
		$http.get('/tpl/projects/list').
			success(function(data, status, headers, config){
				$scope.projects = data;
			});
		$http.get('/tpl/experimenttypes/list').
			success(function(data, status, headers, config){
				$scope.experiments = data;
			});
	}
	
	$scope.search = function(){
		$scope.datatable.search({project:$scope.project,experiment:$scope.experiment,state:$scope.state});
	}
});
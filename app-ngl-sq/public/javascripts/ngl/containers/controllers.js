"use strict";
 containerApp.controller('MainCtrl', function($scope, $http, datatable, baskets) {
 /*$scope.submitPost = function() {
     $http.post('/api/main', {
          post_title: "test",
          post_body: "var=1"
        }).success(function(data, status, headers, config) {
          if(data.success){
            $location.path('/');
          }else {
            //do something about the error
          }
        });
 }*/
	$scope.projects=[{"code":'1',"label":'P1'},{"code":'2',"label":'P2'},{"code":'3',"label":'P3'}];
	$scope.experiments=[{"code":'RECEP_BQ',"label":'RECEP_BQ'},{"code":'2',"label":'E2'},{"code":'3',"label":'E3'}];
	$scope.states=[{"code":'1',"label":'S1'},{"code":'2',"label":'S2'},{"code":'3',"label":'S3'}];
	
	$scope.datatableConfig = {
			edit: false,
			orderReverse:false,
			orderBy:undefined,
			editColumn: {
				all:undefined,
				code:undefined,
				name:undefined,
				collectionName:undefined
			},
			updateColumn: {
				code:undefined,
				name:undefined,
				collectionName:undefined
			},
			hideColumn: {
				id:undefined,
				code:undefined,
				name:undefined,
				objectType:{type:undefined},
				collectionName:undefined
			},
			orderColumn:{
				id:undefined,
				code:undefined,
				name:undefined,
				objectType:{type:undefined},
				collectionName:undefined
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
	
	/*$scope.init = function(){
		$scope.datatable = datatable($scope.datatableConfig);
		$scope.datatable.search = function() {
		$http.get('/api/containers').
			success(function(data, status, headers, config){
			$scope.datatable.searchresult = data.containers;
			$scope.datatable.searchresultMaster = angular.copy(data.containers);
			});
		}
	}*/
});
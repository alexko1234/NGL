"use strict";
function SearchCtrl($scope, $http,datatable,baskets) {
	 
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
			search:{
				//save:"/admin/types?format=json",
				url:'/api/containers'
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
	
	$scope.createProcessus = function(processusType){	
		for(var i = 0; i < $scope.datatable.displayResult.length; i++){
			if($scope.datatable.displayResult[i] && $scope.datatable.displayResult[i].selected){
				$http({
					method: 'POST',
					url: '/api/processus',
					data: {"type":processusType},
					headers: {'Content-Type': 'application/json'}
				}).success(function(data) {
					var process = data;
					var code = process.code;
					$scope.addContainer(code,JSON.stringify({ "container": $scope.datatable.displayResult[i].code}));
				});
			}
		}
	}
	
	$scope.detectSelect = function(){
		for(var i = 0; i < $scope.datatable.displayResult.length; i++){
				if($scope.datatable.displayResult[i] && $scope.datatable.displayResult[i].selected){
					return true;
				}
			}
		return false;
	}
	
	$scope.addContainer = function(code,container){
		$http({
			method: 'POST',
			url: '/api/processus/',
			data: container,
			headers: {'Content-Type': 'application/json'}
		}).success(function(data) {
			//Call post for adding container
		});
	}
	
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
			
		$http.get('/tpl/samples/list').
			success(function(data, status, headers, config){
				$scope.samples = data;
			});
		$http.get('/tpl/processType/list').
			success(function(data, status, headers, config){
				$scope.process = data;
			});
	}
	
	$scope.search = function(){
		if($scope.processus==undefined && ($scope.project!=undefined || $scope.experiment!=undefined || $scope.sample!=undefined)){ 
			$('#processus').addClass('error');
			$('#processusHelp').html("Error: this information is required  ");
		}else{
			alert($scope.project);
			$scope.datatable.search({projectCode:$scope.project,experimentCode:$scope.experiment,containerState:"N",containerSample:$scope.sample,containerProcess:$scope.processus.code});//State have to be IWP
			$('#processus').removeClass('error');
			$('#processusHelp').html("");
		}
	}
}
SearchCtrl.$inject = ['$scope','$http', 'datatable','baskets'];
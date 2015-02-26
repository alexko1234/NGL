angular.module('home').controller('ListNewCtrl', ['$scope', 'datatable','mainService','tabService','$q','$http','$filter',function ($scope, datatable,mainService,tabService,$q,$http,$filter) {
	
	$scope.loadView = false;
	$scope.supportView = true;
	$scope.supports = [];
	
	$scope.datatableConfig = {
			columns:[{
				"header":Messages("containerSupports.table.code"),
				"property":"code",
				"order":true,
				"type":"text"
			},
			{
				"header":Messages("containerSupports.table.projectCodes"),
				"property":"projectCodes",
				"order":true,
				"type":"text"
			},
			{
				"header":Messages("containerSupports.table.sampleCodes"),
				"property":"sampleCodes",
				"order":true,
				"type":"text",
				"render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>",
			}
			],
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local',
				active:true,
				by:'containerInputCode'
			},
			remove:{
				active:true,
				mode:'local',
				callback : function(datatable){
					/*if(!$scope.supportView)
					{
						$scope.setBasket($scope.containers);
					}else{
						$scope.loadView = true;
						var promises = $scope.loadContainersPromises();
						$q.all(promises).then(function (res) {
							$scope.setBasket($scope.containers);
							$scope.loadView = false;
						});
					}*/
					$scope.basket.basket = [];
					$scope.basket.basket = $scope.basket.basket.concat(datatable.allResult);
					$scope.setBasket($scope.basket);
				}
			},
			messages:{
				active:true
			},
			otherButtons:{
				active:true,
				template:'<button class="btn btn-info btn-configure" ng-disabled="basket.length()==0" ng-click="newExperiment();" data-toggle="tooltip" title="">'
						+'Configurer '+'{{form.nextExperimentTypeCode | codes:"type"}}</button>'/*+
						'<button ng-click="swithView()" ng-disabled="loadView"  class="btn-info btn-configure" ng-switch="supportView">'+Messages("baskets.switchView")+
						'<br><b ng-switch-when="true">'+
						Messages("backet.view.supports")+'</b>'+
						'<b ng-switch-when="false">'+Messages("backet.view.containers")+'</b></button>'*/
			}
	};
	
	$scope.newExperiment = function(){
		if(this.basket.length() > 0 && $scope.getTabs().length === 2){
			tabService.addTabs({label:$filter('codes')(mainService.getForm().nextExperimentTypeCode,'type')+" config",href:"/experiments/create/"+mainService.getForm().nextExperimentTypeCode,remove:false});
		}
	};
	
	$scope.swithView = function(){
		if($scope.supportView){
			$scope.supportView = false;
			$scope.swithToContainerView();
		}else{
			$scope.supportView = true;
			$scope.swithToSupportView()
		}
	};
	
	
	$scope.swithToContainerView = function(){
		$scope.containers = [];
		$scope.loadView = true;
		//the promises fill up $scope.containers
		var promises = $scope.loadContainersPromises();
		$q.all(promises).then(function (res) {
			$scope.datatable.setData($scope.containers, $scope.containers.length);
			//$scope.setBasket($scope.containers);
			$scope.loadView = false;
			$scope.datatable.config.remove.active = false;
		});
	};
	
	
	$scope.swithToSupportView = function(){
			$scope.datatable.setData($scope.supports, $scope.supports.length);
			$scope.datatable.config.remove.active = true;
	};
	
	
	$scope.loadContainersPromises = function(){
		var promises = [];
		angular.forEach($scope.supports,function(support){
			var promise = $http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{"supportCode":support.code}})
			.success(function(data, status, headers, config) {
				console.log(data);
				if(data != null){
					angular.forEach(data,function(d){
						$scope.containers.push(d);
					});
				}
			})
			.error(function(data, status, headers, config) {
				alert("error");
			});
			
			promises.push(promise);
		});
		
		return promises;
	};
	
	/*$scope.supportToContainers = function(){
		var promises = 	$scope.loadContainersPromises;
		$q.all(promises).then(function (res) {
			$scope.setBasket($scope.containers);
		});
	};*/
	
	//init
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.basket = $scope.getBasket();
	$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
	$scope.supports = $scope.basket.get();
	//$scope.supportToContainers();
	$scope.form = mainService.getForm();
}]);
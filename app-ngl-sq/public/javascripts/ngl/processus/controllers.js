"use strict";

function SearchCtrl($scope, $http, datatable,basket) {
	 
	$scope.datatableConfig = {
		pagination:{
			mode:'local'
		},		
		search:{
			url:'/api/containers'
		},
		otherButtons :{
			active:true
		}
	};
	
	$scope.basketConfig = {
		transform: function(container){
			var processus = {};
			processus.projectCode = container.projectCodes[0];
			processus.sampleCode = container.sampleCodes[0];
			processus.containerInputCode = container.code;
			processus.codeType = container.categoryCode;
			return processus;
		}
	};
	
	$scope.init = function(){
		if(angular.isUndefined($scope.getDatatable())){
			$scope.tabs.push({label:"Processus",href:jsRoutes.controllers.processus.tpl.Processus.home("list").url,remove:false});//$scope.tabs[1]
			$scope.datatable = datatable($scope, $scope.datatableConfig);			
			$scope.setDatatable($scope.datatable);	
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		
		if(angular.isUndefined($scope.getBasket())){
			$scope.basket = basket($scope,$scope.basketsConfig);			
			$scope.setBasket($scope.basket);
		}else{
			$scope.basket = $scope.getBasket();
		}
		
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {};
			
			$http.get('/tpl/processType/list').
				success(function(data, status, headers, config){
					$scope.form.processTypes = data;
			});
				
			
			$http.get('/tpl/projects/list').
				success(function(data, status, headers, config){
					$scope.form.projects = data;
				});
			
			$http.get('/tpl/samples/list').
				success(function(data, status, headers, config){
					$scope.form.samples = data;
				});
			
			
			$scope.setForm($scope.form);
		}else{
			$scope.form = $scope.getForm();
			$scope.project = $scope.form.projects.selected;
			$scope.sample = $scope.form.samples.selected;		
			$scope.processType = $scope.form.processTypes.selected;
		}
	}
	
	$scope.search = function(){
		if($scope.processType==undefined && ($scope.project!=undefined || $scope.sample!=undefined)){ 
			$('#processus').addClass('error');
			$('#processus').popover({content:"Information needed",trigger:"manual",placement:"bottom"}).popover('show');
			
		}else{
			$('#processus').removeClass('error');
			$('#processus').popover('destroy');
			
			var jsonSearch = {};
			
			jsonSearch.containerState = 'N';
			
			if($scope.project != undefined){
				jsonSearch.projectCode = $scope.project.code;
			}
			
			if($scope.sample != undefined){
				jsonSearch.containerSample = $scope.sample.code;
			}
			
			if($scope.processType != undefined){
				jsonSearch.containerProcess = $scope.processType.code;
			}
			
			$scope.datatable.search(jsonSearch);
		}
		
		//Saving the selected data
		$scope.form.processTypes.selected = $scope.processType;
		$scope.form.projects.selected = $scope.project;
		$scope.form.samples.selected = $scope.sample;
				
	}
}
SearchCtrl.$inject = ['$scope','$http', 'datatable','basket'];

function ListCtrl($scope, datatable) {
	
	$scope.datatableConfig = {
			pagination:{
				active:false
			},		
			search:{
				active:false
			}
		};
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.basket = $scope.getBasket();
		$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
	}
	
};
ListCtrl.$inject = ['$scope', 'datatable'];
"use strict";

function SearchContainerCtrl($scope, $http, datatable,basket) {
	 
	$scope.datatableConfig = {
		search:{
			url:'/api/containers'
		},
		order:{
			active:true,
			by:'code'
		},
		otherButtons :{
			active:true
		}
	};
		
	$scope.init = function(){
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);			
			$scope.setDatatable($scope.datatable);	
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		
		if(angular.isUndefined($scope.getBasket())){
			$scope.basket = basket($scope);			
			$scope.setBasket($scope.basket);
		}else{
			$scope.basket = $scope.getBasket();
		}
		
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {
					processTypes:{},
					projects:{},
					samples:{}
			};
			$scope.setForm($scope.form);
			
			$http.get(jsRoutes.controllers.lists.api.Lists.processTypes().url).
				success(function(data, status, headers, config){
					$scope.form.processTypes.options = data;
			});
				
			
			$http.get(jsRoutes.controllers.lists.api.Lists.projects().url).
				success(function(data, status, headers, config){
					$scope.form.projects.options = data;
				});
			
			
			
			
		}else{
			$scope.form = $scope.getForm();			
		}
	}
	
	$scope.changeProcessType = function(){
		if($scope.form.processTypes.selected){
			$scope.tabs[0] = {label:$scope.form.processTypes.selected.name,href:jsRoutes.controllers.processes.tpl.Processes.home("new/"+$scope.form.processTypes.selected.code).url,remove:false};
			this.search();
		}else{
			$scope.tabs.splice(0,1);
		}	
		$scope.basket.reset();
	}
	
	$scope.changeProject = function(){
		if($scope.form.projects.selected){
			$http.get(jsRoutes.controllers.lists.api.Lists.samples($scope.form.projects.selected.code).url).
			success(function(data, status, headers, config){
				$scope.form.samples.options = data;
			});		
		}else{
			$scope.form.samples.options = [];
		}	
		if($scope.form.processTypes.selected){
			$scope.search();
		}
	}
	
	
	$scope.search = function(){
		if($scope.form.processTypes.selected){ 		
			var jsonSearch = {};			
			jsonSearch.stateCode = 'N';			
			if($scope.form.projects.selected){
				jsonSearch.projectCode = $scope.form.projects.selected.code;
			}			
			if($scope.form.samples.selected){
				jsonSearch.sampleCode = $scope.form.samples.selected.code;
			}			
			if($scope.form.processTypes.selected){
				jsonSearch.processTypeCode = $scope.form.processTypes.selected.code;
			}			
			$scope.datatable.search(jsonSearch);
		}							
	}
	
	$scope.addToBasket = function(containers){
		for(var i = 0; i < containers.length; i++){
				var processus = {
						projectCode: containers[i].projectCodes[0],
						sampleCode: containers[i].sampleCodes[0],
						containerInputCode: containers[i].code
				};			
				this.basket.add(processus);
		}					
	}
}
SearchContainerCtrl.$inject = ['$scope','$http', 'datatable','basket'];

function ListNewCtrl($scope, datatable) {
	
	$scope.datatableConfig = {
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local', //or 
				active:true,
				by:'containerInputCode'
			},
			edit:{
				active:false
			},
			save:{
				active:true,
				withoutEdit:true,
			},
			remove:{
				active:true,
				mode:'local',
				callback : function(datatable){
					$scope.basket.reset();
					$scope.basket.add(datatable.allResult);
				}
			}
		};
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.basket = $scope.getBasket();
		$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);		
	}
	
};
ListNewCtrl.$inject = ['$scope', 'datatable'];
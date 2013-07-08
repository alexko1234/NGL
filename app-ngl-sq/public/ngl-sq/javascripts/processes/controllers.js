"use strict";

function SearchContainerCtrl($scope, datatable,basket, comboLists) {
	 
	$scope.datatableConfig = {
		search:{
			url:jsRoutes.controllers.containers.api.Containers.list()
		},
		order:{
			active:true,
			by:'code'
		},
		otherButtons :{
			active:true
		}
	};
		
	$scope.comboLists = comboLists;
	
	$scope.init = function(){
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);			
			$scope.setDatatable($scope.datatable);	
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('new');
			$scope.addTabs({label:Messages('processes.tabs.search'),href:jsRoutes.controllers.processes.tpl.Processes.home("new").url,remove:false});
			$scope.activeTab(0);
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
			$scope.form.processTypes.options = $scope.comboLists.getProcessTypes().query();
			$scope.form.projects.options = $scope.comboLists.getProjects().query();
			
		}else{
			$scope.form = $scope.getForm();			
		}
	}
	
	$scope.changeProcessType = function(){
		$scope.removeTab(1);
		$scope.basket.reset();
		this.search();
	}
	
	$scope.changeProject = function(){
		if($scope.form.projects.selected){
			$scope.form.samples.options =  $scope.comboLists.getSamples($scope.form.projects.selected.code).query();			
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
			jsonSearch.stateCode = "IWP";	//default state code for containers		
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
			for(var j = 0; j < containers[i].sampleCodes.length; j++){ //one process by sample
				var processus = {
						projectCode: containers[i].projectCodes[0],
						sampleCode: containers[i].sampleCodes[j],
						containerInputCode: containers[i].code,
						typeCode:$scope.form.processTypes.selected.code,
						properties:{}
				};			
				this.basket.add(processus);
			}
		}
		if($scope.form.processTypes.selected){
			$scope.addTabs({label:$scope.form.processTypes.selected.name,href:$scope.form.processTypes.selected.code,remove:false});
		}
	}
}
SearchContainerCtrl.$inject = ['$scope', 'datatable','basket','comboLists'];

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
				active:true
			},
			save:{
				active:true,
				withoutEdit:false,
				url:jsRoutes.controllers.processes.api.Processes.save(),
				callback : function(datatable){
					$scope.basket.reset();
				}
			},
			remove:{
				active:true,
				mode:'local',
				callback : function(datatable){
					$scope.basket.reset();
					$scope.basket.add(datatable.allResult);
				}
			},
			messages:{
				active:true
			}
		};
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.basket = $scope.getBasket();
		$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);		
		$scope.datatable.selectAll(true);
		$scope.datatable.setEditColumn();
	}
};
ListNewCtrl.$inject = ['$scope', 'datatable'];
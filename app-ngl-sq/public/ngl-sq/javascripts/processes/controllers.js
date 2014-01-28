"use strict";

function SearchContainerCtrl($scope, datatable,basket, lists) {
	 
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
		
	$scope.lists = lists;
	
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
			$scope.form = {};
			$scope.setForm($scope.form);
			
			//$scope.form.processTypes.options = $scope.comboLists.getProcessTypes().query();
			//$scope.form.projects.options = $scope.comboLists.getProjects().query();
			
			$scope.lists.refresh.projects();
			$scope.lists.refresh.types({objectTypeCode:"Process"});
			
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
		if($scope.form.project){
				$scope.lists.refresh.samples({projectCode:$scope.form.project.code});
			}else{
				$scope.lists.clear("samples");
			}
		
		if($scope.form.type){
			$scope.search();
		}
	}
	
	
	$scope.search = function(){
		if($scope.form.type){ 		
			var jsonSearch = {};			
			jsonSearch.stateCode = "IW-P";	//default state code for containers		
			if($scope.form.project){
				jsonSearch.projectCode = $scope.form.project.code;
			}			
			if($scope.form.sample){
				jsonSearch.sampleCode = $scope.form.sample.code;
			}			
			if($scope.form.type){
				jsonSearch.processTypeCode = $scope.form.type.code;
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
						support: containers[i].support,
						typeCode:$scope.form.type.code,
						properties:{}
				};			
				this.basket.add(processus);
			}
		}
		if($scope.form.type){
			$scope.addTabs({label:$scope.form.type.name,href:$scope.form.type.code,remove:false});
		}
	}
}
SearchContainerCtrl.$inject = ['$scope', 'datatable','basket','lists'];

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
				active:true,
				columnMode:true
			},
			save:{
				active:true,
				withoutEdit:false,
				url:jsRoutes.controllers.processes.api.Processes.save(),
				callback : function(datatable){
					$scope.basket.reset();
				},
				value:function(line){var val=line; val.support=undefined; return val;}
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
"use strict";

function SearchContainerCtrl($scope, datatable,basket, lists,$filter) {
	 
	$scope.datatableConfig = {
			columns:[
						{
							"header":Messages("containers.table.code"),
							"property":"code",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("experiments.table.projectCodes"),
							"property":"projectCodes",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("containers.table.sampleCodes"),
							"property":"sampleCodes",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("containers.table.valid"),
							"property":"valid",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("containers.table.stateCode"),
							"property":"stateCode",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("containers.table.categoryCode"),
							"property":"categoryCode",
							"order":true,
							"hide":true,
							"type":"text"
						},
						{
							"header":Messages("containers.table.fromExperimentTypeCodes"),
							"property":"typeCode",
							"order":true,
							"hide":true,
							"type":"text"
						}
						],
		search:{
			url:jsRoutes.controllers.containers.api.Containers.list()
		},
		order:{
			active:true,
			by:'code'
		},
		otherButtons :{
			active:true,
			template:'<button class="btn" ng-disabled="!datatable.isSelect()" ng-click="addToBasket(datatable.getSelection(true))" data-toggle="tooltip" title="Messages("button.addbasket")"><i class="icon-shopping-cart icon-large"></i> Messages("button.addbasket") ({{basket.length()}})</button>'
		}
	};
		
	
	var search = function(values, query){
		var queryElts = query.split(',');
		
		var lastQueryElt = queryElts.pop();
		
		var output = [];
		angular.forEach($filter('filter')(values, lastQueryElt), function(value, key){
			if(queryElts.length > 0){
				this.push(queryElts.join(',')+','+value.code);
			}else{
				this.push(value.code);
			}
		}, output);
		
		return output;
	}
	
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
			$scope.lists.refresh.processCategories();
			
		}else{
			$scope.form = $scope.getForm();			
		}
	}
	
	$scope.changeProcessCategory = function(){
		/*$scope.removeTab(1);
		
		$scope.basket.reset();*/
		
		$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
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
	
	
	$scope.searchProjects = function(query){
		return search(lists.getProjects(), query);
	}
	

	$scope.reset = function(){
		$scope.form = {
				
		}
	}
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCodes){
			lists.refresh.samples({projectCodes:$scope.form.projectCodes.split(',')});
		}
	}
	
	$scope.searchSamples = function(query){
		return search(lists.getSamples(), query);
	}
	
	$scope.search = function(){
		if($scope.form.type){ 		
			var jsonSearch = {};			
			jsonSearch.stateCode = "IW-P";	//default state code for containers		
			if($scope.form.projectCodes){
				jsonSearch.projectCodes = $scope.form.projectCodes.split(",");
			}			
			if($scope.form.sampleCodes){
				jsonSearch.sampleCodes = $scope.form.sampleCodes.split(",");
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
SearchContainerCtrl.$inject = ['$scope', 'datatable','basket','lists','$filter'];

function ListNewCtrl($scope, datatable) {
	
	$scope.datatableConfig = {
			columnsUrl:jsRoutes.controllers.processes.tpl.Processes.newProcessesColumns($scope.getForm().type.code).url,
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
		$scope.form = $scope.getForm();
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.basket = $scope.getBasket();
		$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);		
		$scope.datatable.selectAll(true);
		//$scope.datatable.setEditColumn();
		
	}
};
ListNewCtrl.$inject = ['$scope', 'datatable'];
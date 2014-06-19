"use strict";


angular.module('home').controller('SearchContainerCtrl', ['$scope', 'datatable','basket','lists','$filter', function($scope, datatable,basket, lists,$filter) {
	$scope.lists = lists; 
	
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
				"header":Messages("containers.table.supportCode"),
				"property":"support.code",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.support.categoryCode"),
				"property":"support.categoryCode",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.fromExperimentTypeCodes"),
				"property":"fromExperimentTypeCodes",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.support.position"),
				"property":"support.position",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.valid"),
				"property":"valuation.valid",
				"order":true,
				"type":"text",
				"filter":"codes:'valuation'"
			},
			{
				"header":Messages("processes.table.projectCodes"),
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
				"header":Messages("containers.table.state.code"),
				"property":"state.code",
				"order":true,
				"hide":true,
				"type":"text",
				"filter":"codes:'state'"
			},
			{
				"header":Messages("containers.table.categoryCode"),
				"property":"categoryCode",
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
			template:'<button class="btn" ng-disabled="!datatable.isSelect()" ng-click="addToBasket(datatable.getSelection(true))" data-toggle="tooltip" title="Messages("button.addbasket")">'
					+'<i class="fa fa-shopping-cart fa-lg"></i> ({{basket.length()}})</button>'
		}
	};
	
	
	$scope.changeProcessCategory = function(){
		if($scope.form.processCategory){
			$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
		}else{
			$scope.lists.clear("processTypes");
		}
	};
	
	$scope.changeProcessType = function(){
		$scope.removeTab(1);
		$scope.basket.reset();
		this.search();
	};
	
	$scope.changeProject = function(){
		if($scope.form.project){
				$scope.lists.refresh.samples({projectCode:$scope.form.project.code});
			}else{
				$scope.lists.clear("samples");
			}
		
		if($scope.form.type){
			$scope.search();
		}
	};
	
	$scope.reset = function(){
		$scope.form = {};
	};
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCodes && $scope.form.projectCodes.length>0){
			lists.refresh.samples({projectCodes:$scope.form.projectCodes});
		}
	};
	
	$scope.search = function(){	
		if($scope.form.projectCodes || $scope.form.sampleCodes || $scope.form.processType || $scope.form.containerSupportCode 
				|| $scope.form.fromExperimentTypeCodes || $scope.form.containerSupportCategory){
			var jsonSearch = {};
			jsonSearch.stateCode = 'IW-P';
			if($scope.form.projectCodes){
				jsonSearch.projectCodes = $scope.form.projectCodes;
			}			
			
			if($scope.form.sampleCodes){
				jsonSearch.sampleCodes = $scope.form.sampleCodes;
			}				
			
			if($scope.form.containerSupportCategory){
				jsonSearch.containerSupportCategory = $scope.form.containerSupportCategory;
			}	
			
			if($scope.form.fromExperimentTypeCodes){
				jsonSearch.fromExperimentTypeCodes = $scope.form.fromExperimentTypeCodes;
			}
			
			if($scope.form.processType){
				jsonSearch.processTypeCode = $scope.form.processType.code;
			}
			
			if($scope.form.containerSupportCode){
				jsonSearch.supportCode = $scope.form.containerSupportCode;
			}
			
			$scope.datatable.search(jsonSearch);						
		}
	};
	
	$scope.addToBasket = function(containers){
		$scope.errors.processType = {};
		$scope.errors.processCategory = {};
		if($scope.form.processType){
			for(var i = 0; i < containers.length; i++){
				for(var j = 0; j < containers[i].sampleCodes.length; j++){ //one process by sample
					var processus = {
							projectCode: containers[i].projectCodes[0],
							sampleCode: containers[i].sampleCodes[j],
							containerInputCode: containers[i].code,
							support: containers[i].support,
							typeCode:$scope.form.processType.code,
							categoryCode:$scope.form.processCategory.code,
							properties:{}
					};			
					this.basket.add(processus);
				}
			}
			$scope.addTabs({label:$scope.form.processType.name,href:$scope.form.processType.code,remove:false});
		}else{
			if(!$scope.form.processCategory){
				$scope.errors.processCategory = "alert-danger";
			}
			
			$scope.errors.processType = "alert-danger";
		}
	};
	
	//init
	$scope.errors = {};
	if(angular.isUndefined($scope.getDatatable())){
		$scope.datatable = datatable($scope, $scope.datatableConfig);			
		$scope.setDatatable($scope.datatable);	
	}else{
		$scope.datatable = $scope.getDatatable();
	}
	
	if(angular.isUndefined($scope.getHomePage())){
		$scope.setHomePage('new');
		$scope.addTabs({label:Messages('processes.tabs.create'),href:jsRoutes.controllers.processes.tpl.Processes.home("new").url,remove:false});
		$scope.activeTab(0);
	}
	
	if(angular.isUndefined($scope.getBasket())){
		$scope.basket = basket();			
		$scope.setBasket($scope.basket);
	}else{
		$scope.basket = $scope.getBasket();
	}
	
	
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		$scope.setForm($scope.form);
		$scope.lists.refresh.projects();
		$scope.lists.refresh.processCategories();
		$scope.lists.refresh.supports();
		$scope.lists.refresh.containerSupportCategories();
		$scope.lists.refresh.experimentTypes();
		
	}else{
		$scope.form = $scope.getForm();			
	}
}]);

angular.module('home').controller('ListNewCtrl', ['$scope', 'datatable', function($scope, datatable) {

	$scope.datatableConfig = {
			columnsUrl:jsRoutes.controllers.processes.tpl.Processes.newProcessesColumns($scope.getForm().processType.code).url,
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
	
	//init
	$scope.form = $scope.getForm();
	$scope.datatable = datatable($scope, $scope.datatableConfig);
	$scope.basket = $scope.getBasket();
	$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);		
	$scope.datatable.selectAll(true);
}]);
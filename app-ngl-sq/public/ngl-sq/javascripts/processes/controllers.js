"use strict";


angular.module('home').controller('SearchContainerCtrl', ['$scope', 'datatable','basket','lists','$filter','mainService','tabService', function($scope, datatable,basket, lists,$filter,mainService, tabService) {
	$scope.lists = lists; 
	
	$scope.datatableConfig = {
		columns:[
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
				"header":Messages("containers.table.support.column"),
				"property":"support.column",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.support.line"),
				"property":"support.line",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.code"),
				"property":"code",
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
			template:'<button class="btn" ng-disabled="!datatable.isSelect()" ng-click="addToBasket(datatable.getSelection(true))" data-toggle="tooltip" title="'+Messages("button.addbasket")+'">'
					+'<i class="fa fa-shopping-cart fa-lg"></i> ({{basket.length()}})'
		}
	};
	
	
	$scope.changeProcessCategory = function(){
		if($scope.form.processCategory){
			$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory});
		}else{
			$scope.lists.clear("processTypes");
		}
	};
	
	$scope.changeProcessType = function(){
		$scope.removeTab(1);
		$scope.basket.reset();
		this.search();
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
				|| $scope.form.fromExperimentTypeCodes || $scope.form.containerSupportCategory  || $scope.form.valuations){
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
			
			if($scope.form.valuations){
				jsonSearch.valuations = $scope.form.valuations;
			}
			
			if($scope.form.processType){
				jsonSearch.processTypeCode = $scope.form.processType;
			}
			
			if($scope.form.containerSupportCode){
				jsonSearch.supportCode = $scope.form.containerSupportCode;
			}
			
			$scope.datatable.search(jsonSearch);
			mainService.setForm($scope.form);
		}
	};
	
	$scope.addToBasket = function(containers){
		$scope.errors.processType = {};
		$scope.errors.processCategory = {};
		if($scope.form.processType){
			for(var i=0;i<containers.length;i++){
				this.basket.add(containers[i]);
			}
			tabService.addTabs({label:$scope.form.processType,href:$scope.form.processType,remove:false});
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
		$scope.datatable = datatable($scope.datatableConfig);			
		mainService.setDatatable($scope.datatable);	
	}else{
		$scope.datatable = mainService.getDatatable();
	}
	
	if(angular.isUndefined($scope.getHomePage())){
		mainService.setHomePage('new');
		tabService.addTabs({label:Messages('processes.tabs.create'),href:jsRoutes.controllers.processes.tpl.Processes.home("new").url,remove:false});
		tabService.activeTab(0);
	}
	
	if(angular.isUndefined($scope.getBasket())){
		$scope.basket = basket();			
		mainService.setBasket($scope.basket);
	}else{
		$scope.basket = mainService.getBasket();
	}
	
	
	if(angular.isUndefined(mainService.getForm())){
		$scope.form = {};
		mainService.setForm($scope.form);
		$scope.lists.refresh.projects();
		$scope.lists.refresh.processCategories();
		$scope.lists.refresh.supports();
		$scope.lists.refresh.containerSupportCategories();
		$scope.lists.refresh.experimentTypes({"categoryCode":"transformation"});
		
	}else{
		$scope.form = mainService.getForm();			
	}
}]);

angular.module('home').controller('ListNewCtrl', ['$scope', 'datatable','$http','mainService','$q', function($scope, datatable,$http,mainService,$q) {

	$scope.supportView = false;
	$scope.containers = [];
	
	$scope.datatableConfig = {
			columnsUrl:jsRoutes.controllers.processes.tpl.Processes.newProcessesColumns(mainService.getForm().processType).url,
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
				withoutEdit:true,
				showButton : false,
				url:function(value){ return jsRoutes.controllers.processes.api.Processes.saveSupport(value.support.code).url;
					},
				callback : function(datatable){
					$scope.basket.reset();
					$scope.getColumns();
				},
				value:function(line){
						var val=line;
						val.support=undefined; 
						return val;
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
			},
			otherButtons :{
				active:true,
				template:'<button  class="btn" ng-click="save()"><i class="fa fa-save"></i></button><button ng-click="swithView()" ng-disabled="loadView"  class="btn btn-info" ng-switch="supportView">'+Messages("baskets.switchView")+
							'<br><b ng-switch-when="true">'+
							Messages("backet.view.supports")+'</b>'+
							'<b ng-switch-when="false">'+Messages("backet.view.containers")+'</b></button></button>'
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
		$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
	};
	
	$scope.save = function (){
		console.log($scope.datatable.displayResult);
		var data = $scope.datatable.displayResult;
		var url = "";
		if($scope.supportView){
			url = jsRoutes.controllers.processes.api.Processes.saveSupport(value.support.code).url;
		}else{
			url =  jsRoutes.controllers.processes.api.Processes.save().url;
		}
		if(!$scope.supportView){
			$scope.processes = [];
			$scope.promises = [];
			for(var i=0;i<data.length;i++){
				console.log(data[i]);
				for(var j = 0; j < data[i].data.sampleCodes.length; j++){ //one process by sample
					var processData = data[i].data;
					processData.properties.limsCode = undefined;
					processData.properties.receptionDate = undefined;
					var process = {
							projectCode: processData.projectCodes[0],
							sampleCode: processData.sampleCodes[j],
							containerInputCode: processData.code,
							typeCode:$scope.form.processType,
							categoryCode:$scope.form.processCategory,
							properties:processData.properties
					};		
					console.log(process);
					var promise = $http.post(url, process)
					.success(function(data, status, headers, config) {
						if(data!=null){
							$scope.message.clazz="alert alert-success";
							$scope.message.text=Messages('experiments.msg.save.sucess');
							
							$scope.processes.push(data);
						}
					})
					.error(function(data, status, headers, config) {
						$scope.message.clazz = "alert alert-danger";
						$scope.message.text = Messages('experiments.msg.save.error');
		
						$scope.message.details = data;
						$scope.message.isDetails = true;
						alert("error");
					});
					$scope.promises.push(promise);
				}
			}
			$q.all($scope.promises).then(function (res) {
				$scope.basket.reset();
				$scope.getColumns();
				$scope.datatable.setData($scope.processes);
			});
		}else{
			$scope.datatable.save();
		}
	};
	
	$scope.swithToSupportView = function(){
		var processes =  mainService.getBasket().basket;
		$scope.processesSupports = [];
		angular.forEach(processes,function(process){
			var alreadyExist = false;
			angular.forEach($scope.processesSupports,function(processesSupport){
				if(processesSupport.support.code == process.support.code){
					alreadyExist = true;
				}
			});
			if(!alreadyExist){
				//processesSupports.push({"process":process, "supportCode":process.support.code});
				$scope.processesSupports.push(process);
			}
		});
		$scope.datatable.setData($scope.processesSupports,$scope.processesSupports.length);
	};
	
	$scope.getColumns = function(){
		var typeCode = "";
		if($scope.form.processType){
			typeCode = $scope.form.processType;
		}
		
		$http.get(jsRoutes.controllers.processes.tpl.Processes.searchColumns().url,{params:{"typeCode":typeCode}})
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.datatable.setColumnsConfig(data);
			}
		})
		.error(function(data, status, headers, config) {
		
		});
	};
	
	
	//init
	$scope.form = mainService.getForm();
	$scope.message = {};
	$scope.datatable = datatable($scope, $scope.datatableConfig);
	$scope.basket = mainService.getBasket();
	$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);		
	$scope.datatable.selectAll(true);
}]);
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
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"state.code",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text",
			        	 "filter":"codes:'state'"
			         },
			         {
			        	 "header":Messages("containers.table.sampleCode"),
			        	 "property":"sampleCodes",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.projectCode"),
			        	 "property":"projectCodes",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.newContainerSupportCodes"),
			        	 "property":"newContainerSupportCodes",
			        	 "hide":true,
			        	 "type":"text",
			        	 "render":"<div list-resize='value.data.newContainerSupportCodes | unique' below-only-deploy>"
			         },
			         {
			        	 "header":Messages("processes.table.experimentCodes"),
			        	 "property":"experimentCodes",
			        	 "hide":true,
			        	 "type":"text",
			        	 "render":"<div list-resize='value.data.experimentCodes | unique' below-only-deploy>"
			         },
			         {
			        	 "header":Messages("containers.table.valid"),
			        	 "property":"valuation.valid",
			        	 "order":true,
			        	 "type":"text",
			        	 "filter":"codes:'valuation'"
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
		$scope.form.nextProcessTypeCode = undefined;
		$scope.lists.clear("processTypes");

		if($scope.form.processCategory !== undefined && $scope.form.processCategory !== null){
			$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory});
		}
	};

	$scope.changeProcessType = function(){
		$scope.removeTab(1);
		$scope.basket.reset();
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
		$scope.errors.processCategory = {};
		$scope.errors.processType = {};
		//if($scope.form.projectCodes || $scope.form.sampleCodes || $scope.form.processType || $scope.form.containerSupportCode || $scope.form.fromExperimentTypeCodes || $scope.form.containerSupportCategory  || $scope.form.valuations){
		if($scope.form.processCategory && $scope.form.nextProcessTypeCode){
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

			if($scope.form.nextProcessTypeCode){
				jsonSearch.nextProcessTypeCode = $scope.form.nextProcessTypeCode;
			}

			if($scope.form.containerSupportCode){
				jsonSearch.supportCode = $scope.form.containerSupportCode;
			}

			$scope.datatable.search(jsonSearch);
			mainService.setForm($scope.form);
		}else{
			if($scope.form.processCategory === null || $scope.form.processCategory === undefined || $scope.form.processCategory === "" ){
				$scope.errors.processCategory = "has-error";
				$scope.errors.processType = "has-error";
				$scope.form.nextProcessTypeCode = undefined;
			}
			if($scope.form.nextProcessTypeCode === null || $scope.form.nextProcessTypeCode === undefined || $scope.form.nextProcessTypeCode === "" ){
				$scope.errors.processType = "has-error";
			}
			$scope.datatable.setData({},0);
			$scope.basket.reset();

		}
	};

	$scope.addToBasket = function(containers){
		$scope.errors.processType = {};
		$scope.errors.processCategory = {};
		if($scope.form.nextProcessTypeCode){
			for(var i = 0; i < containers.length; i++){
				var alreadyOnBasket = false;
				for(var j=0;j<this.basket.get().length;j++){
					if(this.basket.get()[j].code === containers[i].code){
						alreadyOnBasket = true;
					}
				}
				if(!alreadyOnBasket){
					this.basket.add(containers[i]);
				}
			}
			tabService.addTabs({label:$filter('codes')($scope.form.nextProcessTypeCode,"type"),href:$scope.form.nextProcessTypeCode,remove:false});
		}else{
			if(!$scope.form.processCategory){
				$scope.errors.processCategory = "has-error";
			}

			$scope.errors.processType = "has-error";
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
		lists.refresh.experimentTypes({categoryCode:"transformation", withoutOneToVoid:true});

	}else{
		$scope.form = mainService.getForm();			
	}
}]);

angular.module('home').controller('ListNewCtrl', ['$scope', 'datatable','$http','mainService','$q', function($scope, datatable,$http,mainService,$q) {

	$scope.datatableConfig = {
			columns:[
			         {
			        	 "header":Messages("processes.table.supportCode"),
			        	 "property":"support.code",
			        	 "position":1,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.line"),
			        	 "property":"support.line",
			        	 "position":2,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.columns"),
			        	 "property":"support.column",
			        	 "position":3,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.projectCode"),
			        	 "property":"projectCode",
			        	 "position":4,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.sampleCode"),
			        	 "property":"sampleCode",
			        	 "position":5,
			        	 "type":"text"
			         },
			         {
			        	 "header": function(){
			        		 if($scope.supportView){
			        			 return Messages("containers.table.stateCode");
			        		 }else{
			        			 return  Messages("containers.table.state.code");	
			        		 }
			        	 },
			        	 "property":"state.code",
			        	 "position":6,
			        	 "filter": "codes:'state'",
			        	 "type":"text"
			         }			         
			         ],
			         //columnsUrl:jsRoutes.controllers.processes.tpl.Processes.newProcessesColumns(mainService.getForm().nextProcessTypeCode).url,
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
			        	 columnMode:true
			         },
			         save:{
			        	 active:function(){
			        		 $scope.recorded();
			        	 },
			        	 withoutEdit:true,
			        	 showButton : false,
			        	 mode:"local",
			        	 changeClass : false
			         },
			         remove:{			        	
			        	 mode:'local',
			        	 callback : function(datatable){
			        		 $scope.basket.reset();
			        		 $scope.basket.add(datatable.allResult);
			        	 }
			         },
			         messages:{
			        	 active:false
			         },
			         otherButtons :{
			        	 active:true,
			        	 template:'<button ng-if="doneAndRecorded==false" class="btn" ng-click="save()"><i class="fa fa-save"></i></button><button ng-if="doneAndRecorded==false" ng-click="swithView()" ng-disabled="loadView"  class="btn btn-info" ng-switch="supportView">'+Messages("baskets.switchView")+
			        	 ' '+'<b ng-switch-when="true" class="switchLabel">'+
			        	 Messages("baskets.switchView.containers")+'</b>'+
			        	 '<b ng-switch-when="false" class="switchLabel">'+Messages("baskets.switchView.supports")+'</b></button></button>'
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
		if($scope.basket.length() != 0){
			$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
		}else{
			$scope.datatable.setData($scope.processes);
		}


		$scope.datatable.config.columns[0].header = "containers.table.code";
	};

	$scope.save = function (){
		$scope.message.details = undefined;
		$scope.message.isDetails = false;
		$scope.datatable.save();
		console.log($scope.datatable.getData());
		var data = $scope.datatable.getData();
		var url = "";
		$scope.processes = [];
		$scope.promises = [];
		if(!$scope.supportView){
			url =  jsRoutes.controllers.processes.api.Processes.save().url;
			$scope.datatable.config.spinner.start = true;
			for(var i=0;i<data.length;i++){
				for(var j = 0; j < data[i].sampleCodes.length; j++){ //one process by sample
					var processData = data[i];
					processData.properties.limsCode = undefined;
					processData.properties.receptionDate = undefined;
					var process = {
							projectCode: processData.projectCodes[0],
							sampleCode: processData.sampleCodes[j],
							containerInputCode: processData.code,
							typeCode:$scope.form.nextProcessTypeCode,
							categoryCode:$scope.form.processCategory,
							properties:processData.properties
					};

					var promise = $http.post(url, process, {params:{"fromContainerInputCode": processData.code}})
					.success(function(data, status, headers, config) {
						if(data!=null){
							$scope.message.clazz="alert alert-success";
							$scope.message.text=Messages('experiments.msg.save.sucess');

							//$scope.processes.push(data);
							$scope.processes = $scope.processes.concat(data);
						}
					})
					.error(function(data, status, headers, config) {
						$scope.datatable.config.spinner.start = false;
						$scope.message.clazz = 'alert alert-danger';
						$scope.message.text = Messages('experiments.msg.save.error');

						$scope.message.details = data;
						$scope.message.isDetails = true;
					});
					$scope.promises.push(promise);
				}
			}
		}else{
			$scope.datatable.config.spinner.start = true;
			for(var i=0;i<data.length;i++){
				url =  jsRoutes.controllers.processes.api.Processes.save().url;
				var processData = data[i];
				processData.properties.limsCode = undefined;
				processData.properties.receptionDate = undefined;
				var process = {
						projectCode: processData.projectCodes[0],
						typeCode:$scope.form.nextProcessTypeCode,
						categoryCode:$scope.form.processCategory,
						properties:processData.properties
				};
				var promise = $http.post(url,process, {params:{"fromSupportContainerCode": data[i].support.code}})
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess');

						$scope.processes = $scope.processes.concat(data);
						$scope.doneAndRecorded = true;
						$scope.datatable.config.edit.active = false;
						$scope.datatable.config.remove.active = false;
					}
				})
				.error(function(data, status, headers, config) {
					$scope.datatable.config.spinner.start = false;
					$scope.message.clazz = 'alert alert-danger';
					$scope.message.text = Messages('experiments.msg.save.error');

					$scope.message.details = data;
					$scope.message.isDetails = true;
					$scope.doneAndRecorded = false;
					$scope.datatable.config.edit.active = true;
					$scope.datatable.config.remove.active = true;
				});
				$scope.promises.push(promise);
			}
		}


		$q.all($scope.promises).then(function (res) {
			$scope.basket.reset();
			$scope.getColumns();
			$scope.datatable.setData($scope.processes);
			$scope.datatable.config.spinner.start = false;
		});	
	};

	$scope.swithToSupportView = function(){

		if($scope.basket.length() == 0){
			$scope.datatable.setData($scope.processes);
		}else{
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
					$scope.processesSupports.push(process);
				}
			});
			$scope.datatable.setData($scope.processesSupports,$scope.processesSupports.length);
		}


		console.log($scope.datatable.config);
		if($scope.datatable.config.columns.length>0)
			$scope.datatable.config.columns[0].header = "containers.table.supportCode";
	};

	$scope.getColumns = function(){
		var typeCode = "";
		if($scope.form.nextProcessTypeCode){
			typeCode = $scope.form.nextProcessTypeCode;
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

	$scope.getPropertyColumnType = function(type){
		if(type === "java.lang.String"){
			return "text";
		}else if(type === "java.lang.Double"){
			return "number";
		}else if(type === "java.util.Date"){
			return "date";
		}
		
		return type;
	};
	
	$scope.addNewProcessColumns = function(){		
		var typeCode = "";		
		var column = "";
		if($scope.form.nextProcessTypeCode){
			typeCode = $scope.form.nextProcessTypeCode;
		}

		$http.get(jsRoutes.controllers.processes.tpl.Processes.getPropertiesDefinitions(typeCode).url)
		.success(function(data, status, headers, config) {
			if(data!=null){				
				console.log(data);
				angular.forEach(data, function(property){					

					var unit = "";
					if(angular.isDefined(property.displayMeasureValue)){
						unit = "("+property.displayMeasureValue+")";
					}				
						
					column = $scope.datatable.newColumn(property.name, "properties."+property.code+".value",property.editable,false,true, $scope.getPropertyColumnType(property.valueType),property.choiceInList, property.possibleValues,{});
					
					column.listStyle = "bt-select";
					column.defaultValues = property.defaultValue;
					if(property.displayMeasureValue != undefined && property.displayMeasureValue != null){
						column.convertValue = {"active":true, "displayMeasureValue":property.displayMeasureValue.value, "saveMeasureValue":property.saveMeasureValue.value};
					}

					$scope.datatable.addColumn(7+property.displayOrder,column);	
				});				
			}

		})
		.error(function(data, status, headers, config) {
			console.log(data);
		});		

	};	
	
	$scope.recorded = function(){
		 if($scope.doneAndRecorded==false){
			 return true;
		 }else{
			 return false;
		 }
	 };



	//init
	$scope.form = mainService.getForm();
	$scope.message = {};
	$scope.supportView = false;
	$scope.containers = [];
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.basket = mainService.getBasket();
	$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
	$scope.addNewProcessColumns();
	$scope.datatable.selectAll(false);
	if($scope.basket.length() != 0){
		$scope.doneAndRecorded = false;
		$scope.datatable.config.edit.active = true;
		$scope.datatable.config.remove.active = true;
	}else{
		$scope.doneAndRecorded = true;
		$scope.datatable.config.edit.active = false;
		$scope.datatable.config.remove.active = false;
	}
	$scope.swithView();
}]);
"use strict";


angular.module('home').controller('SearchContainerCtrl', ['$scope', 'datatable','basket','lists','$filter','$http','mainService','tabService','$parse', function($scope, datatable,basket, lists,$filter,$http,mainService, tabService, $parse) {
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
			 			"header":Messages("containers.table.sampleCodes.length"),
			 			"property":"sampleCodes.length",
			 			"order":true,
			 			"hide":true,
			 			"type":"text"
			 		},
			 		{
						"header":Messages("containers.table.sampleCodes"),
						"property":"sampleCodes",
						"order":true,
						"hide":true,
						"type":"text",
						"render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>"
					},
			 		{
						"header":Messages("containers.table.contents.length"),
						"property":"contents.length",
						"order":true,
						"hide":true,
						"type":"number"
					},
					{
						"header":Messages("containers.table.tags"),
						"property": "contents",
						"order":true,
						"hide":true,
						"type":"text",
						"render":"<div list-resize='value.data.contents | getArray:\"properties.tag.value\" | unique' ' list-resize-min-size='3'>",
					},
			         {
			        	 "header":Messages("processes.table.projectCode"),
			        	 "property":"projectCodes",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("containers.table.valid"),
			        	 "property":"valuation.valid",
			        	 "order":true,
			        	 "hide":true,
			        	 "type":"text",
			        	 "filter":"codes:'valuation'"
			         },
			         {
						"header":Messages("containers.table.creationDate"),
						"property":"traceInformation.creationDate",
						"order":true,
						"hide":true,
						"type":"date"
					 },
					 {
						"header":Messages("containers.table.createUser"),
						"property":"traceInformation.createUser",
						"order":true,
						"hide":true,
						"type":"text"
					 }
			         ],
			         search:{
			        	 url:jsRoutes.controllers.containers.api.Containers.list()
			         },
			         pagination:{
			 			mode:'local'
				 	 },
				 	 order:{
			 			by:'code',
			 			mode:'local'
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
	
	$scope.selectDefaultFromExperimentType = function(){
		var selectionList = {};	
		$scope.form.fromExperimentTypeCodes=[];
			
		if(angular.isDefined($scope.form.nextProcessTypeCode)){
			selectionList = angular.copy($scope.lists.getExperimentTypesWithNone());
			$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.getDefaultFirstExperiments($scope.form.nextProcessTypeCode).url)
			.success(function(data, status, headers, config) {
				var defaultFirstExperimentTypes = data;
				console.log("defaultFirstExperimentTypes= "+defaultFirstExperimentTypes);
				angular.forEach(defaultFirstExperimentTypes, function(experimentType, key){
					angular.forEach(selectionList, function(item, index){
						if(experimentType.code==item.code){
							console.log("experimentType.code= "+experimentType.code);
							console.log("item.code= "+item.code);
							console.log("index= "+index);								
							$scope.form.fromExperimentTypeCodes.push(item.code);							
							console.log("form.fromExperimentTypeCodes= "+$scope.form.fromExperimentTypeCodes);
						}
					});
				});
				
				$scope.search();
			});
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
		$scope.errors.processCategory = {};
		$scope.errors.processType = {};
		if(($scope.form.processCategory && $scope.form.nextProcessTypeCode) || $scope.form.createUser){
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
				jsonSearch.isEmptyFromExperimentTypeCodes = false;
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
			
			if($scope.form.createUser){
				jsonSearch.createUser = $scope.form.createUser;
			}

			if($scope.form.fromDate)jsonSearch.fromDate = moment($scope.form.fromDate, Messages("date.format").toUpperCase()).valueOf();
			if($scope.form.toDate)jsonSearch.toDate = moment($scope.form.toDate, Messages("date.format").toUpperCase()).valueOf();

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
		$scope.lists.refresh.containerSupports();
		$scope.lists.refresh.containerSupportCategories();
		$scope.lists.refresh.users();
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
			        	 "order":true,
			        	 "hide":true,
			        	 "position":1,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.line"),
			        	 "property":"support.line",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":2,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.columns"),
			        	 "property":"support.column",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":3,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.projectCode"),
			        	 "property":"projectCode",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":4,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("processes.table.sampleCode"),
			        	 "property":"sampleCode",
			        	 "order":true,
			        	 "hide":true,
			        	 "position":5,
			        	 "type":"text"
			         },
			         {
			 			"header":Messages("containers.table.contents.length"),
			 			"property":"contents.length",
			 			"order":true,
			 			"hide":true,
			 			"position":5.01,
			 			"type":"number"
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
			        	 "order":true,
			        	 "hide":true,
			        	 "position":6,
			        	 "filter": "codes:'state'",
			        	 "type":"text"
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
			         edit:{
			        	 active:true,
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

	$scope.getProcessesColumns = function(){
		var columns = [
		         {
		        	 "header":Messages("processes.table.containerInputCode"),
		        	 "property":"containerInputCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":1,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.sampleCode"),
		        	 "property":"sampleCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":2,
		        	 "type":"text"
		         },
		         {
		 			"header":Messages("containers.table.contents.length"),
		 			"property":"contents.length",
		 			"url":"'/api/containers/'+containerInputCode",
		 			"order":true,
		 			"hide":true,
		 			"position":2.01,
		 			"type":"number"
		 		},
		         {
		        	 "header":Messages("processes.table.columns"),
		        	 "property":"support.column",
		        	 "url":"'/api/containers/'+containerInputCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":3.5,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.line"),
		        	 "property":"support.line",
		        	 "url":"'/api/containers/'+containerInputCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":3,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.sampleOnInputContainer.properties.tag"),
		        	 "property":"sampleOnInputContainer.properties.tag.value",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":4,
		        	 "type":"text"
		         },		         
		         {
		        	 "header":Messages("processes.table.typeCode"),
		        	 "property":"typeCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":9,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.stateCode"),
		        	 "property":"state.code",
		        	 "order":true,
		        	 "hide":true,
		        	 "filter": "codes:'state'",
		        	 "position":30,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.code"),
		        	 "property":"code",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":33,
		        	 "type":"text"
		         },
		         {
		        	 "header":Messages("processes.table.creationDate"),
		        	 "property":"traceInformation.creationDate",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":34,
		        	 "type":"date"
		         },
		         {
		        	 "header":Messages("processes.table.projectCode"),
		        	 "property":"projectCode",
		        	 "order":true,
		        	 "hide":true,
		        	 "position":37,
		        	 "type":"text"
		         }
		 ];
		
		columns = columns.concat($scope.processPropertyColumns);
	
		return columns;
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
		$scope.message.details = {};
		$scope.message.isDetails = false;
		$scope.datatable.save();
		$scope.processesToSave = [];
		var data = $scope.datatable.getData();
		var url = "";
		var processes = [];
		$scope.promises = [];
		if(!$scope.supportView){
			url =  jsRoutes.controllers.processes.api.Processes.saveBatch().url;
			$scope.datatable.config.spinner.start = true;
			for(var i=0;i<data.length;i++){
				if($scope.lineClasses[i] != "success"){
					for(var j = 0; j < data[i].contents.length; j++){ //one process by sample
						var processData = data[i];
						processData.properties.limsCode = undefined;
						processData.properties.receptionDate = undefined;
						var process = {index:i, data:{
								projectCode: data[i].contents[j].projectCode,
								sampleCode: data[i].contents[j].sampleCode,
								containerInputCode: processData.code,
								typeCode:$scope.form.nextProcessTypeCode,
								categoryCode:$scope.form.processCategory,
								properties:processData.properties,
								sampleOnInputContainer:{sampleCode:processData.contents[j].sampleCode,
														sampleCategoryCode:processData.contents[j].sampleCategoryCode,
														sampleTypeCode:processData.contents[j].sampleTypeCode,
														percentage:processData.contents[j].percentage,
														properties:processData.contents[j].properties,
														containerCode:data[i].code,
														containerSupportCode:data[i].support.code,
														mesuredVolume:data[i].mesuredVolume,
														mesuredQuantity:data[i].mesuredQuantity,
														mesuredConcentration:data[i].mesuredConcentration}
						}};
						processes.push(process);
					}
				}
			}
			var nbElementByBatch = Math.ceil(processes.length / 6); //6 because 6 request max in parrallel with firefox and chrome
			for(var i = 0; i  < 6 && processes.length > 0 ; i++){
				$scope.promises.push($scope.getSaveRemoteRequest(url, processes.splice(0, nbElementByBatch))); 								
			}
			
		}else{
			$scope.datatable.config.spinner.start = true;
			var i=0;
			for(i=0;i<data.length;i++){
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
				
				if($scope.lineClasses[i] != "success"){
					$scope.promises.push($scope.saveSupport(url,i,process,data[i].support.code));
				}
			}
		}

		$q.all($scope.promises).then(function (res) {
			$scope.message.clazz="alert alert-success";
			$scope.message.text=Messages('experiments.msg.save.sucess');
			$scope.doneAndRecorded = true;

			$scope.basket.reset();
			$scope.datatable.setColumnsConfig($scope.getProcessesColumns());
			$scope.datatable.setData($scope.processes);
			var displayResult = $scope.datatable.displayResult;
			for(var i=0;i<displayResult.length;i++){
				$scope.datatable.displayResult[i].line.trClass = $scope.lineClasses[i];
			}
			$scope.datatable.config.spinner.start = false;
		}, function(res){
			$scope.datatable.config.spinner.start = false;
			$scope.message.clazz = 'alert alert-danger';
			$scope.message.text = Messages('experiments.msg.save.error');
			
			/*var displayResult = $scope.datatable.displayResult;
			for(var i=0;i<displayResult.length;i++){
				$scope.datatable.displayResult[i].line.trClass = $scope.lineClasses[i];
			}*/
			
			$scope.doneAndRecorded = false;
			$scope.datatable.config.remove.active = true;
		});	
	};

	
	$scope.getSaveRemoteRequest = function(url, processes){
		return $http.post(url, processes)
		.success(function(data, status, headers, config) {
			if(data!=null){
				for(var i=0;i<data.length;i++){
					//$scope.datatable.displayResult[data[i].index].line.trClass = "success";
					$scope.lineClasses[data[i].index] = "success";
					$scope.processes = $scope.processes.concat(data[i].data);
				}
				var displayResult = $scope.datatable.displayResult;
				for(var i=0;i<displayResult.length;i++){
					$scope.datatable.displayResult[i].line.trClass = $scope.lineClasses[i];
				}
			}
		})
		.error(function(data, status, headers, config) {
			for(var i=0;i<data.length;i++){
				$scope.lineClasses[data[i].index] = "danger";
				$scope.datatable.addErrors(data[i].index,data[i].data);
				$scope.message.details = data[i].data;
				$scope.message.isDetails = true;
			}
			var displayResult = $scope.datatable.displayResult;
			for(var i=0;i<displayResult.length;i++){
				$scope.datatable.displayResult[i].line.trClass = $scope.lineClasses[i];
			}
		});
	};
	
	$scope.saveSupport = function(url, index, process, supportCode){
		return $http.post(url,process, {params:{"fromSupportContainerCode": supportCode}})
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.lineClasses[index] = "success";
				$scope.processes = $scope.processes.concat(data);
				$scope.datatable.config.remove.active = false;
				var displayResult = $scope.datatable.displayResult;
				for(var j=0;j<displayResult.length;j++){
					$scope.datatable.displayResult[j].line.trClass = $scope.lineClasses[j];
				}
			}
		}).error(function(data, status, headers, config) {
			$scope.lineClasses[index] = "danger";
			$scope.message.details = data;
			$scope.message.isDetails = true;
			var displayResult = $scope.datatable.displayResult;
			for(var j=0;j<displayResult.length;j++){
				$scope.datatable.displayResult[j].line.trClass = $scope.lineClasses[j];
			}
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
		$scope.processPropertyColumns = [];
		return $http.get(jsRoutes.controllers.processes.tpl.Processes.getPropertiesDefinitions(typeCode).url)
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
					column.position = (9+(property.displayOrder/1000));
					$scope.processPropertyColumns.push(column);
					$scope.datatable.addColumn(9+(property.displayOrder/1000),column);	
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
	$scope.lineClasses = [];
	$scope.processes = [];
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.basket = mainService.getBasket();
	$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
	$scope.addNewProcessColumns();
	$scope.datatable.selectAll(false);
	if($scope.basket.length() != 0){
		$scope.doneAndRecorded = false;
		$scope.datatable.config.remove.active = true;
	}else{
		$scope.doneAndRecorded = true;
		$scope.datatable.config.remove.active = false;
	}
	$scope.swithView();
}]);
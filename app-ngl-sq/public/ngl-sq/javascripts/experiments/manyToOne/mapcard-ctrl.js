angular.module('home').controller('ManyToOneMapcardCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','manyToOne','mainService','tabService', function($scope,$window, datatable, $http,lists,$parse,$q,$position,manyToOne,mainService,tabService) {
	$scope.datatableConfig = {
			name:"FDR_Mapcard",
			columns:[
			         {
			        	 "header":Messages("containers.table.supportCode"),
			        	 "property":"support.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":0,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.categoryCode"),
			        	 "property":"support.categoryCode",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.projectCodes"),
			        	 "property":"projectCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":3,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.sampleCodes"),
			        	 "property":"sampleCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":4,
						 "render":"<div list-resize='value.data.sampleCodes | unique'>",
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":6,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.fromExperimentTypeCodes"),
			        	 "property":"fromExperimentTypeCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":7,
			        	 "extraHeaders":{0:"Inputs"}
			         }
			         ],
			compact:false,
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local', //or 
				active:true,
				by:'ContainerInputCode'
			},
			remove:{
				active:false,
			},
			save:{
				active:true,
				showButton: false,
				mode:'local',
			},
			hide:{
				active:true
			},
			edit:{
				active: !$scope.doneAndRecorded,
				showButton: false,
				columnMode:true
			},
			messages:{
				active:false,
				columnMode:true
			},
			extraHeaders:{
				number:2,
				dynamic:true,
			},
			exportCSV:{
				active:true,
				showButton:true,
				delimiter:";",
				start:false
			},
			otherButton:{
				active:true,
				template:'<button class="btn btn btn-info" ng-click="newPurif()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doPurif" title="'+Messages("experiments.addpurif")+'">Messages("experiments.addpurif")</button><button class="btn btn btn-info" ng-click="newQc()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doQc" title="Messages("experiments.addqc")">Messages("experiments.addqc")</button>'
			}
	};
	
	$scope.$on('experimentToInput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToInput($scope.datatable);
	});
	
	$
	$scope.$on('deleteInstrumentPropertiesInputs', function(e, header) {
			angular.forEach($scope.datatable.config.columns, function(column, index){
				if(column.extraHeaders != undefined && column.extraHeaders[1] == header){
					$scope.datatable.deleteColumn(index);
				}
			});
	});
	
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"inputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(2,column);
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"inputExperimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs","1":"Experiments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(2,column);
	});
	
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"outputExperimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs","1":"Experiments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(-1,column);
	});
	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"outputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(-1,column);
	});
	
	$scope.addOutputColumns = function(){
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(Messages("containers.table.code"),"outputContainerUsed.code",false, true,true,"text",false,undefined,{"0":"Outputs"}));
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(Messages("containers.table.stateCode"),"outputContainerUsed.state.code | codes:'state'",false, true,true,"text",false,undefined,{"0":"Outputs"}));
	};
	
	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.inputToExperiment($scope.datatable);
	});
	
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
			$scope.experiment.value.atomicTransfertMethods[0] = {class:atomicTransfertMethod, inputContainerUseds:[], line:"1", column:"1"};
			angular.forEach(containers, function(container){
				$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.push({code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state,locationOnContainerSupport:container.support});
			});
	};
	
	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
		$scope.init_atomicTransfert(containers, atomicTransfertMethod);
	});
	
	$scope.$on('addInstrumentPropertiesInputToScope', function(e, data) {
		for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerIn")){
					var getter = $parse("datatable.displayResult["+i+"].inputInstrumentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties[data[j].code].value);
					}else{
						getter.assign($scope,"");
					}
				}
			}
		}
	});
	
	$scope.$on('addExperimentPropertiesOutputToScope', function(e, data) {
		for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					var getter = $parse("datatable.displayResult["+i+"].outputExperimentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].experimentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].experimentProperties[data[j].code].value);
					}else{
						getter.assign($scope,"");
					}
				}
			}
		}
	});
	
	$scope.$on('addExperimentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerIn")){
						var getter = $parse("datatable.displayResult["+i+"].inputExperimentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties[data[j].code].value);
						}else{
							getter.assign($scope,"");
						}
					}
				}
			}
		}
	});
	
	$scope.$on('addInstrumentPropertiesOutputToScope', function(e, data) {
		for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					var getter = $parse("datatable.displayResult["+i+"].outputInstrumentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].instrumentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].instrumentProperties[data[j].code].value);
					}else{
						getter.assign($scope,"");
					}
				}
			}
		}
	});
	
	$scope.$on('save', function(e, promises, func, endPromises) {
		$scope.setValidePercentage($scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds);
		promises.push($scope.datatable.save());
		$scope.$emit('viewSaved', promises, func, endPromises);
	});
	
	$scope.refreshView = function(){
		$scope.atomicTransfere.reloadContainersDatatable($scope.datatable, outputToExperimentHelper, experimentToOutputHelper);
	};
	
	$scope.$on('refresh', function(e) {
		$scope.refreshView();
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {
		//$scope.atomicTransfere.outputToExperiment($scope.datatable);
		outputToExperimentHelper($scope.datatable);
	});
	
	var outputToExperimentHelper = function(output) {
		var allData = output.getData();
		if(allData != undefined){
			for(var i=0;i<allData.length;i++){
				var index = $scope.atomicTransfere.searchOutputPositionByInputContainerCode(allData[i].code || allData[i].inputCode);
				if(angular.isDefined(allData[i].outputContainerUsed)/* && allData[i].outputContainerUsed.code !== undefined*/){
					$scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0] = allData[i].outputContainerUsed;
				}										
				if(allData[i].outputInstrumentProperties != undefined){
					$scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0].instrumentProperties = allData[i].outputInstrumentProperties;
					$scope.atomicTransfere.getVarExperimentCommonFunctions.removeNullProperties($scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0].instrumentProperties);
				}
				if(allData[i].outputExperimentProperties!= undefined){
					$scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0].experimentProperties = allData[i].outputExperimentProperties;	
					$scope.atomicTransfere.getVarExperimentCommonFunctions($scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0].experimentProperties);
				}
			}
			output.setData(allData,allData.lenght);
		}
	};
	
	$scope.$on('experimentToOutput', function(e, atomicTransfertMethod) {
		//$scope.atomicTransfere.experimentToOutput($scope.datatable);
		experimentToOutputHelper($scope.datatable);
	});
	
	var experimentToOutputHelper = function(output) {
		var allData = output.getData();
		if(angular.isDefined(allData) && allData.length>0){
			for(var i=0; i<allData.length;i++){
				var position = $scope.atomicTransfere.searchOutputPositionByInputContainerCode(allData[i].code || allData[i].inputCode);
				if($scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds!=null && angular.isDefined($scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0])){
					allData[i].outputContainerUsed  = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0];
					allData[i].outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0].instrumentProperties;
					allData[i].outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0].experimentProperties;
				}										
			}
			output.setData(allData,allData.length)
		}
	};
	
	$scope.setValidePercentage = function(containerUseds){
		var l = containerUseds.length;
		angular.forEach(containerUseds, function(container){			
				if(container.percentage != 100/l){
					container.percentage = 100/l;
				}			
		});
		
	};
	
	$scope.$on('disableEditMode', function(){
		$scope.datatable.config.edit.active = false;
	});
	
	$scope.$on('enableEditMode', function(){
		$scope.datatable.config.edit.active = true;
	});
	
	//Init
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.atomicTransfere = manyToOne($scope, "datatable", "none");

	$scope.experiment.outputGenerated = $scope.isOutputGenerated();
	
	if($scope.experiment.editMode){
		$scope.atomicTransfere.loadExperiment($scope.datatable, outputToExperimentHelper, experimentToOutputHelper);
	}else{
		$scope.atomicTransfere.newExperiment($scope.datatable);
	}
	
}]);
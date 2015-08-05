angular.module('home').controller('DenatDilLibCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','oneToOne','mainService','tabService', function($scope,$window, datatable, $http,lists,$parse,$q,$position,oneToOne,mainService,tabService) {

	/*
	 1) Code Container
2) Etat container
3) Projet(s)
4) Echantillon(s)
5) Code aliquot
6) Tag
7) Concentration (nM) 
	 
	 */
	
	$scope.datatableConfig = {
			name:"FDR_Tube",
			columns:[
			 
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":2,
			        	 "extraHeaders":{0:"Inputs"}
			         },				         
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainer.projectCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":3,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":4,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
				     {
			        	"header":"Code aliquot",
			 			"property": "inputContainer.contents",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":5,
			 			"render": "<div list-resize='cellValue | getArray:\"properties.sampleAliquoteCode.value\"| unique' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
			         {
			        	"header":Messages("containers.table.tags"),
			 			"property": "inputContainer.contents",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":6,
			 			"render":"<div list-resize='cellValue | getArray:\"properties.tag.value\" | unique' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
			         },
				 
					 {
			        	 "header":Messages("containers.table.concentration") + " (nM)",
			        	 "property":"inputContainer.mesuredConcentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":7,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			        /* {
			        	 "header":function(){return Messages("containers.table.volume") + " (µL)"},
			        	 "property":"mesuredVolume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":5,
			        	 "extraHeaders":{0:"Inputs"}
			         },*/
			         {
			        	 "header":Messages("containers.table.concentration") + " (pM)",
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "convertValue": {"active":true, "displayMeasureValue":"pM", "saveMeasureValue":"nM"},			        	 
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 //"defaultValues":10,
			        	 "position":8,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.volume")+ " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":9,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":11,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":12,
			        	 "extraHeaders":{0:"Outputs"}
			         }
			         
			         ],
			compact:true,
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local', //or 
				active:true,
				by:'inputContainer.code'
			},
			remove:{
				active: (!$scope.doneAndRecorded && !$scope.inProgressNow),
				showButton: (!$scope.doneAndRecorded && !$scope.inProgressNow),
				mode:'local'
			},
			save:{
				active:true,
	        	withoutEdit: true,
	        	showButton:false,
	        	mode:'local'
			},
			hide:{
				active:true
			},
			edit:{
				active: (!$scope.doneAndRecorded && !$scope.inProgressNow),
				columnMode:true
			},
			messages:{
				active:false,
				columnMode:true
			},
			exportCSV:{
				active:true,
				showButton:true,
				delimiter:";",
				start:false
			},
			extraHeaders:{
				number:2,
				dynamic:true,
			},
			otherButton:{
				active:true,
				template:'<button class="btn btn btn-info" ng-click="newPurif()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doPurif" title="'+Messages("experiments.addpurif")+'">Messages("experiments.addpurif")</button><button class="btn btn btn-info" ng-click="newQc()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doQc" title="Messages("experiments.addqc")">Messages("experiments.addqc")</button>'
			}
	};
	
	$scope.$on('experimentToInput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToInput($scope.datatable);
	});
	
	$scope.$on('deleteInstrumentPropertiesInputs', function(e, header) {
		angular.forEach($scope.datatable.config.columns, function(column, index){
				if(column.extraHeaders != undefined && column.extraHeaders[1] == header){
					$scope.datatable.deleteColumn(index);
				}
			});
	});
	
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"inputContainerUsed.instrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(2,column);
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {				
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"inputContainerUsed.experimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(data.displayOrder,column);
	});
	
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"outputContainerUsed.experimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs"});
		column.defaultValues = data.defaultValue;
		if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
			column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
		}
		$scope.datatable.addColumn(-1,column);
	});
	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"outputContainerUsed.instrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(-1,column);
	});
	
	$scope.addOutputColumns = function(){
 		//nothing
 	};
	
	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.inputToExperiment($scope.datatable);		
	});
	
	$scope.$on('save', function(e, promises, func, endPromises) {	
		$scope.setValidePercentage($scope.experiment.value.atomicTransfertMethods);
		promises.push($scope.datatable.save());
		$scope.atomicTransfere.outputToExperiment($scope.datatable);
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
		$scope.atomicTransfere.outputToExperiment($scope.datatable);
		//outputToExperimentHelper($scope.datatable);
	});
	
//	var outputToExperimentHelper = function(output) {
//		var allData = output.getData();
//		if(allData != undefined){
//			for(var i=0;i<allData.length;i++){
//				var index = $scope.atomicTransfere.searchOutputPositionByInputContainerCode(allData[i].code || allData[i].inputCode);
//				if(angular.isDefined(allData[i].outputContainerUsed)/* && allData[i].outputContainerUsed.code !== undefined*/){
//					$scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0] = allData[i].outputContainerUsed;
//				}										
//				if(allData[i].outputInstrumentProperties != undefined){
//					$scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0].instrumentProperties = allData[i].outputInstrumentProperties;
//					$scope.atomicTransfere.getVarExperimentCommonFunctions.removeNullProperties($scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0].instrumentProperties);
//				}
//				if(allData[i].outputExperimentProperties!= undefined){
//					$scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0].experimentProperties = allData[i].outputExperimentProperties;	
//					$scope.atomicTransfere.getVarExperimentCommonFunctions($scope.experiment.value.atomicTransfertMethods[index].outputContainerUseds[0].experimentProperties);
//				}
//			}
//			output.setData(allData,allData.lenght);
//		}
//	};

	$scope.$on('experimentToOutput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToOutput($scope.datatable);
		//experimentToOutputHelper($scope.datatable);
	});
	/*
	var experimentToOutputHelper = function(output) {
		var allData = output.getData();
		if(angular.isDefined(allData) && allData.length>0){
			for(var i=0; i<allData.length;i++){
				var position = $scope.atomicTransfere.searchOutputPositionByInputContainerCode(allData[i].code || allData[i].inputCode);
				if(angular.isDefined($scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0])){
					allData[i].outputContainerUsed  = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0];
					allData[i].outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0].instrumentProperties;
					allData[i].outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0].experimentProperties;
				}										
			}
			output.setData(allData,allData.length)
		}
	};
	*/
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
			angular.forEach(containers, function(container,index){
				$scope.experiment.value.atomicTransfertMethods[index] = {class:atomicTransfertMethod,line:(index+1), column:"1", inputContainerUseds:[], outputContainerUseds:[{volume:{unit:"µL"},concentration:{unit:"nM"},experimentProperties:{}}]};
				$scope.experiment.value.atomicTransfertMethods[index].inputContainerUseds = [{code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state,locationOnContainerSupport:container.support}];
			});
			$scope.atomicTransfere.experimentToOutput($scope.datatable);
	};
	
	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
		$scope.init_atomicTransfert(containers, atomicTransfertMethod);
	});	
	
	$scope.setValidePercentage = function(atomics){
		angular.forEach(atomics, function(atomic) {
			atomic.inputContainerUseds[0].percentage = 100.0;
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
	
	$scope.atomicTransfere = oneToOne($scope,"datatable", "none");
	
	$scope.experiment.outputGenerated = $scope.isOutputGenerated();
	
	if($scope.experiment.editMode){
		$scope.atomicTransfere.loadExperiment($scope.datatable);
	}else{		
		$scope.atomicTransfere.newExperiment($scope.datatable);	
	}
}]);
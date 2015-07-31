angular.module('home').controller('NanoporeFragmentationCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','oneToOne','mainService','tabService', function($scope,$window, datatable, $http,lists,$parse,$q,$position,oneToOne,mainService,tabService) {
	$scope.datatableConfig = {
			name:"FDR_Tube",
			columns:[
			  /*       {
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
			         },  */
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.fromExperimentTypeCodes"),
			        	 "property":"fromExperimentTypeCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			 			"render":"<div list-resize='cellValue | unique | codes:\"type\"' list-resize-min-size='3'>",
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
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":5,
			        	 "extraHeaders":{0:"Inputs"}
			         },					 
					 {
			        	 "header":function(){return Messages("containers.table.concentration") + " (ng/µL)"},
			        	 "property":"mesuredConcentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":function(){return Messages("containers.table.volume") + " (µL)"},
			        	 "property":"mesuredVolume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":7,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":function(){return Messages("containers.table.concentration") + " (nM)"},
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "defaultValues":10,
			        	 "position":8,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":function(){return Messages("containers.table.volume")+ " (µL)"},
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":9,
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
				by:'code'
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
		var column = $scope.datatable.newColumn(data.name,"inputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(data.displayOrder,column);
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {				
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"inputExperimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(data.displayOrder,column);
	});
	
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"outputExperimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs"});
		column.defaultValues = data.defaultValue;
		if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
			column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
		}
		$scope.datatable.addColumn(data.displayOrder+10,column);
	});
	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"outputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(data.displayOrder+10,column);
	});
	
	$scope.addOutputColumns = function(){
		$scope.datatable.addColumn(1000050,$scope.datatable.newColumn(Messages("containers.table.code"),"outputContainerUsed.code",false, true,true,"text",false,undefined,{"0":"Outputs"}));
		$scope.datatable.addColumn(1000051,$scope.datatable.newColumn(Messages("containers.table.stateCode"),"outputContainerUsed.state.code | codes:'state'",false, true,true,"text",false,undefined,{"0":"Outputs"}));
	};
	
	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.inputToExperiment($scope.datatable);		
	});
	
	$scope.$on('addInstrumentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
		for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerIn")){
					var getter = $parse("datatable.displayResult["+i+"].inputInstrumentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0].instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0].instrumentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0].instrumentProperties[data[j].code].value);
					}else{
						getter.assign($scope,undefined);
					}
				}
			}
		}
	}
	});
	
	$scope.$on('addExperimentPropertiesOutputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
		for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					var getter = $parse("datatable.displayResult["+i+"].outputExperimentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].experimentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].experimentProperties[data[j].code].value);
					}else{
						getter.assign($scope,undefined);
					}
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
					if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0].experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0].experimentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0].experimentProperties[data[j].code].value);
					}else{
						getter.assign($scope,undefined);
					}
				}
			}
		}
	}
	});
	
	$scope.$on('addInstrumentPropertiesOutputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
		for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					var getter = $parse("datatable.displayResult["+i+"].outputInstrumentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].instrumentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].instrumentProperties[data[j].code].value);
					}else{
						getter.assign($scope,undefined);
					}
				}
			}
		}
	}
	});
	
	$scope.$on('save', function(e, promises, func, endPromises) {	
		$scope.setValidePercentage($scope.experiment.value.atomicTransfertMethods);
		promises.push($scope.datatable.save());
		outputToExperimentHelper($scope.datatable); 
		$scope.$emit('viewSaved', promises, func, endPromises);
	});
	
	$scope.refreshView = function(){
		//inversion
		$scope.atomicTransfere.reloadContainersDatatable($scope.datatable, experimentToOutputHelper, outputToExperimentHelper);
	};
	
	$scope.$on('refresh', function(e) {
		$scope.refreshView();
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {		
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
		experimentToOutputHelper($scope.datatable);
	});
	
	var experimentToOutputHelper = function(output) {
		var allData = output.getData();
		if(angular.isDefined(allData) && allData.length>0){
			for(var i=0; i<allData.length;i++){
				var position = $scope.atomicTransfere.searchOutputPositionByInputContainerCode(allData[i].code || allData[i].inputCode);
				if(angular.isDefined($scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0])){
					//Etat output container bug
					//allData[i].outputContainerUsed  = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0];
					allData[i].outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0].instrumentProperties;
					allData[i].outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0].experimentProperties;
				}										
			}
			output.setData(allData,allData.length)
		}
	};
	
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
			angular.forEach(containers, function(container,index){
				$scope.experiment.value.atomicTransfertMethods[index] = {class:atomicTransfertMethod,line:(index+1), column:"1", inputContainerUseds:[], outputContainerUseds:[{volume:{unit:"µL"},concentration:{unit:"nM"},experimentProperties:{}}]};
				$scope.experiment.value.atomicTransfertMethods[index].inputContainerUseds = [{code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state,locationOnContainerSupport:container.support}];
			});			
			experimentToOutputHelper($scope.datatable);
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
		//inversion function
		//$scope.atomicTransfere.loadExperiment($scope.datatable, outputToExperimentHelper, experimentToOutputHelper);
		$scope.atomicTransfere.loadExperiment($scope.datatable, experimentToOutputHelper,outputToExperimentHelper);
	}else{		
		$scope.atomicTransfere.newExperiment($scope.datatable);	
	}

}]);
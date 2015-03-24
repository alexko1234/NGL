angular.module('home').controller('ManyToOneTubeCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','manyToOne','mainService','tabService', function($scope,$window, datatable, $http,lists,$parse,$q,$position,manyToOne,mainService,tabService) {
	$scope.datatableConfig = {
			name:"FDR_Tube",
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
				mode:'local',
			},
			hide:{
				active:true
			},
			edit:{
				active:true,
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
//		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(Messages("containers.table.support.column"),"outputContainerUsed.locationOnContainerSupport.column",false, true,true,"text",false,undefined,{"0":"Outputs"}));
//		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(Messages("containers.table.support.line"),"outputContainerUsed.locationOnContainerSupport.line",false, true,true,"text",false,undefined,{"0":"Outputs"}));
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(Messages("containers.table.stateCode"),"outputContainerUsed.state.code | codes:'state'",false, true,true,"text",false,undefined,{"0":"Outputs"}));
	};
	
	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.inputToExperiment($scope.datatable);
	});
	
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
			$scope.experiment.value.atomicTransfertMethods[0] = {class:atomicTransfertMethod, inputContainerUseds:[], position:1};
			angular.forEach(containers, function(container){
				$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.push({code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state,locationOnContainerSupport:container.support});
			});
	};
	
	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
		$scope.init_atomicTransfert(containers, atomicTransfertMethod);
	});
	
	$scope.addExperimentOutputDatatableToScope = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerOut")){
						var getter = $parse("datatable.displayResult["+i+"].outputExperimentProperties."+data[j].code+".value");
						var k = $scope.datatable.displayResult[i].data.inputX;
						if($scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUsed.experimentProperties && $scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUsed.experimentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUsed.experimentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	};
	$scope.$on('addInstrumentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerIn")){
						var getter = $parse("datatable.displayResult["+i+"].inputInstrumentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	});

	$scope.addExperimentOutputDatatableToScope = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerOut")){
						var getter = $parse("datatable.displayResult["+i+"].outputExperimentProperties."+data[j].code+".value");
						var k = $scope.datatable.displayResult[i].data.inputX;
						if($scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUsed.experimentProperties && $scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUsed.experimentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUsed.experimentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	};

	$scope.$on('addExperimentPropertiesOutputToScope', function(e, data) {
		var i = 0;
		while($scope.experiment.value.atomicTransfertMethods[i] != undefined){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties == null){
						$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties = {};
					}

					if(!$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code]){
						$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code] = undefined;						
					}
				}
			}
			i++;
		}
	});

	$scope.$on('addExperimentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerIn")){
						var getter = $parse("datatable.displayResult["+i+"].inputExperimentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties[data[j].code]);
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
						if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	});

	$scope.$on('save', function(e, promises, func) {
		promises.push($scope.datatable.save());
		$scope.$emit('viewSaved', promises, func);
	});
	
	$scope.refreshView = function(){
		$scope.atomicTransfere.reloadContainersDatatable($scope.datatable);			
	};
	
	$scope.$on('refresh', function(e) {		
		$scope.refreshView();
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.outputToExperiment($scope.datatable);
	});
	
	$scope.$on('experimentToOutput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToOutput($scope.datatable);
	});	
	
	
	//Init
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.atomicTransfere = manyToOne($scope, "datatable", "none");

	$scope.experiment.outputGenerated = $scope.isOutputGenerated();
	
	if($scope.experiment.editMode){
		$scope.atomicTransfere.loadExperiment($scope.datatable);
	}else{
		$scope.atomicTransfere.newExperiment($scope.datatable);
	}
}]);
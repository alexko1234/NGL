angular.module('home').controller('FlowcellCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','manyToOne', function($scope,$window, datatable, $http,lists,$parse,$q,$position,manyToOne) {
	$scope.datatableConfig = {
			columns:[{
						"header":Messages("containers.table.supportCode"),
						"property":"support.code",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.supportCategoryCode"),
						"property":"support.categoryCode",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.support.column"),
						"property":"support.column",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.support.line"),
						"property":"support.line",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.projectCodes"),
						"property":"projectCodes",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.sampleCodes"),
						"property":"sampleCodes",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.state.code"),
						"property":"state.code",
						"order":true,
						"type":"text",
						"edit":true,
						"choiceInList": true,
						"possibleValues":'lists.getStates()',
						"filter":"codes:'state'",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.valid"),
						"property":"valuation.valid",
						"order":true,
						"type":"text",
						"filter":"codes:'valuation'",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.creationDate"),
						"property":"traceInformation.creationDate",
						"order":true,
						"type":"date",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.fromExperimentTypeCodes"),
						"property":"fromExperimentTypeCodes",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Inputs"}
					}],
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
		$scope.atomicTransfere.experimentToInput();
	});
	
	$scope.$on('deleteInstrumentPropertiesInputs', function(e, header) {
		/* for(var i=0;i<$scope.datatable.config.columns.length;i++){
				if($scope.datatable.config.columns[i].extraHeaders != undefined && $scope.datatable.config.columns[i].extraHeaders[1] == header){
					$scope.datatable.deleteColumn(i);
					i--;
				}
			}*/
	});
	
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(2,$scope.datatable.newColumn(data.name,"inputInstrumentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"}));
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
		console.log("ADD TEST");
		$scope.datatable.addColumn(2,$scope.datatable.newColumn(data.name,"inputExperimentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Inputs","1":"Experiments"}));
	});
	
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(data.name,"outputExperimentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Outputs","1":"Experiments"}));
	});
	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(data.name,"outputInstrumentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"}));
	});
	
	$scope.addOutputColumns = function(){
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn("Code","outputContainerUsed.code",false, true,true,"String",false,undefined,{"0":"Outputs"}));
	};
	
	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		console.log("LALALA");
		$scope.atomicTransfere.inputToExperiment();
	});
	
	$scope.$on('addInstrumentPropertiesInputToScope', function(e, data) {
		/*for(var i=0;i<$scope.datatable.getData().length;i++){
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
		}*/
	});
	
	$scope.$on('addExperimentPropertiesOutputToScope', function(e, data) {
		/*for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					var getter = $parse("datatable.displayResult["+i+"].outputExperimentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code].value);
					}else{
						getter.assign($scope,"");
					}
				}
			}
		}*/
	});
	
	$scope.$on('addExperimentPropertiesInputToScope', function(e, data) {
		/*angular.forEach(data ,function(property){
			if($scope.getLevel(property.levels, "ContainerOut")){
					for(var i=0;i<$scope.inputContainers.length;i++){
						var getter = $parse("inputContainers["+i+"].experimentProperties."+property.code+".value");
						console.log("inputContainers["+i+"].experimentProperties."+property.code+".value");
						getter.assign($scope,"");
					}
				}
			});
		});*/
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
	});
	
	$scope.$on('addInstrumentPropertiesOutputToScope', function(e, data) {
		/*for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					var getter = $parse("datatable.displayResult["+i+"].outputInstrumentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[j].code].value);
					}else{
						getter.assign($scope,"");
					}
				}
			}
		}*/
	});
	
	$scope.$on('save', function(e, promises, func) {
		//promises.push($scope.datatable.save());
		$scope.$emit('viewSaved', promises, func);
	});
	
	$scope.refreshView = function(){
		
	};
	
	$scope.$on('refresh', function(e) {
		$scope.refreshView();
		
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.outputToExperiment();
	});
	
	$scope.$on('experimentToOutput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToOutput();
	});
	
	$scope.duplicateContainer = function(container){
		$scope.inputContainers.push(angular.copy(container));
	};
	
	$scope.drop = function(e, data) {
		//capture the number of the atomicTransfertMethod
		var array_regexp = /^experiment.value.atomicTransfertMethods\[([0-9]+)\].+/;
		var model = e.dataTransfer.getData('Model');
		
		var match = model.match(array_regexp);
		if(!match){
			$scope[model].splice($scope[model].indexOf(data), 1);
		}else{
			$scope.experiment.value.atomicTransfertMethods[match[1]].inputContainerUseds.splice($scope.experiment.value.atomicTransfertMethods[match[1]].inputContainerUseds.indexOf(data), 1);
		}
	};
	
	$scope.beforeDropData = function(e, data, ngModel){
		var array_regexp = /^experiment.value.atomicTransfertMethods\[([0-9]+)\].+/;
		var model = ngModel;
		
		var match = model.match(array_regexp);
		if(match){
			data.locationOnContainerSupport.line = parseInt(match[1])+1;
			data.locationOnContainerSupport.column = 1;
		}
		
		return data;
	};
	
	//Init
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.atomicTransfere = manyToOne($scope, "dragndrop", "datatable");
	$scope.inputContainers = [];
	$scope.flowcells = [];
	//init flowcells
	for(var i=0;i<8;i++){
		$scope.flowcells[i] = [];
	}
	if($scope.experiment.editMode){
		$scope.atomicTransfere.loadExperiment();
	}else{
		$scope.atomicTransfere.newExperiment();
	}
	
}]);
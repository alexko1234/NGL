angular.module('home').controller('FlowcellCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','manyToOne','mainService','tabService', function($scope,$window, datatable, $http,lists,$parse,$q,$position,manyToOne,mainService,tabService) {
	$scope.datatableConfig = {
			columns:[{
						"header":Messages("containers.table.supportCode"),
						"property":"inputSupportCode",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.support.column"),
						"property":"outputPositionX",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Outputs"}
					},
					{
						"header":Messages("containers.table.support.line"),
						"property":"outputPositionY",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Outputs"}
					},
					{
						"header":Messages("containers.table.percentage"),
						"property":"percentage",
						"order":true,
						"type":"text",
						"edit":false,
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.state.code"),
						"property":"inputState.code",
						"order":true,
						"type":"text",
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.tags"),
						"property":"inputTags",
						"order":true,
						"type":"text",
						"edit":false,
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.sampleTypes"),
						"property":"inputSampleTypes",
						"order":true,
						"type":"text",
						"edit":false,
						"extraHeaders":{"0":"Inputs"}
					},
					{
						"header":Messages("containers.table.libProcessTypeCodes"),
						"property":"inputLibProcessTypeCodes",
						"order":true,
						"type":"text",
						"edit":false,
						"extraHeaders":{"0":"Inputs"}
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
		for(var i=0;i<$scope.datatable.config.columns.length;i++){
				if($scope.datatable.config.columns[i].extraHeaders != undefined && $scope.datatable.config.columns[i].extraHeaders[1] == header){
					$scope.datatable.deleteColumn(i);
					i--;
				}
		}
	});
	
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(4,$scope.datatable.newColumn(data.name,"inputInstrumentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"}));
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"inputExperimentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Inputs","1":"Experiments"});
		//column.position = data.displayOrder;
		//console.log(data.name+" - "+data.displayOrder);
		$scope.datatable.addColumn(data.displayOrder+4 ,column);
	});
	
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		if($scope.experiment.containerOutProperties.indexOf(data) == -1){
			$scope.experiment.containerOutProperties.push(data);
			$scope.datatable.addColumn(-1,$scope.datatable.newColumn(data.name,"outputExperimentProperties."+data.code+".value",false, true,true,"String",data.choiceInList,possibleValues,{"0":"Outputs","1":"Experiments"}));
		}
	});
	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(data.name,"outputInstrumentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"}));
	});
	
	$scope.addOutputColumns = function(){
	};
	
	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.inputToExperiment();
	});
	
	$scope.$on('addInstrumentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
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
		}
	});
	
	$scope.addExperimentOutputDatatableToScope = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
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
								$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code] = {};
								$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code].value = "";
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
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
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
			}
		}
	});
	
	$scope.$on('save', function(e, promises, func) {
		promises.push($scope.datatable.save());
		$scope.$emit('viewSaved', promises, func);
	});
	
	$scope.refreshView = function(){
		$scope.atomicTransfere.experimentToInput();
		$scope.atomicTransfere.experimentToOutput();
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
	
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
		console.log("atomic: "+$scope.laneCount);
		for(var i=0;i<$scope.laneCount;i++){
			$scope.experiment.value.atomicTransfertMethods[i] = {class:atomicTransfertMethod, line:(i+1), column:1, position:(i+1),inputContainerUseds:[],outputContainerUsed:{experimentProperties:{}}};
		}
	};
	
	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
		$scope.init_atomicTransfert(containers, atomicTransfertMethod);
	});
	
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
		
		$scope.atomicTransfere.reloadContainerDragNDrop();
	};
	
	$scope.beforeDropData = function(e, data, ngModel){
		/*var array_regexp = /^experiment.value.atomicTransfertMethods\[([0-9]+)\].+/;
		var model = ngModel;
		
		var match = model.match(array_regexp);
		if(match){
			//$scope.experiment.value.atomicTransfertMethods[match[1]].line = parseInt(match[1])+1;
			//$scope.experiment.value.atomicTransfertMethods[match[1]].column = 1;
		}*/
		
		return data;
	};
	
	$scope.setFlowcellProperty = function(lineNumber, value, property){
		for(var i=0;i<$scope.experiment.value.atomicTransfertMethods[lineNumber].inputContainerUseds.length;i++){
			$scope.experiment.value.atomicTransfertMethods[lineNumber].inputContainerUseds.experimentProperties[property].value = value;
		}
	};
	
	$scope.deleteInput = function(container){
		$scope.inputContainers.splice($scope.inputContainers.indexOf(container), 1);
	};
	
	$scope.deleteInputOnAtomic = function(index, container){
		$scope.experiment.value.atomicTransfertMethods[index].inputContainerUseds.splice($scope.experiment.value.atomicTransfertMethods[index].inputContainerUseds.indexOf(container),1);
	};
	
	$scope.hideRow = function(index){
		$scope.rows[index] = !$scope.rows[index];
	};
	
	$scope.init_flowcell = function(laneCount){
		$scope.laneCount = laneCount;
		if(!$scope.experiment.editMode){
			//init flowcells
			console.log($scope.laneCount);
			for(var i=0;i<$scope.laneCount;i++){
				$scope.flowcells[i] = [];
			}
		}
		
		for(var i=0;i<$scope.laneCount;i++){
			$scope.rows[i] = true;
		}
		console.log($scope.flowcells);
	};
	
	//Init
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.experiment.outputGenerated = true;
	$scope.atomicTransfere = manyToOne($scope, "dragndrop", "datatable");
	$scope.inputContainers = [];
	$scope.flowcells = [];
	$scope.rows = [];
	$scope.laneCount = 0;
	
	if($scope.experiment.editMode){
		$scope.atomicTransfere.loadExperiment();
		if(!angular.isUndefined(mainService.getBasket())){
			$scope.basket = mainService.getBasket().get();
			angular.forEach($scope.basket, function(basket){
			  $http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{supportCode:basket.code}})
				.success(function(data, status, headers, config) {
					$scope.clearMessages();
					if(data!=null){
						angular.forEach(data, function(container){
							$scope.inputContainers.push(container);
						});
					}
				})
				.error(function(data, status, headers, config) {
					alert("error");
				});
			});
		}
	}else{
		$scope.atomicTransfere.newExperiment();
	}
	
}]);
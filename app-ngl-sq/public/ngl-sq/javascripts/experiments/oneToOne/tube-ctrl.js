angular.module('home').controller('TubeCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','oneToOne', function($scope,$window, datatable, $http,lists,$parse,$q,$position,oneToOne) {
	$scope.datatableConfig = {
			columnsUrl : jsRoutes.controllers.experiments.tpl.Experiments.getEditExperimentColumns().url,
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
		angular.forEach($scope.datatable.config.columns, function(column, index){
				if(column.extraHeaders != undefined && column.extraHeaders[1] == header){
					$scope.datatable.deleteColumn(index);
				}
			});
	});
	
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(2,$scope.datatable.newColumn(data.name,"inputInstrumentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"}));
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
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
		$scope.atomicTransfere.inputToExperiment();
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
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code].value);
					}else{
						getter.assign($scope,"");
					}
				}
			}
		}
	});
	
	$scope.$on('addExperimentPropertiesInputToScope', function(e, data) {
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
	});
	
	$scope.$on('save', function(e, promises, func) {
		$scope.datatable.save();
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
	
	//Init
	$scope.datatable = datatable($scope, $scope.datatableConfig);
	$scope.atomicTransfere = oneToOne($scope,"datatable", "none");
	
	if($scope.experiment.editMode){
		$scope.atomicTransfere.loadExperiment();
	}else{
		$scope.atomicTransfere.newExperiment();
	}
	
}]);
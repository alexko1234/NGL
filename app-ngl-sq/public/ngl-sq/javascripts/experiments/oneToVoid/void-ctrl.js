angular.module('home').controller('VoidCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','oneToVoid','mainService','tabService', function($scope,$window, datatable, $http,lists,$parse,$q,$position,oneToVoid,mainService,tabService) {
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
		 for(var i=0;i<$scope.datatable.config.columns.length;i++){
				if($scope.datatable.config.columns[i].extraHeaders != undefined && $scope.datatable.config.columns[i].extraHeaders[1] == header){
					$scope.datatable.deleteColumn(i);
					i--;
				}
			}
	});
	
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		console.log(data);
		var column = $scope.datatable.newColumn(data.name,"inputInstrumentProperties."+data.code+".value",data.editable, true,true,"String",data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(2,column);
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
		console.log(data);
		var column = $scope.datatable.newColumn(data.name,"inputExperimentProperties."+data.code+".value",data.editable, true,true,"String",data.choiceInList,possibleValues,{"0":"Inputs","1":"Experiments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(2,column);
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.inputToExperiment($scope.datatable);
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
	
	
	$scope.$on('save', function(e, promises, func) {
		$scope.datatable.save();
		$scope.$emit('viewSaved', promises, func);
	});
	
	$scope.refreshView = function(){
		var i = 0;
		var k = 0;
		while($scope.experiment.value.atomicTransfertMethods[i] != undefined){
			$scope.datatable.displayResult[i].data.state = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.state;
			i++;
		 }
	};
	
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
		angular.forEach(containers, function(container,index){
			$scope.experiment.value.atomicTransfertMethods[index] = {class:atomicTransfertMethod, inputContainerUsed:[]};
			$scope.experiment.value.atomicTransfertMethods[index].inputContainerUsed = {code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state};
			$scope.experiment.outputVoid = true;

		});
	};

	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
		$scope.init_atomicTransfert(containers, atomicTransfertMethod);
	});

	
	$scope.$on('refresh', function(e) {
		$scope.refreshView();
		
		$scope.$emit('viewRefeshed');
	});
	
	//Init
	$scope.datatable = datatable($scope.datatableConfig);
	
	$scope.atomicTransfere = oneToVoid($scope,"datatable");
	
	if($scope.experiment.editMode){
		$scope.atomicTransfere.loadExperiment($scope.datatable);
	}else{
		$scope.atomicTransfere.newExperiment($scope.datatable);
	}
	
}]);
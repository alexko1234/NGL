/* 
	Don't forget to import the service you need (manyToOne, oneToOne, oneToMany, oneToVoid)
	This template file show methods that must be implemented, you can add your owns
*/

angular.module('home').controller('AliquotingCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','mainService','tabService','oneToMany', function($scope,$window, datatable, $http,lists,$parse,$q,$position,mainService,tabService,oneToMany) {
	$scope.datatableConfig = {
			name:"FDR_prepaFC",
			columns:[
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainerUsed.code",
			        	 "order":true,
			        	 "type":"text",
			        	 "position":0,
			        	 "mergeCells" : true,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.supportCode"),
			        	 "property":"inputSupportCode",
			        	 "order":true,
			        	 "type":"text",
			        	 "position":1,	
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.outputNumber"),
			        	 "property":"outputNumber",
			        	 "order":true,
			        	 "type":"number",
			        	 "position":1.1,
			        	 "edit":true,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.tags"),
			        	 "property":"inputTags",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":false,
			        	 "position":2,
			        	 "render":"<div list-resize='value.data.inputTags | unique' below-only-deploy>",
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":function(){
			        		 return Messages("containers.table.concentration") +" (nM)";
			        	 },
			        	 "property":"inputConcentration",
			        	 "order":true,
			        	 "type":"number",
			        	 "edit":false,
			        	 "position":3,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":function(){
			        		 return Messages("containers.table.volume") +" (µl)";
			        	 },
			        	 "property":"inputVolume.value",
			        	 "order":true,
			        	 "type":"number",
			        	 "edit":false,
			        	 "position":4,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputState.code",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":false,
			        	 "position":5,
			        	 "extraHeaders":{0:"Inputs"},
			        	 "filter":"codes:'state'"
			         },
			         {
			        	 "header":Messages("containers.table.percentage"),
			        	 "property":"inputContainerUsed.percentage",
			        	 "order":true,
			        	 "type":"number",
			        	 "edit":false,
			        	 "position":41,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
			        	 "type":"text",
			        	 "position":50,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"outputContainer.state.code",
			        	 "order":true,
			        	 "type":"text",
			        	 "position":50,
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
			         mergeCells:{
			        	active:true 
			         },
			         order:{
			        	 mode:'local', //or 
			        	 active:true,
			        	 by:'outputPositionX'
			         },
			         remove:{
			        	 active:false,
			         },
			         hide:{
			        	 active:true
			         },
			         edit:{
			        	 active: !$scope.doneAndRecorded,
			        	 columnMode:true
			         },
			         save:{
			        	 active:true,
			        	 withoutEdit: true,
			        	 showButton:false,
			        	 mode:'local'
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
	
	//This function copy the experiment informations in the input
	$scope.$on('experimentToInput', function(e, atomicTransfertMethod) {
		//The default function is:
		//$scope.atomicTransfere.experimentToInput($scope.datatable);
		$scope.experimentToInput();
	});
	
	//This function copy the input information to the experiment object
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		//The default function is:
		//$scope.atomicTransfere.inputToExperiment($scope.datatable);
		$scope.inputToExperiment($scope.datatable);
	});
	
	//Call when the view need to delete the instrument propeties for the input
	$scope.$on('deleteInstrumentPropertiesInputs', function(e, header) {
	
	});

	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"inputContainerUsed.instrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(2,column);
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"inputContainerUsed.experimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs","1":"Experiments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(2,column);
	});
	
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"outputContainerUsed.experimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs","1":"Experiments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(-1,column);
	});
	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"outputContainerUsed.instrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(-1,column);
	});

	$scope.addOutputColumns = function(){
	
	};
	
	//Add the output informations to the view
	$scope.$on('addOutputColumns', function(e) {
		
	});

	$scope.inputToExperiment = function(){
		var allData = $scope.datatable.getData();
		for(var i=0;i<allData.length;i++){
			var line = $scope.getInputContainerLine(allData[i].inputContainerUsed.code);
			$scope.experiment.value.atomicTransfertMethods[line].outputNumber = allData[i].outputNumber;
			$scope.experiment.value.atomicTransfertMethods[line].inputContainerUseds[0] = allData[i].inputContainerUsed;
		}
	};
	
	$scope.experimentToInput = function(){
		var allData = $scope.datatable.getData();
		for(var i=0;i<allData.length;i++){
			var line = $scope.getInputContainerLine(allData[i].inputContainerUsed.code);
			allData[i].outputNumber = $scope.experiment.value.atomicTransfertMethods[line].outputNumber;
			allData[i].inputContainerUsed = $scope.experiment.value.atomicTransfertMethods[line].inputContainerUseds[0];
		}
		
		$scope.datatable.setData(allData, allData.length);
	};
	
	$scope.experimentToOutput = function(){
		var allData = $scope.datatable.getData();
		if(allData != undefined){
			var k = 0;
			for(var i=0; i<$scope.experiment.value.atomicTransfertMethods.length;i++){
				for(var j=0; j<$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds.length;j++){
					allData[k].outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j];
					k++;
				}
			}
			$scope.datatable.setData(allData, allData.length);
		}
	};
	
	$scope.outputToExperiment = function(){
		var allData = $scope.datatable.getData();
		if(allData != undefined){
			var k = 0;
			for(var i=0; i<$scope.experiment.value.atomicTransfertMethods.length;i++){
				for(var j=0; j<$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds.length;j++){
					$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j] = allData[k].outputContainerUsed;
					k++;
				}
			}
		}
	};
	
	$scope.getInputContainerLine = function(code){
		for(var i=0;i<$scope.experiment.value.atomicTransfertMethods.length;i++){
			if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0].code === code){
				return i;
			}
		}
		throw "Container not in experiment input !";
		return null;
	};
	
	//Call when the view need to save
	$scope.$on('save', function(e, promises, func, endPromises) {	
		
		//push in the promises of the save you need to do
		promises.push($scope.datatable.save());
		
		$scope.inputToExperiment($scope.datatable);
		
		//Don't change that:
		$scope.$emit('viewSaved', promises, func,endPromises);
		$scope.propertyChanged = [];
	});
	
	//Call when the view need to disable the edit mode
	$scope.$on('disableEditMode', function(){
		
	});
	
	//Call when the view need to enable the edit mode
	$scope.$on('enableEditMode', function(){
	
	});


	//Call when the view need to refresh
	$scope.$on('refresh', function(e) {
		$scope.atomicTransfere.loadExperiment($scope.datatable);
		
		//Don't change that:
		$scope.$emit('viewRefeshed');
	});

	//Copy the informations from the output to the experiment object
	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {
		//$scope.atomicTransfere.outputToExperiment($scope.datatable);
		$scope.outputToExperiment();
	});

	//Copy thre informations from the experiment to the output
	$scope.$on('experimentToOutput', function(e, atomicTransfertMethod) {
		//$scope.atomicTransfere.experimentToOutput($scope.datatable);
		$scope.experimentToOutput();
	});


	//Call when we need to init the atomicTransfertMethod
	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
		$scope.experiment.value.atomicTransfertMethods = [];
		var i = 0;
		containers = $scope.atomicTransfere.containerToContainerUsed(containers);
		angular.forEach(containers, function(container){
			$scope.experiment.value.atomicTransfertMethods[i] = {class:atomicTransfertMethod, line:(i+1), column:1, inputContainerUseds:[],outputContainerUseds:[{experimentProperties:{}}]};
			$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.push(container);
			i++;
		});
	});


	//Init
	//init the input/output you want
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.atomicTransfere = oneToMany($scope, "datatable", "none");
	
	$scope.experiment.outputGenerated = $scope.isOutputGenerated();
	$scope.inputContainers = [];
	$scope.rows = [];
	$scope.view = 1;
	$scope.allOutputContainersUsed = [];


	if($scope.experiment.editMode){
		//When the experiment already exist
		$scope.atomicTransfere.loadExperiment($scope.datatable);
	}else{
		//When the experiment is new
		$scope.atomicTransfere.newExperiment($scope.datatable);
	}

}]);
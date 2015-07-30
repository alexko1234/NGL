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
			        	 "extraHeaders":{0:"solution stock"}
			         },
			         {
			        	 "header":Messages("containers.table.supportCode"),
			        	 "property":"inputSupportCode",
			        	 "order":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"solution stock"}
			         },
			         {
			        	 "header":Messages("containers.table.tags"),
			        	 "property":"inputTags",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":false,
			        	 "position":2,
			        	 "render":"<div list-resize='value.data.inputTags | unique' below-only-deploy>",
			        	 "extraHeaders":{0:"solution stock"}
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
			        	 "extraHeaders":{0:"solution stock"}
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
			        	 "extraHeaders":{0:"solution stock"}
			         },
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputState.code",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":false,
			        	 "position":5,
			        	 "extraHeaders":{0:"solution stock"},
			        	 "filter":"codes:'state'"
			         },
			         {
			        	 "header":Messages("containers.table.percentage"),
			        	 "property":"inputContainerUsed.percentage",
			        	 "order":true,
			        	 "type":"number",
			        	 "edit":false,
			        	 "position":41,
			        	 "extraHeaders":{0:"prep FC"}
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
	
	};
	
	//Add the output informations to the view
	$scope.$on('addOutputColumns', function(e) {
		
	});

	$scope.inputToExperiment = function(){
		var allData = $scope.datatable.getData();
		for(var i=0;i<allData.length;i++){
			var line = $scope.getInputContainerLine(allData[i].inputContainerUsed.code);
			$scope.experiment.value.atomicTransfertMethods[line].inputContainerUseds[0].state = allData[i].inputContainerUsed.state;
			$scope.experiment.value.atomicTransfertMethods[line].inputContainerUseds[0].experimentProperties = allData[i].inputExperimentProperties;
			$scope.experiment.value.atomicTransfertMethods[line].inputContainerUseds[0].instrumentProperties = allData[i].inputInstrumentProperties;
		}
	};
	
	$scope.experimentToInput = function(){
		var allData = $scope.datatable.getData();
		for(var i=0;i<allData.length;i++){
			var line = $scope.getInputContainerLine(allData[i].inputContainerUsed.code);
			allData[i].inputContainerUsed.state = $scope.experiment.value.atomicTransfertMethods[line].inputContainerUseds[0].state;
			allData[i].inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[line].inputContainerUseds[0].experimentProperties;
			allData[i].inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[line].inputContainerUseds[0].instrumentProperties;
		}
		
		$scope.datatable.setData(allData, allData.length);
	};
	
	$scope.experimentToOutput = function(){
		var allData = $scope.datatable.getData();
		if(allData != undefined){
			for(var i=0;i<allData.length;i++){
				if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
					var k = 0;
					for(var i=0; i<$scope.experiment.value.atomicTransfertMethods.length;i++){
						for(var j=0; j<$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds.length;j++){
							allData[k].outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].instrumentProperties;
							allData[k].outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].experimentProperties;
							k++;
						}
					}
				}
			}
			$scope.datatable.setData(allData, allData.length);
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
		$scope.inputToExperiment($scope.datatable);
		
		//push in the promises of the save you need to do
		promises.push($scope.datatable.save());
		
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
		
		//Don't change that:
		$scope.$emit('viewRefeshed');
	});

	//Copy the informations from the output to the experiment object
	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.outputToExperiment($scope.datatable);
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
		angular.forEach(containers, function(container){
			$scope.experiment.value.atomicTransfertMethods[i] = {class:atomicTransfertMethod, line:(i+1), column:1, inputContainerUseds:[],outputContainerUseds:[{experimentProperties:{}}]};
			$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.push(container);
			i++;
		});
		console.log($scope.experiment.value.atomicTransfertMethods);
	});


	//Init
	//init the input/output you want
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.experiment.outputGenerated = false;
	$scope.atomicTransfere = oneToMany($scope, "datatable", "none");
	$scope.inputContainers = [];
	$scope.rows = [];
	$scope.view = 1;
	$scope.allOutputContainersUsed = [];


	if($scope.experiment.editMode){
		//When the experiment already exist
		$scope.atomicTransfere.loadExperiment($scope.datatable, undefined, $scope.experimentToOutput);
		if(!angular.isUndefined(mainService.getBasket())){
			$scope.basket = mainService.getBasket().get();
			if($scope.basket.length > 0){
				$scope.edit();
			}
			angular.forEach($scope.basket, function(basket){
				$http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{supportCode:basket.code}})
				.success(function(data, status, headers, config) {
					$scope.clearMessages();
					if(data!=null){
						angular.forEach(data, function(container){
							$scope.inputContainers.push(container);
						});
						$scope.inputContainers = $scope.atomicTransfere.containersToContainerUseds($scope.inputContainers);
					}
				})
				.error(function(data, status, headers, config) {
					alert("error");
				});
			});
		}
	}else{
		//When the experiment is new
		$scope.atomicTransfere.newExperiment($scope.datatable);
	}

}]);
/* 
	Don't forget to import the service you need (manyToOne, oneToOne, oneToMany, oneToVoid)
	This template file show methods that must be implemented, you can add your owns
*/

angular.module('home').controller('TheNameOfTheCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','mainService','tabService', function($scope,$window, datatable, $http,lists,$parse,$q,$position,mainService,tabService) {
	var datatableConfig = {
			//The configuration of the datatable
	};
	
	//This function copy the experiment informations in the input
	$scope.$on('experimentToInput', function(e, atomicTransfertMethod) {
		//The default function is:
		//$scope.atomicTransfere.experimentToInput($scope.datatable);
	});
	
	//This function copy the input information to the experiment object
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		//The default function is:
		//$scope.atomicTransfere.inputToExperiment($scope.datatable);
	});
	
	//Call when the view need to delete the instrument propeties for the input
	$scope.$on('deleteInstrumentPropertiesInputs', function(e, header) {
	
	});

	//Call when the view need to add the instrument propeties for the input
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
	
	});

	
	//Call when the view need to add the experiment properties for the input
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
	
	});

	//Call when the view need to add the experiment properties for the output
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
	
	});

	//Call when the view need to add the instrument properties for the output
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
	
	});

	//Add the output informations to the view
	$scope.$on('addOutputColumns', function(e) {
		
	});

	
	//Create the level of the properties
	$scope.$on('addInstrumentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerIn")){
						var getter = $parse("datatable.displayResult["+i+"].inputInstrumentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].instrumentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].instrumentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	});

	//Create the level of the properties
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

	//Create the level of the properties
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

	//Create the level of the properties
	$scope.$on('addExperimentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerIn")){
						var getter = $parse("datatable.displayResult["+i+"].inputExperimentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].experimentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.experimentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	});

	//Create the level of the properties
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

	//Call when the view need to save
	$scope.$on('save', function(e, promises, func, endPromises) {	
		//push in the promises the promise of the save you need to do
		//promises.push($scope.datatable.save());
		
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
		$scope.atomicTransfere.experimentToOutput($scope.datatable);
	});


	//Call when we need to init the atomicTransfertMethod
	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
	});
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	/*When the drag and drop is needed*/
	$scope.drop = function(e, data, droppedItem, ngModel, alreadyInTheModel) {
		//capture the number of the atomicTransfertMethod
		if(!alreadyInTheModel){
			var array_regexp = /^experiment.value.atomicTransfertMethods\[([0-9]+)\].+/;
			var model = e.dataTransfer.getData('Model');
	
			var match = model.match(array_regexp);
			if(!match){
				$scope[model].splice($scope[model].indexOf(data), 1);	   
			}else{
				$scope.experiment.value.atomicTransfertMethods[match[1]].inputContainerUseds.splice($scope.experiment.value.atomicTransfertMethods[match[1]].inputContainerUseds.indexOf(data), 1);
			}
	
			$scope.atomicTransfere.reloadContainerDragNDrop(undefined, undefined, $scope.datatable);
			$scope.scanOpenedAll();
		}
	};

	$scope.beforeDropData = function(e, data, ngModel, alreadyInTheModel){
		if(!alreadyInTheModel){
			var array_regexp = /^experiment.value.atomicTransfertMethods\[([0-9]+)\].+/;
			var match = ngModel.match(array_regexp);
			if(match){
				$scope.rows[match[1]]= true;
				
				if(angular.isDefined($scope.experiment.value.atomicTransfertMethods)){
					$scope.scanOpenedAll();
				}
				
			}
		}
		
		return data;
	};

	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	

	//Init
	//init the input/output you want
	$scope.experiment.outputGenerated = true;
	$scope.atomicTransfere = /*the atomicTrasfertMethod you need*/;
	$scope.inputContainers = [];
	$scope.rows = [];
	$scope.view = 1;
	$scope.allOutputContainersUsed = [];


	if($scope.experiment.editMode){
		//When the experiment already exist
		$scope.atomicTransfere.loadExperiment(/*the input*/);
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
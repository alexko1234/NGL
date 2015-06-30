angular.module('home').controller('ManyToOneTubeCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','manyToOne','mainService','tabService', '$filter', function($scope,$window, datatable, $http,lists,$parse,$q,$position,manyToOne,mainService,tabService,$filter) {
	$scope.datatableConfig = {
			name:"FDR_Tube",
			columns:[   
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputCode",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":0,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.projectCodes"),
			        	 "property":"projectCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":0.2,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.sampleCodes"),
			        	 "property":"sampleCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":0.4,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.fromExperimentTypeCodes"),
			        	 "property":"fromExperimentTypeCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "filter":"codes:'type'",
			        	 "position":1,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.tags"),
			        	 "property":"inputTags",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":false,
			        	 "position":3,
			        	 "render":"<div list-resize='value.data.inputTags | unique' below-only-deploy>",
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputState.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":4,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":function(){
			        		 return Messages("containers.table.concentration") +" (nM)";},
			        	 "property":"inputConcentration",
			        	 "order":true,
			        	 "type":"number",
			        	 "edit":false,
			        	 "position":5,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":function(){
			        		 return Messages("containers.table.volume") +" (µl)";},
			        	 "property":"inputVolume.value",
			        	 "order":true,
			        	 "type":"number",
			        	 "edit":false,
			        	 "position":6,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.percentageInsidePool"),
			        	 "property":"percentage",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":8,
			        	 "extraHeaders":{0:"Inputs"}
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
				by:'ContainerInputCode'
			},
			remove:{
				active:false,
			},
			save:{
				active:true,
				withoutEdit: true,
				mode:'local',
				showButton:false
			},
			hide:{
				active:true
			},
			select:{
				active:true,
				showButton:true,
				isSelectAll:false
			},
			edit:{
				active: !$scope.doneAndRecorded,
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
		//$scope.atomicTransfere.experimentToOutput($scope.datatable);
	});	
	
	$scope.changeValueInTransfertTube = function(){
		$scope.atomicTransfere.experimentToInput($scope.datatable);
		$scope.atomicTransfere.experimentToOutput($scope.datatable);	
	};
	
	$scope.$on('deleteInstrumentPropertiesInputs', function(e, header) {
		for(var i=0;i<$scope.datatable.config.columns.length;i++){
			if($scope.datatable.config.columns[i].extraHeaders != undefined && $scope.datatable.config.columns[i].extraHeaders[1] == header){
				$scope.datatable.deleteColumn(i);
				i--;
			}
		}
	});
	
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"inputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
			column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
		}
		$scope.datatable.addColumn(6,column);
	});	
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {		
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"inputExperimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs"});
		column.defaultValues = data.defaultValue;		
		if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
			column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
		}		
		$scope.datatable.addColumn(6 ,column);
	});
	
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		if($scope.experiment.containerOutProperties.indexOf(data) == -1){
			var unit = "";
			if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
			$scope.experiment.containerOutProperties.push(data);
			var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"outputExperimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs"});
			column.defaultValues = data.defaultValue;
			if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
				column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
			}
			console.log(data.displayOrder);
			$scope.datatable.addColumn(10,column); 
		}
	});
	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"outputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
			column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
		}
		$scope.datatable.addColumn(-1,column);
	});
		
	
	$scope.addOutputColumns = function(){		
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(function(){return Messages("containers.table.concentration") + " (nM)"},"outputContainerUsed.concentration.value",false, true,true,"number",false,undefined,{"0":"Outputs"}));
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(function(){return Messages("containers.table.volume") + " (µL)"},"outputContainerUsed.volume.value",false, true,true,"number",false,undefined,{"0":"Outputs"}));
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(Messages("containers.table.code"),"outputContainerUsed.code",false, true,true,"text",false,undefined,{"0":"Outputs"}));		
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(Messages("containers.table.stateCode"),"outputContainerUsed.state.code | codes:'state'",false, true,true,"text",false,undefined,{"0":"Outputs"}));
	};	

	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.inputToExperiment($scope.datatable);
	});
	
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
			$scope.experiment.value.atomicTransfertMethods[0] = {class:atomicTransfertMethod, line:"1", column:"1", inputContainerUseds:[],outputContainerUseds:[{volume:{unit:"µL"},concentration:{unit:"nM"},experimentProperties:{}}]};
		/*	angular.forEach(containers, function(container){
				$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.push({code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state,
					locationOnContainerSupport:container.support});				
			}); */
			$scope.atomicTransfere.experimentToOutput($scope.datatable);
	};
	
	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
		$scope.init_atomicTransfert(containers, atomicTransfertMethod);
	});
	
	$scope.dragInProgress=function(value){
		$scope.dragIt=value;
	};		
	
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
		
		}
	};

	$scope.beforeDropData = function(e, data, ngModel, alreadyInTheModel){
		if(!alreadyInTheModel){
			var array_regexp = /^experiment.value.atomicTransfertMethods\[([0-9]+)\].+/;
			var match = ngModel.match(array_regexp);
			if(match){
				$scope.rows[match[1]]= true;	
				
			}
		}
		
		return data;
	};
	
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

	$scope.addExperimentOutputDatatableToScope = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerOut")){
						var getter = $parse("datatable.displayResult["+i+"].outputExperimentProperties."+data[j].code+".value");
						var k = $scope.datatable.displayResult[i].data.inputX;
						if($scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUseds[0].experimentProperties && $scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUseds[0].experimentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUseds[0].experimentProperties[data[j].code]);
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
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].experimentProperties == null){
						$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].experimentProperties = {};
					}

					if(!$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].experimentProperties[data[j].code]){
						$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].experimentProperties[data[j].code] = undefined;						
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

	$scope.$on('addInstrumentPropertiesOutputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerOut")){
						var getter = $parse("datatable.displayResult["+i+"].outputInstrumentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].instrumentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].instrumentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	});

	$scope.$on('save', function(e, promises, func, endPromises) {
		promises.push($scope.datatable.save());
		if(angular.isDefined($scope.datatable.getData()) && $scope.datatable.getData().length>0){			
			promises.push($scope.datatable.save());
		}
		$scope.atomicTransfere.outputToExperiment($scope.datatable);
		$scope.$emit('viewSaved', promises, func, endPromises);
	});
	
	$scope.refreshView = function(){
		$scope.atomicTransfere.reloadContainersDatatable($scope.datatable);
	};
	
	$scope.$on('refresh', function(e) {		
		$scope.refreshView();
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {
		$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].concentration = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[0].concentration;
		if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].volume === undefined){
			$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].volume = {};
		}
		$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].volume.unit ="µL" ;
		$scope.atomicTransfere.outputToExperiment($scope.datatable);
	});
	
	$scope.$on('experimentToOutput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToOutput($scope.datatable);
	});	
	
	$scope.$on('disableEditMode', function(){
		$scope.datatable.config.edit.active = false;		
	});
	
	$scope.$on('enableEditMode', function(){
		$scope.datatable.config.edit.active = true;		
	});
	
	$scope.deleteInput = function(container){		
		$scope.inputContainers.splice($scope.inputContainers.indexOf(container), 1);		
	};
	
	$scope.dropAllInputContainer = function(){
		var percentages = 0;
		var arrayLength = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.length;
		for(var i= 0; i<$scope.inputContainers.length;i++){
			$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[arrayLength + i] = $scope.inputContainers[i];
			if(angular.isDefined($scope.inputContainers[i].percentage) && $scope.inputContainers[i].percentage!=null){
				percentages+=parseFloat($scope.inputContainers[i].percentage);
			}			
		}
		
		if(percentages!=100){
			var arrayLength = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.length;
			for(var i= 0; i<$scope.inputContainers.length;i++){
				$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[i].percentage = Math.floor(10000/arrayLength)/100;
			}
			
		}
		
		$scope.atomicTransfere.reloadContainerDragNDrop(undefined, undefined, $scope.datatable);
		$scope.inputContainers = [];
	};
	
	$scope.dropOutAllInputContainer = function(){
		
		var i=0;
		var j=$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.length;
		while(j>0){			
			$scope.inputContainers[i] = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[0];
			$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.splice(0,1);
			j=$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.length;
			i++;
		}		
		$scope.atomicTransfere.reloadContainerDragNDrop(undefined, undefined, $scope.datatable);
		
	};	
	
	
	$scope.formatArray = function(array){
		var copyArray = angular.copy(array);
		var newArray = $filter('codes')(copyArray, 'type');
		var newString = newArray.toString();
		newString = newString.replace('[','');
		newString = newString.replace('"','');
		newString = newString.replace(']','');
		return newString;
		
	};
	
	$scope.$on('disableEditMode', function(){
		$scope.datatable.config.edit.active = false;
	});
	
	$scope.$on('enableEditMode', function(){
		$scope.datatable.config.edit.active = true;
	});
	
	
	//Init	
	
	$scope.inputContainers = [];
	$scope.inputContainerUsed = [];
	
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.experiment.outputGenerated = true;
	
	//$scope.atomicTransfere = manyToOne($scope, "datatable", "none");
	$scope.atomicTransfere = manyToOne($scope, "dragndrop", "datatable");
	
	
	if($scope.experiment.editMode){		
		$scope.atomicTransfere.loadExperiment($scope.datatable);
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
		$scope.atomicTransfere.newExperiment($scope.datatable);
		$scope.addOutputColumns();
	}

	
}]);
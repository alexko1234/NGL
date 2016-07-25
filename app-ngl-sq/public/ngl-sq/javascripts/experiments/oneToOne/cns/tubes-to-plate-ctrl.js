angular.module('home').controller('CNSTubesToPlateCtrl',['$scope' ,'$http','$parse', 'atmToSingleDatatable',
                                                       function($scope, $http,$parse,atmToSingleDatatable) {
	var datatableConfig = {
			name:$scope.experiment.typeCode.toUpperCase(),
			columns:[			  
					{
						"header" : Messages("containers.table.code"),
						"property" : "inputContainer.support.code",
						"order" : true,
						"edit" : false,
						"hide" : true,
						"type" : "text",
						"position" : 1,
						"extraHeaders" : {
							0 : Messages("experiments.inputs")
						}
					},
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":3,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
				     },
				     {
			        	 "header":Messages("containers.table.fromTransformationTypeCodes"),
			        	 "property":"inputContainer.fromTransformationTypeCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "filter":"unique | codes:'type'",
			        	 "type":"text",
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "position":4,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			        
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":7,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			 			// barcode plaque sortie == support Container used code... faut Used
			 			"header" : Messages("containers.table.support.name"),
			 			"property" : "outputContainerUsed.locationOnContainerSupport.code",
			 			"hide" : true,
			 			"type" : "text",
			 			"position" : 400,
			 			"extraHeaders" : {
			 				0 : Messages("experiments.outputs")
			 			}
			 		},
			 		{
			 			// Ligne
			 			"header" : Messages("containers.table.support.line"),
			 			"property" : "outputContainerUsed.locationOnContainerSupport.line",
			 			"edit" : true,
			 			"choiceInList":true,
			 			"possibleValues":[{"name":'A',"code":"A"},{"name":'B',"code":"B"},{"name":'C',"code":"C"},{"name":'D',"code":"D"},
			 			                  {"name":'E',"code":"E"},{"name":'F',"code":"F"},{"name":'G',"code":"G"},{"name":'H',"code":"H"}],
			 			"order" : true,
			 			"hide" : true,
			 			"type" : "text",
			 			"position" : 401,
			 			"extraHeaders" : {
			 				0 : Messages("experiments.outputs")
			 			}
			 		},
			 		{// colonne
			 			"header" : Messages("containers.table.support.column"),
			 			// astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel
			 			// forcer a numerique.=> type:number, property: *1
			 			"property" : "outputContainerUsed.locationOnContainerSupport.column",
			 			"edit" : true,
			 			"choiceInList":true,
			 			"possibleValues":[{"name":'1',"code":"1"},{"name":'2',"code":"2"},{"name":'3',"code":"3"},{"name":'4',"code":"4"},
			 			                  {"name":'5',"code":"5"},{"name":'6',"code":"6"},{"name":'7',"code":"7"},{"name":'8',"code":"8"},
			 			                  {"name":'9',"code":"9"},{"name":'10',"code":"10"},{"name":'11',"code":"11"},{"name":'12',"code":"13"}], 
			 			"order" : true,
			 			"hide" : true,
			 			"type" : "number",
			 			"position" : 402,
			 			"extraHeaders" : {
			 				0 : Messages("experiments.outputs")
			 			}
			 		},
			         {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":500,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.storageCode"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.storageCode",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":600,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
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
				by:'inputContainer.support.code'
			},
			remove:{
				active: ($scope.isEditModeAvailable() && $scope.isNewState()),
				showButton: ($scope.isEditModeAvailable() && $scope.isNewState()),
				mode:'local'
			},
			save:{
				active:true,
	        	withoutEdit: true,
	        	showButton:false,
	        	changeClass:false,
	        	mode:'local'
			},
			hide:{
				active:true
			},
			edit:{
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
				showButton: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
				byDefault:($scope.isCreationMode()),
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
			otherButtons: {
                active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
                template: '<button class="btn btn-default" ng-click="computeLineModeMode()" data-toggle="tooltip" ng-disabled="!isEditMode()" ng-if="experiment.instrument.outContainerSupportCategoryCode!==\'tube\'"><i class="fa fa-magic"></i><i class="fa fa-arrow-right"></i> </button>'
                		 +'<button class="btn btn-default" ng-click="computeColumnModeMode()" data-toggle="tooltip" ng-disabled="!isEditMode()" ng-if="experiment.instrument.outContainerSupportCategoryCode!==\'tube\'"><i class="fa fa-magic"></i><i class="fa fa-arrow-down"></i> </button>'
        			
            }
			
	};

	var updateATM = function(experiment){
		if(experiment.instrument.outContainerSupportCategoryCode!=="tube"){
			experiment.atomicTransfertMethods.forEach(function(atm){
				atm.line = atm.outputContainerUseds[0].locationOnContainerSupport.line;
				atm.column = atm.outputContainerUseds[0].locationOnContainerSupport.column;
			});
		}		
	}
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
		$scope.updatePropertyUnit($scope.experiment);
		updateATM($scope.experiment);
		$scope.$emit('childSaved', callbackFunction);
	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");		
		var dtConfig = $scope.atmService.data.getConfig();
		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP'));
		dtConfig.edit.byDefault = false;
		dtConfig.remove.active = ($scope.isEditModeAvailable() && $scope.isNewState());
		$scope.atmService.data.setConfig(dtConfig);
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('cancel', function(e) {
		console.log("call event cancel");
		$scope.atmService.data.cancel();
		
		if($scope.isCreationMode()){
			var dtConfig = $scope.atmService.data.getConfig();
			dtConfig.edit.byDefault = false;
			$scope.atmService.data.setConfig(dtConfig);
		}
		
	});
	
	$scope.$on('activeEditMode', function(e) {
		console.log("call event activeEditMode");
		$scope.atmService.data.selectAll(true);
		$scope.atmService.data.setEdit();
	});

	
	
	/**
	 * Compute A1, B1, C1, etc.
	 */
	$scope.computeColumnModeMode = function(){
		var wells = $scope.atmService.data.displayResult;
		var nbCol = 12;
		var nbLine = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
		var x = 0;
		for(var i = 0; i < nbCol ; i++){
			for(var j = 0; j < nbLine.length; j++){
				if(x < wells.length && x < 96){
					wells[x].data.outputContainerUsed.locationOnContainerSupport.line = nbLine[j]+'';
					wells[x].data.outputContainerUsed.locationOnContainerSupport.column = i+1;					
				}
				x++;
			}
		}		
	};
	
	/**
	 * Compute A1, A2, A3, etc.
	 */
	$scope.computeLineModeMode = function(){
		var wells = $scope.atmService.data.displayResult;
		var nbCol = 12;
		var nbLine = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
		var x = 0;
		for(var j = 0; j < nbLine.length; j++){
			for(var i = 0; i < nbCol ; i++){
				if(x < wells.length && x < 96){
					wells[x].data.outputContainerUsed.locationOnContainerSupport.line = nbLine[j]+'';
					wells[x].data.outputContainerUsed.locationOnContainerSupport.column = i+1;					
				}
				x++;
			}
		}		
	};
	
	$scope.$watch("experiment.instrument.outContainerSupportCategoryCode", function(){
		$scope.experiment.instrument.outContainerSupportCategoryCode = "96-well-plate";
	});
	
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	// defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod =  function(line, column){
		var getLine = function(line){
			if($scope.experiment.instrument.outContainerSupportCategoryCode 
					=== $scope.experiment.instrument.inContainerSupportCategoryCode){
				return line;
			}else if($scope.experiment.instrument.outContainerSupportCategoryCode !== "tube" 
				&& $scope.experiment.instrument.inContainerSupportCategoryCode === "tube") {
				return undefined;
			}
			
		}
		var getColumn=getLine;
		
		return {
			class:"OneToOne",
			line:getLine(line), 
			column:getColumn(column), 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL"
	}
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	if($scope.experiment.instrument.inContainerSupportCategoryCode === "tube"){
		$scope.messages.clear();
		$scope.atmService = atmService;
	}else{
		$scope.messages.setError(Messages('experiment.input.error.only-tubes'));					
	}
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		
		if(col.property === 'outputContainerUsed.concentration.value'){
			computeInputVolume(value.data);
			computeFinalVolume(value.data);
			computeBufferVolume(value.data);
		}else if(col.property === 'outputContainerUsed.volume.value'){
			computeInputVolume(value.data);
			computeBufferVolume(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.inputVolume.value'){
			computeFinalVolume(value.data);
			computeBufferVolume(value.data);
		}
		
	}
	//cOut * vOut / cIn : 
	//outputContainerUsed.concentration.value * outputContainerUsed.volume.value / inputContainerUsed.concentration.value
	var computeInputVolume = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.inputVolume.value");
		var inputVolume = getter(udtData);
		
		var compute = {
				outputConc : $parse("outputContainerUsed.concentration.value")(udtData),
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				outputVol : $parse("outputContainerUsed.volume.value")(udtData),			
				isReady:function(){
					return (this.outputConc && this.inputConc && this.outputVol);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("(outputConc * outputVol) / inputConc")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				inputVolume = Math.round(result*10)/10;				
			}else{
				inputVolume = undefined;
			}	
			getter.assign(udtData, inputVolume);
		}else{
			console.log("not ready to computeInputVolume");
		}
		
	}
	//cIn * inputVolume / cOut : 
	//inputContainerUsed.concentration.value * inputContainerUsed.experimentProperties.inputVolume.value / outputContainerUsed.concentration.value
	var computeFinalVolume = function(udtData){
		var getter = $parse("outputContainerUsed.volume.value");
		var outputVolume = getter(udtData);
		
		var compute = {
				outputConc : $parse("outputContainerUsed.concentration.value")(udtData),
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				inputVol : $parse("inputContainerUsed.experimentProperties.inputVolume.value")(udtData),			
				isReady:function(){
					return (this.outputConc && this.inputConc && this.inputVol);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("(inputConc * inputVol) / outputConc")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				outputVolume = Math.round(result*10)/10;				
			}else{
				outputVolume = undefined;
			}	
			getter.assign(udtData, outputVolume);
		}else{
			console.log("not ready to computeFinalVolume");
		}
		
	}
	//vOut - inputVolume
	//outputContainerUsed.volume.value - inputContainerUsed.experimentProperties.inputVolume.value
	var computeBufferVolume = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.bufferVolume.value");
		var bufferVolume = getter(udtData);
		
		var compute = {
				inputVol : $parse("inputContainerUsed.experimentProperties.inputVolume.value")(udtData),			
				outputVol : $parse("outputContainerUsed.volume.value")(udtData),			
				isReady:function(){
					return (this.outputVol && this.inputVol);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("(outputVol - inputVol)")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				bufferVolume = Math.round(result*10)/10;				
			}else{
				bufferVolume = undefined;
			}	
			getter.assign(udtData, bufferVolume);
		}else{
			console.log("not ready to computeBufferVolume");
		}
	}
	
	
	
}]);
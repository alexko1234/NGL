angular.module('home').controller('FragmentationCtrl',['$scope', '$parse', 'atmToSingleDatatable','lists','mainService',
                                                    function($scope, $parse, atmToSingleDatatable,lists,mainService){
                                                    
	var datatableConfig = {
					name: $scope.experiment.typeCode.toUpperCase(),
					columns:[   
					         /*
							 {
					        	 "header":Messages("containers.table.code"),
					        	 "property":"inputContainer.code",
					        	 "order":true,
								 "edit":false,
								 "hide":true,
					        	 "type":"text",
					        	 "mergeCells" : true,
					        	 "position":1,
					        	 "extraHeaders":{0:Messages("experiments.inputs")}
					         },	
					         */	         
					         {
					        	"header":Messages("containers.table.projectCodes"),
					 			"property": "inputContainer.projectCodes",
					 			"order":true,
					 			"hide":true,
					 			"type":"text",
					 			 "mergeCells" : true,
					 			"position":2,
					 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
					        	 "extraHeaders":{0:Messages("experiments.inputs")}
						     },
						     {
					        	"header":Messages("containers.table.sampleCodes"),
					 			"property": "inputContainer.sampleCodes",
					 			"order":true,
					 			"hide":true,
					 			"type":"text",
					 			 "mergeCells" : true,
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
					        	 "type":"text",
					        	 "mergeCells" : true,
					 			"render":"<div list-resize='cellValue | unique | codes:\"type\"' list-resize-min-size='3'>",
					        	 "position":4,
					        	 "extraHeaders":{0:Messages("experiments.inputs")}
					         },
					         {
					        	 "header":Messages("containers.table.concentration"),
					        	 "property":"inputContainer.concentration.value",
					        	 "order":true,
								 "edit":false,
								 "hide":true,
					        	 "type":"number",
					        	 "position":5,
					        	 "extraHeaders":{0:Messages("experiments.inputs")}
					         },
							 {
					        	 "header":Messages("containers.table.concentration.unit"),
					        	 "property":"inputContainer.concentration.unit",
					        	 "order":true,
								 "edit":false,
								 "hide":true,
					        	 "type":"text",
					        	 "position":5.1,
					        	 "extraHeaders":{0:Messages("experiments.inputs")}
					         },
					         {
					        	 "header":Messages("containers.table.volume") + " (µL)",
					        	 "property":"inputContainer.volume.value",
					        	 "order":true,
								 "edit":false,
								 "hide":true,
					        	 "type":"number",
					        	 "position":6,
					        	 "extraHeaders":{0:Messages("experiments.inputs")}
					         },
					         {
					        	 "header":Messages("containers.table.quantity") + " (ng)",
					        	 "property":"inputContainer.quantity.value",
					        	 "order":true,
								 "edit":false,
								 "hide":true,
					        	 "type":"number",
					        	 "position":7,
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
					        	 "header":Messages("containers.table.volume")+" (µL)",
					        	 "property":"outputContainerUsed.volume.value",
					        	 "editDirectives":" udt-change='updatePropertyFromUDT(value,col)' ",
					        	 "order":true,
								 "edit":true,
								 "hide":true,
								 "required":true,
								 "type":"number",
					        	 "position":51,
					        	 "extraHeaders":{0:Messages("experiments.outputs")}
					         },
					         /*
					         {
					        	 "header":Messages("containers.table.code"),
					        	 "property":"outputContainerUsed.code",
					        	 "order":true,
								 "edit":false,
								 "hide":true,
								 "type":"text",
					        	 "position":400,
					        	 "extraHeaders":{0:Messages("experiments.outputs")}
					         },
					         */
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
						active:true
					},
					remove:{
						active: ($scope.isEditModeAvailable() && $scope.isNewState()),
						showButton: ($scope.isEditModeAvailable() && $scope.isNewState()),
						mode:'local'
					},
					save:{
						active:true,
						withoutEdit: true,
						mode:'local',
						showButton:false,
						changeClass:false
					},
					hide:{
						active:true
					},
					mergeCells:{
						active:true 
					},			
					edit:{
						active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
						showButton: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
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
						number:1,
						dynamic:true,
					}

			};	
	
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
		$scope.$emit('childSaved', callbackFunction);
	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");		
		var dtConfig = $scope.atmService.data.getConfig();
		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
		dtConfig.edit.showButton = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
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
	
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		
		if(col.property === 'outputContainerUsed.volume.value'){
			computeRequiredVolume(value.data);
			computeInputVolume(value.data);
			computeInputQuantity(value.data);
			computeBufferVolume(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.requiredQuantity.value'){
			computeRequiredVolume(value.data);
			computeInputVolume(value.data);
			computeInputQuantity(value.data);
			computeBufferVolume(value.data);
		}
		
	}
	
	// requiredQunatity * concentrationIn
	var computeRequiredVolume = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.requiredVolume.value");
		var requiredVolume = getter(udtData);
		
		var compute = {
				requiredQt : $parse("inputContainerUsed.experimentProperties.requiredQuantity.value")(udtData),
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),			
				isReady:function(){
					return (this.requiredQt && this.inputConc);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("requiredQt / inputConc")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				requiredVolume = Math.round(result*10)/10;				
			}else{
				requiredVolume = undefined;
			}	
			getter.assign(udtData, requiredVolume);
		}else{
			console.log("not ready to computerequiredVolume");
		}
		
	}
	
	
	//if requiredVol > inputVol then outputVol else requiredVol
	var computeInputVolume = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.inputVolume.value");
		var inputVolume = getter(udtData);
		
		var compute = {
				requiredVol : $parse("inputContainerUsed.experimentProperties.requiredVolume.value")(udtData),
				outputVol : $parse("outputContainerUsed.volume.value")(udtData),
				inputVol : $parse("inputContainerUsed.volume.value")(udtData),			
				isReady:function(){
					return (this.requiredVol && this.outputVol && this.inputVol);
				}
			};
		
		if(compute.isReady()){
			
			var result;
			if( compute.requiredVol> compute.inputVol){
				result=compute.outputVol;
			} else {
				result=compute.requiredVol;
			}
			
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
	
	
	// inputVol * concIn 
	var computeInputQuantity = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.frgInputQuantity.value");
		var frgInputQuantity = getter(udtData);
		
		var compute = {
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				inputVol : $parse("inputContainerUsed.experimentProperties.inputVolume.value")(udtData),			
				isReady:function(){
					return (this.inputConc && this.inputVol);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("(inputConc * inputVol)")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				frgInputQuantity = Math.round(result*10)/10;				
			}else{
				frgInputQuantity = undefined;
			}	
			getter.assign(udtData, frgInputQuantity);
		}else{
			console.log("not ready to computeFrgInputQuantity");
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
	
	
	
	//Init		
	if($scope.experiment.instrument.inContainerSupportCategoryCode!=="tube"){
		datatableConfig.columns.push({
			"header" : Messages("containers.table.supportCode"),
			"property" : "inputContainer.support.code",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 1,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		datatableConfig.columns.push({
			"header" : Messages("containers.table.support.line"),
			"property" : "inputContainer.support.line",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 1.1,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		datatableConfig.columns.push({
			"header" : Messages("containers.table.support.column"),
			"property" : "inputContainer.support.column*1",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "number",
			"position" : 1.2,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});

	} else {
		datatableConfig.columns.push({
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
		});
		datatableConfig.order.by = 'inputContainer.sampleCodes';
		
	}
	
	if($scope.experiment.instrument.outContainerSupportCategoryCode !== "tube") {
		datatableConfig.columns.push({
			// barcode plaque sortie == support Container used code... faut Used
			"header" : Messages("containers.table.support.name"),
			"property" : "outputContainerUsed.locationOnContainerSupport.code",
			"hide" : true,
			"type" : "text",
			"position" : 400,
			"extraHeaders" : {
				0 : Messages("experiments.outputs")
			}
		});
		datatableConfig.columns.push({
			// Ligne
			"header" : Messages("containers.table.support.line"),
			"property" : "outputContainerUsed.locationOnContainerSupport.line",
			"edit" : false,
			"order" : true,
			"hide" : true,
			"type" : "text",
			"position" : 401,
			"extraHeaders" : {
				0 : Messages("experiments.outputs")
			}
		});
		datatableConfig.columns.push({// colonne
			"header" : Messages("containers.table.support.column"),
			// astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel
			// forcer a numerique.=> type:number, property: *1
			"property" : "outputContainerUsed.locationOnContainerSupport.column",
			"edit" : false,
			"order" : true,
			"hide" : true,
			"type" : "number",
			"position" : 402,
			"extraHeaders" : {
				0 : Messages("experiments.outputs")
			}
		});

	} else {
		datatableConfig.columns.push({
			"header" : Messages("containers.table.code"),
			"property" : "outputContainerUsed.code",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 400,
			"extraHeaders" : {
				0 : Messages("experiments.outputs")
			}
		});
	}
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(line, column){
		var getLine = function(line){
			if($scope.experiment.instrument.outContainerSupportCategoryCode === 'tube'){
				return "1";
			}else{
				return line;
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

	if($scope.experiment.instrument.inContainerSupportCategoryCode === $scope.experiment.instrument.outContainerSupportCategoryCode){
		$scope.messages.clear();
		$scope.atmService = atmService;
	}else{
		$scope.messages.setError(Messages('experiments.input.error.must-be-same-out'));					
	}
}]);
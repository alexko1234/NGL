angular.module('home').controller('NormalisationCtrl',['$scope' ,'$http','$parse', 'atmToSingleDatatable',
                                                       function($scope, $http,$parse,atmToSingleDatatable) {
	var datatableConfig = {
			name:$scope.experiment.typeCode.toUpperCase(),
			columns:[			  
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
			         /*
			         {
			        	"header":Messages("containers.table.tags"),
			 			"property": "inputContainer.contents",
			 			"filter": "getArray:'properties.tag.value'",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":4,
			 			"render":"<div list-resize='cellValue | unique' ' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
					*/			 
					 {
			        	 "header" : Messages("containers.table.concentration"),
			 			 "property": "inputContainerUsed.concentration.value",
			 			 "order":true,
			 			 "editDirectives":" udt-change='updatePropertyFromUDT(value,col)' ",
			 			"tdClass":"valuationService.valuationCriteriaClass(value.data, experiment.status.criteriaCode, col.property)",			        	
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":5,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			        
					 {
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"inputContainerUsed.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":5.1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			        
			         {
			        	 "header":function(){return Messages("containers.table.volume") + " (µL)"},
			        	 "property":"inputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			        /*
			         {
				 			"header":Messages("containers.table.size"),
				 			"property": "inputContainerUsed.size.value",
				 			"order":false,
				 			"hide":true,
				 			"type":"text",
				 			"position":6.5,
				 			"extraHeaders":{0:Messages("experiments.inputs")}			 						 			
				 	 },
				 	 */
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
			        	 "header":Messages("containers.table.concentration"),
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "editDirectives":" udt-change='updatePropertyFromUDT(value,col)' ",
			        	 "tdClass":"valuationService.valuationCriteriaClass(value.data, experiment.status.criteriaCode, col.property)",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":50,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.concentration.unit") ,
			        	 "property":"outputContainerUsed.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "defaultValues":"nM",
			        	 "position":51,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.volume")+ " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 //utilisation de la directive utd-change car elle capture les modifications du header puis déclenche la function calculVolume 
			        	 // Si ng-change seul l'evenement utilisateur est capturé, la valeur de la cellule est modifiée mais le calcul non executé
			        	 "editDirectives":" udt-change='updatePropertyFromUDT(value,col)' ",
			        	 "tdClass":"valuationService.valuationCriteriaClass(value.data, experiment.status.criteriaCode, col.property)",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":52,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.quantity"),
			        	 "property":'outputContainerUsed.quantity.value',
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":53,
			        	 "watch":true,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.quantity.unit"),
			        	 "property":"outputContainerUsed.quantity.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":54,
			        	 "watch":true,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
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
	        	showButton:false,
	        	changeClass:false,
	        	mode:'local'
			},
			hide:{
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
				number:2,
				dynamic:true,
			},
			otherButtons: {
                active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
                complex:true,
                template:  ''
                	+$scope.plateUtils.templates.buttonLineMode()
                	+$scope.plateUtils.templates.buttonColumnMode()     
                	+$scope.plateUtils.templates.buttonCopyPosition()
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
		checkControlConc($scope.experiment);
		updateATM($scope.experiment);
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
		
		datatableConfig.columns.push({
			"header" : Messages("containers.table.workName"),
			"property" : "inputContainer.properties.workName.value",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 1.1,
			"extraHeaders" : {0 : Messages("experiments.inputs")}
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
		});
		datatableConfig.columns.push({// colonne
			"header" : Messages("containers.table.support.column"),
			// astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel
			// forcer a numerique.=> type:number, property: *1
			"property" : "outputContainerUsed.locationOnContainerSupport.column",
			"edit" : true,
			"choiceInList":true,
			"possibleValues":[{"name":'1',"code":"1"},{"name":'2',"code":"2"},{"name":'3',"code":"3"},{"name":'4',"code":"4"},
			                  {"name":'5',"code":"5"},{"name":'6',"code":"6"},{"name":'7',"code":"7"},{"name":'8',"code":"8"},
			                  {"name":'9',"code":"9"},{"name":'10',"code":"10"},{"name":'11',"code":"11"},{"name":'12',"code":"12"}], 
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
	// defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod =  function(line, column){
		var getLine = function(line){
			if($scope.experiment.instrument.outContainerSupportCategoryCode 
					=== $scope.experiment.instrument.inContainerSupportCategoryCode){
				return line;
			}else if($scope.experiment.instrument.outContainerSupportCategoryCode !== "tube" 
				&& $scope.experiment.instrument.inContainerSupportCategoryCode === "tube") {
				return undefined;
			}else if($scope.experiment.instrument.outContainerSupportCategoryCode === "tube"){
				return "1";
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
	};
	atmService.defaultOutputValue = {
			size : {copyInputContainer:true}
	};
	
	atmService.convertInputPropertiesToDatatableColumn = function(property, pName){
        var column = atmService.$commonATM.convertTypePropertyToDatatableColumn(property,"inputContainerUsed."+pName+".",{"0":Messages("experiments.inputs")});
        if(property.code=="maximumConcentration"){
        //    column.property="(inputContainerUsed.maximumConcentration.value|number).concat(' '+inputContainerUsed.concentration.unit)";
        }else{
            
        console.log("test "+property.code);
        }
        return column;
    };
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	$scope.atmService = atmService;
	
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);

		if(col.property === 'inputContainerUsed.experimentProperties.computeMode.value'){
			resetAll(value.data);
		}else{
			var computeMode = $parse("data.inputContainerUsed.experimentProperties.computeMode.value")(value);


			if(computeMode == 'fixeCfVf'){
				console.log("Volume final fixe")
				if(col.property === 'outputContainerUsed.volume.value' || col.property === 'outputContainerUsed.concentration.value'){
					computeInputVolumeWithConc(value.data);
					computeBufferVolume(value.data);
				}			
			}else if(computeMode == 'fixeCfVi'){
				console.log("volume a engager fixe");
				if(col.property === 'inputContainerUsed.experimentProperties.inputVolume.value' 
					|| col.property === 'outputContainerUsed.concentration.value'){
					computeFinalVolumeWithConc(value.data);
					computeBufferVolume(value.data);
				}			
			}	

			if(col.property === 'outputContainerUsed.volume.value'){
				refreshMaxConc(value.data);
			}
			
			computeOutputQuantity(value.data);
		}
	}
	
	var resetAll = function(udtData){
		$parse("inputContainerUsed.experimentProperties.inputVolume.value").assign(udtData,null);
		$parse("inputContainerUsed.experimentProperties.bufferVolume.value").assign(udtData,null);
		$parse("outputContainerUsed.volume.value").assign(udtData,null);
		$parse("outputContainerUsed.concentration.value").assign(udtData,null);
		
	};
	
/*Pas utilisée ??
	var computeConcentration = function(udtData){
		var getter = $parse("outputContainerUsed.concentration.value");
		var oldOutputConc = getter(udtData);
		var newOutputConc;
		var compute = {
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				inputVol : $parse("inputContainerUsed.experimentProperties.inputVolume.value")(udtData),
				outputVol : $parse("outputContainerUsed.volume.value")(udtData),			
				isReady:function(){
					return (this.inputConc && this.inputVol && this.outputVol);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("(inputConc * inputVol) / outputVol")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				newOutputConc = Math.round(result*10)/10;					
			}else{
				newOutputConc = undefined;
			}	
			if(newOutputConc !== oldOutputConc){
				getter.assign(udtData, newOutputConc);
			}
		}else{
			getter.assign(udtData, null);
			console.log("not ready to computeConcentration");
		}
		
	};
	*/
	
	var refreshMaxConc = function(udtData){
		
		var getter = $parse("inputContainerUsed.experimentProperties.maximumConcentration.value");
		var maxConcValue = getter(udtData);
		var getter2 = $parse("inputContainerUsed.experimentProperties.maximumConcentration.unit");
		var maxConcUnit = getter2(udtData);
		
		
		var compute = {
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				inputConcUnit : $parse("inputContainerUsed.concentration.unit")(udtData),
				inputVol : $parse("inputContainerUsed.volume.value")(udtData),
				outputVol : $parse("outputContainerUsed.volume.value")(udtData),			
				isReady:function(){
					return (this.inputConc && this.inputVol && this.outputVol );
				}
		};

		if(compute.isReady()){
			var result = $parse("(inputConc * inputVol) / outputVol")(compute);
			console.log("refreshMaxConcfunction result"+result);
			if(angular.isNumber(result) && !isNaN(result)){
				maxConcValue = Math.round(result*10)/10;	
				maxConcUnit = (compute.inputConcUnit === 'nM')?'fmol':'ng';
				
			}else{
				maxConcValue =undefined;
				maxConcUnit= undefined;
			}	
				getter.assign(udtData, maxConcValue);
				getter2.assign(udtData, maxConcUnit);
				
		}
	};
	
	//cOut * vOut / cIn : 
	//outputContainerUsed.concentration.value * outputContainerUsed.volume.value / inputContainerUsed.concentration.value
	var computeInputVolumeWithConc = function(udtData){
		
		var getter = $parse("inputContainerUsed.experimentProperties.inputVolume.value");
		var oldInputVolume = getter(udtData);
		var getter2 = $parse("outputContainerUsed.concentration.value");
		var oldConcentration = getter2(udtData);
		
		var newInputVolume;
		var compute = {
				outputConc : $parse("outputContainerUsed.concentration.value")(udtData),
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				outputVol : $parse("outputContainerUsed.volume.value")(udtData),			
				isReady:function(){
					return (this.outputConc && this.inputConc && this.outputVol);
				}
		};

		var compute2 = {
				outputVol : $parse("outputContainerUsed.volume.value")(udtData),			
				isReady:function(){
					return (this.outputVol);
				}
		};

		if(compute.isReady()){
			var result = $parse("(outputConc * outputVol) / inputConc")(compute);
			console.log("computeInputVolumeWithConc result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				newInputVolume = Math.round(result*10)/10;					
			}else{
				newInputVolume = undefined;
			}	
			if(newInputVolume !== oldInputVolume){
				getter.assign(udtData, newInputVolume);
			}
		}else if (! $parse("inputContainerUsed.concentration.value")(udtData)){
			if(compute2.isReady()){
				newInputVolume=$parse("outputVol")(compute2);
				
			}else{
				newInputVolume=undefined;		
				console.log("not ready to computeInputVolume");
			}
			getter.assign(udtData, newInputVolume);	
			if(oldConcentration){
				$scope.messages.setError(Messages('experiments.output.error.must-have-inputConc'));		
			}
			getter2.assign(udtData,undefined);
		}else{
			console.log("not ready to computeInputVolume");
			getter.assign(udtData, null);
		}
	};
	
	/*Pas utilisée??
	var computeInputVolumeWithOtherVol = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.inputVolume.value");
		var oldInputVolume = getter(udtData);
		var newInputVolume;
		var compute = {
				bufferVol : $parse("inputContainerUsed.experimentProperties.bufferVolume.value")(udtData),
				outputVol : $parse("outputContainerUsed.volume.value")(udtData),			
				isReady:function(){
					return (this.outputVol && this.bufferVol);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("outputVol - bufferVol")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				newInputVolume = Math.round(result*10)/10;					
			}else{
				newInputVolume = undefined;
			}	
			if(newInputVolume !== oldInputVolume){
				getter.assign(udtData, newInputVolume);
			}
		}else{
			getter.assign(udtData, null);
			console.log("not ready to computeInputVolume");
		}	
	}
	*/
	
	//cIn * inputVolume / cOut : 
	//inputContainerUsed.concentration.value * inputContainerUsed.experimentProperties.inputVolume.value / outputContainerUsed.concentration.value
	var computeFinalVolumeWithConc = function(udtData){
		var getter = $parse("outputContainerUsed.volume.value");
		var oldOutputVolume = getter(udtData);
		var getter2 = $parse("outputContainerUsed.concentration.value");
		var oldConcentration = getter2(udtData);

		var newOutputVolume;

		var compute = {
				outputConc : $parse("outputContainerUsed.concentration.value")(udtData),
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				inputVol : $parse("inputContainerUsed.experimentProperties.inputVolume.value")(udtData),			
				isReady:function(){
					return (this.outputConc && this.inputConc && this.inputVol);
				}
		};

		var compute2 = {
				inputVol : $parse("inputContainerUsed.experimentProperties.inputVolume.value")(udtData),			
				isReady:function(){
					return (this.inputVol);
				}
		};

		if(compute.isReady()){
			var result = $parse("(inputConc * inputVol) / outputConc")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				newOutputVolume = Math.round(result*10)/10;				
			}else{
				newOutputVolume = undefined;
			}	
			if(newOutputVolume !== oldOutputVolume){
				getter.assign(udtData, newOutputVolume);
			}
		}else if (! $parse("inputContainerUsed.concentration.value")(udtData)){
			if(compute2.isReady()){
				newOutputVolume = $parse("inputVol")(compute);
				getter.assign(udtData, newOutputVolume);
			}
			
			if(oldConcentration){
				$scope.messages.setError(Messages('experiments.output.error.must-have-inputConc'));		
			}
			getter2.assign(udtData,undefined);
			
		}else{ 
			getter.assign(udtData, null);
			console.log("not ready to computeFinalVolume");
		}

	};
	
	var computeFinalVolumeWithOtherVol = function(udtData){
		var getter = $parse("outputContainerUsed.volume.value");
		var oldOutputVolume = getter(udtData);
		var newOutputVolume;
		
		var compute = {
				bufferVol : $parse("inputContainerUsed.experimentProperties.bufferVolume.value")(udtData),
				inputVol : $parse("inputContainerUsed.experimentProperties.inputVolume.value")(udtData),			
				isReady:function(){
					return (this.bufferVol && this.inputVol);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("inputVol + bufferVol")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				newOutputVolume = Math.round(result*10)/10;				
			}else{
				newOutputVolume = undefined;
			}	
			if(newOutputVolume !== oldOutputVolume){
				getter.assign(udtData, newOutputVolume);
			}
		}else{
			getter.assign(udtData, null);
			console.log("not ready to computeFinalVolume");
		}
		
	};
	//vOut - inputVolume
	//outputContainerUsed.volume.value - inputContainerUsed.experimentProperties.inputVolume.value
	var computeBufferVolume = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.bufferVolume.value");
		var oldBufferVolume = getter(udtData);
		var newBufferVolume;
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
				newBufferVolume = Math.round(result*10)/10;				
			}else{
				newBufferVolume = undefined;
			}	
			if(newBufferVolume !== oldBufferVolume){
				getter.assign(udtData, newBufferVolume);
			}
		}else{
			getter.assign(udtData, null);
			console.log("not ready to computeBufferVolume");
		}
	};
	
	
	var computeOutputQuantity = function(udtData){
		var getter = $parse("outputContainerUsed.quantity");
		var outputQuantity = getter(udtData);
		
		
		var compute = {
				outputConc : $parse("outputContainerUsed.concentration.value")(udtData),
				outputConcUnit : $parse("outputContainerUsed.concentration.unit")(udtData),
				outputVol : $parse("outputContainerUsed.volume.value")(udtData),			
				isReady:function(){
					return (this.outputConc && this.outputConcUnit && this.outputVol);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("outputConc * outputVol")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				outputQuantity.value = Math.round(result*10)/10;	
				outputQuantity.unit = (compute.outputConcUnit === 'nM')?'fmol':'ng';
			}else{
				outputQuantity = undefined;
			}	
			getter.assign(udtData, outputQuantity);
		}else{
			outputQuantity.value = null;
			outputQuantity.unit = null;
			getter.assign(udtData, outputQuantity);
			console.log("not ready to computeOutputQuantity");
		}
		
	}
	
	var generateSampleSheetNormalisation = function(){
		$scope.fileUtils.generateSampleSheet({"type":"normalisation"});
	};
	var generateSampleSheetNormalisationPostPCR = function(){
		$scope.fileUtils.generateSampleSheet({"type":"normalisation-post-pcr"});
	};
	
	if($scope.experiment.instrument.outContainerSupportCategoryCode !== "tube" 
		|| $scope.experiment.instrument.inContainerSupportCategoryCode !== "tube"){
		
		$scope.setAdditionnalButtons([{
			isDisabled : function(){return $scope.isNewState();} ,
			isShow:function(){return !$scope.isNewState();},
			click:generateSampleSheetNormalisation,
			label:Messages("experiments.sampleSheet")+" normalisation"
		},{
			isDisabled : function(){return $scope.isNewState();} ,
			isShow:function(){return !$scope.isNewState();},
			click:generateSampleSheetNormalisationPostPCR,
			label:Messages("experiments.sampleSheet")+" normalisation post PCR"
		}]);
	}
	
	
	var checkControlConc = function(experiment){

		experiment.atomicTransfertMethods.forEach(function(atm){
			//Si CONC en IN est null alors conc out doit etre null			
			if (atm.inputContainerUseds[0].concentration == undefined){				
				atm.outputContainerUseds[0].concentration=undefined;
				
				atm.inputContainerUseds[0].experimentProperties.bufferVolume.value =0;

				if (atm.outputContainerUseds[0].volume && atm.outputContainerUseds[0].volume.value  ){
					atm.inputContainerUseds[0].experimentProperties.inputVolume.value = atm.outputContainerUseds[0].volume.value;
				}else {
					atm.outputContainerUseds[0].volume.value = atm.inputContainerUseds[0].experimentProperties.inputVolume.value; 
				}

			}

		});		
	}
}]);
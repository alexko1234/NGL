angular.module('home').controller('FragmentationCtrl',['$scope', '$parse', 'atmToSingleDatatable','lists','mainService',
                                                    function($scope, $parse, atmToSingleDatatable,lists,mainService){
                                                    
	var datatableConfig = {
					name: $scope.experiment.typeCode.toUpperCase(),
					columns:[   
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
					         {
					        	"header":Messages("containers.table.projectCodes"),
					 			"property": "inputContainer.projectCodes",
					 			"order":false,
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
					 			"order":false,
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
					        	 "header":Messages("containers.table.concentration"),
					        	 "property":"outputContainerUsed.concentration.value",
					        	 "editDirectives":' udt-change="calculVolumes(value)" ',
					        	 "order":true,
								 "edit":true,
								 "hide":true,
					        	 "type":"number",
					        	 "defaultValues":10,
					        	 "position":50,
					        	 "extraHeaders":{0:Messages("experiments.outputs")}
					         },
					         {
					        	 "header":Messages("containers.table.volume")+" (µL)",
					        	 "property":"outputContainerUsed.volume.value",
					        	 "order":true,
								 "edit":true,
								 "hide":true,
								 "type":"number",
					        	 "position":16,
					        	 "extraHeaders":{0:Messages("experiments.outputs")}
					         },
					         
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
						by:'inputContainer.code'
					},
					remove:{
						active:false,
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
	
	
	
	
	//Init		

	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(){
		return {
			class:"OneToOne",
			line:"1", 
			column:"1", 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL",
			concentration : "nM"
	}
	
	var calculVolumeFromValue=function(value){
		console.log("call calculVolumeFromValue");
		if(value.outputContainerUsed.volume!=null && value.outputContainerUsed.volume.value!=null && value.outputContainerUsed.concentration.value!=null){
			if(value.inputContainerUsed.concentration.unit===value.outputContainerUsed.concentration.unit){				
				var requiredVolume=value.outputContainerUsed.concentration.value*value.outputContainerUsed.volume.value/value.inputContainerUsed.concentration.value;
				requiredVolume = Math.round(requiredVolume*10)/10
				
				var bufferVolume = value.outputContainerUsed.volume.value-requiredVolume;
				bufferVolume = Math.round(bufferVolume*10)/10
				
				if(value.inputContainerUsed.experimentProperties===undefined || value.inputContainerUsed.experimentProperties!==null){
					value.inputContainerUsed.experimentProperties={};
				}
				value.inputContainerUsed.experimentProperties["requiredVolume"]={"_type":"single","value":requiredVolume,"unit":value.outputContainerUsed.concentration.unit};
				value.inputContainerUsed.experimentProperties["bufferVolume"]={"_type":"single","value":bufferVolume,"unit":value.outputContainerUsed.volume.unit};
				
			}else if(value.inputContainerUsed.concentration.unit==="ng/ul") {
				var requiredVolume=value.outputContainerUsed.concentration.value*value.outputContainerUsed.volume.value/(value.inputContainerUsed.concentration.value*1000000/(660*value.inputContainerUsed.size.value));
				requiredVolume = Math.round(requiredVolume*10)/10
				
				var bufferVolume = value.outputContainerUsed.volume.value-requiredVolume;
				bufferVolume = Math.round(bufferVolume*10)/10
				
				if(value.inputContainerUsed.experimentProperties===undefined || value.inputContainerUsed.experimentProperties!==null){
					value.inputContainerUsed.experimentProperties={};
				}				
				value.inputContainerUsed.experimentProperties["requiredVolume"]={"_type":"single","value":requiredVolume,"unit":value.outputContainerUsed.concentration.unit};
				value.inputContainerUsed.experimentProperties["bufferVolume"]={"_type":"single","value":bufferVolume,
						 "unit":value.outputContainerUsed.volume.unit};
			}
	    }
	}
		
	$scope.calculVolumes=function(value){
		if(value!=null & value !=undefined){
			calculVolumeFromValue(value.data);
	   }
	};
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);

	$scope.atmService = atmService;
}]);
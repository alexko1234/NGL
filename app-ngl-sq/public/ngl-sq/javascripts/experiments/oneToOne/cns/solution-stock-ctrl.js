angular.module('home').controller('SolutionStockCtrl',['$scope' ,'$http','atmToSingleDatatable',
                                                       function($scope, $http,atmToSingleDatatable) {
	var datatableConfig = {
			name:"FDR_Tube",
			columns:[			  
					
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
			        	 "type":"text",
			 			"render":"<div list-resize='cellValue | unique | codes:\"type\"' list-resize-min-size='3'>",
			        	 "position":4,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
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
			        	 "header":function(){return Messages("containers.table.volume") + " (µL)"},
			        	 "property":"inputContainer.volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
				 			"header":Messages("containers.table.size"),
				 			"property": "inputContainer.size.value",
				 			"order":false,
				 			"hide":true,
				 			"type":"text",
				 			"position":6.5,
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
			        	 "editDirectives":' udt-change="calculVolumes(value)" ',
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":52,
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
				by:'code'
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
	
	//Init	
	if($scope.experiment.instrument.inContainerSupportCategoryCode!=="tube"){
		datatableConfig.columns.push( {
       	 "header":Messages("containers.table.supportCode"),
       	 "property":"inputContainer.support.code",
       	 "order":true,
       	 "edit":false,
		  "hide":true,
       	 "type":"text",
       	 "position":1,
       	 "extraHeaders":{0:Messages("experiments.inputs")}
        });
		datatableConfig.columns.push( {
	       	 "header":Messages("containers.table.support.line"),
	       	 "property":"inputContainer.support.line",
	       	 "order":true,
	       	 "edit":false,
			  "hide":true,
	       	 "type":"text",
	       	 "position":1.1,
	       	 "extraHeaders":{0:Messages("experiments.inputs")}
	        });
		datatableConfig.columns.push( {
	       	 "header":Messages("containers.table.support.column"),
	       	 "property":"inputContainer.support.column*1",
	       	 "order":true,
	       	 "edit":false,
			  "hide":true,
	       	 "type":"text",
	       	 "position":1.2,
	       	 "extraHeaders":{0:Messages("experiments.inputs")}
	        });
	
	}else {			
			datatableConfig.columns.push( {
					        	 "header":Messages("containers.table.code"),
					        	 "property":"inputContainer.code",
					        	 "order":true,
								 "edit":false,
								 "hide":true,
					        	 "type":"text",
					        	 "position":1,
					        	 "extraHeaders":{0:Messages("experiments.inputs")}
					         });
	}
	
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod =  function(line, column){
		return {
			class:"OneToOne",
			line:line, 
			column:column, 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL",
			concentration : "nM"
	}
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	$scope.atmService = atmService;
	

	var generateSampleSheet = function(){
		$http.post(jsRoutes.controllers.instruments.io.IO.generateFile($scope.experiment.code).url,{})
		.success(function(data, status, headers, config) {
			var header = headers("Content-disposition");
			var filepath = header.split("filename=")[1];
			
			var filename = filepath.split(/\/|\\/);
			filename = filename[filename.length-1];
			if(data!=null){
				$scope.messages.clazz="alert alert-success";
				$scope.messages.text=Messages('experiments.msg.generateSampleSheet.success')+" : "+filepath;
				$scope.messages.showDetails = false;
				$scope.messages.open();	
				
				var blob = new Blob([data], {type: "text/plain;charset=utf-8"});    					
				saveAs(blob, filename);
			}
		})
		.error(function(data, status, headers, config) {
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages('experiments.msg.generateSampleSheet.error');
			$scope.messages.showDetails = false;
			$scope.messages.open();				
		});
	};

	$scope.setAdditionnalButtons([{
		isDisabled : function(){return $scope.isNewState();} ,
		isShow:function(){return !$scope.isNewState();},
		click:generateSampleSheet,
		label:Messages("experiments.sampleSheet")
	}]);
	
	
}]);
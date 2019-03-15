angular.module('home').controller('DilutionCtrlGET',['$scope' ,'$http','$parse', 'atmToSingleDatatable',
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
				        	"header":Messages("property_definition.Nom_echantillon_collaborateur"),
				 			"property": "inputContainer.contents",
				 			"filter": "getArray:'referenceCollab'| unique",
//				 			"filter": "getArray:'properties.Nom_echantillon_collaborateur.value'| unique",
				 			"order":true,
				 			"hide":true,
				 			"type":"text",
				 			"position":3,
				 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				        	 "extraHeaders":{0:Messages("experiments.inputs")}
					  },
					 {
			        	 "header" : function(){return Messages("Concentration fournie") + " (ng)"},
			 			 "property": "inputContainerUsed.concentration.value",
			 			 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":5,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
				 			"header":Messages("containers.table.size"),
				 			"property": "inputContainerUsed.size.value",
				 			"order":false,
				 			"hide":true,
				 			"type":"text",
				 			"position":6.5,
				 			"extraHeaders":{0:Messages("experiments.inputs")}			 						 			
				 	 },	
			         {
			        	 "header":Messages("Concentration estimée") + " (ng)",
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
						 "watch":true,
			        	 "type":"number",
			        	 "format":"3",
			        	 "position":50,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.storageCode"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.code",
			        	 "order":true,
						 "edit":true,
						 "hide":false,
			        	 "type":"text",
			        	 "position":400,
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
            }
			
	};

	var updateATM = function(experiment){
		console.log("outputContainerUseds : " + JSON.stringify(experiment.atomicTransfertMethods));
		experiment.atomicTransfertMethods.forEach(function(atm){
			if(experiment.instrument.outContainerSupportCategoryCode!=="tube"){
					atm.line = atm.outputContainerUseds[0].locationOnContainerSupport.line;
					atm.column = atm.outputContainerUseds[0].locationOnContainerSupport.column;
			
			}
		});			
	}
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
		updateConcentration($scope.experiment);
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
		datatableConfig.order.by = 'inputContainer.sampleCodes';
	}
	


	if($scope.experiment.instrument.outContainerSupportCategoryCode !== "tube") {

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
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	$scope.atmService = atmService;
	
	
	/**
	 * Update concentration of output
	 */
	var updateConcentration = function(experiment){
		if($scope.experiment.state.code == "N"){
			console.log("updateConcentration");
			for(var j = 0 ; j < experiment.atomicTransfertMethods.length && experiment.atomicTransfertMethods != null; j++){
				var atm = experiment.atomicTransfertMethods[j];
				var concentration = undefined;
				var inputContainerUsed = atm.inputContainerUseds[0];
				var outputContainerUsed = atm.outputContainerUseds[0];
				if (inputContainerUsed.concentration!=null && outputContainerUsed.experimentProperties!=null && outputContainerUsed.experimentProperties.dilution!=null && outputContainerUsed.experimentProperties.dilution.value.match("^([0-9]+[\/][0-9]+)$")!=null){
					var x = parseInt(outputContainerUsed.experimentProperties.dilution.value.split("/")[0]);
					var y = parseInt(outputContainerUsed.experimentProperties.dilution.value.split("/")[1]);
					concentration = inputContainerUsed.concentration.value/y*x;
					atm.outputContainerUseds[0].concentration.value = concentration;
					atm.outputContainerUseds[0].concentration.unit = "ng";
				}
			}//for
		}
	};
		

	
	
	//add buttons
	
//	var generateSampleSheetNormalisation = function(){
//		$scope.fileUtils.generateSampleSheet({"type":"normalisation"});
//	};
//	var generateSampleSheetNormalisationPostPCR = function(){
//		$scope.fileUtils.generateSampleSheet({"type":"normalisation-post-pcr"});
//	};
//	
//	if($scope.experiment.instrument.outContainerSupportCategoryCode !== "tube" 
//		|| $scope.experiment.instrument.inContainerSupportCategoryCode !== "tube"){
//		
//		$scope.setAdditionnalButtons([{
//			isDisabled : function(){return $scope.isNewState();} ,
//			isShow:function(){return !$scope.isNewState();},
//			click:generateSampleSheetNormalisation,
//			label:Messages("experiments.sampleSheet")+" normalisation"
//		},{
//			isDisabled : function(){return $scope.isNewState();} ,
//			isShow:function(){return !$scope.isNewState();},
//			click:generateSampleSheetNormalisationPostPCR,
//			label:Messages("experiments.sampleSheet")+" normalisation post PCR"
//		}]);
//	}
	
	
}]);
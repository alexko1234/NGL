angular.module('home').controller('CNSPlateToTubesCtrl',['$scope' ,'$http','$parse', 'atmToSingleDatatable',
                                                       function($scope, $http,$parse,atmToSingleDatatable) {
	var datatableConfig = {
			name:$scope.experiment.typeCode.toUpperCase(),
			columns:[			  
			 		{
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
					},
					{
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
					},
					{
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
					},
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
			        	 "header" : Messages("containers.table.concentration"),
			 			 "property": "inputContainerUsed.concentration.value",
			 			 "order":true,
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
			         {
			        	 "header" : Messages("containers.table.quantity"),
			 			 "property": "inputContainerUsed.quantity.value",
			 			 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6.1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			        
					 {
			        	 "header":Messages("containers.table.quantity.unit"),
			        	 "property":"inputContainerUsed.quantity.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":6.2,
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
			        	 "order":true,
						 "edit":false,
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
			        	 "position":51,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.volume")+ " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":52,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.quantity"),
			        	 "property":"outputContainerUsed.quantity.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":53,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.quantity.unit") ,
			        	 "property":"outputContainerUsed.quantity.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":54,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
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
                template: 
                	'<button class="btn btn-default" ng-click="copyVolumeInToOut()" data-toggle="tooltip" title="'+Messages("experiments.button.plate.copyVolume")+'"  ng-disabled="!isEditMode()"><i class="fa fa-files-o" aria-hidden="true"></i> Volume </button>'                	                	
            }
			
	};

	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
		 computeQtty($scope.experiment);
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
		 computeQtty($scope.experiment);
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
	
	$scope.copyVolumeInToOut = function(){
		var data = $scope.atmService.data.displayResult;		
		data.forEach(function(value){
			value.data.outputContainerUsed.volume = value.data.inputContainerUsed.volume;
		})		
	};
	
	 var computeQtty = function(experiment){
	    	experiment.atomicTransfertMethods.forEach(function(atm){

	    		var getter = $parse("outputContainerUseds[0].quantity");
	    		var outputQuantity = getter(atm);


	    		var compute = {
	    				inputConc : $parse("inputContainerUseds[0].concentration")(atm),
	    				outputConc : $parse("outputContainerUseds[0].concentration")(atm),
	    				outputVol : $parse("outputContainerUseds[0].volume")(atm),

	    				isReady:function(){
	    					return (this.inputConc && this.outputConc && this.outputVol);
	    				}
	    		};
	    		if(compute.isReady()){
	    			if($parse("(outputConc.unit ===  inputConc.unit)")(compute)){
	    				var result = $parse("outputVol.value  * outputConc.value ")(compute);
	    				console.log("result = "+result);
	    				if(angular.isNumber(result) && !isNaN(result)){
	    					outputQuantity.value = Math.round(result*10)/10;   
	    					if($parse("outputConc.unit")(compute) == "nM"){
	    						outputQuantity.unit = "fmol";	
	    					}else if ($parse("outputConc.unit")(compute) == "ng/µl"){
	    						outputQuantity.unit = "ng";
	    					}else{
	    						console.log("Unité "+outputQuantity.unit+" non gérée!");
	    					}
	    				}else{
	    					outputQuantity.value = undefined;
	    					outputQuantity.unit = undefined;
	    				}    
	    				getter.assign(atm, outputQuantity);
	    			}else{
	    				console.log("not ready to compute outputQuantity"+outputQtty.value);
	    				outputQuantity.value = undefined;
	    				outputQuantity.unit = undefined;
	    				getter.assign(atm,outputQuantity);    
	    			}

	    		}else{
	    			outputQuantity.value = undefined;
					outputQuantity.unit = undefined;
					getter.assign(atm,outputQuantity);
					console.log("not ready to compute outputQuantity");
	    		}
	    	});
	    }
	 
	$scope.$watch("experiment.instrument.outContainerSupportCategoryCode", function(){
		$scope.experiment.instrument.outContainerSupportCategoryCode = "tube";
	});
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	// defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod =  function(line, column){
		
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
			volume : "µL"
	};
	atmService.defaultOutputValue = {
			concentration : {copyInputContainer:true},
			size : {copyInputContainer:true}
	};
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	if($scope.experiment.instrument.inContainerSupportCategoryCode === "96-well-plate"){
		$scope.messages.clear();
		$scope.atmService = atmService;
	}else{
		$scope.messages.setError(Messages('experiments.input.error.only-plates'));					
	}
	
}]);
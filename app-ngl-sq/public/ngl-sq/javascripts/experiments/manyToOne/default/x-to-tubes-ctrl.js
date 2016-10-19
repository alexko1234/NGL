angular.module('home').controller('XToTubesCtrl',['$scope', '$parse', '$filter','atmToDragNDrop2',
                                                               function($scope, $parse, $filter, atmToDragNDrop) {
	
	// NGL-1055: name explicite pour fichier CSV exporté
	// NGL-1055: mettre getArray et codes:'' dans filter et pas dans render
	var datatableConfig = {		
			name: $scope.experiment.typeCode.toUpperCase(),
			columns:[   					
					
			         {
			        	 "header":Messages("containers.table.supportCategoryCode"),
			        	 "property":"inputContainer.support.categoryCode",
			        	 "filter":"codes:'container_support_cat'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },	
			         {
			        	 "header":Messages("containers.table.fromTransformationTypeCodes"),
			        	 "property":"inputContainer.fromTransformationTypeCodes",
			        	 "filter":"unique | codes:'type'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			 			 "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "position":3,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainer.projectCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":4,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			            "extraHeaders":{0:Messages("experiments.inputs")}
				     },
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":5,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0:Messages("experiments.inputs")}
				     },
			         {
				 		"header":Messages("containers.table.libProcessType"),
				 		"property": "inputContainer.contents",
				 		"filter": "getArray:'properties.libProcessTypeCode.value'| unique",
				 		"order":false,
				 		"hide":true,
				 		"type":"text",
				 		"position":6,
				 		"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				 		"extraHeaders": {0:Messages("experiments.inputs")}	 						 			
				 	},
			        {
				        "header":Messages("containers.table.tags"),
				 		"property": "inputContainer.contents",
				 		"filter": "getArray:'properties.tag.value'| unique",
				 		"order":true,
				 		"hide":true,
				 		"type":"text",
				 		"position":7,
				 		"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				        "extraHeaders":{0:Messages("experiments.inputs")}
				     },		
				     {
			        	 "header":Messages("containers.table.volume") + " (µL)",
			        	 "property":"inputContainer.volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":8,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
					 {
			        	 "header":Messages("containers.table.concentration"),
			        	 "property":"inputContainer.concentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":9,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"inputContainer.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":10,
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
			        	 "position":11,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.percentageInsidePool"),
			        	 "property":"inputContainerUsed.percentage",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":12,
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
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"outputContainerUsed.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"text",
			        	 "position":50.5,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.volume")+" (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"number",
			        	 "position":51,
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
				by:"inputContainer.support.code"
			},
			remove:{
				active:false,
			},
			save:{
				active:true,
				withoutEdit: true,
				mode:'local',
				changeClass:false,
				showButton:false
			},
			hide:{
				active:true
			},
			mergeCells:{
	        	active:true 
	        },
			select:{
				active:false,
				showButton:true,
				isSelectAll:false
			},
			edit:{
				active: false,
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
	
	
	$scope.drop = function(e, data, ngModel, alreadyInTheModel, fromModel) {
		//capture the number of the atomicTransfertMethod
		if(!alreadyInTheModel){
			$scope.atmService.data.updateDatatable();
		
		}
	};
	
	// FDS: renommer getOutputContainers car donne une liste de containers et pas de containerSupports, !! contient des doublons 
	$scope.getOutputContainers = function(){
		var outputContainers = [];
		if($scope.experiment.atomicTransfertMethods){
			$scope.experiment.atomicTransfertMethods.forEach(function(atm){
				this.push(atm.outputContainerUseds[0]);
				
			}, outputContainers);
		}
		return outputContainers;
	}
	
	// FDS : liste de containerSupports sans doublons
	$scope.getDistinctOutputContainerSupports = function(){
		var outputContainerSupports = [];
		if($scope.experiment.atomicTransfertMethods){
			var unique = {};
			$scope.experiment.atomicTransfertMethods.forEach(function(atm){
				
				if (!unique[atm.outputContainerUseds[0].locationOnContainerSupport]) {
				    this.push(atm.outputContainerUseds[0].locationOnContainerSupport);
				    unique[atm.outputContainerUseds[0].locationOnContainerSupport] = true;
				}
			}, outputContainerSupports);
		}
		return outputContainerSupports;
	}
	
	
	
	$scope.getInputContainerSupports = function(){
		var inputContainerSupports = [];
		if($scope.experiment.atomicTransfertMethods){
			inputContainerSupports = $scope.experiment.inputContainerSupportCodes;
		}
		return inputContainerSupports;
	}
	
	
	$scope.isEditMode = function(){
		return ($scope.$parent.isEditMode() && $scope.isNewState());
	};
	
	$scope.$on('save', function(e, callbackFunction) {
		console.log("call event save on x-to-tubes");		
		$scope.atmService.viewToExperiment($scope.experiment, false);
		$scope.$emit('childSaved', callbackFunction);
	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh on x-to-tubes");		
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
	
	
	$scope.$on('cancel', function(e) {
		console.log("call event cancel");
		
	});
	
	$scope.$on('activeEditMode', function(e) {
		console.log("call event activeEditMode");
	});
	
	$scope.inputContainerProperties = $filter('filter')($scope.experimentType.propertiesDefinitions, 'ContainerIn');
	$scope.outputContainerProperties = $filter('filter')($scope.experimentType.propertiesDefinitions, 'ContainerOut');
	
	
	var atmService = atmToDragNDrop($scope, 0, datatableConfig);
	
	atmService.inputContainerSupportCategoryCode = $scope.experiment.instrument.inContainerSupportCategoryCode;
	atmService.outputContainerSupportCategoryCode = $scope.experiment.instrument.outContainerSupportCategoryCode;
	
	
	// 19/10/2016 version de Guillaume pour gerer les cas tubes ou 96-well-plate
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
			class:"ManyToOne",
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
	
	
	$scope.atmService = atmService;
	
}]);

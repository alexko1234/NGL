angular.module('home').controller('GETXToTubesCtrl',['$scope', '$parse', '$filter','atmToDragNDrop2','mainService',
                                                               function($scope, $parse, $filter, atmToDragNDrop,mainService) {
	console.log("in GETXToTubesCtrl");
	// NGL-1055: name explicite pour fichier CSV exporté
	// NGL-1055: mettre getArray et codes:'' dans filter et pas dans render
	var datatableConfig = {		
			name: $scope.experiment.typeCode.toUpperCase(),
			columns:[
//			         {
//			        	 "header":Messages("containers.table.supportCategoryCode"),
//			        	 "property":"inputContainer.support.categoryCode",
//			        	 "filter":"codes:'container_support_cat'",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//			        	 "type":"text",
//			        	 "position":2,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },	
//			         {
//			        	 "header":Messages("containers.table.fromTransformationTypeCodes"),
//			        	 "property":"inputContainer.fromTransformationTypeCodes",
//			        	 "filter":"unique | codes:'type'",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//			        	 "type":"text",
//			 			 "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
//			        	 "position":3,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },
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
//				     {
//			        	"header":Messages("containers.table.sampleCodes"),
//			 			"property": "inputContainer.sampleCodes",
//			 			"order":true,
//			 			"hide":true,
//			 			"type":"text",
//			 			"position":5,
//			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
//			        	"extraHeaders":{0:Messages("experiments.inputs")}
//				     },                                          
				     {
				    	 "header":Messages("property_definition.Nom_echantillon_collaborateur"),
				    	 "property": "inputContainer.contents",
				    	 "filter": "getArray:'referenceCollab'| unique",
//                   	"filter": "getArray:'properties.Nom_echantillon_collaborateur.value'| unique",
				    	 "order":true,
				    	 "hide":true,
				    	 "type":"text",
				    	 "position":5,
				    	 "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				    	 "extraHeaders":{0:Messages("experiments.inputs")}
				     },
//			         {
//				 		"header":Messages("containers.table.libProcessType"),
//				 		"property": "inputContainer.contents",
//				 		"filter": "getArray:'properties.libProcessTypeCode.value'| unique",
//				 		"order":false,
//				 		"hide":true,
//				 		"type":"text",
//				 		"position":6,
//				 		"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
//				 		"extraHeaders": {0:Messages("experiments.inputs")}	 						 			
//				 	},
			        {
				        "header":Messages("containers.table.tags"),
				 		"property": "inputContainer.contents",
				 		"filter": "getArray:'properties.tag.value'| unique",
				 		"order":true,
				 		"hide":true,
				 		"type":"text",
				 		"position":6,
				 		"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				        "extraHeaders":{0:Messages("experiments.inputs")}
				     },		
				     {
			        	 "header":Messages("containers.table.volume") + " (µL)",
			        	 "property":"inputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":7,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
					 {
			        	 "header":Messages("containers.table.concentration"),
			        	 "property":"inputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":8,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"inputContainerUsed.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":8.5,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },	        
//			         {
//			        	 "header":Messages("containers.table.state.code"),
//			        	 "property":"inputContainer.state.code",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//			        	 "type":"text",
//						 "filter":"codes:'state'",
//			        	 "position":11,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },
			         {
			        	 "header":Messages("containers.table.percentageInsidePool"),
			        	 "property":"inputContainerUsed.percentage",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":9,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         //out
			         {
			        	 "header":Messages("containers.table.volume")+" (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"number",
			        	 "position":50,
			        	 "mergeCells" : true,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.concentration"),
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"number",
			        	 "position":51,
			        	 "mergeCells" : true,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"outputContainerUsed.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"text",
			        	 "position":51.5,
			        	 "mergeCells" : true,
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
			        	 "mergeCells" : true,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
				     }
//			         {
//			        	 "header":Messages("containers.table.code"),
//			        	 "property":"outputContainerUsed.code",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//						 "type":"text",
//			        	 "position":400,
//			        	 "extraHeaders":{0:Messages("experiments.outputs")}
//			         },
//			         {
//			        	 "header":Messages("containers.table.stateCode"),
//			        	 "property":"outputContainer.state.code | codes:'state'",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//						 "type":"text",
//			        	 "position":500,
//			        	 "extraHeaders":{0:Messages("experiments.outputs")}
//			         }
			         ],
			compact:true,
			pagination:{
				active:true
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
				active:true,
				showButton:true,
				isSelectAll:true
			},
			edit:{
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
				byDefault : true,
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
	
	/*
	 * si containers in sont des tubes ou mixte -> afficher - container.code
	 * si suelement plaque -> affichage - support.code + ligne + colonne
	 */
	var tmp = [];
	if(!$scope.isCreationMode()){
		tmp = $scope.$eval("atomicTransfertMethods|flatArray:'inputContainerUseds'|getArray:'locationOnContainerSupport.categoryCode'|unique",$scope.experiment);			
	}else{
		tmp = $scope.$eval("getBasket().get()|getArray:'support.categoryCode'|unique",mainService);
	}
	var supportCategoryCode = undefined;
	if(tmp.length === 1){
		supportCategoryCode=tmp[0];
		$scope.supportCategoryCode = supportCategoryCode;
	}else{
		supportCategoryCode="mixte";
		$scope.supportCategoryCode = "tube";
	}
		
	console.log("supportCategoryCode : "+supportCategoryCode);
	
	
	if(supportCategoryCode === "96-well-plate"){
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
			"property" : "inputContainer.code",
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
	
	
	//affichage des colonnes size si pas null
	if($scope.experiment.atomicTransfertMethods){
		sizeIN = false;
		sizeOUT = false;
		$scope.experiment.atomicTransfertMethods.forEach(function(atm){
			console.log();
//			atm.inputContainers.forEach(function(inputContainer){
//				if(null === inputContainer.size  || undefined === inputContainer.size || undefined === inputContainer.size.value ||  null === inputContainer.size.value){
//					sizeIN = true;
//				}
//			});
//			atm.outputContainers.forEach(function(outputContainer){
//				if(null === outputContainer.size  || undefined === outputContainer.size || undefined === outputContainer.size.value ||  null === outputContainer.size.value){
//					sizeOUT = true;
//				}
//			});
//			
		});
			
		if(sizeIN){
			datatableConfig.columns.push({
				"header" : Messages("containers.table.size"),
				"property": "inputContainerUsed.size.value",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "number",
				"position" :7.5,
				"extraHeaders" : {
					0 : Messages("experiments.inputs")
				}
			});
		}
		if(sizeOUT){			
			datatableConfig.columns.push({
				"header" : Messages("containers.table.size"),
				"property": "outputContainerUsed.size.value",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "number",
				"position" :50.5,
				"extraHeaders" : {0 : Messages("experiments.outputs")}
			});
		}
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
				
				if (!unique[atm.outputContainerUseds[0].locationOnContainerSupport.code]) {
				    this.push(atm.outputContainerUseds[0].locationOnContainerSupport);
				    unique[atm.outputContainerUseds[0].locationOnContainerSupport.code] = true;
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
	
	// fdsantos 28/09/2017 :NGL-1601 ne pas sauvegarder une experience vide.
	//  !!! ATTENTION COMMUN CNS/CNG !!!
	$scope.$on('save', function(e, callbackFunction) {
		console.log("call event save on x-to-tubes");
		console.log("inputContainerUsed 1 : " + JSON.stringify($scope.experiment.atomicTransfertMethods[0]));
		if($scope.atmService.data.atm.length === 0){
			console.log("call event save on x-to-tubes if");
			$scope.$emit('childSavedError', callbackFunction);
			
		    $scope.messages.clazz = "alert alert-danger";
		    $scope.messages.text = Messages("experiments.msg.nocontainer.save.error");
		    $scope.messages.showDetails = false;
			$scope.messages.open();   
	
		} else {	
//			console.log("inputContainerUsed 1 : " + JSON.stringify($scope.experiment.atomicTransfertMethods[0].inputContainerUseds[0].experimentProperties));
			$scope.atmService.viewToExperiment($scope.experiment, true);
//			console.log("inputContainerUsed 2 : " + JSON.stringify($scope.experiment.atomicTransfertMethods[0].inputContainerUseds[0].experimentProperties));
			$scope.$emit('childSaved', callbackFunction);
//			console.log("inputContainerUsed 3 : " + JSON.stringify($scope.experiment.atomicTransfertMethods[0].inputContainerUseds[0].experimentProperties));
	    } 
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
	// 27/10/2016 bug vu par JG: au CNS pool generique tube=> tube : line et column sont undefined
	atmService.newAtomicTransfertMethod =  function(line, column){
		var getLine = function(line){
			//TEST correction FDS
			if ($scope.experiment.instrument.outContainerSupportCategoryCode === "tube"){
				return 1; // ligne et colonne=1 pour un tube
			} else {
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
			volume : "µL",				
	}
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
	
}]);

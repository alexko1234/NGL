angular.module('home').controller('ManyToOneTubeCtrl',['$scope', '$parse', 'atmToDragNDrop',
                                                               function($scope, $parse, atmToDragNDrop) {
	
	

	var datatableConfig = {
			name:"FDR_Tube",
			columns:[   
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"Inputs"}
			         },		         
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainer.projectCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":2,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":3,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
				     {
			        	 "header":Messages("containers.table.fromExperimentTypeCodes"),
			        	 "property":"inputContainer.fromExperimentTypeCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			 			"render":"<div list-resize='cellValue | unique | codes:\"type\"' list-resize-min-size='3'>",
			        	 "position":4,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         					 
					 {
			        	 "header":Messages("containers.table.concentration"),
			        	 "property":"inputContainer.mesuredConcentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":5,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"inputContainer.mesuredConcentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"string",
			        	 "position":5.5,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.volume") + " (µL)",
			        	 "property":"inputContainer.mesuredVolume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6,
			        	 "extraHeaders":{0:"Inputs"}
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
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.percentageInsidePool"),
			        	 "property":"inputContainerUsed.percentage",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":10,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.concentration"),
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "mergeCells" : true,
			        	 "type":"number",
			        	 "defaultValues":10,
			        	 "position":50,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"outputContainerUsed.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "mergeCells" : true,
			        	 "type":"string",
			        	 "defaultValues":10,
			        	 "position":50.5,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.volume")+" (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "mergeCells" : true,
			        	 "type":"number",
			        	 "position":51,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "mergeCells" : true,
			        	 "type":"text",
			        	 "position":400,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "mergeCells" : true,
			        	 "type":"text",
			        	 "position":500,
			        	 "extraHeaders":{0:"Outputs"}
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
			},
			otherButton:{
				active:true,
				template:'<button class="btn btn btn-info" ng-click="newPurif()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doPurif" title="'+Messages("experiments.addpurif")+'">Messages("experiments.addpurif")</button><button class="btn btn btn-info" ng-click="newQc()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doQc" title="Messages("experiments.addqc")">Messages("experiments.addqc")</button>'
			}
	};	
	
	$scope.drop = function(e, data, droppedItem, ngModel, alreadyInTheModel) {
		//capture the number of the atomicTransfertMethod
		if(!alreadyInTheModel){
			var model = e.dataTransfer.getData('Model');
			var getter = $parse(model);
			var setter = getter.assign
			var inputContainerUseds = getter($scope); 
			inputContainerUseds.splice(inputContainerUseds.indexOf(data), 1);
			
			$scope.atmService.data.updateDatatable();
		
		}
	};
	
	$scope.$on('save', function(e, promises, func, endPromises) {	
		console.log("call event save");		
		$scope.atmService.viewToExperiment($scope.experiment);
		$scope.updateConcentration($scope.experiment);
		$scope.$emit('viewSaved', promises, func, endPromises);
	});
	
	$scope.updateConcentration = function(experiment){
		var concentration = undefined;
		var unit = undefined;
		var isSame = true;
		for(var i=0;i<experiment.value.atomicTransfertMethods[0].inputContainerUseds.length;i++){
			if(concentration === undefined && unit === undefined){
				concentration = experiment.value.atomicTransfertMethods[0].inputContainerUseds[i].concentration.value;
				unit = experiment.value.atomicTransfertMethods[0].inputContainerUseds[i].concentration.unit;
			}else{
				if(concentration !== experiment.value.atomicTransfertMethods[0].inputContainerUseds[i].concentration.value 
						|| unit !== experiment.value.atomicTransfertMethods[0].inputContainerUseds[i].concentration.unit){
					isSame = false;
					break;
				}
			}
		}
		
		if(isSame){
			experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].concentration = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[0].concentration;
			
		}
		
		
	};
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");		
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
	
	var atmService = atmToDragNDrop($scope, 1, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(){
		return {
			class:"ManyToOne",
			line:"1", //TODO only exact for oneToOne of type  tube to tube but not for plate to plate
			column:"1", 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};		
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL"			
	}
	atmService.experimentToView($scope.experiment);
	
	$scope.atmService = atmService;
	
}]);

angular.module('home').controller('TubesToFlowcellCtrl',['$scope', '$parse', 'atmToDragNDrop',
                                                               function($scope, $parse, atmToDragNDrop) {
	
	

	var datatableConfig = {
			name:"FDR_Tube",
			columns:[  
 
					 {
			        	 "header":Messages("containers.table.support.number"),
			        	 "property":"atomicTransfertMethod.line",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":0,
			        	 "extraHeaders":{0:"Inputs"}
			         },	
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
			        	 "type":"text",
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
			        	 "header":Messages("containers.table.percentage"),
			        	 "property":"inputContainerUsed.percentage",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":40,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.concentration"),
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"number",
			        	 "position":50,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"outputContainerUsed.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"text",
			        	"position":50.5,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.volume")+" (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
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
				by:'atomicTransfertMethod.line'
			},
			remove:{
				active:false,
			},
			save:{
				active:true,
				withoutEdit: true,
				mode:'local',
				showButton:false,
				callback:function(datatable){
					copyFlowcellCodeToDT(datatable);
				}
			},
			hide:{
				active:true
			},
			mergeCells:{
	        	active:true 
	        },
			
			edit:{
				active: !$scope.doneAndRecorded,
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
	
	$scope.dragInProgress=function(value){
		$scope.dragIt=value;
	};
	
	$scope.getDroppableClass=function(){
		if($scope.dragIt){
			return "dropZoneHover";
		}else{
			return "";
		}
	}
	
	$scope.$on('save', function(e, promises, func, endPromises) {	
		console.log("call event save");		
		$scope.atmService.viewToExperiment($scope.experiment);
		$scope.updateConcentration($scope.experiment);
		$scope.$emit('viewSaved', promises, func, endPromises);
	});
	
	
	var copyFlowcellCodeToDT = function(datatable){
		
		var dataMain = datatable.getData();
		//copy flowcell code to output code
		var codeFlowcell = $parse("instrumentProperties.containerSupportCode.value")($scope.experiment.value);
		if(null != codeFlowcell && undefined != codeFlowcell){
			for(var i = 0; i < dataMain.length; i++){
				var atm = dataMain[i].atomicTransfertMethod;
				var containerCode = codeFlowcell;
				if($scope.rows.length > 1){ //other than flowcell 1
					containerCode = codeFlowcell+"_"+atm.line;
				}
				$parse('outputContainerUsed.code').assign(dataMain[i],containerCode);
				$parse('outputContainerUsed.locationOnContainerSupport.code').assign(dataMain[i],codeFlowcell);
			}				
			datatable.setData(dataMain);
		}
		
	}
	
	/**
	 * Update concentration of output if all input are same value and unit
	 */
	$scope.updateConcentration = function(experiment){
		
		for(var j = 0 ; j < experiment.value.atomicTransfertMethods.length; j++){
			var atm = experiment.value.atomicTransfertMethods[j];
			var concentration = undefined;
			var unit = undefined;
			var isSame = true;
			for(var i=0;i < atm.inputContainerUseds.length;i++){
				if(concentration === undefined && unit === undefined){
					concentration = atm.inputContainerUseds[i].concentration.value;
					unit = atm.inputContainerUseds[i].concentration.unit;
				}else{
					if(concentration !== atm.inputContainerUseds[i].concentration.value 
							|| unit !== atm.inputContainerUseds[i].concentration.unit){
						isSame = false;
						break;
					}
				}
			}
			
			if(isSame){
				atm.outputContainerUseds[0].concentration = atm.inputContainerUseds[0].concentration;				
			}
			
		}		
	};
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");
		
		var dtConfig = $scope.atmService.data.$atmToSingleDatatable.data.getConfig();
		dtConfig.edit.active = !$scope.doneAndRecorded;
		$scope.atmService.data.$atmToSingleDatatable.data.setConfig(dtConfig);
		
		
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
	//To display sample and tag in one cell
	$scope.getSampleAndTags = function(container){
		var sampleCodeAndTags = [];
		angular.forEach(container.contents, function(content){
			if(content.properties.tag != undefined && content.sampleCode != undefined){
				sampleCodeAndTags.push(content.sampleCode+" / "+content.properties.tag.value);
			}
		});
		return sampleCodeAndTags;
	};
	
	$scope.getDisplayMode = function(atm, rowIndex){
		
		if(atm.inputContainerUseds.length === 0){
			return "empty";
		}else if(atm.inputContainerUseds.length > 0 && $scope.rows[rowIndex]){
			return "open";
		}else{
			return "compact";
		}		
	};
	
	$scope.isAllOpen = true;
	if($scope.experiment.editMode){
		$scope.isAllOpen = false;
	}
	
	//TODO used container_support_category in future
	//init number of lane
	var cscCode = $parse('experiment.value.instrument.outContainerSupportCategoryCode')($scope);
	$scope.rows = [];
	var laneCount = 0;
	if(cscCode !== undefined){
		laneCount = Number(cscCode.split("-",2)[1]);
		$scope.rows = new Array(laneCount);
		for(var i = 0; i < laneCount; i++){
			$scope.rows[i] = $scope.isAllOpen;
		}
		
	}
	
	$scope.hideRowAll = function(){
		for (var i=0; i<$scope.rows.length;i++){	
			$scope.rows[i] = false;
		}	    
		$scope.isAllOpen = false;	    
	};

	$scope.showRowAll = function(){
		for (var i=0; i<$scope.rows.length;i++){	
			$scope.rows[i] = true;
		}	    
		$scope.isAllOpen = true;
	};
	
	$scope.toggleRow = function(rowIndex){
		$scope.rows[rowIndex] = !$scope.rows[rowIndex];
	};
	
	
	//init global ContainerOut Properties outside datatable
	$scope.outputContainerProperties = [];
	$scope.outputContainerValues = {};
	angular.forEach($scope.experiment.experimentProperties.inputs, function(property){
		if($scope.getLevel(property.levels, "ContainerOut")){
			this.push(property);														
		}		
	}, $scope.outputContainerProperties);
	
	$scope.updateAllOutputContainerProperty = function(property){
		var value = $scope.outputContainerValues[property.code];
		var setter = $parse("outputContainerUseds[0].experimentProperties."+property.code+".value").assign;
		for(var i = 0 ; i < $scope.atmService.data.atm.length ; i++){
			var atm = $scope.atmService.data.atm[i];
			if(atm.inputContainerUseds.length > 0){
				setter(atm, value);
			}			
		}
		$scope.changeValueOnFlowcellDesign();
	};
	
	$scope.changeValueOnFlowcellDesign = function(){
		$scope.atmService.data.updateDatatable();
	};
	
	
	//init atmService
	var atmService = atmToDragNDrop($scope, laneCount, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(line){
		return {
			class:"ManyToOne",
			line:line, 
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

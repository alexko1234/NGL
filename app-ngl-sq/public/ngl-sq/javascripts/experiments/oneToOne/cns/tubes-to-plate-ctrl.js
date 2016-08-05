angular.module('home').controller('CNSTubesToPlateCtrl',['$scope' ,'$http','$parse', '$filter','atmToSingleDatatable',
                                                       function($scope, $http,$parse,$filter,atmToSingleDatatable) {
	var datatableConfig = {
			name:$scope.experiment.typeCode.toUpperCase(),
			columns:[			  
					{
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
			 			 "property": "inputContainer.concentration.value",
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
			        	 "header" : Messages("containers.table.quantity"),
			 			 "property": "inputContainer.quantity.value",
			 			 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6.1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			        
					 {
			        	 "header":Messages("containers.table.quantity.unit"),
			        	 "property":"inputContainer.quantity.unit",
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
			 			// barcode plaque sortie == support Container used code... faut Used
			 			"header" : Messages("containers.table.support.name"),
			 			"property" : "outputContainerUsed.locationOnContainerSupport.code",
			 			"hide" : true,
			 			"type" : "text",
			 			"position" : 400,
			 			"extraHeaders" : {
			 				0 : Messages("experiments.outputs")
			 			}
			 		},
			 		{
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
			 		},
			 		{// colonne
			 			"header" : Messages("containers.table.support.column"),
			 			// astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel
			 			// forcer a numerique.=> type:number, property: *1
			 			"property" : "outputContainerUsed.locationOnContainerSupport.column",
			 			"edit" : true,
			 			"choiceInList":true,
			 			"possibleValues":[{"name":'1',"code":"1"},{"name":'2',"code":"2"},{"name":'3',"code":"3"},{"name":'4',"code":"4"},
			 			                  {"name":'5',"code":"5"},{"name":'6',"code":"6"},{"name":'7',"code":"7"},{"name":'8',"code":"8"},
			 			                  {"name":'9',"code":"9"},{"name":'10',"code":"10"},{"name":'11',"code":"11"},{"name":'12',"code":"13"}], 
			 			"order" : true,
			 			"hide" : true,
			 			"type" : "number",
			 			"position" : 402,
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
				active:true,
				by:'inputContainer.support.code'
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
                template:''
                	+'<div class="btn-group">'
                	+'<button class="btn btn-default" ng-click="computeColumnModeMode(7)" data-toggle="tooltip" title="'+Messages("experiments.button.plate.computeColumnMode")+'" ng-disabled="!isEditMode()" ng-if="experiment.instrument.outContainerSupportCategoryCode!==\'tube\'"><i class="fa fa-magic"></i><i class="fa fa-arrow-down"></i></button>'
                	+'<div class="btn-group" role="group">'
                	+'<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" ng-disabled="!isEditMode()">'
                	+'  <span class="caret"></span>'
                	+'</button>'
                	+' <ul class="dropdown-menu">'
                	+'  <li><a href="#" ng-click="computeColumnModeMode(0)" >A</a></li>'
                	+'  <li><a href="#" ng-click="computeColumnModeMode(1)" >B</a></li>'
                	+'  <li><a href="#" ng-click="computeColumnModeMode(2)" >C</a></li>'
                	+'  <li><a href="#" ng-click="computeColumnModeMode(3)" >D</a></li>'
                	+'  <li><a href="#" ng-click="computeColumnModeMode(4)" >E</a></li>'
                	+'  <li><a href="#" ng-click="computeColumnModeMode(5)" >F</a></li>'
                	+'  <li><a href="#" ng-click="computeColumnModeMode(6)" >G</a></li>'
                	+'  <li><a href="#" ng-click="computeColumnModeMode(7)" >H</a></li>'
                	+'</ul>'
                	+'</div>'
                	+'</div>'
                	
                	+'<div class="btn-group" style="margin-left:5px">'
                	+'<button class="btn btn-default" ng-click="computeLineModeMode(11)" data-toggle="tooltip" title="'+Messages("experiments.button.plate.computeLineMode")+'"  ng-disabled="!isEditMode()" ng-if="experiment.instrument.outContainerSupportCategoryCode!==\'tube\'"><i class="fa fa-magic"></i><i class="fa fa-arrow-right"></i></button>'
                	+'<div class="btn-group" role="group">'
                	+'<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" ng-disabled="!isEditMode()">'
                	+'  <span class="caret"></span>'
                	+'</button>'
                	+' <ul class="dropdown-menu">'
                	+'  <li><a href="#" ng-click="computeLineModeMode(0)" >1</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(1)" >2</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(2)" >3</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(3)" >4</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(4)" >5</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(5)" >6</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(6)" >7</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(7)" >8</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(8)" >9</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(9)" >10</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(10)" >11</a></li>'
                	+'  <li><a href="#" ng-click="computeLineModeMode(11)" >12</a></li>'
                	+'</ul>'
                	+'</div>'
                	+'</div>'
                	
                	+'<div class="btn-group" style="margin-left:5px">'
                	+'<button class="btn btn-default" ng-click="copyVolumeInToOut()" data-toggle="tooltip" title="'+Messages("experiments.button.plate.copyVolume")+'"  ng-disabled="!isEditMode()" ng-if="experiment.instrument.outContainerSupportCategoryCode!==\'tube\'"><i class="fa fa-files-o" aria-hidden="true"></i> Volume </button>'                	                	
                	+'</div>'
			}
			
	};

	var updateATM = function(experiment){
		if(experiment.instrument.outContainerSupportCategoryCode!=="tube"){
			experiment.atomicTransfertMethods.forEach(function(atm){
				atm.line = atm.outputContainerUseds[0].locationOnContainerSupport.line;
				atm.column = atm.outputContainerUseds[0].locationOnContainerSupport.column;
			});
		}		
	};
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
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

	
	$scope.copyVolumeInToOut = function(){
		var data = $scope.atmService.data.displayResult;		
		data.forEach(function(value){
			value.data.outputContainerUsed.volume = value.data.inputContainerUsed.volume;
		})		
	};
	
	/**
	 * Compute A1, B1, C1, etc.
	 */
	$scope.computeColumnModeMode = function(maxLine){
		var wells = $scope.atmService.data.displayResult;
		var nbCol = 12;
		var nbLine = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
		var x = 0;
		for(var i = 0; i < nbCol ; i++){
			for(var j = 0; j < nbLine.length && j <= maxLine; j++){
				if(x < wells.length && x < 96){
					wells[x].data.outputContainerUsed.locationOnContainerSupport.line = nbLine[j]+'';
					wells[x].data.outputContainerUsed.locationOnContainerSupport.column = i+1;					
				}
				x++;
			}
		}		
	};
	
	/**
	 * Compute A1, A2, A3, etc.
	 */
	$scope.computeLineModeMode = function(maxColumn){
		var wells = $scope.atmService.data.displayResult;
		var nbCol = 12;
		var nbLine = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
		var x = 0;
		for(var j = 0; j < nbLine.length; j++){
			for(var i = 0; i < nbCol && i <= maxColumn; i++){
				if(x < wells.length && x < 96){
					wells[x].data.outputContainerUsed.locationOnContainerSupport.line = nbLine[j]+'';
					wells[x].data.outputContainerUsed.locationOnContainerSupport.column = i+1;					
				}
				x++;
			}
		}		
	};
	/**
	 * Info on plate design
	 */
	$scope.getCellPlateData = function(line, column){
		var well = $filter('filter')($scope.atmService.data.displayResult,{data:{outputContainerUsed:{locationOnContainerSupport:{line:line,column:column}}}});
		if(well && well[0]){
			var sampleCodeAndTags = [];
			angular.forEach(well[0].data.inputContainer.contents, function(content){
				var value = content.projectCode+" / "+content.sampleCode;
				
				if(content.properties && content.properties.libProcessTypeCode){
					value = value +" / "+content.properties.libProcessTypeCode.value;
				}
				
				if(content.properties && content.properties.tag){
					value = value +" / "+content.properties.tag.value;
				}
				
				sampleCodeAndTags.push(value);
			});
			return sampleCodeAndTags;			
		}
	}
	
	$scope.$watch("experiment.instrument.outContainerSupportCategoryCode", function(){
		$scope.experiment.instrument.outContainerSupportCategoryCode = "96-well-plate";
	});
	
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
			concentration : {copyInputContainer:true},
			size : {copyInputContainer:true}
	};	
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	if($scope.experiment.instrument.inContainerSupportCategoryCode === "tube"){
		$scope.messages.clear();
		$scope.atmService = atmService;
	}else{
		$scope.messages.setError(Messages('experiments.input.error.only-tubes'));					
	}
	
	
	
}]);
angular.module('home').controller('OneToVoidQCCtrl',['$scope', '$parse','atmToSingleDatatable',
                                                             function($scope,$parse, atmToSingleDatatable) {
	 
	
	// NGL-1055: mettre les getArray et codes'' dans filter et pas dans render
	var getDefaultDatatableColumn = function() {
		var columns = [];
		
		columns.push({
			"header" : Messages("containers.table.fromTransformationTypeCodes"),
			"property" : "inputContainer.fromTransformationTypeCodes",
			"filter": "unique | codes:'type'",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
			"position" : 4,
			"extraHeaders" : {0 : Messages("experiments.inputs")}
		});
		columns.push({
			"header" : Messages("containers.table.valuation.valid"),
			"property" : "inputContainer.valuation.valid",
			"filter" : "codes:'valuation'",
			"order" : true,
			"edit" : false,
			"hide" : false,
			"type" : "text",
			"position" : 5,
			"extraHeaders" : {0 : Messages("experiments.inputs")}
		});
		columns.push({
			"header" : Messages("containers.table.projectCodes"),
			"property" : "inputContainer.projectCodes",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 6,
			"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
			"extraHeaders" : {0 : Messages("experiments.inputs")}
		});
		columns.push({
			"header" : Messages("containers.table.sampleCodes"),
			"property" : "inputContainer.sampleCodes",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 7,
			"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
			"extraHeaders" : {0 : Messages("experiments.inputs")}
		});

				
	/*	columns.push({
			"header" : Messages("containers.table.concentration"),
			"property": "inputContainer.concentration",
			"render":"<span ng-bind='cellValue.value|number'/> <span ng-bind='cellValue.unit'/>",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 8,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});

		columns.push({
			"header" : Messages("containers.table.volume") + " (µL)",
			"property" : "inputContainer.volume.value",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "number",
			"position" : 9,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		
		
		columns.push({
			"header" : Messages("containers.table.libProcessType"),
			"property" : "inputContainer.contents",
			"order" : false,
			"hide" : true,
			"type" : "text",
			"position" : 10,
			"render" : "<div list-resize='cellValue | getArray:\"properties.libProcessTypeCode.value\" | unique' list-resize-min-size='3'>",
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		columns.push({
			"header" : Messages("containers.table.tags"),
			"property" : "inputContainer.contents",
			"order" : false,
			"hide" : true,
			"type" : "text",
			"position" : 11,
			"render" : "<div list-resize='cellValue | getArray:\"properties.tag.value\" | unique' list-resize-min-size='3'>",
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}

		}); */
		columns.push({
			"header" : Messages("containers.table.stateCode"),
			"property" : "inputContainer.state.code",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"filter" : "codes:'state'",
			"position" : 10.5,
			"extraHeaders" : {0 : Messages("experiments.inputs")}
		});
		columns.push({
			"header" : Messages("containers.table.valuationqc.valid"),
			"property" : "inputContainerUsed.valuation.valid",
			"filter" : "codes:'valuation'",
			"order" : true,
			"edit" : true,
			"hide" : false,
			"type" : "text",
			"choiceInList" : true,
			"listStyle" : 'bt-select',
			"possibleValues" : 'lists.getValuations()',
			"position" : 30,
			"extraHeaders" : {0 : Messages("experiments.inputs")}
		});
		
		columns.push({
			"header" : Messages("containers.table.valuationqc.comment"),
			"property" : "inputContainerUsed.valuation.comment",
			"order" : false,
			"edit" : true,
			"hide" : true,
			"type" : "text",			
			"position" : 31,
			"extraHeaders" : {0 : Messages("experiments.inputs")}
		});
		columns.push({
        	 "header":Messages("containers.table.storageCode"),
        	 "property":"inputContainerUsed.locationOnContainerSupport.storageCode",
        	 "order":true,
			 "edit":true,
			 "hide":true,
        	 "type":"text",
        	 "position":600,
        	 "extraHeaders":{0:Messages("experiments.inputs")}
		});
		
		
		if($scope.experiment.instrument.inContainerSupportCategoryCode!=="tube"){
			columns.push({
				"header" : Messages("containers.table.supportCode"),
				"property" : "inputContainer.support.code",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "text",
				"position" : 1,
				"extraHeaders" : {0 : Messages("experiments.inputs")}
			});
			columns.push({
				"header" : Messages("containers.table.support.line"),
				"property" : "inputContainer.support.line",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "text",
				"position" : 1.1,
				"extraHeaders" : {0 : Messages("experiments.inputs")}
			});
			columns.push({
				"header" : Messages("containers.table.support.column"),
				"property" : "inputContainer.support.column*1",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "number",
				"position" : 1.2,
				"extraHeaders" : {0 : Messages("experiments.inputs")}
			});
		}else{
			columns.push({
				"header" : Messages("containers.table.code"),
				"property" : "inputContainer.support.code",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "text",
				"position" : 3,
				"extraHeaders" : {0 : Messages("experiments.inputs")}
			});
		}
		
		return columns;
	}
	
	if($scope.experiment.instrument.inContainerSupportCategoryCode ==="tube"){
		datatableConfig.order.by = 'inputContainer.sampleCodes';
	}
	
	
	// NGL-1055: name explicite pour fichier CSV exporté: typeCode experience
	var datatableConfig = {		
			name: $scope.experiment.typeCode.toUpperCase(),
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
	        	changeClass:false,
	        	showButton:false,
	        	withoutEdit: true,
				mode:'local'
			},
			hide:{
				active:true
			},
			edit:{
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
				showButton: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
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
		console.log("call event save on one-to-void");
		$scope.atmService.data.save();			
		$scope.atmService.viewToExperimentOneToVoid($scope.experiment);
		$scope.copyPropertiesToInputContainer($scope.experiment); //override from child
		$scope.$emit('childSaved', callbackFunction);
	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh on one-to-void");		
		var dtConfig = $scope.atmService.data.getConfig();
		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
		dtConfig.edit.shwoButton = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
		dtConfig.remove.active = ($scope.isEditModeAvailable() && $scope.isNewState());
		$scope.atmService.data.setConfig(dtConfig);
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('cancel', function(e) {
		console.log("call event cancel");
		$scope.atmService.data.cancel();				
	});
	
	$scope.$on('activeEditMode', function(e) {
		console.log("call event activeEditMode");
		$scope.atmService.data.selectAll(true);
		$scope.atmService.data.setEdit();
	});
	
	datatableConfig.columns = getDefaultDatatableColumn();
	var atmService = atmToSingleDatatable($scope, datatableConfig, true);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(line, column){
		return {
			class:"OneToVoid",
			line:line, 
			column:column, 				
			inputContainerUseds:new Array(0)
		};
	};
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
		

}]);
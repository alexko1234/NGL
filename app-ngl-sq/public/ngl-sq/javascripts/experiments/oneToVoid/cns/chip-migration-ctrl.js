angular.module('home').controller('OneToVoidChipMigrationCNSCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	// NGL-1055: surcharger la variable "name" definie dans le controleur parent ( one-to-void-qc-ctrl.js) => nom de fichier CSV exporté 
	var config = $scope.atmService.data.getConfig();
	config.name = $scope.experiment.typeCode.toUpperCase();
	if($scope.experiment.typeCode === "chip-migration-rna-evaluation"){
		config.order.by = "inputContainer.sampleCodes";		
	}
	$scope.atmService.data.setConfig(config );
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
				
				var measuredSize = $parse("experimentProperties.measuredSize")(inputContainerUsed);
				
				if(measuredSize){
					inputContainerUsed.size = measuredSize;
					
					
					if(experiment.typeCode === "chip-migration" && 
							(inputContainerUsed.fromTransformationTypeCodes.indexOf("pcr-amplification-and-purification") > -1
									|| inputContainerUsed.fromTransformationTypeCodes.indexOf("sizing")  > -1)){
						var experimentProperties = $parse("experimentProperties")(inputContainerUsed);
						
						experimentProperties.insertSize = {value:inputContainerUsed.size.value, unit:inputContainerUsed.size.unit};
						experimentProperties.insertSize.value = inputContainerUsed.size.value - 121;
						experimentProperties.libLayoutNominalLength = experimentProperties.insertSize;										
					}
				}
			
				var volume1 = $parse("experimentProperties.volume1")(inputContainerUsed);
				if(volume1){
					inputContainerUsed.volume = volume1;
				}
				
				
				
			}
			
		});	
	
	};
	
		
	var profilsMap = {};
	angular.forEach($scope.experiment.atomicTransfertMethods, function(atm){
		var pos = null;
		if("labchip-gx" === $scope.experiment.instrument.typeCode){
			pos = atm.inputContainerUseds[0].locationOnContainerSupport.line+atm.inputContainerUseds[0].locationOnContainerSupport.column;
		}else if("agilent-2100-bioanalyzer" === $scope.experiment.instrument.typeCode){
			pos = $parse('inputContainerUseds[0].instrumentProperties.chipPosition.value')(atm);			
		}
		var img = $parse('inputContainerUseds[0].experimentProperties.migrationProfile')(atm);
		if(pos && img)this[pos] = img;
	},profilsMap)
	
	var internalProfils = profilsMap;
	$scope.getProfil=function(line, column){
		if("labchip-gx" === $scope.experiment.instrument.typeCode){
			return internalProfils[line+column];
		}else if("agilent-2100-bioanalyzer" === $scope.experiment.instrument.typeCode){
			return internalProfils[line];					
		}
	};
	
	$scope.$watch("profils",function(newValues, oldValues){
		if(newValues){			
			var _profilsMap = {};
			angular.forEach(newValues, function(img){
				var pos = null;
				if("labchip-gx" === $scope.experiment.instrument.typeCode){
					var pos = img.fullname.match(/_([A-H]\d+)\./)[1];					
				}else if("agilent-2100-bioanalyzer" === $scope.experiment.instrument.typeCode){
					var pos = img.fullname.match(/_Sample(\d+)\./)[1];					
				}
				if(pos && img)this[pos] = img;
							
			}, _profilsMap);
			
			internalProfils = _profilsMap;
			
			angular.forEach($scope.atmService.data.displayResult, function(dr){
				var pos = null;
				if("labchip-gx" === $scope.experiment.instrument.typeCode){
					pos = dr.data.inputContainerUsed.locationOnContainerSupport.line+dr.data.inputContainerUsed.locationOnContainerSupport.column;
				}else if("agilent-2100-bioanalyzer" === $scope.experiment.instrument.typeCode){
					pos = $parse('inputContainerUsed.instrumentProperties.chipPosition.value')(dr.data);			
				}
				if(pos)	$parse('inputContainerUsed.experimentProperties.migrationProfile').assign(dr.data, this[pos]);
			}, _profilsMap);
		
		}
		angular.element('#importProfils')[0].value = null;
		
	})
	$scope.$watch("instrumentType", function(newValue, OldValue){
		if(newValue)
			$scope.atmService.addInstrumentPropertiesToDatatable(newValue.propertiesDefinitions);
	})
	
	
	var columns = $scope.atmService.data.getColumnsConfig();
	columns.push({
			"header" : Messages("containers.table.concentration"),
			"property": "(inputContainer.concentration.value|number).concat(' '+inputContainer.concentration.unit)",
			//"render":"<span ng-bind='cellValue.value|number'/> <span ng-bind='cellValue.unit'/>",
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
		
		
/*	columns.push({
			"header" : Messages("containers.table.libProcessType"),
			"property" : "inputContainer.contents",
			"order" : false,
			"hide" : true,
			"type" : "text",
			"position" : 9.1,
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
			"position" : 9.2,
			"render" : "<div list-resize='cellValue | getArray:\"properties.tag.value\" | unique' list-resize-min-size='3'>",
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}

		}); 
	*/
	$scope.atmService.data.setColumnsConfig(columns);

	
	$scope.button = {
		isShow:function(){
			return ($scope.isInProgressState() && !$scope.mainService.isEditMode())
			}	
	};
	
	
	var importData = function(){
		$scope.messages.clear();
		
		$http.post(jsRoutes.controllers.instruments.io.IO.importFile($scope.experiment.code).url, $scope.file)
		.success(function(data, status, headers, config) {
			$scope.messages.clazz="alert alert-success";
			$scope.messages.text=Messages('experiments.msg.import.success');
			$scope.messages.showDetails = false;
			$scope.messages.open();	
			//only atm because we cannot override directly experiment on scope.parent
			$scope.experiment.atomicTransfertMethods = data.atomicTransfertMethods;
			$scope.file = undefined;
			angular.element('#importFile')[0].value = null;
			$scope.$emit('refresh');
			
		})
		.error(function(data, status, headers, config) {
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages('experiments.msg.import.error');
			$scope.messages.setDetails(data);
			$scope.messages.open();	
			$scope.file = undefined;
			angular.element('#importFile')[0].value = null;
		});
	};
	
	$scope.importButton = {
		isShow:function(){
			return ("labchip-gx" === $scope.experiment.instrument.typeCode && $scope.isInProgressState() && !$scope.mainService.isEditMode())
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData,		
	};
}]);
angular.module('home').controller('OneToVoidChipMigrationCNSCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	// NGL-1055: surcharger la variable "name" definie dans le controleur parent ( one-to-void-qc-ctrl.js) => nom de fichier CSV exporté 
	var config = $scope.atmService.data.getConfig();
	config.name = $scope.experiment.typeCode.toUpperCase();
	
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
									|| inputContainerUsed.fromTransformationTypeCodes.indexOf("sizing")  > -1 
									|| inputContainerUsed.fromTransformationTypeCodes.indexOf("spri-select")  > -1)){
						var experimentProperties = $parse("experimentProperties")(inputContainerUsed);
						
						experimentProperties.insertSize = {value:inputContainerUsed.size.value, unit:inputContainerUsed.size.unit};
						experimentProperties.insertSize.value = inputContainerUsed.size.value - 121;
						experimentProperties.libLayoutNominalLength = experimentProperties.insertSize;
						
						var firstContent = inputContainerUsed.contents[0]; 
						
						if(firstContent.properties.libProcessTypeCode.value === 'N'
								|| firstContent.properties.libProcessTypeCode.value === 'A'
									|| firstContent.properties.libProcessTypeCode.value === 'C'){
							experimentProperties.libLayoutNominalLength = {value:-1, unit:"pb"};						
						}
						
					}
				}
			
				var volume1 = $parse("experimentProperties.volume1")(inputContainerUsed);
				if(volume1){
					inputContainerUsed.volume = volume1;
				}
				
				
				if(experiment.typeCode === "chip-migration-rna-evaluation"){
					var concentration1 = $parse("experimentProperties.concentration1")(inputContainerUsed);
					if(concentration1){
						inputContainerUsed.concentration = concentration1;
						inputContainerUsed.quantity = $scope.computeQuantity(inputContainerUsed.concentration, inputContainerUsed.volume);
					}										
				}else{
					var quantity1 = $parse("experimentProperties.quantity1")(inputContainerUsed);
					if(quantity1){
						inputContainerUsed.quantity = quantity1;
					}
				}
			}
			
		});	
	
	};
	
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		
		if($scope.experiment.typeCode === "chip-migration" && col.property === 'inputContainerUsed.experimentProperties.volume1.value'){
			computeQuantity1(value.data);
		}
		
	}
	
	var computeQuantity1 = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.quantity1");
		var quantity1 = getter(udtData);
		
		var compute = {
				inputVol1 : $parse("inputContainerUsed.experimentProperties.volume1")(udtData),
				inputConc1 : $parse("inputContainerUsed.concentration")(udtData),
				isReady:function(){
					return (this.inputVol1 && this.inputConc1 && this.inputVol1.value && this.inputConc1.value);
				}
			};
		
		if(compute.isReady()){
			getter.assign(udtData, $scope.computeQuantity(compute.inputConc1, compute.inputVol1));
		}else{
			console.log("not ready to computeQuantity1");
		}
		
	}
	
	
	
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
	
	
	if ($scope.experiment.instrument.inContainerSupportCategoryCode.indexOf('well') == -1) {
		columns.push({
			"header" : Messages("containers.table.workName"),
			"property" : "inputContainer.properties.workName.value",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 3.1,
			"extraHeaders" : {0 : Messages("experiments.inputs")}
		});
	}
	
	columns.push({
			"header" : Messages("containers.table.libProcessType"),
			"property" : "inputContainer.contents",
			"order" : false,
			"hide" : true,
			"type" : "text",
			"position" : 7.1,
			"render" : "<div list-resize='cellValue | getArray:\"properties.libProcessTypeCode.value\" | unique' list-resize-min-size='3'>",
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
	
	if($scope.experiment.typeCode === "chip-migration-rna-evaluation"){
		columns.push({		
			"header" : Messages("containers.table.libraryToDo"),
			"property" : "inputContainerUsed.contents",
			"filter" : "getArray:'processProperties.libraryToDo.value' | unique ",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 10.8,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
	}
		/*	
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
			return ($scope.isInProgressState() && !$scope.mainService.isEditMode() || Permissions.check("admin"))
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
			return ("labchip-gx" === $scope.experiment.instrument.typeCode  && !$scope.mainService.isEditMode() 
					&& ($scope.isInProgressState() || Permissions.check("admin")))
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData,		
	};
	
	
	var generateSampleSheet = function(){
		$scope.messages.clear();
		$http.post(jsRoutes.controllers.instruments.io.IO.generateFile($scope.experiment.code).url,{})
		.success(function(data, status, headers, config) {
			var header = headers("Content-disposition");
			var filepath = header.split("filename=")[1];
			
			var filename = filepath.split(/\/|\\/);
			filename = filename[filename.length-1];
			if(data!=null){
				$scope.messages.setSuccess(Messages('experiments.msg.generateSampleSheet.success')+" : "+filepath);
				var blob = new Blob([data], {type: "text/plain;charset=utf-8"});    					
				saveAs(blob, filename);
			}
		})
		.error(function(data, status, headers, config) {
			$scope.messages.setError(Messages('experiments.msg.generateSampleSheet.error'));
			$scope.messages.setDetails(data);
			$scope.messages.showDetails = true;							
		});
	};
	
	if("labchip-gx" === $scope.experiment.instrument.typeCode){
		
		$scope.setAdditionnalButtons([{
			isDisabled : function(){return $scope.isNewState();} ,
			isShow:function(){return !$scope.isNewState();},
			click:generateSampleSheet,
			label:Messages("experiments.sampleSheet")
		}]);
	}
	
	
}]);
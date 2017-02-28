angular.module('home').controller('OneToVoidBioanalyzerMigrationProfileCNGCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {

	
	// NGL-1055: surcharger la variable "name" definie dans le controleur parent ( one-to-void-qc-ctrl.js) => nom de fichier CSV exporté 
	var config = $scope.atmService.data.getConfig();
	config.name = $scope.experiment.typeCode.toUpperCase();
	
	$scope.atmService.data.setConfig(config );
			
    //  version plus simple du labchipgx CNG....
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		//FDS  30/08/2016 concentration et size de l'expérience doivent etres copiées dans le container
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
					
				var concentration1 = $parse("experimentProperties.concentration1")(inputContainerUsed);
				if(concentration1){
					inputContainerUsed.concentration = concentration1;
				}
				
				var size1 = $parse("experimentProperties.size1")(inputContainerUsed);
				if(size1){
					inputContainerUsed.size = size1;
				}
			}	
		});	
	};	
	

	// code venant de chip-migration-ctrl.js au CNS: prevus pour LabChipGX ET bionanalyzer
	// supprimer code pour labchipGX
	
	var profilsMap = {};
	angular.forEach($scope.experiment.atomicTransfertMethods, function(atm){
		var pos = $parse('inputContainerUseds[0].instrumentProperties.chipPosition.value')(atm);			
		var img = $parse('inputContainerUseds[0].experimentProperties.migrationProfile')(atm);
		if(pos && img)this[pos] = img;
	},profilsMap)
	
	var internalProfils = profilsMap;
	$scope.getProfil=function(line, column){
		return internalProfils[line];					
	};
	
	$scope.$watch("profils",function(newValues, oldValues){
		if(newValues){			
			var _profilsMap = {};
			angular.forEach(newValues, function(img){						
				var pos = img.fullname.match(/_Sample(\d+)\./)[1];					
				if(pos && img)this[pos] = img;
							
			}, _profilsMap);
			
			internalProfils = _profilsMap;
			
			angular.forEach($scope.atmService.data.displayResult, function(dr){
				var pos = $parse('inputContainerUsed.instrumentProperties.chipPosition.value')(dr.data);			
				if(pos)	$parse('inputContainerUsed.experimentProperties.migrationProfile').assign(dr.data, this[pos]);
			}, _profilsMap);	
		}
		angular.element('#importProfils')[0].value = null;
		
	})
	
	// FDS CNG peut pas changer d'instrument..
	//$scope.$watch("instrumentType", function(newValue, OldValue){
	//	if(newValue)
	//		$scope.atmService.addInstrumentPropertiesToDatatable(newValue.propertiesDefinitions);
	//})
	
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
	
	// CNG pas de workname
	
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
	
	/*	OUI il car il un QC au bioanalyzer apre l'etape Chromium PCR indexing....*/
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
	
	$scope.atmService.data.setColumnsConfig(columns);

	// boutton pour ???????
	
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
	
	/* pas de sample sheet pour bioanalyzer
	 * 
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
	*/
	
}]);
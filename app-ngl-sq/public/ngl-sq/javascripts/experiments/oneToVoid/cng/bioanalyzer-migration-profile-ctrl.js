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
	

	// code venant de chip-migration-ctrl.js au CNS: prevus pour LabChipGX ET bionanalyzer => supprimer code pour labchipGX
	var profilsMap = {};
	angular.forEach($scope.experiment.atomicTransfertMethods, function(atm){
		var pos = $parse('inputContainerUseds[0].instrumentProperties.chipPosition.value')(atm);
		var img = $parse('inputContainerUseds[0].experimentProperties.migrationProfile')(atm);
		if(pos && img)this[pos] = img;
	},profilsMap)
	
	var internalProfils = profilsMap;
	/// pas besoin de line ?????
	$scope.getProfil=function(column){
		return internalProfils[column];					
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
	
    // meme s'il n'y a pas de choix possible par l'utilisateur, ce watch est indispensable pour que les proprietes d'instrument soient injectees dans l'interface..	
	// MERCI Maud !!!
	$scope.$watch("instrumentType", function(newValue, OldValue){
		if(newValue)
			$scope.atmService.addInstrumentPropertiesToDatatable(newValue.propertiesDefinitions);
	})
	
	
	var columns = $scope.atmService.data.getColumnsConfig();
	
	columns.push({
    	"header": Messages("containers.table.codeAliquot"),
		"property": "inputContainer.contents",
		"filter": "getArray:'properties.sampleAliquoteCode.value'| unique",
		"order":false,
		"hide":true,
		"type":"text",
		"position":7.1,
		"render": "<div list-resize='cellValue' list-resize-min-size='3'>",
		"extraHeaders": {0 : Messages("experiments.inputs")}
	});
	
	columns.push({
			"header" : Messages("containers.table.libProcessType"),
			"property" : "inputContainer.contents",
			"order" : false,
			"hide" : true,
			"type" : "text",
			"position" : 7.2,
			"render" : "<div list-resize='cellValue | getArray:\"properties.libProcessTypeCode.value\" | unique' list-resize-min-size='3'>",
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
	
	
	$scope.atmService.data.setColumnsConfig(columns);

	// bouton des profils
	$scope.button = {
		isShow:function(){
			return ($scope.isInProgressState() && !$scope.mainService.isEditMode() || Permissions.check("admin"))
			}	
	};
	
	
}]);
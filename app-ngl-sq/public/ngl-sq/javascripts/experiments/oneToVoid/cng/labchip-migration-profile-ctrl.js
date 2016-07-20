angular.module('home').controller('OneToVoidLabChipMigrationProfileCNGCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {

	// NGL-1055: surcharger la variable "name" definie dans le controleur parent ( one-to-void-qc-ctrl.js) => nom de fichier CSV exporté 
	var config = $scope.atmService.data.getConfig();
	config.name = $scope.experiment.typeCode.toUpperCase();
	$scope.atmService.data.setConfig(config );

	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		/* aucune propriété ne doit etre copié
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
				
				var concentration1 = $parse("experimentProperties.concentration1")(inputContainerUsed);
				/*
				if(concentration1){
					inputContainerUsed.concentration = concentration1;
				}
				
				
			}
			
		});	
		*/
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
	
	$scope.button = {
		isShow:function(){
			return ($scope.isInProgressState() && !$scope.mainService.isEditMode())
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData,		
	};
	
	// FDS NGL-1055: mettre le getArray|unique dans filter et pas dans render
	var columns = $scope.atmService.data.getColumnsConfig();
	columns.push({
    	"header": Messages("containers.table.codeAliquot"),
		"property": "inputContainer.contents",
		"filter": "getArray:'properties.sampleAliquoteCode.value'| unique",
		"order":false,
		"hide":true,
		"type":"text",
		"position":7.5,
		"render": "<div list-resize='cellValue' list-resize-min-size='3'>",
		"extraHeaders": {0 : Messages("experiments.inputs")}
	});
	columns.push({
		"header" : Messages("containers.table.libProcessTypeCode"),
		"property" : "inputContainer.contents",
		"filter": "getArray: 'properties.libProcessTypeCode.value'| unique",
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 9,
		"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
		"extraHeaders" : {0 : Messages("experiments.inputs")}
	});
	columns.push({
		"header" : Messages("containers.table.tags"),
		"property" : "inputContainer.contents",
		"filter": "getArray:'properties.tag.value'| unique",
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 10,
		"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
		"extraHeaders" : {0 : Messages("experiments.inputs")}
	});
	
	$scope.atmService.data.setColumnsConfig(columns);
	

	
	var profilsMap = {};
	angular.forEach($scope.experiment.atomicTransfertMethods, function(atm){
		var pos = atm.inputContainerUseds[0].locationOnContainerSupport.line+atm.inputContainerUseds[0].locationOnContainerSupport.column;
		var img = $parse('inputContainerUseds[0].experimentProperties.migrationProfile')(atm);
		this[pos] = img;
	},profilsMap)
	
	var internalProfils = profilsMap;
	$scope.getProfil=function(line, column){
		return internalProfils[line+column];
	};
	
	$scope.$watch("profils",function(newValues, oldValues){
		if(newValues){			
			var _profilsMap = {};
			angular.forEach(newValues, function(img){
				var pos = img.fullname.split('_')[0];
				this[pos] = img;			
			}, _profilsMap);
			
			internalProfils = _profilsMap;
			
			angular.forEach($scope.atmService.data.displayResult, function(dr){
				var pos = dr.data.inputContainerUsed.locationOnContainerSupport.line+dr.data.inputContainerUsed.locationOnContainerSupport.column;
				$parse('inputContainerUsed.experimentProperties.migrationProfile').assign(dr.data, this[pos]);
			}, _profilsMap);
		
		}
		
	})
	
}]);
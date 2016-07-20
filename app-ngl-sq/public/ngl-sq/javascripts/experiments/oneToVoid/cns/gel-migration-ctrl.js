angular.module('home').controller('OneToVoidGelMigrationCNSCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	// NGL-1055: surcharger la variable "name" definie dans le controleur parent ( one-to-void-qc-ctrl.js) => nom de fichier CSV exporté 
	var config = $scope.atmService.data.getConfig();
	config.name = $scope.experiment.typeCode.toUpperCase();
	$scope.atmService.data.setConfig(config );
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
				var volume1 = $parse("experimentProperties.volume1")(inputContainerUsed);
				if(volume1){
					inputContainerUsed.volume = volume1;
				}
			}
				
		});			
	};
	
	$scope.$watch("gel",function(imgNew, imgOld){
		if(imgNew){			
			
			angular.forEach($scope.atmService.data.displayResult, function(dr){
				$parse('inputContainerUsed.experimentProperties.electrophoresisGelPhoto').assign(dr.data, this);
			}, imgNew);
			
		}
		angular.element('#importFile')[0].value = null;
		
	});
	
	$scope.button = {
			isShow:function(){
				return ($scope.isInProgressState() && !$scope.mainService.isEditMode())
				}	
		};
	
	var columns = $scope.atmService.data.getColumnsConfig();
	
	// FDS NGL-1055: mettre le getArray|unique dans filter et pas dans render
	columns.push({
		"header" : Messages("containers.table.sampleTypes"),
		"property" : "inputContainer.contents",
		"filter" : "getArray:'sampleTypeCode' | unique",
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 7,
		"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}

	});
	
	columns.push({
		"header" : Messages("containers.table.concentration"),
		"property": "(inputContainer.concentration.value|number).concat(' '+inputContainer.concentration.unit)",
		//"render":"<span ng-bind='cellValue.value|number'/> <span ng-bind='cellValue.unit'/>",
		"order" : true,
		"edit" : false,
		"hide" : true,
		"type" : "text",
		"position" : 9,
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
		"position" : 10,
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}
	});
	
/*	columns.push({
		"header":Messages("containers.table.size")+ " (pb)",
		"property": "inputContainer.size.value",
		"order":false,
		"hide":true,
		"type":"text",
		"position":11,
		"extraHeaders":{0:Messages("experiments.inputs")}			 						 			
	});*/
	
	$scope.atmService.data.setColumnsConfig(columns);

	
}]);
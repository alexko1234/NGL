angular.module('home').controller('OneToVoidQcardCNSCtrl',['$scope', '$parse','$http',
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
				inputContainerUsed.quantity = $scope.computeQuantity(inputContainerUsed.concentration, inputContainerUsed.volume);
			}			
		});			
	};
	
	$scope.button = {
			isShow:function(){
				return ($scope.isInProgressState() && !$scope.mainService.isEditMode())
				}	
		};
	
	var columns = $scope.atmService.data.getColumnsConfig();
	
	columns.push({
		"header" : Messages("containers.table.libraryToDo"),
		"property": "inputContainer.contents",
		"filter": "getArray:'processProperties.libraryToDo.value'| unique",
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 7,
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}

	});
	
	
	$scope.atmService.data.setColumnsConfig(columns);

	
}]);
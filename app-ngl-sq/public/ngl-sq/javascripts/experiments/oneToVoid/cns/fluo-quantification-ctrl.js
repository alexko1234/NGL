angular.module('home').controller('OneToVoidFluoQuantificationCNSCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	// NGL-1055: surcharger la variable "name" definie dans le controleur parent ( one-to-void-qc-ctrl.js) => nom de fichier CSV exporté 
	var config = $scope.atmService.data.getConfig();
	config.name = $scope.experiment.typeCode.toUpperCase();
	$scope.atmService.data.setConfig(config );
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
				var concentration1 = $parse("experimentProperties.concentration1")(inputContainerUsed);
				if(concentration1){
					inputContainerUsed.concentration = concentration1;
				}
				
				var volume1 = $parse("experimentProperties.volume1")(inputContainerUsed);
				if(volume1){
					inputContainerUsed.volume = volume1;
				}
				
				var quantity1 = $parse("experimentProperties.quantity1")(inputContainerUsed);
				if(quantity1){
					inputContainerUsed.quantity = quantity1;
				}
			}
			
			
		});			
	};
	
	
	var columns = $scope.atmService.data.getColumnsConfig();

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
	
	/*
	columns.push({
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

	}); */
	
	$scope.atmService.data.setColumnsConfig(columns);
	
	
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		
		if(col.property === 'inputContainerUsed.experimentProperties.concentration1.value'){
			computeQuantity1(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.volume1.value'){
			computeQuantity1(value.data);
		}
		
	}
	
	var computeQuantity1 = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.quantity1.value");
		var quantity1 = getter(udtData);
		
		var compute = {
				inputVol1 : $parse("inputContainerUsed.experimentProperties.volume1.value")(udtData),
				inputConc1 : $parse("inputContainerUsed.experimentProperties.concentration1.value")(udtData),
				isReady:function(){
					return (this.inputVol1 && this.inputConc1);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("(inputVol1 * inputConc1)")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				quantity1 = Math.round(result*10)/10;				
			}else{
				quantity1 = undefined;
			}	
			getter.assign(udtData, quantity1);
		}else{
			console.log("not ready to computeQuantity1");
		}
		
	}
	
}]);
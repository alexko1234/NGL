angular.module('home').controller('OneToVoidReceptionFluoQuantificationCNSCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	// NGL-1055: surcharger la variable "name" definie dans le controleur parent ( one-to-void-qc-ctrl.js) => nom de fichier CSV exporté 
	var config = $scope.atmService.data.getConfig();
	config.name = $scope.experiment.typeCode.toUpperCase();
	config.order.by = "inputContainer.sampleCodes";
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
				}else{
					inputContainerUsed.quantity = $scope.computeQuantity(inputContainerUsed.concentration, inputContainerUsed.volume);
				}
			}
			
			
		});			
	};
	
	
	var columns = $scope.atmService.data.getColumnsConfig();

	columns.push({
		"header" : Messages("containers.table.volume") + " (µL)",
		"property" : "inputContainerUsed.volume.value",
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
	
	$scope.atmService.data.setColumnsConfig(columns);
	
	
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		
		if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactor.value'){
			computeConcentrationBR1(value.data);
			computeConcentrationBS1(value.data);
        	computeConcentrationBS2(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.concentrationDilBR1.value'){
			computeConcentrationBR1(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS1.value'){
			computeConcentrationBS1(value.data);
        }else if(col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS2.value'){
        	computeConcentrationBS2(value.data);
    	}else if(col.property === 'inputContainerUsed.experimentProperties.calculationMethod.value'){
    		computeConcentration1(value.data);
    	}
		
	}
	
	
	
	var computeConcentrationBR1 = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.concentrationBR1.value");
		var concentration1 = getter(udtData);
		var compute = {
				conc1 : $parse("inputContainerUsed.experimentProperties.concentrationDilBR1.value")(udtData),
				dilution1 :  (($parse("inputContainerUsed.experimentProperties.dilutionFactor.value")(udtData)).indexOf("1/") ==0 ? ($parse("inputContainerUsed.experimentProperties.dilutionFactor.value")(udtData)).substring(2) : undefined ) ,
				isReady:function(){
					return (this.conc1 && this.dilution1);
				}
			};
		
		if(compute.isReady()){
			
			var result = $parse("(conc1 * dilution1)")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				concentration1 = Math.round(result*10)/10;				
			}else{
				concentration1 = undefined;
			}	
			getter.assign(udtData, concentration1);
		}else{
			getter.assign(udtData, undefined);
			console.log("not ready to computeConcentrationBR1");
		}
		
	}
	
	
	var computeConcentrationBS1 = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.concentrationHS1.value");
		var concentration1 = getter(udtData);
		var compute = {
				conc1 : $parse("inputContainerUsed.experimentProperties.concentrationDilHS1.value")(udtData),
				dilution1 :  (($parse("inputContainerUsed.experimentProperties.dilutionFactor.value")(udtData)).indexOf("1/") ==0 ? ($parse("inputContainerUsed.experimentProperties.dilutionFactor.value")(udtData)).substring(2) : undefined ) ,
				isReady:function(){
					return (this.conc1 && this.dilution1);
				}
			};
		
		if(compute.isReady()){
			
			var result = $parse("(conc1 * dilution1)")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				concentration1 = Math.round(result*10)/10;				
			}else{
				concentration1 = undefined;
			}	
			getter.assign(udtData, concentration1);
		}else{
			getter.assign(udtData, undefined);
			console.log("not ready to computeConcentrationBS1");
		}
		
	}
	
	var computeConcentrationBS2 = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.concentrationHS2.value");
		var concentration1 = getter(udtData);
		var compute = {
				conc1 : $parse("inputContainerUsed.experimentProperties.concentrationDilHS2.value")(udtData),
				dilution1 :  (($parse("inputContainerUsed.experimentProperties.dilutionFactor.value")(udtData)).indexOf("1/") ==0 ? ($parse("inputContainerUsed.experimentProperties.dilutionFactor.value")(udtData)).substring(2) : undefined ) ,
				isReady:function(){
					return (this.conc1 && this.dilution1);
				}
			};
		
		if(compute.isReady()){
			
			var result = $parse("(conc1 * dilution1)")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				concentration1 = Math.round(result*10)/10;				
			}else{
				concentration1 = undefined;
			}	
			getter.assign(udtData, concentration1);
		}else{
			getter.assign(udtData, undefined);
			console.log("not ready to computeConcentrationBS2");
		}
		
	}
	
	var computeConcentration1 = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.concentration1.value");
		var concentration1 = getter(udtData);
		var calMethod=$parse("inputContainerUsed.experimentProperties.calculationMethod.value")(udtData);
		if(calMehod){
			if(calMethod===""){
				
				var compute = {
						calMethod : $parse("inputContainerUsed.experimentProperties.calculationMethod.value")(udtData),
						inputConc1 : $parse("inputContainerUsed.experimentProperties.concentration1.value")(udtData),
						isReady:function(){
							return (this.calMethod && this.inputConc1);
						}
					};
				
				if(compute.isReady()){
					var result = $parse("(inputVol1 * inputConc1)")(compute);
					console.log("result = "+result);
					if(angular.isNumber(result) && !isNaN(result)){
						concentration1 = Math.round(result*10)/10;				
					}else{
						concentration1 = undefined;
					}	
					getter.assign(udtData, quantity1);
				}else{
					console.log("not ready to computeConcentration1");
				}
				
			}else if(calMethod==""){
				
			}else if(calMethod==""){
				
			}else if(calMethod==""){
				
			}else {	console.log("calMethod "+calMethod+" not implemented");}
			
			
		}
		var compute = {
				calMethod : $parse("inputContainerUsed.experimentProperties.calculationMethod.value")(udtData),
				inputConc1 : $parse("inputContainerUsed.experimentProperties.concentration1.value")(udtData),
				isReady:function(){
					return (this.calMethod && this.inputConc1);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("(inputVol1 * inputConc1)")(compute);
			console.log("result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				concentration1 = Math.round(result*10)/10;				
			}else{
				concentration1 = undefined;
			}	
			getter.assign(udtData, quantity1);
		}else{
			console.log("not ready to computeConcentration1");
		}
		
	}
	
	
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
			return ("fluoroskan" === $scope.experiment.instrument.typeCode && !$scope.mainService.isEditMode() 
					&& ($scope.isInProgressState() || Permissions.check("admin")))
					 
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData,		
	};
	
}]);
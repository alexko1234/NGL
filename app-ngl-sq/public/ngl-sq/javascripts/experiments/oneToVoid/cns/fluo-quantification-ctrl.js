angular.module('home').controller('OneToVoidFluoQuantificationCNSCtrl',['$scope', '$parse','$http',
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
					inputContainerUsed.newConcentration = concentration1;
				}
				
				var volume1 = $parse("experimentProperties.volume1")(inputContainerUsed);
				if(volume1){
					inputContainerUsed.newVolume = volume1;
				}
				
				var quantity1 = $parse("experimentProperties.quantity1")(inputContainerUsed);
				if(quantity1){
					inputContainerUsed.newQuantity = quantity1;
				}else{
					inputContainerUsed.newQuantity = $scope.computeQuantity(
							(concentration1)?inputContainerUsed.newConcentration:inputContainerUsed.concentration, 
							(volume1)?inputContainerUsed.newVolume:inputContainerUsed.volume);
				}
			
			}
			
			
		});			
	};
	
	
	var columns = $scope.atmService.data.getColumnsConfig();

	
	columns.push({
		"header" : Messages("containers.table.libProcessType"),
		"property" : "inputContainer.contents",
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 8,
		"render" : "<div list-resize='cellValue | getArray:\"properties.libProcessTypeCode.value\" | unique' list-resize-min-size='3'>",
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}
	});
	columns.push({
		"header":Messages("containers.table.size")+ " (pb)",
		"property": "inputContainerUsed.size.value",
		"order":false,
		"hide":true,
		"type":"text",
		"position":8.05,
		"extraHeaders":{0:Messages("experiments.inputs")}			 						 			
	});
	
	columns.push({
		"header" : Messages("containers.table.tags"),
		"property" : "inputContainer.contents",
		"order":true,
		"hide" : true,
		"type" : "text",
		"position" : 8.1,
		"filter":"getArray:\"properties.tag.value\"",
		"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}

	});
	
	columns.push({
		"header" : Messages("containers.table.volume") + " (µL)",
		"property" : "inputContainerUsed.volume.value",
		"order" : true,
		"edit" : false,
		"hide" : true,
		"type" : "number",
		"position" : 8.2,
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
		if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorBR1.value'
			|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilBR1.value'){
			computeConcentrationBR1(value.data);
    		computeConcentration1(value.data);
    		computeConcNm(value.data);
	 	}else if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorHS1.value'
	 		|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS1.value'){
			computeConcentrationHS1(value.data);
    		computeConcentration1(value.data);
    		computeConcNm(value.data);
	 	} else if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorHS2.value'
	 		|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS2.value'){
        	computeConcentrationHS2(value.data);
    		computeConcentration1(value.data);
    		computeConcNm(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.calculationMethod.value'){
    		computeConcentration1(value.data);
    		computeConcNm(value.data);
    	}else if(col.property === 'inputContainerUsed.experimentProperties.volume1.value'){
    		computeQuantity1(value.data);
     	}
		
	}
	
	
	
	var computeConcentrationBR1 = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.concentrationBR1.value");
		var concentration1 = getter(udtData);
		var compute = {
				conc1 : $parse("inputContainerUsed.experimentProperties.concentrationDilBR1.value")(udtData),
				dilution1 :  (($parse("inputContainerUsed.experimentProperties.dilutionFactorBR1.value")(udtData)).indexOf("1/") ==0 ? ($parse("inputContainerUsed.experimentProperties.dilutionFactorBR1.value")(udtData)).substring(2) : undefined ) ,
				isReady:function(){
					return (this.conc1 && this.dilution1);
				}
			};
		
		if(compute.isReady()){
			
			var result = $parse("(conc1 * dilution1)")(compute);
			console.log("computeConcentrationBR1 result = "+result);
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
	
	
	var computeConcentrationHS1 = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.concentrationHS1.value");
		var concentration1 = getter(udtData);
		var compute = {
				conc1 : $parse("inputContainerUsed.experimentProperties.concentrationDilHS1.value")(udtData),
				dilution1 :  (($parse("inputContainerUsed.experimentProperties.dilutionFactorHS1.value")(udtData)).indexOf("1/") ==0 ? ($parse("inputContainerUsed.experimentProperties.dilutionFactorHS1.value")(udtData)).substring(2) : undefined ) ,
				isReady:function(){
					return (this.conc1 && this.dilution1);
				}
			};
		
		if(compute.isReady()){
			
			var result = $parse("(conc1 * dilution1)")(compute);
			console.log("computeConcentrationHS1 result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				concentration1 = Math.round(result*10)/10;				
			}else{
				concentration1 = undefined;
			}	
			getter.assign(udtData, concentration1);
		}else{
			getter.assign(udtData, undefined);
			console.log("not ready to computeConcentrationHS1");
		}
		
	}
	
	var computeConcentrationHS2 = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.concentrationHS2.value");
		var concentration1 = getter(udtData);
		var compute = {
				conc1 : $parse("inputContainerUsed.experimentProperties.concentrationDilHS2.value")(udtData),
				dilution1 :  (($parse("inputContainerUsed.experimentProperties.dilutionFactorHS2.value")(udtData)).indexOf("1/") ==0 ? ($parse("inputContainerUsed.experimentProperties.dilutionFactorHS2.value")(udtData)).substring(2) : undefined ) ,
				isReady:function(){
					return (this.conc1 && this.dilution1);
				}
			};
		
		if(compute.isReady()){
			
			var result = $parse("(conc1 * dilution1)")(compute);
			console.log("computeConcentrationHS2 result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				concentration1 = Math.round(result*10)/10;				
			}else{
				concentration1 = undefined;
			}	
			getter.assign(udtData, concentration1);
		}else{
			getter.assign(udtData, undefined);
			console.log("not ready to computeConcentrationHS2");
		}
		
	}
	
	var computeConcentration1 = function(udtData){
		
		var getter = $parse("inputContainerUsed.experimentProperties.concentration1.value");
		var concentration1 = getter(udtData);
		
		var calMethod=$parse("inputContainerUsed.experimentProperties.calculationMethod.value")(udtData);
		if(calMethod){
			console.log("CalMethod")
			if(calMethod==="Moyenne des 2 HS"){
				
				var compute = {
						inputConcHS1 : $parse("inputContainerUsed.experimentProperties.concentrationHS1.value")(udtData),
						inputConcHS2 : $parse("inputContainerUsed.experimentProperties.concentrationHS2.value")(udtData),
						isReady:function(){
							return (this.inputConcHS1 && this.inputConcHS2);
						}
					};
				
				if(compute.isReady()){
					var result = $parse("(inputConcHS1 + inputConcHS2)/2")(compute);
					console.log("computeConcentration1 result Moyenne des 2 HS = "+result);
					if(angular.isNumber(result) && !isNaN(result)){
						concentration1 = Math.round(result*10)/10;				
					}else{
						concentration1 = undefined;
					}	
					getter.assign(udtData, concentration1);
				}else{
					console.log("not ready to concentration CalMethod moyenne 2 HS");
				}
				
			}else if(calMethod==="BR si > 25 et HS1 si BR <= 25"){
				var compute = {
						inputConcHS1 : $parse("inputContainerUsed.experimentProperties.concentrationHS1.value")(udtData),
						inputConcBR1 : $parse("inputContainerUsed.experimentProperties.concentrationBR1.value")(udtData),
						isReady:function(){
							return (this.inputConcHS1 && this.inputConcBR1);
						}
					};
				var concentration1;
				if(compute.isReady()){
					concentration1 = $parse("inputContainerUsed.experimentProperties.concentrationBR1.value")(udtData) >25 ? $parse("inputContainerUsed.experimentProperties.concentrationBR1.value")(udtData) :  $parse("inputContainerUsed.experimentProperties.concentrationHS1.value")(udtData); 
					console.log("computeConcentration1 result BR si > 25 et HS1 si BR <= 25 = "+result);
				}else {
					concentration1=undefined;
				}
					getter.assign(udtData,concentration1);
				
			}else if(calMethod==="BR 1 seul"){
				getter.assign(udtData,$parse("inputContainerUsed.experimentProperties.concentrationBR1.value")(udtData));

			}else if(calMethod==="HS 1 seul"){
				getter.assign(udtData,$parse("inputContainerUsed.experimentProperties.concentrationHS1.value")(udtData));

			}else if(calMethod==="HS 2 seul"){	
				getter.assign(udtData,$parse("inputContainerUsed.experimentProperties.concentrationHS2.value")(udtData));
			}else if(calMethod==="Non quantifiable"){	
				getter.assign(udtData,undefined);
			}
			else {	console.log("calMethod "+calMethod+" not implemented");}
			
			
		}
		
		computeQuantity1(udtData);
		
	}
	
	var computeQuantity1 = function(udtData){
		var getter= $parse("inputContainerUsed.experimentProperties.quantity1.value");
		var quantity1=getter(udtData);
		
		var compute = {
				inputVol1 : $parse("inputContainerUsed.experimentProperties.volume1.value")(udtData),
				inputConc1 : $parse("inputContainerUsed.experimentProperties.concentration1.value")(udtData),
				isReady:function(){
					return (this.inputVol1 && this.inputConc1);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("(inputVol1 * inputConc1)")(compute);
			console.log("computeQuantity1 result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
				quantity1 = Math.round(result*10)/10;				
			}else{
				quantity1 = undefined;
			}	
			getter.assign(udtData, quantity1);
		}else{
			getter.assign(udtData,undefined);
			console.log("not ready to computeQuantity1");
		}

	}
	
	/*
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
	*/
	
	
	
	var importData = function(typeQC){
		$scope.messages.clear();
		console.log("File :"+$scope.fileBR+", typeqc :"+typeQC);
		$http.post(jsRoutes.controllers.instruments.io.IO.importFile($scope.experiment.code).url+"?gamme="+typeQC, ($scope.fileBR===null || $scope.fileBR===undefined)?$scope.fileHS:$scope.fileBR)
		.success(function(data, status, headers, config) {
			$scope.messages.clazz="alert alert-success";
			$scope.messages.text=Messages('experiments.msg.import.success');
			$scope.messages.showDetails = false;
			$scope.messages.open();	
			//only atm because we cannot override directly experiment on scope.parent
			$scope.experiment.atomicTransfertMethods = data.atomicTransfertMethods;
			$scope.fileHS = undefined;
			$scope.fileBR = undefined;
			angular.element('#importFileHS')[0].value = null;
			angular.element('#importFileBR')[0].value = null;

			$scope.$emit('refresh');
			
		})
		.error(function(data, status, headers, config) {
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages('experiments.msg.import.error');
			$scope.messages.setDetails(data);
			$scope.messages.open();	
			$scope.fileHS = undefined;
			$scope.fileBR = undefined;
			angular.element('#importFileHS')[0].value = null;
			angular.element('#importFileBR')[0].value = null;

		});
	};
	
	$scope.button = {
		isShow:function(){
			return ("fluoroskan" === $scope.experiment.instrument.typeCode && !$scope.mainService.isEditMode() 
					&& ($scope.isInProgressState() || Permissions.check("admin")))
					 
			},
		isFileSetHS:function(){
			return ($scope.fileHS ===null || $scope.fileHS === undefined)?"disabled":"";
		},
		isFileSetBR:function(){
			return ($scope.fileBR === null || $scope.fileBR === undefined)?"disabled":"";
		},
		clickHS:function(){ return importData("HS");},
		clickBR:function(){ return importData("BR");}
	};
	
	
	var computeConcNm = function(udtData){
		var getter= $parse("inputContainerUsed.experimentProperties.nMcalculatedConcentration.value");
		var nmConc=getter(udtData);
		
		var compute = {
				conc : $parse ("inputContainerUsed.experimentProperties.concentration1.value")(udtData),
				size : $parse ("inputContainerUsed.size.value")(udtData),
				isReady:function(){
					return (this.conc && this.size);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("(conc / 660 / size * 1000000)")(compute);
			console.log("computeConcNm result = "+result);
			if(angular.isNumber(result) && !isNaN(result)){
			//	nmConc= Math.round(result*10)/10;	
				nmConc=result;
			}else{
				nmConc = undefined;
			}	
			getter.assign(udtData, nmConc);
		}else{
			getter.assign(udtData,undefined);
			console.log("not ready to nmolCalculatedQuantity");
		}

	}
	
	
}]);
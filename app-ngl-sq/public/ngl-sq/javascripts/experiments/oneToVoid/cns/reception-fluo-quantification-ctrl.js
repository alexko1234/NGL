angular.module('home')
.factory('fluoQuantificationCNSHelpers', ['$http', '$parse', '$filter', '$q', 'mainService', 
                                         function($http, $parse,$filter, $q, mainService){
	
	var helpers = {
			computeConcentration : function(udtData, concentrationPName, concentrationDilPName, dilutionFactorPName){
				var getter = $parse("inputContainerUsed.experimentProperties."+concentrationPName+".value");
				var concentration1 = getter(udtData);
				var compute = {
						conc1 : $parse("inputContainerUsed.experimentProperties."+concentrationDilPName+".value")(udtData),
						dilution1 :  (($parse("inputContainerUsed.experimentProperties."+dilutionFactorPName+".value")(udtData)).indexOf("1/") ==0 ? ($parse("inputContainerUsed.experimentProperties."+dilutionFactorPName+".value")(udtData)).substring(2) : undefined ) ,
						isReady:function(){
							return (this.conc1 && this.dilution1);
						}
					};
				
				if(compute.isReady()){
					
					var result = $parse("(conc1 * dilution1)")(compute);
					console.log(concentrationPName+" result = "+result);
					if(angular.isNumber(result) && !isNaN(result)){
						concentration1 = result;				
					}else{
						concentration1 = undefined;
					}	
					getter.assign(udtData, concentration1);
				}else{
					getter.assign(udtData, undefined);
					console.log("not ready to "+concentrationPName);
				}
			},
			
	
			computeConcentrationBR1 : function(udtData){
				this.computeConcentration(udtData, "concentrationBR1","concentrationDilBR1","dilutionFactorBR1");				
			},
			computeConcentrationHS1 : function(udtData){
				this.computeConcentration(udtData, "concentrationHS1","concentrationDilHS1","dilutionFactorHS1");				
			},
			computeConcentrationHS2 : function(udtData){
				this.computeConcentration(udtData, "concentrationHS2","concentrationDilHS2","dilutionFactorHS2");				
			},
			computeConcentrationHS3 : function(udtData){
				this.computeConcentration(udtData, "concentrationHS3","concentrationDilHS3","dilutionFactorHS3");
			},
			computeConcentration1 : function(udtData){
				
				var getter = $parse("inputContainerUsed.experimentProperties.concentration1.value");
				var concentration1 = getter(udtData);
				var compute = undefined;
				
				var calMethod=$parse("inputContainerUsed.experimentProperties.calculationMethod.value")(udtData);
				if(calMethod){
					console.log("CalMethod")
					if(calMethod==="Moyenne HS1 HS2"){
						
						compute = {
							inputConcHS1 : $parse("inputContainerUsed.experimentProperties.concentrationHS1.value")(udtData),
							inputConcHS2 : $parse("inputContainerUsed.experimentProperties.concentrationHS2.value")(udtData),
							isReady:function(){
								return (this.inputConcHS1 && this.inputConcHS2);
							},
							getResult : function(){
								return $parse("(inputConcHS1 + inputConcHS2)/2")(this);
							}
						};
						
					}else if(calMethod==="Moyenne HS2 HS3"){
						
						compute = {
							inputConcHS1 : $parse("inputContainerUsed.experimentProperties.concentrationHS2.value")(udtData),
							inputConcHS2 : $parse("inputContainerUsed.experimentProperties.concentrationHS3.value")(udtData),
							isReady:function(){
								return (this.inputConcHS1 && this.inputConcHS2);
							},
							getResult : function(){
								return $parse("(inputConcHS1 + inputConcHS2)/2")(this);
							}
						};
						
					}else if(calMethod==="Moyenne HS1 HS2 HS3"){
						
						compute = {
							inputConcHS1 : $parse("inputContainerUsed.experimentProperties.concentrationHS1.value")(udtData),
							inputConcHS2 : $parse("inputContainerUsed.experimentProperties.concentrationHS2.value")(udtData),
							inputConcHS3 : $parse("inputContainerUsed.experimentProperties.concentrationHS3.value")(udtData),
							
							isReady:function(){
								return (this.inputConcHS1 && this.inputConcHS2 && this.inputConcHS3);
							},
							getResult : function(){
								return $parse("(inputConcHS1 + inputConcHS2 + inputConcHS3)/3")(this);
							}
						};
						
					}else if(calMethod==="BR si > 25 et HS1 si BR <= 25"){
						compute = {
							inputConcHS1 : $parse("inputContainerUsed.experimentProperties.concentrationHS1.value")(udtData),
							inputConcBR1 : $parse("inputContainerUsed.experimentProperties.concentrationBR1.value")(udtData),
							isReady:function(){
								return (this.inputConcHS1 && this.inputConcBR1);
							},
							getResult : function(){
								return (this.inputConcBR1 > 25 ? this.inputConcBR1 : this.inputConcHS1);
							}							
						};						
					}else if(calMethod==="BR 1 seul"){
						compute = {
							inputConcBR1 : $parse("inputContainerUsed.experimentProperties.concentrationBR1.value")(udtData),
							isReady:function(){
								return (this.inputConcBR1);
							},
							getResult : function(){
								return this.inputConcBR1;
							}							
						};
					}else if(calMethod==="HS 1 seul"){
						compute = {
								inputConcHS1 : $parse("inputContainerUsed.experimentProperties.concentrationHS1.value")(udtData),
								isReady:function(){
									return (this.inputConcHS1);
								},
								getResult : function(){
									return this.inputConcHS1;
								}								
							};
					}else if(calMethod==="HS 2 seul"){	
						compute = {
								inputConcHS2 : $parse("inputContainerUsed.experimentProperties.concentrationHS2.value")(udtData),
								isReady:function(){
									return (this.inputConcHS2);
								},
								getResult : function(){
									return this.inputConcHS2;
								}								
							};
					}else if(calMethod==="HS 3 seul"){	
						compute = {
								inputConcHS3 : $parse("inputContainerUsed.experimentProperties.concentrationHS3.value")(udtData),
								isReady:function(){
									return (this.inputConcHS3);
								},
								getResult : function(){
									return this.inputConcHS3;
								}								
							};
					}else if(calMethod==="Non quantifiable"){	
						compute = {
								isReady:function(){
									return false
								},
								getResult : function(){
									return undefined;
								}								
							};
					}
					else {	throw ("calMethod "+calMethod+" not implemented");}
					
					
					if(compute.isReady()){
						var result = compute.getResult(udtData);
						if(angular.isNumber(result) && !isNaN(result)){
							concentration1 = result;				
						}else{
							concentration1 = undefined;
						}	
						getter.assign(udtData, concentration1);
					}else{
						console.log("not ready to compute concentration");
						getter.assign(udtData,undefined);
					}
					
				}
				
				this.computeQuantity1(udtData);
				
			},
			computeQuantity1 : function(udtData){
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

			},
			computeConcNm : function(udtData){
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

			},
			hideColumns:function(atmService, visualChoice){
				var udt = atmService.data;
				var columns = udt.getColumnsConfig(columns);
				
				for(var i = 13; i<29 ; i++){
					var selectedColumns = $filter('filter')(columns,{position:i});
					if(selectedColumns.length === 1){
						var column = selectedColumns[0];
						
						if(("Tout" === visualChoice && i >=12 && i <= 28 
								|| "BR1 + HS1"  === visualChoice && i >=12 && i <= 20
								|| "HS1 + HS2 + HS3"  === visualChoice && i >=17 && i <= 28)
								&& udt.isHide(column.id)
								){
							udt.setHideColumn(column);
						}else if( ("BR1 + HS1"  === visualChoice && i > 20
								|| "HS1 + HS2 + HS3"  === visualChoice && i < 17) && !udt.isHide(column.id) ){
							udt.setHideColumn(column);
						}						
					}
				}				
			}
			
			
			
	}
	
	
	return helpers;
}]);

angular.module('home').controller('OneToVoidFluoQuantificationCNSCtrl',['$scope', '$parse','$http','fluoQuantificationCNSHelpers',
                                                             function($scope,$parse,$http,fluoQuantificationCNSHelpers) {
	
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
			fluoQuantificationCNSHelpers.computeConcentrationBR1(value.data);
			fluoQuantificationCNSHelpers.computeConcentration1(value.data);
			fluoQuantificationCNSHelpers.computeConcNm(value.data);
	 	}else if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorHS1.value'
	 		|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS1.value'){
	 		fluoQuantificationCNSHelpers.computeConcentrationHS1(value.data);
	 		fluoQuantificationCNSHelpers.computeConcentration1(value.data);
	 		fluoQuantificationCNSHelpers.computeConcNm(value.data);
	 	}else if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorHS2.value'
	 		|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS2.value'){
	 		fluoQuantificationCNSHelpers.computeConcentrationHS2(value.data);
	 		fluoQuantificationCNSHelpers.computeConcentration1(value.data);
	 		fluoQuantificationCNSHelpers.computeConcNm(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorHS3.value'
	 		|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS3.value'){
	 		fluoQuantificationCNSHelpers.computeConcentrationHS3(value.data);
	 		fluoQuantificationCNSHelpers.computeConcentration1(value.data);
	 		fluoQuantificationCNSHelpers.computeConcNm(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.calculationMethod.value'){
			fluoQuantificationCNSHelpers.computeConcentration1(value.data);
			fluoQuantificationCNSHelpers.computeConcNm(value.data);
    	}else if(col.property === 'inputContainerUsed.experimentProperties.volume1.value'){
    		fluoQuantificationCNSHelpers.computeQuantity1(value.data);
     	}
		
	}
	
	
	
	
	
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
	
	$scope.$watch("experiment.experimentProperties.displayChoice.value", function(newValue, oldValue){
		if (newValue !== oldValue)  {
			fluoQuantificationCNSHelpers.hideColumns($scope.atmService, newValue);
		}
	});
	
	if($scope.experiment.experimentProperties.displayChoice.value){
		fluoQuantificationCNSHelpers.hideColumns($scope.atmService, $scope.experiment.experimentProperties.displayChoice.value);
	}
	
}]);



angular.module('home').controller('OneToVoidReceptionFluoQuantificationCNSCtrl',['$scope', '$parse','$http','fluoQuantificationCNSHelpers',
                                                             function($scope,$parse,$http,fluoQuantificationCNSHelpers) {
	
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
		"header" : Messages("containers.table.libraryToDo"),
		"property" : "inputContainerUsed.contents",
		"filter" : "getArray:'processProperties.libraryToDo.value' | unique ",
		"order" : true,
		"edit" : false,
		"hide" : true,
		"type" : "text",
		"position" : 8.1,
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}
	});
	
	$scope.atmService.data.setColumnsConfig(columns);
	
	
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorBR1.value'
			|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilBR1.value'){
			fluoQuantificationCNSHelpers.computeConcentrationBR1(value.data);
			fluoQuantificationCNSHelpers.computeConcentration1(value.data);
	 	}else if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorHS1.value'
	 		|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS1.value'){
	 		fluoQuantificationCNSHelpers.computeConcentrationHS1(value.data);
	 		fluoQuantificationCNSHelpers.computeConcentration1(value.data);
	 	}else if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorHS2.value'
	 		|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS2.value'){
	 		fluoQuantificationCNSHelpers.computeConcentrationHS2(value.data);
	 		fluoQuantificationCNSHelpers.computeConcentration1(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.dilutionFactorHS3.value'
	 		|| col.property === 'inputContainerUsed.experimentProperties.concentrationDilHS3.value'){
	 		fluoQuantificationCNSHelpers.computeConcentrationHS3(value.data);
	 		fluoQuantificationCNSHelpers.computeConcentration1(value.data);
		}else if(col.property === 'inputContainerUsed.experimentProperties.calculationMethod.value'){
			fluoQuantificationCNSHelpers.computeConcentration1(value.data);
    	}else if(col.property === 'inputContainerUsed.experimentProperties.volume1.value'){
    		fluoQuantificationCNSHelpers.computeQuantity1(value.data);
    	}
		
	}
	
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
	
	$scope.$watch("experiment.experimentProperties.displayChoice.value", function(newValue, oldValue){
		if (newValue !== oldValue)  {
			fluoQuantificationCNSHelpers.hideColumns($scope.atmService, newValue);
		}
	});
	
	if($scope.experiment.experimentProperties.displayChoice.value){
		fluoQuantificationCNSHelpers.hideColumns($scope.atmService, $scope.experiment.experimentProperties.displayChoice.value);
	}
}]);
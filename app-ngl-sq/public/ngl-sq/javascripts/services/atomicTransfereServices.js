angular.module('atomicTransfereServices', [])
.factory('experimentCommonFunctions', ['$rootScope','$http', '$parse', '$q','mainService',  function($rootScope, $http, $parse, $q, mainService){
/*  This module store all the common function between all the experiments such as:
 *  init a new experiment (loading containers from the basket...) with the function newExperiment that take a function in parameter (launched after the load of the containers)
 *  newExperimentDatatable is a wrapper of newExperiment that pass the function that set the data of the datatable you give it to
 *  containersToContainerUseds take a list of containers and convert it to a list of containerUseds
 *  newExperimentDragndrop  is a wrapper of newExperiment that pass the function that convert the containers in containerUseds
 *  removeNullProperties take a list of properties and set the null properties to undefined in order to not send them to the controllers
 *  loadContainer take a list of containerUseds and load the containers from the server, it return an object results containing,
 *   results.promise (the promise) and results.containers(the containers when the promise is ok
 */
	var constructor = function($scope){
		var common = {
				newExperiment05082015: function(fn){
					console.log("used newExperiment05082015 instead of newExperiment");
					var containers = [];
					var promises = [];
					$scope.basket = mainService.getBasket().get();
					angular.forEach($scope.basket, function(basket){
						var promise = $http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{code:basket.code}})
						.success(function(data, status, headers, config) {
							$scope.clearMessages();
							if(data!=null){
								angular.forEach(data, function(container){
									containers.push(container);
								});
							}
						})
						.error(function(data, status, headers, config) {
							alert("error");
						});
						promises.push(promise);
					});
					//var that = this;
					$q.all(promises).then(function (res) {
						fn(containers);
						$scope.doPurifOrQc($scope.experiment.value.typeCode);
						if(!$scope.experiment.editMode) {
							$scope.init_experiment(containers, $scope.experimentType.atomicTransfertMethod);
						}else{
							$scope.addExperimentPropertiesInputsColumns();
						}
						
						$scope.addExperimentPropertiesOutputsColumns();
						$scope.addInstrumentPropertiesOutputsColumns();
					});
				},
				
				newExperiment: function(fn){
					var containers = [];
					var promises = [];
					$scope.basket = mainService.getBasket().get();
					
					var containerInputCodes = [];
					angular.forEach($scope.basket, function(containerInput) {
					  this.push(containerInput.code);
					}, containerInputCodes);
					
					var promise = $http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{codes:containerInputCodes}})
						.success(function(data, status, headers, config) {
							$scope.clearMessages();
							if(data!=null){
								angular.forEach(data, function(container){
									containers.push(container);
								});
							}
						})
						.error(function(data, status, headers, config) {
							alert("error");
						});
					
					promises.push(promise);
				
					//var that = this;
					$q.all(promises).then(function (res) {
						fn(containers);
						//$scope.doPurifOrQc($scope.experiment.value.typeCode); //move to createExperimentService pas utile maintenant dixit Yann Deshayes le 05/08/2015
						if(!$scope.experiment.editMode) {
							//plus utile
							$scope.init_experiment(containers, $scope.experimentType.atomicTransfertMethod);
						}else{
							//$scope.addExperimentPropertiesInputsColumns();
							throw 'probleme !!!! ne devrait pas passer par là lors de la création';
						}
						
						//$scope.addExperimentPropertiesOutputsColumns(); //pas utile ici
						//$scope.addInstrumentPropertiesOutputsColumns(); //pas utile ici
					});
				},
				newExperimentDatatable : function(datatable){ //TODO Obsolète
					var that = this;
					this.newExperiment05082015(function(containers){
						datatable.setData(containers,containers.length);
					});
				},
				containersToContainerUseds : function(containers){
					var containerUseds = [];
					var that = this;
					angular.forEach(containers, function(container){
						containerUseds.push(that.containerToContainerUsed(container));
					});

					return containerUseds;
				},
				containerToContainerUsed : function(container){
					var mesuredVolume = 0;
					if(container.mesuredVolume !== undefined){
						mesuredVolume = container.mesuredVolume;
					}
					return {"code":container.code, "state":container.state, "instrumentProperties":{},"experimentProperties":{}, "fromExperimentTypeCodes":container.fromExperimentTypeCodes,
						"percentage":100, "categoryCode":container.categoryCode, "volume":mesuredVolume,
						"concentration":container.mesuredConcentration, "contents":container.contents, "locationOnContainerSupport":container.support};
				},
				newExperimentDragndrop : function(){
					var that = this;
					this.newExperiment05082015(function(containers){
						$scope.inputContainers = that.containersToContainerUseds(containers);
					});
				},
				removeNullProperties : function(properties){
					for (var p in properties) {
						if(properties[p] != undefined && (properties[p].value === undefined || properties[p].value === null || properties[p].value === "")){
							properties[p] = undefined;
						}
					}
				},
				loadContainer : function(containerUsed){
					var results = {container:{},promise:{}};
					var line = 1;
					var column = 1;

					if(containerUsed.locationOnContainerSupport != undefined){
						line = containerUsed.locationOnContainerSupport.line;
						column = containerUsed.locationOnContainerSupport.column;
					}
					if(containerUsed.code !== null && containerUsed.code !== undefined){
						results.promise = $http.get(jsRoutes.controllers.containers.api.Containers.list().url, {params:{"code":containerUsed.code, "line":line, "column":column}})
							.success(function(data, status, headers, config) {
								if(data!=null){
									results.container = data[0];
								}
							})
							.error(function(data, status, headers, config) {
								alert("error");
							});
					}
					return results;
				}

		};
		return common;
	};
	return constructor;
}]).
factory('oneToX', ['$rootScope','experimentCommonFunctions', function($rootScope, experimentCommonFunctions){

	var constructor = function($scope, inputType){
		var inputType = inputType;
		var varExperimentCommonFunctions = undefined;

		var init = function(){
			varExperimentCommonFunctions = experimentCommonFunctions($scope);
		};

		var oneToX = {
				/*
				experimentToInput : function(input){
					if(inputType === "datatable"){
						var allData = input.getData();
						if(allData != undefined){
							for(var i=0;i<allData.length;i++){
								var position = this.searchInputContainerUsedPosition(allData[i].code);
								if($scope.experiment.value.atomicTransfertMethods[position] != null && angular.isDefined($scope.experiment.value.atomicTransfertMethods[position].inputContainerUseds[0])){
									allData[i].inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[position].inputContainerUseds[0].instrumentProperties;
									allData[i].inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[position].inputContainerUseds[0].experimentProperties;
									allData[i].inputContainerUsed =  $scope.experiment.value.atomicTransfertMethods[position].inputContainerUseds[0];
									allData[i].contents = $scope.experiment.value.atomicTransfertMethods[position].inputContainerUseds[0].contents;											
								}										
							}
							input.setData(allData, allData.length);
						}
					}
				},
				*/
				
				inputToExperiment : function(input){
					if(inputType === "datatable"){
						var allData = input.getData();
						if(allData != undefined){
							for(var i=0;i<allData.length;i++){
								var atomicIndex = allData[i].atomicIndex;
								$scope.experiment.value.atomicTransfertMethods[atomicIndex] = allData[i].atomicTransfertMethod
								$scope.experiment.value.atomicTransfertMethods[atomicIndex].inputContainerUseds[0] = allData[i].inputContainerUsed;									
							}
						}
					}
				},
				
				loadInputContainers : function(ContainerUseds){
					var results = {containers:[],promises:[]};
					angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
						var result = varExperimentCommonFunctions.loadContainer(atomicTransfertMethod.inputContainerUseds[0]);
						results.promises.push(result.promise);
						result.promise.then(function(container){
							results.containers.push(container.data[0]);
						});
					});

					return results;
				}
		};
		init();
		return oneToX;
	};
	return constructor;
}]).factory('manyToX', ['$rootScope','experimentCommonFunctions', function($rootScope, experimentCommonFunctions){

	var constructor = function($scope, inputType){
		var inputType = inputType;
		var varExperimentCommonFunctions = undefined;

		var init = function(){
			varExperimentCommonFunctions = experimentCommonFunctions($scope);
		};

		var manyToX = {
				experimentToInput : function(input){
					if(inputType === "datatable" || inputType === "dragndrop"){
						var allData = input.getData();
						if(allData != undefined){
							var i = 0;
							for(var k=0;k<$scope.experiment.value.atomicTransfertMethods.length;k++){
								for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[k].inputContainerUseds.length;j++){
									allData[i].inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[k].inputContainerUseds[j].instrumentProperties;
									allData[i].inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[k].inputContainerUseds[j].experimentProperties;
									allData[i].inputContainerUsed =  $scope.experiment.value.atomicTransfertMethods[k].inputContainerUseds[j];
									allData[i].contents = $scope.experiment.value.atomicTransfertMethods[k].inputContainerUseds[j].contents;
									i++;
								}
							}
							input.setData(allData, allData.length);
						}
					}
				},
				searchContainer : function(code, x, y){
					var i = 0;
					while($scope.experiment.value.atomicTransfertMethods[i] != undefined){
						for(var j=0;j<$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length;j++){
							if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].code == code && $scope.experiment.value.atomicTransfertMethods[i].line == x && $scope.experiment.value.atomicTransfertMethods[i].column == y){
								return {"x":i,"y":j};
							}
						}
						i++;
					}
					return {};
				},
				inputToExperiment : function(input){
					if(inputType === "datatable"){
						var allData = input.getData();
						if(allData != undefined){
							for(var i=0;i<allData.length;i++){
								for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.length;j++){											
									if(angular.isDefined(allData[i].inputContainerUsed) && angular.isDefined(allData[i].inputContainerUsed.percentage)){
										$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].percentage = allData[i].inputContainerUsed.percentage;
									}											
									$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].contents = allData[i].contents;
									$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].instrumentProperties = allData[i].inputInstrumentProperties;
									$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].experimentProperties = allData[i].inputExperimentProperties;											

									varExperimentCommonFunctions.removeNullProperties($scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].instrumentProperties);
									varExperimentCommonFunctions.removeNullProperties($scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].experimentProperties);

									i++;
								}
							}
							input.setData(allData, allData.length);
						}
					}else if(inputType === "dragndrop"){
						var allData = input.getData();
						if(allData != undefined){
							for(var i=0;i<allData.length;i++){
								//$scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].inputContainerUseds[($scope.datatable.displayResult[i].data.outputPositionY-1)].percentage = $scope.datatable.displayResult[i].data.percentage;
								var positions = this.searchContainer(allData[i].inputCode, allData[i].outputPositionX, allData[i].outputPositionY);
								if(angular.isDefined(positions) && angular.isDefined(positions.x) && angular.isDefined(positions.y)){
									if(allData[i].inputInstrumentProperties != undefined){											
										$scope.experiment.value.atomicTransfertMethods[positions.x].inputContainerUseds[positions.y].instrumentProperties = allData[i].inputInstrumentProperties;
									}
									if(allData[i].inputExperimentProperties != undefined){											
										$scope.experiment.value.atomicTransfertMethods[positions.x].inputContainerUseds[positions.y].experimentProperties = allData[i].inputExperimentProperties;
									}

								}

							}
							input.setData(allData, allData.length);
						}
					}
				},
				loadInputContainers : function(ContainerUseds){
					var results = {containers:[],promises:[]};
					var that = this;
					angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
						angular.forEach(atomicTransfertMethod.inputContainerUseds, function(inputContainerUsed){
							var result = varExperimentCommonFunctions.loadContainer(inputContainerUsed);
							results.promises.push(result.promise);
							result.promise.then(function(container){
								results.containers.push(container.data[0]);
							});

						});
					});
					return results;
				}
		};
		init();
		return manyToX;
	};
	return constructor;
}]).factory('xToOne', ['$rootScope','experimentCommonFunctions', function($rootScope, experimentCommonFunctions){

	var constructor = function($scope, outputType){
		var outputType = outputType;

		var varExperimentCommonFunctions = undefined;

		var init = function(){
			varExperimentCommonFunctions = experimentCommonFunctions($scope);
		};

		var xToOne = {
				getVarExperimentCommonFunctions : function(properties){
					return varExperimentCommonFunctions.removeNullProperties(properties);
				},
				/* not necessary
				experimentToOutput : function(output){
					if(outputType === "none" || outputType === "datatable"){							
						var allData = output.getData();
						if(angular.isDefined(allData) && allData.length>0){
							for(var i=0; i<allData.length;i++){
								var position = this.searchOutputPositionByInputContainerCode(allData[i].code || allData[i].inputCode);
								if(angular.isDefined($scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0])){
									allData[i].outputContainerUsed  = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0];
									allData[i].outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0].instrumentProperties;
									allData[i].outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[position].outputContainerUseds[0].experimentProperties;
								}										
							}
							output.setData(allData,allData.length)
						}
						
					}
				},
				*/
				/*not used 
				searchOutputContainerUsedPosition : function(outputContainercode){
					var i = 0;
					while($scope.experiment.value.atomicTransfertMethods[i] != undefined){							
						if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].code === outputContainercode){
							return i;
						}							
						i++;
					}
					return undefined;
				},
				*/
				/*not necessary
				searchOutputPositionByInputContainerCode : function(inputContainerCode){
					for(var i=0;i<$scope.experiment.value.atomicTransfertMethods.length;i++){
						for(var j=0;j<$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length;j++){
							if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].code === inputContainerCode){
								return i;
							}
						}
					}
					return undefined;
				},
				searchInputContainerUsedPosition: function(inputContainerCode){
					var j=0;
					while($scope.experiment.value.atomicTransfertMethods[j]){
						if($scope.experiment.value.atomicTransfertMethods[j].inputContainerUseds[0].code===inputContainerCode){
							return j;
						}
						j++
					}
					return undefined;
				},
				*/
				/* not used
				findOutputContainer : function(code, containers){						
					var j = -1;
					var i =0;
					while(containers[i]){
						if(code !== null && angular.isDefined(containers[i].outputContainerUseds) && (code === containers[i].outputContainerUseds[0].code)){
							j++;
						}
						i++;
					}
					return j;

				},	
				*/								
				outputToExperiment : function(output){
					if(outputType === "none" || outputType === "datatable"){
						var allData = output.getData();
						if(allData != undefined){
							for(var i=0;i<allData.length;i++){
								var atomicIndex = allData[i].atomicIndex;
								
								$scope.experiment.value.atomicTransfertMethods[atomicIndex] = allData[i].atomicTransfertMethod
								$scope.experiment.value.atomicTransfertMethods[atomicIndex].outputContainerUseds[0] = allData[i].outputContainerUsed;																								
							}
						}
					}
				},
				loadOutputContainers : function(ContainerUseds){
					var results = {containers:[],promises:[]};
					var that = this;
					angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
						if(atomicTransfertMethod.outputContainerUseds[0] != undefined && atomicTransfertMethod.outputContainerUseds[0].code != null){
							var result = varExperimentCommonFunctions.loadContainer(atomicTransfertMethod.outputContainerUseds[0]);
							results.promises.push(result.promise);
							result.promise.then(function(container){
								if(container.data.length > 0){
									results.containers = results.containers.concat(container.data);
								}
							});
						}
					});
					return results;
				}
		};
		init();
		return xToOne;
	};
	return constructor;
}]).factory('xToMany', ['$rootScope','experimentCommonFunctions', function($rootScope,experimentCommonFunctions){

	var constructor = function($scope, outputType){
		var outputType = outputType;

		var varExperimentCommonFunctions = undefined;

		var init = function(){
			varExperimentCommonFunctions = experimentCommonFunctions($scope);
		};
		
		var xToMany = {
				experimentToOutput : function(input){
					if(outputType === "none"){
						var allData = input.getData();
						if(allData != undefined){
							for(var i=0;i<allData.length;i++){
								if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
									allData[i].outputContainerUseds = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds;
									for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
										allData[i].outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].instrumentProperties;
										allData[i].outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].experimentProperties;
										i++;
									}
								}
							}
						}
					}
				},
				outputToExperiment : function(input){
					if(outputType === "none"){
						var allData = input.getData();
						if(allData != undefined){
							for(var i=0;i<allData.length;i++){
								if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds!=undefined){
									$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds = allData[i].outputContainerUseds;
									for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
										$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].instrumentProperties = allData[i].outputInstrumentProperties;
										$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].experimentProperties = allData[i].outputExperimentProperties;
										varExperimentCommonFunctions.removeNullProperties($scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].experimentProperties);
										varExperimentCommonFunctions.removeNullProperties($scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].instrumentProperties);
										i++;
									}
								}
							}
						}
					}
				},
				loadOutputContainers : function(containerUseds){
					var results = {containers:[],promises:[]};
					var that = this;
					angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
						/*if(atomicTransfertMethod.outputContainerUseds[0] != undefined && atomicTransfertMethod.outputContainerUseds[0].code != null){
							var result = varExperimentCommonFunctions.loadContainer(atomicTransfertMethod.outputContainerUseds[0]);
							results.promises.push(result.promise);
							result.promise.then(function(container){
								if(container.data.length > 0){
									results.containers = results.containers.concat(container.data);
								}
							});
						}*/
						var i = 0;
						for(i=0;i<containerUseds.length;i++){
							if(containerUseds !== undefined && containerUseds !== null && containerUseds.code !== undefined && containerUseds.code !== null){
								var result = varExperimentCommonFunctions.loadContainer(containerUseds[i]);
								results.promises.push(result.promise);
								result.promise.then(function(container){
									if(container.data.length > 0){
										results.containers = results.containers.concat(container.data);
									}
								});
							}
						}
					});
					return results;
				}
		};
		init();
		return xToMany;
	};
	return constructor;
}]).factory('oneToOne', ['$rootScope','oneToX','xToOne', '$http', '$parse', '$q', 'experimentCommonFunctions','mainService', 
                         function($rootScope, oneToX, xToOne, $http, $parse, $q, experimentCommonFunctions,mainService){

	var constructor = function($scope, inputType, outputType){
		//var inputType = inputType;
		//var outputType = outputType;
		//var varOneToX = undefined;
		//var varXToOne = undefined;

		var experimentCommons = experimentCommonFunctions($scope);
/*
		var init = function(){
			//varOneToX = oneToX($scope, inputType);
			//varXToOne = xToOne($scope, outputType);
			varexperimentCommonFunctions = experimentCommonFunctions($scope);
		};
*/
		var oneToOne = {
//				experimentToInput : function(input){
//					varOneToX.experimentToInput(input);
//				},
				
//				experimentToOutput : function(output){
//					varXToOne.experimentToOutput(output);
//				},
				
//				searchOutputPositionByInputContainerCode : function(inputContainerCode){
//					return varXToOne.searchOutputPositionByInputContainerCode(inputContainerCode);
//				},
//				inputToExperiment : function(input){
//					varOneToX.inputToExperiment(input);
//				},
//				outputToExperiment : function(output){
//					varXToOne.outputToExperiment(output);
//				},
				
				deleteDatatableColumnFromHeader : function(datatable, header){
					var columns = datatable.getColumnsConfig();
					
					angular.forEach(columns, function(column, index){
						if(column.extraHeaders != undefined && column.extraHeaders[1] == header){
							$scope.datatable.deleteColumn(index);
						}
					});
				},
				
				
				outputType : outputType,
				
				newAtomicTransfertMethod : function(){
					return {
						class:"OneToOne",
						line:"1", 
						column:"1", 
						inputContainerUseds:new Array(1), 
						outputContainerUseds:new Array(1)
					};
				},
				//Common for all but try to replace slowly
				convertContainerToInputContainerUsed : function(container){
					return {
							percentage:100, //rules by defaut need check with server
							code:container.code,
							instrumentProperties:{},
						    experimentProperties:{},
						    //can be updated
						    categoryCode:container.categoryCode, 
						    volume:container.mesuredVolume, //used in rules
							concentration:container.mesuredConcentration,  //used in rules
							quantity:container.mesuredQuantity,
							contents:container.contents, //used in rules							
						    locationOnContainerSupport:container.support,
						    fromExperimentTypeCodes:container.fromExperimentTypeCodes
					};
					
					/*
					 return {"state":container.state
						}; 
					 
					 */
				},
				
				updateContainerUsedFromContainer : function(containerUsed, container){
					containerUsed.categoryCode = container.categoryCode; 
					containerUsed.volume = container.mesuredVolume;
					containerUsed.concentration = container.mesuredConcentration;
					containerUsed.quantity = container.mesuredQuantity;
					containerUsed.contents = container.contents;
					containerUsed.locationOnContainerSupport = container.support;
					containerUsed.fromExperimentTypeCodes = container.fromExperimentTypeCodes;
					return containerUsed;
				},
				
				newOutputContainerUsed : function(){
					return {
						code:undefined,
						volume:{unit:"µL"}, //not best place to put unit
						concentration:{unit:"nM"}, //not best place to put unit
						instrumentProperties:{},
					    experimentProperties:{}
					};
				},
				
				getContainerListPromise : function(containerCodes){
					if(containerCodes.length > 0){
						 return $http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{codes:containerCodes}, atomicObject:this});		
					}else{
						return $q(function(resolve, reject){
							resolve({data:[]}); //empty array
						});
					}								 				 	
				},
				
				loadInputContainerFromAtomicTransfertMethods : function(atomicTransfertMethods){
					var containerInputCodes = [];
					angular.forEach(atomicTransfertMethods, function(atomicTransfertMethod) {
						angular.forEach(atomicTransfertMethod.inputContainerUseds, function(inputContainerUsed){
								this.push(inputContainerUsed.code);
								}, this);														
						}, containerInputCodes);
					return this.getContainerListPromise(containerInputCodes).then(function(result){
				 		var inputContainers = {};
				 		angular.forEach(result.data, function(container) {
							this[container.code] = container;
							}, inputContainers);
						return {"input":inputContainers};
				 	});
				},
				
				loadOutputContainerFromAtomicTransfertMethods : function(atomicTransfertMethods){
					var containerOutpuCodes = [];
					angular.forEach(atomicTransfertMethods, function(atomicTransfertMethod) {
						angular.forEach(atomicTransfertMethod.outputContainerUseds, function(outputContainerUsed){
								if(null !== outputContainerUsed.code && undefined !== outputContainerUsed.code){
									this.push(outputContainerUsed.code);
								}									
								}, this);														
						}, containerOutpuCodes);
															
					return this.getContainerListPromise(containerOutpuCodes).then(function(result){
						var outputContainers = {};
						angular.forEach(result.data, function(container) {
							this[container.code] = container;
							}, outputContainers);
				 		return {"output":outputContainers};
				 	});
				},
				
				loadInputContainerFromBasket : function(basketValues){					
						var containerInputCodes = [];
						angular.forEach(basketValues, function(containerInput) {
							this.push(containerInput.code);
						}, containerInputCodes);
						return this.getContainerListPromise(containerInputCodes).then(function(result){					 		
							return result.data;
					 	});					
				},
				
				convertExperimentToDatatable : function(datatable){
					var promises = [];
					promises.push(this.loadInputContainerFromAtomicTransfertMethods($scope.experiment.value.atomicTransfertMethods));
					promises.push(this.loadOutputContainerFromAtomicTransfertMethods($scope.experiment.value.atomicTransfertMethods));
									 
                                      
                    var $that = this;
                    $q.all(promises).then(function (result) {
		            		var allData = [];
		                 	var inputContainers, outputContainers;
		            		if(result[0].input && result[1].output){
		            			inputContainers = result[0].input;
		            			outputContainers = result[1].output;
		            		}else if(result[0].output && result[1].input){
		            			inputContainers = result[1].input;
		            			outputContainers = result[0].output;
		            		}else{
		            			throw "not managed";
		            		}
                    		
                    		var l=0;
							for(var i=0; i<$scope.experiment.value.atomicTransfertMethods.length;i++){
							 //in this case you can begin by output or input. it's same because same number one to one
							       for(var j=0; j<$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds.length;j++){
							              var outputContainerCode = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].code;
							              var inputContainerCode = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0].code;
							              allData[l] = {atomicIndex:i};
							              allData[l].atomicTransfertMethod = angular.copy($scope.experiment.value.atomicTransfertMethods[i]);
							              
							              allData[l].inputContainerUsed = angular.copy($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0]);
							              allData[l].inputContainerUsed = $that.updateContainerUsedFromContainer(allData[l].inputContainerUsed, inputContainers[inputContainerCode]);
							              allData[l].inputContainer = inputContainers[inputContainerCode];							              
							              
							              allData[l].outputContainerUsed = angular.copy($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j]);
							              allData[l].outputContainer = outputContainers[outputContainerCode];
							              
							              l++;
							        }
							}
							datatable.setData(allData, allData.length);
							//add new atomic in datatable
							$that.addNewAtomicTransfertMethodsInDatatable(datatable);
                          
                    });
				},
				addNewAtomicTransfertMethodsInDatatable : function(datatable){
					if(null != mainService.getBasket() && null != mainService.getBasket().get()){
						var $that = this;
						this.loadInputContainerFromBasket(mainService.getBasket().get())
							.then(function(containers) {								
								var allData = [], i = 0;
								if(datatable.getData() !== undefined){
									allData = datatable.getData();
									i = allData.length;
								}
								
								angular.forEach(containers, function(container){
									var line = {};
									line.atomicIndex=i++;
									line.atomicTransfertMethod = $that.newAtomicTransfertMethod();
									line.inputContainer = container;
									line.inputContainerUsed = $that.convertContainerToInputContainerUsed(line.inputContainer);
									line.outputContainerUsed = $that.newOutputContainerUsed();
									line.outputContainer = undefined;
									allData.push(line);
								});
								datatable.setData(allData, allData.length);								
						});
					}					
				},
				viewToExperiment:function(view){
					if(outputType === "datatable"){
						var allData = view.getData();
						if(allData != undefined){
							$scope.experiment.value.atomicTransfertMethods = []; // to manage remove
							for(var i=0;i<allData.length;i++){
								var atomicIndex = allData[i].atomicIndex;								
								$scope.experiment.value.atomicTransfertMethods[atomicIndex] = allData[i].atomicTransfertMethod
								$scope.experiment.value.atomicTransfertMethods[atomicIndex].inputContainerUseds[0] = allData[i].inputContainerUsed;	
								
								$scope.experiment.value.atomicTransfertMethods[atomicIndex].outputContainerUseds[0] = allData[i].outputContainerUsed;
								
								experimentCommons.removeNullProperties($scope.experiment.value.atomicTransfertMethods[atomicIndex].inputContainerUseds[0].instrumentProperties);
								experimentCommons.removeNullProperties($scope.experiment.value.atomicTransfertMethods[atomicIndex].inputContainerUseds[0].experimentProperties);

								experimentCommons.removeNullProperties($scope.experiment.value.atomicTransfertMethods[atomicIndex].outputContainerUseds[0].instrumentProperties);
								experimentCommons.removeNullProperties($scope.experiment.value.atomicTransfertMethods[atomicIndex].outputContainerUseds[0].experimentProperties);

							}
							//remove atomic null
							var cleanAtomicTransfertMethods = [];
							for(var i = 0; i < $scope.experiment.value.atomicTransfertMethods.length ; i++){
								if($scope.experiment.value.atomicTransfertMethods[i] !== null){
									cleanAtomicTransfertMethods.push($scope.experiment.value.atomicTransfertMethods[i]);
								}
							}
							$scope.experiment.value.atomicTransfertMethods = cleanAtomicTransfertMethods;
						}
					}else{
						throw "not managed";
					}					
				},
				experimentToView:function(view){
					if(inputType === "datatable"){
						if($scope.experiment.editMode){
							this.convertExperimentToDatatable(view);													
						}else{
							this.addNewAtomicTransfertMethodsInDatatable(view);
						}
						$scope.addOutputColumns();
                        $scope.addExperimentPropertiesOutputsColumns();
                        $scope.addExperimentPropertiesInputsColumns();
                        $scope.addInstrumentPropertiesOutputsColumns();
						
					}else{
						throw 'not implemented';
					}
				},
				
				refreshViewFromExperiment : function(view){
					if(inputType === "datatable"){
						this.convertExperimentToDatatable(view);
					}else{
						throw 'not implemented';
					}
				},
				
				
				/* Old Version
				newExperiment : function(input){
					if(inputType === "datatable"){
						varexperimentCommonFunctions.newExperimentDatatable(input);
						$scope.addExperimentPropertiesInputsColumns();
					}							
				},
				*/
		};
		return oneToOne;
	};
	return constructor;
}]).factory('oneToMany', ['$rootScope', 'oneToX','xToMany','$http', '$parse', '$q', 'experimentCommonFunctions', function($rootScope, oneToX, xToMany, $http, $parse, $q, experimentCommonFunctions){

	var constructor = function($scope, inputType, outputType){
		var inputType = inputType;
		var outputType = outputType;
		var varOneToX = undefined;
		var varXToMany = undefined;
		var varExperimentCommonFunctions = undefined;

		var init = function(){
			varOneToX = oneToX($scope, inputType);
			varXToMany = xToMany($scope, outputType);
			varExperimentCommonFunctions = experimentCommonFunctions($scope);
		};

		var oneToMany = {
				experimentToInput : function(input){
					varOneToX.experimentToInput(input);
				},
				inputToExperiment : function(input){
					varOneToX.inputToExperiment(input);
				},
				experimentToOutput : function(output){
					varXToMany.experimentToOutput(output);
				},
				outputToExperiment : function(output){
					varXToMany.outputToExperiment(output);
				},
				newExperiment : function(input){
					if(inputType === "datatable"){
						//varExperimentCommonFunctions.newExperimentDatatable(input);
						varExperimentCommonFunctions.newExperiment05082015(function(containers){
							var atomicTransferts = [];
							var i=0;
							for(i=0;i<containers.length;i++){
								var atomicTransfert = {"inputContainer":containers[i],"inputContainerUsed":varExperimentCommonFunctions.containerToContainerUsed(containers[i])};
								atomicTransferts.push(atomicTransfert);
							}
							input.setData(atomicTransferts,atomicTransferts.length);
						});
					}
					$scope.addExperimentPropertiesInputsColumns();
				},
				loadExperimentDatatable : function(datatable){
					var promises = [];
					var allData = [];
					
					var resultInput = varOneToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
					promises = promises.concat(resultInput.promises);
					var resultOutput = varXToMany.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
					promises = promises.concat(resultOutput.promises);
					
					var that = this;
					$q.all(promises).then(function (res) {
						var l=0;
						for(var i=0; i<$scope.experiment.value.atomicTransfertMethods.length;i++){
							for(var j=0; j<$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds.length;j++){
								var outputContainerCode = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].code;
								var inputContainerCode = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0].code;
								allData[l] = {"outputNumber":$scope.experiment.value.atomicTransfertMethods[i].outputNumber};
								allData[l].outputContainerUsed = angular.copy($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j]);
								allData[l].inputContainerUsed = angular.copy($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[0]);
								
								for(var k=0;k<resultOutput.containers.length;k++){
									if(resultOutput.containers[k].code === outputContainerCode){
										allData[l].outputContainer = resultOutput.containers[k];
										break;
									}
								}
								for(var k=0;k<resultInput.containers.length;k++){
									if(resultInput.containers[k].code === inputContainerCode){
										allData[l].inputContainer = resultInput.containers[k];
										break;
									}
								}
								l++;
							}
						}
						
						datatable.setData(allData, allData.length);
						
						if($scope.isOutputGenerated()){
							$scope.addOutputColumns();
							$scope.addExperimentPropertiesOutputsColumns();
							$scope.addInstrumentPropertiesOutputsColumns();
						}
						
						$scope.addExperimentPropertiesInputsColumns();
					});
					
				},
				loadExperiment : function(input){
					if(inputType === "datatable"){
						this.loadExperimentDatatable(input);
					}
				},
				containerToContainerUsed:function(container){
					return varExperimentCommonFunctions.containersToContainerUseds(container);
				}
		};

		init();
		return oneToMany;
	};
	return constructor;
}]).factory('manyToOne',['$rootScope','manyToX','xToOne','$http', '$parse', '$q', 'experimentCommonFunctions','mainService',  function($rootScope, manyToX, xToOne, $http, $parse, $q, experimentCommonFunctions,mainService){

	var constructor = function($scope, inputType, outputType){
		var inputType = inputType;
		var outputType = outputType;
		var varManyToX = undefined;
		var varXToOne = undefined;

		var varExperimentCommonFunctions = undefined;

		var init = function(){
			varManyToX = manyToX($scope, inputType);
			varXToOne = xToOne($scope, outputType);
			varExperimentCommonFunctions = experimentCommonFunctions($scope);

		};

		var manyToOne = {
				experimentToInput : function(input){
					varManyToX.experimentToInput(input);
				},
				inputToExperiment : function(input){
					varManyToX.inputToExperiment(input);
				},
				experimentToOutput : function(output){
					varXToOne.experimentToOutput(output);
				},
				outputToExperiment : function(output){
					varXToOne.outputToExperiment(output);
				},
				searchOutputPositionByInputContainerCode : function(inputContainerCode){
					return varXToOne.searchOutputPositionByInputContainerCode(inputContainerCode);
				},
				getVarExperimentCommonFunctions : function(properties){
					return varXToOne.getVarExperimentCommonFunctions(properties);
				},	
				reloadContainersDatatable : function(datatable,outputToExperimentFunc,experimentToOutputFunc){
					var promises = [];
					var resultInput = varManyToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
					promises = promises.concat(resultInput.promises);
					if($scope.experiment.outputGenerated == true){
						var resultOutput = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
						promises = promises.concat(resultOutput.promises);
					}
					var that = this;
					$q.all(promises).then(function (res) {
						datatable.setData(resultInput.containers,resultInput.containers.length);
						if($scope.experiment.outputGenerated == true){
							var allData = datatable.getData();
							if(resultOutput.containers.length > 0){
								angular.forEach(allData, function(data){
									data.outputContainerUsed = resultOutput.containers[0];
								});
							}
							datatable.setData(allData,allData.length);
						}
						that.experimentToInput(datatable);
						
						if(angular.isDefined(outputToExperimentFunc) && angular.isFunction(outputToExperimentFunc)){								
							outputToExperimentFunc(datatable);
						}else{
							that.outputToExperiment(datatable);
						}
						if(angular.isDefined(experimentToOutputFunc)&& angular.isFunction(experimentToOutputFunc)){
							experimentToOutputFunc(datatable);								
						}else{
							that.experimentToOutput(datatable);
						}
					});					

				},
				reloadContainerDragNDrop : function(containersIn, containersOut, datatable,outputToExperimentFunc,experimentToOutputFunc){
					var i = 0;
					var containers = [];
					var promises = [];
					if(containersIn == undefined){
						containersIn = varManyToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
					}
					promises = promises.concat(containersIn.promises);
					if(containersOut == undefined && $scope.experiment.outputGenerated === true){
						containersOut = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
						promises = promises.concat(containersOut.promises);
					}
					
					var that = this;
					$q.all(promises).then(function (res) {
						containersIn = containersIn;
						if(containersOut != undefined){
							containersOut = containersOut;									
						}
						while($scope.experiment.value.atomicTransfertMethods[i] != undefined){
							for(var j=0;j<$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length;j++){
								//create new container with in/out property
								var containerIn =  $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j];
								var percentage = containerIn.percentage;									
								var containerOut = {locationOnContainerSupport:{}};
								angular.forEach(containersIn.containers, function(container) {
									if(container.code == $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].code){
										containerIn = container;
										containerIn.percentage = percentage;
									}
								});
								if(containersOut != undefined && containersOut.containers.length>0){
									angular.forEach(containersOut.containers, function(container) {
										if(angular.isArray(container) && container[0].code == $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].code){
											containerOut.code = container[0].code;
											containerOut.volume = container[0].mesuredVolume;
											containerOut.concentration = container[0].mesuredConcentration;
											containerOut.state = container[0].state;
											containerOut.experimentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].experimentProperties;
											containerOut.instrumentProperties =$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].instrumentProperties;
											containerOut.locationOnContainerSupport = container[0].locationOnContainerSupport;
											
										}else if(container.code == $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].code){
											containerOut.code = container.code;
											containerOut.volume = container.mesuredVolume;
											containerOut.concentration = container.mesuredConcentration;
											containerOut.state = container.state;
											containerOut.experimentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].experimentProperties;
											containerOut.instrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0].instrumentProperties;
											containerOut.locationOnContainerSupport = container.locationOnContainerSupport;
										}
										
										
									});
								}else{
									containerOut = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0];
								}
								
								var tags  = [];
								var sampleTypes = [];
								var libProcessTypeCodes = [];
								var sampleCodeAndTags = [];									
								angular.forEach(containerIn.contents, function(content){
									if(content.properties.tag != undefined && content.properties.tag != undefined && tags.indexOf(content.properties.tag.value) == -1){
										tags.push(content.properties.tag.value);
									}
									if(sampleTypes.indexOf(content.sampleTypeCode) == -1){
										sampleTypes.push(content.sampleTypeCode);
									}
									if(content.properties.libProcessTypeCode != undefined && libProcessTypeCodes.indexOf(content.properties.libProcessTypeCode.value) == -1){
										libProcessTypeCodes.push(content.properties.libProcessTypeCode.value);
									}
									if(content.properties.tag != undefined && content.sampleCode != undefined){
										sampleCodeAndTags.push(content.sampleCode+"/"+content.properties.tag.value);
									}
								});
								var concentration = undefined;
								if(angular.isDefined(containerIn.mesuredConcentration) && containerIn.mesuredConcentration!=null && containerIn.mesuredConcentration.value!=null){
									concentration = containerIn.mesuredConcentration.value;
								}

								var mesuredVolume = 0;
								if(containerIn.mesuredVolume !== undefined && containerIn.mesuredVolume !== null){
									mesuredVolume = containerIn.mesuredVolume;
								}
								
								var support = {};
								if(containerIn.locationOnContainerSupport !== undefined){
									support = containerIn.locationOnContainerSupport;
								}else{
									support = containerIn.support;
								}
								var container = {"inputCode":containerIn.code,"inputSupportCode":support.code, "projectCodes":containerIn.projectCodes,"sampleCodes":containerIn.sampleCodes,
										"inputX":support.line, "inputTags":tags,"inputSampleTypes":sampleTypes, "inputLibProcessTypeCodes":libProcessTypeCodes, "inputState":containerIn.state,
										"inputY":support.column, "experimentProperties":containerIn.experimentProperties,"fromExperimentTypeCodes":containerIn.fromExperimentTypeCodes,
										"instrumentProperties":containerIn.instrumentProperties, "outputPositionX":$scope.experiment.value.atomicTransfertMethods[i].line, "percentage": containerIn.percentage, "outputContainerUsed":containerOut,
										"outputPositionY":$scope.experiment.value.atomicTransfertMethods[i].column,"inputConcentration":concentration,"sampleCodeAndTags":sampleCodeAndTags,"inputVolume":mesuredVolume};//Fake container
								containers.push(container);
							}
							i++;
						}

						datatable.setData(containers,containers.length);
						that.experimentToInput(datatable);
						//$scope.addExperimentOutputDatatableToScope();
						if(angular.isDefined(outputToExperimentFunc) && angular.isFunction(outputToExperimentFunc)){								
							outputToExperimentFunc(datatable);
						}else{
							that.outputToExperiment(datatable);
						}
						if(angular.isDefined(experimentToOutputFunc)&& angular.isFunction(experimentToOutputFunc)){
							experimentToOutputFunc(datatable);								
						}else{
							that.experimentToOutput(datatable);
						}
						
					});
				},
				loadExperimentCommon : function(fn){
					var promises = [];
					var resultInput = varManyToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
					promises = promises.concat(resultInput.promises);
					if($scope.experiment.outputGenerated == true){
						var resultOutput = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
						promises = promises.concat(resultOutput.promises);
					}

					$q.all(promises).then(function (res) {
						if ('undefined' !== typeof fn) {
							fn(resultInput, resultOutput);
						}
						$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,true);
						$scope.getInstruments(true);
						//$scope.atomicTransfere.experimentToInput();
						//$scope.atomicTransfere.experimentToOutput();

						if($scope.experiment.outputGenerated == true){
							$scope.addOutputColumns();
							$scope.addExperimentPropertiesOutputsColumns();
							$scope.addInstrumentPropertiesOutputsColumns();
						}
						$scope.$broadcast('experimentLoaded');
					});
				},					
				loadExperiment : function(input,outputToExperimentFunc,experimentToOutputFunc){
					var that = this;
					if(inputType === "datatable"){
						this.loadExperimentCommon(function(resultInput, resultOutput){
							input.setData(resultInput.containers,resultInput.containers.length);
							var allData = input.getData();
							if($scope.experiment.outputGenerated == true){
								if(resultOutput.containers.length > 0){
									angular.forEach(allData, function(data){										
										data.outputContainerUsed = resultOutput.containers[0];
									});
								} else {
									for(var i=0;i<$scope.experiment.value.atomicTransfertMethods.length;i++){
										allData[i].outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[0];
									}
								}
							}
							//To add container after the experiment creation (remove or add container)
							if(!angular.isUndefined(mainService.getBasket())){
								$scope.basket = mainService.getBasket().get();
								angular.forEach($scope.basket, function(basket){
									$http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{supportCode:basket.code}})
									.success(function(data, status, headers, config) {
										$scope.clearMessages();
										if(data!=null){
											angular.forEach(data, function(container){
												allData.push(container);
												$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.push({code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state});
											});
											input.setData(allData,allData.length);
											that.experimentToInput(input);										
											if(angular.isDefined(experimentToOutputFunc)&& angular.isFunction(experimentToOutputFunc)){
												experimentToOutputFunc(input);								
											}else{
												that.experimentToOutput(input);
											}
										}
									})
									.error(function(data, status, headers, config) {
										alert("error");
									});
								});
							}else{
								input.setData(allData,allData.length);
								that.experimentToInput(input);
								if(angular.isDefined(experimentToOutputFunc)&& angular.isFunction(experimentToOutputFunc)){
									experimentToOutputFunc(input);								
								}else{
									that.experimentToOutput(input);
								}

							}
						});
					}else if(inputType === "dragndrop"){
						var that = this;
						this.loadExperimentCommon(function(resultInput, resultOutput){
							var containersOutput = undefined;
							if(resultOutput != undefined){
								containersOutput = resultOutput;
							}						
							that.reloadContainerDragNDrop(resultInput, resultOutput, input,outputToExperimentFunc,experimentToOutputFunc);
							$scope.addExperimentPropertiesInputsColumns();
						});
					}
				},
				newExperiment : function(input){
					if(inputType === "datatable"){
						varExperimentCommonFunctions.newExperimentDatatable(input);
					}else if(inputType === "dragndrop"){
						varExperimentCommonFunctions.newExperimentDragndrop();
					}
					$scope.addExperimentPropertiesInputsColumns();					
				},
				containersToContainerUseds : function(containers){
					return varExperimentCommonFunctions.containersToContainerUseds(containers);
				}
		};

		init();
		return manyToOne;
	};
	return constructor;
}]).factory('oneToVoid',['$rootScope','oneToX','$http', '$parse', '$q', 'experimentCommonFunctions',  function($rootScope, oneToX, $http, $parse, $q, experimentCommonFunctions){

	var constructor = function($scope, inputType){
		var inputType = inputType;
		var varOneToX = undefined;

		var varexperimentCommonFunctions = undefined;

		var init = function(){
			varOneToX = oneToX($scope, inputType);
			varexperimentCommonFunctions = experimentCommonFunctions($scope);

		};

		var oneToVoid = {
				experimentToInput : function(input){
					varOneToX.experimentToInput(input);
				},
				inputToExperiment : function(input){
					varOneToX.inputToExperiment(input);
				},

				loadExperimentDatatable : function(datatable){
					var containers = [];
					var promises = [];
					angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
						var promise = $http.get(jsRoutes.controllers.containers.api.Containers.get(atomicTransfertMethod.inputContainerUseds[0].code).url)
						.success(function(data, status, headers, config) {
							if(data!=null){
								containers.push(data);
							}
						})
						.error(function(data, status, headers, config) {
							alert("error");
						});

						promises.push(promise);
					});
					$q.all(promises).then(function (res) {
						datatable.setData(containers,containers.length);
						$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,true);
						$scope.getInstruments(true);
						$scope.addExperimentPropertiesInputsColumns();
					});
				},
				loadExperiment : function(input){
					if(inputType === "datatable"){
						this.loadExperimentDatatable(input);
					}
				},
				reloadContainersDatatable : function(datatable){
					var promises = [];
					var resultInput = varOneToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
					promises = promises.concat(resultInput.promises);					
					var that = this;
					$q.all(promises).then(function (res) {						
						datatable.setData(resultInput.containers,resultInput.containers.length);						
						that.experimentToInput(datatable);						
					});
				},
				newExperiment : function(input){
					if(inputType === "datatable"){
						varexperimentCommonFunctions.newExperimentDatatable(input);
					}
					$scope.addExperimentPropertiesInputsColumns();
				}
		};

		init();
		return oneToVoid;
	};
	return constructor;
}]).factory('manyToVoid',['$rootScope','manyToX','$http', '$parse', '$q', 'experimentCommonFunctions',  function($rootScope, manyToX, $http, $parse, $q, experimentCommonFunctions){

	var constructor = function($scope, inputType){
		var inputType = inputType;
		var varManyToX = undefined;

		var varexperimentCommonFunctions = undefined;

		var init = function(){
			varManyToX = manyToX($scope, inputType);
			varexperimentCommonFunctions = experimentCommonFunctions($scope);

		};

		var manyToVoid = {
				experimentToInput : function(input){
					varManyToX.experimentToInput(input);
				},
				inputToExperiment : function(input){
					varManyToX.inputToExperiment(input);
				},

				loadExperimentDatatable : function(datatable){
					var containers = [];
					var promises = [];
					angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
						angular.forEach(atomicTransfertMethod.inputContainerUseds,function(inputContainerUsed){
							var promise = $http.get(jsRoutes.controllers.containers.api.Containers.get(inputContainerUsed.code).url)
							.success(function(data, status, headers, config) {
								if(data!=null){
									containers.push(data);
								}
							})
							.error(function(data, status, headers, config) {
								alert("error");
							});
							promises.push(promise);
						});
					});
					$q.all(promises).then(function (res) {
						datatable.setData(containers,containers.length);
						$scope.getInstruments(true);
					});
				},
				loadExperiment : function(input){
					if(inputType === "datatable"){
						this.loadExperimentDatatable(input);
					}
				},
				newExperiment : function(input){
					if(inputType === "datatable"){
						varexperimentCommonFunctions.newExperimentDatatable(input);
					}
				}
		};

		init();
		return manyToVoid;
	};
	return constructor;
}]);
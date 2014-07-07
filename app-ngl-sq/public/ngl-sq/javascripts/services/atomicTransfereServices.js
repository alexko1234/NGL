angular.module('atomicTransfereServices', []).factory('experimentCommonFunctions', ['$rootScope','$http', '$parse', '$q',  function($rootScope, $http, $parse, $q){
	
			var constructor = function($scope){
				var common = {
						newExperiment: function(fn){
							var containers = [];
							var promises = [];
							$scope.basket = $scope.getBasket().get();
							angular.forEach($scope.basket, function(basket){
								var promise = $http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{supportCode:basket.code}})
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
							
							$q.all(promises).then(function (res) {
								//console.log(containers);
								//$scope.datatable.setData(containers,containers.length);
								fn(containers);
								$scope.doPurifOrQc($scope.experiment.value.typeCode);
								$scope.getInstruments();
								if(!$scope.experiment.editMode) {
									$scope.init_experiment(containers, $scope.experimentType.atomicTransfertMethod);
								}else{
									$scope.addExperimentPropertiesInputsColumns();
								}
							});
						},
					newExperimentDatatable : function(){
						this.newExperiment(function(containers){
							$scope.datatable.setData(containers,containers.length);
						});
					},
					containersToContainerUseds : function(containers){
						var containerUseds = [];
						angular.forEach(containers, function(container){
							containerUseds.push({"code":container.code,"state":container.state,"instrumentProperties":{},"experimentProperties":{},
								"percentage":100,"categoryCode":container.categoryCode,"volume":container.mesuredVolume,
								"concentration":container.mesuredConcentration, "contents":container.contents,"locationOnContainerSupport":container.support});
						});
						
						return containerUseds;
					},
					newExperimentDragndrop : function(){
						var that = this;
						this.newExperiment(function(containers){
							console.log(containers);
							$scope.inputContainers = that.containersToContainerUseds(containers);
							$scope.datatable.setData(containers,containers.length);
						});
					},
					loadContainer : function(containerUsed){
						var results = {container:{},promise:{}};
						results.promise = $http.get(jsRoutes.controllers.containers.api.Containers.get(containerUsed.code).url)
						.success(function(data, status, headers, config) {
							$scope.clearMessages();
								if(data!=null){
									results.container = data;
								}
							})
						.error(function(data, status, headers, config) {
							alert("error");
						});
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
						experimentToInput : function(){
							if(inputType === "datatable"){
								for(var i=0;i<$scope.datatable.displayResult.length;i++){
									$scope.datatable.displayResult[i].data.inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties;
									$scope.datatable.displayResult[i].data.inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties;
								}
							}
						},
						inputToExperiment : function(){
							if(inputType === "datatable"){
								for(var i=0;i<$scope.datatable.displayResult.length;i++){
									$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].data.inputInstrumentProperties;
									$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].data.inputExperimentProperties;
								}
							}
						},
						loadInputContainers : function(ContainerUseds){
							var results = {containers:[],promises:[]};
							angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
								var result = varExperimentCommonFunctions.loadContainer(atomicTransfertMethod.inputContainerUsed);
								results.promises.push(result.promise);
								result.promise.then(function(container){
									results.containers.push(container.data);
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
						experimentToInput : function(){
							if(inputType === "datatable"){
								for(var i=0;i<$scope.datatable.displayResult.length;i++){
									for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
										$scope.datatable.displayResult[i].data.inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].instrumentProperties;
										$scope.datatable.displayResult[i].data.inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].experimentProperties;
										i++;
									}
								}
							}
						},
						searchContainer : function(code){
							var i = 0;
							while($scope.experiment.value.atomicTransfertMethods[i] != undefined){
								for(var j=0;j<$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length;j++){
									if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].code == code){
										return {"x":i,"y":j};
									}
								}
								i++;
							}
							return {};
						},
						inputToExperiment : function(){
							console.log("InputToExperiment");
							console.log(inputType);
							if(inputType === "datatable"){
								for(var i=0;i<$scope.datatable.displayResult.length;i++){
									for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
										$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].instrumentProperties = $scope.datatable.displayResult[i].data.inputInstrumentProperties;
										$scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].experimentProperties = $scope.datatable.displayResult[i].data.inputExperimentProperties;
										i++;
									}
								}
							}else if(inputType === "dragndrop"){
								for(var i=0;i<$scope.datatable.displayResult.length;i++){
									var pos = this.searchContainer($scope.datatable.displayResult[i].data.code);
									$scope.experiment.value.atomicTransfertMethods[pos.x].inputContainerUseds[pos.y].instrumentProperties = $scope.datatable.displayResult[i].data.inputInstrumentProperties;
								    $scope.experiment.value.atomicTransfertMethods[pos.x].inputContainerUsed[pos.y].experimentProperties = $scope.datatable.displayResult[i].data.inputExperimentProperties;
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
										results.containers.push(container.data);
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
					experimentToOutput : function(){
						if(outputType === "none"){
							for(var i=0;i<$scope.datatable.displayResult.length;i++){
								if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
									if($scope.datatable.displayResult[i].data.outputContainerUsed == undefined){
										$scope.datatable.displayResult[i].data.outputContainerUsed = {};
										$scope.datatable.displayResult[i].data.outputContainerUsed.code = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.code;
									}
									$scope.datatable.displayResult[i].data.outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.instrumentProperties;
									$scope.datatable.displayResult[i].data.outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.experimentProperties;
								}
							}
						}
					},
					outputToExperiment : function(){
						if(outputType === "none"){
							for(var i=0;i<$scope.datatable.displayResult.length;i++){
								if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed != undefined){
									$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].data.outputInstrumentProperties;					
									$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].data.outputExperimentProperties;					
								}
							}
						}
					},
					loadOutputContainers : function(ContainerUseds){
						var results = {containers:[],promises:[]};
						var that = this;
						angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
								var result = varExperimentCommonFunctions.loadContainer(atomicTransfertMethod.outputContainerUsed);
								results.promises.push(result.promise);
								result.promise.then(function(container){
									results.containers.push(container.data);
								});
						});
						return results;
					}
				};
				init();
				return xToOne;
			};
		return constructor;
	}]).factory('xToMany', ['$rootScope', function($rootScope){
    		
			var constructor = function($scope, outputType){
				var outputType = outputType;
				
				var xToMany = {
					experimentToOutput : function(){
						if(outputType === "none"){
							for(var i=0;i<$scope.datatable.displayResult.length;i++){
								if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
									$scope.datatable.displayResult[i].data.outputContainerUseds = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds;
									for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
										$scope.datatable.displayResult[i].data.outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].instrumentProperties;
										$scope.datatable.displayResult[i].data.outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].experimentProperties;
										i++;
									}
								}
							}
						}
					},
					outputToExperiment : function(){
						if(outputType === "none"){
							for(var i=0;i<$scope.datatable.displayResult.length;i++){
								if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds!=undefined){
									$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds = $scope.datatable.displayResult[i].data.outputContainerUseds;
									for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
										$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].instrumentProperties = $scope.datatable.displayResult[i].data.outputInstrumentProperties;
										$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[j].experimentProperties = $scope.datatable.displayResult[i].data.outputExperimentProperties;
										i++;
									}
								}
							}
						}
					}
				};

				return xToOne;
			};
		return constructor;
	}]).factory('oneToOne', ['$rootScope','oneToX','xToOne', '$http', '$parse', '$q', 'experimentCommonFunctions', function($rootScope, oneToX, xToOne, $http, $parse, $q, experimentCommonFunctions){
    		
				var constructor = function($scope, inputType, outputType){
					var inputType = inputType;
					var outputType = outputType;
				    var varOneToX = undefined;
				    var varXToOne = undefined;
				    
				    var varexperimentCommonFunctions = undefined;
					
					var init = function(){
						varOneToX = oneToX($scope, inputType);
						varXToOne = xToOne($scope, outputType);
						varexperimentCommonFunctions = experimentCommonFunctions($scope);
					};
					
					var oneToOne = {
						experimentToInput : function(){
							varOneToX.experimentToInput();
						},
						inputToExperiment : function(){
							varOneToX.inputToExperiment();
						},
						experimentToOutput : function(){
							varXToOne.experimentToOutput();
						},
						outputToExperiment : function(){
							varXToOne.outputToExperiment();
						},
						reloadContainersDatatable : function(){
							var promises = [];
							var resultInput = varOneToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
							promises = promises.concat(resultInput.promises);
							if($scope.experiment.value.state.code === "F"){
								var resultOutput = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
								promises = promises.concat(resultOutput.promises);
							}
							var that = this;
							$q.all(promises).then(function (res) {
								$scope.datatable.setData(resultInput.containers,resultInput.containers.length);
								if($scope.experiment.value.state.code === "F"){
									var allData = $scope.datatable.getData();
									angular.forEach(allData, function(data){
										data.outputContainerUsed = resultOutput.containers[0];
									});
									
									$scope.datatable.setData(allData,allData.length);
								}
								that.experimentToInput();
								that.experimentToOutput();
							});
						},
						loadExperimentDatatable : function(){
							var promises = [];
							var resultInput = varOneToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
							promises = promises.concat(resultInput.promises);
							if($scope.experiment.value.state.code === "F"){
								var resultOutput = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
								promises = promises.concat(resultOutput.promises);
							}
							
							$q.all(promises).then(function (res) {
								$scope.datatable.setData(resultInput.containers,resultInput.containers.length);
								if($scope.experiment.value.state.code === "F"){
									var allData = $scope.datatable.getData();
									var i = 0;
									angular.forEach(allData, function(data){
										data.outputContainerUsed = resultOutput.containers[i];
										i++;
									});
									$scope.datatable.setData(allData,allData.length);
								}
								$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,true);
								$scope.atomicTransfere.experimentToInput();
								$scope.atomicTransfere.experimentToOutput();

								if($scope.isOutputGenerated()){
									$scope.addOutputColumns();
									$scope.addExperimentPropertiesOutputsColumns();
									$scope.addInstrumentPropertiesOutputsColumns();
								}
							});
						},
						loadExperiment : function(){
							if(inputType === "datatable"){
								this.loadExperimentDatatable();
							}
						},
						newExperiment : function(){
							if(inputType === "datatable"){
								varexperimentCommonFunctions.newExperimentDatatable();
							}
						},
					};
					
					init();
					return oneToOne;
				};
			return constructor;
    	}]).factory('oneToMany', ['$rootScope', 'oneToX','xToMany','$http', '$parse', '$q', 'experimentCommonFunctions', function($rootScope, oneToX, xToMany, $http, $parse, $q, experimentCommonFunctions){
    		
				var constructor = function($scope, inputType, outputType){
					var inputType = inputType;
					var outputType = outputType;
					var varOneToX = undefined;
				    var varXToMany = undefined;
					
					var init = function(){
						varOneToX = oneToX($scope, inputType);
						varXToMany = xToMany($scope, outputType);
					};
				
					var oneToMany = {
						experimentToInput : function(){
							varOneToX.experimentToInput();
						},
						inputToExperiment : function(){
							varOneToX.inputToExperiment();
						},
						experimentToOutput : function(){
							varXToMany.experimentToOutput();
						},
						outputToExperiment : function(){
							varXToMany.outputToExperiment();
						}
					};
					
					init();
					return oneToMany;
				};
			return constructor;
    	}]).factory('manyToOne',['$rootScope','manyToX','xToOne','$http', '$parse', '$q', 'experimentCommonFunctions',  function($rootScope, manyToX, xToOne, $http, $parse, $q, experimentCommonFunctions){
    		
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
					experimentToInput : function(){
						varManyToX.experimentToInput();
					},
					inputToExperiment : function(){
						varManyToX.inputToExperiment();
					},
					experimentToOutput : function(){
						varXToOne.experimentToOutput();
					},
					outputToExperiment : function(){
						varXToOne.outputToExperiment();
					},
					reloadContainersDatatable : function(){
						var promises = [];
						var resultInput = varManyToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
						promises = promises.concat(resultInput.promises);
						if($scope.experiment.value.state.code === "F"){
							var resultOutput = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
							promises = promises.concat(resultOutput.promises);
						}
						var that = this;
						$q.all(promises).then(function (res) {
							$scope.datatable.setData(resultInput.containers,resultInput.containers.length);
							if($scope.experiment.value.state.code === "F"){
								var allData = $scope.datatable.getData();
								angular.forEach(allData, function(data){
									data.outputContainerUsed = resultOutput.containers[0];
								});
								
								$scope.datatable.setData(allData,allData.length);
							}
							that.experimentToInput();
							that.experimentToOutput();
						});
					},
					loadExperimentCommon : function(fn){
						var promises = [];
						var resultInput = varManyToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
						promises = promises.concat(resultInput.promises);
						if($scope.experiment.value.state.code === "F"){
							var resultOutput = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
							promises = promises.concat(resultOutput.promises);
						}
						
						
						$q.all(promises).then(function (res) {
							if ('undefined' !== typeof fn) {
								fn(resultInput);
							}
							$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,true);
							$scope.getInstruments(true);
							$scope.atomicTransfere.experimentToInput();
							$scope.atomicTransfere.experimentToOutput();
						
							if($scope.isOutputGenerated()){
								$scope.addOutputColumns();
								$scope.addExperimentPropertiesOutputsColumns();
								$scope.addInstrumentPropertiesOutputsColumns();
							}
						});
					},
					loadExperiment : function(){
						if(inputType === "datatable"){
							this.loadExperimentCommon(function(resultInput){
								$scope.datatable.setData(resultInput.containers,resultInput.containers.length);
								if($scope.experiment.value.state.code === "F"){
									var allData = $scope.datatable.getData();
									angular.forEach(allData, function(data){
										data.outputContainerUsed = resultOutput.containers[0];
									});
									$scope.datatable.setData(allData,allData.length);
								}
							});
						}else if(inputType === "dragndrop"){
							this.loadExperimentCommon(function(resultInput){
								$scope.datatable.setData(resultInput.containers,resultInput.containers.length);
								if($scope.experiment.value.state.code === "F"){
									var allData = $scope.datatable.getData();
									angular.forEach(allData, function(data){
										data.outputContainerUsed = resultOutput.containers[0];
									});
									$scope.datatable.setData(allData,allData.length);
								}
							});
						}
					},
					newExperiment : function(){
						if(inputType === "datatable"){
							varExperimentCommonFunctions.newExperimentDatatable();
						}else if(inputType === "dragndrop"){
							varExperimentCommonFunctions.newExperimentDragndrop();
						}
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
		
			var manyToOne = {
				experimentToInput : function(){
					varOneToX.experimentToInput();
				},
				inputToExperiment : function(){
					varOneToX.inputToExperiment();
				},
				
				loadExperimentDatatable : function(){
					var containers = [];
					var promises = [];
					angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
						var promise = $http.get(jsRoutes.controllers.containers.api.Containers.get(atomicTransfertMethod.inputContainerUsed.code).url)
						.success(function(data, status, headers, config) {
							$scope.clearMessages();
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
						$scope.datatable.setData(containers,containers.length);
						$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,true);
						$scope.getInstruments(true);
					});
				},
				loadExperiment : function(){
					if(inputType === "datatable"){
						this.loadExperimentDatatable();
					}
				},
				newExperiment : function(){
					if(inputType === "datatable"){
						varexperimentCommonFunctions.newExperimentDatatable();
					}
				}
			};
			
			init();
			return manyToOne;
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
	
		var manyToOne = {
			experimentToInput : function(){
				varManyToX.experimentToInput();
			},
			inputToExperiment : function(){
				varManyToX.inputToExperiment();
			},
			
			loadExperimentDatatable : function(){
				var containers = [];
				var promises = [];
					angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
							angular.forEach(atomicTransfertMethod.inputContainerUseds,function(inputContainerUsed){
						var promise = $http.get(jsRoutes.controllers.containers.api.Containers.get(inputContainerUsed.code).url)
						.success(function(data, status, headers, config) {
							$scope.clearMessages();
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
					$scope.datatable.setData(containers,containers.length);
					$scope.getInstruments(true);
				});
			},
			loadExperiment : function(){
				if(inputType === "datatable"){
					this.loadExperimentDatatable();
				}
			},
			newExperiment : function(){
				if(inputType === "datatable"){
					varexperimentCommonFunctions.newExperimentDatatable();
				}
			}
		};
		
		init();
		return manyToOne;
	};
return constructor;
}]);
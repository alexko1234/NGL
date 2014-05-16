angular.module('atomicTransfereServices', []).factory('experimentCommonFunctions', ['$rootScope','$http', '$parse', '$q',  function($rootScope, $http, $parse, $q){
	
			var constructor = function($scope){
				var common = {
					newExperimentDatatable : function(){
						var containers = [];
						var promises = [];
						$scope.basket = $scope.getBasket().get();
						$scope.basket.forEach(function(basket){
							var promise = $http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{supportCode:basket.code}})
							.success(function(data, status, headers, config) {
								$scope.clearMessages();
								if(data!=null){
									data.forEach(function(container){
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
							console.log(containers);
							$scope.datatable.setData(containers,containers.length);
							$scope.doPurifOrQc($scope.experiment.value.typeCode);
							$scope.getInstruments();
							if(angular.isUndefined($scope.form.experiment)) {
								$scope.init_experiment(containers, $scope.experimentType.atomicTransfertMethod);
							}else {
								$scope.experiment = $scope.form.experiment;
								$scope.addExperimentPropertiesInputsColumns();
							}
						});
					}
				};
				return common;
			};
			return constructor;
		}]).
		factory('oneToX', ['$rootScope', function($rootScope){
    		
				var constructor = function($scope, inputType){
					var inputType = inputType;
				
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
						}
					};
					
					return oneToX;
				};
			return constructor;
    	}]).factory('manyToX', ['$rootScope', function($rootScope){
    		
				var constructor = function($scope, inputType){
					var inputType = inputType;
				
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
						inputToExperiment : function(){
							if(inputType === "datatable"){
								for(var i=0;i<$scope.datatable.displayResult.length;i++){
									for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
										$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].instrumentProperties = $scope.datatable.displayResult[i].data.inputInstrumentProperties;
										$scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].experimentProperties = $scope.datatable.displayResult[i].data.inputExperimentProperties;
										i++;
									}
								}
							}
						}
					};
					
					return manyToX;
				};
			return constructor;
    	}]).factory('xToOne', ['$rootScope', function($rootScope){
    		
			var constructor = function($scope, outputType){
				var outputType = outputType;
				
				var xToOne = {
					experimentToOutput : function(){
						if(outputType === "none"){
							for(var i=0;i<$scope.datatable.displayResult.length;i++){
								if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
									$scope.datatable.displayResult[i].data.outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed;
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
					}
				};

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
						loadExperimentDatatable : function(){
							var containers = [];
							var promises = [];
							$scope.experiment.value.atomicTransfertMethods.forEach(function(atomicTransfertMethod){
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
			    
			    var varexperimentCommonFunctions = undefined;
				
				var init = function(){
					varManyToX = manyToX($scope, inputType);
					varXToOne = xToOne($scope, outputType);
					varexperimentCommonFunctions = experimentCommonFunctions($scope);
					
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
					loadExperimentDatatable : function(){
						var containers = [];
						var promises = [];
						$scope.experiment.value.atomicTransfertMethods.forEach(function(atomicTransfertMethod){
							atomicTransfertMethod.inputContainerUseds.forEach(function(inputContainerUsed){
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
							$scope.atomicTransfere.experimentToInput();
							$scope.atomicTransfere.experimentToOutput();
							console.log($scope.isOutputGenerated());
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
					$scope.experiment.value.atomicTransfertMethods.forEach(function(atomicTransfertMethod){
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
				$scope.experiment.value.atomicTransfertMethods.forEach(function(atomicTransfertMethod){
							atomicTransfertMethod.inputContainerUseds.forEach(function(inputContainerUsed){
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
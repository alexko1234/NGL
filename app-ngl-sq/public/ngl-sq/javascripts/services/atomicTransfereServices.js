angular.module('atomicTransfereServices', []).factory('experimentCommonFunctions', ['$rootScope','$http', '$parse', '$q','mainService',  function($rootScope, $http, $parse, $q, mainService){
	
			var constructor = function($scope){
				var common = {
						newExperiment: function(fn){
							var containers = [];
							var promises = [];
							$scope.basket = mainService.getBasket().get();
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
								fn(containers);
								$scope.doPurifOrQc($scope.experiment.value.typeCode);
								//$scope.getInstruments();
								if(!$scope.experiment.editMode) {
									$scope.init_experiment(containers, $scope.experimentType.atomicTransfertMethod);
								}else{
									$scope.addExperimentPropertiesInputsColumns();
								}
								
								$scope.addExperimentPropertiesOutputsColumns();
							    $scope.addInstrumentPropertiesOutputsColumns();
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
						
							/*var tags  = [];
							var sampleTypes = [];
							var libProcessTypeCodes = [];
							angular.forEach(container.contents, function(content){
								if(content.properties.tag != undefined && tags.indexOf(content.properties.tag.value) == -1){
									tags.push(content.properties.tag.value);
								}
								if(content.sampleTypeCode && sampleTypes.indexOf(content.sampleTypeCode) == -1){
									sampleTypes.push(content.sampleTypeCode);
								}
								if(content.properties.libProcessTypeCode && libProcessTypeCodes.indexOf(content.properties.libProcessTypeCode.value) == -1){
									libProcessTypeCodes.push(content.properties.libProcessTypeCode.value);
								}
							});*/
							
							//concat sampleCode_tag
							/*var sampleCodeAndTags = [];
							angular.forEach(container.contents, function(content){
								if(content.properties.tag != undefined && content.sampleCode != undefined){
									sampleCodeAndTags.push(content.sampleCode+" "+content.properties.tag.value);
								}
							});*/
							containerUseds.push({"code":container.code,"state":container.state,"instrumentProperties":{},"experimentProperties":{},
								"percentage":100,"categoryCode":container.categoryCode,"volume":container.mesuredVolume,
								"concentration":container.mesuredConcentration,"contents":container.contents/*,"contentsInput":{"tags":tags,"sampleTypes":sampleTypes,"libProcessTypeCodes":libProcessTypeCodes}*/,"locationOnContainerSupport":container.support});
						});
						
						return containerUseds;
					},
					newExperimentDragndrop : function(){
						var that = this;
						this.newExperiment(function(containers){
							$scope.inputContainers = that.containersToContainerUseds(containers);
						});
					},
					loadContainer : function(containerUsed){
						var results = {container:{},promise:{}};
						var line = 1;
						var column = 1;
						
						if(containerUsed.locationOnContainerSupport != undefined){
							line = containerUsed.locationOnContainerSupport.line;
							column = containerUsed.locationOnContainerSupport.column;
						}

						results.promise = $http.get(jsRoutes.controllers.containers.api.Containers.list().url, {params:{"code":containerUsed.code, "line":line, "column":column}})
						.success(function(data, status, headers, config) {
								if(data!=null){
									results.container = data[0];
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
						experimentToInput : function(){
							if(inputType === "datatable"){
								if($scope.datatable.displayResult != undefined){
									for(var i=0;i<$scope.datatable.displayResult.length;i++){
										for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
											$scope.datatable.displayResult[i].data.inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].instrumentProperties;
											$scope.datatable.displayResult[i].data.inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].experimentProperties;
											i++;
										}
									}
								}
							}else if(inputType === "dragndrop"){
								if($scope.datatable.displayResult != undefined){
									for(var i=0;i<$scope.datatable.displayResult.length;i++){
										$scope.datatable.displayResult[i].data.percentage = $scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].inputContainerUseds[($scope.datatable.displayResult[i].data.outputPositionY-1)].percentage;
										if($scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].inputContainerUseds[($scope.datatable.displayResult[i].data.outputPositionY-1)].instrumentProperties != undefined){
											 var positions = this.searchContainer($scope.datatable.displayResult[i].data.inputCode, $scope.datatable.displayResult[i].data.outputPositionX, $scope.datatable.displayResult[i].data.outputPositionY);
											 $scope.datatable.displayResult[i].data.inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[positions.x].inputContainerUseds[positions.y].instrumentProperties;
										}
										if($scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].inputContainerUseds[($scope.datatable.displayResult[i].data.outputPositionY-1)].experimentProperties != undefined){
											 var positions = this.searchContainer($scope.datatable.displayResult[i].data.inputCode, $scope.datatable.displayResult[i].data.outputPositionX, $scope.datatable.displayResult[i].data.outputPositionY);
											 $scope.datatable.displayResult[i].data.inputExperimentProperties =	$scope.experiment.value.atomicTransfertMethods[positions.x].inputContainerUseds[positions.y].experimentProperties;
										}
									}
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
						inputToExperiment : function(){
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
									//$scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].inputContainerUseds[($scope.datatable.displayResult[i].data.outputPositionY-1)].percentage = $scope.datatable.displayResult[i].data.percentage;
									if($scope.datatable.displayResult[i].data.inputInstrumentProperties != undefined){
										var positions = this.searchContainer($scope.datatable.displayResult[i].data.inputCode, $scope.datatable.displayResult[i].data.outputPositionX, $scope.datatable.displayResult[i].data.outputPositionY);
										$scope.experiment.value.atomicTransfertMethods[positions.x].inputContainerUseds[positions.y].instrumentProperties = $scope.datatable.displayResult[i].data.inputInstrumentProperties;
									}
									if($scope.datatable.displayResult[i].data.inputExperimentProperties != undefined){
										var positions = this.searchContainer($scope.datatable.displayResult[i].data.inputCode, $scope.datatable.displayResult[i].data.outputPositionX, $scope.datatable.displayResult[i].data.outputPositionY);
										$scope.experiment.value.atomicTransfertMethods[positions.x].inputContainerUseds[positions.y].experimentProperties = $scope.datatable.displayResult[i].data.inputExperimentProperties;
									}
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
					experimentToOutput : function(){
						if(outputType === "none"){
							if($scope.datatable.displayResult != undefined){
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
						}else if(outputType === "datatable"){
							if($scope.datatable.displayResult != undefined){
								for(var i=0;i<$scope.datatable.displayResult.length;i++){
									if($scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].outputContainerUsed != undefined){
										$scope.datatable.displayResult[i].data.outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].outputContainerUsed.instrumentProperties;
									}
									if($scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].outputContainerUsed != undefined){
										$scope.datatable.displayResult[i].data.outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].outputContainerUsed.experimentProperties;	
									}
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
						}else if(outputType === "datatable"){
							for(var i=0;i<$scope.datatable.displayResult.length;i++){
								if($scope.datatable.displayResult[i].data.outputInstrumentProperties != undefined){
									$scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].outputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].data.outputInstrumentProperties;
								}
								if($scope.datatable.displayResult[i].data.outputExperimentProperties!= undefined){
									$scope.experiment.value.atomicTransfertMethods[($scope.datatable.displayResult[i].data.outputPositionX-1)].outputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].data.outputExperimentProperties;	
								}
							}
						}
					},
					loadOutputContainers : function(ContainerUseds){
						var results = {containers:[],promises:[]};
						var that = this;
						angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
							if(atomicTransfertMethod.outputContainerUsed != undefined && atomicTransfertMethod.outputContainerUsed.code != null){
								var result = varExperimentCommonFunctions.loadContainer(atomicTransfertMethod.outputContainerUsed);
								results.promises.push(result.promise);
								result.promise.then(function(container){
									results.containers.push(container.data);
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
	}]).factory('oneToOne', ['$rootScope','oneToX','xToOne', '$http', '$parse', '$q', 'experimentCommonFunctions','mainService', function($rootScope, oneToX, xToOne, $http, $parse, $q, experimentCommonFunctions,mainService){
    		
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
							if($scope.experiment.outputGenerated == true){
								var resultOutput = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
								promises = promises.concat(resultOutput.promises);
							}
							var that = this;
							$q.all(promises).then(function (res) {
								$scope.datatable.setData(resultInput.containers,resultInput.containers.length);
								if($scope.experiment.outputGenerated == true){
									var allData = $scope.datatable.getData();
									angular.forEach(allData, function(data){
										data.outputContainerUsed = resultOutput.containers[0][0];
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
							if($scope.experiment.outputGenerated == true){
								var resultOutput = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
								promises = promises.concat(resultOutput.promises);
							}
							
							$q.all(promises).then(function (res) {
								$scope.datatable.setData(resultInput.containers,resultInput.containers.length);
								if($scope.experiment.outputGenerated == true){
									var allData = $scope.datatable.getData();
									var i = 0;
									angular.forEach(allData, function(data){
										data.outputContainerUsed = resultOutput.containers[i][0];
										i++;
									});
									$scope.datatable.setData(allData,allData.length);
								}
								$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,true);
								$scope.atomicTransfere.experimentToInput();
								$scope.atomicTransfere.experimentToOutput();
								
								if(!angular.isUndefined(mainService.getBasket())){
									var containers = [];
									promises = [];
									$scope.basket = mainService.getBasket().get();
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
										var containersDatatable = $scope.datatable.getData();
										containersDatatable = containersDatatable.concat(containers);
										$scope.datatable.setData(containersDatatable,containersDatatable.length);
									});
								}
								
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
						if($scope.experiment.outputGenerated == true){
							var resultOutput = varXToOne.loadOutputContainers($scope.experiment.value.atomicTransfertMethods);
							promises = promises.concat(resultOutput.promises);
						}
						var that = this;
						$q.all(promises).then(function (res) {
							$scope.datatable.setData(resultInput.containers,resultInput.containers.length);
							if($scope.experiment.outputGenerated == true){
								var allData = $scope.datatable.getData();
								angular.forEach(allData, function(data){
									data.outputContainerUsed = resultOutput.containers[0][0];
								});
								
								$scope.datatable.setData(allData,allData.length);
							}
							that.experimentToInput();
							that.experimentToOutput();
						});
					},
					reloadContainerDragNDrop : function(containersIn, containersOut){
						var i = 0;
						var containers = [];
						var promises = [];
						if(containersIn == undefined){
									containersIn = varManyToX.loadInputContainers($scope.experiment.value.atomicTransfertMethods);
						}
						promises = promises.concat(containersIn.promises);
						if(containersOut == undefined && $scope.outputGenerated === true){
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
									var containerOut = {locationOnContainerSupport:{}};
									angular.forEach(containersIn.containers, function(container) {
										if(container.code == $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].code){
											containerIn = container;
										}
									});
									if(containersOut != undefined){
										angular.forEach(containersOut.containers, function(container) {
											if(container.code == $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.code){
												containerOut = container;
											}
										});
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
											sampleCodeAndTags.push(content.sampleCode+" "+content.properties.tag.value);
										}
									});
									
									
									var container = {"inputCode":containerIn.code,"inputSupportCode":containerIn.support.code,
											"inputX":containerIn.support.line, "inputTags":tags,"inputSampleTypes":sampleTypes, "inputLibProcessTypeCodes":libProcessTypeCodes, "inputState":containerIn.state,
														"inputY":containerIn.support.column, "experimentProperties":containerIn.experimentProperties,
														"instrumentProperties":containerIn.instrumentProperties, "outputPositionX":i+1,
														"outputPositionY":1,"inputConcentration":containerIn.mesuredConcentration.value,"sampleCodeAndTags":sampleCodeAndTags,"inputVolume":containerIn.mesuredVolume.value};//Fake container
									containers.push(container);
								}
								i++;
						}
						
						$scope.datatable.setData(containers,containers.length);
						that.experimentToInput();
						$scope.addExperimentOutputDatatableToScope();
						that.experimentToOutput();
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
							$scope.atomicTransfere.experimentToInput();
							$scope.atomicTransfere.experimentToOutput();
						
							if($scope.experiment.outputGenerated == true){
								$scope.addOutputColumns();
								$scope.addExperimentPropertiesOutputsColumns();
								$scope.addInstrumentPropertiesOutputsColumns();
							}
						});
					},
					loadExperiment : function(){
						if(inputType === "datatable"){
							this.loadExperimentCommon(function(resultInput, resultOutput){
								$scope.datatable.setData(resultInput.containers,resultInput.containers.length);
								var allData = $scope.datatable.getData();
								if($scope.experiment.outputGenerated == true){
									angular.forEach(allData, function(data){
										data.outputContainerUsed = resultOutput.containers[0][0];
									});
								}
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
												$scope.datatable.setData(allData,allData.length);
											}
										})
										.error(function(data, status, headers, config) {
											alert("error");
										});
									});
								}else{
									$scope.datatable.setData(allData,allData.length);
								}
							});
						}else if(inputType === "dragndrop"){
							var that = this;
							this.loadExperimentCommon(function(resultInput, resultOutput){
							    var containersOutput = undefined;
								if(resultOutput != undefined){
									containersOutput = resultOutput;
								}
								that.reloadContainerDragNDrop(resultInput, resultOutput);
								$scope.addExperimentPropertiesInputsColumns();
							});
						}
					},
					newExperiment : function(){
						if(inputType === "datatable"){
							varExperimentCommonFunctions.newExperimentDatatable();
						}else if(inputType === "dragndrop"){
							varExperimentCommonFunctions.newExperimentDragndrop();
						}
						$scope.addExperimentPropertiesInputsColumns();
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
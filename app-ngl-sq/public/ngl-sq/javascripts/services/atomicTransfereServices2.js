angular.module('atomicTransfereServices2', [])
.factory('commonAtomicTransfertMethod', ['$http', '$parse', '$q', 'mainService', 
                                         function($http, $parse, $q, mainService){

                	var constructor = function($scope){
                		var common = {
                				
                				removeNullProperties : function(properties){
                					for (var p in properties) {
                						if(properties[p] != undefined && (properties[p].value === undefined || properties[p].value === null || properties[p].value === "")){
                							properties[p] = undefined;
                						}
                					}
                				},
                				getDisplayUnitFromProperty:function(propertyDefinition){
                					var unit = $parse("displayMeasureValue.value")(propertyDefinition);
                					if(undefined !== unit && null !== unit) return " ("+unit+")";
                					else return "";
                				},
                				convertSinglePropertyToDatatableColumn : function(propertyDefinition, propertyNamePrefix, extraHeaders){
                					return this.convertPropertyToDatatableColumn(propertyDefinition, propertyNamePrefix, ".value", extraHeaders); 
                				},
                				convertObjectPropertyToDatatableColumn : function(propertyDefinition, propertyNamePrefix, extraHeaders){
                					return this.convertPropertyToDatatableColumn(propertyDefinition, propertyNamePrefix, "", extraHeaders); 
                				},
                				convertObjectListPropertyToDatatableColumn : function(propertyDefinition, propertyNamePrefix, extraHeaders){
                					//in case of list the datatable manage the list so we remove the prefix of the property definition					
                					var pd = angular.copy(propertyDefinition);
                					pd.code = pd.code.substring(pd.code.indexOf(".")+1, pd.code.length);					
                					return this.convertPropertyToDatatableColumn(pd, propertyNamePrefix, "", extraHeaders); 
                				},
                				convertPropertyToDatatableColumn : function(propertyDefinition, propertyNamePrefix, propertyNameSuffix,extraHeaders){
                    				var column = {};
                    				column.header = propertyDefinition.name + this.getDisplayUnitFromProperty(propertyDefinition);
                    				column.property = propertyNamePrefix+propertyDefinition.code+propertyNameSuffix;
                    				column.edit = propertyDefinition.editable;
                    				column.hide =  true;
                    				column.order = true;
                    				column.type = $scope.getPropertyColumnType(propertyDefinition.valueType);
                    				column.choiceInList = propertyDefinition.choiceInList;
                    				column.position=propertyDefinition.displayOrder;
                    				column.defaultValues = propertyDefinition.defaultValue;
                    				if(propertyDefinition.possibleValues!=undefined){
                    					column.possibleValues = propertyDefinition.possibleValues;
                    				}
                    				if(extraHeaders!=undefined){
                    					column.extraHeaders = extraHeaders;
                    				}
                    				if(propertyDefinition.displayMeasureValue != undefined && propertyDefinition.displayMeasureValue != null){
                    					column.convertValue = {"active":true, "displayMeasureValue":propertyDefinition.displayMeasureValue.value, 
                    							"saveMeasureValue":propertyDefinition.saveMeasureValue.value};
                    				}
                    				return column;
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
                				/**
                				 * Create a new OutputContainerUsed. By default unit is the same as inputContainer for volume, concentration, quantity
                				 * In second time, we need to find the default concentration because several concentration are available for one container
                				 */
                				newOutputContainerUsed : function(inputContainer, defaultOutputUnit){
                					return {
                						code:undefined,
                						volume:{unit:this.getUnit(inputContainer.mesuredVolume, defaultOutputUnit.volume)}, 
                						concentration:{unit:this.getUnit(inputContainer.mesuredConcentration, defaultOutputUnit.concentration)}, 
                						quantity:{unit:this.getUnit(inputContainer.mesuredQuantity, defaultOutputUnit.quantity)},
                						instrumentProperties:{},
                					    experimentProperties:{}
                					};
                				},
                				
                				getUnit: function(object, defaultValue){
                					var unit = $parse("unit")(object);
                					if(undefined === unit || null === unit || unit !== defaultValue)unit = defaultValue
                					return unit;
                				},
                				

                		};
                		return common;
                	};
                	return constructor;


                	
                }]).factory('atmToSingleDatatable', ['$http', '$parse', '$q', 'commonAtomicTransfertMethod','mainService', 
                                         function($http, $parse, $q, commonAtomicTransfertMethod, mainService){
                	
                	
                	var constructor = function($scope, outputIsVoid){
                		
                		var $outputIsVoid = (outputIsVoid !== undefined)?outputIsVoid : false; //false when void in output
                		var $commonATM = commonAtomicTransfertMethod($scope);
                		var view = {
                				$outputIsVoid : $outputIsVoid,
                				$commonATM : $commonATM,
                				newAtomicTransfertMethod : function(){
                					throw 'not defined int atmSingleDatatable';
                				},
                				
                				deleteInstrumentDatatableColumn : function(datatable){
                					var columns = datatable.getColumnsConfig();
                					
                					angular.forEach(columns, function(column, index){
                						if(column.extraHeaders != undefined && column.extraHeaders[1] === "Instrument"){
                							console.log("check if works correctly : deleteInstrumentDatatableColumn");
                							columns.splice(index,1);
                						}
                					});
                					datatable.setColumnsConfig(columns);
                				},
                				
                				addColumnToDatatable:function(columns, newColumn){
                					if(null !== newColumn && undefined !== newColumn){
                						columns.push(newColumn);
                					}
                				},
                				
                				convertOutputPropertiesToDatatableColumn : function(property){
                					return  $commonATM.convertSinglePropertyToDatatableColumn(property,"outputContainerUsed.experimentProperties.",{"0":"Outputs"});
                				},
                				convertInputPropertiesToDatatableColumn : function(property){
                					return  $commonATM.convertSinglePropertyToDatatableColumn(property,"inputContainerUsed.experimentProperties.",{"0":"Inputs"});
                				},
                				
                				addExperimentPropertiesToDatatable : function(datatable){
                					var expProperties = $scope.experiment.experimentProperties.inputs;
                					var newColums = []; 
                					var $that = this;
                					if(expProperties != undefined && expProperties != null){
                						angular.forEach(expProperties, function(property){
                							if(!$that.$outputIsVoid && $scope.getLevel(property.levels, "ContainerOut")){
                								$that.addColumnToDatatable(this, $that.convertOutputPropertiesToDatatableColumn(property));														
                							}else if($scope.getLevel(property.levels, "ContainerIn")){
                								$that.addColumnToDatatable(this,  $that.convertInputPropertiesToDatatableColumn(property));								
                							}
                							
                						}, newColums);
                					}
                					datatable.setColumnsConfig(datatable.getColumnsConfig().concat(newColums))
                				},
                				customExperimentToView : undefined, //used to cutom the view with one atm
                				convertExperimentToDatatable : function(datatable, defaultOutputUnit){
                					var promises = [];
                					promises.push($commonATM.loadInputContainerFromAtomicTransfertMethods($scope.experiment.value.atomicTransfertMethods));					
                					promises.push($commonATM.loadOutputContainerFromAtomicTransfertMethods($scope.experiment.value.atomicTransfertMethods));
                					
                					var $that = this;
                	                $q.all(promises).then(function (result) {
                						var allData = [];
                						var inputContainers, outputContainers;
                						if(result[0].input){
                							inputContainers = result[0].input;
                						}else if(result[1].input){
                							inputContainers = result[1].input;
                						}
                						
                						if(!$that.$outputIsVoid && result[1].output){
                							outputContainers = result[1].output;
                						}else if(!$that.$outputIsVoid && result[0].output){
                							outputContainers = result[0].output;
                						}
                						
                						var l=0;
                						var atms = $scope.experiment.value.atomicTransfertMethods;
                						for(var i=0; i< atms.length;i++){
                							var atm = angular.copy(atms[i]);
                							for(var j=0; j<atm.inputContainerUseds.length ; j++){
                								
                								var inputContainerCode = atm.inputContainerUseds[j].code;
                								var inputContainer = inputContainers[inputContainerCode];
                								if(!$that.$outputIsVoid){
                									for(var k=0 ; k < atm.outputContainerUseds.length ; k++){
                							              var outputContainerCode = atm.outputContainerUseds[k].code;
                							              var outputContainer = outputContainers[outputContainerCode];
                							              
                							              allData[l] = {atomicIndex:i};
                							              allData[l].atomicTransfertMethod = atm;							              
                							              allData[l].inputContainerUsed = angular.copy(atm.inputContainerUseds[j]);
                							              allData[l].inputContainerUsed = $commonATM.updateContainerUsedFromContainer(allData[l].inputContainerUsed, inputContainer);
                							              allData[l].inputContainer = inputContainer;							              
                							              
                							              allData[l].outputContainerUsed = angular.copy(atm.outputContainerUseds[k]);
                							              allData[l].outputContainer = outputContainer;
                							              
                							              l++;							             
                							        }
                									if($that.customExperimentToView !== undefined){
                										$that.customExperimentToView(atm, inputContainers, outputContainers);
                									}
                								}else{
                									allData[l] = {atomicIndex:i};
                									allData[l].atomicTransfertMethod = atm;							              
                									allData[l].inputContainerUsed = angular.copy(atm.inputContainerUseds[j]);
                									allData[l].inputContainerUsed = $commonATM.updateContainerUsedFromContainer(allData[l].inputContainerUsed, inputContainer);
                									allData[l].inputContainer = inputContainer;	
                									l++;
                									if($that.customExperimentToView !== undefined){
                										$that.customExperimentToView(atm, inputContainers);
                									}
                								}
                							}
                						}
                						datatable.setData(allData, allData.length);
                						//add new atomic in datatable
                						$that.addNewAtomicTransfertMethodsInDatatable(datatable, defaultOutputUnit);							
                	                });
                				},
                				
                				addNewAtomicTransfertMethodsInDatatable : function(datatable, defaultOutputUnit){
                					if(null != mainService.getBasket() && null != mainService.getBasket().get()){
                						$that = this;
                						$commonATM.loadInputContainerFromBasket(mainService.getBasket().get())
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
                									line.inputContainerUsed = $commonATM.convertContainerToInputContainerUsed(line.inputContainer);
                									if(!$that.$outputIsVoid){
                										line.outputContainerUsed = $commonATM.newOutputContainerUsed(line.inputContainer, defaultOutputUnit);
                										line.outputContainer = undefined;
                									}
                									allData.push(line);
                								});
                								datatable.setData(allData, allData.length);			
                								
                						});
                					}					
                				},
                				
                				experimentToView:function(view, defaultOutputUnit){
                					if($scope.experiment.editMode){
                						this.convertExperimentToDatatable(view, defaultOutputUnit);													
                					}else{
                						this.addNewAtomicTransfertMethodsInDatatable(view, defaultOutputUnit);
                					}
                					
                					this.addExperimentPropertiesToDatatable(view);						
                				},
                				
                				refreshViewFromExperiment : function(view, defaultOutputUnit){
                					this.convertExperimentToDatatable(view, defaultOutputUnit);				
                				},
                				
                				viewToExperimentOneToOne :function(view){				
                					var allData = view.getData();
                					
                					var experiment = $scope.experiment.value; 
                					
                					if(allData != undefined){
                						experiment.atomicTransfertMethods = []; // to manage remove
                						for(var i=0;i<allData.length;i++){
                							var atomicIndex = allData[i].atomicIndex;								
                							experiment.atomicTransfertMethods[atomicIndex] = allData[i].atomicTransfertMethod
                							experiment.atomicTransfertMethods[atomicIndex].inputContainerUseds[0] = allData[i].inputContainerUsed;	
                							
                							$commonATM.removeNullProperties(experiment.atomicTransfertMethods[atomicIndex].inputContainerUseds[0].instrumentProperties);
                							$commonATM.removeNullProperties(experiment.atomicTransfertMethods[atomicIndex].inputContainerUseds[0].experimentProperties);
                							
                							if(!this.$outputIsVoid){
                								experiment.atomicTransfertMethods[atomicIndex].outputContainerUseds[0] = allData[i].outputContainerUsed;
                								$commonATM.removeNullProperties(experiment.atomicTransfertMethods[atomicIndex].outputContainerUseds[0].instrumentProperties);
                								$commonATM.removeNullProperties(experiment.atomicTransfertMethods[atomicIndex].outputContainerUseds[0].experimentProperties);
                							}
                	
                						}
                						//remove atomic null
                						var cleanAtomicTransfertMethods = [];
                						for(var i = 0; i < experiment.atomicTransfertMethods.length ; i++){
                							if(experiment.atomicTransfertMethods[i] !== null){
                								cleanAtomicTransfertMethods.push(experiment.atomicTransfertMethods[i]);
                							}
                						}
                						experiment.atomicTransfertMethods = cleanAtomicTransfertMethods;
                					}								
                				}
                		};
                		return view;
                		
                	};
                	return constructor;
                	
                	
                	
                //default view with singleDatatable	
                }]).factory('oneToOne', ['$http', '$parse', '$q', 'atmToSingleDatatable', 
                                         function($http, $parse, $q , atmToSingleDatatable){

                	var constructor = function($scope, atmViewInput){
                		var atmView = atmViewInput;
                		if(undefined === atmView){
                			atmView = atmToSingleDatatable($scope);
                		}
                		
                		atmView.newAtomicTransfertMethod = function(){
                			return {
                				class:"OneToOne",
                				line:"1", //TODO only exact for oneToOne of type  tube to tube but not for plate to plate
                				column:"1", 				
                				inputContainerUseds:new Array(1), 
                				outputContainerUseds:new Array(1)
                			};
                		};
                		
                		var oneToOne = {
                				defaultOutputUnit:{volume:undefined, concentration:undefined, quantity:undefined},
                				//specific of oneToOne and single datatable
                				viewToExperiment:function(view){
                					atmView.viewToExperimentOneToOne(view);
                				},				
                				experimentToView:function(view){
                					atmView.experimentToView(view, this.defaultOutputUnit);
                				},
                				
                				refreshViewFromExperiment: function(view){
                					atmView.refreshViewFromExperiment(view, this.defaultOutputUnit);
                				}				
                		};
                		return oneToOne;
                	};
                	return constructor;

                }]).factory('oneToVoid', ['$http', '$parse', '$q', 'atmToSingleDatatable', 
                                          function($http, $parse, $q , atmToSingleDatatable){
                	
                	var constructor = function($scope, atmViewInput){
                		var atmView = atmViewInput;
                		if(undefined === atmView){
                			atmView = atmToSingleDatatable($scope, true);
                		}
                		
                		atmView.newAtomicTransfertMethod = function(){
                			return {
                				class:"OneToVoid",
                				line:"1", 
                				column:"1", 				
                				inputContainerUseds:new Array(1)
                			};
                		};
                		
                		var oneToVoid = {
                				defaultOutputUnit:{volume:undefined, concentration:undefined, quantity:undefined},
                				//specific of oneToOne and single datatable
                				viewToExperiment:function(view){
                					atmView.viewToExperimentOneToOne(view);
                				},				
                				experimentToView:function(view){
                					atmView.experimentToView(view, this.defaultOutputUnit);
                				},
                				
                				refreshViewFromExperiment: function(view){
                					atmView.refreshViewFromExperiment(view, this.defaultOutputUnit);
                				}				
                		};
                		return oneToVoid;
                	};
                	return constructor;
                	
                	
                }])
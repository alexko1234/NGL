angular.module('atomicTransfereServices', [])
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
				getPropertyColumnType : function(type){
					if(type === "java.lang.String"){
						return "text";
					}else if(type === "java.lang.Double" || type === "java.lang.Integer" || type === "java.lang.Long"){
						return "number";
					}else if(type === "java.util.Date"){
						return "date";
					}else{
						throw 'not manage : '+type;
					}

					return type;
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
    				column.type = this.getPropertyColumnType(propertyDefinition.valueType);
    				column.choiceInList = propertyDefinition.choiceInList;
    				column.position=propertyDefinition.displayOrder;
    				column.defaultValues = propertyDefinition.defaultValue;
    				if(propertyDefinition.possibleValues!=undefined){
    					column.possibleValues = propertyDefinition.possibleValues;
    				}
    				if(column.choiceInList){
    					//column.listStyle = "bt-select";
    					column.filter = "codes:'value."+propertyDefinition.code+"'";    					
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
						code:container.code,
						categoryCode:container.categoryCode, 
						contents:container.contents, //used in rules							
					    locationOnContainerSupport:container.support,
					    volume:container.mesuredVolume, //used in rules
						concentration:container.mesuredConcentration,  //used in rules
						quantity:container.mesuredQuantity,
						instrumentProperties:undefined,
					    experimentProperties:undefined,
					    percentage:100, //rules by defaut need check with server
						//can be updated
						sampleCodes:container.sampleCodes,
						projectCodes:container.projectCodes,
					    fromExperimentTypeCodes:container.fromExperimentTypeCodes,
					    processTypeCodes:(container.processTypeCode)?[container.processTypeCode]:container.processTypeCodes,
						inputProcessCodes:container.inputProcessCodes
						
					};
					/*
					 return {"state":container.state
						}; 
					 
					 */
				},
				updateContainerUsedFromContainer : function(containerUsed, container){
					containerUsed.categoryCode = container.categoryCode; 
					containerUsed.contents = container.contents;
					containerUsed.locationOnContainerSupport = container.support;
					containerUsed.volume = container.mesuredVolume;
					containerUsed.concentration = container.mesuredConcentration;
					containerUsed.quantity = container.mesuredQuantity;
					containerUsed.sampleCodes=container.sampleCodes;
					containerUsed.projectCodes=container.projectCodes;
					containerUsed.fromExperimentTypeCodes=container.fromExperimentTypeCodes;
					containerUsed.processTypeCodes=(container.processTypeCode)?[container.processTypeCode]:container.processTypeCodes;
					containerUsed.inputProcessCodes=container.inputProcessCodes;
					return containerUsed;
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
						if(atomicTransfertMethod !== null){
							angular.forEach(atomicTransfertMethod.inputContainerUseds, function(inputContainerUsed){
									this.push(inputContainerUsed.code);
									}, this);	
						}
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
						if(atomicTransfertMethod !== null){
							angular.forEach(atomicTransfertMethod.outputContainerUseds, function(outputContainerUsed){
									if(null !== outputContainerUsed.code && undefined !== outputContainerUsed.code){
										this.push(outputContainerUsed.code);
									}									
								}, this);	
						}
																			
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
							if(containerInput !== null){
								this.push(containerInput.code);
							}							
						}, containerInputCodes);
						return this.getContainerListPromise(containerInputCodes).then(function(result){					 		
							return result.data;
					 	});					
				},
				
				/**
				 * Create a new OutputContainerUsed. By default unit is the same as inputContainer for volume, concentration, quantity
				 * In second time, we need to find the default concentration because several concentration are available for one container
				 */
				newOutputContainerUsed : function(defaultOutputUnit, atmLine, atmColumn, inputContainer){
					return {
						code:undefined,
						categoryCode:this.getContainerCategoryCode(), 
						locationOnContainerSupport:{
							categoryCode:this.getSupportContainerCategoryCode(), 
							line:atmLine,
							column:atmColumn
						},
						contents:undefined, //populate by the server
						volume:{unit:this.getUnit($parse('mesuredVolume')(inputContainer), defaultOutputUnit.volume)}, 
						concentration:{unit:this.getUnit($parse('mesuredConcentration')(inputContainer), defaultOutputUnit.concentration)}, 
						quantity:{unit:this.getUnit($parse('mesuredQuantity')(inputContainer), defaultOutputUnit.quantity)},
						instrumentProperties:undefined,
					    experimentProperties:undefined
					};
				},
				updateOutputContainerUsed:function(outputContainer, atmLine, atmColumn){
					if(null === outputContainer.categoryCode || undefined === outputContainer.categoryCode){
						outputContainer.categoryCode = this.getContainerCategoryCode();
					}
					if(null === outputContainer.locationOnContainerSupport || undefined === outputContainer.locationOnContainerSupport){
						outputContainer.locationOnContainerSupport = {};
					}
					if(null === outputContainer.locationOnContainerSupport.categoryCode || undefined === outputContainer.locationOnContainerSupport.categoryCode){
						outputContainer.locationOnContainerSupport.categoryCode = this.getSupportContainerCategoryCode();
					}
					if(null === outputContainer.locationOnContainerSupport.line || undefined === outputContainer.locationOnContainerSupport.line){
						outputContainer.locationOnContainerSupport.line = atmLine;
					}
					if(null === outputContainer.locationOnContainerSupport.column || undefined === outputContainer.locationOnContainerSupport.column){
						outputContainer.locationOnContainerSupport.column = atmColumn;
					}
					return outputContainer;
					
				},				
				getContainerCategoryCode :function(){
					var supportContainerCategoryCode = this.getSupportContainerCategoryCode();
					var instrumentType = mainService.get("instrumentType");
					var containerCategoryCode = [];
					angular.forEach(instrumentType.outContainerSupportCategories,function(value){
						if(supportContainerCategoryCode === value.code){
							containerCategoryCode[0] = value.containerCategory.code;
						}
					},containerCategoryCode);
					if(containerCategoryCode.length === 1){
						return containerCategoryCode[0];
					}else{
						throw "not found containerCategoryCode";
					}
					
				},				
				getSupportContainerCategoryCode :function(){
					return mainService.get("experiment").instrument.outContainerSupportCategoryCode;
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


	
}]).factory('atmToSingleDatatable', ['$http', '$parse', '$filter', '$q', 'commonAtomicTransfertMethod','mainService', 'datatable', 
                         function($http, $parse, $filter, $q, commonAtomicTransfertMethod, mainService, datatable){
	
	
	var constructor = function($scope, datatableConfig, outputIsVoid){
		
		var $outputIsVoid = (outputIsVoid !== undefined)?outputIsVoid : false; //false when void in output
		var $commonATM = commonAtomicTransfertMethod($scope);
		var $datatable = datatable(datatableConfig);
		
		var view = {
				$outputIsVoid : $outputIsVoid,
				$commonATM : $commonATM,
				data:$datatable,
				isAddNew:true, //used to add or not new input container in datatable
				defaultOutputUnit:{volume:undefined, concentration:undefined, quantity:undefined},
				newAtomicTransfertMethod : function(line, column){
					throw 'newAtomicTransfertMethod not defined in atmToSingleDatatable client';
				},
				/*
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
				*/
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
				addExperimentPropertiesToDatatable : function(experimentProperties){
					var expProperties = experimentProperties;
					var newColums = []; 
					var $that = this;
					if(expProperties != undefined && expProperties != null){
						if(!$that.$outputIsVoid){
							var outNewColumn = $filter('filter')(expProperties, 'ContainerOut');
							angular.forEach(outNewColumn, function(property){
								$that.addColumnToDatatable(this, $that.convertOutputPropertiesToDatatableColumn(property));														
							}, newColums);
						}
						
						var inNewColumn = $filter('filter')(expProperties, 'ContainerIn')
						angular.forEach(inNewColumn, function(property){
							$that.addColumnToDatatable(this, $that.convertInputPropertiesToDatatableColumn(property));														
						}, newColums);
												
					}
					this.data.setColumnsConfig(this.data.getColumnsConfig().concat(newColums))
				},
				
				customExperimentToView : undefined, //used to cutom the view with one atm
				
				convertExperimentATMToDatatable : function(experimentATMs){
					var promises = [];
					
					var atms = experimentATMs;
					
					promises.push($commonATM.loadInputContainerFromAtomicTransfertMethods(atms));					
					promises.push($commonATM.loadOutputContainerFromAtomicTransfertMethods(atms));
					
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
						
						var l=0, atomicIndex=0;
						for(var i=0; i< atms.length;i++){
							
							if(atms[i] === null){
								continue;
							}
							//var atm = angular.copy(atms[i]);
							var atm = $.extend(true,{}, atms[i]);
							
							atm.inputContainerUseds = $filter('orderBy')(atm.inputContainerUseds, 'code');
							
							for(var j=0; j<atm.inputContainerUseds.length ; j++){
								
								var inputContainerCode = atm.inputContainerUseds[j].code;
								var inputContainer = inputContainers[inputContainerCode];
								if(!$that.$outputIsVoid){
									for(var k=0 ; k < atm.outputContainerUseds.length ; k++){
							              var outputContainerCode = atm.outputContainerUseds[k].code;
							              var outputContainer = outputContainers[outputContainerCode];
							              
							              allData[l] = {atomicIndex:atomicIndex};
							              allData[l].atomicTransfertMethod = atm;
							              allData[l].inputContainer = inputContainer;							              
							              
							              //allData[l].inputContainerUsed = angular.copy(atm.inputContainerUseds[j]);
							              allData[l].inputContainerUsed = $.extend(true,{}, atm.inputContainerUseds[j]);
							              allData[l].inputContainerUsed = $commonATM.updateContainerUsedFromContainer(allData[l].inputContainerUsed, inputContainer);
							              
							              //allData[l].outputContainerUsed = angular.copy(atm.outputContainerUseds[k]);
							              allData[l].outputContainerUsed =  $.extend(true,{}, atm.outputContainerUseds[k]);
							              allData[l].outputContainerUsed = $commonATM.updateOutputContainerUsed(allData[l].outputContainerUsed, atm.line, atm.column);
							              allData[l].outputContainer = outputContainer;
							              l++;							             
							        }
									if($that.customExperimentToView !== undefined){
										$that.customExperimentToView(atm, inputContainers, outputContainers);
									}
								}else{
									allData[l] = {atomicIndex:atomicIndex};
									allData[l].atomicTransfertMethod = atm;							              
									//allData[l].inputContainerUsed = angular.copy(atm.inputContainerUseds[j]);
									allData[l].inputContainerUsed = $.extend(true,{}, atm.inputContainerUseds[j]);
									allData[l].inputContainerUsed = $commonATM.updateContainerUsedFromContainer(allData[l].inputContainerUsed, inputContainer);
									allData[l].inputContainer = inputContainer;	
									l++;
									if($that.customExperimentToView !== undefined){
										$that.customExperimentToView(atm, inputContainers);
									}
								}
							}
							atomicIndex++;
						}
						$that.data.setData(allData, allData.length);
						//add new atomic in datatable
						$that.addNewAtomicTransfertMethodsInDatatable();							
	                });
				},
				//One atomic by input only for OneToOne but not manyToOne ???
				/**
				 * type = OneToOne or ManyToOne
				 */
				addNewAtomicTransfertMethodsInDatatable : function(){
					if(null != mainService.getBasket() && null != mainService.getBasket().get() && this.isAddNew){
						$that = this;
						
						var type = $that.newAtomicTransfertMethod().class;
						
						$commonATM.loadInputContainerFromBasket(mainService.getBasket().get())
							.then(function(containers) {								
								var allData = [], i = 0;
								var atomicTransfertMethod = undefined;
								
								if($that.data.getData() !== undefined && $that.data.getData().length > 0){
									allData = $that.data.getData();
									i = allData.length;
								}
								
								if(type === "ManyToOne" && i === 0){
									atomicTransfertMethod =  $that.newAtomicTransfertMethod();
								}else if(type === "ManyToOne" && i > 0){
									atomicTransfertMethod =  allData[0].atomicTransfertMethod;
								}
								
								angular.forEach(containers, function(container){
									var line = {};
									if(type === "ManyToOne"){
										line.atomicTransfertMethod = atomicTransfertMethod;
										line.atomicIndex=0;
									}else{
										line.atomicTransfertMethod = $that.newAtomicTransfertMethod();
										line.atomicIndex=i++;
									}
										
									line.inputContainer = container;
									line.inputContainerUsed = $commonATM.convertContainerToInputContainerUsed(line.inputContainer);
									if(!$that.$outputIsVoid){
										line.outputContainerUsed = $commonATM.newOutputContainerUsed($that.defaultOutputUnit,line.atomicTransfertMethod.line,
												line.atomicTransfertMethod.column,line.inputContainer);
										line.outputContainer = undefined;
									}
									allData.push(line);
								});
								$that.data.setData(allData, allData.length);			
								
						});
					}					
				},
				
				experimentToView:function(experiment, experimentType){
					if(null === experiment || undefined === experiment){
						throw 'experiment is required';
					}
					
					if(!$scope.creationMode){
						this.convertExperimentATMToDatatable(experiment.atomicTransfertMethods);													
					}else{
						this.addNewAtomicTransfertMethodsInDatatable();
					}
					
					this.addExperimentPropertiesToDatatable(experimentType.propertiesDefinitions);						
				},
				
				refreshViewFromExperiment : function(experiment){
					if(null === experiment || undefined === experiment){
						throw 'experiment is required';
					}
					this.convertExperimentATMToDatatable(experiment.atomicTransfertMethods, experiment.instrument);				
				},
				viewToExperimentOneToVoid :function(experimentIn){
					this.viewToExperimentOneToOne(experimentIn);
				},
				viewToExperimentOneToOne :function(experimentIn){		
					if(null === experimentIn || undefined === experimentIn){
						throw 'experiment is required';
					}
					experiment = experimentIn;
					var allData = this.data.getData();
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
							if(experiment.atomicTransfertMethods[i]){
								cleanAtomicTransfertMethods.push(experiment.atomicTransfertMethods[i]);
							}
						}
						experiment.atomicTransfertMethods = cleanAtomicTransfertMethods;
					}								
				},
				viewToExperimentOneToMany :function(experimentIn){		
					if(null === experimentIn || undefined === experimentIn){
						throw 'experiment is required';
					}
					experiment = experimentIn;
					var allData = this.data.getData();
					if(allData != undefined){
						experiment.atomicTransfertMethods = []; // to manage remove
						//first reinitialise atomicTransfertMethod
						for(var i=0;i<allData.length;i++){
							var atomicIndex = allData[i].atomicIndex;								
							experiment.atomicTransfertMethods[atomicIndex] = allData[i].atomicTransfertMethod
							experiment.atomicTransfertMethods[atomicIndex].inputContainerUseds = new Array(0);
							experiment.atomicTransfertMethods[atomicIndex].outputContainerUseds = new Array(0);
							
							//oneTo
							var inputContainerUsed = allData[i].inputContainerUsed;
							$commonATM.removeNullProperties(inputContainerUsed.instrumentProperties);
							$commonATM.removeNullProperties(inputContainerUsed.experimentProperties);
							experiment.atomicTransfertMethods[atomicIndex].inputContainerUseds.push(inputContainerUsed);	
							
						}
						//ToMany
						for(var i=0;i<allData.length;i++){
							var atomicIndex = allData[i].atomicIndex;								
							
							var outputContainerUsed = allData[i].outputContainerUsed;
							$commonATM.removeNullProperties(outputContainerUsed.instrumentProperties);
							$commonATM.removeNullProperties(outputContainerUsed.experimentProperties);
							experiment.atomicTransfertMethods[atomicIndex].outputContainerUseds.push(outputContainerUsed);
							
	
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
				},
				viewToExperimentManyToOne :function(experimentIn){		
					if(null === experimentIn || undefined === experimentIn){
						throw 'experiment is required';
					}
					experiment = experimentIn;
					var allData = this.data.getData();
					if(allData != undefined){
						experiment.atomicTransfertMethods = []; // to manage remove
						//first reinitialise atomicTransfertMethod
						for(var i=0;i<allData.length;i++){
							var atomicIndex = allData[i].atomicIndex;								
							experiment.atomicTransfertMethods[atomicIndex] = allData[i].atomicTransfertMethod
							experiment.atomicTransfertMethods[atomicIndex].inputContainerUseds = new Array(0);
							experiment.atomicTransfertMethods[atomicIndex].outputContainerUseds = new Array(0);
							
							//ToOne
							var outputContainerUsed = allData[i].outputContainerUsed;
							$commonATM.removeNullProperties(outputContainerUsed.instrumentProperties);
							$commonATM.removeNullProperties(outputContainerUsed.experimentProperties);
							experiment.atomicTransfertMethods[atomicIndex].outputContainerUseds.push(outputContainerUsed);	
							
						}
						//ManyTo
						for(var i=0;i<allData.length;i++){
							var atomicIndex = allData[i].atomicIndex;								
							
							var inputContainerUsed = allData[i].inputContainerUsed;
							$commonATM.removeNullProperties(inputContainerUsed.instrumentProperties);
							$commonATM.removeNullProperties(inputContainerUsed.experimentProperties);
							experiment.atomicTransfertMethods[atomicIndex].inputContainerUseds.push(inputContainerUsed);
							
	
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
	
}]).factory('atmToDragNDrop', ['$http', '$parse', '$q', 'commonAtomicTransfertMethod','mainService', 'atmToSingleDatatable', 
                               function($http, $parse, $q, commonAtomicTransfertMethod, mainService, atmToSingleDatatable){	
	
	var constructor = function($scope, nbATM, datatableConfig){
		var $commonATM = commonAtomicTransfertMethod($scope);
		var $nbATM = nbATM;	
		var $atmToSingleDatatable = atmToSingleDatatable($scope, datatableConfig);
		$atmToSingleDatatable.isAddNew = false;
		var view = {
				$commonATM : $commonATM,
				$atmToSingleDatatable:$atmToSingleDatatable,
				defaultOutputUnit:{volume:undefined, concentration:undefined, quantity:undefined},
				data : {
					$atmToSingleDatatable : $atmToSingleDatatable,
					inputContainers:[],
					atm : [], 
					datatable : $atmToSingleDatatable.data,
					deleteInputContainer : function(inputContainer){
						this.inputContainers.splice(this.inputContainers.indexOf(inputContainer), 1);
					},
					duplicateInputContainer : function(inputContainer, position){
						this.inputContainers.splice(position+1, 0 , $.extend(true, {}, inputContainer));						
					},
					dropInAllInputContainer : function(atmIndex){
						var percentage = {value:0};
						
						var inputContainerUseds = this.atm[atmIndex].inputContainerUseds.concat(this.inputContainers);
						
						angular.forEach(inputContainerUseds, function(container){
							if(container.percentage !== undefined && container.percentage !== null){
								this.value +=  parseFloat(container.percentage);
							}			
						}, percentage)
						
						
						if(percentage.value != 100){
							var percentageForOneContainer = Math.floor(10000/inputContainerUseds.length)/100
							
							angular.forEach(inputContainerUseds, function(container){
								container.percentage = percentageForOneContainer;
							}, percentageForOneContainer)
							
						}
						
						this.inputContainers = [];
						this.atm[atmIndex].inputContainerUseds = inputContainerUseds;
						this.updateDatatable();
					},
					dropOutAllInputContainer : function(atmIndex){						
						var inputContainers = this.inputContainers.concat(this.atm[atmIndex].inputContainerUseds);
						this.inputContainers = inputContainers;
						this.atm[atmIndex].inputContainerUseds = [];	
						this.updateDatatable();
					},
					/**
					 * Call by drop directive
					 */
					drop : function(e, data, ngModel, alreadyInTheModel, fromModel){
						if(!alreadyInTheModel){
							$scope.atmService.data.updateDatatable();		
						}
					},
					
					updateDatatable : function(){
						this.$atmToSingleDatatable.convertExperimentATMToDatatable(this.atm);
					},
					updateFromDatatable : function(){
						var experiment = {value:{}};
						this.$atmToSingleDatatable.data.save();					
						this.$atmToSingleDatatable.viewToExperimentManyToOne(experiment);
						this.atm = experiment.value.atomicTransfertMethods;
					}
					
				},
				newAtomicTransfertMethod : function(line, column){
					throw 'newAtomicTransfertMethod not defined in atmToDragNDrop client';
				},
				
				convertExperimentToDnD:function(experimentATMs){
					var promises = [];
					
					var atms = experimentATMs;
					
					promises.push($commonATM.loadInputContainerFromAtomicTransfertMethods(atms));					
					
					var $that = this;
	                $q.all(promises).then(function (result) {
						var allData = [];
						var inputContainers, outputContainers;
						if(result[0].input){
							inputContainers = result[0].input;
						}else if(result[1].input){
							inputContainers = result[1].input;
						}
						
						//$that.data.atm = angular.copy(atms);
						$that.data.atm = $.extend(true,[], atms);
						for(var i=0; i< $that.data.atm.length;i++){
							var atm = $that.data.atm[i];
							for(var j=0; j<	atm.inputContainerUseds.length ; j++){
								var inputContainerCode = atm.inputContainerUseds[j].code;
								var inputContainer = inputContainers[inputContainerCode];
								atm.inputContainerUseds[j] = $commonATM.updateContainerUsedFromContainer(atm.inputContainerUseds[j], inputContainer);
							}
						}
						
						//add new atomic in datatable
						$that.addNewAtomicTransfertMethodsInDnD();
	                });
				},
				
				//exact for ManyToOne not for other
				addNewAtomicTransfertMethodsInDnD : function(){
					if(null != mainService.getBasket() && null != mainService.getBasket().get()){
						$that = this;
						$commonATM.loadInputContainerFromBasket(mainService.getBasket().get())
							.then(function(containers) {								
								var allData = [], i = 0;
								if($that.data.inputContainers !== undefined){
									allData = $that.data.inputContainers;									
								}
								
								angular.forEach(containers, function(container){
									var inputContainerUsed = $commonATM.convertContainerToInputContainerUsed(container);
									allData.push(inputContainerUsed);
								});
								$that.data.inputContainers = allData;	
								
						});
					}
					
					for(var i = this.data.atm.length; i < $nbATM; i++){
						var atm = this.newAtomicTransfertMethod(i+1);
						atm.outputContainerUseds.push($commonATM.newOutputContainerUsed(this.defaultOutputUnit, atm.line, atm.column));
						this.data.atm.push(atm);
					}
					
				},
				experimentToView:function(experiment, experimentType){
					if(null === experiment || undefined === experiment){
						throw 'experiment is required';
					}
					if(!$scope.creationMode){
						this.convertExperimentToDnD(experiment.atomicTransfertMethods);	
						this.$atmToSingleDatatable.convertExperimentATMToDatatable(experiment.atomicTransfertMethods);
					}else{
						this.addNewAtomicTransfertMethodsInDnD();
					}	
					this.$atmToSingleDatatable.addExperimentPropertiesToDatatable(experimentType.propertiesDefinitions);
					
				},
				viewToExperiment :function(experiment){		
					if(null === experiment || undefined === experiment){
						throw 'experiment is required';
					}
					this.$atmToSingleDatatable.data.save();					
					this.$atmToSingleDatatable.viewToExperimentManyToOne(experiment);
					this.data.atm = experiment.atomicTransfertMethods;
				},
				refreshViewFromExperiment:function(experiment){
					if(null === experiment || undefined === experiment){
						throw 'experiment is required';
					}
					this.convertExperimentToDnD(experiment.atomicTransfertMethods);
					this.$atmToSingleDatatable.convertExperimentATMToDatatable(experiment.atomicTransfertMethods, experiment.instrument);
				}
		}
		
		return view;
	};
	return constructor;

}]).factory('atmToGenerateMany', ['$http', '$parse', '$q', 'commonAtomicTransfertMethod','mainService', 'atmToSingleDatatable', 'datatable', 
                               function($http, $parse, $q, commonAtomicTransfertMethod, mainService, atmToSingleDatatable, datatable){

	var constructor = function($scope, datatableConfigTubeParam, datatableConfigTubeConfig){
		var $commonATM = commonAtomicTransfertMethod($scope);
		
		var $datatable = datatable(datatableConfigTubeParam);
		var $atmToSingleDatatable = atmToSingleDatatable($scope, datatableConfigTubeConfig);
		$atmToSingleDatatable.isAddNew = false;
		var view = {
				$commonATM : $commonATM,
				$atmToSingleDatatable:$atmToSingleDatatable,
				defaultOutputUnit:{volume:undefined, concentration:undefined, quantity:undefined},
				data : {
					$atmToSingleDatatable : $atmToSingleDatatable,
					datatableParam : $datatable,
					atm : [], 
					datatableConfig : $atmToSingleDatatable.data,
					updateDatatable : function(){
						this.$atmToSingleDatatable.convertExperimentATMToDatatable(this.atm);
					},					
					
				},
				newAtomicTransfertMethod : function(){
					throw 'newAtomicTransfertMethod not defined in atmToGenerateMany client';
				},
				generateATM:function(){
					this.data.datatableParam.save();
					var allData = this.data.datatableParam.getData();
					this.data.atm = [];
					for(var i = 0; i < allData.length; i++){
						var data = allData[i];
						var atm = this.newAtomicTransfertMethod();
						atm.inputContainerUseds.push($commonATM.convertContainerToInputContainerUsed(data.inputContainer));
						
						for(var j = 0; j < data.outputNumber ; j++){
							atm.outputContainerUseds.push($commonATM.newOutputContainerUsed(this.defaultOutputUnit,atm.line,atm.column));
						}
						this.data.atm.push(atm);
					}
					this.data.updateDatatable();
				},
				convertExperimentToData:function(experimentATMs){
					var promises = [];
					
					var atms = experimentATMs;
					var $that = this;
					$commonATM.loadInputContainerFromAtomicTransfertMethods(atms).then(function (result) {
						var allData = [];
						var inputContainers = result.input;
					
						//$that.data.atm = angular.copy(atms);
						$that.data.atm = $.extend(true,[], atms);
						var allData = []
						for(var i=0; i< $that.data.atm.length;i++){
							var atm = $that.data.atm[i];
							var inputContainerCode = atm.inputContainerUseds[0].code;
							var inputContainer = inputContainers[inputContainerCode];
							allData.push({inputContainer:inputContainer, outputNumber:atm.outputContainerUseds.length});
						}
						$that.data.datatableParam.setData(allData, allData.length);
						//add new atomic in datatable
						$that.addNewAtomicTransfertMethodsInData();
	                });
				},
				
				//exact for ManyToOne
				addNewAtomicTransfertMethodsInData : function(){
					if(null != mainService.getBasket() && null != mainService.getBasket().get()){
						$that = this;
						$commonATM.loadInputContainerFromBasket(mainService.getBasket().get())
							.then(function(containers) {								
								var allData = [];
								if($that.data.datatableParam.getData() !== undefined){
									allData = $that.data.datatableParam.getData();									
								}
								
								angular.forEach(containers, function(container){									
									allData.push({inputContainer:container, outputNumber:undefined});
								});
								$that.data.datatableParam.setData(allData);									
						});
					}										
				},
				experimentToView:function(experiment, experimentType){
					if(null === experiment || undefined === experiment){
						throw 'experiment is required';
					}
					if(!$scope.creationMode){
						this.convertExperimentToData(experiment.atomicTransfertMethods);	
						this.$atmToSingleDatatable.convertExperimentATMToDatatable(experiment.atomicTransfertMethods);
					}else{
						this.addNewAtomicTransfertMethodsInData();
					}	
					this.$atmToSingleDatatable.addExperimentPropertiesToDatatable(experimentType.propertiesDefinitions);					
				},
				viewToExperiment :function(experiment){		
					if(null === experiment || undefined === experiment){
						throw 'experiment is required';
					}
					this.$atmToSingleDatatable.data.save();					
					this.$atmToSingleDatatable.viewToExperimentOneToMany(experiment);					
				},
				refreshViewFromExperiment:function(experiment){
					if(null === experiment || undefined === experiment){
						throw 'experiment is required';
					}					
					this.convertExperimentToData(experiment.atomicTransfertMethods);
					this.$atmToSingleDatatable.convertExperimentATMToDatatable(experiment.atomicTransfertMethods, experiment.instrument);
				}
		}
		
		return view;
	};
	return constructor;
	
}]);

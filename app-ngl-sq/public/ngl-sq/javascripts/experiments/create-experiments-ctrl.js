angular.module('home').controller('CreateNewCtrl',['$scope', '$window','$http','lists','$parse','$q','$position','$routeParams', function($scope,$window, $http,lists,$parse,$q,$position,$routeParams) {
	$scope.experiment = {
		outputGenerated:false,
		outputVoid:false,
		doPurif:false,
		doQc:false,
		value: {
			code:"",
			typeCode:"",
			state:{
				resolutionCodes:[],
				code:"N"
			},
			protocolCode:"",
			instrument:{
				code:"",
				categoryCode:"",
				outContainerSupportCategoryCode:""
			},
			atomicTransfertMethods:[],
			comments:[],
			traceInformation:{
				createUser:"",
				creationDate:"",
				modifyUser:"",
				modifyDate:""
			}
		}
	};
	
	$scope.setImage = function(imageData, imageName, imageFullSizeWidth, imageFullSizeHeight) {
		$scope.modalImage = imageData;
		
		$scope.modalTitle = imageName;
	
		var margin = 25;		
		var zoom = Math.min((document.body.clientWidth - margin) / imageFullSizeWidth, 1);

		$scope.modalWidth = imageFullSizeWidth * zoom;
		$scope.modalHeight = imageFullSizeHeight * zoom; //in order to conserve image ratio
		$scope.modalLeft = (document.body.clientWidth - $scope.modalWidth)/2;
	
		$scope.modalTop = (window.innerHeight - $scope.modalHeight)/2;
	
		$scope.modalTop = $scope.modalTop - 50; //height of header and footer
	}
	
	$scope.experiment.comments = {
			save:function(){
				$scope.clearMessages();
				$scope.experiment.value.comments.push({"comment":$scope.experiment.comment});
				$http.put(jsRoutes.controllers.experiments.api.Experiments.updateComments($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess')
						$scope.experiment.value = data;
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-danger";
					$scope.message.text = Messages('experiments.msg.save.error');

					$scope.message.details = data;
					$scope.message.isDetails = true;
				});
			}
	};
	
	$scope.experiment.experimentInformation = {
			protocols:{},
			resolutions:{},
			enabled:true,
			toggleEdit:function(){
				this.enabled = !this.enabled;
			},
			save:function(){
					if($scope.experiment.value._id){
						$scope.clearMessages();
						return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateExperimentInformations($scope.experiment.value.code).url, $scope.experiment.value)
						.success(function(data, status, headers, config) {
							if(data!=null){
								$scope.experiment.value = data;
							}
						})
						.error(function(data, status, headers, config) {
							$scope.message.clazz = "alert alert-danger";
							$scope.message.text += Messages('experiments.msg.save.error');
							$scope.message.details += data;
							$scope.message.isDetails = true;
						});
					}else{
						$scope.save();
					}
			}
	};
	
	$scope.isOutputGenerated = function(){
		var j = 1;
		while($scope.experiment.value.atomicTransfertMethods[(j-1)] != null){
			if($scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUsed != null || $scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUseds != null){
				$scope.experiment.outputGenerated = true;
				return true
			}
			j++;
		}
			
		return false;
	
	};
	
	$scope.getTemplate = function(){
		console.log("get template== "+$scope.experiment.outputVoid);
		if($scope.experiment.value.instrument.outContainerSupportCategoryCode){
			$scope.experiment.inputTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getTemplate($scope.experimentType.atomicTransfertMethod, $scope.experiment.value.instrument.inContainerSupportCategoryCode,$scope.experiment.value.instrument.outContainerSupportCategoryCode).url;
		}else if($scope.experiment.outputVoid){
			$scope.experiment.inputTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getTemplate($scope.experimentType.atomicTransfertMethod, $scope.experiment.value.instrument.inContainerSupportCategoryCode,'void').url;
		}
	};
	
	$scope.experiment.experimentProperties = {
			enabled:true,
			toggleEdit:function(){
				this.enabled = !this.enabled;
			},
			save:function(){
					$scope.clearMessages();
					
					$scope.$broadcast('InputToExperiment', $scope.experimentType.atomicTransfertMethod);
					$scope.$broadcast('OutputToExperiment', $scope.experimentType.atomicTransfertMethod);
					
					return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateExperimentProperties($scope.experiment.value.code).url, $scope.experiment.value)
					.success(function(data, status, headers, config) {
						if(data!=null){
							$scope.experiment.value = data;
							$scope.$broadcast('experimentToInput', $scope.experimentType.atomicTransfertMethod);
						}
					})
					.error(function(data, status, headers, config) {
						$scope.message.clazz = "alert alert-danger";
						$scope.message.text += Messages('experiments.msg.save.error');

						$scope.message.details += data;
						$scope.message.isDetails = true;
					});
				
			}
	};
	
	$scope.addExperimentPropertiesInputsColumns = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		if(data != undefined){
			angular.forEach(data, function(property){
				if($scope.getLevel( property.levels, "ContainerIn")){		
					if(property.choiceInList){
						var possibleValues = $scope.possibleValuesToMap(property.possibleValues);
					}
					$scope.$broadcast('addExperimentPropertiesInput', property, possibleValues);
				}
			});
			$scope.$broadcast('addExperimentPropertiesInputToScope', property);		
		}
	};
	
	$scope.experiment.instrumentInformation = {
		instrumentUsedTypes:{},
		instrumentCategorys:{},
		instruments:{},
		enabled:true,
		toggleEdit:function(){
			this.enabled = !this.enabled;
		},
		save:function(){
			$scope.clearMessages();
			if(this.instruments.selected){
				$scope.experiment.value.instrument.code = this.instruments.selected.code;
				return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentInformations($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.experiment.value = data;
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-danger";
					$scope.message.text += Messages('experiments.msg.save.error');
					$scope.message.details += data;
					$scope.message.isDetails = true;
				});
			}
		}	
	};
	
	$scope.experiment.instrumentProperties = {
		inputs:[],
		enabled:true,
		toggleEdit:function(){
			this.enabled = !this.enabled;
		},
		save:function(){
				$scope.clearMessages();
				$scope.$broadcast('inputToExperiment', $scope.experimentType.atomicTransfertMethod);
				$scope.$broadcast('outputToExperiment', $scope.experimentType.atomicTransfertMethod);
				
				return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentProperties($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.experiment.value = data;
						$scope.$broadcast('experimentToInput', $scope.experimentType.atomicTransfertMethod);
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-danger";
					$scope.message.text += Messages('experiments.msg.save.error');

					$scope.message.details += data;
					$scope.message.isDetails = true;
					alert("error");
				});
		}
	};

	
	$scope.saveContainers = function(){
		$scope.clearMessages();
		return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateContainers($scope.experiment.value.code).url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.experiment.value = data;
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});

	};

	$scope.clearMessages  = function(){
		$scope.message = {clazz : undefined, text : undefined, showDetails : false, isDetails : false, details : []};
	};
	
	$scope.generateSampleSheet = function(){
		$http.post(jsRoutes.instruments.io.Outputs.sampleSheets().url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess')
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
			alert("error");
		});
	};
	
	$scope.saveAllPromise = function(){
		if(!$scope.saveInProgress){
			$scope.saveInProgress = true;
			var promises = [];
			$scope.$broadcast('save', promises, $scope.saveAll);
		}
	};
	
	$scope.$on('viewSaved', function(e, promises, func) {
		$scope.message.details = {};
		$scope.message.isDetails = false;
		
		$scope.experiment.experimentProperties.enabled = false;
		$scope.experiment.experimentInformation.enabled = false;
		$scope.experiment.instrumentProperties.enabled = false;
		$scope.experiment.instrumentInformation.enabled = false;
		if($scope.experiment.value._id != undefined){
			promises.push($scope.experiment.instrumentProperties.save());
	
			promises.push($scope.experiment.instrumentInformation.save());
			
			promises.push($scope.experiment.experimentInformation.save());
	
			promises.push($scope.experiment.experimentProperties.save());
	
			promises.push($scope.saveContainers());
		}else{
			promises.push($scope.save());
		}
		
		if(func){
			func(promises);
		}
	});
	
	$scope.saveAll = function(promises){
		$q.all(promises).then(function (res) {
			if($scope.message.text != Messages('experiments.msg.save.error')){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess');
			}
			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.$broadcast('refresh');
			$scope.saveInProgress = false;
		},function(reason) {
		    console.log(reason);
		    $scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.$broadcast('refresh');
			$scope.saveInProgress = false;
		  });
	};
	
	$scope.save = function(){
		return $http.post(jsRoutes.controllers.experiments.api.Experiments.save().url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess')
				$scope.experiment.value = data;
				$scope.saveInProgress = false;
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};
	
	$scope.saveAllAndChangeState = function(){
		if(!$scope.saveInProgress){
			var promises = [];
			$scope.$broadcast('save', promises, $scope.changeState);
			$scope.saveInProgress = true;
		}
	};
	
	$scope.changeState = function(promises){
		$q.all(promises).then(function (res) {
			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.clearMessages();
			var promise = $http.put(jsRoutes.controllers.experiments.api.Experiments.nextState($scope.experiment.value.code).url)
			.success(function(data, status, headers, config) {
				if(data!=null){
					$scope.message.clazz="alert alert-success";
					$scope.message.text=Messages('experiments.msg.save.sucess')
					$scope.experiment.value = data;
					if(!$scope.experiment.outputGenerated && $scope.isOutputGenerated()){
						$scope.$broadcast('addOutputColumns');
						$scope.addExperimentPropertiesOutputsColumns();
						$scope.addInstrumentPropertiesOutputsColumns();
						$scope.$broadcast('experimentToOutput', $scope.experimentType.atomicTransfertMethod);
						$scope.saveInProgress = false;
					}
					$scope.$broadcast('refreshView');
				}
			})
			.error(function(data, status, headers, config) {
				$scope.message.clazz = "alert alert-danger";
				$scope.message.text = Messages('experiments.msg.save.error');
				$scope.message.details = data;
				$scope.message.isDetails = true;
				$scope.saveInProgress = false;
			});
			
			 promise.then(function(res) {
				if(	$scope.message.text != Messages('experiments.msg.save.error')){
					$scope.message.clazz="alert alert-success";
					$scope.message.text=Messages('experiments.msg.save.sucess');
					$scope.saveInProgress = false;
				}
			}, function(reason){
				$scope.saveInProgress = false;
			});
		}, function(reason){
			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.saveInProgress = false;
		});
	};

	$scope.doPurifOrQc = function(code){
		$http.get(jsRoutes.controllers.experiments.api.ExperimentTypeNodes.list().url,{params:{"code":code}})
			.success(function(data, status, headers, config) {
				$scope.clearMessages();
				if(data != null && data[0] !=null){
					$scope.experiment.doPurif = data[0].doPurification;
					$scope.experiment.doQc = data[0].doQualityControl;
				}
			})
			.error(function(data, status, headers, config) {
				alert("error");
			});
	};
	
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
		if(atomicTransfertMethod == "ManyToOne"){
			$scope.experiment.value.atomicTransfertMethods[0] = {class:atomicTransfertMethod, inputContainerUseds:[]};
			angular.forEach(containers, function(container){
				$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.push({code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state});
			});
		}else{
			angular.forEach(containers, function(container,index){
				$scope.experiment.value.atomicTransfertMethods[index] = {class:atomicTransfertMethod, inputContainerUsed:[]};
				$scope.experiment.value.atomicTransfertMethods[index].inputContainerUsed = {code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state};
				
				if($scope.experiment.value.atomicTransfertMethods[index].class == "OneToVoid"){
					$scope.experiment.outputVoid = true;
				}
			});
		}
	};
	
	$scope.create_experiment = function(containers, atomicTransfertMethod){
		$scope.init_atomicTransfert(containers,atomicTransfertMethod);

		angular.element(document).ready(function() {
			if($scope.experiment.experimentProperties.inputs != undefined){
				angular.forEach($scope.experiment.experimentProperties.inputs, function(input){
					var getter = $parse("experiment.value.experimentProperties."+input.code+".value");
					getter.assign($scope,"");
				});

				$scope.addExperimentPropertiesInputsColumns();
			}
		});
	}
	
	$scope.init_experiment = function(containers,atomicTransfertMethod){
		if($scope.form != undefined && $scope.form.experiment != undefined){
			$scope.form.experiment = $scope.experiment;
			$scope.setForm($scope.form);
		}
		$scope.experiment.value.categoryCode = $scope.experimentType.category.code;
		$scope.experiment.value.atomicTransfertMethods = {};
		if($scope.experiment.value.code === ""){
			$scope.create_experiment(containers,atomicTransfertMethod);
		}
		
	};
	
	$scope.getInstrumentsTrigger = function(){
		if($scope.experiment.value.instrument != undefined && $scope.experiment.value.instrument.typeCode != null){
			$scope.getInstruments(false);
			$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,false);
		}
	};
	
	$scope.getInstruments = function(loaded){
		if($scope.experiment.value.instrument.typeCode === null){
			$scope.experiment.instrumentProperties.inputs = [];
			$scope.experiment.instrumentInformation.instrumentCategorys.inputs = [];
			
		}
		
		$scope.$broadcast('deleteInstrumentPropertiesInputs', "Instruments");
		$scope.$broadcast('deleteInstrumentPropertiesOutputs', "Instruments");
		
		if(loaded == false){
			$scope.experiment.value.instrumentProperties = {};
			$scope.experiment.value.instrument.outContainerSupportCategoryCode = "";
			if($scope.experimentType.atomicTransfertMethod == "ManyToOne"){
				$scope.experiment.value.atomicTransfertMethods = $scope.experiment.value.atomicTransfertMethods.map(function(atomicTransfertMethod){
					atomicTransfertMethod.inputContainerUseds = atomicTransfertMethod.inputContainerUseds.map(function(inputContainerUsed){
						inputContainerUsed.instrumentProperties = {};
						return inputContainerUsed;
					});
					return atomicTransfertMethod;
				});
			}else{
				for(var i=0;i< $scope.experiment.value.atomicTransfertMethods.length;i++){
					 $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = {};
				}
				
				//Bug with the map ?
				/*$scope.experiment.value.atomicTransfertMethods = $scope.experiment.value.atomicTransfertMethods.map(function(atomicTransfertMethod){
						atomicTransfertMethod.inputContainerUsed.instrumentProperties = {};
						return atomicTransfertMethod;
				});*/
			}
		}

		if($scope.experiment.value.instrument.typeCode != null ){
			$scope.getInstrumentCategory($scope.experiment.value.instrument.typeCode);
			$scope.lists.refresh.instruments({"typeCode":$scope.experiment.value.instrument.typeCode});
			$scope.lists.refresh.containerSupportCategories({"instrumentUsedTypeCode":$scope.experiment.value.instrument.typeCode});
		}
	};
	
	$scope.getInstrumentCategory = function(intrumentUsedTypeCode){
		$http.get(jsRoutes.controllers.instruments.api.InstrumentCategories.list().url, {params:{instrumentTypeCode:intrumentUsedTypeCode, list:true}})
		.success(function(data, status, headers, config) {
			$scope.experiment.value.instrument.categoryCode = data[0].code;
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};
	
	$scope.getInstrumentProperties = function(code,loaded){
		$scope.clearMessages();
		if(!loaded){
			loaded = false;
		}
		$http.get(jsRoutes.controllers.experiments.api.Experiments.getInstrumentProperties(code).url)
		.success(function(data, status, headers, config) {

			$scope.experiment.instrumentProperties.inputs = data;

			angular.forEach(data, function(property){
				//Creation of the properties on the scope
				if(loaded == false){
					var getter = $parse("experiment.value.instrumentProperties."+property.code+".value");
					getter.assign($scope,"");
				}
				
				if($scope.getLevel( property.levels,"ContainerIn")){
					if(property.choiceInList){
						//var possibleValues = [];
						var possibleValues = property.possibleValues.map(function(propertyPossibleValue){
							return {"name":propertyPossibleValue.value,"code":propertyPossibleValue.value};
						});
					}
					
					$scope.$broadcast('addInstrumentPropertiesInput', property, possibleValues);
				}
			});
			
			$scope.$broadcast('addInstrumentPropertiesInputToScope', data);
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};
	
	$scope.possibleValuesToSelect = function(possibleValues){
		return possibleValues.map(function(possibleValue){
			return {"code":possibleValue.value,"name":possibleValue.value};
		});
	};
	
	$scope.addExperimentPropertiesOutputsColumns = function(){
		if( $scope.experiment.experimentProperties.inputs != undefined){
			var data = $scope.experiment.experimentProperties.inputs;
			var outputGenerated = $scope.isOutputGenerated();
			
			angular.forEach(data, function(property){
				if($scope.getLevel( property.levels, "ContainerOut")){
					if(property.choiceInList){
						var possibleValues = $scope.possibleValuesToSelect(property.possibleValues);
					}
					
					$scope.$broadcast('addExperimentPropertiesOutput', property, possibleValues);
				}
			});
			
			if(outputGenerated){
				$scope.$broadcast('addExperimentPropertiesOutputToScope', data);
			}
		}
	};
	
	$scope.addInstrumentPropertiesOutputsColumns = function(){
		var data = $scope.experiment.instrumentProperties.inputs;		
		var outputGenerated = $scope.isOutputGenerated();
		
		angular.forEach(data, function(property){
			if($scope.getLevel( property.levels,"ContainerOut")){	 					
				if(property.choiceInList){
					var possibleValues = $scope.possibleValuesToSelect(property.possibleValues);
				}

				$scope.$broadcast('addInstrumentPropertiesOutput', property, possibleValues);
			}
		});
		
		if(outputGenerated){
			$scope.$broadcast('addInstrumentPropertiesOutputToScope', data);
		}
		
	};
	
	$scope.getLevel = function(levels,level){
		if(levels != undefined){
			for(var i=0;i<levels.length;i++){
				if(levels[i].code === level){
					return true;
				}
			}
		}
		return false;
	};
	
	$scope.experimentType =  {};
	var promise = $q.when($scope.experimentType);
	var experiment = {
		code:"",
		typeCode:"",
		state:{
			resolutionCodes:[],
			code:"N"
		},
		protocolCode:"",
		instrument:{
			code:"",
			categoryCode:"",
			outContainerSupportCategoryCode:""
		},
		atomicTransfertMethods:[],
		comments:[],
		traceInformation:{
			createUser:"",
			creationDate:"",
			modifyUser:"",
			modifyDate:""
		}
	};
	if($routeParams.experimentCode){
	   promise = $http.get(jsRoutes.controllers.experiments.api.Experiments.get($routeParams.experimentCode).url)
		.success(function(data, status, headers, config) {
			experiment = data;
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	}
		promise.then(function(result) {
			if($routeParams.experimentTypeCode){
				$scope.experimentType.code = $routeParams.experimentTypeCode;
			}else{
				$scope.experimentType.code = experiment.typeCode;
			}
			
			promise = $http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.get($scope.experimentType.code).url)
			.success(function(data, status, headers, config) {
				$scope.experimentType.category = data.category;
				$scope.experimentType.atomicTransfertMethod = data.atomicTransfertMethod;
				if($scope.experimentType.atomicTransfertMethod == "OneToVoid"){
					$scope.experiment.outputVoid = true;
					$scope.getTemplate();
				}
				experiment.typeCode =  data.code;
			})
			.error(function(data, status, headers, config) {
				$scope.message.clazz = "alert alert-danger";
				$scope.message.text = Messages('experiments.msg.save.error');

				$scope.message.details = data;
				$scope.message.isDetails = true;
			});
			promise.then(function(result) {
				$scope.lists = lists;
				$scope.lists.clear("containerSupportCategories");
				$scope.lists.clear("instruments");
				$scope.lists.clear("instrumentUsedTypes");
				$scope.lists.clear("protocols");
				$scope.lists.clear("resolutions");
				$scope.lists.clear("states");
				
				$scope.lists.refresh.instrumentUsedTypes({"experimentTypeCode":experiment.typeCode});
				$scope.lists.refresh.protocols({"experimentTypeCode":experiment.typeCode});
				$scope.lists.refresh.resolutions({"objectTypeCode":"Experiment"});
				$scope.lists.refresh.states({"objectTypeCode":"Experiment"});
				
				if(!$routeParams.experimentCode){
					$scope.form = $scope.getForm();
					experiment.instrument.inContainerSupportCategoryCode = $scope.form.containerSupportCategory;
					$scope.experiment.editMode=false;
					$scope.experiment.value = experiment;
				}else{
					$scope.experiment.editMode=true;
					$scope.experiment.value.instrument.outContainerSupportCategoryCode = experiment.instrument.outContainerSupportCategoryCode;
					$scope.experiment.value = experiment;
					if($scope.experiment.value.state.code === "F"){
						$scope.experiment.instrumentProperties.enabled = false;
						$scope.experiment.experimentProperties.enabled = false;
						$scope.experiment.instrumentInformation.enabled = false;
						$scope.experiment.experimentInformation.enabled = false;
					}
					$scope.getInstruments();
					$scope.getTemplate();
					$scope.addExperimentPropertiesInputsColumns();
				}
			});
	  });
}]);
angular.module('home').controller('CreateNewCtrl',['$scope', '$window','$http','lists','$parse','$q','$position', function($scope,$window, $http,lists,$parse,$q,$position) {
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
						$http.put(jsRoutes.controllers.experiments.api.Experiments.updateExperimentInformations($scope.experiment.value.code).url, $scope.experiment.value)
						.success(function(data, status, headers, config) {
							if(data!=null){
								//$scope.message.clazz="alert alert-success";
								//$scope.message.text=Messages('experiments.msg.save.sucess')
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
	
	$scope.getInputTemplate = function(){
		if($scope.experiment.value.instrument.outContainerSupportCategoryCode){
			$scope.experiment.inputTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getInputTemplate($scope.experimentType.atomicTransfertMethod, $scope.experiment.value.instrument.outContainerSupportCategoryCode).url;
		}
	};
	
	$scope.getOutputTemplate = function(){
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
					
					$http.put(jsRoutes.controllers.experiments.api.Experiments.updateExperimentProperties($scope.experiment.value.code).url, $scope.experiment.value)
					.success(function(data, status, headers, config) {
						if(data!=null){
							//$scope.message.clazz="alert alert-success";
							//$scope.message.text=Messages('experiments.msg.save.sucess')
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
			for(var i=0; i<data.length;i++){
				if($scope.getLevel( data[i].levels, "ContainerIn")){		
					if(data[i].choiceInList){
						var possibleValues = $scope.possibleValuesToMap(data[i].possibleValues);
					}
					$scope.$broadcast('addExperimentPropertiesInput', data, possibleValues);
				}
			}
			$scope.$broadcast('addExperimentPropertiesInputToScope', data);		
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
				$http.put(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentInformations($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						//$scope.message.clazz="alert alert-success";
						//$scope.message.text=Messages('experiments.msg.save.sucess')
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
			if($scope.experiment.value._id){
				if($scope.experiment.value.instrument.code){
					$scope.clearMessages();
					$scope.$broadcast('inputToExperiment', $scope.experimentType.atomicTransfertMethod);
					$scope.$broadcast('outputToExperiment', $scope.experimentType.atomicTransfertMethod);
					
					$http.put(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentProperties($scope.experiment.value.code).url, $scope.experiment.value)
					.success(function(data, status, headers, config) {
						if(data!=null){
							//$scope.message.clazz="alert alert-success";
							//$scope.message.text=Messages('experiments.msg.save.sucess')
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
			}else{
			$scope.save();
		}
		}
	};

	
	$scope.saveContainers = function(){
		$scope.clearMessages();
		$http.put(jsRoutes.controllers.experiments.api.Experiments.updateContainers($scope.experiment.value.code).url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				//$scope.message.clazz="alert alert-success";
				//$scope.message.text += Messages('experiments.msg.save.sucess')
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
		var promises = [];
		
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
	
			//promises.push($scope.datatable.save());
	
			promises.push($scope.saveContainers());
		}else{
			promises.push($scope.save());
		}
		
		return promises;
	};
	
	$scope.saveAll = function(){
		var promises = $scope.saveAllPromise();
		$q.all(promises).then(function (res) {
			if(	$scope.message.text != Messages('experiments.msg.save.error')){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess');
			}
			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
		});
	};
	
	$scope.save = function(){
		$http.post(jsRoutes.controllers.experiments.api.Experiments.save().url, $scope.experiment.value)
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
			alert("error");
		});
	};
	
	$scope.changeState = function(){
		var promises = $scope.saveAllPromise();
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
						$scope.$broadcast('experimentToInput', $scope.experimentType.atomicTransfertMethod);
					}
				}
			})
			.error(function(data, status, headers, config) {
				$scope.message.clazz = "alert alert-danger";
				$scope.message.text = Messages('experiments.msg.save.error');
				$scope.message.details = data;
				$scope.message.isDetails = true;
			});
			
			 promise.then(function(res) {
				if(	$scope.message.text != Messages('experiments.msg.save.error')){
					$scope.message.clazz="alert alert-success";
					$scope.message.text=Messages('experiments.msg.save.sucess');
				}
			});
		});
	};

	$scope.doPurifOrQc = function(code){
		$http.get(jsRoutes.controllers.experiments.api.ExperimentTypeNodes.list().url,{params:{"code":code}})
			.success(function(data, status, headers, config) {
				$scope.clearMessages();
				if(data!=null){
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
			for(var i=0;i<containers.length;i++){
				$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.push({code:containers[i].code,instrumentProperties:{},experimentProperties:{}});
			}
		}else{
			for(var i=0;i<containers.length;i++){
				$scope.experiment.value.atomicTransfertMethods[i] = {class:atomicTransfertMethod, inputContainerUsed:[]};
				$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed = {code:containers[i].code,instrumentProperties:{},experimentProperties:{}};
				
				if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToVoid"){
					$scope.experiment.outputVoid = true;
				}
			}
		}
	};
	
	$scope.create_experiment = function(containers, atomicTransfertMethod){
		$scope.init_atomicTransfert(containers,atomicTransfertMethod);

		angular.element(document).ready(function() {
			if($scope.experiment.experimentProperties.inputs != undefined){
				for(i=0; i<$scope.experiment.experimentProperties.inputs.length;i++){
					var getter = $parse("experiment.value.experimentProperties."+$scope.experiment.experimentProperties.inputs[i].code+".value");
					getter.assign($scope,"");
				}

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
	
	$scope.getInstruments = function(loaded){
		if($scope.experiment.value.instrument.typeCode === null){
			$scope.experiment.instrumentProperties.inputs = [];
			$scope.experiment.instrumentInformation.instrumentCategorys.inputs = [];
		}

		$scope.$broadcast('deleteInstrumentPropertiesInputs', "Instruments");
		$scope.$broadcast('deleteInstrumentPropertiesOutputs', "Instruments");
		
		if(loaded == false){
			$scope.experiment.value.instrumentProperties = {};
			if($scope.experimentType.atomicTransfertMethod == "ManyToOne"){
				var i = 0;
				while($scope.experiment.value.atomicTransfertMethods[i] != undefined){
					var j = 0;
					while($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j] != undefined){
						$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].instrumentProperties = {};
						j++;
					}
					i++;
				}
			}else{
				var i = 0;
				while($scope.experiment.value.atomicTransfertMethods[i] != undefined){
						$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = {};
						i++;
				}
			}
		}

		if($scope.experiment.value.instrument.typeCode != null ){
			$scope.getInstrumentCategory($scope.experiment.value.instrument.typeCode);
			$scope.lists.refresh.instruments({"typeCode":$scope.experiment.value.instrument.typeCode});
			$scope.lists.refresh.containerSupportCategories({"instrumentUsedTypeCode":$scope.experiment.value.instrument.typeCode});
			$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,loaded);
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

			for(var i=0; i<data.length;i++){	
				//Creation of the properties on the scope
				if(loaded == false){
					var getter = $parse("experiment.value.instrumentProperties."+data[i].code+".value");
					getter.assign($scope,"");
				}
				
				if($scope.getLevel( data[i].levels,"ContainerIn")){
					if(data[i].choiceInList){
						var possibleValues = [];
						var j = 0;
						while(data[i].possibleValues[j] != undefined){
							possibleValues[j] = {};
							possibleValues[j].name = data[i].possibleValues[j].value;
							possibleValues[j].code = data[i].possibleValues[j].value;
							j++;
						}
					}
					
					$scope.$broadcast('addInstrumentPropertiesInput', data[i], possibleValues);
				}
			}
			
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
		var selectPossibleValues = [];

		for(var i=0;i<possibleValues.length;i++){
			var value = possibleValues[i].value;
			var possibleValue = {};
			possibleValue.code = value;
			possibleValue.name = value;
			selectPossibleValues[i] = possibleValue;
		}

		return selectPossibleValues;
	};
	
	$scope.addExperimentPropertiesOutputsColumns = function(){
		if( $scope.experiment.experimentProperties.inputs != undefined){
			var data = $scope.experiment.experimentProperties.inputs;
			var outputGenerated = $scope.isOutputGenerated();
			
			for(var i=0; i<data.length;i++){
				if($scope.getLevel( data[i].levels, "ContainerOut")){
					if(data[i].choiceInList){
						var possibleValues = $scope.possibleValuesToSelect(data[i].possibleValues);
					}
					
					$scope.$broadcast('addExperimentPropertiesOutput', data, possibleValues);
				}
			}
			if(outputGenerated){
				$scope.$broadcast('addExperimentPropertiesOutputToScope', data);
			}
		}
	};
	
	$scope.addInstrumentPropertiesOutputsColumns = function(){
		var data = $scope.experiment.instrumentProperties.inputs;		
		var outputGenerated = $scope.isOutputGenerated();
		
		for(var i=0; i<data.length;i++){
			if($scope.getLevel( data[i].levels,"ContainerOut")){	 					
				if(data[i].choiceInList){
					var possibleValues = $scope.possibleValuesToSelect(data[i].possibleValues);
				}

				$scope.$broadcast('addInstrumentPropertiesOutput', data, possibleValues);
			}
		}
		
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
	
	$scope.init = function(experimentCategoryCode, atomicTransfertMethod, experiment){
	
		$scope.experimentType =  {};
		$scope.experimentType.category= {};
		$scope.experimentType.category.code = experimentCategoryCode;
		$scope.experimentType.atomicTransfertMethod = atomicTransfertMethod;
		if(experiment != ""){
			experiment =  JSON.parse(experiment);
		}
		
		$scope.form = $scope.getForm();
		if($scope.form != undefined && $scope.form.experimentType != undefined){
			$scope.experiment.value.typeCode = $scope.form.experimentType.code;
		}
		$scope.lists = lists;
	
		$scope.lists.refresh.instrumentUsedTypes({"experimentTypeCode":$scope.experiment.value.typeCode});
		$scope.lists.refresh.protocols({"experimentTypeCode":$scope.experiment.value.typeCode});
		$scope.lists.refresh.resolutions({"objectTypeCode":"Experiment"});
		$scope.lists.refresh.states({"objectTypeCode":"Experiment"});
		
		if(experiment == ""){
			$scope.experiment.editMode=false;
		}else{
			$scope.experiment.editMode=true;
			$scope.experiment.value.instrument.outContainerSupportCategoryCode = experiment.instrument.outContainerSupportCategoryCode;
			$scope.getInputTemplate();
			$scope.experiment.value = experiment;
			
			$scope.addExperimentPropertiesInputsColumns();
			
			if($scope.isOutputGenerated()){
				$scope.$broadcast('addOutputColumns');
				$scope.addExperimentPropertiesOutputsColumns();
				$scope.addInstrumentPropertiesOutputsColumns();
			}
		}
	};
}]);
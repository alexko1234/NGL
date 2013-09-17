function CreateNewCtrl($scope,$window, datatable, $http,comboLists,$parse) {
	$scope.experiment = {
			value: {
				code:"",
				typeCode:"",
				resolutionCode:"",
				protocolCode:"",
				stateCode:"N",
				instrument:{
					code:"",
					categoryCode:""
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
	
	
	$scope.datatableConfig = {
	    columnsUrl : jsRoutes.controllers.experiments.tpl.Experiments.getEditExperimentColumns().url,
	    compact:false,
		pagination:{
			active:true
		},		
		search:{
			active:false
		},
		order:{
			mode:'local', //or 
			active:true,
			by:'ContainerInputCode'
		},
		remove:{
			active:false,
		},
		save:{
			active:true,
			withoutEdit:true,
			url:function(value){
				return jsRoutes.controllers.experiments.api.Experiments.updateContainers(value.code).url;
			},
			callback : function(datatable){
				$scope.basket.reset();
			}
		},
		hide:{
			active:true
		},
		edit:{
			active:true
		},
		messages:{
			active:true
		},
		extraHeaders:{
			number:2,
			dynamic:true,
	}
	};
	
	$scope.inputContainersResolutions = [{"code":"IS","name":"IS"},{"code":"UA","name":"UA"}];
	$scope.ouputContainersResolutions = [{"code":"IWP","name":"IWP"},{"code":"A","name":"A"},{"code":"UA","name":"UA"}];
	
	$scope.experiment.experimentInformation = {
		protocols:{},
		resolutions:{},
		enabled:true,
		toggleEdit:function(){
			this.enabled = !this.enabled;
		},
		save:function(){
			if(this.enabled && this.protocols.selected &&  this.resolutions.selected.code){
				$scope.clearMessages();
				$scope.experiment.value.protocolCode = this.protocols.selected.code;
				$scope.experiment.value.resolutionCode = this.resolutions.selected.code;
				
				$http.post(jsRoutes.controllers.experiments.api.Experiments.updateExperimentInformations($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess')
						$scope.experiment.value = data;
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-error";
					$scope.message.text = Messages('experiments.msg.save.error');
					
					$scope.message.details = data;
					$scope.message.isDetails = true;
				});
			}
		}
	};

	
	$scope.saveContainers = function(){
		$scope.clearMessages();
		$http.post(jsRoutes.controllers.experiments.api.Experiments.updateContainers($scope.experiment.value.code).url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess')
				$scope.experiment.value = data;
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-error";
			$scope.message.text = Messages('experiments.msg.save.error');
			
			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
		
	};
	
	$scope.clearMessages  = function(){
		$scope.message = {clazz : undefined, text : undefined, showDetails : false, isDetails : false, details : []};
	}
	
	$scope.experiment.instrumentInformation = {
		instrumentUsedTypes:{},
		instruments:{},
		enabled:true,
		toggleEdit:function(){
			this.enabled = !this.enabled;
		},
		save:function(){
			$scope.clearMessages();
			if(this.instruments.selected){
				$scope.experiment.value.instrument.code = this.instruments.selected.code;
				$http.post(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentInformations($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess')
						$scope.experiment.value = data;
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-error";
					$scope.message.text = Messages('experiments.msg.save.error');
					
					$scope.message.details = data;
					$scope.message.isDetails = true;
				});
			}
		}	
	};
	
	$scope.experiment.experimentProperties = {
		enabled:true,
		toggleEdit:function(){
			this.enabled = !this.enabled;
		},
		save:function(){
			if(this.enabled){	
				$scope.clearMessages();
				for(var i=0;i<$scope.datatable.displayResult.length;i++){
					if($scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne"){
						for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
							$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed[j].experimentProperties = $scope.datatable.displayResult[i].inputExperimentProperties;
							i++;
						}
						if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
							$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].outputExperimentProperties;					
						}
					}else{
						$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].inputExperimentProperties;
						if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToMany"){
							if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
								for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
									$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].experimentProperties = $scope.datatable.displayResult[i].outputExperimentProperties;
									i++;
								}
							}
						}else{
							if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
								$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].outputExperimentProperties;					
							}
						}
					}
				}
				$http.post(jsRoutes.controllers.experiments.api.Experiments.updateExperimentProperties($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess')
						$scope.experiment.value = data;
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-error";
					$scope.message.text = Messages('experiments.msg.save.error');
					
					$scope.message.details = data;
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
			if(this.enabled && $scope.experiment.instrumentInformation.instrumentUsedTypes.selected){
				$scope.clearMessages();
				for(var i=0;i<$scope.datatable.displayResult.length;i++){
					if($scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne"){
						for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
							$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].instrumentProperties = $scope.datatable.displayResult[i].inputInstrumentProperties;
							i++;
						}
						if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
							$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].outputInstrumentProperties;
						}
					}else{
						$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].inputInstrumentProperties;
						if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToMany"){
							if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
								for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
									$scope.experiment.value.atomicTransfertMethods[i].ouputContainerUseds[j].instrumentProperties = $scope.datatable.displayResult[i].outputInstrumentProperties;
									i++;
								}
							}
						}else{
							if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
								$scope.experiment.value.atomicTransfertMethods[i].ouputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].outputInstrumentProperties;					
							}
						}
					}
				}
				$http.post(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentProperties($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess')
						$scope.experiment.value = data;
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-error";
					$scope.message.text = Messages('experiments.msg.save.error');
					
					$scope.message.details = data;
					$scope.message.isDetails = true;
					alert("error");
				});
			}
		}
	};
	
	
	$scope.setExperimentProperties = function(experimentProperties){
		$scope.experiment.experimentProperties.inputs = experimentProperties;
	};
	
	$scope.experiment.comments = {
		save:function(){
				$scope.clearMessages();
				$scope.experiment.value.comments.push({"comment":$scope.experiment.comment});
				$http.post(jsRoutes.controllers.experiments.api.Experiments.updateComments($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess')
						$scope.experiment.value = data;
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-error";
					$scope.message.text = Messages('experiments.msg.save.error');
					
					$scope.message.details = data;
					$scope.message.isDetails = true;
				});
		}	
	};
	
	$scope.outputGeneration = function(){
		$http.post(jsRoutes.controllers.experiments.api.Experiments.generateOutput($scope.experiment.value.code).url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.clearMessages();
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess');
				$scope.experiment.value = data;
				var basketList = $scope.getBasket().get();
				for(var i=0;i<basketList.length;i++){
					if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToOne"){
						$scope.datatable.displayResult[i].outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed;
					}else if($scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne"){
						$scope.datatable.displayResult[i].outputContainerUseds = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds;
						//In process -  gestion du rowspan
						//example
						//$scope.datatable.config.columns[0].cells[0] = {"rowSpan":2};
						for(var j=0;j<$scope.datatable.config.columns.length;j++){
							if($scope.datatable.config.columns[j].extraHeaders[0] == "Outputs"){
								$scope.datatable.config.columns[j].cells[i] = {"rowSpan":$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length};
							}
						}
					}
				}
				
				$scope.datatable.addColumn(-1,$scope.datatable.newColumn("Code","outputContainerUsed.containerCode",true, true,true,"String",false,undefined,{"0":"Outputs"}));
				
				$scope.addExperimentPropertiesOutputsColumns();
				$scope.addInstrumentPropertiesOutputsColumns();
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-error";
			$scope.message.text = Messages('experiments.msg.save.error');
			
			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};
	
	$scope.getInstrumentProperties = function(code){
		$scope.clearMessages();
		$http.get(jsRoutes.controllers.experiments.api.Experiments.getInstrumentProperties(code).url)
		.success(function(data, status, headers, config) {
			
			$scope.experiment.instrumentProperties.inputs = data;
			
			for(var i=0; i<data.length;i++){			
					//Creation of the properties on the scope
					var getter = $parse("experiment.value.instrumentProperties."+data[i].code+".value");
					getter.assign($scope,"");
				
					if(data[i].choiceInList){
						var possibleValues = $scope.possibleValuesToSelect(data[i].possibleValues);
					}
					$scope.datatable.addColumn(2,$scope.datatable.newColumn(data[i].name,"inputInstrumentProperties."+data[i].code+".value",true, true,true,"String",data[i].choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"}));
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-error";
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
	},

	
	$scope.addInstrumentPropertiesOutputsColumns = function(){
		var data = $scope.experiment.instrumentProperties.inputs;
		
		for (var j=1; j<$scope.getBasket().get().length+1; j++) {
			$scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUsed.instrumentProperties = {};
		}
		
		for(var i=0; i<data.length;i++){
			if($scope.getLevel( data[i].levels,"ContainerOut")){	 					
				if(data[i].choiceInList){
					var possibleValues = $scope.possibleValuesToSelect(data[i].possibleValues);
				}
				
				$scope.datatable.addColumn(2,$scope.datatable.newColumn(data[i].name,"outputInstrumentProperties."+data[i].code+".value",true, true,true,"String",data[i].choiceInList,possibleValues));
			}
		}
		
	},
	
	$scope.getLevel = function(levels,level){
		if(levels != undefined){
			for(var i=0;i<levels.length;i++){
				if(levels[i].code === level){
					return true;
				}
			}
		}
		return false;
	},
	
	$scope.addExperimentPropertiesInputsColumns = function(){
		var data = $scope.experiment.experimentProperties.inputs;
	
		for(var i=0; i<data.length;i++){
			if($scope.getLevel( data[i].levels, "ContainerIn")){		
				if(data[i].choiceInList){
					var possibleValues = $scope.possibleValuesToMap(data[i].possibleValues);
				}
				$scope.datatable.addColumn(2,$scope.datatable.newColumn(data[i].name,"inputExperimentProperties."+data[i].code+".value",true, true,true,"String",data[i].choiceInList,possibleValues,{"0":"Inputs","1":"Experiments"}));
			}
		}
	},
	
	$scope.addExperimentPropertiesOutputsColumns = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		
		for (var j=1; j<$scope.getBasket().get().length+1; j++) {
			$scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUsed.experimentProperties = {};
		}

		for(var i=0; i<data.length;i++){
			if($scope.getLevel( data[i].levels, "ContainerOut")){
				if(data[i].choiceInList){
					var possibleValues = $scope.possibleValuesToSelect(data[i].possibleValues);
				}
				
				$scope.datatable.addColumn(-1,$scope.datatable.newColumn(data[i].name,"outputExperimentProperties."+data[i].code+".value",false, true,true,"String",data[i].choiceInList,possibleValues,{"0":"Outputs","1":"Experiments"}));
			}
		}
	},
	
	
	$scope.getInstruments = function(){
		if($scope.experiment.instrumentInformation.instrumentUsedTypes.selected === null){
			$scope.experiment.instrumentProperties.inputs = [];
		}
		
		for(var i=0;i<$scope.datatable.config.columns.length;i++){
			if($scope.datatable.config.columns[i].extraHeaders != undefined && $scope.datatable.config.columns[i].extraHeaders[1] == "Instruments"){
				$scope.datatable.deleteColumn(i);
				i--;
			}
		}
		
		$scope.experiment.value.instrumentProperties = {};
		
		for(var i=0;i< $scope.getBasket().get().length;i++){
			if($scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne"){
				$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.instrumentProperties = {};
			}else{
				$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = {};
			}	
		}
		if($scope.experiment.instrumentInformation.instrumentUsedTypes.selected != null ){
			$scope.experiment.instrumentInformation.instruments.options = $scope.comboLists.getInstruments($scope.experiment.instrumentInformation.instrumentUsedTypes.selected.code).query();
			$scope.getInstrumentProperties($scope.experiment.instrumentInformation.instrumentUsedTypes.selected.code);
		}
	};
	
	$scope.newQc = function(){
		$window.location.href = "/experiments/newqc/home";
	};
	
	$scope.newPurif = function(){
		$window.location.href = "/experiments/newp/home";
	};
	
	$scope.saveAll = function(){		
		$scope.experiment.experimentInformation.save();
		
		$scope.experiment.experimentProperties.save();
		
		$scope.experiment.instrumentProperties.save();
		
		$scope.experiment.instrumentInformation.save();
		
		$scope.saveContainers();
	};
	
	$scope.changeState = function(){
		$scope.clearMessages();
		if(($scope.experiment.value.stateCode == "IP" && $scope.state == "N") || ($scope.experiment.value.stateCode == "F" && $scope.state == "IP")){
		$http.post(jsRoutes.controllers.experiments.api.Experiments.updateStateCode().url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess')
				$scope.experiment.value = data;
				$scope.state=$scope.experiment.value.stateCode;
				if($scope.experiment.value.stateCode == "F"){
					//ajout des colonnes de selection de resolution pour chaque tube
					$scope.datatable.addColumn(-1,$scope.datatable.newColumn("Resolution","resolutionCode",true, true,true,"String",true,$scope.ouputContainersResolutions,{"0":"Outputs","1":"Resolution"}));
				}else if($scope.experiment.value.stateCode == "IP"){
					$scope.datatable.addColumn(2,$scope.datatable.newColumn("Resolution","resolutionCode",true, true,true,"String",true,$scope.inputContainersResolutions,{"0":"Inputs","1":"Resolution"}));
				}
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-error";
			$scope.message.text = Messages('experiments.msg.save.error');
			$scope.experiment.value.stateCode = $scope.state;
			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	}else{
		$scope.experiment.value.stateCode = $scope.state;
	}
	};
	
	$scope.init = function(experimentType){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.basket = $scope.getBasket();
		$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
		$scope.comboLists = comboLists;
		$scope.form = $scope.getForm();
		$scope.state=$scope.experiment.value.stateCode;
		if(angular.isUndefined($scope.getForm().experiment)) {
			$scope.form.experiment = $scope.experiment;
			$scope.setForm($scope.form);
		
		
			$scope.experiment.value.typeCode = $scope.form.experimentTypes.selected.code;
			
			var basketList = $scope.getBasket().get();
			
			$scope.experiment.value.atomicTransfertMethods = {};
			
			//Initialisation of the experiment
			if($scope.experiment.value.code === ""){
				$http.post(jsRoutes.controllers.experiments.api.Experiments.create().url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					$scope.clearMessages();
					if(data!=null){
						$scope.experiment.value = data;
						$scope.experiment.value.categoryCode = experimentType.category.code;
						
						for(var i=0;i<basketList.length;i++){
							$scope.experiment.value.atomicTransfertMethods[i] = {class:experimentType.atomicTransfertMethod, inputContainerUsed:[]};
							
							if($scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne"){
								$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.push({containerCode:basketList[i].code,instrumentProperties:{},experimentProperties:{}});
							}else{
								$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed = {containerCode:basketList[i].code,instrumentProperties:{},experimentProperties:{}};
							}
						}
					}
					
					angular.element(document).ready(function() {
						for(i=0; i<$scope.experiment.experimentProperties.inputs.length;i++){
							var getter = $parse("experiment.value.experimentProperties."+$scope.experiment.experimentProperties.inputs[i].code+".value");
							getter.assign($scope,"");
						}
						
						$scope.addExperimentPropertiesInputsColumns();
					});
				})
				.error(function(data, status, headers, config) {
					alert("error");
				});
			}
			
			angular.element(document).ready(function() {
				$scope.experiment.experimentTypeCode = $scope.getForm().experimentTypes.selected.code;
				$scope.experiment.instrumentInformation.instrumentUsedTypes.options = $scope.comboLists.getInstrumentUsedTypes($scope.experiment.value.typeCode).query();
				$scope.experiment.experimentInformation.protocols.options = $scope.comboLists.getProtocols($scope.experiment.value.typeCode).query();
				$scope.experiment.experimentInformation.resolutions.options = $scope.comboLists.getResolution().query();
			});
			
		} else {
			$scope.experiment = $scope.form.experiment;
		}
	}	
}
CreateNewCtrl.$inject = ['$scope', '$window','datatable','$http','comboLists','$parse'];
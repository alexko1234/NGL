function CreateNewCtrl($scope,$window, datatable, $http,lists,$parse,$q,$position) {
	$scope.experiment = {
			outputGenerated:false,
			outputVoid:false,
			doPurif:false,
			doQc:false,
			value: {
				code:"",
				typeCode:"",
				resolutionCodes:[],
				protocolCode:"",
				stateCode:"N",
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


	$scope.datatableConfig = {
			columnsUrl : jsRoutes.controllers.experiments.tpl.Experiments.getEditExperimentColumns().url,
			compact:false,
			pagination:{
				active:false
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
				mode:'local',
			},
			hide:{
				active:true
			},
			edit:{
				active:true,
				columnMode:true
			},
			messages:{
				active:true,
				columnMode:true
			},
			extraHeaders:{
				number:2,
				dynamic:true,
			},
			otherButton:{
				active:true,
				template:'<button class="btn btn btn-info" ng-click="newPurif()" data-toggle="tooltip" ng-disabled="experiment.value.stateCode != \'F\'" ng-hide="!experiment.doPurif" title="'+Messages("experiments.addpurif")+'">Messages("experiments.addpurif")</button><button class="btn btn btn-info" ng-click="newQc()" data-toggle="tooltip" ng-disabled="experiment.value.stateCode != \'F\'" ng-hide="!experiment.doQc" title="Messages("experiments.addqc")">Messages("experiments.addqc")</button>'
			}
	};

	//Temporairement en dur (� mettre dans la base)
	$scope.inputContainersStates = [{"code":"IS","name":"IS"},{"code":"UA","name":"UA"}];
	$scope.ouputContainersStates = [{"code":"IW-P","name":"IW-P"},{"code":"A","name":"A"},{"code":"UA","name":"UA"}];

	$scope.experiment.experimentInformation = {
			protocols:{},
			resolutions:{},
			enabled:true,
			toggleEdit:function(){
				this.enabled = !this.enabled;
			},
			save:function(){
				if(this.enabled){
					if($scope.experiment.value._id){
						$scope.clearMessages();
	
						//copy resoltion to atomicTransfere
						for(var i=0;i<$scope.datatable.displayResult.length &&  $scope.datatable.displayResult[i].inputResolutionCodes != null;i++){
							if($scope.experiment.value.atomicTransfertMethods[i] != undefined&& $scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne"){
								for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length;j++){
									$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].resolutionCodes = $scope.datatable.displayResult[i].inputResolutionCodes;
									$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].stateCode = $scope.datatable.displayResult[i].inputStateCode;
								}
								i += j;
							}else{
								$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.resolutionCodes =  $scope.datatable.displayResult[i].inputResolutionCodes;
								$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.stateCode =  $scope.datatable.displayResult[i].inputStateCode;
								
							}
						}
	
						for(var i=0;i<$scope.datatable.displayResult.length &&  $scope.datatable.displayResult[i].outputResolutionCodes != null;i++){
							if($scope.experiment.value.atomicTransfertMethods[i] != undefined && $scope.experiment.value.atomicTransfertMethods[i].class != "ManyToOne"){
								for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.length;j++){
									$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].resolutionCodes = $scope.datatable.displayResult[i].outputResolutionCodes;
									$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].stateCode = $scope.datatable.displayResult[i].inputStateCode;
									
								}
								i += j;
							}else if($scope.experiment.value.atomicTransfertMethods[0].class != "OneToVoid"){
								if($scope.experiment.value.atomicTransfertMethods[0].class != "ManyToOne"){
									$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.resolutionCodes =  $scope.datatable.displayResult[i].outputResolutionCodes;
									$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.stateCode =  $scope.datatable.displayResult[i].outputStateCode;
								}else{
									$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.resolutionCodes =  $scope.datatable.displayResult[i].outputResolutionCodes;
									$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.stateCode =  $scope.datatable.displayResult[i].outputStateCode;
								}
								
							}
						}
	
						$http.put(jsRoutes.controllers.experiments.api.Experiments.updateExperimentInformations($scope.experiment.value.code).url, $scope.experiment.value)
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
					}else{
						$scope.save();
					}
				}
			}
	};


	$scope.saveContainers = function(){
		$scope.clearMessages();
		$http.put(jsRoutes.controllers.experiments.api.Experiments.updateContainers($scope.experiment.value.code).url, $scope.experiment.value)
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

	};

	$scope.clearMessages  = function(){
		$scope.message = {clazz : undefined, text : undefined, showDetails : false, isDetails : false, details : []};
	}

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
			}	
	};

	$scope.experimentToDatatable = function(){
		for(var i=0;i<$scope.datatable.displayResult.length;i++){
			if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
				for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
					$scope.datatable.displayResult[i].inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed[j].experimentProperties;
					i++;
				}
				if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
					$scope.datatable.displayResult[i].outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.experimentProperties;					
				}
			}else{
				$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].inputExperimentProperties;
				if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToMany"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
						for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
							$scope.datatable.displayResult[i].outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].experimentProperties;
							i++;
						}
					}
				}else if($scope.experiment.value.atomicTransfertMethods[i].class != "OneToVoid"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
						$scope.datatable.displayResult[i].outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties;					
					}
				}
			}
		}
	};

	$scope.instrumentToDatatable = function(){
		for(var i=0;i<$scope.datatable.displayResult.length;i++){
			if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
				for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
					$scope.datatable.displayResult[i].inputinstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].instrumentProperties;
					i++;
				}
				if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
					$scope.datatable.displayResult[i].outputinstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.instrumentProperties;					
				}
			}else{
				$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].inputinstrumentProperties;
				if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToMany"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
						for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
							$scope.datatable.displayResult[i].outputinstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].instrumentProperties;
							i++;
						}
					}
				}else if($scope.experiment.value.atomicTransfertMethods[i].class != "OneToVoid"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
						$scope.datatable.displayResult[i].outputinstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties;					
					}
				}
			}
		}
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
	
	$scope.experiment.experimentProperties = {
			enabled:true,
			toggleEdit:function(){
				this.enabled = !this.enabled;
			},
			save:function(){
				if(this.enabled){	
					$scope.clearMessages();
					for(var i=0;i<$scope.datatable.displayResult.length;i++){
						if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
							for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
								$scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].experimentProperties = $scope.datatable.displayResult[i].inputExperimentProperties;
								i++;
							}
							if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
								$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].outputExperimentProperties;					
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
							}else if($scope.experiment.value.atomicTransfertMethods[i].class != "OneToVoid"){
								if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
									$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].outputExperimentProperties;					
								}
							}
						}
					}
					$http.put(jsRoutes.controllers.experiments.api.Experiments.updateExperimentProperties($scope.experiment.value.code).url, $scope.experiment.value)
					.success(function(data, status, headers, config) {
						if(data!=null){
							$scope.message.clazz="alert alert-success";
							$scope.message.text=Messages('experiments.msg.save.sucess')
							$scope.experiment.value = data;
							$scope.experimentToDatatable();
						}
					})
					.error(function(data, status, headers, config) {
						$scope.message.clazz = "alert alert-danger";
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
				if($scope.experiment.value._id){
				if(this.enabled && $scope.experiment.value.instrument.code){
					$scope.clearMessages();
					for(var i=0;i<$scope.datatable.displayResult.length;i++){
						if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
							for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
								$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].instrumentProperties = $scope.datatable.displayResult[i].inputInstrumentProperties;
								i++;
							}
							if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
								$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].outputInstrumentProperties;
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
							}else if($scope.experiment.value.atomicTransfertMethods[i].class != "OneToVoid"){
								if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
									$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].outputInstrumentProperties;					
								}
							}
						}
					}
					$http.put(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentProperties($scope.experiment.value.code).url, $scope.experiment.value)
					.success(function(data, status, headers, config) {
						if(data!=null){
							$scope.message.clazz="alert alert-success";
							$scope.message.text=Messages('experiments.msg.save.sucess')
							$scope.experiment.value = data;
							$scope.instrumentToDatatable();
						}
					})
					.error(function(data, status, headers, config) {
						$scope.message.clazz = "alert alert-danger";
						$scope.message.text = Messages('experiments.msg.save.error');

						$scope.message.details = data;
						$scope.message.isDetails = true;
						alert("error");
					});
			}
				}else{
				$scope.save();
			}
			}
	};


	$scope.save = function(){
		$http.post(jsRoutes.controllers.experiments.api.Experiments.save($scope.experiment.value.code).url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess')
				$scope.experiment.value = data;
				$scope.saveAll();
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
	
	$scope.setExperimentProperties = function(experimentProperties){
		$scope.experiment.experimentProperties.inputs = experimentProperties;
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

	$scope.outputGeneration = function(){
			if($scope.experiment.value.atomicTransfertMethods[0].class != "ManyToOne"){
				for (var j=1; j<$scope.datatable.getData().length+1; j++) {
					if($scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUsed != null || $scope.experiment.value.atomicTransfertMethods[(j-1)].class == "OneToVoid"){
						$scope.experiment.outputGenerated = true;
					}
				}
			}else{
				$scope.experiment.outputGenerated = ($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed != null)
			}
			
			if(!$scope.experiment.outputGenerated){
				$http.put(jsRoutes.controllers.experiments.api.Experiments.generateOutput($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.clearMessages();
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess');
						$scope.experiment.value = data;
						for(var i=0;i<$scope.datatable.getData().length;i++){
							if($scope.experiment.value.atomicTransfertMethods[i] != undefined && $scope.experiment.value.atomicTransfertMethods[i].class == "OneToOne"){
								$scope.datatable.displayResult[i].outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed;
							}else if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
								$scope.datatable.displayResult[i].outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed;
								//In process -  gestion du rowspan
								//example
								//$scope.datatable.config.columns[0].cells[0] = {"rowSpan":2};
								/*for(var j=0;j<$scope.datatable.config.columns.length;j++){
									if($scope.datatable.config.columns[j].extraHeaders[0] == "Outputs"){
										$scope.datatable.config.columns[j].cells[i] = {"rowSpan":$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length};
									}
								}*/
							}
						}
						$scope.datatable.addColumn(-1,$scope.datatable.newColumn("Code","outputContainerUsed.containerCode",false, true,true,"String",false,undefined,{"0":"Outputs"}));
						//$scope.datatable.addColumn(-1,$scope.datatable.newColumn("Support Code","outputContainerUsed.support.categoryCode",false, true,true,"String",false,undefined,{"0":"Outputs"}));
						
						$scope.addExperimentPropertiesOutputsColumns();
						$scope.addInstrumentPropertiesOutputsColumns();
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
						var possibleValues = $scope.possibleValuesToSelect(data[i].possibleValues);
					}
					$scope.datatable.addColumn(2,$scope.datatable.newColumn(data[i].name,"inputInstrumentProperties."+data[i].code+".value",true, true,true,"String",data[i].choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"}));
				}
			}
			
			for(var i=0;i<$scope.datatable.getData().length;i++){
				
				for(var j=0; j<data.length;j++){
					
					if($scope.getLevel( data[j].levels, "ContainerIn")){
						var getter = $parse("datatable.displayResult["+i+"].inputInstrumentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties[data[j].code].value);
						}else{
							getter.assign($scope,"");
						}
						//$scope.datatable.displayResult[i].outputInstrumentProperties[data[i].code].value = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[i].code].value;
					}
				}
			}		
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
	},


	$scope.addInstrumentPropertiesOutputsColumns = function(){
		var data = $scope.experiment.instrumentProperties.inputs;
		var outputGenerated = false;
		
		var j = 1;
		while($scope.experiment.value.atomicTransfertMethods[(j-1)] != null){
			if($scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUsed == null){
				$scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUsed.instrumentProperties = {};
			}
			else{
				outputGenerated = true;
			}
			j++;
		}
			for(var i=0; i<data.length;i++){
				if($scope.getLevel( data[i].levels,"ContainerOut")){	 					
					if(data[i].choiceInList){
						var possibleValues = $scope.possibleValuesToSelect(data[i].possibleValues);
					}
	
					$scope.datatable.addColumn(-1,$scope.datatable.newColumn(data[i].name,"outputInstrumentProperties."+data[i].code+".value",true, true,true,"String",data[i].choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"}));
				}
			}
			
			if(outputGenerated){
				for(var i=0;i<$scope.datatable.getData().length;i++){
					
					for(var j=0; j<data.length;j++){
						if($scope.getLevel( data[j].levels, "ContainerOut")){
							var getter = $parse("datatable.displayResult["+i+"].outputInstrumentProperties."+data[j].code+".value");
							if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[j].code]){
								getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[j].code].value);
							}else{
								getter.assign($scope,"");
							}
							//$scope.datatable.displayResult[i].outputInstrumentProperties[data[i].code].value = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[i].code].value;
						}
					}
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

		for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerIn")){
					var getter = $parse("datatable.displayResult["+i+"].inputExperimentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties[data[j].code].value);
					}else{
						getter.assign($scope,"");
					}
					//$scope.datatable.displayResult[i].outputInstrumentProperties[data[i].code].value = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[i].code].value;
				}
			}
		}		
	},

	$scope.addExperimentPropertiesOutputsColumns = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		var outputGenerated = false;
		var j = 1;
		while($scope.experiment.value.atomicTransfertMethods[(j-1)] != null){
			if($scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUsed == null){
				$scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUsed.experimentProperties = {};
			}else{
				outputGenerated = true;
			}
			j++;
		}
		
			for(var i=0; i<data.length;i++){
				if($scope.getLevel( data[i].levels, "ContainerOut")){
					if(data[i].choiceInList){
						var possibleValues = $scope.possibleValuesToSelect(data[i].possibleValues);
					}
	
					$scope.datatable.addColumn(-1,$scope.datatable.newColumn(data[i].name,"outputExperimentProperties."+data[i].code+".value",true, true,true,"String",data[i].choiceInList,possibleValues,{"0":"Outputs","1":"Experiments"}));
				}
			}
			if(outputGenerated){
				for(var i=0;i<$scope.datatable.getData().length;i++){
					
					for(var j=0; j<data.length;j++){
						if($scope.getLevel( data[j].levels, "ContainerOut")){
							var getter = $parse("datatable.displayResult["+i+"].outputExperimentProperties."+data[j].code+".value");
							if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code]){
								getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code].value);
							}else{
								getter.assign($scope,"");
							}
							//$scope.datatable.displayResult[i].outputExperimentProperties[data[i].code].value = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[i].code].value;
						}
					}
				}
			}
	},


	$scope.getInstruments = function(loaded){
		if(!loaded){
			var loaded = false;
		}
		if($scope.experiment.value.instrumentUsedTypeCode === null){
			$scope.experiment.instrumentProperties.inputs = [];
			$scope.experiment.instrumentInformation.instrumentCategorys.inputs = [];
		}

		for(var i=0;i<$scope.datatable.config.columns.length;i++){
			if($scope.datatable.config.columns[i].extraHeaders != undefined && $scope.datatable.config.columns[i].extraHeaders[1] == "Instruments"){
				$scope.datatable.deleteColumn(i);
				i--;
			}
		}
		if(loaded == false){
			$scope.experiment.value.instrumentProperties = {};
			if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
				$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.instrumentProperties = {}
			}else{
				for(var i=0;i< $scope.datatable.getData().length;i++){
						$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = {};
				}
			}
		}
		
		if($scope.experiment.value.instrumentUsedTypeCode != null ){
			
			
			$scope.lists.refresh.instruments({"typeCode":$scope.experiment.value.instrumentUsedTypeCode});
			$scope.lists.refresh.containerSupportCategories({"instrumentUsedTypeCode":$scope.experiment.value.instrumentUsedTypeCode});
			
			//$scope.experiment.instrumentInformation.instruments.options = $scope.comboLists.getInstruments($scope.experiment.value.instrument.categoryCode).query();
			//$scope.experiment.instrumentInformation.instrumentCategorys.options =  $scope.comboLists.getCategoryCodes($scope.experiment.value.instrument.categoryCode).query();
			$scope.getInstrumentProperties($scope.experiment.value.instrumentUsedTypeCode,loaded);
		}
	};

	$scope.newQc = function(){
		$window.location.href = "/experiments/newqc/home";
	};

	$scope.newPurif = function(){
		$window.location.href = "/experiments/newp/home";
	};

	$scope.saveAll = function(){		
		
		if($scope.experiment.value._id){
			$scope.experiment.experimentInformation.save();
	
			$scope.experiment.experimentProperties.save();
	
			$scope.experiment.instrumentProperties.save();
	
			$scope.experiment.instrumentInformation.save();
	
			$scope.datatable.save();
	
			$scope.saveContainers();
		}else{
			$scope.save();
		}

	};

	$scope.changeState = function(state){
		$scope.clearMessages();
		$http.put(jsRoutes.controllers.experiments.api.Experiments.updateStateCode($scope.experiment.value.code,state).url)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess')
				//$scope.experiment.value = data;
				$scope.state=state;
				$scope.experiment.value.stateCode = state;
				if($scope.experiment.value.stateCode == "F"){
					//ajout des colonnes de selection de resolution pour chaque tube
					var col = $scope.datatable.newColumn("State","outputStateCode",true, true,true,"String",true,$scope.ouputContainersStates,{"0":"Outputs","1":"States"});
					
					$scope.datatable.addColumn(-1, col);
					var col = $scope.datatable.newColumn("Resolution","outputResolutionCodes",true, true,true,"String",true,$scope.lists.get('resolutions'),{"0":"Output","1":"Resolutions"});
					col.listStyle = "bt-select-multiple";
					$scope.datatable.addColumn(-1, col);
				}else if($scope.experiment.value.stateCode == "IP"){
					var col = $scope.datatable.newColumn("State","inputStateCode",true, true,true,"String",true,$scope.inputContainersStates,{"0":"Inputs","1":"State"});
					
					$scope.datatable.addColumn(2, col);
					var col = $scope.datatable.newColumn("Resolution","inputResolutionCodes",true, true,true,"String",true,$scope.lists.get('resolutions'),{"0":"Inputs","1":"Resolutions"});
					col.listStyle = "bt-select-multiple";
					$scope.datatable.addColumn(2, col);
				}
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');
			$scope.experiment.value.stateCode = $scope.state;
			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
		
	};

	$scope.init = function(experimentCategoryCode, atomicTransfertMethod,experiment){
		experimentType =  {};
		experimentType.category= {};
		experimentType.category.code = experimentCategoryCode;
		experimentType.atomicTransfertMethod = atomicTransfertMethod;
		if(experiment != ""){
			experiment =  JSON.parse(experiment);
		}
		
		$scope.lists = lists;
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		if(experiment == ""){
			$scope.basket = $scope.getBasket().get();
			var containers = [];//container list for the datatable
			var promises = [];//promise for loading everithing after the data was set to datatable
			for (var i=0;i<$scope.basket.length;i++) {
				var promise = $http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{supportCode:$scope.basket[i].code}})
				.success(function(data, status, headers, config) {
					$scope.clearMessages();
						if(data!=null){
							for(var j=0;j<data.length;j++){
								containers.push(data[j]);
							}
						}
					})
				.error(function(data, status, headers, config) {
					alert("error");
				});
				promises.push(promise);
			}
			$q.all(promises).then(function (res) {
				
			$scope.datatable.setData(containers,containers.length);
			$scope.form = $scope.getForm();
			$scope.state=$scope.experiment.value.stateCode;
			 $http.get(jsRoutes.controllers.experiments.api.ExperimentTypeNodes.list().url,{params:{code: $scope.form.experimentType.code}})
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
			if(angular.isUndefined($scope.getForm().experiment)) {
				$scope.form.experiment = $scope.experiment;
				$scope.setForm($scope.form);
				$scope.experiment.value.typeCode = $scope.form.experimentType.code;
				
				
				$scope.experiment.value.categoryCode = experimentType.category.code;
				$scope.experiment.value.atomicTransfertMethods = {};
	
				//Initialisation of the experiment
				if($scope.experiment.value.code === ""){
					$http.post(jsRoutes.controllers.experiments.api.Experiments.create().url, $scope.experiment.value)
					.success(function(data, status, headers, config) {
						$scope.clearMessages();
						if(data!=null){
							$scope.experiment.value = data;
							if(experimentType.atomicTransfertMethod == "ManyToOne"){
								$scope.experiment.value.atomicTransfertMethods[0] = {class:experimentType.atomicTransfertMethod, inputContainerUseds:[]};
								for(var i=0;i<containers.length;i++){
									$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.push({containerCode:containers[i].code,instrumentProperties:{},experimentProperties:{}});
								}
							}else{
								for(var i=0;i<containers.length;i++){
										$scope.experiment.value.atomicTransfertMethods[i] = {class:experimentType.atomicTransfertMethod, inputContainerUsed:[]};
										$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed = {containerCode:containers[i].code,instrumentProperties:{},experimentProperties:{}};
									
									if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToVoid"){
										$scope.experiment.outputVoid = true;
									}
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
					$scope.experiment.experimentTypeCode = $scope.getForm().experimentType.code;
					
					$scope.lists.refresh.instrumentUsedTypes({"experimentTypeCode":$scope.experiment.value.typeCode});
					$scope.lists.refresh.protocols({"experimentTypeCode":$scope.experiment.value.typeCode});
					$scope.lists.refresh.resolutions({"objectTypeCode":"Experiment"});
				});
	
			} else {
				$scope.experiment = $scope.form.experiment;
				$scope.addExperimentPropertiesInputsColumns();
			}
			});
			}else{
				$scope.experiment.value = experiment;
				$scope.state=$scope.experiment.value.stateCode;

				$scope.lists.refresh.instrumentUsedTypes({"experimentTypeCode":$scope.experiment.value.typeCode});
				$scope.lists.refresh.protocols({"experimentTypeCode":$scope.experiment.value.typeCode});
				$scope.lists.refresh.resolutions({"objectTypeCode":"Experiment"});

				var i=0;
				var containers = [];//container list for the datatable
				var promises = [];//promise for loading everithing after the data was set to datatable
				
				while($scope.experiment.value.atomicTransfertMethods[i] != null){
					if($scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne"){
						for(var j=0;j<$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length;j++){
							var promise = $http.get(jsRoutes.controllers.containers.api.Containers.get($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].containerCode).url)
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
							
							
						}
					}else{
						var promise = $http.get(jsRoutes.controllers.containers.api.Containers.get($scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.containerCode).url)
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
					}
					
					
					i++;
				}
				
				$q.all(promises).then(function (res) {
					$scope.datatable.setData(containers,containers.length);
					$scope.addExperimentPropertiesInputsColumns();
					$scope.getInstruments(true);
					var outputGenerated = false;
					var i = 0;
					for(i=0;i<$scope.datatable.getData().length;i++){
						if($scope.experiment.value.atomicTransfertMethods[i] != undefined && $scope.experiment.value.atomicTransfertMethods[i].class == "OneToOne"){
							$scope.datatable.displayResult[i].outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed;
							if($scope.datatable.displayResult[i].outputContainerUsed != undefined){
								$scope.datatable.displayResult[i].outputResolutionCodes = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.resolutionCodes;
								$scope.datatable.displayResult[i].outputStateCode = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.stateCode;
							}
							$scope.datatable.displayResult[i].inputResolutionCodes = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.resolutionCodes;
							$scope.datatable.displayResult[i].inputStateCode = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.stateCode;
						}else if($scope.experiment.value.atomicTransfertMethods[i] != undefined && $scope.experiment.value.atomicTransfertMethods[i].class == "OneToMany"){
							$scope.datatable.displayResult[i].outputContainerUseds = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds;
							if($scope.datatable.displayResult[i].outputContainerUseds != undefined){
								for(var j=0;j<$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.length;j++){
									$scope.datatable.displayResult[j].outputResolutionCodes = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].resolutionCodes;
									$scope.datatable.displayResult[j].outputStateCode = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].stateCode;
								}
							}
							$scope.datatable.displayResult[i].inputResolutionCodes = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.resolutionCodes;
							$scope.datatable.displayResult[i].inputStateCode = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.stateCode;
						}else if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
							$scope.datatable.displayResult[i].outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed;
							if($scope.datatable.displayResult[i].outputContainerUsed != undefined){
								$scope.datatable.displayResult[i].outputResolutionCodes = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.resolutionCodes;
								$scope.datatable.displayResult[i].outputStateCode = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.stateCode;
							}

							for(var j=0;j<$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.length;j++){
								$scope.datatable.displayResult[j].inputResolutionCodes = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].resolutionCodes;
								$scope.datatable.displayResult[j].inputStateCode = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].stateCode;
							}
						}else if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToVoid"){
							$scope.experiment.outputVoid = true;
							$scope.datatable.displayResult[i].inputResolutionCodes = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.resolutionCodes;
						}
						
						if(($scope.experiment.value.atomicTransfertMethods[i] != undefined && $scope.experiment.value.atomicTransfertMethods[i].class == "OneToMany" && $scope.datatable.displayResult[i].outputContainerUseds.length > 0) || $scope.experiment.value.atomicTransfertMethods[i] != undefined && (($scope.experiment.value.atomicTransfertMethods[i].class == "OneToOne" || $scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne") && $scope.datatable.displayResult[i].outputContainerUsed)){
							$scope.experiment.outputGenerated = true;
						}
					}
					
					
					if($scope.experiment.outputGenerated){
						$scope.datatable.addColumn(-1,$scope.datatable.newColumn("Code","outputContainerUsed.containerCode",false, true,true,"String",false,undefined,{"0":"Outputs"}));
						$scope.datatable.addColumn(-1,$scope.datatable.newColumn("SupportCode","outputContainerUsed.support.categoryCode",false, true,true,"String",false,undefined,{"0":"Outputs"}));
						$scope.addExperimentPropertiesOutputsColumns();
						$scope.addInstrumentPropertiesOutputsColumns();
					}
					
					if($scope.experiment.value.stateCode == "F"){
						//ajout des colonnes de selection de resolution pour chaque tube
						if($scope.experiment.value.atomicTransfertMethods[0].class != "OneToVoid"){
							var col = $scope.datatable.newColumn("State","outputStateCode",true, true,true,"String",true,$scope.ouputContainersStates,{"0":"Outputs","1":"States"});
							
							$scope.datatable.addColumn(-1, col);
							var col = $scope.datatable.newColumn("Resolution","outputResolutionCodes",true, true,true,"String",true,$scope.lists.get('resolutions'),{"0":"Output","1":"Resolutions"});
							col.listStyle = "bt-select-multiple";
							$scope.datatable.addColumn(-1, col);
						}
						var col = $scope.datatable.newColumn("State","inputStateCode",true, true,true,"String",true,$scope.inputContainersStates,{"0":"Inputs","1":"State"});
						
						$scope.datatable.addColumn(2, col);
						var col = $scope.datatable.newColumn("Resolution","inputResolutionCodes",true, true,true,"String",true,$scope.lists.get('resolutions'),{"0":"Inputs","1":"Resolutions"});
						col.listStyle = "bt-select-multiple";
						$scope.datatable.addColumn(2, col);
					}else if($scope.experiment.value.stateCode == "IP"){
						var col = $scope.datatable.newColumn("State","inputStateCode",true, true,true,"String",true,$scope.inputContainersStates,{"0":"Inputs","1":"State"});
					
						$scope.datatable.addColumn(2, col);
						var col = $scope.datatable.newColumn("Resolution","inputResolutionCodes",true, true,true,"String",true,$scope.lists.get('resolutions'),{"0":"Inputs","1":"Resolutions"});
						col.listStyle = "bt-select-multiple";
						$scope.datatable.addColumn(2, col);
				}
				});
			}
		}
}
CreateNewCtrl.$inject = ['$scope', '$window','datatable','$http','lists','$parse','$q','$position'];
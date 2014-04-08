function CreateNewCtrl($scope,$window, datatable, $http,lists,$parse,$q,$position) {
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
				code:""
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
				template:'<button class="btn btn btn-info" ng-click="newPurif()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doPurif" title="'+Messages("experiments.addpurif")+'">Messages("experiments.addpurif")</button><button class="btn btn btn-info" ng-click="newQc()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doQc" title="Messages("experiments.addqc")">Messages("experiments.addqc")</button>'
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
	
	$scope.experimentToDatatable = function(){
		for(var i=0;i<$scope.datatable.displayResult.length;i++){
			if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
				for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
					$scope.datatable.displayResult[i].data.inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed[j].experimentProperties;
					i++;
				}
				if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
					$scope.datatable.displayResult[i].data.outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed;
					$scope.datatable.displayResult[i].data.outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.experimentProperties;					
				}
			}else{
				$scope.datatable.displayResult[i].data.inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties;
				if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToMany"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
						$scope.datatable.displayResult[i].data.outputContainerUseds = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds;
						for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
							$scope.datatable.displayResult[i].data.outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].experimentProperties;
							i++;
						}
					}
				}else if($scope.experiment.value.atomicTransfertMethods[i].class != "OneToVoid"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
						$scope.datatable.displayResult[i].data.outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed;
						$scope.datatable.displayResult[i].data.outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties;					
					}
				}
			}
		}
	};

	$scope.instrumentToDatatable = function(){
		for(var i=0;i<$scope.datatable.displayResult.length;i++){
			if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
				for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
					$scope.datatable.displayResult[i].data.inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].instrumentProperties;
					i++;
				}
				if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
					$scope.datatable.displayResult[i].data.outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.instrumentProperties;					
				}
			}else{
				$scope.datatable.displayResult[i].data.inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties;
				if($scope.experiment.value.atomicTransfertMethods[i].class == "OneToMany"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
						for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
							$scope.datatable.displayResult[i].data.outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].instrumentProperties;
							i++;
						}
					}
				}else if($scope.experiment.value.atomicTransfertMethods[i].class != "OneToVoid"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
						$scope.datatable.displayResult[i].data.outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties;					
					}
				}
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
						if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
							for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
								$scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].experimentProperties = $scope.datatable.displayResult[i].data.inputExperimentProperties;
								i++;
							}
							if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
								$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].outputExperimentProperties;					
							}
						}else{
							$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].data.inputExperimentProperties;
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
							//$scope.message.clazz="alert alert-success";
							//$scope.message.text=Messages('experiments.msg.save.sucess')
							$scope.experiment.value = data;
							$scope.experimentToDatatable();
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
	
	$scope.addExperimentPropertiesInputsColumns = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		if(data != undefined){
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
					}
				}
			}		
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
				if(this.enabled && $scope.experiment.value.instrument.code){
					$scope.clearMessages();
					for(var i=0;i<$scope.datatable.displayResult.length;i++){
						if($scope.experiment.value.atomicTransfertMethods[0].class == "ManyToOne"){
							for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
								$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].instrumentProperties = $scope.datatable.displayResult[i].data.inputInstrumentProperties;
								i++;
							}
							if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
								$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].outputInstrumentProperties;
							}
						}else{
							$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].data.inputInstrumentProperties;
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
							//$scope.message.clazz="alert alert-success";
							//$scope.message.text=Messages('experiments.msg.save.sucess')
							$scope.experiment.value = data;
							$scope.instrumentToDatatable();
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
		if($scope.experiment.value._id != undefined){
			promises.push($scope.experiment.experimentInformation.save());
	
			promises.push($scope.experiment.experimentProperties.save());
	
			promises.push($scope.experiment.instrumentProperties.save());
	
			promises.push($scope.experiment.instrumentInformation.save());
	
			promises.push($scope.datatable.save());
	
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
			$scope.clearMessages();
			var promise = $http.put(jsRoutes.controllers.experiments.api.Experiments.nextState($scope.experiment.value.code).url)
			.success(function(data, status, headers, config) {
				if(data!=null){
					$scope.message.clazz="alert alert-success";
					$scope.message.text=Messages('experiments.msg.save.sucess')
					$scope.experiment.value = data;
					if(!$scope.experiment.outputGenerated && $scope.isOutputGenerated()){
						$scope.datatable.addColumn(-1,$scope.datatable.newColumn("Code","outputContainerUsed.code",false, true,true,"String",false,undefined,{"0":"Outputs"}));
						//$scope.datatable.addColumn(-1,$scope.datatable.newColumn("SupportCode","outputContainerUsed.support.code",false, true,true,"String",false,undefined,{"0":"Outputs"}));
						$scope.addExperimentPropertiesOutputsColumns();
						$scope.addInstrumentPropertiesOutputsColumns();
						$scope.experimentToDatatable();
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
						}
					}
				}
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
					}
				}
			}
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
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.lists.refresh.instrumentUsedTypes({"experimentTypeCode":$scope.experiment.value.typeCode});
		$scope.lists.refresh.protocols({"experimentTypeCode":$scope.experiment.value.typeCode});
		$scope.lists.refresh.resolutions({"objectTypeCode":"Experiment"});
		$scope.lists.refresh.states({"objectTypeCode":"Experiment"});
		
		var containers = [];//container list for the datatable
		var promises = [];//promise for loading everything after the data was set to datatable
		
		if(experiment == ""){
			$scope.basket = $scope.getBasket().get();
			for(var i=0;i<$scope.basket.length;i++) {
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
				$scope.doPurifOrQc($scope.experiment.value.typeCode);
				if(angular.isUndefined($scope.form.experiment)) {
					$scope.init_experiment(containers, $scope.experimentType.atomicTransfertMethod);
				}else {
					$scope.experiment = $scope.form.experiment;
					$scope.addExperimentPropertiesInputsColumns();
				}
			});
		}else{
			$scope.experiment.value = experiment;
			var i = 0;
			while($scope.experiment.value.atomicTransfertMethods[i] != null){
				if($scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne"){
					for(var j=0;j<$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length;j++){
						var promise = $http.get(jsRoutes.controllers.containers.api.Containers.get($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].code).url)
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
					var promise = $http.get(jsRoutes.controllers.containers.api.Containers.get($scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.code).url)
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
				$scope.experimentToDatatable();
				$scope.instrumentToDatatable();
				
				if($scope.isOutputGenerated()){
					$scope.datatable.addColumn(-1,$scope.datatable.newColumn("Code","outputContainerUsed.code",false, true,true,"String",false,undefined,{"0":"Outputs"}));
					//$scope.datatable.addColumn(-1,$scope.datatable.newColumn("SupportCode","outputContainerUsed.support.code",false, true,true,"String",false,undefined,{"0":"Outputs"}));
					$scope.addExperimentPropertiesOutputsColumns();
					$scope.addInstrumentPropertiesOutputsColumns();
					$scope.experimentToDatatable();
				}
			});
		}
	};
}
CreateNewCtrl.$inject = ['$scope', '$window','datatable','$http','lists','$parse','$q','$position'];
function CreateNewCtrl($scope, datatable, $http,comboLists,$parse) {
	
	$scope.INSTRUMENTPROPERTIES = "instrumentProperties",
	$scope.EXPERIMENTPROPERTIES = "experimentProperties",
	
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
		pagination:{
			active:false
		},		
		search:{
			active:false
		},
		order:{
			mode:'local', //or 
			active:true,
			by:'containerInputCode'
		},
		remove:{
			active:true,
			mode:'local',
			callback : function(datatable){
				$scope.basket.reset();
				$scope.basket.add(datatable.allResult);
			}
		},
		save:{
			active:true,
			withoutEdit:false,
			url:jsRoutes.controllers.experiments.api.Experiments.updateContainers($scope.experiment.value.code).url,
			callback : function(datatable){
				$scope.basket.reset();
			}
		},
		edit:{
			active:true
		},
		messages:{
			active:true
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
	
	$scope.getInstrumentProperties = function(code){
		$scope.clearMessages();
		$http.get(jsRoutes.controllers.experiments.api.Experiments.getInstrumentProperties(code).url)
		.success(function(data, status, headers, config) {
			
			$scope.experiment.instrumentProperties.inputs = data;
			
			$scope.addInstrumentPropertiesColumns();
			
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-error";
			$scope.message.text = Messages('experiments.msg.save.error');
			
			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};
	
	$scope.addInstrumentPropertiesColumns = function(){
		var data = $scope.experiment.instrumentProperties.inputs;
		for(var i=0; i<data.length;i++){
			$scope.datatable.addColumn(data[i].name,2,data[i].name+$scope.INSTRUMENTPROPERTIES);
			
			//Creation of the properties on the scope
			var getter = $parse("experiment.value.instrumentProperties."+data[i].name+".value");
			getter.assign($scope,"");
			
			$scope.addInstrumentPropertiesColumn(data,i);
		}
		
	},
	
	$scope.addExperimentPropertiesColumns = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		for(var i=0; i<data.length;i++){
			$scope.datatable.addColumn(data[i].name,2,data[i].name+$scope.EXPERIMENTPROPERTIES);
			
			//Creation of the properties on the scope
			var getter = $parse("experiment.value.experimentProperties."+data[i].name+".value");
			getter.assign($scope,"");
			
			$scope.addExperimentPropertiesColumn(data,i);
		}
		
	},
	
	$scope.addInstrumentPropertiesColumn = function(data,i){
		if(data[i].choiceInList){
			$scope.datatable.addRow(0, 2,"<div class='controls'><select class='input-small' ng-change=\"alert('ok');datatable.updateColumn('"+data[i].name+"', '"+data[i].id+"')\"> <option ng-repeat='opt in experiment.instrumentProperties.inputs["+i+"].possibleValues' value='{{opt.value}}'>{{opt.value}}</option></select></div>",data[i].name+$scope.INSTRUMENTPROPERTIES);
		}else{
			$scope.datatable.addRow(0,2, "<div class='controls'> <input type='text' class='input-small' ng-readonly='!experiment.instrumentInformation.enabled' /></div>",data[i].name+$scope.INSTRUMENTPROPERTIES);
		}
		
		for (var j=1; j<$scope.datatable.getNumberRows()-1; j++) {
			if($scope.experiment.value.atomicTransfertMethods[(j-1)].class == "ManyToOne"){
				$scope.manyToInput(data[i],i,j,"instrumentProperties");
			}else{
				$scope.oneToInput(data[i],i,j,"instrumentProperties");
			}
		}
	};
	
	$scope.addExperimentPropertiesColumn = function(data,i){
		if(data[i].choiceInList){
			$scope.datatable.addRow(0, 2,"<div class='controls'><select class='input-small' ng-change=\"alert('ok');datatable.updateColumn('"+data[i].name+"', '"+data[i].id+"')\"> <option ng-repeat='opt in experiment.experimentProperties.inputs["+i+"].possibleValues' value='{{opt.value}}'>{{opt.value}}</option></select></div>",data[i].name+$scope.EXPERIMENTPROPERTIES);
		}else{
			$scope.datatable.addRow(0,2, "<div class='controls'> <input type='text' class='input-small' ng-readonly='!experiment.experimentInformation.enabled' /></div>",data[i].name+$scope.EXPERIMENTPROPERTIES);
		}
		
		for (var j=1; j<$scope.datatable.getNumberRows()-1; j++) {
			if($scope.experiment.value.atomicTransfertMethods[(j-1)].class == "ManyToOne"){
				$scope.manyToInput(data[i],i,j,"experimentProperties");
			}else{
				$scope.oneToInput(data[i],i,j,"experimentProperties");
			}
		}
	};


	$scope.manyToInput = function(data,i,j,field){
		for(var k=0;k<$scope.experiment.value.atomicTransfertMethods[(j-1)].inputContainerUseds.length;k++){
			//Creation of the properties on the scope
			getter = $parse("experiment.value.atomicTransfertMethods["+(j-1)+"].inputContainerUseds["+k+"]."+field+"[\""+data.name+"\"]");
			getter.assign($scope,{"value":""});
			
			if(data.choiceInList){
				$scope.datatable.addRow(j, 2,"<div class='controls'><select class='input-small' ng-model='experiment.value.atomicTransfertMethods["+(j-1)+"].inputContainerUseds["+k+"]."+field+"[\""+data.name+"\"].value'> <option ng-repeat='opt in experiment."+field+".inputs["+i+"].possibleValues' value='{{opt.value}}'>{{opt.value}}</option></select></div>",data.name+field);	
			}else{
				$scope.datatable.addRow(j,2, "<div class='controls'> <input type='text' class='input-small'ng-model='experiment.value.atomicTransfertMethods["+(j-1)+"].inputContainerUseds["+k+"]."+field+"[\""+data.name+"\"].value' /></div>",data.name+field);
			}
		}
	}
	
	$scope.oneToInput = function(data,i,j,field){
		//Creation of the properties on the scope
		getter = $parse("experiment.value.atomicTransfertMethods["+(j-1)+"].inputContainerUsed."+field+"[\""+data.name+"\"]");
		getter.assign($scope,{"value":""});
		
		if(data.choiceInList){
			$scope.datatable.addRow(j, 2,"<div class='controls'><select class='input-small' ng-model='experiment.value.atomicTransfertMethods["+(j-1)+"].inputContainerUsed."+field+"[\""+data.name+"\"].value'> <option ng-repeat='opt in experiment."+field+".inputs["+i+"].possibleValues' value='{{opt.value}}'>{{opt.value}}</option></select></div>",data.name+field);	
		}else{
			$scope.datatable.addRow(j,2, "<div class='controls'> <input type='text' class='input-small' ng-model='experiment.value.atomicTransfertMethods["+(j-1)+"].inputContainerUsed."+field+"[\""+data.name+"\"].value' /></div>",data.name+field);
		}
	}
	
	$scope.getInstruments = function(){
		if($scope.experiment.instrumentInformation.instrumentUsedTypes.selected === null){
			$scope.experiment.instrumentProperties.inputs = [];
		}
		
		$scope.experiment.value.instrumentProperties = {};
		
		for(var i=0;i< $scope.getBasket().get().length;i++){
			if($scope.experiment.value.atomicTransfertMethods[i].class == "manyToOne"){
				$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.instrumentProperties = {};
			}else{
				$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = {};
			}
				
		}
		
		//suppression des colonnes dans le datatable
		//alert(Math.ceil($('td[id*="instrumentProperties"]').length / $scope.datatable.getNumberRows()));
		$('th[id*="instrumentProperties"]').remove();
		$('td[id*="instrumentProperties"]').remove();
		
		$scope.experiment.instrumentInformation.instruments.options = $scope.comboLists.getInstruments($scope.experiment.instrumentInformation.instrumentUsedTypes.selected.code).query();
		$scope.getInstrumentProperties($scope.experiment.instrumentInformation.instrumentUsedTypes.selected.code);
	
	};
	
	$scope.saveAll = function(){
		$scope.experiment.experimentInformation.save();
		
		$scope.experiment.experimentProperties.save();
		
		$scope.experiment.instrumentProperties.save();
		
		$scope.experiment.instrumentInformation.save();
		
		$scope.saveContainers();
	};
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.basket = $scope.getBasket();
		$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
		$scope.comboLists = comboLists;
		$scope.form = $scope.getForm();
		if(angular.isUndefined($scope.getForm().experiment)) {
			$scope.form.experiment = $scope.experiment;
			$scope.setForm($scope.form);
		
		
			$scope.experiment.value.typeCode = $scope.form.experimentTypes.selected.code;
			
			var basketList = $scope.getBasket().get();
			

			$scope.experiment.value.atomicTransfertMethods = {};
			
			//Initialisation of the experiment
			if($scope.experiment.value.code === ""){
				$http.post(jsRoutes.controllers.experiments.api.Experiments.save($scope.getForm().experimentTypes.selected.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.experiment.value = data;
						
						for(var i=0;i<basketList.length;i++){
							$scope.experiment.value.atomicTransfertMethods[i] = {class:"OneToOne", inputContainerUsed:[]};
							
							if($scope.experiment.value.atomicTransfertMethods[i].class == "ManyToOne"){
								$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.push({containerCode:basketList[i].code,instrumentProperties:{},experimentProperties:{}});
							}else{
								$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed = {containerCode:basketList[i].code,instrumentProperties:{},experimentProperties:{}};
							}
						}
					}
					
					angular.element(document).ready(function() {
						for(i=0; i<$scope.experiment.experimentProperties.inputs.length;i++){
							var getter = $parse("experiment.value.experimentProperties."+$scope.experiment.experimentProperties.inputs[i].name+".value");
							getter.assign($scope,"");
							
							$scope.addExperimentPropertiesColumns();
						}
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
			
			angular.element(document).ready(function() {
				$scope.addExperimentPropertiesColumns();
				$scope.addInstrumentPropertiesColumns();
			});

		}
	}	
}
CreateNewCtrl.$inject = ['$scope', 'datatable','$http','comboLists','$parse'];
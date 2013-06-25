function CreateNewCtrl($scope, datatable, $http,comboLists,$parse) {
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
				listInputOutputContainers:[],
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
				$scope.experiment.value.protocol = this.protocols.selected.code;
				$scope.experiment.value.resolutionCode = this.resolutions.selected.code;
				
				$http.post(jsRoutes.controllers.experiments.api.Experiments.updateExperimentInformation($scope.experiment.value.code).url, $scope.experiment.value)
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
				$http.post(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentInformation($scope.experiment.value.code).url, $scope.experiment.value)
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
		instrumentPropertiesInputs:[],
		enabled:true,
		toggleEdit:function(){
			this.enabled = !this.enabled;
		},
		save:function(){
			if(this.enabled && $scope.experiment.instrumentInformation.selected){
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
				});
			}
		}
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
	
	$scope.getInstrumentProperties = function(code){
		$scope.clearMessages();
		$http.get(jsRoutes.controllers.experiments.api.Experiments.getInstrumentProperties(code).url)
		.success(function(data, status, headers, config) {
			//Creation of the properties on the scope
			for(var i=0; i<data.length;i++){
				var getter = $parse("experiment.value.instrumentProperties."+data[i].name+".value");
				getter.assign($scope,"");
			}
			alert(JSON.stringify(data));
			$scope.experiment.instrumentProperties.instrumentPropertiesInputs = data;
			//alert($scope.experiment.instrumentProperties.instrumentPropertiesInputs.properties.choiceInList);
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-error";
			$scope.message.text = Messages('experiments.msg.save.error');
			
			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};
	
	$scope.getInstruments = function(){
		if($scope.experiment.instrumentInformation.instrumentUsedTypes.selected === null){
			$scope.experiment.instrumentProperties.instrumentPropertiesInputs = [];
		}
		
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
			
			//Initialisation of the experiment
			if($scope.experiment.value.code === ""){
				$http.post(jsRoutes.controllers.experiments.api.Experiments.save($scope.getForm().experimentTypes.selected.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.experiment.value = data;
					}
				})
				.error(function(data, status, headers, config) {
					alert("error");
				});
			}
			
			for(var i=0;i<basketList.length;i++){
				$scope.experiment.value.listInputOutputContainers.push({inputContainers:[],outputContainers:[]});
				$scope.experiment.value.listInputOutputContainers[i].inputContainers.push({"containerCode":basketList[i].code});
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
CreateNewCtrl.$inject = ['$scope', 'datatable','$http','comboLists','$parse'];
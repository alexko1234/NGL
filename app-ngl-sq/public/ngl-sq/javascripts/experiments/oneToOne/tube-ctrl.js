angular.module('home').controller('TubeCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position', function($scope,$window, datatable, $http,lists,$parse,$q,$position) {
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
				active:false,
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
	
	$scope.$on('experimentToInput', function(e, atomicTransfertMethod) {
		$scope.experimentToInput(atomicTransfertMethod);
	});
	
	$scope.experimentToInput = function(atomicTransfertMethod){
		for(var i=0;i<$scope.datatable.displayResult.length;i++){
			if(atomicTransfertMethod == "ManyToOne"){
				for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
					$scope.datatable.displayResult[i].data.inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].instrumentProperties;
					$scope.datatable.displayResult[i].data.inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed[j].experimentProperties;
					i++;
				}
				if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
					$scope.datatable.displayResult[i].data.outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed;
					$scope.datatable.displayResult[i].data.outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.experimentProperties;					
					$scope.datatable.displayResult[i].data.outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.instrumentProperties;					
					
				}
			}else{
				$scope.datatable.displayResult[i].data.inputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties;
				$scope.datatable.displayResult[i].data.inputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties;
				if(atomicTransfertMethod == "OneToMany"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
						$scope.datatable.displayResult[i].data.outputContainerUseds = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds;
						for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
							$scope.datatable.displayResult[i].data.outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].instrumentProperties;
							$scope.datatable.displayResult[i].data.outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].experimentProperties;
							i++;
						}
					}
				}else if(atomicTransfertMethod != "OneToVoid"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
						$scope.datatable.displayResult[i].data.outputContainerUsed = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed;
						$scope.datatable.displayResult[i].data.outputExperimentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties;					
						$scope.datatable.displayResult[i].data.outputInstrumentProperties = $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties;					
					}
				}
			}
		}
	}
	
	$scope.$on('deleteInstrumentPropertiesInputs', function(e, header) {
		 for(var i=0;i<$scope.datatable.config.columns.length;i++){
				if($scope.datatable.config.columns[i].extraHeaders != undefined && $scope.datatable.config.columns[i].extraHeaders[1] == header){
					$scope.datatable.deleteColumn(i);
					i--;
				}
			}
	});
	
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(2,$scope.datatable.newColumn(data.name,"inputInstrumentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"}));
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(2,$scope.datatable.newColumn(data.name,"inputExperimentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Inputs","1":"Experiments"}));
	});
	
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(data.name,"outputExperimentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Outputs","1":"Experiments"}));
	});
	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(data.name,"outputInstrumentProperties."+data.code+".value",true, true,true,"String",data.choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"}));
	});
	
	$scope.addOutputColumns = function(){
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn("Code","outputContainerUsed.code",false, true,true,"String",false,undefined,{"0":"Outputs"}));
	};
	
	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		for(var i=0;i<$scope.datatable.displayResult.length;i++){
		    if(atomicTransfertMethod == "ManyToOne"){
				for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[0].length;j++){
					$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[j].instrumentProperties = $scope.datatable.displayResult[i].data.inputInstrumentProperties;
					$scope.experiment.value.atomicTransfertMethods[0].inputContainerUsed[j].experimentProperties = $scope.datatable.displayResult[i].data.inputExperimentProperties;
					i++;
				}
			}else{
				$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].data.inputInstrumentProperties;
				$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].data.inputExperimentProperties;
			}
		}
	});
	
	$scope.$on('addInstrumentPropertiesInputToScope', function(e, data) {
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
	});
	
	$scope.$on('addExperimentPropertiesOutputToScope', function(e, data) {
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
	});
	
	$scope.$on('addExperimentPropertiesInputToScope', function(e, data) {
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
	});
	
	$scope.$on('addInstrumentPropertiesOutputToScope', function(e, data) {
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
	});
	
	$scope.$on('save', function(e, promises, func) {
		promises.push($scope.datatable.save());
		$scope.$emit('viewSaved', promises, func);
	});
	
	$scope.refreshView = function(){
		var i = 0;
		var k = 0;
		while($scope.experiment.value.atomicTransfertMethods[i] != undefined){
			if($scope.experimentType.atomicTransfertMethod != "ManyToOne"){
				$scope.datatable.displayResult[i].data.state = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.state
			}else{
				var j = 0;
				while($scope.experiment.value.atomicTransfertMethods[k].inputContainerUseds[j] != undefined){
					$scope.datatable.displayResult[k].data.state = $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].state;
					k++;
				}
			}
			i++;
		 }
	};
	
	$scope.$on('refresh', function(e) {
		$scope.refreshView();
		
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {
		for(var i=0;i<$scope.datatable.displayResult.length;i++){
			if(atomicTransfertMethod == "ManyToOne"){
				if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed!=undefined){
					$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].data.outputInstrumentProperties;
					$scope.experiment.value.atomicTransfertMethods[0].outputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].data.outputExperimentProperties;					
				}
			}else{
				if(atomicTransfertMethod == "OneToMany"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds!=undefined){
						for(var j =0;j<$scope.experiment.value.atomicTransfertMethods[i].length;j++){
							$scope.experiment.value.atomicTransfertMethods[i].ouputContainerUseds[j].instrumentProperties = $scope.datatable.displayResult[i].data.outputInstrumentProperties;
							
							$scope.experiment.value.atomicTransfertMethods[i].outputContainerUseds[j].experimentProperties = $scope.datatable.displayResult[i].data.outputExperimentProperties;
							i++;
						}
					}
				}else if(atomicTransfertMethod != "OneToVoid"){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed!=undefined){
						$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties = $scope.datatable.displayResult[i].data.outputInstrumentProperties;					
						
						$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties = $scope.datatable.displayResult[i].data.outputExperimentProperties;					
					}
				}
			}
		}
	});
	
	$scope.newExperiment = function (){
		var containers = [];
		var promises = [];
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
			$scope.getInstruments();
			if(angular.isUndefined($scope.form.experiment)) {
				$scope.init_experiment(containers, $scope.experimentType.atomicTransfertMethod);
			}else {
				$scope.experiment = $scope.form.experiment;
				$scope.addExperimentPropertiesInputsColumns();
			}
		});
	};
	
	$scope.getContainersPromise = function(){
		
	}
	
	$scope.loadExperiment = function (){
		var containers = [];
		var promises = [];
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
			$scope.getInstruments(true);
			$scope.experimentToInput($scope.experimentType.atomicTransfertMethod);

			if($scope.isOutputGenerated()){
				$scope.addOutputColumns();
				$scope.addExperimentPropertiesOutputsColumns();
				$scope.addInstrumentPropertiesOutputsColumns();
			}
		});
	};
	
	//Init
	$scope.datatable = datatable($scope, $scope.datatableConfig);
	//$scope.test = oneToOne($scope,"datatable", "none");
	
	if($scope.experiment.editMode){
		$scope.loadExperiment();
	}else{
		$scope.newExperiment();
	}
	
}]);
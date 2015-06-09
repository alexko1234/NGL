angular.module('home').controller('ManyToOneFlowcellCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','manyToOne','mainService','tabService', function($scope,$window, datatable, $http,lists,$parse,$q,$position,manyToOne,mainService,tabService) {
	$scope.datatableConfig = {
			name:"FDR_prepaFC",
			columns:[
			         {
			        	 "header":Messages("containers.table.support.column"),
			        	 "property":"outputPositionX",
			        	 "order":true,
			        	 "type":"text",
			        	 "position":0,
			        	 "extraHeaders":{0:"solution stock"}
			         },
			         {
			        	 "header":Messages("containers.table.supportCode"),
			        	 "property":"inputSupportCode",
			        	 "order":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"solution stock"}
			         },
			         {
			        	 "header":Messages("containers.table.tags"),
			        	 "property":"inputTags",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":false,
			        	 "position":2,
			        	 "render":"<div list-resize='value.data.inputTags | unique' below-only-deploy>",
			        	 "extraHeaders":{0:"solution stock"}
			         },
			         {
			        	 "header":function(){
			        		 return Messages("containers.table.concentration") +" (nM)";
			        	 },
			        	 "property":"inputConcentration",
			        	 "order":true,
			        	 "type":"number",
			        	 "edit":false,
			        	 "position":3,
			        	 "extraHeaders":{0:"solution stock"}
			         },
			         {
			        	 "header":function(){
			        		 return Messages("containers.table.volume") +" (µl)";
			        	 },
			        	 "property":"inputVolume.value",
			        	 "order":true,
			        	 "type":"number",
			        	 "edit":false,
			        	 "position":4,
			        	 "extraHeaders":{0:"solution stock"}
			         },
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputState.code",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":false,
			        	 "position":5,
			        	 "extraHeaders":{0:"solution stock"},
			        	 "filter":"codes:'state'"
			         },
			         {
			        	 "header":Messages("containers.table.percentage"),
			        	 "property":"inputContainerUsed.percentage",
			        	 "order":true,
			        	 "type":"number",
			        	 "edit":false,
			        	 "position":41,
			        	 "extraHeaders":{0:"prep FC"}
			         }
			         ],
			         compact:true,
			         pagination:{
			        	 active:false
			         },		
			         search:{
			        	 active:false
			         },
			         order:{
			        	 mode:'local', //or 
			        	 active:true,
			        	 by:'outputPositionX'
			         },
			         remove:{
			        	 active:false,
			         },
			         hide:{
			        	 active:true
			         },
			         edit:{
			        	 active: !$scope.doneAndRecorded,
			        	 columnMode:true
			         },
			         save:{
			        	 active:true,
			        	 withoutEdit: true,
			        	 showButton:false,
			        	 mode:'local'
			         },
			         messages:{
			        	 active:false,
			        	 columnMode:true
			         },
			         exportCSV:{
			        	 active:true,
			        	 showButton:true,
			        	 delimiter:";",
			        	 start:false			        	 
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

	$scope.propertyChanged = [];//The list of property name changed by the user and not saved
	
	$scope.notifyChange = function(laneNumber, property){
		var exist = false;
		for(var i=0;i<$scope.propertyChanged.length;i++){
			if($scope.propertyChanged[i].property.name === property.name &&$scope.propertyChanged[i].index === laneNumber){
				exist = true;
			}
		}
		if(!exist){
			$scope.message.clazz = "alert alert-warning";
			$scope.message.text = "Vous venez de modifier "+property.name+" de la lane "+(laneNumber+1);
			for(i=0;i<$scope.propertyChanged.length;i++){
				$scope.message.text += ", "+$scope.propertyChanged[i].property.name+" de la lane "+($scope.propertyChanged[i].index+1);
			}
			$scope.message.text += ", vous devez impérativement cliquer sur sauvegarder pour que les calculs de la FDR se remettent à jour";
			$scope.propertyChanged.push({"index":laneNumber,"property":property});
			console.log($scope.propertyChanged);
			$scope.message.isDetails = false;
		}
	};
	
	$scope.$on('experimentToInput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToInput($scope.datatable);
	});

	$scope.changeValueInFlowcellCompo = function(){
		$scope.atomicTransfere.experimentToInput($scope.datatable);
	};

	$scope.$on('deleteInstrumentPropertiesInputs', function(e, header) {
		for(var i=0;i<$scope.datatable.config.columns.length;i++){
			if($scope.datatable.config.columns[i].extraHeaders != undefined && $scope.datatable.config.columns[i].extraHeaders[1] == header){
				$scope.datatable.deleteColumn(i);
				i--;
			}
		}
	});

	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"inputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
			column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
		}
		$scope.datatable.addColumn(data.displayOrder+5,column);
	});

	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
		console.log(data);		
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"inputExperimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{});
		column.defaultValues = data.defaultValue;
		if(data.code == "requiredVolume1" || data.code == "NaOHVolume" || data.code == "NaOHConcentration" || data.code == "EBVolume" || data.code == "finalConcentration1" || data.code == "finalVolume1"){
			column.extraHeaders = {0:"denaturation"};
		}else if(data.code == "requiredVolume2" || data.code == "HT1Volume" || data.code == "phixVolume" || data.code == "phixConcentration" || data.code == "finalConcentration2" || data.code == "finalVolume2"){
			column.extraHeaders = {0:"dilution"};
		}else if(data.code == "requiredVolume3"){
			column.extraHeaders = {0:"prep FC"};
		}
		if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
			column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
		}
		console.log(data.displayOrder);
		$scope.datatable.addColumn(data.displayOrder+5 ,column);
	});

	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		if($scope.experiment.containerOutProperties.indexOf(data) == -1){
			var unit = "";
			if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
			$scope.experiment.containerOutProperties.push(data);
			var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"outputExperimentProperties."+data.code+".value",false, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{});
			column.defaultValues = data.defaultValue;
			column.extraHeaders = {0:"prep FC"};
			if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
				column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
			}
			$scope.datatable.addColumn(data.displayOrder+40,column);
		}
	});

	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		var unit = "";
		if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
		var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"outputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{});
		column.defaultValues = data.defaultValue;
		if(data.displayMeasureValue != undefined && data.displayMeasureValue != null){
			column.convertValue = {"active":true, "displayMeasureValue":data.displayMeasureValue.value, "saveMeasureValue":data.saveMeasureValue.value};
		}
		$scope.datatable.addColumn(data.displayOrder+5,column);
	});

	$scope.addOutputColumns = function(){
		var column = $scope.datatable.newColumn(Messages("containers.table.supportCode"),function(){return $scope.experiment.value.instrumentProperties.containerSupportCode.value;},false, true,true,"text",false,undefined,{"0":"prep FC"});
		$scope.datatable.addColumn(1000,column);
	};	

	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});

	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.inputToExperiment($scope.datatable);
	});

	$scope.$on('addInstrumentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerIn")){
						var getter = $parse("datatable.displayResult["+i+"].inputInstrumentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].instrumentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].instrumentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	});

	$scope.addExperimentOutputDatatableToScope = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerOut")){
						var getter = $parse("datatable.displayResult["+i+"].outputExperimentProperties."+data[j].code+".value");
						var k = $scope.datatable.displayResult[i].data.inputX;
						if($scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUsed.experimentProperties && $scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUsed.experimentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[k-1].outputContainerUsed.experimentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	};

	$scope.$on('addExperimentPropertiesOutputToScope', function(e, data) {
		var i = 0;
		while($scope.experiment.value.atomicTransfertMethods[i] != undefined){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties == null){
						$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties = {};
					}

					if(!$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code]){
						$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code] = undefined;						
					}
				}
			}
			i++;
		}
	});

	$scope.$on('addExperimentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerIn")){
						var getter = $parse("datatable.displayResult["+i+"].inputExperimentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].experimentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.experimentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	});

	$scope.$on('addInstrumentPropertiesOutputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
			for(var i=0;i<$scope.datatable.getData().length;i++){
				for(var j=0; j<data.length;j++){
					if($scope.getLevel( data[j].levels, "ContainerOut")){
						var getter = $parse("datatable.displayResult["+i+"].outputInstrumentProperties."+data[j].code+".value");
						if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[j].code]){
							getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.instrumentProperties[data[j].code]);
						}else{
							getter.assign($scope,undefined);
						}
					}
				}
			}
		}
	});

	$scope.$on('save', function(e, promises, func, endPromises) {		
		promises.push($scope.datatable.save());
		$scope.$emit('viewSaved', promises, func,endPromises);
		$scope.propertyChanged = [];
	});

	$scope.refreshView = function(){
		$scope.atomicTransfere.experimentToInput($scope.datatable);
		$scope.atomicTransfere.experimentToOutput($scope.datatable);
	};
	
	$scope.$on('disableEditMode', function(){
		$scope.datatable.config.edit.active = false;
	});
	
	$scope.$on('enableEditMode', function(){
		$scope.datatable.config.edit.active = true;
	});


	$scope.$on('refresh', function(e) {
		$scope.refreshView();

		$scope.$emit('viewRefeshed');
	});

	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.outputToExperiment($scope.datatable);
	});

	$scope.$on('experimentToOutput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToOutput($scope.datatable);
	});

	$scope.duplicateContainer = function(container,position){
		$scope.inputContainers.splice(position+1,0,angular.copy(container));
	};

	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
		console.log("atomic: "+$scope.laneCount);
		for(var i=0;i<$scope.laneCount;i++){
			$scope.experiment.value.atomicTransfertMethods[i] = {class:atomicTransfertMethod, line:(i+1), column:1, position:(i+1),inputContainerUseds:[],outputContainerUsed:{experimentProperties:{}}};
		}
	};

	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
		$scope.init_atomicTransfert(containers, atomicTransfertMethod);
	});
	
	$scope.dragInProgress=function(value){
		$scope.dragIt=value;
	};		
	
	$scope.drop = function(e, data, droppedItem, ngModel, alreadyInTheModel) {
		//capture the number of the atomicTransfertMethod
		if(!alreadyInTheModel){
			var array_regexp = /^experiment.value.atomicTransfertMethods\[([0-9]+)\].+/;
			var model = e.dataTransfer.getData('Model');
	
			var match = model.match(array_regexp);
			if(!match){
				$scope[model].splice($scope[model].indexOf(data), 1);	   
			}else{
				$scope.experiment.value.atomicTransfertMethods[match[1]].inputContainerUseds.splice($scope.experiment.value.atomicTransfertMethods[match[1]].inputContainerUseds.indexOf(data), 1);
			}
	
			$scope.atomicTransfere.reloadContainerDragNDrop(undefined, undefined, $scope.datatable);
			$scope.scanOpenedAll();
		}
	};

	$scope.beforeDropData = function(e, data, ngModel, alreadyInTheModel){
		if(!alreadyInTheModel){
			var array_regexp = /^experiment.value.atomicTransfertMethods\[([0-9]+)\].+/;
			var match = ngModel.match(array_regexp);
			if(match){
				$scope.rows[match[1]]= true;
				
				if(angular.isDefined($scope.experiment.value.atomicTransfertMethods)){
					$scope.scanOpenedAll();
				}
				
			}
		}
		
		return data;
	};

	$scope.setFlowcellProperty = function(lineNumber, value, property){
		for(var i=0;i<$scope.experiment.value.atomicTransfertMethods[lineNumber].inputContainerUseds.length;i++){
			$scope.experiment.value.atomicTransfertMethods[lineNumber].inputContainerUseds.experimentProperties[property].value = value;
		}
	};

	$scope.deleteInput = function(container){
		$scope.inputContainers.splice($scope.inputContainers.indexOf(container), 1);
	};

	$scope.deleteInputOnAtomic = function(index, container){
		$scope.experiment.value.atomicTransfertMethods[index].inputContainerUseds.splice($scope.experiment.value.atomicTransfertMethods[index].inputContainerUseds.indexOf(container),1);
	};

	$scope.hideRow = function(index){
		$scope.rows[index] = !$scope.rows[index];
		$scope.scanOpenedAll();
	};

	$scope.hideRowAll = function(){
		for (var i=0; i<$scope.laneCount;i++){	
			$scope.rows[i] = false;
		}	    
		$scope.isAllOpen = false;	    
	};

	$scope.showRowAll = function(){
		for (var i=0; i<$scope.laneCount;i++){	
			$scope.rows[i] = true;
		}	    
		$scope.isAllOpen = true;
	};

	$scope.scanOpenedAll = function(){
		if ($scope.isAllOpen == false){		
			for(var i=0; i<$scope.laneCount;i++){
				if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length > 0){
					if ($scope.rows[i] == true){
						$scope.isAllOpen = true;			    
					}
				}
			}

		}else{
			for(var i=0; i<$scope.laneCount;i++){
				if($scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length > 0){
					if ($scope.rows[i] == false){
						$scope.isAllOpen = false;			     
					}else{
						$scope.isAllOpen = true;
						i = $scope.laneCount +1;
					}
				}
			}
		}   

	};

	$scope.isFilled = function(){ 	
		for (var i=0; i<$scope.laneCount;i++){
			if(angular.isDefined($scope.experiment.value.atomicTransfertMethods[i])
					&& $scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length >0){		
				return true;
			}
		}


	};

	$scope.updateColumnPropertyCodeValues = function(codeValue){
		var data = $scope.experiment.experimentProperties.inputs; 
		for(var i=0;i<$scope.laneCount;i++){
			for(var j=0; j<data.length;j++ ){
				if(codeValue == data[j].code){		      
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code] === undefined){
						$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code] = {};
					}
					$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed.experimentProperties[data[j].code].value = $scope.allOutputContainersUsed[data[j].code].value;			 
				}
			}
		}
	}

	$scope.init_flowcell = function(laneCount){
		$scope.laneCount = laneCount;
		if(!$scope.experiment.editMode){
			//init flowcells
			console.log($scope.laneCount);
			for(var i=0;i<$scope.laneCount;i++){
				$scope.flowcells[i] = [];
				$scope.rows[i] = true;
			}
		}else{

			for(var i=0;i<$scope.laneCount;i++){

				$scope.rows[i] = false;
			}
		}
		console.log($scope.flowcells);
	};

	$scope.getSampleAndTags = function(container){
		var sampleCodeAndTags = [];
		angular.forEach(container.contents, function(content){
			if(content.properties.tag != undefined && content.sampleCode != undefined){
				sampleCodeAndTags.push(content.sampleCode+" / "+content.properties.tag.value);
			}
		});
		return sampleCodeAndTags;
	};

	$scope.changeView = function(view){
		console.log(view);
		$scope.view = view;
	};	
	

	//Init
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.experiment.outputGenerated = true;
	$scope.atomicTransfere = manyToOne($scope, "dragndrop", "datatable");
	$scope.inputContainers = [];
	$scope.flowcells = [];
	$scope.rows = [];
	$scope.laneCount = 0;
	$scope.view = 1;
	$scope.isAllOpen = true;
	$scope.allOutputContainersUsed = [];
	$scope.dragIt=false;


	if($scope.experiment.editMode){
		$scope.isAllOpen = false;
		$scope.atomicTransfere.loadExperiment($scope.datatable);
		if(!angular.isUndefined(mainService.getBasket())){
			$scope.basket = mainService.getBasket().get();
			if($scope.basket.length > 0){
				$scope.edit();
			}
			angular.forEach($scope.basket, function(basket){
				$http.get(jsRoutes.controllers.containers.api.Containers.list().url,{params:{supportCode:basket.code}})
				.success(function(data, status, headers, config) {
					$scope.clearMessages();
					if(data!=null){
						angular.forEach(data, function(container){
							$scope.inputContainers.push(container);
						});
						$scope.inputContainers = $scope.atomicTransfere.containersToContainerUseds($scope.inputContainers);
					}
				})
				.error(function(data, status, headers, config) {
					alert("error");
				});
			});
		}
	}else{
		$scope.atomicTransfere.newExperiment($scope.datatable);
	}

}]);
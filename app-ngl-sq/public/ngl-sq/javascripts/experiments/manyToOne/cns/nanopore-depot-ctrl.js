angular.module('home').controller('NanoporeDepotCtrl',['$scope', '$window','datatable','$http','lists','$parse','$q','$position','manyToOne','mainService','tabService', function($scope,$window, datatable, $http,lists,$parse,$q,$position,manyToOne,mainService,tabService) {
	$scope.datatableConfig = {
			name:"NanoportInputOutput",
			columns:[
			         {
			        	 "header":Messages("containers.table.supportCode"),
			        	 "property":"support.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":0,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         {
			        	 "header":Messages("containers.table.categoryCode"),
			        	 "property":"support.categoryCode",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.projectCodes"),
			        	 "property":"projectCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":3,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.sampleCodes"),
			        	 "property":"sampleCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":4,
						 "render":"<div list-resize='value.data.sampleCodes | unique'>",
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":6,
			        	 "extraHeaders":{0:"Inputs"}
			         },
					 {
			        	 "header":Messages("containers.table.fromExperimentTypeCodes"),
			        	 "property":"fromExperimentTypeCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":7,
			        	 "extraHeaders":{0:"Inputs"}
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
				by:'ContainerInputCode'
			},
			remove:{
				active:false,
			},
			save:{
				active:true,
				showButton: false,
				mode:'local',
			},
			hide:{
				active:true
			},
			edit:{
				active: !$scope.doneAndRecorded,
				showButton: true,
				columnMode:false
			},
			messages:{
				active:false,
				columnMode:true
			},
			extraHeaders:{
				number:2,
				dynamic:true,
			},
			exportCSV:{
				active:true,
				showButton:true,
				delimiter:";",
				start:false
			},
			otherButton:{
				active:true,
				template:'<button class="btn btn btn-info" ng-click="newPurif()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doPurif" title="'+Messages("experiments.addpurif")+'">Messages("experiments.addpurif")</button><button class="btn btn btn-info" ng-click="newQc()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doQc" title="Messages("experiments.addqc")">Messages("experiments.addqc")</button>'
			},
			showTotalNumberRecords:false
	};
	
	
	$scope.datatableConfigLoadingReport = {
			name:"NanoportInputOutput",
			columns:[
			         {
			        	 "header":function() { return $scope.header.loadingReport.hour},
			        	 "property":"hour",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"Bilan chargement"}
			         },
			         {
			        	 "header": function() { return $scope.header.loadingReport.time},
			        	 "property":"time",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			        	 "extraHeaders":{0:"Bilan chargement"}
			         },
			         {
			        	 "header":function(){
			        		 return $scope.header.loadingReport.volume;
			        		 },
			        	 "property":"volume",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":3,
			        	 "extraHeaders":{0:"Bilan chargement"}
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
				by:'code'
			},
			remove:{
				active:true,
			},
			save:{
				active:true,
				showButton: false,
				mode:'local',
			},
			hide:{
				active:true
			},
			edit:{
				active: !$scope.doneAndRecorded,
				showButton: false,
				columnMode:false
			},
			add:{
				active:true
			},
			messages:{
				active:false,
				columnMode:true
			},
			extraHeaders:{
				number:1,
				dynamic:true,
			},
			showTotalNumberRecords:false
	};
	
	
	
	$scope.datatableConfigQcFlowcell = {
			name:"NanoportQcFlowcell",
			columns:[
						
			         {
			        	 "header": function() { return $scope.header.qcFlowcell.group},
			        	 "property":"group",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"QC Flowcell"}
			         },
			         {
			        	 "header":function() { return $scope.header.qcFlowcell.preLoadingNbActivePores},
			        	 "property":"preLoadingNbActivePores",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"integer",
			        	 "position":2,
			        	 "extraHeaders":{0:"QC Flowcell"}
			         },
			         {
			        	 "header":function() { return $scope.header.qcFlowcell.postLoadingNbActivePores},
			        	 "property":"postLoadingNbActivePores",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"integer",
			        	 "position":3,
			        	 "extraHeaders":{0:"QC Flowcell"}

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
				by:'code'
			},
			remove:{
				active:false,
			},
			save:{
				active:true,
				showButton: false,
				mode:'local',
			},
			hide:{
				active:true
			},
			edit:{
				active: !$scope.doneAndRecorded,
				showButton: true,
				columnMode:false
			},
			messages:{
				active:false,
				columnMode:true
			},
			extraHeaders:{
				number:1,
				dynamic:true,
			},
			showTotalNumberRecords:false
	};
	
	$scope.$on('experimentToInput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToInput($scope.datatable);
	});
	
	$
	$scope.$on('deleteInstrumentPropertiesInputs', function(e, header) {
			angular.forEach($scope.datatable.config.columns, function(column, index){
				if(column.extraHeaders != undefined && column.extraHeaders[1] == header){
					$scope.datatable.deleteColumn(index);
				}
			});
	});
	
	$scope.$on('addInstrumentPropertiesInput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"inputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(data.displayOrder+5,column);
	});
	
	$scope.$on('addExperimentPropertiesInput', function(e, data, possibleValues) {
		if(data.propertyValueType!="object_list"){
			var unit = "";
			if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
			var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"inputExperimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Inputs"});
			column.defaultValues = data.defaultValue;
			$scope.datatable.addColumn(data.displayOrder+5,column);
		}
	});
	
	$scope.$on('addExperimentPropertiesOutput', function(e, data, possibleValues) {
		if(data.propertyValueType!="object_list"){
			var unit = "";
			if(data.displayMeasureValue!=undefined) unit = "("+data.displayMeasureValue.value+")";
			var column = $scope.datatable.newColumn(function(){return data.name+" "+unit;},"outputExperimentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs"});
			column.defaultValues = data.defaultValue;
			$scope.datatable.addColumn(data.displayOrder+5,column);
		}
	});
	
	$scope.$on('addInstrumentPropertiesOutput', function(e, data, possibleValues) {
		var column = $scope.datatable.newColumn(data.name,"outputInstrumentProperties."+data.code+".value",data.editable, true,true,$scope.getPropertyColumnType(data.valueType),data.choiceInList,possibleValues,{"0":"Outputs","1":"Instruments"});
		column.defaultValues = data.defaultValue;
		$scope.datatable.addColumn(data.displayOrder+5,column);
	});
	
	$scope.addOutputColumns = function(){
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(Messages("containers.table.code"),"outputContainerUsed.code",false, true,true,"text",false,undefined,{"0":"Outputs"}));
		$scope.datatable.addColumn(-1,$scope.datatable.newColumn(Messages("containers.table.stateCode"),"outputContainerUsed.state.code | codes:'state'",false, true,true,"text",false,undefined,{"0":"Outputs"}));
	};
	
	$scope.$on('addOutputColumns', function(e) {
		$scope.addOutputColumns();
	});
	
	$scope.$on('inputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.inputToExperiment($scope.datatable);
	});
	
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
			$scope.experiment.value.atomicTransfertMethods[0] = {class:atomicTransfertMethod, inputContainerUseds:[], line:"1", column:"1", outputContainerUseds:[]};
			angular.forEach(containers, function(container){
				$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.push({code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state,locationOnContainerSupport:container.support});
				$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds.push({instrumentProperties:{},experimentProperties:{}});
			});
	};
	
	$scope.$on('initAtomicTransfert', function(e, containers, atomicTransfertMethod) {
		$scope.init_atomicTransfert(containers, atomicTransfertMethod);
	});
	
	$scope.$on('addInstrumentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
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
	  }
	});
	
	$scope.$on('addExperimentPropertiesOutputToScope', function(e, data) {
		for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					var getter = $parse("datatable.displayResult["+i+"].outputExperimentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].experimentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].experimentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].experimentProperties[data[j].code].value);
					}else{
						getter.assign($scope,"");
					}
				}
			}
		}
	});
	
	$scope.$on('addExperimentPropertiesInputToScope', function(e, data) {
		if($scope.datatable.getData() != undefined){
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
	});
	
	$scope.$on('addInstrumentPropertiesOutputToScope', function(e, data) {
		for(var i=0;i<$scope.datatable.getData().length;i++){
			for(var j=0; j<data.length;j++){
				if($scope.getLevel( data[j].levels, "ContainerOut")){
					var getter = $parse("datatable.displayResult["+i+"].outputInstrumentProperties."+data[j].code+".value");
					if($scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].instrumentProperties && $scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].instrumentProperties[data[j].code]){
						getter.assign($scope,$scope.experiment.value.atomicTransfertMethods[i].outputContainerUsed[0].instrumentProperties[data[j].code].value);
					}else{
						getter.assign($scope,"");
					}
				}
			}
		}
	});
	
	$scope.$on('save', function(e, promises, func, endPromises) {
		promises.push($scope.datatableLoadingReport.save());
		promises.push($scope.datatableQcFlowcell.save());

		$scope.setValidePercentage($scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds);
		outputLoadingReportToExperiment($scope.datatableLoadingReport);
		outputQcFlowcellToExperiment($scope.datatableQcFlowcell);
		promises.push($scope.datatable.save());
		$scope.$emit('viewSaved', promises, func, endPromises);
	});
	
	$scope.refreshView = function(){
		$scope.atomicTransfere.reloadContainersDatatable($scope.datatable);		
	};
	
	$scope.$on('refresh', function(e) {
		$scope.refreshView();
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('outputToExperiment', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.outputToExperiment($scope.datatable);
		outputLoadingReportToExperiment($scope.datatableLoadingReport);
		outputQcFlowcellToExperiment($scope.datatableQcFlowcell);
	});
	
	$scope.$on('experimentToOutput', function(e, atomicTransfertMethod) {
		$scope.atomicTransfere.experimentToOutput($scope.datatable);
	});	
	
	
	$scope.$on('disableEditMode', function(){
		$scope.datatable.config.edit.active = false;
	});
	
	$scope.$on('enableEditMode', function(){
		$scope.datatable.config.edit.active = true;
	});
	
	$scope.$on('experimentLoaded',function(e){
		console.log("experimentLoaded");
		$scope.datatableQcFlowcell.setData($scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties.qcFlowcell.value);
		$scope.datatableLoadingReport.setData($scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties.loadingReport.value);
	});
	
	var outputLoadingReportToExperiment =function(output){
		var allData = output.getData();
		if(allData != undefined){
			if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties==undefined){
				$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties={};
			}
			$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties.loadingReport={value:[],_type:"object_list"};
			$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties.loadingReport.value=$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties.loadingReport.value.concat(allData);
		}
	};
	
	
	var outputQcFlowcellToExperiment =function(output){
		var allData = output.getData();
		if(allData != undefined){
			if($scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties==undefined){
				$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties={};
			}
			$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties.qcFlowcell={value:[],_type:"object_list"};
			$scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties.qcFlowcell.value = $scope.experiment.value.atomicTransfertMethods[0].outputContainerUseds[0].experimentProperties.qcFlowcell.value.concat(allData);
		}
	};
	
	
	var getPropertyName = function(code){
		var name="";
		angular.forEach($scope.experiment.experimentProperties.inputs, function(input){
			var unit = "";
			if(input.code===code){
				if(input.displayMeasureValue!=undefined){
					name= input.name+ " ("+input.displayMeasureValue.value+")";
				}else { 
					name = input.name; 
				}
			}
		});
		return name;
	}
	
	$scope.setValidePercentage = function(containerUseds){
		var l = containerUseds.length;
		angular.forEach(containerUseds, function(container){			
				if(container.percentage != 100/l){
					container.percentage = 100/l;
				}			
		});
		
	};
	
	//Init
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.datatableLoadingReport = datatable($scope.datatableConfigLoadingReport);	
	$scope.datatableQcFlowcell = datatable($scope.datatableConfigQcFlowcell);
	
	$scope.atomicTransfere = manyToOne($scope, "datatable", "none");

	$scope.experiment.outputGenerated = $scope.isOutputGenerated();
	
	//Header Tables QcFlowcell and LoadingReport
	$scope.header={"loadingReport":[],"qcFlowcell":[]};
	$scope.header.loadingReport.volume=getPropertyName("loadingReport.volume");
	$scope.header.loadingReport.hour=getPropertyName("loadingReport.hour");
	$scope.header.loadingReport.time=getPropertyName("loadingReport.time");
	$scope.header.qcFlowcell.group=getPropertyName("qcFlowcell.group");
	$scope.header.qcFlowcell.postLoadingNbActivePores=getPropertyName("qcFlowcell.postLoadingNbActivePores");
	$scope.header.qcFlowcell.preLoadingNbActivePores=getPropertyName("qcFlowcell.preLoadingNbActivePores");
	
	
	if($scope.experiment.editMode){
		$scope.atomicTransfere.loadExperiment($scope.datatable);
		
	}else{
		var qcFlowcell=[{group: "total", preLoadingNbActivePores: undefined, postLoadingNbActivePores: undefined}, 
		                {group: "groupe1", preLoadingNbActivePores: undefined, postLoadingNbActivePores:undefined},
		                {group: "groupe2", preLoadingNbActivePores: undefined, postLoadingNbActivePores:undefined},
		                {group: "groupe3", preLoadingNbActivePores: undefined, postLoadingNbActivePores:undefined},
		                {group: "groupe4", preLoadingNbActivePores: undefined, postLoadingNbActivePores:undefined}];
		
		$scope.atomicTransfere.newExperiment($scope.datatable);
		$scope.datatableLoadingReport.setData([]);
		$scope.datatableQcFlowcell.setData(qcFlowcell);
	}
	
	
	
}]);
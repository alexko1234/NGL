angular.module('home').controller('GETTubesToFlowcellIntCtrl',['$scope', '$parse', '$filter','$http', 'atmToDragNDrop','datatable',
                                                               function($scope, $parse, $filter,$http, atmToDragNDrop, datatable) {
	
	$scope.isRoadMapAvailable = true;
	
	// NGL-1055: name explicite pour fichier CSV exporté: typeCode experience
	// NGL-1055: mettre getArray et codes:'' dans filter et pas dans render
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),
			columns:[  
					 {
			        	 "header":Messages("containers.table.support.number"),
			        	 "property":"atomicTransfertMethod.line",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":0,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },	
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainerUsed.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.sampleTypes"),
			        	 "property":"inputContainerUsed.contents[0].properties.type_echantillon.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text", 
			        	 "position":1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.nomPool"),
			        	 "property":"inputContainerUsed.contents[0].properties.Nom_pool_sequencage.value",
			        	 "order":true,
						 "edit":false,
						 "hide":false,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainerUsed.projectCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":2,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0:Messages("experiments.inputs")}
				     },			         					 
					 {
			        	 "header":Messages("containers.table.concentration"),
			        	 "property":"inputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":5,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"inputContainerUsed.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":false,
			        	 "type":"text",
			        	 "position":5.5,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.tagCategory"),
			        	 "property":"inputContainerUsed.contents[0].properties.tagCategory.value",
			        	 "order":true,
						 "edit":false,
						 "hide":false,
			        	 "type":"text",
			        	 "position":6,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"text",
			        	 "position":400,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },		         					 
					 {
			        	 "header":Messages("containers.table.concentration"),
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":405,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.concentration.unit"),
			        	 "property":"outputContainerUsed.concentration.unit",
			        	 "order":true,
						 "edit":false,
						 "hide":false,
			        	 "type":"text",
			        	 "position":405.5,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
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
				by:"atomicTransfertMethod.line"
			},
			remove:{
				active:false,
			},
			save:{
				active:true,
				withoutEdit: true,
				mode:'local',
				showButton:false,
				changeClass:false,
				callback:function(datatable){
					copyFlowcellCodeToDT(datatable);
				}
			},
			hide:{
				active:true
			},
			mergeCells:{
	        	active:true 
	        },
			
			edit:{
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
	        	byDefault : true,
				columnMode:true
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
			}
			
	};	
		
	/*
	 * affiche processProperties
	 */
	var setColomns = function(experiment){
		if(experiment.state.code === "N"){
			console.log("experiment.state.code " + experiment.state.code);
			datatableConfig.columns.push({
			   	 "header":Messages("containers.table.sampleAnalyseTypes"),
			   	 "property":"inputContainerUsed.contents[0].processProperties.analyseType.value",
			   	 "order":true,
			   	 "edit":false,
			   	 "hide":true,
			   	 "type":"text",
			   	 "position":390,
			   	 "extraHeaders":{0:Messages("experiments.inputs")}
		    });
			datatableConfig.columns.push({
	        	"header":Messages("containers.table.comments"),
	 			"property": "inputContainerUsed.contents[0].processComments[0].comment",
	 			"order":true,
			   	"edit":false,
	 			"hide":true,
	 			"type":"text",
	 			"position":3,
	 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
	        	"extraHeaders":{0:Messages("experiments.inputs")}
			});
		}else{
			console.log("experiment.state.code " + experiment.state.code);
			datatableConfig.columns.push({
			   	 "header":Messages("containers.table.sampleAnalyseTypes"),
			   	 "property":"outputContainerUsed.contents[0].processProperties.analyseType.value",
			   	 "order":true,
					 "edit":false,
					 "hide":true,
					 "type":"text",
			   	 "position":390,
			   	 "extraHeaders":{0:Messages("experiments.outputs")}
			 });
			datatableConfig.columns.push({
	        	"header":Messages("containers.table.comments"),
	 			"property": "outputContainerUsed.contents[0].processComments[0].comment",
	 			"order":true,
			   	"edit":false,
	 			"hide":true,
	 			"type":"text",
	 			"position":410,
	 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
	        	"extraHeaders":{0:Messages("experiments.outputs")}
			});
		}
	}
	
	setColomns($scope.experiment);
	   
	$scope.dragInProgress=function(value){
		$scope.dragIt=value;
	};
	
	$scope.getDroppableClass=function(){
		if($scope.dragIt){
			return "dropZoneHover";
		}else{
			return "";
		}
	}
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save on tubes-to-flowcell");
		validateProcessProperties($scope.experiment);
		$scope.atmService.viewToExperiment($scope.experiment);
		console.log("avant updateConcentration");
		$scope.updateConcentration($scope.experiment);
		console.log("apres updateConcentration");
		$scope.$emit('childSaved', callbackFunction);
		addReagents();
	});

	var copyFlowcellCodeToDT = function(datatable){
		
		var dataMain = datatable.getData();
		//copy flowcell code to output code
		var codeFlowcell = $parse("instrumentProperties.containerSupportCode.value")($scope.experiment);
		if(null != codeFlowcell && undefined != codeFlowcell){
			for(var i = 0; i < dataMain.length; i++){
				var atm = dataMain[i].atomicTransfertMethod;
				var containerCode = codeFlowcell;
				if($scope.rows.length > 1){ //other than flowcell 1
					containerCode = codeFlowcell+"_"+atm.line;
				}
				$parse('outputContainerUsed.code').assign(dataMain[i],containerCode);
				$parse('outputContainerUsed.locationOnContainerSupport.code').assign(dataMain[i],codeFlowcell);
			}				
			//datatable.setData(dataMain);
		}
		
	}
	
	
	
	var validateProcessProperties = function(experiment) {
		if(experiment.state.code === "N"){
			for(var j = 0 ; j < experiment.atomicTransfertMethods.length && experiment.atomicTransfertMethods != null; j++){
				var atm = experiment.atomicTransfertMethods[j];
				var concentration = undefined;
				for(var i=0;i < atm.inputContainerUseds.length;i++){
					var inputContainerUsed = atm.inputContainerUseds[i];
					for(var cn=0;cn < inputContainerUsed.contents.length;cn++){
						var content = inputContainerUsed.contents[cn];
						if(content.processProperties){
							console.log("validateProcessProperties OK " + JSON.stringify(content.processProperties));							
						}else{
							console.log("validateProcessProperties KO " + JSON.stringify(content));

				    		$scope.messages.clazz = "alert alert-danger";
				    		$scope.messages.text = Messages("Pas de processProperties pour " + content.sampleCode);
				    		$scope.messages.showDetails = false;
				    		$scope.messages.open();	
						}
					}
				}
			}			
		}
		
	}
	
	
	/**
	 * Update concentration of output
	 */
	$scope.updateConcentration = function(experiment){
		console.log("updateConcentration in");
		for(var j = 0 ; j < experiment.atomicTransfertMethods.length && experiment.atomicTransfertMethods != null; j++){
			var atm = experiment.atomicTransfertMethods[j];
			var concentration = undefined;
			for(var i=0;i < atm.inputContainerUseds.length;i++){
				var inputContainerUsed = atm.inputContainerUseds[i];
				if(inputContainerUsed.experimentProperties.finalConcentration!=undefined && inputContainerUsed.experimentProperties.finalConcentration!=null){
					console.log("updateConcentration experimentProperties - " + inputContainerUsed.code + " : " + JSON.stringify(inputContainerUsed.experimentProperties.finalConcentration.value));
					
					if(inputContainerUsed.experimentProperties && inputContainerUsed.experimentProperties.volumeFinalPhiX){
						console.log("updateConcentration inputContainerUsed.experimentProperties.finalConcentration.value - " + inputContainerUsed.experimentProperties.finalConcentration.value);
						concentration = inputContainerUsed.experimentProperties.finalConcentration.value;
						console.log("Concentration = " + inputContainerUsed.experimentProperties.finalConcentration.value);
	//					console.log(JSON.stringify(inputContainerUsed.experimentProperties));
						if (!atm.outputContainerUseds[0].concentration){
							var setter = $parse("atm.outputContainerUseds[0].concentration.value").assign;
							setter(atm, concentration);
						}
						atm.outputContainerUseds[0].concentration.value = Math.round(concentration);
						atm.outputContainerUseds[0].concentration.unit = "pM";
	//					atm.outputContainerUseds[0].concentration.value = concentration;
						
					}//if
					
				}
			}//for
		}//for
	};
	
	
//add reagents into new experiment
	var addReagents = function() {
//TODO	
/*
		if($parse('experiment.state.code')($scope) === "N" && $scope.experiment.reagents.length === 0 && $parse('experiment.instrumentProperties.sequencingProgramType')($scope)){
			 var lectureType = $scope.experiment.instrumentProperties.sequencingProgramType.value;
	//		 console.log("Type lectures : " + JSON.stringify($scope.experiment.instrumentProperties));
			 console.log("Exp.state : " + $parse('experiment.state.code')($scope) + ", type lecture : " + lectureType);
			 if (lectureType === "PE"){
				 var ReagentUseds = [
				                     {
				                         "kitCatalogCode": "24SA5FUL0",
				                         "boxCatalogCode": "24SB1MUK4",
				                         "reagentCatalogCode": "24SB2EOEC" 
				                      },
				                       {
				                         "kitCatalogCode": "24SA5FUL0",
				                         "boxCatalogCode": "24SC2HEKT" 
				                      },
				                       {
				                         "kitCatalogCode": "24SA5FUL0",
				                         "boxCatalogCode": "24SC2JW11" 
				                      },
				                      {
				                          "kitCatalogCode": "26K91I1IF",
				                          "boxCatalogCode": "26K91I25Z" 
				                       } 
				                    ];
				 
			 }
			 else if(lectureType === "SR"){
				 var ReagentUseds = [
				                  {
			                         "kitCatalogCode": "24SC16VM8",
			                         "boxCatalogCode": "24SC19ECW",
			                         "reagentCatalogCode": "24SC1VKL2" 
			                      },
			                       {
			                         "kitCatalogCode": "24SC16VM8",
			                         "boxCatalogCode": "24SC1F18X" 
			                      },
			                       {
			                         "kitCatalogCode": "24SC16VM8",
			                         "boxCatalogCode": "24SC1HQNY" 
			                      },
			                      {
			                          "kitCatalogCode": "26K91I1IF",
			                          "boxCatalogCode": "26K91I25Z" 
			                       } 
			                    ];
			 }
			 
			 $scope.experiment.reagents = ReagentUseds;
			 $scope.$emit('askRefreshReagents');
		 }
	*/
    }
	
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");
		
		var dtConfig = $scope.atmService.data.$atmToSingleDatatable.data.getConfig();

		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP'));
		dtConfig.edit.byDefault = false;
		$scope.atmService.data.$atmToSingleDatatable.data.setConfig(dtConfig);
//		$scope.atmService.viewToExperiment($scope.experiment);
		 
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
	
	
	$scope.$on('cancel', function(e) {
		console.log("call event cancel");
		$scope.atmService.data.$atmToSingleDatatable.data.cancel();
				
	});
	
		
	$scope.$on('activeEditMode', function(e) {
		
		validateProcessProperties($scope.experiment);
		console.log("call event activeEditMode");
		$scope.atmService.data.$atmToSingleDatatable.data.selectAll(true);
		$scope.atmService.data.$atmToSingleDatatable.data.setEdit();
	});
		
	//To display sample and tag in one cell
	$scope.getSampleAndTags = function(container){
		var sampleCodeAndTags = [];
		angular.forEach(container.contents, function(content){
			if(content.properties.tag != undefined && content.sampleCode != undefined){
				sampleCodeAndTags.push(content.sampleCode+" / "+content.properties.tag.value);
			}
		});
		return sampleCodeAndTags;
	};

	$scope.getDisplayMode = function(atm, rowIndex){
		
		if(atm && atm.inputContainerUseds && atm.inputContainerUseds.length === 0){
			return "empty";
		}else if(atm && atm.inputContainerUseds && atm.inputContainerUseds.length > 0 && $scope.rows[rowIndex]){
			return "open";
		}else{
			return "compact";
		}		
	};
	
	$scope.isAllOpen = true;
	if(!$scope.isCreationMode()){
		$scope.isAllOpen = false;
	}
	
	//TODO used container_support_category in future
	//init number of lane
	var cscCode = $parse('experiment.instrument.outContainerSupportCategoryCode')($scope);
	$scope.rows = [];
	var laneCount = 0;
	if(cscCode !== undefined){
		laneCount = Number(cscCode.split("-",2)[1]);
		$scope.rows = new Array(laneCount);
		for(var i = 0; i < laneCount; i++){
			$scope.rows[i] = $scope.isAllOpen;
		}
		
	}
	
	$scope.hideRowAll = function(){
		for (var i=0; i<$scope.rows.length;i++){	
			$scope.rows[i] = false;
		}	    
		$scope.isAllOpen = false;	    
	};

	$scope.showRowAll = function(){
		for (var i=0; i<$scope.rows.length;i++){	
			$scope.rows[i] = true;
		}	    
		$scope.isAllOpen = true;
	};
	
	$scope.toggleRow = function(rowIndex){
		$scope.rows[rowIndex] = !$scope.rows[rowIndex];
	};
	
	
	//init global ContainerOut Properties outside datatable
	$scope.outputContainerProperties = $filter('filter')($scope.experimentType.propertiesDefinitions, 'ContainerOut');
	$scope.outputContainerValues = {};
	
	$scope.updateAllOutputContainerProperty = function(property){
		console.log("updateAllOutputContainerProperty");
		var value = $scope.outputContainerValues[property.code];
		var setter = $parse("outputContainerUseds[0].experimentProperties."+property.code+".value").assign;
		for(var i = 0 ; i < $scope.atmService.data.atm.length ; i++){
			var atm = $scope.atmService.data.atm[i];
			if(atm.inputContainerUseds.length > 0){
				setter(atm, value);
			}			
		}
		$scope.changeValueOnFlowcellDesign();
	};
	
	$scope.changeValueOnFlowcellDesign = function(){
		$scope.atmService.data.updateDatatable();
	};
	
	
	//init atmService
	var atmService = atmToDragNDrop($scope, laneCount, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(line){
		return {
			class:"ManyToOne",
			line:line, 
			column:"1", 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};		
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL",
			concentration:"nM"
	}
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
	
	
	
	
	/////////////////////

	
	var generateSampleSheet = function(){		
		$scope.fileUtils.generateSampleSheet("":"");
//		$scope.messages.clear();
//		$http.post(jsRoutes.controllers.instruments.io.IO.generateFile($scope.experiment.code).url,{})
//		.success(function(data, status, headers, config) {
//			var header = headers("Content-disposition");
//			var filepath = header.split("filename=")[1];
//			console.log(filepath);
//			var filename = filepath.split(/\/|\\/);
//			filename = filename[filename.length-1];
//			if(data!=null){
//				$scope.messages.clazz="alert alert-success";
//				$scope.messages.text=Messages('experiments.msg.generateSampleSheet.success')+" : "+filepath;
//				$scope.messages.showDetails = false;
//				$scope.messages.open();	
//				
//				var blob = new Blob([data], {type: "text/plain;charset=utf-8"});    					
//				saveAs(blob, filename);
//			}
//		})
//		.error(function(data, status, headers, config) {
//			$scope.messages.clazz = "alert alert-danger";
//			$scope.messages.text = Messages('experiments.msg.generateSampleSheet.error');
//			$scope.messages.setDetails(data);
//			$scope.messages.showDetails = true;
//			$scope.messages.open();				
//		});
	};

	$scope.setAdditionnalButtons([{
		isDisabled : function(){return $scope.isCreationMode();},
		isShow:function(){return true},
		click:generateSampleSheet,
		label:Messages("experiments.fdr")
	}]);

	if ($parse('experiment.state.code')($scope) === "N" && !$scope.mainService.isEditMode()){
		$scope.activeEditMode();
	}
}]);

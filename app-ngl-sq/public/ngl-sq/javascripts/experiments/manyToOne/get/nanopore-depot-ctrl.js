angular.module('home').controller('GETNanoporeDepotCtrl',['$scope', '$parse', '$filter','$http', 'atmToDragNDrop','datatable', 'atmToSingleDatatable',
                                                               function($scope, $parse, $filter,$http, atmToDragNDrop, datatable, atmToSingleDatatable) {
	
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
						 "mergeCells":true,
						 "extraHeaders":{0:Messages("experiments.inputs")}
					},
//					 {
//			        	 "header":Messages("containers.table.sampleCodes"),
//			        	 "property":"inputContainerUsed.contents[0].properties.Nom_echantillon_collaborateur.value",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//			        	 "type":"text",
//			        	 "position":1,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },	

			         {
					        "header":Messages("property_definition.Nom_echantillon_collaborateur"),
		                     "property": "inputContainer.contents",
		                     "filter": "getArray:'referenceCollab'| unique",
		//                   "filter": "getArray:'properties.Nom_echantillon_collaborateur.value'| unique",
		                     "order":true,
		                     "hide":true,
		                     "type":"text",
		                     "position":1,
		                     "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			                "extraHeaders":{0:Messages("experiments.inputs")}
			           },

//			         {
//			        	 "header":Messages("containers.table.code"),
//			        	 "property":"inputContainerUsed.code",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//			        	 "type":"text",
//			        	 "position":1,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },
//			         {
//			        	 "header":Messages("containers.table.sampleTypes"),
//			        	 "property":"inputContainerUsed.contents[0].properties.type_echantillon.value",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//			        	 "type":"text", 
//			        	 "position":1,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },
//			         {
//			        	 "header":Messages("containers.table.nomPool"),
//			        	 "property":"inputContainerUsed.contents[0].properties.Nom_pool_sequencage.value",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":false,
//			        	 "type":"text",
//			        	 "position":1,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },
//			         //afficher type de librairie
//			         {
//			        	 "header":Messages("containers.table.libProcessTypeCodes"),
//			        	 "property":"inputContainer.properties.Type_librairie.value",
//			        	 "order":true,
//						 "edit":true,
//						 "choiceInList":true,
//						 "possibleValues":[{"code":"Amplicon","name":"Amplicon"},
//						                   {"code":"Bisulfite-DNA","name":"Bisulfite-DNA"},
//						                   {"code":"ChIP-Seq","name":"ChIP-Seq"},
//						                   {"code":"DNA","name":"DNA"},
//						                   {"code":"DNA-MP","name":"DNA-MP"},
//						                   {"code":"MeDIP-Seq","name":"MeDIP-Seq"},
//						                   {"code":"RAD-Seq","name":"RAD-Seq"},
//						                   {"code":"ReadyToLoad","name":"ReadyToLoad"},
//						                   {"code":"RNA-Stranded","name":"RNA-Stranded"},
//						                   {"code":"10X","name":"10X"},
//						                   {"code":"16S","name":"16S"}],
//						 "required":true,
//						 "hide":true,
//			        	 "type":"text",
//			        	 "position":1,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },
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
			        	 "header":Messages("containers.table.supportCode"),
			        	 "property":"inputContainer.support.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":3,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         // concentration Qubit
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
			         // taille insert
			         {
			        	 "header":Messages("containers.table.size"),
			        	 "property":"inputContainerUsed.size.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":4,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
//			         {
//			        	 "header":Messages("containers.table.concentration.unit"),
//			        	 "property":"inputContainerUsed.concentration.unit",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":false,
//			        	 "type":"text",
//			        	 "position":5.5,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },
//			         {
//			        	 "header":Messages("containers.table.tagCategory"),
//			        	 "property":"inputContainerUsed.contents[0].properties.tagCategory.value",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":false,
//			        	 "type":"text",
//			        	 "position":6,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },
//			         {
//			        	 "header":Messages("containers.table.state.code"),
//			        	 "property":"inputContainer.state.code",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":false,
//			        	 "type":"text",
//						 "filter":"codes:'state'",
//			        	 "position":45,
//			        	 "extraHeaders":{0:Messages("experiments.inputs")}
//			         },
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
						 "type":"text",
			        	 "position":40,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },		         					 
//					 {
//			        	 "header":Messages("containers.table.concentration"),
//			        	 "property":"outputContainerUsed.concentration.value",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//			        	 "type":"number",
//			        	 "position":405,
//			        	 "extraHeaders":{0:Messages("experiments.outputs")}
//			         },
//			         {
//			        	 "header":Messages("containers.table.concentration.unit"),
//			        	 "property":"outputContainerUsed.concentration.unit",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":false,
//			        	 "type":"text",
//			        	 "position":405.5,
//			        	 "extraHeaders":{0:Messages("experiments.outputs")}
//			         },
//			         {
//			        	 "header":Messages("containers.table.stateCode"),
//			        	 "property":"outputContainer.state.code | codes:'state'",
//			        	 "order":true,
//						 "edit":false,
//						 "hide":true,
//						 "type":"text",
//			        	 "position":502,
//			        	 "extraHeaders":{0:Messages("experiments.outputs")}
//			         }
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
//				by:"atomicTransfertMethod.line"
				by:"atomicTransfertMethod.viewIndex"
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
//				callback:function(datatable){
//					copyFlowcellCodeToDT(datatable);
//				}
			},
			hide:{
				active:true
			},
			mergeCells:{
	        	active:true 
	        },
			
			edit:{
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
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
//	var setColomns = function(experiment){
//		if(experiment.state.code === "N"){
//			console.log("setColomns experiment.state.code " + experiment.state.code);
//			datatableConfig.columns.push({
//			   	 "header":Messages("containers.table.sampleAnalyseTypes"),
//			   	 "property":"inputContainerUsed.contents[0].processProperties.analyseType.value",
//			   	 "order":true,
//			   	 "edit":false,
//			   	 "hide":true,
//			   	 "type":"text",
//			   	 "position":390,
//			   	 "extraHeaders":{0:Messages("experiments.inputs")}
//		    });
//			datatableConfig.columns.push({
//	        	"header":Messages("containers.table.comments"),
//	 			"property": "inputContainerUsed.contents[0].processComments[0].comment",
//	 			"order":true,
//			   	"edit":false,
//	 			"hide":true,
//	 			"type":"text",
//	 			"position":3,
//	 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
//	        	"extraHeaders":{0:Messages("experiments.inputs")}
//			});
//		}else{
//			console.log("experiment.state.code " + experiment.state.code);
//			datatableConfig.columns.push({
//			   	 "header":Messages("containers.table.sampleAnalyseTypes"),
//			   	 "property":"outputContainerUsed.contents[0].processProperties.analyseType.value",
//			   	 "order":true,
//					 "edit":false,
//					 "hide":true,
//					 "type":"text",
//			   	 "position":390,
//			   	 "extraHeaders":{0:Messages("experiments.outputs")}
//			 });
//			datatableConfig.columns.push({
//	        	"header":Messages("containers.table.comments"),
//	 			"property": "outputContainerUsed.contents[0].processComments[0].comment",
//	 			"order":true,
//			   	"edit":false,
//	 			"hide":true,
//	 			"type":"text",
//	 			"position":410,
//	 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
//	        	"extraHeaders":{0:Messages("experiments.outputs")}
//			});
//		}
//	}
	
//	setColomns($scope.experiment);
	
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
		//console.log("call event save on nanopore-depot");
//		validateProcessProperties($scope.experiment);
		$scope.atmService.viewToExperiment($scope.experiment);
//		$scope.updateConcentration($scope.experiment);
		$scope.$emit('childSaved', callbackFunction);
//		addReagents();
	});

	
//	var copyFlowcellCodeToDT = function(datatable){
//		//console.log("copyFlowcellCodeToDT");
//		var dataMain = datatable.getData();
//		//console.log("dataMain : " + JSON.stringify(dataMain));
//		//copy flowcell code to output code
//		var codeFlowcell = $parse("instrumentProperties.containerSupportCode.value")($scope.experiment);
//		console.log("codeFlowcell : " + codeFlowcell);
//		if(null != codeFlowcell && undefined != codeFlowcell){
//			console.log("dans le if");
//			for(var i = 0; i < dataMain.length; i++){
//				var atm = dataMain[i].atomicTransfertMethod;
//				var containerCode = codeFlowcell;
//				if($scope.rows.length > 1){ //other than flowcell 1
//					containerCode = codeFlowcell+"_"+atm.line;
//				}
//				console.log("dataMain apres : " + JSON.stringify(dataMain[i]) + " , containerCode : " + containerCode);
//				$parse('outputContainerUsed.code').assign(dataMain[i],containerCode);
//				$parse('outputContainerUsed.locationOnContainerSupport.code').assign(dataMain[i],codeFlowcell);
//			}				
//			//datatable.setData(dataMain);
//		}
//		
//	}
	
	
//	// Generate run name automatically
//	var generateRunName = function(experiment){
//		var runName = experiment.experimentProperties.nom_run.value;
//		console.log(runName);
//		// Insérer les règles de construction du nom du run
//		$scope.experiment.experimentProperties.nom_run.value = "nom de run";
//	};

	
	$scope.$on('refresh', function(e) {
		//console.log("test longueur refresh : " + JSON.stringify($scope.atmService.data.atm[0].inputContainerUseds.length));
		
		var dtConfig = $scope.atmService.data.$atmToSingleDatatable.data.getConfig();

		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
		dtConfig.edit.byDefault = false;
		$scope.atmService.data.$atmToSingleDatatable.data.setConfig(dtConfig);
//		$scope.atmService.viewToExperiment($scope.experiment);
		 
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
	
	
	$scope.$on('cancel', function(e) {
		$scope.atmService.data.$atmToSingleDatatable.data.cancel();
	});
	
		
	$scope.$on('activeEditMode', function(e) {
//		validateProcessProperties($scope.experiment);
		//console.log("call event activeEditMode");
		$scope.atmService.data.$atmToSingleDatatable.data.selectAll(true);
		//console.log("call event activeEditMode - selectAll");
		$scope.atmService.data.$atmToSingleDatatable.data.setEdit();
		//console.log("call event activeEditMode - setEdit");
		//console.log("test longueur active : " + JSON.stringify($scope.atmService.data.atm[0].inputContainerUseds.length));
	});
		
	//To display sample and tag in one cell
	$scope.getSampleAndTags = function(container){
//		console.log("getSampleAndTags");
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
//			return "compact";
			return "open";
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
		laneCount = 1;
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
		//console.log("updateAllOutputContainerProperty");
		var value = $scope.outputContainerValues[property.code];
		var setter = $parse("outputContainerUseds[0].experimentProperties."+property.code+".value").assign;
		//console.log("setter : " + setter);
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
	var cscCode = $parse('experiment.instrument.outContainerSupportCategoryCode')($scope);
	$scope.rows = [];
	var laneCount = 0;
	if(cscCode !== undefined){
		switch(cscCode){
			case "flowcell_gd" : 
				laneCount = "5";
				break;
			case "flowcell_pt" :
				laneCount = "24";
				break;
			default:
				console.log("Problème de nom de FC");
		}
		$scope.rows = new Array(laneCount);
		for(var i = 0; i < laneCount; i++){
			$scope.rows[i] = $scope.isAllOpen;
		}	
	}
	
	var atmService = atmToDragNDrop($scope, laneCount, datatableConfig);
	
	//defined new atomictransfertMethod
	//var atmsNbr = 0;	
	atmService.newAtomicTransfertMethod = function(line){
		return {
			class:"ManyToOne",
			line:line, 
			column:"1", 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0),
			//viewIndex: atmsNbr+1
		};		
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL",
			concentration:"nM"
	}
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
	
	// tester comment récupérer le nombre de containers en entrée
	//console.log("test longueur : " + JSON.stringify($scope.atmService));
	
	//Récupérer la liste des emplacements et types FC en fonction de la machine
	$scope.getFCpositions = function() {
		$http.get(jsRoutes.controllers.commons.api.Parameters.list().url,{params:{typeCode:"map-parameter"}})
		//$http.get(jsRoutes.controllers.commons.api.Parameters.get("map-parameter", "GridionPositionList").url,{params:{typeCode:"map-parameter"}})
		   .success(function(data, status, headers, config) {
		    var positions = [];
		    var FCtypes = [];
		    data.forEach(function(mapParameter){
			    if($scope.experiment.instrument.typeCode == "GridION") {
			    	if (mapParameter.code.indexOf("GridionPosition") !== -1){
				    	for (var value in mapParameter.map){
				    		positions.push({"code":value,"name":value});
				    	}
			    	}
			    	else if(mapParameter.code.indexOf("GridionFCtypes") !== -1){
			    		for (var types in mapParameter.map){
			    			FCtypes.push({"code":mapParameter.map[types],"name":mapParameter.map[types]});
				    	}
			    	}
				}
			    else if ($scope.experiment.instrument.typeCode == "PromethION") {
			    	if(mapParameter.code.indexOf("PromethionPosition") !== -1){
				    	for (var value in mapParameter.map){
				    		positions.push({"code":value,"name":value});
				    	}
				    }
			    	else if(mapParameter.code.indexOf("PromethionFCtypes") !== -1){
			    		for (var types in mapParameter.map){
			    			FCtypes.push({"code":mapParameter.map[types],"name":mapParameter.map[types]});
				    	}
			    	}
			    }
		    });
		    var columns = $scope.atmService.$atmToSingleDatatable.data.getColumnsConfig();
		    columns.forEach(function(tab){
		    	if(tab.header === "Emp. FC"){
		    		//console.log("tabTag : " + JSON.stringify(tab.header));
		    		tab.possibleValues = positions;
		    		tab.choiceInList=true;
		    	}
		    	else if(tab.header === "Type FC"){
		    		//console.log("tabTag : " + JSON.stringify(tab.header));
		    		tab.possibleValues = FCtypes;
		    		tab.choiceInList=true;
		    	}
		    });
		    $scope.atmService.$atmToSingleDatatable.data.setColumnsConfig(columns);
		    	
		    });
		};
	
	// Récupération de la liste des tags ONT
	$scope.getONTindexes = function() {
		//console.log("test longueur : " + JSON.stringify($scope.atmService.data.atm[0].inputContainerUseds.length));
		var atm = $scope.atmService.data.atm;
		for(var size = 0 ; size < atm.length ; size++){
			if(JSON.stringify(atm[size].inputContainerUseds.length) > 1 ){
				$http.get(jsRoutes.controllers.commons.api.Parameters.list().url,{params:{typeCode:"index-nanopore-sequencing"}})
			    .success(function(data, status, headers, config) {
			    	var tags = [];
			    	data.forEach(function(tag){
			    		tags.push({"code":tag.code,"name":tag.name}); 
					});
			    	//console.log(tags);
			    	var columns = $scope.atmService.$atmToSingleDatatable.data.getColumnsConfig();
			    	columns.forEach(function(tab){
			    		if(tab.header === "Tag"){
			    			//console.log("tabTag : " + JSON.stringify(tab.header));
			    			tab.possibleValues = tags;
			    			tab.choiceInList=true;
			    		}
			    	});
			    	$scope.atmService.$atmToSingleDatatable.data.setColumnsConfig(columns);
			    	
			    });
			}
//			else{
//		    	var columns = $scope.atmService.$atmToSingleDatatable.data.getColumnsConfig();
//		    	columns.forEach(function(tab){
//		    		if(tab.header === "Tag"){
//		    			//console.log("tabTag : " + JSON.stringify(tab.header));
//		    			tab.edit=false;
//		    		}
//		    	});
//		    	$scope.atmService.$atmToSingleDatatable.data.setColumnsConfig(columns);
//				
//			}
		}
	};
	
//	$scope.createNewATM = function(){
////		var atmService = atmToSingleDatatable($scope, datatableConfig);
////		var atmsNbr = $scope.atmService.data.atm.length;
//		if ($scope.atmService.data !== undefined){
//			atmsNbr = $scope.atmService.data.atm.length;
//		}
//		$scope.atmService.data.atm.push(
//			{
//				class:"ManyToOne",
//				line:"1", 
//				column:"1", 				
//				inputContainerUseds:new Array(0), 
//				outputContainerUseds:new Array(0),
//				viewIndex: atmsNbr+1
//			}
//		);
		
//		atmService.newAtomicTransfertMethod;
//		atmService.experimentToView($scope.experiment, $scope.experimentType);
//		console.log("service : " + JSON.stringify($scope.atmService.data.atm));
//		console.log("experiment : " + JSON.stringify($scope.experiment));
//		$scope.atmService.data.$atmToSingleDatatable.data.setConfig(dtConfig);
		//$scope.atmService.data.addNewAtomicTransfertMethodsInDatatable();
		
//		atmService.addNewAtomicTransfertMethodsInDatatable = function(){
//			if(null != mainService.getBasket() && null != mainService.getBasket().get() && this.isAddNew){
//				$that = this;
//				
//				var type = $that.newAtomicTransfertMethod().class;
//				
//				$that.$commonATM.loadInputContainerFromBasket(mainService.getBasket().get())
//					.then(function(containers) {								
//						var allData = [], i = 0;
//						
//						if($that.data.getData() !== undefined && $that.data.getData().length > 0){
//							allData = $that.data.getData();
//							i = allData.length;
//						}
//						
//						angular.forEach(containers, function(container){
//							var tmpLine = {};
//							tmpLine.atomicTransfertMethod = $that.newAtomicTransfertMethod(container.support.line, container.support.column);
//							tmpLine.atomicIndex=i++;
//								
//							tmpLine.inputContainer = container;
//							tmpLine.inputContainerUsed = $that.$commonATM.convertContainerToInputContainerUsed(tmpLine.inputContainer);
//							
//							for(var j = 0; j < $scope.experimentType.sampleTypes.length ; j++){
//								var line = {};
//								line.atomicTransfertMethod = tmpLine.atomicTransfertMethod;
//								line.atomicIndex = tmpLine.atomicIndex;
//								line.inputContainer = tmpLine.inputContainer;
//								line.inputContainerUsed = tmpLine.inputContainerUsed;
//								line.outputContainerUsed = $that.$commonATM.newOutputContainerUsed($that.defaultOutputUnit,$that.defaultOutputValue,line.atomicTransfertMethod.line,
//										line.atomicTransfertMethod.column,line.inputContainer);
//								
//								var value = $scope.experimentType.sampleTypes[j].code;
//								var setter = $parse("experimentProperties.sampleTypeCode.value").assign;
//								setter(line.outputContainerUsed, value);
//								
//								line.outputContainer = undefined;
//								allData.push(line);
//							}						
//						});
//						
//						allData = $filter('orderBy')(allData,'inputContainer.support.code');
//						$that.data.setData(allData, allData.length);											
//				});
//			}					
//		};
//	};
	
	/////////////////////
	var generateSampleSheetPerATM = function(){
		var atm = $scope.atmService.data.atm;
		for(var atmsize = 0 ; atmsize < atm.length ; atmsize++){
			for(var outputsize = 0 ; outputsize < atm[atmsize].outputContainerUseds.length ; outputsize++){
				outputContainerUsed = atm[atmsize].outputContainerUseds[outputsize];
				if(outputContainerUsed.code !== undefined){
					console.log("nb atm : " + outputContainerUsed.code);
					generateSampleSheet(outputContainerUsed.code);
				}
			}
		}
	}
	
	
	var generateSampleSheet = function(outputCode){	
		console.log("sample sheet");
		$scope.fileUtils.generateSampleSheet({"outputCode":outputCode});	
//		$scope.messages.clear();
		$http.post(jsRoutes.controllers.instruments.io.IO.generateFile($scope.experiment.code).url,{'outputCode':outputCode})
		.success(function(data, status, headers, config) {
			var header = headers("Content-disposition");
			var filepath = header.split("filename=")[1];
			console.log(filepath);
			var filename = filepath.split(/\/|\\/);
			filename = filename[filename.length-1];
			if(data!=null){
				$scope.messages.clazz="alert alert-success";
				$scope.messages.text=Messages('experiments.msg.generateSampleSheet.success')+" : "+filepath;
				$scope.messages.showDetails = false;
				$scope.messages.open();	
				
				var blob = new Blob([data], {type: "text/plain;charset=utf-8"});    					
				saveAs(blob, filename);
			}
		})
		.error(function(data, status, headers, config) {
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages('experiments.msg.generateSampleSheet.error');
			$scope.messages.setDetails(data);
			$scope.messages.showDetails = true;
			$scope.messages.open();				
		});
	};
	
	var allPropertiesGood;
	$scope.displaySampleSheet = function () {
//		// Vérifier si toutes les propriétés sont renseignées avant d'afficher le bouton "Feuille de route"
		allPropertiesGood = false;
		
//		// emplacement FC
//		if (($parse("instrumentProperties.empl_FC.value")($scope.experiment)) === undefined){
//			allPropertiesGood = true;
//		}
//		// sampleID
//		if (($parse("experimentProperties.sampleID.value")($scope.experiment)) === undefined){
//			allPropertiesGood = true;
//		}
		
		// properties containerOut
//		if ($scope.atmService.data.atm[0].outputContainerUseds[0].experimentProperties === null){
//			allPropertiesGood = true;
//		}
		// comment faire pour afficher la liste de toutes les propriétés quand elles ne sont pas renseignées ?
		// containerOut properties (marche pas) $parse("outputContainerUseds[0].experimentProperties."+property.code+".value").assign; ?
		
		// reagents
		if ($scope.experiment.reagents.length === 0){
			//console.log("taille reactif : " + $scope.experiment.reagents.length);
			allPropertiesGood = true;
		}
		
		//console.log("allPropertiesGood : " + allPropertiesGood);
		return allPropertiesGood;
	}
	
	$scope.setAdditionnalButtons([{
//		isDisabled : function(){return $scope.isCreationMode();},		
		isDisabled : function(){
			if ($parse('experiment.state.code')($scope) === "N"){
				return true;
			}
			else {
				if ($scope.displaySampleSheet()){
					return true;
				}
				else {
					return false;
				}
			}
		},
		isShow:function(){return true},
		click:generateSampleSheetPerATM,
		label:Messages("experiments.fdr")
	}]);
	

	if ($parse('experiment.state.code')($scope) === "N" && !$scope.mainService.isEditMode()){
		console.log("final if");
		$scope.activeEditMode();
	}
}]);

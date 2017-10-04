// FDS 04/10/2017 -- JIRA NGL-1584: denaturation en tubes et plaques
//                                  reprendre ce que faisait l'ancien controller pour tubes uniquement...

angular.module('home').controller('DenatDilLibCtrlPlates',['$scope', '$parse', 'atmToSingleDatatable',
                                                     function($scope, $parse, atmToSingleDatatable){

	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	// NGL-1055: name explicite pour fichier CSV exporté: typeCode experience
	// NGL-1055: mettre getArray et codes:'' dans filter et pas dans render
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),
			columns:[
			         //--------------------- INPUT containers section -----------------------
			         
					  /* plus parlant pour l'utilisateur d'avoir Plate barcode | line | column{
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.support.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },	
			         { // barcode plaque entree == input support Container code
			        	 "header":Messages("containers.table.support.name"),
			        	 "property":"inputContainer.support.code",
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },
			         { // Ligne
			        	 "header":Messages("containers.table.support.line"),
			        	 "property":"inputContainer.support.line",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },
			         { // colonne
			        	 "header":Messages("containers.table.support.column"),
				         // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
			        	 "property":"inputContainer.support.column*1",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":3,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },	
			         */
			         { // Projet(s)
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainer.projectCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":4,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0: inputExtraHeaders}
				     },
				     { // Echantillon(s) 
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":5,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0: inputExtraHeaders}
				     },
				     { //sample Aliquots
			        	"header":Messages("containers.table.codeAliquot"),
			 			"property": "inputContainer.contents",
			 			"filter": "getArray:'properties.sampleAliquoteCode.value'| unique",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":6,
			 			"render": "<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0: inputExtraHeaders}
				     },
			         { // libProcessType ajout 04/10/2017
						 "header":Messages("containers.table.libProcessType"),
						 "property": "inputContainer.contents",
						 //"filter": "getArray:'properties.libProcessTypeCode.value'| codes:'libProcessTypeCode'",.. peut on decoder ???? 
						 "filter": "getArray:'properties.libProcessTypeCode.value'| unique",
						 "order":false,
						 "hide":true,
						 "type":"text",
						 "position":6.5,
						 "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
						 "extraHeaders": {0: inputExtraHeaders}	 						 			
			         },
			         { //Tags
			        	"header":Messages("containers.table.tags"),
			 			"property": "inputContainer.contents",
			 			"filter": "getArray:'properties.tag.value'| unique",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":7,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0: inputExtraHeaders}
			         },				 
					 {  //Concentration en nM;
			        	 "header":Messages("containers.table.concentration") + " (nM)",
			        	 "property":"inputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":8,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },
			        /* { // volume pas necessaire ????
			        	 "header":function(){return Messages("containers.table.volume") + " (µL)"},
			        	 "property":"volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":9,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },*/
			         {  // Etat input Container
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code",
						 "filter":"codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":10,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },
			         // colonnes specifiques experience viennent s'insererer ici s'il y en a
			          
			         //------------------------ OUTPUT containers section -------------------
			         /* { // barcode plaque sortie == support Container used code... faut Used 
			        	 "header":Messages("containers.table.support.name"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.code", 
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":100,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },
			         { // Line
			        	 "header":Messages("containers.table.support.line"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.line",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":110,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         { // column
			        	 "header":Messages("containers.table.support.column"),
			        	 // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
			        	 "property":"outputContainerUsed.locationOnContainerSupport.column*1",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":120,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },	
			         */
			         { // Concentration shortLabel en pM;
			        	 "header":Messages("containers.table.concentration.shortLabel") + " (pM)",
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "convertValue": {"active":true, "displayMeasureValue":"pM", "saveMeasureValue":"nM"},			        	 
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 //"defaultValues":10,
			        	 "position":130,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         { // volume en uL
			        	 "header":Messages("containers.table.volume")+ " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":140,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         { 
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":500,
			        	 "extraHeaders":{0:outputExtraHeaders}
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
				mode:'local',
				active:true,
				by:'inputContainer.code'
			},
			remove:{
				active: ($scope.isEditModeAvailable() && $scope.isNewState()),
				showButton: ($scope.isEditModeAvailable() && $scope.isNewState()),
				mode:'local'
			},
			save:{
				active:true,
	        	withoutEdit: true,
	        	changeClass:false,
	        	showButton:false,
	        	mode:'local',
	        	callback:function(datatable){
	        		copyContainerSupportCodeAndStorageCodeToDT(datatable); //// mode plaque uniquement
	        	}
			},
			hide:{
				active:true
			},
			edit:{
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
				showButton: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
				byDefault:($scope.isCreationMode()),
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
	}; // fin struct datatableConfig
	
	// colonnes variables
	 if ( $scope.experiment.instrument.inContainerSupportCategoryCode === "96-well-plate" ){
		 //INPUT
		 datatableConfig.columns.push({
			// barcode plaque entree == input support Container code
	        "header":Messages("containers.table.support.name"),
	        "property":"inputContainer.support.code",
			"hide":true,
	        "type":"text",
	        "position":1,
	        "extraHeaders":{0: inputExtraHeaders}
	      });
		 datatableConfig.columns.push({
	        // Ligne
	        "header":Messages("containers.table.support.line"),
	        "property":"inputContainer.support.line",
	        "order":true,
			"hide":true,
	        "type":"text",
	         "position":2,
	        "extraHeaders":{0: inputExtraHeaders}
	     });
		 datatableConfig.columns.push({
	        // colonne
	        "header":Messages("containers.table.support.column"),
		    // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
	        "property":"inputContainer.support.column*1",
	        "order":true,
			"hide":true,
	        "type":"number",
	        "position":3,
	        "extraHeaders":{0: inputExtraHeaders}
	     });
		 // OUTPUT
		 datatableConfig.columns.push({
			 // barcode plaque sortie == support Container used code... faut Used 
			 "header":Messages("containers.table.support.name"),
			 "property":"outputContainerUsed.locationOnContainerSupport.code", 
			 "order":true,
			 "hide":true,
			 "type":"text",
			 "position":100,
			 "extraHeaders":{0: outputExtraHeaders}
         });
		 datatableConfig.columns.push({
			 // Line
        	 "header":Messages("containers.table.support.line"),
        	 "property":"outputContainerUsed.locationOnContainerSupport.line",
        	 "order":true,
			 "hide":true,
        	 "type":"text",
        	 "position":110,
        	 "extraHeaders":{0:outputExtraHeaders}
         });
		 datatableConfig.columns.push({
			 // column
        	 "header":Messages("containers.table.support.column"),
        	 // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
        	 "property":"outputContainerUsed.locationOnContainerSupport.column*1",
        	 "order":true,
			 "hide":true,
        	 "type":"number",
        	 "position":120,
        	 "extraHeaders":{0:outputExtraHeaders}
         });
		 
		 
	} else {
		//tube ou strip
		//INPUT
		datatableConfig.columns.push({
			"header":Messages("containers.table.code"),
			"property":"inputContainer.support.code",
			"order":true,
			"edit":false,
			"hide":true,
			"type":"text",
			"position":1,
			"extraHeaders":{0: inputExtraHeaders}
		});
		//OUTPUT
		datatableConfig.columns.push({
			"header":Messages("containers.table.code"),
			"property":"outputContainerUsed.code",
			"order":true,
			"edit":true,
			"hide":true,
			"type":"text",
			"position":100,
			"extraHeaders":{0:outputExtraHeaders}
		});
		datatableConfig.columns.push({
			"header":Messages("containers.table.storageCode"),
			"property":"outputContainerUsed.locationOnContainerSupport.storageCode",
			"order":true,
			"edit":true,
			"hide":true,
			"type":"text",
			"position":150,
			"extraHeaders":{0:outputExtraHeaders}
		});	
	}

	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
		$scope.$emit('childSaved', callbackFunction);
	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");		
		var dtConfig = $scope.atmService.data.getConfig();
		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP'));
		dtConfig.edit.byDefault = false;
		dtConfig.remove.active = ($scope.isEditModeAvailable() && $scope.isNewState());
		$scope.atmService.data.setConfig(dtConfig);
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('cancel', function(e) {
		console.log("call event cancel");
		$scope.atmService.data.cancel();
		
		if($scope.isCreationMode()){
			var dtConfig = $scope.atmService.data.getConfig();
			dtConfig.edit.byDefault = false;
			$scope.atmService.data.setConfig(dtConfig);
		}
	});
	
	$scope.$on('activeEditMode', function(e) {
		console.log("call event activeEditMode");
		$scope.atmService.data.selectAll(true);
		$scope.atmService.data.setEdit();
	});
	
	// pour plaque ( et strip ???)
	
	// recuperation des info du support out
	// pour plaque ( et strip ??) 
	var copyContainerSupportCodeAndStorageCodeToDT = function(datatable){

		var dataMain = datatable.getData();
		
		var outputContainerSupportCode = $scope.outputContainerSupport.code;
		var outputContainerSupportStorageCode = $scope.outputContainerSupport.storageCode;

		if ( null != outputContainerSupportCode && undefined != outputContainerSupportCode){
			for(var i = 0; i < dataMain.length; i++){
				
				var atm = dataMain[i].atomicTransfertMethod;
				var newContainerCode = outputContainerSupportCode+"_"+atm.line + atm.column;

				$parse('outputContainerUsed.code').assign(dataMain[i],newContainerCode);
				$parse('outputContainerUsed.locationOnContainerSupport.code').assign(dataMain[i],outputContainerSupportCode);
				
				if( null != outputContainerSupportStorageCode && undefined != outputContainerSupportStorageCode){
				    $parse('outputContainerUsed.locationOnContainerSupport.storageCode').assign(dataMain[i],outputContainerSupportStorageCode);
				}
			}
		}
	}
		
	//Init
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(l,c){
		return {
			class:"OneToOne",
			line:l, 
			column:c, 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL",
			concentration : "nM"
	}
	
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	console.log("in="+$scope.experiment.instrument.inContainerSupportCategoryCode);	
	console.log("out="+$scope.experiment.instrument.outContainerSupportCategoryCode);
	
	// 13/12/2016 verifier que inContainerSupportCategoryCode == outContainerSupportCategoryCode
	// 04/10/2017 supprimer ce controle
	
	// et pour le choix 'strip' possible si l'intrument choisi est main ?????
	
	$scope.atmService = atmService;
	
	/// pour plaque ( et strip ??)
	if ( $scope.experiment.instrument.inContainerSupportCategoryCode === "96-well-plate" ) {
		// recuperation des infos du support out
		$scope.outputContainerSupport = { code : null , storageCode : null};	
	
		if ( undefined !== $scope.experiment.atomicTransfertMethods[0]) { 
			$scope.outputContainerSupport.code=$scope.experiment.atomicTransfertMethods[0].outputContainerUseds[0].locationOnContainerSupport.code;
			console.log("previous code: "+ $scope.outputContainerSupport.code);
		}
		if ( undefined !== $scope.experiment.atomicTransfertMethods[0]) {
			$scope.outputContainerSupport.storageCode=$scope.experiment.atomicTransfertMethods[0].outputContainerUseds[0].locationOnContainerSupport.storageCode;
			console.log("previous storageCode: "+ $scope.outputContainerSupport.storageCode);
		}
	}
	
	/* 08/12/2016 pas de sample sheet... pour l'instant
	var generateSampleSheet = function(){
		$scope.messages.clear();
		
		$http.post(jsRoutes.controllers.instruments.io.IO.generateFile($scope.experiment.code).url,{})
		.success(function(data, status, headers, config) {
			var header = headers("Content-disposition");
			var filepath = header.split("filename=")[1];
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
	
	$scope.setAdditionnalButtons([{
		isDisabled : function(){return $scope.isCreationMode();},
		isShow:function(){return ($scope.experiment.instrument.typeCode === 'janus')}, // FDS pourquoi ce forcage ???
		click: generateSampleSheet,
		label:Messages("experiments.sampleSheet") 
	}]);
	*/
	
	
}]);
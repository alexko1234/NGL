// FDS 23/02/2017 -- JIRA NGL-1167
angular.module('home').controller('ChromiumGemCtrl',['$scope', '$parse',  '$filter', 'atmToSingleDatatable',
	                                                     function($scope, $parse, $filter, atmToSingleDatatable ){	
	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),

			"columns":[
			         //--------------------- INPUT containers section -----------------------
			         // entree tubes
					{
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":0,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },	
			         { // Projet(s)
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainer.projectCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":11,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0: inputExtraHeaders}
				     },
				     { // Echantillon(s) 
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":12,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0: inputExtraHeaders}
				     },
				     { // sampleAliquoteCode 
				        "header":Messages("containers.table.codeAliquot"),
				 		"property": "inputContainer.contents", 
				 		"filter": "getArray:'properties.sampleAliquoteCode.value'",
				 		"order":true,
				 		"hide":true,
				 		"type":"text",
				 		"position":13,
				 		"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				        "extraHeaders":{0: inputExtraHeaders}
					 },
					 { // Concentration
			        	 "header":Messages("containers.table.concentration") + " (ng/µL)",
			        	 "property":"inputContainer.concentration.value",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":14,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },  
			         { // Volume
			        	 "header":Messages("containers.table.volume") + " (µL)",
			        	 "property":"inputContainer.volume.value",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":15,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },
			         { // Etat input Container 
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":16,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },
			         //--->  colonnes specifiques experience s'inserent ici  (inputUsed ??)     
			         
			         //------------------------- OUTPUT containers section --------------------------
			         
			         //--->  colonnes specifiques experience s'inserent ici  (outputUsed ??)
			         
			         { //  barcode du containerSupport strip sortie == support Container used code... faut Used 
			        	 "header":Messages("containers.table.support.name"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.code", 
						 "hide":true,
			        	 "type":"text",
			        	 "position":35,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },      
			         { // colonne==> position dans le strip ( renommer ??)
			        	 "header":Messages("containers.table.support.column"),
			        	 // ne pas utiliser  *1 ici car affiche "0" quand n'est pas encore defini...
			        	 "property":"outputContainerUsed.locationOnContainerSupport.column", 
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":37,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },	
			         { // Etat outpout container      
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":40,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         }
			         ],
			"compact":true,
			"pagination":{
				"active":false
			},		
			"search":{
				"active":false
			},
			"order":{
				"mode":"local",
				"active":true //,
				// FDS : ce tri donne 1,10,11,12,2.... comment avoir un tri 1,2....10,11,12,13 ??
				//"by":"inputContainer.support.column*1"
			},
			"remove":{
				"active": ($scope.isEditModeAvailable() && $scope.isNewState()),
				"showButton": ($scope.isEditModeAvailable() && $scope.isNewState()),
				"mode":"local"
			},
			"save":{
				"active":true,
	        	"withoutEdit": true,
	        	"changeClass":false,
	        	"showButton":false,
	        	"mode":"local" //,
	        	//"callback":function(datatable){
	        	//	copyContainerSupportCodeAndStorageCodeToDT(datatable);
	        	//}
			},
			"hide":{
				"active":true
			},
			"edit":{ // editable si mode=Finished ????
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
				showButton: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
				byDefault:($scope.isCreationMode()),
				columnMode:true
			},
			"messages":{
				"active":false,
				"columnMode":true
			},
			"exportCSV":{
				"active":true,
				"showButton":true,
				"delimiter":";",
				"start":false
			},
			"extraHeaders":{
				"number":2,
				"dynamic":true,
			}
	}; // fin struct datatableConfig

	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
		$scope.$emit('childSaved', callbackFunction);
	});
	
	/* GA 27/02/2017 : ne plus utiliser le system copyContainerXXToDT sur action save mais utiliser plutot updatePropertyFromUDT
	  updatePropertyFromUDT  est automatiqut defini pour les colonnes injectees dans le datatable...
	  
	  FDS: Pose 2 problemes:
	  1) ca n'affiche pas en temps reels  alors que ca devrait !!!    
	  2) seules les modifiees sont mises a jour en cas de modifiaction du support ====> essai ajout updateContainerCodeDT
	  
	07/03/2017 essai de refaire marcher....
	*/
	
	$scope.updatePropertyFromUDT = function(udt, col){
		console.log("update property : "+ col.property );
		
		var outputContainerSupportCode = $scope.outputContainerSupport.code;
		
		if(col.property === 'inputContainerUsed.instrumentProperties.chipPosition.value'){
			var newChipPos =  $parse("inputContainerUsed.instrumentProperties.chipPosition.value")(udt.data);
			console.log("new position on chip=" + newChipPos);
			
			if ((undefined != newChipPos) && (undefined != outputContainerSupportCode))
			{	
				// creation du code du container
				var newContainerCode = outputContainerSupportCode +"_"+ newChipPos ;
				console.log("....newContainerCode="+ newContainerCode);
					
				$parse('outputContainerUsed.code').assign(udt.data, newContainerCode);
				
				console.log("assigning container support code!!!"+ outputContainerSupportCode);
				$parse('outputContainerUsed.locationOnContainerSupport.code').assign(udt.data, outputContainerSupportCode);// devrait se mettre a jour "live"!!!
				
				$parse('outputContainerUsed.locationOnContainerSupport.line').assign(udt.data, '1');
				
				console.log("assigning column !!!"+newChipPos);		
				$parse('outputContainerUsed.locationOnContainerSupport.column').assign(udt.data, newChipPos);// devrait se mettre a jour "live"!!!
				
				// Historique mais continuer a renseigner car effets de bord possibles ????
				$parse('line').assign(udt.data.atomicTransfertMethod, 1);
				$parse('column').assign(udt.data.atomicTransfertMethod, newChipPos);
				
				console.log("end assigning...");
			}
			
			var outputContainerSupportStorageCode = $scope.outputContainerSupport.storageCode;
			if( null != outputContainerSupportStorageCode && undefined != outputContainerSupportStorageCode){
				$parse('outputContainerUsed.locationOnContainerSupport.storageCode').assign(udt.data,outputContainerSupportStorageCode);
			}
		}
	}
	
	
	/* 07/02/2017  ne plus passer par ce systeme....
	var copyContainerSupportCodeAndStorageCodeToDT = function(datatable){
		
		var dataMain = datatable.getData();
		
		var outputContainerSupportCode = $scope.outputContainerSupport.code;
		var outputContainerSupportStorageCode = $scope.outputContainerSupport.storageCode;

		if ( null != outputContainerSupportCode && undefined != outputContainerSupportCode){
			
			for(var i = 0; i < dataMain.length; i++){
				
				// recuperer la valeur du select "chipPosition"
				// !!!! instrument property
				//var newChipPos =$parse("inputContainerUsed.experimentProperties.chipPosition.value")(dataMain[i]);
				var newChipPos =$parse("inputContainerUsed.instrumentProperties.chipPosition.value")(dataMain[i]);
				console.log("data :"+ i + "=> new chip position =" + newChipPos);
				////var oldPosChip =$scope.experiment.atomicTransfertMethods[i].inputContainerUseds[0].experimentProperties.chipPosition.value;
				
				var atm = dataMain[i].atomicTransfertMethod;
						
				if ( null != newChipPos ) {		
					// creation du code du container
					var newContainerCode = outputContainerSupportCode+"_"+newChipPos ;
					console.log("newContainerCode="+ newContainerCode);
					
					$parse('outputContainerUsed.code').assign(dataMain[i],newContainerCode);
					// independant de la position...
					$parse('outputContainerUsed.locationOnContainerSupport.code').assign(dataMain[i],outputContainerSupportCode);
					
					//assigner la column et line du support !!!!
					$parse('outputContainerUsed.locationOnContainerSupport.line').assign(dataMain[i],1);
					$parse('outputContainerUsed.locationOnContainerSupport.column').assign(dataMain[i],newChipPos);
					
					// Historique mais continer a renseigner car effets de bord possible ????
					$parse('line').assign(atm,1);
					$parse('column').assign(atm,newChipPos);
					//console.log("atm.line="+ atm.line + " atm.column="+atm.column);	
				
					if( null != outputContainerSupportStorageCode && undefined != outputContainerSupportStorageCode){
						$parse('outputContainerUsed.locationOnContainerSupport.storageCode').assign(dataMain[i],outputContainerSupportStorageCode);
					}
				}
			}	
		}
		
	    datatable.setData(dataMain);
	}
	
	*/
	// ajout showButton + suppression start = false;
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");		
		var dtConfig = $scope.atmService.data.getConfig();
		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
		dtConfig.edit.showButton = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
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
	
	// meme s'il n'y a pas de choix possible par l'utilisateur, ce watch est indispensable pour que les proprietes d'instrument soient injectees dans l'interface..	
	$scope.$watch("instrumentType", function(newValue, OldValue){
		if(newValue)
			$scope.atmService.addInstrumentPropertiesToDatatable(newValue.propertiesDefinitions);
	});
	
	// ESSAI ....
	$scope.$watch("outputContainerSupport.code", function(newValue, OldValue){
		if ((newValue !== undefined ) && ( newValue !== OldValue)){
			console.log("outputContainerSupport.code  CHANGED !!! "+newValue+ " ...  update all outputContainers");	
			updateContainerCodesDT($scope.datatable);
		}
	});
		
	// TEST !!!!  PBBBB  datatable   pas defini !!!!!!!!
	var updateContainerCodesDT = function(datatable){			
		var dataMain = datatable.getData();
			
		for(var i = 0; i < dataMain.length; i++){
			var currentChipPos =$scope.experiment.atomicTransfertMethods[i].inputContainerUseds[0].instrumentProperties.chipPosition.value;
			
			console.log("updating chip position :"+ currentChipPos);
			var newContainerCode = $scope.outputContainerSupport.code+"_"+currentChipPos ;

			console.log("reassigning outputContainerUsed.code..=> "+  newContainerCode);
			$parse('outputContainerUsed.code').assign(udt.data,newContainerCode);
		}
   }	
   
		
	//Init
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//define new atomictransfertMethod; line is set to one for strip-8
	atmService.newAtomicTransfertMethod = function(l, c){
		return {
			class:"OneToOne",
			line: 1, 
			column: undefined, 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL"
	};
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	// verification du nombre d'inputs container
	if ( $scope.experiment.atomicTransfertMethods.length > 8 ){
		$scope.messages.setError("Warning: "+ Messages('experiments.input.error.maxContainers',8));
		// continuer qd meme... il n'existe pas de setWarning
		$scope.atmService = atmService;
	}else{
		// au tout debut lenght=0
		$scope.atmService = atmService;
	}
	
	$scope.outputContainerSupport = { code : null , storageCode : null};	
	

	if ( undefined !== $scope.experiment.atomicTransfertMethods[0]) { 
		 $scope.outputContainerSupport.code=$scope.experiment.atomicTransfertMethods[0].outputContainerUseds[0].locationOnContainerSupport.code;
		//console.log("previous code: "+ $scope.outputContainerSupport.code);
	}
	if ( undefined !== $scope.experiment.atomicTransfertMethods[0]) {
		 $scope.outputContainerSupport.storageCode=$scope.experiment.atomicTransfertMethods[0].outputContainerUseds[0].locationOnContainerSupport.storageCode;
		 //console.log("previous storageCode: "+ $scope.outputContainerSupport.storageCode);
	}

}]);
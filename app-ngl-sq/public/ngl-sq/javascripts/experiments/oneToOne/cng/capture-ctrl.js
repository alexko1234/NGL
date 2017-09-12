// FDS 27/07/2017 -- JIRA NGL-1201. Duplication a partir de additional-normalization
// A ADAPTER !!!!!!!!!!
angular.module('home').controller('CaptureCtrl',['$scope', '$parse', '$http', 'atmToSingleDatatable',
                                                     function($scope, $parse, $http, atmToSingleDatatable){

	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	// NGL-1055: name explicite pour fichier CSV exporté: typeCode experience
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),
			columns:[
			         //--------------------- INPUT containers section -----------------------
			         			
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
			         { // Projet(s)
			        	"header":Messages("containers.table.projectCodes"),
			 			"property":"inputContainer.projectCodes",
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
				 		"filter": "getArray:'properties.sampleAliquoteCode.value'",
				 		"order":true,
				 		"hide":true,
				 		"type":"text",
				 		"position":6,
				 		"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				        "extraHeaders":{0: inputExtraHeaders}
					 },
					 { //Concentration; 12/09/2016 ne pas inclure l'unité dans le label; 08/11/2016 label court
			        	 "header":Messages("containers.table.concentration.shortLabel"), 
			        	 "property":"inputContainerUsed.concentration.value",  
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":8,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         { // 12/09/2016 afficher l'unité concentration dans une colonne séparée pour récupérer la vraie valeur
			        	 "header":Messages("containers.table.concentration.unit.shortLabel"),
			        	 "property":"inputContainerUsed.concentration.unit",  
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":8.5,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         { //Volume 
			        	 "header":Messages("containers.table.volume") + " (µL)", 
			        	 "property":"inputContainerUsed.volume.value",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":9,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },      
			         { // 31/08/2017 niveau process ET contents => utiliser properties et pas processProperties; 04/09/2017 si filtre codes:'value' alors header =>libProcessType
			        	 "header": Messages("containers.table.libProcessType"),
			        	 "property" : "inputContainerUsed.contents",
			        	 "filter" : "getArray:'properties.libProcessTypeCode.value' | unique | codes:'value'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":9.2,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },      
			         { // 31/08/2017 niveau process uniquement =>  utiliser processProperties; ne fonctionne que a nouveau et en cours c'est normal !!
			        	 "header": Messages("containers.table.expectedBaits"),
			        	 "property" : "inputContainerUsed.contents",
			        	 "filter" : "getArray:'processProperties.expectedBaits.value' | unique  | codes:'value'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":9.4,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         }, 
			         { // 31/08/2017 niveau process ET contents => utiliser properties et pas processProperties
			        	 "header": Messages("containers.table.captureProtocol"),
			        	 "property" : "inputContainerUsed.contents",
			        	 "filter" : "getArray:'properties.captureProtocol.value' | unique  | codes:'value'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":9.6,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         { // Etat input Container
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":10,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },

			         // colonnes correspondant aux propriétés experience de niveau containerIN viennent automatiquement ici
                     //   => Volume engagé, Quantité engagée, Baits (sondes)
			          
			         //------------------------ OUTPUT containers section -------------------

		            /* ne pas aficher les containercodes sauf pour DEBUG 
			         {
			        	 "header":"[["+Messages("containers.table.code")+"]]",
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
						 "hide":true,
						 "edit":false,
			        	 "type":"text",
			        	 "position":100,
			        	 "extraHeaders":{0:"outputExtraHeaders"}
			         },*/
			         { // barcode plaque sortie
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
			        	 "position":111,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },	
			         /* 30/08/2017 pas de volume ni concentration OUT
			         { // Concentration
			        	 "header":Messages("containers.table.concentration.shortLabel") + " (ng/µL)",
			        	 "property":"outputContainerUsed.concentration.value",
						 "edit":true,
						 "editDirectives":"udt-change='updatePropertyFromUDT(value,col)'",
						 "hide":true,
			        	 "type":"number",
			        	 "position":120,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         { // Volume 
			        	 "header":Messages("containers.table.volume")+ " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
						 "edit":true,
						 "editDirectives":"udt-change='updatePropertyFromUDT(value,col)'",
						 "hide":true,
			        	 "type":"number",
			        	 "position":130,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         */
			         { // Etat outpout container 
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"outputContainer.state.code | codes:'state'",
						 "hide":true,
			        	 "type":"text",
			        	 "position":160,
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
	        		copyContainerSupportCodeAndStorageCodeToDT(datatable);
	        	}
			},
			hide:{
				active:true
			},
			edit:{ 
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
				showButton: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
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

	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
		$scope.$emit('childSaved', callbackFunction);
	});
	
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
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");		
		var dtConfig = $scope.atmService.data.getConfig();
		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP'));
		dtConfig.edit.byDefault = false;
		dtConfig.edit.start = false;
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
		
	//Init atmService
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	atmService.newAtomicTransfertMethod = function(l, c){
		return {
			class:"OneToOne",
			line: l, 
			column: c, 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};
	
	//defined default output unit;  28/07/2017 modif en "ng/µL" (au lieu de nM)
	atmService.defaultOutputUnit = {
			volume : "µL",
			concentration : "ng/µL"
	}
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	// 28/08/2017 OK countInputSupportCodes
	if ( $scope.countInputSupportCodes() > 1) {
		console.log(" > 1 support en entree");
		
		$scope.messages.clear();
		$scope.messages.clazz = "alert alert-danger";
		$scope.messages.text = Messages("experiments.input.error.only-1-plate");
		$scope.messages.showDetails = false;
		$scope.messages.open();
	} else {
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
		isShow:function(){return ($scope.experiment.instrument.typeCode === 'bravo-workstation')}, 
		click: generateSampleSheet,
		label:Messages("experiments.sampleSheet") 
	}]);
	
	
	// FDS 28/07/2017  calculs
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		if ( col.property === 'inputContainerUsed.experimentProperties.inputQuantity.value'){
		    computeVolumes(value.data);
		}
	}

	// FDS 28/07/2017 calculs; attention aux pb d'unité, le calcul n'est possible que si l'unité est "ng/µL"
	var computeVolumes = function(udtData){
		var getterEngageVol=$parse("inputContainerUsed.experimentProperties.inputVolume.value");
		
		var compute = {
				inputConc :  $parse("inputContainerUsed.concentration.value")(udtData), 
				inputQty :   $parse("inputContainerUsed.experimentProperties.inputQuantity.value")(udtData),
				inputConcUnit: $parse("inputContainerUsed.concentration.unit")(udtData),

				isReady:function(){
					// traiter le cas ou la qté est volontairement mise a 0
					return (this.inputConc && ( this.inputQty || this.inputQty === 0 ) && (this.inputConcUnit === "ng/µL"||this.inputConcUnit === "ng/µl" ) );
				}
		};
		

		if(compute.isReady()){
			var engageVol=$parse("inputQty / inputConc")(compute);
			var inputConc=$parse("inputConc")(compute);
			
			// arrondir...
			if(angular.isNumber(engageVol) && !isNaN(engageVol)){
				engageVol = Math.round(engageVol*10)/10;				
			}
			console.log("vol engagé = "+engageVol);
	
			getterEngageVol.assign(udtData, engageVol);
			
		}else{
			console.log("Impossible de calculer les volumes: valeurs manquantes OU concentration avec unité incorrecte");
			getterEngageVol.assign(udtData, undefined);
		}
	}
	
}]);
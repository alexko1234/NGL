// FDS 04/8/2016 -- JIRA NGL-1026 :library-prep experiment ( idem a frg-and-library-prep mais sans la fragmentation )
// difference avec PrepPcrFreeCtrl est sur la/les plaques index...
angular.module('home').controller('LibraryPrepCtrl',['$scope', '$parse',  '$filter', 'atmToSingleDatatable','$http',
                                                     function($scope, $parse, $filter, atmToSingleDatatable, $http){
	
	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),

			"columns":[
			         //--------------------- INPUT containers section -----------------------
			         
			         /* plus parlant pour l'utilisateur d'avoir Plate barcode | line | column
					  {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":0,
			        	 "extraHeaders":{0: inputExtraHeaders}
			          },	
			          */		        
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
				   /*  { // sampleAliquoteCode 
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
					*/
					 { // Concentration
			        	 "header":Messages("containers.table.concentration") + " (ng/µL)",
			        	 "property":"inputContainerUsed.concentration.value",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":14,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },  
			         { // Volume
			        	 "header":Messages("containers.table.volume") + " (µL)",
			        	 "property":"inputContainerUsed.volume.value",
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
			         
		            /* ne pas aficher les containercodes  sauf pour DEBUG
			         {
			        	 "header":"DEBUG code",
			        	 "property":"outputContainer.code",
			        	 "order":true,
						 "hide":true,
						 "edit":false,
			        	 "type":"text",
			        	 "position":99,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },*/
			         { // Volume avec valeur par defaut
			        	 "header":Messages("containers.table.volume") + " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "hide":true,
			        	 "edit":true,
			        	 "type":"number",
			        	 "defaultValues":20,
			        	 "position":34,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },
			         { //  barcode plaque sortie == support Container used code... faut Used 
			        	 "header":Messages("containers.table.support.name"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.code", 
						 "hide":true,
			        	 "type":"text",
			        	 "position":35,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },  
			         { //  Ligne 
			        	 "header":Messages("containers.table.support.line"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.line", 
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":36,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },     
			         { // colonne
			        	 "header":Messages("containers.table.support.column"),
			        	 // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
			        	 "property":"outputContainerUsed.locationOnContainerSupport.column*1", 
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
				"active":true//,
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
	        	"mode":"local",
	        	"callback":function(datatable){
	        		copyContainerSupportCodeAndStorageCodeToDT(datatable);
	        	}
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
			},
			"otherButtons": {
                active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
                complex:true,
                template:''
                	+'<div class="btn-group" style="margin-left:5px">'
                	+'<button class="btn btn-default" ng-click="copyVolumeInToExp()" data-toggle="tooltip" title="'+Messages("experiments.button.plate.copyVolumeTo")+' vol. eng. librairie'
                	+'" ng-disabled="!isEditMode()" ng-if="experiment.instrument.outContainerSupportCategoryCode!==\'tube\'"><i class="fa fa-files-o" aria-hidden="true"></i> Volume </button>'                	                	
                	+'</div>'
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
		
		//ne plus faire...datatable.setData(dataMain);
	}
	
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
	
    // 24/11/2016 FDS copier le volume containerIn dans le volume engagé Librairie
	//     code adapté depuis copyVolumeInToOut de x-to-plates-ctrl.js
	$scope.copyVolumeInToExp = function(){
		console.log("copyVolumeInToExp");
		
		var data = $scope.atmService.data.displayResult;		
		data.forEach(function(value){
			
			if ( !value.data.inputContainerUsed.experimentProperties ){
				value.data.inputContainerUsed.experimentProperties = {};
			}
			value.data.inputContainerUsed.experimentProperties.inputVolumeLib=value.data.inputContainerUsed.volume;
		})		
	};
	
	// FDS 16/03/2018 : NGL-1906. rechercher le  ngsRunWorkLabel positionné au niveau processus pour le copier dans robotRunCode (sauf s'il y en plusieurs!!)
	$scope.$watch("experiment.instrument.code", function(newValue, OldValue){
		if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {		
			// exemple dans prepa-fc-ordered: var categoryCodes = $scope.$eval("getBasket().get()|getArray:'support.categoryCode'|unique",mainService);
			// mais ici mainService n'est pas defini, et pas necessaire...
			// obliger de passer par contents[0], mais normalement ne doit pas poser de probleme...
			var workLabels= $scope.$eval("getBasket().get()|getArray:'contents[0].processProperties.ngsRunWorkLabel.value'|unique");
			if ( workLabels.length > 1 ){
				$scope.messages.clear();
				$scope.messages.clazz = "alert alert-warning";
				$scope.messages.text = "Plusieurs noms de travail (robot) trouvés parmi les containers d'entrée (info processus)";
				$scope.messages.open();			
			
				console.log('>1  run workLabel trouvé !!');
				
			} else if ( workLabels.length === 1 ){
				// verifier que TOUS les containers ont une valeur...
				var contents= $scope.$eval("getBasket().get()|getArray:'contents[0]'");
				var labels= $scope.$eval("getBasket().get()|getArray:'contents[0].processProperties.ngsRunWorkLabel.value'");
				if ( labels.length < contents.length ) {
					$scope.messages.clear();
					$scope.messages.clazz = "alert alert-warning";
					$scope.messages.text = "Certains containers en entrée n'ont pas de nom de travail run (robot) (info processus)";
					$scope.messages.open();			

					console.log("Certains containers n'ont pas de workLabel.");
				} else {
					$parse("instrumentProperties.robotRunCode.value").assign($scope.experiment, workLabels[0]);
				}
			} 
			// si aucun workLabel ne rien faire
		}
	});
		
	//Init
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(l, c){
		return {
			class:"OneToOne",
			line: l, 
			column: c, 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL"
	};
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
	
	// Calculs 
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);

		// si l'utilisateur défini le volume a engager => calculer la quantité
		if(col.property === 'inputContainerUsed.experimentProperties.inputVolumeLib.value'){
			computeQuantity(value.data);
		}
		
		// 05/08/2016 essai d'ajouter le calcul inverse...===> PB LES 2 MODIFICATIONS SE MARCHENT SUR LES PIEDS !!
		//
		// si l'utilisateur défini la quantité a engager => calculer le volume
		//if(col.property === 'inputContainerUsed.experimentProperties.inputQuantityLib.value'){
		//	computeVolume(value.data);
		//}
	}

	// -1- inputQuantityLib=inputContainerUsed.concentration.value * inputContainerUsed.experimentProperties.inputVolumeLib.value
	var computeQuantity = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.inputQuantityLib.value");

		if($parse("inputContainerUsed.concentration.unit === 'nM'")(udtData)) {
			console.log("unit = nM");
		}
		
		var compute = {
				inputConcUnit: $parse("inputContainerUsed.concentration.unit")(udtData),
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				inputVolume : $parse("inputContainerUsed.experimentProperties.inputVolumeLib.value")(udtData),		
				isReady:function(){
					/// return (this.inputVolume && this.inputConc); bug!!!  le calcul ne se fait pas si inputConc=0 ( par exemple WATER)
					/// bloquer le calcul si l'unité n'est pas nM TODO...
					return (this.inputVolume && (this.inputConc != undefined));
				}
		};
		
		if(compute.isReady()){
			var result = $parse("inputConc * inputVolume")(compute);
			console.log("result = "+result);
			
			if(angular.isNumber(result) && !isNaN(result)){
				inputQuantity = Math.round(result*10)/10;				
			}else{
				inputQuantity = undefined;
			}	
			getter.assign(udtData, inputQuantity);
			
		}else{
			console.log("Missing values to calculate Quantity");
		}
	}
	
	// PLUS APPELLEE...voir plus haut...
	// -2- inputVolumeLib= inputContainerUsed.experimentProperties.QuantityLib.value / inputContainerUsed.concentration.value
	var computeVolume = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.inputVolumeLib.value");

		var compute = {
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				inputQuantity : $parse("inputContainerUsed.experimentProperties.inputQuantityLib.value")(udtData),		
				isReady:function(){
					return (this.inputConc && this.inputQuantity);
				}
		};
		
		if(compute.isReady()){
			var result = $parse("inputQuantity / inputConc")(compute);
			console.log("result = "+result);
			
			if(angular.isNumber(result) && !isNaN(result)){
				inputVolume = Math.round(result*10)/10;				
			}else{
				inputVolume = undefined;
			}	
			getter.assign(udtData, inputVolume);
			
		}else{
			console.log("Missing values to calculate Volume");
		}
	}
	
	
	var importData = function(){
		$scope.messages.clear();

		$http.post(jsRoutes.controllers.instruments.io.IO.importFile($scope.experiment.code).url, $scope.file)
		.success(function(data, status, headers, config) {
			
			$scope.messages.clazz="alert alert-success";
			$scope.messages.text=Messages('experiments.msg.import.success');
			$scope.messages.showDetails = false;
			$scope.messages.open();	
			//only atm because we cannot override directly experiment on scope.parent
			$scope.experiment.atomicTransfertMethods = data.atomicTransfertMethods;
			$scope.file = undefined;
			// reinit select File...
			angular.element('#importFile')[0].value = null;
			$scope.$emit('refresh');
			
		})
		.error(function(data, status, headers, config) {
			
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages('experiments.msg.import.error');
			$scope.messages.setDetails(data);
			$scope.messages.open();	
			$scope.file = undefined;
			// reinit select File...
			angular.element('#importFile')[0].value = null;
		});		
	};
	
	$scope.outputContainerSupport = { code : null , storageCode : null};	
		
	if ( undefined !== $scope.experiment.atomicTransfertMethods[0]) { 
		 $scope.outputContainerSupport.code=$scope.experiment.atomicTransfertMethods[0].outputContainerUseds[0].locationOnContainerSupport.code;
		//console.log("previous code: "+ $scope.outputContainerSupport.code);
	}
	if ( undefined !== $scope.experiment.atomicTransfertMethods[0]) {
		$scope.outputContainerSupport.storageCode=$scope.experiment.atomicTransfertMethods[0].outputContainerUseds[0].locationOnContainerSupport.storageCode;
		//console.log("previous storageCode: "+ $scope.outputContainerSupport.storageCode);
	}
	
	// importer un fichier definissant quels index sont déposés dans quels containers
	$scope.button = {
		isShow:function(){
			return ( ($scope.isInProgressState() && !$scope.mainService.isEditMode())|| Permissions.check("admin") );
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData	
	};
	
	// Autre mode possible : utiliser une plaque d'index prédéfinis, l'utilisateur a juste a indiquer a partir de quelle colonne
	// de cette plaque le robot doit prelever les index

	$scope.columns = [ {name:'---', position: undefined },
	                   {name:'1', position:0}, {name:'2', position:8}, {name:'3', position:16}, {name:'4',  position:24}, {name:'5',  position:32}, {name:'6',  position:40},
	                   {name:'7', position:48},{name:'8', position:56},{name:'9', position:64}, {name:'10', position:72}, {name:'11', position:80}, {name:'12', position:88}
	                 ];
	$scope.tagPlateColumn = $scope.columns[0]; // defaut du select
	
	// 21/11/2017 modifications pour possibilité d'utiliser plusieurs plaques
	// 12/04/2018 NGL-2012 ne rien mettre par defaut !!!
	$scope.plates = [ {name:"---",                                         tagCategory: undefined,   tags: undefined },
	                  {name:"RAP TruSeq RNA HT",                           tagCategory:"DUAL-INDEX", tags:[] }, 
	                  {name:"IDT-ILMN TruSeq DNA UD Indexes (96 Indexes)", tagCategory:"DUAL-INDEX", tags:[] },
	                  {name:"IDT-ILMN TruSeq DNA UD Indexes (24 Indexes x4)", tagCategory:"DUAL-INDEX", tags:[] }
	                ];
	
	// l'indice dans le tableau correspond a l'ordre "colonne d'abord" dans la plaque
    // c'est le code des index qu'il faut mettre ici  exemple:  AglSSXT-01(name)/aglSSXT-01(code) 
	
	//-1- RAP TruSeq RNA HT
	// NB: ce sont les memes index et dans la meme disposition que pour la "DAP TruSeq DNA HT"
	var tagPlateCode=[];
	$scope.plates[1].tags.push("D701-D501", "D701-D502", "D701-D503", "D701-D504", "D701-D505", "D701-D506", "D701-D507", "D701-D508"); //colonne 1
	$scope.plates[1].tags.push("D702-D501", "D702-D502", "D702-D503", "D702-D504", "D702-D505", "D702-D506", "D702-D507", "D702-D508"); //colonne 2
	$scope.plates[1].tags.push("D703-D501", "D703-D502", "D703-D503", "D703-D504", "D703-D505", "D703-D506", "D703-D507", "D703-D508"); //colonne 3
	$scope.plates[1].tags.push("D704-D501", "D704-D502", "D704-D503", "D704-D504", "D704-D505", "D704-D506", "D704-D507", "D704-D508"); //colonne 4
	$scope.plates[1].tags.push("D705-D501", "D705-D502", "D705-D503", "D705-D504", "D705-D505", "D705-D506", "D705-D507", "D705-D508"); //colonne 5
	$scope.plates[1].tags.push("D706-D501", "D706-D502", "D706-D503", "D706-D504", "D706-D505", "D706-D506", "D706-D507", "D706-D508"); //colonne 6
	$scope.plates[1].tags.push("D707-D501", "D707-D502", "D707-D503", "D707-D504", "D707-D505", "D707-D506", "D707-D507", "D707-D508"); //colonne 7
	$scope.plates[1].tags.push("D708-D501", "D708-D502", "D708-D503", "D708-D504", "D708-D505", "D708-D506", "D708-D507", "D708-D508"); //colonne 8
	$scope.plates[1].tags.push("D709-D501", "D709-D502", "D709-D503", "D709-D504", "D709-D505", "D709-D506", "D709-D507", "D709-D508"); //colonne 9
	$scope.plates[1].tags.push("D710-D501", "D710-D502", "D710-D503", "D710-D504", "D710-D505", "D710-D506", "D710-D507", "D710-D508"); //colonne 10
	$scope.plates[1].tags.push("D711-D501", "D711-D502", "D711-D503", "D711-D504", "D711-D505", "D711-D506", "D711-D507", "D711-D508"); //colonne 11
	$scope.plates[1].tags.push("D712-D501", "D712-D502", "D712-D503", "D712-D504", "D712-D505", "D712-D506", "D712-D507", "D712-D508"); //colonne 12
	
    //-2- IDT-ILMN TruSeq DNA UD Indexes (96 Indexes)
    $scope.plates[2].tags.push("udi0001_i7-udi0001_i5","udi0002_i7-udi0002_i5","udi0003_i7-udi0003_i5","udi0004_i7-udi0004_i5","udi0005_i7-udi0005_i5","udi0006_i7-udi0006_i5","udi0007_i7-udi0007_i5","udi0008_i7-udi0008_i5"); //colonne 1
    $scope.plates[2].tags.push("udi0009_i7-udi0009_i5","udi0010_i7-udi0010_i5","udi0011_i7-udi0011_i5","udi0012_i7-udi0012_i5","udi0013_i7-udi0013_i5","udi0014_i7-udi0014_i5","udi0015_i7-udi0015_i5","udi0016_i7-udi0016_i5"); //colonne 2
    $scope.plates[2].tags.push("udi0017_i7-udi0017_i5","udi0018_i7-udi0018_i5","udi0019_i7-udi0019_i5","udi0020_i7-udi0020_i5","udi0021_i7-udi0021_i5","udi0022_i7-udi0022_i5","udi0023_i7-udi0023_i5","udi0024_i7-udi0024_i5"); //colonne 3
    $scope.plates[2].tags.push("udi0025_i7-udi0025_i5","udi0026_i7-udi0026_i5","udi0027_i7-udi0027_i5","udi0028_i7-udi0028_i5","udi0029_i7-udi0029_i5","udi0030_i7-udi0030_i5","udi0031_i7-udi0031_i5","udi0032_i7-udi0032_i5"); //colonne 4
    $scope.plates[2].tags.push("udi0033_i7-udi0033_i5","udi0034_i7-udi0034_i5","udi0035_i7-udi0035_i5","udi0036_i7-udi0036_i5","udi0037_i7-udi0037_i5","udi0038_i7-udi0038_i5","udi0039_i7-udi0039_i5","udi0040_i7-udi0040_i5"); //colonne 5
    $scope.plates[2].tags.push("udi0041_i7-udi0041_i5","udi0042_i7-udi0042_i5","udi0043_i7-udi0043_i5","udi0044_i7-udi0044_i5","udi0045_i7-udi0045_i5","udi0046_i7-udi0046_i5","udi0047_i7-udi0047_i5","udi0048_i7-udi0048_i5"); //colonne 6
    $scope.plates[2].tags.push("udi0049_i7-udi0049_i5","udi0050_i7-udi0050_i5","udi0051_i7-udi0051_i5","udi0052_i7-udi0052_i5","udi0053_i7-udi0053_i5","udi0054_i7-udi0054_i5","udi0055_i7-udi0055_i5","udi0056_i7-udi0056_i5"); //colonne 7
    $scope.plates[2].tags.push("udi0057_i7-udi0057_i5","udi0058_i7-udi0058_i5","udi0059_i7-udi0059_i5","udi0060_i7-udi0060_i5","udi0061_i7-udi0061_i5","udi0062_i7-udi0062_i5","udi0063_i7-udi0063_i5","udi0064_i7-udi0064_i5"); //colonne 8
    $scope.plates[2].tags.push("udi0065_i7-udi0065_i5","udi0066_i7-udi0066_i5","udi0067_i7-udi0067_i5","udi0068_i7-udi0068_i5","udi0069_i7-udi0069_i5","udi0070_i7-udi0070_i5","udi0071_i7-udi0071_i5","udi0072_i7-udi0072_i5"); //colonne 9
    $scope.plates[2].tags.push("udi0073_i7-udi0073_i5","udi0074_i7-udi0074_i5","udi0075_i7-udi0075_i5","udi0076_i7-udi0076_i5","udi0077_i7-udi0077_i5","udi0078_i7-udi0078_i5","udi0079_i7-udi0079_i5","udi0080_i7-udi0080_i5"); //colonne 10
    $scope.plates[2].tags.push("udi0081_i7-udi0081_i5","udi0082_i7-udi0082_i5","udi0083_i7-udi0083_i5","udi0084_i7-udi0084_i5","udi0085_i7-udi0085_i5","udi0086_i7-udi0086_i5","udi0087_i7-udi0087_i5","udi0088_i7-udi0088_i5"); //colonne 11
    $scope.plates[2].tags.push("udi0089_i7-udi0089_i5","udi0090_i7-udi0090_i5","udi0091_i7-udi0091_i5","udi0092_i7-udi0092_i5","udi0093_i7-udi0093_i5","udi0094_i7-udi0094_i5","udi0095_i7-udi0095_i5","udi0096_i7-udi0096_i5"); //colonne 12

    //-3- IDT-ILMN TruSeq DNA UD Indexes (24 Indexes repetes 4 fois)
    $scope.plates[3].tags.push("udi0001_i7-udi0001_i5","udi0002_i7-udi0002_i5","udi0003_i7-udi0003_i5","udi0004_i7-udi0004_i5","udi0005_i7-udi0005_i5","udi0006_i7-udi0006_i5","udi0007_i7-udi0007_i5","udi0008_i7-udi0008_i5"); //colonne 1
    $scope.plates[3].tags.push("udi0009_i7-udi0009_i5","udi0010_i7-udi0010_i5","udi0011_i7-udi0011_i5","udi0012_i7-udi0012_i5","udi0013_i7-udi0013_i5","udi0014_i7-udi0014_i5","udi0015_i7-udi0015_i5","udi0016_i7-udi0016_i5"); //colonne 2
    $scope.plates[3].tags.push("udi0017_i7-udi0017_i5","udi0018_i7-udi0018_i5","udi0019_i7-udi0019_i5","udi0020_i7-udi0020_i5","udi0021_i7-udi0021_i5","udi0022_i7-udi0022_i5","udi0023_i7-udi0023_i5","udi0024_i7-udi0024_i5"); //colonne 3
    $scope.plates[3].tags.push("udi0001_i7-udi0001_i5","udi0002_i7-udi0002_i5","udi0003_i7-udi0003_i5","udi0004_i7-udi0004_i5","udi0005_i7-udi0005_i5","udi0006_i7-udi0006_i5","udi0007_i7-udi0007_i5","udi0008_i7-udi0008_i5"); //colonne 4
    $scope.plates[3].tags.push("udi0009_i7-udi0009_i5","udi0010_i7-udi0010_i5","udi0011_i7-udi0011_i5","udi0012_i7-udi0012_i5","udi0013_i7-udi0013_i5","udi0014_i7-udi0014_i5","udi0015_i7-udi0015_i5","udi0016_i7-udi0016_i5"); //colonne 5
    $scope.plates[3].tags.push("udi0017_i7-udi0017_i5","udi0018_i7-udi0018_i5","udi0019_i7-udi0019_i5","udi0020_i7-udi0020_i5","udi0021_i7-udi0021_i5","udi0022_i7-udi0022_i5","udi0023_i7-udi0023_i5","udi0024_i7-udi0024_i5"); //colonne 6
    $scope.plates[3].tags.push("udi0001_i7-udi0001_i5","udi0002_i7-udi0002_i5","udi0003_i7-udi0003_i5","udi0004_i7-udi0004_i5","udi0005_i7-udi0005_i5","udi0006_i7-udi0006_i5","udi0007_i7-udi0007_i5","udi0008_i7-udi0008_i5"); //colonne 7
    $scope.plates[3].tags.push("udi0009_i7-udi0009_i5","udi0010_i7-udi0010_i5","udi0011_i7-udi0011_i5","udi0012_i7-udi0012_i5","udi0013_i7-udi0013_i5","udi0014_i7-udi0014_i5","udi0015_i7-udi0015_i5","udi0016_i7-udi0016_i5"); //colonne 8
    $scope.plates[3].tags.push("udi0017_i7-udi0017_i5","udi0018_i7-udi0018_i5","udi0019_i7-udi0019_i5","udi0020_i7-udi0020_i5","udi0021_i7-udi0021_i5","udi0022_i7-udi0022_i5","udi0023_i7-udi0023_i5","udi0024_i7-udi0024_i5"); //colonne 9
    $scope.plates[3].tags.push("udi0001_i7-udi0001_i5","udi0002_i7-udi0002_i5","udi0003_i7-udi0003_i5","udi0004_i7-udi0004_i5","udi0005_i7-udi0005_i5","udi0006_i7-udi0006_i5","udi0007_i7-udi0007_i5","udi0008_i7-udi0008_i5"); //colonne 10
    $scope.plates[3].tags.push("udi0009_i7-udi0009_i5","udi0010_i7-udi0010_i5","udi0011_i7-udi0011_i5","udi0012_i7-udi0012_i5","udi0013_i7-udi0013_i5","udi0014_i7-udi0014_i5","udi0015_i7-udi0015_i5","udi0016_i7-udi0016_i5"); //colonne 11
    $scope.plates[3].tags.push("udi0017_i7-udi0017_i5","udi0018_i7-udi0018_i5","udi0019_i7-udi0019_i5","udi0020_i7-udi0020_i5","udi0021_i7-udi0021_i5","udi0022_i7-udi0022_i5","udi0023_i7-udi0023_i5","udi0024_i7-udi0024_i5"); //colonne 12

	$scope.tagPlate = $scope.plates[0]; // defaut du select
	
	/*garder pour l'instant au cas ou....
	var setTags = function(){
		$scope.messages.clear();
		
        var dataMain = atmService.data.getData();
        // trier dans l'ordre "colonne d'abord"
        var dataMain = $filter('orderBy')(dataMain, ['atomicTransfertMethod.column*1','atomicTransfertMethod.line']);
        
        //attention certains choix de colonne sont incorrrects !!!
        // 26/10/2017 NGL-1671: le controle doit porter sur la valeur maximale de colonne trouvee sur la plaque a indexer
        //=>dernier puit si on a trié  dans l'ordre "colonne d'abord"
        
        var last=dataMain.slice(-1)[0];
        var maxcol=last.atomicTransfertMethod.column*1;
        console.log("last col in input plate :"+maxcol);
        console.log("selected index plate :" +$scope.tagPlateColumn.name);
        console.log("selected index column :" +$scope.tagPlateColumn.name);
        
        if  ($scope.tagPlateColumn.name*1 + maxcol > 13 ){		
			$scope.messages.clazz="alert alert-danger";
			//$scope.messages.text=Messages('select.WrongStartColumnTagPlate'+ " "+$scope.tagPlateColumn.position +"+"+dataMain.length+"="+ ($scope.tagPlateColumn.position + dataMain.length));
			$scope.messages.text=Messages('select.msg.error.wrongStartColumn.tagPlate', $scope.tagPlateColumn.name); // en attendant modif de l'algo
			$scope.messages.showDetails = false;
			$scope.messages.open();	
			return;
		}
       
	    for(var i = 0; i < dataMain.length; i++){
			var udtData = dataMain[i];
			var ocu=udtData.outputContainerUsed;
			//console.log("outputContainerUsed.code"+udtData.outputContainerUsed.code);
			
			if ($scope.tagPlateColumn.position != undefined ){
				//calculer la position sur la plaque:   pos= (col -1)*8 + line      (line est le code ascii - 65)
				var libPos= (udtData.atomicTransfertMethod.column  -1 )*8 + ( udtData.atomicTransfertMethod.line.charCodeAt(0) -65);
				var indexPos= libPos + $scope.tagPlateColumn.position;
				//console.log("=> setting index "+indexPos+ ": "+ tagPlateCode[indexPos] );
				
				//ajouter dans experimentProperties les PSV tagCategory et tag
				var ocu=udtData.outputContainerUsed;
				if(ocu.experimentProperties===undefined || ocu.experimentProperties===null){
					ocu.experimentProperties={};
				}				
				// 21/11/2017 modification pour possibilité d'utilisation plusieurs plaques
				ocu.experimentProperties["tag"]={"_type":"single","value":$scope.tagPlate.tags[indexPos]};
				ocu.experimentProperties["tagCategory"]={"_type":"single","value":$scope.tagPlate.tagCategory};

			} else {
				//l'utilisateur n'a rien selectionné => suprimer les PSV tagCategory et tagCode 
				ocu.experimentProperties["tag"]= undefined;
				ocu.experimentProperties["tagCategory"]=undefined;
			}
		}	
	    atmService.data.setData(dataMain);
	};
	*/
	//NGL-2012 - 04/05/2018: Nvel algorithme plus générique, capable de gérer des plaques d'index incomplètes...(repris de small-rnaseq-lib-prep-ctrl.js)
	//TODO ==> algorithme utilisé dans 6 experiences: mettre dans un service pour eviter duplication !!!!
	var setTags = function(p){
		$scope.messages.clear();
			
		console.log("selected plate is "+ $scope.tagPlate.name);
		console.log("selected start column is " + $scope.tagPlateColumn.name);
		console.log("selected start position is " + $scope.tagPlateColumn.position);
		
		var dataMain = atmService.data.getData();
		// trier dans l'ordre "colonne d'abord"
		var dataMain = $filter('orderBy')(dataMain, ['atomicTransfertMethod.column*1','atomicTransfertMethod.line']); 

		if (($scope.tagPlateColumn.name === '---' ) && ($scope.tagPlate.name === '---')){
			// remise a 0 des selects par l'utilisateur ????=> nettoyage de ce qui a ete positionné precedemment
			console.log("suppression des index ...");
				
			for(var i = 0; i < dataMain.length; i++){
				var udtData = dataMain[i];
				var ocu=udtData.outputContainerUsed;
				ocu.experimentProperties["tag"]= undefined;
				ocu.experimentProperties["tagCategory"]=undefined;
			}	
			atmService.data.setData(dataMain);	
			
		} else if (($scope.tagPlateColumn.name !== '---' ) && ($scope.tagPlate.name !== '---')){	
			
			//attention certains choix de colonne sont incorrrects !!! 
			//le controle doit porter sur la valeur maximale de colonne trouvee sur la plaque a indexer
			//=>dernier puit si on a trié  dans l'ordre "colonne d'abord"
			var last=dataMain.slice(-1)[0];
			var lastInputCol=last.atomicTransfertMethod.column*1;
			console.log("last col in input plate="+ lastInputCol);
			
			var lastTagCol=$scope.tagPlate.tags.length / 8;    // ce sont des colonnes de 8
			console.log("last col in tag plate="+ lastTagCol);
			
			// meme en prennant tous les index possibles, il n'y en a pas assez dans la plaque !!
			if ( lastTagCol < lastInputCol ){
	        	$scope.messages.clazz="alert alert-danger";
	        	$scope.messages.text=Messages('select.msg.error.notEnoughTags.tagPlate',$scope.tagPlate.name);
	        	$scope.messages.showDetails = false;
	        	$scope.messages.open();
	        	return;
			}
			
			// la colonne de debut choisie est vide
			if ( $scope.tagPlateColumn.name*1 > lastTagCol){
	        	$scope.messages.clazz="alert alert-danger";
	        	$scope.messages.text=Messages('select.msg.error.emptyStartColumn.tagPlate', $scope.tagPlateColumn.name, $scope.tagPlate.name );
	        	$scope.messages.showDetails = false;
	        	$scope.messages.open();	
	        	return;
	        }
				
			// la colonne choisie est incorrecte (toutes les puits input ne recevront pas d'index) !!INTERDIT
		    if ( (lastTagCol - $scope.tagPlateColumn.name*1  +1) < lastInputCol ) {   	
	        	$scope.messages.clazz="alert alert-danger";
	        	$scope.messages.text=Messages('select.msg.error.wrongStartColumn.tagPlate', $scope.tagPlateColumn.name);
	        	$scope.messages.showDetails = false;
	        	$scope.messages.open();	
	        	return;
	        }
	
			for(var i = 0; i < dataMain.length; i++){
				var udtData = dataMain[i];
				var ocu=udtData.outputContainerUsed;
				//console.log("outputContainerUsed.code"+udtData.outputContainerUsed.code);

				//calculer la position sur la plaque:   pos= (col -1)*8 + line      (line est le code ascii - 65)
				var libPos= (udtData.atomicTransfertMethod.column  -1 )*8 + ( udtData.atomicTransfertMethod.line.charCodeAt(0) -65);
				//console.log("lib pos=" +libPos);
				var indexPos= libPos + $scope.tagPlateColumn.position; 
				//console.log("index pos="+indexPos);
				console.log("=> setting index "+indexPos+ ": "+ $scope.tagPlate.tags[indexPos] );
				
				//ajouter dans experimentProperties les PSV tagCategory et tag
				var ocu=udtData.outputContainerUsed;
				if(ocu.experimentProperties===undefined || ocu.experimentProperties===null){
					ocu.experimentProperties={};
				}
				
				// attention aux positions non definies des plaques d'index ( plaques de 48..) /// ne doit plus arriver avec les tests initiaux...
				// reste le cas possible de plan d'index avec des trous ???
				if ( $scope.tagPlate.tags[indexPos] !== undefined) {
					ocu.experimentProperties["tag"]={"_type":"single","value":$scope.tagPlate.tags[indexPos]};
					ocu.experimentProperties["tagCategory"]={"_type":"single","value":$scope.tagPlate.tagCategory};
				}
			}	
			
			atmService.data.setData(dataMain);
		}
		// dans le dernier cas rien a faire...
	};
	
	
	// NGL-2012
	$scope.selectColOrPlate = {
		isShow:function(){
			return ( ($scope.isInProgressState() && !$scope.mainService.isEditMode())|| Permissions.check("admin") );
		},	
		select:setTags
	};
}]);
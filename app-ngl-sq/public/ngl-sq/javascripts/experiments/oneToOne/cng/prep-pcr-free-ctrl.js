// FDS 04/02/2016 -- JIRA NGL-894 : prep pcr free experiment
// 16/09/2016 commun a prepprcfree et prepwgnano ???NON
angular.module('home').controller('PrepPcrFreeCtrl',['$scope', '$parse',  '$filter', 'atmToSingleDatatable','$http',
                                                     function($scope, $parse, $filter, atmToSingleDatatable, $http){

	
	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	// NGL-1055: name explicite pour fichier CSV exporté: typeCode experience
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),
			//Guillaume le 04/03 => utiliser containerUsed seulement pour proprietes dynamiques...
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
				"active":true
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
                	+'<button class="btn btn-default" ng-click="copyVolumeInToExp()" data-toggle="tooltip" title="'+Messages("experiments.button.plate.copyVolumeTo")+' vol. eng. librairie ET vol. eng. fragmentation'
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
	
    // 24/11/2016 FDS copier le volume containerIn dans le volume engagé Librairie ET volume Engagé Frag...
	//     code adapté depuis copyVolumeInToOut de x-to-plates-ctrl.js
	$scope.copyVolumeInToExp = function(){
		console.log("copyVolumeInToExp");
		
		var data = $scope.atmService.data.displayResult;		
		data.forEach(function(value){
			
			if ( !value.data.inputContainerUsed.experimentProperties ){
				value.data.inputContainerUsed.experimentProperties = {};
			}
			value.data.inputContainerUsed.experimentProperties.inputVolumeLib=value.data.inputContainerUsed.volume;
			value.data.inputContainerUsed.experimentProperties.inputVolumeFrag=value.data.inputContainerUsed.volume;	
		})		
	};
		
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
	}
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
	
	
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
			return ( $scope.isInProgressState() && !$scope.mainService.isEditMode())
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData,		
	};
	
	// Autre mode possible : utiliser une plaque d'index prédéfinis, l'utilisateur a juste a indiquer a partir de quelle colonne
	// de cette plaque le robot doit prelever les index
	$scope.columns = [ {name:'---', position:-1 },
	                   {name:'1', position:0}, {name:'2', position:8}, {name:'3', position:16}, {name:'4',  position:24}, {name:'5',  position:32}, {name:'6',  position:40},
	                   {name:'7', position:48},{name:'8', position:56},{name:'9', position:64}, {name:'10', position:72}, {name:'11', position:80}, {name:'12', position:88},
	                 ];
	$scope.tagPlateColumn = $scope.columns[0]; // defaut du select
	
	// 17/11/2017 modifications pour possibilité d'utiliser plusieurs plaques
	$scope.plates = [ {name:"DAP TruSeq DNA HT",   tagCategory:"DUAL-INDEX", tags:[] }, 
	                  {name:"IDT-ILMN TruSeq DNA UD Indexes (96 Indexes)", tagCategory:"DUAL-INDEX", tags:[] },
	                  {name:"IDT-ILMN TruSeq DNA UD Indexes (24 Indexes)", tagCategory:"DUAL-INDEX", tags:[] }];
	
	// l'indice dans le tableau correspond a l'ordre "colonne d'abord" dans la plaque
    // c'est le code des index qu'il faut mettre ici ??? exemple:  AglSSXT-01(name)/aglSSXT-01(code) 
	
	//-1- DAP TruSeq DNA HT
	$scope.plates[0].tags.push("D701-D501", "D701-D502", "D701-D503", "D701-D504", "D701-D505", "D701-D506", "D701-D507", "D701-D508"); //colonne 1
	$scope.plates[0].tags.push("D702-D501", "D702-D502", "D702-D503", "D702-D504", "D702-D505", "D702-D506", "D702-D507", "D702-D508"); //colonne 2
	$scope.plates[0].tags.push("D703-D501", "D703-D502", "D703-D503", "D703-D504", "D703-D505", "D703-D506", "D703-D507", "D703-D508"); //colonne 3
	$scope.plates[0].tags.push("D704-D501", "D704-D502", "D704-D503", "D704-D504", "D704-D505", "D704-D506", "D704-D507", "D704-D508"); //colonne 4
	$scope.plates[0].tags.push("D705-D501", "D705-D502", "D705-D503", "D705-D504", "D705-D505", "D705-D506", "D705-D507", "D705-D508"); //colonne 5
	$scope.plates[0].tags.push("D706-D501", "D706-D502", "D706-D503", "D706-D504", "D706-D505", "D706-D506", "D706-D507", "D706-D508"); //colonne 6
	$scope.plates[0].tags.push("D707-D501", "D707-D502", "D707-D503", "D707-D504", "D707-D505", "D707-D506", "D707-D507", "D707-D508"); //colonne 7
	$scope.plates[0].tags.push("D708-D501", "D708-D502", "D708-D503", "D708-D504", "D708-D505", "D708-D506", "D708-D507", "D708-D508"); //colonne 8
	$scope.plates[0].tags.push("D709-D501", "D709-D502", "D709-D503", "D709-D504", "D709-D505", "D709-D506", "D709-D507", "D709-D508"); //colonne 9
	$scope.plates[0].tags.push("D710-D501", "D710-D502", "D710-D503", "D710-D504", "D710-D505", "D710-D506", "D710-D507", "D710-D508"); //colonne 10
	$scope.plates[0].tags.push("D711-D501", "D711-D502", "D711-D503", "D711-D504", "D711-D505", "D711-D506", "D711-D507", "D711-D508"); //colonne 11
	$scope.plates[0].tags.push("D712-D501", "D712-D502", "D712-D503", "D712-D504", "D712-D505", "D712-D506", "D712-D507", "D712-D508"); //colonne 12
	
    //-2- IDT-ILMN TruSeq DNA UD Indexes (96 Indexes)
    $scope.plates[1].tags.push("UDI0001_I7-UDI0001_I5","UDI0002_I7-UDI0002_I5","UDI0003_I7-UDI0003_I5","UDI0004_I7-UDI0004_I5","UDI0005_I7-UDI0005_I5","UDI0006_I7-UDI0006_I5","UDI0007_I7-UDI0007_I5","UDI0008_I7-UDI0008_I5"); //colonne 1
    $scope.plates[1].tags.push("UDI0009_I7-UDI0009_I5","UDI0010_I7-UDI0010_I5","UDI0011_I7-UDI0011_I5","UDI0012_I7-UDI0012_I5","UDI0013_I7-UDI0013_I5","UDI0014_I7-UDI0014_I5","UDI0015_I7-UDI0015_I5","UDI0016_I7-UDI0016_I5"); //colonne 2
    $scope.plates[1].tags.push("UDI0017_I7-UDI0017_I5","UDI0018_I7-UDI0018_I5","UDI0019_I7-UDI0019_I5","UDI0020_I7-UDI0020_I5","UDI0021_I7-UDI0021_I5","UDI0022_I7-UDI0022_I5","UDI0023_I7-UDI0023_I5","UDI0024_I7-UDI0024_I5"); //colonne 3
    $scope.plates[1].tags.push("UDI0025_I7-UDI0025_I5","UDI0026_I7-UDI0026_I5","UDI0027_I7-UDI0027_I5","UDI0028_I7-UDI0028_I5","UDI0029_I7-UDI0029_I5","UDI0030_I7-UDI0030_I5","UDI0031_I7-UDI0031_I5","UDI0032_I7-UDI0032_I5"); //colonne 4
    $scope.plates[1].tags.push("UDI0033_I7-UDI0033_I5","UDI0034_I7-UDI0034_I5","UDI0035_I7-UDI0035_I5","UDI0036_I7-UDI0036_I5","UDI0037_I7-UDI0037_I5","UDI0038_I7-UDI0038_I5","UDI0039_I7-UDI0039_I5","UDI0040_I7-UDI0040_I5"); //colonne 5
    $scope.plates[1].tags.push("UDI0041_I7-UDI0041_I5","UDI0042_I7-UDI0042_I5","UDI0043_I7-UDI0043_I5","UDI0044_I7-UDI0044_I5","UDI0045_I7-UDI0045_I5","UDI0046_I7-UDI0046_I5","UDI0047_I7-UDI0047_I5","UDI0048_I7-UDI0048_I5"); //colonne 6
    $scope.plates[1].tags.push("UDI0049_I7-UDI0049_I5","UDI0050_I7-UDI0050_I5","UDI0051_I7-UDI0051_I5","UDI0052_I7-UDI0052_I5","UDI0053_I7-UDI0053_I5","UDI0054_I7-UDI0054_I5","UDI0055_I7-UDI0055_I5","UDI0056_I7-UDI0056_I5"); //colonne 7
    $scope.plates[1].tags.push("UDI0057_I7-UDI0057_I5","UDI0058_I7-UDI0058_I5","UDI0059_I7-UDI0059_I5","UDI0060_I7-UDI0060_I5","UDI0061_I7-UDI0061_I5","UDI0062_I7-UDI0062_I5","UDI0063_I7-UDI0063_I5","UDI0064_I7-UDI0064_I5"); //colonne 8
    $scope.plates[1].tags.push("UDI0065_I7-UDI0065_I5","UDI0066_I7-UDI0066_I5","UDI0067_I7-UDI0067_I5","UDI0068_I7-UDI0068_I5","UDI0069_I7-UDI0069_I5","UDI0070_I7-UDI0070_I5","UDI0071_I7-UDI0071_I5","UDI0072_I7-UDI0072_I5"); //colonne 9
    $scope.plates[1].tags.push("UDI0073_I7-UDI0073_I5","UDI0074_I7-UDI0074_I5","UDI0075_I7-UDI0075_I5","UDI0076_I7-UDI0076_I5","UDI0077_I7-UDI0077_I5","UDI0078_I7-UDI0078_I5","UDI0079_I7-UDI0079_I5","UDI0080_I7-UDI0080_I5"); //colonne 10
    $scope.plates[1].tags.push("UDI0081_I7-UDI0081_I5","UDI0082_I7-UDI0082_I5","UDI0083_I7-UDI0083_I5","UDI0084_I7-UDI0084_I5","UDI0085_I7-UDI0085_I5","UDI0086_I7-UDI0086_I5","UDI0087_I7-UDI0087_I5","UDI0088_I7-UDI0088_I5"); //colonne 11
    $scope.plates[1].tags.push("UDI0089_I7-UDI0089_I5","UDI0090_I7-UDI0090_I5","UDI0091_I7-UDI0091_I5","UDI0092_I7-UDI0092_I5","UDI0093_I7-UDI0093_I5","UDI0094_I7-UDI0094_I5","UDI0095_I7-UDI0095_I5","UDI0096_I7-UDI0096_I5"); //colonne 12

    //-3- IDT-ILMN TruSeq DNA UD Indexes (24 Indexes)
    $scope.plates[2].tags.push("UDI0001_I7-UDI0001_I5","UDI0002_I7-UDI0002_I5","UDI0003_I7-UDI0003_I5","UDI0004_I7-UDI0004_I5","UDI0005_I7-UDI0005_I5","UDI0006_I7-UDI0006_I5","UDI0007_I7-UDI0007_I5","UDI0008_I7-UDI0008_I5"); //colonne 1
    $scope.plates[2].tags.push("UDI0009_I7-UDI0009_I5","UDI0010_I7-UDI0010_I5","UDI0011_I7-UDI0011_I5","UDI0012_I7-UDI0012_I5","UDI0013_I7-UDI0013_I5","UDI0014_I7-UDI0014_I5","UDI0015_I7-UDI0015_I5","UDI0016_I7-UDI0016_I5"); //colonne 2
    $scope.plates[2].tags.push("UDI0017_I7-UDI0017_I5","UDI0018_I7-UDI0018_I5","UDI0019_I7-UDI0019_I5","UDI0020_I7-UDI0020_I5","UDI0021_I7-UDI0021_I5","UDI0022_I7-UDI0022_I5","UDI0023_I7-UDI0023_I5","UDI0024_I7-UDI0024_I5"); //colonne 3
    $scope.plates[2].tags.push("UDI0001_I7-UDI0001_I5","UDI0002_I7-UDI0002_I5","UDI0003_I7-UDI0003_I5","UDI0004_I7-UDI0004_I5","UDI0005_I7-UDI0005_I5","UDI0006_I7-UDI0006_I5","UDI0007_I7-UDI0007_I5","UDI0008_I7-UDI0008_I5"); //colonne 4
    $scope.plates[2].tags.push("UDI0009_I7-UDI0009_I5","UDI0010_I7-UDI0010_I5","UDI0011_I7-UDI0011_I5","UDI0012_I7-UDI0012_I5","UDI0013_I7-UDI0013_I5","UDI0014_I7-UDI0014_I5","UDI0015_I7-UDI0015_I5","UDI0016_I7-UDI0016_I5"); //colonne 5
    $scope.plates[2].tags.push("UDI0017_I7-UDI0017_I5","UDI0018_I7-UDI0018_I5","UDI0019_I7-UDI0019_I5","UDI0020_I7-UDI0020_I5","UDI0021_I7-UDI0021_I5","UDI0022_I7-UDI0022_I5","UDI0023_I7-UDI0023_I5","UDI0024_I7-UDI0024_I5"); //colonne 6
    $scope.plates[2].tags.push("UDI0001_I7-UDI0001_I5","UDI0002_I7-UDI0002_I5","UDI0003_I7-UDI0003_I5","UDI0004_I7-UDI0004_I5","UDI0005_I7-UDI0005_I5","UDI0006_I7-UDI0006_I5","UDI0007_I7-UDI0007_I5","UDI0008_I7-UDI0008_I5"); //colonne 7
    $scope.plates[2].tags.push("UDI0009_I7-UDI0009_I5","UDI0010_I7-UDI0010_I5","UDI0011_I7-UDI0011_I5","UDI0012_I7-UDI0012_I5","UDI0013_I7-UDI0013_I5","UDI0014_I7-UDI0014_I5","UDI0015_I7-UDI0015_I5","UDI0016_I7-UDI0016_I5"); //colonne 8
    $scope.plates[2].tags.push("UDI0017_I7-UDI0017_I5","UDI0018_I7-UDI0018_I5","UDI0019_I7-UDI0019_I5","UDI0020_I7-UDI0020_I5","UDI0021_I7-UDI0021_I5","UDI0022_I7-UDI0022_I5","UDI0023_I7-UDI0023_I5","UDI0024_I7-UDI0024_I5"); //colonne 9
    $scope.plates[2].tags.push("UDI0001_I7-UDI0001_I5","UDI0002_I7-UDI0002_I5","UDI0003_I7-UDI0003_I5","UDI0004_I7-UDI0004_I5","UDI0005_I7-UDI0005_I5","UDI0006_I7-UDI0006_I5","UDI0007_I7-UDI0007_I5","UDI0008_I7-UDI0008_I5"); //colonne 10
    $scope.plates[2].tags.push("UDI0009_I7-UDI0009_I5","UDI0010_I7-UDI0010_I5","UDI0011_I7-UDI0011_I5","UDI0012_I7-UDI0012_I5","UDI0013_I7-UDI0013_I5","UDI0014_I7-UDI0014_I5","UDI0015_I7-UDI0015_I5","UDI0016_I7-UDI0016_I5"); //colonne 11
    $scope.plates[2].tags.push("UDI0017_I7-UDI0017_I5","UDI0018_I7-UDI0018_I5","UDI0019_I7-UDI0019_I5","UDI0020_I7-UDI0020_I5","UDI0021_I7-UDI0021_I5","UDI0022_I7-UDI0022_I5","UDI0023_I7-UDI0023_I5","UDI0024_I7-UDI0024_I5"); //colonne 12

	$scope.tagPlate = $scope.plates[0]; // defaut du select
	
	var setTags = function(){
		$scope.messages.clear();
		//console.log("selected column=" +$scope.tagPlateColumn.name);
		
        var dataMain = atmService.data.getData();
        // trier dans l'ordre "colonne d'abord"
        var dataMain = $filter('orderBy')(dataMain, ['atomicTransfertMethod.column*1','atomicTransfertMethod.line']);
        
        //attention certains choix de colonne sont incorrrects !!!
        // 26/10/2017 NGL-1671: le controle doit porter sur la valeur maximale de colonne trouvee sur la plaque a indexer
        //=>dernier puit si on a trié  dans l'ordre "colonne d'abord"
        
        var last=dataMain.slice(-1)[0];
        var maxcol=last.atomicTransfertMethod.column*1;
        console.log("last col in input plate="+maxcol);
        
        if  ($scope.tagPlateColumn.name*1 + maxcol > 13 ){			
			$scope.messages.clazz="alert alert-danger";
			//$scope.messages.text=Messages('select.WrongStartColumnTagPlate'+ " "+$scope.tagPlateColumn.position +"+"+dataMain.length+"="+ ($scope.tagPlateColumn.position + dataMain.length));
			$scope.messages.text=Messages('select.wrongStartColumnTagPlate');
			$scope.messages.showDetails = false;
			$scope.messages.open();	
			return;
		}
       
	    for(var i = 0; i < dataMain.length; i++){
			var udtData = dataMain[i];
			var ocu=udtData.outputContainerUsed;
			//console.log("outputContainerUsed.code"+udtData.outputContainerUsed.code);
			
			if ($scope.tagPlateColumn.position != -1 ){
				//calculer la position sur la plaque:   pos= (col -1)*8 + line      (line est le code ascii - 65)
				var libPos= (udtData.atomicTransfertMethod.column  -1 )*8 + ( udtData.atomicTransfertMethod.line.charCodeAt(0) -65);
				var indexPos= libPos + $scope.tagPlateColumn.position;
				//console.log("=> setting index "+indexPos+ ": "+ tagPlateCode[indexPos] );
				
				//ajouter dans experimentProperties les PSV tagCategory et tag
				var ocu=udtData.outputContainerUsed;
				if(ocu.experimentProperties===undefined || ocu.experimentProperties===null){
					ocu.experimentProperties={};
				}				
				// 17/11/2017 modification pour possibilité d'utilisation plusieurs plaques
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
	
	$scope.selectCol = {
		isShow:function(){
			return ( $scope.isInProgressState() && !$scope.mainService.isEditMode())
			},	
		select:setTags,
	};
	
	// 24/11/2016  ajouté comme dans prep-wg-nano pour remplacer les valeurs par defaut dans la definition de l'experience
	// calculer les qtés inputQuantityFrag et inputQuantityLib a partir de inputVolumeFrag et inputVolumeLib
	// updatePropertyFromUDT  est automatiqut defini pour les colonnes injectees dans le datatable....
	$scope.updatePropertyFromUDT = function(value, col){
		//console.log("update from property : "+col.property);

		if(col.property === 'inputContainerUsed.experimentProperties.inputVolumeFrag.value'){
			// verifier si le volume saisi est > au volume IN:  si oui ecraser le volume saisi par volume IN
			// TODO...?? plus tard
			computeQuantityFrag(value.data);
		}
		
		if(col.property === 'inputContainerUsed.experimentProperties.inputVolumeLib.value'){
			// verifier si le volume saisi est > au volume IN:  si oui ecraser le volume saisi par volume IN
			// TODO...?? plus tard
			computeQuantityLib(value.data);
		}
	}

	//inputQuantity=inputContainerUsed.concentration.value * inputContainerUsed.experimentProperties.inputVolume.value
	var computeQuantityFrag = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.inputQuantityFrag.value");
		var getter2 = $parse("inputContainerUsed.experimentProperties.inputVolumeLib.value");

		var compute = {
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				inputVolume : $parse("inputContainerUsed.experimentProperties.inputVolumeFrag.value")(udtData),		
				isReady:function(){
					// traiter le cas ou il y a 1 des 2 valeurs (en general c'est la conc) est a 0
					return (this.inputConc >= 0  && this.inputVolume >= 0);
				}
			};
		
		if(compute.isReady()){
			var result = $parse("inputConc * inputVolume")(compute);
			//console.log("result = "+result);
			
			if(angular.isNumber(result) && !isNaN(result)){
				inputQuantity = Math.round(result*10)/10;				
			}else{
				//inputQuantity = undefined;
				inputQuantity = 0;
			}	
			getter.assign(udtData, inputQuantity);
			
			//copie inputVolumeLib--> inputVolumeFrag
			getter2.assign(udtData, $parse("inputVolume")(compute));
			
		}else{
			console.log("Missing values to exec computeQuantityFrag");
		}
	}
	
	/// TODO: faire une seule fonction mais avec un parametre Lib ou Frag
	var computeQuantityLib = function(udtData){
		var getter = $parse("inputContainerUsed.experimentProperties.inputQuantityLib.value");

		var compute = {
				inputConc : $parse("inputContainerUsed.concentration.value")(udtData),
				inputVolume : $parse("inputContainerUsed.experimentProperties.inputVolumeLib.value")(udtData),		
				isReady:function(){
					// traiter le cas ou il y a 1 des 2 valeurs (en general c'est la conc) est a 0
					return (this.inputConc >= 0 && this.inputVolume >= 0 );
				}
			};
		
		if(compute.isReady()){
			var result = $parse("inputConc * inputVolume")(compute);
			console.log("result = "+result);
			
			if(angular.isNumber(result) && !isNaN(result)){
				inputQuantity = Math.round(result*10)/10;				
			}else{
				///inputQuantity = undefined;\\
				inputQuantity = 0;
			}	
			getter.assign(udtData, inputQuantity);
			
		}else{
			console.log("Missing values to exec computeQuantityLib");
		}
	}	

    
}]);
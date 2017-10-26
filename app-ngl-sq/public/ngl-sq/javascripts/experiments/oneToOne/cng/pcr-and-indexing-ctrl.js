// FDS 01/08/2017 - copiee depuis library-prep-ctrl
//     10/10/2017 1er essai d'utilisationnde multiplaque: OK mais il faudra passer par un service pour eviter la duplication de code...
angular.module('home').controller('PcrAndIndexingCtrl',['$scope', '$parse',  '$filter', 'atmToSingleDatatable','$http',
                                                     function($scope, $parse, $filter, atmToSingleDatatable, $http){
	
	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),

			"columns":[
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
				     /*
				     { // sampleAliquoteCode 
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
					*/
				     { // 31/08/2017 niveau process ET contents =>utiliser properties et pas processProperties; 04/09/2017 si filtre codes:'value' alors header =>libProcessType
				       "header": Messages("containers.table.libProcessType"),
				       "property" : "inputContainerUsed.contents",
				       "filter" : "getArray:'properties.libProcessTypeCode.value' |unique | codes:'value'",
				       "order":true,
					   "edit":false,
					   "hide":true,
				       "type":"text",
				       "position":8.2,
				       "extraHeaders":{0:inputExtraHeaders}
				     },
				     { // 31/08/2017 baits rellement utilisees (mises dans l'experience precedente capture) => outputContainerUsed.contents
				       "header": Messages("containers.table.baits"),
				      	"property" : "outputContainerUsed.contents",
				      	"filter" : "getArray:'properties.baits.value' | unique | codes:'value'",
				      	"order":true,
					    "edit":false,
					    "hide":true,
				      	"type":"text",
				      	"position":8.4,
				      	"extraHeaders":{0:inputExtraHeaders}
				     },
				     { // 31/08/2017 niveau process ET contents =>utiliser properties et pas processProperties
				        "header":  Messages("containers.table.captureProtocol"),
				      	"property" : "inputContainerUsed.contents",
				      	"filter" : "getArray:'properties.captureProtocol.value' | unique | codes:'value'",
				      	"order":true,
						"edit":false,
					    "hide":true,
				      	"type":"text",
				      	"position":8.6,
				      	"extraHeaders":{0:inputExtraHeaders}
				     },	         
			         { // Etat input Container 
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":9,
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
				//30/08/2017 plus de volume in donc plus besoin de bouton de copie de volume...
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
	
	// 10/10/2017 modifications pour possibilité d'utiliser plusieurs plaques
	/*$scope.plates = [ {name:"DAP TruSeq DNA HT",   tagCategory:"DUAL-INDEX", tags:[] }, 
	                  {name:"DAP TruSeq DNA HT-X", tagCategory:"DUAL-INDEX", tags:[] } ];/// nom et composition pas encore données par la la prod....
	*/
	$scope.plates = [ {name:"DAP TruSeq DNA HT",   tagCategory:"DUAL-INDEX", tags:[] } ];

	// l'indice dans le tableau correspond a l'ordre "colonne d'abord" dans la plaque
	// plaque originale "DAP TruSeq DNA HT"
	$scope.plates[0].tags.push("D701-D501", "D701-D502", "D701-D503", "D701-D504", "D701-D505", "D701-D506", "D701-D507", "D701-D508");
	$scope.plates[0].tags.push("D702-D501", "D702-D502", "D702-D503", "D702-D504", "D702-D505", "D702-D506", "D702-D507", "D702-D508");
	$scope.plates[0].tags.push("D703-D501", "D703-D502", "D703-D503", "D703-D504", "D703-D505", "D703-D506", "D703-D507", "D703-D508");
	$scope.plates[0].tags.push("D704-D501", "D704-D502", "D704-D503", "D704-D504", "D704-D505", "D704-D506", "D704-D507", "D704-D508"); 
	$scope.plates[0].tags.push("D705-D501", "D705-D502", "D705-D503", "D705-D504", "D705-D505", "D705-D506", "D705-D507", "D705-D508"); 
	$scope.plates[0].tags.push("D706-D501", "D706-D502", "D706-D503", "D706-D504", "D706-D505", "D706-D506", "D706-D507", "D706-D508"); 
	$scope.plates[0].tags.push("D707-D501", "D707-D502", "D707-D503", "D707-D504", "D707-D505", "D707-D506", "D707-D507", "D707-D508"); 
	$scope.plates[0].tags.push("D708-D501", "D708-D502", "D708-D503", "D708-D504", "D708-D505", "D708-D506", "D708-D507", "D708-D508"); 
	$scope.plates[0].tags.push("D709-D501", "D709-D502", "D709-D503", "D709-D504", "D709-D505", "D709-D506", "D709-D507", "D709-D508"); 
	$scope.plates[0].tags.push("D710-D501", "D710-D502", "D710-D503", "D710-D504", "D710-D505", "D710-D506", "D710-D507", "D710-D508"); 
	$scope.plates[0].tags.push("D711-D501", "D711-D502", "D711-D503", "D711-D504", "D711-D505", "D711-D506", "D711-D507", "D711-D508"); 
	$scope.plates[0].tags.push("D712-D501", "D712-D502", "D712-D503", "D712-D504", "D712-D505", "D712-D506", "D712-D507", "D712-D508"); 

	/* 10/10/2017 TEST 
	$scope.plates[1].tags.push("X701-X501", "X701-X502", "X701-X503", "X701-X504", "X701-X505", "X701-X506", "X701-X507", "X701-X508");
	$scope.plates[1].tags.push("X702-X501", "X702-X502", "X702-X503", "X702-X504", "X702-X505", "X702-X506", "X702-X507", "X702-X508");
	$scope.plates[1].tags.push("X703-X501", "X703-X502", "X703-X503", "X703-X504", "X703-X505", "X703-X506", "X703-X507", "X703-X508");
	$scope.plates[1].tags.push("X704-X501", "X704-X502", "X704-X503", "X704-X504", "X704-X505", "X704-X506", "X704-X507", "X704-X508"); 
	$scope.plates[1].tags.push("X705-X501", "X705-X502", "X705-X503", "X705-X504", "X705-X505", "X705-X506", "X705-X507", "X705-X508"); 
	$scope.plates[1].tags.push("X706-X501", "X706-X502", "X706-X503", "X706-X504", "X706-X505", "X706-X506", "X706-X507", "X706-X508"); 
	$scope.plates[1].tags.push("X707-X501", "X707-X502", "X707-X503", "X707-X504", "X707-X505", "X707-X506", "X707-X507", "X707-X508"); 
	$scope.plates[1].tags.push("X708-X501", "X708-X502", "X708-X503", "X708-X504", "X708-X505", "X708-X506", "X708-X507", "X708-X508"); 
	$scope.plates[1].tags.push("X709-X501", "X709-X502", "X709-X503", "X709-X504", "X709-X505", "X709-X506", "X709-X507", "X709-X508"); 
	$scope.plates[1].tags.push("X710-X501", "X710-X502", "X710-X503", "X710-X504", "X710-X505", "X710-X506", "X710-X507", "X710-X508"); 
	$scope.plates[1].tags.push("X711-X501", "X711-X502", "X711-X503", "X711-X504", "X711-X505", "X711-X506", "X711-X507", "X711-X508"); 
	$scope.plates[1].tags.push("X712-X501", "X712-X502", "X712-X503", "X712-X504", "X712-X505", "X712-X506", "X712-X507", "X712-X508"); 
	*/
	
	$scope.tagPlate = $scope.plates[0]; // defaut du select

	var setTags = function(){
		$scope.messages.clear();
		
		console.log("selected plate is "+ $scope.tagPlate.name);
		console.log("selected column is " + $scope.tagPlateColumn.name);
		
        var dataMain = atmService.data.getData();
        // trier dans l'ordre "colonne d'abord"
        var dataMain = $filter('orderBy')(dataMain, ['atomicTransfertMethod.column*1','atomicTransfertMethod.line']);
        
        //attention certains choix de colonne sont incorrrects !!! 
        // 24/10/2017 NGL-1671: le controle doit porter sur la valeur maximale de colonne trouvee sur la plaque a indexer
        //=>dernier puit si on a trié  dans l'ordre "colonne d'abord"
         
        var last=dataMain.slice(-1)[0];
        var maxcol=last.atomicTransfertMethod.column*1;
        console.log("last col in input plate="+maxcol);
        
        if  ($scope.tagPlateColumn.name*1 + maxcol > 13 ){
        	$scope.messages.clazz="alert alert-danger";

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
				// 10/10/2017 modification pour possibilité d'utilisation plusieurs plaques
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
	
	$scope.selectColOrPlate = {
		isShow:function(){
			return ( $scope.isInProgressState() && !$scope.mainService.isEditMode())
			},	
		select:setTags,
	};
	
}]);
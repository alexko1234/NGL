// FDS 05/04/2018 - copiee depuis pcr-and-indexing-ctrl
angular.module('home').controller('SmallRNASeqLibPrepCtrl',['$scope', '$parse',  '$filter', 'atmToSingleDatatable','$http',
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
			         /*                     ???? PAS DEMANDE
				     { 
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
				     */       
			         { //Volume 
			        	 "header":Messages("containers.table.volume") + " (µL)", 
			        	 "property":"inputContainerUsed.volume.value",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6,
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
			         
		            /* ne pas afficher les containercodes  sauf pour DEBUG
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
			return ( ($scope.isInProgressState() && !$scope.mainService.isEditMode())|| Permissions.check("admin") );
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData	
	};
	
	// Autre mode possible : utiliser une plaque d'index prédéfinis, l'utilisateur a juste a indiquer a partir de quelle colonne
	// de cette plaque le robot doit prelever les index
	// !!!!!!!  CE SYSTEME EST CONCU POUR DES PLAQUES COMPLETES.... ET POUR LES PLAQUES 48 INDEX ???????????????

	$scope.columns = [ {name:'---', position: undefined },
	                   {name:'1', position:0}, {name:'2', position:8}, {name:'3', position:16}, {name:'4',  position:24}, {name:'5',  position:32}, {name:'6',  position:40},
	                   {name:'7', position:48},{name:'8', position:56},{name:'9', position:64}, {name:'10', position:72}, {name:'11', position:80}, {name:'12', position:88}
	                 ];
	
	$scope.tagPlateColumn = $scope.columns[0]; // defaut du select

	$scope.plates = [ {name:"",                             tagCategory: undefined,     tags: undefined },
	                  {name:"NEBNext small RNA plaque 48",  tagCategory:"SINGLE-INDEX", tags:[] }, 
	                  {name:"QiaSeq miRNA NGS 48 index",    tagCategory:"SINGLE-INDEX", tags:[] } 
	                ];

	// l'indice dans le tableau correspond a l'ordre "colonne d'abord" dans la plaque
	// !! ce sont les codes des index qu'il faut mettre ici !!

	// NEBNext small RNA plaque 48
	//								A         B         C         D         E         F         G         H
	$scope.plates[1].tags.push("neb-01", "neb-02", "neb-03", "neb-04", "neb-05", "neb-06", "neb-07", "neb-08"); //colonne 1
	$scope.plates[1].tags.push("neb-09", "neb-10", "neb-11", "neb-12", "neb-02", "neb-03", "neb-04", "neb-05"); //colonne 2
	$scope.plates[1].tags.push("neb-06", "neb-07", "neb-08", "neb-09", "neb-10", "neb-11", "neb-12", "neb-01"); //colonne 3
	$scope.plates[1].tags.push("neb-03", "neb-04", "neb-05", "neb-06", "neb-07", "neb-08", "neb-09", "neb-10"); //colonne 4
	$scope.plates[1].tags.push("neb-11", "neb-12", "neb-01", "neb-02", "neb-04", "neb-05", "neb-06", "neb-07"); //colonne 5
	$scope.plates[1].tags.push("neb-08", "neb-09", "neb-10", "neb-11", "neb-12", "neb-01", "neb-02", "neb-03"); //colonne 6
	
	//QiaSeq miRNA NGS 48 index
	//								A         B         C         D         E         F         G         H
	$scope.plates[2].tags.push("qmr-01", "qmr-02", "qmr-03", "qmr-04", "qmr-05", "qmr-06", "qmr-07", "qmr-08"); //colonne 1
	$scope.plates[2].tags.push("qmr-09", "qmr-10", "qmr-11", "qmr-12", "qmr-13", "qmr-14", "qmr-15", "qmr-16"); //colonne 2
	$scope.plates[2].tags.push("qmr-17", "qmr-18", "qmr-19", "qmr-20", "qmr-21", "qmr-22", "qmr-23", "qmr-24"); //colonne 3
	$scope.plates[2].tags.push("qmr-25", "qmr-26", "qmr-27", "qmr-28", "qmr-29", "qmr-30", "qmr-31", "qmr-32"); //colonne 4
	$scope.plates[2].tags.push("qmr-33", "qmr-34", "qmr-35", "qmr-36", "qmr-37", "qmr-38", "qmr-39", "qmr-40"); //colonne 5
	$scope.plates[2].tags.push("qmr-41", "qmr-42", "qmr-43", "qmr-44", "qmr-45", "qmr-46", "qmr-47", "qmr-48"); //colonne 6
	
	$scope.tagPlate = $scope.plates[0]; // defaut du select

	var setTags = function(){
		$scope.messages.clear();
			
		console.log("selected plate is "+ $scope.tagPlate.name);
		console.log("selected start column is " + $scope.tagPlateColumn.name);
		
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
			return ( ($scope.isInProgressState() && !$scope.mainService.isEditMode())|| Permissions.check("admin") );
		},	
		select:setTags()

	};
	
}]);
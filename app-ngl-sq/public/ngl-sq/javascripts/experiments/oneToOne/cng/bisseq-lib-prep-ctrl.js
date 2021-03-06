// FDS 09/04/2018 - copiee depuis pcr-and-indexing-ctrl
angular.module('home').controller('BisSeqLibPrepCtrl',['$scope', '$parse',  '$filter', 'atmToSingleDatatable','$http',
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
					 { // Concentration
			        	 "header":Messages("containers.table.concentration") + " (ng/µL)",
			        	 "property":"inputContainerUsed.concentration.value",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         }, 
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
	// NGL-2012 :Ajouter les permissions pour admin; supprimer condition sur EditMode
	$scope.button = {
		isShow:function(){
			return ( $scope.isInProgressState() || Permissions.check("admin") );
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

	$scope.plates = [ {name:"NUGEN Ovation Ultralow Methyl-Seq System 1-96",   tagCategory:"SINGLE-INDEX", tags:[] } ];

	// l'indice dans le tableau correspond a l'ordre "colonne d'abord" dans la plaque
	// !! ce sont les codes des index qu'il faut mettre ici !!
	//                             A         B         C         D         E         F         G         H
	$scope.plates[0].tags.push("nuo-01", "nuo-13", "nuo-25", "nuo-37", "nuo-49", "nuo-61", "nuo-73", "nuo-85"); //colonne 1
	$scope.plates[0].tags.push("nuo-02", "nuo-14", "nuo-26", "nuo-38", "nuo-50", "nuo-62", "nuo-74", "nuo-86"); //colonne 2
	$scope.plates[0].tags.push("nuo-03", "nuo-15", "nuo-27", "nuo-39", "nuo-51", "nuo-63", "nuo-75", "nuo-87"); //colonne 3
	$scope.plates[0].tags.push("nuo-04", "nuo-16", "nuo-28", "nuo-40", "nuo-52", "nuo-64", "nuo-76", "nuo-88"); //colonne 4
	$scope.plates[0].tags.push("nuo-05", "nuo-17", "nuo-29", "nuo-41", "nuo-53", "nuo-65", "nuo-77", "nuo-89"); //colonne 5
	$scope.plates[0].tags.push("nuo-06", "nuo-18", "nuo-30", "nuo-42", "nuo-54", "nuo-66", "nuo-78", "nuo-90"); //colonne 6
	$scope.plates[0].tags.push("nuo-07", "nuo-19", "nuo-31", "nuo-43", "nuo-55", "nuo-67", "nuo-79", "nuo-91"); //colonne 7	
	$scope.plates[0].tags.push("nuo-08", "nuo-20", "nuo-32", "nuo-44", "nuo-56", "nuo-68", "nuo-80", "nuo-92"); //colonne 8
	$scope.plates[0].tags.push("nuo-09", "nuo-21", "nuo-33", "nuo-45", "nuo-57", "nuo-69", "nuo-81", "nuo-93"); //colonne 9
	$scope.plates[0].tags.push("nuo-10", "nuo-22", "nuo-34", "nuo-46", "nuo-58", "nuo-70", "nuo-82", "nuo-94"); //colonne 10
	$scope.plates[0].tags.push("nuo-11", "nuo-23", "nuo-35", "nuo-47", "nuo-59", "nuo-71", "nuo-83", "nuo-95"); //colonne 11
	$scope.plates[0].tags.push("nuo-12", "nuo-24", "nuo-36", "nuo-48", "nuo-60", "nuo-72", "nuo-84", "nuo-96"); //colonne 12
	
	
	$scope.tagPlate = $scope.plates[0]; // defaut du select

	//NGL-2012 - 11/03/2018: Nvel algorithme plus générique, capable de gérer des plaques d'index incomplètes...(repris de small-rnaseq-lib-prep-ctrl.js)
	//TODO ==> algorithme utilisé dans 6 experiences: mettre dans un service pour eviter duplication !!!!
	var setTags = function(){
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
	
	// NGL-2012 :Ajouter les permissions pour admin; supprimer condition sur EditMode
	$scope.selectColOrPlate = {
		isShow:function(){
			return ( $scope.isInProgressState() || Permissions.check("admin") );
		},	
		select:setTags
	};
	
}]);
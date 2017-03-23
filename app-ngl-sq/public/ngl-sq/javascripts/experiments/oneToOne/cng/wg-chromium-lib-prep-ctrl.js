/* FDS 02/03/2017 -- JIRA NGL-1167 : processus Chromium
   code copié depuis library-prep-ctrl......==> utiliser plaque d'index Chromium????? Pas encore specifie...
   
   2 fonctionnements  -main     : strip-8       => tubes         ( Julie demande de bloquer   strip-8  et  96-well-plate pour l'instant...)
                      -sciclone : 96-well-plate => 96-well-plate
*/
angular.module('home').controller('WgChromiumLibraryPrepCtrl',['$scope', '$parse',  '$filter', 'atmToSingleDatatable','$http',
                                                     function($scope, $parse, $filter, atmToSingleDatatable, $http){
	
	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),

			"columns":[
			         //--------------------- INPUT containers section -----------------------
			         		        
			          { // barcode support entree
			        	 "header":Messages("containers.table.support.name"),
			        	 "property":"inputContainer.support.code",
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },    
			         // Ligne:  seulement pour plaques voir + loin
			         
			         { // colonne:  strip-8 ou plaque
			        	 "header":Messages("containers.table.support.column"),
			        	 // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
			        	 "property":"inputContainer.support.column*1",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":3,
			        	 "extraHeaders":{0: inputExtraHeaders}
			         },	
			         /*
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
			         */
			         
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
			        	 "position":500,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },	 */	     
			         { // Volume avec valeur par defaut
			        	 "header":Messages("containers.table.volume") + " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "hide":true,
			        	 "edit":true,
			        	 "type":"number",
			        	 "defaultValues":20,
			        	 "position":34,
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
			}
			/*,
			"otherButtons": {
                active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
                complex:true,
                template:''
                	+'<div class="btn-group" style="margin-left:5px">'
                	+'<button class="btn btn-default" ng-click="copyVolumeInToExp()" data-toggle="tooltip" title="'+Messages("experiments.button.plate.copyVolumeTo")+' vol. eng. librairie'
                	+'" ng-disabled="!isEditMode()" ng-if="experiment.instrument.outContainerSupportCategoryCode!==\'tube\'"><i class="fa fa-files-o" aria-hidden="true"></i> Volume </button>'                	                	
                	+'</div>'
             
			}*/
	}; // fin struct datatableConfig
	

	if ($scope.experiment.instrument.inContainerSupportCategoryCode == "96-well-plate") {
		datatableConfig.columns.push({
			// Ligne  seulement pour plaques 
			"header":Messages("containers.table.support.line"),
			"property":"inputContainer.support.line",
			"order":true,
			"hide":true,
			"type":"text",
			"position":2,
			"extraHeaders":{0: inputExtraHeaders}
		});
	}
	
	// probleme de rafraichissement de la vue en cas de mauvais choix inital de l'utilisateur
	// dans le watch  "$scope.experiment.instrument.categoryCode"   => forcer la vue ici...
	if ( $scope.experiment.instrument.categoryCode === "hand") {
		// tubes 
		datatableConfig.columns.push({
			"header" : Messages("containers.table.code"),
			"property" : "outputContainerUsed.locationOnContainerSupport.code",
			"order" : true,
			"edit" : true,
			"hide" : true,
			"type" : "text",
			"position" : 400,
			"extraHeaders" : {
				0 : Messages("experiments.outputs")
			}
		});
		datatableConfig.columns.push({
			//storage pour tubes
			"header" : Messages("containers.table.storageCode"),
			"property" : "outputContainerUsed.locationOnContainerSupport.storageCode",
			"order" : true,
			"edit" : true,
			"hide" : true,
			"type" : "text",
			"position" : 401,
			"extraHeaders" : {
				0 : Messages("experiments.outputs")
			}
		});
	} else {
		// l'autre cas pour l'instant est le sciclone qui n'a que des palques-96 en sortie
	
		datatableConfig.columns.push({
			// barcode plaque sortie == support Container used code
			"header" : Messages("containers.table.support.name"),
			"property" : "outputContainerUsed.locationOnContainerSupport.code",
			"hide" : true,
			"type" : "text",
			"position" : 400,
			"extraHeaders" : {
				0 : Messages("experiments.outputs")
			}
		});
		

		datatableConfig.columns.push({
			// Ligne
			"header" : Messages("containers.table.support.line"),
			"property" : "outputContainerUsed.locationOnContainerSupport.line",
			"edit" : true,
			"order" : true,
			"hide" : true,
			"type" : "text",
			"position" : 401,
			"extraHeaders" : {
				0 : Messages("experiments.outputs")
			}
		});
		
		datatableConfig.columns.push({
			// colonne
			"header" : Messages("containers.table.support.column"),
			// astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel
			// forcer a numerique.=> type:number, property: *1
			"property" : "outputContainerUsed.locationOnContainerSupport.column*1",
			"edit" : true,
			"order" : true,
			"hide" : true,
			"type" : "number",
			"position" : 402,
			"extraHeaders" : {
				0 : Messages("experiments.outputs")
			}
		});
	} 
	
	// en mode plaque ou strip uniquement !!!!!!
	var copyContainerSupportCodeAndStorageCodeToDT = function(datatable){		
		if($scope.experiment.instrument.outContainerSupportCategoryCode !== "tube") {
			var dataMain = datatable.getData();
		
			var outputContainerSupportCode = $scope.outputContainerSupport.code;
		
			if ( null != outputContainerSupportCode && undefined != outputContainerSupportCode){
				for(var i = 0; i < dataMain.length; i++){
				
					var atm = dataMain[i].atomicTransfertMethod;
					var newContainerCode = outputContainerSupportCode+"_"+atm.line + atm.column;

					$parse('outputContainerUsed.code').assign(dataMain[i],newContainerCode);
					$parse('outputContainerUsed.locationOnContainerSupport.code').assign(dataMain[i],outputContainerSupportCode);
				
					// Historique mais continuer a renseigner car effets de bord possible ????
					$parse('line').assign(atm, atm.line);
					$parse('column').assign(atm,atm.column );
					//console.log("atm.line="+ atm.line + " atm.column="+atm.column);	
				
					var outputContainerSupportStorageCode = $scope.outputContainerSupport.storageCode;
					if( null != outputContainerSupportStorageCode && undefined != outputContainerSupportStorageCode){
						$parse('outputContainerUsed.locationOnContainerSupport.storageCode').assign(dataMain[i],outputContainerSupportStorageCode);
					}
				}
			}		
		}
		// Ne plus faire ... datatable.setData(dataMain);
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
		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
		
		/// NECESSAIRE ??
		// dtConfig.edit.showButton = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
		
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
	
	// le sciclone ne traite que des plaques (le type de container de sortie est deja restreint)
	if ( ( $scope.experiment.instrument.categoryCode !== 'hand') && ($scope.experiment.instrument.inContainerSupportCategoryCode !== $scope.experiment.instrument.outContainerSupportCategoryCode) ) {
		$scope.messages.setError(Messages('experiments.input.error.must-be-same-out'));
	} else {
		$scope.messages.clear();
		$scope.atmService = atmService;
	}
	
    // recuperer les tags existants
	$http.get(jsRoutes.controllers.commons.api.Parameters.list().url,{params:{typeCode:"index-illumina-sequencing"}})
	.success(function(data, status, headers, config) {
			$scope.tags = data;		
	})
	
	// Calculs 
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		
		if(col.property === 'outputContainerUsed.experimentProperties.tag.value'){
			computeTagCategory(value.data);			
		}
	}
	
	// determination  automatique de TagCategory
	var computeTagCategory = function(udtData){
		var getter = $parse("outputContainerUsed.experimentProperties.tagCategory.value");
		var tagCategory = getter(udtData);
		
		var compute = {
				tagValue : $parse("outputContainerUsed.experimentProperties.tag.value")(udtData),
				tag : $filter("filter")($scope.tags,{code:$parse("outputContainerUsed.experimentProperties.tag.value")(udtData)},true),
				isReady:function(){
					return (this.tagValue && this.tag && this.tag.length === 1);
				}
		};
		
		if(compute.isReady()){
			var result = compute.tag[0].categoryCode;
			console.log("result = "+result);
			if(result){
				tagCategory = result;				
			}else{
				tagCategory = undefined;
			}	
			getter.assign(udtData, tagCategory);
		}else if(compute.tagValue){
			getter.assign(udtData, undefined);
		}
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
	
	/* FORCER A STRIP si instrument= main  et  sortie=plaque  */
	$scope.$watch("$scope.experiment.instrument.categoryCode", function(){
			if (($scope.experiment.instrument.categoryCode === "hand") && ($scope.experiment.instrument.outContainerSupportCategoryCode === "96-well-plate"))
				$scope.experiment.instrument.outContainerSupportCategoryCode = "strip-8";
	});	
		
	
/* pas specifié, voir plus tard..................?????????????????????//
 
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
	
	//actuellement 1 seule definie
	
	$scope.plates = [ {name:"???????", tagCategory:"POOL-INDEX"} ];
	$scope.tagPlate = $scope.plates[0]; // defaut du select
	
	// pour l'instant une seule plaque => faire un simple tableau
	// l'indice dans le tableau correspond a l'ordre "colonne d'abord" dans la plaque
	// NB: ce sont les memes index et dans la meme disposition que pour la "DAP TruSeq RNA HT", faut il tout dupliquer ???
	var tagPlateCode=[];
	!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! index DUAL.. a changer !!!!!!!!!!!!!!!!!!!
	tagPlateCode.push("D701-D501", "D701-D502", "D701-D503", "D701-D504", "D701-D505", "D701-D506", "D701-D507", "D701-D508");
	tagPlateCode.push("D702-D501", "D702-D502", "D702-D503", "D702-D504", "D702-D505", "D702-D506", "D702-D507", "D702-D508");
	tagPlateCode.push("D703-D501", "D703-D502", "D703-D503", "D703-D504", "D703-D505", "D703-D506", "D703-D507", "D703-D508");
	tagPlateCode.push("D704-D501", "D704-D502", "D704-D503", "D704-D504", "D704-D505", "D704-D506", "D704-D507", "D704-D508"); 
	tagPlateCode.push("D705-D501", "D705-D502", "D705-D503", "D705-D504", "D705-D505", "D705-D506", "D705-D507", "D705-D508"); 
	tagPlateCode.push("D706-D501", "D706-D502", "D706-D503", "D706-D504", "D706-D505", "D706-D506", "D706-D507", "D706-D508"); 
	tagPlateCode.push("D707-D501", "D707-D502", "D707-D503", "D707-D504", "D707-D505", "D707-D506", "D707-D507", "D707-D508"); 
	tagPlateCode.push("D708-D501", "D708-D502", "D708-D503", "D708-D504", "D708-D505", "D708-D506", "D708-D507", "D708-D508"); 
	tagPlateCode.push("D709-D501", "D709-D502", "D709-D503", "D709-D504", "D709-D505", "D709-D506", "D709-D507", "D709-D508"); 
	tagPlateCode.push("D710-D501", "D710-D502", "D710-D503", "D710-D504", "D710-D505", "D710-D506", "D710-D507", "D710-D508"); 
	tagPlateCode.push("D711-D501", "D711-D502", "D711-D503", "D711-D504", "D711-D505", "D711-D506", "D711-D507", "D711-D508"); 
	tagPlateCode.push("D712-D501", "D712-D502", "D712-D503", "D712-D504", "D712-D505", "D712-D506", "D712-D507", "D712-D508"); 
	
	var setTags = function(){
		$scope.messages.clear();
		//console.log("selected column=" +$scope.tagPlateColumn.name);
		
        var dataMain = atmService.data.getData();
        // trier dans l'ordre "colonne d'abord"
        var dataMain = $filter('orderBy')(dataMain, ['atomicTransfertMethod.column*1','atomicTransfertMethod.line']);
        
       //attention certains choix de colonne sont incorrrects !!!
		if  ($scope.tagPlateColumn.position + dataMain.length > 96 ) {			
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
				ocu.experimentProperties["tag"]={"_type":"single","value":tagPlateCode[indexPos]};
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
*/
	
}]);
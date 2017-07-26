// FDS 19/07/2017 -- JIRA NGL-1519. Duplication avec qq differences: PAS DEFAUT pour volume, concentration
angular.module('home').controller('AdditionalNormalizationCtrl',['$scope', '$parse', '$http', 'atmToSingleDatatable',
                                                     function($scope, $parse, $http, atmToSingleDatatable){

	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	// NGL-1055: name explicite pour fichier CSV exporté: typeCode experience
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),
			columns:[
			         //--------------------- INPUT containers section -----------------------
			         
			         /* plus parlant pour l'utilisateur d'avoir Plate barcode | line | column
					  {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0: inputExtraHeaders }
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
			         { // libProcessType ajout 08/11/2016
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
					 	"order":true,
					 	"hide":true,
					 	"type":"text",
					 	"position":7,
					 	"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
					    "extraHeaders":{0:inputExtraHeaders}
					 },
			         { // 17/11/2016 expected Coverage
				        "header":Messages("containers.table.expectedCoverage"),
				 		"property": "inputContainer.contents",
				 		"filter": "getArray:'properties.expectedCoverage.value'| unique",
				 		"order":true,
				 		"hide":true,
				 		"type":"text",
				 		"position":7.5,
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
			         { // Etat input Container
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":10,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         // colonnes specifiques experience viennent ici.. Volume engagé, Volume tampon
			          
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
			         { // barcode plaque sortie == support Container used code... faut Used 
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
			         { // Concentration  sans  valeur par defaut
			        	 "header":Messages("containers.table.concentration.shortLabel") + " (nM)",
			        	 "property":"outputContainerUsed.concentration.value",
						 "edit":true,
						 "editDirectives":"udt-change='updatePropertyFromUDT(value,col)'",  // 26/07/2017 NGL-1519: ajout calculs en Javascript
						 "hide":true,
			        	 "type":"number",
			        	 "position":120,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         { // Volume sans  valeur par defaut
			        	 "header":Messages("containers.table.volume")+ " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
						 "edit":true,
						 "editDirectives":"udt-change='updatePropertyFromUDT(value,col)'",  // 26/07/2017 NGL-1519: ajout calculs en Javascript
						 "hide":true,
			        	 "type":"number",
			        	 "position":130,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
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
				// FDS : ce tri donne 1,10,11,12,2.... comment avoir un tri 1,2....10,11,12,13 ??
				//by:"inputContainer.support.column*1"
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
		
		//ne plus faire...datatable.setData(dataMain);
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
		
	//Init
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	// FDS ajout variables pour ligne et colonne
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
			volume : "µL",
			concentration : "nM"
	}
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
	
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
		isShow:function(){return ($scope.experiment.instrument.typeCode === 'janus')}, // FDS ne pas afficher bouton pour "hand"
		click: generateSampleSheet,
		label:Messages("experiments.sampleSheet") 
	}]);
	
	// 26/07/2017: remplacer les calculs de calculation.drl par du javascript....
	$scope.updatePropertyFromUDT = function(value, col){
		//console.log("update from property : "+col.property);

		if (( col.property === 'outputContainerUsed.concentration.value')||
			( col.property === 'outputContainerUsed.volume.value')
		){
			var outputConc=$parse("outputContainerUsed.concentration.value")(value.data); //OUI !!
			var inputConc= $parse("inputContainerUsed.concentration.value")(value.data); //OUI !!
			var vol= $parse("inputContainerUsed.volume.value")(value.data); //OUI !!
			
			//console.log(">>>outputContainerUsed.concentration.value="+ outputConc );
			//console.log(">>>inputContainerUsed.concentration.value="+ inputConc );

			if ( outputConc > inputConc)
			{
				console.log("concentration out trop forte !!");
				// forcer valeurs
				$parse("inputContainerUsed.experimentProperties.bufferVolume.value").assign(value.data, 0); 
				$parse("inputContainerUsed.experimentProperties.inputVolume.value").assign(value.data, vol);
				$parse("outputContainerUsed.volume.value").assign(value.data, vol);
				$parse("outputContainerUsed.concentration.value").assign(value.data, inputConc);
			} else {
			    computeVolumes(value.data);
			}
		}
	}
	
	// 26/07/2017: remplacer les calculs de volumes de calculation.drl par du javascript....
	var computeVolumes = function(udtData){

		var getterEngageVol= $parse("inputContainerUsed.experimentProperties.inputVolume.value");
		var getterBufferVol= $parse("inputContainerUsed.experimentProperties.bufferVolume.value");

		var compute = {
				inputConc :  $parse("inputContainerUsed.concentration.value")(udtData), // pas forcement dispo ( si pas de QC avant)
				outputConc:  $parse("outputContainerUsed.concentration.value")(udtData),
				outputVol:   $parse("outputContainerUsed.volume.value")(udtData),
			   
				isReady:function(){
					// attention division par 0 !
					return (this.inputConc && this.outputConc && this.outputVol);
				}
		};
		
		if(compute.isReady()){

			var engageVol=$parse("outputConc * outputVol  / inputConc")(compute);
			// arrondir...
			if(angular.isNumber(engageVol) && !isNaN(engageVol)){
				engageVol = Math.round(engageVol*10)/10;				
			}
			console.log("vol engagé = "+engageVol);
			
			var bufferVol=$parse("outputVol")(compute) - engageVol;
			// arrondir...
			if(angular.isNumber(bufferVol) && !isNaN(bufferVol)){
				bufferVol = Math.round(bufferVol*10)/10;	
			}
			console.log("vol buffer= "+ bufferVol);
			
			getterEngageVol.assign(udtData, engageVol);
			getterBufferVol.assign(udtData, bufferVol);
			
		}else{
			console.log("Impossible de calculer les volumes: valeurs manquantes");
			getterEngageVol.assign(udtData, undefined);
			getterBufferVol.assign(udtData, undefined);
		}
	}
	
}]);
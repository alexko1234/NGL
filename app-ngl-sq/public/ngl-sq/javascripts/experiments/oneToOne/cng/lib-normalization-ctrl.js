angular.module('home').controller('LibNormalizationCtrl',['$scope', '$parse', 'atmToSingleDatatable',
                                                     function($scope, $parse, atmToSingleDatatable){
// FDS 15/02/2016 -- JIRA NGL-894 : lib-normalization experiment
// probleme comment traiter des tubes ou des plaques ???
	
	
	
	// actuellement le nom du header est en dur dans ATM (Inputs ou Outputs )
	// donc utiliser ici message va introduire des erreurs... laisser aussi les valeurs en dur pour l'instant
	var inputExtraHeaders="Inputs";
	var outputExtraHeaders="Outputs";
	//var inputExtraHeaders=Messages("containers.table.support.in.code");
	//var outputExtraHeaders=Messages("containers.table.support.out.code");
	
	var datatableConfig = {
			name:"FDR_Plaque", //peut servir pour le nom de fichier si export demandé
			columns:[
			         //-------- INPUT containers section -----------
			         
			         /* plus parlant pour l'utilisateur d'avoir Plate barcode | line | column
					  {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"Inputs"}
			         },	
			         */				
			         {
			        	 "header":Messages("containers.table.support.name"),
			        	 "property":"inputContainerUsed.locationOnContainerSupport.code",
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         // Line ( si plaque )
			         {
			        	 "header":Messages("containers.table.support.line"),
			        	 "property":"inputContainerUsed.locationOnContainerSupport.line",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         // column ( si plaque )
			         // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
			         {
			        	 "header":Messages("containers.table.support.column"),
			        	 "property":"inputContainerUsed.locationOnContainerSupport.column*1",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":3,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },	
			         // Project(s)             si rajoute Used ?? marche encore
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainerUsed.projectCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":4,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:inputExtraHeaders}
				     },
				     //Echantillon(s)    si rajoute Used ???? marche encore
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainerUsed.sampleCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":5,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0:inputExtraHeaders}
				     },
				     //Aliquot    essayer avec Used  ??? toujours pas
				     {
				        "header":Messages("containers.table.codeAliquot"),
				 		"property": "inputContainerUsed.sampleAliquoteCodes", /// PBBBBBBBBBB
				 		"order":true,
				 		"hide":true,
				 		"type":"text",
				 		"position":6,
				 		"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				        "extraHeaders":{0:inputExtraHeaders}
					 },
				     //Tag        Used ?????? toujours pas    avec value ???? non plus...
					 {
					    "header":Messages("containers.table.tags"),
					 	"property": "inputContainerUsed.tags.value", /// PBBBBBBBBBBBB
					 	"order":true,
					 	"hide":true,
					 	"type":"text",
					 	"position":6,
					 	"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
					    "extraHeaders":{0:inputExtraHeaders}
					 },
				     //Concentration  Used ?????? OUIIII
					 {
			        	 "header":Messages("containers.table.concentration") + " (ng/µL)", 
			        	 "property":"inputContainerUsed.volume.value",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":13,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         
			         // colonnes specifiques instrument viennent ici
			         
			         // Etat input Container
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":30,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         
			         //------ OUTPUT containers section ------

		            /* ne pas aficher les containercodes ????? 
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
						 "hide":true,
						 "edit":false,
			        	 "type":"text",
			        	 "position":100,
			        	 "extraHeaders":{0:"Outputs"}
			         },*/
			         // Line
			         {
			        	 "header":Messages("containers.table.support.line"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.line",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":110,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         // column
			         {
			        	 "header":Messages("containers.table.support.column"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.column",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":111,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },	
			         // Concentration et volume mesurees sont des proprietes de l'experiment
			         // Volume
			         // Etat outpout container      !! containers.table.stateCode c'est pour le support
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
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
				mode:'local', //or 
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
					copyPlateCodeAndStorageToDT(datatable);
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
	};

	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
		$scope.$emit('childSaved', callbackFunction);
	});
	
	var copyPlateCodeAndStorageToDT = function(datatable){

		var dataMain = datatable.getData();
		
		/* pas necessaire de verifier le type outContainerSupportCategoryCode ???
		var cscCode = $parse('experiment.instrument.outContainerSupportCategoryCode')($scope);
		if(cscCode !== undefined){
			wellsCount = Number(cscCode.split("-",2)[0]);
		    console.log("TESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSST "+ wellsCount);
			//alert ("wellsCount ="+wellsCount);
			}
		}
		*/
		
		//-1- copy plate code to output code
		var codePlate = $parse("instrumentProperties.outputContainerSupportCode.value")($scope.experiment);
		console.log("setting outputContainerUsed code from: "+ codePlate);
		if(null != codePlate && undefined != codePlate){
			for(var i = 0; i < dataMain.length; i++){
				var atm = dataMain[i].atomicTransfertMethod;
				var containerCode = codePlate+"_"+atm.line + atm.column;

				$parse('outputContainerUsed.code').assign(dataMain[i],containerCode);
				$parse('outputContainerUsed.locationOnContainerSupport.code').assign(dataMain[i],codePlate);
			}
			
			//-2- TODO copy storage to containerSupport...?????
			/*
			var storage = $parse("instrumentProperties.outputStorage.value")($scope.experiment);
			alert ("TODO : setting containerSupport storage to : "+ storage);
			*/	
			
			datatable.setData(dataMain);
		}
	}
	
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
		
	//Init
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	// FDS ajout variables pour ligne et colonne ( doivent etre prises en compte dans atomicTransfereServices.js)
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
	
}]);
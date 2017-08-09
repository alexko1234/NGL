/* 30/06/2016 dupliqué a partir de pcr-and-purification-ctrl.js
   25/07/2017 Ne pas faire apparaitre les volumes.... */

angular.module('home').controller('FragmentationCtrl',['$scope', '$parse', 'atmToSingleDatatable','mainService',
                                                    function($scope, $parse, atmToSingleDatatable, mainService){
	// variables pour extraheaders
	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	var datatableConfig = {
			name: $scope.experiment.typeCode.toUpperCase(),
			columns:[	
			         //--------------------- INPUT containers section -----------------------
			         /* plus parlant pour l'utilisateur d'avoir Plate barcode | line | column
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:inputExtraHeaders}
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
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainer.projectCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":4,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0:inputExtraHeaders}
				     },
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":5,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0:inputExtraHeaders}
				     },
				     {
			        	 "header":Messages("containers.table.fromTransformationTypeCodes"),
			        	 //"property":"inputContainer.fromTransformationTypeCodes",  ///pourquoi ?????????????????????????
			        	 "property":"inputContainerUsed.fromTransformationTypeCodes",
			        	 "filter":"unique | codes:'type'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			 			 "render":"<div list-resize='cellValue'  list-resize-min-size='3'>",
			        	 "position":6,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
                     /* 09/08/2017 libProcessTypeCode (niveau content) 
                                   expected baits (niveau processus)
                                   captureProtocol (niveau content)
                        A VERIFIER
                     */
			         {
			        	 "header": Messages("containers.table.libProcessTypeCode"),
			        	 "property" : "inputContainerUsed.contents",
			        	 "filter" : "getArray:'processProperties.libProcessTypeCode.value' | unique ",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":7,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         {
			        	 "header": "Baits (sondes) prévues",
			        	 "property" : "inputContainerUsed.contents",
			        	 "filter" : "getArray:'processProperties.expectedBaits.value' | unique ",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":7.2,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         {
			        	 "header": "Protocole/ Kit",
			        	 "property" : "inputContainerUsed.contents",
			        	 "filter" : "getArray:'processProperties.captureProtocol.value' | unique ",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":7.4,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":9,
			        	 "extraHeaders":{0:inputExtraHeaders}
			         },
			         
			         // colonnes specifiques experience viennent ici...
			         
			         //--------------------- OUTPUT containers section -----------------------
			         /* 25/07/2017 ne pas faire apparaitre les volumes...
			         {
			        	 "header":Messages("containers.table.volume")+ " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "editDirectives":"udt-change='updatePropertyFromUDT(value,col)'",
			        	 "tdClass":"valuationService.valuationCriteriaClass(value.data, experiment.status.criteriaCode, col.property)",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":300,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         */
			         /* ne pas afficher les containercodes  sauf pour DEBUG
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":400,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         */
			         { //  barcode plaque sortie == support Container used 
			        	 "header":Messages("containers.table.support.name"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.code", 
						 "hide":true,
			        	 "type":"text",
			        	 "position":500,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },  
			         { //  Ligne 
			        	 "header":Messages("containers.table.support.line"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.line", 
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":600,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },     
			         { // colonne
			        	 "header":Messages("containers.table.support.column"),
			        	 // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
			        	 "property":"outputContainerUsed.locationOnContainerSupport.column*1", 
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":700,
			        	 "extraHeaders":{0: outputExtraHeaders}
			         },	
			         {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":800,
			        	 "extraHeaders":{0:outputExtraHeaders}
			         },
			         {
			        	 "header":Messages("containers.table.storageCode"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.storageCode",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":900,
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
				active:true
			},
			remove:{
				active: ($scope.isEditModeAvailable() && $scope.isNewState()),
				showButton: ($scope.isEditModeAvailable() && $scope.isNewState()),
				mode:'local'
			},
			save:{
				active:true,
	        	withoutEdit: true,
	        	showButton:false,
	        	changeClass:false,
	        	mode:'local',
	        	// GA ne voudrait plus de callback...................................
	        	callback:function(datatable){
	        		copyContainerSupportCodeAndStorageCodeToDT(datatable);
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
				active:false
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
			},
			otherButtons: {
				/* 25/07/2017 pas necessaire tant que les volumes ne sont pas affichés
                active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
                complex:true,
                template:''
                	+'<div class="btn-group" style="margin-left:5px">'
                	+'<button class="btn btn-default" ng-click="copyVolumeInToExp()" data-toggle="tooltip" title="'+Messages("experiments.button.plate.copyVolumeTo")+' volume container de sortie'
                	+'" ng-disabled="!isEditMode()" ng-if="experiment.instrument.outContainerSupportCategoryCode!==\'tube\'"><i class="fa fa-files-o" aria-hidden="true"></i> Volume </button>'                	                	
                	+'</div>'
                */
			}
	};
	
	// 31/07/2017 ajouter les columns 'processProperties' uniquement si experience state= N ou IP car n'existe que temporairement
	//  plateWorkLabel / ngsRunWorkLabel 
	if ( $scope.isInProgressState() || $scope.isNewState() ) {
		datatableConfig.columns.push({
	       	 "header": "Nom de travail plaque",
	       	 "property":"inputContainerUsed.contents",
	       	 "filter" : "getArray:'processProperties.plateWorkLabel.value' | unique",
	       	 "order":true,
			 "hide":true,
	       	 "type":"text",
	       	 "position":9.5,
	       	 "extraHeaders":{0:inputExtraHeaders}
		 });
		
		datatableConfig.columns.push({
	       	 "header": "Nom de travail run NGS",
	       	 "property":"inputContainerUsed.contents",
	       	 "filter" : "getArray:'processProperties.ngsRunWorkLabel.value' | unique",
	       	 "order":true,
			 "hide":true,
	       	 "type":"text",
	       	 "position":9.7,
	       	 "extraHeaders":{0:inputExtraHeaders}
		 });
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
	
	// for save callback
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
	};
	
    // il faudrait la mettre qq part ou elle puisse etre utilisee par toutes les experiences.. voir GA...
	var checkOneSupportInput = function(){
		  // !! en mode creation $scope.experiment.atomicTransfertMethod n'est pas encore chargé=> passer par Basket ( ajouter mainService dans le controller !!! )

		  /* plusieurs  categoryCode  pas possible ici car filtré par les inputs type Used de l'instrument
		  var categoryCode = [];
		  if(!$scope.isCreationMode()){
			var categoryCode = $scope.$eval("atomicTransfertMethods|flatArray:'inputContainerUseds'|getArray:'locationOnContainerSupport.categoryCode'|unique",$scope.experiment);			
		  }else{
			var categoryCode = $scope.$eval("getBasket().get()|getArray:'support.categoryCode'|unique", mainService);
		  }
		
		  if(categoryCode.length > 1){
				console.log("> 1  type support en entree");
				
			$scope.messages.clear(); 
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages("> 1  type support en entree") ;
			$scope.messages.showDetails = false;
			$scope.messages.open();
			
			return false;
		  } 
		  */
		
		
		  var supportCode = [];
		  if(!$scope.isCreationMode()){
			var supportCode = $scope.$eval("atomicTransfertMethods|flatArray:'inputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);			
		  }else{
			var supportCode = $scope.$eval("getBasket().get()|getArray:'support.code'|unique", mainService);
		  }
		
		  if(supportCode.length > 1){
			console.log(" > 1 support en entree");
			
			$scope.messages.clear();
			$scope.messages.clear();
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages("experiments.input.error.only-1-plate");
			$scope.messages.showDetails = false;
			$scope.messages.open();
			
			return false;
		  }
		  
		  return true;
	};
	
	
	/* 25/07/2017 pas necessaire tant que les volumes ne sont pas demandés...
	   copier volume in vers volume out
	 
	$scope.copyVolumeInToExp = function(){
		console.log("copyVolumeInToExp");
		
		var data = $scope.atmService.data.displayResult;		
		data.forEach(function(value){
			$parse("outputContainerUsed.volume").assign(value.data, angular.copy(value.data.inputContainer.volume));			
		})		
	};
	*/
		
	//Init
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(l,c){
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
	
	// 01/08/2017 TEST checkOneSupportInput
	if ( checkOneSupportInput() ){
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
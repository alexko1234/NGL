angular.module('home').controller('PrepPcrFreeCtrl',['$scope', '$parse', 'atmToSingleDatatable',
                                                     function($scope, $parse, atmToSingleDatatable){
// FDS 04/02/2016 -- JIRA NGL-894 : prep pcr free experiment

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
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         // Line
			         {
			        	 "header":Messages("containers.table.support.line"),
			        	 "property":"inputContainerUsed.locationOnContainerSupport.line",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         // column
			         // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
			         {
			        	 "header":Messages("containers.table.support.column"),
			        	 "property":"inputContainerUsed.locationOnContainerSupport.column*1",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":3,
			        	 "extraHeaders":{0:"Inputs"}
			         },	
			         // Project(s)
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainer.projectCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":11,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
				     //Echantillon(s)
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":12,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
				     //Concentration
					 {
			        	 "header":Messages("containers.table.concentration") + " (ng/µl)",
			        	 "property":"inputContainer.mesuredConcentration.value",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":13,
			        	 "extraHeaders":{0:"Inputs"}
			         },
			         // Volume
			         {
			        	 "header":function(){return Messages("containers.table.volume") + " (µl)"},
			        	 "property":"mesuredVolume.value",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":14,
			        	 "extraHeaders":{0:"Inputs"}
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
			        	 "extraHeaders":{0:"Inputs"}
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
			         // TEST Line
			         {
			        	 "header":Messages("containers.table.support.line"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.line",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":110,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         // TEST column
			         {
			        	 "header":Messages("containers.table.support.column"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.column",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":111,
			        	 "extraHeaders":{0:"Outputs"}
			         },	
			         // Volume
			         {
			        	 "header":Messages("containers.table.volume")+ " (µl)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":120,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         // Pas de concentration, elle sera mesuree plus tard
			         // Etat outpout container
			         {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":160,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         //Storage... ne pas l'afficher N fois !!!!!
			         {
			        	 "header":Messages("containers.table.storageCode"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.storageCode",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":170,
			        	 "extraHeaders":{0:"Outputs"}
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
	        	mode:'local'
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
		
		for(var i = 0 ; i < $scope.experiment.atomicTransfertMethods.length ; i++){
			var atm = $scope.experiment.atomicTransfertMethods[i];
			
			$parse('locationOnContainerSupport.code').assign(atm.outputContainerUseds[0], atm.outputContainerUseds[0].code);			
		}
		
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
		
	//Init
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	// FDS ce code est prevu pour les tube line:1, column:1...
	//  TODO  ==> utiliser des variables !!
	//  TODO  modifier atomicTransfereServices.js ?????
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
			volume : "µl",
			concentration : "nM"
	}
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
	
}]);
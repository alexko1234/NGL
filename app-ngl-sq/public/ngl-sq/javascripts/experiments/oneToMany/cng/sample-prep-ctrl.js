angular.module('home').controller('AliquotingCtrl',['$scope', '$parse', 'atmToGenerateMany',
                                                               function($scope, $parse, atmToGenerateMany) {

/*  ESSAI......
angular.module('home').controller('SamplePrepCtrl',['$scope', '$parse', 'commomAtomicTransferMethod',
                                                               function($scope, $parse, commomAtomicTransferMethod) {
*/
	

	//-1- datatable ( necessaire juste pour un barcode ???)
	
	var datatableConfigTubeParam = {
			//peut etre exporté CSV ??
			name: $scope.experiment.typeCode+'_PARAM'.toUpperCase(),
			columns:[   
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainerSupport.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },	
			         {
			        	 "header":Messages("containers.table.outputNumber"),
			        	 "property":"outputNumber",
			        	 "order":false,
						 "edit":true,
						 "hide":false,
			        	 "type":"number",						
			        	 "position":8,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         }
			         
			         ],
			compact:true,
			showTotalNumberRecords:false,
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local', //or 
				active:true,
				by:'inputContainerSupport.code'
			},
			remove:{
				active: ($scope.isEditModeAvailable() && $scope.isNewState()),
				showButton: ($scope.isEditModeAvailable() && $scope.isNewState()),
				mode:'local'
			},
			save:{
				active:true,
				withoutEdit: true,
				keepEdit:true,
				changeClass : false,
				mode:'local',
				showButton:false
			},			
			select:{
				active:true
			},
			edit:{
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
				showButton: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
				byDefault:($scope.isCreationMode()),
				columnMode:true
			},	
			cancel : {
				active:true
			},
			extraHeaders:{
				number:1,
				dynamic:true,
			}

	};	
	
	// METTRE OU ?????
	//var $commonATM = commonAtomicTransfertMethod($scope);
	
	
	/*
	/// tentative de creation d'une nouvelle function appelee sur le click du boutton....
	generateATM:function(){
		this.data.datatableParam.save();
		var allData = this.data.datatableParam.getData();
		this.data.atm = [];
		
		for(var i = 0; i < allData.length; i++){    // boucler sur les inputContainer trouves dans le basket ???
			var data = allData[i];
			var atm = this.newAtomicTransfertMethod();   // doit etre de type oneToMany
			
			// transformer chaque inputContainers en inputContainerUseds
			atm.inputContainerUseds.push($commonATM.convertContainerToInputContainerUsed(data.inputContainer));
			
			// boucler sur le nbre donné par l'utilisateur et generer autant de OutputContainerUsed
			for(var j = 0; j < data.outputNumber ; j++){
				atm.outputContainerUseds.push($commonATM.newOutputContainerUsed(this.defaultOutputUnit,this.defaultOutputValue,atm.line,atm.column, data.inputContainer));
			}
			this.data.atm.push(atm);
		}
		/// NON PAS DE DATATABLE...this.data.updateDatatable();					
	},
	
	*/
	var generateATM_TEST = function(){
	  alert('button pushed');
	}
	
	

	var atmService = atmToGenerateMany($scope, datatableConfigTubeParam, datatableConfigTubeConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(){
		return {
			class:"OneToMany",
			line:($scope.experiment.instrument.outContainerSupportCategoryCode!=="tube")?undefined:"1", 
			column:($scope.experiment.instrument.outContainerSupportCategoryCode!=="tube")?undefined:"1",				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};		
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL",
			quantity:"ng"
	};
	atmService.defaultOutputValue = {
			size : {copyInputContainer:true}
	};
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
	
}]);
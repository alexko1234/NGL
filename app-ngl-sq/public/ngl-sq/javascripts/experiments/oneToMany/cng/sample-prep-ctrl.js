/* 11/08/2017 GA/FDS experience One to Many qui n'utilise pas de datatable... */
/* 30/08/2017 finalement il faut meme plusieurs datatables ???  => injecter atmToSingleDatatable ???? .....*/

angular.module('home').controller('SamplePrepCtrl',['$scope', '$parse', 'commonAtomicTransfertMethod','mainService','atmToSingleDatatable',
                                                               function($scope, $parse, commonAtomicTransfertMethod, mainService, atmToSingleDatatable ) {

	
	
    var nbOutputSupport;
	
    if(!$scope.isCreationMode()){
	   getExperimentData();
		
    }else{
	   // trouver LE/LES codes des supports de tous les containers en entree de l'experience (il peut y en avoir plusieurs..)
	   $scope.inputSupportCodes = $scope.$eval("getBasket().get()|getArray:'support.code'|unique", mainService); 
	   
	   if ($scope.inputSupportCodes.length > 1){
		   $scope.messages.clear();
		   $scope.messages.clazz = "alert alert-danger";
		   $scope.messages.text = Messages("experiments.input.error.only-1-plate");
		   $scope.messages.showDetails = false;
		   $scope.messages.open();
	   } else {		  
		   $scope.inputSupportCode=$scope.inputSupportCodes[0];
		   $scope.outputContainerSupportCodes=[];
		   $scope.storageCodes=[];
	   }
	}	
	  
   // créer un tableau sur lequel pourra boucler ng-repeat
   // ce tableau est modifié sur onChange de "nbOutputSupport"
   $scope.initOutputContainerSupportCodes = function(nbOutputSupport){
	   if(nbOutputSupport){
		    $scope.nbOutputSupport = nbOutputSupport; // GA: a cause d'un probleme de rafraichissement...
		    
			if(!$scope.isCreationMode()){
				 
			    //récupérer les codes des outputContainers et reinjecter si possible ce qu'il y avait avant
				previousOutputContainerSupportCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);
				previousStorageCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.storageCode'|unique",$scope.experiment);
				if(previousOutputContainerSupportCodes.length >= nbOutputSupport){
					//tronquer le tableau
					$scope.outputContainerSupportCodes = previousOutputContainerSupportCodes.splice(0, nbOutputSupport);
					// idem pour storageCodes
					$scope.storageCodes = previousStorageCodes.splice(0, nbOutputSupport);
					
				}else if(previousOutputContainerSupportCodes.length < nbOutputSupport){
					// completer le tableau  //idem pour storageCodes
					$scope.outputContainerSupportCodes=previousOutputContainerSupportCodes;
					$scope.storageCodes=previousStorageCodes;
					for (var j=previousOutputContainerSupportCodes.length ; j<  nbOutputSupport; j++){
						$scope.outputContainerSupportCodes.push(null);
						$scope.storageCodes.push(null);
					}
				}
			} else {
				$scope.outputContainerSupportCodes= new Array(nbOutputSupport*1);// *1 pour forcer en numerique nbOutputSupport qui est est un input type text
			}
		}	
	}


   // il faut la callbackFunction pour le $emit 
   function generateATM(callbackFunction){
	    console.log ('ouput supports='+ $scope.outputContainerSupportCodes);
	    console.log ('ouput storages='+ $scope.storageCodes);
	    $scope.messages.clear();
	    
	    if($scope.isCreationMode()){
	    	console.log ('creation mode...');
	    	
	    	//1 initialiser
	    	$scope.experiment.atomicTransfertMethods = [];
	    	
	    	//2 récupérer les inputContainers depuis le basket
	    	//Each promise object will have a "then" function that can take two arguments, a "success" handler and an "error" handler.
	    	$commonATM.loadInputContainerFromBasket(mainService.getBasket().get())
			  .then(function(containers) {	
				 containers.forEach(function(inputContainer){
					 
					//2.1 création de l'ATM
					var atm = newAtomicTransfertMethod(inputContainer.support.line, inputContainer.support.column);
					
					//2.2 création d'1 inputContainerUsed
					var inputContainerUsed=$commonATM.convertContainerToInputContainerUsed(inputContainer);
					atm.inputContainerUseds.push(inputContainerUsed);
					//console.log('inputContainerUsed='+atm.inputContainerUseds[0].code);
							
					//2.3 création de j outputContainerUsed
					for(var j = 0; j < $scope.outputContainerSupportCodes.length ; j++){
						// si l'utilisateur a bien entré des supportCodes
						if ($scope.outputContainerSupportCodes[j] !== undefined && $scope.outputContainerSupportCodes[j] !== null && $scope.outputContainerSupportCodes[j] !== ''){
							var outputContainerUsed = $commonATM.newOutputContainerUsed(defaultOutputUnit, defaultOutputValue, atm.line, atm.column, inputContainer);
							//affectation du SupportCode
							outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
							//affectation du storageCode si defini
							if ( $scope.storageCodes[j] !== undefined && $scope.storageCodes[j] !== null){			  
								  outputContainerUsed.locationOnContainerSupport.storageCode=  $scope.storageCodes[j];
							}
							atm.outputContainerUseds.push(outputContainerUsed);
						}
					}
					
					//2.4 mettre l'atm dans l'expérience
					$scope.experiment.atomicTransfertMethods.push(atm);
					console.log('atm pushed into experiment');
				});
				 
				// !!! promise=asynchronisme. mettre le $emit('childSaved') ici et plus dans $scope.$on('save',
				// tester avec le premier ATM
				if ( $scope.experiment.atomicTransfertMethods[0].outputContainerUseds.length === 0){
					$scope.$emit('childSavedError', callbackFunction);

				    $scope.messages.clazz = "alert alert-danger";
				    $scope.messages.text = Messages('experiments.output.error.minSupports',1);
				    $scope.messages.showDetails = false;
					$scope.messages.open();   
					
				} else {	
					$scope.$emit('childSaved', callbackFunction);
			    } 
			    
			});	    
	   } else {
	    	console.log ('modification mode...');
	    	// l'utilisateur peut modifier - le nombre de supports en output et/ou les codes barres en output
	    	
	    	// TODO  chg algo: se contenter de supprimer les outputContainerUseds au lieu de tout refaire !!
	    	
	    	//0 copier les anciens ATMs (on n'a plus le basket...)
	    	var previousATMs=$scope.experiment.atomicTransfertMethods;
	    	
	    	//1 supprimer les ATM de l'expérience (on va les recréer)
	    	$scope.experiment.atomicTransfertMethods = [];
	    	
	    	//2 boucler sur les previousATMs
	    	previousATMs.forEach(function(prevatm){
	    		
			    	//2.1 création de l'ATM 
					var atm = newAtomicTransfertMethod(prevatm.line, prevatm.column);
			    
					//2.2 création d'1 inputContainerUsed (récupérer l'ancien)
					atm.inputContainerUseds.push (prevatm.inputContainerUseds[0]);
					
					//2.3 création de j outputContainerUsed
					for(var j = 0; j < $scope.outputContainerSupportCodes.length ; j++){
						// si l'utilisateur a bien entré des supportCodes
						if ($scope.outputContainerSupportCodes[j] !== undefined && $scope.outputContainerSupportCodes[j] !== null && $scope.outputContainerSupportCodes[j] !== ''){
							var outputContainerUsed = $commonATM.newOutputContainerUsed(defaultOutputUnit, defaultOutputValue, atm.line, atm.column, prevatm.inputContainerUseds[0]);
							//affectation du SupportCode 
							outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
							//affectation du storageCode si defini
							if ( $scope.storageCodes[j] !== undefined && $scope.storageCodes[j] !== null){
							  outputContainerUsed.locationOnContainerSupport.storageCode= $scope.storageCodes[j];
							}
							atm.outputContainerUseds.push(outputContainerUsed);
						}
					}
			
					//2.4 mettre l'atm dans l'expérience
					$scope.experiment.atomicTransfertMethods.push(atm);
	    	});
	    	
	    	// pas de promise dans ce cas mais obligé de faire le $emit('childSaved de facon similaire...
	    	// tester avec le premier ATM
			if ( $scope.experiment.atomicTransfertMethods[0].outputContainerUseds.length === 0){
				$scope.$emit('childSavedError', callbackFunction);
				
			    $scope.messages.clazz = "alert alert-danger";
			    $scope.messages.text = Messages('experiments.output.error.minSupports',1);
			    $scope.messages.showDetails = false;
				$scope.messages.open(); 
			} else {
				$scope.$emit('childSaved', callbackFunction);
			}
			
	    }
	}
	
	function getExperimentData(){	
		//1 récupérer LE locationOnContainerSupport.code des containers (il ne peux y en avoir qu'un seul)
	    $scope.inputSupportCode = $scope.$eval("atomicTransfertMethods|flatArray:'inputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment)[0];
	     
		//2 récupérer le nbre de nbOutputSupport en se basant sur atomic[0]: tjrs vrai ??? 
        //  => oui si on bloque les cas de sauvegarde sans  nbOutputSupport

	    $scope.nbOutputSupport=$scope.experiment.atomicTransfertMethods[0].outputContainerUseds.length;
			  
	    //3 récupérer les codes des outputContainers  
	    $scope.outputContainerSupportCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);
		
	    //4 récupérer les storageCodes
	    $scope.storageCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.storageCode'|unique",$scope.experiment);
		
	    //?? qu'est-ce qui prouve que les 2 tableaux locationOnContainerSupport.code  et locationOnContainerSupport.storageCode  sont récupéres dans le meme ordre ?????
	}
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");

		generateATM(callbackFunction);
		// le $emit est effectue DANS generateATM, APRES la creation des ATM !! (si il y a des ATMS)

	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('cancel', function(e) {
		console.log("call event cancel");		
		getExperimentData();

	});
	
	$scope.$on('activeEditMode', function(e) {
		console.log("call event activeEditMode");
		// rien  ????
	});
	
	/*Init*/
	var $commonATM = commonAtomicTransfertMethod($scope);
	
	var newAtomicTransfertMethod = function(l,c){
		return {
			class:"OneToMany",
			line: l, 
			column: c, 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};

	//defined default output unit
	var defaultOutputUnit = {
			volume : "µL",
			concentration : "nM"
	};
	
	//nécessaire pour newOutputContainerUsed meme si vide
	var defaultOutputValue = {		
	};
	
	
	
	/*----------------- TEST 30/08 il faut des datatables !!!! -----------------------------------------------
	
	var datatableConfigTest = {
			//peut etre exporté CSV ??
			name: $scope.experiment.typeCode+'_TEST'.toUpperCase(),
			columns:[   
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
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
	
	
	var atmService = atmToSingleDatatable($scope, datatableConfigTest);
	atmService.newAtomicTransfertMethod = function(l, c){
		return {
			class:"OneToMany",
			line: l, 
			column: c, 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};
    atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;	
		
	var init = function(){
		$scope.clearMessages();		
		$scope.datatable = datatable(datatableConfigTest);
	}
	
	// copiée de ngl-plates/ details-ctrl.js
	var displayCellPlaque =function(x, y){
		var wells = $scope.datatable.displayResult;
		if(!angular.isUndefined(wells)){
	        for (var i = 0; i <wells.length; i++) {
		         if (wells[i].data.x === (x) && wells[i].data.y===(y+'')) {
		        	 return wells[i].data.name.replace(/_/g,' ');
		         }
	        }
		}
        return "------";
     }
	
	var computeXY = function(){
		var wells = $scope.datatable.displayResult;
		var nbCol = 12;
		var nbLine = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
		var x = 0;
		for(var i = 0; i < nbCol ; i++){
			for(var j = 0; j < nbLine.length; j++){
				if(x < wells.length){
					wells[x].data.y = nbLine[j]+'';
					wells[x].data.x = i+1;					
				}
				x++;
			}
		}		
	};
	
	// copiée de ngl-plates/ details-ctrl.js
	var getClass = function(x, y){
		var wells = $scope.datatable.displayResult;
		if(!angular.isUndefined(wells)){
	        for (var i = 0; i <wells.length; i++) {
		         if (wells[i].data.x === (x) && wells[i].data.y===(y+'')) {
		        	 var well = wells[i];
		        	 if(well.data.valid === "FALSE"){
		        		 return "alert alert-danger hidden-print";
		        	 }else if(well.data.valid === "TRUE"){
		        		 return "alert alert-success hidden-print";
		        	 }	        	
		         }
	        }
		}
        return "hidden-print";
     }
	*/
	
}]);
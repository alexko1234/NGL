/* 11/08/2017 GA/FDS experience One to Many qui n'utilise pas de datatable... */
angular.module('home').controller('SamplePrepCtrl',['$scope', '$parse', 'commonAtomicTransfertMethod','mainService',
                                                               function($scope, $parse, commonAtomicTransfertMethod, mainService) {

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
	    
	    if($scope.isCreationMode()){
	    	console.log ('creation mode...');
	    	
	    	//1 initaliser
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
						// vérifier que l'utilisateur a bien entré qq chose...
						if ($scope.outputContainerSupportCodes[j] !== undefined && $scope.outputContainerSupportCodes[j] !== null){
							var outputContainerUsed = $commonATM.newOutputContainerUsed(defaultOutputUnit, defaultOutputValue, atm.line, atm.column, inputContainer);
							//affectation du SupportCode
							outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
							if ( $scope.storageCodes[j] !== undefined && $scope.storageCodes[j] !== null){
								  //affectation du storageCode
								  outputContainerUsed.locationOnContainerSupport.storageCode=  $scope.storageCodes[j];
							}
							atm.outputContainerUseds.push(outputContainerUsed);
							//console.log(' >  outputContainerUsed'+'['+j+']=' +$scope.outputContainerSupportCodes[j] +':'+ atm.line+':'+atm.column )
							//console.log(' >> outputContainerUsed'+'['+j+']=' +atm.outputContainerUseds[j].locationOnContainerSupport.code+ atm.outputContainerUseds[j].locationOnContainerSupport.line+atm.outputContainerUseds[j].locationOnContainerSupport.column )
						}
					}
					
					//2.4 mettre l'atm dans l'expérience
					$scope.experiment.atomicTransfertMethods.push(atm);
					console.log('atm pushed into experiment');
				});
				 
				// !!! promise=asynchronisme. mettre le $emit('childSaved') ici et plus dans $scope.$on('save',
				$scope.$emit('childSaved', callbackFunction); 
				
				/*
				if ( $scope.experiment.atomicTransfertMethods.outputContainerUseds.length === 0){
					$scope.messages.setError(Messages('experiments.output.error.minSupports',1));
					// PB il n'existe pas pour l'instant de  $scope.$emit('childError').. TODO
					$scope.$emit('childError', callbackFunction);
				} else {	
					$scope.$emit('childSaved', callbackFunction);
			    }
			    */
			    
			});	    
	   } else {
	    	console.log ('modification mode...');
	    	// l'utilisateur peut modifier - le nombre de supports en output et/ou les codes barres en output
	    	
	    	// chg algo  afaire... se contenter de supprimer les outpuContainerUseds au lieu de tout refaire !!
	    	
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
						// vérifier que l'utilisateur a bien entré qq chose...
						if ($scope.outputContainerSupportCodes[j] !== undefined && $scope.outputContainerSupportCodes[j] !== null){
							var outputContainerUsed = $commonATM.newOutputContainerUsed(defaultOutputUnit, defaultOutputValue, atm.line, atm.column, prevatm.inputContainerUseds[0]);
							//affectation du SupportCode 
							outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
							if ( $scope.storageCodes[j] !== undefined && $scope.storageCodes[j] !== null){
							  //affectation du storageCode
							  outputContainerUsed.locationOnContainerSupport.storageCode= $scope.storageCodes[j];
							}
							atm.outputContainerUseds.push(outputContainerUsed);
						}
					}
			
					//2.4 mettre l'atm dans l'expérience
					$scope.experiment.atomicTransfertMethods.push(atm);
	    	});
	    	// pas de promise dans ce cas mais obligé de faire le $emit('childSaved de facon similaire...
	    	$scope.$emit('childSaved', callbackFunction);
	    	
	    	/*
			if ( $scope.experiment.atomicTransfertMethods.outputContainerUseds.length === 0){
				$scope.messages.setError(Messages('experiments.output.error.minSupports',1));
				// PB il n'existe pas pour l'instant de  $scope.$emit('childError')... TODO
				$scope.$emit('childError', callbackFunction);
			} else {
				$scope.$emit('childSaved', callbackFunction);
			}
			*/
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
	
}]);
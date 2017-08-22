/* 11/08/2017 GA: experience One to Many qui n'utilise pas de datatable... */
angular.module('home').controller('SamplePrepCtrl',['$scope', '$parse', 'commonAtomicTransfertMethod','mainService',
                                                               function($scope, $parse, commonAtomicTransfertMethod, mainService) {

	  var inputSupportCode;
	  
	  if(!$scope.isCreationMode()){
		  // mode nouveau/terminé : l'experience existe
		  // récupérer LE locationOnContainerSupport.code des containers (il ne peux y en avoir qu'un seul)
		  inputSupportCode = $scope.$eval("atomicTransfertMethods|flatArray:'inputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);
		  
		  //récupérer le nbre de nbOutputSupport en se basant sur atomic[0]: tjrs vrai ??? 
		  // marche avec scope uniquement !!
		  $scope.nbOutputSupport=$scope.experiment.atomicTransfertMethods[0].outputContainerUseds.length;
		  console.log('nbOutputSuppor>>>>>'+ $scope.nbOutputSupport);
		  
		  //récupérer les codes des outputContainers
		  $scope.outputContainerSupportCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);

	  }else{
		  // trouver LE/LES codes des suports de tous les container en entree de l'experience (il peux y en avoir plusieurs..)
		  inputSupportCode = $scope.$eval("getBasket().get()|getArray:'support.code'|unique", mainService); 
	  }	
	  
	  //GA: traiter les 2 cas (simple ou tableau)
	  if(angular.isArray(inputSupportCode) && inputSupportCode.length === 1){
		  $scope.messages.clear();
		  $scope.inputSupportCode = inputSupportCode[0];
	  }else if($scope.isCreationMode() && angular.isArray(inputSupportCode) && inputSupportCode.length > 1){
		  $scope.messages.clear();
		  $scope.messages.clazz = "alert alert-danger";
		  $scope.messages.text = Messages("experiments.input.error.only-1-plate");
		  $scope.messages.showDetails = false;
		  $scope.messages.open();
	  }else{
		  throw 'problem with inputSupportCode';
	  }
	 
	  // creer un tableau sur lequel pourra boucler ng-repeat
	  // ce tableau est modifié sur onChange de" nbOutputSupport"
	  $scope.initOutputContainerSupportCodes = function(nbOutputSupport){
			if(nbOutputSupport){
				 if(!$scope.isCreationMode()){
					  // OK GA $scope.outputContainerSupportCodes = new Array(nbOutputSupport*1);	// transformer type text en nombre=> *1	
				 
					  //récupérer les codes des outputContainers  et reijincter si possible ce qu'il y avait avant
					  previousOutputContainerSupportCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);
					  if ( previousOutputContainerSupportCodes.length >= nbOutputSupport){
						  //tronquer le tableau
						  $scope.outputContainerSupportCodes = previousOutputContainerSupportCodes.splice(0, nbOutputSupport);
					  }else if ( previousOutputContainerSupportCodes.length < nbOutputSupport){
						  // completer le tableau
						  $scope.outputContainerSupportCodes=previousOutputContainerSupportCodes;
						  for (var j=previousOutputContainerSupportCodes.length ; j<  nbOutputSupport; j++){
							  $scope.outputContainerSupportCodes.push('');
						  }
					  }
				 }
			}	
	  }


	$scope.generateATM=function(outputContainerSupportCodes){
	    console.log ('ouput supports='+ outputContainerSupportCodes);
	    
	    if($scope.isCreationMode()){
	    	console.log ('creation mode...');
	    	
	    	//1 reinitaliser au cas ou l'utilisateur utilise plusieurs fois le bouton
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
					
					//2.3 création de j outputContainerUsed
					for(var j = 0; j < $scope.outputContainerSupportCodes.length ; j++){
						// vérifier que l'utilisateur a bien entré qq chose...
						if ($scope.outputContainerSupportCodes[j] !== undefined && $scope.outputContainerSupportCodes[j] !== null && $scope.outputContainerSupportCodes[j] !=='' ){
							var outputContainerUsed = $commonATM.newOutputContainerUsed(defaultOutputUnit, defaultOutputValue, atm.line, atm.column, inputContainer);
							//affectation du SupportCode
							outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
							atm.outputContainerUseds.push(outputContainerUsed);
						}
					}
					
					//2.4 mettre l'atm dans l'expérience
					$scope.experiment.atomicTransfertMethods.push(atm);
				});
			});	    
	   } else {
	    	console.log ('modif mode...');
	    	// l'utilisateur peut modifier - le nombre de supports en output et/ou les codes barres en output
	    	
	    	//0 copier les anciens ATMs (on n'a plus le basket...)
	    	var previousATMs=$scope.experiment.atomicTransfertMethods;
	    	
	    	//1 supprimer les ATM de l'expérience (on va les recréer)
	    	$scope.experiment.atomicTransfertMethods = [];
	    	
	    	//2 boucler sur sur les previousATMs
	    	previousATMs.forEach(function(prevatm){
	    		
			    	//2.1 création de l'ATM 
					var atm = newAtomicTransfertMethod(prevatm.line, prevatm.column);
			    
					//2.2 création d'1 inputContainerUsed (le meme que l'ancien)
					atm.inputContainerUseds.push (prevatm.inputContainerUseds[0]);
					
					//2.3 création de j outputContainerUsed
					for(var j = 0; j < $scope.outputContainerSupportCodes.length ; j++){
						// vérifier que l'utilisateur a bien entré qq chose...
						if ($scope.outputContainerSupportCodes[j] !== undefined && $scope.outputContainerSupportCodes[j] !== null && $scope.outputContainerSupportCodes[j] !==''){
							var outputContainerUsed = $commonATM.newOutputContainerUsed(defaultOutputUnit, defaultOutputValue, atm.line, atm.column, prevatm.inputContainerUseds[0]);
							//affectation du SupportCode 
							outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
							atm.outputContainerUseds.push(outputContainerUsed);
						}
					}
			
					//2.4 mettre l'atm dans l'expérience
					$scope.experiment.atomicTransfertMethods.push(atm);
	    	});	    	
	    }
	}
	
	
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
		//TEST.. CA MARCHE!!!
		console.log("generateATM ici ?????")
		$scope.generateATM($scope.outputContainerSupportCodes);
		
		$scope.$emit('childSaved', callbackFunction);
	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('cancel', function(e) {
		console.log("call event cancel");	
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
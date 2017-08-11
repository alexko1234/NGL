/* 11/08/2017 GA: experience One to Many qui n'utilise pas de datatable...
 * 
 */
angular.module('home').controller('SamplePrepCtrl',['$scope', '$parse', 'commonAtomicTransfertMethod','mainService',
                                                               function($scope, $parse, commonAtomicTransfertMethod, mainService) {

	

	  var inputSupportCode;
	  if(!$scope.isCreationMode()){
		  // mode nouveau, terminé
		  // l'experience existe recuperer LE locationOnContainerSupport.code des container ( il ne peux y en avoir qu'un seul)
		  inputSupportCode = $scope.$eval("atomicTransfertMethods|flatArray:'inputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);			
	  }else{
		  // trouver le/les codes des suports de tous les container en entree de l'experience (il peux y en avoir plusieurs..)
		  inputSupportCode = $scope.$eval("getBasket().get()|getArray:'support.code'|unique", mainService);
	  }	
	  
	  //traiter les 2 cas
	  if(angular.isArray(inputSupportCode) && inputSupportCode.length === 1){
		  $scope.messages.clear();
		  $scope.inputSupportCode = inputSupportCode[0];
	  }else if($scope.isCreationMode() && angular.isArray(inputSupportCode) && inputSupportCode.length > 1){
		  $scope.messages.clear();
		  $scope.messages.clazz = "alert alert-danger";
		  $scope.messages.text = Messages("experiments.input.error.only-1-plate");
		  $scope.messages.showDetails = false;
		  $scope.messages.open();
		  //// pourquoi un return ??? return false;
	  }else{
		  throw 'problem with inputSupportCode';
	  }
	 
	
	// creer un tableau sur lequel pourra boucler ng-repeat
	// ce tableau est initialisé sur onChange de" nbOutputSupport"
	// transformer au passage le type text en nombre=> *1
	$scope.initOutputContainerSupportCodes = function(nbOutputSupport){
		if(nbOutputSupport){
			$scope.outputContainerSupportCodes = new Array(nbOutputSupport*1);			
		}	
	}


	$scope.generateATM=function(outputContainerSupportCodes){
	    console.log ('ouput supports='+ outputContainerSupportCodes);
	    
	    if($scope.isCreationMode()){
	    	//En mode creation d'experience=> creation des ATM
	    	console.log ('creation mode...');
	    	 
	    	$scope.experiment.atomicTransfertMethods = []; // reinitaliser en cas ou l'utilisateur utilise plusieurs fois le bouton
	    	
	    	//Each promise object will have a "then" function that can take two arguments, a "success" handler and an "error" handler.
	    	$commonATM.loadInputContainerFromBasket(mainService.getBasket().get())
			  .then(function(containers) {	
				 containers.forEach(function(inputContainer){
					 
					//1 creation de l'ATM
					var atm = newAtomicTransfertMethod(inputContainer.support.line, inputContainer.support.column);
					
					//2 creation d'1 inputContainerUsed
					var inputContainerUsed=$commonATM.convertContainerToInputContainerUsed(inputContainer);
					atm.inputContainerUseds.push(inputContainerUsed);
					
					//3 creation de j outputContainerUsed
					for(var j = 0; j < $scope.outputContainerSupportCodes.length ; j++){
						var outputContainerUsed = $commonATM.newOutputContainerUsed(defaultOutputUnit, defaultOutputValue, atm.line, atm.column, inputContainer);
						//affectation du SupportCode
						outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
						atm.outputContainerUseds.push(outputContainerUsed);
					}
					
					//4 mettre l'atm dans l'experience
					$scope.experiment.atomicTransfertMethods.push(atm);
				});
			});	    
	    } else {
	    	console.log ('modifif mode...');
	    	/* TODO ...pour la modification d'une experience existente= > autre algo a faire !!
	    	l'utilisateur peut modifier - le code support Input ???????????????????
	    	                            - le nombre de supporte en output
	    	                            - les codes barres en output
	    	*/
	    	console.log ('convertExperiment...');
	    	convertExperimentATM(experiment.atomicTransfertMethods, experiment.state.code);
	    }
	};
	
	/*TODO creer une fonction de conversion inspiree de convertExperimentATMToDatatable...*/
	 
	var  convertExperimentATM= function(experimentATMs, experimentStateCode){
		var promises = [];
		
		var atms = experimentATMs;
		
		promises.push($commonATM.loadInputContainerFromAtomicTransfertMethods(atms));					
		promises.push($commonATM.loadOutputContainerFromAtomicTransfertMethods(atms));
		$q.all(promises).then(function (result) {
			var toto=undefined;
		});
	};
	
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");
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
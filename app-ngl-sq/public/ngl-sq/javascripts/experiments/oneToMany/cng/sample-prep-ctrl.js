/*
 
angular.module('home').controller('SamplePrepCtrl',['$scope', '$parse', 'atmToSingleDatatable','mainService',
                                              function($scope, $parse, atmToSingleDatatable, mainService) {
*/
	

angular.module('home').controller('SamplePrepCtrl',['$scope', '$parse', 'commonAtomicTransfertMethod','mainService',
                                                               function($scope, $parse, commonAtomicTransfertMethod, mainService) {

	

	  $scope.supportCode = [];
	  
	  if(!$scope.isCreationMode()){
		  $scope.supportCode = $scope.$eval("atomicTransfertMethods|flatArray:'inputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);			
	  }else{
		  $scope.supportCode = $scope.$eval("getBasket().get()|getArray:'support.code'|unique", mainService);
	  }	

	  if($scope.supportCode.length > 1){
			console.log(" > 1 support en entree");
			
			$scope.messages.clear();
			$scope.messages.clear();
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages("experiments.input.error.only-1-plate");
			$scope.messages.showDetails = false;
			$scope.messages.open();
			
			return false;
	  }
	
	  
	//var $commonATM = commonAtomicTransfertMethod($scope);
	

	$scope.generateATMtest=function(nbOutputSupport){
	    console.log ('Nb ouput support='+ nbOutputSupport);
	    
	    // !! si utilisateur chge de valeur il faut purger les atm precedemment crees ????
	  
		//atmService.data.save();
		//var allData = atmService.data.getData();
		//atmService.data.atm = [];
		
		//for(var i = 0; i < allData.length; i++){  
	    for(var i = 0; i < 17 ; i++){

			//var data = allData[i];
			//var atm = this.newAtomicTransfertMethod();
			//atm.inputContainerUseds.push($commonATM.convertContainerToInputContainerUsed(data.inputContainer));
			console.log(i+ " atm.inputContainerUseds.push($commonATM.convertContainerToInputContainerUsed(data.inputContainer))");
			
			for(var j = 0; j < nbOutputSupport ; j++){
			  //	atm.outputContainerUseds.push($commonATM.newOutputContainerUsed(this.defaultOutputUnit,this.defaultOutputValue,atm.line,atm.column, data.inputContainer));
			   console.log("  "+ j+ " atm.outputContainerUseds.push($commonATM.newOutputContainerUsed(this.defaultOutputUnit,this.defaultOutputValue,atm.line,atm.column, data.inputContainer))");	
			}
			
			//this.data.atm.push(atm);
		}				
	};
	
	
	/*Init
	var atmService = atmToSingleDatatable($scope, datatableConfigTubeParam);
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
	

	$scope.atmService = atmService;
   */
	
	
}]);
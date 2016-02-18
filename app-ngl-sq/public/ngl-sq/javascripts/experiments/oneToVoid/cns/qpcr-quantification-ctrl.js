angular.module('home').controller('OneToVoidQPCRQuantificationCtrl',['$scope', '$parse',
                                                             function($scope,$parse) {
	
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
				var concentration1 = $parse("experimentProperties.concentration1")(inputContainerUsed);
				if(concentration1){
					inputContainerUsed.concentration = concentration1;
				}
			}
			
		});			
	};
	
}]);
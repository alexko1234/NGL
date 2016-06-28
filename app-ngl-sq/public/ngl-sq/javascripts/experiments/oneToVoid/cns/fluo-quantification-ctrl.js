angular.module('home').controller('OneToVoidFluoQuantificationCNSCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
				var concentration1 = $parse("experimentProperties.concentration1")(inputContainerUsed);
				if(concentration1){
					inputContainerUsed.concentration = concentration1;
				}
				
				var volume1 = $parse("experimentProperties.volume1")(inputContainerUsed);
				if(volume1){
					inputContainerUsed.volume = volume1;
				}
				
				var quantity1 = $parse("experimentProperties.quantity1")(inputContainerUsed);
				if(quantity1){
					inputContainerUsed.quantity = quantity1;
				}
			}
			
			
		});			
	};
	
	
}]);
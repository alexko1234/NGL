angular.module('home').controller('OneToVoidChipMigrationCNSCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
				
				var measuredSize = $parse("experimentProperties.measuredSize")(inputContainerUsed);
				
				if(measuredSize){
					inputContainerUsed.size = measuredSize;
				}
				
				
			}
			
		});	
	
	};
	
		
	var profilsMap = {};
	angular.forEach($scope.experiment.atomicTransfertMethods, function(atm){
		var pos = atm.inputContainerUseds[0].locationOnContainerSupport.line+atm.inputContainerUseds[0].locationOnContainerSupport.column;
		var img = $parse('inputContainerUseds[0].experimentProperties.migrationProfile')(atm);
		this[pos] = img;
	},profilsMap)
	
	var internalProfils = profilsMap;
	$scope.getProfil=function(line, column){
		return internalProfils[line+column];
	};
	
	$scope.$watch("profils",function(newValues, oldValues){
		if(newValues){			
			var _profilsMap = {};
			angular.forEach(newValues, function(img){
				var pos = img.fullname.match(/_([A-F]\d+)\./)[1];
				this[pos] = img;			
			}, _profilsMap);
			
			internalProfils = _profilsMap;
			
			angular.forEach($scope.atmService.data.displayResult, function(dr){
				var pos = dr.data.inputContainerUsed.locationOnContainerSupport.line+dr.data.inputContainerUsed.locationOnContainerSupport.column;
				$parse('inputContainerUsed.experimentProperties.migrationProfile').assign(dr.data, this[pos]);
			}, _profilsMap);
		
		}
		
	})
	$scope.$watch("instrumentType", function(newValue, OldValue){
		if(newValue)
			$scope.atmService.addInstrumentPropertiesToDatatable(newValue.propertiesDefinitions);
	})
	
	
	
}]);
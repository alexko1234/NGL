// 08/11/2017 NGL-1326:  le bouton specifique CNG d'import des balances Mettler
// essai avec ou sans mainService
angular.module('home').controller('IlluminaDepotCNGCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse, $http) {
	
	
	var importDataMettler = function(){
		console.log('Import Mettler file');
		
		$scope.messages.clear();
		$http.post(jsRoutes.controllers.instruments.io.IO.importFile($scope.experiment.code).url+"?extraInstrument=labxmettlertoledo", $scope.file)
		.success(function(data, status, headers, config) {
			$scope.messages.setSuccess(Messages('experiments.msg.import.success'));

			// data est l'experience retournée par input.java
			$scope.experiment.instrumentProperties= data.instrumentProperties;
			
			// et reagents ....
			$scope.experiment.reagents=data.reagents;
			
			// reinit select File...
			$scope.file = undefined;
			angular.element('#importFileMettler')[0].value = null;
			
			//refresh  reagents !!!
			$scope.$emit('askRefreshReagents');
			
		})
		.error(function(data, status, headers, config) {
			///$scope.messages.setError(Messages('experiments.msg.import.error')); // Ne fonctionne que pour une seule erreur !!!!!!!
			
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages('experiments.msg.import.error');
			$scope.messages.setDetails(data);
			$scope.messages.showDetails = true;
			$scope.messages.open();	
	
			// reinit select File..
			$scope.file = undefined;
			// il faut aussi réinitaliser le bouton d'import
			angular.element('#importFileMettler')[0].value = null;
		});		
	};
	
	// evenement pas recu ????
	$scope.$on('activeEditMode', function(e) {
		console.log("XXXXXXXX  activeEditMode");
	});
	
	// 25/10/2017 FDS ajout pour l'import du fichier Mettler; 08/11/2017 renommage button2=>buttonMettler
	$scope.buttonMettler = {
			isShow:function(){

				// console.log('progressState:'+$scope.isInProgressState());
				// console.log('finishState:'+$scope.isFinishState() );
				console.log('editMode:'+$scope.isEditMode() );// isEditMode est toujours false l'action d'activation du bouton edition n'est pas vue ici... !!!
				
				//return ( ($scope.isInProgressState() || $scope.isInFinishState() ) && $scope.isEditMode() ) ;
				return ( $scope.isInProgressState() || $scope.isFinishState() );
				},
			isFileSet:function(){
				return ($scope.file === undefined)?"disabled":"";
			},
			click:importDataMettler	
		};
		
}]);
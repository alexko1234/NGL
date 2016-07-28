// FDS 21/06/2016 dupliqué depuis manyToOne.experiments.cns.x-to-tubes avec modifs
angular.module('home').controller('XToPlatesCtrl',['$scope', '$http','$parse',
                                                               function($scope, $http, $parse) {

	// FDS 29/06/2016 calcul de la ligne en fonction de position 96 numerotée en mode colonne ( 1=A1, 2=B1... 9=A2...) [ thx NW ]
	// reutilisable ailleurs ??
	$scope.setLineFromPosition96C = function(pos){	
		line= String.fromCharCode (((pos -1) % 8 ) + 65 );
		//console.log("set line to "+ line +"("+pos+")" );
		return line;
	};
	
	//  FDS 29/06/2016  calcul de la colonne en fonction de position 96 numerotée en mode colonne ( 1=A1, 2=B1... 9=A2...) [ thx NW ]
	//  reutilisable ailleurs ??
	$scope.setColFromPosition96C = function(pos){	
		c=Math.floor((pos -1) / 8) +1 ;
		column= c.toString();
		//console.log("set col to "+column+"("+pos+")" );
		return column;
	};
	
	/* FDS TEST 
	$scope.$on('updateInstrumentProperty', function(e, pName) {
		console.log("call event updateInstrumentProperty "+pName);
		
		if($scope.isCreationMode() && pName === 'program'){
			console.log("update program "+$scope.experiment.instrumentProperties[pName].value);
			var program = $scope.experiment.instrumentProperties[pName].value
			
			if ( program === 'pooling custom (mode colonne)'){
				//laisser comme maintenant...
			} else if ( program === 'pooling 3-plex (mode colonne)'){
				// pooling automatique...3 puits par 3 puits
				// TODO mais quoi ??
			} else if ( program === 'pooling 4-plex (mode colonne)'){	
				// pooling automatique...4 puits par 4 puits
				// TODO mais quoi ??
			} else if ( program === 'programme 1_normalisation' ) {
				// ???????
			}
		}
	});
	*/
	
	$scope.atmService.updateOutputConcentration = function(atm){
		
		if(atm){
		// ne pas faire l'update si déjà renseigné
			var concentration = undefined;
			var unit = undefined;
			var isSame = true;
			for(var i=0;i<atm.inputContainerUseds.length;i++){
				if(atm.inputContainerUseds[i].concentration !== null 
						&& atm.inputContainerUseds[i].concentration !== undefined){
					if(concentration === undefined && unit === undefined){
						concentration = atm.inputContainerUseds[i].concentration.value;
						unit = atm.inputContainerUseds[i].concentration.unit;
					}else{
						if(concentration !== atm.inputContainerUseds[i].concentration.value 
								|| unit !== atm.inputContainerUseds[i].concentration.unit){
							isSame = false;
							break;
						}
					}
				}
			}
			if(isSame 
					&& (atm.outputContainerUseds[0].concentration === null
							|| atm.outputContainerUseds[0].concentration.value === null
						|| atm.outputContainerUseds[0].concentration === undefined
						|| atm.outputContainerUseds[0].concentration.value === undefined)){
				atm.outputContainerUseds[0].concentration = angular.copy(atm.inputContainerUseds[0].concentration);				
			}
			
			
			// FDS  29/06/2016 positionnement automatique de ligne et colonne
			// DEVRAIT PAS ETRE DANS UPDATE CONCENTRATION ???....a voir avec GA  ( atomicTransfereservice ??? )
			//    !! marche seulement si on specife completement la fonction appellee    =setColFromPosition( )  marche pas
			atm.outputContainerUseds[0].locationOnContainerSupport.column=$scope.setColFromPosition96C(atm.viewIndex);
			atm.column=atm.outputContainerUseds[0].locationOnContainerSupport.column;
			
			atm.outputContainerUseds[0].locationOnContainerSupport.line=$scope.setLineFromPosition96C(atm.viewIndex);
			atm.line=atm.outputContainerUseds[0].locationOnContainerSupport.line;
			
			// récupérer le dernier supportCode entré par l'utilisateur
			atm.outputContainerUseds[0].locationOnContainerSupport.code=$scope.lastSupportCode;	// a ecrire avec $parse ???
			//pas de atm.xx= ???
			console.log("get last SupportCode:"+$scope.lastSupportCode);
			
			// récupérer le dernier storageCode entré par l'utilisateur
			atm.outputContainerUseds[0].locationOnContainerSupport.storageCode=$scope.lastStorageCode;	// a ecrire avec $parse ???
			//pas de atm.xx= ???
			console.log("get last StorageCode: "+$scope.lastStorageCode);
			
		}
	};
	

		
	$scope.update = function(atm, containerUsed, propertyName){
		console.log("update "+propertyName);
		
		if(propertyName === 'outputContainerUseds[0].concentration.value' ||
				propertyName === 'outputContainerUseds[0].concentration.unit' ||
				propertyName === 'outputContainerUseds[0].volume.value'){
			console.log("compute all input volume");
			
			angular.forEach(atm.inputContainerUseds, function(inputContainerUsed){
				computeInputVolume(inputContainerUsed, atm);
			});
			
		}else if(propertyName.match(/inputContainerUseds\[\d\].percentage/) != null){
			console.log("compute one input volume");
			computeInputVolume(containerUsed, atm);
		} 
		else if(propertyName === 'outputContainerUseds[0].locationOnContainerSupport.line' ){
			atm.line =$parse("outputContainerUseds[0].locationOnContainerSupport.line")(atm)
			console.log("support.line="+atm.line);
		}
		else if(propertyName === 'outputContainerUseds[0].locationOnContainerSupport.column' ){
			atm.column =$parse("outputContainerUseds[0].locationOnContainerSupport.column")(atm)
			console.log("support.column="+atm.column);
		}
		else if(propertyName === 'outputContainerUseds[0].locationOnContainerSupport.code' ){
			//stocker dans une variable tampon la valeur saisie
			$scope.lastSupportCode=$parse("outputContainerUseds[0].locationOnContainerSupport.code")(atm)
			console.log("lastSupportCode="+$scope.lastSupportCode);
		}
		else if(propertyName === 'outputContainerUseds[0].locationOnContainerSupport.storageCode' ){
			//stocker dans une variable tampon la valeur saisie
			$scope.lastStorageCode=$parse("outputContainerUseds[0].locationOnContainerSupport.storageCode")(atm)
			console.log("lastStorageCode="+$scope.lastStorageCode);
		}
		
		console.log("compute buffer volume");
		computeBufferVolume(atm);
	}
	
	var computeInputVolume = function(inputContainerUsed, atm){
		var getter = $parse("experimentProperties.inputVolume");
		var inputVolume = getter(inputContainerUsed);
		if(null === inputVolume  || undefined === inputVolume){
			inputVolume = {value : undefined, unit : 'µl'};
		}
		
		//compute only if empty
		var compute = {
			inputPercentage : $parse("percentage")(inputContainerUsed),
			inputConc : $parse("concentration")(inputContainerUsed),
			outputConc : $parse("outputContainerUseds[0].concentration")(atm),
			outputVol : $parse("outputContainerUseds[0].volume")(atm)
		
		};
		if($parse("(outputConc.unit ===  inputConc.unit)")(compute)){
			var result = $parse("(inputPercentage * outputConc.value *  outputVol.value) / (inputConc.value * 100)")(compute);
			console.log("result = "+result);
			
			if(angular.isNumber(result) && !isNaN(result)){
				inputVolume.value = Math.round(result*10)/10;				
			}else{
				inputVolume.value = undefined;
			}	
			getter.assign(inputContainerUsed, inputVolume);
		}else{
			inputVolume.value = undefined;
			getter.assign(inputContainerUsed, inputVolume);
		}
		return inputVolume.value;
		
	}
	
	var computeBufferVolume = function(atm){
		
		var inputVolumeTotal = 0;
		var getterInputVolume = $parse("experimentProperties.inputVolume");
		
		atm.inputContainerUseds.forEach(function(icu){
			var inputVolume = getterInputVolume(icu);
			if(null === inputVolume  || undefined === inputVolume || undefined === inputVolume.value ||  null === inputVolume.value){
				inputVolumeTotal = undefined;
			}else if(inputVolumeTotal !== undefined){
				inputVolumeTotal += inputVolume.value;
			}						
		})
		
		var outputVolume  = $parse("outputContainerUseds[0].volume")(atm);
		
		if(outputVolume && outputVolume.value && inputVolumeTotal){
			var bufferVolume = {value : undefined, unit : 'µl'};
			var result = outputVolume.value - inputVolumeTotal;
			
			if(angular.isNumber(result) && !isNaN(result)){
				bufferVolume.value = Math.round(result*10)/10;				
			}else{
				bufferVolume.value = undefined;
			}	
			
			$parse("outputContainerUseds[0].experimentProperties.bufferVolume").assign(atm, bufferVolume);
		}
	}
	
    //FDS ajout param ftype + {'fdrType':ftype} 
	var generateSampleSheet = function(ftype){
		console.log ("generateSampleSheet type="+ftype);
		
		$http.post(jsRoutes.controllers.instruments.io.IO.generateFile($scope.experiment.code).url, {'fdrType':ftype})
		.success(function(data, status, headers, config) {
			var header = headers("Content-disposition");
			var filepath = header.split("filename=")[1];
			
			var filename = filepath.split(/\/|\\/);
			filename = filename[filename.length-1];
			if(data!=null){
				$scope.messages.clazz="alert alert-success";
				$scope.messages.text=Messages('experiments.msg.generateSampleSheet.success')+" : "+filepath;
				$scope.messages.showDetails = false;
				$scope.messages.open();	
				
				var blob = new Blob([data], {type: "text/plain;charset=utf-8"});    					
				saveAs(blob, filename);
			}
		})
		.error(function(data, status, headers, config) {
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages('experiments.msg.generateSampleSheet.error');
			$scope.messages.setDetails(data);
			$scope.messages.showDetails = true;
			$scope.messages.open();				
		});
	};


	if($scope.atmService.inputContainerSupportCategoryCode !== "tube"){
		// FDS pas de boutons generateSampleSheet pour la main
		if (  $scope.experiment.instrument.categoryCode !== "hand") {
		  // FDS ajout 2eme bouton + param a la fonction generateSampleSheet....
		  $scope.setAdditionnalButtons([{
			  isDisabled : function(){return $scope.isNewState();} ,
			  isShow:function(){return !$scope.isNewState();},
			  //click:generateSampleSheet,
			  click: function(){return generateSampleSheet("samples")},
			  label: Messages("experiments.sampleSheet")+ " / échantillons"
		  },{
			  isDisabled : function(){return $scope.isNewState();} ,
			  isShow:function(){return !$scope.isNewState();},
			  //click:generateSampleSheet,
			  click: function(){return generateSampleSheet("buffer")},
			  label:Messages("experiments.sampleSheet")+ " / tampon"
		  }]);	
		}
	}
	
	//Only 96-well-plate is authorized=> force in cas of hand is used
	$scope.$watch("experiment.instrument.outContainerSupportCategoryCode", function(){
			$scope.experiment.instrument.outContainerSupportCategoryCode = "96-well-plate";
	});
	
	//FDS 23/06/2016 surcharger newAtomicTransfertMethod pour mettre line et column a null
	$scope.atmService.newAtomicTransfertMethod = function(){
		return {
			class:"ManyToOne",
			line:null,
			column:null, 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};		
	};
	
	// pour selects
	$scope.columns = ['1','2','3','4','5','6','7','8','9','10','11','12']; 
	$scope.lines=['A','B','C','D','E','F','G','H'];  
	
	//variables tampon
    $scope.lastSupportCode=null;
    $scope.lastStorageCode=null;
	
}]);
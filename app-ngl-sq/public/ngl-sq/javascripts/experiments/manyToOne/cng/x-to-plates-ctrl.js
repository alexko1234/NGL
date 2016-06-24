// FDS 21/06/2016 dupliqué depuis manyToOne.experiments.cns.x-to-tubes avec modifs
angular.module('home').controller('XToPlatesCtrl',['$scope', '$http','$parse',
                                                               function($scope, $http, $parse) {

	// pas appellée !!!!! c'est scope.atmService.updateConcentration du controler parent qui est appelle !!!
	/****
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
		}

	};
	****/
	
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
		// TESTS FDS
		else if(propertyName === 'outputContainerUseds[0].locationOnContainerSupport.code' ){
			var code =$parse("outputContainerUseds[0].locationOnContainerSupport.code")(atm)
			console.log("support.Code="+code);
		}else if(propertyName === 'outputContainerUseds[0].locationOnContainerSupport.line' ){
			var line =$parse("outputContainerUseds[0].locationOnContainerSupport.line")(atm)
			console.log("support.line="+line);
		}else if(propertyName === 'outputContainerUseds[0].locationOnContainerSupport.column' ){
			var column =$parse("outputContainerUseds[0].locationOnContainerSupport.column")(atm)
			console.log("support.column="+column);
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
	
	var generateSampleSheet = function(){
		$http.post(jsRoutes.controllers.instruments.io.IO.generateFile($scope.experiment.code).url,{})
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
			$scope.messages.showDetails = false;
			$scope.messages.open();				
		});
	};


	//TODO...generer une feuille de route differente pour les plaques...  !!!
	//if($scope.atmService.inputContainerSupportCategoryCode !== "tube"){
	if($scope.atmService.inputContainerSupportCategoryCode !== "96-well-plate"){
		$scope.setAdditionnalButtons([{
			isDisabled : function(){return $scope.isNewState();} ,
			isShow:function(){return !$scope.isNewState();},
			click:generateSampleSheet,
			label:Messages("experiments.sampleSheet")
		}]);
	}
	
	//Only 96-well-plate is authorized
	$scope.$watch("experiment.instrument.outContainerSupportCategoryCode", function(){
			$scope.experiment.instrument.outContainerSupportCategoryCode = "96-well-plate";
		});
	
	//NOTE si necessaire on peut surcharger des methodes du controler parent XToTubesCtrl
	
	/* il faut prefixer par le scope*/
	$scope.atmService.newAtomicTransfertMethod = function(){
		return {
			class:"ManyToOne",
			line:null,
			column:null, 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};		
	};
	
	//FDS 23/06/2016 surcharger le save pour ajouter  updateAtmLineColumn OUIIIII
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save on x-to-plates");	
		
		$scope.atmService.viewToExperiment($scope.experiment, false);
		$scope.updatePropertyUnit($scope.experiment); // ajout demandé par GA....
		$scope.updateConcentration($scope.experiment);
		$scope.updateAtm($scope.experiment);  // necessaire pour la mise a jour de line et column...
		$scope.$emit('childSaved', callbackFunction);
	});
	
	// POUR SELECT
	$scope.columns = [1,2,3,4,5,6,7,8,9,10,11,12]; 
	$scope.lines=['A','B','C','D','E','F','G','H'];  
	//// $scope.lines=[{label:'A'},{label:'B'},{label:'C'},{label:'D'},{label:'E'},{label:'F'},{label:'G'},{label:'H'}];  
	
	$scope.updateAtm = function(experiment){
		console.log("updateAtm.");	
		
		for(var j = 0 ; j < experiment.atomicTransfertMethods.length && experiment.atomicTransfertMethods != null; j++){
			
			var atm = experiment.atomicTransfertMethods[j];
			// mise a jour line/column a partir des valeur donnéées par l'utilisateur 
			
			atm.line = atm.outputContainerUseds[0].locationOnContainerSupport.line;
			atm.column = atm.outputContainerUseds[0].locationOnContainerSupport.column
			
			//TEST 
			//atm.outputContainerUseds[0].code=???????????????
			// TODO  recupere le champ generique de sortie du barcode de la plaque...
			//atm.code=
		}
	}
   
	
}]);
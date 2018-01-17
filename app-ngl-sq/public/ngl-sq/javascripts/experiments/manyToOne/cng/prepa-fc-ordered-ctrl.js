angular.module('home').controller('CNGPrepaFlowcellOrderedCtrl',['$scope', '$parse', '$http','atmToDragNDrop','mainService',
                                                               function($scope, $parse, $http, atmToDragNDrop, mainService) {
	
	//surcharge default/tubes-to-flowcell-ctrl.js !!!
	
	var atmToSingleDatatable = $scope.atmService.$atmToSingleDatatable;
	
	// Pour le datatble dans l'onglet feuille de calcul
	var columns = [  
	             {
		        	 "header":Messages("containers.table.support.number"),
		        	 "property":"atomicTransfertMethod.line",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"text",
		        	 "position":0,
		        	 "extraHeaders":{0:"lib normalisée"}
		         },	
		         {
		        	 "header":Messages("containers.table.supportCode"),
		        	 "property":"inputContainer.support.code",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"text",
		        	 "position":1,
		        	 "extraHeaders":{0:"lib normalisée"}
		         },	
		         {
		        	"header":Messages ("containers.table.codeAliquot"),
		 			"property": "inputContainer.contents",
		 			"filter": "getArray:'properties.sampleAliquoteCode.value'| unique",
		 			"order":false,
		 			"hide":true,
		 			"type":"text",
		 			"position":3,
		 			"render": "<div list-resize='cellValue' list-resize-min-size='3'>",
		        	 "extraHeaders":{0:"lib normalisée"}
			     },
		         {
		        	"header":Messages("containers.table.tags"),
		 			"property": "inputContainer.contents",
		 			"filter": "getArray:'properties.tag.value'| unique",
		 			"order":false,
		 			"hide":true,
		 			"type":"text",
		 			"position":4,
		 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
		        	 "extraHeaders":{0:"lib normalisée"}
		         },				         
				 {
		        	 "header":Messages("containers.table.concentration") + " (nM)",
		        	 "property":"inputContainerUsed.concentration.value",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"number",
		        	 "position":5,
		        	 "extraHeaders":{0:"lib normalisée"}
		         },
		         {
		        	 "header":Messages("containers.table.volume") + " (µL)",
		        	 "property":"inputContainerUsed.volume.value",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"number",
		        	 "position":6,
		        	 "extraHeaders":{0:"lib normalisée"}
		         },
		         {
		        	 "header":Messages("containers.table.state.code"),
		        	 "property":"inputContainer.state.code",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"text",
					 "filter":"codes:'state'",
		        	 "position":7,
		        	 "extraHeaders":{0:"lib normalisée"}
		         },
		         {
		        	 "header":Messages("containers.table.percentage"),
		        	 "property":"inputContainerUsed.percentage",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"number",
		        	 "position":50,
		        	 "extraHeaders":{0:"prep FC"}
		         },		  
		         //-- output section
		         {
		        	 "header":Messages("containers.table.code"),
		        	 "property":"outputContainerUsed.code",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
					 "type":"text",
		        	 "position":400,
		        	 "extraHeaders":{0:"prep FC"}
		         },
		         {
		        	 "header":Messages("containers.table.stateCode"),
		        	 "property":"outputContainer.state.code | codes:'state'",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
					 "type":"text",
		        	 "position":500,
		        	 "extraHeaders":{0:"prep FC"}
		         }
		         ];
	
	// 25/04/2017  si utilisation du janus alors il ne faut aussi afficher la colonne de la plaque
	if($scope.experiment.instrument.inContainerSupportCategoryCode !== "tube"){
		columns.push(
			 {
	        	 "header":Messages("containers.table.well"),
	        	 "property":"inputContainer.support.line+inputContainer.support.column",
	        	 "order":true,
				 "edit":false,
				 "hide":true,
	        	 "type":"text",
	        	 "position":1.1,
	        	 "extraHeaders":{0:"lib normalisée"}
	         }
		);
	}
	
	
	
	atmToSingleDatatable.data.setColumnsConfig(columns);
	
	atmToSingleDatatable.convertOutputPropertiesToDatatableColumn = function(property, pName){
		return   this.$commonATM.convertSinglePropertyToDatatableColumn(property,"outputContainerUsed."+pName+".",{"0":"prep FC"});
		
	};
	// ??? ca fait quoi ????
	atmToSingleDatatable.convertInputPropertiesToDatatableColumn = function(property, pName){
		if(property.code === "source"){
			return   this.$commonATM.convertSinglePropertyToDatatableColumn(property,"inputContainerUsed."+pName+".",{"0":"lib normalisée"});
		}else{
			return   this.$commonATM.convertSinglePropertyToDatatableColumn(property,"inputContainerUsed."+pName+".",{"0":"Dénaturation - neutralisation"});
		}
		
	};
	
	atmToSingleDatatable.addExperimentPropertiesToDatatable($scope.experimentType.propertiesDefinitions);
	
	$scope.$watch("instrumentType", function(newValue, OldValue){
		if(newValue)
			atmToSingleDatatable.addInstrumentPropertiesToDatatable(newValue.propertiesDefinitions);
	})
	
	
	$scope.setAdditionnalButtons([{
		isDisabled : function(){return $scope.isCreationMode();},
		isShow:function(){return ($scope.experiment.instrument.typeCode === 'janus-and-cBotV2')},
		click:$scope.fileUtils.generateSampleSheet,
		label:Messages("experiments.sampleSheet")
	}]);
	
	

	/* 06/01/2017 FDS ajout pour l'import du fichier Cbot-V2 */
	var importDataCbot = function(){
		console.log('Import cBot file');
		
		$scope.messages.clear();
		$http.post(jsRoutes.controllers.instruments.io.IO.importFile($scope.experiment.code).url, $scope.file)
		.success(function(data, status, headers, config) {		
			$scope.messages.setSuccess(Messages('experiments.msg.import.success'));
			
			// data est l'experience retournée par input.java
			// 16/01/2017 recuperer instrumentProperties 
			$scope.experiment.instrumentProperties= data.instrumentProperties;
			
			// et reagents ....
			$scope.experiment.reagents=data.reagents;
			
			// reinit select File...
			$scope.file = undefined;
			angular.element('#importFilecBot')[0].value = null;
			
			// NGL-1256 refresh special pour les reagents !!!
			$scope.$emit('askRefreshReagents');
			
		})
		.error(function(data, status, headers, config) {	
			// correction 18/12/2017 ; setError ne peut afficher qu'une seule erreur....
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages('experiments.msg.import.error');
			$scope.messages.setDetails(data);
			$scope.messages.showDetails = true;
			$scope.messages.open();	
			
			// reinit select File..
			$scope.file = undefined;
			// il faut aussi réinitaliser le bouton d'import
			angular.element('#importFileCbot')[0].value = null;
		});		
	};
	
	// 08/11/2017 renommage button=>buttonCbot
	$scope.buttonCbot = {
			isShow:function(){
				// 31/01/2017  activer le bouton d'import si l'experience est a InProgress
				// 19/12/2018 et pas en edit mode  || afficher bouton pour admin
				return ($scope.isInProgressState() && !$scope.mainService.isEditMode() || Permissions.check("admin")  );
				},
			isFileSet:function(){
				return ($scope.file === undefined)?"disabled":"";
			},
			click:importDataCbot		
		};
	
	
	/* 25/10/2017 FDS ajout pour l'import du fichier Mettler */
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
	
	// 25/10/2017 FDS ajout pour l'import du fichier Mettler; 08/11/2017 renommage button2=>buttonMettler
	$scope.buttonMettler = {
			isShow:function(){
				// visible meme si terminé, mais seulement en mode edition
				//return ( ($scope.isInProgressState()||$scope.isFinishState()) && $scope.isEditMode() ); MARCHE PAS !!!!
				return ( $scope.isInProgressState()||$scope.isFinishState() );
				},
			isFileSet:function(){
				return ($scope.file === undefined)?"disabled":"";
			},
			click:importDataMettler	
		};
	
	
	// 07/02/2017 si l'utilisateur modifie le codeStrip OU le code Flowcell OU l'instrument  il doit recharger le fichier pour qu'on puisse garantir la coherence !!
    //-1- stripCode.value
	$scope.$watch("experiment.instrumentProperties.stripCode.value", function(newValue, OldValue){
			if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {
				if ( $scope.experiment.instrumentProperties.cbotFile ) { $scope.experiment.instrumentProperties.cbotFile.value = undefined;}
			}
	});	
	
	//-2- code Flowcell
	$scope.$watch("experiment.instrumentProperties.containerSupportCode.value", function(newValue, OldValue){
		if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {
			if ( $scope.experiment.instrumentProperties.cbotFile ) { $scope.experiment.instrumentProperties.cbotFile.value = undefined; } 
		    checkFCsequencingType();// ajout 24/04/2017 NGL-1325
		}
	});	
	
	//-3- code cBot
	$scope.$watch("experiment.instrument.code" , function(newValue, OldValue){
		if ( newValue !== OldValue ) {
			if ( $scope.experiment.instrumentProperties.cbotFile ) { $scope.experiment.instrumentProperties.cbotFile.value = undefined; }
			
			// 18/12/2017 NGL-1754: restreindre instrument a MarieCurix-A  ou MarieCurix-B quand le type de sequencage choisi est sequencage choisi est NovaSeq 6000
			$scope.messages.clear();
			var NovaSeq6000Regexp=/MarieCurix/;
			if (( $scope.experiment.experimentProperties.sequencingType.value === "NovaSeq 6000") && ( null===$scope.experiment.instrument.code.match(NovaSeq6000Regexp))){
				$scope.messages.clazz = "alert alert-warning";
				$scope.messages.text = "L'instrument choisi n'est pas un NovaSeq 6000";
				$scope.messages.showDetails = false;
				$scope.messages.open();
			}	
		}
	});	
	
    // -4-ajout 24/04/2017 NGL-1325
	$scope.$watch("experiment.experimentProperties.sequencingType.value", function(newValue, OldValue){
		if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {
			console.log('sequencing type changed to :'+ newValue);
			checkFCsequencingType();
			setFeuilleCalcul();// ajout 16/01/2018 NGL-1767 modification dynamique de la feuille de calcul
		} 
	});	
	
	// -5- ajout 16/01/2018: NGL-1767 modification dynamique de la feuille de calcul
	$scope.$watch("experiment.instrument.outContainerSupportCategoryCode", function(newValue, OldValue){
		if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {	
			setFeuilleCalcul();
		} 
	});	
	
	
	function checkFCsequencingType (){
		var H4000fcRegexp= /^[A-Za-z0-9]*BBXX$/;
		var HXfcRegexp= /^[A-Za-z0-9]*ALXX$/;
		// ??? pattern pour NovaSeq6000 ???? reponse Illumina en attente
		
		$scope.messages.clear();
		// !! peut etre non encore definie...
		var fcBarcode=undefined;
		if ( $scope.experiment.instrumentProperties.containerSupportCode ) {
			fcBarcode= $scope.experiment.instrumentProperties.containerSupportCode.value;
		}
		/// ! fcBarcode.test ( ) fonctionne pas !!!
		if (($scope.experiment.experimentProperties.sequencingType.value === 'Hiseq 4000') && ( null===fcBarcode.match(H4000fcRegexp))) {
		   //( null===$scope.experiment.instrumentProperties.containerSupportCode.value.match(H4000fcRegexp))) {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Code Flowcell n'est pas du type Hiseq 4000 (*BBXX)";
			$scope.messages.showDetails = false;
			$scope.messages.open();
		} else if (($scope.experiment.experimentProperties.sequencingType.value === 'Hiseq X') && ( null===fcBarcode.match(HXfcRegexp))) {
				//( null ===$scope.experiment.instrumentProperties.containerSupportCode.value.match(HXfcRegexp))) {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Code Flowcell n'est pas du type Hiseq X (*ALXX)";
			$scope.messages.showDetails = false;
			$scope.messages.open();
		} else if (($scope.experiment.experimentProperties.sequencingType.value === 'NovaSeq 6000') && ( $scope.experiment.instrument.code !== undefined )) {
			//18/12/2017 NGL-1754 : restreindre instrument a MarieCurix-A  ou MarieCurix-B quand le type de sequencage choisi est NovaSeq 6000
			var NovaSeq6000Regexp=/MarieCurix/;
		
			console.log('intrument='+ $scope.experiment.instrument.code);	
			if ( null===$scope.experiment.instrument.code.match(NovaSeq6000Regexp) ){
				$scope.messages.clazz = "alert alert-warning";
				$scope.messages.text = "L'instrument choisi n'est pas un NovaSeq 6000";
				$scope.messages.showDetails = false;
				$scope.messages.open();
			}		
		} else {
			console.log('checkFCsequencingType OK');
			$scope.messages.clear();
		}	
	}
	
	// ajout 25/04/2017 NGL-1287: les supports d'entree ne doivent etre QUE des plaques pour le Janus
	if ( $scope.isCreationMode() && ($scope.experiment.instrument.typeCode === 'janus-and-cBotV2')){
		// !! en mode creation $scope.experiment.atomicTransfertMethod n'est pas encore chargé=> passer par Basket (ajouter mainService dans le controller)
		// $parse marche pas ici.... var tmp = $scope.$parse("getBasket().get()|getArray:'support.categoryCode'|unique",mainService); 
		var categoryCodes = $scope.$eval("getBasket().get()|getArray:'support.categoryCode'|unique",mainService);
		var supports = $scope.$eval("getBasket().get()|getArray:'support.code'|unique",mainService);
		
		if ( ((categoryCodes.length === 1) && ( categoryCodes[0] ==="tube")) || (categoryCodes.length > 1) ){
			                          // only tubes                                      mixte
			$scope.messages.setError(Messages('experiments.input.error.only-plates')+ ' si vous utilisez cet instrument'); 
			
			$scope.experiment.instrument.typeCode =null; // pas suffisant pour bloquer la page..
			$scope.atmService = null; //empeche la page de se charger...
			$scope.experimentTypeTemplate = null;
		} else {
			// plaques uniqt mais il y a une limite !! 09/08/2017 NGL-1550: passage a 8 sources pour le Janus
			if ( supports.length > 8 ){ 
				$scope.messages.setError(Messages('experiments.input.error.maxSupports', 8));
				$scope.atmService = null; //empeche la page de se charger...
				$scope.experimentTypeTemplate = null;
			}
		}
	}
	
	// ajout 16/01/2018 : NGL-1767 modification dynamique de la feuille de calcul
	function setFeuilleCalcul(){
		console.log('setFeuilleCalcul');
		
		if ( $scope.experiment.experimentProperties.sequencingType.value ==='NovaSeq 6000' ) {
			// et le cas flowcell-1 ????? existe pour MiSeq?????
			if ( "experiment.instrument.outContainerSupportCategoryCode"==='flowcell-2'){
				console.log('S2...NaoH=37/0.2N; TrisHCL=38/400; EPX=525; Phix=1' );
			}
			if ( "experiment.instrument.outContainerSupportCategoryCode"==='flowcell-4'){
				console.log('S4...NaoH=77/0.2N; TrisHCL=78/400; EPX=1085; Phix=1');
			}
		} else {
			// Hiseq-4000 ou Hiseq-X: remettre les valeurs par defaut..
			console.log('default...NaoH=5/0.1N; TrisHCL=5/200; EPX=35; Phix=3 ?');
		}
	}
	

	// TODO ???? 15/01/2018 faire les calculs en Javascript au lieu de Drools ????
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		if ( col.property === 'inputContainerUsed.experimentProperties.inputVolume2.value'){ 
			console.log('Volume engagé modifié....');
		    computeConcentration(value.data);
		}
	}
	
	//TEST
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		if ( col.property === 'inputContainerUsed.experimentProperties.finalVolume.value'){ 
			console.log('Volume final modifié....');
		    computeConcentration(value.data);
		}
	}
	
	// TODO ???? 15/01/2018 faire les calculs en Javascript au lieu de Drools ????
	var computeConcentration = function(udtData){
		
		var getterFinalConcentration2=$parse("inputContainerUsed.finalConcentration2.value")(udtData);
		
		var compute = {
				inputConc :  $parse("inputContainerUsed.concentration.value")(udtData), 
				engagedVol :  $parse("inputContainerUsed.inputVolume2.value")(udtData), 
				finalVol:  $parse("outputContainerUsed.finalVolume.value")(udtData),

				isReady:function(){
					// !! final volume doit imperativement etre != 0 sino div by 0
					return ((this.finalVol) && ( this.inputConc ));
				}
		};
		

		if(compute.isReady()){
			console.log("compute.isReady");
			
			var finalConcentration= compute.inputConc * compute. engagedVol / compute.finalVol;
			
			// arrondir...
			//DROOLS: new PropertySingleValue(Math.round((convertPVToDouble($finalConcentration1)*convertPVToDouble($inputVolume2)/convertPVToDouble($finalVolume))*100.0)/100.0,"nM");
			if(angular.isNumber(finalConcentration) && !isNaN(finalConcentration)){
				finalConcentration = Math.round(finalConcentration*100.0)/100.0;				
			}
			
			console.log("conc finale = "+finalConcentration);
			getterFinalConcentration2.assign(udtData, finalConcentration);
			
		}else{
			console.log("Impossible de calculer le volume final: valeurs manquantes");
			getterFinalConcentration2.assign(udtData, undefined);
			//getterFinalConcentration2.assign(udtData, 999);//DEBUG...
		}
	}
	
}]);
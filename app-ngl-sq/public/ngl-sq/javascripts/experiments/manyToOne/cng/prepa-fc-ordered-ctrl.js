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
		    checkFCpattern();// ajout 24/04/2017 NGL-1325
		}
	});	
	
	//-3- code cBot
	$scope.$watch("experiment.instrument.code" , function(newValue, OldValue){
		if ( newValue !== OldValue ) {
			// reset du fichier Cbot
			if ( $scope.experiment.instrumentProperties.cbotFile ) { $scope.experiment.instrumentProperties.cbotFile.value = undefined; }

			// 18/12/2017 NGL-1754: restreindre instrument a MarieCurix-A  ou MarieCurix-B quand le type de sequencage choisi est sequencage choisi est NovaSeq 6000
			$scope.messages.clear();
			var NovaSeq6000Regexp=/MarieCurix/;
			// 17/01/2018 il y a 2 sequencingType !! TODO noms exacts a definir...
			if ((($scope.experiment.experimentProperties.sequencingType.value === "NovaSeq 6000 / S2")||
				 ($scope.experiment.experimentProperties.sequencingType.value === "NovaSeq 6000 / S4")) &&
				 (null===$scope.experiment.instrument.code.match(NovaSeq6000Regexp))){
				$scope.messages.clazz = "alert alert-warning";
				$scope.messages.text = "L'instrument choisi n'est pas un NovaSeq 6000";
				$scope.messages.showDetails = false;
				$scope.messages.open();
			} 
			// 17/01/2018 reset sequencingType 
			$scope.experiment.experimentProperties.sequencingType.value=undefined; 
		}
	});	
	
    // -4-ajout 
	$scope.$watch("experiment.experimentProperties.sequencingType.value", function(newValue, OldValue){
		if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {
			console.log('sequencing type changed to :'+ newValue);
			// 18/12/2017 NGL-1754 : restreindre instrument a MarieCurix-A  ou MarieCurix-B quand le type de sequencage choisi est NovaSeq 6000
			// 18/01/2018 subdiviser en 2... EN COURS.. labels Novaseq pas definitifs: voir Julie...
		    if (((newValue === 'NovaSeq 6000 / S2')||(newValue === 'NovaSeq 6000 / S4'))   && ( $scope.experiment.instrument.code !== undefined )) {
		    	// attention maintenir a jour !!!!
		    	var NovaSeq6000Regexp=/MarieCurix/;
		    	if ( null===$scope.experiment.instrument.code.match(NovaSeq6000Regexp) ){
		    		$scope.messages.clazz = "alert alert-warning";
		    		$scope.messages.text = "L'instrument choisi n'est pas un NovaSeq 6000";
		    		$scope.messages.showDetails = false;
		    		$scope.messages.open();
		    	}
			}
		    
			checkFCpattern();
			// ajout 16/01/2018 NGL-1767 modification dynamique de la feuille de calcul
			setFeuilleCalcul();
		} 
	});	
	
	function checkFCpattern (){	
		// en mode 'a sauvegarder'  experimentProperties n'est pas encore defini
		if (undefined===$scope.experiment.experimentProperties|| undefined===$scope.experiment.instrumentProperties.containerSupportCode ){
			console.log('pas encore de test possible');
			return;
		}
		
		//if (undefined===$scope.experiment.experimentProperties.sequencingType.value || undefined===$scope.experiment.instrumentProperties.containerSupportCode.value ){
		//	console.log('pas de test possible');
		//	return;
		//}
		
		var H4000fcRegexp= /^[A-Za-z0-9]*BBXX$/;
		var HXfcRegexp= /^[A-Za-z0-9]*ALXX$/;
		// var Nv6000S1fcRegexp= /^[A-Za-z0-9]*???XX$/; // pas encore dispo chez Illumina
		var Nv6000S2fcRegexp= /^[A-Za-z0-9]*DMXX$/; // info Illumina 25/01/2018
		var Nv6000S4fcRegexp= /^[A-Za-z0-9]*DSXX$/; // info Illumina 25/01/2018
		
		$scope.messages.clear();
		fcBarcode=$scope.experiment.instrumentProperties.containerSupportCode.value;
		seqType=$scope.experiment.experimentProperties.sequencingType.value;
		//console.log('check FC pattern: FC='+ fcBarcode + 'sequencing type='+seqType );
		
		// !! labels NovaSeq pas definitifs: voir Julie...
		if ((seqType === 'Hiseq 4000') && ( null===fcBarcode.match(H4000fcRegexp))) {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Code Flowcell n'est pas du type 'Hiseq 4000' (*BBXX)";
			$scope.messages.showDetails = false;
			$scope.messages.open();
		} else if ((seqType === 'Hiseq X') && ( null===fcBarcode.match(HXfcRegexp))) {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Code Flowcell n'est pas du type 'Hiseq X' (*ALXX)";
			$scope.messages.showDetails = false;
			$scope.messages.open();
		} else if ((seqType === 'NovaSeq 6000 / S2') && (null===fcBarcode.match(Nv6000S2fcRegexp))) {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Code Flowcell n'est pas du type 'NovaSeq 6000 / S2' (*DMXX)";
			$scope.messages.showDetails = false;
			$scope.messages.open();
		} else if ((seqType === 'NovaSeq 6000 / S4') && (null===fcBarcode.match(Nv6000S4fcRegexp))) {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Code Flowcell n'est pas du type 'NovaSeq 6000 / S4' (*DSXX)";
			$scope.messages.showDetails = false;
			$scope.messages.open();		
		} else {
			//console.log('checkFCpattern OK !!!');
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
		console.log('setFeuilleCalcul...');
		
		// !! la feuille de calcul peut ne pas encore etre prete
		// !! labels NovaSeq pas definitifs: voir Julie...
		if ($scope.atmService.data.datatable.allResult) { 
			if ( $scope.experiment.experimentProperties.sequencingType.value ==='NovaSeq 6000 / S2' ) {
				console.log('S2...engag=150; NaoH=37/0.2N; TrisHCL=38/400; EPX=525');
				updateAllInputContainerUsedsPropertyValue("inputVolume2","150");
				updateAllInputContainerUsedsPropertyValue("NaOHConcentration","0.2N");	
				updateAllInputContainerUsedsPropertyValue("NaOHVolume","37");
				updateAllInputContainerUsedsPropertyValue("trisHCLConcentration","400000000");	
				updateAllInputContainerUsedsPropertyValue("trisHCLVolume","38");
				updateAllInputContainerUsedsPropertyValue("masterEPXVolume","525");	
				
			} else if ( $scope.experiment.experimentProperties.sequencingType.value ==='NovaSeq 6000 / S4' ) {
				console.log('S4...engag=310; NaoH=77/0.2N; TrisHCL=78/400; EPX=1085');
				updateAllInputContainerUsedsPropertyValue("inputVolume2","310");
				updateAllInputContainerUsedsPropertyValue("NaOHConcentration","0.2N");
				updateAllInputContainerUsedsPropertyValue("NaOHVolume","77");
				updateAllInputContainerUsedsPropertyValue("trisHCLConcentration","400000000");	
				updateAllInputContainerUsedsPropertyValue("trisHCLVolume","78");
				updateAllInputContainerUsedsPropertyValue("masterEPXVolume","1085");		
			} else {
				// Hiseq-4000 ou Hiseq-X: remettre les valeurs par defaut..
				console.log('default...engag=5; NaoH=5/0.1N; TrisHCL=5/200; EPX=35');
				updateAllInputContainerUsedsPropertyValue("inputVolume2","5");
				updateAllInputContainerUsedsPropertyValue("NaOHConcentration","0.1N");
				updateAllInputContainerUsedsPropertyValue("NaOHVolume","5");
				updateAllInputContainerUsedsPropertyValue("trisHCLConcentration","200000000");	
				updateAllInputContainerUsedsPropertyValue("trisHCLVolume","5");
				updateAllInputContainerUsedsPropertyValue("masterEPXVolume","35");
			}
			
			// ne faire l'update du datatable qu'apres les 6 appels a  updateAllInputContainerUsedsPropertyValue !!!
			$scope.atmService.data.updateDatatable();
		} else {
			//debug
			console.log('f de calcul pas prete...');
		}	
	}
	
	// copié d'après  $scope.updateAllOutputContainerProperty   dans tubes-to-flowcell-ctrl.js
	// function locale, ne pas la mettre dans $scope
	updateAllInputContainerUsedsPropertyValue = function(propertyCode, value){
		for(var i = 0 ; i < $scope.atmService.data.atm.length ; i++){
			var atm = $scope.atmService.data.atm[i];
			
			$parse("inputContainerUseds[0].experimentProperties."+propertyCode+".value").assign(atm, value);
			
			// si la colonne mise a jour est le volume engagé il faut relancer le calcul de concentration finale
			if (propertyCode ==='inputVolume2'){
				// !!!!! il manque "inputContainer.concentration dans l'atm !!!!
				computeConcentrationAtm(atm, i);		
			}
		}
		/// NON faire l'updateDatatable dans l'appelant sinon executee 6 fois de suite !!!
		/// $scope.atmService.data.updateDatatable();
	};
	
	// 15/01/2018 faire les calculs en Javascript au lieu de Drools ????
	// surcharger celle de tubes-to-flowcell-ctrl.js pour declencher les calculs
	// utilisateur modifie une cellule "volume final" ( changeValueOnFlowcellDesign est appelle depuis le scala.html )
	$scope.changeValueOnFlowcellDesign = function(i){
		//i=atm.line; 
		console.log('% depot ou  % phix  ou Volume final modifié:  atm.line: '+ i );
		$scope.atmService.data.updateDatatable(); // ca c'est qui est fait dans tubes-to-flowcell-ctrl.js: met a jour TOUT le udt !!!	
		
		//PB  est atm.line  ne correspond pas forcement a l'index i de l'atm !!!!!
		console.log ( "apres update : new final vol="+ $scope.atmService.data.atm[i-1].outputContainerUseds[0].experimentProperties.finalVolume.value);
		
		//test 1 recalculer la concentration finale pour LA ligne changee    MARCHE PAS !!!!!!!
		//computeConcentrationAtm($scope.atmService.data.atm[i-1], i-1));	
		
		//test : recalculer toutes les lignes....MARCHE PAS NON PLUS.. quel parametre passer a computeConcentration ???
       //computeConcentration($scope.atmService);
	};
	

	// 15/01/2018 faire les calculs en Javascript au lieu de Drools
	// utilisateur modifie une cellule "volume engagé"
	$scope.updatePropertyFromUDT = function(value, col){
		console.log("update from property : "+col.property);
		if ( col.property === 'inputContainerUsed.experimentProperties.inputVolume2.value'){ 
			console.log('Volume engagé modifié....');
			// value.data ne contient qu'une seule ligne
		    computeConcentration(value.data);
		}
	}
	
	// 15/01/2018 faire les calculs en Javascript au lieu de Drools
	var computeConcentration = function(udtData){	
		console.log("computeConcentration (udtData)....");
		var getterFinalConcentration2=$parse("inputContainerUsed.experimentProperties.finalConcentration2.value");
		
		var compute = {
				inputConc : $parse("inputContainer.concentration.value")(udtData), 
				engagedVol: $parse("inputContainerUsed.experimentProperties.inputVolume2.value")(udtData), 
				finalVol:   $parse("outputContainerUsed.experimentProperties.finalVolume.value")(udtData),

				isReady:function(){
					// !! final volume doit imperativement etre != 0 sinon div by 0
					console.log('inputConc='+ this.inputConc +'  engagedVol='+ this.engagedVol +'  finalVol='+ this.finalVol);
					return (this.finalVol && this.engagedVol);
				}
		};
		
		if(compute.isReady()){
			var finalConcentration= compute.inputConc * compute.engagedVol / compute.finalVol;
			// arrondir...
			if(angular.isNumber(finalConcentration) && !isNaN(finalConcentration)){
				finalConcentration = Math.round(finalConcentration*100.0)/100.0;	
			}
			console.log("conc finale = "+finalConcentration);
			getterFinalConcentration2.assign(udtData, finalConcentration);
			
		}else{
			console.log("Impossible de calculer la concentration finale: valeurs manquantes");
			getterFinalConcentration2.assign(udtData, undefined);
		}
	}
	
	// version pour les cas on modifie seulement 1 atm
	// PB on a pas inputContainer.concentration dans l'atm !!!!, passer par  datatable.allResult[i], faut i en parametre
	var computeConcentrationAtm = function(atm, i){
		console.log("computeConcentration atm....");
		
		var getterFinalConcentration2=$parse("inputContainerUseds[0].experimentProperties.finalConcentration2.value");
		var test=$parse("atmService.data.datatable.allResult[i].inputContainerUsed.concentration.value");
			
		var compute = {
				inputConc : $scope.atmService.data.datatable.allResult[i].inputContainerUsed.concentration.value,
				engagedVol: $parse("inputContainerUseds[0].experimentProperties.inputVolume2.value")(atm), 
				finalVol:   $parse("outputContainerUseds[0].experimentProperties.finalVolume.value")(atm),

				isReady:function(){
					// !! final volume doit imperativement etre != 0 sinon div by 0
					console.log('inputConc='+ this.inputConc +'  engagedVol='+ this.engagedVol +'  finalVol='+ this.finalVol);
					return (this.finalVol && this.engagedVol );
				}
		};
		
		if(compute.isReady()){
			var finalConcentration = compute.inputConc * compute.engagedVol / compute.finalVol;
			// arrondir...
			if(angular.isNumber(finalConcentration) && !isNaN(finalConcentration)){
				finalConcentration = Math.round(finalConcentration*100.0)/100.0;	
			}
			
			console.log("conc finale = "+finalConcentration);
			getterFinalConcentration2.assign(atm, finalConcentration);
		} else {
			console.log("Impossible de calculer la concentration finale: valeurs manquantes");
			getterFinalConcentration2.assign(atm, undefined);
		}
	}
	
}]);
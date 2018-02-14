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
	// 31/01//2018  pas de fichier Mettler pour les cbot interne des novaseq 
	$scope.buttonMettler = {
			isShow:function(){
				// visible meme si terminé, mais seulement en mode edition
				//return ( ($scope.isInProgressState()||$scope.isFinishState()) && $scope.isEditMode() ); // MARCHE PAS: editMode pas vu ici
				return ( ( $scope.isInProgressState()||$scope.isFinishState()) &&  (null===$scope.experiment.instrument.code.match(/MarieCurix/)) );
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
			$scope.messages.clear();
			
			// ajout 14/02/2018
			if ( undefined !=$scope.experiment.experimentProperties ){
				checkNovaSeq($scope.experiment.experimentProperties.sequencingType.value,  $scope.experiment.instrument.code );
			}
		
			// ajout 24/04/2017 NGL-1325
		    checkFCpattern();
		}
	});	
	
	//-3- code instrument
	$scope.$watch("experiment.instrument.code" , function(newValue, OldValue){
		if ( newValue !== OldValue ) {
			// reset du fichier Cbot
			if ( $scope.experiment.instrumentProperties.cbotFile ) { $scope.experiment.instrumentProperties.cbotFile.value = undefined; }
			$scope.messages.clear();
			
			// 14/02/2018 separer dans une fonction
			if ( undefined !=$scope.experiment.experimentProperties ){
				checkNovaSeq($scope.experiment.experimentProperties.sequencingType.value,  newValue);
			}
			
			// 13/02/2018 ajouter la verification FC 	
			checkFCpattern();
		}
	});	
	
    //-4- sequencingType
	$scope.$watch("experiment.experimentProperties.sequencingType.value", function(newValue, OldValue){
		if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {
			console.log('sequencing type changed to :'+ newValue);
			$scope.messages.clear();
			
			// 14/02/2018 separer dans une fonction
			checkNovaSeq(newValue,  $scope.experiment.instrument.code);
			
			checkFCpattern();
			
			// ajout 16/01/2018 NGL-1767 modification dynamique de la feuille de calcul
			// !! marche pas si le design Flowcell n'as pas encore ete fait...
			setFeuilleCalcul();
			
			setVolumeFinal();
		} 
	});	
	
	// 18/12/2017 NGL-1754 : restreindre instrument a MarieCurix-A ou MarieCurix-B quand le type de sequencage choisi est NovaSeq 6000
	// !! /MarieCurix/ --> HARDCODED.. a maintenir a jour en cas d'evolution...
	// 14/02/2018 creer fonction dediee

	function checkNovaSeq(sequencingType,instrumentCode){
		if ( undefined===sequencingType || undefined===instrumentCode ){ return; }

		if ( (null!=sequencingType.match(/NovaSeq 6000/) ) && (null===instrumentCode.match(/MarieCurix/))){
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "L'instrument choisi n'est pas un NovaSeq 6000";
			///$scope.messages.showDetails = false;
			$scope.messages.open();			
		} 
		
		//13/02/2018 ajouter la verification inverse
		if ( (null===sequencingType.match(/NovaSeq 6000/) ) && (null != instrumentCode.match(/MarieCurix/))){
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Le type de séquencage choisi n'est pas NovaSeq 6000 / *";
			///$scope.messages.showDetails = false;
			$scope.messages.open();
		} 
	}
	
	
	function checkFCpattern (){	
		// en mode 'a sauvegarder'  experimentProperties n'est pas encore defini
		if ( undefined===$scope.experiment.experimentProperties || undefined===$scope.experiment.instrumentProperties.containerSupportCode ){ return; }
		
		var H4000fcRegexp= /^[A-Za-z0-9]*BBXX$/;
		var HXfcRegexp= /^[A-Za-z0-9]*ALXX$/;
		// var Nv6000S1fcRegexp= /^[A-Za-z0-9]*???XX$/; // pas encore d'info sur le pattern
		var Nv6000S2fcRegexp= /^[A-Za-z0-9]*DMXX$/; // info Illumina 25/01/2018
		var Nv6000S4fcRegexp= /^[A-Za-z0-9]*DSXX$/; // info Illumina 25/01/2018
		
		fcBarcode=$scope.experiment.instrumentProperties.containerSupportCode.value;
		seqType=$scope.experiment.experimentProperties.sequencingType.value;
		//console.log('check FC pattern: FC='+ fcBarcode + 'sequencing type='+seqType );
		
		if ((seqType === 'Hiseq 4000') && ( null === fcBarcode.match(H4000fcRegexp))) {
			setAlert($scope.messages.text, "Code Flowcell n'est pas du type 'Hiseq 4000' (*BBXX)")
			
		} else if ((seqType === 'Hiseq X') && ( null === fcBarcode.match(HXfcRegexp))) {
			setAlert($scope.messages.text, "Code Flowcell n'est pas du type 'Hiseq X' (*ALXX)");	
			
		} else if ((seqType === 'NovaSeq 6000 / S2') && (null === fcBarcode.match(Nv6000S2fcRegexp))) {
			setAlert($scope.messages.text,"Code Flowcell n'est pas du type 'NovaSeq 6000 / S2' (*DMXX)");
			
		} else if ((seqType === 'NovaSeq 6000 / S4') && (null === fcBarcode.match(Nv6000S4fcRegexp))) {
			setAlert($scope.messages.text,"Code Flowcell n'est pas du type 'NovaSeq 6000 / S4' (*DSXX)");

		} 
		//else if ((seqType === 'NovaSeq 6000 / S1') && (null === fcBarcode.match(Nv6000S1fcRegexp))) { ....}
	}
	
	//14/02/2018 fonction qui affiche Alerte simple ou multiligne
	// test avec variable pour libelle synamique...marche pas...
	function setAlert(prevMsg, newMsg){
		if ( prevMsg===undefined ) {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = newMsg;
			$scope.messages.open();	
		} else {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Alertes";
				
			//var data = { Instrument_et_Expérience:[ prevMsg ]} // pour utilisation de push
			//data.Instrument_et_Expérience.push(newMsg);
			
			var data = { "Instrument et Expérience":[prevMsg,newMsg ]};// cle avec des espaces marche si on n'utilise pas push...
		
			$scope.messages.setDetails(data);
			$scope.messages.showDetails = true;
			$scope.messages.open();	
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
	
	//------------ 16/01/2018 : NGL-1767 modification dynamique de la feuille de calcul en fonction du mode de sequencage-------
	//                          remplacer les calculs drools par calculs javascript
	
	// !! ne pas mettre le volume Final ici.. sinon pb d'appels en boucle
	function setFeuilleCalcul(){
		console.log('setFeuilleCalcul...');
	
		// attention l'appel depuis $watch...sequencingType.value  ne marche pas quand l'utilisateur n'a pas encore fait le design de la flowcell
		if ( $scope.experiment.experimentProperties && $scope.experiment.experimentProperties.sequencingType.value ==='NovaSeq 6000 / S2' ) {
			console.log('S2...engag=150; NaoH=37/0.2N; TrisHCL=38/400; EPX=525');
			
			updateAllInputContainerUsedsPropertyValue("inputVolume2","150");
			updateAllInputContainerUsedsPropertyValue("NaOHConcentration","0.2N");	
			updateAllInputContainerUsedsPropertyValue("NaOHVolume","37");
			updateAllInputContainerUsedsPropertyValue("trisHCLConcentration","400000000");	
			updateAllInputContainerUsedsPropertyValue("trisHCLVolume","38");
			updateAllInputContainerUsedsPropertyValue("masterEPXVolume","525");	
				
		} else if ( $scope.experiment.experimentProperties &&  $scope.experiment.experimentProperties.sequencingType.value ==='NovaSeq 6000 / S4' ) {
			console.log('S4...engag=310; NaoH=77/0.2N; TrisHCL=78/400; EPX=1085....volume final=1550');
			
			updateAllInputContainerUsedsPropertyValue("inputVolume2","310");
			updateAllInputContainerUsedsPropertyValue("NaOHConcentration","0.2N");
			updateAllInputContainerUsedsPropertyValue("NaOHVolume","77");
			updateAllInputContainerUsedsPropertyValue("trisHCLConcentration","400000000");	
			updateAllInputContainerUsedsPropertyValue("trisHCLVolume","78");
			updateAllInputContainerUsedsPropertyValue("masterEPXVolume","1085");	
			
		} else {
			// Hiseq-4000 ou Hiseq-X: remettre les valeurs par defaut..
			console.log('default...engag=5; NaoH=5/0.1N; TrisHCL=5/200; EPX=35....volume final=50');
			
			updateAllInputContainerUsedsPropertyValue("inputVolume2","5");
			updateAllInputContainerUsedsPropertyValue("NaOHConcentration","0.1N");
			updateAllInputContainerUsedsPropertyValue("NaOHVolume","5");
			updateAllInputContainerUsedsPropertyValue("trisHCLConcentration","200000000");	
			updateAllInputContainerUsedsPropertyValue("trisHCLVolume","5");
			updateAllInputContainerUsedsPropertyValue("masterEPXVolume","35");	
		}
		
		// ne faire l'update du datatable qu'apres les 6 appels a  updateAllInputContainerUsedsPropertyValue !!!
		console.log ("mise a jour du datatable(2)");
		$scope.atmService.data.updateDatatable();
	}
	
	
	// copié d'après  $scope.updateAllOutputContainerProperty   dans tubes-to-flowcell-ctrl.js
	// function locale, ne pas la mettre dans $scope
	updateAllInputContainerUsedsPropertyValue = function(propertyCode, value){
		
		for(var i = 0 ; i < $scope.atmService.data.atm.length ; i++){
			var atm = $scope.atmService.data.atm[i];
			
			// boucler sur TOUS les inputContainerUsed
			for ( var j=0; j < $scope.atmService.data.atm[i].inputContainerUseds.length; j++ ){
				$parse("inputContainerUseds["+j+"].experimentProperties."+propertyCode+".value").assign(atm, value);
				
			    // si la colonne mise a jour est le volume engagé il faut recalculer la concentration finale
				if (propertyCode ==='inputVolume2'){
					computeConcentrationAtm(atm, i, j);	
				}
			}
		}
		//$scope.atmService.data.updateDatatable();   // NON faire l'updateDatatable dans la fonction appelante sinon update execute 6 fois de suite !!!
	};
	
	// 08/02/2018 Demande de faire aussi un final volume dependant du type de sequencage choisi...
	// impossible d'ajouter la mise a jour du volume final dans setFeuilleCalcul car appels en boucle....
	function setVolumeFinal(){
		console.log('setVolumeFinal...');
		if ( $scope.experiment.experimentProperties && $scope.experiment.experimentProperties.sequencingType.value ==='NovaSeq 6000 / S2' ) {
			//console.log('S2...volume final=750');
			updateAllOutputContainerUsedsPropertyValue("finalVolume","750");	
			
		} else if ( $scope.experiment.experimentProperties &&  $scope.experiment.experimentProperties.sequencingType.value ==='NovaSeq 6000 / S4' ) {
			//console.log('S4...volume final=1550');
			updateAllOutputContainerUsedsPropertyValue("finalVolume","1550");
			
		} else {
			// Hiseq-4000 ou Hiseq-X: remettre les valeurs par defaut..
			//console.log('default...volume final=50');
			updateAllOutputContainerUsedsPropertyValue("finalVolume","50");	
		}
	}
	
	// 08/02/2018
	updateAllOutputContainerUsedsPropertyValue = function(propertyCode, value){
		
		for(var i = 0 ; i < $scope.atmService.data.atm.length ; i++){
			var atm = $scope.atmService.data.atm[i];
			
			// boucler sur TOUS les outputContainerUsed
			for ( var j=0; j < $scope.atmService.data.atm[i].outputContainerUseds.length; j++ ){
				$parse("outputContainerUseds["+j+"].experimentProperties."+propertyCode+".value").assign(atm, value);
				//console.log ("mise a jour du volume final="+ value);
				
			    // si la colonne mise a jour est le volume final  il faut recalculer la concentration finale
				if (propertyCode ==='finalVolume'){
					computeConcentrationAtm(atm, i, j);	
				}
			}
		}
	}
	
	// surcharger celle de tubes-to-flowcell-ctrl.js  pour qu'elle appelle la changeValueOnFlowcellDesign locale qui utilise un parametre	
	$scope.updateAllOutputContainerProperty = function(property){
		var value = $scope.outputContainerValues[property.code];
		var setter = $parse("outputContainerUseds[0].experimentProperties."+property.code+".value").assign;
		for(var i = 0 ; i < $scope.atmService.data.atm.length ; i++){
			var atm = $scope.atmService.data.atm[i];
			if(atm.inputContainerUseds.length > 0){
				setter(atm, value);
				// dans la boucle et non hors comme dans l'originale
				$scope.changeValueOnFlowcellDesign(i+1);
			}			
		}
	};
	
	// surcharger celle de tubes-to-flowcell-ctrl.js pour declencher les calculs
	// utilisateur modifie une cellule "volume final" ( changeValueOnFlowcellDesign est appelle depuis le scala.html )
	$scope.changeValueOnFlowcellDesign = function(l){ //l=atm.line si appel depuis scala.html
	
		//console.log('% depot ou  % phix  ou Volume final modifié:  atm.line: '+ l );
		
		//!! il faut  appeler setFeuilleCalcul() ici car le premier appel sur le $watch..sequencingType.value ne marche pas quand 
		// le design de la flowcell n'est pas encore fait
		// oui mais si si ce n'est pas le premier appel alors il peut ecraser des valeur que l'utilisateur a defini en editant le datatable !!
		setFeuilleCalcul(); 
		
		// recalculer la concentration finale pour tous les inputContainerUsed de l'atm [l-1] 
		// calcul efectuee meme si la la propriete changee est % depot ou  % phix qui n'interviennent pas dans calcul!!!
		for ( var j=0; j < $scope.atmService.data.atm[l-1].inputContainerUseds.length; j++ ){
				computeConcentrationAtm($scope.atmService.data.atm[l-1], l-1, j);	
		}
		
		console.log("update datatable (4)");
		$scope.atmService.data.updateDatatable(); 
	};
	

	// utilisateur modifie une cellule "volume engagé"
	$scope.updatePropertyFromUDT = function(value, col){
		if ( col.property === 'inputContainerUsed.experimentProperties.inputVolume2.value'){ 
			console.log('propriete volume engagé modifiée....');
			// value.data ne contient qu'une seule ligne
			// recalculer la concentration finale 
		    computeConcentration(value.data);
		}
	}
	
	var computeConcentration = function(udtData){	
		console.log("computeConcentration (udtData)....");
		var getterFinalConcentration2=$parse("inputContainerUsed.experimentProperties.finalConcentration2.value");
		
		var compute = {
				inputConc : $parse("inputContainer.concentration.value")(udtData), 
				engagedVol: $parse("inputContainerUsed.experimentProperties.inputVolume2.value")(udtData), 
				finalVol:   $parse("outputContainerUsed.experimentProperties.finalVolume.value")(udtData),

				isReady:function(){
					// !! final volume doit imperativement etre != 0 sinon div by 0
					//console.log('inputConc='+ this.inputConc +'  engagedVol='+ this.engagedVol +'  finalVol='+ this.finalVol);
					return (this.finalVol && this.engagedVol);
				}
		};
		
		if(compute.isReady()){
			var finalConcentration= compute.inputConc * compute.engagedVol / compute.finalVol;
			console.log("conc finale avant arrondi = "+finalConcentration);
			// arrondir...
			if(angular.isNumber(finalConcentration) && !isNaN(finalConcentration)){
				// pas suffisant pour les conc en PicoMolaire: finalConcentration = Math.round(finalConcentration*100.0)/100.0;	
				finalConcentration = Math.round(finalConcentration*10000.0)/10000.0;
			}
			//console.log("conc finale apres arrondi= "+finalConcentration);
			getterFinalConcentration2.assign(udtData, finalConcentration);
			
		}else{
			console.log("Impossible de calculer la concentration finale: valeurs manquantes");
			getterFinalConcentration2.assign(udtData, undefined);
		}
	}
	
	// version pour les cas on modifie seulement 1 atm, 1 inputContainerUsed
	var computeConcentrationAtm = function(atm, i, j){
		//console.log("computeConcentration atm.."+ i +".inputContainerUsed:"+j);
		var getterFinalConcentration2=$parse("inputContainerUseds["+ j +"].experimentProperties.finalConcentration2.value");
			
		var compute = {
				inputConc : $parse("inputContainerUseds["+ j +"].concentration.value")(atm),
				engagedVol: $parse("inputContainerUseds["+ j +"].experimentProperties.inputVolume2.value")(atm), 
				finalVol:   $parse("outputContainerUseds[0].experimentProperties.finalVolume.value")(atm),	

				isReady:function(){
					// !! final volume doit imperativement etre != 0 sinon div by 0
					//console.log('inputConc='+ this.inputConc +'  engagedVol='+ this.engagedVol +'  finalVol='+ this.finalVol);
					return (this.finalVol && this.engagedVol );
				}
		};
		
		if(compute.isReady()){
			var finalConcentration = compute.inputConc * compute.engagedVol / compute.finalVol;
			//console.log("conc finale avant arrondi = "+finalConcentration);
			// arrondir...
			if(angular.isNumber(finalConcentration) && !isNaN(finalConcentration)){
				// pas suffisant pour les conc en PicoMolaire: finalConcentration = Math.round(finalConcentration*100.0)/100.0;	
				finalConcentration = Math.round(finalConcentration*10000.0)/10000.0;	
			}
			
			//console.log("conc finale apres arrondi= "+finalConcentration);
			getterFinalConcentration2.assign(atm, finalConcentration);
		} else {
			console.log("Impossible de calculer la concentration finale: valeurs manquantes");
			getterFinalConcentration2.assign(atm, undefined);
		}
	}
}]);
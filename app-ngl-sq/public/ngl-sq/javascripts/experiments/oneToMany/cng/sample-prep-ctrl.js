/* 11/08/2017 GA/FDS experience One to Many */
/* 16/10/2017 finalement il faut qd meme un datatable */

angular.module('home').controller('SamplePrepCtrl',['$scope', '$parse', '$filter','commonAtomicTransfertMethod','mainService','datatable',
                                                               function($scope, $parse, $filter, commonAtomicTransfertMethod, mainService, datatable ) {
	
	
   var nbOutputSupport=1;  // essai d'initialisation

   // créer un tableau sur lequel pourra boucler ng-repeat
   // ce tableau est modifié sur onChange de "nbOutputSupport"
   $scope.initOutputContainerSupportCodes = function(nbOutputSupport){
	   if(nbOutputSupport){
		    $scope.nbOutputSupport = nbOutputSupport; // GA: a cause d'un probleme de rafraichissement...
		    
			if(!$scope.isCreationMode()){
				 
			    //récupérer les codes des outputContainers et reinjecter si possible ce qu'il y avait avant
				previousOutputContainerSupportCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);
				previousStorageCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.storageCode'|unique",$scope.experiment);
				if(previousOutputContainerSupportCodes.length >= nbOutputSupport){
					//tronquer le tableau
					$scope.outputContainerSupportCodes = previousOutputContainerSupportCodes.splice(0, nbOutputSupport);
					// idem pour storageCodes
					$scope.storageCodes = previousStorageCodes.splice(0, nbOutputSupport);
					
				}else if(previousOutputContainerSupportCodes.length < nbOutputSupport){
					// completer le tableau  //idem pour storageCodes
					$scope.outputContainerSupportCodes=previousOutputContainerSupportCodes;
					$scope.storageCodes=previousStorageCodes;
					for (var j=previousOutputContainerSupportCodes.length ; j<  nbOutputSupport; j++){
						$scope.outputContainerSupportCodes.push(null);
						$scope.storageCodes.push(null);
					}
				}
			} else {
				$scope.outputContainerSupportCodes= new Array(nbOutputSupport*1);// *1 pour forcer en numerique nbOutputSupport qui est est un input type text
			}
		}	
	}

   if(!$scope.isCreationMode()){
	   getExperimentData();
    }else{
	   // trouver LE/LES codes des supports de tous les containers en entree de l'experience (il peut y en avoir plusieurs..)
	   $scope.inputSupportCodes = $scope.$eval("getBasket().get()|getArray:'support.code'|unique", mainService); 
	   
	   $scope.initOutputContainerSupportCodes (1);// test ajout pour initaliser mais chge rien 
	   
	   if ($scope.inputSupportCodes.length > 1){
		   $scope.messages.clear();
		   $scope.messages.clazz = "alert alert-danger";
		   $scope.messages.text = Messages("experiments.input.error.only-1-plate");
		   $scope.messages.showDetails = false;
		   $scope.messages.open();
	   } else {		  
		   $scope.inputSupportCode=$scope.inputSupportCodes[0];
		   $scope.outputContainerSupportCodes=['']; // essai ajout ''
		   $scope.storageCodes=[''];// essai ajout ''
	   }
	}	
   
   /* ancienne methode avant ajout datatable  A PURGER SI TOUT OK.....
    
   // il faut la callbackFunction pour le $emit 
   function generateATM(callbackFunction){
	    console.log ('ouput supports='+ $scope.outputContainerSupportCodes);
	    console.log ('ouput storages='+ $scope.storageCodes);
	    $scope.messages.clear();
	    
	    if($scope.isCreationMode()){
	    	console.log ('creation mode...');
	    	
	    	//1 initialiser
	    	$scope.experiment.atomicTransfertMethods = [];
	    	
	    	//2 récupérer les inputContainers depuis le basket
	    	//Each promise object will have a "then" function that can take two arguments, a "success" handler and an "error" handler.
	    	$commonATM.loadInputContainerFromBasket(mainService.getBasket().get())
			  .then(function(containers) {	
				 containers.forEach(function(inputContainer){
					 
					//2.1 création de l'ATM
					var atm = newAtomicTransfertMethod(inputContainer.support.line, inputContainer.support.column);
					
					//2.2 création d'1 inputContainerUsed
					var inputContainerUsed=$commonATM.convertContainerToInputContainerUsed(inputContainer);
					atm.inputContainerUseds.push(inputContainerUsed);
					//console.log('inputContainerUsed='+atm.inputContainerUseds[0].code);
							
					//2.3 création de j outputContainerUsed
					for(var j = 0; j < $scope.outputContainerSupportCodes.length ; j++){
						// si l'utilisateur a bien entré des supportCodes
						if ($scope.outputContainerSupportCodes[j] !== undefined && $scope.outputContainerSupportCodes[j] !== null && $scope.outputContainerSupportCodes[j] !== ''){
							var outputContainerUsed = $commonATM.newOutputContainerUsed(defaultOutputUnit, defaultOutputValue, atm.line, atm.column, inputContainer);
							//affectation du SupportCode
							outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
							//affectation du storageCode si defini
							if ( $scope.storageCodes[j] !== undefined && $scope.storageCodes[j] !== null){			  
								  outputContainerUsed.locationOnContainerSupport.storageCode=  $scope.storageCodes[j];
							}
							atm.outputContainerUseds.push(outputContainerUsed);
						}
					}
					
					//2.4 mettre l'atm dans l'expérience
					$scope.experiment.atomicTransfertMethods.push(atm);
					console.log('atm pushed into experiment');
				});
				 
				// !!! promise=asynchronisme. mettre le $emit('childSaved') ici et plus dans $scope.$on('save',
				// tester avec le premier ATM
				if ( $scope.experiment.atomicTransfertMethods[0].outputContainerUseds.length === 0){
					$scope.$emit('childSavedError', callbackFunction);

				    $scope.messages.clazz = "alert alert-danger";
				    $scope.messages.text = Messages('experiments.output.error.minSupports',1);
				    $scope.messages.showDetails = false;
					$scope.messages.open();   
					
				} else {	
					$scope.$emit('childSaved', callbackFunction);
			    } 
			    
			});	    
	   } else {
	    	console.log ('modification mode...');
	    	// l'utilisateur peut modifier - le nombre de supports en output et/ou les codes barres en output
	    	
	    	// TODO  chg algo: se contenter de supprimer les outputContainerUseds au lieu de tout refaire !!
	    	
	    	//0 copier les anciens ATMs (on n'a plus le basket...)
	    	var previousATMs=$scope.experiment.atomicTransfertMethods;
	    	
	    	//1 supprimer les ATM de l'expérience (on va les recréer)
	    	$scope.experiment.atomicTransfertMethods = [];
	    	
	    	//2 boucler sur les previousATMs
	    	previousATMs.forEach(function(prevatm){
	    		
			    	//2.1 création de l'ATM 
					var atm = newAtomicTransfertMethod(prevatm.line, prevatm.column);
			    
					//2.2 création d'1 inputContainerUsed (récupérer l'ancien)
					atm.inputContainerUseds.push (prevatm.inputContainerUseds[0]);
					
					//2.3 création de j outputContainerUsed
					for(var j = 0; j < $scope.outputContainerSupportCodes.length ; j++){
						// si l'utilisateur a bien entré des supportCodes
						if ($scope.outputContainerSupportCodes[j] !== undefined && $scope.outputContainerSupportCodes[j] !== null && $scope.outputContainerSupportCodes[j] !== ''){
							var outputContainerUsed = $commonATM.newOutputContainerUsed(defaultOutputUnit, defaultOutputValue, atm.line, atm.column, prevatm.inputContainerUseds[0]);
							//affectation du SupportCode 
							outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
							//affectation du storageCode si defini
							if ( $scope.storageCodes[j] !== undefined && $scope.storageCodes[j] !== null){
							  outputContainerUsed.locationOnContainerSupport.storageCode= $scope.storageCodes[j];
							}
							atm.outputContainerUseds.push(outputContainerUsed);
						}
					}
			
					
					
					//2.4 mettre l'atm dans l'expérience
					$scope.experiment.atomicTransfertMethods.push(atm);
	    	});
	    	
	    	// pas de promise dans ce cas mais obligé de faire le $emit('childSaved de facon similaire...
	    	// tester avec le premier ATM
			if ( $scope.experiment.atomicTransfertMethods[0].outputContainerUseds.length === 0){
				$scope.$emit('childSavedError', callbackFunction);
				
			    $scope.messages.clazz = "alert alert-danger";
			    $scope.messages.text = Messages('experiments.output.error.minSupports',1);
			    $scope.messages.showDetails = false;
				$scope.messages.open(); 
			} else {
				$scope.$emit('childSaved', callbackFunction);
			}
			
	    }
	}
	*/
	
	function getExperimentData(){	
		//1 récupérer LE locationOnContainerSupport.code des containers (il ne peux y en avoir qu'un seul)
	    $scope.inputSupportCode = $scope.$eval("atomicTransfertMethods|flatArray:'inputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment)[0];
	     
		//2 récupérer le nbre de nbOutputSupport en se basant sur atomic[0]: tjrs vrai ??? 
        //  => oui si on bloque les cas de sauvegarde sans  nbOutputSupport

	    $scope.nbOutputSupport=$scope.experiment.atomicTransfertMethods[0].outputContainerUseds.length;
			  
	    //3 récupérer les codes des outputContainers  
	    $scope.outputContainerSupportCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.code'|unique",$scope.experiment);
		
	    //4 récupérer les storageCodes
	    $scope.storageCodes=$scope.$eval("atomicTransfertMethods|flatArray:'outputContainerUseds'|getArray:'locationOnContainerSupport.storageCode'|unique",$scope.experiment);
		
	    //?? qu'est-ce qui prouve que les 2 tableaux locationOnContainerSupport.code  et locationOnContainerSupport.storageCode  sont récupéres dans le meme ordre ?????
	}
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save");

		//  ancienne methode avant ajout Datatable.... generateATM(callbackFunction);
		$scope.atmService.viewToExperimentOneToMany($scope.experiment);
		if ( $scope.experiment.atomicTransfertMethods[0].outputContainerUseds.length === 0){
			$scope.$emit('childSavedError', callbackFunction);
			
		    $scope.messages.clazz = "alert alert-danger";
		    $scope.messages.text = Messages('experiments.output.error.minSupports',1);
		    $scope.messages.showDetails = false;
			$scope.messages.open(); 
		} else {
			$scope.$emit('childSaved', callbackFunction);
		}
		
	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('cancel', function(e) {
		console.log("call event cancel");		
		getExperimentData();

	});
	
	$scope.$on('activeEditMode', function(e) {
		console.log("call event activeEditMode");
		// rien  ????
	});
	
	var inputExtraHeaders=Messages("experiments.inputs");
	var outputExtraHeaders=Messages("experiments.outputs");	
	
	var inputContainerDatatableConfig = {
			columns:[   
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.support.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         { // Ligne
			        	 "header":Messages("containers.table.support.line"),
			        	 "property":"inputContainer.support.line",
			        	 "order":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         { // colonne
			        	 "header":Messages("containers.table.support.column"),
				         // astuce GA: pour pouvoir trier les colonnes dans l'ordre naturel forcer a numerique.=> type:number,   property:  *1
			        	 "property":"inputContainer.support.column*1",
			        	 "order":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":3,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         { // Projet(s)
				        	"header":Messages("containers.table.projectCodes"),
				 			"property": "inputContainer.projectCodes",
				 			"order":true,
				 			"hide":true,
				 			"type":"text",
				 			"position":4,
				 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				        	 "extraHeaders":{0: inputExtraHeaders}
					     },
					     { // Echantillon(s) 
				        	"header":Messages("containers.table.sampleCodes"),
				 			"property": "inputContainer.sampleCodes",
				 			"order":true,
				 			"hide":true,
				 			"type":"text",
				 			"position":5,
				 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
				        	"extraHeaders":{0: inputExtraHeaders}
					     },
					     /*
					     { // sampleAliquoteCode 
					        "header":Messages("containers.table.codeAliquot"),
					 		"property": "inputContainer.contents", 
					 		"filter": "getArray:'properties.sampleAliquoteCode.value'",
					 		"order":true,
					 		"hide":true,
					 		"type":"text",
					 		"position":6,
					 		"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
					        "extraHeaders":{0: inputExtraHeaders}
						 },
						*/
					     { 
					       "header": Messages("containers.table.libProcessType"),
					       "property" : "inputContainerUsed.contents",
					       "filter" : "getArray:'properties.libProcessTypeCode.value' |unique | codes:'value'",
					       "order":true,
						   "edit":false,
						   "hide":true,
					       "type":"text",
					       "position":8.2,
					       "extraHeaders":{0:inputExtraHeaders}
					     },
				         { // Etat input Container 
				        	 "header":Messages("containers.table.state.code"),
				        	 "property":"inputContainer.state.code | codes:'state'",
				        	 "order":true,
							 "hide":true,
				        	 "type":"text",
				        	 "position":9,
				        	 "extraHeaders":{0: inputExtraHeaders}
				         }   		         
			],
			compact:true,
			// tout a false, on ne fait que de l'affichage
			showTotalNumberRecords:false,
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				active:false
			},
			remove:{
				active: false				
			},
			save:{
				active:false
			},			
			select:{
				active:false
			},
			edit:{
				active: false
			},	
			cancel : {
				active:false
			},
			extraHeaders:{
				number:1,
				dynamic:true,
			}
	};
	
	//init data
	//GA 16/10/2017 Cette experience est la seule qui fait du one to many avec plaque en entre/ plaques en sortie.
	//=>Il faut refaire un atmService "allégé" par rapport a celui dans atomicTransfereServices.js
	var atmService =  {
			data:datatable(inputContainerDatatableConfig), //UDT
			$commonATM : commonAtomicTransfertMethod($scope),
			defaultOutputUnit : {
					volume : "µL",
					concentration : "nM"
			},
			defaultOutputValue :{},
			newAtomicTransfertMethod : function(l,c){
				return {
					class:"OneToMany",
					line: l, 
					column: c, 				
					inputContainerUseds:new Array(0), 
					outputContainerUseds:new Array(0)
				};
			},
			convertExperimentATMToDatatable: function(experimentATMs, experimentStateCode){
				$that = this;
				var atms = experimentATMs;
				$that.$commonATM.loadInputContainerFromAtomicTransfertMethods(atms)
					.then(function(result) {								
						var allData = [];
						var inputContainers = result.input;
						var atomicIndex=0;
						for(var i=0; i< atms.length;i++){
							
							if(atms[i] === null){
								continue;
							}
							//var atm = angular.copy(atms[i]);
							var atm = $.extend(true,{}, atms[i]);
								
							var inputContainerCode = atm.inputContainerUseds[0].code;
							var inputContainer = inputContainers[inputContainerCode];
							
							var line = {atomicIndex:atomicIndex};
							line.atomicTransfertMethod = atm;							              
							line.inputContainer = inputContainer;	
							line.inputContainerUsed = $.extend(true,{}, atm.inputContainerUseds[0]);
							line.inputContainerUsed = $that.$commonATM.updateInputContainerUsedFromContainer(line.inputContainerUsed, inputContainer, experimentStateCode);							
							allData.push(line);
							atomicIndex++;
						}
						
						allData = $filter('orderBy')(allData, ['inputContainer.support.code','inputContainer.support.column*1', 'inputContainer.support.line']);							
						$that.data.setData(allData, allData.length);											
				});		
			},
			addNewAtomicTransfertMethodsInData:function(){
				if(null != mainService.getBasket() && null != mainService.getBasket().get()){
					$that = this;
					$that.$commonATM.loadInputContainerFromBasket(mainService.getBasket().get())
						.then(function(containers) {								
							var allData = [], i = 0;
							angular.forEach(containers, function(container){
								var line = {};
								line.atomicTransfertMethod =  $that.newAtomicTransfertMethod(container.support.line, container.support.column);
								line.inputContainer = container;
								line.inputContainerUsed = $that.$commonATM.convertContainerToInputContainerUsed(line.inputContainer);
								allData.push(line);
							});
							allData = $filter('orderBy')(allData, ['inputContainer.support.code','inputContainer.support.column*1', 'inputContainer.support.line']);							
							$that.data.setData(allData, allData.length);											
					});
				}
			},
			experimentToView:function(experiment){
				if(null === experiment || undefined === experiment){
					throw 'experiment is required';
				}
				if(!$scope.isCreationMode()){
					this.convertExperimentATMToDatatable(experiment.atomicTransfertMethods, experiment.state.code);	
				}else{
					this.addNewAtomicTransfertMethodsInData();
				}	
								
			},
			viewToExperimentOneToMany :function(experimentIn){		
				if(null === experimentIn || undefined === experimentIn){
					throw 'experiment is required';
				}
				experiment = experimentIn;
				var allData = this.data.getData();
				if(allData != undefined){
					experiment.atomicTransfertMethods = []; // to manage remove
					//first reinitialise atomicTransfertMethod
					for(var i=0;i<allData.length;i++){
						var atomicIndex = allData[i].atomicIndex;								
						experiment.atomicTransfertMethods[atomicIndex] = allData[i].atomicTransfertMethod
						experiment.atomicTransfertMethods[atomicIndex].inputContainerUseds = new Array(0);
						var atm = experiment.atomicTransfertMethods[atomicIndex];
						
						//oneTo
						var inputContainerUsed = allData[i].inputContainerUsed;
						this.$commonATM.removeNullProperties(inputContainerUsed.instrumentProperties);
						this.$commonATM.removeNullProperties(inputContainerUsed.experimentProperties);
						atm.inputContainerUseds.push(inputContainerUsed);	
						
						// Ne recreer les outputs que dans les etats Nouveau ou en cours  ( mais PAS si elle est terminee)
						if('F' !== experimentIn.state.code){
							experiment.atomicTransfertMethods[atomicIndex].outputContainerUseds = new Array(0);
							
							for(var j = 0; j < $scope.outputContainerSupportCodes.length ; j++){
								// si l'utilisateur a bien entré des supportCodes
								if ($scope.outputContainerSupportCodes[j] !== undefined && $scope.outputContainerSupportCodes[j] !== null && $scope.outputContainerSupportCodes[j] !== ''){
									var outputContainerUsed = this.$commonATM.newOutputContainerUsed(this.defaultOutputUnit, this.defaultOutputValue, atm.line, atm.column, inputContainerUsed);
									//affectation du SupportCode
									outputContainerUsed.locationOnContainerSupport.code=  $scope.outputContainerSupportCodes[j];
									//affectation du storageCode si defini
									if ( $scope.storageCodes[j] !== undefined && $scope.storageCodes[j] !== null){			  
										  outputContainerUsed.locationOnContainerSupport.storageCode=  $scope.storageCodes[j];
									}
									atm.outputContainerUseds.push(outputContainerUsed);
								}
							}
						}
						
					}
					
					//remove atomic null
					var cleanAtomicTransfertMethods = [];
					for(var i = 0; i < experiment.atomicTransfertMethods.length ; i++){
						if(experiment.atomicTransfertMethods[i] !== null){
							cleanAtomicTransfertMethods.push(experiment.atomicTransfertMethods[i]);
						}
					}
					experiment.atomicTransfertMethods = cleanAtomicTransfertMethods;
				}								
			},
	};
	
	atmService.experimentToView($scope.experiment);
	
	$scope.atmService = atmService;
	
	
	// TEST copié depuis details-ctrl.js
	// mais prevu pour 1 seule plaque out put A MODIFIER !!!
	var plateUtils = {
		plateCells : undefined,
		computePlateCells : function(atmService, i){
			console.log ("computePlateCells for plate "+ i );
			
			// un seul tableau ne suffit plus !!! 
			var plateCells = [];
			
			
			var wells = atmService.data.displayResult;
			// displayResult pas defini...?
			//var  wells = atmService.data.getData();
				
			angular.forEach(wells, function(well){
				var containerUsed = undefined;
				
				containerUsed = well.data.outputContainerUsed;

				var line = containerUsed.locationOnContainerSupport.line;
				var column = containerUsed.locationOnContainerSupport.column;
				if(line && column){
					if(plateCells[line] == undefined){
						plateCells[line] = [];
					}
					var sampleCodeAndTags = [];
					angular.forEach(containerUsed.contents, function(content){
						var value = content.projectCode+" / "+content.sampleCode;
						
						if(content.properties && content.properties.libProcessTypeCode){
							value = value +" / "+content.properties.libProcessTypeCode.value;
						}
						
						if(content.properties && content.properties.tag){
							value = value +" / "+content.properties.tag.value;
						}
						
						sampleCodeAndTags.push(value);
					});
					plateCells[line][column] = sampleCodeAndTags;
					
				}						
			})	
			this.plateCells = plateCells;
		},
		getCellPlateData : function(line, column){
			if(this.plateCells && this.plateCells[line] && this.plateCells[line][column]){
				return this.plateCells[line][column];
			}
		},
		getCellPlateDataTEST : function(output, plate, line, column){
			return "("+output+")"+ plate+"/"+line+"/"+column;
		}
	};

	$scope.plateUtils=plateUtils;
	
}]);
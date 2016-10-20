// FDS 21/06/2016 dupliqué depuis manyToOne.experiments.cns.x-to-tubes avec modifs
angular.module('home').controller('XToPlatesCtrl',['$scope', '$http','$parse', '$filter',
                                                               function($scope, $http, $parse, $filter) {

	//-----------------------------------------reutilisables ailleurs ??----------------------------------------------------------
	// FDS 29/06/2016 calcul de la ligne en fonction de position 96 numerotée en mode colonne ( 1=A1, 2=B1... 9=A2...) [ thx NW ]
	var getLineFromPosition96_C = function(pos96){	
		var line= String.fromCharCode (((pos96 -1) % 8 ) + 65 );
		//console.log(">line ="+ line +" ("+pos96+")");
		return line;
	};
	
	//  FDS 29/06/2016  calcul de la colonne en fonction de position-96 numerotée en mode colonne ( 1=A1, 2=B1... 9=A2...) [ thx NW ]
	var getColumnFromPosition96_C = function(pos96){	
		var c=Math.floor((pos96 -1) / 8) +1 ;
		var column= c.toString();
		//console.log(">col ="+column+" ("+pos96+")");
		return column;
	};
	
	
	var getLineFromPosition96_L = function(pos96){	
		//TODO ??
	}
	
	var getColFromPosition96_L = function(pos96){	
		//TODO ??
	}
	
	
	// FDS 03/10/2016 calculer la position-96 en mode colonne a partir de ligne et colonne ( 1=A1, 2=B1... 9=A2...) 
	var getPos96FromLineAndCol_C = function(ln,col){
		if ( ln && col ){
			// numeroter a partir de 1 et pas 0!!   var pos= (col  -1 )*8 + ( ln.charCodeAt(0) -65);
			var pos96=(col -1 )*8 + ( ln.charCodeAt(0) -64);
			//console.log(ln+"/"+col+"=>"+pos96);
			return pos96;
		}
	}
	
	//  FDS 03/10/2016 calculer la position-96 en mode ligne a partir de ligne et colonne ( 1=A1, 2=A2...13=B1...)
	var getPos96FromLineAndCol_L = function(ln,col){
       //TODO
	}
	//---------------------------------------------------------------------------------------------------------------------------
	
	// s'execute a la creation de chaque ATM meme sans mise a jour de concentration!!!!
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
			
			// FDS 29/06/2016 positionnement automatique de ligne et colonne sur une plaque
			console.log("instrument.outContainerSupportCategoryCode="+ $scope.experiment.instrument.outContainerSupportCategoryCode);
			if ( $scope.experiment.instrument.outContainerSupportCategoryCode === "96-well-plate" ){	
				atm.outputContainerUseds[0].locationOnContainerSupport.column=getColumnFromPosition96_C(atm.viewIndex);
				atm.outputContainerUseds[0].locationOnContainerSupport.line=getLineFromPosition96_C(atm.viewIndex);
			} else {
				atm.outputContainerUseds[0].locationOnContainerSupport.column=1;
				atm.outputContainerUseds[0].locationOnContainerSupport.line=1;
			}
				
			atm.column=atm.outputContainerUseds[0].locationOnContainerSupport.column;
			console.log ("setting locationOnContainerSupport.column=..."+atm.outputContainerUseds[0].locationOnContainerSupport.column);

			atm.line=atm.outputContainerUseds[0].locationOnContainerSupport.line;
			console.log ("setting locationOnContainerSupport.line=..."+ atm.outputContainerUseds[0].locationOnContainerSupport.line);	
			
			atm.outputContainerUseds[0].locationOnContainerSupport.code=$scope.outputContainerSupport.code;
			//pas d'affectation atm.xx= ???
			console.log("setting locationOnContainerSupport.code="+$scope.outputContainerSupport.code);	
			
			//faudrait pas de storage code sur des puits de plaque !!!
			atm.outputContainerUseds[0].locationOnContainerSupport.storageCode=$scope.outputContainerSupport.storageCode;
			//pasd'affectation atm.xx= ???
			console.log("setting locationOnContainerSupport.StorageCode: "+$scope.outputContainerSupport.storageCode);
			
			// 19/10/2016  affectation de WorkName. !! GA: la propriete n'existe pas=> la creer
			/*  methode basique...
			if(!atm.outputContainerUseds[0].experimentProperties){
				atm.outputContainerUseds[0].experimentProperties = {};
				atm.outputContainerUseds[0].experimentProperties.workName = {};
			}
			atm.outputContainerUseds[0].experimentProperties.workName.value=$scope.outputContainerSupport.rootWorkName+atm.viewIndex;
			*/
			/* methode angular*/
			$parse('experimentProperties.workName.value').assign(atm.outputContainerUseds[0],$scope.outputContainerSupport.rootWorkName+atm.viewIndex);
		}
	};
	
	//TEST 19/10/2016 modification des ATM deja crees s'il y en a.......... MARCHE PAS.. avoir ??
	/*$scope.updateSupportCodes= function(supportCode,atm){
		console.log ("supportCode changed: "+supportCode);
	    if (atm){
		//PAS OK...PASSE PAS ICI !!
	    	console.log ("updating ATMs supportCode with :"+supportCode);
		    atm.outputContainerUseds.forEach(function(ocu){	
		    	ocu.locationOnContainerSupport.code=supportCode;
	      });
	    }
	};
	$scope.updateStorageCodes= function(storageCode, atm){
		console.log ("storageCode changed:"+ storageCode);
		if (atm){
			//PAS OK...PASSE PAS ICI !!
			console.log ("updating ATMs storageCode with :"+ storageCode);
			 $scope.atm.outputContainerUseds.forEach(function(ocu){	
				 ocu.locationOnContainerSupport.storageCode=storageCode;
		   });
		}
	};
	$scope.updateWorkNames= function(rootWorkName,atm){
		console.log ("rootworkName changed:"+ rootWorkName);
		if (atm){
			//PAS OK...PASSE PAS ICI !!
			console.log ("updating ATMs workNames with :"+ rootWorkName);
			 atm.outputContainerUseds.forEach(function(ocu){	
				 ocu.experimentPropertie.workName=rootWorkName+"_"+atm.viewIndex;
		   });
		}
	};
	*/
	
	// !! controller commun a normalization-and-pooling et pooling
	// Julie demande de bloquer le calcul en normalization-and-pooling si unité de concentration n'EST PAS nM 
	// TODO...
		
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
			inputVolume = {value : undefined, unit : 'µL'};
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
			var bufferVolume = {value : undefined, unit : 'µL'};
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

	// 20/10/2016 OK!!!
	$scope.getInstrumentCategoryCode= function() { return $scope.experiment.instrument.categoryCode; }
	
	// FDS pas de boutons generateSampleSheet pour la main
	//console.log ("container="+ $scope.atmService.inputContainerSupportCategoryCode );
	//console.log ("instrument="+ $scope.experiment.instrument.categoryCode + " / experiment="+$scope.experiment.typeCode);	
	if ( $scope.experiment.instrument.categoryCode !== "hand") {	
			// FDS 2 boutons pour genener 2 generateSampleSheet...
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

	
	// 19/10/2016 Only tube is authorized for hand marche PRESQUE...
	$scope.$watch("$scope.experiment.instrument.categoryCode", function(){
			if ($scope.experiment.instrument.categoryCode === "hand")
				$scope.experiment.instrument.outContainerSupportCategoryCode = "tube";
	});	
	
	
	// pour selects de position de sortie dans le cas des plaques !!!!
	//// TODO mettre ces tableaux dans le scala.html plutot qu'ici ???
	$scope.columns = ["1","2","3","4","5","6","7","8","9","10","11","12"]; 
	$scope.lines=["A","B","C","D","E","F","G","H"];  
	
    //rootWorkName= valeur par defaut pour generation automatique du label de travail
    $scope.outputContainerSupport = { code : null , storageCode : null, rootWorkName:"pool"};
    
    // 01/10/2016--------------------------- pooling "automatique"--------------------------------------------------------------------
    // HARDCODER les parametres des modes predefinis ( numtype=typ de numerotation  L:en ligne C: en colonne )
    if ( $scope.experiment.instrument.typeCode === "janus") {	
    	$scope.poolingModes=[ 
                           //{code: 'L4',  name:'Ligne 4-p',    poolPlex: 4, startLine:'A', startColumn: 1, endLine:'H', endColumn:12, numtype:'L'}, 
                           //{code: 'L6',  name:'Ligne 6-p',    poolPlex: 6, startLine:'A', startColumn: 1, endLine:'H', endColumn:12, numtype:'L'},
                           //{code: 'IS4', name:'Ill Sing 4-p', poolPlex: 4, startLine:'A', startColumn: 1, endLine:'H', endColumn:12, numtype:'L'},
                           //{code: 'IS6', name:'Ill Sing 6-p', poolPlex: 6, startLine:'A', startColumn: 1, endLine:'H', endColumn:12, numtype:'L'},
                             {code: 'ID4', name:'Ill Dual 4-p', poolPlex: 4, startLine:'A', startColumn: 1, endLine:'H', endColumn:12, numtype:'C'},
                             {code: 'ID6', name:'Ill Dual 6-p', poolPlex: 6, startLine:'A', startColumn: 1, endLine:'H', endColumn:12, numtype:'C'}
                            ];
    } else if ( $scope.experiment.instrument.typeCode === "epimotion") {	
    	$scope.poolingModes=[ 
    	                     {code: 'C4',  name:'Col 4-p',  poolPlex: 4, startLine:'A', startColumn: 1, endLine:'H', endColumn:12, numtype:'C'},
    	                     {code: 'C6',  name:'Col 6-p',  poolPlex: 6, startLine:'A', startColumn: 1, endLine:'H', endColumn:12, numtype:'C'}
    	                    ];
    } 

    $scope.poolingMode=null;
    $scope.autoPooling =function (poolingMode){
    	// verifier qu'on a une seule plaque
    	///ca marche mais il faudrait le detecter plus tot !!
    	if ($scope.atmService.data.inputContainerSupports.length > 1) {
    		$scope.messages.clear();
    		$scope.messages.clazz = "alert alert-danger";
    		$scope.messages.text = Messages("experiments.input.error.only-1-plate");
    		$scope.messages.showDetails = false;
    		$scope.messages.open();	
    	} else {
    		
        	console.log( "poolingMode ="+poolingMode.name);
        	console.log( "plaque en cours="+ $scope.atmService.data.getCurrentSupportCode() );
    	
    	
        	if ( poolingMode.numtype === "L") {
        		var startPos=getPos96FromLineAndCol_L( poolingMode.startLine, poolingMode.startColumn );
        		var endPos=getPos96FromLineAndCol_L( poolingMode.endLine, poolingMode.endColumn);		
        	} else if (poolingMode.numtype === "C")  {
        		var startPos=getPos96FromLineAndCol_C( poolingMode.startLine, poolingMode.startColumn );
        		var endPos=getPos96FromLineAndCol_C( poolingMode.endLine, poolingMode.endColumn);
        	} else { 
        		throw "OOps incorrect numtype :"+ poolingMode.numtype ;
        	}
        	//console.log ("start position ="+ startPos + "/ end position ="+ endPos);

		
        	if (( poolingMode.code === "L4")||( poolingMode.code === "L6")||( poolingMode.code === "C4")||( poolingMode.code === "C6")){ 
        		poolContigu( poolingMode.poolPlex, startPos, endPos, poolingMode.numtype ); 
        	}
        	else if ((poolingMode.code === "ID4")||(poolingMode.code === "ID6")){ 
        		poolIlluminaDual( poolingMode.poolPlex, startPos, endPos );
        	}
        	else if ((poolingMode.code === "IS4")||(poolingMode.code === "IS6")){ 
        		poolIlluminaSingle( poolingMode.poolPlex, startPos, endPos );
        	}else{
        		throw "OOps pooling mode not supported: "+ poolingMode.name;
        	}
        	console.log ("--END OF POOLING--");
    	}
    }
    

    var poolIlluminaDual = function( poolPlex, startPos, endPos ){
    	// le pooling illumina Dual index est un pooling par blocs contigus...( voir doc Illumina: TruSeq Library prepPooling)
    	//    3-plex =bloc 2x2 avec 1 trou; 4-plex=bloc 2X2;
    	//    5-plex= bloc 2x3 avec 1 trou; 6-plex=bloc 2x3; 
    	//    7-plex= bloc 2x4 avec 1 trou; 8-plex-option1 =bloc 2x4; 8-plex-option2=bloc 4x2
    	//    12-plex= bloc 4x3; 16-plex= bloc xx4
    	console.log ("POOLING MODE ILLUMINA DUAL INDEX");
    	///console.log (">> startPos="+ startPos +" endPos="+ endPos );
    	
    	var block=null;
    	// members contient la position relative des autres puits du bloc par rapport au puit "0" du bloc
    	// ATTENTION numerotation en mode colonne des puits!!!!
    	/// cas non geree...if      (poolPlex === 2) { block={ line: 1, col:2, members:['X']}; }  // ESSAI!!!!!!!! marche pas
    	if      (poolPlex === 3) { block={ line: 2, col:2, members:[1,8]}; }  // comme poolPlex 4 mais sans le puit +9
    	else if (poolPlex === 4) { block={ line: 2, col:2, members:[1,8,9]}; }
    	else if (poolPlex === 5) { block={ line: 2, col:3, members:[1,8,9,16]}; } // comme poolPlex 6 mais sans le puit +17
    	else if (poolPlex === 6) { block={ line: 2, col:3, members:[1,8,9,16,17]}; }
    	else if (poolPlex === 7) { block={ line: 2, col:4, members:[1,8,9,16,17,24]}; } // comme poolPlex 8 mais sans le puit +25
    	else if (poolPlex === 8) { block={ line: 2, col:4, members:[1,8,9,16,17,24,25]}; } // le mode 2 (=4x2) pas pris encharge..]
    	else if (poolPlex === 12){ block={ line: 4, col:3, members:[1,2,3,8,9,10,11,16,17,18,19,]}; } // le mode 3x4 n'est pas pertinent pour une plaque 8x12
    	else if (poolPlex === 16){ block={ line: 4, col:4, members:[1,2,3,8,9,10,11,16,17,18,19,24,25,26,27]}; }
    	else {
    		throw "plex "+ poolPlex +" not managed in Illumina Dual Index mode";				
    	}

    	if ( block ){
    		//$scope.messages.clear();
    		//console.log (">> plex="+ poolPlex + "("+ block.line+"/"+block.col+")" );
    		
    	    var blockSize=block.line*block.col;
    	    var lastBlockMember=block.members.slice(-1)[0];
    	    
    	    // verifier que les positions choisies correspondent a un nombre de blocs complet
    	    //  PB PAS SI SIMPLE !!!!    nbWellsSelected=endPos-StartPos +1;
    	    //  par exemple si debut =0 et fin =12 en mode block de 4 on a 8 puits et pas 12 (11-0 +1)!!! 
    	    
    	    // attention !!! en fonction de la position de depart  le block peut se retrouver a cheval le bord de plaque
    	    // ==> comment controler ca ?????????

    	    // ALGORITHME pour determiner le puit "0" (en haut a gauche) de chaque bloc en fonction de ses dimensions
    	    var pi=0 //pool index
    	    var loop=true;
    	    while ( loop  ) {
    	    	//[thx NW   encore !!]
    	    	//!! cet algo marche pour numerotation a partir de 0 donc:  -1 a startPos, +1 en sortie pour (pi) Pool et pour (wi) puit
    	    	var blockparcol = 8/block.line; // line toujours diviseur de 8 pour l'instant ( 2 ou 4!! voir liste des poolPlex autorisés )
    	        var j= blockparcol * Math.floor(pi / blockparcol) * blockSize;
    	        var k= (pi % blockparcol ) * block.line;
    	        var wi= j + k +(startPos -1);// prendre en compte la position de debut !!
    	        
    	        var lastWell=lastBlockMember + (wi+1);
    	        //console.log ("pool "+ (pi+1) +" commence au puit "+(wi+1)+" et fini au puit "+lastWell);
    	        createPoolBlock( pi+1, wi+1, block.members );
    	        
    	        // il faut sortir quand le plus grand indice de puit des membres du block courant est plus grand que la position de fin demandee
    	        // probleme pour les blocs a trou...compter les trous ( toujours testé avec 1 seul trou !!)
    	        //   !!! marche pas pour le pool 1x2 !!!
    	        var blockHoles=blockSize - block.members.length -1;
    	        
        	    if (lastWell  >=  ( endPos - blockHoles )) { 
    	        	//console.log ("exit loop!!!"); 
    	        	loop=false
    	        } 
    	    	pi++;
   			}
    	}
    }// end poolIlluminaDual
    
    var poolIlluminaSingle = function( poolPlex,  startPos, endPos, numtype ){
    	console.log ("POOLINGMODE ILLUMINA SINGLE INDEX...TODO");
    } 
    
    var poolContigu = function( poolPlex,  startPos, endPos, numtype ){
    	console.log ("POOLING MODE CONTIGU ("+numtype+")"  );
    	
		var nbWells=endPos - startPos +1;
		var nbPools= Math.floor(nbWells / poolPlex);
		console.log ("nbPools="+ nbPools);
		
		var remain= nbWells % poolPlex;
		//le dernier pool est donc un pool reduit
		console.log ("reste="+ remain);
		
		// ici les indices pi et wi commencent a 1 et non 0 
		var pi=1;
		var pool=[];
		for ( var wi= startPos; wi < (endPos+1); wi++){
			 console.log (">>ajouter puit "+ wi +" dans pool "+ pi);
			 pool.push(wi);
			 // si wi multiple de poolPlex alors on change de pool  ATTENTION prise en compte de la position de debut 	 
			 if  ((wi - startPos +1 )%poolPlex == 0 ) {
				 createPoolContigu( pi, pool, numtype);
				 var pool=[];
				 pi++;
			 }
			 // le pool reduit final eventuel
			 if (( wi == endPos )&& ( remain > 0)) {
				 //console.log ("pool final avec le reste");
				 createPoolContigu( pi, pool, numtype); 
			}
		}
    } 
    
    var createPoolBlock = function(p, firstWell, otherWellsArray ){
    	console.log ("creating pool "+ p);
    	var supportCode=$scope.atmService.data.getCurrentSupportCode();
    	
    	//--1-- puit "0" du block ( numtype toujours en mode Colonne )
    	var line=getLineFromPosition96_C(firstWell);
    	var column=getColumnFromPosition96_C(firstWell);
    	//console.log( ">>container 0:"+ supportCode+"/"+line+"/"+column);

       	// recuperer LE container en position  line/column sur le supportCode et s'il existe le flager ( il ne doit y enavoir qu'un seul a une position donnée)
    	var containers = $filter('filter')($scope.atmService.data.inputContainers, {locationOnContainerSupport:{code:supportCode, line:line+"", column:column+""}},true);	
		if(containers && containers.length === 1)
			containers[0]._addToOutputContainer = true;
    	
    	   //--2-- autres puits  	
    		for (var j=0; j < otherWellsArray.length; j++){
    	    	var line=getLineFromPosition96_C (firstWell + otherWellsArray[j]);
    	    	var column=getColumnFromPosition96_C (firstWell + otherWellsArray[j]);
    	    	
    	    	// recuperer LE container en position  line/column sur le supportCode et s'il existe le flagger
    			var containers = $filter('filter')($scope.atmService.data.inputContainers, {locationOnContainerSupport:{code:supportCode, line:line+"", column:column+""}},true);
    			if(containers && containers.length === 1)
    				containers[0]._addToOutputContainer = true;
    		}
    		
            // creer le container de sortie correspondant au pool (ne sera pas cree si ne contient aucun puit)
        	$scope.atmService.data.dropInSelectInputContainer();	
    }
    
    var createPoolContigu = function( p, wellsArray, numtype ){
    	// pour les mode ligne ou colonne
    	console.log ("creating pool "+ p);
    	var supportCode=$scope.atmService.data.getCurrentSupportCode() ;
    	
    	for (var j=0; j < wellsArray.length; j++){
    		// retraduire l'index en position <ligne><colonne> (depend du mode de numerotation)
    		if (numtype === 'C') {
        		var line=getLineFromPosition96_C( wellsArray[j] );
        		var column=getColumnFromPosition96_C( wellsArray[j] );
    		} else if (numtype === 'L') {
        		var line=getLineFromPosition96_L( wellsArray[j] );
        		var column=getColumnFromPosition96_L( wellsArray[j] );
    		} else { 
    			throw "OOps incorrect numtype:"+ numtype; 
    		}		
    		//console.log( ">>container :"+ supportCode+"/"+line+"/"+column);
    		
        	// recuperer le container en position  line/column  sur le support et s'il existe le flagger
    		var containers = $filter('filter')($scope.atmService.data.inputContainers, {locationOnContainerSupport:{code:supportCode, line:line+"", column:column+""}},true);
    		if(containers && containers.length === 1)
    			containers[0]._addToOutputContainer = true;
    	}
    
        // creer le container de sortie correspondant au pool (ne sera pas cree si ne contient aucun puit)
    	$scope.atmService.data.dropInSelectInputContainer();
    }
    
}]);
// FDS 21/06/2016 dupliqué depuis manyToOne.experiments.cns.x-to-tubes avec modifs
angular.module('home').controller('XToPlatesCtrl',['$scope', '$http','$parse',
                                                               function($scope, $http, $parse) {

	// FDS 29/06/2016 calcul de la ligne en fonction de position 96 numerotée en mode colonne ( 1=A1, 2=B1... 9=A2...) [ thx NW ]
	// reutilisable ailleurs ??
	$scope.setLineFromPosition96_C = function(pos96){	
		line= String.fromCharCode (((pos96 -1) % 8 ) + 65 );
		//console.log("set line to "+ line +"("+pos96+")" );
		return line;
	};
	
	//  FDS 29/06/2016  calcul de la colonne en fonction de position 96 numerotée en mode colonne ( 1=A1, 2=B1... 9=A2...) [ thx NW ]
	//  reutilisable ailleurs ??
	$scope.setColFromPosition96_C = function(pos96){	
		c=Math.floor((pos96 -1) / 8) +1 ;
		column= c.toString();
		//console.log("set col to "+column+"("+pos96+")" );
		return column;
	};
	
	// FDS 03/10/2016 calculer la position96 en mode colonne a partir de ligne et colonne ( 1=A1, 2=B1... 9=A2...) 
	$scope.setPos96FromLineAndCol_C = function(ln,col){
		if ( ln && col ){
			// numeroter a partir de 1 et pas 0!!   var pos= (col  -1 )*8 + ( ln.charCodeAt(0) -65);
			var pos96=(col -1 )*8 + ( ln.charCodeAt(0) -64);
			//console.log(ln+"/"+col+"=>"+pos96);
			return pos96;
		}
	}
	
	//  FDS 03/10/2016 calculer la position96 en mode ligne a partir de ligne et colonne ( 1=A1, 2=A2...13=B1...)
	$scope.setPos96FromLineAndCol_L = function(ln,col){
       //TODO
	}
	
	
	/*TEST...VERIFIER UNE SEULE PLAQUE EN ENTREE POUR  "normalization-and-pooling" !!!
	// marche pas !!!
    if (($scope.experiment.typeCode == "normalization-and-pooling") && $scope.isNewState()) ;{
        $scope.messages.clear();
        $scope.messages.setError( "DEBUG: NORMALIZATION-AND-POOLING / STATE=NEW /NB="+  $scope.atmService.data.inputContainerSupports.length );
    }
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

			// 05/10/2016 si le support de sortie est tube: il faut mettre 1/1 ????
			atm.outputContainerUseds[0].locationOnContainerSupport.column=$scope.setColFromPosition96_C(atm.viewIndex);
			atm.column=atm.outputContainerUseds[0].locationOnContainerSupport.column;
			
			atm.outputContainerUseds[0].locationOnContainerSupport.line=$scope.setLineFromPosition96_C(atm.viewIndex);
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
	
	// !! controller commun a normalization-and-pooling et pooling
	// Julie demande de bloquer le calcul en normalization-and-pooling si unité de concentration n'EST PAS nM TODO...
		
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

    // boutons generateSampleSheet seulement si autre type de containerSuppor que tubes
	if($scope.atmService.inputContainerSupportCategoryCode !== "tube"){
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
	}

	/*     05/10/2016  suppression pour autoriser l'Epimotion
	//Only 96-well-plate is authorized=> force in cas of hand is used
	$scope.$watch("experiment.instrument.outContainerSupportCategoryCode", function(){
			$scope.experiment.instrument.outContainerSupportCategoryCode = "96-well-plate";
	});
	*/
	
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
	
	// pour selects de position de sortie dans le cas des plaques !!!!
	//// TODO mettre ces tableaux dans le scala.html plutot qu'ici ???
	$scope.columns = ["1","2","3","4","5","6","7","8","9","10","11","12"]; 
	$scope.lines=["A","B","C","D","E","F","G","H"];  
	
	//variables tampon
    $scope.lastSupportCode=null;
    $scope.lastStorageCode=null;
    
    // 01/10/2016  gestion du mode pooling "automatique"
    $scope.poolModes=[ {code: 'Lmode',  name: 'ligne'}, 
                       {code: 'Cmode',  name: 'colonne'},
                       {code: 'IDmode', name: 'Illumina Dual Index'},
                       {code: 'ISmode', name: 'Illumina Single Index'}
                       ];
    
    $scope.poolMode=null;
    $scope.poolPlex=null;
    $scope.startColumn=null; 
    $scope.startLine=null;
    $scope.endColumn=null;
    $scope.endLine=null;
    
    $scope.showSelections =function( poolMode, poolPlex, startColumn, startLine, endColumn, endLine ){
    	console.log("selection changed...");
    	
    	console.log("mode="+ poolMode.name + "("+ poolMode.code+")");
    	console.log("plex="+ poolPlex);
    	//console.log("selected startColumn="+startColumn);
    	//console.log("selected startLine="+startLine);
    	//console.log("selected endColumn="+endColumn);
    	//console.log("selected endLine="+endLine);
    		
        // 12/10/2016 si le mode choisi est le mode Ligne il faut ultiliser les fonctions XX_L
    	if ( poolMode.code === "Lmode") {
    		var startPos=$scope.setPos96FromLineAndCol_L(startLine, startColumn );
    		var endPos=$scope.setPos96FromLineAndCol_L(endLine, endColumn);		
    	} else {
    		var startPos=$scope.setPos96FromLineAndCol_C(startLine, startColumn );
    		var endPos=$scope.setPos96FromLineAndCol_C(endLine, endColumn);
    	}	
		console.log ("start position ="+ startPos);
		console.log ("end position ="+ endPos);
		
	    if ( endPos && startPos && poolPlex ){	
    		if ( endPos > startPos ){
    			$scope.messages.clear();
            	if      ( poolMode.code === "IDmode"){ $scope.poolIlluminaDual( poolPlex, startPos, endPos ); }
            	else if ( poolMode.code === "ISmode"){ $scope.poolIlluminaSingle( poolPlex, startPos, endPos ); }
            	// 12/10/2016 en fait mode colonn ou ligne sont identiques si le robot est capable de switcher sa numerotation 
            	// en fonction de l'orientation de la plaque " horizontalement ou verticalement !! Voir D. Derbala
            	// la methode de pooling etant alors "poolContigu" !!!
            	//else if ( poolMode.code === "Cmode") { $scope.poolColumn( poolPlex, startPos, endPos ); }
            	//else if ( poolMode.code === "Lmode") { $scope.poolLine( poolPlex, startPos, endPos ); }
            	else if ( (poolMode.code === "Lmode") || ( poolMode.code === "Cmode")) { $scope.poolContigu( poolPlex, startPos, endPos ); }
    		}else{
        		$scope.messages.clazz = "alert alert-danger";
        		$scope.messages.text = Messages("Position debut doit etre apres la position fin sur la plaque");
        		$scope.messages.showDetails = false;
        		$scope.messages.open();
    		}
    	}
    }
    

    $scope.poolIlluminaDual=function( poolPlex,  startPos, endPos ){
    	// le pooling illumina Dual index est un pooling par blocs contigus...( voir doc Illumina: TruSeq Library prepPooling)
    	//    3-plex =bloc 2x2 avec 1 trou; 4-plex=bloc 2X2;
    	//    5-plex= bloc 2x3 avec 1 trou; 6-plex=bloc 2x3; 
    	//    7-plex= bloc 2x4 avec 1 trou; 8-plex-option1 =bloc 2x4; 8-plex-option2=bloc 4x2
    	//    12-plex= bloc 4x3; 16-plex= bloc xx4
    	console.log ("POOLING MODE ILLUMINA DUAL INDEX");
    	
    	var block=null;
    	// members contient la position relative des autres puits du bloc par rapport au puit "pere" du bloc
    	// ATTENTION numerotation en mode colonne des puits!!!!=> plaque placee horizontalement sur robot 8 aiguilles
    	if      (poolPlex === 3) { block={ line: 2, col:2, members:[1,8]}; }  // comme poolPlex 4 mais sans le puit +9
    	else if (poolPlex === 4) { block={ line: 2, col:2, members:[1,8,9]}; }
    	else if (poolPlex === 5) { block={ line: 2, col:3, members:[1,8,9,16]}; } // comme poolPlex 6 mais sans le puit +17
    	else if (poolPlex === 6) { block={ line: 2, col:3, members:[1,8,9,16,17]}; }
    	else if (poolPlex === 7) { block={ line: 2, col:4, members:[1,8,9,16,17,24]}; } // comme poolPlex 8 mais sans le puit +25
    	else if (poolPlex === 8) { block={ line: 2, col:4, members:[1,8,9,16,17,24,25]}; } // le mode 2 (=4x2) pas pris encharge..]
    	else if (poolPlex === 12){ block={ line: 4, col:3, members:[1,2,3,8,9,10,11,16,17,18,19,]}; } // le mode 3x4 n'est pas pertinent pour une plaque 8x12
    	else if (poolPlex === 16){ block={ line: 4, col:4, members:[1,2,3,8,9,10,11,16,17,18,19,24,25,26,27]}; }
    	else{
    		$scope.messages.clazz = "alert alert-danger";
    		$scope.messages.text = Messages("plex "+ poolPlex +" not managed in Illumina Dual Index mode");
    		$scope.messages.showDetails = false;
    		$scope.messages.open();				
    	}

    	if ( block ){
    		$scope.messages.clear();
    		//console.log ("POOLING MODE ILLUMINA DUAL INDEX plex="+ poolPlex + "("+ block.line+"/"+block.col+")" );
    		
    	    var blockSize=block.line*block.col;
    	    var lastBlockMember=block.members.slice(-1)[0];
    	    
    	    // verifier que les positions choisies correspondent a un nombre de blocs complet
    	    //  PB PAS SI SIMPLE !!!!    nbWellsSelected=endPos-StartPos +1;
    	    //  par exemple si debut =0 et fin =12 en mode block de 4 on a 8 puits et pas 12 (11-0 +1)!!! 
    	    
    	    // attention !!! en fonction de la posion de depart  le block  peut se retrouver a cheval le bord de plaque
    	    // ==> comment controler ca ?????????

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
    	        console.log ("pool "+ (pi+1) +" commence au puit "+(wi+1)+" et fini au puit "+lastWell);
    	        $scope.createPoolBlock( pi+1, wi+1, block.members );
    	        
    	        // il faut sortir quand le plus grand indice de puit du block courant est plus grand que la position de fin demandee
    	        // probleme pour les blocs a trou...compter les trous
    	        var blockHoles=blockSize - block.members.length -1;
    	        
        	    if (lastWell  >=  ( endPos - blockHoles )) { 
    	        	console.log ("exit loop!!!"); 
    	        	loop=false
    	        } 
    	    	pi++;
   			}
    	}
    }// end poolIlluminaDual
    
    $scope.poolIlluminaSingle=function( poolPlex,  startPos, endPos ){
    	console.log ("POOLINGMODE ILLUMINA SINGLE INDEX");
    	console.log ("....TODO....");
    } 
    
    $scope.poolContigu=function( poolPlex,  startPos, endPos ){
    	console.log ("POOLING MODE 'CONTIGU'");
    	
		var nbWells=endPos - startPos +1;
		var nbPools= Math.floor(nbWells / poolPlex);
		console.log ("nbPools="+ nbPools);
		
		var remain= nbWells % poolPlex;
		//le dernier pool est donc un pool reduit
		console.log ("reste="+ remain);
		
		// ici les indices pi et wi commencent a 1
		var pi=1;
		var pool=[];
		for ( var wi= startPos; wi < (endPos+1); wi++){
			 //console.log (">>ajouter puit "+ wi +" dans pool "+ pi);
			 pool.push(wi);
			 // si wi multiple de poolPlex alors on change de pool 
			 // ATTENTION prise en compte de la position de debut 	 
			 if  ((wi - startPos +1 )%poolPlex == 0 ) {
				 $scope.createPoolContigu( pi, pool);
				 var pool=[];
				 pi++;
			 }
			 // et le pool reduitfinal eventuel
			 if (( wi == endPos )&& ( remain > 0)) {
				 console.log ("pool final avec le reste");
				 $scope.createPoolContigu( pi, pool); 
			}
		}
    } 
    
    $scope.createPoolBlock=function(p, firstWell, otherWellsArray ){
    	console.log ("creating pool "+ p);
    	
    	if ( $scope.isValidForPool(firstWell) ) {
    	   console.log ("add well="+ firstWell+" in pool");
    	  //add2Pool.....
        }
    	
    	// utiliser plutot forEach ???NON plus lent et pas standard...
    	for (var j=0; j < otherWellsArray.length; j++){
    		if ( $scope.isValidForPool(firstWell + otherWellsArray[j]) ) {
    		   console.log ("add well="+  (firstWell+ otherWellsArray[j]) +" in pool" );
    		   //add2Pool...
    		}
    	}
    }
    
    $scope.createPoolContigu=function(p, wellsArray ){
    	// pour les mode ligne ou colonne
    	console.log ("creating pool "+ p);
    	
    	// utiliser plutot forEach ???NON plus lent et pas standard...
    	for (var j=0; j < wellsArray.length; j++){
    		if ( $scope.isValidForPool( wellsArray[j]) ) {
    		   console.log ("add well="+  wellsArray[j] +" in pool" );
    		   //add2Pool...
    		}
    	}
    }
    
    $scope.isValidForPool=function(well){
    	//TODO
       	//  -1- il doit y avoir qq chose dans le puits
    	//  -2- concentration minimale ???
    	console.log ("Check  valid criteria for well "+well);
    }
    
}]);
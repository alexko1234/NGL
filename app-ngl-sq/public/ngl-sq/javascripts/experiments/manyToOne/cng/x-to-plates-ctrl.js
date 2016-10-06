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
    
    
    // TEST FDS 03/10/2016 selection des parametres de pool
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
       	var startPos=$scope.setPos96FromLineAndCol_C(startLine, startColumn );
    	console.log ("start position ="+ startPos);

    	//console.log("selected endColumn="+endColumn);
    	//console.log("selected endLine="+endLine);
    	var endPos=$scope.setPos96FromLineAndCol_C(endLine, endColumn);
    	console.log ("end position ="+ endPos);
    	
    	// startPos ne peux plus etre null ( voir setPos96FromLineAndCol_C )
    	if ( endPos && startPos && poolPlex ){
    		if ( endPos > startPos ){
    			$scope.messages.clear();
    			
    		//var nbWells=	endPos - startPos;
            //	if ( Math.floor(nbWells/poolPlex) === nbWells/poolPlex ){
            		if      ( poolMode.code === "IDmode"){ $scope.poolIlluminaDual( poolPlex, startPos, endPos ); }
            		else if ( poolMode.code === "ISmode"){ $scope.poolIlluminaSingle( poolPlex, startPos, endPos ); }
            		else if ( poolMode.code === "Cmode") { $scope.poolColumn( poolPlex, startPos, endPos ); }
            		else if ( poolMode.code === "Lmode") { $scope.poolLine( poolPlex, startPos, endPos ); }
            	//} else {
            	//	$scope.messages.clazz = "alert alert-danger";
            	//	$scope.messages.text = Messages("Le nombre de puits choisis n'est pas multiple de "+poolPlex );
            	//	$scope.messages.showDetails = false;
            	//	$scope.messages.open();				
            	//}
    		}else{
        		$scope.messages.clazz = "alert alert-danger";
        		$scope.messages.text = Messages("Position debut doit etre apres la position fin sur la plaque");
        		$scope.messages.showDetails = false;
        		$scope.messages.open();
    		}
    	}
    }
    
	// chaque fonction doit verifier que les positions choisies correspondent a un nombre de bloc complet !!!
	// TODO !!! 
    $scope.poolIlluminaDual=function( poolPlex,  startPos, endPos ){
    	// le pooling illumina est un pooling par blocs contigus...
    	//    3-plex =bloc 2x2 avec 1 trou; 4-plex= bloc 2X2;
    	//    5-plex= bloc 2x3 avec 1 trou; 6-plex=bloc 2x3; 
    	//    7-plex= bloc 2x4 avec 1 trou; 8-plex-option1 =bloc 2x4; 8-plex-option2=bloc 4x2
    	//    12-plex= bloc 4x3; 16-plex= bloc xx4
    	//console.log ("POOLING MODE ILLUMINA DUAL INDEX");
    	
    	var bloc=null;
    	
    	if      (poolPlex === 3) { bloc={ line: 2, col:2, neighbors:[1, 8]}; }
    	else if (poolPlex === 4) { bloc={ line: 2, col:2, neighbors:[1, 8,9]}; }
    	else if (poolPlex === 5) { bloc={ line: 2, col:3, neighbors:[1, 8,9, 16]}; }
    	else if (poolPlex === 6) { bloc={ line: 2, col:3, neighbors:[1, 8,9, 16,17]}; }
    	else if (poolPlex === 7) { bloc={ line: 2, col:4, neighbors:[1, 8,9, 16,17, 24]}; }
    	else if (poolPlex === 8) { bloc={ line: 2, col:4, neighbors:[1, 8,9, 16,17, 24,25]}; } // mode 1
    	else if (poolPlex === 12){ bloc={ line: 4, col:3, neighbors:[1,2,3, 8,9,10,11, 16,17,18,19,]}; } // mode 1
    	else if (poolPlex === 16){ bloc={ line: 4, col:4, neighbors:[1,2,3, 8,9,10,11, 16,17,18,19, 24,25,26,27]}; }
    	else{
    		$scope.messages.clazz = "alert alert-danger";
    		$scope.messages.text = Messages('plex '+ poolPlex +' not managed in Illumina Dual Index mode');
    		$scope.messages.showDetails = false;
    		$scope.messages.open();				
    	}

    	if ( bloc ){
    		$scope.messages.clear();
    		//console.log ("POOLING MODE ILLUMINA DUAL INDEX plex="+ poolPlex + "("+ bloc.line+"/"+bloc.col+")" );
    		
    	    var blocSize=bloc.line*bloc.col;

    	    //!! cet algo marche pour numerotation a partir de 0 donc:  -1 a startPos, +1 en sortie pour (i) Pool et (n) puit
    	    // TEST remplacer for par while
    	    //for ( var i=(startPos-1) ; i <  96 ; i++){
    	    // NON c'est toujours pas le bon critere de sortie
    	    var i=(startPos-1);
    	    var n=0;
    	    while ( n < (endPos -1) ) {
    	    	 //[encore !!  thx NW ]
    	    	var blocparcol = 8/bloc.line; // line toujours diviseur de 8 ( 2 ou 4!!)
    	        var j= blocparcol *Math.floor(i /blocparcol) * blocSize;
    	        var k= (i % blocparcol ) * bloc.line;
    	        var n= j+k ;

    	    	console.log ("pool "+ (i+1) +" commence a puit="+(n+1));
    	        $scope.makePool ( i+1, n+1, bloc.neighbors );
    	    	i++;
   			}
    	}
    }// end poolIlluminaDual
    
    $scope.poolIlluminaSingle=function( poolPlex,  startPos, endPos ){
    	console.log ("POOLINGMODE ILLUMINA SINGLE INDEX");
    } 
    
    $scope.poolColumn=function( poolPlex,  startPos, endPos ){
    	console.log ("POOLING COLUMN MODE");
    	
 
		//var nbWells=endPos - startPos +1;
		//var nbPools= Math.floor(nbWells / nbPlex);
		//console.log ("nbPools="+ nbPools);
		//var rest= nbWells - (nbPools * nbPlex);// il doit y avoir une method Math pour ca !!
		//console.log ("reste="+ rest);

    }
    
    $scope.poolLine=function( poolPlex,  startPos, endPos ){
    	console.log ("POOLING LINE MODE");	
    }
    
    $scope.makePool=function(p, firstWell, otherWells ){
    	console.log ("making pool "+ p);
    	console.log ("firstWell="+ firstWell)
    	// utiliser plutot forEach ???
    	for (var j=0; j < otherWells.length; j++){
    		console.log ("nextWell="+  (firstWell+ otherWells[j]));
    	}
    }
    
    
}]);
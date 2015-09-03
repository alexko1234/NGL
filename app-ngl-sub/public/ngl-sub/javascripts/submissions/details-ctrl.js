"use strict";

angular.module('home').controller('DetailsCtrl',[ '$http', '$scope', '$routeParams' , 'mainService', 'lists', 'tabService','messages','datatable',
                                                  function($http, $scope, $routeParams, mainService, lists, tabService, messages, datatable) { 



	var samplesDTConfig = {
			name:'sampleDT',
			order :{by:'code',mode:'local', reverse:true},
			search:{active:false},
			pagination:{active:false},
			select:{active:true},
			showTotalNumberRecords:false,
			edit : {
				active:true,
				showButton : false,
				withoutSelect : true,
				columnMode : true,
				lineMode : function(line){
					if(line.state.code != "inWaiting")
						return true;
					else 
						return false;
				}
			},
			save : {
				active:true,
				showButton : false,
				changeClass : false,
				// important de mettre en mode local pour rafraichissement de la page, mais sauvegarde globale via bouton
				mode:'local',
				url:function(line){
					return jsRoutes.controllers.sra.samples.api.Samples.update(line.code).url; // jamais utilisé en mode local
				},
				method:'put',
				value:function(line){
					return line;
				},
			},
			cancel : {
				showButton:true
			},
			hide:{
				active:true
			},
			columns : [
			        {property:"code",
			        	  header: "code",
			        	  type :"text",		    	  	
			        	  order:true
			        },
			        {property:"projectCode",
			        	  header: "projectCode",
			        	  type :"text",		    	  	
			        	  order:false,
			        	  edit:true,            // false
			        	  choiceInList:false  
			        },
			        {property:"title",
			        	  header: "title",
			        	  type :"text",	
			        	  hide:true,
			        	  order:false,
			        	  edit:true,
			        	  choiceInList:false
			        },
			        {property:"description",
			        	  header: "description",
			        	  type :"text",	
			        	  hide:true,
			        	  order:false,
			        	  edit:true,
			        	  choiceInList:false
			        },
			        {property:"clone",
			        	  header: "clone",
			        	  type :"text",	
			        	  hide:true,
			        	  order:false,
			        	  edit:true,
			        	  choiceInList:false
			        },
			        {property:"taxonId",
			        	  header: "taxonId",
			        	  type :"int",
			        	  hide:true,
			        	  order:false,
			        	  edit:true,
			        	  choiceInList:false
			        },
			        {property:"classification",
			        	  header: "classification",
			        	  type :"text",		    	  	
			        	  order:false,
			        	  edit:true,
			        	  choiceInList:false
			        },
			        {property:"commonName",
			        	  header: "commonName",
			        	  type :"text",		    	  	
			        	  hide:true,
			        	  order:false,
			        	  edit:true,
			        	  choiceInList:false
			        },
			        {property:"scientificName",
			        	  header: "scientificName",
			        	  type :"text",		    	  	
			        	  order:false,
			        	  edit:true,
			        	  choiceInList:false
			        },
			        {property:"state.code",
			        	  header: "state",
			        	  type :"text",		    	  	
			        	  order:false,
			        	  edit:false,
			        	  choiceInList:false
			        }
			]				
	};

	var experimentsDTConfig = {
			name:'experimentDT',
			order :{by:'code',mode:'local', reverse:true},
			search:{active:false},
			pagination:{active:false},
			select:{active:true},
			showTotalNumberRecords:false,
			edit : {
				active:true,
				showButton : false,
				withoutSelect : true,
				columnMode : true
			},
			save : {
				active:true,
				mode:'local',
				showButton : false,
				changeClass : false,
				url:function(lineValue){
					return jsRoutes.controllers.sra.experiments.api.Experiments.update(lineValue.code).url; // jamais utilisé en mode local
				},
				method:'put',
				value:function(line){
					return line;
				},
			},
			cancel : {
				showButton:true
			},
			hide:{
				active:true,
				showButton:true
			},
			exportCSV:{
				active:false
			},
			columns : [
			        {property:"code",
			        	header: "code",
			        	type :"text",		    	  	
			        	order:true
			        },	
			        {property:"projectCode",
			        	header: "projectCode",
			        	type :"text",		    	  	
			        	order:false,
			        	edit:false,
			        	choiceInList:false  
			        },
			        {property:"librarySelection",
						header: "librarySelection",
						type :"String",
			        	hide:true,
			        	edit:true,
						order:false,
				    	choiceInList:true,
				    	listStyle:'bt-select-multiple',
				    	possibleValues:'sraVariables.librarySelection',
				    },
				    {property:"libraryStrategy",
						header: "libraryStrategy",
						type :"String",
						hide:true,
						edit:true,
						order:false,
						choiceInList:true,
						listStyle:'bt-select-multiple',
						possibleValues:'sraVariables.libraryStrategy',
				    },
					{property:"librarySource",
						header: "librarySource",
						type :"String",
						hide:true,
						edit:true,
						order:false,
						choiceInList:true,
						listStyle:'bt-select-multiple',
						possibleValues:'sraVariables.librarySource',
					},
					{property:"libraryLayout",
						header: "libraryLayout",
						type :"String",
						hide:true,
						edit:false,
						order:false,
						choiceInList:true,
						listStyle:'bt-select-multiple',
						possibleValues:'sraVariables.libraryLayout',
					},	
					{property:"libraryLayoutNominalLength",
			        	header: "libraryLayoutNominalLength",
			        	type :"integer",		    	  	
			        	hide:true,
						edit:true,
			        	order:true
					},	
					{property:"libraryLayoutOrientation",
						header: "libraryLayoutOrientation",
						type :"String",
						hide:true,
						edit:true,
						order:false,
						choiceInList:true,
						listStyle:'bt-select-multiple',
						possibleValues:'sraVariables.libraryLayoutOrientation',
					},	
					{property:"libraryName",
						header: "libraryName",
						type :"String",		    	  	
						hide:true,
						edit:true,
						order:true
					},
					{property:"libraryConstructionProtocol",
						 header: "libraryConstructionProtocol",
						 type :"String",		    	  	
						 hide:true,
						 edit:true,
						 order:true
					},
					{property:"typePlatform",
						 header: "typePlatform",
						 type :"String",		    	  	
						 hide:true,
						 edit:false,
						 order:true
					},
					{property:"instrumentModel",
						 header: "instrumentModel",
						 type :"String",		    	  	
						 hide:true,
						 edit:true,
						 order:true
					},
					{property:"lastBaseCoord",
			        	header: "lastBaseCoord",
			        	type :"integer",		    	  	
			        	hide:true,
						edit:true,
			        	order:true
					},	
					{property:"spotLength",
			        	header: "spotLength",
			        	type :"Long",		    	  	
			        	hide:true,
						edit:true,
			        	order:true
					},	
					{property:"sampleCode",
			        	header: "sampleCode",
			        	type :"String",		    	  	
			        	hide:true,
						edit:false,
			        	order:true
					},	
					{property:"studyCode",
			        	header: "studyCode",
			        	type :"String",		    	  	
			        	hide:true,
						edit:false,
			        	order:true
					},
					 {property:"state.code",
			        	  header: "state",
			        	  type :"text",		    	  	
			        	  order:false,
			        	  edit:false,
			        	  choiceInList:false
			        }
			 ]	        
	};
	
	var runsDTConfig = {
			name:'runDT',
			order :{by:'code',mode:'local', reverse:true},
			search:{active:false},
			pagination:{active:false},
			showTotalNumberRecords:false,
			edit : {
				active:true,
				showButton : false,
				withoutSelect : true,
				columnMode : true
			},
			/*
			save : {
				active:true,
				showButton : true,
				changeClass : false,
				url:function(value){
					return jsRoutes.controllers.sra.experiments.api.Experiments.update(value.code).url;
				},
				method:'put'
			},
			cancel : {
				showButton:true
			},
			hide:{
				active:true
			},
			exportCSV:{
				active:true
			},*/
			columns : [
			           {property:"code",
			        	header: "experiment.code",
			        	type :"text",		    	  	
			        	order:true
			           },
			           {property:"run.code",
			        	header: "code",
			        	type :"text",		    	  	
			        	order:true
			           },
			           {property:"run.runDate",
				        header: "runDate",
				       	type :"Date",		    	  	
				       	order:true
				       }
			          
		    ]	        
		};
	var rawDatasDTConfig = {
			name:'rawDataDT',
			order :{by:'code',mode:'local', reverse:true},
			search:{active:false},
			pagination:{active:false},
			select:{active:true},
			showTotalNumberRecords:false,
			edit : {
				active:true,
				showButton : false,
				withoutSelect : true,
				columnMode : true
			},
			cancel : {
				showButton:true
			},
			hide:{
				active:true
			},
			exportCSV:{
				active:true
			},
			columns : [
			           {property:"relatifName",
			        	header: "relatifName",
			        	type :"text",		    	  	
			        	order:true
			           }
		    ]	        
		};	

	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('create');
		tabService.addTabs({label:Messages('submissionss.menu.create'),href:jsRoutes.controllers.sra.submissions.tpl.Submissions.home("create").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	// si on declare dans services => var sraVariables = {};
	// si on declare dans le controlleur :
	$scope.sraVariables = {};
	$scope.subList={};
	$scope.checkSample=false;
	
	
	var init = function(){
		$scope.messages = messages();
		$scope.mainService = mainService;
		$scope.mainService.stopEditMode();
		$scope.sampleCheck=false;
		$scope.experimentCheck=false;
		$scope.runCheck=false;
		$scope.rawDataCheck=false;
		// Attention appel de get du controller api.sra.submissions qui est herite
		$http.get(jsRoutes.controllers.sra.submissions.api.Submissions.get($routeParams.code).url).success(function(data) {
			$scope.submission = data;	
			console.log("$routeParams.code:"+$routeParams.code);
			console.log("Submission.code :"+$scope.submission.code);
			console.log("Submission.refSampleCodes :"+$scope.submission.refSampleCodes);
			console.log("Submission.sampleCodes :"+$scope.submission.sampleCodes);
			console.log("Submission.experimentCodes :"+$scope.submission.experimentCodes);
			console.log("Submission.runCodes :"+$scope.submission.runCodes);
			$http.get(jsRoutes.controllers.sra.api.Variables.get('librarySelection').url)
			.success(function(data) {
				// initialisation de la variable sraVariables.librarySelection utilisee dans experimentsDTConfig
				$scope.sraVariables.librarySelection = data;
			});	
			$http.get(jsRoutes.controllers.sra.api.Variables.get('libraryStrategy').url)
			.success(function(data) {
				// initialisation de la variable sraVariables.libraryStrategy utilisee dans experimentsDTConfig
				$scope.sraVariables.libraryStrategy = data;
			});
			$http.get(jsRoutes.controllers.sra.api.Variables.get('librarySource').url)
			.success(function(data) {
				// initialisation de la variable sraVariables.librarySource utilisee dans experimentsDTConfig
				$scope.sraVariables.librarySource = data;
			});
			$http.get(jsRoutes.controllers.sra.api.Variables.get('libraryLayout').url)
			.success(function(data) {
				// initialisation de la variable sraVariables.libraryLayout utilisee dans experimentsDTConfig
				$scope.sraVariables.libraryLayout = data;
			});
			$http.get(jsRoutes.controllers.sra.api.Variables.get('libraryLayoutOrientation').url)
			.success(function(data) {
				// initialisation de la variable sraVariables.libraryLayoutOrientation utilisee dans experimentsDTConfig
				$scope.sraVariables.libraryLayoutOrientation = data;
			});	
			//Get samples
			$http.get(jsRoutes.controllers.sra.samples.api.Samples.list().url, {params: {listSampleCodes:$scope.submission.refSampleCodes}}).success(function(data)
					{
					$scope.samples = data;
			
					console.log("$scope.submission.sampleCodes: " + $scope.submission.sampleCodes);
					console.log("$scope.samples :" + $scope.samples);

					//Init datatable
					$scope.sampleDT = datatable(samplesDTConfig);
					$scope.sampleDT.setData($scope.samples, $scope.samples.length);
					});
			//Get experiments (and runs)
			$http.get(jsRoutes.controllers.sra.experiments.api.Experiments.list().url, {params: {listExperimentCodes:$scope.submission.experimentCodes}}).success(function(data)
					{
					$scope.experiments = data;
					//Init datatable
					$scope.experimentDT = datatable(experimentsDTConfig);
					$scope.experimentDT.setData($scope.experiments, $scope.experiments.length);
					//Get Runs
					$scope.runDT = datatable(runsDTConfig);
					// Comme on a un seul run par experiment, on n'a pas besoin de boucler pour recuperer les données :
					$scope.runDT.setData($scope.experiments, $scope.experiments.length);
					
					
					// Get RawDatas : construction de la liste des rawData puis injection dans datatable :
					var maListRawDatas = [];
					for (var i=0; i<$scope.experiments.length; i++) {
						var run = $scope.experiments[i].run;
						for (var j=0; j<run.listRawData.length; j++) {
							maListRawDatas.push(run.listRawData[j]);
						}
					}
					
					$scope.rawDataDT = datatable(rawDatasDTConfig);
					$scope.rawDataDT.setData(maListRawDatas, maListRawDatas.length);
					});			
		
		});
		
		
	};

	init();

	function closeSubmission(){
	   	$scope.submission.state.code = "userValidate";		
		$http.put(jsRoutes.controllers.sra.submissions.api.Submissions.update($scope.submission.code).url, $scope.submission)
 				.success(function(data) {
					//Set success message
					$scope.messages.clazz="alert alert-success";
					$scope.messages.text=Messages('submissions.msg.validate.success');
					$scope.messages.open();
				}).error(function(data){
					$scope.messages.addDetails(data);
					$scope.messages.setError("save");
				});
	}
	
	//en Javascript, ce n'est pas vous qui choisissez le mode de passage ; 
    //ça fonctionne un peu comme en Java[1] : 
    //si c'est un type natif (Number, String, etc.) c'est passé par valeur, 
    //tandis que si c'est un objet d'une classe à vous, c'est passé par référence.
    // => d'ou l'importance du return ici
	function processInSubmission(decompte, error) { // pas d'indication de retour dans la signature.
		decompte = decompte - 1;
 		if (decompte === 0) {
 			if (error){
 				// afficher message d'erreur sans sauver la soumission.
 				$scope.messages.setError("save");
 			} else {
 				// sauver la soumission dans base et afficher resultat de la requete 
 				closeSubmission();
 			}
 		}
 		return decompte;
	}

		
	/* buttons section */
	$scope.userValidate = function(){
		$scope.messages.clear();
		
		
		var error = false;
		// Recuperation des samples :
		$scope.sampleDT.save();	// sauvegarde dans client des samples avec valeurs editees (valeurs utilisateurs)	
		var tab_samples = $scope.sampleDT.getData();

		// Recuperation des experiments :	
		$scope.experimentDT.save();		// recuperation saisie utilisateur et sauvegarde dans client.
		var tab_experiments = $scope.experimentDT.getData();

		var decompte = tab_samples.length +  tab_experiments.length;
		
		
		// Mise à jour du status  des samples :
		for(var i = 0; i < tab_samples.length ; i++){
			console.log("sampleCode = " + tab_samples[i].code + " state = "+ tab_samples[i].state.code);
			tab_samples[i].state.code = "userValidate";
			console.log("sampleCode = " + tab_samples[i].code + " state = "+ tab_samples[i].state.code);
			// sauvegarde dans database asynchrone
			$http.put(jsRoutes.controllers.sra.samples.api.Samples.update(tab_samples[i].code).url, tab_samples[i])
			.success(function(data){
			//Set success message
			//$scope.messages.clazz="alert alert-success";
			//$scope.messages.text=Messages('submissions.msg.validate.success');
			//$scope.messages.open();
			decompte = processInSubmission(decompte, error);
			}).error(function(data){
			$scope.messages.addDetails(data);
			//$scope.messages.setError("save");
			error = true;
			decompte = processInSubmission(decompte, error);
			});			
			console.log("sampleTitle = " + tab_samples[i].title + " state = "+ tab_samples[i].state.code);
		}
		$scope.sampleDT.setData(tab_samples, tab_samples.length);
		// sauvegarde cote client des samples avec bon statut
		$scope.sampleDT.save(); // fait le save cote client mais n'utilise pas url et ne fait pas save dans database.
	
		
		// Mise à jour du statut des experiments :
		for(var i = 0; i < tab_experiments.length ; i++){
			console.log("experimentCode = " + tab_experiments[i].code + " state = "+ tab_experiments[i].state.code);
			tab_experiments[i].state.code = "userValidate";
			console.log("experimentCode = " + tab_experiments[i].code + " state = "+ tab_experiments[i].state.code);
			// sauvegarde dans database :
			$http.put(jsRoutes.controllers.sra.experiments.api.Experiments.update(tab_experiments[i].code).url, tab_experiments[i]).success(function(data){
			//Set success message
			//$scope.messages.clazz="alert alert-success";
			//$scope.messages.text=Messages('submissions.msg.validate.success');
			//$scope.messages.open();
			decompte = processInSubmission(decompte, error);
		}).error(function(data){
			$scope.messages.addDetails(data);
			error = true;
			decompte = processInSubmission(decompte, error);
			//$scope.messages.setError("save");
		});			
		}
		// initialisation inutile $scope.experimentDT = datatable(experimentsDTConfig);
		$scope.experimentDT.setData(tab_experiments, tab_experiments.length);
		// sauvegarde cote client des experiments avec bon statut :
		$scope.experimentDT.save(); // fait le save cote client mais n'utilise pas url et ne fait pas save dans database.
		
		//mise a jour l'etat de submission a userValidate realise dans closeSubmission appelé par processInSubmission
	
	};
	
	$scope.cancel = function(){
		console.log("call cancel");
		$scope.messages.clear();
		$scope.sampleDT.cancel();
		$scope.experimentDT.cancel();
		$scope.mainService.stopEditMode();		
	};
	
	$scope.activeEditMode = function(){
		$scope.messages.clear();
		$scope.mainService.startEditMode();
		$scope.sampleDT.setEdit();
		$scope.experimentDT.setEdit();
	};
	
}]);


angular.module('home').controller('CNGPrepaFlowcellOrderedCtrl',['$scope', '$parse', '$http','atmToDragNDrop',
                                                               function($scope, $parse, $http, atmToDragNDrop) {
	
	
	
	var atmToSingleDatatable = $scope.atmService.$atmToSingleDatatable;
	
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
		        	"header":"Code aliquot",
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
	
	
	if($scope.experiment.instrument.inContainerSupportCategoryCode!=="tube"){
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
	
	
	var generateSampleSheet = function(){
		$scope.messages.clear();
		
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
			$scope.messages.setDetails(data);
			$scope.messages.showDetails = true;
			$scope.messages.open();				
		});
	};
	
	$scope.setAdditionnalButtons([{
		isDisabled : function(){return $scope.isCreationMode();},
		isShow:function(){return ($scope.experiment.instrument.typeCode === 'janus-and-cBotV2')},
		click:generateSampleSheet,
		label:Messages("experiments.sampleSheet")
	}]);
	
	/* 06/01/2017 FDS ajout pour l'import du fichier Cbot-V2 */
	var importData = function(){
		
		$scope.messages.clear();
		$http.post(jsRoutes.controllers.instruments.io.IO.importFile($scope.experiment.code).url, $scope.file)
		.success(function(data, status, headers, config) {
			
			$scope.messages.clazz="alert alert-success";
			$scope.messages.text=Messages('experiments.msg.import.success');
			$scope.messages.showDetails = false;
			$scope.messages.open();	
			
			// data est l'experience retournée par input.java
			
			// 16/01/2017 recuperer instrumentProperties 
			$scope.experiment.instrumentProperties= data.instrumentProperties;
			
			// et reagents ....
			$scope.experiment.reagents=data.reagents;
			
			// reinit select File...
			$scope.file = undefined;
			angular.element('#importFile')[0].value = null;
			
			// NGL-1256 refresh special pour les reagents !!!
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
			angular.element('#importFile')[0].value = null;
		});		
	};
	
	$scope.button = {
		isShow:function(){
			// 31/01/2017  activer le bouton d'import si l'experience est a InProgress
			return ($scope.isInProgressState());
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData,		
	};
	
	// 07/02/2017 si l'utilisateur modifie le codeStrip OU le code Flowcell OU l'instrument  il doit recharger le fichier pour qu'on puisse garantir la coherence !!
    //-1- stripCode.value
	$scope.$watch("experiment.instrumentProperties.stripCode.value", function(newValue, OldValue){
			if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {
				$scope.experiment.instrumentProperties.cbotFile.value = undefined;
			}
	});	
	
	//-2- code Flowcell
	$scope.$watch("experiment.instrumentProperties.containerSupportCode.value", function(newValue, OldValue){
		if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {
		    $scope.experiment.instrumentProperties.cbotFile.value = undefined;		
		    checkFCsequencingType();// ajout 24/04/2017 NGL-1325
		}
	});	
	
	//-3- code cBot
	$scope.$watch("experiment.instrument.code" , function(newValue, OldValue){
		if ( newValue !== OldValue ) {
			$scope.experiment.instrumentProperties.cbotFile.value = undefined;
		}
	});	
	
   // ajout 24/04/2017 NGL-1325
	$scope.$watch("experiment.experimentProperties.sequencingType.value", function(newValue, OldValue){
		if ((newValue) && (newValue !== null ) && ( newValue !== OldValue ))  {
			checkFCsequencingType();
		} else {
			$scope.messages.clear();
		}
	});	
	
	function checkFCsequencingType (){
		var H4000fcRegexp= /^[A-Za-z0-9]*BBXX$/;
		var HXfcRegexp= /^[A-Za-z0-9]*ALXX$/;
		var fcBarcode= $scope.experiment.instrumentProperties.containerSupportCode.value;
		
		/// ! fcBarcode.test ( ) fonctionne pas !!!
		if (($scope.experiment.experimentProperties.sequencingType.value === 'Hiseq 4000') && ( null===fcBarcode.match(H4000fcRegexp))) {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Code Flowcell n'est pas du type Hiseq 4000 (*BBXX)";
			$scope.messages.showDetails = false;
			$scope.messages.open();
		} else	if (($scope.experiment.experimentProperties.sequencingType.value === 'Hiseq X') && ( null ===fcBarcode.match(HXfcRegexp))) {
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Code Flowcell n'est pas du type Hiseq X (*ALXX)";
			$scope.messages.showDetails = false;
			$scope.messages.open();
		} else {
			// attention ecrase des eventuels messages precedents...
			$scope.messages.clear();
		}	
	}
	
}]);

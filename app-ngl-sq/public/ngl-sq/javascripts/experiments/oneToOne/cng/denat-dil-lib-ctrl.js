angular.module('home').controller('DenatDilLibCtrl',['$scope', '$parse', 'atmToSingleDatatable',
                                                     function($scope, $parse, atmToSingleDatatable){
	/*
	 1) Code Container
2) Etat container
3) Projet(s)
4) Echantillon(s)
5) Code aliquot
6) Tag
7) Concentration (nM) 
	 
	 */
	// JIRA-781 rendre editable quand experience est en cours=> supprimer:  && !$scope.Inprogress 
	var datatableConfig = {
			name:"FDR_Tube",
			columns:[
			 
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:"Inputs"}
			         },					 				         
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainer.projectCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":2,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":3,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
				     
				     {
			        	"header":"Code aliquot",
			 			"property": "inputContainer.contents",
			 			"filter": "getArray:'properties.sampleAliquoteCode.value'",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":4,
			 			"render": "<div list-resize='cellValue | unique' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
				     },
			         {
			        	"header":Messages("containers.table.tags"),
			 			"property": "inputContainer.contents",
			 			"filter": "getArray:'properties.tag.value'",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":5,
			 			"render":"<div list-resize='cellValue |  unique' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:"Inputs"}
			         },				 
					 {
			        	 "header":Messages("containers.table.concentration") + " (nM)",
			        	 "property":"inputContainer.mesuredConcentration.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6,
			        	 "extraHeaders":{0:"Inputs"}
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
			        	 "extraHeaders":{0:"Inputs"}
			         },
			        /* {
			        	 "header":function(){return Messages("containers.table.volume") + " (µL)"},
			        	 "property":"mesuredVolume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":5,
			        	 "extraHeaders":{0:"Inputs"}
			         },*/
			         {
			        	 "header":Messages("containers.table.concentration") + " (pM)",
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "convertValue": {"active":true, "displayMeasureValue":"pM", "saveMeasureValue":"nM"},			        	 
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 //"defaultValues":10,
			        	 "position":50,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.volume")+ " (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"number",
			        	 "position":51,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":400,
			        	 "extraHeaders":{0:"Outputs"}
			         },
			         {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":500,
			        	 "extraHeaders":{0:"Outputs"}
			         }
			         
			         ],
			compact:true,
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local', //or 
				active:true,
				by:'inputContainer.code'
			},
			remove:{
				active: (!$scope.doneAndRecorded ),
				showButton: (!$scope.doneAndRecorded ),
				mode:'local'
			},
			save:{
				active:true,
	        	withoutEdit: true,
	        	showButton:false,
	        	mode:'local'
			},
			hide:{
				active:true
			},
			edit:{
				active: (!$scope.doneAndRecorded ),
				columnMode:true
			},
			messages:{
				active:false,
				columnMode:true
			},
			exportCSV:{
				active:true,
				showButton:true,
				delimiter:";",
				start:false
			},
			extraHeaders:{
				number:2,
				dynamic:true,
			},
			otherButton:{
				active:true,
				template:'<button class="btn btn btn-info" ng-click="newPurif()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doPurif" title="'+Messages("experiments.addpurif")+'">Messages("experiments.addpurif")</button><button class="btn btn btn-info" ng-click="newQc()" data-toggle="tooltip" ng-disabled="experiment.value.state.code != \'F\'" ng-hide="!experiment.doQc" title="Messages("experiments.addqc")">Messages("experiments.addqc")</button>'
			}
	};

	$scope.$on('save', function(e, promises, func, endPromises) {	
		console.log("call event save");
		$scope.atmService.data.save();
		$scope.atmService.viewToExperimentOneToOne($scope.experiment);
		$scope.$emit('viewSaved', promises, func, endPromises);
	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh");
		
		var dtConfig = $scope.atmService.data.getConfig();
		dtConfig.edit.active = (!$scope.doneAndRecorded );
		dtConfig.remove.active = (!$scope.doneAndRecorded );
		dtConfig.remove.showButton = (!$scope.doneAndRecorded );
		$scope.atmService.data.setConfig(dtConfig);
		
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
		
	//Init
	
	var atmService = atmToSingleDatatable($scope, datatableConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(){
		return {
			class:"OneToOne",
			line:"1", 
			column:"1", 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL",
			concentration : "nM"
	}
	atmService.experimentToView($scope.experiment);
	
	$scope.atmService = atmService;
	
}]);
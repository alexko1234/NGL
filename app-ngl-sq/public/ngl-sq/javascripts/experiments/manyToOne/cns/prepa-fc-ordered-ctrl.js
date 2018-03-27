angular.module('home').controller('CNSPrepaFlowcellOrderedCtrl',['$scope', '$parse', 'atmToDragNDrop',
                                                               function($scope, $parse, atmToDragNDrop) {
	

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
		        	 "extraHeaders":{0:"Solution Stock"}
		         },	
		         {
		        	 "header":Messages("containers.table.supportCode"),
		        	 "property":"inputContainer.support.code",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"text",
		        	 "position":1,
		        	 "extraHeaders":{0:"Solution Stock"}
		         },	
		         {
		        	 "header":Messages("containers.table.workName"),
		        	 "property":"inputContainer.properties.workName.value",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"text",
		        	 "position":1.2,
		        	 "extraHeaders":{0:"Solution Stock"}
		         },	
		         {
		        	"header":Messages("containers.table.tags"),
		 			"property": "inputContainer.contents",
		 			"filter": "getArray:'properties.tag.value'| unique",
		 			"order":false,
		 			"hide":true,
		 			"type":"text",
		 			"position":2,
		 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
		        	 "extraHeaders":{0:"Solution Stock"}
		         },				         
				 {
		        	 "header":Messages("containers.table.concentration") + " (nM)",
		        	 "property":"inputContainerUsed.concentration.value",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"number",
		        	 "position":5,
		        	 "extraHeaders":{0:"Solution Stock"}
		         },
		        
		         {
		        	 "header":Messages("containers.table.volume") + " (µL)",
		        	 "property":"inputContainerUsed.volume.value",
		        	 "order":true,
					 "edit":false,
					 "hide":true,
		        	 "type":"number",
		        	 "position":6,
		        	 "extraHeaders":{0:"Solution Stock"}
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
		        	 "extraHeaders":{0:"Solution Stock"}
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
	
	
	var defaultValues = {
			"4000":{
				"inputContainerUsed.experimentProperties.inputVolume2.value":5,
				"inputContainerUsed.experimentProperties.NaOHVolume.value":5,
				"inputContainerUsed.experimentProperties.NaOHConcentration.value":"0.1N",
				"inputContainerUsed.experimentProperties.trisHCLVolume.value":5,
				"inputContainerUsed.experimentProperties.trisHCLConcentration.value":200, 
				"inputContainerUsed.experimentProperties.masterEPXVolume.value":35
			},
			"NovaSeq S2 / onboard":{
				"inputContainerUsed.experimentProperties.inputVolume2.value":150,
				"inputContainerUsed.experimentProperties.NaOHVolume.value":37,
				"inputContainerUsed.experimentProperties.NaOHConcentration.value":"0.2N",
				"inputContainerUsed.experimentProperties.trisHCLVolume.value":38,
				"inputContainerUsed.experimentProperties.trisHCLConcentration.value":400, 
				"inputContainerUsed.experimentProperties.masterEPXVolume.value":525
			},
			"NovaSeq S2 / XP FC":{
				"inputContainerUsed.experimentProperties.inputVolume2.value":22,
				"inputContainerUsed.experimentProperties.NaOHVolume.value":5,
				"inputContainerUsed.experimentProperties.NaOHConcentration.value":"0.2N",
				"inputContainerUsed.experimentProperties.trisHCLVolume.value":6,
				"inputContainerUsed.experimentProperties.trisHCLConcentration.value":400, 
				"inputContainerUsed.experimentProperties.masterEPXVolume.value":77
			},
			"NovaSeq S4 / onboard":{
				"inputContainerUsed.experimentProperties.inputVolume2.value":310,
				"inputContainerUsed.experimentProperties.NaOHVolume.value":77,
				"inputContainerUsed.experimentProperties.NaOHConcentration.value":"0.2N",
				"inputContainerUsed.experimentProperties.trisHCLVolume.value":78,
				"inputContainerUsed.experimentProperties.trisHCLConcentration.value":400, 
				"inputContainerUsed.experimentProperties.masterEPXVolume.value":1085				
			},
			"NovaSeq S4 / XP FC":{
				"inputContainerUsed.experimentProperties.inputVolume2.value":30,
				"inputContainerUsed.experimentProperties.NaOHVolume.value":7,
				"inputContainerUsed.experimentProperties.NaOHConcentration.value":"0.2N",
				"inputContainerUsed.experimentProperties.trisHCLVolume.value":8,
				"inputContainerUsed.experimentProperties.trisHCLConcentration.value":400, 
				"inputContainerUsed.experimentProperties.masterEPXVolume.value":105				
			}
	};
	
	var getDefaultValueForWorkSheet = function(line, col){
		//inputVolume2
		//NaOHVolume
		//NaOHConcentration
		//trisHCLVolume
		//trisHCLConcentration 
		//masterEPXVolume

		var worksheet = $parse("experimentProperties.worksheet.value")($scope.experiment);
		
		if(worksheet && defaultValues[worksheet][col.property]){
			return defaultValues[worksheet][col.property];
		}
		return undefined;
		
	}
	
	//overide defaut method
	atmToSingleDatatable.convertOutputPropertiesToDatatableColumn = function(property){
		return   this.$commonATM.convertSinglePropertyToDatatableColumn(property,"outputContainerUsed.experimentProperties.",{"0":"prep FC"});
		
	};
	atmToSingleDatatable.convertInputPropertiesToDatatableColumn = function(property){
		if(property.displayOrder < 20){		
			return   this.$commonATM.convertSinglePropertyToDatatableColumn(property,"inputContainerUsed.experimentProperties.",{"0":"Dilution"});
		}else if(property.displayOrder < 30){
			var column = this.$commonATM.convertSinglePropertyToDatatableColumn(property,"inputContainerUsed.experimentProperties.",{"0":"Dénaturation - neutralisation"});
			column.defaultValues = getDefaultValueForWorkSheet;
			return column;
		}else if(property.displayOrder < 50){
			return   this.$commonATM.convertSinglePropertyToDatatableColumn(property,"inputContainerUsed.experimentProperties.",{"0":"prep FC"});
		}
	};
	
		
	atmToSingleDatatable.data.setColumnsConfig(columns);
	atmToSingleDatatable.addExperimentPropertiesToDatatable($scope.experimentType.propertiesDefinitions);
	
	$scope.$parent.changeValueOnFlowcellDesign = function(){
		$scope.atmService.data.updateDatatable();
		
		if($scope.mainService.isEditMode() && !$scope.isCreationMode()){
			$scope.messages.clazz = "alert alert-warning";
			$scope.messages.text = "Vous venez de modifier une valeur";
			$scope.messages.text += ", vous devez impérativement cliquer sur sauvegarder pour que les calculs de la FDR se remettent à jour.";
			$scope.messages.showDetails = false;
			$scope.messages.open();
		}
	};
	
}]);

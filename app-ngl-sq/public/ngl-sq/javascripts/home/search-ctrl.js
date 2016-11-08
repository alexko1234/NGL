"use strict";

angular.module('home').controller('SearchCtrl', ['$scope', 'datatable' , function($scope, datatable) {

	var datatableExperimentConfig = {
			order :{by:'traceInformation.creationDate', reverse:true, mode:'local'},
			search:{
				url:jsRoutes.controllers.experiments.api.Experiments.list()
			},
			pagination:{
				mode:'local',
				numberRecordsPerPage:5
			},
			select:{
				active:false
			},
			showTotalNumberRecords:false,
			columns : [
			           {  	property:"code",
					    	header: "Code",
					    	type :"text",
					    	"position":1,
					    	order:true
						},
						{
							"header":Messages("experiments.intrument"),
							"property":"instrument.code",
							"order":true,
							"hide":true,
							"position":2,
							"type":"text",
							"filter":"codes:'instrument'"
						},		
						{
							"header":Messages("experiments.table.typeCode"),
							"property":"typeCode",
							"filter":"codes:'type'",
							"order":true,
							"hide":true,
							"position":4,
							"type":"text"
						},
						{
							"header":Messages("experiments.table.state.code"),
							"property":"state.code",
							"order":true,
							"type":"text",
							"position":5,
							"hide":true,
							"filter":"codes:'state'"
						},
						{
							"header":Messages("containers.table.sampleCodes"),
							"property":"sampleCodes",
							"order":false,
							"hide":true,
							"position":9,
							"type":"text",
							"render":"<div list-resize='value.data.sampleCodes | unique' list-resize-min-size='3'>",
						},
						{
							"header":Messages("experiments.table.projectCodes"),
							"property":"projectCodes",
							"order":false,
							"render":"<div list-resize='value.data.projectCodes | unique' list-resize-min-size='3'>",
							"hide":true,
							"position":10,
							"type":"text"
						},
						{
							"header":Messages("experiments.table.creationDate"),
							"property":"traceInformation.creationDate",
							"order":true,
							"hide":true,
							"position":11,
							"type":"date"
						},
						{
							"header":Messages("experiments.table.createUser"),
							"property":"traceInformation.createUser",
							"order":true,
							"hide":true,
							"position":12,
							"type":"text"
						}
						]
	};
	
	var datatableProcessIPConfig = {
			order :{by:'traceInformation.creationDate', reverse:true, mode:'local'},			
			search:{
				url:jsRoutes.controllers.processes.api.Processes.list()
			},
			pagination:{
				mode:'local',
				numberRecordsPerPage:5
			},
			select:{
				active:false
			},
			showTotalNumberRecords:false,
			columns : [
			           {
						"header":Messages("processes.table.inputContainerCode"),
						"property":"inputContainerCode",
						"position":1,
						"type":"text"
						},
			           {
							"header":Messages("processes.table.projectCode"),
							"property":"projectCodes",
							"position":2,
							"type":"text"
						},
						{
							"header":Messages("processes.table.sampleCode"),
							"property":"sampleCodes",
							"position":3,
							"type":"text"
						},
			          
			           {
							"header":Messages("processes.table.typeCode"),
							"property":"typeCode",
							"position":4,
							"filter":"codes:'type'",
							"type":"text"         
						} ,
						{
							"header" : Messages("processes.table.currentExperimentTypeCode"),
							"property" : "currentExperimentTypeCode",
							"filter" : "codes:'type'",
							"position" : 5,
							"type" : "text"
						},
						{
							"header" : Messages("processes.table.outputContainerSupportCodes"),
							"property" : "outputContainerSupportCodes",
							"position" : 6,
							"filter":"unique",
							"render" : "<div list-resize='cellValue' list-resize-min-size='2'>",
							"type" : "text"
						},
						{
							"header" : Messages("processes.table.creationDate"),
							"property" : "traceInformation.creationDate",
							"position" : 7,
							"format" : Messages("datetime.format"),
							"type" : "date"
						},
						{
							"header" : Messages("processes.table.createUser"),
							"property" : "traceInformation.createUser",
							"position" : 8,
							"type" : "text"
						},
						{
							"header" : Messages("processes.table.comments"),
							"property" : "comments[0].comment",
							"position" : 9,
							"type" : "text"
						}
						
						]
	};  
	
	var datatableProcessNConfig = {
			order :{by:'traceInformation.creationDate', reverse:true, mode:'local'},
			search:{
				url:jsRoutes.controllers.processes.api.Processes.list()
			},
			pagination:{
				mode:'local',
				numberRecordsPerPage:5
			},
			select:{
				active:false
			},
			showTotalNumberRecords:false,
			columns : [
			           {
						"header":Messages("processes.table.inputContainerCode"),
						"property":"inputContainerCode",
						"position":1,
						"type":"text"
						},
			           {
							"header":Messages("processes.table.projectCode"),
							"property":"projectCodes",
							"position":2,
							"type":"text"
						},
						{
							"header":Messages("processes.table.sampleCode"),
							"property":"sampleCodes",
							"position":3,
							"type":"text"
						},
			          
			           {
							"header":Messages("processes.table.typeCode"),
							"property":"typeCode",
							"position":4,
							"filter":"codes:'type'",
							"type":"text"         
						} ,
						
						{
							"header" : Messages("processes.table.creationDate"),
							"property" : "traceInformation.creationDate",
							"position" : 7,
							"format" : Messages("datetime.format"),
							"type" : "date"
						},
						{
							"header" : Messages("processes.table.createUser"),
							"property" : "traceInformation.createUser",
							"position" : 8,
							"type" : "text"
						},
						{
							"header" : Messages("processes.table.comments"),
							"property" : "comments[0].comment",
							"position" : 9,
							"type" : "text"
						}
						
						]
	};  
		$scope.experimentIPDatatable = datatable(datatableExperimentConfig);			
		$scope.experimentIPDatatable.search({stateCodes:["IP"]});
		
		$scope.experimentNDatatable = datatable(datatableExperimentConfig);			
		$scope.experimentNDatatable.search({stateCodes:["N"]});
		
		$scope.processIPDatatable = datatable(datatableProcessIPConfig);			
		$scope.processIPDatatable.search({stateCodes:["IP"]});
		
		$scope.processNDatatable = datatable(datatableProcessNConfig);			
		$scope.processNDatatable.search({stateCodes:["N"]});
}]);



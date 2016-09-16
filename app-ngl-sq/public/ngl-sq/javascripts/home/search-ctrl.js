"use strict";

angular.module('home').controller('SearchCtrl', ['$scope', 'datatable' , function($scope, datatable) {

	var datatableConfig = {
			order :{by:'traceInformation.creationDate', reverse:false, mode:'remote'},
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
	var datatableProcessConfigN = {
			order :{by:'traceInformation.creationDate', reverse:false, mode:'remote'},
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
			           {  	property:"code",
					    	header: "Code",
					    	type :"text",
					    	"position":1,
					    	order:true
			           },
			           {
							"header":Messages("processes.table.typeCode"),
							"property":"typeCode",
							"order":true,
							"hide":true,
							"position":2,
							"filter":"codes:'type'",
							"type":"text"         
						} ,
						 {
							"header":Messages("processes.table.category"),
							"property":"categoryCode",
							"order":true,
							"hide":true,
							"position":2.5,
							"filter":"codes:'type'",
							"type":"text"         
						} ,
			           {
							"header":Messages("processes.table.projectCode"),
							"property":"projectCode",
							"order":true,
							"hide":true,
							"position":3,
							"type":"text"
						},
						{
							"header":Messages("processes.table.sampleCode"),
							"property":"sampleCode",
							"order":true,
							"hide":true,
							"position":4,
							"type":"text"
							},
						{
							"header":Messages("processes.table.inputContainerCode"),
							"property":"inputContainerCode",
							"order":true,
							"hide":true,								
							"position":5,
							"type":"text"
							}
						]
	};  
	var datatableProcessConfigIP = {
			order :{by:'traceInformation.creationDate', reverse:false, mode:'remote'},
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
			           {  	property:"code",
					    	header: "Code",
					    	type :"text",
					    	"position":1,
					    	order:true
			           },
			           {
							"header":Messages("processes.table.typeCode"),
							"property":"typeCode",
							"order":true,
							"hide":true,
							"position":2,
							"filter":"codes:'type'",
							"type":"text"         
						} ,
						 {
							"header":Messages("processes.table.category"),
							"property":"categoryCode",
							"order":true,
							"hide":true,
							"position":2.5,
							"filter":"codes:'type'",
							"type":"text"         
						} ,
			           {
							"header":Messages("processes.table.projectCode"),
							"property":"projectCode",
							"order":true,
							"hide":true,
							"position":3,
							"type":"text"
						},
						 {
							"header":Messages("processes.table.experimentCodes"),
							"property":"experimentCodes",
							"order":true,
							"hide":true,
							"render":"<div list-resize='value.data.experimentCodes | unique' list-resize-min-size='1'>",
							"position":4,
							"type":"text"
						},
						{
							"header":Messages("processes.table.sampleCode"),
							"property":"sampleCode",
							"order":true,
							"hide":true,
							"position":5,
							"type":"text"
						},
						{
							"header":Messages("processes.table.inputContainerCode"),
							"property":"inputContainerCode",
							"order":true,
							"hide":true,
							"position":6,
							"type":"text"
						},
						{
							"header":Messages("processes.table.currentExperimentTypeCode"),
							"property":"currentExperimentTypeCode",
							"order":true,
							"hide":true,
							"position":7,
							"filter":"codes:'type'",
							"type":"text"
						}
						
						]
	};  
		$scope.experimentIPDatatable = datatable(datatableConfig);			
		$scope.experimentIPDatatable.search({stateCodes:["IP"]});
		
		$scope.experimentNDatatable = datatable(datatableConfig);			
		$scope.experimentNDatatable.search({stateCodes:["N"]});
		
		$scope.processIPDatatable = datatable(datatableProcessConfigIP);			
		$scope.processIPDatatable.search({stateCodes:["IP"]});
		
		$scope.processNDatatable = datatable(datatableProcessConfigN);			
		$scope.processNDatatable.search({stateCodes:["N"]});
}]);



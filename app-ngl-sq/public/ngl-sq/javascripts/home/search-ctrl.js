"use strict";

angular.module('home').controller('SearchCtrl', ['$scope', 'datatable' , function($scope, datatable) {

	var datatableConfig = {
			order :{by:'traceInformation.creationDate', reverse:true, mode:'remote'},
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
	
	
	
		$scope.experimentDatatable = datatable(datatableConfig);			
		$scope.experimentDatatable.search({stateCodes:["IP"]});
}]);



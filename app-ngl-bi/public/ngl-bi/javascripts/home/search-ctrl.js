"use strict";

angular.module('home').controller('SearchCtrl', ['$scope', '$http','$httpParamSerializer', 'datatable' , function($scope, $http,$httpParamSerializer,datatable) {

	var datatableConfig = {
			order :{by:'sequencingStartDate', reverse:true, mode:'remote'},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			pagination:{
				mode:'local',
				numberRecordsPerPage:5,
				numberRecordsPerPageList: [{number:5, clazz:''},{number:10, clazz:''},{number:25, clazz:''},{number:50, clazz:''},{number:100, clazz:''}]
			},
			select:{
				active:false
			},
			showTotalNumberRecords:false,
			columns : [
			           {  	"property":"code",
			        	   	"header":Messages("runs.code"),
			        	   	"type":"text",
			        	   	"order":true
						},
						{	"property":"typeCode",
							"filter":"codes:'type'",
							"header":Messages("runs.typeCode"),
							"type":"text",
							"order":true
						},
						{	"property":"sequencingStartDate",
							"header":Messages("runs.sequencingStartDate"),
							"type":"date",
							"order":true
						},
						{	"property":"state.code",
							"filter":"codes:'state'",
							"header":Messages("runs.stateCode"),
							"type":"text",
							"order":true								
						},
						{	"property":"valuation.valid",
							"filter":"codes:'valuation'",
							"header":Messages("runs.valuation.valid"),
							"type":"text",
							"order":true
						} 
						]
	};
	
	
	
	$scope.init = function(){
		$scope.runsIPS = datatable(datatableConfig);			
		$scope.runsIPS.search({stateCodes:["IP-S"], excludes:["lanes","treatments"]});
		$scope.runsIPRG = datatable(datatableConfig);			
		$scope.runsIPRG.search({stateCodes:["IP-RG"], excludes:["lanes","treatments"]});
		$scope.runsIWV_IPV = datatable(datatableConfig);			
		$scope.runsIWV_IPV.search({stateCodes:["IW-V","IP-V"], excludes:["lanes","treatments"]});	
		$scope.runsKeep = datatable(datatableConfig);			
		$scope.runsKeep.search({keep:true});	
		
		
		
		//Init list runCodes
		var form = {includes : []};
		form.includes.push("runCode");
		form.productionValidCode="UNSET";
		form.typeCode="rsillumina";
		//filter for CNG
		form.fromDate=moment("13/12/2013", Messages("date.format").toUpperCase()).valueOf();
		$http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url,{params:form}).then(function(result){
			$scope.runCodes =result.data.map(function(readSet){return readSet.runCode;}).filter((value, index, self) => self.indexOf(value) === index);
			
			var datatableConfigRunsNoValid = datatableConfig;
			datatableConfigRunsNoValid.order={by:'sequencingStartDate', reverse:false, mode:'remote'},
			$scope.runsNoValid = datatable(datatableConfig);			
			$scope.runsNoValid.search({codes:$scope.runCodes});
			$scope.runCodesUrl = $httpParamSerializer({codes:$scope.runCodes});
		});
	}
		
}]);



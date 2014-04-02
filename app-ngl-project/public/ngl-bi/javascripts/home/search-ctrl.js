"use strict";

function SearchCtrl($scope, datatable) {

	var datatableConfig = {
			order :{by:'sequencingStartDate', reverse:true, mode:'local'},
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
			           {  	property:"code",
					    	header: "runs.code",
					    	type :"text",
					    	order:true
						},
						{	property:"typeCode",
							header: "runs.typeCode",
							type :"text",
					    	order:true
						},
						{	property:"sequencingStartDate",
							header: "runs.sequencingStartDate",
							type :"date",
					    	order:true
						},
						{	property:"state.code",
							filter:"codes:'state'",
							header: "runs.stateCode",
							type :"text",
							order:true								
						},
						{	property:"valuation.valid",
							filter:"codes:'valuation'",
							header: "runs.valuation.valid",
							type :"text",
					    	order:true
						} 
						]
	};
	
	
	
	$scope.init = function(){
		$scope.runsIPS = datatable($scope, datatableConfig);			
		$scope.runsIPS.search({stateCodes:["IP-S"], excludes:["lanes","treatments"]});
		$scope.runsIPRG = datatable($scope, datatableConfig);			
		$scope.runsIPRG.search({stateCodes:["IP-RG"], excludes:["lanes","treatments"]});
		$scope.runsIWV_IPV = datatable($scope, datatableConfig);			
		$scope.runsIWV_IPV.search({stateCodes:["IW-V","IP-V"], excludes:["lanes","treatments"]});	
		$scope.runsKeep = datatable($scope, datatableConfig);			
		$scope.runsKeep.search({keep:true});	
	}	
};

SearchCtrl.$inject = ['$scope', 'datatable'];


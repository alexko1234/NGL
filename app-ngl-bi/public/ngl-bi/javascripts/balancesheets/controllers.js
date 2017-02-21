"use strict";

angular.module('home').controller('BalanceSheetsGeneralCtrl', ['$scope', '$http', 'mainService', 'tabService', 'datatable', 'balanceSheetsGeneralSrv', '$routeParams',
                                                               function($scope, $http, mainService, tabService, datatable, balanceSheetsGeneralSrv, $routeParams){
	
	 var configYearlyDT = {
		name:'yearlyDT',
		group : {
			active : false
		},
		search : {
			active:false
		},
		pagination:{
			active : false
		},
		hide:{
			active:false
		},
		select : {
			active : false
		},
		columns : [
		    {"property":"year",
			 "header": Messages("balanceSheets.year"),
			 "type" :"text",
			 "position":1
		   	},
		   	{"property":"nbBases",
			"header": Messages("balanceSheets.nbBases"),
			"type" :"number",
		  	"position":2
		    }
		  ]
	};
	var configYearlySumDT = {
		name : 'yearlySumDT',
		showTotalNumberRecords : false,
		search : {
			active:false
		},
		pagination:{
			active : false
		},
		hide:{
			active:false
		},
		select : {
			active : false
		},
		callbackEndDisplayResult : function(){
			 colorBlue($scope.dtSumYearlyBalanceSheets, 0);	
		},
		columns : [
		    {"property":"property",
		    "header": Messages("balanceSheets.property"),
		    "type" :"text",
		    "position":1
		    },
		    {"property":"value",
		    "header": Messages("balanceSheets.value"),
		    "type" :"number",
		    "position":2,
		    }
		]
	};
	
	var colorBlue = function(datatable, pos){
		 datatable.displayResult[pos].line.trClass="text-primary";
	}
	
	
	$scope.balanceSheetsGeneralService = balanceSheetsGeneralSrv;
	
	mainService.put('activeYear', 'general');
	
	
	$scope.isLoading = function(){
		return $scope.loading;
	};
	
	var init = function(){
		// Tabs
		var actualYear = new Date().getFullYear();
		tabService.addTabs({label:Messages("balanceSheets.tab.generalBalanceSheets"), href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home($routeParams.typeCode, "general").url});
		for(var i = actualYear; i >= 2008 ; i--){
			tabService.addTabs({label:Messages("balanceSheets.tab.year") +" "+ i,href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home($routeParams.typeCode, i).url});
		}
		
		tabService.activeTab(0);
		
		$scope.loading=true;
		
		//loadData();
		var form = {};
		form.includes = [];
		form.includes.push("default");
		form.includes.push("runSequencingStartDate");
		form.includes.push("typeCode");
		form.typeCode=$routeParams.typeCode;
		//For rsillumina
		form.includes.push("treatments.ngsrg.default.nbBases");
		//for rsnanopore
		form.includes.push("treatments.ngsrg.default.1DReverse");
		form.includes.push("treatments.ngsrg.default.1DForward");
		form.limit = 100000;
		
		$http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url, {params : form}).success(function(data, status, headers, config) {
			var dataByYear = $scope.balanceSheetsGeneralService.computeDataByYear(data);
			
			$scope.dtYearlyBalanceSheets = datatable(configYearlyDT);
			$scope.dtYearlyBalanceSheets.setData(dataByYear, dataByYear.length);
			
			var sumData = $scope.balanceSheetsGeneralService.computeSumData(dataByYear);
			$scope.dtSumYearlyBalanceSheets = datatable(configYearlySumDT);
			$scope.dtSumYearlyBalanceSheets.setData(sumData, 1);
			
			$scope.chartYearlyBalanceSheets = $scope.balanceSheetsGeneralService.computeChartYearlyBalanceSheets(dataByYear);
			
			$scope.loading = false;
		});
	};
	init();
	
	//TODO generalBanlanceSheets+typeCode in mainService
	/*if(!angular.isUndefined(mainService.get('generalBalanceSheets'))){
		//$scope.balanceSheetsGeneralService.loadFromCache();
	}else{
		$scope.balanceSheetsGeneralService.init($routeParams.typeCode,datatableConfigYearlySum,datatableConfigYearly);
	}*/
	
	
	
	

	
}]);

angular.module('home').controller('BalanceSheetsYearCtrl', ['$scope', '$http','mainService', 'tabService', 'datatable', 'balanceSheetsGeneralSrv', '$routeParams', 
                                                    function($scope, $http, mainService, tabService, datatable, balanceSheetsGeneralSrv, $routeParams){
	
	
	 var configQuarterDT = {
		name:'quarterDT',
		showTotalNumberRecords : false,
		search : {
			active:false
		},
		pagination:{
			active : false
		},
		hide:{
			active:false
		},
		select : {
			active : false
		},
		callbackEndDisplayResult : function(){
			 for(var i = 0; i < linesToColor.length; i++){
				 colorBlue($scope.dtQuarters, linesToColor[i]);
			 }
		},
		columns : [
			{	"property":"quarter",
				"header":Messages("balanceSheets.quarters"),
			  	"type":"text",
			  	"position":1
			},
			{	"property":"month",
				"header":Messages("balanceSheets.monthRun"),
				"type":"text",
				"position":2
			},
			{
				"property":"nbBases",
				"header":Messages("balanceSheets.nbBases"),
				"type":"number",
				"position":3
			}
		]
	 };
	
	 var colorBlue = function(datatable, pos){
		 datatable.displayResult[pos].line.trClass="text-primary";
	}
	 
	// Service
	$scope.balanceSheetsGeneralService = balanceSheetsGeneralSrv;
	
	$scope.isLoading = function(){
		return $scope.loading;
	};
	
	var actualYear = new Date().getFullYear();
	
	/*if(mainService.get('activeYear') == undefined){
		mainService.put('activeYear', actualYear);
	}*/
	
	var init = function(){
		$scope.loading=true;
		// Year managing
		var actualYear = new Date().getFullYear();
		var activeYear = $routeParams.year;
		var changeYear = true;

		// Tabs
		tabService.addTabs({label:Messages("balanceSheets.tab.generalBalanceSheets"), href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home($routeParams.typeCode, "general").url});
		for(var i = actualYear; i >= 2008 ; i--){
			tabService.addTabs({label:Messages("balanceSheets.tab.year") +" "+ i,href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home($routeParams.typeCode, i).url});
		}
		// Activate the tab corresponding to the selected year
		tabService.activeTab(actualYear - activeYear + 1);
		
		var form = {includes : [], typeCodes : []};
		 form.includes.push("default");
		 //For rsillumina
		 form.includes.push("treatments.ngsrg.default.nbBases");
		 //for rsnanopore
		 form.includes.push("treatments.ngsrg.default.1DReverse");
		 form.includes.push("treatments.ngsrg.default.1DForward");
		 form.includes.push("projectCode");
		 form.includes.push("runTypeCode");
		 form.includes.push("runSequencingStartDate");
		 form.includes.push("sampleOnContainer.sampleTypeCode");
		 form.includes.push("sampleOnContainer.sampleCategoryCode");
		 form.fromDate = moment("01/01/"+activeYear, Messages("date.format").toUpperCase()).valueOf();
		 form.toDate = moment("31/12/"+activeYear, Messages("date.format").toUpperCase()).valueOf();
		 form.typeCode=$routeParams.typeCode;
		 form.limit = 20000;
		 
		/* var runForm = {includes : []};
		 runForm.includes.push("instrumentUsed.typeCode");
		 runForm.includes.push("sequencingStartDate");
		 runForm.includes.push("typeCode");
		 runForm.fromDate = moment("01/01/"+activeYear, Messages("date.format").toUpperCase()).valueOf();
		 runForm.toDate = moment("31/12/"+activeYear, Messages("date.format").toUpperCase()).valueOf();
		 
		 var projectForm = {includes : []};
		 projectForm.includes.push("code");
		 projectForm.includes.push("name");
		 projectForm.includes.push("traceInformation.creationDate");*/
		
		 $http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url, {params : form})
		 	.success(function(data, status, headers, config) {
		 		var dataForYear = $scope.balanceSheetsGeneralService.computeDataForYear(data,activeYear);
		 		$scope.dtQuarters = datatable(configQuarterDT);
				$scope.dtQuarters.setData(dataForYear.dataQuarterDT, dataForYear.dataQuarterDT.length);
		 		$scope.loading=false;
		 });
		 
		
		
	}
	init();
	
	
	
	// Keeping the active balance sheet opened after we change year
	/*$scope.tabs = [true, false, false, false];
	$scope.setActiveTab = function(value){
		mainService.put('balanceSheetsActiveTab', value);
		for(var i = 0; i < $scope.tabs.length; i++){
			if(i == value){
				$scope.tabs[i] = true;
			}else{
				$scope.tabs[i] = false;
			}
		}
		
		// Detect if the active year has changed
		if(mainService.get('activeYear') == activeYear){
			changeYear = false;
		}else{
			mainService.put('activeYear', activeYear);
		}
		
		
		// Init the balance sheet
		$scope.balanceSheetsService.init(changeYear, activeYear);
	};*/
	
	
	// The first tab will always be the active one when consulting the balance sheet for the first time

	/*if(mainService.get('balanceSheetsActiveTab') == undefined){
		mainService.put('balanceSheetsActiveTab', "0");
	}
	$scope.setActiveTab(mainService.get('balanceSheetsActiveTab'));*/
	
	
}]);



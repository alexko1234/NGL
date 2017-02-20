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
			 colorBlue(dtSumYearly, 0);	
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
	
	
	$scope.balanceSheetsGeneralService = balanceSheetsGeneralSrv;
	
	mainService.put('activeYear', 'general');
	
	// Year managing
	var actualYear = new Date().getFullYear();
	
	$scope.isLoading = function(){
		return $scope.loading;
	};
	
	var init = function(){
		// Tabs
		tabService.addTabs({label:Messages("balanceSheets.tab.generalBalanceSheets"), href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home($routeParams.typeCode).url});
		for(var i = actualYear; i >= 2008 ; i--){
			tabService.addTabs({label:Messages("balanceSheets.tab.year") +" "+ i,href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home(i).url});
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

angular.module('home').controller('BalanceSheetsYearCtrl', ['$scope', 'mainService', 'tabService', 'balanceSheetsSrv', '$routeParams', 
                                                    function($scope, mainService, tabService, balanceSheetsSrv, $routeParams){
	
	
	// Service
	$scope.balanceSheetsService = balanceSheetsSrv;
	
	
	// Year managing
	var actualYear = new Date().getFullYear();
	var activeYear = $routeParams.year;
	var changeYear = true;

	if(mainService.get('activeYear') == undefined){
		mainService.put('activeYear', actualYear);
	}
	
	// Tabs
	tabService.addTabs({label:Messages("balanceSheets.tab.generalBalanceSheets"), href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home("general").url});
	for(var i = actualYear; i >= 2008 ; i--){
		tabService.addTabs({label:Messages("balanceSheets.tab.year") +" "+ i,href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home(i).url});
	}
	// Activate the tab corresponding to the selected year
	tabService.activeTab(actualYear - activeYear + 1);
	
	
	
	// Keeping the active balance sheet opened after we change year
	$scope.tabs = [true, false, false, false];
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
	};
	
	
	// The first tab will always be the active one when consulting the balance sheet for the first time

	if(mainService.get('balanceSheetsActiveTab') == undefined){
		mainService.put('balanceSheetsActiveTab', "0");
	}
	$scope.setActiveTab(mainService.get('balanceSheetsActiveTab'));
	
	
}]);



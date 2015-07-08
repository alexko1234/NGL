"use strict";

angular.module('home').controller('BalanceSheetsGeneralCtrl', ['$scope', 'mainService', 'tabService', 'balanceSheetsGeneralSrv', '$routeParams',
                                                               function($scope, mainService, tabService, balanceSheetsGeneralSrv, $routeParams){
	$scope.balanceSheetsGeneralService = balanceSheetsGeneralSrv;
	
	mainService.put('activeYear', 'general');
	
	// Year managing
	var actualYear = new Date().getFullYear();
	
	// Tabs
	tabService.addTabs({label:Messages("balanceSheets.tab.generalBalanceSheets"), href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home("general").url});
	for(var i = actualYear; i >= 2008 ; i--){
		tabService.addTabs({label:Messages("balanceSheets.tab.year") +" "+ i,href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home(i).url});
	}
	
	tabService.activeTab(0);
	
	
	if(!angular.isUndefined(mainService.get('generalBalanceSheets'))){
		$scope.balanceSheetsGeneralService.loadFromCache();
	}else{
		$scope.balanceSheetsGeneralService.init();
	}

	
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
		mainService.put('activeYear', activeYear);
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



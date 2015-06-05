"use strict";

angular.module('home').controller('BalanceSheetsYearCtrl', ['$scope', 'mainService', 'tabService', 'balanceSheetsSrv', '$routeParams', 
                                                    function($scope, mainService, tabService, balanceSheetsSrv, $routeParams){
	
	
	
	
	// Year managing
	var actualYear = new Date().getFullYear();
	
	// Tabs
	tabService.addTabs({label:Messages("balanceSheets.tab.generalBalanceSheets"), href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.general().url});
	for(var i = actualYear; i >= 2008 ; i--){
		tabService.addTabs({label:Messages("balanceSheets.tab.year") +" "+ i,href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home(i).url});
	}
	
	var activeYear = $routeParams.year;
	tabService.activeTab(actualYear - activeYear + 1);
	
	$scope.balanceSheetsService = balanceSheetsSrv;
	$scope.balanceSheetsService.init(activeYear);
	
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
		if(mainService.get('balanceSheetsActiveTab') == 0) $scope.balanceSheetsService.showQuarters();
		else if(mainService.get('balanceSheetsActiveTab') == 1) $scope.balanceSheetsService.showSequencingProduction();
		else if(mainService.get('balanceSheetsActiveTab') == 2) $scope.balanceSheetsService.showFirstTen();
		else if(mainService.get('balanceSheetsActiveTab') == 3) $scope.balanceSheetsService.showProjectType();
	};
	
	$scope.getTabClass = function(value){
		 if(value === mainService.get('balanceSheetsActiveTab')){
			 return true;
		 }
	};
	
	if(mainService.get('balanceSheetsActiveTab') != undefined){
		$scope.setActiveTab(mainService.get('balanceSheetsActiveTab'));
	}
	
	if(mainService.get('balanceSheetsActiveTab') == undefined){
		$scope.setActiveTab(mainService.get('balanceSheetsActiveTab'));
	}
	
	
	
}]);

angular.module('home').controller('BalanceSheetsGeneralCtrl', ['$scope', 'mainService', 'tabService', 'balanceSheetsGeneralSrv', '$routeParams',
                                                               function($scope, mainService, tabService, balanceSheetsGeneralSrv, $routeParams){
	// Year managing
	var actualYear = new Date().getFullYear();
	
	// Tabs
	tabService.addTabs({label:"Bilan général", href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.general().url});
	for(var i = actualYear; i >= 2008 ; i--){
		tabService.addTabs({label:"Année " + i,href:jsRoutes.controllers.balancesheets.tpl.BalanceSheets.home(i).url});
	}
	
	tabService.activeTab(0);
	
	$scope.balanceSheetsGeneralService = balanceSheetsGeneralSrv;
	$scope.balanceSheetsGeneralService.init();
	
}]);

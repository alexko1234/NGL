"use strict";

angular.module('home').controller('SearchCtrl',[ '$scope', '$routeParams', 'datatable', 'mainService', 'tabService', 'readSetsSearchService', 'valuationService',
                                                 function($scope, $routeParams, datatable, mainService, tabService, readSetsSearchService, valuationService) { 
    
	var datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.get(line.code).url,remove:true});
				}
			},
			hide:{
				active:true
			}
	};

	$scope.search = function(){
		$scope.searchService.search($scope.datatable);
	};
	
	$scope.reset = function(){
		$scope.searchService.reset();
	};
	
	$scope.updateColumn = function(){
		$scope.searchService.updateColumn($scope.datatable);
	};
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("search").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.searchService = readSetsSearchService();	
	$scope.searchService.setRouteParams($routeParams);
	$scope.valuationService = valuationService();
		
	//to avoid to lost the previous search
	if(angular.isUndefined(mainService.getDatatable())){
		$scope.datatable = datatable($scope, datatableConfig);
		mainService.setDatatable($scope.datatable);
		$scope.datatable.setColumnsConfig($scope.searchService.getColumns());		
	}else{
		$scope.datatable = mainService.getDatatable();			
	}		
	$scope.search();	
}]);




angular.module('home').controller('SearchValuationCtrl', ['$scope', '$routeParams', '$parse', 'datatable', 'mainService', 'tabService', 'readSetsSearchService', 'valuationService', 
                                                          function($scope, $routeParams, $parse, datatable, mainService, tabService, readSetsSearchService, valuationService) {
	var datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},			
			save : {
				active:true,
				url: jsRoutes.controllers.readsets.api.ReadSets.valuationBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code,productionValuation:line.productionValuation,bioinformaticValuation:line.bioinformaticValuation};}				
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.valuation(line.code).url,remove:true});
				}
			},
			hide:{
				active:true
			},
			messages : {active:true}
	};
	
	$scope.search = function(){
		$scope.searchService.search($scope.datatable);
	};
	
	$scope.reset = function(){
		$scope.searchService.reset();
	};
	
	$scope.updateColumn = function(){
		$scope.searchService.updateColumn($scope.datatable);
	};
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('valuation');
		tabService.addTabs({label:Messages('readsets.page.tab.validate'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("valuation").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.searchService = readSetsSearchService();		
	$scope.searchService.setRouteParams($routeParams);
	$scope.valuationService = valuationService();
	//to avoid to lost the previous search
	if(angular.isUndefined(mainService.getDatatable())){
		$scope.datatable = datatable($scope, datatableConfig);
		mainService.setDatatable($scope.datatable);
		$scope.datatable.setColumnsConfig($scope.searchService.getColumns());			
	}else{
		$scope.datatable = mainService.getDatatable();			
	}		
	$scope.search();
	
}]);


angular.module('home').controller('SearchStateCtrl', ['$scope', '$routeParams', 'datatable', 'mainService', 'tabService', 'readSetsSearchService', 
                                                      function($scope, $routeParams, datatable, mainService, tabService, readSetsSearchService) {

	var datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},
			save : {
				active:true,
				url: jsRoutes.controllers.readsets.api.ReadSets.stateBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code,state:line.state};}				
			},
			show:{
				active:true,
				add :function(line){
					tabService.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.get(line.code).url,remove:true});
				}
			},
			messages : {active:true}
	};
	
	$scope.search = function(){
		$scope.searchService.search($scope.datatable);
	};
	
	$scope.reset = function(){
		$scope.searchService.reset();
	};
	
	$scope.updateColumn = function(){
		$scope.searchService.updateColumn($scope.datatable);
	};
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('state');
		tabService.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("state").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.searchService = readSetsSearchService();		
	$scope.searchService.setRouteParams($routeParams);
	
	//to avoid to lost the previous search
	if(angular.isUndefined(mainService.getDatatable())){
		$scope.datatable = datatable($scope, datatableConfig);
		mainService.setDatatable($scope.datatable);
		$scope.datatable.setColumnsConfig($scope.searchService.getColumns());			
	}else{
		$scope.datatable = mainService.getDatatable();			
	}		
	$scope.search();	
}]);


angular.module('home').controller('SearchBatchCtrl', ['$scope', '$routeParams', 'datatable', 'mainService', 'tabService', 'readSetsSearchService', 
                                                      function($scope, $routeParams, datatable, mainService, tabService, readSetsSearchService) {

	var datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},
			save : {
				active:true,
				url: jsRoutes.controllers.readsets.api.ReadSets.propertiesBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code, properties : line.properties};}				
			},	
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.valuation(line.code).url,remove:true});
				}
			},
			messages : {active:true}
	};
	
	$scope.search = function(){
		$scope.searchService.search($scope.datatable);
	};
	
	$scope.reset = function(){
		$scope.searchService.reset();
	};
	
	$scope.updateColumn = function(){
		$scope.searchService.updateColumn($scope.datatable);
	};
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('batch');
		tabService.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("batch").url,remove:true});
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.searchService = readSetsSearchService();		
	$scope.searchService.setRouteParams($routeParams);
	
	//to avoid to lost the previous search
	if(angular.isUndefined(mainService.getDatatable())){
		$scope.datatable = datatable($scope, datatableConfig);
		mainService.setDatatable($scope.datatable);
		$scope.datatable.setColumnsConfig($scope.searchService.getColumns());			
	}else{
		$scope.datatable = mainService.getDatatable();			
	}		
	$scope.search();	
	
}]);

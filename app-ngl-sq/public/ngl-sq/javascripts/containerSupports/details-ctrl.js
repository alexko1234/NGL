"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'datatable', 'mainService', 'tabService', 'containerSupportsDetailsSearchService',
                                                  function($scope,$http,$routeParams,datatable,mainService,tabService,containerSupportsDetailsSearchService){

	
	/*
	 * ActiveTab System
	 */
	$scope.getTabClass = function(value){
		if(value === mainService.get('containerSupportActiveTab')){
			return 'active';
		}
	}
	$scope.setActiveTab = function(value){
		mainService.put('containerSupportActiveTab', value);
	};
	
	/*
	 * search() Method
	 */
	$scope.search = function(){
		$scope.detailsSearchService.search();
	};
	/*
	 * Get Bootstrap classes for colors
	 */
	$scope.getClass = function(x, y){
		var wells = mainService.getDatatable().displayResult;
		if(angular.isDefined(wells)){
	        for (var i = 0; i <wells.length; i++) {
		         if (wells[i].data.support.column === (x+'') && wells[i].data.support.line===(y+'')) {
		        	 var well = wells[i];
		        	 if(well.data.valuation.valid === "FALSE"){
		        		 return "alert alert-danger hidden-print";
		        	 }else if(well.data.valuation.valid === "TRUE"){
		        		 return "alert alert-success hidden-print";
		        	 }else if(well.data.valuation.valid === "UNSET"){
		        		 return "alert alert-warning hidden-print";
		        	 }
		         }
	        }
		}
        return "hidden-print";
     }
	/*
	 * Display Method for all views 
	 */
	$scope.displayCellAll =function(x, y){
		if(angular.isDefined(mainService.getDatatable().displayResult)){	
			var wells = mainService.getDatatable().displayResult;
			if(angular.isDefined(wells)){
		        for (var i = 0; i <wells.length; i++) {
	        		if (wells[i].data.support.column === (x+'') && wells[i].data.support.line===(y+'')) {
			        	return wells[i].data.code.replace(/_/g,' ');
			        }
		        }
			}
	        return "------";
		}
     }
	
	/*
	 * Set Coordinates $scope.nbCol & $scope.nbLine
	 */
	var setColXLine = function(column, line){
		var azArray = ['A','B','C','D','E','F','G','H','I','J','K','L','M',
		               'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];
		$scope.nbCol = [];
		for(var i=1; i<=column ; i++){
			$scope.nbCol.push(i);
		}
		if($scope.target === "plate"){
			$scope.nbLine = azArray.slice(0,line);
		}else{ // Case when display Method would not work
			$scope.nbLine = [];
			for(var k=1; k<=line ; k++){
				$scope.nbLine.push(k);
			}
		}
	};
	/*
	 * Method filterCategorySupport() config the display... 
	 *  //	=> TODO
	 */
	var filterCategorySupport = function(){
		var categoryCode;
		if(!angular.isUndefined($scope.support.categoryCode)){
			categoryCode = $scope.support.categoryCode;
			if(categoryCode.includes('irys')){
				$scope.target = 'iryschip';
				setColXLine(1,2); 
			}else if(categoryCode.includes('flowcell')){
				$scope.target = 'flowcell';
				for(var i=1; i<=8; i++){
					if(categoryCode.includes(i.toString())){
						setColXLine(1,i);
					}
				}
			}else if(categoryCode.includes('mapcard')){
				$scope.target = 'mapcard';
				setColXLine(1,2);
			}else if(categoryCode.includes('plate')){
				$scope.target = 'plate';
				if(categoryCode.includes('96')){
					setColXLine(12,8);
				}else if(categoryCode.includes('384')){
					setColXLine(24,16);
				}
			}else if(categoryCode.includes('tube')){
				$scope.target = undefined;
			}else{
				$scope.target = 'table';
				//console.warn("Default Case !!! \n....or Maybe a Tube");
			}
		}
		if(!angular.isUndefined($scope.target)){
			$scope.dynamicMessage = Messages("containerSupports.button."+$scope.target); // Build msg for the button
			//$scope.setActiveTab('table'); 
			$scope.setActiveTab($scope.target);
			$scope.search();	// search call
		}
	};
	
	/*
	 * Configuration of the datatable
	 */
	var datatableConfig = {
			pagination:{
				active:false
			},
			search:{
				url:jsRoutes.controllers.containers.api.Containers.list().url
			},
			order:{
				active:true,
				mode:'local'
			},
			cancel:{
				showButton:false
			},
			hide:{
				active:true,
				showButton:false,
				showButtonColumn:true
			},
			exportCSV:{
				active:true,
				showButton:false,
				delimiter:','
			},
			lines:{
				trClass: function(value){
					if(value.valuation.valid === "TRUE"){
						return "success";
					}else if(value.valuation.valid === "FALSE"){
						return "danger";
					}else if(value.valuation.valid === "UNSET"){
						return "warning";
					}
				}
			}
	};
	
	/*
	 * init()
	 */
	var init = function(){
			
		$http.get(jsRoutes.controllers.containers.api.ContainerSupports.get($routeParams.code).url).then(function(response){
			$scope.support = response.data;
			//console.info($scope.support);	
			filterCategorySupport();	// Display configuration function
		});			

		if(tabService.getTabs().length == 0){
			tabService.addTabs({label:Messages('containerSupports.tabs.search'),href:jsRoutes.controllers.containers.tpl.ContainerSupports.home("search").url,remove:true});
			tabService.addTabs({label:$routeParams.code,href:jsRoutes.controllers.containers.tpl.ContainerSupports.home($routeParams.code).url,remove:true});
			tabService.activeTab($scope.getTabs(1));
		}

		// Call the Specifical Service
		$scope.detailsSearchService = containerSupportsDetailsSearchService;
		$scope.detailsSearchService.init($routeParams, datatableConfig);

	};

	init();
	
}]);
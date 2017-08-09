"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$q', '$routeParams', '$filter','$window', '$sce','mainService', 'tabService', 'lists', 'messages', 
                                          function($scope,$http,$q,$routeParams,$filter,$window,$sce,mainService,tabService,lists,messages){
	
	$scope.angular = angular;
	
	$scope.getTabClass = function(value){
		if(value === mainService.get('sampleActiveTab')){
			return 'active';
		}
	};
	$scope.setActiveTab = function(value){
		mainService.put('sampleActiveTab', value)
	};
	
	$scope.getPropertyDefinition = function(key){
		$http.get(jsRoutes.controllers.common.api.PropertyDefinitions.get(key).url).then(function(response) {
			var propertyDefinitions = {};			
		});
	}
	
	var saveInProgress = false;
	var samplePropertyDefinitionMap = {};	
	var init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.mainService = mainService;
		mainService.stopEditMode();
			
		$http.get(jsRoutes.controllers.samples.api.Samples.get($routeParams.code).url).then(function(response) {
		$scope.sample = response.data;			
			if(tabService.getTabs().length == 0){			
				tabService.addTabs({label:Messages('samples.tabs.search'),href:jsRoutes.controllers.samples.tpl.Samples.home("search").url,remove:true});
				tabService.addTabs({label:$scope.sample.code,href:jsRoutes.controllers.samples.tpl.Samples.get($scope.sample.code).url,remove:true});
				tabService.activeTab($scope.getTabs(1));
			}
			
			$scope.lists.refresh.samples();
			$scope.lists.refresh.resolutions({"objectTypeCode":"Sample"}, "sampleResolutions");
					
			if(undefined === mainService.get('sampleActiveTab')){
				mainService.put('sampleActiveTab', 'general');
				console.log("youhou");
			}else if('treeoflife' ===  mainService.get('sampleActiveTab')){
				mainService.put('sampleActiveTab', 'general');
			}
			
		});
		
		
		
		
		$http.get(jsRoutes.controllers.commons.api.PropertyDefinitions.list().url,{params:{'levelCode':'Sample'}}).then(function(response) {
			
			response.data.forEach(function(pdef){
					this[pdef.code]=pdef;
			}, samplePropertyDefinitionMap);
			
		});
	}
	init();
	
	$scope.getSamplePropertyDefinitionValueType = function(key){
		var propertyDef = samplePropertyDefinitionMap[key];
		if(propertyDef){
			return propertyDef.valueType;
		}
		return null;
	}
	
}]);
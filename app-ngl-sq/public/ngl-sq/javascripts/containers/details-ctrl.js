"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'mainService', 'tabService', 'messages', function($scope,$http,$routeParams,mainService,tabService,messages){
	
	$scope.getActiveTabClass = function(value){
		if(value === mainService.get('containerActiveTab')){
			return 'active';
		}
	};
	$scope.setActiveTab = function(value){
		mainService.put('containerActiveTab', value)
	};
	
	$scope.save = function(){
		console.log("Sauvegarde !");
	};
	$scope.cancel = function(){
		$scope.messages.clear();
		mainService.stopEditMode();
		updateData();
		console.log("Cancel !");
	};
	$scope.activeEditMode = function(){
		$scope.messages.clear();
		mainService.startEditMode();
	};
	
	var updateData = function(){
		$http.get(jsRoutes.controllers.containers.api.Containers.get($routeParams.code).url).success(function(response) {
			$scope.container = response;
		});
	}
	
	var isStateMode = function() {
		return (mainService.isHomePage('state') || ($routeParams.page && $routeParams.page.indexof('state') == 0));
	}
	
	var init = function(){
		$scope.messages = messages();
		mainService.stopEditMode();
		/*
			if(isStateMode()){
				mainService.startEditMode();
			}
		*/
		$http.get(jsRoutes.controllers.containers.api.Containers.get($routeParams.code).url).success(function(response) {
			$scope.container = response;	
		
			if(tabService.getTabs().length ==0){
				/*
				if(isStateMode()){ //state mode
					tabService.addTabs({label:Messages('containers.page.tab.state'),href:jsRoutes.controllers.containers.tpl.Containers.home("state").url,remove:true});
					tabService.addTabs({label:$scope.container.code,href:jsRoutes.controllers.containers.tpl.Containers.state($scope.container.code).url,remove:true});
				} else { //detail mode
				*/
				tabService.addTabs({label:Messages('containers.tab.search'),href:jsRoutes.controllers.containers.tpl.Containers.home("search").url,remove:true});
				tabService.addTabs({label:$scope.container.code,href:jsRoutes.controllers.containers.tpl.Containers.home($scope.container.code).url,remove:true});
				tabService.activeTab($scope.getTabs(1));
			}
			
			if(undefined == mainService.get('containerActiveTab')){
				mainService.put('containerActiveTab', 'general');
			}
		});
	}
	
	init();
}]);
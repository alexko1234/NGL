"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$routeParams', 'mainService', 'tabService', 'lists', 'messages', function($scope,$http,$routeParams,mainService,tabService,lists,messages){
	
	$scope.getTabClass = function(value){
		if(value === mainService.get('containerActiveTab')){
			return 'active';
		}
	};
	$scope.setActiveTab = function(value){
		mainService.put('containerActiveTab', value)
	};
	
	var init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		mainService.stopEditMode();

		$http.get(jsRoutes.controllers.containers.api.Containers.get($routeParams.code).url).success(function(response) {
			
			$scope.container = response;
			console.log($scope.container);
			
			if(tabService.getTabs().length == 0){
				
				tabService.addTabs({label:Messages('containers.tabs.search'),href:jsRoutes.controllers.containers.tpl.Containers.home("search").url,remove:true});
				tabService.addTabs({label:$scope.container.code,href:jsRoutes.controllers.containers.tpl.Containers.home($scope.container.code).url,remove:true});
				tabService.activeTab($scope.getTabs(1));
			}
			
			$scope.lists.refresh.containerSupportCategories();
			$scope.lists.refresh.experimentTypes({categoryCodes:["transformation"], withoutOneToVoid:false});
			
			
			if(undefined == mainService.get('containerActiveTab')){
				mainService.put('containerActiveTab', 'general');
			}
		});
	}
	
	init();
}]);
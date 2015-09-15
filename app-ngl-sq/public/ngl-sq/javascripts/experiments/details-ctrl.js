angular.module('home').controller('DetailsCtrl',['$scope','$sce', '$window','$http','$parse','$q','$position','$routeParams','$location','$filter',
                                                 'mainService','tabService','lists','datatable', 'messages',
                                                  function($scope,$sce,$window, $http,$parse,$q,$position,$routeParams,$location,$filter,
                                                		  mainService,tabService,lists,datatable, messages) {
	
	console.log("call DetailsCtrl");
	
	
	
	var init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.mainService = mainService;
		
		var promise=undefined;
		
		if($routeParams.typeCode){
			promise=$q.when({typeCode:$routeParams.typeCode});
		}else{
			promise = $http.get(jsRoutes.controllers.experiments.api.Experiments.get($routeParams.code).url)							
							.error(function(data, status, headers, config) {
								$scope.messages.setError("get");									
							});
		}
		
		promise.then(function(result) {
			if(result.data){
				experiment = result.data;
			} else {
				experiment = result;
			}
		});
		
		
	};
	
	init();
	
}]);
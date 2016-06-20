"use strict";

angular.module('home').controller('NewFromFileCtrl', ['$scope', '$http','lists', 'mainService', 'tabService','datatable', 'messages',
                                                  function($scope,$http,lists,mainService,tabService,datatable, messages){

	
	
	
	$scope.upload = function(){
		$scope.messages.clear();
		if($scope.form.receptionConfigurationCode && $scope.form.file){
			$http.post(jsRoutes.controllers.receptions.io.Receptions.importFile($scope.form.receptionConfigurationCode).url, $scope.form.file)
			.success(function(data, status, headers, config) {
				$scope.messages.clazz="alert alert-success";
				$scope.messages.text=Messages('experiments.msg.import.success');
				$scope.messages.showDetails = false;
				$scope.messages.open();	
				$scope.file = undefined;
				angular.element('#importFile')[0].value = null;
				$scope.$emit('refresh');
				
			})
			.error(function(data, status, headers, config) {
				$scope.messages.clazz = "alert alert-danger";
				$scope.messages.text = Messages('experiments.msg.import.error');
				$scope.messages.setDetails(data);
				$scope.messages.open();	
				$scope.file = undefined;
				angular.element('#importFile')[0].value = null;
			});
		}
	};
	
	$scope.reset = function(){
		$scope.form = {};	
		angular.element('#importFile')[0].value = null;
	}
	
	/*
	 * init()
	 */
	var init = function(){
		
		
		lists.refresh.receptionConfigs();
		$scope.lists = lists;
		$scope.reset();
		$scope.messages = messages();
		if(angular.isUndefined($scope.getHomePage())){
			mainService.setHomePage('new');
			tabService.addTabs({label:Messages('containerSupports.tabs.new'),href:jsRoutes.controllers.containers.tpl.ContainerSupports.home("new").url,remove:true});
			tabService.activeTab(0);
		}
	};

	init();
	
}]);
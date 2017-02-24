"use strict";

angular.module('home').controller('NewFromFileCtrl', ['$scope', '$http','$filter','lists', 'mainService', 'tabService','datatable', 'messages',
                                                  function($scope,$http,$filter,lists,mainService,tabService,datatable, messages){

	
	
	
	$scope.upload = function(){
		$scope.messages.clear();
		if($scope.form.receptionConfigurationCode && $scope.form.file){
			$scope.spinner = true;
			$http.post(jsRoutes.controllers.receptions.io.Receptions.importFile($scope.form.receptionConfigurationCode).url, $scope.form.file)
			.success(function(data, status, headers, config) {
				$scope.messages.clazz="alert alert-success";
				$scope.messages.text=Messages('experiments.msg.reception.success');
				$scope.messages.showDetails = false;
				$scope.messages.open();	
				$scope.file = undefined;
				angular.element('#importFile')[0].value = null;
				$scope.spinner = false;
			})
			.error(function(data, status, headers, config) {
				$scope.messages.clazz = "alert alert-danger";
				$scope.messages.text = Messages('experiments.msg.reception.error');
				$scope.messages.setDetails(data);
				$scope.messages.open();	
				$scope.file = undefined;
				angular.element('#importFile')[0].value = null;
				$scope.spinner = false;
			});
		}
	};
	
	$scope.reset = function(){
		$scope.form = {};	
		angular.element('#importFile')[0].value = null;
	};
	
	$scope.generateBarcode = function(){
		if($scope.nbCodes > 0){
			$http.post(jsRoutes.controllers.containers.api.ContainerSupports.saveCode().url+"?nbCodes="+$scope.nbCodes)
				.success(function(data) {
					var lineValue = "";
					data.forEach(function(code){
						lineValue += code + "\n";
					});
	
					
					var fixedstring = "\ufeff" + lineValue;
	
	                //save
	                var blob = new Blob([fixedstring], {
	                    type: "text/plain;charset=utf-8"
	                });
	                var currdatetime = $filter('date')(new Date(), 'yyyyMMdd_HHmmss');
	                var text_filename = "barcodes_" + currdatetime;
	                saveAs(blob, text_filename + ".csv");
					
				});
		}
	};
	
	
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
			tabService.addTabs({label:Messages('containers.tabs.new'),href:jsRoutes.controllers.containers.tpl.Containers.home("new").url,remove:true});
			tabService.activeTab(0);
		}
	};

	init();
	
}]);
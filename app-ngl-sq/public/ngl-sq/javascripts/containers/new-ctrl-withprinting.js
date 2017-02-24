"use strict";

angular.module('home').controller('NewFromFileCtrl', ['$scope','$http','$filter','lists', 'mainService', 'tabService','datatable', 'messages',
                                                  function($scope,$http,$filter,lists,mainService,tabService,datatable, messages){

	
	
	var datatableConfig = {	
			columns :[
				{
					"header":Messages("printing.tags.table.barcode"),
					"property":"barcode",
					"position":2,
					"order":true,
					"type":"text"
				}   
			
			],
			search:{
				active:false
			},
			pagination:{
				active:false
			},
			order:{
				by:'label',
				mode:'local'
			},
			hide:{
				active:false
		 	},
			edit:{
				active:false
			},
			exportCSV:{
				active:false
			},
			select:{
				active:false
			},
			showTotalNumberRecords:false
	};
	
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
	
	$scope.print = function(){
		$scope.messages.clear();
		var tags = $scope.datatable.getData();
		$scope.form.tags = tags;
		
		$http.post(jsRoutes.controllers.printing.api.Tags.print().url, $scope.form)
		.success(function(data, status, headers, config) {
			
			$scope.messages.setSuccess(Messages("printing.tags.msg.success.printing"));
		})
		.error(function(data, status, headers, config) {
			$scope.messages.setError(Messages("printing.tags.msg.error.printing"));
			$scope.messages.setDetails(data);								
		});				
	};
	
	
	$scope.reset = function(){
		$scope.form = {};	
		$scope.nbCodes=null;
		$scope.messages=messages();
		angular.element('#importFile')[0].value = null;
		$scope.datatable.setData([]);
	};
	
	$scope.changePrinter = function(){
		if($scope.form.printerCode){
			$scope.selectedPrinter = $scope.printers.find(function(printer){
				return printer.code == $scope.form.printerCode;
			})
		}else{
			$scope.selectedPrinter = undefined;
		}
		$scope.form.barcodePositionId = undefined;
	}
	
	$scope.generateBarcode = function(){
		if($scope.nbCodes > 0){

			$http.post(jsRoutes.controllers.containers.api.ContainerSupports.saveCode().url+"?nbCodes="+$scope.nbCodes)
				.success(function(data) {
					var lineValue = "";
					var values=[];
					data.forEach(function(code){
						values.push({"barcode": code});
					});
							                
	               //Datatable
	                $scope.datatable.setData(values);
				});
			
		}
	};
	
	$scope.exportCSV= function(){
		
		var data=$scope.datatable.getData();
		var lineValue = "";
		data.forEach(function(data){
			lineValue += data.barcode + "\n";
		});
			
		var fixedstring = "\ufeff" + lineValue;

        //save
        var blob = new Blob([fixedstring], {
            type: "text/plain;charset=utf-8"
        });
        var currdatetime = $filter('date')(new Date(), 'yyyyMMdd_HHmmss');
        var text_filename = "barcodes_" + currdatetime;
        saveAs(blob, text_filename + ".csv");
	}
	
	$http.get(jsRoutes.controllers.commons.api.Parameters.list().url,{params:{typeCode:"BBP11"}})
	.success(function(data, status, headers, config) {
			$scope.printers = data;		
	})
	
	/*
	 * init()
	 */
	var init = function(){
		
		$scope.datatable = datatable(datatableConfig);
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
angular.module('home').controller('OneToVoidQCMiseqCNGCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		/*
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
				var concentration1 = $parse("experimentProperties.concentration1")(inputContainerUsed);
				if(concentration1){
					inputContainerUsed.concentration = concentration1;
				}
			}
			
		});		
		*/	
	};
	
	
	var importData = function(){
		$scope.messages.clear();
		
		$http.post(jsRoutes.controllers.instruments.io.IO.importFile($scope.experiment.code).url, $scope.file)
		.success(function(data, status, headers, config) {
			$scope.messages.clazz="alert alert-success";
			$scope.messages.text=Messages('experiments.msg.import.success');
			$scope.messages.showDetails = false;
			$scope.messages.open();	
			//only atm because we cannot override directly experiment on scope.parent
			$scope.experiment.atomicTransfertMethods = data.atomicTransfertMethods;
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
	};
	
	$scope.button = {
		isShow:function(){
			return ($scope.isInProgressState() && !$scope.mainService.isEditMode())
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData,		
	};
	
	var columns = $scope.atmService.data.getColumnsConfig();
	columns.push({
    	"header":"Code aliquot",
		"property": "inputContainer.contents",
		"filter": "getArray:'properties.sampleAliquoteCode.value'",
		"order":false,
		"hide":true,
		"type":"text",
		"position":7.5,
		"render": "<div list-resize='cellValue | unique' list-resize-min-size='3'>",
		"extraHeaders":{0:Messages("experiments.inputs")}
	});

	
	columns.push({
		"header" : Messages("containers.table.concentration"),
		"property": "(inputContainer.concentration.value|number).concat(' '+inputContainer.concentration.unit)",
		//"render":"<span ng-bind='cellValue.value|number'/> <span ng-bind='cellValue.unit'/>",
		"order" : true,
		"edit" : false,
		"hide" : true,
		"type" : "text",
		"position" : 10,
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}
	});	
	
	columns.push({
		"header" : Messages("containers.table.libProcessType"),
		"property" : "inputContainer.contents",
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 8,
		"render" : "<div list-resize='cellValue | getArray:\"properties.libProcessTypeCode.value\" | unique' list-resize-min-size='3'>",
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}
	});
	columns.push({
		"header" : Messages("containers.table.tags"),
		"property" : "inputContainer.contents",
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 9,
		"render" : "<div list-resize='cellValue | getArray:\"properties.tag.value\" | unique' list-resize-min-size='3'>",
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}

	});
	$scope.atmService.data.setColumnsConfig(columns);
	
	
	var generateSampleSheet = function(){
		$http.post(jsRoutes.controllers.instruments.io.IO.generateFile($scope.experiment.code).url,{})
		.success(function(data, status, headers, config) {
			var header = headers("Content-disposition");
			var filepath = header.split("filename=")[1];
			var filename = filepath.split(/\/|\\/);
			filename = filename[filename.length-1];
			if(data!=null){
				$scope.messages.clazz="alert alert-success";
				$scope.messages.text=Messages('experiments.msg.generateSampleSheet.success')+" : "+filepath;
				$scope.messages.showDetails = false;
				$scope.messages.open();	
				
				var blob = new Blob([data], {type: "text/plain;charset=utf-8"});    					
				saveAs(blob, filename);
			}
		})
		.error(function(data, status, headers, config) {
			$scope.messages.clazz = "alert alert-danger";
			$scope.messages.text = Messages('experiments.msg.generateSampleSheet.error');
			$scope.messages.showDetails = false;
			$scope.messages.open();				
		});
	};
	
	$scope.setAdditionnalButtons([{
		isDisabled : function(){return $scope.isCreationMode();},
		isShow:function(){return true},
		click:generateSampleSheet,
		label:Messages("experiments.sampleSheet")
	}]);
	
}]);
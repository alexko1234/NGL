angular.module('home').controller('OneToVoidQCMiseqCNGCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	// NGL-1055: surcharger la variable "name" definie dans le controleur parent ( one-to-void-qc-ctrl.js) => nom de fichier CSV exporté 
	var config = $scope.atmService.data.getConfig();
	config.name = $scope.experiment.typeCode.toUpperCase();
	$scope.atmService.data.setConfig(config );
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		/* FDS decommenté le 30/08 + ajout de la size: les 2 propriétés de l'expérience doivent etres copiées dans le container */
		experiment.atomicTransfertMethods.forEach(function(atm){
			var inputContainerUsed =$parse("inputContainerUseds[0]")(atm);
			if(inputContainerUsed){
				
				var insertsize = $parse("experimentProperties.measuredInsertSize")(inputContainerUsed);
				if(insertsize){
					console.log("copy experimentProperties.measuredInsertSize to inputContainerUsed.size :"+ insertsize.value);
					inputContainerUsed.newSize = insertsize;
				}
			}
		});		
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
			return ($scope.isInProgressState() && !$scope.mainService.isEditMode() || Permissions.check("admin"))
			},
		isFileSet:function(){
			return ($scope.file === undefined)?"disabled":"";
		},
		click:importData,		
	};
	
	// NGL-1055: mettre les getArray et codes'' dans filter et pas dans render
	var columns = $scope.atmService.data.getColumnsConfig();
	columns.push({
    	"header" :Messages("containers.table.codeAliquot"),
		"property" : "inputContainer.contents",
		"filter" : "getArray:'properties.sampleAliquoteCode.value'| unique",
		"order" :false,
		"hide" :true,
		"type" :"text",
		"position" :7.5,
		"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
		"extraHeaders" :{0:Messages("experiments.inputs")}
	});
	columns.push({
		"header" : Messages("containers.table.concentration"),
		"property": "(inputContainerUsed.concentration.value|number).concat(' '+inputContainerUsed.concentration.unit)",
		//"render":"<span ng-bind='cellValue.value|number'/> <span ng-bind='cellValue.unit'/>",
		"order" : true,
		"edit" : false,
		"hide" : true,
		"type" : "text",
		"position" : 11, // chgé de 10 a 11
		"extraHeaders" : {0 : Messages("experiments.inputs")}
	});	
	columns.push({
		"header" : Messages("containers.table.libProcessTypeCode"),
		"property" : "inputContainer.contents",
		"filter" : " getArray:'properties.libProcessTypeCode.value'| unique", 
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 8,
		"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
		"extraHeaders" : {0 : Messages("experiments.inputs")}
	});
	columns.push({
		"header" : Messages("containers.table.tags"),
		"property" : "inputContainer.contents",
		"filter" : "getArray:'properties.tag.value'| unique",
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 9,
		"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
		"extraHeaders" : {0 : Messages("experiments.inputs")}
	});
	$scope.atmService.data.setColumnsConfig(columns);
	
	
	var generateSampleSheet = function(){
		$scope.messages.clear();
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
			$scope.messages.setDetails(data);
			$scope.messages.showDetails = true;
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
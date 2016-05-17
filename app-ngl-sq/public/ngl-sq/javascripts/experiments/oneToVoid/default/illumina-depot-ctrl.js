angular.module('home').controller('IlluminaDepotCtrl',['$scope', '$parse','$http','atmToSingleDatatable',
                                                             function($scope,$parse, $http, atmToSingleDatatable) {
	 var datatableConfig = {
			name:"FDR_Void",
			columns:[
			         {
			        	 "header":Messages("containers.table.supportCode"),
			        	 "property":"inputContainer.support.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.categoryCode"),
			        	 "property":"inputContainer.support.categoryCode",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":3,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
					 {
			        	 "header":Messages("containers.table.projectCodes"),
			        	 "property":"inputContainer.projectCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":4,
			        	 "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
					 {
			        	 "header":Messages("containers.table.sampleCodes"),
			        	 "property":"inputContainer.sampleCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":5,
			        	 "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
					 {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"inputContainer.state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":6,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.fromTransformationTypeCodes"),
			        	 "property":"inputContainer.fromTransformationTypeCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			 			"render":"<div list-resize='cellValue | unique | codes:\"type\"' list-resize-min-size='3'>",
			        	 "position":7,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         ],
			compact:true,
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local', //or 
				active:true,
				by:'inputContainer.code'
			},
			remove:{
				active:false,
			},
			save:{
				active:true,
	        	changeClass:false,
	        	showButton:false,
	        	withoutEdit: true,
				mode:'local',
			},
			hide:{
				active:true
			},
			edit:{
				active: false
			},
			messages:{
				active:false,
				columnMode:true
			},
			exportCSV:{
				active:true,
				showButton:true,
				delimiter:";",
				start:false
			},
			extraHeaders:{
				number:2,
				dynamic:true,
			}
	};

		$scope.$on('save', function(e, callbackFunction) {	
			console.log("call event save on one-to-void");
			$scope.atmService.data.save();
			$scope.atmService.viewToExperimentOneToVoid($scope.experiment);
			$scope.$emit('childSaved', callbackFunction);
		});
		
		$scope.$on('refresh', function(e) {
			console.log("call event refresh on one-to-void");		
			var dtConfig = $scope.atmService.data.getConfig();
			$scope.atmService.data.setConfig(dtConfig);
			
			$scope.atmService.refreshViewFromExperiment($scope.experiment);
			$scope.$emit('viewRefeshed');
		});
		
		$scope.$on('cancel', function(e) {
			console.log("call event cancel");
			$scope.atmService.data.cancel();						
		});
		
		$scope.$on('activeEditMode', function(e) {
			console.log("call event activeEditMode");
			$scope.atmService.data.selectAll(true);
			$scope.atmService.data.setEdit();
		});
		
		var atmService = atmToSingleDatatable($scope, datatableConfig, true);
		//defined new atomictransfertMethod
		atmService.newAtomicTransfertMethod = function(line, column){
			return {
				class:"OneToVoid",
				line:line, 
				column:column, 				
				inputContainerUseds:new Array(0)
			};
		};
		
		atmService.experimentToView($scope.experiment, $scope.experimentType);
		
		$scope.atmService = atmService;
		
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
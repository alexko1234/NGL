angular.module('home').controller('DetailsCtrl',['$scope','$sce', '$window','$http','$parse','$q','$position','$routeParams','$location','$filter',
                                                 'mainService','tabService','lists','datatable', 'messages',
                                                  function($scope,$sce,$window, $http,$parse,$q,$position,$routeParams,$location,$filter,
                                                		  mainService,tabService,lists,datatable, messages) {
	
	console.log("call DetailsCtrl");
	
	var saveInProgress = false;
	
	/*move to a directive*/
	$scope.setImage = function(imageData, imageName, imageFullSizeWidth, imageFullSizeHeight) {
		$scope.modalImage = imageData;

		$scope.modalTitle = imageName;

		var margin = 25;		
		var zoom = Math.min((document.body.clientWidth - margin) / imageFullSizeWidth, 1);

		$scope.modalWidth = imageFullSizeWidth * zoom;
		$scope.modalHeight = imageFullSizeHeight * zoom; //in order to conserve image ratio
		$scope.modalLeft = (document.body.clientWidth - $scope.modalWidth)/2;

		$scope.modalTop = (window.innerHeight - $scope.modalHeight)/2;

		$scope.modalTop = $scope.modalTop - 50; //height of header and footer
	}
	
	
	$scope.activeEditMode = function(){
		console.log("call activeEditMode");
		$scope.messages.clear();
		mainService.startEditMode();
	};
	
	$scope.cancel = function(){
		console.log("call cancel");
		$scope.messages.clear();
		mainService.stopEditMode();
		saveInProgress = false;
	};
	
	$scope.save = function(){
		console.log("call save");
		saveInProgress = true;
	};
	
	$scope.startExperiment = function(){
		console.log("call startExperiment");
	};
	
	$scope.finishExperiment = function(){
		console.log("call finishExperiment");
	};
	
	
	$scope.isWorkflowModeAvailable = function(nextStateCode){
		if($scope.experiment !== undefined){
			return (nextStateCode === 'IP' && $scope.experiment.state.code === "N" 
				|| nextStateCode === 'F' && $scope.experiment.state.code !== "F");
		}else{
			return false;
		}
	};
	
	$scope.isEditModeAvailable = function(){		
		if($scope.experiment !== undefined){
			return ($scope.experiment.state.code !== "F");
		}else{
			return false;
		}		
	};
	
	$scope.isNewState = function(){				
		return ($scope.experiment.state.code === "N");
	};
	
	$scope.isInProgressState = function(){				
		return ($scope.experiment.state.code === "IP");
	};
	
	$scope.isFinishState = function(){				
		return ($scope.experiment.state.code === "F");
	};
	
	$scope.isSaveInProgress = function(){
		return saveInProgress;
	};
	
	
	$scope.isOutputATMVoid = function(){
		return ($scope.experimentType.atomicTransfertMethod === "OneToVoid");
	}
	
	$scope.changeInstrumentType = function(){
		console.log("call changeInstrumentType see getInstrumentsTrigger() in old version");
		if($scope.experiment || false && $scope.experiment.instrument || false){
			loadInstrumentType($scope.experiment.instrument.typeCode);
			//reinit experiment instrument
			$scope.experiment.instrument.code = undefined;
			$scope.experiment.instrument.outContainerSupportCategoryCode = undefined;	
			
		}
		$scope.experimentTypeTemplate = undefined;		
	}
	
	
	
	$scope.loadTemplate = function(){
		console.log("call loadTemplate see getTemplate() in old version");
		if($scope.experimentType || false &&  $scope.experiment.instrument || false && $scope.experiment.instrument.outContainerSupportCategoryCode || false ){
			$scope.experimentTypeTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getTemplate($scope.experimentType.atomicTransfertMethod.toLowerCase(),$scope.experiment.instrument.outContainerSupportCategoryCode,$scope.experimentType.code).url;
		}else{
			$scope.experimentTypeTemplate =  undefined;
		}				
	}
	
	var loadInstrumentType = function(code){
		$http.get(jsRoutes.controllers.instruments.api.InstrumentUsedTypes.get(code).url)
			.success(function(data, status, headers, config) {
				$scope.instrumentType = data;
			})
			.error(function(data, status, headers, config) {
				$scope.messages.setError("get");
			});
	}
	
	var clearLists = function(){
		$scope.lists.clear("instrumentUsedTypes");
		$scope.lists.clear("protocols");
		$scope.lists.clear("resolutions");
		$scope.lists.clear("states");
		$scope.lists.clear("experimentTypeCodes");
		$scope.lists.clear("experimentCategories");

		$scope.lists.refresh.experimentTypes({categoryCode:$scope.experimentType.category.code},$scope.experimentType.category.code);
		$scope.lists.refresh.instrumentUsedTypes({"experimentTypeCode":$scope.experimentType.code});
		$scope.lists.refresh.protocols({"experimentTypeCode":$scope.experimentType.code});
		$scope.lists.refresh.resolutions({"typeCode":$scope.experimentType.code});
		$scope.lists.refresh.states({"objectTypeCode":"Experiment"});
		
		$scope.lists.refresh.kitCatalogs();
		$scope.lists.refresh.experimentCategories();
	}
	
	var init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.mainService = mainService;
		
		if(angular.isUndefined($scope.getHomePage())){
			mainService.setHomePage('search');
			tabService.addTabs({label:Messages('experiments.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("search").url,remove:true});
			tabService.addTabs({label:$routeParams.code,href:jsRoutes.controllers.experiments.tpl.Experiments.get($routeParams.code).url,remove:true});			
			tabService.activeTab($scope.getTabs(1));
		}
		
		var promise=undefined;
		
		if($routeParams.code === 'new'){
			var defaultExperiment = mainService.get("newExp");
			if(!defaultExperiment){
				$scope.creationMode = true;
				$scope.startEditMode();
				defaultExperiment = {
						state : {
							resolutionCodes : [],
							code : "N"
						},
						reagents : [],
						atomicTransfertMethods : [],
						comments : [],
						typeCode:$routeParams.typeCode
					};
				
				mainService.put("newExp", defaultExperiment);
			}
			
			promise=$q.when(defaultExperiment);
		}else{
			promise = $http.get(jsRoutes.controllers.experiments.api.Experiments.get($routeParams.code).url)							
							.error(function(data, status, headers, config) {
								$scope.messages.setError("get");									
							});
		}
		
		promise.then(function(result) {
			var experiment = undefined;
			if(result.data){
				experiment = result.data;
			} else {
				experiment = result;
			}
			
			//experiment.state.code = 'N';
			
			return experiment;
		}).then(function(experiment){
			//console.log(experiment);
			
			$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.get(experiment.typeCode).url).error(function(data, status, headers, config) {
				$scope.messages.setError("get");
			}).then(function(result) {
				$scope.experiment = experiment;
				$scope.experimentType = result.data;
				
				$scope.experiment.typeCode =  $scope.experimentType.code;
				$scope.experiment.categoryCode = $scope.experimentType.category.code;
				
				if($scope.isOutputATMVoid()){
					$scope.experiment.instrument.outContainerSupportCategoryCode = "void";					
				}
				
				clearLists();
				if($scope.experiment.instrument || false && $scope.experiment.instrument.typeCode || false){
					loadInstrumentType($scope.experiment.instrument.typeCode);
				}
				$scope.loadTemplate();
			});			
		})
		
		
	};
	
	init();
	
}]).controller('ReagentsCtrl',['$scope','$http','lists','$parse','$filter','datatable', 
                                                   function($scope,$http,lists,$parse,$filter,datatable) {
	
	var datatableConfigReagents = {
			name:"reagents",
			columns:[
			         {
			        	 "header":Messages("reagents.table.kitname"),
			        	 "property":"kitCatalogCode",
			        	 "order":true,
			        	 "type":"text",
			        	 "listStyle":"bt-select-filter",
			        	 "choiceInList":true,
			        	 "possibleValues": 'lists.getKitCatalogs()',
			        	 "render":'<div bt-select ng-model="value.data.kitCatalogCode" bt-options="kitCatalog.code as kitCatalog.name for kitCatalog in lists.getKitCatalogs()" ng-edit="false" placeholder="Messages("experiment.placeholder.reagents.kit")"></div>',
			        	 "edit":true
			         },
			         {
			        	 "header":Messages("reagents.table.boxcode"),
			        	 "property":"boxCode",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":true,
			        	 "editDirectives":'ng-keydown="scan($event,value.data,\'boxCode\')"'
			         },
			         {
			        	 "header":Messages("reagents.table.reagentcode"),
			        	 "property":"code",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":true,
			        	 "editDirectives":'ng-keydown="scan($event,value.data,\'code\')"'
			         },
			         {
			        	 "header":Messages("reagents.table.description"),
			        	 "property":"description",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":true
			         }
			         ],
			         compact:true,
			         pagination:{
			        	 mode:'local',
			        	 active:false
			         },		
			         search:{
			        	 active:false
			         },
			         order:{
			        	 mode:'local',
			        	 active:true,
			        	 by:'outputPositionX'
			         },
			         remove:{
			        	 mode:'local',
			        	 active:true,
			        	 withEdit:true,
			        	 callback:function(datatable){
			        		 var reagents = datatable.allResult;
			        		 $scope.experiment.reagents = reagents;
			        	 }
			         },
			         hide:{
			        	 active:false
			         },
			         edit:{
			        	 active:true,
			        	 columnMode:false,
			        	 showButton : false,
			        	 withoutSelect:true,
			        	 byDefault : false
			         },
			         save:{
			        	 active:true,
			        	 showButton:false,
			        	 withoutEdit:true,
			        	 mode:'local',
			        	 callback:function(datatable){
			        		 var reagents = datatable.allResult;
			        		 $scope.experiment.reagents = reagents;
			        	 }
			         },
			         messages:{
			        	 active:false,
			        	 columnMode:true
			         },
			         exportCSV:{
			        	 active:true,
			        	 showButton:true,
			        	 delimiter:";"
			         },
			         add:{
			        	 active:true
			         }
			         /*
			         otherButtons:{
			        	 active:true,
			        	 template:'<button class="btn btn btn-info" ng-click="addNewReagentLine()" title="'+Messages("experiments.addNewReagentLine")+'">'+Messages("experiments.addNewReagentLine")+'</button>'
			         }
			         */
	};

	$scope.scan = function(e, property, propertyName){
		//console.log(property);
		//console.log(e);
		if(e.keyCode === 9 || e.keyCode === 13){
			property[propertyName] += '_';
			//console.log(property);
			e.preventDefault();
		}
	};

	$scope.searchReagentsEvent = function(e){
		if(e.keyCode === 9 || e.keyCode === 13){
			$scope.searchReagents();
			e.preventDefault();
		}
	};


	$scope.searchReagents = function(){
		$http.get(jsRoutes.controllers.reagents.api.Reagents.list().url, {params:{"barCode":$scope.searchBarCode, "boxBarCode":$scope.searchBarCode}})
		.success(function(data, status,headers,config){
			console.log(data);
			var datatableData = $scope.datatableReagent.getData();
			for(var i=0;i<data.length;i++){
				var closureData = data[i];
				$http.get(jsRoutes.controllers.reagents.api.Boxes.list().url, {params:{"code":data[i].boxCode}})
				.success(function(dataBox, status,headers,config){
					var r = {"boxCode":dataBox[0].catalogRefCode+"_"+dataBox[0].bundleBarCode+"_"+dataBox[0].barCode,
							"code":closureData.catalogRefCode+"_"+closureData.bundleBarCode+"_"+closureData.barCode,
							"kitCatalogCode":closureData.catalogCode};
					if($scope.isReagentAdded(r.code) === false){
						datatableData.push(r);
						console.log(dataBox);
						$scope.datatableReagent.setData(datatableData);
					}
				});
			}
		}).error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');
			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
		$scope.reagentCodeErrorClass = ""
		$scope.reagentCodeError = "";
		if($scope.searchBarCode !== undefined && $scope.searchBarCode !== ""){
			$http.get(jsRoutes.controllers.reagents.api.Reagents.list().url, {params:{"barCode":$scope.searchBarCode, "boxBarCode":$scope.searchBarCode}})
			.success(function(data, status,headers,config){
				console.log(data);
				var datatableData = $scope.datatableReagent.getData();
				if(data.length > 0){
				for(var i=0;i<data.length;i++){
					var closureData = data[i];
					$http.get(jsRoutes.controllers.reagents.api.Boxes.list().url, {params:{"code":data[i].boxCode}})
					.success(function(dataBox, status,headers,config){
						if(data.length>0){
							var r = {"boxCode":dataBox[0].catalogRefCode+"_"+dataBox[0].bundleBarCode+"_"+dataBox[0].barCode,
									"code":closureData.catalogRefCode+"_"+closureData.bundleBarCode+"_"+closureData.barCode,
									"kitCatalogCode":closureData.catalogCode};
							if($scope.isReagentAdded(r.code) === false){
								datatableData.push(r);
								console.log(dataBox);
								$scope.datatableReagent.setData(datatableData);
							}
						}
					});
				}
				}else{
					$scope.reagentCodeErrorClass = "has-error"
					$scope.reagentCodeError = "Code non reconnu";
				}
			}).error(function(data, status, headers, config) {
				$scope.message.clazz = "alert alert-danger";
				$scope.message.text = Messages('experiments.msg.save.error');
				$scope.message.details = data;
				$scope.message.isDetails = true;
			});
		}
	};

	$scope.isReagentAdded = function(code){
		var datatableData = $scope.datatableReagent.getData();
		for(var i=0;i<datatableData.length;i++){
			if(datatableData[i].code === code){
				return true;
			}
		}
		return false
	};
	/*
	$scope.addNewReagentLine = function(){
		$scope.datatableReagent.save();
		$scope.datatableReagent.addData([{}]);
		$scope.datatableReagent.setEdit();
	};
	*/
	
	$scope.datatableReagent = datatable(datatableConfigReagents);
	
	$scope.$watch('experiment', function() {
		console.log('watch experiment')
		if (angular.isDefined($scope.experiment)){
			if($scope.experiment.reagents === null || $scope.experiment.reagents === undefined || $scope.experiment.reagents.length === 0){
				$scope.datatableReagent.setData([]);				
			}else{
				$scope.datatableReagent.setData($scope.experiment.reagents);
			}
		}
	
	});

	$scope.$on('save', function(e, promises, func, endPromises) {	
		console.log("call event save for reagents");
		$scope.datatableReagent.save()
		$scope.$emit('viewSaved', promises, func, endPromises);
	});
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh for reagents");				
		$scope.$emit('viewRefeshed');
	});
	
}]).controller('CommentsCtrl',['$scope','$sce', '$http','lists','$parse','$filter','datatable', 
                               function($scope,$sce,$http,lists,$parse,$filter,datatable) {

	$scope.currentComment = {comment:undefined};
	
	$scope.convertToBr = function(text){
		return $sce.trustAsHtml(text.replace(/\n/g, "<br>"));
	};
	
	$scope.setCurrentComment = function(com){
		$scope.currentComment = com;
	};
	
	$scope.save = function(){		
		$scope.messages.clear();
		$http.post(jsRoutes.controllers.experiments.api.ExperimentsOld.addComment($scope.experiment.code).url, $scope.currentComment)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.messages.setSuccess("save");
				$scope.experiment.comments.push(data);
				$scope.currentComment = {comment:undefined};
			}
		})
		.error(function(data, status, headers, config) {
			$scope.messages.setError("save");
			$scope.messages.setDetails(data);
		});		
	};
	
	$scope.update = function(){
		$scope.messages.clear();
		$http.put(jsRoutes.controllers.experiments.api.ExperimentsOld.updateComment($scope.experiment.code).url, $scope.currentComment)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.messages.setSuccess("save");
				$scope.currentComment = {comment:undefined};
			}
		})
		.error(function(data, status, headers, config) {
			$scope.messages.setError("save");
			$scope.messages.setDetails(data);
		});
	};
	
	$scope.delete = function(com){
		if (confirm(Messages("comments.remove.confirm"))) {
			$scope.messages.clear();
			$http.delete(jsRoutes.controllers.experiments.api.ExperimentsOld.deleteComment($scope.experiment.code, com.code).url)
			.success(function(data, status, headers, config) {
				if(data!=null){
					$scope.messages.setSuccess("save");
					$scope.currentComment = {comment:undefined};
					for(var i=0;$scope.experiment.comments.length;i++){
						if($scope.experiment.comments[i].code == com.code){
							$scope.experiment.comments.splice(i, 1);
						}
					}
				}
			})
			.error(function(data, status, headers, config) {
				$scope.messages.setError("remove");
				$scope.messages.setDetails(data);				
			});
		}
	};
}]);

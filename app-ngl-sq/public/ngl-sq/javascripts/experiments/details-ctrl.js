angular.module('home').controller('DetailsCtrl',['$scope','$sce', '$window','$http','$parse','$q','$position','$routeParams','$location','$filter',
                                                 'mainService','tabService','lists','datatable', 'messages',
                                                  function($scope,$sce,$window, $http,$parse,$q,$position,$routeParams,$location,$filter,
                                                		  mainService,tabService,lists,datatable, messages) {
	
	console.log("call DetailsCtrl");
	
	$scope.isCreationMode=function(){
		return creationMode;
	};
	
	$scope.getNbAvailableAdditionnalButtons = function(){
		var nbButtons = 0;
		for(var i = 0; i < additionnalButtons.length; i++){
			if(additionnalButtons[i].isShow()){
				nbButtons++;
			}
		}
		
		return nbButtons;
	};
	$scope.getAdditionnalButtons = function(){
		return additionnalButtons;
	};
	/* move to a directive */
	$scope.setImage = function(imageData, imageName, imageFullSizeWidth, imageFullSizeHeight) {
		$scope.modalImage = imageData;

		$scope.modalTitle = imageName;

		var margin = 25;		
		var zoom = Math.min((document.body.clientWidth - margin) / imageFullSizeWidth, 1);

		$scope.modalWidth = imageFullSizeWidth * zoom;
		$scope.modalHeight = imageFullSizeHeight * zoom; // in order to
															// conserve image
															// ratio
		$scope.modalLeft = (document.body.clientWidth - $scope.modalWidth)/2;

		$scope.modalTop = (window.innerHeight - $scope.modalHeight)/2;

		$scope.modalTop = $scope.modalTop - 50; // height of header and footer
	};
	
	
	$scope.activeEditMode = function(){
		console.log("call activeEditMode");
		$scope.messages.clear();
		mainService.startEditMode();
		
		if(mainService.isHomePage('search') 
				&& !tabService.isBackupTabs() 
				&& $scope.isNewState()){
			
			tabService.backupTabs();
			tabService.resetTabs();
			tabService.addTabs({label:Messages('experiments.tabs.create'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("new").url,remove:false});
			tabService.addTabs({label:$scope.experiment.code,href:jsRoutes.controllers.experiments.tpl.Experiments.get($scope.experiment.code).url,remove:true});
			tabService.activeTab(1);
			mainService.setDatatable(undefined);
			
			var form = {};
			form.nextExperimentTypeCode = $scope.experimentType.code;
			form.experimentCategoryCode = $scope.experimentType.category.code;
			mainService.setForm(form);		
		}
		$scope.$broadcast('activeEditMode');
		
	};
	
	$scope.cancel = function(){
		console.log("call cancel");
		$scope.messages.clear();
		mainService.stopEditMode();
		saveInProgress = false;
		updateData();
		
		if(mainService.isHomePage('search') 
				&& tabService.isBackupTabs() ){
			$scope.restoreBackupTabs();
			$scope.activeTab(1);
			$scope.setDatatable(undefined);	
			
			var form = {};
			form.typeCode = $scope.experimentType.code;
			form.experimentCategoryCode = $scope.experimentType.category.code;
			
			mainService.setForm(form);								
		}	
		$scope.$broadcast('refresh');
	};
	
	$scope.save = function(callbackFunction){
		console.log("call save on main");
		$scope.messages.clear();
		saveInProgress = true;
		$scope.$broadcast('saveReagents', callbackFunction);		
	};
	
	$scope.startExperiment = function(){
		console.log("call startExperiment");
		$scope.save(function(experiment){
			console.log("call callback startExperiment");
			
			mainService.put("experiment",$scope.experiment);
			$scope.experiment = experiment;
			var state = angular.copy($scope.experiment.state);
			state.code = "IP";
			$http.put(jsRoutes.controllers.experiments.api.Experiments.updateState(experiment.code).url, state)
			.success(function(data, status, headers, config) {
				endSaveSuccess(data);															
			})
			.error(function(data, status, headers, config) {
				$scope.messages.setError("save");
				$scope.messages.setDetails(data);				
				saveInProgress = false;	
			});
		});
	};
	
	
	$scope.finishExperiment = function(){
		console.log("call finishExperiment");
		
		if($scope.experiment.state.resolutionCodes !== null 
				&& $scope.experiment.state.resolutionCodes !== undefined 
				&& $scope.experiment.state.resolutionCodes.length > 0){
			
			angular.element('#finalResolutionModal').modal('hide');
			
			$scope.save(function(experiment){
				console.log("call callback finishExperiment");
				
				mainService.put("experiment",$scope.experiment);
				$scope.experiment = experiment;
				var state =  angular.copy($scope.experiment.state);
				state.code = "F";
				$http.put(jsRoutes.controllers.experiments.api.Experiments.updateState(experiment.code).url, state)
				.success(function(data, status, headers, config) {
					endSaveSuccess(data);
					angular.element('#finalDispatchModal').modal('show');
				})
				.error(function(data, status, headers, config) {				
					$scope.messages.setError("save");
					$scope.messages.setDetails(data);				
					saveInProgress = false;	
				});			
			});
		}else{
			angular.element('#finalResolutionModal').modal('show');
		}
	};
	
	$scope.$on('reagentsSaved', function(e, callbackFunction) {
		console.log('call event reagentsSaved on main');
		$scope.$broadcast('save', callbackFunction);
	});
	
	
	var endSaveSuccess = function(newExperiment){
		// purge basket when save ok or not ?
		resetBasket();					
		mainService.put("experiment",$scope.experiment);
		$scope.experiment = newExperiment;
		$scope.messages.setSuccess("save");						
		mainService.stopEditMode();
		saveInProgress = false;
		$scope.$broadcast('refresh'); // utile seulement si l'update fonctionne				
	}
	
	$scope.$on('childSaved', function(e, callbackFunction) {
		console.log('call event childSaved on main');
		
		// TODO effective save or update
		if(creationMode){
			$http.post(jsRoutes.controllers.experiments.api.Experiments.save().url, $scope.experiment, {callbackFunction:callbackFunction})
				.success(function(data, status, headers, config) {
					
					creationMode = false;
					mainService.setHomePage('search')
					tabService.resetTabs();
					tabService.addTabs({label:Messages('experiments.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("search").url,remove:true});
					tabService.addTabs({label:data.code,href:jsRoutes.controllers.experiments.tpl.Experiments.get(data.code).url,remove:true});
					
					if(config.callbackFunction){
						config.callbackFunction(data);
					}else{
						endSaveSuccess(data);
					}						
				})
				.error(function(data, status, headers, config) {
					$scope.messages.setError("save");
					$scope.messages.setDetails(data);					
					saveInProgress = false;						
				});
		}else{
			$http.put(jsRoutes.controllers.experiments.api.Experiments.update($scope.experiment.code).url, $scope.experiment, {callbackFunction:callbackFunction})
			.success(function(data, status, headers, config) {
				if(config.callbackFunction){
					config.callbackFunction(data);
				}else{
					endSaveSuccess(data);
				}											
			})
			.error(function(data, status, headers, config) {
				$scope.messages.setError("save");
				$scope.messages.setDetails(data);				
				saveInProgress = false;	
			});			
		}
		
	});
	
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
	};
	
	$scope.changeInstrumentType = function(){
		console.log("call changeInstrumentType see getInstrumentsTrigger() in old version");
		if($scope.experiment && $scope.experiment.instrument && $scope.experiment.instrument.typeCode){
			var instrumentTypeCode = $scope.experiment.instrument.typeCode;
			$scope.experiment.instrument = {};
			$scope.experiment.instrumentProperties = undefined;
			$scope.instrumentType = undefined;
			loadInstrumentType(instrumentTypeCode);												
		}else if($scope.experiment){
			$scope.experiment.instrument = {};
			$scope.experiment.instrumentProperties = undefined;
			$scope.instrumentType = undefined;
		}
		$scope.experimentTypeTemplate = undefined;		
	};
	
	$scope.loadTemplate = function(){
		console.log("call loadTemplate see getTemplate() in old version");
		if($scope.experimentType && $scope.experiment.instrument && $scope.experiment.instrument.outContainerSupportCategoryCode){
			$scope.experimentTypeTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getTemplate($scope.experimentType.atomicTransfertMethod.toLowerCase(),$scope.experiment.instrument.outContainerSupportCategoryCode,$scope.experimentType.code).url;
		}else{
			$scope.experimentTypeTemplate =  undefined;
		}				
	};
	
	var resetBasket = function(){
		if(mainService.getBasket())mainService.getBasket().reset();
	};
	
	var loadInstrumentType = function(code){
		$http.get(jsRoutes.controllers.instruments.api.InstrumentUsedTypes.get(code).url)
			.success(function(data, status, headers, config) {
				$scope.instrumentType = data;
				updateInstrumentIfNeeded();				
				mainService.put("instrumentType",$scope.instrumentType);				
				
			})
			.error(function(data, status, headers, config) {
				$scope.messages.setError("get");
			});
	};
	
	var updateInstrumentIfNeeded = function(){
		var instrument = $scope.experiment.instrument;
		if(undefined === instrument.typeCode){
			instrument.typeCode = $scope.instrumentType.code;
		}
		if(undefined === instrument.categoryCode){
			instrument.categoryCode = $scope.instrumentType.category.code;
		}
		if(undefined === instrument.inContainerSupportCategoryCode || null === instrument.inContainerSupportCategoryCode){
			instrument.inContainerSupportCategoryCode = getInContainerSupportCategoryCode();
		}
		if(undefined === instrument.outContainerSupportCategoryCode){
			instrument.outContainerSupportCategoryCode = getOutContainerSupportCategoryCode();
		}
	};
	
	
	var getInContainerSupportCategoryCode = function(){
		if(mainService.getBasket() && mainService.getBasket().get() && mainService.getBasket().get()[0]){
			return mainService.getBasket().get()[0].support.categoryCode; 
		}else {
			return $parse('atomicTransfertMethods[0].inputContainerUseds[0].locationOnContainerSupport.categoryCode')($scope.experiment);			
		}
	};
	
	
	var getOutContainerSupportCategoryCode = function(){
		if($scope.isOutputATMVoid()){
			return "void";					
		}else{
			return undefined
		}
	};
	
	var updateData = function(){
		if($scope.experiment.code){
			$http.get(jsRoutes.controllers.experiments.api.Experiments.get($scope.experiment.code).url).success(function(data) {
				$scope.experiment = data;		
				mainService.put("experiment",$scope.experiment);
			});
		}		
	};
	
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
		$scope.lists.refresh.kitCatalogs({"experimentTypeCodes":$scope.experiment.typeCode});
		$scope.lists.refresh.experimentCategories();
	};
	
	var creationMode = false;
	var saveInProgress = false;
	var additionnalButtons = [];
	
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
				creationMode = true;
				$scope.startEditMode();
				defaultExperiment = {
					state : {
						resolutionCodes : [],
						code : "N"
					},
					reagents : [],
					atomicTransfertMethods : [],
					comments : [],
					typeCode:$routeParams.typeCode,
					instrument:{}
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
			
			// experiment.state.code = 'N';
			
			return experiment;
		}).then(function(experiment){
			// console.log(experiment);
			
			$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.get(experiment.typeCode).url).error(function(data, status, headers, config) {
				$scope.messages.setError("get");
			}).then(function(result) {
				$scope.experiment = experiment;
				$scope.experimentType = result.data;
				
				$scope.experiment.typeCode =  $scope.experimentType.code;
				$scope.experiment.categoryCode = $scope.experimentType.category.code;
				
				clearLists();
				if($scope.experiment.instrument && $scope.experiment.instrument.typeCode){
					loadInstrumentType($scope.experiment.instrument.typeCode);
				}
				
				mainService.put("experiment",$scope.experiment);
				mainService.put("experimentType",$scope.experimentType);
				
				
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
			        	 mode:'local'
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
						 * otherButtons:{ active:true, template:'<button
						 * class="btn btn btn-info"
						 * ng-click="addNewReagentLine()"
						 * title="'+Messages("experiments.addNewReagentLine")+'">'+Messages("experiments.addNewReagentLine")+'</button>' }
						 */
	};

	$scope.scan = function(e, property, propertyName){
		// console.log(property);
		// console.log(e);
		if(e.keyCode === 9 || e.keyCode === 13){
			property[propertyName] += '_';
			// console.log(property);
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
	 * $scope.addNewReagentLine = function(){ $scope.datatableReagent.save();
	 * $scope.datatableReagent.addData([{}]); $scope.datatableReagent.setEdit(); };
	 */
	
	$scope.datatableReagent = datatable(datatableConfigReagents);
	
	$scope.$watch('experiment', function() {
		console.log('watch experiment on reagents')
		if (angular.isDefined($scope.experiment)){
			if($scope.experiment.reagents === null || $scope.experiment.reagents === undefined || $scope.experiment.reagents.length === 0){
				$scope.datatableReagent.setData([]);				
			}else{
				$scope.datatableReagent.setData($scope.experiment.reagents);
			}
		}
	
	});

	$scope.$on('saveReagents', function(e, callbackFunction) {	
		console.log("call event save for reagents");
		$scope.datatableReagent.save()
		$scope.experiment.reagents = $scope.datatableReagent.getData();
		$scope.$emit('reagentsSaved', callbackFunction);
	});
	
}]).controller('CommentsCtrl',['$scope','$sce', '$http','lists','$parse','$filter','datatable', 
                               function($scope,$sce,$http,lists,$parse,$filter,datatable) {

	$scope.currentComment = {comment:undefined};
	
	$scope.convertToBr = function(text){
		return $sce.trustAsHtml(text.replace(/\n/g, "<br>"));
	};
	
	
	
	$scope.save = function(){	
		if($scope.isCreationMode()){
			$scope.experiment.comments.push($scope.currentComment);
			$scope.currentComment = {comment:undefined};
		}else{
			$scope.messages.clear();
			$http.post(jsRoutes.controllers.experiments.api.ExperimentComments.save($scope.experiment.code).url, $scope.currentComment)
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
		}		
	};
	
	$scope.isUpdate = function(){
		return ($scope.index != undefined);		
	};
	
	$scope.setUpdate = function(comment, index){
		$scope.currentComment = angular.copy(comment);
		$scope.index = index;
	};
	
	$scope.update = function(){		
		if($scope.isCreationMode()){
			$scope.experiment.comments[$scope.index] = $scope.currentComment;
			$scope.currentComment = {comment:undefined};
			$scope.index = undefined;			
		}else{	
			$scope.messages.clear();
			$http.put(jsRoutes.controllers.experiments.api.ExperimentComments.update($scope.experiment.code, $scope.currentComment.code).url, $scope.currentComment)
			.success(function(data, status, headers, config) {
				if(data!=null){
					$scope.messages.setSuccess("save");
					$scope.experiment.comments[$scope.index] = $scope.currentComment;
					$scope.currentComment = {comment:undefined};
					$scope.index = undefined;
				}
			})
			.error(function(data, status, headers, config) {
				$scope.messages.setError("save");
				$scope.messages.setDetails(data);
			});
		}
	};
	
	$scope.remove = function(comment, index){
		if($scope.isCreationMode()){
			$scope.currentComment = {comment:undefined};
			$scope.experiment.comments.splice(index, 1);
		}else if (confirm(Messages("comments.remove.confirm"))) {
			$scope.messages.clear();
			$http.delete(jsRoutes.controllers.experiments.api.ExperimentComments.delete($scope.experiment.code, comment.code).url)
			.success(function(data, status, headers, config) {
				if(data!=null){
					$scope.messages.setSuccess("save");
					$scope.currentComment = {comment:undefined};
					$scope.experiment.comments.splice(index, 1);
				}
			})
			.error(function(data, status, headers, config) {
				$scope.messages.setError("remove");
				$scope.messages.setDetails(data);				
			});
		}
	};
}]);

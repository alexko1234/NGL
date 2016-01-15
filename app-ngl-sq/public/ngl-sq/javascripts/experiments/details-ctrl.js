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
	
	$scope.isDispactchModalAvailable = function(){
		return $scope.isFinishState();
	};
	
	$scope.initDispatchModal = function(){
		$scope.messages.clear();
		$scope.$broadcast('initDispatchModal');
	};
	
	$scope.$on('initDispatchModalDone', function(e) {
		angular.element('#finalDispatchModal').modal('show');
	});
	
	$scope.$on('dispatchDone', function(e) {
		$scope.messages.setSuccess("save");
		updateData();
		angular.element('#finalDispatchModal').modal('hide');
	});
	
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
		$scope.$broadcast('cancel');
		$scope.messages.clear();
		mainService.stopEditMode();
		finishEditMode=false;
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
		
		if($scope.experiment.status.valid !== 'UNSET'){
			
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
					$scope.initDispatchModal();
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
	
	var finishEditMode = false;
	$scope.isFinishEditMode = function(){		
		return finishEditMode;	
	};
	
	$scope.activeFinishEditMode = function(){		
		finishEditMode=true;
	};
	
	
	$scope.isWorkflowModeAvailable = function(nextStateCode){
		if($scope.experiment !== undefined){
			return (nextStateCode === 'IP' && $parse('experiment.state.code')($scope) === "N" 
				|| nextStateCode === 'F' && $parse('experiment.state.code')($scope) !== "F");
		}else{
			return false;
		}
	};
	
	$scope.isEditModeAvailable = function(){		
		if($scope.experiment !== undefined){
			return ($parse('experiment.state.code')($scope) !== "F");
		}else{
			return false;
		}		
	};
	
	$scope.isNewState = function(){				
		return ($parse('experiment.state.code')($scope) === "N");
	};
	
	$scope.isInProgressState = function(){				
		return ($parse('experiment.state.code')($scope) === "IP");
	};
	
	$scope.isFinishState = function(){				
		return ($parse('experiment.state.code')($scope) === "F");
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
				$scope.$broadcast('refresh');
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
			creationMode = true;
			$scope.startEditMode();
			
			if(!defaultExperiment){
				defaultExperiment = {
					state : {
						resolutionCodes : [],
						code : "N"
					},
					status : {
						valid : "UNSET"
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
	
	$scope.cancel = function(){	
		$scope.currentComment = {comment:undefined};
		$scope.index = undefined;
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
}]).controller('DispatchCtrl',['$scope', '$http','$q','$parse','lists','mainService','datatable','commonAtomicTransfertMethod', 
                               function($scope,$http,$q,$parse,lists,mainService,datatable, commonAtomicTransfertMethod) {
	console.log("Dispatch Ctrl");
	
	var datatableConfig = {
			name:"dispatch",
			columns:[			  
					 {
			        	 "header":Messages("containers.table.support.code"),
			        	 "property":"container.support.code +' / '+container.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1
			         },		         
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "container.projectCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":2,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>"
				     },
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "container.sampleCodes",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":3,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>"
				     },
				     {
			        	"header":Messages("containers.table.tags"),
			 			"property": "container.contents",
			 			"filter": "getArray:'properties.tag.value'",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"position":4,
			 			"render":"<div list-resize='cellValue | unique' ' list-resize-min-size='3'>"
				      },
				      {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"container.state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":7
				       },
				       {
			        	 "header":Messages("containers.table.status"),
			        	 "property":"status",
			        	 "order":false,
						 "edit":true,
						 "hide":false,
			        	 "type":"text",
			        	 "filter":"codes:'valuation'",
			        	 "choiceInList":true,
					     "listStyle":"bt-select",
					     "possibleValues":"lists.get('status')",					     
			        	 "position":8
					    },
					    {
			        	 "header":Messages("containers.table.dispatch"),
			        	 "property":"dispatch",
			        	 "order":false,
						 "edit":true,
						 "hide":false,
			        	 "type":"text",
			        	 "choiceInList":true,
					     "listStyle":"radio",
					     "possibleValues":"getDispatchValues()",
					     "editDirectives":"ng-if='isDispatchValueAvailable(opt.code, value)'",
			        	 "position":9
						},
						{
			        	 "header":Messages("containers.table.processResolutions"),
			        	 "property":"processResolutions",
			        	 "order":false,
						 "edit":true,
						 "hide":false,
			        	 "type":"text",
			        	 "choiceInList":true,
					     "listStyle":"bt-select-multiple",
					     "possibleValues":"lists.get('processResolutions')",
					     "editDirectives":"ng-if='isProcessResolutionsMustBeSet(value)'",
					     "position":10
						}					   				       				      				     
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
				by:'code'
			},
			remove:{
				active:false,
			},
			save:{
				active:true,
	        	withoutEdit: true,
	        	showButton:false,
	        	mode:'local',
	        	changeClass: false,
	        	keepEdit: true,
	        	callback:function(datatable){
	        		
	        		//beginning of algo
	        		var data = datatable.getData();
	        		var isError = false;
	        		for(var i = 0 ; i < data.length ; i++){
	        			if(data[i].dispatch === undefined || data[i].dispatch === null){
	        				datatable.addErrorsForKey(i, {"dispatch":[Messages("containers.dispatch.value.mandatory")]}, "dispatch");
	        				isError = true;
	        			}
	        			if(data[i].status === undefined || data[i].status === null || data[i].status === 'UNSET'){
	        				datatable.addErrorsForKey(i, {"status":[Messages("containers.status.value.mandatory")]}, "status");
	        				isError = true;
	        			}
	        		}
	        		
	        		if(!isError && !$scope.isOutputATMVoid()){
	        			callbackSaveForOutputContainer(datatable);
	        		}else if(!isError){
	        			callbackSaveForInputContainer(datatable);
	        		}
	        	}
	        		
			},
			edit:{
				active: true,
				columnMode:true,
				byDefault: true,
				showButton:false
			},
			hide:{
				active:false
			},
			messages:{
				active:false,
				columnMode:true
			},			
			extraHeaders:{
				number:2,
				dynamic:true,
			},
			mergeCells: {
                 active: false
            },
            showTotalNumberRecords: false,
	};
	
	$scope.isDispatchValueAvailable = function(dispatchCode, value){
		if(value !== undefined){
			var dvet = dispatchValuesForExperimentType[value.data.container.fromExperimentTypeCodes[0]];						
			if($scope.isOutputATMVoid()){
				dvet = dispatchValuesForExperimentType[$scope.experiment.typeCode];
    		}
			
			if(value.data.status === 'FALSE' && (value.data.dispatch !== 5 && value.data.dispatch !== 6)){
				value.data.dispatch = undefined;
			}else if(value.data.status === 'TRUE' && (value.data.dispatch === 5 || value.data.dispatch === 6)){
				value.data.dispatch = undefined;
			}else if(value.data.status === 'UNSET'){
				value.data.dispatch = undefined;
			}else if(dvet && dvet.indexOf(dispatchCode) === -1 && value.data.dispatch === dispatchCode){
				value.data.dispatch = undefined;
			}
			
			if(dvet && dvet.indexOf(dispatchCode) !== -1){
				if(value.data.status === 'FALSE' && (dispatchCode === 5 || dispatchCode === 6)){
					return true;
				}else if(value.data.status === 'TRUE' && dispatchCode !== 5 && dispatchCode !== 6){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
			
			
		}else{
			return true;
		}
	};
	
	
	var getContainerStateRequests = function(containerCodes, stateCode){
		var containerPromises = [];
		containerCodes.forEach(function(value){
			this.push($http.put(jsRoutes.controllers.containers.api.Containers.updateState(value).url,{code:stateCode}));			
		},containerPromises);
		
		return containerPromises;
		
	};
	
	var getContainerSupportStateRequests = function(supportCodes, stateCode){
		var supportPromises = [];
		supportCodes.forEach(function(value){
			this.push($http.put(jsRoutes.controllers.containers.api.ContainerSupports.updateState(value).url,{code:stateCode}));
		},supportPromises);
		
		return supportPromises;
	};
	var getProcessStateRequests = function(processCodes, stateCode, resolutionCodes){
		var processPromises = [];
		processCodes.forEach(function(value){
			this.push($http.put(jsRoutes.controllers.processes.api.Processes.updateState(value).url,{code:stateCode, resolutionCodes:resolutionCodes}));
		},processPromises);	        			
		return processPromises;	        			
	};
	
	var getInputStateForRetry = function(){
		var stateCode = 'A-TM';
		switch($scope.experiment.categoryCode){
			case "qualitycontrol": 
				stateCode = 'A-QC';
				break;
			case "transfert":
				stateCode = 'A-TF';
				break;
			case "purification":
				stateCode = 'A-PF';
				break;
			case "transformation":
				stateCode = 'A-TM';
				break;								   		        			
		} 
		return stateCode;
	};
	
	var callbackSaveForInputContainer = function(datatable){
		//usable function
		var getXCodes = function(inputContainer){
			var codes = {	
					inputContainerCode:inputContainer.code, 
					inputSupportCode:inputContainer.support.code, 
					processCodes:inputContainer.inputProcessCodes
			};
			
			return codes;
		};
		
		var containers = [], supports = [], processes = [];
		
		//update input container and support, //update container container and support, // update process
		var containerPromises = [];
		var supportPromises = [];
		var processPromises = [];
		var data = datatable.getData();
		for(var i = 0 ; i < data.length ; i++){
			
			var codes = getXCodes(data[i].container);
			
			if(data[i].status === 'TRUE'){
				containerPromises = containerPromises.concat(getContainerStateRequests([codes.inputContainerCode], "IS"));
				supportPromises = supportPromises.concat(getContainerSupportStateRequests([codes.inputSupportCode], "IS"));
				if(data[i].dispatch === 0){
					if($scope.isProcessResolutionsMustBeSet({data:data[i]})){
						processPromises = processPromises.concat(getProcessStateRequests(codes.processCodes,"F", data[i].processResolutions));
					}		        					
				}else if(data[i].dispatch === 4){
					processPromises = processPromises.concat(getProcessStateRequests(codes.processCodes,"F", data[i].processResolutions));
				}				
				
			}else if(data[i].status === 'FALSE'){
				if(data[i].dispatch === 5){
					containerPromises = containerPromises.concat(getContainerStateRequests([codes.inputContainerCode], getInputStateForRetry()));
					supportPromises = supportPromises.concat(getContainerSupportStateRequests([codes.inputSupportCode], getInputStateForRetry()));
					
				}else if(data[i].dispatch === 6){		
					containerPromises = containerPromises.concat(getContainerStateRequests([codes.inputContainerCode], "IS"));
					supportPromises = supportPromises.concat(getContainerSupportStateRequests([codes.inputSupportCode], "IS"));
					
					processPromises = processPromises.concat(getProcessStateRequests(codes.processCodes,"F", data[i].processResolutions));
				}
			}
									
		}
		//TODO Bash Mode to manage when one error
		$q.all(containerPromises).then(function(result){
			console.log("all containerPromises done TODO error management");
			$scope.$emit('dispatchDone');
		});
		
		$q.all(supportPromises).then(function(result){
			console.log("all supportPromises done TODO error management");
		});
		
		$q.all(processPromises).then(function(result){
			console.log("all processPromises done TODO error management");
		});		
	};
	var callbackSaveForOutputContainer = function(datatable){
		//usable function
		var getXCodes = function(outputContainer){
			var codes = {	
					outputContainerCode:outputContainer.code, 
					inputContainerCodes:[], 
					outputSupportCode:outputContainer.support.code, 
					inputSupportCodes:[], 
					processCodes:outputContainer.inputProcessCodes
			};
			
			inputContainers = outputContainer.treeOfLife.from.containers;
			for(var i = 0 ; i < inputContainers.length ; i++){
				codes.inputContainerCodes.push(inputContainers[i].code);
				if(codes.inputSupportCodes.indexOf(inputContainers[i].supportCode) === -1){
					codes.inputSupportCodes.push(inputContainers[i].supportCode);
				}
			}
			return codes;
		};
		
		var containers = [], supports = [], processes = [];
		
		//update input container and support, //update container container and support, // update process
		var containerPromises = [];
		var supportPromises = [];
		var processPromises = [];
		
		var data = datatable.getData();
		for(var i = 0 ; i < data.length ; i++){
			
			var codes = getXCodes(data[i].container);
			
			if(data[i].status === 'TRUE'){
				containerPromises = containerPromises.concat(getContainerStateRequests(codes.inputContainerCodes, "IS"));
				supportPromises = supportPromises.concat(getContainerSupportStateRequests(codes.inputSupportCodes, "IS"));
				var outputStateCode = null;
				if(data[i].dispatch === 0){
					if($scope.isProcessResolutionsMustBeSet({data:data[i]})){
						outputStateCode = "IW-P";
						processPromises = processPromises.concat(getProcessStateRequests(codes.processCodes,"F", data[i].processResolutions));
					}else{
						outputStateCode = "A-TM";
					} 		        					
				}else if(data[i].dispatch === 1){
					outputStateCode = "A-TF";
				}else if(data[i].dispatch === 2){
					outputStateCode = "A-QC";
				}else if(data[i].dispatch === 3){
					outputStateCode = "A-PF";
				}else if(data[i].dispatch === 4){
					outputStateCode = "IS";
					processPromises = processPromises.concat(getProcessStateRequests(codes.processCodes,"F", data[i].processResolutions));
				}
				
				if(null !== outputStateCode){
					containerPromises = containerPromises.concat(getContainerStateRequests([codes.outputContainerCode], outputStateCode));
					supportPromises = supportPromises.concat(getContainerSupportStateRequests([codes.outputSupportCode], outputStateCode));
				}else{
					console.log("ERROR no outputStateCode");
				}
				
			}else if(data[i].status === 'FALSE'){
				if(data[i].dispatch === 5){
					containerPromises = containerPromises.concat(getContainerStateRequests(codes.inputContainerCodes, getInputStateForRetry()));
					supportPromises = supportPromises.concat(getContainerSupportStateRequests(codes.inputSupportCodes, getInputStateForRetry()));
					
					containerPromises = containerPromises.concat(getContainerStateRequests([codes.outputContainerCode], "UA"));
					supportPromises = supportPromises.concat(getContainerSupportStateRequests([codes.outputSupportCode], "UA"));
					
				}else if(data[i].dispatch === 6){		
					containerPromises = containerPromises.concat(getContainerStateRequests(codes.inputContainerCodes, "IS"));
					supportPromises = supportPromises.concat(getContainerSupportStateRequests(codes.inputSupportCodes, "IS"));
					
					containerPromises = containerPromises.concat(getContainerStateRequests([codes.outputContainerCode], "UA"));
					supportPromises = supportPromises.concat(getContainerSupportStateRequests([codes.outputSupportCode], "UA"));
					
					processPromises = processPromises.concat(getProcessStateRequests(codes.processCodes,"F", data[i].processResolutions));
				}
			}
			
		}
		//TODO Bash Mode to manage when one error
		$q.all(containerPromises).then(function(result){
			console.log("all containerPromises done TODO error management");
			$scope.$emit('dispatchDone');
		});
		
		$q.all(supportPromises).then(function(result){
			console.log("all supportPromises done TODO error management");
		});
		
		$q.all(processPromises).then(function(result){
			console.log("all processPromises done TODO error management");
		});
		
	}
	
	var dispatchValues;
	var dispatchValuesForExperimentType = [];
	$scope.getDispatchValues = function(){
		return dispatchValues;		
	};
	
	var processTypes = {};
	$scope.isProcessResolutionsMustBeSet = function(value){
		//TODO GA rename to fromTransformationCodes
		if(value !== undefined){
			var fromTransformationTypeCode = ($scope.isOutputATMVoid())?$scope.experiment.typeCode:value.data.container.fromExperimentTypeCodes[0];
			if(value.data.dispatch === 6 || 
					((value.data.dispatch === 0 || value.data.dispatch === 4) 
							&& fromTransformationTypeCode === processTypes[value.data.container.processTypeCodes[0]].lastExperimentType.code)){
				return true;
			}else{
				value.data.processResolutions = undefined;
				return false;
			}
		}else{
			return true;
		}
	};
	
	$scope.$on('initDispatchModal', function(e) {	
		
		if($parse('experiment.state.code')($scope) === 'F'){
			
			var atmService = commonAtomicTransfertMethod($scope);
			$scope.lists.refresh.resolutions({"objectTypeCode":"Process"}, "processResolutions");
			
			if(dispatchValues === undefined){
				dispatchValues = [];
				for(var i = 0; i <= 6 ; i++){
					if($scope.experimentType.atomicTransfertMethod !== "OneToMany" || 
							($scope.experimentType.atomicTransfertMethod === "OneToMany" && i !== 5)){
						dispatchValues.push({"code":i,"name":Messages("containers.dispatch.value."+i)});
					}
											
				}
			}
			
			var initDisplayValues = function(fromExperimentTypeCodes){
				
				var fromTransformationTypeCode = fromExperimentTypeCodes[0];				
				if(undefined === dispatchValuesForExperimentType[fromTransformationTypeCode]){	
					dispatchValuesForExperimentType[fromTransformationTypeCode] = [];
					$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.list().url,{params:{previousExperimentTypeCode:fromTransformationTypeCode}})
						.success(function(data, status,headers,config){
							var isNextExperimentType = (data.length > 0) ? true:false;	
							//extract the node to have their configuration
							$http.get(jsRoutes.controllers.experiments.api.ExperimentTypeNodes.get(fromTransformationTypeCode).url)
								.success(function(data, status,headers,config){
									for(var i = 0; i <= 6 ; i++){
										if((i === 1 && data.doTransfert) || 
												(i === 2 && data.doQualityControl) || 
													(i === 3 && data.doPurification) ||
														(i === 4 && !isNextExperimentType) ||
														(i === 0 && isNextExperimentType) ||
													i > 4){
											dispatchValuesForExperimentType[data.code].push(i);
										}
									}
								});
						});
				}				
			};
					
			if(!$scope.isOutputATMVoid()){
				atmService.loadOutputContainerFromAtomicTransfertMethods($scope.experiment.atomicTransfertMethods).then(function (result) {
					
					var getValidStatus = function(){
						return $scope.experiment.status.valid;
					};
					
					if(result.output){
						var outputContainers = [];
						var processTypeCodes = [];
						
						var containers = result.output;
						for(var key in containers){
							if(containers[key].state.code === 'N'){
								outputContainers.push({container:containers[key], status:getValidStatus(), dispatch:undefined, processResolutions:[]});
								processTypeCodes = processTypeCodes.concat(containers[key].processTypeCodes);
								initDisplayValues(containers[key].fromExperimentTypeCodes);
							}
						}
							
						$scope.containersDT = datatable(datatableConfig);
						$scope.containersDT.setData(outputContainers);
						
						$http.get(jsRoutes.controllers.processes.api.ProcessTypes.list().url,{params:{codes:processTypeCodes}})
							.success(function(data, status,headers,config){
								data.forEach(function(value){
									this[value.code] = value;
								}, processTypes);
								
							});
						
					}
					//console.log("outputContainers : "+outputContainers.length);
				});
			}else {
				atmService.loadInputContainerFromAtomicTransfertMethods($scope.experiment.atomicTransfertMethods).then(function (result) {
					
					var getValidStatus = function(){
						return $scope.experiment.status.valid;
					};
					
					if(result.input){
						var inputContainers = [];
						var processTypeCodes = [];
						
						var containers = result.input;
						initDisplayValues([$scope.experiment.typeCode]);
						for(var key in containers){
							inputContainers.push({container:containers[key], status:getValidStatus(), dispatch:undefined, processResolutions:[]});
							processTypeCodes = processTypeCodes.concat(containers[key].processTypeCodes);								
						}
							
						$scope.containersDT = datatable(datatableConfig);
						$scope.containersDT.setData(inputContainers);
						
						$http.get(jsRoutes.controllers.processes.api.ProcessTypes.list().url,{params:{codes:processTypeCodes}})
							.success(function(data, status,headers,config){
								data.forEach(function(value){
									this[value.code] = value;
								}, processTypes);
								
							});
						
					}//TODO les void and when ok
					
					console.log("inputContainers : "+inputContainers.length);
				});
			}
			$scope.$emit('initDispatchModalDone');
		}
	});

	
}]);

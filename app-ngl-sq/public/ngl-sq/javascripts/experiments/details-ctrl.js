angular.module('home').controller('DetailsCtrl',['$scope','$sce', '$window','$http','$parse','$q','$position','$routeParams','$location','$filter',
                                                 'mainService','tabService','lists','datatable', 'messages','valuationService',
                                                  function($scope,$sce,$window, $http,$parse,$q,$position,$routeParams,$location,$filter,
                                                		  mainService,tabService,lists,datatable, messages,valuationService) {
	var enableValidation = false;
	$scope.getHasErrorClass = function(formName, property){
		if(enableValidation
			&& $scope[formName] 
			&& $scope[formName][property] 
			&& $scope[formName][property].$error 
			&& $scope[formName][property].$error.required){
			return 'has-error';
		}else{
			return undefined;
		}
	}
	
	$scope.isRequired=function(propertyDefinition){
		if($scope.experiment !== undefined){
		if(propertyDefinition.required 
				&& (
					($scope.isCreationMode() && $parse('experiment.state.code')($scope) === "N"  
							&& propertyDefinition.requiredState === 'N')
					|| (!$scope.isCreationMode() && $parse('experiment.state.code')($scope) === "N"  
							&& (propertyDefinition.requiredState === null || propertyDefinition.requiredState === 'N' || propertyDefinition.requiredState === 'IP'))
					|| (($parse('experiment.state.code')($scope) === "IP" || $parse('experiment.state.code')($scope) === "F"))
				))
				return true;
		}
		return false;
	}
	
		
	$scope.updateInstrumentProperty = function(pName){
		$scope.$broadcast('updateInstrumentProperty', pName);
	}
	$scope.updateExperimentProperty = function(pName){
		$scope.$broadcast('updateExperimentProperty', pName);
	}
	
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
	$scope.setAdditionnalButtons = function(buttons){
		additionnalButtons = buttons;
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
	
	$scope.openPrintTagsPage = function(){
		$window.open(jsRoutes.controllers.printing.tpl.Printing.home("tags").url+"?experimentCode="+$scope.experiment.code, 'tags');
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
	
	$scope.$on('dispatchDone', function(e, args) {
		
		if(args.isErrors){
			$scope.messages.setError("save");
			$scope.messages.setDetails(args.errors);
			$scope.messages.showDetails = true;				
		}else{
			$scope.messages.setSuccess("save");
		}
		
		if(args.newExperiment){
			var creationMode = false;
			var saveInProgress = false;
			var additionnalButtons = [];
			$routeParams.code="new";
			$routeParams.typeCode=args.newExperimentTypeCode;
			
			tabService.resetTabs();			
			tabService.addTabs({label:Messages('experiments.tabs.create'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("new").url,remove:false});
			tabService.activeTab(1);
			init();
		}else{
			updateData();
		}
		
		
		angular.element('#finalDispatchModal').modal('hide');
	});
	
	$scope.activeEditMode = function(){
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
			form.containerSupportCategory = $parse("instrument.inContainerSupportCategoryCode")($scope.experiment);
			mainService.setForm(form);		
		}
		$scope.$broadcast('activeEditMode');
		
	};
	
	$scope.cancel = function(){
		$scope.$broadcast('cancel');
		$scope.messages.clear();
		mainService.stopEditMode();
		finishEditMode=false;
		saveInProgress = false;
		enableValidation = false;
		updateData();
		
		if(mainService.isHomePage('search') 
				&& tabService.isBackupTabs() ){
			$scope.restoreBackupTabs();
			$scope.activeTab(1);
			$scope.setDatatable(undefined);	
			
			var form = {};
			form.typeCode = $scope.experimentType.code;
			form.categoryCode = $scope.experimentType.category.code;
			
			mainService.setForm(form);								
		}			
	};
	
	$scope.askDeleteExperiment = function(){
		
		angular.element('#deleteModal').modal('show');
		
	};
	
	$scope.deleteExperiment = function(){
		$scope.messages.clear();
		$http.delete(jsRoutes.controllers.experiments.api.Experiments.delete($scope.experiment.code).url)
			.success(function(data, status, headers, config) {
				//$scope.messages.setSuccess("remove");	
				angular.element('#deleteModal').on('hidden.bs.modal', function (e) {
					console.log("call when hide ok");
					$scope.$apply(function(scope){
						tabService.activeTab(tabService.getTab(0),true);
						tabService.removeTab(1);
					});
				});
				angular.element('#deleteModal').modal('hide');
				
			}).error(function(data, status, headers, config) {
				$scope.messages.setError("remove");
				$scope.messages.setDetails(data);
				angular.element('#deleteModal').modal('hide');						
		});
			
	};
	
	$scope.save = function(callbackFunction){
		
		$scope.messages.clear();
		saveInProgress = true;
		$scope.$broadcast('saveReagents', callbackFunction);		
	};
	
	$scope.startExperiment = function(){
		
		$scope.save(function(experiment){
			
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
				if(mainService.isEditMode()){
					$scope.$broadcast('activeEditMode');
				}
			});
		});
	};
	
	
	$scope.finishExperiment = function(){
		
		if($scope.experiment.status.valid !== 'UNSET'){
			
			angular.element('#finalResolutionModal').modal('hide');
			
			$scope.save(function(experiment){
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
					if(mainService.isEditMode()){
						$scope.$broadcast('activeEditMode');
					}
				});			
			});
		}else{
			angular.element('#finalResolutionModal').modal('show');
		}
	};
	
	$scope.$on('reagentsSaved', function(e, callbackFunction) {
		
		$scope.$broadcast('save', callbackFunction);
	});
	
	
	var endSaveSuccess = function(newExperiment){
		resetBasket();					
		mainService.put("experiment",$scope.experiment);
		$scope.experiment = newExperiment;
		$scope.messages.setSuccess("save");						
		mainService.stopEditMode();
		finishEditMode=false;
		saveInProgress = false;
		enableValidation = false;
		
		if(mainService.isHomePage('search') 
				&& tabService.isBackupTabs() ){
			$scope.restoreBackupTabs();
			$scope.activeTab(1);
			$scope.setDatatable(undefined);	
			
			var form = {};
			form.typeCode = $scope.experimentType.code;
			form.categoryCode = $scope.experimentType.category.code;
			
			mainService.setForm(form);								
		}
		
		$scope.$broadcast('refresh'); // utile seulement si l'update fonctionne				
	}
	
	$scope.$on('askRefreshReagents',function(){
		$scope.$broadcast('refreshReagents');
	}); 
	
	$scope.$on('childSaved', function(e, callbackFunction) {
		
		updatePropertyUnit($scope.experiment);
		
		if($scope.allForm.$invalid){
			enableValidation = true;
			$scope.messages.setError("save");
			saveInProgress = false;	
			if(mainService.isEditMode()){
				$scope.$broadcast('activeEditMode');
			}
		}else if(creationMode){
			$http.post(jsRoutes.controllers.experiments.api.Experiments.save().url, $scope.experiment, {callbackFunction:callbackFunction})
				.success(function(data, status, headers, config) {
					
					creationMode = false;
					mainService.setHomePage('search')
					tabService.resetTabs();
					tabService.addTabs({label:Messages('experiments.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("search").url,remove:true});
					tabService.addTabs({label:data.code,href:jsRoutes.controllers.experiments.tpl.Experiments.get(data.code).url,remove:true});
					tabService.activeTab(1);
					
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
					if(mainService.isEditMode()){
						$scope.$broadcast('activeEditMode');
					}
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
				if(mainService.isEditMode()){
					$scope.$broadcast('activeEditMode');
				}
			});			
		}
		
	});
	
	var finishEditMode = false;
	$scope.isFinishEditMode = function(){		
		return finishEditMode;	
	};
	
	$scope.activeFinishEditMode = function(){		
		finishEditMode=true;
		$scope.$broadcast('activeFinishEditMode');
	};
	
	
	$scope.isWorkflowModeAvailable = function(nextStateCode){
		if($scope.experiment !== undefined){
			return ((nextStateCode === 'IP' && $parse('experiment.state.code')($scope) === "N") 
				|| (nextStateCode === 'F' && $parse('experiment.state.code')($scope) !== "F")
					|| (nextStateCode === 'F' && Permissions.check("admin")));
		}else{
			return false;
		}
	};
	
	$scope.isEditModeAvailable = function(){		
		if($scope.experiment !== undefined){
			return ($parse('experiment.state.code')($scope) !== "F" || Permissions.check("admin"));
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
		if($scope.experimentType && $scope.experiment.instrument && $scope.experiment.instrument.outContainerSupportCategoryCode){
			$scope.experimentTypeTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getTemplate($scope.experimentType.atomicTransfertMethod.toLowerCase(),
					$scope.experiment.instrument.outContainerSupportCategoryCode,
					$scope.experimentType.category.code,
					$scope.experimentType.code).url;
		}else{
			$scope.experimentTypeTemplate =  undefined;
		}				
	};
	
	$scope.plateUtils = {
			/**
			 * Compute A1, B1, C1, etc.
			 */
			computeColumnMode : function(atmService, udt, maxLine){
				var wells = udt.displayResult;
				var nbCol = 12;
				var nbLine = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
				var x = 0;
				for(var i = 0; i < nbCol ; i++){
					for(var j = 0; j < nbLine.length && j <= maxLine; j++){
						if(x < wells.length && x < 96){
							wells[x].data.outputContainerUsed.locationOnContainerSupport.line = nbLine[j]+'';
							wells[x].data.outputContainerUsed.locationOnContainerSupport.column = i+1;					
						}
						x++;
					}
				}		
			},
			
			/**
			 * Compute A1, A2, A3, etc.
			 */
			computeLineMode : function(atmService, udt, maxColumn){
				var wells = udt.displayResult;
				var nbCol = 12;
				var nbLine = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
				var x = 0;
				for(var j = 0; j < nbLine.length; j++){
					for(var i = 0; i < nbCol && i <= maxColumn; i++){
						if(x < wells.length && x < 96){
							wells[x].data.outputContainerUsed.locationOnContainerSupport.line = nbLine[j]+'';
							wells[x].data.outputContainerUsed.locationOnContainerSupport.column = i+1;					
						}
						x++;
					}
				}		
			},
			copyMode : function(atmService, udt){
				var wells = udt.displayResult;
				for(var j = 0; j < wells.length; j++){
					if(wells[j].data.inputContainerUsed.categoryCode ==='well'){
						wells[j].data.outputContainerUsed.locationOnContainerSupport.line = wells[j].data.inputContainerUsed.locationOnContainerSupport.line
						wells[j].data.outputContainerUsed.locationOnContainerSupport.column = wells[j].data.inputContainerUsed.locationOnContainerSupport.column
					}
				}
				
			},
			plateCells : undefined,
			computePlateCells : function(atmService){
				var plateCells = [];
				var wells = atmService.data.displayResult;
				angular.forEach(wells, function(well){
					var containerUsed = undefined;
					if($scope.experimentType.atomicTransfertMethod === 'OneToVoid'){
						containerUsed = well.data.inputContainerUsed;
					}else{
						containerUsed = well.data.outputContainerUsed;
					}
					
					var line = containerUsed.locationOnContainerSupport.line;
					var column = containerUsed.locationOnContainerSupport.column;
					if(line && column){
						if(plateCells[line] == undefined){
							plateCells[line] = [];
						}
						var sampleCodeAndTags = [];
						angular.forEach(containerUsed.contents, function(content){
							var value = content.projectCode+" / "+content.sampleCode;
							
							if(content.properties && content.properties.libProcessTypeCode){
								value = value +" / "+content.properties.libProcessTypeCode.value;
							}
							
							if(content.properties && content.properties.tag){
								value = value +" / "+content.properties.tag.value;
							}
							
							sampleCodeAndTags.push(value);
						});
						plateCells[line][column] = sampleCodeAndTags;
						
					}						
				})	
				this.plateCells = plateCells;
			},
			isPlate:function(){
				if($scope.experimentType.atomicTransfertMethod === 'OneToVoid'){
					return ($scope.experiment.instrument.inContainerSupportCategoryCode === '96-well-plate');	
				}else{
					return ($scope.experiment.instrument.outContainerSupportCategoryCode === '96-well-plate');
				}								
			},
			isPlateToPlate:function(){
				return ($scope.experiment.instrument.inContainerSupportCategoryCode === '96-well-plate' 
						&& $scope.experiment.instrument.outContainerSupportCategoryCode === '96-well-plate');								
			},
			getPlateCode:function(){
				if($scope.experimentType.atomicTransfertMethod === 'OneToVoid'){
					return $scope.experiment.inputContainerSupportCodes[0];
				}else if($scope.experiment.outputContainerSupportCodes){
					return $scope.experiment.outputContainerSupportCodes[0];
				}
				
								
			},
			/**
			 * Info on plate design
			 */
			getCellPlateData : function(line, column){
				if(this.plateCells && this.plateCells[line] && this.plateCells[line][column]){
					return this.plateCells[line][column];
				}
			},
			templates : {
				buttonLineMode : function(udtName){
					if(!udtName)udtName='atmService.data';
					return ''
					+'<div class="btn-group" style="margin-left:5px" ng-if="plateUtils.isPlate()">'
	            	+'<button class="btn btn-default" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 11)" data-toggle="tooltip" title="'+Messages("experiments.button.plate.computeLineMode")+'"  ng-disabled="!isEditMode()"><i class="fa fa-magic"></i><i class="fa fa-arrow-right"></i></button>'
	            	+'<div class="btn-group" role="group">'
	            	+'<button type="button" title="'+Messages("experiments.button.plate.computeLineMode.advanced")+'" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" ng-disabled="!isEditMode()">'
	            	+'  <span class="caret"></span>'
	            	+'</button>'
	            	+' <ul class="dropdown-menu">'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 0)" >1</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 1)" >2</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 2)" >3</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 3)" >4</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 4)" >5</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 5)" >6</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 6)" >7</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 7)" >8</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 8)" >9</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 9)" >10</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 10)" >11</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeLineMode(atmService, '+udtName+', 11)" >12</a></li>'
	            	+'</ul>'
	            	+'</div>'
	            	+'</div>'
	            },
				buttonColumnMode : function(udtName){
					if(!udtName)udtName='atmService.data';
					return ''
					+'<div class="btn-group" ng-if="plateUtils.isPlate()">'
	            	+'<button class="btn btn-default" ng-click="plateUtils.computeColumnMode(atmService, '+udtName+', 7)" data-toggle="tooltip" title="'+Messages("experiments.button.plate.computeColumnMode")+'" ng-disabled="!isEditMode()"><i class="fa fa-magic"></i><i class="fa fa-arrow-down"></i></button>'
	            	+'<div class="btn-group" role="group">'
	            	+'<button type="button"  title="'+Messages("experiments.button.plate.computeColumnMode.advanced")+'" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" ng-disabled="!isEditMode()">'
	            	+'  <span class="caret"></span>'
	            	+'</button>'
	            	+' <ul class="dropdown-menu">'
	            	+'  <li><a href="#" ng-click="plateUtils.computeColumnMode(atmService, '+udtName+', 0)" >A</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeColumnMode(atmService, '+udtName+', 1)" >B</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeColumnMode(atmService, '+udtName+', 2)" >C</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeColumnMode(atmService, '+udtName+', 3)" >D</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeColumnMode(atmService, '+udtName+', 4)" >E</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeColumnMode(atmService, '+udtName+', 5)" >F</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeColumnMode(atmService, '+udtName+', 6)" >G</a></li>'
	            	+'  <li><a href="#" ng-click="plateUtils.computeColumnMode(atmService, '+udtName+', 7)" >H</a></li>'
	            	+'</ul>'
	            	+'</div>'
	            	+'</div>'
	            },
	            buttonCopyPosition : function(udtName){
	            	if(!udtName)udtName='atmService.data';
	            	return ''
	            	+'<div class="btn-group" ng-if="plateUtils.isPlate()">'
	            	+'<button class="btn btn-default" ng-click="plateUtils.copyMode(atmService, '+udtName+')" data-toggle="tooltip" title="'+Messages("experiments.button.plate.copyMode.title")+'" ng-disabled="!isEditMode()">'
	            	+'	<i class="fa fa-copy"></i> '+Messages("experiments.button.plate.copyMode")
	            	+'</button>'
	            	+'</div>'
	            }
			}
	};
	
	var updatePropertyUnit = function(experiment){
		$scope.experimentType.propertiesDefinitions.forEach(function(propertyDef){
			if(propertyDef.saveMeasureValue){
				experiment.atomicTransfertMethods.forEach(function(atm){
					if(atm.inputContainerUseds){
						atm.inputContainerUseds.forEach(function(icu){
							if(icu.experimentProperties && icu.experimentProperties[propertyDef.code]
								&& (icu.experimentProperties[propertyDef.code].unit === null || icu.experimentProperties[propertyDef.code].unit === undefined)){
								icu.experimentProperties[propertyDef.code].unit = propertyDef.saveMeasureValue.code;
							}
						});
					}
					if(atm.outputContainerUseds){
						atm.outputContainerUseds.forEach(function(ocu){
							if(ocu.experimentProperties && ocu.experimentProperties[propertyDef.code]
							&& (ocu.experimentProperties[propertyDef.code].unit === null || ocu.experimentProperties[propertyDef.code].unit === undefined)){
								ocu.experimentProperties[propertyDef.code].unit = propertyDef.saveMeasureValue.code;
							}
						});
					}
				});
			}
		});		
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
		if(undefined === instrument.typeCode || null === instrument.typeCode){
			instrument.typeCode = $scope.instrumentType.code;
		}
		if(undefined === instrument.categoryCode || null === instrument.categoryCode){
			instrument.categoryCode = $scope.instrumentType.category.code;
		}
		if(undefined === instrument.inContainerSupportCategoryCode || null === instrument.inContainerSupportCategoryCode){
			instrument.inContainerSupportCategoryCode = getInContainerSupportCategoryCode();
		}
		if(undefined === instrument.outContainerSupportCategoryCode|| null === instrument.outContainerSupportCategoryCode){
			instrument.outContainerSupportCategoryCode = getOutContainerSupportCategoryCode();
		}
		
		if($scope.experimentTypeTemplate === undefined){
			$scope.loadTemplate();
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
		$scope.lists.refresh.valuationCriterias({typeCode:$scope.experiment.typeCode,objectTypeCode:"Experiment"});
		$scope.lists.refresh.experimentTypes({categoryCode:$scope.experimentType.category.code},$scope.experimentType.category.code);
		$scope.lists.refresh.instrumentUsedTypes({"experimentTypeCode":$scope.experimentType.code});
		
		if($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')){
			$scope.lists.refresh.protocols({"experimentTypeCode":$scope.experimentType.code,"isActive":true});
		}else {
			$scope.lists.refresh.protocols({"experimentTypeCode":$scope.experimentType.code});

		}
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
		$scope.valuationService= valuationService();
		$scope.dispatchConfiguration = {/*orderBy*/};
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
			        	 "possibleValues": "kitCatalogIsActive()",
			        	 "render":'<div bt-select ng-model="value.data.kitCatalogCode" bt-options="v.code as v.name for v in lists.getKitCatalogs()" ng-edit="false"></div>',
			        	 "edit":true
			         },
			         {
			        	 "header":Messages("reagents.table.boxname"),
			        	 "property":"boxCatalogCode",
			        	 "order":false,
			        	 "type":"text",
			        	 "listStyle":"bt-select-filter",
			        	 "choiceInList":true,
			        	 "possibleValues": 'getBoxCatalogs(value,true)',
			        	 "render":'<div bt-select ng-model="value.data.boxCatalogCode" bt-options="v.code as v.name for v in getBoxCatalogs(value)" ng-edit="false"></div>',			        	 
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
			        	 "header":Messages("reagents.table.reagentname"),
			        	 "property":"reagentCatalogCode",
			        	 "order":false,
			        	 "type":"text",
			        	 "listStyle":"bt-select-filter",
			        	 "choiceInList":true,
			        	 "possibleValues": 'getReagentCatalogs(value)',
			        	 "render":'<div bt-select ng-model="value.data.reagentCatalogCode" bt-options="v.code as v.name for v in getReagentCatalogs(value)" ng-edit="false"></div>',			        	 
			        	 "edit":true
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
			        	 active:Permissions.check("writing")?true:false,
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
			        	 active:Permissions.check("writing")?true:false,
			        	 columnMode:false,
			        	 showButton : false,
			        	 withoutSelect:true,
			        	 byDefault : false
			         },
			         save:{
			        	 active:Permissions.check("writing")?true:false,
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
			        	 active:Permissions.check("writing")?true:false,
			         }
			         /*
						 * otherButtons:{ active:true, template:'<button
						 * class="btn btn btn-info"
						 * ng-click="addNewReagentLine()"
						 * title="'+Messages("experiments.addNewReagentLine")+'">'+Messages("experiments.addNewReagentLine")+'</button>' }
						 */
	};
	
	var listKitCatalogsActive = undefined;
	var listBoxCatalogsActive = undefined;
	
	$scope.kitCatalogIsActive = function() {
		var liste = lists.getKitCatalogs();
		if (listKitCatalogsActive == undefined && liste!=undefined) {
			listKitCatalogsActive = $filter('filter')(liste, {active:true})
		}
		return listKitCatalogsActive;
	}
	
	
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

	$scope.getBoxCatalogs = function(value, isActive){
		
		var kitCatalogCode = $parse("data.kitCatalogCode")(value);
		
		if(null !== kitCatalogCode && undefined !== kitCatalogCode){			
			var key = "boxCatalogs-"+kitCatalogCode+((isActive)?"+-true":"-all");
			
			var result = lists.get(key);
			if(!result){
				if(!isActive){
					result = lists.refresh.boxCatalogs({"kitCatalogCode":kitCatalogCode},key);
				}else{
					result = lists.refresh.boxCatalogs({"kitCatalogCode":kitCatalogCode,"isActive":isActive},key);
				}			
			}	
			return result;
		} 				
	}
	
	$scope.getReagentCatalogs = function(value){
		
		var kitCatalogCode = $parse("data.kitCatalogCode")(value);
		var boxCatalogCode = $parse("data.boxCatalogCode")(value);
		
		if(null !== kitCatalogCode && undefined !== kitCatalogCode
				&& null !== boxCatalogCode && undefined !== boxCatalogCode){
			return lists.getReagentCatalogs({"kitCatalogCode":kitCatalogCode,"boxCatalogCode":boxCatalogCode},"reagentCatalogs-"+kitCatalogCode+"-"+boxCatalogCode);
		}				
	}

	$scope.searchReagents = function(){
		
		//$http.get(jsRoutes.controllers.reagents.api.Reagents.list().url, {params:{"barCode":$scope.searchBarCode, "boxBarCode":$scope.searchBarCode}})
		$http.get(jsRoutes.controllers.reagents.api.KitCatalogs.list().url, {params:{"providerID":$scope.searchBarCode, "boxBarCode":$scope.searchBarCode}})
		.success(function(data, status,headers,config){
			
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
		if (angular.isDefined($scope.experiment)){
			if($scope.experiment.reagents === null || $scope.experiment.reagents === undefined || $scope.experiment.reagents.length === 0){
				$scope.datatableReagent.setData([]);				
			}else{
				$scope.datatableReagent.setData($scope.experiment.reagents);
			}
		}
	
	});
	
	$scope.$on('refreshReagents', function(e, callbackFunction) {	
		console.log("call refresh reagents");
		if (angular.isDefined($scope.experiment)){
			if($scope.experiment.reagents === null || $scope.experiment.reagents === undefined || $scope.experiment.reagents.length === 0){
				$scope.datatableReagent.setData([]);				
			}else{
				$scope.datatableReagent.setData($scope.experiment.reagents);
			}
		}
	});

	$scope.$on('saveReagents', function(e, callbackFunction) {	
		$scope.datatableReagent.save()
		$scope.experiment.reagents = $scope.datatableReagent.getData();
		$scope.$emit('reagentsSaved', callbackFunction);
	});
	
	$scope.$on('cancel', function(e) {
		$scope.datatableReagent.cancel();						
	});
	
	$scope.$on('activeEditMode', function(e) {
		$scope.datatableReagent.selectAll(true);
		$scope.datatableReagent.setEdit();
	});
	
	$scope.$on('activeFinishEditMode', function(e) {
		$scope.datatableReagent.selectAll(true);
		$scope.datatableReagent.setEdit();
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
}]).controller('DispatchCtrl',['$scope', '$http','$q','$parse','$filter','lists','mainService','datatable','commonAtomicTransfertMethod', 
                               function($scope,$http,$q,$parse,$filter,lists,mainService,datatable, commonAtomicTransfertMethod) {
	console.log("Dispatch Ctrl");
	
	getColumns = function(){
		var columns = [];
		if(!$scope.isOutputATMVoid()){
			columns.push({
				"header":Messages("containers.table.support.in.code"),
				"property":"container.treeOfLife.from.containers",
				"render":'<div list-resize="cellValue | collect:\'code\' | unique" list-resize-min-size="3" vertical="true">',
				"order":true,
				"edit":false,
				"hide":true,
				"type":"text",
				"position":0.5
			});
			
			columns.push({
				"header":Messages("containers.table.support.out.code"),
				"property":"container.code",
				"order":true,
				"edit":false,
				"hide":true,
				"type":"text",
				"position":1
			});
		}else{
			columns.push({
				"header":Messages("containers.table.support.in.code"),
				"property":"container.code",
				"order":true,
				"edit":false,
				"hide":true,
				"type":"text",
				"position":1
			});
		}
		columns.push({
	        "header":Messages("containers.table.projectCodes"),
	 		"property": "container.projectCodes",
	 		"order":false,
	 		"hide":true,
	 		"type":"text",
	 		"position":2,
	 		"render":"<div list-resize='cellValue' list-resize-min-size='3'>"
	    });
		columns.push({
        	"header":Messages("containers.table.sampleCodes"),
 			"property": "container.sampleCodes",
 			"order":false,
 			"hide":true,
 			"type":"text",
 			"position":3,
 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>"
	    });
		columns.push({
        	"header":Messages("containers.table.tags"),
 			"property": "container.contents",
 			"filter": "getArray:'properties.tag.value'",
 			"order":true,
 			"hide":true,
 			"type":"text",
 			"position":4,
 			"render":"<div list-resize='cellValue | unique' ' list-resize-min-size='3'>"
	    });
		columns.push({
			"header":Messages("containers.table.state.code"),
			"property":"container.state.code",
			"order":true,
			"edit":false,
			"hide":true,
			"type":"text",
			"filter":"codes:'state'",
			"position":7
		});
		columns.push({
			"header":Messages("containers.table.status"),
			"property":"status",
			"order":false,
			"edit":true,
			"hide":false,
			"type":"text",
			"required":true,
			"filter":"codes:'valuation'",
			"choiceInList":true,
			"listStyle":"bt-select",
			"possibleValues":"lists.get('status')",					     
			"position":8,
			"thClass":"columnWidth70px"
		});
		columns.push({
	       	"header":Messages("containers.table.dispatch"),
	    	"property":"dispatch",
	    	"order":false,
			"edit":true,
			"hide":false,
	    	"type":"text",
	    	"required":true,
	    	"choiceInList":true,
		    "listStyle":"radio",
		    "possibleValues":"getDispatchValues()",
		    "editDirectives":"ng-if='isDispatchValueAvailable(opt.code, value)'",
	    	"position":9
		});
		columns.push({
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
		});		
		
		
		if($scope.experiment.categoryCode === 'qualitycontrol'){
			columns.push({
				 "header":Messages("containers.table.valuationqc.valid"),
	        	 "property":"container.valuation.valid",
	        	 "filter":"codes:'valuation'",
	        	 "order":true,
				 "edit":false,
				 "hide":false,
	        	 "type":"text",
	        	 "position":7.5				
			});	
		}
		
		return columns;
	};
	
	var datatableConfig = {
			name:"dispatch",
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
				//by:'code'
			},
			remove:{
				active:false,
			},
			save:{
				active:Permissions.check("writing")?true:false,
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
				active:Permissions.check("writing")?true:false,
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
			var dvet = dispatchValuesForExperimentType[value.data.container.fromTransformationTypeCodes[0]+"-"+value.data.container.processTypeCodes[0]];						
			if($scope.isOutputATMVoid() && $scope.experiment.categoryCode !== 'qualitycontrol'){
				dvet = dispatchValuesForExperimentType[$scope.experiment.typeCode+"-"+value.data.container.processTypeCodes[0]];
    		}
			
			if(value.data.status === 'FALSE' && (value.data.dispatch !== 5 && value.data.dispatch !== 6)){
				value.data.dispatch = undefined;
			}else if(value.data.status === 'TRUE' && (value.data.dispatch === 5)){
				value.data.dispatch = undefined;
			}else if(value.data.status === 'UNSET'){
				value.data.dispatch = undefined;
			}else if(dvet && dvet.indexOf(dispatchCode) === -1 && value.data.dispatch === dispatchCode){
				value.data.dispatch = undefined;
			}
			
			if(dvet && dvet.indexOf(dispatchCode) !== -1){
				if(value.data.status === 'FALSE' && (dispatchCode === 5 || dispatchCode === 6)){
					return true;
				}else if(value.data.status === 'TRUE' && (dispatchCode !== 5 && dispatchCode !== 6
						|| (dispatchCode === 6 && dvet.indexOf(4) === -1 ))){ //To avoid to have finish and stop together
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
			
			
		}else if(dispatchValuesForExperimentType["all"].indexOf(dispatchCode) > -1){
			return true;
		}
	};
	
	 
	//TODO used cache to optimize the update of support and add priority on stateCode
	var getContainerSupportStateRequests = function(index, supportCodes, stateCode){
		var supportPromises = [];
		supportCodes.forEach(function(value){
			this.push({index:index, data:{code:value, state:{code:stateCode}}});
		},supportPromises);
		
		return supportPromises;
	};
	
	var getContainerStateRequests = function(index, containerCodes, stateCode){
		var containerPromises = [];
		containerCodes.forEach(function(value){
			this.push({index:index, data:{code:value, state:{code:stateCode}}});
		},containerPromises);
		
		return containerPromises;
		
	};
	
	var getProcessStateRequests = function(index, processCodes, stateCode, resolutionCodes){
		var processPromises = [];
		processCodes.forEach(function(value){
			this.push({index:index, data:{code:value, state:{code:stateCode, resolutionCodes:resolutionCodes}}});
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
					processCodes:inputContainer.processCodes
			};
			
			return codes;
		};
		
		var containers = [], processes = [];
		
		//update input container and support, //update container container and support, // update process
		var containerPromises = [];
		var supportPromises = [];
		var processPromises = [];
		var data = datatable.getData();
		for(var i = 0 ; i < data.length ; i++){
			
			var codes = getXCodes(data[i].container);
			
			if(data[i].status === 'TRUE'){
				
				if($scope.experiment.categoryCode === 'qualitycontrol'){
					if(data[i].dispatch === 0){
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "A-TM"));
												
					}else if(data[i].dispatch === 1){
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "A-TF"));
						
					}else if(data[i].dispatch === 2){
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "A-QC"));
						
					}else if(data[i].dispatch === 3){
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode],  "A-PF"));
						
					}else if(data[i].dispatch === 4){
						/*
						if(nextExperimentsForExperimentType[data[i].container.fromTransformationTypeCodes[0]]){
							containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "IW-P"));
						}else{
							containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "IS"));
						}
						*/
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "IW-P"));
						processPromises = processPromises.concat(getProcessStateRequests(i, codes.processCodes,"F", data[i].processResolutions));
					}else if(data[i].dispatch === 6){
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "IS"));
						processPromises = processPromises.concat(getProcessStateRequests(i, codes.processCodes,"F", data[i].processResolutions));
					}
				}else{
				
					containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "IS"));
					if(data[i].dispatch === 4 || data[i].dispatch === 6){
						processPromises = processPromises.concat(getProcessStateRequests(i, codes.processCodes,"F", data[i].processResolutions));
					}				
				}
				
			}else if(data[i].status === 'FALSE'){
				if($scope.experiment.categoryCode === 'qualitycontrol'){
					if(data[i].dispatch === 5){
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], getInputStateForRetry()));
						
					}else if(data[i].dispatch === 6){		
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "UA"));
						
						processPromises = processPromises.concat(getProcessStateRequests(i, codes.processCodes,"F", data[i].processResolutions));
					}
				}else{
					if(data[i].dispatch === 5){
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], getInputStateForRetry()));
						
					}else if(data[i].dispatch === 6){		
						containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.inputContainerCode], "IS"));
						
						processPromises = processPromises.concat(getProcessStateRequests(i, codes.processCodes,"F", data[i].processResolutions));
					}
				}
			}
									
		}
		
		
		saveData(supportPromises, containerPromises, processPromises);
		
	};
	var callbackSaveForOutputContainer = function(datatable){
		//usable function
		var getXCodes = function(outputContainer){
			var codes = {	
					outputContainerCode:outputContainer.code, 
					inputContainerCodes:[], 
					outputSupportCode:outputContainer.support.code, 
					inputSupportCodes:[], 
					processCodes:outputContainer.processCodes
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
		
		var containers = [], processes = [];
		
		//update input container and support, //update container container and support, // update process
		var containerPromises = [];
		var supportPromises = [];
		var processPromises = [];
		
		var data = datatable.getData();
		for(var i = 0 ; i < data.length ; i++){
			
			var codes = getXCodes(data[i].container);
			
			if(data[i].status === 'TRUE'){
				containerPromises = containerPromises.concat(getContainerStateRequests(i, codes.inputContainerCodes, "IS"));
				//supportPromises = supportPromises.concat(getContainerSupportStateRequests(i, codes.inputSupportCodes, "IS"));
				var outputStateCode = null;
				if(data[i].dispatch === 0){
					outputStateCode = "A-TM";					 		        					
				}else if(data[i].dispatch === 1){
					outputStateCode = "A-TF";
				}else if(data[i].dispatch === 2){
					outputStateCode = "A-QC";
				}else if(data[i].dispatch === 3){
					outputStateCode = "A-PF";
				}else if(data[i].dispatch === 4){
					if(nextExperimentsForExperimentType[data[i].container.fromTransformationTypeCodes[0]]){
						outputStateCode = "IW-P";
					}else{
						outputStateCode = "IS";
					}
					processPromises = processPromises.concat(getProcessStateRequests(i, codes.processCodes,"F", data[i].processResolutions));
				}else if(data[i].dispatch === 6){
					outputStateCode = "IS";
					processPromises = processPromises.concat(getProcessStateRequests(i, codes.processCodes,"F", data[i].processResolutions));
				}
				
				if(null !== outputStateCode){
					containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.outputContainerCode], outputStateCode));
					//supportPromises = supportPromises.concat(getContainerSupportStateRequests(i, [codes.outputSupportCode], outputStateCode));
				}else{
					console.log("ERROR no outputStateCode");
				}
				
			}else if(data[i].status === 'FALSE'){
				if(data[i].dispatch === 5){
					containerPromises = containerPromises.concat(getContainerStateRequests(i, codes.inputContainerCodes, getInputStateForRetry()));
					//supportPromises = supportPromises.concat(getContainerSupportStateRequests(i, codes.inputSupportCodes, getInputStateForRetry()));
					
					containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.outputContainerCode], "UA"));
					//supportPromises = supportPromises.concat(getContainerSupportStateRequests(i, [codes.outputSupportCode], "UA"));
					
				}else if(data[i].dispatch === 6){		
					containerPromises = containerPromises.concat(getContainerStateRequests(i, codes.inputContainerCodes, "IS"));
					//supportPromises = supportPromises.concat(getContainerSupportStateRequests(i, codes.inputSupportCodes, "IS"));
					
					containerPromises = containerPromises.concat(getContainerStateRequests(i, [codes.outputContainerCode], "UA"));
					//supportPromises = supportPromises.concat(getContainerSupportStateRequests(i, [codes.outputSupportCode], "UA"));
					
					processPromises = processPromises.concat(getProcessStateRequests(i, codes.processCodes,"F", data[i].processResolutions));
				}
			}
			
		}
		
		saveData(supportPromises, containerPromises, processPromises);		
	};
	
	var saveData = function(supports, containers, processes){
		//$http.put(jsRoutes.controllers.containers.api.ContainerSupports.updateStateBatch().url,supports)
		//	.then(function(data, status,headers,config){
				$http.put(jsRoutes.controllers.containers.api.Containers.updateStateBatch().url,containers)
					.then(function(result){
						var data = result.data;
						var containers = result.config.data
						var errors = []; var isErrors = false;
						
						data.forEach(function(value, key){
							if(value.status !== 200){
								errors[result.config.data[key].data.code] = value.data.code;
								isErrors = true;
							}
						})
						
						if(processes.length > 0){
							$http.put(jsRoutes.controllers.processes.api.Processes.updateStateBatch().url,processes)
								.then(function(result){
									var data = result.data;
									
									data.forEach(function(value, key){
										if(value.status !== 200){
											errors[result.config.data[key].data.code] = value.data.code;
											isErrors = true;
										}
									})
									$scope.$emit('dispatchDone', {errors:errors, isErrors:isErrors});
								});
						}else{
							$scope.$emit('dispatchDone', {errors:errors, isErrors:isErrors});
						}
				},function(data, status,headers,config){
					console.log("ERROR to update state for containers");
				});
			//},function(data, status,headers,config){
			//	console.log("ERROR to update state for supports");
			//});		
	};
	
	var dispatchValues;
	var dispatchValuesForExperimentType = [];
	dispatchValuesForExperimentType["all"] = [];
	
	var nextExperimentsForExperimentType = [];
	
	$scope.getDispatchValues = function(){
		return dispatchValues;		
	};
	
	var processTypes = {};
	$scope.isProcessResolutionsMustBeSet = function(value){
		//TODO GA rename to fromTransformationCodes
		if(value !== undefined){
			var fromTransformationTypeCode = ($scope.isOutputATMVoid())?$scope.experiment.typeCode:value.data.container.fromTransformationTypeCodes[0];
			if(value.data.dispatch === 6 || 
					(value.data.dispatch === 4 
							&& fromTransformationTypeCode === processTypes[value.data.container.processTypeCodes[0]].lastExperimentType.code)){
				return true;
			}else{
				value.data.processResolutions = undefined;
				return false;
			}
		}else if(dispatchValuesForExperimentType["all"].indexOf(6) > -1
				|| dispatchValuesForExperimentType["all"].indexOf(4) > -1){
			return true;
		}
	};
	
	$scope.$on('initDispatchModal', function(e) {	
		
		if($parse('experiment.state.code')($scope) === 'F'){
			
			var atmService = commonAtomicTransfertMethod($scope);
			$scope.lists.refresh.resolutions({"objectTypeCode":"Process"}, "processResolutions");
			
			if(dispatchValues === undefined){
				dispatchValues = [];
				for(var i = 0; i <= 7 ; i++){
					if($scope.experimentType.atomicTransfertMethod !== "OneToMany" || 
							($scope.experimentType.atomicTransfertMethod === "OneToMany" && i !== 5)){
						dispatchValues.push({"code":i,"name":Messages("containers.dispatch.value."+i)});
					}
											
				}
			}
			
			var initDisplayValues = function(fromTransformationTypeCodes, processTypeCodes){
				initNextExperimentAvailable(fromTransformationTypeCodes);
				var fromTransformationTypeCode = fromTransformationTypeCodes[0];			
				var processTypeCode = processTypeCodes[0];
				var key = fromTransformationTypeCode+"-"+processTypeCode;
				if(undefined === dispatchValuesForExperimentType[key]){	
					dispatchValuesForExperimentType[key] = [];
					$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.list().url,{params:{previousExperimentTypeCode:fromTransformationTypeCode,processTypeCode:processTypeCode}})
						.success(function(data, status,headers,config){
							var isNextExperimentType = (data.length > 0) ? true:false;	
							//extract the node to have their configuration
							$http.get(jsRoutes.controllers.experiments.api.ExperimentTypeNodes.get(fromTransformationTypeCode).url)
								.success(function(data, status,headers,config){
									for(var i = 0; i <= 7 ; i++){
										if((i === 1 && data.doTransfert) || 
												(i === 2 && data.doQualityControl) || 
													(i === 3 && data.doPurification) ||
														(i === 4 && !isNextExperimentType) ||
														(i === 0 && isNextExperimentType) ||
													i > 4){
											dispatchValuesForExperimentType[key].push(i);
											if(dispatchValuesForExperimentType["all"].indexOf(i) < 0){
												dispatchValuesForExperimentType["all"].push(i);
											}											
										}
									}
								});
						});
				}				
			};
					
			var initNextExperimentAvailable = function(fromTransformationTypeCodes){
				var fromTransformationTypeCode = fromTransformationTypeCodes[0];			
				var key = fromTransformationTypeCode;
				if(undefined === nextExperimentsForExperimentType[key]){	
					nextExperimentsForExperimentType[key] = [];
					$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.list().url,{params:{previousExperimentTypeCode:fromTransformationTypeCode}})
						.success(function(data, status,headers,config){
							var isNextExperimentType = (data.length > 0) ? true:false;	
							nextExperimentsForExperimentType[key] = isNextExperimentType;
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
								initDisplayValues(containers[key].fromTransformationTypeCodes, containers[key].processTypeCodes);
							}
						}
						
						if($scope.dispatchConfiguration.orderBy){
							outputContainers = $filter("orderBy")(outputContainers,$scope.dispatchConfiguration.orderBy);
						}else if(outputContainers[0] && outputContainers[0].container.categoryCode === 'well'){
							outputContainers = $filter("orderBy")(outputContainers,['container.support.column*1', 'container.support.line']);							
						}else{
							outputContainers = $filter("orderBy")(outputContainers,'container.code');
						}
						
						datatableConfig.columns = getColumns(),
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
						
						for(var key in containers){
							if(containers[key].state.code === 'IW-D'){
								inputContainers.push({container:containers[key], status:getValidStatus(), dispatch:undefined, processResolutions:[]});
								processTypeCodes = processTypeCodes.concat(containers[key].processTypeCodes);	
								if($scope.experiment.categoryCode === 'qualitycontrol'){
									initDisplayValues(containers[key].fromTransformationTypeCodes, containers[key].processTypeCodes);
								}else{
									initDisplayValues([$scope.experiment.typeCode], containers[key].processTypeCodes);
								}
							}
						}
						
						if($scope.dispatchConfiguration.orderBy){
							inputContainers = $filter("orderBy")(inputContainers,$scope.dispatchConfiguration.orderBy);
						}else if(inputContainers[0] && inputContainers[0].container.categoryCode === 'well'){
							inputContainers = $filter("orderBy")(inputContainers,['container.support.column*1', 'container.support.line']);							
						}else{
							inputContainers = $filter("orderBy")(inputContainers,'container.code');
						}
						
						datatableConfig.columns = getColumns(),						
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

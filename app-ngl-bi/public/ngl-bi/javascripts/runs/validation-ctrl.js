"use strict";

function SearchValidationCtrl($scope, datatable) {

	$scope.datatableConfig = {
			order :{by:'traceInformation.creationDate'},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.runs.tpl.Runs.validation(line.code).url,remove:true});
				}
			}
	};
	
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		$scope.datatable.search({stateCode:"IW-V"});
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('runs.page.tab.validate'),href:jsRoutes.controllers.runs.tpl.Runs.home("validation").url,remove:false});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchValidationCtrl.$inject = ['$scope', 'datatable'];


function ValidationDetailsCtrl($scope, $http, $routeParams, datatable, messages) {
	
	$scope.validation = {
			values :  [{code:"TRUE", name:Messages("validate.value.TRUE")},
		                 {code:"FALSE", name:Messages("validate.value.FALSE")},
		                 {code:"UNSET", name:Messages("validate.value.UNSET")}],
		    resolutions :  undefined
	}
	
	
	$scope.laneDatatableConfig = {
			name:'laneDatatable',
			order :{by:'number',mode:'local'},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			edit : {
				active:true,
				showButton : false,
				byDefault : true
			},
			save : {
				active:true,
				keepEdit:true,
				showButton : false,
				changeClass : false,
				url:function(line){
					return jsRoutes.controllers.runs.api.Lanes.validation($scope.run.code, line.number, line.validation.valid).url;
				},
				method:'put',
				value:function(line){
					return line.validation;
				},
				callback:function(datatable, nbError){
					if(nbError == 0){
						$http.put(jsRoutes.controllers.runs.api.Runs.validation($scope.run.code, $scope.run.validation.valid).url, $scope.run.validation).
							success(function(data, status, headers, config){
								$scope.messages.setSuccess("save");
								$scope.updateData();
							}).error(function(data, status, headers, config){
								$scope.messages.setError("save");	
							});
					}else{
						$scope.messages.setError("save");
					}
				}
			},
			cancel : {
				showButton:false
			},
						
			columns : [
			    {  	property:"number",
			    	render:function(value){
			    		if(value.number == $scope.run.treatments.ngsrg["default"].controlLane.value){
			    			value.trClass = "warning";
			    		}
			    		return "<strong>"+value.number+"</strong>";
			    	},
			    	header: Messages("runs.table.lane.code"),
			    	type :"String",
			    	order:false
				},
				{  	property:function(value){
						if(angular.isDefined(value.treatments.ngsrg.default.nbCycleRead2)){
							return value.treatments.ngsrg.default.nbCycleRead1.value +','+value.treatments.ngsrg.default.nbCycleRead2.value;
						}else{
							return value.treatments.ngsrg.default.nbCycleRead1.value
						}
					},
			    	header: Messages("runs.table.lane.nbCycles"),
			    	type :"Sring",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbCluster.value",
			    	header: Messages("runs.table.lane.nbCluster"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.percentClusterIlluminaFilter.value",
			    	header: Messages("runs.table.lane.percentClusterIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbClusterIlluminaFilter.value",
			    	header: Messages("runs.table.lane.nbClusterIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.percentClusterInternalAndIlluminaFilter.value",
			    	header: Messages("runs.table.lane.percentClusterInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbClusterInternalAndIlluminaFilter.value",
			    	header: Messages("runs.table.lane.nbClusterInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbBaseInternalAndIlluminaFilter.value",
			    	header: Messages("runs.table.lane.nbBaseInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.phasing.value",
			    	header: Messages("runs.table.lane.phasing"),
			    	type :"String",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.prephasing.value",
			    	header: Messages("runs.table.lane.prephasing"),
			    	type :"String",
			    	order:false
				},
				
				{	property:"validation.valid",
					header: Messages("runs.table.lane.validation.valid"),
					type :"String",
					edit:true,
			    	order:false,
			    	choiceInList:true,
			    	listStyle:'bs-select',
			    	possibleValues:$scope.validation.values
				},
				
				{	property:"validation.resolutionCodes",
					header: Messages("runs.table.lane.validation.resolutions"),
					type :"String",
			    	edit:true,
					order:false,
			    	choiceInList:true,
			    	listStyle:'bs-select-multiple',
			    	possibleValues:undefined
				}
			]				
	};
		
	$scope.isEdit = function(){
		return true;
	};
	
	$scope.save = function(){
		$scope.laneDatatable.save();
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		$scope.updateData();
		
	};
	
	$scope.updateData = function(){
		$http.get(jsRoutes.controllers.runs.api.Runs.get($routeParams.code).url).success(function(data) {
			$scope.run = data;	
			$scope.laneDatatable.setData($scope.run.lanes, $scope.run.lanes.length);
		});
	}
	
	
	$scope.init = function(){
		$scope.messages = messages();
		$http.get(jsRoutes.controllers.runs.api.Runs.get($routeParams.code).url).success(function(data) {
			$scope.run = data;	
				
			if($scope.getTabs().length == 0){
				$scope.addTabs({label:Messages('runs.page.tab.validate'),href:jsRoutes.controllers.runs.tpl.Runs.home("validation").url,remove:false});
				$scope.addTabs({label:$scope.run.code,href:jsRoutes.controllers.runs.tpl.Runs.validation($scope.run.code).url,remove:true})
				$scope.activeTab($scope.getTabs(1));
			}
			
			$scope.laneDatatable = datatable($scope, $scope.laneDatatableConfig);
			$scope.laneDatatable.setData($scope.run.lanes, $scope.run.lanes.length);
			//$scope.laneDatatable.setEdit();
			$http.get(jsRoutes.controllers.lists.api.Lists.resolutions().url,{params:{typeCode:$scope.run.typeCode}}).success(function(data) {
				$scope.validation.resolutions=data;
				$scope.laneDatatable.getColumn({property:"validation.resolutionCodes"}).possibleValues = data;
				
			});
			
		});
		
		
	}
	
};
ValidationDetailsCtrl.$inject = ['$scope', '$http', '$routeParams', 'datatable', 'messages'];
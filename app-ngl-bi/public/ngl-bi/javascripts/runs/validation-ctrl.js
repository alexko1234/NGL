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


function ValidationDetailsCtrl($scope, $http, $routeParams, datatable, messages, lists, treatments) {
		
	$scope.lanesDTConfig = {
			name:'lanesDT',
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
			    		if(angular.isDefined($scope.run.treatments.ngsrg) && value.number == $scope.run.treatments.ngsrg["default"].controlLane.value){
			    			value.trClass = "warning";
			    		}
			    		return "<strong>"+value.number+"</strong>";
			    	},
			    	header: Messages("runs.table.lane.code"),
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
			    	possibleValues:'lists.getValidations()'
				},
				{	property:"validation.resolutionCodes",
					header: Messages("runs.table.lane.validation.resolutions"),
					type :"String",
			    	edit:true,
					order:false,
			    	choiceInList:true,
			    	listStyle:'bs-select-multiple',
			    	possibleValues:'lists.getResolutions()'
				}
			]				
	}
	
	
	$scope.isEdit = function(){
		return true;
	};
	
	$scope.save = function(){
		$scope.lanesDT.save();
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		$scope.updateData();
		
	};
	
	$scope.updateData = function(){
		$http.get(jsRoutes.controllers.runs.api.Runs.get($routeParams.code).url).success(function(data) {
			$scope.run = data;	
			$scope.lanesDT.setData($scope.run.lanes, $scope.run.lanes.length);
		});
	}
	
	$scope.init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.treatments = treatments;
		
		$http.get(jsRoutes.controllers.runs.api.Runs.get($routeParams.code).url).success(function(data) {
			$scope.run = data;	
				
			if($scope.getTabs().length == 0){
				$scope.addTabs({label:Messages('runs.page.tab.validate'),href:jsRoutes.controllers.runs.tpl.Runs.home("validation").url,remove:false});
				$scope.addTabs({label:$scope.run.code,href:jsRoutes.controllers.runs.tpl.Runs.validation($scope.run.code).url,remove:true})
				$scope.activeTab($scope.getTabs(1));
			}
			
			$scope.lanesDT = datatable($scope, $scope.lanesDTConfig);
			$scope.lanesDT.setData($scope.run.lanes, $scope.run.lanes.length);
			
			$scope.lists.refresh({typeCode:$scope.run.typeCode});
			
			if(angular.isDefined($scope.run.lanes[0].treatments)){
				$scope.treatments.init($scope.run.lanes[0].treatments);				
			}
			
		});
		
		
	}
	
};
ValidationDetailsCtrl.$inject = ['$scope', '$http', '$routeParams', 'datatable', 'messages', 'lists', 'treatments'];

function LanesNGSRGCtrl($scope, datatable) {
	
	$scope.lanesNGSRGConfig = {
			name:'lanesNGSRG',
			order :{by:'number',mode:'local'},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			cancel : {active:false},						
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
						if(angular.isDefined(value.treatments.ngsrg["default"].nbCycleRead2)){
							return value.treatments.ngsrg["default"].nbCycleRead1.value +','+value.treatments.ngsrg["default"].nbCycleRead2.value;
						}else{
							return value.treatments.ngsrg["default"].nbCycleRead1.value
						}
					},
			    	header: Messages("runs.table.lane.ngsrg.nbCycles"),
			    	type :"Sring",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbCluster.value",
			    	header: Messages("runs.table.lane.ngsrg.nbCluster"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.percentClusterIlluminaFilter.value",
			    	header: Messages("runs.table.lane.ngsrg.percentClusterIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbClusterIlluminaFilter.value",
			    	header: Messages("runs.table.lane.ngsrg.nbClusterIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.percentClusterInternalAndIlluminaFilter.value",
			    	header: Messages("runs.table.lane.ngsrg.percentClusterInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbClusterInternalAndIlluminaFilter.value",
			    	header: Messages("runs.table.lane.ngsrg.nbClusterInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbBaseInternalAndIlluminaFilter.value",
			    	header: Messages("runs.table.lane.ngsrg.nbBaseInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbBaseInternalAndIlluminaFilter.value",
			    	header: Messages("runs.table.lane.ngsrg.nbBaseInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.seqLossPercent.value",
			    	header: Messages("runs.table.lane.ngsrg.seqLossPercent"),
			    	type :"Number",
			    	order:false
				}
				/*,
				{  	property:"treatments.ngsrg.default.phasing.value",
			    	header: Messages("runs.table.lane.ngsrg.phasing"),
			    	type :"String",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.prephasing.value",
			    	header: Messages("runs.table.lane.ngsrg.prephasing"),
			    	type :"String",
			    	order:false
				},*/
								
			]				
	};
	
	
	$scope.init = function(){
			$scope.lanesNGSRG = datatable($scope, $scope.lanesNGSRGConfig);
			$scope.lanesNGSRG.setData($scope.run.lanes, $scope.run.lanes.length);					
		
	}
	
}

LanesNGSRGCtrl.$inject = ['$scope', 'datatable'];


function LanesSAVCtrl($scope, $filter,datatable) {
	$scope.lanesSAVR1Config = {
			name:'lanesSAVR1',
			order :{by:'number',mode:'local'},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			cancel : {active:false},
			extraHeaders:{
				number:1,
				dynamic:true,
			},
			columns : [
			    {  	property:"number",
			    	render:function(value){
			    		if(angular.isDefined($scope.run.treatments.ngsrg) && value.number == $scope.run.treatments.ngsrg["default"].controlLane.value){
			    			value.trClass = "warning";
			    		}
			    		return "<strong>"+value.number+"</strong>";
			    	},
			    	header: Messages("runs.table.lane.code"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.clusterDensity.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.clusterDensityStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.clusterDensity"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read1.clusterPFPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.clusterPFPercStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.clusterPF"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read1.phasing.value,3) +' / '+$filter('number')(value.treatments.sav.read1.prephasing.value,3);						
					},
			    	header: Messages("runs.table.lane.sav.phasing"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},
				{  property:"treatments.sav.read1.reads.value",
			    	header: Messages("runs.table.lane.sav.reads"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},
				{  	property:"treatments.sav.read1.readsPF.value",
			    	header: Messages("runs.table.lane.sav.readsPF"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},
				{  	property:"treatments.sav.read1.greaterQ30Perc.value",
			    	header: Messages("runs.table.lane.sav.greaterQ30Perc"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},
				{  property:"treatments.sav.read1.cyclesErrRated.value",
			    	header: Messages("runs.table.lane.sav.cyclesErrRated"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read1.alignedPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.alignedPercStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.alignedPerc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read1.errorRatePerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.errorRatePercStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.errorRatePerc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.errorRatePercCycle35.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.errorRatePercCycle35Std.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.errorRatePercCycle35"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.errorRatePercCycle75.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.errorRatePercCycle75Std.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.errorRatePercCycle75"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.errorRatePercCycle100.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.errorRatePercCycle100Std.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.errorRatePercCycle100"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.intensityCycle1.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.intensityCycle1Std.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.intensityCycle1"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.intensityCycle20Perc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.intensityCycle20PercStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.intensityCycle20Perc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read1")}
				}												

				
			]				
	};
	
	$scope.lanesSAVR2Config = {
			name:'lanesSAVR2',
			order :{by:'number',mode:'local'},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			cancel : {active:false},
			extraHeaders:{
				number:1,
				dynamic:true,
			},
			columns : [
			    {  	property:"number",
			    	render:function(value){
			    		if(angular.isDefined($scope.run.treatments.ngsrg) && value.number == $scope.run.treatments.ngsrg["default"].controlLane.value){
			    			value.trClass = "warning";
			    		}
			    		return "<strong>"+value.number+"</strong>";
			    	},
			    	header: Messages("runs.table.lane.code"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.clusterDensity.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.clusterDensityStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.clusterDensity"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.clusterPFPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.clusterPFPercStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.clusterPF"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.phasing.value,3) +' / '+$filter('number')(value.treatments.sav.read2.prephasing.value,3);						
					},
			    	header: Messages("runs.table.lane.sav.phasing"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},
				{  property:"treatments.sav.read2.reads.value",
			    	header: Messages("runs.table.lane.sav.reads"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},
				{  	property:"treatments.sav.read2.readsPF.value",
			    	header: Messages("runs.table.lane.sav.readsPF"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},
				{  	property:"treatments.sav.read2.greaterQ30Perc.value",
			    	header: Messages("runs.table.lane.sav.greaterQ30Perc"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},
				{  property:"treatments.sav.read2.cyclesErrRated.value",
			    	header: Messages("runs.table.lane.sav.cyclesErrRated"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.alignedPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.alignedPercStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.alignedPerc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.errorRatePerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.errorRatePerc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.errorRatePercCycle35.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercCycle35Std.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.errorRatePercCycle35"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.errorRatePercCycle75.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercCycle75Std.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.errorRatePercCycle75"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.errorRatePercCycle100.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercCycle100Std.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.errorRatePercCycle100"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.intensityCycle1.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.intensityCycle1Std.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.intensityCycle1"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.intensityCycle20Perc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.intensityCycle20PercStd.value,2);						
					},
			    	header: Messages("runs.table.lane.sav.intensityCycle20Perc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.table.lane.sav.read2")}
				}												

				
			]				
	};
	
	$scope.init = function(){
		$scope.lanesSAVR1 = datatable($scope, $scope.lanesSAVR1Config);
		$scope.lanesSAVR1.setData($scope.run.lanes, $scope.run.lanes.length);
		
		$scope.lanesSAVR2 = datatable($scope, $scope.lanesSAVR2Config);
		$scope.lanesSAVR2.setData($scope.run.lanes, $scope.run.lanes.length);
	
	}
}

LanesSAVCtrl.$inject = ['$scope', '$filter', 'datatable'];


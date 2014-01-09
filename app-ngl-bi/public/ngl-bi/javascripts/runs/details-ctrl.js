"use strict";

function DetailsCtrl($scope, $http, $routeParams, $window, datatable, messages, lists, treatments) {
		
	var lanesDTConfig = {
			name:'lanesDT',
			order :{by:'number',mode:'local'},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			edit : {
				active:false,
				showButton : false,
				byDefault : true,
				columnMode : true
			},
			save : {
				active:false,
				keepEdit:true,
				showButton : false,
				changeClass : false,
				url:function(line){
					return jsRoutes.controllers.runs.api.Lanes.valuation($scope.run.code, line.number).url;
				},
				method:'put',
				value:function(line){
					return line.valuation;
				},
				callback:function(datatable, nbError){
					if(nbError == 0){
						$http.put(jsRoutes.controllers.runs.api.Runs.valuation($scope.run.code).url, $scope.run.valuation).
							success(function(data, status, headers, config){
								$scope.messages.setSuccess("save");
								$scope.run = data;
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
			    	header: Messages("runs.lane.code"),
			    	type :"String",
			    	order:false
				},				
				{	property:"valuation.valid",
					header: Messages("runs.lane.valuation.valid"),
					render:function(value){
						return Codes("valuation."+value.valuation.valid);
					},
					type :"String",
					edit:true,
			    	order:false,
			    	choiceInList:true,
			    	listStyle:'bs-select',
			    	possibleValues:'lists.getValuations()'
				},
				{	property:"valuation.resolutionCodes",
					header: Messages("runs.lane.valuation.resolutions"),
					render:function(value){
						var html = "";
						if(value.valuation.resolutionCodes){
							var html = "<ul class='unstyled'>";
							for(var i =0; i < value.valuation.resolutionCodes.length; i++){
								html += "<li>"+Codes("resolution."+value.valuation.resolutionCodes[i])+"</li>";
							}
							html += "</ul>";
							
						}
						return html;
					},
					type :"String",
			    	edit:true,
					order:false,
			    	choiceInList:true,
			    	listStyle:'bs-select-multiple',
			    	possibleValues:'lists.getResolutions()'
				}
			]				
	}
	
	
	$scope.readSetsDTConfig = {
			name:'readSetsDT',
			order :{by:'laneNumber',mode:'local'},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			cancel : {active:false},						
			columns : [
				{  	property:"laneNumber",
					header: Messages("readsets.laneNumber"),
					type :"String",
					order:false
				}, 
				{  	property:"code",
					header: Messages("readsets.code"),
					render : function(value){
						return '<a href="" ng-click="goToReadSet(\''+value.code+'\')">'+value.code+'</a>';
					},
					type :"String",
					order:false
				},
			   	{  	property:"treatments.ngsrg.default.validSeqPercent.value",
			    	header: Messages("readsets.ngsrg.validSeqPercent"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbCluster.value",
			    	header: Messages("readsets.ngsrg.nbCluster"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbBases.value",
			    	header: Messages("readsets.ngsrg.nbBases"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.Q30.value",
			    	header: Messages("readsets.ngsrg.Q30"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.qualityScore.value",
			    	header: Messages("readsets.ngsrg.qualityScore"),
			    	type :"Number",
			    	order:false
				},			
				{	property:"productionValuation.valid",
					header: Messages("readsets.productionValuation.valid"),
					render:function(value){
						return Codes("valuation."+value.productionValuation.valid);
					},
					type :"String"
				},			
				{	property:"bioinformaticValuation.valid",
					header: Messages("readsets.bioinformaticValuation.valid"),
					render:function(value){
						return Codes("valuation."+value.bioinformaticValuation.valid);
					},
					type :"String"
				}
			    
			]
	};
	
	$scope.save = function(){
		$scope.lanesDT.save();
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		updateData();
		
	};
	
	var updateData = function(){
		$http.get(jsRoutes.controllers.runs.api.Runs.get($routeParams.code).url).success(function(data) {
			$scope.run = data;	
			$scope.lanesDT.setData($scope.run.lanes, $scope.run.lanes.length);
		});
	}
	
	var isValuationMode = function(){
		return ($scope.isHomePage('valuation') || $routeParams.page === 'valuation');
	}
	
	$scope.goToReadSet = function(readSetCode){
		$window.open(jsRoutes.controllers.readsets.tpl.ReadSets.get(readSetCode).url, 'readsets');
	}
	
	$scope.goToReadSets = function(){
		$window.open(jsRoutes.controllers.readsets.tpl.ReadSets.home('search').url+'?runCode='+$scope.run.code, 'readsets');
	}
	
	$scope.init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.treatments = treatments;
		$scope.stopEditMode();
		if(isValuationMode()){
			$scope.startEditMode();
			
		}
		
		lanesDTConfig.edit.active=$scope.isEditMode();
		lanesDTConfig.save.active=$scope.isEditMode();
		
		$http.get(jsRoutes.controllers.runs.api.Runs.get($routeParams.code).url).success(function(data) {
			$scope.run = data;	
				
			if($scope.getTabs().length == 0){
				if(isValuationMode()){ //valuation mode
					$scope.addTabs({label:Messages('runs.page.tab.validate'),href:jsRoutes.controllers.runs.tpl.Runs.home("valuation").url,remove:false});
					$scope.addTabs({label:$scope.run.code,href:jsRoutes.controllers.runs.tpl.Runs.valuation($scope.run.code).url,remove:true})
				}else{ //detail mode
					$scope.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("search").url,remove:false});
					$scope.addTabs({label:$scope.run.code,href:jsRoutes.controllers.runs.tpl.Runs.get($scope.run.code).url,remove:true})									
				}
				$scope.activeTab($scope.getTabs(1));
			}
			
			$scope.lanesDT = datatable($scope, lanesDTConfig);
			$scope.lanesDT.setData($scope.run.lanes, $scope.run.lanes.length);
			
			$scope.lists.refresh.resolutions({typeCode:$scope.run.typeCode});
			$scope.lists.refresh.valuationCriterias({typeCode:$scope.run.typeCode});
			
			if(angular.isDefined($scope.run.lanes[0].treatments)){
				$scope.treatments.init($scope.run.lanes[0].treatments, jsRoutes.controllers.runs.tpl.Runs.laneTreatments);				
			}
			
			$http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url,{params:{runCode:$scope.run.code}}).success(function(data) {
				$scope.readSetsDT = datatable($scope, $scope.readSetsDTConfig);
				$scope.readSetsDT.setData(data, data.length);
			});
		});
		
		
	}
	
};
DetailsCtrl.$inject = ['$scope', '$http', '$routeParams', '$window', 'datatable', 'messages', 'lists', 'treatments'];

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
			    	header: Messages("runs.lane.code"),
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
			    	header: Messages("runs.lane.ngsrg.nbCycles"),
			    	type :"Sring",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbCluster.value",
			    	header: Messages("runs.lane.ngsrg.nbCluster"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.percentClusterIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg.percentClusterIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbClusterIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg.nbClusterIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.percentClusterInternalAndIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg.percentClusterInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbClusterInternalAndIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg.nbClusterInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbBaseInternalAndIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg.nbBaseInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},				
				{  	property:"treatments.ngsrg.default.seqLossPercent.value",
			    	header: Messages("runs.lane.ngsrg.seqLossPercent"),
			    	type :"Number",
			    	order:false
				}
				/*,
				{  	property:"treatments.ngsrg.default.phasing.value",
			    	header: Messages("runs.lane.ngsrg.phasing"),
			    	type :"String",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.prephasing.value",
			    	header: Messages("runs.lane.ngsrg.prephasing"),
			    	type :"String",
			    	order:false
				},*/
								
			]				
	};
	
	
	$scope.init = function(){
		$scope.$watch('run', function() {
			if(angular.isDefined($scope.run)){
				$scope.lanesNGSRG = datatable($scope, $scope.lanesNGSRGConfig);
				$scope.lanesNGSRG.setData($scope.run.lanes, $scope.run.lanes.length);
			}
		}); 
		
	}
	
}

LanesNGSRGCtrl.$inject = ['$scope', 'datatable'];


function LanesSAVCtrl($scope, $filter, $http, datatable) {
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
			    	header: Messages("runs.lane.code"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.clusterDensity.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.clusterDensityStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.clusterDensity"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read1.clusterPFPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.clusterPFPercStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.clusterPF"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read1.phasing.value,3) +' / '+$filter('number')(value.treatments.sav.read1.prephasing.value,3);						
					},
			    	header: Messages("runs.lane.sav.phasing"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  property:"treatments.sav.read1.reads.value",
			    	header: Messages("runs.lane.sav.reads"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  	property:"treatments.sav.read1.readsPF.value",
			    	header: Messages("runs.lane.sav.readsPF"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  	property:"treatments.sav.read1.greaterQ30Perc.value",
			    	header: Messages("runs.lane.sav.greaterQ30Perc"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  property:"treatments.sav.read1.cyclesErrRated.value",
			    	header: Messages("runs.lane.sav.cyclesErrRated"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read1.alignedPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.alignedPercStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.alignedPerc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read1.errorRatePerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.errorRatePercStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.errorRatePerc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.errorRatePercCycle35.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.errorRatePercCycle35Std.value,2);						
					},
			    	header: Messages("runs.lane.sav.errorRatePercCycle35"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.errorRatePercCycle75.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.errorRatePercCycle75Std.value,2);						
					},
			    	header: Messages("runs.lane.sav.errorRatePercCycle75"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.errorRatePercCycle100.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.errorRatePercCycle100Std.value,2);						
					},
			    	header: Messages("runs.lane.sav.errorRatePercCycle100"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.intensityCycle1.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.intensityCycle1Std.value,2);						
					},
			    	header: Messages("runs.lane.sav.intensityCycle1"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read1.intensityCycle20Perc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read1.intensityCycle20PercStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.intensityCycle20Perc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
				},
				{  	property:"treatments.sav.read1.alert",
					render : function(value){
						return getAlertButton(value,'read1');
					},
			    	header: Messages("runs.lane.sav.alerts"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read1")}
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
			    	header: Messages("runs.lane.code"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.clusterDensity.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.clusterDensityStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.clusterDensity"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.clusterPFPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.clusterPFPercStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.clusterPF"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.phasing.value,3) +' / '+$filter('number')(value.treatments.sav.read2.prephasing.value,3);						
					},
			    	header: Messages("runs.lane.sav.phasing"),
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  property:"treatments.sav.read2.reads.value",
			    	header: Messages("runs.lane.sav.reads"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:"treatments.sav.read2.readsPF.value",
			    	header: Messages("runs.lane.sav.readsPF"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:"treatments.sav.read2.greaterQ30Perc.value",
			    	header: Messages("runs.lane.sav.greaterQ30Perc"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  property:"treatments.sav.read2.cyclesErrRated.value",
			    	header: Messages("runs.lane.sav.cyclesErrRated"),
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.alignedPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.alignedPercStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.alignedPerc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.errorRatePerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.errorRatePerc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.errorRatePercCycle35.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercCycle35Std.value,2);						
					},
			    	header: Messages("runs.lane.sav.errorRatePercCycle35"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.errorRatePercCycle75.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercCycle75Std.value,2);						
					},
			    	header: Messages("runs.lane.sav.errorRatePercCycle75"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.errorRatePercCycle100.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercCycle100Std.value,2);						
					},
			    	header: Messages("runs.lane.sav.errorRatePercCycle100"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.intensityCycle1.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.intensityCycle1Std.value,2);						
					},
			    	header: Messages("runs.lane.sav.intensityCycle1"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.intensityCycle20Perc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.intensityCycle20PercStd.value,2);						
					},
			    	header: Messages("runs.lane.sav.intensityCycle20Perc"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:"treatments.sav.read2.alert",
					render : function(value){
						return getAlertButton(value,'read2');
					},
			    	header: Messages("runs.lane.sav.alerts"),
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				}												

				
			]				
	};
	
	
	var getAlertButton = function(lane, readPos){
		var button = "";
		var alert = $scope.alerts[$scope.run.code+'.'+lane.number+'.'+readPos];
		
		if(alert){
			var button = '<button class="btn btn-mini btn-danger" type="button" popover="'+getAlertBody(alert)+'" popover-title="'+getAlertTitle()+'" popover-placement="right"><i class="icon-warning-sign"></i></button>'; 			
		}
		return button;
	}
	
	var getAlertTitle = function(){
		return Messages("runs.lane.sav.alerts");
	};
	
	var getAlertBody = function(alert){
		var text = "";
		for(var propertyName in alert.propertiesAlert) {
			var list =alert.propertiesAlert[propertyName];
			text = propertyName+" : \n";
			for(var i = 0; i < list.length; i++ ){
				text = text +"\t"+Messages(Messages("runs.lane.sav."+list[i]))+", ";
			}		
		}
		return text;
	};
	
	$scope.init = function(){
		$scope.$watch('run', function() {
			if(angular.isDefined($scope.run)){
								
				$http.get(jsRoutes.controllers.alerts.api.Alerts.list().url, {params:{regexCode:$scope.run.code+'*'}})
					.success(function(data, status, headers, config) {
					$scope.alerts = {};
					for(var i =	0; i < data.length ; i++){
						$scope.alerts[data[i].code] = data[i]; 
					}
					
					$scope.lanesSAVR1 = datatable($scope, $scope.lanesSAVR1Config);
					$scope.lanesSAVR1.setData($scope.run.lanes, $scope.run.lanes.length);
					
					$scope.lanesSAVR2 = datatable($scope, $scope.lanesSAVR2Config);
					$scope.lanesSAVR2.setData($scope.run.lanes, $scope.run.lanes.length);
					
				});
				
				
			};
		});
	
	}
}

LanesSAVCtrl.$inject = ['$scope', '$filter', '$http', 'datatable'];


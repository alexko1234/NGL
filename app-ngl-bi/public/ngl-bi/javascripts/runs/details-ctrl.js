"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$q', '$routeParams', '$window', '$filter', 'mainService', 'tabService', 'datatable', 'messages', 'lists', 'treatments', 'valuationService', 
                                                  function($scope, $http, $q, $routeParams, $window, $filter, mainService, tabService, datatable, messages, lists, treatments, valuationService) {
	/* configuration datatables */	
	var lanesDTConfig = {
			name:'lanesDT',
			order :{by:'number',mode:'local'},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			edit : {
				active:true,
				showButton : false,
				withoutSelect : true,
				columnMode : true
			},
			save : {
				active:true,
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
						var queries = [];
						queries.push($http.put(jsRoutes.controllers.runs.api.Runs.update($scope.run.code).url+"?fields=keep", {keep:$scope.run.keep}));
						queries.push($http.put(jsRoutes.controllers.runs.api.Runs.valuation($scope.run.code).url, $scope.run.valuation));
						
						$q.all(queries).then(function(results){
							var error = false;
							for(var i = 0; i  < results.length; i++){
								var result = results[i];
								if(result.status !== 200){
									error = true;
								}
							}
							if(error){
								$scope.messages.setError("save");	
							}else{
								$scope.messages.setSuccess("save");
								updateData();
							}
						});					
					}else{
						$scope.messages.setError("save");
					}
					
				}
			},
			cancel : {
				showButton:false
			},
			lines : {
				trClass : function(value){
					if(angular.isDefined($scope.run.treatments.ngsrg) && value.number == $scope.run.treatments.ngsrg["default"].controlLane.value){
		    			return "info";
		    		}		    		
				}
			},
			columns : [
			    {  	property:"number",
			    	render:function(value, line){
			    		return "<strong>"+value.number+"</strong>";
			    	},
			    	header: "runs.lane.code",
			    	type :"String",
			    	order:false
				},				
				{	property:"valuation.valid",
					header: "runs.lane.valuation.valid",
					filter:"codes:'valuation'",
					type :"String",
					edit:true,
			    	order:false,
			    	choiceInList:true,
			    	listStyle:'bt-select',
			    	possibleValues:'lists.getValuations()'			    	
				},
				{	property:"valuation.resolutionCodes",
					header: "runs.lane.valuation.resolutions",
					render:function(value){						
						return '<div bt-select ng-model="value.data.valuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in lists.getResolutions()" ng-edit="false"></div>';
					},
					type :"String",
			    	edit:true,
					order:false,
			    	choiceInList:true,
			    	listStyle:'bt-select-multiple',
			    	possibleValues:'lists.getResolutions()',
			    	groupBy:'category.name'
				}
			]				
	}
	
	
	var readSetsDTConfig = {
			name:'readSetsDT',
			order :{by:'laneNumber',mode:'local'},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			cancel : {active:false},		
			columns : [
				{  	property:"laneNumber",
					header: "readsets.laneNumber",
					type :"String",
					order:false
				}, 
				{  	property:"code",
					header: "readsets.code",
					render : function(value){
						return '<a href="" ng-click="showReadSet(\''+value.code+'\')">'+value.code+'</a>';
					},
					type :"String",
					order:false
				},
				{	property:"state.code",
					filter:"codes:'state'",
					header: "readsets.stateCode",
					type :"String"
				},
				{  	
					property:"sampleOnContainer.properties.percentPerLane.value",
					header: "readsets.sampleOnContainer.percentPerLane",
					type :"Number",
					format:2,
					order:false
				},
			   	{  	property:"treatments.ngsrg.default.validSeqPercent.value",
			    	header: "readsets.treatments.ngsrg_illumina.validSeqPercent",
			    	type :"Number",
			    	format:2,
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbCluster.value",
			    	header: "readsets.treatments.ngsrg_illumina.nbCluster",
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbBases.value",
			    	header: "readsets.treatments.ngsrg_illumina.nbBases",
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.Q30.value",
			    	header: "readsets.treatments.ngsrg_illumina.Q30",
			    	type :"Number",
			    	format:2,
			    	order:false,
			    	tdClass : "valuationService.valuationCriteriaClass({readsets:value.data}, run.valuation.criteriaCode, 'readsets.' + col.property)"
				},
				{  	property:"treatments.ngsrg.default.qualityScore.value",
			    	header: "readsets.treatments.ngsrg_illumina.qualityScore",
			    	type :"Number",
			    	format:2,
			    	order:false,
			    	tdClass : "valuationService.valuationCriteriaClass({readsets:value.data}, run.valuation.criteriaCode, 'readsets.' + col.property)"
				},			
				{	property:"productionValuation.valid",
					header: "readsets.productionValuation.valid",
					filter:"codes:'valuation'",
					type :"String"
				},			
				{	property:"bioinformaticValuation.valid",
					header: "readsets.bioinformaticValuation.valid",
					filter:"codes:'valuation'",
					type :"String"
				}
			    
			]
	};
	
	/* buttons section */
	$scope.save = function(){
		$scope.lanesDT.save();		
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		updateData(true);				
	};
	
	$scope.activeEditMode = function(){
		$scope.mainService.startEditMode();
		$scope.lanesDT.setEdit();		
	}

	
	
	/* readset section */
	
	$scope.form = {
	};	
	
	$scope.search = function(){
		//get lane numbers selected
		var laneNum = [];
		if($scope.form.laneNumbers) laneNum = $scope.form.laneNumbers;		
		//query by laneNumbers
		$http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url,{params:{runCode:$scope.run.code,laneNumbers:laneNum}}).success(function(data) {
			$scope.readSetsDT.setData(data, data.length);
		});
	};
	

	$scope.showReadSets = function(){
		var laneNumbers={value:''};
		angular.forEach($scope.form.laneNumbers, function(value, key){
			this.value +='&laneNumbers='+value;
		}, laneNumbers);
		
		
		$window.open(jsRoutes.controllers.readsets.tpl.ReadSets.home('search').url+'?runCode='+$scope.run.code+laneNumbers.value, 'readsets');
	}
	
	$scope.valuateReadSets = function(){
		var laneNumbers={value:''};
		angular.forEach($scope.form.laneNumbers, function(value, key){
			this.value +='&laneNumbers='+value;
		}, laneNumbers);
		
		
		$window.open(jsRoutes.controllers.readsets.tpl.ReadSets.home('valuation').url+'?runCode='+$scope.run.code+laneNumbers.value, 'readsets');
	}
	
	$scope.showReadSet = function(readSetCode){
		$window.open(jsRoutes.controllers.readsets.tpl.ReadSets.get(readSetCode).url, 'readsets');
	}
	
	/* main section  */
	var updateData = function(isCancel){
		$http.get(jsRoutes.controllers.runs.api.Runs.get($routeParams.code).url).success(function(data) {
			$scope.run = data;	
			$scope.lanesDT.setData($scope.run.lanes, $scope.run.lanes.length);
			if(isCancel && !isValuationMode()){
				$scope.lanesDT.cancel();
				$scope.mainService.stopEditMode();
			}else{
				$scope.lanesDT.setEdit();
				$http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url,{params:{runCode:$scope.run.code, includes:["code","state","bioinformaticValuation", "productionValuation","laneNumber","treatments.ngsrg", "sampleOnContainer"]}}).success(function(data) {
					$scope.readSetsDT.setData(data, data.length);				
				});
			}			
		});
	}
	
	var isValuationMode = function(){
		return ($scope.mainService.isHomePage('valuation') || $routeParams.page === 'valuation');
	}
	
	$scope.highLight = function(prop){
			if (lists.getValuationCriterias() && $scope.run) {
				return "bg-" + $scope.valuationService.valuationCriteriaClass($scope.run, $scope.run.valuation.criteriaCode, prop);
			}
			else {
				return undefined;
			}
	};
	
	var init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.treatments = treatments;
		$scope.mainService = mainService;
		$scope.mainService.stopEditMode();
		$scope.valuationService = valuationService();
		
		$http.get(jsRoutes.controllers.runs.api.Runs.get($routeParams.code).url).success(function(data) {
			
			$scope.run = data;
			
			if(tabService.getTabs().length == 0){
				if(isValuationMode()){ //valuation mode
					tabService.addTabs({label:Messages('runs.page.tab.validate'),href:jsRoutes.controllers.runs.tpl.Runs.home("valuation").url,remove:true});
					tabService.addTabs({label:$scope.run.code,href:jsRoutes.controllers.runs.tpl.Runs.valuation($scope.run.code).url,remove:true})
				}else{ //detail mode
					tabService.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("search").url,remove:true});
					tabService.addTabs({label:$scope.run.code,href:jsRoutes.controllers.runs.tpl.Runs.get($scope.run.code).url,remove:true})									
				}
				tabService.activeTab(tabService.getTabs(1));
			}
			
			$scope.lanesDT = datatable($scope, lanesDTConfig);
			$scope.lanesDT.setData($scope.run.lanes, $scope.run.lanes.length);
			if(isValuationMode()){
				$scope.mainService.startEditMode();	
				$scope.lanesDT.setEdit();
			}
			$scope.lists.refresh.resolutions({typeCode:$scope.run.typeCode});
			$scope.lists.refresh.valuationCriterias({typeCode:$scope.run.typeCode, objectTypeCode:"Run"});
			
			if(angular.isDefined($scope.run.lanes[0].treatments)){
				$scope.treatments.init($scope.run.lanes[0].treatments, jsRoutes.controllers.runs.tpl.Runs.laneTreatments);				
			}
			
			$scope.laneOptions = $filter('orderBy')($scope.run.lanes, 'number');
			
			$http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url,{params:{runCode:$scope.run.code, includes:["code","state","bioinformaticValuation", "productionValuation","laneNumber","treatments.ngsrg", "sampleOnContainer"]}}).success(function(data) {
				$scope.readSetsDT = datatable($scope, readSetsDTConfig);
				$scope.readSetsDT.setData(data, data.length);	
			});
		});
		
		
	};
	
	init();
	
	
	
}]);

angular.module('home').controller('LanesNGSRGCtrl', [ '$scope', 'datatable', function($scope, datatable) {
	
	var lanesNGSRGConfig = {
			name:'lanesNGSRG',
			order :{by:'number',mode:'local'},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			cancel : {active:false},
			lines : {
				trClass : function(value){
					if(value.number == $scope.run.treatments.ngsrg["default"].controlLane.value && $scope.run.valuation.criteriaCode == undefined){
		    			return "info";
		    		}		    		
				}
			},
			columns : [
			    {  	property:"number",
			    	render:function(value, line){
			    		return "<strong>"+value.number+"</strong>";
			    	},
			    	header: Messages("runs.lane.code"),
			    	type :"String",
			    	order:false,
			    	tdClass : function(value){
			    		if(value.number == $scope.run.treatments.ngsrg["default"].controlLane.value) {
			    			return "info";
			    		}
			    	}
				},
				{  	property:function(value){
						if(angular.isDefined(value.treatments.ngsrg["default"].nbCycleRead2)){
							return value.treatments.ngsrg["default"].nbCycleRead1.value +','+value.treatments.ngsrg["default"].nbCycleRead2.value;
						}else{
							return value.treatments.ngsrg["default"].nbCycleRead1.value
						}
					},
			    	header: Messages("runs.lane.ngsrg_illumina.nbCycles"),
			    	type :"Sring",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbCluster.value",
			    	header: Messages("runs.lane.ngsrg_illumina.nbCluster"),
			    	type :"Number",
			    	order:false,
			    	tdClass : "valuationService.valuationCriteriaClass({lanes:value.data}, run.valuation.criteriaCode, 'lanes.' + col.property)"
				},
				{  	property:"treatments.ngsrg.default.percentClusterIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg_illumina.percentClusterIlluminaFilter"),
			    	type :"Number",
			    	order:false,
			    	tdClass : "valuationService.valuationCriteriaClass({lanes:value.data}, run.valuation.criteriaCode, 'lanes.' + col.property)"
				},
				{  	property:"treatments.ngsrg.default.nbClusterIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg_illumina.nbClusterIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.percentClusterInternalAndIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg_illumina.percentClusterInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false,
			    	tdClass : "valuationService.valuationCriteriaClass({lanes:value.data}, run.valuation.criteriaCode, 'lanes.' + col.property)"
				},
				{  	property:"treatments.ngsrg.default.nbClusterInternalAndIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg_illumina.nbClusterInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"treatments.ngsrg.default.nbBaseInternalAndIlluminaFilter.value",
			    	header: Messages("runs.lane.ngsrg_illumina.nbBaseInternalAndIlluminaFilter"),
			    	type :"Number",
			    	order:false
				},				
				{  	property:"treatments.ngsrg.default.seqLossPercent.value",
			    	header: Messages("runs.lane.ngsrg_illumina.seqLossPercent"),
			    	type :"Number",
			    	order:false,
			    	tdClass : "valuationService.valuationCriteriaClass({lanes:value.data}, run.valuation.criteriaCode, 'lanes.' + col.property)"
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
	
	
	var init = function(){
		$scope.$watch('run', function() {
			if(angular.isDefined($scope.run)){
				$scope.lanesNGSRG = datatable($scope, lanesNGSRGConfig);
				$scope.lanesNGSRG.setData($scope.run.lanes, $scope.run.lanes.length);
			}
		}); 
		
	};
	
	init();
	
}]);

angular.module('home').controller('LanesSAVCtrl', [ '$scope', '$filter', '$http', 'datatable', function($scope, $filter, $http, datatable) {
	var lanesSAVR1Config = {
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
			lines : {
				trClass : function(value){
					if(angular.isDefined($scope.run.treatments.ngsrg) && value.number == $scope.run.treatments.ngsrg["default"].controlLane.value){
		    			return "info";
		    		}		    		
				}
			},
			columns : [
			    {  	property:"number",
			    	render:function(value, line){
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
	
	var lanesSAVR2Config = {
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
			lines : {
				trClass : function(value){
					if(angular.isDefined($scope.run.treatments.ngsrg) && value.number == $scope.run.treatments.ngsrg["default"].controlLane.value){
			    		return "info";
		    		}		    		
				}
			},
			columns : [
			    {  	property:"number",
			    	render:function(value, line){
			    		return "<strong>"+value.number+"</strong>";
			    	},
			    	header: "runs.lane.code",
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.clusterDensity.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.clusterDensityStd.value,2);						
					},
			    	header: "runs.lane.sav.clusterDensity",
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.clusterPFPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.clusterPFPercStd.value,2);						
					},
			    	header: "runs.lane.sav.clusterPF",
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.phasing.value,3) +' / '+$filter('number')(value.treatments.sav.read2.prephasing.value,3);						
					},
			    	header: "runs.lane.sav.phasing",
			    	type :"Sring",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  property:"treatments.sav.read2.reads.value",
			    	header: "runs.lane.sav.reads",
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:"treatments.sav.read2.readsPF.value",
			    	header: "runs.lane.sav.readsPF",
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:"treatments.sav.read2.greaterQ30Perc.value",
			    	header: "runs.lane.sav.greaterQ30Perc",
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  property:"treatments.sav.read2.cyclesErrRated.value",
			    	header: "runs.lane.sav.cyclesErrRated",
			    	type :"Number",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.alignedPerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.alignedPercStd.value,2);						
					},
			    	header: "runs.lane.sav.alignedPerc",
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},
				{  	property:function(value){
						return $filter('number')(value.treatments.sav.read2.errorRatePerc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercStd.value,2);						
					},
			    	header: "runs.lane.sav.errorRatePerc",
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.errorRatePercCycle35.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercCycle35Std.value,2);						
					},
			    	header: "runs.lane.sav.errorRatePercCycle35",
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.errorRatePercCycle75.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercCycle75Std.value,2);						
					},
			    	header: "runs.lane.sav.errorRatePercCycle75",
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.errorRatePercCycle100.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.errorRatePercCycle100Std.value,2);						
					},
			    	header: "runs.lane.sav.errorRatePercCycle100",
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.intensityCycle1.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.intensityCycle1Std.value,2);						
					},
			    	header: "runs.lane.sav.intensityCycle1",
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:function(value){
					return $filter('number')(value.treatments.sav.read2.intensityCycle20Perc.value,2) +' +/- '+$filter('number')(value.treatments.sav.read2.intensityCycle20PercStd.value,2);						
					},
			    	header: "runs.lane.sav.intensityCycle20Perc",
			    	type :"String",
			    	order:false,
			    	extraHeaders:{"0":Messages("runs.lane.sav.read2")}
				},											
				{  	property:"treatments.sav.read2.alert",
					render : function(value){
						return getAlertButton(value,'read2');
					},
			    	header: "runs.lane.sav.alerts",
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
			var button = '<button class="btn btn-xs btn-danger" type="button" popover="'+getAlertBody(alert)+'" popover-title="'+getAlertTitle()+'" popover-placement="right"><i class="fa fa-warning"></i></button>'; 			
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
	
	var init = function(){
		$scope.$watch('run', function() {
			if(angular.isDefined($scope.run)){
								
				$http.get(jsRoutes.controllers.alerts.api.Alerts.list().url, {params:{regexCode:$scope.run.code+'*'}})
					.success(function(data, status, headers, config) {
					$scope.alerts = {};
					for(var i =	0; i < data.length ; i++){
						$scope.alerts[data[i].code] = data[i]; 
					}
					
					$scope.lanesSAVR1 = datatable($scope,  lanesSAVR1Config);
					$scope.lanesSAVR1.setData($scope.run.lanes, $scope.run.lanes.length);
					
					$scope.lanesSAVR2 = datatable($scope,  lanesSAVR2Config);
					$scope.lanesSAVR2.setData($scope.run.lanes, $scope.run.lanes.length);
					
				});
				
				
			};
		});
	
	};
	
	init();
}]);


"use strict";

function DetailsCtrl($scope, $http, $routeParams, datatable, messages, lists, treatments, $window) {
	
	$scope.goToRun=function(){
		$window.open(jsRoutes.controllers.runs.tpl.Runs.get($scope.run.code).url, 'runs');
	}
	
	$scope.save = function(){
		$http.put(jsRoutes.controllers.readsets.api.ReadSets.valuation($scope.readset.code).url, 
				{productionValuation:$scope.readset.productionValuation,bioinformaticValuation:$scope.readset.bioinformaticValuation})
		.success(function(data, status, headers, config){
			$scope.messages.setSuccess("save");
			updateData();
		}).error(function(data, status, headers, config){
			$scope.messages.setError("save");	
		});
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		updateData();
		
	};
	
	var updateData = function(){
		$http.get(jsRoutes.controllers.readsets.api.ReadSets.get($routeParams.code).url).success(function(data) {
			$scope.readset = data;	
			
		});
	}
	
	var isValuationMode = function(){
		return ($scope.isHomePage('valuation') || $routeParams.page === 'valuation');
	}
	
	
	$scope.init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.treatments = treatments;
		$scope.stopEditMode();
		if(isValuationMode()){
			$scope.startEditMode();
			
		}
		
		$http.get(jsRoutes.controllers.readsets.api.ReadSets.get($routeParams.code).url).success(function(data) {
			$scope.readset = data;	
				
			if($scope.getTabs().length == 0){
				if(isValuationMode()){ //valuation mode
					$scope.addTabs({label:Messages('readsets.page.tab.validate'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("valuation").url,remove:false});
					$scope.addTabs({label:$scope.readset.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.valuation( $scope.readset.code).url,remove:true})
				}else{ //detail mode
					$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("search").url,remove:false});
					$scope.addTabs({label:$scope.readset.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.get($scope.readset.code).url,remove:true})									
				}
				$scope.activeTab($scope.getTabs(1));
			}
			
			$scope.lists.refresh.resolutions({typeCode:$scope.readset.typeCode});
			$scope.lists.refresh.validationCriterias({typeCode:$scope.readset.typeCode});
			
			if(angular.isDefined($scope.readset.treatments)){				
				$scope.treatments.init($scope.readset.treatments, jsRoutes.controllers.readsets.tpl.ReadSets.treatments,{global:true});				
			}
			
			$http.get(jsRoutes.controllers.runs.api.Runs.get($scope.readset.runCode).url).success(function(data) {
				$scope.run = data;	
			});
			
		});
		
		
	}
	
};
DetailsCtrl.$inject = ['$scope', '$http', '$routeParams', 'datatable', 'messages', 'lists', 'treatments', '$window'];

function NGSRGCtrl($scope, datatable) {
	
	$scope.NGSRGConfig = {
			name:'NGSRG',
			order :{active:false},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			cancel : {active:false},						
			columns : [
			   	{  	property:"validSeqPercent.value",
			    	header: Messages("readsets.dt.ngsrg.validSeqPercent"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"nbCluster.value",
			    	header: Messages("readsets.dt.ngsrg.nbCluster"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"nbBases.value",
			    	header: Messages("readsets.dt.ngsrg.nbBases"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"Q30.value",
			    	header: Messages("readsets.dt.ngsrg.Q30"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"qualityScore.value",
			    	header: Messages("readsets.dt.ngsrg.qualityScore"),
			    	type :"Number",
			    	order:false
				}
								
			]				
	};
		
	$scope.init = function(){
		$scope.$watch('readset', function() {
			if(angular.isDefined($scope.readset)){
				$scope.NGSRG = datatable($scope, $scope.NGSRGConfig);
				$scope.NGSRG.setData([$scope.readset.treatments.ngsrg['default']], 1);
			}
		}); 		
	}
	
}

NGSRGCtrl.$inject = ['$scope', 'datatable'];


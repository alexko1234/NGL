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
			$scope.readset = data;
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
			$scope.lists.refresh.valuationCriterias({typeCode:$scope.readset.typeCode});
			
			if(angular.isDefined($scope.readset.treatments)){				
				$scope.treatments.init($scope.readset.treatments, jsRoutes.controllers.readsets.tpl.ReadSets.treatments,{global:true});				
			}
			
			$http.get(jsRoutes.controllers.runs.api.Runs.get($scope.readset.runCode).url).success(function(data) {
				$scope.run = data;	
			});
			
		});
		
		$scope.setImage = function(imageData, imageName, context, treatment, width) {
			$scope.modalImage = imageData;
			
			$scope.modalTitle = '';
			if (context != '') {
				$scope.modalTitle = context + ' : ';
			}
			$scope.modalTitle = $scope.modalTitle + Messages('readsets.treatments.' + treatment + '.' + imageName);
			
			//pb : return the width of the image after resizing (not the original size)
			//var imageId = context + '.' + imageName;
			//$scope.imageWidth = document.getElementById(imageId).width;
			
			$scope.modalWidth = width;
			$scope.modalLeft = (document.body.clientWidth - width)/2;
		}
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
			    	header: Messages("readsets.treatments.ngsrg.validSeqPercent"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"nbCluster.value",
			    	header: Messages("readsets.treatments.ngsrg.nbCluster"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"nbBases.value",
			    	header: Messages("readsets.treatments.ngsrg.nbBases"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"Q30.value",
			    	header: Messages("readsets.treatments.ngsrg.Q30"),
			    	type :"Number",
			    	order:false
				},
				{  	property:"qualityScore.value",
			    	header: Messages("readsets.treatments.ngsrg.qualityScore"),
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

function DuplicatesCtrl($scope) {
	$scope.init = function(name) {
		$scope.$watch('readset', function() { 
			if (angular.isDefined($scope.readset)) {
				$scope.objsInDuplicates = $scope.readset.treatments[name];
			}
		});
	}
}



function ReadQualityCtrl($scope) {
	$scope.init = function(name) {
		$scope.$watch('readset', function() { 
			if (angular.isDefined($scope.readset)) {
				$scope.objsInAdapters = $scope.readset.treatments[name];
				
				$scope.msgQualScore = Messages("readset.treatments." + name + '.qualScore');
			}
		});
	}
}


function SortingCtrl($scope) {
	$scope.init = function(name) {
		$scope.$watch('readset', function() { 
			if (angular.isDefined($scope.readset)) {
				$scope.objsInRNABilan = $scope.readset.treatments[name];
			}
		});
	}
}



function TaxonomyCtrl($scope) {
	$scope.init = function(name) {		
		$scope.$watch('readset', function() { 
			if (angular.isDefined($scope.readset)) {
				$scope.objsInDivisionBilan = $scope.readset.treatments[name].read1.divisionBilan.value;				
				$scope.objsInKeywordBilan = $scope.readset.treatments[name].read1.keywordBilan.value;
				$scope.objsInTaxonBilan = $scope.readset.treatments[name].read1.taxonBilan.value;
				
				$scope.krona = "data:text/html;base64,"+$scope.readset.treatments[name].read1.krona.value
				
				$scope.ncbiUrl = Messages("readsets.treatments.taxonomy.beginNcbiUrl");
			}
		});

	}
}

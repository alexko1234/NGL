"use strict";

function DetailsCtrl($scope, $http, $q, $routeParams, datatable, messages, lists, treatments, $window) {
	
	$scope.goToRun=function(){
		$window.open(jsRoutes.controllers.runs.tpl.Runs.get($scope.readset.runCode).url, 'runs');
	}
	
	$scope.save = function(){
		var queries = [];
		queries.push($http.put(jsRoutes.controllers.readsets.api.ReadSets.properties($scope.readset.code).url,
				{properties : $scope.readset.properties}));
		queries.push($http.put(jsRoutes.controllers.readsets.api.ReadSets.valuation($scope.readset.code).url, 
				{productionValuation:$scope.readset.productionValuation,bioinformaticValuation:$scope.readset.bioinformaticValuation}));
		
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
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		updateData();
		if(!isValuationMode()){
			$scope.stopEditMode();
		}
		
	};
	
	$scope.activeEditMode = function(){
		$scope.startEditMode();			
	}
	
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
			
			$http.get(jsRoutes.controllers.runs.api.Lanes.get($scope.readset.runCode, $scope.readset.laneNumber).url).success(function(data) {
				$scope.lane = data;	
			});
			$scope.isTreatmentFullScreen=false;
		});
		
		$scope.setImage = function(imageData, imageName, treatmentContext, treatmentCode, imageFullSizeWidth, imageFullSizeHeight) {
			$scope.modalImage = imageData;
			
			$scope.modalTitle = '';
			if (treatmentContext != '') {
				$scope.modalTitle = treatmentContext + ' : ';
			}
			$scope.modalTitle = $scope.modalTitle + Messages('readsets.treatments.' + treatmentCode + '.' + imageName);
			
			var margin = Messages("readsets.treatments.images.margin");			
			var zoom = Math.min((document.body.clientWidth - margin) / imageFullSizeWidth, 1);

			$scope.modalWidth = imageFullSizeWidth * zoom;
			$scope.modalHeight = imageFullSizeHeight * zoom;
			$scope.modalLeft = (document.body.clientWidth - imageFullSizeWidth * zoom )/2;
		}
		
	}
	
};
DetailsCtrl.$inject = ['$scope', '$http', '$q', '$routeParams', 'datatable', 'messages', 'lists', 'treatments', '$window'];

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


function TaxonomyCtrl($scope) {
	$scope.init = function(name) {		
		$scope.$watch('readset', function() { 
			if (angular.isDefined($scope.readset)) {				
				$scope.krona = "data:text/html;base64,"+$scope.readset.treatments[name].read1.krona.value;
				
				$scope.ncbiUrl = Messages("readsets.treatments.taxonomy.beginNcbiUrl");
			}
		});

	}
}

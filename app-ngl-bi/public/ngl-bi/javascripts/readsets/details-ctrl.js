 "use strict";

 angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$q', '$routeParams', '$sce', 'mainService', 'tabService', 'datatable', 'messages', 'lists', 'treatments', '$window', 'valuationService', 
                                                   function($scope, $http, $q, $routeParams, $sce, mainService, tabService, datatable, messages, lists, treatments, $window, valuationService) {
	
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
				mainService.stopEditMode();
				updateData();
			}
		});						
	};
	
	$scope.cancel = function(){
		$scope.messages.clear();
		mainService.stopEditMode();
		updateData();				
	};
	
	$scope.activeEditMode = function(){
		$scope.messages.clear();
		mainService.startEditMode();			
	};
	
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
		$scope.modalHeight = imageFullSizeHeight * zoom; //in order to conserve image ratio
		$scope.modalLeft = (document.body.clientWidth - $scope.modalWidth)/2;
	
		$scope.modalTop = (window.innerHeight - $scope.modalHeight)/2;
	
		$scope.modalTop = $scope.modalTop - 50; //height of header and footer
	}
	
	var updateData = function(){
		$http.get(jsRoutes.controllers.readsets.api.ReadSets.get($routeParams.code).url).success(function(data) {
			$scope.readset = data;	
			
		});
	}
	
	var isValuationMode = function(){
		return (mainService.isHomePage('valuation') || ($routeParams.page && $routeParams.page.indexOf('valuation') == 0));
	}
	
	
    $scope.deliberatelyTrustHTMLComment = function() {
		if ($scope.readset && $scope.readset.productionValuation.comment && $scope.readset.productionValuation.comment != null) {
			return $sce.trustAsHtml($scope.readset.productionValuation.comment.trim().replace(/\n/g, "<br>"));
		}
		else {
			return "";
		}
    };
	
    $scope.highLight = function(prop){
		if (lists.getValuationCriterias() && $scope.readset && $scope.readset.productionValuation) {
			return "bg-" + $scope.valuationService.valuationCriteriaClass($scope.readset, $scope.readset.productionValuation.criteriaCode, prop);
		}
		else {
			return undefined;
		}
    };
    
	var init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		$scope.treatments = treatments;
		$scope.valuationService = valuationService();
		mainService.stopEditMode();
		if(isValuationMode()){
			mainService.startEditMode();			
		}
		
		$http.get(jsRoutes.controllers.readsets.api.ReadSets.get($routeParams.code).url).success(function(data) {
			$scope.readset = data;	
				
			if(tabService.getTabs().length == 0){
				if(isValuationMode()){ //valuation mode
					tabService.addTabs({label:Messages('readsets.page.tab.validate'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("valuation").url,remove:true});
					tabService.addTabs({label:$scope.readset.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.valuation( $scope.readset.code).url,remove:true})
				}else{ //detail mode
					tabService.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("search").url,remove:true});
					tabService.addTabs({label:$scope.readset.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.get($scope.readset.code).url,remove:true})									
				}
				tabService.activeTab($scope.getTabs(1));
			}
			
			$scope.lists.refresh.resolutions({typeCode:$scope.readset.typeCode});
			$scope.lists.refresh.valuationCriterias({typeCode:$scope.readset.typeCode, objectTypeCode:"ReadSet"});
			$scope.lists.refresh.states({objectTypeCode:"ReadSet"});
			
			if(angular.isDefined($scope.readset.treatments)){				
				$scope.treatments.init($scope.readset.treatments, jsRoutes.controllers.readsets.tpl.ReadSets.treatments, 'readsets', {global:true});				
			}
			
			$http.get(jsRoutes.controllers.runs.api.Lanes.get($scope.readset.runCode, $scope.readset.laneNumber).url).success(function(data) {
				$scope.lane = data;	
			});	
			
			$http.get(jsRoutes.controllers.runs.api.RunTreatments.get($scope.readset.runCode, "ngsrg").url).success(function(data) {
				$scope.runNGSRG = data;	
			});	
			
			$http.get(jsRoutes.controllers.commons.api.StatesHierarchy.list().url,  {params: {objectTypeCode:"ReadSet"}}).success(function(data) {
				$scope.statesHierarchy = data;	
			});	
		});
	};
	
	init();
	
}]);



 angular.module('home').controller('TaxonomyCtrl', ['$scope', function($scope) {
	 
		$scope.isDataExistsForPhylogeneticTree = function() {
			var b = true;
			if (angular.isDefined($scope.readset.treatments)) {
				var treatments = $scope.readset.treatments;
				if (!angular.isDefined(treatments["taxonomy"].read1.phylogeneticTree) ||
						( angular.isDefined(treatments["taxonomy"].read1.phylogeneticTree.value) && (treatments["taxonomy"].read1.phylogeneticTree.value == null) ) ) {
					b = false;
				}
			}
			return b;
		}
			
		var init = function() {		
			$scope.$watch('readset', function() { 
				if (angular.isDefined($scope.readset)) {				
					$scope.krona = "data:text/html;base64,"+$scope.readset.treatments[$scope.treatments.getTreatment().code].read1.krona.value;
					
					$scope.ncbiUrl = Messages("readsets.treatments.taxonomy.beginNcbiUrl");
				}
			});
		}
		
		init();
}]);
 
 
 
 angular.module('home').controller('MappingCtrl', ['$scope', function($scope) {
	 
		$scope.isDataExistsForRead1 = function() {
			var b = true;
			if (angular.isDefined($scope.readset.treatments)) {
				var treatments = $scope.readset.treatments;
				if (((!angular.isDefined(treatments["mapping"].read1)) || (!angular.isDefined(treatments["mapping"].read1.errorPosition.value))) ||
						( angular.isDefined(treatments["mapping"].read1.errorPosition.value) && (treatments["mapping"].read1.errorPosition.value == null) ))  {
					b = false;
				}
			}
			return b;
		}
		
		$scope.isDataExistsForRead2 = function() {
			var b = true;
			if (angular.isDefined($scope.readset.treatments)) {
				var treatments = $scope.readset.treatments;
				if (((!angular.isDefined(treatments["mapping"].read2)) || (!angular.isDefined(treatments["mapping"].read2.errorPosition.value))) ||
						( angular.isDefined(treatments["mapping"].read2.errorPosition.value) && (treatments["mapping"].read2.errorPosition.value == null) ))  {
					b = false;
				}
			}
			return b;
		}

}]);

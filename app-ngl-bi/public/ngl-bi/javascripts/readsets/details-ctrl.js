 "use strict";

 angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$q', '$routeParams', 'datatable', 'messages', 'lists', 'treatments', '$window', 
                                                   function($scope, $http, $q, $routeParams, datatable, messages, lists, treatments, $window) {
	
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
		return ($scope.isHomePage('valuation') || $scope.isHomePage('valuationWheat') || ($routeParams.page && $routeParams.page.indexOf('valuation') == 0));
	}
	
	
	var init = function(){
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
					$scope.addTabs({label:Messages('readsets.page.tab.validate'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("valuation").url,remove:true});
					$scope.addTabs({label:$scope.readset.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.valuation( $scope.readset.code).url,remove:true})
				}else{ //detail mode
					$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("search").url,remove:true});
					$scope.addTabs({label:$scope.readset.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.get($scope.readset.code).url,remove:true})									
				}
				$scope.activeTab($scope.getTabs(1));
			}
			
			$scope.lists.refresh.resolutions({typeCode:$scope.readset.typeCode});
			$scope.lists.refresh.valuationCriterias({typeCode:$scope.readset.typeCode, objectTypeCode:"ReadSet"});
			
			if(angular.isDefined($scope.readset.treatments)){				
				$scope.treatments.init($scope.readset.treatments, jsRoutes.controllers.readsets.tpl.ReadSets.treatments,{global:true});				
			}
			
			$http.get(jsRoutes.controllers.runs.api.Lanes.get($scope.readset.runCode, $scope.readset.laneNumber).url).success(function(data) {
				$scope.lane = data;	
			});	
			
			$http.get(jsRoutes.controllers.runs.api.RunTreatments.get($scope.readset.runCode, "ngsrg").url).success(function(data) {
				$scope.runNGSRG = data;	
			});	
		});
	};
	
	init();
	
}]);

 angular.module('home').controller('NGSRGCtrl', ['$scope', 'datatable', function($scope, datatable) {
	
	var NGSRGConfig = {
			name:'NGSRG',
			order :{active:false},
			search:{active:false},
			pagination:{active:false},
			select:{active:false},
			showTotalNumberRecords:false,
			cancel : {active:false},						
			columns : [
				{  	property:"sampleOnContainer.properties.percentPerLane.value",
					header: "readsets.sampleOnContainer.percentPerLane",
					type :"Number",
					format:2,
					order:false
				},
			   	{  	property:"ngsrg.validSeqPercent.value",
			    	header: "readsets.treatments.ngsrg_illumina.validSeqPercent",
			    	type :"Number",
			    	format:2,
			    	order:false
				},
				{  	property:"ngsrg.nbCluster.value",
			    	header: "readsets.treatments.ngsrg_illumina.nbCluster",
			    	type :"Number",
			    	order:false
				},
				{  	property:"ngsrg.nbBases.value",
			    	header: "readsets.treatments.ngsrg_illumina.nbBases",
			    	type :"Number",
			    	order:false
				},
				{  	property:"ngsrg.Q30.value",
			    	header: "readsets.treatments.ngsrg_illumina.Q30",
			    	type :"Number",
			    	order:false
				},
				{  	property:"ngsrg.qualityScore.value",
			    	header: "readsets.treatments.ngsrg_illumina.qualityScore",
			    	type :"Number",
			    	order:false
				}
								
			]				
	};
		
	var init = function(){
		$scope.$watch('readset', function() {
			if(angular.isDefined($scope.readset)){
				$scope.NGSRG = datatable($scope, NGSRGConfig);
				var data = {};
				data.ngsrg = $scope.readset.treatments.ngsrg['default'];
				data.sampleOnContainer = $scope.readset.sampleOnContainer;
				$scope.NGSRG.setData([data], 1);
			}
		}); 		
	};
	
	init();
	
}]);


 angular.module('home').controller('TaxonomyCtrl', ['$scope', function($scope) {
	var init = function(name) {		
		$scope.$watch('readset', function() { 
			if (angular.isDefined($scope.readset)) {				
				$scope.krona = "data:text/html;base64,"+$scope.readset.treatments[name].read1.krona.value;
				
				$scope.ncbiUrl = Messages("readsets.treatments.taxonomy.beginNcbiUrl");
			}
		});
	}
	
	init();
}]);

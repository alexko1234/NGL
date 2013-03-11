"use strict";

function SearchCtrl($scope, datatable) {

	$scope.datatableConfig = {
			addshow:function(line){
				$scope.tabs.push({label:line.code,href:jsRoutes.controllers.runs.tpl.Runs.home(line.code).url,remove:true})
			},				
			orderBy:'traceInformation.creationDate',
			url:{
				search:jsRoutes.controllers.runs.api.Runs.list()
			}
	};
	
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);
			$scope.datatable.search();
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
	}	
};

SearchCtrl.$inject = ['$scope', 'datatable'];


function DetailsCtrl($scope, $http, $routeParams) {
	
	$scope.computeLost=function(lane){
		var count = [0];
		angular.forEach(lane.readsets, function(value, key){
			this[0]+=value.properties.nbClusterInternalAndIlluminaFilter.value;
			}, count);
		return (1 - (count[0]/lane.properties.nbClusterInternalAndIlluminaFilter.value))*100;
	};
	
	$scope.init = function(){
		$http.get(jsRoutes.controllers.runs.api.Runs.get($routeParams.code).url).success(function(data) {
			$scope.run = data;	
			//init tabs on left screen when none exist
			if($scope.tabs.length == 0){
				$scope.tabs.push({label:$scope.run.code,href:jsRoutes.controllers.runs.tpl.Runs.home($scope.run.code).url,remove:true})
				$scope.activeTab($scope.tabs[0]);				
			}			
		});
	}
	
};
DetailsCtrl.$inject = ['$scope', '$http', '$routeParams'];
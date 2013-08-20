"use strict";

function SearchCtrl($scope, $routeParams, datatable) {

	$scope.datatableConfig = {
			order :{by:'traceInformation.creationDate'},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.runs.tpl.Runs.get(line.code).url,remove:true});
				}
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
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("search").url,remove:false});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchCtrl.$inject = ['$scope', '$routeParams', 'datatable'];


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
			if($scope.getTabs().length == 0){
				$scope.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("search").url,remove:false});
				$scope.addTabs({label:$scope.run.code,href:jsRoutes.controllers.runs.tpl.Runs.get($scope.run.code).url,remove:true})
				$scope.activeTab($scope.getTabs(1));
			}
			
		});
	}
	
};
DetailsCtrl.$inject = ['$scope', '$http', '$routeParams'];
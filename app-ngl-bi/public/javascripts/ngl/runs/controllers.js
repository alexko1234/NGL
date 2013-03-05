"use strict";

function MainCtrl($scope){
	$scope.tabs = [
	               {
	            	   label:'Search',
	            	   href:'/runs/home',
	            	   clazz:'active',
	            	   remove:false
	               }	               
	              ];
	/**
	 * Set one element of list active
	 */
	$scope.activeTab = function(tab){
		tab.clazz='active';
		for(var i = 0; i < $scope.tabs.length; i++){
			if($scope.tabs[i].href != tab.href){
				$scope.tabs[i].clazz='';
			}
		}
	};
	$scope.removeTab = function(index){
		$scope.tabs.splice(index,1);
	}
}


function SearchCtrl($scope, datatable) {

	$scope.datatableConfig = {
			addshow:function(line){
				$scope.tabs.push({label:line.code,href:'/runs/'+line.code,remove:true})
				},				
			edit: false,
			orderReverse:false,
			orderBy:'traceInformation.creationDate',
			editColumn: {
				all:undefined,
				traceInformation:{creationDate:undefined}
				
			},
			updateColumn: {
				traceInformation:{creationDate:undefined}
			},
			hideColumn: {
				traceInformation:{creationDate:undefined}
			},
			orderColumn:{
				traceInformation:{creationDate:true}
			},
			url:{
				save:"",
				remove:"",
				search:'/api/runs'
			}
	};
	
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.datatable.search();
	}
	
};

SearchCtrl.$inject = ['$scope', 'datatable'];


function DetailsCtrl($scope, $http, $routeParams) {
	
	
	$http.get("/api/runs/"+$routeParams.code).success(function(data) {
		$scope.run =  data;		
	});
	
	$scope.computeLost=function(lane){
		var count = [0];
		angular.forEach(lane.readsets, function(value, key){
			this[0]+=value.properties.nbClusterInternalAndIlluminaFilter.value;
			}, count);
		return (1 - (count[0]/lane.properties.nbClusterInternalAndIlluminaFilter.value))*100;
	};
	
};
DetailsCtrl.$inject = ['$scope', '$http', '$routeParams'];
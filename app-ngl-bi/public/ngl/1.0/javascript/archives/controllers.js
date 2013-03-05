"use strict";

function MainCtrl($scope){
	$scope.tabs = [
	               {
	            	   label:'Search',
	            	   href:'/archives/home',
	            	   clazz:'active',
	            	   remove:false
	               }	               
	              ];	
}


function SearchCtrl($scope, datatable) {
	$scope.archive = 2; //default only need archive
	
	$scope.datatableConfig = {
			edit: false,
			orderReverse:true,
			orderBy:'date',
			editColumn: {
				all:undefined				
			},
			updateColumn: {
			},
			hideColumn: {
			},
			orderColumn:{
			},
			url:{
				search:'/api/archives/readsets'
			}
	};
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.search();
	}
	
	$scope.search = function(){
		$scope.datatable.search({archive:$scope.archive});
	}
	
};

SearchCtrl.$inject = ['$scope', 'datatable'];

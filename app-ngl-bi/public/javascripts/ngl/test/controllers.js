"use strict";
 
function SearchCtrl($scope, datatable) {

	$scope.datatableConfig = {
			edit: false,
			orderReverse:false,
			orderBy:undefined,
			editColumn: {
				all:undefined,
				_id:undefined,
				code:undefined
				
			},
			updateColumn: {
				_id:undefined,
				code:undefined
			},
			hideColumn: {
				_id:undefined,
				code:undefined
			},
			orderColumn:{
				_id:undefined,
				code:undefined
			},
			url:{
				//save:"/admin/types?format=json",
				remove:"",
				search:'/api/runs'
			},
	};
	
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.datatable.search();
		
	}
	
};

SearchCtrl.$inject = ['$scope', 'datatable'];


function DetailsCtrl($scope, $http, $routeParams) {
	$http.get("/runs/"+$routeParams.code+"?format=json").success(function(data) {
		$scope.run =  data;
	});
	
};
DetailsCtrl.$inject = ['$scope', '$http', '$routeParams'];
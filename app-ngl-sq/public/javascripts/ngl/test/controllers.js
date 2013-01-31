 "use strict";

function DataTableCtrl($scope, $http, datatable) {
	

	$scope.name = "test";
	$scope.types=[{"code":'1',"label":'L1'},{"code":'2',"label":'L2'},{"code":'3',"label":'L3'}];
	
	$scope.datatableConfig = {
			edit: false,
			orderReverse:false,
			orderBy:undefined,
			editColumn: {
				all:undefined,
				code:undefined,
				name:undefined,
				collectionName:undefined
			},
			updateColumn: {
				code:undefined,
				name:undefined,
				collectionName:undefined
			},
			hideColumn: {
				id:undefined,
				code:undefined,
				name:undefined,
				objectType:{type:undefined},
				collectionName:undefined
			},
			orderColumn:{
				id:undefined,
				code:undefined,
				name:undefined,
				objectType:{type:undefined},
				collectionName:undefined
			},
			url:{
				//save:"/admin/types?format=json",
				remove:""
			}			
	};
	
	$scope.init = function(){
		$scope.datatable = datatable($scope.datatableConfig);
		$scope.datatable.search = function() {
			/*
			$http.get('/admin/types',{params:{name:$scope.name,typeCode:$scope.type.code}}).success(function(data) {
				  $scope.tableresult = data;
				  $scope.tableresultMaster = angular.copy($scope.tableresult);
			});
			*/
			this.searchresult = new Array();
			for(var i=0; i < 50; i++){
				var o = {
						id :i,
						code: "code"+i,
						name:"name"+i,
						objectType:{type:"Experiment Type"},
						collectionName:"col"+i
				};
				this.searchresult[i] = o;
			}
			
			this.searchresultMaster = angular.copy(this.searchresult);
				
				
		}
	}
	
	
}

DataTableCtrl.$inject = ['$scope', '$http', 'datatable'];
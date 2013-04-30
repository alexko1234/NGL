"use strict";

function SearchCtrl($scope, $http,datatable) {

	var datatableConfig = {
			order :{by:'code', mode:'local'},
			search:{
				url:jsRoutes.controllers.plaques.api.Plaques.list()
			},
			pagination:{
				active:true,
				mode:'local'
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home(line.code).url,remove:true, from:'search'});
				}
			}
	};
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getTabs(0))){
			$scope.addTabs({label:Messages('plates.tabs.search'),href:jsRoutes.controllers.plaques.tpl.Plaques.home("search").url,remove:false, from:'search'});
			$scope.activeTab($scope.getTabs(0));
		}
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, datatableConfig);
			$scope.datatable.search();
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {
					projects:{},
					etmanips:{}
			};
			
			$scope.setForm($scope.form);
			$http.get(jsRoutes.controllers.lists.api.Lists.projects().url).
				success(function(data, status, headers, config){
					$scope.form.projects.options = data;
				});
						
			$http.get(jsRoutes.controllers.lists.api.Lists.etmanips().url).
				success(function(data, status, headers, config){
					$scope.form.etmanips.options = data;
				});
						
		}else{
			$scope.form = $scope.getForm();			
		}
	};
	
	$scope.search = function(){		
		var jsonSearch = {};
		if($scope.form.projects.selected){
			jsonSearch.project = $scope.form.projects.selected.code;
		}
		
		if($scope.form.etmanips.selected){
			jsonSearch.etmanip = $scope.form.etmanips.selected.code;
		}
		$scope.datatable.search(jsonSearch);
	};
};

SearchCtrl.$inject = ['$scope', '$http','datatable'];

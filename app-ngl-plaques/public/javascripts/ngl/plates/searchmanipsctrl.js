"use strict";

function SearchManipsCtrl($scope, $http, datatable, basket) {

	var datatableConfig = {
			order :{by:'matmanom', mode:'local'},
			search:{
				url:jsRoutes.controllers.manips.api.Manips.list()
			},
			pagination:{
				active:true,
				mode:'local'
			},
			otherButtons:{
				active:true				
			}
	};
	
	$scope.init = function(){
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('creation');
		}
		
		if(angular.isUndefined($scope.getTabs(0))){
			$scope.addTabs({label:Messages('plates.tabs.searchmanips'),href:jsRoutes.controllers.plaques.tpl.Plaques.home("new").url,remove:false});
			$scope.activeTab(0);
		}
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, datatableConfig);
			$scope.setDatatable($scope.datatable);	
			
		}else{
			$scope.datatable = $scope.getDatatable();
		}

		if(angular.isUndefined($scope.getBasket())){
			$scope.basket = basket($scope);			
			$scope.setBasket($scope.basket);
		}else{
			$scope.basket = $scope.getBasket();
		}
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {
					projects:{},
					etmateriels:{},
					etmanips:{}
			};
			
			$scope.setForm($scope.form);
			//jsRoutes.controllers.lists.api.Lists.processTypes().url
			$http.get(jsRoutes.controllers.lists.api.Lists.projects().url).
			success(function(data, status, headers, config){
				$scope.form.projects.options = data;
			});
			
			$http.get(jsRoutes.controllers.lists.api.Lists.etmateriels().url).
			success(function(data, status, headers, config){
				$scope.form.etmateriels.options = data;
				for(var i = 0; i < data.length ; i++){
					//by default Disponible
					if(data[i].code === "2"){
						$scope.form.etmateriels.selected = data[i];
						break;
					}
				}
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
		
		if($scope.form.etmanips.selected && $scope.form.etmateriels.selected){
			jsonSearch.emateriel =  $scope.form.etmateriels.selected.code;
			jsonSearch.etmanip = $scope.form.etmanips.selected.code;
			if($scope.form.projects.selected){
				jsonSearch.project = $scope.form.projects.selected.code;
			}
		
			$scope.datatable.search(jsonSearch);
			
		}
	};

	$scope.addToBasket = function(manips){
		for(var i = 0; i < manips.length; i++){
			var well = {
					code:manips[i].matmaco,
					name:manips[i].matmanom,
					typeCode:$scope.form.etmanips.selected.code,
					typeName:$scope.form.etmanips.selected.name
			};		
			this.basket.add(well);
		}
		if(this.basket.length() > 0 && $scope.getTabs().length === 1){
			$scope.addTabs({label:Messages('plates.tabs.new'),href:jsRoutes.controllers.plaques.tpl.Plaques.get("new").url,remove:false});//$scope.getTab()[1]
		}
		
	};
		
};
SearchManipsCtrl.$inject = ['$scope', '$http','datatable','basket'];



"use strict";

function SearchCtrl($scope, $http,datatable) {

	$scope.datatableConfig = {
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
					$scope.tabs.push({label:line.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home(line.code).url,remove:true});
				}
			}
	};
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.tabs[0])){
			$scope.tabs.push({label:"Recherche",href:jsRoutes.controllers.plaques.tpl.Plaques.home("search").url,remove:false});
			$scope.activeTab($scope.tabs[0]);
		}
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);
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
			//jsRoutes.controllers.lists.api.Lists.processTypes().url
			$http.get(jsRoutes.controllers.Lists.projects().url).
			success(function(data, status, headers, config){
				$scope.form.projects.options = data;
			});
						
			$http.get(jsRoutes.controllers.Lists.etmanips().url).
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


function SearchManipsCtrl($scope, $http, datatable, basket) {

	$scope.datatableConfig = {
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
		if(angular.isUndefined($scope.tabs[0])){
			$scope.tabs.push({label:"Recherche Manips",href:jsRoutes.controllers.plaques.tpl.Plaques.home("new").url,remove:false, edit:true});
			$scope.activeTab($scope.tabs[0]);
		}
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);
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
			$http.get(jsRoutes.controllers.Lists.projects().url).
			success(function(data, status, headers, config){
				$scope.form.projects.options = data;
			});
			
			$http.get(jsRoutes.controllers.Lists.etmateriels().url).
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
			
			$http.get(jsRoutes.controllers.Lists.etmanips().url).
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
					name:manips[i].matmanom
			};		
			this.basket.add(well);
		}
		if(this.basket.length() > 0 && $scope.tabs.length === 1){
			$scope.tabs.push({label:"Nouveau",href:jsRoutes.controllers.plaques.tpl.Plaques.home("details").url,remove:false});//$scope.tabs[1]
		}
		
	};
		
};
SearchManipsCtrl.$inject = ['$scope', '$http','datatable','basket'];


function DetailsCtrl($scope, $http, $routeParams, datatable, basket) {
	
	$scope.datatableConfig = {
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				active:true,
				mode:'local'
			},
			edit : {
				active:true
			},
			remove:{
				active:true,
				mode:'local',
				callback : function(datatable){
					$scope.basket.reset();
					$scope.basket.add(datatable.allResult);
				}
			},
			otherButtons:{
				active:true
			}
		};
		
	$scope.init = function(){
		$scope.clearMessages();		
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.plate = {code:undefined, wells:undefined};
		
		if(angular.isUndefined($scope.getBasket())){
			$scope.basket = basket($scope);			
			$scope.setBasket($scope.basket);
			$scope.editMode = false;
		}else{			
			$scope.basket = $scope.getBasket();
		}
		
		$scope.editMode = false;
		if($scope.tabs[0].edit){
			$scope.editMode = true; // panier exist donc edition
		}
		
		if($routeParams.code === 'details'){
			$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
			$scope.editMode = true;
		}else{			
			$http.get(jsRoutes.controllers.plaques.api.Plaques.get($routeParams.code).url).success(function(data) {
				$scope.plate.code=data.code;
				$scope.datatable.setData(data.wells, data.wells.length);
				$scope.datatable.addData($scope.basket.get());
				//init tabs on left screen when none exist
				if($scope.tabs.length == 0){
					$scope.tabs.push({label:$scope.plate.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home($scope.plate.code).url,remove:true});
					$scope.activeTab($scope.tabs[0]);				
				}			
			});		
		}			
	};
	
	$scope.edit = function(){
		$scope.editMode = true;
		$scope.resetTabs();
		$scope.setDatatable(undefined);	
		$scope.setForm(undefined);	
		$scope.tabs.push({label:"Recherche Manips",href:jsRoutes.controllers.plaques.tpl.Plaques.home("new").url,remove:false, edit:true});
		$scope.tabs.push({label:$scope.plate.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home($scope.plate.code).url,remove:false});
		$scope.activeTab($scope.tabs[1]);
	};
	
	$scope.computeXY = function(){
		var wells = $scope.datatable.getData();
		var nbCol = 12;
		var nbLine = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
		var x = 0;
		for(var i = 0; i < nbCol ; i++){
			for(var j = 0; j < nbLine.length; j++){
				if(x < wells.length){
					wells[x].y = nbLine[j]+'';
					wells[x].x = i+1+'';					
				}
				x++;
			}
		}
		$scope.datatable.setData(wells,wells.length);
		$scope.basket.reset();
		$scope.basket.add(wells);	
	};
	
	$scope.save = function(){
		$scope.clearMessages();
		$scope.plate.wells = $scope.datatable.displayResult;
		
		$http.post(jsRoutes.controllers.plaques.api.Plaques.save().url, $scope.plate).
			success(function(data, status, headers, config){
				$scope.plate.code=data.code;
				$scope.datatable.setData(data.wells,data.wells.length);
				$scope.basket.reset();
				$scope.message.clazz="alert alert-success";
				$scope.message.text="Sauvegarde réussie";
				$scope.tabs[1]= {label:$scope.plate.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home($scope.plate.code).url,remove:false};
				
		}).error(function(data, status, headers, config){
				$scope.message.clazz="alert alert-error";
				$scope.message.text="Sauvegarde échouée";
				$scope.message.details = data;
		});
	
	};
	
	$scope.clearMessages  = function(){
		$scope.message = {clazz : undefined, text : undefined, isDetails : false, details : []};
	}
	
	$scope.displayCellPlaque =function(x, y){
		var wells = $scope.datatable.getData();
		if(!angular.isUndefined(wells)){
	        for (var i = 0; i <wells.length; i++) {
		         if (wells[i].x === (x+'') && wells[i].y===(y+'')) {
		        	 return wells[i].name;
		         }
	        }
		}
        return "------";
     }
};
DetailsCtrl.$inject = ['$scope', '$http', '$routeParams', 'datatable', 'basket'];
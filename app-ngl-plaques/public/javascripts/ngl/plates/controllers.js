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
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home(line.code).url,remove:true});
				}
			}
	};
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getTabs(0))){
			$scope.addTabs({label:Messages('plates.tabs.search'),href:jsRoutes.controllers.plaques.tpl.Plaques.home("search").url,remove:false});
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
		if(angular.isUndefined($scope.getTabs(0))){
			$scope.addTabs({label:Messages('plates.tabs.searchmanips'),href:jsRoutes.controllers.plaques.tpl.Plaques.home("new").url,remove:false, edit:true});
			$scope.activeTab($scope.getTabs(0));
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
					name:manips[i].matmanom
			};		
			this.basket.add(well);
		}
		if(this.basket.length() > 0 && $scope.getTabs().length === 1){
			$scope.addTabs({label:Messages('plates.tabs.new'),href:jsRoutes.controllers.plaques.tpl.Plaques.home("details").url,remove:false});//$scope.getTabs()[1]
		}
		
	};
		
};
SearchManipsCtrl.$inject = ['$scope', '$http','datatable','basket'];


function DetailsCtrl($scope, $http, $routeParams, datatable, basket) {
	
	var datatableConfig = {
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
				active:false
			},
			remove:{
				active:false,
				mode:'local',
				callback : function(datatable){
					$scope.basket.reset();
					$scope.basket.add(datatable.allResult);
				}
			}
		};
		
	$scope.init = function(){
		$scope.clearMessages();		
		$scope.datatable = datatable($scope, datatableConfig);
		$scope.plate = {code:undefined, wells:undefined};
		
		if(angular.isUndefined($scope.getBasket())){
			$scope.basket = basket($scope);			
			$scope.setBasket($scope.basket);			
		}else{			
			$scope.basket = $scope.getBasket();
		}
		
		$scope.editMode = false;
		if($scope.getTabs().length > 0 && $scope.getTabs(0).edit){
			$scope.setEditConfig(true);
		}
		
		if($routeParams.code === 'details'){
			$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);			
		}else{			
			$http.get(jsRoutes.controllers.plaques.api.Plaques.get($routeParams.code).url).success(function(data) {
				$scope.plate.code=data.code;
				$scope.datatable.setData(data.wells, data.wells.length);
				$scope.datatable.addData($scope.basket.get());
				//init tabs on left screen when none exist
				if($scope.getTabs().length == 0){
					$scope.addTabs({label:$scope.plate.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home($scope.plate.code).url,remove:true});
					$scope.activeTab($scope.getTabs(0));				
				}			
			});		
		}			
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
				$scope.message.text=Messages('plates.msg.save.sucess')
				
				if($scope.isBackupTabs()){
					$scope.restoreBackupTabs();
					$scope.setEditConfig(false);							
				}else{
					$scope.setTab(1,{label:$scope.plate.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home($scope.plate.code).url,remove:false});
				}
		}).error(function(data, status, headers, config){
				$scope.message.clazz="alert alert-error";
				$scope.message.text=Messages('plates.msg.save.error');
				$scope.message.details = data;
				$scope.message.isDetails = true;
				var columns = $scope.datatable.getColumnsConfig();
				
				for(var i = 0; i < $scope.plate.wells.length; i++){
					var isError = false;
					for(var j = 0; j < columns.length; j++){
						if(new Function("data", "return (!angular.isUndefined(data['wells["+i+"]."+columns[j].property+"']))")(data)){
							isError = true;
						}
					}		
					if(isError){
						$scope.plate.wells[i].trClass = "error";
					}else{
						$scope.plate.wells[i].trClass = "success";
					}
				} 
				/*
				    angular.forEach(data, function(value, key){
					console.log(key + ': ' + value);
					});
				 */
		});
	};
	
	$scope.edit = function(){
		$scope.setDatatable(undefined);	
		$scope.setForm(undefined);	
		$scope.setEditConfig(true);
		
		$scope.backupTabs();
		$scope.resetTabs();		
		$scope.addTabs({label:Messages('plates.tabs.searchmanips'),href:jsRoutes.controllers.plaques.tpl.Plaques.home("new").url,remove:false,edit:true});
		$scope.addTabs({label:$scope.plate.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home($scope.plate.code).url,remove:false});
		$scope.activeTab($scope.getTabs(1));
	};
	
	$scope.setEditConfig = function(value){
		$scope.editMode = value;
		$scope.datatable.getConfig().edit.active=value;
		$scope.datatable.getConfig().remove.active=value;		
		
	};
	
	$scope.clearMessages  = function(){
		$scope.message = {clazz : undefined, text : undefined, showDetails : false, isDetails : false, details : []};
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
"use strict";

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
				active:false,
				withoutSelect:true,
				showButton:false
			},
			remove:{
				active:false,
				withEdit:true,
				mode:'local',
				callback : function(datatable){
					var dataInitial = $scope.basket.get();
					var dataFinal = []; 
					var dateDatatable = $scope.datatable.displayResult;
					for(var i = 0; i < dataInitial.length ; i++){
						for(var j = 0; j < dateDatatable.length ; j++){
							if(dataInitial[i].code === dateDatatable[j].code){
								dataFinal.push(dataInitial[i]);
								break;
							}
						}
					}
					$scope.basket.reset();
					$scope.basket.add(dataFinal);
				}
			},
			cancel : {
				showButton:false
			},
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
		
		//extract from creation or search
		if($scope.getTabs().length > 0){
			$scope.from = $scope.getTabs(0).from;			
		}
		
		//on traite le details
		if($scope.isCreation() && $scope.getTabs().length > 0 && $scope.getTabs(1).isNew){
			$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
			$scope.edit();
		}else{
			$http.get(jsRoutes.controllers.plaques.api.Plaques.get($routeParams.code).url).success(function(data) {
				$scope.plate.code=data.code;
				$scope.datatable.setData(data.wells, data.wells.length);
				$scope.datatable.addData($scope.basket.get());	
				
				if($scope.getTabs().length > 0 && $scope.getTabs(1).isEdit){
					$scope.edit();
				}else {
					$scope.unedit();
				}
			});		
		}			
	};
	
	$scope.isCreation = function(){
		return ($scope.from === 'creation');
	};
	
	$scope.isDetails = function(){
		return ($scope.from === 'details');
	};
	
	
	/**
	 * Pass in edit mode
	 */
	$scope.edit = function(){
		$scope.setEditConfig(true);		
		$scope.datatable.setEditColumn();
		
		if(!$scope.isCreation() && !$scope.isDetails()){
			$scope.backupTabs();
			$scope.resetTabs();
			$scope.addTabs({label:Messages('plates.tabs.searchmanips'),href:jsRoutes.controllers.plaques.tpl.Plaques.home("new").url,remove:false,from:'details', isEdit:$scope.editMode});
			$scope.addTabs({label:$scope.plate.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home($scope.plate.code).url,remove:false,from:'details', isEdit:$scope.editMode});
			$scope.activeTab($scope.getTabs(1));
			//reinit datatable and form
			$scope.setDatatable(undefined);	
			$scope.setForm(undefined);			
		}
	};
	
	/**
	 * Remove all change
	 */
	$scope.unedit = function(){
		$scope.clearMessages();
		$scope.setEditConfig(false);
		$scope.datatable.cancel();
		
		if(!$scope.isCreation() && $scope.isBackupTabs()){
			$scope.restoreBackupTabs();
			$scope.setDatatable(undefined);	
			$scope.setForm(undefined);
			$scope.from = 'search';
		}		
	}
	
	/**
	 * Configure edit and remote on datatable
	 */
	$scope.setEditConfig = function(value){
		$scope.editMode = value;
		var config = $scope.datatable.getConfig();
		config.edit.active=value;		
		config.remove.active=value;
		$scope.datatable.setConfig(config);
		if($scope.isCreation()){
			$scope.getTabs(1).isEdit=value;								
		}
				
	};
	
	/**
	 * Save the entire plate
	 */
	$scope.save = function(){
		$scope.clearMessages();
		$scope.plate.wells = $scope.datatable.displayResult;
		
		$http.post(jsRoutes.controllers.plaques.api.Plaques.save().url, $scope.plate).
			success(function(data, status, headers, config){
				$scope.plate.code=data.code;
				$scope.datatable.setData(data.wells,data.wells.length);
				$scope.basket.reset();
				$scope.unedit();
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('plates.msg.save.sucess')
				
				if($scope.isCreation()){
					$scope.setTab(1,{label:$scope.plate.code,href:jsRoutes.controllers.plaques.tpl.Plaques.home($scope.plate.code).url,remove:false, from:'creation', isEdit:$scope.editMode});					
				}				
		}).error(function(data, status, headers, config){
				$scope.message.clazz="alert alert-error";
				$scope.message.text=Messages('plates.msg.save.error');
				$scope.message.details = data;
				$scope.message.isDetails = true;
				var columns = $scope.datatable.getColumnsConfig();
				
				for(var i = 0; i < $scope.plate.wells.length; i++){
					var isError = false;
					if(!angular.isUndefined(data["wells["+i+"]"])){
						isError = true;
					}
					for(var j = 0; j < columns.length && !isError; j++){
						if(!angular.isUndefined(data["wells["+i+"]."+columns[j].property])){
							isError = true;
						}						
					}		
					if(isError){
						$scope.plate.wells[i].trClass = "error";
					}else{
						$scope.plate.wells[i].trClass = "success";
					}
				} 				
		});
	};
	
	$scope.clearMessages  = function(){
		$scope.message = {clazz : undefined, text : undefined, showDetails : false, isDetails : false, details : []};
	}
	/**
	 * Compute the coordinates
	 */
	$scope.computeXY = function(){
		var wells = $scope.datatable.displayResult;
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
	};
	
	$scope.displayCellPlaque =function(x, y){
		var wells = $scope.datatable.displayResult;
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
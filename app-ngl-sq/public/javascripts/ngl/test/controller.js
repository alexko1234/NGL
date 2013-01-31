 "use strict";

function DataTableCtrl($scope, $http) {
	

	$scope.name = "test";
	$scope.types=[{"code":'1',"label":'L1'},{"code":'2',"label":'L2'},{"code":'3',"label":'L3'}];
	
	$scope.orderBy = "name";
	
	$scope.search = function() {
		/*
		$http.get('/admin/types',{params:{name:$scope.name,typeCode:$scope.type.code}}).success(function(data) {
			  $scope.tableresult = data;
			  $scope.tableresultMaster = angular.copy($scope.tableresult);
		});
		*/
		$scope.tableresult = new Array();
		for(var i=0; i < 50; i++){
			var o = {
					id :i,
					code: "code"+i,
					name:"name"+i,
					objectType:{type:"Experiment Type"},
					collectionName:"col"+i
			};
			$scope.tableresult[i] = o;
		}
		
		$scope.tableresultMaster = angular.copy($scope.tableresult);
			
			
	}
	
	
	$scope.datatableConfig = {
			edit: false,
			orderReverse:false,
			orderBy:false,
			editColumn: {
				all:false,
				code:false,
				name:false,
				collectionName:false
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
	$scope.datatableConfigMaster = angular.copy($scope.datatableConfig);
	
	//$scope.datatableConfig = {};
	
	
	
	/**
	 * Select all the table line
	 */
	$scope.select = function(line){
		if(line){
			if(!line.selected){
				line.selected=true;
				line.trClass="row_selected";
			}
		}else{
			for(var i = 0; i < $scope.tableresult.length; i++){
				$scope.tableresult[i].selected=true;
				$scope.tableresult[i].trClass="row_selected";
			}
		}
	};

	/**
	 * cancel all actions (edit, hide, order, etc.)
	 */
	$scope.cancel = function(){
		$scope.tableresult = angular.copy($scope.tableresultMaster);
		$scope.datatableConfig = angular.copy($scope.datatableConfigMaster);
	};
	/**
	 * Save the selected table line
	 */
	$scope.save = function(){
		for(var i = 0; i < $scope.tableresult.length; i++){
			if($scope.tableresult[i].selected){
				if($scope.datatableConfig.url.save){
					$scope.saveObject($scope.tableresult[i], i);
				}else{
					$scope.tableresult[i].selected = false;
					$scope.tableresult[i].edit=false;
					$scope.tableresult[i].trClass = undefined;
					$scope.tableresultMaster[i] = angular.copy($scope.tableresult[i]);
					$scope.tableresult[i].trClass = "success";
				}
			}						
		}
		//$scope.datatableConfig = angular.copy($scope.datatableConfigMaster);
	};
	
	$scope.saveObject = function(value, i){
		$http.post($scope.datatableConfig.url.save, value)
		.success(function(data) {
			$scope.tableresult[i].selected = false;
			$scope.tableresult[i].edit=false;
			$scope.tableresult[i].trClass = undefined;
			$scope.tableresultMaster[i] = angular.copy($scope.tableresult[i]);
			$scope.tableresult[i].trClass = "success";
		})
		.error(function(data) {
			$scope.tableresult[i].trClass = "error";
		});
		
	};
	
	/**
	 *  Remove the selected table line
	 */
	$scope.remove = function(){
		for(var i = 0; i < $scope.tableresult.length; i++){
			if($scope.tableresult[i].selected){
				$scope.tableresult.splice(i,1);				
				$scope.tableresultMaster.splice(i,1);
				//missing update in db
				i--;
			}						
		}
		$scope.datatableConfig = angular.copy($scope.datatableConfigMaster);
	};
	
	/**
	 * set Edit all column or just one
	 * @param editColumnName : column name
	 */
	$scope.setEditColumn = function(editColumnName){		
		var find = false;
		for(var i = 0; i < $scope.tableresult.length; i++){
			if($scope.tableresult[i].selected){
				$scope.tableresult[i].edit=true;
				find = true;
			}else{
				$scope.tableresult[i].edit=false;
			}
		}
		if(find){
			$scope.datatableConfig.edit = true;			
			if(editColumnName){  (new Function("$scope","$scope.datatableConfig.editColumn."+editColumnName+"=true"))($scope);}
			else $scope.datatableConfig.editColumn.all = true;
		}
	};
	/**
	 * Test if a column must be in edition mode
	 * @param editColumnName : column name
	 * @param line : the line in the table
	 */
	$scope.isEdit = function(editColumnName, line){
		if(editColumnName && line){
			var columnEdit = (new Function("$scope","return $scope.datatableConfig.editColumn."+editColumnName))($scope);
			return (line.edit && columnEdit) || (line.edit && $scope.datatableConfig.editColumn.all);
		}else if(editColumnName){
			var columnEdit =  (new Function("$scope","return $scope.datatableConfig.editColumn."+editColumnName))($scope);
			return (columnEdit || $scope.datatableConfig.editColumn.all);
		}else{
			return $scope.datatableConfig.edit;
		}
	}
	/**
	 * Update all line with the same value
	 * @param updateColumnName : column name
	 */
	$scope.updateColumn = function(updateColumnName){	
		for(var i = 0; i < $scope.tableresult.length; i++){
			if($scope.tableresult[i].selected){
				var fn = new Function("$scope","i", "$scope.tableresult[i]."+updateColumnName+"=$scope.datatableConfig.updateColumn."+updateColumnName);
				fn($scope, i);				
			}
		}
	}
	//Hide a column
	/**
	 * set the hide column
	 * @param hideColumnName : column name
	 */
	$scope.setHideColumn = function(hideColumnName){	
		var fn = new Function("$scope", "if(!$scope.datatableConfig.hideColumn."+hideColumnName+"){$scope.datatableConfig.hideColumn."+hideColumnName+"=true;} else{ $scope.datatableConfig.hideColumn."+hideColumnName+"=false;}");
		fn($scope);		
	};
	/**
	 * Test if a column must be hide
	 * @param hideColumnName : column name 
	 */
	$scope.isHide = function(hideColumnName){
		var fn = new Function("$scope", "return $scope.datatableConfig.hideColumn."+hideColumnName);
		return fn($scope);
	}
	/**
	 * set the order column name
	 * @param orderColumnName : column name
	 */
	$scope.setOrderColumn= function(orderColumnName){
		$scope.datatableConfig.orderBy = orderColumnName;
		var fn = new Function("$scope", "if(!$scope.datatableConfig.orderColumn."+orderColumnName+"){$scope.datatableConfig.orderColumn."+orderColumnName+"=true; $scope.datatableConfig.orderReverse=true;} else{ $scope.datatableConfig.orderColumn."+orderColumnName+"=false; $scope.datatableConfig.orderReverse=false;}");
		fn($scope);
	};
	
}

DataTableCtrl.$inject = ['$scope', '$http'];
function SearchMainCtrl($scope){
	//contain each tab of on element of the datatable
	$scope.tabs = [];
	$scope.searchTab = {
		clazz:'active'	
	};
	/**
	 * function to keep the datatable when we display a detail of one element
	 */
	$scope.setDatatable= function(datatable){
		$scope.datatableMaster = datatable;
	};
	
	/**
	 * function to return the datatable
	 */
	$scope.getDatatable= function(){
		return $scope.datatableMaster;
	};
	
	/**
	 * Set one element of list active
	 */
	$scope.activeTab = function(tab){
		if(angular.isObject(tab)){
			tab.clazz='active';
			$scope.searchTab.clazz='';
			for(var i = 0; i < $scope.tabs.length; i++){
				if($scope.tabs[i].href != tab.href){
					$scope.tabs[i].clazz='';
				}
			}
		}else{
			$scope.searchTab.clazz='active';
			for(var i = 0; i < $scope.tabs.length; i++){				
					$scope.tabs[i].clazz='';
			}
		}
		
	};
	/**
	 * remove one tab
	 */
	$scope.removeTab = function(index){
		$scope.tabs.splice(index,1);
	}
}
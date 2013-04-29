function TabMainCtrl($scope){
	//contain each tab of on element of the datatable
	var tabs = [];
	var bcktabs = undefined;
	var basketMaster = undefined;
	var datatableMaster = undefined;
	var form = undefined;
	
	$scope.hideTabs = { 
		hide:false,
		clazz:'icon-resize-full'
	};
	
	/**
	 * function to reset all tabs
	 */
	$scope.getTabs = function(index){
		if(angular.isUndefined(index)){
			return tabs;
		}else{
			return tabs[index];
		}
		
	};
	
	/**
	 * function to reset all tabs
	 */
	$scope.resetTabs = function(){
		tabs = [];
	};
	
	/**
	 * function to reset all tabs
	 */
	$scope.addTabs = function(newtabs){
		if(angular.isArray(newtabs)){
			for(var i = 0; i < newtabs.length; i++){
				tabs.push(newtabs[i]);
			}
		}else{
			tabs.push(newtabs);
		}		
	};
	
	/**
	 * set tab to a specific index
	 */
	$scope.setTab = function(index, tab){
		tabs[index] = tab;	
	};
	
	/**
	 * remove one tab
	 */
	$scope.removeTab = function(index){
		tabs.splice(index,1);
	};
	
	/**
	 * Set one element of list active
	 */
	$scope.activeTab = function(tab){
		if(angular.isObject(tab)){
			tab.clazz='active';
			for(var i = 0; i < tabs.length; i++){
				if(tabs[i].href != tab.href){
					tabs[i].clazz='';
				}
			}
		}else{
			for(var i = 0; i < tabs.length; i++){				
					tabs[i].clazz='';
			}
		}
		
	};
	/**
	 * Backup the current tabs
	 */
	$scope.backupTabs = function(){
		bcktabs = angular.copy(tabs);
	}
	
	/**
	 * Backup the current tabs
	 */
	$scope.isBackupTabs = function(){
		return !angular.isUndefined(bcktabs);
	}
	
	/**
	 * Backup the current tabs
	 */
	$scope.restoreBackupTabs = function(){
		tabs = angular.copy(bcktabs);
		bcktabs = undefined;
	}
	
	/**
	 * function to keep the basket when we switch views
	 */
	$scope.getBasket = function(){
		return basketMaster;
	};
	
	/**
	 * function to return the basket
	 */
	$scope.setBasket = function(basket){
		basketMaster = basket;
	};
	
	/**
	 * function to keep the form when we switch views
	 */
	$scope.getForm = function(){
		return form;
	};
	
	/**
	 * function to return the form
	 */
	$scope.setForm = function(form){
		form = form;
	};
	
	/**
	 * function to keep the datatable when we display a detail of one element
	 */
	$scope.setDatatable= function(datatable){
		datatableMaster = datatable;
	};
	
	/**
	 * function to return the datatable
	 */
	$scope.getDatatable= function(){
		return datatableMaster;
	};
	
		
	$scope.toggleTabs = function(){
		$scope.hideTabs.hide = !$scope.hideTabs.hide;
		if($scope.hideTabs.hide){
			$scope.hideTabs.clazz='icon-resize-small';
		}else{
			$scope.hideTabs.clazz='icon-resize-full';
		}
	};
	
	$scope.setHideTabs =  function(){
		$scope.hideTabs.hide = true;
		$scope.hideTabs.clazz='icon-resize-small';
	};
}
"use strict";

function TabMainCtrl($scope, $location){
	//contain each tab of on element of the datatable
	var tabs = [];
	var bcktabs = undefined;
	var basketMaster = undefined;
	var datatableMaster = undefined;
	var form = undefined;
	var homePage = undefined; //the home page who called the ctrl
	var editMode = false; //active or not the edit mode for the details page
	
	$scope.hideTabs = { 
		hide:false,
		clazz:'icon-resize-full'
	};
	
	/**
	 * function to get all tabs or one
	 */
	$scope.getTabs = function(index){
		if(angular.isUndefined(index)){
			return tabs;
		}else{
			return tabs[index];
		}
		
	};
	
	/**
	 * function to get a tabs
	 */
	$scope.getTab = function(index){		
		return tabs[index];				
	};
	
	/**
	 * function to reset all tabs
	 */
	$scope.resetTabs = function(){
		tabs = [];
	};
	
	/**
	 * function to add tabs
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
	$scope.activeTab = function(value, changeLocation){
		var tab = undefined;
		if(angular.isNumber(value)){
			tab = tabs[value];
		}else if(angular.isObject(value)){
			tab = value;
		}
		
		if(!angular.isUndefined(tab)){
			tab.clazz='active';
			for(var i = 0; i < tabs.length; i++){
				if(tabs[i].href != tab.href){
					tabs[i].clazz='';
				}
			}
		} else{
			for(var i = 0; i < tabs.length; i++){				
				tabs[i].clazz='';
			}
		}
		
		if(changeLocation){
			$location.url(tab.href);
		}
		
	};
	
	
	$scope.getActiveTabIndex = function(){
		for(var i = 0; i < tabs.length; i++){				
			if(tabs[i].clazz === 'active'){
				return i;
			}
		}
	}
	
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
	 * function to return the search form
	 */
	$scope.setForm = function(value){
		form = value;
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
	
	/**
	 * function to set the origine of a page
	 */
	$scope.getHomePage = function(){
		return homePage;
	};
	
	/**
	 * function to return the origine of a page
	 */
	$scope.setHomePage = function(value){
		homePage = value;
	};
	/**
	 * Test if home page equal value
	 */
	$scope.isHomePage= function(value){
		return homePage === value;
	};
	/**
	 * Start edition in details page
	 */
	$scope.startEditMode = function(){
		editMode = true;
	};
	/**
	 *  Stop edition in details page
	 */
	$scope.stopEditMode = function(){
		editMode = false;
	};
	/**
	 * Edition mode status
	 */
	$scope.isEditMode = function(){
		return editMode;
	};
}

TabMainCtrl.$inject = ['$scope', '$location'];
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
		clazz:'fa fa-expand'
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
	 * "private" function to keep only the first tab and an eventually an other tab
	 * (if active) to remenber the last selection
	 */
	var keepOnlyActiveTab = function(keepLastActiveTab, $event) { 
		var activeTabIndex = 0;
		var firstTab = tabs[0];
		var newtabs = [];
		newtabs[0] = firstTab;		
		//if we want to have the last active tab ...
		if (keepLastActiveTab) {
			activeTabIndex = getActiveTabIndex();
			if (activeTabIndex > 0) {
				newtabs[1] = tabs[activeTabIndex]; 
			}
		}
		//.. end
		tabs = newtabs;
		$scope.activeTab(tabs.length-1, true);		
		
		$event.preventDefault();
	}
	
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
		
		var doubledTab = [];

		for(var i = 0; i < tabs.length; i++) {
		    var valueIsInArray = false;

		    for(var j = 0; j < doubledTab.length; j++) {
		        if(doubledTab[j].label == tabs[i].label) {
		            valueIsInArray = true;
		        }
		    }

		    if(valueIsInArray) {
		        tabs.splice(i, 1); 
		    } else {
		        doubledTab.push(tabs[i]);
		    }
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
	 * remove current tab (if inactive) or re-init menu
	 */
	$scope.removeOrKeepOnlyActiveTab = function(index, $event, keepLastActiveTab) {		 
		if (index != 0) {
			var activeTabIndex = getActiveTabIndex();
			$scope.removeTab(index);
			if (index == activeTabIndex) {
				$scope.activeTab(0, true);	
			}
		}
		else {
			keepOnlyActiveTab(keepLastActiveTab, $event);
		} 
	    $event.preventDefault();
	    $event.stopPropagation(); 
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
	
	var getActiveTabIndex = function(){
		for(var i = 0; i < tabs.length; i++){				
			if(tabs[i].clazz === 'active'){
				return i;
			}
		}
	}
	
	$scope.toggleTabs = function(){
		$scope.hideTabs.hide = !$scope.hideTabs.hide;
		if($scope.hideTabs.hide){
			$scope.hideTabs.clazz='fa fa-compress';
		}else{
			$scope.hideTabs.clazz='fa fa-expand';
		}
	};
	
	$scope.setHideTabs =  function(){
		$scope.hideTabs.hide = true;
		$scope.hideTabs.clazz='fa fa-compress';
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
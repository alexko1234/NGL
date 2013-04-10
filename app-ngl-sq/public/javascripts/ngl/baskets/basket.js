"use strict";

angular.module('basketsLocalServices', []).
factory('basketsLocal', ['$http', function($http){ //service to manage baskets local
	var constructor = function($scope, iConfig){
		var baskets = {
			config : undefined,
			configMaster : undefined,
			basket : [],
			
			configDefault:{
				transform: function(element){
					return element;
				}
			},
			/**
			 * function to keep the basket when we switch views
			 */
			get: function(){
				return this.basket;
			},
			
			/**
			 * function to add an element to the basket
			 */
			add: function(element){
				this.basket.push(this.config.transform(element));
			},
			
			/**
			 * function to remove one element in the basket
			 */
			remove: function(index){
				this.basket.splice(index, 1);
			},
			/**
			 * function to change the configuration
			 */
			setConfig: function(newConfig){
				var settings = $.extend(true, {}, this.configDefault, newConfig);
			    this.config = angular.copy(settings);
			    this.configMaster = angular.copy(settings);    
			}
	
	};
	    var settings = $.extend(true, {}, baskets.configDefault, iConfig);
	    baskets.config = angular.copy(settings);
	    baskets.configMaster = angular.copy(settings);    
	    return baskets;
	}
	return constructor;
}]);
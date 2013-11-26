"use strict";

angular.module('commonsServices', []).
    	factory('messages', function(){
    		var constructor = function($scope, iConfig){
				var messages = {
						
						configDefault : {
							errorClass:'alert alert-error',
							successClass: 'alert alert-success',							
							errorKey:{save:'msg.error.save',remove:'msg.error.remove'},
							successKey:{save:'msg.success.save',remove:'msg.success.remove'}
						},
						config:undefined,
    					configMaster:undefined,
						
						clazz : undefined, 
						text : undefined, 
						showDetails : false, 
						isDetails : false, 
						details : [],
						opening : false,
						getConfig: function(){
		    				return this.config;		    				
		    			},
		    			setConfig: function(config){
		    				var settings = $.extend(true, {}, this.configDefault, config);
		    	    		this.config = angular.copy(settings);
		    	    		this.configMaster = angular.copy(settings);		    	    		
		    			},
						clear : function() {
							this.clazz = undefined;
							this.text = undefined; 
							this.showDetails = false; 
							this.isDetails = false;
							this.details = [];
							this.opening = false;
						},
						setDetails : function(details){
							this.isDetails = true;
							this.details = details;
						},
						setSuccess : function(type){
							this.clazz=this.config.successClass;
							this.text=this.transformKey(this.config.successKey[type]);
							this.open();
						},
						setError : function(type){
							this.clazz=this.config.errorClass;
							this.text=this.transformKey(this.config.errorKey[type]);
							
							this.open();
						},
						open : function(){
							this.opening = true;
						},
						close : function(){
							this.opening = false;
						},
						isOpen : function(){
							return this.opening;
						},
						transformKey : function(key, args){
							return Messages(key, args);
						}
				};
    			messages.setConfig(iConfig)
				return messages;
    		}
    		return constructor;
    	}).factory('lists', ['$http', function($http){
    		
    		var resolutions = [];
    		var validationCriterias = [];
    		
    		var refresh = {
    				resolutions : function(params){
    					$http.get(jsRoutes.controllers.lists.api.Lists.resolutions().url,{params:params}).success(function(data) {
    	    				resolutions=data;    				
    	    			});
    				},
    				validationCriterias: function(params){
    					$http.get(jsRoutes.controllers.lists.api.Lists.validationCriterias().url,{params:params}).success(function(data) {
    						validationCriterias=data;    				
    	    			});
    				},
    				all : function(params){
    					this.resolutions(params);
    					this.validationCriterias(params);
    				}
    		};
    		
    		function getValidations(){
    			return [{code:"TRUE", name:Messages("validate.value.TRUE")},
                 {code:"FALSE", name:Messages("validate.value.FALSE")},
                 {code:"UNSET", name:Messages("validate.value.UNSET")}];
    		};
    		
    		return {
    			refresh : refresh,
    			getResolutions : function(){return resolutions;},
    			getValidations : getValidations,
    			getValidationCriterias : function(){return validationCriterias;}
    		};
    		
    	}]).directive('messages', function() {
    		return {
    			restrict: 'A',
    			scope: {
    				  messages: '=messages'
    				},
    			template: '<div ng-class="messages.clazz" ng-show="messages.isOpen()">'+
    				'<button class="close" ng-click="messages.close()" type="button">×</button>'+
    				'<strong>{{messages.text}}</strong><button class="btn btn-link" ng-click="messages.showDetails=!messages.showDetails" ng-show="messages.isDetails">{{messages.transformKey("msg.details")}}</button>'+
    				'<div ng-show="messages.showDetails">'+
    				'    <ul>'+
    				'		<li ng-repeat="(key1, value1) in messages.details">{{key1}}'+
    				'		<ul>'+
    				'			<li ng-repeat="(key2, value2) in value1"> {{key2}} == {{value2}} </li>'+
    			    '		</ul>'+
    			    '		</li>'+
    			    '	</ul>'	+
    			    '</div>'+
    			    '</div>'
    			};
    	}).directive('codes', function() {
    		return {
    			restrict: 'A',
    			require: 'ngModel',
    			link: function(scope, element, attrs, ngModel) {
    				 if(!ngModel) return;
    				 var type = attrs.codes;
    				 ngModel.$render = function() {
    					 element.html(Codes(type+"."+ngModel.$viewValue));
    				 };
    			}    					
    			};
    				
    	});
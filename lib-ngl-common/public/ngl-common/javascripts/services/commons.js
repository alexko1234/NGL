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
    		
    		var results = {};    		
    		
    		var refresh = {
    				resolutions : function(params){
    					load(jsRoutes.controllers.lists.api.Lists.resolutions().url,params,'resolutions');
    				},
    				validationCriterias: function(params){
    					load(jsRoutes.controllers.lists.api.Lists.validationCriterias().url,params,'validationCriterias');    					
    				},
    				projects : function(params){
    					load(jsRoutes.controllers.lists.api.Lists.projects().url,params,'projects');    					
    				},
    				samples : function(params){
    					load(jsRoutes.controllers.lists.api.Lists.samples().url,params,'samples');    					
    				},
    				states : function(params){
    					load(jsRoutes.controllers.commons.api.States.list().url,params,'states');    				
    				},
    				types : function(params){
    					load(jsRoutes.controllers.commons.api.CommonInfoTypes.list().url,params,'types');    				
    				},
    				all : function(params){
    					this.resolutions(params);
    					this.validationCriterias(params);
    					this.projects(params);
    					this.samples(params);
    					this.states(params);
    					this.types(params);
    				}
    		};
    		
    		function getValuations(){
    			return [{code:"TRUE", name:Messages("valuation.value.TRUE")},
                 {code:"FALSE", name:Messages("valuation.value.FALSE")},
                 {code:"UNSET", name:Messages("valuation.value.UNSET")}];
    		};
    		
    		function load(url, params, values){
    			if(angular.isUndefined(params)){
    				params = {};
    			}
    			params.list = true;
    			$http.get(url,{params:params,values:values}).success(function(data, status, headers, config) {
    				results[config.values]=data;    				
    			});
    		}
    		
    		return {
    			refresh : refresh,
    			get : function(values){return results[values];},
    			getResolutions : function(){return results['resolutions'];},
    			getValidationCriterias : function(){return results['validationCriterias'];},
    			getProjects : function(){return results['projects'];},
    			getSamples : function(){return results['samples'];},
    			getStates : function(){return results['states'];},
    			getTypes : function(){return results['types'];},
    			getValuations : getValuations    			
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
    					 if(ngModel.$viewValue){
    						 element.html(Codes(type+"."+ngModel.$viewValue));
    					 }
    				 };
    			}    					
    			};
    				
    	});
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
    					load(jsRoutes.controllers.commons.api.Resolutions.list().url,params,'resolutions');
    				},
    				instruments : function(params){
    					load(jsRoutes.controllers.instruments.api.Instruments.list().url,params,'instruments');
    				},
    				instrumentUsedTypes : function(params){
    					load(jsRoutes.controllers.instruments.api.InstrumentUsedTypes.list().url,params,'instrumentUsedTypes');
    				},
    				containerSupportCategories : function(params){
    					load(jsRoutes.controllers.containers.api.ContainerSupportCategories.list().url,params,'containerSupportCategories');
    				},
    				valuationCriterias: function(params){
    					load(jsRoutes.controllers.lists.api.Lists.valuationCriterias().url,params,'valuationCriterias');    					
    				},
    				projects : function(params){
    					load(jsRoutes.controllers.projects.api.Projects.list().url,params,'projects');    					
    				},
    				samples : function(params){
    					load(jsRoutes.controllers.samples.api.Samples.list().url,params,'samples');    					
    				},
    				states : function(params){
    					load(jsRoutes.controllers.commons.api.States.list().url,params,'states');    				
    				},
    				protocols : function(params){
    					load(jsRoutes.controllers.experiments.api.Protocols.list().url,params,'protocols');    				
    				},
    				types : function(params, multi){
    					var name = "types";
    					if(multi!=undefined){
    						name = params.objectTypeCode+'Types';
    					}
    					load(jsRoutes.controllers.commons.api.CommonInfoTypes.list().url,params,name);    				
    				},
    				containerCategories : function(params){
    					load(jsRoutes.controllers.containers.api.ContainerCategories.list().url,params,'containerCategories');
    				},
    				experimentCategories : function(params){
    					load(jsRoutes.controllers.experiments.api.ExperimentCategories.list().url,params,'experimentCategories');
    				},
    				experimentTypes : function(params){
    					load(jsRoutes.controllers.experiments.api.ExperimentTypes.list().url,params,'experimentTypes');
    				},
    				runs : function(params){
    					load(jsRoutes.controllers.runs.api.Runs.list().url,params,'runs');    				
    				},
    				all : function(params){
    					this.resolutions(params);
    					this.containerCategories(params);
    					this.valuationCriterias(params);
    					this.projects(params);
    					this.samples(params);
    					this.states(params);
    					this.types(params);
    					this.runs(params);
    					this.protocols(params);
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
    			clear : function(values){results[values] = undefined;},
    			getResolutions : function(){return results['resolutions'];},
    			getValuationCriterias : function(){return results['valuationCriterias'];},
    			getProjects : function(){return results['projects'];},
    			getContainerSupportCategories : function(){return results['containerSupportCategories'];},
    			getSamples : function(){return results['samples'];},
    			getContainerCategories : function(){return results['containerCategories'];},
    			getExperimentCategories : function(){return results['experimentCategories'];},
    			getExperimentTypes : function(){return results['experimentTypes'];},
    			getStates : function(){return results['states'];},
    			getTypes : function(params){
	    						if(params != undefined){
	    							return results[params+'Types'];
	    						}else{
	    							return results['types'];
	    						}
    					   },
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
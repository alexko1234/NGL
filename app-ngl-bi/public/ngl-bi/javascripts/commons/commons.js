"use strict";

angular.module('biCommonsServices', []).
    	factory('treatments', function(){
    		var _treatments = [];
    		var _treatment = {};
    		
    		
    		/**
    		 * Set one element of list active
    		 */
    		function activeTreatment(value){
    			if(angular.isDefined(value)){
    				_treatment = value;
    				value.clazz='active';
    				for(var i = 0; i < _treatments.length; i++){
    					if(_treatments[i].code != _treatment.code){
    						_treatments[i].clazz='';
    					}
    				}
    			} 
    		};
    		
    		function init(treatments, url, excludes){
    			_treatments = [];
    			_treatment = {};
    			for (var key in treatments) {
					var treatment = treatments[key];
					if(angular.isUndefined(excludes) || angular.isUndefined(excludes[treatment.code])){
						_treatments.push({code:treatment.code, url:url(treatment.typeCode).url});
					}
				}
				this.activeTreatment(_treatments[0]);
    		};
    		
    		function getTreatment(){
    			return _treatment;
    		};
    		
    		function getTreatments(){
    			return _treatments;
    		};
    		
    		return {
    			init : init,
    			activeTreatment : activeTreatment,
    			getTreatment : getTreatment,
    			getTreatments : getTreatments
    		};
    	}).directive('treatments', function() {
    		return {
    			restrict: 'A',
    			scope: {
    				treatments: '=treatments'
    				},
    			template: '<ul class="nav nav-tabs">'+
    				      '<li ng-repeat="treament in treatments.getTreatments()" ng-class="treament.clazz">'+
    					  '<a href="#" ng-click="treatments.activeTreatment(treament)" >{{treament.code}}</a></li>'+		   
    					  '</ul>'+
    					  '<div class="tab-content">'+
    					  '<div class="tab-pane active" ng-include="treatments.getTreatment().url"/>'
    			};
    	});
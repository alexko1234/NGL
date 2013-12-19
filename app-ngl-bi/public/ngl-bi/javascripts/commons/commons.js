"use strict";

angular.module('biCommonsServices', []).
    	factory('treatments',['$q','$http','$filter', function($q,$http,$filter){
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
    			var queries = [];
				
    			for (var key in treatments) {
					var treatment = treatments[key];	
					if(angular.isUndefined(excludes) || angular.isUndefined(excludes[treatment.code])){
						queries.push($http.get(jsRoutes.controllers.treatmenttypes.api.TreatmentTypes.get(treatment.typeCode).url, 
								{key:key})	
						);
					}
					//_treatments.push({code:config.treatment.code, url:url(config.treatment.typeCode).url, order:data.displayOrder});
					//this.activeTreatment(_treatments[0]);				
    			}				
    			$q.all(queries).then(function(results){
					for(var i = 0; i  < results.length; i++){
						var result = results[i];
						_treatments.push({code:result.config.key, name:Messages("treatments."+result.config.key), url:url(result.data.code).url, order:result.data.displayOrder});
					}
					_treatments = $filter("orderBy")(_treatments,"order");
					activeTreatment(_treatments[0]);		
				});
    		};
    		
    		function getTreatment(){
    			return _treatment;
    		};
    		
    		function getTreatmentName(){
    			return _treatment.name.split(".")[1];
    		};
    		
    		function getTreatments(){
    			return _treatments;
    		};
    		
    		return {
    			init : init,
    			activeTreatment : activeTreatment,
    			getTreatment : getTreatment,
    			getTreatments : getTreatments,
    			getTreatmentName : getTreatmentName
    		};
    	}]).directive('treatments', function() {
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
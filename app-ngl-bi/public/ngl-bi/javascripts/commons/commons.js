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
    			}				
    			$q.all(queries).then(function(results){
					for(var i = 0; i  < results.length; i++){
						var result = results[i];
						
						_treatments.push({code:result.config.key, name:Messages("treatments."+result.config.key), url:url(result.data.code).url, order:orderTabs(result)});
					}
					_treatments = $filter("orderBy")(_treatments,"order");
					activeTreatment(_treatments[0]);		
				});
    		};
    		
    		function orderTabs(result) {
    			//specific order for treatments versus clean files
				var orderTabs = 0;
				if (result.config.key == "readQualityClean") 
					{orderTabs=83;}
				else {
					if (result.config.key == "duplicatesClean") 
						{orderTabs=86;}
					else {orderTabs= result.data.displayOrder; }
				}
				return orderTabs;
    		}
    		
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
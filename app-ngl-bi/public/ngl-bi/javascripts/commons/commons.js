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
						_treatments.push({code:result.config.key, name:Messages("readsets.treatments."+result.config.key), url:url(result.data.code).url, order:displayOrder(result, key) });
					}
					_treatments = $filter("orderBy")(_treatments,"order");
					activeTreatment(_treatments[0]);		
				});
    		};
    		
    		function displayOrder(result, key) {
    			var position = "-1"; 
    			if (result.data.displayOrders.indexOf(",") != -1) {
	    			var names = [];
	    			names = result.data.names.split(",");
	    			var orders = [];
	    			orders = result.data.displayOrders.split(",");
	    			
	    			for (var i=0; i<names.length; i++) {
	    				if (names[i] == result.config.key) {
	    					position = orders[i];
	    					break;
	    				}
	    			}
    			}
    			else {
    				position = result.data.displayOrders;
    			}
    			return Number(position);
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
    	}).directive('ngBindSrc', ['$parse',function($parse){
    		return {
    			restrict: 'A',
    			link: function(scope, element, attr) {
    				var parsed = $parse(attr.ngBindSrc);
    				function getStringValue() { return (parsed(scope) || '').toString(); }
    				
    				scope.$watch(getStringValue, function ngBindHtmlWatchAction(value) {
    					element.attr("src", parsed(scope) || '');
    				    });
    			}
    		}
    	}]);


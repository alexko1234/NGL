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
    	}).directive('btSelect',  ['$parse', function($parse)  {
    			//0000111110000000000022220000000000000000000000333300000000000000444444444444444000000000555555555555555000000066666666666666600000000000000007777000000000000000000088888
      		  var BT_OPTIONS_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?\s+for\s+(?:([\$\w][\$\w\d]*))\s+in\s+(.*)$/;
      		// jshint maxlen: 100
    		    return {
    		    	restrict: 'E',
    		    	replace:false,
    		    	scope:true,
    		    	template: '<div ng-class="getClass()">'
		    	  		+'<button class="btn dropdown-toggle" data-toggle="dropdown">'
		    	  		+'<div class="filter-option pull-left">{{selectedItemLabel()}}</div>&nbsp;'
		    	  		+'<span class="caret"></span>'
		    	  		+'</button>'
		    	  		+'<ul class="dropdown-menu">'
		    	  		+'<li ng-repeat="item in items" ng-class="item.class" ng-click="selectItem(item, $event)">'
		    	  		+'<a tabindex="-1"  href="#">'
		    	  		+'<span class="text">{{itemLabel(item)}}</span>'
		    	  		+'<i class="glyphicon glyphicon-ok icon-ok check-mark"></i>'
		    	  		+'</a></li>'
		    	  		+'</ul>'
		    	  		+'</div>',
	    	  		require: ['?ngModel'],
	       		    link: function(scope, element, attr, ctrls) {
	       		  // if ngModel is not defined, we don't need to do anything
	      		      if (!ctrls[0]) return;

	      		      var ngModelCtrl = ctrls[0],
	      		          multiple = attr.multiple || false,
	      		          btOptions = attr.btOptions,
	      		          placeholder = attr.placeholder;

	      		      var optionsConfig = parseBtsOptions(btOptions);
	      		      
	      		      function parseBtsOptions(input){
	      		    	  var match = input.match(BT_OPTIONS_REGEXP);
		      		      if (!match) {
		      		        throw new Error(
		      		          "Expected typeahead specification in form of '_modelValue_ (as _label_)? for _item_ in _collection_'" +
		      		            " but got '" + input + "'.");
		      		      }
	
		      		    return {
		      		        itemName:match[3],
		      		        source:$parse(match[4]),
		      		        viewMapper:match[2] || match[1],
		      		        modelMapper:match[1]
		      		      };
		      		      
	      		      };
	      		    
	      		      var selectedLabels = [];
	      		      
	      		      scope.getClass = function(){
	      		    	return "btn-group bootstrap-select show-tick "+attr.class;  
	      		      };
	      		      
	      		      scope.selectedItemLabel = function(){
	      		    	  return selectedLabels.join();
	      		      };  
	      	        
	      		      scope.itemLabel = function(item){
	      		    	 return item[optionsConfig.viewMapper.replace(optionsConfig.itemName+'.','')];  
	      		      };
	      		      
	      		      scope.itemValue = function(item){
	      		    	 return item[optionsConfig.modelMapper.replace(optionsConfig.itemName+'.','')];  
	      		      };
	      		      
	      		      scope.selectItem = function(item, $event){
	      		    	  if(multiple){
	      		    			var selectedValues = ngModelCtrl.$viewValue || [];
	      		    		    var newSelectedValues = [];
	      		    			var itemValue = scope.itemValue(item);
	      		    			var find = false;
	      		    			for(var i = 0; i < selectedValues.length; i ++){
	      		    				if(selectedValues[i] !== itemValue){
	      		    					newSelectedValues.push(selectedValues[i]);
	      		    				}else{
	      		    					find = true;
	      		    				}
	      		    			}
	      		    			if(!find){
	      		    				newSelectedValues.push(itemValue);
	      		    			}
	      		    			selectedValues = newSelectedValues;
	      		    			
	      		    			ngModelCtrl.$setViewValue(selectedValues);
	      		    			ngModelCtrl.$render();
	      		    			$event.preventDefault();
	      		    			$event.stopPropagation();
	      		    	  	}else{
	      		    	  		ngModelCtrl.$setViewValue(scope.itemValue(item));
	      		    	  		ngModelCtrl.$render();
	      		    	  	}
	      		      };
	      		   ngModelCtrl.$render = render;
	      		    
	      		      // TODO(vojta): can't we optimize this ? astuce provenant du select de angular
	      		    scope.$watch(render);
	      	        
	      		    function render() {
	      		    	selectedLabels = [];
		      	    	scope.items = optionsConfig.source(scope) || [];
		      	    	var modelValues = ngModelCtrl.$modelValue || [];
		      	    	if(!angular.isArray(modelValues)){
		      	    		modelValues = [modelValues];
		      	    	}
		      	    	if(scope.items){
			      	    	for(var i = 0; i < scope.items.length; i++){
			      	    		var item = scope.items[i];
			      	    		item.class = "";
		      		    		for(var j = 0; j < modelValues.length; j++){
			      	    			var modelValue = modelValues[j];
			      	    			if(scope.itemValue(item) === modelValue){
				      	    			item.class = "selected";
				      		    		selectedLabels.push(scope.itemLabel(item));
				      	    		}
			      	    		}	      	    		
			      	    	}
		      	    	}
		      	    	if(modelValues.length === 0){
		      	    		selectedLabels.push(placeholder);
		      	    	}
		      	    		
	      	        };  
	      		  }
    		    };
    		   }]).directive('ngBindSrc', ['$parse',function($parse){
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


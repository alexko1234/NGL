angular.module('commonsServices').directive('btInput',  ['$parse', '$filter', function($parse, $filter)  {
			//0000111110000000000022220000000000000000000000333300000000000000444444444444444000000000555555555555555000000066666666666666600000000000000007777000000000000000000088888
    		var BT_OPTIONS_REGEXP = /^\s*([\s\S]+?)(?:\s+as\s+([\s\S]+?))?(?:\s+group\s+by\s+([\s\S]+?))?\s+for\s+(?:([\$\w][\$\w]*))\s+in\s+([\s\S]+?)$/;                        
    		 // 1: value expression (valueFn)
            // 2: label expression (displayFn)
            // 3: group by expression (groupByFn)
            // 4: disable when expression (disableWhenFn)
            // 5: array item variable name
            // 6: object item key variable name
            // 7: object item value variable name
            // 8: collection expression
            // 9: track by expression
  		    return {
  		    	restrict: 'A',
  		    	replace:false,
  		    	scope:true,
  		    	template:''
  		    			/*
  		    		    +'<div ng-switch on="isEdit()">'
  		    			+'<div ng-switch-when="false">'
  		    			+'<ul class="list-unstyled form-control-static">'
		    	  		+'<li ng-repeat-start="item in getItems()" ng-if="groupBy(item, $index)" ng-bind="itemGroupByLabel(item)" style="font-weight:bold"></li>'
		    	  		+'<li ng-repeat-end  ng-if="item.selected" ng-bind="itemLabel(item)"></li>'
			    	  	+'</ul>'
  		    			+'</div>'
  		    			+'<div class="dropdown" ng-switch-when="true">'  				        
  		    			*/
  		    			+'<div class="dropdown" >'  				        
		    			
  		    			+'<div class="input-group">'
  		    			
  		    			+'<div class="input-group-btn" ng-if="isTextarea()">'
  		    			//textarea mode
  		    			+'<button tabindex="-1" data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle" type="button" ng-disabled="isDisabled()" ng-click="open()">'
  		    			+'<i class="fa fa-list-ul"></i>'
  		    			+'</button>'
  		    			+'<ul class="dropdown-menu dropdown-menu-left"  role="menu">'
  				        +'<li>'
  				        +'<textarea ng-class="inputClass" ng-model="textareaValue" ng-change="setTextareaValue(textareaValue)" rows="5"></textarea>'  				      
  				        +'</li>'  				        
		    	  		+'</ul>'		    	  		
		    	  		+'</div>'
  		    			
		    	  		//select mode
  		    			+'<input type="text" style="background:white" ng-class="inputClass" ng-model="ngModel" placeholder="{{placeholder}}" title="{{placeholder}}"/>'  		    			
  		    			/*
  		    			+'<div class="input-group-btn">'
  		    			+'<button tabindex="-1" data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle" type="button" ng-disabled="isDisabled()" ng-click="open()">'
  		    			+'<span class="caret"></span>'
  		    			+'</button>'
  		    			+'<ul class="dropdown-menu dropdown-menu-right"  role="menu">'
  				        +'<li ng-if="filter"><input ng-class="inputClass" type="text" ng-click="inputClick($event)" ng-model="filterValue" ng-change="setFilterValue(filterValue)" placeholder="{{getMessage(\'bt-select.here\')}}"/></li>'
  				        // Liste des items déja cochés
		    	  		+'<li ng-repeat-start="item in getSelectedItems()" ng-if="groupBy(item, $index) && acceptsMultiple()"></li>'
  				        +'<li class="dropdown-header" ng-if="groupBy(item, $index)" ng-bind="itemGroupByLabel(item)"></li>'
  				        +'<li ng-repeat-end ng-if="item.selected" ng-click="selectItem(item, $event)">'
  				        +'<a href="#">'
		    	  		+'<i class="fa fa-check pull-right" ng-show="item.selected"></i>'
		    	  		+'<span class="text" ng-bind="itemLabel(item)" style="margin-right:30px;"></span>'		    	  		
		    	  		+'</a></li>'
		    	  		+'<li ng-show="getSelectedItems().length > 0" class="divider pull-left" style="width: 100%;"></li>'		    	  		
  				        // Liste des items
  				        +'<li ng-repeat-start="item in getItems()" ng-if="groupBy(item, $index)" class="divider"></li>'
  				        +'<li class="dropdown-header" ng-if="groupBy(item, $index)" ng-bind="itemGroupByLabel(item)"></li>'
		    	  		+'<li ng-repeat-end ng-click="selectItem(item, $event)">'
			    	  	+'<a href="#">'
		    	  		+'<i class="fa fa-check pull-right" ng-show="item.selected"></i>'
		    	  		+'<span class="text" ng-bind="itemLabel(item)" style="margin-right:30px;"></span>'		    	  		
		    	  		+'</a></li>'
		    	  		+'</ul>'		    	  		
		    	  		+'</div>'
		    	  		+'</div>'
		    	  		*/
		    	  		
		    	  		+'</div>'
		    	  		+'</div>'
		    	  		,
	    	  		require: ['?ngModel'],
	       		    link: function(scope, element, attr, ctrls) {
	       		  // if ngModel is not defined, we don't need to do anything
	      		      if (!ctrls[0]) return;
	      		      scope.inputClass = element.attr("class");
	      		      scope.placeholder = attr.placeholder;
	      		      scope.ngModel = attr.ngModel;
	      		      
	      		      element.attr("class",''); //remove custom class
	      		     
	      		      var ngModelCtrl = ctrls[0],
	      		          textarea = attr.textarea || false
	      		          ;
	      		          //btOptions = attr.btOptions,
	      		          //editMode = (attr.ngEdit)?$parse(attr.ngEdit):undefined,
	      		          //filter = attr.filter || false;

	      		      //var optionsConfig = parseBtsOptions(btOptions);
	      		      //var items = [];
	      		      //var groupByLabels = {};
	      		      //var filterValue;
	      		      var textareaValue;
	      		      var ngFocus = attr.ngFocus;
	      		      var ngModelValue = attr.ngModel;
	      		      /*
	      		      function parseBtsOptions(input){
	      		    	  var match = input.match(BT_OPTIONS_REGEXP);
		      		      if (!match) {
		      		        throw new Error(
		      		          "Expected typeahead specification in form of '_modelValue_ (as _label_)? for _item_ in _collection_'" +
		      		            " but got '" + input + "'.");
		      		      }
	
		      		    return {
		      		        itemName:match[4],
		      		        sourceKey:match[5],
		      		        source:$parse(match[5]),
		      		        viewMapper:match[2] || match[1],
		      		        modelMapper:match[1],
		      		        groupBy:match[3],
		      		        groupByGetter:match[3]?$parse(match[3].replace(match[4]+'.','')):undefined
		      		      };
		      		      
	      		      };
	      		      */
	      		      /*
		      		    var displayFn = $parse(match[2] || match[1]),
		                valueName = match[4] || match[6],
		                keyName = match[5],
		                groupByFn = $parse(match[3] || ''),
		                valueFn = $parse(match[2] ? match[1] : valueName),
		                valuesFn = $parse(match[7]),
		                track = match[8],
		                trackFn = track ? $parse(match[8]) : null,
	                */
	      		     /*
	      		     scope.filter = filter; 
	      		     scope.setFilterValue = function(value){
	      		    	filterValue = value
	      		     };
	      		     */
	      		     scope.isTextarea = function(){
	      		    	return (textarea); 
	      		     };
	      		     scope.textareaValue = textareaValue; 
	      		     scope.setTextareaValue = function(values, $event){
	      		    	if(multiple){
      		    			var selectedValues = values.split(/\s*[,;\n]\s*/);
      		    			ngModelCtrl.$setViewValue(selectedValues);
      		    			ngModelCtrl.$render();      		    			
      		    	  	}	      		    	 	      		    	 
	      		     }; 
	      		     
	      		     scope.open = function(){
	      		    	 if(ngFocus){
	      		    		$parse(ngFocus)(scope);  
	      		    	 }
	      		     };
	      		     
	      		     scope.isDisabled = function(){
	      		    	return (attr.ngDisabled)?scope.$parent.$eval(attr.ngDisabled):false;
	      		     };
	      		     /*
	      		     scope.isEdit = function(){
	      		    	 return (editMode)?editMode(scope):true;
	      		     };
	      		    
	      		     scope.acceptsMultiple = function(){
	      		    	 return attr.multiple;
	      		     }
	      		     
	      		     scope.getSelectedItems = function(){
	      		    	 var itemsList = items;
	      		    	 var selectedItems = [];
	      		    	 itemsList.forEach(function(s){
	      		    		 if (s.selected){
	      		    			 selectedItems.push(s);
	      		    		 }
	      		    	 });
	      		    	return selectedItems;
	      		     };
	      		     
	      		     scope.getItems = function(){
	      		    	 if(scope.isEdit() && scope.filter){
	      		    		var filter = {};
	      		    		//Angularjs 1.3.11 change, we don't want the filter to match an undefined
	      		    		//filterValue, so we don't assign it
	      		    		if(filterValue){
	      		    			var getter = $parse(optionsConfig.viewMapper.replace(optionsConfig.itemName+'.',''));
	      		    			getter.assign(filter, filterValue);
	      		    		}
	      		    		//Then here the filter will be empty if the filterValue is undefined
	      		    		return $filter('limitTo')($filter('filter')(items, filter), 20);
	      		    	 }else{
	      		    		return items;
	      		    	 }
	      		     };
	      		    
	      		    scope.groupBy = function(item, index){
	      		    	if(index === 0){ //when several call
	      		    		groupByLabels = {};
	      		    	}
	      		    	
	      		    	if(optionsConfig.groupByGetter && scope.isEdit()){
	      		    		if(index === 0 || (index > 0 && optionsConfig.groupByGetter(items[index-1]) !== optionsConfig.groupByGetter(item))){
	      		    			return true;
	      		    		}	      		    		
	      		    	}else if(optionsConfig.groupByGetter && !scope.isEdit()){
	      		    		if(item.selected && !groupByLabels[optionsConfig.groupByGetter(item)]){
	      		    			groupByLabels[optionsConfig.groupByGetter(item)] = true;
	      		    			return true;
	      		    		}	      		    		
	      		    	}
	      		    	return false;	      		    	
	      		    }; 
	      		   */
	      		  scope.getMessage = function(value){
	      			  return Messages(value);
	      		  };
	      		  /*  
      		      scope.itemGroupByLabel = function(item){
      		    	 return optionsConfig.groupByGetter(item);
      		      }
      		      
      		      scope.itemLabel = function(item){	      		    	
      		    	// return item[optionsConfig.viewMapper.replace(optionsConfig.itemName+'.','')];  
      		    	return $parse(optionsConfig.viewMapper.replace(optionsConfig.itemName+'.',''))(item);
      		      };
      		      
      		      scope.itemValue = function(item){
      		    	 //return item[optionsConfig.modelMapper.replace(optionsConfig.itemName+'.','')];
      		    	  return $parse(optionsConfig.modelMapper.replace(optionsConfig.itemName+'.',''))(item);
      		      };
      		      */
      		      scope.$watch(ngModelValue, function(newValue, oldValue){
      		    	     if(newValue!= undefined && newValue !== null && oldValue !== newValue){		    		
      		    	    	 render();
      		    	     }
      		      }, true);
      		      /*
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
      		    	  		if(scope.itemValue(item) !== ngModelCtrl.$viewValue){
      		    	  			ngModelCtrl.$setViewValue(scope.itemValue(item));
      		    	  		}else{
      		    	  			ngModelCtrl.$setViewValue(null);
      		    	  		}
      		    	  		ngModelCtrl.$render();
      		    	  		
      		    	  	}
      		      };
      		      
      		      scope.inputClick = function($event){
      		    	$event.preventDefault();
	    			$event.stopPropagation();
      		      };
	      		     
      		      
      		      scope.$watchCollection(optionsConfig.sourceKey, function(newValue, oldValue){
      		    	  if(newValue && angular.isArray(newValue)){
      		    		items = angular.copy(newValue);      		    		
      		    		render();      		    		
      		    	  }
      		      });
	      		    */   
	      		   ngModelCtrl.$render = render;
	      		   
	      		    function render() {
	      		    	var selectedLabels = [];
	      		    		      		    	
		      	    	var modelValues = ngModelCtrl.$modelValue || [];
		      	    	if(!angular.isArray(modelValues)){
		      	    		modelValues = [modelValues];
		      	    	}		      	    	
		      	    	if(textarea){
		      	    		selectedLabels = modelValues;
		      	    	}
		      	    	scope.selectedLabels = selectedLabels;
	      	        };	      	        		      		
	      		  }	      		  
  		    };
}]);
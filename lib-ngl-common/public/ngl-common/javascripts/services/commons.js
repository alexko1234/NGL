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
    		
    		var results = {
    				valuations : [{code:"TRUE", name:Messages("valuation.value.TRUE")},
    				                 {code:"FALSE", name:Messages("valuation.value.FALSE")},
    				                 {code:"UNSET", name:Messages("valuation.value.UNSET")}]
    		};    		
    		
    		var refresh = {
    				resolutions : function(params){
    					load(jsRoutes.controllers.commons.api.Resolutions.list().url,params,'resolutions');
    				},
    				instruments : function(params){
    					load(jsRoutes.controllers.instruments.api.Instruments.list().url,params,'instruments');
    				},
    				instrumentCategories : function(params){
    					load(jsRoutes.controllers.instruments.api.InstrumentCategories.list().url,params,'instrumentCategories');
    				},
    				instrumentUsedTypes : function(params){
    					load(jsRoutes.controllers.instruments.api.InstrumentUsedTypes.list().url,params,'instrumentUsedTypes');
    				},
    				containerSupportCategories : function(params){
    					load(jsRoutes.controllers.containers.api.ContainerSupportCategories.list().url,params,'containerSupportCategories');
    				},
    				processCategories : function(params){
    					load(jsRoutes.controllers.processes.api.ProcessCategories.list().url,params,'processCategories');
    				},
    				processTypes : function(params){
    					load(jsRoutes.controllers.commons.api.ProcessTypes.list().url,params,'processTypes');
    				},
       				projectCategories : function(params){
    					load(jsRoutes.controllers.projects.api.ProjectCategories.list().url,params,'projectCategories');
    				},
    				projectTypes : function(params){
    					load(jsRoutes.controllers.projects.api.ProjectTypes.list().url,params,'projectTypes');
    				},
    				projectUmbrellas : function(params){
    					load(jsRoutes.controllers.projectUmbrellas.api.ProjectUmbrellas.list().url,params,'projectUmbrellas');
    				},
    				valuationCriterias: function(params){
    					load(jsRoutes.controllers.valuation.api.ValuationCriterias.list().url,params,'valuationCriterias');    					
    				},
    				supports : function(params){
    					load(jsRoutes.controllers.supports.api.Supports.list().url,params,'supports'); 
    				},
    				projects : function(params){
    					load(jsRoutes.controllers.projects.api.Projects.list().url,params,'projects');    					
    				},
    				samples : function(params){
    					load(jsRoutes.controllers.samples.api.Samples.list().url,params,'samples');    					
    				},
    				users : function(params){
    					load(jsRoutes.controllers.commons.api.Users.list().url,params,'users');    					
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
    				reportConfigs : function(params){
    					load(jsRoutes.controllers.reporting.api.ReportingConfigurations.list().url,params,'reportConfigs');    				
    				},
    				all : function(params){
    					this.resolutions(params);
    					this.containerCategories(params);
    					this.processCategories(params);
    					this.valuationCriterias(params);
    					this.projects(params);
    					this.samples(params);
    					this.states(params);
    					this.types(params);
    					this.users(params);
    					this.runs(params);
    					this.protocols(params);
    				}
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
    			getProcessCategories : function(){return results['processCategories'];},
    			getProcessTypes : function(){return results['processTypes'];},
    			getProjectCategories : function(){return results['projectCategories'];},
    			getProjectTypes : function(){return results['projectTypes'];},
    			getProjectUmbrellas : function(){return results['projectUmbrellas'];},
    			getSamples : function(){return results['samples'];},
    			getUsers : function(){return results['users'];},
    			getSupports : function(){return results['supports'];},
    			getContainerCategories : function(){return results['containerCategories'];},
    			getExperimentCategories : function(){return results['experimentCategories'];},
    			getExperimentTypes : function(){return results['experimentTypes'];},
    			getStates : function(){return results['states'];},
    			getRuns : function(){return results['runs'];},
    			getInstrumentCategories : function(){return results['instrumentCategories'];},
    			getTypes : function(params){
	    						if(params != undefined){
	    							return results[params+'Types'];
	    						}else{
	    							return results['types'];
	    						}
    					   },
    			getInstruments : function(){return results['instruments'];},		   
    			getValuations : function(){return results['valuations'];}
    			
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
    	//If the select or multiple choices contain 1 element, this directive select it automaticaly
    	//USE: As attribut, auto-select="theListOfChoice"
    	}).directive('autoSelect', function() {
    		return {
    			restrict: 'A',
    			require: 'ngModel',
    			link: function(scope, element, attrs, ngModel) {
					 scope.$watch(attrs.autoSelect, function(newVal) {
						if(newVal && newVal.length == 1){
							ngModel.$setViewValue(newVal[0].code);
						}
					 });
    			}
    		};
    	}).directive('btSelect',  ['$parse', '$document', '$window', function($parse,$document, $window)  {
			//0000111110000000000022220000000000000000000000333300000000000000444444444444444000000000555555555555555000000066666666666666600000000000000007777000000000000000000088888
    		var BT_OPTIONS_REGEXP = /^\s*([\s\S]+?)(?:\s+as\s+([\s\S]+?))?(?:\s+group\s+by\s+([\s\S]+?))?\s+for\s+(?:([\$\w][\$\w]*))\s+in\s+([\s\S]+?)$/;                        
    		//var BT_OPTIONS_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?\s+for\s+(?:([\$\w][\$\w\d]*))\s+in\s+(.*)$/;
    		// jshint maxlen: 100
  		    return {
  		    	restrict: 'A',
  		    	replace:false,
  		    	scope:true,
  		    	template:'<div ng-switch on="isEdit()">'
  		    			+'<div ng-switch-when="false">'
  		    			+'<ul class="list-unstyled">'
		    	  		+'<li ng-repeat-start="item in getItems()" ng-if="groupBy(item, $index)" ng-bind="itemGroupByLabel(item)" style="font-weight:bold"></li>'
		    	  		+'<li ng-repeat-end  ng-if="item.selected" ng-bind="itemLabel(item)" style="padding-left: 15px;"></li>'
			    	  	+'</ul>'
  		    			+'</div>'
  		    			+'<div class="dropdown" ng-switch-when="true">'
  				        +'<input type="text" ng-class="inputClass" data-toggle="dropdown" role="button" value="{{selectedItemLabel()}}" style="cursor:context-menu;"/>'
  				        +'<ul class="dropdown-menu {{btDropdownClass}}"  role="menu">'
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
		    	  		,
	    	  		require: ['?ngModel'],
	       		    link: function(scope, element, attr, ctrls) {
	       		  // if ngModel is not defined, we don't need to do anything
	      		      if (!ctrls[0]) return;
	      		      scope.inputClass = element.attr("class");
	      		      element.attr("class",''); //remove custom class
	      		      
	      		      if(attr.btDropdownClass){
	      		    	  scope.btDropdownClass = attr.btDropdownClass
	      		      }
	      		      
	      		      var ngModelCtrl = ctrls[0],
	      		          multiple = attr.multiple || false,
	      		          btOptions = attr.btOptions,
	      		          editMode = (attr.ngEdit)?$parse(attr.ngEdit):undefined,
	      		          placeholder = attr.placeholder;

	      		      var optionsConfig = parseBtsOptions(btOptions);
	      		      var items = [];
	      		      var selectedLabels = [];
	      		      var groupByLabels = [];
	      		      
	      		      function parseBtsOptions(input){
	      		    	  var match = input.match(BT_OPTIONS_REGEXP);
		      		      if (!match) {
		      		        throw new Error(
		      		          "Expected typeahead specification in form of '_modelValue_ (as _label_)? for _item_ in _collection_'" +
		      		            " but got '" + input + "'.");
		      		      }
	
		      		    return {
		      		        itemName:match[4],
		      		        source:$parse(match[5]),
		      		        viewMapper:match[2] || match[1],
		      		        modelMapper:match[1],
		      		        groupBy:match[3],
		      		        groupByGetter:match[3]?$parse(match[3].replace(match[4]+'.','')):undefined
		      		      };
		      		      
	      		      };
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
	      		   
	      		     scope.isEdit = function(){
	      		    	 return (editMode)?editMode(scope):true;
	      		     } 
	      		      
	      		     scope.getItems = function(){
	      		    	 return items;
	      		     };
	      		    
	      		    scope.groupBy = function(item, index){
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
	      		    
	      		      scope.selectedItemLabel = function(){
	      		    	  return selectedLabels.join();
	      		      };  
	      	        
	      		      scope.itemGroupByLabel = function(item){
	      		    	 return optionsConfig.groupByGetter(item);
	      		      }
	      		      
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
	      		    	  		if(scope.itemValue(item) !== ngModelCtrl.$viewValue){
	      		    	  			ngModelCtrl.$setViewValue(scope.itemValue(item));
	      		    	  		}else{
	      		    	  			ngModelCtrl.$setViewValue(null);
	      		    	  		}
	      		    	  		ngModelCtrl.$render();
	      		    	  		
	      		    	  	}
	      		      };
	      		   ngModelCtrl.$render = render;
	      		    
	      		      // TODO(vojta): can't we optimize this ? astuce provenant du select de angular
	      		    scope.$watch(render);
	      		  
	      		    
	      		   
	      		    function render() {
	      		    	
	      		    	selectedLabels = [];
	      		    	
	      		    	if(items.length == 0){ //load only once the possible values
	      		    		var v = optionsConfig.source(scope) || []; //copy avoid conflict with other same values
	      		    		items = angular.copy(v);
	      		    	}
	      		    	
		      	    	var modelValues = ngModelCtrl.$modelValue || [];
		      	    	if(!angular.isArray(modelValues)){
		      	    		modelValues = [modelValues];
		      	    	}
		      	    	if(items){
			      	    	for(var i = 0; i < items.length; i++){
			      	    		var item = items[i];
			      	    		item.selected = false;
		      		    		for(var j = 0; j < modelValues.length; j++){
			      	    			var modelValue = modelValues[j];
			      	    			if(scope.itemValue(item) === modelValue){
			      	    				if(optionsConfig.groupByGetter){
			      	    					groupByLabels[optionsConfig.groupByGetter(item)]=false;
			      	    				}
				      	    			item.selected = true;
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
    	}]).filter('filters',['$filter',function ($filter) {
    		return function (array, expressions) {
    			if (!angular.isArray(expressions)) expressions = [expressions];
    			var filtered = [];
    			for(var i = 0; i < expressions.length; i++){
    				var result = $filter('filter')(array, expressions[i]);
    				if(result && result.length > 0)filtered = filtered.concat(result);    				
    			}
    			if(filtered.length > 0)return filtered;
    			return undefined;
    		}
    	}]).filter('unique', function() {
    	    return function(input, key) {
    	        var unique = {};
    	        var uniqueList = [];
    	        for(var i = 0; i < input.length; i++){
    	            if(typeof unique[input[i][key]] == "undefined"){
    	                unique[input[i][key]] = "";
    	                uniqueList.push(input[i]);
    	            }
    	        }
    	        return uniqueList;
    	    };
    	}).filter('sum', ['$parse',function($parse) {
    	    return function(array, key) {
    	    	if(!array)return undefined;
    	    	if(!angular.isArray(array) && (angular.isObject(array) || angular.isNumber(array))) array = [array];
    	    	else if(!angular.isArray(array)) throw "input is not an array, object or a number !";
    	    	
    	    	if(key && !angular.isString(key))throw "key is not valid, only string is authorized";
    	    	
    	    	var params = {sum:0, key:key};
    	    	angular.forEach(array, function(value, index){
    	    		if(params.key && angular.isObject(value))params.sum = params.sum + value[params.key];
    	    		else if(!params.key && angular.isObject(value))throw "missing key !";
    	    		else params.sum = params.sum + value;
    	    	}, params);
    	    	return params.sum;
    	    };
    	}]).filter('codes', function(){
    		return function(input, key){
    			if(input)return Codes(key+"."+input);
    			return undefined;
    		}
    	}).filter('messages', function(){
    		return function(input){
    			return Messages(input);    			
    		}
    	});
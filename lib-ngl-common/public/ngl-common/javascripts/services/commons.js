"use strict";

angular.module('commonsServices', []).
    	factory('messages', function(){
    		var constructor = function($scope, iConfig){
				var messages = {
						
						configDefault : {
							errorClass:'alert alert-danger',
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
    		var inProgress = {};
    		var results = {
    				valuations : [{code:"TRUE", name:Messages("valuation.value.TRUE")},
    				                 {code:"FALSE", name:Messages("valuation.value.FALSE")},
    				                 {code:"UNSET", name:Messages("valuation.value.UNSET")}],
    				booleans : [{code:"true", name:Messages("boolean.value.TRUE")}, {code:"false", name:Messages("boolean.value.FALSE")}]
    			};    		
    		
    		var refresh = {
    				resolutions : function(params, key){
    					load(jsRoutes.controllers.resolutions.api.Resolutions.list().url,params,(key)?key:'resolutions');
    				},
    				instruments : function(params, key){
    					load(jsRoutes.controllers.instruments.api.Instruments.list().url,params,(key)?key:'instruments');
    				},
    				instrumentCategories : function(params, key){
    					load(jsRoutes.controllers.instruments.api.InstrumentCategories.list().url,params,(key)?key:'instrumentCategories');
    				},
    				instrumentUsedTypes : function(params, key){
    					load(jsRoutes.controllers.instruments.api.InstrumentUsedTypes.list().url,params,(key)?key:'instrumentUsedTypes');
    				},
    				containerSupportCategories : function(params, key){
    					load(jsRoutes.controllers.containers.api.ContainerSupportCategories.list().url,params,(key)?key:'containerSupportCategories');
    				},
    				processCategories : function(params, key){
    					load(jsRoutes.controllers.processes.api.ProcessCategories.list().url,params,(key)?key:'processCategories');
    				},
    				processTypes : function(params, key){
    					load(jsRoutes.controllers.processes.api.ProcessTypes.list().url,params,(key)?key:'processTypes');
    				},
    				kitCatalogs : function(params, key){
    					load(jsRoutes.controllers.reagents.api.KitCatalogs.list().url,params,(key)?key:'kitCatalogs');
    				},
       				projectCategories : function(params, key){
    					load(jsRoutes.controllers.projects.api.ProjectCategories.list().url,params,(key)?key:'projectCategories');
    				},
    				projectTypes : function(params, key){
    					load(jsRoutes.controllers.projects.api.ProjectTypes.list().url,params,(key)?key:'projectTypes');
    				},
    				umbrellaProjects : function(params, key){
    					load(jsRoutes.controllers.umbrellaprojects.api.UmbrellaProjects.list().url,params,(key)?key:'umbrellaProjects');
    				},
    				valuationCriterias: function(params, key){
    					load(jsRoutes.controllers.valuation.api.ValuationCriterias.list().url,params,(key)?key:'valuationCriterias');    					
    				},
    				containerSupports : function(params, key){
    					load(jsRoutes.controllers.containers.api.ContainerSupports.list().url,params,(key)?key:'containerSupports'); 
    				},
    				projects : function(params, key){
    					load(jsRoutes.controllers.projects.api.Projects.list().url,params,(key)?key:'projects');    					
    				},
    				samples : function(params, key){
    					load(jsRoutes.controllers.samples.api.Samples.list().url,params,(key)?key:'samples');    					
    				},
    				users : function(params, key){
    					load(jsRoutes.controllers.commons.api.Users.list().url,params,(key)?key:'users');    					
    				},
    				experiments : function(params, key){
    					load(jsRoutes.controllers.experiments.api.Experiments.list().url,params,(key)?key:'experiments');    					
    				},
    				states : function(params, key){
    					load(jsRoutes.controllers.commons.api.States.list().url,params,(key)?key:'states');    				
    				},
    				protocols : function(params, key){
    					load(jsRoutes.controllers.protocols.api.Protocols.list().url,params,(key)?key:'protocols');    				
    				},
    				types : function(params, multi, key){
    					var name = "types";
    					if(multi!=undefined){
    						name = params.objectTypeCode+'Types';
    					}
    					load(jsRoutes.controllers.commons.api.CommonInfoTypes.list().url,params,(key)?key:name);    				
    				},
    				containerCategories : function(params, key){
    					load(jsRoutes.controllers.containers.api.ContainerCategories.list().url,params,(key)?key:'containerCategories');
    				},
    				experimentCategories : function(params, key){
    					load(jsRoutes.controllers.experiments.api.ExperimentCategories.list().url,params,(key)?key:'experimentCategories');
    				},
    				experimentTypes : function(params, key){
    					load(jsRoutes.controllers.experiments.api.ExperimentTypes.list().url,params,(key)?key:'experimentTypes');
    				},
    				runs : function(params, key){
    					load(jsRoutes.controllers.runs.api.Runs.list().url,params,(key)?key:'runs');    				
    				},
    				reportConfigs : function(params, key){
    					load(jsRoutes.controllers.reporting.api.ReportingConfigurations.list().url,params,(key)?key:'reportConfigs');    				
    				},
    				filterConfigs : function(params, key){
    					load(jsRoutes.controllers.reporting.api.FilteringConfigurations.list().url,params,(key)?key:'filterConfigs');    				
    				},
    				statsConfigs : function(params, key){
    					load(jsRoutes.controllers.stats.api.StatsConfigurations.list().url, params, (key)?key:'statsConfigs');
    				},
    				values : function(params, key){
    					load(jsRoutes.controllers.commons.api.Values.list().url,params,(key)?key:'values');    				
    				},
    				sampleTypes : function(params, key){
    					if(angular.isUndefined(params)){
    	    				params = {};
    	    			}
    					params.objectTypeCode='Sample';
    					load(jsRoutes.controllers.commons.api.CommonInfoTypes.list().url,params,(key)?key:'sampleTypes');
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
    					this.experiments(params);
    					this.experimentTypes(params);
    					this.runs(params);
    					this.protocols(params);
    					this.instruments(params);
    				}
    		};
    		
    		
    		
    		function load(url, params, key){
    			if(inProgress[key] === undefined){
	    			inProgress[key] = true; //avoid multiple load in parallele
	    			if(angular.isUndefined(params)){
	    				params = {};
	    			}
	    			params.list = true;
	    			$http.get(url,{params:params,key:key}).success(function(data, status, headers, config) {
	    				results[config.key]=data;
	    				inProgress[key] = undefined;
	    			});
    			}
    		}
    		
    		return {
    			refresh : refresh,
    			get : function(key){return results[key];},
    			clear : function(key){results[key] = undefined;},
    			getResolutions : function(){return results['resolutions'];},
    			getValuationCriterias : function(){return results['valuationCriterias'];},
    			getProjects : function(){return results['projects'];},
    			getContainerSupportCategories : function(){return results['containerSupportCategories'];},
    			getProcessCategories : function(){return results['processCategories'];},
    			getProcessTypes : function(){return results['processTypes'];},
    			getProjectCategories : function(){return results['projectCategories'];},
    			getProjectTypes : function(){return results['projectTypes'];},
    			getUmbrellaProjects : function(){return results['umbrellaProjects'];},
    			getSamples : function(){return results['samples'];},
    			getUsers : function(){return results['users'];},
    			getExperiments : function(){return results['experiments'];},
    			getContainerSupports : function(){return results['containerSupports'];},
    			getContainerCategories : function(){return results['containerCategories'];},
    			getExperimentCategories : function(){return results['experimentCategories'];},
    			getExperimentTypes : function(){return results['experimentTypes'];},
    			getStates : function(){return results['states'];},
    			getRuns : function(){return results['runs'];},
    			getInstrumentCategories : function(){return results['instrumentCategories'];},
    			getProtocols : function(){return results['protocols'];},
    			getTypes : function(params){
	    						if(params != undefined){
	    							return results[params+'Types'];
	    						}else{
	    							return results['types'];
	    						}
    					   },
			    getKitCatalogs : function(params){
    				if(results['kitCatalogs'] === undefined){
    					refresh.kitCatalogs(params);
    				}
    				return results['kitCatalogs'];
    			},			
    			getInstruments : function(){return results['instruments'];},		   
    			getValuations : function(){return results['valuations'];},
    			getValues : function(params, key){
    				if(results[key] === undefined){
    					refresh.values(params, key);
    				}
    				return results[key];
    			},
    			getSampleTypes : function(params, key){
    				key = (key)?key:'sampleTypes';
    				if(results[key] === undefined){
    					refresh.sampleTypes(params, key);
    				}
    				return results[key];
    			}
    		};
    		
    	}]).factory('convertValueServices', [function() {
    		var constructor = function($scope){
				var convertValueServices = {
				    //Convert the value in inputUnit to outputUnit if the units are different
					convertValue : function(value, inputUnit, outputUnit, precision){
							if(inputUnit !== outputUnit && !isNaN(value)){
								var convert = this.getConversion(inputUnit,outputUnit);
								if(convert != undefined && !angular.isFunction(convert)){
									value = value * convert;
									if(precision !== undefined){
										value = value.toPrecision(precision);
									}else{
										value = value.toPrecision(convert.toString().length);
									}
								}else if(convert == undefined){
									throw "Error: Unknown Conversion "+inputUnit+" to "+outputUnit;
									return undefined;
								}
							}
							
							return value;
					},
					//Get the multiplier to convert the value
					getConversion : function(inputUnit, outputUnit){
						if((inputUnit === 'µg' && outputUnit === 'ng') || (inputUnit === 'ml' && outputUnit === 'µl') || (inputUnit === 'pM' && outputUnit === 'nM')){
							return (1/1000);
						}else if((inputUnit === 'ng' && outputUnit === 'µg') || (inputUnit === 'µl' && outputUnit === 'ml') || (inputUnit === 'nM' && outputUnit === 'pM')){
							return 1000;
						}
						return undefined;
					},
					parse : function(value){
						var valueToConvert = value;
						if(!angular.isNumber(valueToConvert)){
							var valueConverted = value.replace(/\s+/g,"").replace(',','.');
							valueConverted = parseFloat(valueConverted);
							
							return valueConverted;
						}
						
						return value;
					}
				};
				return convertValueServices;
			};
    		return constructor;
    	}]).directive('messages', function() {
    		return {
    			restrict: 'A',
    			scope: {
    				  messages: '=messages'
    				},
    			template: '<div ng-class="messages.clazz" ng-show="messages.isOpen()">'+
    				'<button class="close" ng-click="messages.close()" type="button">Ã—</button>'+
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
    						 element.html(Messages(Codes(type+"."+ngModel.$viewValue)));
    					 }
    				 };
    			}    					
    		};
    	//If the select or multiple choices contain 1 element, this directive select it automaticaly
    	//EXAMPLE: <select ng-model="x" ng-option="x as x for x in x" auto-select>...</select>
    	}).directive('autoSelect',['$parse', function($parse) {
    		var OPTIONS_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?(?:\s+group\s+by\s+(.*))?\s+for\s+(?:([\$\w][\$\w\d]*)|(?:\(\s*([\$\w][\$\w\d]*)\s*,\s*([\$\w][\$\w\d]*)\s*\)))\s+in\s+(.*)$/;
    		return {
    			require: 'ngModel',
    			link: function(scope, element, attrs, ngModel) {
    				var valOption = undefined;
					if(attrs.ngOptions){	
						valOption = attrs.ngOptions;
					}else if(attrs.btOptions){
						valOption = attrs.btOptions;
					}
					
					if(valOption != undefined){
						var match = valOption.match(OPTIONS_REGEXP);
						var model = $parse(match[7]);
						scope.$watch(model, function(value){
							if(value){
				                if(value.length === 1 && (ngModel.$modelValue == undefined || ngModel.$modelValue == "")){
									ngModel.$setViewValue(value[0].code);
									ngModel.$render();
								}
							}
				        });
					}else{
						console.log("ng-options or bt-options required");
					}
    			}
    		};    	
    	}]).directive('convertValue',['convertValueServices','$filter', function(convertValueServices, $filter) {
            return {
                require: 'ngModel',
                link: function(scope, element, attr, ngModel) {
                	//init service
                	var convertValues = convertValueServices();
                	var property = undefined;
                	
					var watchModelValue = function(){
						return scope.$watch(
									function(){
										return ngModel.$modelValue;
									}, function(newValue, oldValue){
										if(property != undefined){
											var convertedValue = convertValues.convertValue(newValue, property.saveMeasureValue, property.displayMeasureValue);
											ngModel.$setViewValue($filter('number')(convertedValue));
											ngModel.$render();
										}
								});
					};
					
                	scope.$watch(attr.convertValue, function(value){
    					if(value.saveMeasureValue != undefined && value.displayMeasureValue != undefined){
    						property = value;
    					}
    				});
                	
                	//model to view when the user go out of the input
                	element.bind('blur', function () {
                		var convertedValue = convertValues.convertValue(ngModel.$modelValue, property.saveMeasureValue, property.displayMeasureValue, ngModel.$viewValue.length);
                		ngModel.$setViewValue($filter('number')(convertedValue));
						ngModel.$render();
						//We restart the watcher when the user is out of the inputs
						scope.currentWatcher = watchModelValue();
                	});
                	
					//when the user go into the input
					element.bind('focus', function () {
						//We need to disable the watcher when the user is typing
						scope.currentWatcher();
                	});
					
                	//model to view whatcher
                	scope.currentWatcher = watchModelValue();
                	
                    //view to model
                    ngModel.$parsers.push(function(value) {
                    	value = convertValues.parse(value);
                    	if(property != undefined){
	                    	value = convertValues.convertValue(value, property.displayMeasureValue, property.saveMeasureValue);
                    	}
                    	return value;
                    });
                }
            };
        //Convert the date in format(view) to a timestamp date(model)
        }]).directive('dateTimestamp', function() {
            return {
                require: 'ngModel',
                link: function(scope, ele, attr, ngModel) {
					var typedDate = "01/01/1970";//Initialisation of the date
					
                	var convertToDate = function(date){
                		if(date !== null && date !== undefined && date !== ""){
	                		var format = Messages("date.format").toUpperCase();
	                		date = moment(date).format(format);
	                		return date;
                		}
                		return "";
                	};
                	
                	var convertToTimestamp = function(date){
                		if(date !== null && date !== undefined && date !== ""){
	                		var format = Messages("date.format").toUpperCase();
	    					return moment(date, format).valueOf();
                		}
                		return "";
    				};
					
                	//model to view
                	scope.$watch(
						function(){
							return ngModel.$modelValue;
						}, function(newValue, oldValue){
							//We check if the
							if(newValue !== null && newValue !== undefined && newValue !== "" && typedDate.length === 10){
    							var date = convertToDate(newValue);
    							ngModel.$setViewValue(date);
								ngModel.$render();
							}
                    });
					
                	//view to model
                    ngModel.$parsers.push(function(value) {
                    	var date = value;
						typedDate = date;//The date of the user
                    	if(value.length === 10){//When the date is complete
                    		date = convertToTimestamp(value);
                    	}
						return date;
                    });
                }
            }
        }).directive('base64Img', [function () {
        	return {
        		 restrict: 'A',
        		 scope: {
        			 base64Img: "="
        	        },
        		 link: function (scope, elem, attrs, ngModel) {
	        		  var reader = new FileReader();
	        		  var file;
	        		  if(scope.base64Img != undefined && scope.base64Img.value == ""){
	        			  scope.base64Img = undefined;
	        		  }
	        		  
	        		  reader.onload = function (e) {
	        			  scope.$apply(function () {
	        				  if(e.target.result!= undefined && e.target.result != ""){
		        				  scope.base64Img = {};
		        				  scope.base64Img._type = "img";
		        				  scope.base64Img.fullname = file.name;
		        				  
		        				  //Get the extension
		        				  var matchExtension = file.type.match(/^image\/(.*)/);
			        				  if(matchExtension && matchExtension.length > 1){
			        				  scope.base64Img.extension = matchExtension[1];
			        				  
			        				  //Get the base64 without the extension feature
			        				  var matchBase64 = e.target.result.match(/^.*,(.*)/);
			        				  scope.base64Img.value = matchBase64[1];
			        				  //Load image from the base64 to get the width and height
			        				  var img = new Image();
			        				  img.src =  e.target.result;
		
			        				  img.onload = function(){
			        					  scope.base64Img.width = img.width;
			        					  scope.base64Img.height = img.height;
			        				  };
		        				  }else{
		        					 alert("This is not an image...");
		        					 scope.base64Img = undefined;
		        				  }
	        				  }else{
	        					  scope.base64Img = undefined;
	        				  }
	        			  });
	        		  }
	
				      elem.on('change', function() {
				    	  	file = elem[0].files[0];
				    	  	reader.readAsDataURL(elem[0].files[0]);
				      });
        		 }
        		};
        		}]).directive('btSelect',  ['$parse', '$document', '$window', '$filter', function($parse,$document, $window, $filter)  {
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
  				        
  		    			+'<div class="input-group">'
  		    			+'<input type="text" style="background:white" ng-class="inputClass" ng-model="selectedLabels" placeholder="{{placeholder}}" title="{{placeholder}}" readonly/>'
  		    			+'<div class="input-group-btn">'
  		    			+'<button tabindex="-1" data-toggle="dropdown" class="btn btn-default dropdown-toggle" type="button" ng-disabled="isDisabled()" ng-click="open()">'
  		    			+'<span class="caret"></span>'
  		    			+'</button>'
  		    			+'<ul class="dropdown-menu dropdown-menu-right"  role="menu">'
  				        +'<li ng-if="filter"><input ng-class="inputClass" type="text" ng-click="inputClick($event)" ng-model="filterValue" ng-change="setFilterValue(filterValue)"/></li>'


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
		    	  		+'</div>'
		    	  		+'</div>'
		    	  		,
	    	  		require: ['?ngModel'],
	       		    link: function(scope, element, attr, ctrls) {
	       		  // if ngModel is not defined, we don't need to do anything
	      		      if (!ctrls[0]) return;
	      		      scope.inputClass = element.attr("class");
	      		      scope.placeholder = attr.placeholder;
    		          
	      		      element.attr("class",''); //remove custom class
	      		     
	      		      var ngModelCtrl = ctrls[0],
	      		          multiple = attr.multiple || false,
	      		          btOptions = attr.btOptions,
	      		          editMode = (attr.ngEdit)?$parse(attr.ngEdit):undefined,
	      		          filter = attr.filter || false;

	      		      var optionsConfig = parseBtsOptions(btOptions);
	      		      var items = [];
	      		      var groupByLabels = {};
	      		      var filterValue;
	      		      var ngFocus = attr.ngFocus;
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
	      		   
	      		     scope.filter = filter; 
	      		     scope.setFilterValue = function(value){
	      		    	filterValue = value
	      		     };
	      		     
	      		     scope.open = function(){
	      		    	 if(ngFocus){
	      		    		$parse(ngFocus)(scope);  
	      		    	 }
	      		     };
	      		     
	      		     scope.isDisabled = function(){
	      		    	return (attr.ngDisabled)?scope.$parent.$eval(attr.ngDisabled):false;
	      		     };
	      		     
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
      		      scope.inputClick = function($event){
      		    	$event.preventDefault();
	    			$event.stopPropagation();
      		      };
	      		      
      		      
      		      scope.$watch(optionsConfig.sourceKey, function(newValue, oldValue){
      		    	  if(newValue && newValue.length > 0){
      		    		items = angular.copy(newValue);  
      		    		render();      		    		
      		    	  }
      		      });
	      		      
	      		   ngModelCtrl.$render = render;
	      		   
	      		    function render() {
	      		    	var selectedLabels = [];
	      		    		      		    	
		      	    	var modelValues = ngModelCtrl.$modelValue || [];
		      	    	if(!angular.isArray(modelValues)){
		      	    		modelValues = [modelValues];
		      	    	}		      	    	
		      	    	if(items.length > 0){
			      	    	for(var i = 0; i < items.length; i++){
			      	    		var item = items[i];
			      	    		item.selected = false;
			      	    		for(var j = 0; j < modelValues.length; j++){
			      	    			var modelValue = modelValues[j];
			      	    			if(scope.itemValue(item) === modelValue){
			      	    				item.selected = true;
				      		    		selectedLabels.push(scope.itemLabel(item));
				      	    		}
			      	    		}	      	    		
			      	    	}
		      	    	}
		      	    	scope.selectedLabels = selectedLabels;
	      	        };
	      	        
	      		  }
	      		  
  		    };
    	}]).directive('chart', function() {
    	    return {
    	        restrict: 'E',
    	        template: '<div></div>',
    	        scope: {
    	            chartData: "=value",
    	            chartObj: "=?"
    	        },
    	        transclude: true,
    	        replace: true,
    	        link: function($scope, $element, $attrs) {

    	            //Update when charts data changes
    	            $scope.$watch('chartData', function(value) {
    	                if (!value)
    	                    return;

    	                // use default values if nothing is specified in the given settings
    	                $scope.chartData.chart.renderTo = $scope.chartData.chart.renderTo || $element[0];
    	                if ($attrs.type)
    	                    $scope.chartData.chart.type = $scope.chartData.chart.type || $attrs.type;
    	                if ($attrs.height)
    	                    $scope.chartData.chart.height = $scope.chartData.chart.height || $attrs.height;
    	                if ($attrs.width)
    	                    $scope.chartData.chart.width = $scope.chartData.chart.type || $attrs.width;

    	                $scope.chartObj = new Highcharts.Chart($scope.chartData);
    	            });
    	        }
    	    };
    	    
    	}).filter('filters',['$filter',function ($filter) {
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
    	}]).filter('unique', function($parse) {
    		return function (collection, property) {
    			var isDefined = angular.isDefined,
    		    isUndefined = angular.isUndefined,
    		    isFunction = angular.isFunction,
    		    isString = angular.isString,
    		    isNumber = angular.isNumber,
    		    isObject = angular.isObject,
    		    isArray = angular.isArray,
    		    forEach = angular.forEach,
    		    extend = angular.extend,
    		    copy = angular.copy,
    		    equals = angular.equals;

	
	    		/**
	    		* get an object and return array of values
	    		* @param object
	    		* @returns {Array}
	    		*/
	    		function toArray(object) {
	    		    var i = -1,
	    		        props = Object.keys(object),
	    		        result = new Array(props.length);
	
	    		    while(++i < props.length) {
	    		        result[i] = object[props[i]];
	    		    }
	    		    return result;
	    		}
    			
    		      collection = (angular.isObject(collection)) ? toArray(collection) : collection;
				  if(collection !== undefined && collection !== null){
					  if (isUndefined(property)) {
						return collection.filter(function (elm, pos, self) {
						  return self.indexOf(elm) === pos;
						})
					  }
					  //store all unique members
					  var uniqueItems = [],
						  get = $parse(property);

					  return collection.filter(function (elm) {
						var prop = get(elm);
						if(some(uniqueItems, prop)) {
						  return false;
						}
						uniqueItems.push(prop);
						return true;
					  });
				  }
    		      //checked if the unique identifier is already exist
    		      function some(array, member) {
    		        if(isUndefined(member)) {
    		          return false;
    		        }
    		        return array.some(function(el) {
    		          return equals(el, member);
    		        });
    		      }
    		    }
    	}).filter('sum', ['$parse',function($parse) {
    	    return function(array, key) {
    	    	if(!array)return undefined;
    	    	if(!angular.isArray(array) && (angular.isObject(array) || angular.isNumber(array))) array = [array];
    	    	else if(!angular.isArray(array)) throw "input is not an array, object or a number !";
    	    	
    	    	if(key && !angular.isString(key))throw "key is not valid, only string is authorized";
    	    	
    	    	var params = {sum:0, key:key};
    	    	angular.forEach(array, function(value, index){
    	    		if(params.key && angular.isObject(value))params.sum = params.sum + $parse(params.key)(value);
    	    		else if(!params.key && angular.isObject(value))throw "missing key !";
    	    		else params.sum = params.sum + value;
    	    	}, params);
    	    	return params.sum;
    	    };
    	}]).filter('get', ['$parse',function($parse) {
    	    return function(object, key) {
    	    	if(!object)return undefined;
    	    	if(angular.isArray(object) && object.length === 1) object = object[0];
    	    	else if(angular.isArray(object) && object.length > 1){
    	    		object = object[0];
    	    		console.log("input contains several values take the first !");
    	    	}
    	    	if(!angular.isObject(object))return object;
    	    	if(key && !angular.isString(key))throw "key is not valid, only string is authorized";    	    	
    	    	return $parse(key)(object);
    	    };
    	}]).
    	/**
		* get an object and a key of this object and return array of values
		* @param object
		* @param key
		* @returns [Array]
		*/
    	
    	filter('getArray', ['$parse',function($parse) {
    	    return function(objects, key) {
    	    	if(key && !angular.isString(key))throw "key is not valid, only string is authorized"; 	
    	    	if(!objects)return undefined;    	    	
    	    	if(!angular.isObject(objects))  return objects;    	    
    	    	var data=[];
    	    	var get="";
    	    	if(angular.isObject(objects)  && objects.length > 0){    	    		
    	    		angular.forEach(objects, function(value, index){
    	    			get=$parse(key)(value);    	    			
    	    			data.push(get);    	    			
    	    		});    	    		
    	    	}     	    	
    	    	return data;
    	    };
    	}]).filter('codes', function(){
    		return function(input, key){
    			if(input !== undefined && null != input) return Messages(Codes(key+"."+input));
    			return undefined;
    		}
    	}).filter('convert', ['convertValueServices', function(convertValueServices){
    		return function(input, property){
				var convertValues = convertValueServices();
				if(property != undefined){
					input = convertValues.convertValue(input, property.saveMeasureValue, property.displayMeasureValue);
				}
    			return input;
    		}
    	}]).filter('messages', function(){
    		return function(input){
    			return Messages(input);    			
    		}
    	}).filter('inttostring', function(){
    		return function(input){
    			return String(input);    			
    		}
    	}).filter('countDistinct', ['$parse',function($parse) {
    	    return function(array, key) {
    	    	if (!array || array.length === 0)return undefined;
    	    	if (!angular.isArray(array) && (angular.isObject(array) || angular.isNumber(array) || angular.isString(array) || angular.isDate(array))) array = [array];
    	    	else if(!angular.isArray(array)) throw "input is not an array, object, number or string !";
    	    	
    	    	if(key && !angular.isString(key))throw "key is not valid, only string is authorized";
    	    	
    	    	var possibleValues = [];
    	    	angular.forEach(array, function(element){
    	    		if (angular.isObject(element)) {
    	    			var currentValue = $parse(key)(element);
    	    			if(undefined !== currentValue && null !== currentValue && possibleValues.indexOf(currentValue) === -1){
       	    				possibleValues.push(currentValue);
    	    			}
    	    			
    	    			
    	    		}else if (!params.key && angular.isObject(value)){
    	    			throw "missing key !";
    	    		}
    	    		
    	    	});
    	    	return possibleValues.length;    	    	
    	    };
    	}]);
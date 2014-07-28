"use strict";

angular.module('biCommonsServices', []).
    	factory('treatments',['$q','$http','$filter', function($q,$http,$filter){
    		var _treatments = [];
    		var _allTreatments = {};
    		var _treatment = {};    		
    		var codeLastActive = undefined;
    		/**
    		 * Set one element of list active
    		 */
    		function activeTreatment(value){
    			if(angular.isDefined(value)){
    				_treatment = value;
    				for(var i = 0; i < _treatments.length; i++){
    					if(_treatments[i].code != _treatment.code){
    						_treatments[i].clazz='';
    					}else{
    						_treatments[i].clazz='active';
    						codeLastActive=_treatments[i].code;
    					}
    				}
    			} 
    		};
    		
    		function activeLastTreatment(){
    			var find = false;
				for(var i = 0; i < _treatments.length; i++){
					if(_treatments[i].code == codeLastActive){
						_treatment = _treatments[i];
						_treatments[i].clazz='active';
						find = true;
					}else{
						_treatments[i].clazz='';    						
					}
				}
				if(!find)activeTreatment(_treatments[0]);
    		};
    		
    		function prepareCurrentTreatments(){
    			for(var key in _allTreatments){
    				if(_allTreatments[key].show){
    					_treatments.push(_allTreatments[key]);
    				}    				
    			}
    			_treatments = $filter("orderBy")(_treatments,"order");
				activeLastTreatment();	
    		};
    		
    		function init(treatments, url, excludes){
    			_treatment = {};
    			_treatments = [];
    			var queries = [];
				
    			for(var key in _allTreatments){
    				_allTreatments[key].show = false;
    			}
    			
    			for (var key in treatments) {
					var treatment = treatments[key];	
					if(!_allTreatments[key] && (angular.isUndefined(excludes) || angular.isUndefined(excludes[treatment.code]))){
						queries.push($http.get(jsRoutes.controllers.treatmenttypes.api.TreatmentTypes.get(treatment.typeCode).url, 
								{key:key})	
						);
					}else if(_allTreatments[key]){
						_allTreatments[key].show = true;						
					}
    			}	
    			if(queries.length > 0){
    				$q.all(queries).then(function(results){
    					for(var i = 0; i  < results.length; i++){
    						var result = results[i];
    						_allTreatments[result.config.key]={code:result.config.key, name:Messages("readsets.treatments."+result.config.key), url:url(result.data.code).url, order:displayOrder(result, key), show:true};
    					}
    					prepareCurrentTreatments();    					
    				});
    			}else{
    				prepareCurrentTreatments();
    			}
    			
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
    	}]).factory('valuationService',['$parse', 'lists', function($parse, lists){
    		var criterias = undefined;
    		var valuationCriteriaClass = function(value, criteriaCode, propertyName){
    			//init criterias
    			if((!criterias || !criterias[criteriaCode]) && lists.getValuationCriterias() && lists.getValuationCriterias().length > 0 ){
    				var values = lists.getValuationCriterias();
    				criterias = {};
    				for(var i = 0 ; i < values.length; i++){
    					criterias[values[i].code] = values[i]; 
    				}
    			}
    			
    			if (criteriaCode && criterias[criteriaCode]) {
    				var criteria = criterias[criteriaCode];
    				var property;
    				for(var i = 0; i < criteria.properties.length; i++){
    					if(criteria.properties[i].name === propertyName){
    						property = criteria.properties[i];
    						break;
    					}
    				}
    				if(property){
    					for(var i = 0; i  < property.expressions.length; i++){
    						var expression = property.expressions[i];
    						if($parse(expression.rule)({context:value, pValue : $parse(propertyName)(value)})){
    							return expression.result;
    						}
    					}
    				}
    			}
    			return undefined;			
    		};
    		return function() {
    			criterias = undefined;
    			return {valuationCriteriaClass : valuationCriteriaClass};
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
    	}).directive('ngBindSrc', ['$parse',function($parse){ //used to include krona
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
    	}]).directive('reportingConfigTreatments', function($parse){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	scope:true,
  		    	template:''
  		    			+'<div class="row">'
  		    			+'<div class="col-md-4 col-lg-4"><div reporting-properties-select></div></div>'  		    			
  		    			+'<div class="col-md-8 col-lg-8"><div reporting-properties-config></div></div>' 
  		    			+'</div>'
  		    			,  	
  		    	controller: function($scope){ 
  		    		//private
  		    		var selectedProperties = {};
  		    		var allProperties = {};
  		    		var prefix = '';
  		    		
  		    		//public 
  		    		this.init = function(prefixMsg, properties){
  		    			prefix = prefixMsg;
  		    			for(var i = 0; i < properties.length ; i++){
  		    				properties[i].show = false;
  		    				properties[i].toggleShow = function(){
								this.show = !this.show
  		    				};  		  		    					    			
		    			}  		    				
  		    			allProperties = properties;
  		    		};
  		    		
  		    		
  		    		//scope
  		    		$scope.selectShowAll = false;
  		    		$scope.configShowAll = false;
  		    		
  		    		
  		    		$scope.isSelectedProperties = {};
  		    		
  		    		$scope.toggleSelectShowAll = function(){
  		    			$scope.selectShowAll = !$scope.selectShowAll;
  		    			for(var i = 0; i < allProperties.length ; i++){
  		    				allProperties[i].show = !$scope.selectShowAll;
  		    				allProperties[i].toggleShow();
		    			}
  		    		}
  		    		
  		    		$scope.toggleConfigShowAll = function(){
  		    			$scope.configShowAll = !$scope.configShowAll;
  		    			for(var key in selectedProperties){
  		    				selectedProperties[key].show = !$scope.configShowAll;
  		    				selectedProperties[key].toggleShow();
		    			}
  		    		}
  		    		
  		    		$scope.toggleSelectProperty = function(treatmentType, context, property){
  		    			if($scope.isSelectedProperties[treatmentType.instanceCode+"."+context.code+"."+property.code]){
  		    				if(!selectedProperties[treatmentType.instanceCode]){
  		    					$parse(treatmentType.instanceCode+".code").assign(selectedProperties, treatmentType.code);
  		    					$parse(treatmentType.instanceCode+".show").assign(selectedProperties, $scope.configShowAll);
  	  		    				$parse(treatmentType.instanceCode+".toggleShow").assign(selectedProperties, function(){this.show = !this.show});  	  		    			
  		    				}
  		    				$parse(treatmentType.instanceCode+".contexts."+context.code+"."+property.code).assign(selectedProperties, property);  		    				
  		    			} else {
  		    				delete selectedProperties[treatmentType.instanceCode]["contexts"][context.code][property.code];
  		    				var exist = false;
  		    				for (var x in selectedProperties[treatmentType.instanceCode]["contexts"][context.code]){
  		    					if(x)exist = true;
  		    				}
  		    				if(!exist){delete selectedProperties[treatmentType.instanceCode]["contexts"][context.code];	}
  		    				
  		    				exist = false;
  		    				for (var x in selectedProperties[treatmentType.instanceCode]["contexts"]){
  		    					if(x)exist = true;
  		    				}
  		    				if(!exist){delete selectedProperties[treatmentType.instanceCode]["contexts"];}
  		    				
  		    				exist = false;
  		    				for (var x in selectedProperties[treatmentType.instanceCode]){
  		    					if(x)exist = true;
  		    				}
  		    				if(!exist){delete selectedProperties[treatmentType.instanceCode];}  		    					
  		    			}
  		    		}
  		    		
  		    		$scope.getMessage = function(key){
  		    			return Messages(key);
  		    		};
  		    		
  		    		$scope.getTreatmentName = function(value){
  		    			return Messages(prefix+".treatments."+value.replace("-","_"));
  		    		};
  		    		$scope.getContextName = function(value){
  		    			return Messages("treatments.context."+value);
  		    		};
  		    		$scope.getPropertyName = function(treatmentCode, value){
  		    			return Messages(prefix+".treatments."+treatmentCode.replace("-","_")+"."+value);
  		    		};
  		    		
  		    		$scope.getSelectedProperties = function(){
  		    			return selectedProperties;
  		    		}
  		    		
  		    		$scope.getAllProperties = function(){
  		    			return allProperties;
  		    		}
  		    		
  		    		$scope.addToDatatable = function(){
  		    			var columns = [];
  		    			for(var treatmentInstanceName in selectedProperties){
  		    				var treatmentType = selectedProperties[treatmentInstanceName];
  		    				for(var contextCode in treatmentType.contexts){
  		    					var context = treatmentType.contexts[contextCode];
  		    					for(var propertyCode in context){
  		    						var property = context[propertyCode].subProperties[0];
  		    						var column = {
  		    								id:$scope.datatable.generateColumnId(),
  		    								header:$scope.getPropertyName(treatmentType.code,property.code),
  		    								property:"treatments."+treatmentInstanceName+"."+contextCode+"."+property.code+".value", 
											type:property.valueType,
											order:true,
											format:property.format 										
  		    						};
  		    						$scope.datatable.addColumn(4,column);
  		    					}
  		    				}  		  		    			
  		    			}
  		    		}
  		    		
  		    	
  		    	},
  		    	link: function(scope, element, attr, ctrl) {
  		    		if(!attr.reportingConfigTreatments) return;
  		    		var prefix = attr.prefixMsg;
  		    		
  		    		scope.$watch(attr.reportingConfigTreatments, function(newValue, oldValue) {
  		    			if(newValue && (newValue !== oldValue || !scope.dtTable)){
  		    				var reportingConfigTreatments = $parse(attr.reportingConfigTreatments)(scope);
  		    				ctrl.init(prefix, reportingConfigTreatments);
  		    			}
		            });
  		    		
  		    		
  		    	}
    		};
    	}).directive('reportingPropertiesSelect', function($parse){
			return {
				restrict: 'A',
				replace:true,
				scope:true,
		    	template:''
		    			+'<div class="panel panel-default">'   		    			
		    			+'<div class="panel-heading">'
		    			+'	<button class="btn btn-default btn-xs pull-right" ng-click="toggleSelectShowAll()">'
		  		    	+'	<i class="fa fa-plus-square" ng-if="!selectShowAll"></i>'
	  		    		+'	<i class="fa fa-minus-square" ng-if="selectShowAll"></i>' 		
		  		    	+'	</button>'		  		    	
		  		    	+'  <span ng-bind="getMessage(\'title.report.property.selection\')" ng-click="toggleSelectShowAll()"/>'
		    			+'</div>'  		    			
		    			+'<ul class="list-group">'
		    			+'<li class="list-group-item" ng-repeat="treatmentType in getAllProperties()">'
		    			+'	<button class="btn btn-default btn-xs pull-right" ng-click="treatmentType.toggleShow()">'
		  		    	+'	<i class="fa fa-plus-square" ng-if="!treatmentType.show"></i>'
	  		    		+'	<i class="fa fa-minus-square" ng-if="treatmentType.show"></i>' 		
		  		    	+'	</button>'
		    			+'	<h4 class="list-group-item-heading margin-bottom-7" ng-bind="getTreatmentName(treatmentType.instanceCode)" ng-click="treatmentType.toggleShow()"></h4>'
		    			+'	<div class="row" ng-if="treatmentType.show">'
		    			+' 		<div class="col-md-6 col-lg-6" ng-repeat="context in treatmentType.contexts">'
		    			+'		<h5 class="list-group-item-heading"><strong ng-bind="getContextName(context.code)"></strong></h5>'		
		  		    	+'		<ul class="list-unstyled">'
		  		    	+'			<li ng-repeat="property in context.properties">'
		  		    	+'			<div class="checkbox text-overflow">'
		  		    	+'			  <label>'
		  		    	+'			    <input type="checkbox" ng-model="isSelectedProperties[treatmentType.instanceCode+\'.\'+context.code+\'.\'+property.code]" ng-change="toggleSelectProperty(treatmentType, context, property)"><span ng-bind="getPropertyName(treatmentType.code, property.code)"/>'
		  		    	+'			  </label>'
		  		    	+'			</div>'
		  		    	+'			</li>'
		  		    	+'		</ul>'
		    			+'		</div>'  		    			
		    			+'	</div>'
		    			+'</li>'
		    			+'</ul>'
		    			+'</div>'
			};    			
	}).directive('reportingPropertiesConfig', function($parse){
    			return {
    				restrict: 'A',
    				scope:true,
    				replace:true,
    		    	template:''
    		    		+'<div class="panel panel-default">'   		    			
  		    			+'<div class="panel-heading">'
  		    			+'	<button class="btn btn-default btn-xs pull-right" ng-click="toggleConfigShowAll()">'
		  		    	+'	<i class="fa fa-plus-square" ng-if="!configShowAll"></i>'
	  		    		+'	<i class="fa fa-minus-square" ng-if="configShowAll"></i>' 		
		  		    	+'	</button>'
		  		    	+'	<button class="btn btn-default btn-xs pull-right" ng-click="addToDatatable()">'
		  		    	+'	<i class="fa fa-table"></i>'
	  		    		+'	</button>'
  		    			+'  <span ng-bind="getMessage(\'title.report.property.configuration\')" ng-click="toggleConfigShowAll()"/>'
  		    			+'</div>'
  		    			+'<ul class="list-group">'
		    			+'<li class="list-group-item" ng-repeat="(instanceCode, treatmentType) in getSelectedProperties()">'
		    			+'	<button class="btn btn-default btn-xs pull-right" ng-click="treatmentType.toggleShow()">'
		  		    	+'		<i class="fa fa-plus-square" ng-if="!treatmentType.show"></i>'
	  		    		+'		<i class="fa fa-minus-square" ng-if="treatmentType.show"></i>' 		
		  		    	+'	</button>'
		    			+'	<h4 class="list-group-item-heading margin-bottom-7" ng-bind="getTreatmentName(instanceCode)"></h4>'
		    			+'	<div class="row" ng-if="treatmentType.show">'
		    			+' 		<div class="col-md-6 col-lg-6" ng-repeat="(contextCode, context) in treatmentType.contexts">'
		    			+'		<h5 class="list-group-item-heading"><strong ng-bind="getContextName(contextCode)"></strong></h5>'		
		  		    	+'		<ul class="list-unstyled">'
		  		    	+'			<li ng-repeat="(propertyCode, property) in context">'
		  		    	+'				<span ng-bind="getPropertyName(treatmentType.code, property.code)"/>'
		  		    	+'			</li>'
		  		    	+'		</ul>'
		    			+'		</div>'  		    			
		    			+'	</div>'
		    			+'</li>'
		    			+'</ul>'
  		    			+'</div>'  		    			
    			};    			
    	}).directive('modalChart', ['$compile', '$http', '$q', '$filter', function ($compile, $http, $q, $filter) {
    		
    		//use path to generate arrow instead of image (more flexibility : allow to specify color, line-width, curve)
    		//use label instead of text (allow shadow and simplify code : no need BBox and rect)
    		
    	    var modalTemplate = angular.element("<div id='{{modalId}}' class='modal'  tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true' style='left:100px; top:100px;overflow:hidden'>"+
    	    										"<div class='modal-content' style='width:{{modalContentWidth}}px; height:{{modalContentHeight}}px; border:0px'>"+
    	    											"<div class='modal-header'>"+
    	    												"<h3 id='myModalChartLabel'>{{modalHeaderText}}</h3>" +
    	    												"<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"+
    	    											"</div>"+
    	    											"<div class='modal-body' style='padding:0px'>"+
    	    												"<div id='container0'></div>"+
    	    											"</div>"+
    	    										"</div>"+
    	    									"</div>");
    	    
    	    var linkTemplate = "<a href='#{{modalId}}' id='linkTo{{modalId}}' role='button' data-toggle='modal' class='btn small_link_button'>{{modalCurrentCode | codes:'state'}}</a>";  // not angular.element !
    	    
    	    var linker = function (scope, element, attrs) {
    	    	
    	        scope.modalHeaderText = attrs.modalHeaderText;
    	        scope.modalBodyText = attrs.modalBodyText;
    	        scope.modalId = attrs.modalId;
    	        scope.modalBgColor = attrs.modalBgColor;
    	        scope.modalWidth = attrs.modalWidth;
    	        scope.modalHeight = attrs.modalHeight;
    	        scope.modalContentWidth = parseFloat(scope.modalWidth) + 100;
    	        scope.modalContentHeight = parseFloat(scope.modalHeight) + 100;
    	        
    	        $compile(element.contents())(scope);
    	        var newTemplate = $compile(modalTemplate)(scope);

    	        $(newTemplate).appendTo('body');
    	    
    	        $("#" + scope.modalId).modal({
    	            backdrop: false,
    	            show: false
    	        });
    	        
    			element.on('click', function(event) {
    			    event.preventDefault();
    	            $q.when(createEmptyChart()).then(function(chart) {
    	            		populateChart(chart);
    	            	}
    	            );                
    			});
    	        
    			function createEmptyChart() {
    			 	var chart1 = new Highcharts.Chart({
    					chart : {
    						renderTo : 'container0',
    						backgroundColor: scope.modalBgColor,
    						height: scope.modalHeight,
    						width:scope.modalWidth
    					},	
    					title : {text : scope.modalBodyText}
    				});
    			 	return chart1;
    			};
    			
    			
    			function drawLabel(ren, data, highLightCode, offsetXText, offsetYText, globalParam) {
    				ren.label(data.name, offsetXText, offsetYText)
	                .attr({
	                    fill: getFillColor(data.code, highLightCode),
	                    stroke: getBorderColor(data.code, highLightCode, data.specificColor),
	                    'stroke-width': 2,
	                    padding: 5,
	                    width: globalParam.boxWidth,
	                    //height:globalParam.boxHeight,
	                    r: 5
	                })
	                .css({
	                    color: getFontColor(data.code, highLightCode, data.specificColor),
	                    fontStyle: '10px', // not 10!
	                    fontWeight: 'normal',
	                    fontFamily: 'arial'
	                })
	                .add()
	                .shadow(true);
    				
    				drawComment(ren, data, offsetXText, offsetYText, globalParam);
    			}
    			
    			
    			function drawComment(ren, data, offsetXText, offsetYText, globalParam) {
    				if (data.comment != undefined && data.comment != null) {
						var lbl = data.comment.label;
						if (data.comment.type == 'datetime') {
							lbl = $filter('date')(data.comment.label, Messages("datetime.format"))
						}
						if (data.comment.type == 'datetime') {
							lbl = $filter('date')(data.comment.label, Messages("date.format"))
						}
	    				ren.label(lbl, offsetXText + globalParam.boxWidth+15, offsetYText)
	    				.css({
		                    color: 'darkgray',
		                    fontStyle: '9px',
		                    fontWeight: 'italic',
		                    fontFamily: 'arial'
		                })
		                .add()
    				}	
    			}
    			
    			
    			
    			function drawArrow(ren, offsetXText, offsetXText2, offsetYText, offsetYText2, globalParam) {
    				var offsetXArrow = offsetXText + (globalParam.boxWidth/2 -1);
    				var offsetXArrow2 = offsetXText2 + (globalParam.boxWidth/2 -1);    				
    				var offsetYArrow = offsetYText + globalParam.boxHeight;
    				var offsetYArrow2 = offsetYText2;
    				var arrow;
    				
    				if (offsetXArrow == offsetXArrow2) {
	    				arrow = ren.path(['M', offsetXArrow, offsetYArrow, 'L', offsetXArrow2,  offsetYArrow2, /*body arrow*/ 
	                              'L', offsetXArrow2-5, offsetYArrow2-5, 'M', offsetXArrow2,  offsetYArrow2, /*left side*/ 
	                              'L', offsetXArrow2+5, offsetYArrow2-5, 'M', offsetXArrow2,  offsetYArrow2]) /*right side*/;
    				}
    				else {
        				arrow = ren.path(['M', offsetXArrow, offsetYArrow, 'L', offsetXArrow2,  offsetYArrow2, 
                                  'L', offsetXArrow2-1, offsetYArrow2-5, 'M', offsetXArrow2,  offsetYArrow2, 
                                  'L', offsetXArrow2+3, offsetYArrow2+4, 'M', offsetXArrow2,  offsetYArrow2]);
    				}
    				
    				arrow.attr({'stroke-width': 2, stroke: 'darkgray'}).add();
     			}
    			
    			
    			function drawSeparatorLine(ren, data, offsetY, globalParam) {
    				var offsetXLine = globalParam.offsetXText -10;
    				var offsetYLine = offsetY  + globalParam.boxHeight + globalParam.spaceVbetween2box*0.75;
    				var offsetXLine2 = globalParam.offsetXText + globalParam.boxWidth+20;
    				
                    ren.path(['M', offsetXLine, offsetYLine, 'L', offsetXLine2, offsetYLine])
                    .attr({
                        'stroke-width': 2,
                        stroke: 'silver',
                        dashstyle: 'dash'
                    })
                    .add();
    			}
    			
    			
    			function renderChart(renderer, currentLevel, data, highLightCode, offsetXText, offsetYText, hasSeparatorLine, globalParam) {
    				
    				var offsetXText2, offsetYText2 = offsetYText;    	
    				
					if (currentLevel > 0) {
						offsetYText2 += globalParam.spaceVbetween2box + globalParam.boxHeight;
					}
					if (hasSeparatorLine) {
						offsetYText2 += globalParam.spaceVbetween2box / 2;
					}

    				for (var i=0; i<data.length; i++) {
    					if (data.length == 1) {
    						offsetXText2 = offsetXText;
    					}
    					else {
    						if (data.length % 2 == 0) {
    							offsetXText2 = offsetXText + i * (globalParam.spaceHbetween2box + globalParam.boxWidth);
    						}
    						else {
    							offsetXText2 = offsetXText - (((data.length-1) / 2) - i) * (globalParam.spaceHbetween2box + globalParam.boxWidth);
    						}
    					}
    					
    					if (currentLevel > 0) {
    						drawArrow(renderer, offsetXText, offsetXText2, offsetYText, offsetYText2, globalParam);
    					}
    					
    					drawLabel(renderer, data[i], highLightCode, offsetXText2, offsetYText2, globalParam); 
    					
    					if (data[i].separatorLine) {
    						drawSeparatorLine(renderer, data[i], offsetYText2, globalParam); // old /2
    						hasSeparatorLine = true;
    					}
    					else {
    						hasSeparatorLine = false;
    					}
    					
    					if (data[i].children != undefined && data[i].children != null && data[i].children.length != 0) {
    						// call again renderChart for the new level (currentLevel+1)
    						renderChart(renderer, currentLevel+1, data[i].children, highLightCode, offsetXText2, offsetYText2, hasSeparatorLine, globalParam);  
    					}
    				}
    				
    			}
    			
    			function populateChart(chart) {
    				
    				 scope.$watch('modalDataConfig', function () { 
    					 var data;
    					 
    					 if (scope.modalDataConfig == "runStates") {
    	    	        		//hard coded list in order to conserve the order (we exclude status 'N');
    	    	        		data=[
    	    	        	              {code:'IP-S',name:'Séquençage en cours', separatorLine:false,  children : [ 
    	    	        	               {code:'F-S',name:'Séquençage terminé', separatorLine:true, children : [
    											{code:'IW-RG',name:'Read generation en attente', separatorLine:false, children : [
    											     {code:'IP-RG',name:'Read generation en cours', separatorLine:false, children : [
    											         {code:'F-RG',name:'Read generation terminée', separatorLine:true, children : [
    											             {code:'IW-V',name:'Evaluation en attente', separatorLine:false, children : [
    											                  {code:'IP-V',name:'Evaluation en cours', separatorLine:false, children : [
    											                       {code:'F-V',name:'Evaluation terminée', separatorLine:false}]}]}]}]}]}]}, 
    	    	        	               {code:'FE-S',name:'Séquençage en échec', separatorLine:false, specificColor:true} 
    	    	        	              ]} 
    	    	        	             ];
    	    	        	}
    	    	        	if (scope.modalDataConfig == "readSetStatesWithoutAnalysisBA") {
    	    	        		data=[
    	    	        		          {code:'IP-RG',name:'Read Generation en cours', separatorLine:false,  children : [ 
    	      	        	               {code:'F-RG',name:'Read Generation terminé', separatorLine:true, children : [
    	  										{code:'IW-QC',name:'Contrôle qualité en attente', separatorLine:false, children : [
    	  										     {code:'IP-QC',name:'Contrôle qualité en cours', separatorLine:false, children : [
    	  										         {code:'F-QC',name:'Contrôle qualité terminée', separatorLine:true, children : [
    	  										             {code:'IW-VQC',name:'EVAL. QC en attente', separatorLine:false, children : [
    	  										                  {code:'F-VQC',name:'EVAL. QC terminé', separatorLine:true, children : [
    	  										                       {code:'A',name:'Disponible', separatorLine:false}, 
    	  										                       {code:'UA',name:'Indisponible', separatorLine:false} ]}]}]}]}]}]}]}];
    	    	        	}
    	    	        	if (scope.modalDataConfig == "readSetStatesWithAnalysisBA") {
    	    	        		data=[
    	    	        		          {code:'IP-RG',name:'Read Generation en cours', separatorLine:false,  children : [ 
    	      	        	               {code:'F-RG',name:'Read Generation terminé', separatorLine:true, children : [
    	  										{code:'IW-QC',name:'Contrôle qualité en attente', separatorLine:false, children : [
    	  										     {code:'IP-QC',name:'Contrôle qualité en cours', separatorLine:false, children : [
    	  										         {code:'F-QC',name:'Contrôle qualité terminée', separatorLine:true, children : [
    	  										             {code:'IW-VQC',name:'EVAL. QC en attente', separatorLine:false, children : [
    	  										                  {code:'F-VQC',name:'EVAL. QC terminé', separatorLine:true, children : [
    	  										                       {code:'IW-BA',name:'Analyses BI en attente', separatorLine:false, children : [
    	                                                                   {code:'IP-BA',name:'Analyses BI en cours', separatorLine:false, children : [
    	                                                                        {code:'F-BA',name:'Analyses BI terminée', separatorLine:true, children : [
    	                                                                           {code:'IW-VBA',name:'EVAL. Analyses BI en attente', separatorLine:false, children : [
    	                                                                              {code:'F-VBA',name:'EVAL. Analyses BI terminé', separatorLine:true, children : [
    	                                                                                  {code:'A',name:'Disponible', separatorLine:false}, 
    	                                                                                  {code:'UA-2',name:'Indisponible', separatorLine:false} ]}]}]}]}] 
    	  										                       }, 
    	  										                       {code:'UA',name:'Indisponible', separatorLine:false} ]}]}]}]}]}]}]} ];

    	    	        	}
    	    	        	
    		    	        
    	    	        	if (scope.modalHistoricalData != undefined && scope.modalHistoricalData != null && scope.modalHistoricalData.length > 0) {
    		    	        	data = updateDataWithComment(data, scope.modalHistoricalData, 0);
    		    	        }



    	    	            // Draw the flow chart
    	    	            var globalParam = { spaceVbetween2box:20, 
    						    	            spaceHbetween2box:150,
    						    	            boxWidth:160,
    						    	            boxHeight:25, //memo : 25 for arial 9 : do not change (bug with height property)
    						    				offsetXText:100, //old 200
    						    	            offsetYText:32 };
    	    	            
    	    	            scope.$watch('modalHighlightCode', function () { 

    	    	            	renderChart(chart.renderer, 0, data, scope.modalHighlightCode, globalParam.offsetXText, globalParam.offsetYText, false, globalParam);
    	    	            });
    	   	            
    	    	            
    	    	            
    		            }, true);
    			} 
    	    	
    	    	function getFillColor(code1, code2) {
    				return (code1==code2?'#4BACC6':'#F2F2F2');
    			};
    			
    	    	function getBorderColor(code1, code2, specificColor) {
    	    		return (code1==code2?'#31859C':(specificColor===undefined?'#BFBFBF':'#D9D9D9'));
    			};
    			
    	    	function getFontColor(code1, code2, specificColor) {
    	    		return (code1==code2?'white':(specificColor===undefined?'black':'#A6A6A6'));
    			};
    			
    			
    			function updateDataWithComment(data, historical, currentLevel) {
    				for (var i=0; i<data.length; i++) {
    					for (var j=0; j<historical.length; j++) {
    						if (data[i].code == historical[j].code) {
    							data[i].comment = {label:historical[j].date,type:'datetime'};	
    							break;
    						} 
    					}
    					if (data[i].children != undefined && data[i].children != null && data[i].children.length != 0) {
    						updateDataWithComment(data[i].children, historical, currentLevel+1);
    					}
    				}	
    				return data;
    			};
    			 		
    	    } //end of linker
    	        
    	    return {
    	        restrict: "E",
    	        replace: false,
    	        link: linker,
    	        template: linkTemplate,
    	        transclude: false,
    	        scope: {modalCurrentCode: "=", modalHistoricalData: "=", modalDataConfig: "=", modalHighlightCode: "=" }
    	    };
    	    
    	}]);


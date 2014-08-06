"use strict";

angular.module('biWorkflowChartServices', []).
	directive('workflowChart', ['$compile', '$http', '$q', '$filter', 'lists', function ($compile, $http, $q, $filter, lists) {
    		
	    var modalTemplate = "<div id='{{modalId}}' class='modal'  tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true' style='left:100px; top:100px;overflow:hidden'>"+
	    										"<div class='modal-content' style='width:{{modalContentWidth}}px; height:{{modalContentHeight}}px; border:0px'>"+
	    											"<div class='modal-header'>"+
	    												"<h3 id='myModalChartLabel'>{{modalHeaderText}}</h3>" +
	    												"<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"+
	    											"</div>"+
	    											"<div class='modal-body' style='padding:0px'>"+
	    												"<div id='container0'></div>"+
	    											"</div>"+
	    										"</div>"+
	    									"</div>";
	    
	    var linkTemplate = "<a href='#{{modalId}}' id='linkTo{{modalId}}' role='button' data-toggle='modal' class='btn small_link_button'>{{modalCurrentCode | codes:'state'}}</a>";
	    
	    var linker = function (scope, element, attrs) {
	    	
	        scope.modalHeaderText = attrs.modalHeaderText;
	        scope.modalBodyText = attrs.modalBodyText;
	        scope.modalId = attrs.modalId || "modalWorflowChart";
	        scope.modalBgColor = attrs.modalBgColor;
	        scope.modalWidth = attrs.modalWidth;
	        scope.modalHeight = attrs.modalHeight;
	        scope.modalContentWidth = parseFloat(scope.modalWidth) + 100;
	        scope.modalContentHeight = parseFloat(scope.modalHeight) + 100;
	        scope.lists = lists;
	        
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
			
			
			function drawLabel(ren, data, offsetXText, offsetYText, globalParam) {
				ren.label(data.name, offsetXText, offsetYText)
                .attr({
                    fill: getFillColor(data.code, scope.modalCurrentCode),
                    stroke: getBorderColor(data.code, scope.modalCurrentCode, data.specificColor),
                    'stroke-width': 2,
                    padding: 5,
                    width: globalParam.boxWidth,
                    //height:globalParam.boxHeight,
                    r: 5
                })
                .css({
                    color: getFontColor(data.code, scope.modalCurrentCode, data.specificColor),
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
			
			
			function drawSeparatorLine(ren, offsetY, globalParam) {
				var offsetXLine = globalParam.offsetXText -10;
				var offsetYLine = offsetY;
				var offsetXLine2 = globalParam.offsetXText + globalParam.boxWidth+20;
				
                ren.path(['M', offsetXLine, offsetYLine, 'L', offsetXLine2, offsetYLine])
                .attr({
                    'stroke-width': 2,
                    stroke: 'silver',
                    dashstyle: 'dash'
                })
                .add();
			}
			
			
			function renderChart(renderer, data, globalParam) {
				
				var offsetXText = globalParam.offsetXText;
				var offsetYText = globalParam.offsetYText;				
				var offsetXText2 = offsetXText, offsetYText2 = offsetYText;    	

				for (var i=0; i<data.length; i++) {
					
					if (i > 0) {
						if (data[i].position != data[i-1].position) {
							offsetYText2 += globalParam.spaceVbetween2box + globalParam.boxHeight;
							
							if ((data[i].functionnalGroup != undefined) && (data[i].functionnalGroup != null) && (data[i].functionnalGroup != data[i-1].functionnalGroup)) {
								
								drawSeparatorLine(renderer, offsetYText2 - globalParam.spaceVbetween2box/4, globalParam); 
								
								offsetYText2 += globalParam.spaceVbetween2box / 2;
							}
							drawArrow(renderer, offsetXText, offsetXText2, offsetYText, offsetYText2, globalParam);
						}
						else {
							offsetXText2 = offsetXText + (globalParam.spaceHbetween2box + globalParam.boxWidth);
							
							drawArrow(renderer, offsetXText, offsetXText2, offsetYText -  globalParam.spaceVbetween2box - globalParam.boxHeight , offsetYText2, globalParam);
						}
						
					}
					
					drawLabel(renderer, data[i], offsetXText2, offsetYText2, globalParam); 
					
					offsetXText = offsetXText2;
					offsetYText = offsetYText2;
				}
				
			}
			
			function populateChart(chart) {
				
				 scope.$watch('modalData', function() { 
					
					 //get data
					var data = triData(scope.modalData); 
    	            
    	        	if (scope.modalHistoricalData != undefined && scope.modalHistoricalData != null && scope.modalHistoricalData.length > 0) {
	    	        	data = updateDataWithComment(data, scope.modalHistoricalData);
	    	        }
    	        	
    	            // Draw the flow chart
    	            var globalParam = { spaceVbetween2box:20, 
					    	            spaceHbetween2box:100,
					    	            boxWidth:160,
					    	            boxHeight:25, //memo : 25 for arial 9 : do not change (bug with height property)
					    				offsetXText:100, //old 200
					    	            offsetYText:32 };

	            	renderChart(chart.renderer, data, globalParam);
        
		        }, true);
				 
			} 
			
			
			function triData(data) {
				data.sort(function(a, b){return a.position-b.position});
				return data;
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
			
			
			function updateDataWithComment(data, historical) {
				for (var i=0; i<data.length; i++) {
					for (var j=0; j<historical.length; j++) {
						if (data[i].code == historical[j].code) {
							data[i].comment = {label:historical[j].date,type:'datetime'};	
							break;
						} 
					}
				}	
				return data;
			};
			 		
	    } //end of linker
	        
	    return {
	        restrict: "E",
	        replace: false,
	        link: linker,
	        template: modalTemplate + linkTemplate,
	        transclude: false,
	        scope: {modalCurrentCode: "=", modalHistoricalData: "=", modalData: "="},
	    };
	    
	}]);
"use strict";

angular.module('biWorkflowChartServices', []).
	directive('workflowChart', ['$compile', '$http', '$q', '$filter', function ($compile, $http, $q, $filter) {
    		
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
	        scope.modalId = attrs.modalId || "modalWorflowChart";
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
	      	        	               {code:'F-RG',name:'Read Generation terminée', separatorLine:true, children : [
	  										{code:'IW-QC',name:'Contrôle qualité en attente', separatorLine:false, children : [
	  										     {code:'IP-QC',name:'Contrôle qualité en cours', separatorLine:false, children : [
	  										         {code:'F-QC',name:'Contrôle qualité terminé', separatorLine:true, children : [
	  										             {code:'IW-VQC',name:'EVAL. QC en attente', separatorLine:false, children : [
	  										                  {code:'F-VQC',name:'EVAL. QC terminée', separatorLine:true, children : [
	  										                       {code:'A',name:'Disponible', separatorLine:false}, 
	  										                       {code:'UA',name:'Indisponible', separatorLine:false} ]}]}]}]}]}]}]}];
	    	        	}
	    	        	if (scope.modalDataConfig == "readSetStatesWithAnalysisBA") {
	    	        		data=[
	    	        		          {code:'IP-RG',name:'Read Generation en cours', separatorLine:false,  children : [ 
	      	        	               {code:'F-RG',name:'Read Generation terminée', separatorLine:true, children : [
	  										{code:'IW-QC',name:'Contrôle qualité en attente', separatorLine:false, children : [
	  										     {code:'IP-QC',name:'Contrôle qualité en cours', separatorLine:false, children : [
	  										         {code:'F-QC',name:'Contrôle qualité terminé', separatorLine:true, children : [
	  										             {code:'IW-VQC',name:'EVAL. QC en attente', separatorLine:false, children : [
	  										                  {code:'F-VQC',name:'EVAL. QC terminée', separatorLine:true, children : [
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
	    	        	

	    	            // Draw the flow chart
	    	            var globalParam = { spaceVbetween2box:20, 
						    	            spaceHbetween2box:100,
						    	            boxWidth:160,
						    	            boxHeight:25, //memo : 25 for arial 9 : do not change (bug with height property)
						    				offsetXText:100, //old 200
						    	            offsetYText:32 };
	    	            
	    	            scope.$watch('modalHighlightCode', function () {
	    	            	
    	    	        	if (scope.modalHistoricalData != undefined && scope.modalHistoricalData != null && scope.modalHistoricalData.length > 0) {
    		    	        	data = updateDataWithComment(data, scope.modalHistoricalData, scope.modalHighlightCode);
    		    	        }

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
			
			
			function updateHistoricalWithUA2(historical, highlightCode) {
				if (highlightCode == "UA-2") {
					for (var i=historical.length; i>1; i--) {
						if (historical[i-1].code == "UA") {
							historical[i-1].code = "UA-2";
							break;
						}
					}
				}
				return historical;
			};
			
			
			function updateDataWithComment(data, historical, highlightCode) {
				//to set date to the appropriate "unavailable" box (the second and not the first) 
				historical = updateHistoricalWithUA2(historical, highlightCode); 
				
				for (var i=0; i<data.length; i++) {
					for (var j=0; j<historical.length; j++) {
						if (data[i].code == historical[j].code) {
							data[i].comment = {label:historical[j].date,type:'datetime'};	
							break;
						} 
					}
					if (data[i].children != undefined && data[i].children != null && data[i].children.length != 0) {
						updateDataWithComment(data[i].children, historical, highlightCode);
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
	        scope: {modalCurrentCode: "=", modalHistoricalData: "=", modalDataConfig: "=", modalHighlightCode: "=" },
	    };
	    
	}]);
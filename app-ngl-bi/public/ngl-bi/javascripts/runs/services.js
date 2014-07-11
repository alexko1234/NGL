 "use strict";
 
 angular.module('ngl-bi.RunsServices', []).
	factory('searchService', ['$http', 'mainService', 'lists', function($http, mainService, lists){
		
		var searchService = {
				getColumns:function(){
					var columns = [
								    {  	property:"code",
								    	header: "runs.code",
								    	type :"String",
								    	order:true
									},
									{	property:"typeCode",
										header: "runs.typeCode",
										type :"String",
								    	order:true
									},
									{	property:"sequencingStartDate",
										header: "runs.sequencingStartDate",
										type :"Date",
								    	order:true
									},
									{	property:"state.code",
										filter:"codes:'state'",					
										header: "runs.stateCode",
										type :"String",
										edit:true,
										order:true,
										choiceInList:true,
								    	listStyle:'bt-select',
								    	possibleValues:'searchService.lists.getStates()'	
									},
									{	property:"valuation.valid",
										filter:"codes:'valuation'",					
										header: "runs.valuation.valid",
										type :"String",
								    	order:true
									},
									{	property:"valuation.resolutionCodes",
										header: "runs.valuation.resolutions",
										render:'<div bt-select ng-model="value.data.valuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
										type :"text",
										hide:true
									} 
								];						
					return columns;
				},
				isRouteParam:false,
				lists : lists,
				form : undefined,
				
				setRouteParams:function($routeParams){
					var count = 0;
					for(var p in $routeParams){
						count++;
						break;
					}
					if(count > 0){
						this.isRouteParam = true;
						this.form = $routeParams;
					}
				},
				
				updateForm : function(){
					if (mainService.isHomePage('valuation')) {
						if(!this.isRouteParam && (this.form.stateCodes === undefined || this.form.stateCodes.length === 0)) {
							//No stateCodes selected, the filter by default (on the only two possible states for the valuation) is applied
							this.form.stateCodes = ["IW-V","IP-V"];
						}		
					}
					this.form.excludes =  ["treatments","lanes"];				
				},
				convertForm : function(){
					var _form = angular.copy(this.form);
					if(_form.fromDate)_form.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
					if(_form.toDate)_form.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();		
					return _form
				},
				refreshSamples : function(){
					if(this.form.projectCodes && this.form.projectCodes.length > 0){
						this.lists.refresh.samples({projectCodes:this.form.projectCodes});
					}
				},
				
				search : function(datatable){
					this.updateForm();
					mainService.setForm(this.form);
					datatable.search(this.convertForm());
				},
				
				reset : function(){
					this.form = {};
				},
				
				states : function(){
					if (mainService.isHomePage('valuation')) {
						return [{code:"IW-V",name:Codes("state.IW-V")}];
					}else{
						return this.lists.get('statetrue');
					}
				}
		};
		
		return function(){
			
			searchService.lists.refresh.projects();
			searchService.lists.refresh.states({objectTypeCode:"Run", display:true},'statetrue');				
			searchService.lists.refresh.states({objectTypeCode:"Run"});							
			searchService.lists.refresh.types({objectTypeCode:"Run"});
			searchService.lists.refresh.resolutions({objectTypeCode:"Run"});
			searchService.lists.refresh.runs();
			searchService.lists.refresh.instruments({categoryCode:"seq-illumina"});
			searchService.lists.refresh.users();
			
			searchService.lists.refresh.valuationCriterias({objectTypeCode:"Run",orderBy:'name'});
			
			if(angular.isDefined(mainService.getForm())){
				searchService.form = mainService.getForm();
			}else{
				searchService.reset();
			}
			
			return searchService;		
		}
	}
]).directive('modalChartV2', ['$compile', '$http', '$q', 'lists', function ($compile, $http, $q, lists) {
	
	//use path to generate arrow instead of image (more flexibility : allow to specify color, line-width, curve)
	//use label instead of text (allow shadow and simplify code : no need BBox and rect)
	
    var modalTemplate = angular.element("<div id='{{modalId}}' class='modal'  tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true' style='left:100px; top:100px;overflow:hidden'>"+
    										"<div class='modal-content' style='width:800px; height:800px; border:0px'>"+
    											"<div class='modal-header'>"+
    												"<h3 id='myModalLabel'>{{modalHeaderText}}</h3>" +
    												"<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"+
    											"</div>"+
    											"<div class='modal-body' style='padding:0px'>"+
    												"<div id='container0'></div>"+
    											"</div>"+
    										"</div>"+
    									"</div>");
    
    var linkTemplate = "<a href='#{{modalId}}' id='linkTo{{modalId}}' role='button' data-toggle='modal' class='btn small_link_button'>{{state | codes:'state'}}</a>";  // not angular.element !
    
    var linker = function (scope, element, attrs) {
    	
        scope.modalHeaderText = attrs.modalHeaderText;
        scope.modalBodyText = attrs.modalBodyText;
        scope.modalId = attrs.modalId;
        scope.lists = lists;
        
        $compile(element.contents())(scope);
        var newTemplate = $compile(modalTemplate)(scope);

        $(newTemplate).appendTo('body');
    
        $("#" + scope.modalId).modal({
            backdrop: false,
            show: false
        });
        
		element.on('click', function(event) {
		    // Prevent default dragging of selected content
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
					backgroundColor: 'white',
					height: 600,
					width:600
				},	
				title : {text : scope.modalBodyText, style: {color: 'black'}}
			});
		 	return chart1;
		};
		
		function populateChart(chart) {
	        //$q.when(scope.lists.refresh.states({objectTypeCode:"Run"})).then(function(){ //on callback after refresh states list
        	//var states = scope.lists.getStates();
			
        	//hard coded list in order to conserve the order (we exclude status 'N');
        	var states = [{code:'IP-S',name:'Séquençage en cours'}, {code:'F-S',name:'Séquençage terminé'}, {code:'FE-S',name:'Séquençage en échec'}, {code:'IW-RG',name:'Read generation en attente'}, 
        	              {code:'IP-RG',name:'Read generation en cours'}, {code:'F-RG',name:'Read generation terminée'}, {code:'IW-V',name:'Evaluation en attente'}, 
        	              {code:'IP-V',name:'Evaluation en cours'}, {code:'F-V',name:'Evaluation terminée'}];
        	

            // Draw the flow chart
            var ren = chart.renderer;
            var offsetXText = 96; //left space
            var offsetYText = 30; // for title
            var spaceVbetween2box = 50;
            var boxHeight = 30;
            var offsetXArrow = 182;

			for (var i=0; i<states.length; i++) {
					
                ren.label(states[i].name, offsetXText, offsetYText+i*spaceVbetween2box)
                .attr({
                    fill: getColor(states[i].code, scope.state),
                    stroke: 'gray',
                    'stroke-width': 2,
                    padding: 5,
                    r: 5
                })
                .css({
                    color: 'blue',
                    fontStyle: '12px',
                    fontWeight: 'normal'
                })
                .add()
                .shadow(true);
                
		        if (i != states.length-1) {
		        	var startPosY = offsetYText+boxHeight;
                    ren.path(['M', offsetXArrow, startPosY, 'L', offsetXArrow,  startPosY+(spaceVbetween2box-boxHeight), //body arrow 
                              'L', offsetXArrow-5, startPosY+(spaceVbetween2box-boxHeight)-5, 'M', offsetXArrow,  startPosY+(spaceVbetween2box-boxHeight), //left side 
                              'L', offsetXArrow+5, startPosY+(spaceVbetween2box-boxHeight)-5, 'M', offsetXArrow,  startPosY+(spaceVbetween2box-30)]) //right side
                     .attr({
                         'stroke-width': 2,
                         stroke: 'darkgray'
                     })
                     .translate(0, i*spaceVbetween2box)
                     .add();
		        }
			}				
								
			//});	
		}
    	
    	function getColor(stateCode1, stateCode2) {
			return (stateCode1===stateCode2?'lightgreen':'lightgray');
		};
		 		
    } //end of linker
        
    return {
        restrict: "E",
        replace: false,
        link: linker,
        template: linkTemplate,
        transclude: false,
        scope: {state: "="}
    };
    
}]);
 
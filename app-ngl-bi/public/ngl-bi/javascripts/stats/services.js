 "use strict";
 
 angular.module('ngl-bi.StatsServices', []).
	factory('statsConfigReadSetsService', ['$http', '$filter', 'lists', 'datatable', function($http, $filter, lists, datatable){
		var datatableConfig = {
				search : {
					active:false
				},
				pagination:{
					active:false
				},
				remove:{
					active:true,
					mode:'local'
				},
				columns : [
							{
								   property:"column.header",
								   header: "stats.property",
								   type :"text",
								   order:true
							},
					           {	
					        	   property:"typeCode",
					        	   header: "stats.typeCode",
					        	   type :"text",		    	  	
					        	   order:true
					           }
				           
				]
		};
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				lists.refresh.reportConfigs({pageCodes:["readsets-addcolumns"]}, "readsets-addcolumns");				
				isInit=true;
			}
		};
		
		var statsService = {
				datatable:undefined,
				statsTypes : [{code:'z-score', name:Messages("stat.typelabel.zscore")},{code:'simple-value', name:Messages("stat.typelabel.simplevalue")}],
				statColumns:[],
				select : {
					typeCode:'z-score',
					properties:[]
				},
				getStatsTypes : function(){
					return this.statsTypes;//,{code:'histogram', name:Messages("stat.typelabel.histogram")}];
				},
				reset : function(){
					this.select =  {
							typeCode:'z-score',
							properties:[]
						};
				},
				add : function(){
					var data = [];
					for(var i = 0; i < this.select.properties.length; i++){
						for(var j = 0; j < this.statColumns.length; j++){
							if(this.select.properties[i] === this.statColumns[j].header){
								if(this.select.typeCode){
									data.push({typeCode : this.select.typeCode, column: this.statColumns[j]});
								}
							}
						}						
					}		
					this.datatable.addData(data);
					this.reset();
				},
				getData : function(){
					return this.datatable.getData();
				},
				isData : function(){
					return (this.datatable && this.datatable.getData().length > 0);
				},
				initStatColumns:function(){
					if(lists.get("readsets-addcolumns") && lists.get("readsets-addcolumns").length === 1){
						this.statColumns = $filter('filter')(lists.get("readsets-addcolumns")[0].columns,{modes:"chart"});
					}
				},
				getStatColumns : function(){
					if(this.statColumns.length === 0){
						this.initStatColumns();
					}
					return this.statColumns;									
				},
				init : function(){
					initListService();
					this.datatable= datatable(datatableConfig);
					this.datatable.setData([], 0);
				}
		};
		
		return statsService;				
	}
		
	
]).
factory('queriesConfigReadSetsService', ['$http', '$q', 'datatable', function($http, $q, datatable){
	var datatableConfig = {
			search : {
				active:false
			},
			pagination:{
				mode:'local'
			},
			remove:{
				active:true,
				mode:'local',
				callback:function(datatable){
					mainService.getBasket().reset();
					mainService.getBasket().add(datatable.getData());
				}
			},
			columns : [
			           {	
			        	   property:"title",
			        	   header: "query.title",
			        	   type :"text",		    	  	
			        	   order:true
			           },
			           {	
			        	   property:"nbResults",
			        	   header: "query.nbResults",
			        	   type :"number",		    	  	
			        	   order:true
			           },
			           {
			        	   property:"form",
			        	   render:function(v){
			        		 return JSON.stringify(v.form);  
			        	   },
			        	   header: "query.form",
			        	   type :"text"
			           }
			]
	};
	
	var url = jsRoutes.controllers.readsets.api.ReadSets.list().url;
	
	var updateResultQueries = function(queries){
		var promises = [];
		for(var i = 0; i < queries.length ; i++){
			var form = angular.copy(queries[i].form);
			form.count = true;
			promises.push($http.get(url,{params:form, query:queries[i]}));			
		}
		return promises;
	}
	
	
	var queriesService = {
			datatable:undefined,
			getData : function(){
				return this.datatable.getData();
			},
			isData : function(){
				return (this.datatable && this.datatable.getData().length > 0);
			},
			init:function(queries){
				this.datatable = datatable(datatableConfig);
				if(queries && queries.length > 0){
					$q.all(updateResultQueries(queries)).then(function(results){
						angular.forEach(results, function(value, key){
							value.config.query.nbResults = value.data;																
						});	
						queriesService.datatable.setData(queries, queries.length);
					});	
										
				} else {
					queriesService.datatable.setData([], 0);
				}
			}
	};
	
	return queriesService;		
}
	

]).factory('chartsReadSetsService', ['$http', '$q','$parse', 'datatable', 'statsConfigReadSetsService','queriesConfigReadSetsService', 
                                     function($http, $q, $parse, datatable, statsConfigReadSetsService, queriesConfigReadSetsService){
	
	var datatableConfig = {
			search : {
				active:false
			},
			pagination:{
				mode:'local'
			},
			order:{
				mode:'local',
				orderBy:'code',
				callback:function(datatable){
					computeCharts();
				}
			},
			hide:{
				active:true
			}
	};
	var defaultDatatableColumns = [
			{	property:"code",
			  	header: "readsets.code",
			  	type :"text",		    	  	
			  	order:true,
			  	position:1
			},
			{	property:"runCode",
				header: "readsets.runCode",
				type :"text",
				order:true,
			  	position:2
			},
			{	property:"laneNumber",
				header: "readsets.laneNumber",
				type :"text",
				order:true,
			  	position:3
			},
			{	property:"projectCode",
				header: "readsets.projectCode",
				type :"text",
				order:true,
			  	position:4
			},
			{	property:"sampleCode",
				header: "readsets.sampleCode",
				type :"text",
				order:true,
			  	position:5
		  	},
		  	{	property:"runSequencingStartDate",
				header: "runs.sequencingStartDate",
				type :"date",
				order:true,
			  	position:6
		  	},
		  	{	property:"state.code",
				filter:"codes:'state'",
				header: "readsets.stateCode",
				type :"text",
				order:true,
			  	position:7
			},
		 	{	property:"productionValuation.valid",
				filter:"codes:'valuation'",
				header: "readsets.productionValuation.valid",
				type :"text",
		    	order:true,
			  	position:70
			},
			{	property:"productionValuation.resolutionCodes",
				header: "readsets.productionValuation.resolutions",
				render:'<div bt-select ng-model="value.data.productionValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
				type :"text",
				hide:true,
			  	position:72
			},
			{	property:"bioinformaticValuation.valid",
				filter:"codes:'valuation'",
				header: "readsets.bioinformaticValuation.valid",
				type :"text",
		    	order:true,
			  	position:80
			},
			{	property:"bioinformaticValuation.resolutionCodes",
				header: "readsets.bioinformaticValuation.resolutions",
				render:'<div bt-select ng-model="value.data.bioinformaticValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
				type :"text",
				hide:true,
			  	position:82
			}];
	var readsetDatatable;
	var charts = [];
	var statsConfigs, queriesConfigs = [];
	var loadData = function(){
		if(statsConfigReadSetsService.isData() && queriesConfigReadSetsService.isData()){
			statsConfigs =  	statsConfigReadSetsService.getData();
			queriesConfigs =  queriesConfigReadSetsService.getData();
			var properties = ["default"];
			for(var i = 0; i < statsConfigs.length; i++){
				properties.push(statsConfigs[i].column.property);
			}
			
			var promises = [];
			for(var i = 0; i < queriesConfigs.length ; i++){
				var form = angular.copy(queriesConfigs[i].form);
				form.list = true;
				form.includes = properties;
				//form.limit=3000;
				promises.push($http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url,{params:form}));			
			}
			
			$q.all(promises).then(function(results){
				var values = {r:[]};
				angular.forEach(results, function(value, key){
					this.r = this.r.concat(value.data);
					
				}, values);	
				var data = values.r;
				readsetDatatable = datatable(datatableConfig);
				readsetDatatable.setColumnsConfig(defaultDatatableColumns.concat(statsConfigs.map(function(statsConfig){
					statsConfig.column.order=true;
					return statsConfig.column;
					})))
				readsetDatatable.setData(data, data.length);
				computeCharts();
				
			});			
		}		
	}
	
	var computeCharts = function(){
		charts = [];
		for(var i = 0; i < statsConfigs.length; i++){
			var statsConfig = statsConfigs[i];
			if("z-score" === statsConfig.typeCode){
				charts.push(getZScoreChart(statsConfig));
			}else if("simple-value" === statsConfig.typeCode){
				charts.push(getSimpleValueChart(statsConfig));
			}else{
				throw 'not manage'+statsConfig.typeCode;
			}
		}		
	};
	
	var getProperty = function(column){
		if(column.property){
			var p = column.property
			if(column.filter){
				p += '|'+column.filter;
			}
			//TODO format
			return p;
		}else{
			throw 'no property defined for column '+Messages(column.header);
		}
	};
	
	var getZScoreChart = function(statsConfig){
		var property = getProperty(statsConfig.column);
		var data = readsetDatatable.getData();
		var statData = data.map(function(value){return $parse(property)(value)});
		var mean = ss.mean(statData);
		var stdDev = ss.standard_deviation(statData);
		var zscodeData = data.map(function(x){return {name:x.code,  y:ss.z_score($parse(property)(x), mean, stdDev), _value:$parse(property)(x)};});
		
		var chart = {
		        chart: {
		        	zoomType : 'x',
		        	height:770
		        },
		        title: {
		        	text : 'z-score : ' + Messages(statsConfig.column.header)
		        },
		        tooltip: {
		        	formatter: function() {
		                var s = '<b>'+ this.point.name +'</b>';
		                	s += '<br/>'+ this.point.series.name +': '+ this.point.y ;
		                    s += '<br/>'+Messages(statsConfig.column.header)+': '+ this.point._value ;		                
		                return s;
		            }   
		        },
		        xAxis: {
		        	title: {
		        		text: 'Readsets',
		        	}, 
		        	labels : {
						enabled : false,
						rotation : -75
					},
					type: "category",
					tickPixelInterval:1
		        },
		        yAxis: {
		            title: {
		                text: 'z-score'
		            },
		            tickInterval : 2,
		            plotLines : [ {
						value : -2,
						color : 'green',
						dashStyle : 'shortdash',
						width : 2,
						label : {
							text : 'z-score = -2'
						}
					}, {
						value : 2,
						color : 'red',
						dashStyle : 'shortdash',
						width : 2,
						label : {
							text : 'z-score = 2'
						}
					} ]
		        },
		        series: [{type : 'column', name:'z-score', data:zscodeData, turboThreshold:0}]
		    };
		return chart;
	};
	
	var getSimpleValueChart = function(statsConfig){
		var property = getProperty(statsConfig.column);
		var data = readsetDatatable.getData();
		var statData = data.map(function(x){return {name:x.code,  y:$parse(property)(x)};});
		
		var chart = {
		        chart: {
		        	zoomType : 'x',
		        	height:770
		        },
		        title: {
		        	text : Messages(statsConfig.column.header)
		        },
		        tooltip: {
		        	formatter: function() {
		                var s = '<b>'+ this.point.name +'</b>';
		                	s += '<br/>'+ this.point.series.name +': '+ this.point.y ;		                    	                
		                return s;
		            }   
		        },
		        xAxis: {
		        	title: {
		        		text: 'Readsets',
		        	}, 
		        	labels : {
						enabled : false,
						rotation : -75
					},
					type: "category",
					tickPixelInterval:1
		        },
		        yAxis: {
		            title: {
		                text: Messages(statsConfig.column.header)
		            }
		        },
		        series: [{type : 'column', name:Messages(statsConfig.column.header), data:statData, turboThreshold:0}]
		    };
		return chart;
	}
	
	var chartService = {
			datatable:function(){return readsetDatatable},
			charts:function(){return charts},
			init:function(){
				loadData();
			}	
	};
	
	return chartService;
	
}]);
 
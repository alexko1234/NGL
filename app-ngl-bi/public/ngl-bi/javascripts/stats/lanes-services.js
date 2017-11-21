"use strict";

angular.module('ngl-bi.LanesStatsServices', []).
factory('statsConfigLanesService', ['$http', '$filter', 'lists', 'datatable', function($http, $filter, lists, datatable){
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
					"property":"column.header",
					"header": Messages("stats.property"),
					"type" :"text",
					"order":true
				}
				]
	};

	var isInit = false;


	var statsService = {
			lists : lists,
			treatmentType:undefined,
			properties:[],
			property:undefined,
			//statColumns:[{header:'SAV Q30', code:'sav.q30',name:'Q30',property:'lanes.treatments.sav', value:'greaterQ30Perc',treatment:'sav'}],
			select : {
				properties:[]
			},
			
			
			reset : function(){
				this.select =  {
						properties:[]
				};
			},
			
			getTreatmentType : function(){
				return this.treatmentType;
			},
			getProperty : function(){
				return this.property;
			},
			isData : function(){
				if(this.treatmentType!=undefined && this.property!=undefined){
					return true;
				}else{
					return false;
				}
			},
			getData : function(){
				var statConfig = {header:this.treatmentType+' '+this.property, code:this.treatmentType+'.'+this.property,property:'lanes.treatments.'+this.treatmentType,value:this.property,treatment:this.treatmentType};
				return statConfig;
			},
			refreshProperty:function()
			{
				if(this.treatmentType!=undefined){
					$http.get(jsRoutes.controllers.treatmenttypes.api.TreatmentTypes.get(this.treatmentType).url,{params:{levels:"Lane"}}).success(function(data) {
						statsService.properties=data.propertiesDefinitions;
					});
				};
			},
			initListService : function(){
				if(!isInit){
					lists.refresh.treatmentTypes({levels:'Lane'});
					isInit=true;
				};
			},
			init : function(){
				this.datatable= datatable(datatableConfig);
				this.datatable.setData([], 0);
				this.initListService();
			}
	};
	if(!isInit){
		statsService.init();
	}
	return statsService;				
}]).factory('chartsLanesService', ['$http', '$q','$parse', '$window', '$filter', 'datatable', 'statsConfigLanesService','queriesConfigReadSetsService', 'lists', 'mainService',
	function($http, $q, $parse, $window, $filter, datatable, statsConfigLanesService, queriesConfigReadSetsService, lists, mainService){

	var datatableConfig = {
			group : {
				active : true,
				callback:function(datatable){
					computeChart();
				}
			},
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
					computeChart();
				}
			},
			hide:{
				active:true
			}
	};
	var defaultDatatableColumns = [
		{  	"property":"code",
			"header": Messages("runs.code"),
			"type" :"text",
			"order":true,
			"position":1,
			"groupMethod":"count:true",
		},
		{	"property":"typeCode",
			"header": Messages("runs.typeCode"),
			"type" :"text",
			"order":true,
			"group":true,
			"position":2
		},
		{	"property":"sequencingStartDate",
			"header": Messages("runs.sequencingStartDate"),
			"type" :"date",
			"order":true,
			"position":3
		},
		{	"property":"state.historical|filter:'F-RG'|get:'date'",
			"header": Messages("runs.endOfRG"),
			"type" :"date",
			"order":true,
			"position":4
		},
		{	"property":"state.code",
			"filter":"codes:'state'",					
			"header": Messages("runs.stateCode"),
			"type" :"text",
			"edit":true,
			"order":true,
			"choiceInList":true,
			"listStyle":'bt-select',
			"possibleValues":'searchService.lists.getStates()',
			"position":5	
		},
		{	"property":"valuation.valid",
			"filter":"codes:'valuation'",					
			"header": Messages("runs.valuation.valid"),
			"type" :"text",
			"order":true,
			"position":100
		},
		{	"property":"valuation.resolutionCodes",
			"header": Messages("runs.valuation.resolutions"),
			"render":'<div bt-select ng-model="value.data.valuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
			"type" :"text",
			"hide":true,
			"position":101
		} ,
		{  	"property":"lanes",
			"render": function(value){
			if(angular.isDefined(value) && angular.isDefined(value.lanes)){
				var display = "";
				var treatment = statsConfigs.treatment;
				var valueColumn = statsConfigs.value;
				display+="<table class=\"table table-condensed table-hover table-bordered\">";
				display+="<thead>";
				display+="<tr>";
				display+="<th>Property</th>";
				for(var l=0; l<value.lanes.length; l++){
					var nbLane = value.lanes[l].number;
					display+="<th>Lane "+nbLane+"</th>";
				}
				display+="</tr></thead><tbody>";
				var mapData = getDataLane(value.lanes,treatment,valueColumn);
				for(var key of mapData.keys()){
					display += "<tr><td>"+key+"</td>";
					var tabData = mapData.get(key);
					for(var t=0; t<tabData.length; t++){
						display+="<td>{{"+tabData[t]+"|number:2}}</td>";
					}
					display+="</tr>";
				}
				display +="</tbody></table>";
				return display;
			}
				
				
		},
    	"header": Messages("stats.property"),
    	"type":"text",
    	"order":false
		}
		];	
	
	var getDataLane = function(lanes, treatment,valueColumn){
		var mapData = new Map();
		for(var l=0; l<lanes.length; l++){
			if(lanes[l].treatments[treatment] !=null){
				if(lanes[l].treatments[treatment].read1!=null && lanes[l].treatments[treatment].read1[valueColumn]!=null){
					var tabValue = [];
					if(mapData.get("read1")!=undefined){
						tabValue=mapData.get("read1");
					}
					tabValue.push(lanes[l].treatments[treatment].read1[valueColumn].value);
					mapData.set("read1",tabValue);
				}
				if(lanes[l].treatments[treatment].read2!=null && lanes[l].treatments[treatment].read2[valueColumn]!=null){
					var tabValue = [];
					if(mapData.get("read2")!=undefined){
						tabValue=mapData.get("read2");
					}
					tabValue.push(lanes[l].treatments[treatment].read2[valueColumn].value);
					mapData.set("read2",tabValue);
				}
				if(lanes[l].treatments[treatment].default!=null && lanes[l].treatments[treatment].default[valueColumn]!=null){
					var tabValue = [];
					if(mapData.get("default")!=undefined){
						tabValue=mapData.get("default");
					}
					tabValue.push(lanes[l].treatments[treatment].default[valueColumn].value);
					mapData.set("default",tabValue);
				}
			}
		}
		return mapData;
	}
	var queriesConfigs = [];
	var readsetDatatable;
	var charts = [];
	var statsConfigs;
	var propertyGroupGetter;

	var generateCharts = function() {
		readsetDatatable = datatable(datatableConfig);
		readsetDatatable.config.spinner.start = true;

		var properties = ["default"];
		var propExistingFiels = [];
		properties.push(statsConfigs.property);	
		propExistingFiels.push(statsConfigs.property);	
		properties.push("lanes.number");

		var promises = [];
		for(var i = 0; i < queriesConfigs.length ; i++){
			var form = angular.copy(queriesConfigs[i].form);
			form.includes = properties;
			form.existingFields = propExistingFiels;
			promises.push($http.get(jsRoutes.controllers.runs.api.Runs.list().url,{params:form}));			
		}

		$q.all(promises).then(function(results){
			var values = {r:[]};
			angular.forEach(results, function(value, key){
				this.r = this.r.concat(value.data);

			}, values);	
			var data = values.r;
			readsetDatatable = datatable(datatableConfig);
			readsetDatatable.setColumnsConfig(defaultDatatableColumns);

			readsetDatatable.setData(data, data.length);
			readsetDatatable.config.spinner.start = false;
			computeChart();

		});
	};	

	var computeChart = function() {	
		var data = readsetDatatable.getData();
		//compute data
		charts = [];
		propertyGroupGetter = undefined;
		if(readsetDatatable.config.group.by != undefined){
			propertyGroupGetter = readsetDatatable.config.group.by.property;
		}
		
		var mapSeriesLane = undefined;
		if(propertyGroupGetter!=undefined){
			mapSeriesLane = computeDataGroup(data, propertyGroupGetter);
		}else{
			mapSeriesLane = computeData(data);
		}
		
		for(var key of mapSeriesLane.keys()){
			if(propertyGroupGetter!=undefined){
				charts.push(getChartGroup(mapSeriesLane.get(key),propertyGroupGetter));
			}else{
				charts.push(getChart(mapSeriesLane.get(key)));
			}
		}
		console.log(charts);
	};
	
	
	var computeData = function(dataRun)
	{
		var treatment = statsConfigs.treatment;
		var value = statsConfigs.value;
		var dataSeries = new Map();
		var newData = [];
		for(var i=0; i<dataRun.length; i++){
			//get run code
			var runCode = dataRun[i].code;
			if(dataRun[i].lanes !=null){
				for(var l=0; l<dataRun[i].lanes.length; l++){
					var nbLane = dataRun[i].lanes[l].number;
					var dataLane ={
						laneNumber:nbLane,
						dataRead1:[],
						dataRead2:[],
						dataDefault:[],
					};
					
					var mapGroup = new Map();
					
					if(dataSeries.get(nbLane)!=null){
						dataLane=dataSeries.get(nbLane);
					}
					if(dataRun[i].lanes[l].treatments[treatment] !=null){
						if(dataRun[i].lanes[l].treatments[treatment].read1!=null && dataRun[i].lanes[l].treatments[treatment].read1[value]!=null){
							dataLane.dataRead1.push({'name':runCode,'x':i,'y':dataRun[i].lanes[l].treatments[treatment].read1[value].value,'marker':{'symbol':'triangle'}});
						}
						if(dataRun[i].lanes[l].treatments[treatment].read2!=null && dataRun[i].lanes[l].treatments[treatment].read2[value]!=null){
							dataLane.dataRead2.push({'name':runCode,'x':i,'y':dataRun[i].lanes[l].treatments[treatment].read2[value].value,'marker':{'symbol':'square'}});
						}
						if(dataRun[i].lanes[l].treatments[treatment].default!=null && dataRun[i].lanes[l].treatments[treatment].default[value]!=null){
							dataLane.dataDefault.push({'name':runCode,'x':i,'y':dataRun[i].lanes[l].treatments[treatment].default[value].value,'marker':{'symbol':'diamond'}});
						}
						
					}
					dataSeries.set(nbLane,dataLane);
				}
			}
		}
		return dataSeries;
	};
	
	var computeDataGroup = function(dataRun, propertyGroup)
	{
		var treatment = statsConfigs.treatment;
		var value = statsConfigs.value;
		var dataSeries = new Map();
		var newData = [];
		for(var i=0; i<dataRun.length; i++){
			//get run code
			var runCode = dataRun[i].code;
			var group = dataRun[i][propertyGroup];
			
			if(dataRun[i].lanes !=null){
				for(var l=0; l<dataRun[i].lanes.length; l++){
					var nbLane = dataRun[i].lanes[l].number;
					var dataLane ={
						laneNumber:nbLane,
						groupValue:group,
						dataGroup:[],
					};
					
					var mapGroup = new Map();
					if(dataSeries.get(nbLane)!=null){
						mapGroup = dataSeries.get(nbLane);
						if(mapGroup.get(group)!=null){
							dataLane=mapGroup.get(group);
						}
					}
					
					if(dataRun[i].lanes[l].treatments[treatment] !=null){
						if(dataRun[i].lanes[l].treatments[treatment].read1!=null && dataRun[i].lanes[l].treatments[treatment].read1[value]!=null){
							dataLane.dataGroup.push({'name':runCode,'x':i,'y':dataRun[i].lanes[l].treatments[treatment].read1[value].value,'marker':{'symbol':'triangle'},'_group':'read1'});
						}
						if(dataRun[i].lanes[l].treatments[treatment].read2!=null && dataRun[i].lanes[l].treatments[treatment].read2[value]!=null){
							dataLane.dataGroup.push({'name':runCode,'x':i,'y':dataRun[i].lanes[l].treatments[treatment].read2[value].value,'marker':{'symbol':'square'},'_group':'read2'});
						}
						if(dataRun[i].lanes[l].treatments[treatment].default!=null && dataRun[i].lanes[l].treatments[treatment].default[value]!=null){
							dataLane.dataGroup.push({'name':runCode,'x':i,'y':dataRun[i].lanes[l].treatments[treatment].default[value].value,'marker':{'symbol':'diamond'},'_group':'default'});
						}
						
					}
						mapGroup.set(group,dataLane);
						dataSeries.set(nbLane,mapGroup);
				}
			}
		}
		return dataSeries;
	};
	
	var getChart = function(dataLane) {
		
		var allSeries = [];
		
		if(dataLane.dataRead1.length>0){
			allSeries.push({name:'read1',data:dataLane.dataRead1});
		}
		if(dataLane.dataRead2.length>0){
			allSeries.push({name:'read2',data:dataLane.dataRead2});
		}
		if(dataLane.dataDefault.length>0){
			allSeries.push({name:'default',data:dataLane.dataDefault});
		}
		
		var chart = {

			chart : {
				type: 'scatter',
				zoomType : 'x',
				height : 770
			},
			title : {
				text : statsConfigs.header+' Lane '+dataLane.laneNumber,
			},
			xAxis : {
				title : {
					text : 'RunCode',
				},
				type : "category",
				tickPixelInterval : 1
			},

			yAxis : {
				title : {
					text :  statsConfigs.name
				},
				tickInterval : 2,
			},
			plotOptions: {
		        scatter: {
		            marker: {
		                radius: 3,
		                states: {
		                    hover: {
		                        enabled: true,
		                        lineColor: 'rgb(100,100,100)'
		                    }
		                }
		            },
		            states: {
		                hover: {
		                    marker: {
		                        enabled: false
		                    }
		                }
		            },
		            tooltip: {
		                headerFormat: '<b>{series.name} </b><br>',
		                pointFormat: '{point.y}'
		            }
		        }
		    },
			series : allSeries,
		}
		return chart;
	};


	var getChartGroup = function(dataLane,propertyGroup) {
		
		var allSeries = [];
		
		for(var key of dataLane.keys()){
			var data = dataLane.get(key);
			allSeries.push({name:key,data:data.dataGroup});
		}
		
		var chart = {

			chart : {
				type: 'scatter',
				zoomType : 'x',
				height : 770
			},
			title : {
				text : statsConfigs.header+' Lane '+dataLane.laneNumber,
			},
			xAxis : {
				title : {
					text : 'Run Code',
				},
				type : "category",
				tickPixelInterval : 1
			},

			yAxis : {
				title : {
					text :  statsConfigs.name
				},
				tickInterval : 2,
				min : 0,
			},
		    plotOptions: {
		        scatter: {
		            marker: {
		                radius: 3,
		                states: {
		                    hover: {
		                        enabled: true,
		                        lineColor: 'rgb(100,100,100)'
		                    }
		                }
		            },
		            states: {
		                hover: {
		                    marker: {
		                        enabled: false
		                    }
		                }
		            },
		            tooltip: {
		                headerFormat: '<b>{series.name} </b><br>',
		                pointFormat: '{point.y} ({point._group})'
		            }
		        }
		    },
			series :allSeries
		}
		return chart;
	};
	
	var loadData = function() {
		if(statsConfigLanesService.isData() && queriesConfigReadSetsService.queries.length > 0){
			statsConfigs = statsConfigLanesService.getData();
			queriesConfigs = queriesConfigReadSetsService.queries;
			generateCharts();
		}else{
			statsConfigs=undefined;
			queriesConfigs=[];
			charts=[];
			readsetDatatable=undefined;
		}
	};

	var chartService = {
			datatable : function() {return readsetDatatable;},

			init : function() {
				loadData();
			},
			queries:function() {
				return queriesConfigs;
			},
			charts : function() {
				return charts;
			}
	};
	return chartService;
}]);

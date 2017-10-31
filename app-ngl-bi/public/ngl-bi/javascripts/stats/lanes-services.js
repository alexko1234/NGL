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
			datatable:undefined,
			statColumns:[{header:'SAV Q30', code:'sav.q30',name:'Q30',property:'lanes.treatments.sav'}],
			select : {
				properties:[]
			},
			reset : function(){
				this.select =  {
						properties:[]
				};
			},
			add : function(){
				var data = [];
				for(var i = 0; i < this.select.properties.length; i++){
					for(var j = 0; j < this.statColumns.length; j++){
						if(this.select.properties[i] === this.statColumns[j].header){
							data.push({column: this.statColumns[j]});
						}
					}						
				}		
				this.datatable.addData(data);
				this.reset();
			},
			setData : function(values){
				this.datatable.setData(values);
			},
			getData : function(){
				return this.datatable.getData();
			},
			isData : function(){
				return (this.datatable && this.datatable.getData().length > 0);
			},
			getStatColumns : function(){
				return this.statColumns;									
			},
			init : function(){
				this.datatable= datatable(datatableConfig);
				this.datatable.setData([], 0);
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
					computeCharts();
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
					computeCharts();
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
		} 
		];	
	var statsConfigs, queriesConfigs = [];
	var readsetDatatable;
	var charts = [];

	var generateCharts = function() {
		readsetDatatable = datatable(datatableConfig);
		readsetDatatable.config.spinner.start = true;

		var properties = ["default"];
		for(var i = 0; i < statsConfigs.length; i++){
			properties.push(statsConfigs[i].column.property);			
		}
		properties.push("lanes.number");

		var promises = [];
		for(var i = 0; i < queriesConfigs.length ; i++){
			var form = angular.copy(queriesConfigs[i].form);
			form.includes = properties;
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
		var mapSeriesLane = computeData(data);
		for(var key of mapSeriesLane.keys()){
			charts.push(getChart(mapSeriesLane.get(key)));
		}
		
	};
	
	var computeData = function(dataRun)
	{
		
		var dataSeries = new Map();
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
						existRead1Value:false,
						existRead2Value:false,
						existDefaultValue:false
					};
					if(dataSeries.get(nbLane)!=null){
						dataLane=dataSeries.get(nbLane);
					}
					
					
					if(dataRun[i].lanes[l].treatments.sav !=null){
						if(dataRun[i].lanes[l].treatments.sav.read1!=null && dataRun[i].lanes[l].treatments.sav.read1.greaterQ30Perc!=null){
							dataLane.dataRead1.push([runCode,dataRun[i].lanes[l].treatments.sav.read1.greaterQ30Perc.value]);
							dataLane.existRead1Value=true;
						}else{
							dataLane.dataRead1.push([runCode,0]);
						}
						if(dataRun[i].lanes[l].treatments.sav.read2!=null && dataRun[i].lanes[l].treatments.sav.read2.greaterQ30Perc!=null){
							dataLane.dataRead2.push([runCode,dataRun[i].lanes[l].treatments.sav.read2.greaterQ30Perc.value]);
							dataLane.existRead2Value=true;
						}else{
							dataLane.dataRead2.push([runCode,0]);
						}
						if(dataRun[i].lanes[l].treatments.sav.default!=null && dataRun[i].lanes[l].treatments.sav.default.greaterQ30Perc!=null){
							dataLane.dataDefault.push([runCode,dataRun[i].lanes[l].treatments.sav.default.greaterQ30Perc.value]);
							dataLane.existDefaultValue=true;
						}else{
							dataLane.dataDefault.push([runCode,0]);
						}
						
					}
					dataSeries.set(nbLane,dataLane);
				}
			}
		}
		return dataSeries;
	};
	
	var getChart = function(dataLane) {
		
		var allSeries = [];
		
		if(dataLane.existRead1Value){
			allSeries.push({name:'read1',data:dataLane.dataRead1,lineWidth:0,states:{hover:{lineWidthPlus: 0}}});
		}
		if(dataLane.existRead2Value){
			allSeries.push({name:'read2',data:dataLane.dataRead2,lineWidth:0,states:{hover:{lineWidthPlus: 0}}});
		}
		if(dataLane.existDefaultValue){
			allSeries.push({name:'default',data:dataLane.dataDefault,lineWidth:0,states:{hover:{lineWidthPlus: 0}}});
		}
		
		var chart = {

			chart : {
				zoomType : 'x',
				height : 770
			},
			title : {
				text : 'Q30 Value for Lane '+dataLane.laneNumber,
			},
			xAxis : {
				title : {
					text : 'RunCode',
				},
				type : "category",
			},

			yAxis : {
				title : {
					text : 'Q30'
				},
				min : 0
			},
			series : allSeries,
		}
		return chart;
	};


	var loadData = function() {
		if(statsConfigLanesService.isData() && queriesConfigReadSetsService.queries.length > 0){
			statsConfigs = statsConfigLanesService.getData();
			queriesConfigs = queriesConfigReadSetsService.queries;
			generateCharts();
		}else{
			statsConfigs=[];
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

 "use strict";
 
 angular.module('ngl-bi.BalanceSheetsService', []).
 factory('balanceSheetsSrv', ['$http', 'mainService', 'datatable', '$parse',
                                    function($http, mainService, datatable, $parse){
	 
	 // From DB
	 var readsets = [];
	 var runs = [];
	 var projects = [];
	 var yearlyProjects = [];
	 
	 // Charts
	 var chartSequencingProduction;
	 var chartFirstTen;
	 var chartQuarters;
	 var chartProjectType;
	 
	 // Datatables
	 var dtSequencingProduction;
	 var dtFirstTen;
	 var dtQuarters;
	 var dtProjectType;
	 
	 // Data
	 var dataSequencing;
	 var dataFirstTen;
	 var dataQuarters;
	 var dataProjectType;
	 
	 // Others
	 var selectedYear = 0;
	 var actualYear = new Date().getFullYear();
	 var stillLoading = true;
	 var total = 0;
	 
			 
	 var loadData = function(year){
		 stillLoading = true;
		 flushData();
		 selectedYear = year;

		 // We initialize our form
		 var form = {includes : []};
		 form.includes.push("treatments.ngsrg.default.nbBases");
		 form.includes.push("projectCode");
		 form.includes.push("runTypeCode");
		 form.includes.push("runSequencingStartDate");
		 form.includes.push("sampleOnContainer.sampleTypeCode");
		 form.fromDate = moment("01/01/"+selectedYear, Messages("date.format").toUpperCase()).valueOf();
		 form.toDate = moment("31/12/"+selectedYear, Messages("date.format").toUpperCase()).valueOf();
		 form.limit = 20000;
		 
		 var runForm = {includes : []};
		 runForm.includes.push("instrumentUsed.typeCode");
		 runForm.includes.push("sequencingStartDate");
		 runForm.includes.push("typeCode");
		 runForm.fromDate = moment("01/01/"+selectedYear, Messages("date.format").toUpperCase()).valueOf();
		 runForm.toDate = moment("31/12/"+selectedYear, Messages("date.format").toUpperCase()).valueOf();
		 
		 var projectForm = {includes : []};
		 projectForm.includes.push("code");
		 projectForm.includes.push("name");
		 projectForm.includes.push("traceInformation.creationDate");
		 
		 // We retrieve everything we need
		 
		 $http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url, {params : form}).success(function(data, status, headers, config) {
			 stillLoading = true;
			 readsets = data;
			 data = [];
			 for(var i = 0; i < readsets.length; i++){
				 readsets[i].runSequencingStartDate = convertToDate(readsets[i].runSequencingStartDate);
				 total += readsets[i].treatments.ngsrg.default.nbBases.value;
			 }	
			 $http.get(jsRoutes.controllers.runs.api.Runs.list().url, {params : runForm}).success(function(runData, status, headers, config) {
				 runs = runData;
				 runData = [];
				 for(var i = 0; i < runs.length; i++){
					 // We don't want ARGUS sequencers
					 if(runs[i].typeCode == "RARGUS"){
						 runs.splice(i,1);
					 }else{
						 runs[i].sequencingStartDate = convertToDate(runs[i].sequencingStartDate);
					 }
				 }
				 $http.get(jsRoutes.controllers.projects.api.Projects.list().url, {params : projectForm}).success(function(projectData, status, headers, config) {
					 projects = projectData;
					 projectData = [];
					 // Then we load our first BalanceSheets
					 loadQuarters();
					 loadSequencingProduction();
					 loadFirstTen();
					 loadProjectType();
					
					 // End of loading
					 stillLoading = false;
				 });
			 });
			 
		 });
	 }		 

	 /** Generating datatables **/ 
	 
	 var loadQuarters = function(){
		// Initializing our components
		 var balanceSheetsQuarters = [];	
		 var months = [];
		 var datatableConfig = {
				group : {
					active : false,
				},
				search : {
					active:false
				},
				pagination:{
					mode:'local',
					numberRecordsPerPage:25
				},
				hide:{
					active:false
				},
				select : {
					active : false
				}
			 }; 
		 var defaultDatatableColumns = [
				{	property:"quarter",
				  	header: "balanceSheets.quarters",
				  	type :"String",
				  	position:1
				},
				{	property:"month",
					header: "balanceSheets.monthRun",
					type :"String",
				  	position:2
				},
				{
					property:"nbBases",
					header: "balanceSheets.nbBases",
					type:"Number",
					position:3
				}
			 ];
		 
		 // Treatment
		 // Getting our months
		 if(selectedYear == actualYear){
			 for(var i = 0; i < readsets.length; i++){
				 // We get our months
				 if(months.indexOf(readsets[i].runSequencingStartDate.getMonth()) == -1){
					 months.push(readsets[i].runSequencingStartDate.getMonth());
				 }
				 // We quit our loop if we have four quarters (small optimization)
				 if(months.length == 12) break;
			 }
		 }else{
			 for(var i = 0; i < 12; i++){
				 months.push(i);
			 }
		 }
		 
		 // Initializing our main object
		 for(i = 0; i < months.length; i++){
			 balanceSheetsQuarters[i] = {
					 quarter : getQuarter(months[i]),
					 month : getMonthName(months[i]),
					 nbBases : 0
			 }
		 }
		 // Calculating our bases for each month
		 for(var i = 0; i < readsets.length; i++){
			 balanceSheetsQuarters[readsets[i].runSequencingStartDate.getMonth()].nbBases += readsets[i].treatments.ngsrg.default.nbBases.value;
		 }
		 
		 dataQuarters = balanceSheetsQuarters;
		 
		 // Creating charts
		 computeChartQuarters(balanceSheetsQuarters);
		 
		 // Adding the sum of nbBases for each quarter
		 var sum = 0;
		 var lines = [];
		 var j = 0;
		 for(var i = 0; i < balanceSheetsQuarters.length; i++){
			 sum += balanceSheetsQuarters[i].nbBases;
			 j++;
			 if(balanceSheetsQuarters[i+1] == undefined){
				 var line = {
						 quarter : '',
						 month : Messages("balanceSheets.sum"),
						 nbBases : sum
				 };
				 lines.push(line);
				 break;
			 }else if(j == 3){
				 var line = {
						 quarter : '',
						 month : Messages("balanceSheets.sum"),
						 nbBases : sum
				 };
				 lines.push(line);
				 sum = 0;
				 j = 0;
			 }
		 }
		 // We add our sum lines to our main component
		 var countLine = 0;
		 var linesToColor = [];
		 for(var i = 3; i <= Math.ceil(months.length / 3) * 4 - 1; i=i+4){
			 if(i >= balanceSheetsQuarters.length){
				 balanceSheetsQuarters.push(lines[countLine]);
				 linesToColor.push(balanceSheetsQuarters.length -1);
			 }else{
				 balanceSheetsQuarters.splice(i,0,lines[countLine]);
				 linesToColor.push(i);
			 }
			 countLine++;
		 }
		 
		 // Initialize datatable
		 dtQuarters = datatable(datatableConfig);
		 dtQuarters.setColumnsConfig(defaultDatatableColumns);	 
		 dtQuarters.setData(balanceSheetsQuarters, balanceSheetsQuarters.length);
		 for(var i = 0; i < linesToColor.length; i++){
			 colorBlue(dtQuarters, linesToColor[i]);
		 }
	 }
	 
	 
	 var loadSequencingProduction = function(){
		// Initializing our components
		 var datatableConfig = {
					group : {
						active : true
					},
					search : {
						active:false
					},
					pagination:{
						mode:'local',
						numberRecordsPerPage:25
					},
					hide:{
						active:false
					},
					order : {
						active : true
					}, 
					select : {
						active : false
					}
				 }; 
		 var defaultDatatableColumns = [
				{	property:"name",
				  	header: "balanceSheets.runTypeCode",
				  	type :"String",
				  	position:1
				},
				{	property:"nbBases",
					header: "balanceSheets.nbBases",
					type :"Number",
					order : true,
				  	position:2
				},
				{
					property:"percentage",
					header: "balanceSheets.percentage",
					type:"String",
					order : true,
					position:3
				}
			 ];
		 
		 
		 // Treatment
		 var balanceSheetsSequencingProduction = [];
		 var sequencers = [];
		 var sequencerMap = new Map();
		 
		 // Initializing our sequencer map
		 sequencerMap.set("RHS2000", "HiSeq 2000");
		 sequencerMap.set("RHS2500", "Hi2500");
		 sequencerMap.set("RHS2500R", "Hi2500 Fast");
		 sequencerMap.set("RMISEQ", "MiSeq");
		 sequencerMap.set("RGAIIx", "GA IIx");
		 
		 // Getting our sequencers
		 for(var i = 0; i < readsets.length; i++){
			if(sequencers.indexOf(readsets[i].runTypeCode) == -1){
				sequencers.push(readsets[i].runTypeCode);
			}
		 }
		 
		 // Initializing our main object
		 for(var i = 0; i < sequencers.length; i++){
			balanceSheetsSequencingProduction[i] = {
					name : sequencers[i],
					nbBases : 0,
					percentage : null
			};
		 }
		
		 // Calculating our bases for each sequencer
		 for(var i = 0; i < readsets.length; i++){
			 for(var j = 0; j < sequencers.length; j++){
				 if(sequencers[j] == readsets[i].runTypeCode){
					 balanceSheetsSequencingProduction[j].nbBases += readsets[i].treatments.ngsrg.default.nbBases.value;
				 }
			 }
		 }
		 
		 // Getting the right name for our sequencers
		 for(var i = 0; i < balanceSheetsSequencingProduction.length; i++){
			 balanceSheetsSequencingProduction[i].name = sequencerMap.get(balanceSheetsSequencingProduction[i].name);
		 }
		 
		 
		 
		 // Calculating our percentage for each sequencer
		 var sum = 0;
		 for(var i = 0; i < balanceSheetsSequencingProduction.length; i++){
			 sum+= balanceSheetsSequencingProduction[i].nbBases;
		 }
		 for(var i = 0; i < balanceSheetsSequencingProduction.length; i++){
			 balanceSheetsSequencingProduction[i].percentage = (balanceSheetsSequencingProduction[i].nbBases*100/sum).toFixed(2) + "%";
		 }
		 
		 dataSequencing = balanceSheetsSequencingProduction;
		 
		 // Initialize datatable
		 dtSequencingProduction = datatable(datatableConfig);
		 dtSequencingProduction.setColumnsConfig(defaultDatatableColumns);
		 dtSequencingProduction.setData(balanceSheetsSequencingProduction, balanceSheetsSequencingProduction.length);
		 
		 // Creating chart
		 computeChartSequencingProduction();
	 }
	 
	 
	 var loadFirstTen = function(){
		 // Initializing our components
		 var datatableConfig = {
				group : {
					active : false,
				},
				search : {
					active:false
				},
				pagination:{
					mode:'local',
					numberRecordsPerPage:25
				},
				hide:{
					active:false
				},
				select : {
					active : false
				}
			 }; 
		 var defaultDatatableColumns = [
				{	property:"code",
				  	header: "balanceSheets.projectCode",
				  	type :"String",
				  	position:1
				},
				{	property:"name",
					header: "balanceSheets.projectName",
					type :"String",
				  	position:2
				},
				{
					property:"nbBases",
					header: "balanceSheets.nbBases",
					type:"Number",
					position:3
				}
			 ];
		 
		 // Treatment
		 var balanceSheetsFirstTen = [];

	 	 // Getting our codes for each project
		 for(var i = 0; i < readsets.length; i++) {
			 if(yearlyProjects.indexOf(readsets[i].projectCode) == -1){
				 yearlyProjects.push(readsets[i].projectCode);
			 }
		 }
		 // Get our names for each project
		 for(var i = 0; i < yearlyProjects.length; i++){
			 for(var j = 0; j < projects.length; j++){
				 if(yearlyProjects[i] == projects[j].code) {
					 balanceSheetsFirstTen[i] = {
						 code : yearlyProjects[i],
						 name : projects[j].name,
						 nbBases : 0
					 };
				 }
			 }
		 }	
		 
		 // Calculating our bases for each project
		 for(var i = 0; i < readsets.length; i++){
			 for(var j = 0; j < balanceSheetsFirstTen.length; j++){
				 if(readsets[i].projectCode == balanceSheetsFirstTen[j].code){
					 balanceSheetsFirstTen[j].nbBases += readsets[i].treatments.ngsrg.default.nbBases.value;
				 }
			 }
		 }
		 
		 // We sort the projects by balanceSheetsFirstTen.nbBases
		 balanceSheetsFirstTen.sort(function(a, b){return parseInt(b.nbBases) - parseInt(a.nbBases)});
		 
		 
		 
		 // We only keep the top ten
		 balanceSheetsFirstTen = balanceSheetsFirstTen.slice(0,10);
		 
		 dataFirstTen = balanceSheetsFirstTen;
		
		 // Creating chart
		 computeChartFirstTen(balanceSheetsFirstTen);
		 
		 // TODO : virer calcul
		 // Adding sum of our bases to our main component
		 var sumBases = 0;
		 for(var i = 0; i < balanceSheetsFirstTen.length; i++){
			 sumBases += balanceSheetsFirstTen[i].nbBases;
		 }
		 var sum = {
				 code : Messages("balanceSheets.totalTen"),
				 name : "",
				 nbBases : sumBases
		 };
		 var posSum = balanceSheetsFirstTen.push(sum) - 1;
		 
		 var percentage = {
				 code : Messages("balanceSheets.percentageTotalSum"),
				 name : (sumBases * 100 / total).toFixed(2) + "%",
				 nbBases : null
		 }
		 var posPercentage = balanceSheetsFirstTen.push(percentage) - 1;
		 // Initializing datatable
		 dtFirstTen = datatable(datatableConfig);
		 dtFirstTen.setColumnsConfig(defaultDatatableColumns);
		 dtFirstTen.setData(balanceSheetsFirstTen, balanceSheetsFirstTen.length);
		 colorBlue(dtFirstTen, posSum);
		 colorBlue(dtFirstTen, posPercentage);
		 
	 }
	 
	 var loadProjectType = function(){
		 // Initializing our components
		 var datatableConfig = {
				group : {
					active : false,
				},
				search : {
					active:false
				},
				pagination:{
					mode:'local',
					numberRecordsPerPage:25
				},
				hide:{
					active:false
				},
				select : {
					active : false
				}
			 }; 
		 var defaultDatatableColumns = [
				{	property:"type",
				  	header: "balanceSheets.projectType",
				  	type :"String",
				  	position:1
				},
				{	property:"nbBases",
					header: "balanceSheets.nbBases",
					type :"Number",
				  	position:2
				},
				{
					property:"percentage",
					header: "balanceSheets.percentage",
					type:"String",
					position:3
				}
			 ];
		 
		 // Treatment
		 var types = [];
		 var balanceSheetsProjectType = [];
		 
		 // Gathering types of projects
		 for(var i = 0; i < readsets.length; i++){
			 if(types.indexOf(readsets[i].sampleOnContainer.sampleTypeCode) == -1){
				 types.push(readsets[i].sampleOnContainer.sampleTypeCode);
			 }
		 }
		 
		 // Initializing main component
		 for(var i = 0; i < types.length; i++){
			 balanceSheetsProjectType[i] = {
					 type : types[i],
					 nbBases : 0,
					 percentage : null
			 };
		 }
		 
		 // NbBases
		 for(var i = 0; i < readsets.length; i++){
			 for(var j = 0; j < balanceSheetsProjectType.length; j++){
				 if(readsets[i].sampleOnContainer.sampleTypeCode == balanceSheetsProjectType[j].type){
					 balanceSheetsProjectType[j].nbBases += readsets[i].treatments.ngsrg.default.nbBases.value;
				 }
			 }
		 }
		 
		 // Percentage
		 for(var i = 0; i < balanceSheetsProjectType.length; i++){
			 balanceSheetsProjectType[i].percentage = (balanceSheetsProjectType[i].nbBases * 100 / total).toFixed(2) + "%";
		 }
		 
		 dataProjectType = balanceSheetsProjectType;
		 
		 // Creating chart
		 computeChartProjectType(balanceSheetsProjectType);
		 
		 // Adding sum of our bases to our main component
		 var sum = {
				 type : Messages("balanceSheets.sum"),
				 nbBases : total,
				 percentages : ""
		 };
		 var posSum = balanceSheetsProjectType.push(sum) -1;
		 
		 var percentage = {
				 type : Messages("balanceSheets.percentage"),
				 nbBases : null,
				 percentage : "100%"
		 };
		 var posPercentage = balanceSheetsProjectType.push(percentage) -1;
		 
		 // Initializing datatable
		 dtProjectType = datatable(datatableConfig);
		 dtProjectType.setColumnsConfig(defaultDatatableColumns);
		 dtProjectType.setData(balanceSheetsProjectType, balanceSheetsProjectType.length);
		 colorBlue(dtProjectType, posSum);
		 colorBlue(dtProjectType, posPercentage);	 	 
	 }
	 
	 
	/** Generating charts **/
	 
	 var computeChartQuarters = function(data){
		 // Adapted from statsService
		 var propertyGroupGetter = "quarter";
		 var groupGetter = $parse(propertyGroupGetter);
		 var getter = $parse("nbBases");
		 
		 var statData = data.map(function(value) {
				return getter(value)
		 });
		 var mean = ss.mean(statData);
		 var stdDev = ss.standard_deviation(statData);
		 var i = 0;
		 var dataSeries = data.map(function(x) {
			 return {
				name : x.month,
				y : getter(x),
				x : i++,
				_value : getter(x),
				_group : (groupGetter)?groupGetter(x):undefined
			 };
		 });
		 dataSeries = getGroupValues(dataSeries);
		 
		 var allSeries = [];
		 for(var key in dataSeries){
			allSeries.push({
				data : dataSeries[key],
				name : "T"+key,
				type : 'column',
				turboThreshold : 0
			});						
		 }		 

		 chartQuarters = {
			 chart : {
				 zoomType : 'x',
				 height : 770
			 },
			 title : {
				 text : Messages("balanceSheets.quarterBases")
			 },
			 xAxis : {
				 title : {
					 text : "Mois",
				 },
				 labels : {
					 enabled : true,
					 rotation : -45
				 },
				 type : "category",
				 tickPixelInterval : 1
			 },
			 yAxis : {
				 labels : {
					 formatter : function(){
						 return (this.value/Math.pow(10,9)) + ' Gb';
					 }
				 },
				 title : {
					 text : Messages("balanceSheets.nbBases")
				 }
			 },
			 series : allSeries,
			 plotOptions : {column:{grouping:false}}
		 };
	 }
	 
	 
	 var computeChartSequencingProduction = function(){
		 var data = dtSequencingProduction.getData();
		 var typeCode = [];
		 for(var i = 0; i < data.length; i++){
			 typeCode.push(data[i].name);
		 }
		 var statData = [];
		 for(var i = 0; i < data.length; i++){
			 statData.push(data[i].nbBases);
		 }
		 chartSequencingProduction = {
				chart : {
	                zoomType : 'x',
					height : 770,
					type : 'column'
				},
				title : {
					text : Messages("balanceSheets.nbSequencingType")
				},
				xAxis : {
					categories: typeCode,
					crosshair: true,
					title : {
						text : Messages("balanceSheets.sequencingType")
					},
					labels : {
						enabled : true,
						rotation : -75
					},
					type : "category",
					tickPixelInterval : 1
				},
	
				yAxis : {
					title : {
						text : Messages("balanceSheets.nbBases")
					},
					labels: {
		                formatter: function () {
		                    return (this.value/Math.pow(10,9)).toFixed(2) + ' Gb';
		                }
		            },
					tickInterval : 2,
				},
				series : [{
					name : Messages("balanceSheets.nbBases"), 
					data : statData,
					turboThreshold : 0
				}]
		};
	 }
	 
	 var computeChartFirstTen = function(data){
		 var codes = [];
		 var sum = 0;
		 var percentages = [];
		 for(var i = 0; i < readsets.length; i++){
			 sum += readsets[i].treatments.ngsrg.default.nbBases.value;
		 }
		 for(var i = 0; i < data.length; i++){
			 codes[i] = data[i].code;
			 percentages[i] = data[i].nbBases * 100 / sum;
		 }
		 var allData = [];
		 for(var i = 0; i < codes.length; i++){
			 var temp = [];
			 temp.push(codes[i]);
			 temp.push(percentages[i]);
			 allData[i] = temp;
		 }
				 		 
		 chartFirstTen = {
				 chart : {
					 type : 'pie'
				 },
				 title : {
					 text : Messages("balanceSheets.tab.firstTen")
				 },
				 plotOptions : {
					 pie : {
						 allowPointSelect : true,
						 cursor : 'pointer',
						 dataLabels : {
							 enabled : true,
							 format : "<b>{point.name}</b>	: {point.percentage:.2f} %"
						 }
					 }
				 },
				 series : [{
					 name : Messages("balanceSheets.percentage"),
					 data : allData,
					 turboThreshold : 0
				 }]
		 }; 
	 }
	 
	 var computeChartProjectType = function(data){
		 var types = [];
		 var percentages = [];
		 for(var i = 0; i < data.length; i++){
			 types.push(data[i].type);
			 percentages.push(data[i].nbBases * 100 / total);
		 }
		 var allData = [];
		 
		 for(var i = 0; i < types.length; i++){
			 var temp = [];
			 temp.push(types[i]);
			 temp.push(percentages[i]);
			 allData[i] = temp;
		 }
		 
		 chartProjectType = {
				 chart : {
					 type : 'pie'
				 },
				 title : {
					 text : Messages("balanceSheets.tab.projectType")
				 },
				 plotOptions : {
					 pie : {
						 allowPointSelect : true,
						 cursor : 'pointer',
						 dataLabels : {
							 enabled : true,
							 format : "<b>{point.name}</b>	: {point.percentage:.2f} %"
						 }
					 }
				 },
				 series : [{
					 name : Messages("balanceSheets.percentage"),
					 data : allData,
					 turboThreshold : 0
				 }]
		 }; 
	 }
	 
	 /** Other functions **/
	 
	 
	 var convertToDate = function(dateInMilliSeconds){
		 return new Date(dateInMilliSeconds);
	 }
	 
	 var getQuarter = function(month){
		 return parseInt(month/3) + 1;
	 }
	 
	 var getMonthName = function(month){
		 var monthNames = [Messages("balanceSheets.january"), Messages("balanceSheets.february"), Messages("balanceSheets.march"),
		                   Messages("balanceSheets.april"), Messages("balanceSheets.may"), Messages("balanceSheets.june"),
		                   Messages("balanceSheets.july"),Messages("balanceSheets.august"), Messages("balanceSheets.september"),
		                   Messages("balanceSheets.october"), Messages("balanceSheets.november"), Messages("balanceSheets.december")];
		 return monthNames[month];
	 }
	 
	 var getGroupValues = function(dataMustBeGroup){
			var groupValues = dataMustBeGroup.reduce(function(array, value){
				var groupValue = value._group;
				if(!array[groupValue]){
					array[groupValue]=[];
				}
				array[groupValue].push(value);
				return array;
			}, {});
			return groupValues;
	 }
	 
	 var flushData = function(){
		 readsets = [];
		 runs = [];
		 projects = [];
	 }
	 
	 var colorBlue = function(datatable, pos){
		 datatable.displayResult[pos].line.trClass="info";
	 }
	
	 
		
	 /** Service object **/	
		
	 var balanceSheets = {
		// Charts
			chartSequencingProduction : function(){return chartSequencingProduction},
			chartFirstTen : function(){return chartFirstTen},
			chartQuarters : function(){return chartQuarters},
			chartProjectType : function(){return chartProjectType},
		// Datatables	
			dtSequencingProduction : function(){return dtSequencingProduction;},
			dtFirstTen : function(){return dtFirstTen;},
			dtQuarters : function(){return dtQuarters;},
			dtProjectType : function(){return dtProjectType},
		// others
			init : function(year){loadData(year);},
			isLoading : function(){return stillLoading;},
			showQuarters : function(){loadQuarters();},
			showFirstTen : function(){loadFirstTen();},
			showSequencingProduction : function(){loadSequencingProduction();},
			showProjectType : function(){loadProjectType();}
	 };
	 
	 return balanceSheets;	 
 }
 
 
 
 ]).factory('balanceSheetsGeneralSrv', ['$http', 'mainService', 'datatable', '$parse',
                                        function($http, mainService, datatable, $parse){
			var readsets = [];
			var dtYearlyBalanceSheets;
			var chartYearlyBalanceSheets;
			var isLoading = true;
			var actualYear = new Date().getFullYear();
			
			var loadData = function(){
				isLoading = true;
				var form = {includes : []};
				form.includes.push("treatments.ngsrg.default.nbBases");
				form.includes.push("runSequencingStartDate");
				form.limit = 100000;
				$http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url, {params : form}).success(function(data, status, headers, config) {
					readsets = data;
					for(var i = 0; i < readsets.length; i++){
						readsets[i].runSequencingStartDate = convertToDate(readsets[i].runSequencingStartDate);
					}	
					loadYearlyBalanceSheets();
					
					isLoading = false;
				});
			}
			
			var loadYearlyBalanceSheets = function(){
				// Initializing our components
				 var datatableConfig = {
							group : {
								active : false
							},
							search : {
								active:false
							},
							pagination:{
								mode:'local',
								numberRecordsPerPage:25
							},
							hide:{
								active:false
							},
							select : {
								active : false
							}
						 };
						 var defaultDatatableColumns = [
							{	property:"year",
							  	header: "balanceSheets.year",
							  	type :"String",
							  	position:1
							},
							{	property:"nbBases",
								header: "balanceSheets.nbBases",
								type :"Number",
							  	position:2,
							}
						 ];	
				 
				 	 
				 
				 // Treatment
				 // Initializing our main object
				 var balanceSheetsByYearAndTechnology = [];
				 for (var i = 2008; i <= actualYear; i++){
					 balanceSheetsByYearAndTechnology[i-2008] = {
							 nbBases : 0,
							 year : i
					 };
				 }
				 
				 // Calculating our bases for each year
				 for(var i = 0; i < readsets.length; i++){
					balanceSheetsByYearAndTechnology[readsets[i].runSequencingStartDate.getFullYear() - 2008].nbBases += readsets[i].treatments.ngsrg.default.nbBases.value;
				 }
				 
				 // Creating chart
				 computeChartYearlyBalanceSheets(balanceSheetsByYearAndTechnology);
				 
				 // Adding sum of our bases to our datatable
				 var sum = {
						 year : Messages("balanceSheets.sum"),
						 nbBases : 0
				 };
				 for(var i = 0; i < balanceSheetsByYearAndTechnology.length; i++){
					 sum.nbBases += balanceSheetsByYearAndTechnology[i].nbBases;
				 }
				 var posSum = balanceSheetsByYearAndTechnology.push(sum) -1;
				 
				 // Initialize datatable
				 dtYearlyBalanceSheets = datatable(datatableConfig);
				 dtYearlyBalanceSheets.setColumnsConfig(defaultDatatableColumns);	 
				 dtYearlyBalanceSheets.setData(balanceSheetsByYearAndTechnology, balanceSheetsByYearAndTechnology.length);
				 colorBlue(dtYearlyBalanceSheets, posSum);
			}
			
			
			var computeChartYearlyBalanceSheets = function(data){
				 var years = [];
				 for(var i = 0; i < data.length; i++){
					 years[i] = data[i].year;
				 }
				 var statData = [];
				 for(var i = 0; i < data.length; i++){
					 statData[i] = data[i].nbBases;
				 }
				 chartYearlyBalanceSheets = {
						chart : {
			                zoomType : 'x',
							height : 770,	
						},
						title : {
							text : Messages("balanceSheets.yearlyBases")
						},
						xAxis : {
							categories: years,
							crosshair: true,
							title : {
								text : 'Année',
							},
							labels : {
								enabled : true,
								rotation : -75
							},
							type : "category",
							tickPixelInterval : 1
						},
			
						yAxis : {
							title : {
								text : Messages("balanceSheets.nbBases")
							},
							labels: {
				                formatter: function () {
				                    return (this.value/Math.pow(10,12)).toFixed(2) + ' Tb';
				                }
				            },
							tickInterval : 2,
						},
						series : [{
							type : 'column',
							name : Messages("balanceSheets.nbBases"), 
							data : statData,
							turboThreshold : 0
						}]
				};
			}
			
			
			var convertToDate = function(dateInMilliSeconds){
				 return new Date(dateInMilliSeconds);
			}
			
			var colorBlue = function(datatable, pos){
				 datatable.displayResult[pos].line.trClass="info";
			}
			
			// TODO : Conserver l'onglet actif lors du changement d'année
			// Mettre le texte dans Messages
			
			var balanceSheetsGeneral = {
					isLoading : function(){return isLoading;},
					chartYearlyBalanceSheets : function(){return chartYearlyBalanceSheets},
					dtYearlyBalanceSheets : function(){return dtYearlyBalanceSheets;},
					init : function(){loadData();}	
			};
			
			return balanceSheetsGeneral;	
 
 }]);
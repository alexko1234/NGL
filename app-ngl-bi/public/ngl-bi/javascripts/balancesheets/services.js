 "use strict";
 
 angular.module('ngl-bi.BalanceSheetsService', []).
 factory('balanceSheetsSrv', ['$http', 'mainService', 'datatable', '$parse', '$filter',
                                    function($http, mainService, datatable, $parse, $filter){
	 
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
	 var dtSumSequencingProduction;
	 var dtFirstTen;
	 var dtSumFirstTen;
	 var dtQuarters;
	 var dtProjectType;
	 var dtSumProjectType;
	 
	 // Data
	 var dataSequencing;
	 var dataFirstTen;
	 var dataQuarters;
	 var dataProjectType;
	 
	 // Others
	 var selectedYear = 0;
	 var actualDay = new Date();
	 var actualYear = actualDay.getFullYear();
	 var stillLoading = true;
	 var total = 0;
	 	 
	 var manageCache = function(changeYear, year){
		 stillLoading = true;
		 selectedYear = year;
		 if(mainService.get('yearsInCache') != undefined){
			var map = mainService.get('yearsInCache');
			if(!map.has(selectedYear)){
				loadData(selectedYear);
			}else{
				if(changeYear){
					balanceSheets.setData(map.get(selectedYear), selectedYear);
					balanceSheets.loadFromCache();
				}else{
					if(mainService.get('balanceSheetsActiveTab') == 0) {
						balanceSheets.showQuarters();
					}
					else if(mainService.get('balanceSheetsActiveTab') == 1){
						balanceSheets.showSequencingProduction();
					}
					else if(mainService.get('balanceSheetsActiveTab') == 2){
						balanceSheets.showFirstTen();						
					}
					else if(mainService.get('balanceSheetsActiveTab') == 3){
						balanceSheets.showProjectType();
					}
					stillLoading = false;
				}
			}
		}else{
			loadData(selectedYear);
		}
	 }
	 
	 
	 
	 var loadData = function(year){
		 flushData();

		 // We initialize our form
		 var form = {includes : []};
		 form.includes.push("default");
		 form.includes.push("treatments.ngsrg.default.nbBases");
		 form.includes.push("projectCode");
		 form.includes.push("runTypeCode");
		 form.includes.push("runSequencingStartDate");
		 form.includes.push("sampleOnContainer.sampleTypeCode");
		 form.includes.push("sampleOnContainer.sampleCategoryCode");
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
		 $http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url, {params : form})
		 	.success(function(data, status, headers, config) {
		 		
		 	 // For CNG when no data	
		     if(data.length == 0){
		    	 stillLoading = false;
		     }else{
		    	 // Test for when the user clicks like a monkey on every year, and twice the same.
			 	 if(readsets.length != 0 && readsets[0].runSequencingStartDate.getFullYear() == mainService.get('activeYear')) {
			 		 flushData();
			 	 }
			 	 
			 	 
			 	 
				 for(var i = 0; i < data.length; i++){
					 data[i].runSequencingStartDate = convertToDate(data[i].runSequencingStartDate);
				 }
				 for(var i = 0; i < data.length; i++){
					 if(data[i].runSequencingStartDate.getFullYear() == selectedYear){
						 total += data[i].treatments.ngsrg.default.nbBases.value;	
						 readsets.push(data[i]);
					 }
				 }
				 
				 for(var i = 0; i < readsets.length; i++){
					 if(readsets[i].sampleOnContainer == null || readsets[i].sampleOnContainer == undefined){
						 readsets[i].sampleOnContainer = {
								 sampleTypeCode : 'not-defined',
								 sampleCategoryCode : 'unknown'
						 };
					 }
				 }
				 
				 $http.get(jsRoutes.controllers.projects.api.Projects.list().url, {params : projectForm}).success(function(projectData, status, headers, config) {
					 projects = projectData;
					 projectData = [];
					 
					 // Then we load our balance sheets
					 loadFunctions();
					 
					 
					 
					 // Caching
					 var yearMap = new Map();
					 var years = new Map();
					 if(mainService.get('yearsInCache') != undefined){
						 if(!mainService.get('yearsInCache').has(String(selectedYear))){
							 years = mainService.get('yearsInCache');
							 years.set(selectedYear, balanceSheets.returnData());
							 mainService.put('yearsInCache', years);
						 }
					 }else{
						 yearMap.set(selectedYear, balanceSheets.returnData());
						 mainService.put('yearsInCache', yearMap);
					 }
					 
					 // End of loading
					 if(readsets[0] != undefined){
						 if(mainService.get('activeYear') == readsets[0].runSequencingStartDate.getFullYear()){
							 stillLoading = false;
						 }
					 }
				 });
				 
				 
				 /*
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
					
				 });
				 */
		     }
		     
		    
		 });
	 }		 
	 
	 var loadFunctions = function(){
		loadQuarters();
		loadSequencingProduction();
		loadFirstTen();
		loadProjectType();
		
		 if(readsets[0] != undefined){
			 if(mainService.get('activeYear') == readsets[0].runSequencingStartDate.getFullYear()){
				 stillLoading = false;
			 }
		 }
	 }
	 
	 var loadFunctionsFromCache = function(){
		 stillLoading = true;
		 setTimeout(loadFunctions, 0);
	 }

	 /** Generating datatables **/ 
	 
	 var loadQuarters = function(){
		// Initializing our components
		 var balanceSheetsQuarters = [];	
		 var months = [];
		 var datatableConfig = {
				showTotalNumberRecords : false,
				search : {
					active:false
				},
				pagination:{
					active : false
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
		 /*
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
		 */
		 for(var i = 0; i < 12; i++){
			 months.push(i);
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
			 try{			
				 	balanceSheetsQuarters[readsets[i].runSequencingStartDate.getMonth()].nbBases += readsets[i].treatments.ngsrg.default.nbBases.value;			
			 }catch (e) {
				 console.log("readset with ngsrg.default null "+i+" / "+readsets[i].code);
			}
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
		 
		 // Adding total of nbBases for this year
		 
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
		 var yearLine = {
				quarter : '',
				month : Messages("balanceSheets.totalSum"),
		 		nbBases : total
		 };
		 balanceSheetsQuarters.push(yearLine);
		 countLine++;
		 linesToColor.push(balanceSheetsQuarters.length -1);
		 
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
					search : {
						active:false
					},
					pagination:{
						active : false
					},
					hide:{
						active:false
					},
					select : {
						active : false
					},
					order : {
						mode : 'local',
						active : true,
						by : 'nbBases',
						reverse : true,
						callback:function(datatable){
							computeChartSequencingProduction();
						}
					}
				 }; 
		 var defaultDatatableColumns = [
				{	property:"name",
				  	header: "balanceSheets.runTypeCode",
				  	type :"String",
				  	order : true,
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
		 sequencerMap.set("RNEXTSEQ500", "NextSeq 500");
		 
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
			 balanceSheetsSequencingProduction[i].percentage = (balanceSheetsSequencingProduction[i].nbBases*100/sum).toFixed(2) + " %";
		 }
		 for(var i = 0; i < balanceSheetsSequencingProduction.length; i++){
			 balanceSheetsSequencingProduction[i].percentage = parseFloat(balanceSheetsSequencingProduction[i].percentage).toLocaleString() + " %";
		 }
		 
		 dataSequencing = balanceSheetsSequencingProduction;
		 
		 // Initialize datatable
		 dtSequencingProduction = datatable(datatableConfig);
		 dtSequencingProduction.setColumnsConfig(defaultDatatableColumns);
		 dtSequencingProduction.setData(balanceSheetsSequencingProduction, balanceSheetsSequencingProduction.length);
		 
		 // Initialize other datatable
		 loadDtSumSequencingProduction();
		 
		 
		 // Creating chart
		 computeChartSequencingProduction();
		 
	 }
	 
	 var loadDtSumSequencingProduction = function(){
		 var datatableConfig = {
				 	showTotalNumberRecords : false,
					search : {
						active:false
					},
					pagination:{
						active : false
					},
					hide:{
						active:false
					},
					select : {
						active : false
					}
				 }; 
		 var defaultDatatableColumns = [
				{	property:"property",
				  	header: "balanceSheets.property",
				  	type :"String",
				  	position:1
				},
				{	property:"value",
					header: "balanceSheets.value",
					type :"Number",
				  	position:2
				}
			 ];
		 var sum = [{
				 property : Messages('balanceSheets.sum'),
				 value : total
		 }];
		 
		 
		 dtSumSequencingProduction = datatable(datatableConfig);
		 dtSumSequencingProduction.setColumnsConfig(defaultDatatableColumns);
		 dtSumSequencingProduction.setData(sum, 1);
		 colorBlue(dtSumSequencingProduction, 0);
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
					active : false
				},
				hide:{
					active:false
				},
				select : {
					active : false
				},
				order : {
					active : true,
					mode : 'local'
				}
			 }; 
		 var defaultDatatableColumns = [
				{	property:"code",
				  	header: "balanceSheets.projectCode",
				  	type :"String",
				  	order : true,
				  	position:1
				},
				{	property:"name",
					header: "balanceSheets.projectName",
					type :"String",
					order : true,
				  	position:2
				},
				{
					property:"nbBases",
					header: "balanceSheets.nbBases",
					type:"Number",
					order : true,
					position:3
				},
				{
					property:"percentageForTenProjects",
					header: "balanceSheets.percentageForTenProjects",
					type:"String",
					order : true,
					position:4
				},
				{
					property:"percentageForYear",
					header:"balanceSheets.percentageForYear",
					type:"String",
					order:true,
					position:5
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
						 nbBases : 0,
						 percentageForTenProjects:null,
						 percentageForYear:null
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
		 
		 var nbBasesForTenProjects = 0;
		 
		 for(var i = 0; i < balanceSheetsFirstTen.length; i++){
			 nbBasesForTenProjects += balanceSheetsFirstTen[i].nbBases;
		 }
		 
		 // We calculate percentage for each project
		 for(var i = 0; i < balanceSheetsFirstTen.length; i++){
			 balanceSheetsFirstTen[i].percentageForTenProjects = (balanceSheetsFirstTen[i].nbBases * 100 / nbBasesForTenProjects).toFixed(2) + " %";
			 balanceSheetsFirstTen[i].percentageForYear = (balanceSheetsFirstTen[i].nbBases *100 / total).toFixed(2) + " %";
		 }
		 
		 // European formatting
		 for(var i = 0; i < balanceSheetsFirstTen.length; i++){
			 balanceSheetsFirstTen[i].percentageForTenProjects = parseFloat(balanceSheetsFirstTen[i].percentageForTenProjects).toLocaleString() + " %";
			 balanceSheetsFirstTen[i].percentageForYear = parseFloat(balanceSheetsFirstTen[i].percentageForYear).toLocaleString() + " %";

		 }
		 
		 dataFirstTen = balanceSheetsFirstTen;
		 
		 // Initialize other datatable
		 loadDtSumFirstTen();
		
		 // Creating chart
		 computeChartFirstTen(balanceSheetsFirstTen);
		 		 
		 // Initializing datatable
		 dtFirstTen = datatable(datatableConfig);
		 dtFirstTen.setColumnsConfig(defaultDatatableColumns);
		 dtFirstTen.setData(balanceSheetsFirstTen, balanceSheetsFirstTen.length);
		 
	 }
	 
	 var loadDtSumFirstTen = function(){
		 var datatableConfig = {
				 	showTotalNumberRecords : false,
					search : {
						active:false
					},
					pagination:{
						active : false
					},
					hide:{
						active:false
					},
					select : {
						active : false
					}
				 }; 
		 var defaultDatatableColumns = [
				{	property:"property",
				  	header: "balanceSheets.property",
				  	type :"String",
				  	position:1
				},
				{	property:"value",
					header: "balanceSheets.value",
					type :"Number",
				  	position:2
				},
				{
					property :"percentage",
					header: "balanceSheets.percentage",
					type : "String",
					position : 3
				}
			 ];
		 
		 var dataToInsert = [];
		 var linesToColor = [];
		 
		 var sumBases = 0;
		 for(var i = 0; i < dataFirstTen.length; i++){
			 sumBases += dataFirstTen[i].nbBases;
		 }
		 var sum = {
				 property : Messages("balanceSheets.totalTen"),
				 value : sumBases,
				 percentage : (sumBases * 100 / total).toFixed(2).toLocaleString() + " %"
		 };
		 linesToColor.push(dataToInsert.push(sum) - 1);
		 
		 var totalSum = {
				 property : Messages("balanceSheets.totalSum"),
				 value : total,
				 percentage : "100 %"
		 };
		 linesToColor.push(dataToInsert.push(totalSum) -1);
		 
		 // European formatting 
		 for(var i = 0; i < dataToInsert.length; i++){
			 dataToInsert[i].percentage = parseFloat(dataToInsert[i].percentage).toLocaleString() + " %";
		 }
		 
		 dtSumFirstTen = datatable(datatableConfig);
		 dtSumFirstTen.setColumnsConfig(defaultDatatableColumns);
		 dtSumFirstTen.setData(dataToInsert, dataToInsert.length);
		 
		 // Color text in blue
		 for(var i = 0; i < linesToColor.length; i++) colorBlue(dtSumFirstTen, linesToColor[i]);
		 
		 
	 }
	 
	 var loadProjectType = function(){
		 // Initializing our components
		 var datatableConfig = {
				order : {
					active : true,
					by : 'nbBases',
					reverse : true,
					mode: 'local'
				},
				search : {
					active:false
				},
				pagination:{
					active : false
				},
				hide:{
					active:false
				},
				select : {
					active : false
				}
			 }; 
		 var defaultDatatableColumns = [
		        {
		        	property : "category",
		        	header : "balanceSheets.categoryType",
		        	//filter: "codes:'sample_cat'",
		        	type : "String",
		        	order : true,
		        	position : 1
		        },
				{	property:"type",
					filter: "codes:'type'",
				  	header: "balanceSheets.projectType",
				  	type :"String",
				  	order : true,
				  	position:2
				},
				{	property:"nbBases",
					header: "balanceSheets.nbBases",
					type :"Number",
					order : true,
				  	position:3
				},
				{
					property:"percentage",
					header: "balanceSheets.percentage",
					type:"String",
					order : true,
					position:4
				}
			 ];
		 
		 // Treatment
		 var types = [];	
		 var balanceSheetsProjectType = [];
		 
		 // Gathering types of projects
		 for(var i = 0; i < readsets.length; i++){
			 //var sampleTypeCode = $filter('codes')(readsets[i].sampleOnContainer.sampleTypeCode, 'type');
			 var sampleTypeCode = readsets[i].sampleOnContainer.sampleTypeCode;
			 if(types.indexOf(sampleTypeCode) == -1){
				 types.push(sampleTypeCode);
			 }			
		 }
		 
		 
		 // Initializing main component
		 for(var i = 0; i < types.length; i++){
			 balanceSheetsProjectType[i] = {
					 category : "test",
					 type : types[i],
					 nbBases : 0,
					 percentage : null
			 };
		 }
		 
		 // categories
		 for(var i = 0; i < balanceSheetsProjectType.length; i++){
			 for(var j = 0; j < readsets.length; j++){
				 if(readsets[j].sampleOnContainer.sampleTypeCode == balanceSheetsProjectType[i].type){
					 balanceSheetsProjectType[i].category = $filter('codes')(readsets[j].sampleOnContainer.sampleCategoryCode, 'sample_cat');
					 break;
				 }
			 }
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
			 balanceSheetsProjectType[i].percentage = (balanceSheetsProjectType[i].nbBases * 100 / total).toFixed(2) + " %";
		 }
		 
		 // European formatting
		 for(var i = 0; i < balanceSheetsProjectType.length; i++){
			 balanceSheetsProjectType[i].percentage = parseFloat(balanceSheetsProjectType[i].percentage).toLocaleString() + " %";
		 }
		 
		 balanceSheetsProjectType.sort(function(a, b){return parseInt(b.nbBases) - parseInt(a.nbBases)});

		 
		 dataProjectType = balanceSheetsProjectType;
		 
		 // Initialize other datatable
		 loadDtSumProjectType();
		 
		 // Creating chart
		 $(function(){
			 computeChartProjectType(balanceSheetsProjectType);
		 });
		 
		 		 
		 // Initializing datatable
		 dtProjectType = datatable(datatableConfig);
		 dtProjectType.setColumnsConfig(defaultDatatableColumns);
		 dtProjectType.setData(dataProjectType, dataProjectType.length);
	 }
	 
	 var loadDtSumProjectType = function(){
		 var datatableConfig = {
				 	showTotalNumberRecords : false,
					search : {
						active:false
					},
					pagination:{
						active : false
					},
					hide:{
						active:false
					},
					select : {
						active : false
					}
				 }; 
		 var defaultDatatableColumns = [
				{	property:"property",
				  	header: "balanceSheets.property",
				  	type :"String",
				  	position:1
				},
				{	property:"value",
					header: "balanceSheets.value",
					type :"Number",
				  	position:2
				}
			 ];
		 
		 var sum = [{
			 property : Messages('balanceSheets.sum'),
			 value : total
		 }];
		 
		 
		 dtSumProjectType = datatable(datatableConfig);
		 dtSumProjectType.setColumnsConfig(defaultDatatableColumns);
		 dtSumProjectType.setData(sum, 1);
		 colorBlue(dtSumProjectType, 0); 
		 
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
			 exporting : {
				 enabled : true,
				 filename : Messages('balanceSheets.export.quarters') + $filter('date')(new Date(), 'yyyyMMdd_HHmmss'),
				 sourceWidth : 1200
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
		                    return (this.value/Math.pow(10,12)).toFixed(2) + ' Tb';
		                }
		            },
					tickInterval : 2,
				},
				exporting : {
					enabled : true,
					filename : Messages('balanceSheets.export.sequencingType') + $filter('date')(new Date(), 'yyyyMMdd_HHmmss'),
					sourceWidth : 1200
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
		 for(var i = 0; i < dataFirstTen.length; i++){
			 sum += dataFirstTen[i].nbBases;
		 }
		 for(var i = 0; i < data.length; i++){
			 codes[i] = data[i].code;
			 percentages[i] = parseFloat((data[i].nbBases * 100 / sum).toFixed(2));
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
					 type : 'pie',
					 options3d: {
			                enabled: true,
			                alpha: 45,
			                beta: 0
					 }
				 },
				 title : {
					 text : Messages("balanceSheets.tab.firstTen")
				 },
				 plotOptions : {
					 pie : {
						 size : 350,
						 allowPointSelect : true,
						 cursor : 'pointer',
						 depth: 35,
						 dataLabels : {
							 enabled : true,
							 format : "<b>{point.name}</b>	: {point.percentage:.2f} %"
						 },
				 showInLegend : true
					 }
				 },
				 exporting : {
					 enabled : true,
					 filename : Messages('balanceSheets.export.firstTen') + $filter('date')(new Date(), 'yyyyMMdd_HHmmss'),
					 sourceWidth : 1000
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
			 types.push($filter('codes')(data[i].type,'type'));
			 percentages.push(parseFloat((data[i].nbBases * 100 / total).toFixed(2)));
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
					 type : 'pie',
					 options3d: {
			                enabled: true,
			                alpha: 45,
			                beta: 0
			         }
				 },
				 title : {
					 text : Messages("balanceSheets.tab.projectType")
				 },
				 plotOptions : {
					 pie : {
						 size : 350,
						 allowPointSelect : true,
						 cursor : 'pointer',
						 depth: 35,
						 dataLabels : {
							 enabled : true,
							 format : "<b>{point.name}</b>	: {point.percentage:.2f} %"
						 },
				 		 showInLegend : true
					 }
				 },
				 exporting : {
					 enabled : true,
					 filename : Messages('balanceSheets.export.projectType') + $filter('date')(new Date(), 'yyyyMMdd_HHmmss'),
					 sourceWidth : 1000
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
		 total = 0;
	 }
	 
	 var colorBlue = function(datatable, pos){
		 datatable.displayResult[pos].line.trClass="text-primary";
	 }
	 
	 var cleanData = function(){
		 total = 0;
		 for(var i = 0; i < readsets.length; i++){
			 total += readsets[i].treatments.ngsrg.default.nbBases.value;
		 }
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
			dtSumSequencingProduction : function(){return dtSumSequencingProduction},
			dtSumFirstTen : function(){return dtSumFirstTen},
			dtSumProjectType : function(){return dtSumProjectType},
		// others
			getLength : function(){return readsets.length;},
			init : function(changeYear, year){manageCache(changeYear, year);},
			isLoading : function(){return stillLoading;},
			loadFromCache : function(){loadFunctionsFromCache();},
			returnData : function(){return new Array(readsets, runs, projects);},
			setData : function(array, year){readsets = array[0]; runs = array[1]; projects = array[2]; selectedYear = year; cleanData()},
			showQuarters : function(){loadQuarters();},
			showFirstTen : function(){loadFirstTen();},
			showSequencingProduction : function(){loadSequencingProduction();},
			showProjectType : function(){loadProjectType();}
	 };
	 stillLoading = false;
	 return balanceSheets;	 
 }
 
 
 
 ]).factory('balanceSheetsGeneralSrv', ['$http', 'mainService', 'datatable', '$parse', '$filter',
                                        function($http, mainService, datatable, $parse, $filter){
			var readsets = [];
			var dtYearlyBalanceSheets;
			var dtSumYearly;
			var chartYearlyBalanceSheets;
			var isLoading = true;
			var actualDay = new Date();
			var actualYear = actualDay.getFullYear();
			
			var loadData = function(){
				isLoading = true;
				var form = {includes : []};
				form.includes.push("default");
				form.includes.push("treatments.ngsrg.default.nbBases");
				form.includes.push("runSequencingStartDate");
				form.limit = 100000;
				$http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url, {params : form}).success(function(data, status, headers, config) {
					readsets = data;
					/*
					for(var i = 0; i < readsets.length; i++){
						readsets[i].runSequencingStartDate = convertToDate(readsets[i].runSequencingStartDate);
					}
					*/	
					loadYearlyBalanceSheets();
					
					mainService.put('generalBalanceSheets', readsets);
					
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
								active : false
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
					var readsetDate =  convertToDate(readsets[i].runSequencingStartDate);
					balanceSheetsByYearAndTechnology[readsetDate.getFullYear() - 2008].nbBases += readsets[i].treatments.ngsrg.default.nbBases.value;
				 }

				 // Creating chart
				 computeChartYearlyBalanceSheets(balanceSheetsByYearAndTechnology);
				 
				 // Initialize datatable
				 dtYearlyBalanceSheets = datatable(datatableConfig);
				 dtYearlyBalanceSheets.setColumnsConfig(defaultDatatableColumns);	 
				 dtYearlyBalanceSheets.setData(balanceSheetsByYearAndTechnology, balanceSheetsByYearAndTechnology.length);
				
				 
				 // Initialize other datatable
				 loadDtSumYearly();
			}
			
			var loadDtSumYearly = function(){
				// Initializing our components
				var datatableConfig = {
				 	showTotalNumberRecords : false,
					search : {
						active:false
					},
					pagination:{
						active : false
					},
					hide:{
						active:false
					},
					select : {
						active : false
					}
				 };
				 var defaultDatatableColumns = [
					{	property:"property",
					  	header: "balanceSheets.property",
					  	type :"String",
					  	position:1
					},
					{	property:"value",
						header: "balanceSheets.value",
						type :"Number",
					  	position:2,
					}
				 ];	
				 
				 // Calculing sum
				 var data = dtYearlyBalanceSheets.getData();
				 var sum = [{
						property : Messages('balanceSheets.sum'),
						value : 0
				 }];
				 for(var i = 0; i < data.length; i++){
					 sum[0].value += data[i].nbBases;
				 }
				 
				 // Creating datatable
				 
				 dtSumYearly = datatable(datatableConfig);
				 dtSumYearly.setColumnsConfig(defaultDatatableColumns);
				 dtSumYearly.setData(sum, 1);
				 colorBlue(dtSumYearly, 0);		 
			 
			}
			
			
			var computeChartYearlyBalanceSheets = function(data){
				 var years = [];
				 var statData = [];
				 for(var i = 0; i < data.length; i++){
					 years[i] = data[i].year;
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
						exporting : {
							enabled : true,
							filename : Messages('balanceSheets.export.general') + $filter('date')(new Date(), 'yyyyMMdd_HHmmss'),
							sourceWidth : 1200
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
				 datatable.displayResult[pos].line.trClass="text-primary";
			}
			
			var balanceSheetsGeneral = {
					isLoading : function(){return isLoading;},
					chartYearlyBalanceSheets : function(){return chartYearlyBalanceSheets},
					dtYearlyBalanceSheets : function(){return dtYearlyBalanceSheets;},
					dtSumYearly : function(){return dtSumYearly},
					loadFromCache : function(){loadYearlyBalanceSheets()},
					init : function(){loadData();}	
			};
			
			return balanceSheetsGeneral;	
 
 }]);
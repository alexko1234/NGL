"use strict";


function getCommonColumns(columns){
	
	columns.push({	property:"code",
		    	  	header: "readsets.code",
		    	  	type :"text",
		    	  	order:true});
	columns.push({	property:"runCode",
					header: "readsets.runCode",
					type :"text",
					order:true});
	columns.push({	property:"laneNumber",
					header: "readsets.laneNumber",
					type :"text",
					order:true});
	columns.push({	property:"projectCode",
					header: "readsets.projectCode",
					type :"text",
					order:true});
	columns.push({	property:"sampleCode",
					header: "readsets.sampleCode",
					type :"text",
					order:true});
	columns.push({	property:"runSequencingStartDate",
					header: "runs.sequencingStartDate",
					type :"date",
					order:true});	
	return columns;
};

function getSearchColumns(columns){
	columns.push({	property:"state.code",
					filter:"codes:'state'",
					header: "readsets.stateCode",
					type :"text",
					order:true});
	columns.push({	property:"productionValuation.valid",
					filter:"codes:'valuation'",
					header: "readsets.productionValuation.valid",
					type :"text",
			    	order:true});
	columns.push({	property:"bioinformaticValuation.valid",
					filter:"codes:'valuation'",
					header: "readsets.bioinformaticValuation.valid",
					type :"text",
			    	order:true});
	return columns;
};

function getStateColumns(columns){

	columns.push({	property:"state.code",
					filter:"codes:'state'",
					header: "readsets.stateCode",
					type :"text",
					edit:true,
					order:true,
			    	choiceInList:true,
			    	listStyle:'bt-select',
			    	possibleValues:'listsTable.getStates()'});
	return columns;
};
function getValuationColumns(columns){	
	
	columns.push({	property:"productionValuation.valid",
					filter:"codes:'valuation'",
					header: "readsets.productionValuation.valid",
					type :"text",
			    	order:true,
			    	edit:true,
			    	choiceInList:true,
			    	listStyle:'bt-select',
			    	possibleValues:'listsTable.getValuations()'
			    	});
	columns.push({	property:"bioinformaticValuation.valid",
					filter:"codes:'valuation'",
					header: "readsets.bioinformaticValuation.valid",
					type :"text",
					order:true,
			    	edit:true,
			    	choiceInList:true,
			    	listStyle:'bt-select',
			    	possibleValues:'listsTable.getValuations()'
			    	});		
	return columns;
};

function getBatchColumns(columns){
	
	columns.push({	property:"properties.isSentCCRT.value",
					header: "readsets.properties.isSentCCRT",
					type :"boolean",
					order:true,
			    	edit:true
    	});
	columns.push({	property:"properties.isSentCollaborator.value",
					header: "readsets.properties.isSentCollaborator",
					type :"boolean",
					order:true,
			    	edit:true
    	});
		
	return columns;
};


function getValuationWheatColumns(columns, $filter){
		columns.push({	property:"treatments['mergingNoRiboClean'].pairs.mergedReadsPercent.value",
						header: "Merged Reads",
						type :"number",
				    	order:true
		});			
		columns.push({	property:"treatments['mergingNoRiboClean'].pairs.medianeSize.value",
						header: "Mediane Size (bases)",
						type :"number",
				    	order:true
		});			
		columns.push({	property:"treatments['mappingNoRiboClean'].pairs.RFAlignedReadsPercent.value",
						header: "% RF (MP) aligned reads",
						type :"number",
				    	order:true
		});			
		columns.push({	property:"treatments['mappingNoRiboClean'].pairs.estimatedMPInsertSize.value",
					header: "Estimated MP insert size",
					type :"number",
			    	order:true
			    	});		
		columns.push({	property:"taxon.totalPercent",
						render:function(value){
							return calculTaxonPcts(value, "Escherichia coli", $filter);
						},
						header: "% Escherichia coli", 
						type :"number",
				    	order:true
		});
		columns.push({	property:"taxon.totalPercent",
						render:function(value){
							return calculTaxonPcts(value, "Triticum", $filter);
						},
						header: "% Triticum",
						type :"number",
				    	order:true
		});
		return columns;
};

function convertForm(iform){
	var form = angular.copy(iform);
	if(form.fromDate)form.fromDate = moment(form.fromDate, Messages("date.format").toUpperCase()).valueOf();
	if(form.toDate)form.toDate = moment(form.toDate, Messages("date.format").toUpperCase()).valueOf();		
	return form
};

function updateForm(form, page){
	if (page && page.indexOf('valuation') == 0) {
		if(form.stateCodes === undefined || form.stateCodes.length === 0) {
			//No stateCodes selected, the filter by default (on the only two possible states for the valuation) is applied
			form.stateCodes = ["IW-V","IP-V"];
		}		
	}
	form.excludes = ["files"];
	return form;
}


function SearchFormCtrl($scope, $filter, lists){
	$scope.lists = lists;
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	};
	
	$scope.search = function(){
		$scope.form = updateForm($scope.form, $scope.getHomePage());
		$scope.setForm($scope.form);
		$scope.datatable.search(convertForm($scope.form));
	};
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	};
	
	$scope.init = function(){		
		if ($scope.isHomePage('valuation') || $scope.isHomePage('valuationWheat')) {
			//If we want to show the 2 states used to filter the data...
			//$scope.form.stateCodes = ["IW-V","IP-V"];
			//Reduce data to the set of states specific to the valuation
			$scope.states = [{code:"IW-V",name:Codes("state.IW-V")},{code:"IP-V",name:Codes("state.IP-V")}];
		}
		
		$scope.lists.refresh.projects();
		$scope.lists.refresh.states({objectTypeCode:"ReadSet"});
		$scope.lists.refresh.types({objectTypeCode:"Run"});
		$scope.lists.refresh.runs();
		$scope.lists.refresh.instruments({categoryCode:"seq-illumina"});
		$scope.lists.refresh.reportConfigs({pageCodes:["readsets"]});
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
	};
	
};
SearchFormCtrl.$inject = ['$scope', '$filter', 'lists'];

function SearchCtrl($scope, $routeParams, datatable) {

	$scope.datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.get(line.code).url,remove:true});
				}
			},
			columns : getSearchColumns(getCommonColumns([]))
	};
	
	
	
	$scope.init = function(){
		//to avoid to lost the previous search		
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);
			$scope.setDatatable($scope.datatable);			
			$scope.datatable.search(convertForm(updateForm($routeParams,'search')));
		}else{
			$scope.datatable = $scope.getDatatable();			
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("search").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
		$scope.taxons= [{name:"Escherichia coli",composition:["Escherichia coli"]},{name:"Triticum",composition:["Triticum aestivum", "Triticeae", "Triticum"]}];
	}	
};

SearchCtrl.$inject = ['$scope', '$routeParams', 'datatable'];

function SearchStateCtrl($scope,  datatable, lists) {

	$scope.listsTable = lists;
	
	$scope.datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},
			save : {
				active:true,
				url: jsRoutes.controllers.readsets.api.ReadSets.stateBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code,state:line.state};}				
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.get(line.code).url,remove:true});
				}
			},
			columns : getStateColumns(getCommonColumns([]))
	};
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);
			$scope.datatable.search(updateForm({},'state'));
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("state").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
		$scope.listsTable.refresh.states({objectTypeCode:"ReadSet"});		
	}	
};

SearchStateCtrl.$inject = ['$scope', 'datatable', 'lists'];


function SearchValuationCtrl($scope, $http, datatable, lists, $routeParams) {

	$scope.listsTable = lists;
	
	var datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},			
			save : {
				active:true,
				url: jsRoutes.controllers.readsets.api.ReadSets.valuationBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code,productionValuation:line.productionValuation,bioinformaticValuation:line.bioinformaticValuation};}				
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.valuation(line.code).url,remove:true});
				}
			},
			columns : getValuationColumns(getCommonColumns([]))
	};
	
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, datatableConfig);
			$scope.setDatatable($scope.datatable);
			
			
			//$http.get(jsRoutes.controllers.reporting.api.ReportingConfigurations.get("RC-20140331094258").url)
		//		.success(function(data) {
		//			datatableConfig.columns = datatableConfig.columns.concat(data.columns);
					
			//	});
			
			
		}
		
		
		var count = 0;
		for(var p in $routeParams){
			count++;
		}
		
		if(count > 0){
			$scope.datatable.search(updateForm($routeParams));
		}else{
			$scope.datatable.search(updateForm({},'valuation'));
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('valuation');
			$scope.addTabs({label:Messages('readsets.page.tab.validate'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("valuation").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
	};
	
};

SearchValuationCtrl.$inject = ['$scope', '$http', 'datatable', 'lists', '$routeParams'];

function SearchValuationWheatCtrl($scope, datatable, lists, $routeParams, $filter) {
	
	$scope.listsTable = lists;
	
	$scope.datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},			
			save : {
				active:true,
				url: jsRoutes.controllers.readsets.api.ReadSets.valuationBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code,productionValuation:line.productionValuation,bioinformaticValuation:line.bioinformaticValuation};}				
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.valuation(line.code).url,remove:true});
				}
			},
			columns : getValuationColumns(getValuationWheatColumns(getCommonColumns([]), $filter))
	};
	
	
	$scope.init = function(){
				
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		var count = 0;
		for(var p in $routeParams){
			count++;
		}
		
		if(count > 0){
			$scope.datatable.search(updateForm($routeParams));
		}else{
			$scope.datatable.search(updateForm({},'valuationWheat'));
		}		
		
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('valuationWheat');
			$scope.addTabs({label:Messages('readsets.page.tab.validate'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("valuationWheat").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchValuationWheatCtrl.$inject = ['$scope', 'datatable', 'lists', '$routeParams', '$filter'];



function findTaxonPct(value, element, $filter) {
	var pct = 0;
	var taxonData = value.treatments["taxonomyClean"].read1.taxonBilan.value;	
	var objTaxon = $filter("filter")(taxonData, {taxon:element});
	if (objTaxon.length > 0) {
		pct = objTaxon[0].percent;
	}	
	return pct;
}

function calculTaxonPcts(value, taxonName, $filter) {
	var taxons = [{name:"Escherichia coli",composition:["Escherichia coli"]},{name:"Triticum",composition:["Triticum aestivum", "Triticeae", "Triticum"]}]; 
	var pct = null;
	if (value.treatments["taxonomyClean"] !== undefined) {
		for(var i=0; i<taxons.length; i++) {
			if (taxons[i].name == taxonName) {
				var pct = 0;
				for (var j=0; j<taxons[i].composition.length; j++) {	
					pct += findTaxonPct(value, taxons[i].composition[j], $filter);
				}
			}
		}
	}
	return $filter('number')(pct, 2);
}

function SearchBatchCtrl($scope,  datatable) {

	$scope.datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},
			save : {
				active:true,
				url: jsRoutes.controllers.readsets.api.ReadSets.propertiesBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code, properties : line.properties};}				
			},	
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.valuation(line.code).url,remove:true});
				}
			},
			columns : getBatchColumns(getCommonColumns([]))
	};
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);
			$scope.datatable.search(updateForm({}));
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("batch").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchBatchCtrl.$inject = ['$scope', 'datatable'];


angular.module('home').controller('ReportCtrl', ['$scope', '$http', '$filter', function($scope, $http, $filter) {
	
	var convertJavaValueTypeToJSValueType = function(valueType){
		switch(valueType) {
			case 'java.lang.String':
				valueType = 'text';
				break;
			case 'java.lang.Integer':
				valueType = 'number';
				break;
			case 'java.lang.Double':
				valueType = 'number';
				break;
			case 'java.lang.Float':
				valueType = 'number';
				break;
			case 'java.lang.Long':
				valueType = 'number';
				break;
			case 'java.lang.Date':
				valueType = 'date';
				break;
			case 'java.lang.Boolean':
				valueType = 'boolean';
				break;
			case 'java.awt.Image':
				valueType = 'img';
				break;
			case 'java.io.File':
				valueType = 'file';
				break;			
			default:
				throw 'not managed :'+valueType;
		}
		return valueType;
	};
	var convertPropertyValueTypeToType = function(propertyValueType){
		switch(propertyValueType) {
			case 'single':
				propertyValueType = 'single';
				break;
			case 'list':
				propertyValueType = 'list';
				break;
			case 'file':
				propertyValueType = 'single';
				break;
			case 'img':
				propertyValueType = 'single';
				break;
			case 'map':
				propertyValueType = 'map';
				break;
			case 'object':
				propertyValueType = 'single';
				break;
			case 'object_list':
				propertyValueType = 'list';
				break;
			default:
				throw 'not managed :'+propertyValueType;
		}
		return propertyValueType;
	};
	var convertPropertyValueTypeToIsObject = function(propertyValueType){
		switch(propertyValueType) {
			case 'single':
				propertyValueType = false;
				break;
			case 'list':
				propertyValueType = false;
				break;
			case 'file':
				propertyValueType = false;
				break;
			case 'img':
				propertyValueType = false;
				break;
			case 'map':
				propertyValueType = false;
				break;
			case 'object':
				propertyValueType = true;
				break;
			case 'object_list':
				propertyValueType = true;
				break;
			default:
				throw 'not managed :'+propertyValueType;
		}
		return propertyValueType;
	};
	/**
	 * Convert propertyDefinition in property and filter with level
	 */
	var getProperties = function(treatmentType, level){
		var properties = {};
		var propertiesDef = $filter('filter')(treatmentType.propertiesDefinitions, level)
		
		angular.forEach(propertiesDef, function(value, key){
			if(!this[value.code.split(".")[0]]){
				this[value.code.split(".")[0]] = {code:value.code.split(".")[0], name:value.name.split(".")[0], levels:value.levels,
						type:convertPropertyValueTypeToType(value.propertyValueType), isObject:convertPropertyValueTypeToIsObject(value.propertyValueType), subProperties:[]};
			}
			this[value.code.split(".")[0]].subProperties.push({code:value.code, name:value.name, 
				format:value.displayFormat,	valueType:convertJavaValueTypeToJSValueType(value.valueType)});
			
		}, properties);
		
		var propertiesA = [];
		
		for(var key in properties){
			propertiesA.push(properties[key]);
		}
		
		return propertiesA;
	};
	
	var init = function(){
		$http.get(jsRoutes.controllers.treatmenttypes.api.TreatmentTypes.list().url, {params:{levels: "ReadSet"}}).success(function(data) {
			var treatmentTypes = [];
			angular.forEach(data, function(value, key){
				var properties = getProperties(value, "ReadSet");
				var names = value.names.split(',');
				var orders = value.displayOrders.split(',');
				for(var i = 0 ; i < names.length; i++){
					var contexts = [];
					for(var j = 0; j < value.contexts.length ; j++){
						contexts.push({code:value.contexts[j].code, properties:$filter('filter')(properties, value.contexts[j].code)})
					}
					this.push({code:value.code, name:value.name, instanceCode:names[i], 
						displayOrder:Number(orders[i]), contexts:contexts })	
				}
							
			}, treatmentTypes);
			$scope.treatmentTypes = treatmentTypes;			
		});
		
		
	};
	
	init();
	
}]);


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
	
	columns.push({	property:"productionValuation.resolutionCodes",
					header: "readsets.productionValuation.resolutions",
					render:'<div bt-select ng-model="value.data.productionValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in listsTable.getResolutions()" ng-edit="false"></div>',
					type :"text",
					hide:true});
	
	columns.push({	property:"bioinformaticValuation.valid",
					filter:"codes:'valuation'",
					header: "readsets.bioinformaticValuation.valid",
					type :"text",
			    	order:true});
	
	columns.push({	property:"bioinformaticValuation.resolutionCodes",
					header: "readsets.bioinformaticValuation.resolutions",
					render:'<div bt-select ng-model="value.data.bioinformaticValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in listsTable.getResolutions()" ng-edit="false"></div>',
					type :"text",
					hide:true});
	
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
	
	columns.push({	property:"productionValuation.valid",
					filter:"codes:'valuation'",
					header: "readsets.productionValuation.valid",
					type :"text",
			    	order:true    	
	});
	
	
	columns.push({	property:"bioinformaticValuation.valid",
					filter:"codes:'valuation'",
					header: "readsets.bioinformaticValuation.valid",
					type :"text",
					order:true
	});

	
	return columns;
};
function getValuationColumns(columns){	
	
	columns.push({	property:"state.code",
					filter:"codes:'state'",
					header: "readsets.stateCode",
					type :"text",
					order:true});
	
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
	
	columns.push({	property:"productionValuation.criteriaCode",
					filter:"codes:'valuation_criteria'",
					header: "readsets.productionValuation.criteria",
					type :"text",
			    	edit:true,
			    	choiceInList:true,
			    	listStyle:'bt-select',
			    	possibleValues:'listsTable.getValuationCriterias()'
    });
	
	columns.push({	property:"productionValuation.resolutionCodes",
					header: "readsets.productionValuation.resolutions",
					render:'<div bt-select ng-model="value.data.productionValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in listsTable.getResolutions()" ng-edit="false"></div>',
					type :"text",
			    	edit:true,
			    	choiceInList:true,
			    	listStyle:'bt-select-multiple',
			    	possibleValues:'listsTable.getResolutions()',
			    	groupBy:'category.name'
			    		
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
	
	columns.push({	property:"productionValuation.valid",
					filter:"codes:'valuation'",
					header: "readsets.productionValuation.valid",
					type :"text",
			    	order:true    	
    	});
	
	
	columns.push({	property:"bioinformaticValuation.valid",
					filter:"codes:'valuation'",
					header: "readsets.bioinformaticValuation.valid",
					type :"text",
					order:true
    	});
	
	
	
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

function convertForm(iform){
	var form = angular.copy(iform);
	if(form.fromDate)form.fromDate = moment(form.fromDate, Messages("date.format").toUpperCase()).valueOf();
	if(form.toDate)form.toDate = moment(form.toDate, Messages("date.format").toUpperCase()).valueOf();		
	
	if(form.reportingConfigurationCode)form.reportingConfigurationCode=undefined;
	
	return form
};

function updateForm(form, page, $scope){
	if (page && page.indexOf('valuation') == 0) {
		if(form.stateCodes === undefined || form.stateCodes.length === 0) {
			//No stateCodes selected, the filter by default (on the only two possible states for the valuation) is applied
			form.stateCodes = ["IW-VQC", "IW-VBA"];
		}		
	}
	if($scope && $scope.datatableConfigCustom.reportingConfiguration && $scope.datatableConfigCustom.reportingConfiguration.queryConfiguration){
		var queryParams = $scope.datatableConfigCustom.reportingConfiguration.queryConfiguration;
		if(queryParams && queryParams.includeKeys && queryParams.includeKeys.length > 0){
			form.includes = queryParams.includeKeys;
		}else if(queryParams && queryParams.excludeKeys && queryParams.excludeKeys.length > 0){
			form.excludes = queryParams.excludeKeys;
		}else{
			form.excludes = ["files", "treatments"];
		}
	}else{
		form.excludes = ["files", "treatments"];
	}
	return form;
}


angular.module('home').controller('SearchFormCtrl', ['$scope', '$filter', '$http', 'lists', function($scope, $filter, $http, lists){
	$scope.lists = lists;
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	};
	
	$scope.search = function(){
		$scope.form = updateForm($scope.form, $scope.getHomePage(), $scope);
		$scope.setForm($scope.form);
		$scope.datatable.search(convertForm($scope.form));
	};
	
	$scope.reset = function(){
		
		$scope.form = {
				
		}
	};
	
	
	$scope.updateColumn = function(){
		if($scope.datatableConfigCustom.reportingConfigurationCode){
			$http.get(jsRoutes.controllers.reporting.api.ReportingConfigurations.get($scope.datatableConfigCustom.reportingConfigurationCode).url)
					.success(function(data) {
						$scope.datatableConfigCustom.reportingConfiguration = data;
						$scope.search();
						$scope.datatable.setColumnsConfig(data.columns);						
			});
		}else{
			$scope.datatableConfigCustom.reportingConfiguration = undefined;
			$scope.datatable.setColumnsConfig($scope.datatableConfigCustom.defaultColumns);
			$scope.search();
		}
		
	}
	
	var init = function(){		
		if ($scope.isHomePage('valuation') || $scope.isHomePage('valuationWheat')) {
			//If we want to show the 2 states used to filter the data...
			//$scope.form.stateCodes = ["IW-V","IP-V"];
			//Reduce data to the set of states specific to the valuation
			$scope.states = [{code:"IW-VQC",name:Codes("state.IW-VQC")}];
		}
		
		$scope.lists.refresh.projects();
		$scope.lists.refresh.states({objectTypeCode:"ReadSet"});
		$scope.lists.refresh.types({objectTypeCode:"Run"});
		$scope.lists.refresh.runs();
		$scope.lists.refresh.instruments({categoryCode:"seq-illumina"});
		$scope.lists.refresh.reportConfigs({pageCodes:["readsets"+"-"+$scope.getHomePage()]});
		$scope.lists.refresh.resolutions({objectTypeCode:"ReadSet"});
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
		
		$scope.datatableConfigCustom = {};
		$scope.datatableConfigCustom.defaultColumns = $scope.datatable.getColumnsConfig();
	};
	
	
	init();
}]);

angular.module('home').controller('SearchCtrl',[ '$scope', '$routeParams', 'datatable', '$parse','lists',  function($scope, $routeParams, datatable, $parse, lists) {

	$scope.listsTable = lists;
	
	var datatableConfig = {
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
			hide:{
				active:true
			},
			columns : getSearchColumns(getCommonColumns([]))
	};

	var init = function(){
		//to avoid to lost the previous search		
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, datatableConfig);
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
		$scope.listsTable.refresh.resolutions({objectTypeCode:"ReadSet"});
	};	
	
	init();
}]);


angular.module('home').controller('SearchStateCtrl', ['$scope', 'datatable', 'lists', function($scope, datatable, lists) {

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
	
	var init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, datatableConfig);
			$scope.datatable.search(updateForm({},'state'));
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('state');
			$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("state").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
		$scope.listsTable.refresh.states({objectTypeCode:"ReadSet"});		
	};
	
	init();
}]);


angular.module('home').controller('SearchValuationCtrl', ['$scope', '$http', 'datatable', 'lists', '$routeParams', '$parse', 
                                                          function($scope, $http, datatable, lists, $routeParams, $parse) {

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
			hide:{
				active:true
			},
			columns : getValuationColumns(getCommonColumns([]))
	};
	
	var criterias = {};
	
	$scope.valuationCriteriaClass = function(value, propertyName){
		
		if(value.productionValuation.criteriaCode && criterias[value.productionValuation.criteriaCode]){
			
			var criteria = criterias[value.productionValuation.criteriaCode];
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
	
	
	var init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, datatableConfig);
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
			$scope.datatable.search(updateForm({},'valuation'));
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('valuation');
			$scope.addTabs({label:Messages('readsets.page.tab.validate'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("valuation").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}

		$scope.listsTable.refresh.resolutions({objectTypeCode:"ReadSet"});
		$scope.listsTable.refresh.valuationCriterias({objectTypeCode:"ReadSet"});
		
		$scope.$watch("listsTable.getValuationCriterias()", function(newValue, oldValue){
			if(newValue && newValue.length > 0){
				for(var i = 0 ; i < newValue.length; i++){
					criterias[newValue[i].code] = newValue[i]; 
				}
			}
		});
	};
	
	init();
	
}]);

angular.module('home').controller('SearchBatchCtrl', ['$scope',  'datatable', function($scope,  datatable) {

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
	
	var init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, datatableConfig);
			$scope.datatable.search(updateForm({}));
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('batch');
			$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("batch").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
	};
	
	init();
}]);

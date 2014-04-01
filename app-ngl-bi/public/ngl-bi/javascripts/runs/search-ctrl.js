"use strict";

function getColumns(){
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
			    	possibleValues:'listsTable.getStates()'	
				},
				{	property:"valuation.valid",
					filter:"codes:'valuation'",					
					header: "runs.valuation.valid",
					type :"String",
			    	order:true
				}      
			];						
	return columns;
}
function convertForm(iform){
	var form = angular.copy(iform);
	if(form.fromDate)form.fromDate = moment(form.fromDate, Messages("date.format").toUpperCase()).valueOf();
	if(form.toDate)form.toDate = moment(form.toDate, Messages("date.format").toUpperCase()).valueOf();		
	return form
};

function updateForm(form, page){
	if (page === 'valuation') {
		if(form.stateCodes === undefined || form.stateCodes.length === 0) {
			//No stateCodes selected, the filter by default (on the only two possible states for the valuation) is applied
			form.stateCodes = ["IW-V","IP-V"];
		}		
	}
	form.excludes = ["treatments","lanes"];
	return form;
}

function SearchFormCtrl($scope, $filter, lists){
	$scope.lists = lists;
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	}
	
	$scope.search = function(){
		$scope.form = updateForm($scope.form, $scope.getHomePage());
		$scope.setForm($scope.form);		
		$scope.datatable.search(convertForm($scope.form));
	}
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	}
	
	$scope.init = function(){
		$scope.lists.refresh.projects();
		$scope.lists.refresh.states({objectTypeCode:"Run"});
		$scope.lists.refresh.types({objectTypeCode:"Run"});
		$scope.lists.refresh.runs();
		$scope.lists.refresh.instruments({categoryCode:"seq-illumina"});
		
		if(angular.isDefined($scope.getForm())){
			$scope.form = $scope.getForm();
		}else{
			$scope.reset();
		}
	}
	
};
SearchFormCtrl.$inject = ['$scope', '$filter', 'lists'];

function SearchCtrl($scope, $routeParams, datatable) {

	$scope.datatableConfig = {
			order :{by:'sequencingStartDate', reverse:true},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.runs.tpl.Runs.get(line.code).url,remove:true});
				}
			},
			columns : getColumns()
	};
	
	
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);			
			$scope.datatable.search(updateForm(convertForm($routeParams),'search'));
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("search").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchCtrl.$inject = ['$scope', '$routeParams', 'datatable'];

function SearchStateCtrl($scope, datatable, lists) {

	$scope.listsTable = lists;
	
	$scope.datatableConfig = {
			order :{by:'sequencingStartDate', reverse:true},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},
			save : {
				active:true,
				url: jsRoutes.controllers.runs.api.State.updateBatch().url,				
				batch:true,
				method:'put',
				value:function(line){return {code:line.code,state:line.state};}				
			},
			
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.runs.tpl.Runs.get(line.code).url,remove:true});
				}
			},
			columns : getColumns(),
			messages : {active:true}
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
			$scope.setHomePage('state');
			$scope.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("state").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
		$scope.listsTable.refresh.states({objectTypeCode:"Run"});
	}	
};

SearchStateCtrl.$inject = ['$scope', 'datatable', 'lists'];


function SearchValuationCtrl($scope, datatable) {

	$scope.datatableConfig = {
			order :{by:'sequencingStartDate', reverse:true},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.runs.tpl.Runs.valuation(line.code).url,remove:true});
				}
			},
			columns : getColumns()
	};
	
	
	$scope.init = function(){
		//to avoid to lost the previous search
		if(angular.isUndefined($scope.getDatatable())){
			$scope.datatable = datatable($scope, $scope.datatableConfig);
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		$scope.datatable.search(updateForm({},'valuation'));
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('valuation');
			$scope.addTabs({label:Messages('runs.page.tab.validate'),href:jsRoutes.controllers.runs.tpl.Runs.home("valuation").url,remove:true});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchValuationCtrl.$inject = ['$scope','datatable'];




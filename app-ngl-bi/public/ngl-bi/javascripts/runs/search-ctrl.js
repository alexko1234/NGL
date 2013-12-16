"use strict";

function getColumns(){
var columns = [
			    {  	property:"code",
			    	header: Messages("runs.code"),
			    	type :"String",
			    	order:true
				},
				{	property:"typeCode",
					header: Messages("runs.typeCode"),
					type :"String",
			    	order:true
				},
				{	property:"sequencingStartDate",
					header: Messages("runs.sequencingStartDate"),
					type :"Date",
			    	order:true
				},
				{	property:"state.code",
					render:function(value){
						return Codes("state."+value.state.code);
					},
					header: Messages("runs.stateCode"),
					type :"String",
					edit:true,
					order:true,
			    	choiceInList:true,
			    	listStyle:'bs-select',
			    	//possibleValues:[{code:"IW-QC",name:Codes("state.IW-QC")},{code:"IW-V",name:Codes("state.IW-V")},{code:"F-V",name:Codes("state.F-V")}, {code:"F",name:Codes("state.F")}]
			    	possibleValues:'listsTable.getStates()'	
				},
				{	property:"valuation.valid",
					render:function(value){
						return Codes("valuation."+value.valuation.valid);
					},
					header: Messages("runs.valuation.valid"),
					type :"String",
			    	order:true
				}      
			];						
	return columns;
}

function SearchFormCtrl($scope, $filter, lists){
	$scope.lists = lists;
	
	$scope.form = {
			
	}

	var search = function(values, query){
		var queryElts = query.split(',');
		
		var lastQueryElt = queryElts.pop();
		
		var output = [];
		angular.forEach($filter('filter')(values, lastQueryElt), function(value, key){
			if(queryElts.length > 0){
				this.push(queryElts.join(',')+','+value.code);
			}else{
				this.push(value.code);
			}
		}, output);
		
		return output;
	}
	
	$scope.searchProjects = function(query){
		return search(lists.getProjects(), query);
				
	}
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCodes){
			lists.refresh.samples({projectCodes:$scope.form.projectCodes.split(',')});
		}
	}
	
	$scope.searchSamples = function(query){
		return search(lists.getSamples(), query);				
	}
	
	$scope.search = function(){
		var form = angular.copy($scope.form);
		if(form.fromDate)form.fromDate = moment(form.fromDate, Messages("date.format").toUpperCase()).valueOf();
		if(form.toDate)form.toDate = moment(form.toDate, Messages("date.format").toUpperCase()).valueOf();	
		if(form.projectCodes) form.projectCodes = form.projectCodes.split(',')
		if(form.sampleCodes) form.sampleCodes = form.sampleCodes.split(',')
		$scope.datatable.search(form);
	}
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	}
	
	$scope.init = function(){
		$scope.lists.refresh.projects();
		$scope.lists.refresh.states({objectTypeCode:"Run"});
		$scope.lists.refresh.types({objectTypeCode:"Run"});
	}
	
};
SearchFormCtrl.$inject = ['$scope', '$filter', 'lists'];

function SearchCtrl($scope, $routeParams, datatable) {

	$scope.datatableConfig = {
			order :{by:'traceInformation.creationDate'},
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
			$scope.datatable.search();
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("search").url,remove:false});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchCtrl.$inject = ['$scope', '$routeParams', 'datatable'];

function SearchStateCtrl($scope, datatable, lists) {

	$scope.listsTable = lists;
	
	$scope.datatableConfig = {
			order :{by:'traceInformation.creationDate'},
			search:{
				url:jsRoutes.controllers.runs.api.Runs.list()
			},
			edit : {
				active:true,
				columnMode:true		    	
			},
			save : {
				active:true,
				url:function(line){
					return jsRoutes.controllers.runs.api.Runs.state(line.code, line.state.code).url;
				},
				method:'put',
				value:function(line){return line.state;}
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
			$scope.datatable.search();
			$scope.setDatatable($scope.datatable);
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('runs.menu.search'),href:jsRoutes.controllers.runs.tpl.Runs.home("search").url,remove:false});
			$scope.activeTab(0); // desactive le lien !
		}
		$scope.listsTable.refresh.states({objectTypeCode:"Run"});
	}	
};

SearchStateCtrl.$inject = ['$scope', 'datatable', 'lists'];


function SearchValuationCtrl($scope, datatable) {

	$scope.datatableConfig = {
			order :{by:'traceInformation.creationDate'},
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
		$scope.datatable.search({stateCodes:["IW-V","IP-V"]});
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('valuation');
			$scope.addTabs({label:Messages('runs.page.tab.validate'),href:jsRoutes.controllers.runs.tpl.Runs.home("valuation").url,remove:false});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchValuationCtrl.$inject = ['$scope','datatable'];




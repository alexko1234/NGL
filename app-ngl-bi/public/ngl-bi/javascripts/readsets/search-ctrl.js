"use strict";


function getColumns(page){
	var columns = [];
	
	columns.push({	property:"code",
		    	  	header: Messages("readsets.code"),
		    	  	type :"String",
		    	  	order:true});
	
	columns.push({	property:"runCode",
					header: Messages("readsets.runCode"),
					type :"String",
					order:true});
	columns.push({	property:"laneNumber",
					header: Messages("readsets.laneNumber"),
					type :"String",
					order:true});
	columns.push({	property:"projectCode",
					header: Messages("readsets.projectCode"),
					type :"String",
					order:true});
	columns.push({	property:"sampleCode",
					header: Messages("readsets.sampleCode"),
					type :"String",
					order:true});
	columns.push({	property:"runSequencingStartDate",
					header: Messages("runs.sequencingStartDate"),
					type :"Date",
					order:true});	
	
	if('state' === page){
		columns.push({	property:"state.code",
						render:function(value){
							return Codes("state."+value.state.code);
						},
						header: Messages("readsets.stateCode"),
						type :"String",
						edit:true,
						order:true,
				    	choiceInList:true,
				    	listStyle:'bt-select',
				    	//possibleValues:[{code:"IW-QC",name:Codes("state.IW-QC")},{code:"IW-V",name:Codes("state.IW-V")},{code:"F-V",name:Codes("state.F-V")}, {code:"F",name:Codes("state.F")}]
						possibleValues:'listsTable.getStates()'});
	}else{
		columns.push({	property:"state.code",
						render:function(value){
							return Codes("state."+value.state.code);
						},
						header: Messages("readsets.stateCode"),
						type :"String",
						order:true});
	}
	
	if('valuation' === page){
		columns.push({	property:"productionValuation.valid",
						render:function(value){
							return Codes("valuation."+value.productionValuation.valid);
						},
						header: Messages("readsets.productionValuation.valid"),
						type :"String",
				    	order:true,
				    	edit:true,
				    	choiceInList:true,
				    	listStyle:'bt-select',
				    	possibleValues:'listsTable.getValuations()'
				    	});
		columns.push({	property:"bioinformaticValuation.valid",
						render:function(value){
							return Codes("valuation."+value.bioinformaticValuation.valid);
						},
						header: Messages("readsets.bioinformaticValuation.valid"),
						type :"String",
						order:true,
				    	edit:true,
				    	choiceInList:true,
				    	listStyle:'bt-select',
				    	possibleValues:'listsTable.getValuations()'
				    	});
	}else{
		columns.push({	property:"productionValuation.valid",
						render:function(value){
							return Codes("valuation."+value.productionValuation.valid);
						},
						header: Messages("readsets.productionValuation.valid"),
						type :"String",
				    	order:true});
		columns.push({	property:"bioinformaticValuation.valid",
						render:function(value){
							return Codes("valuation."+value.bioinformaticValuation.valid);
						},
						header: Messages("readsets.bioinformaticValuation.valid"),
						type :"String",
				    	order:true});
	}
	
	if('batch' === page){
		columns.push({	property:"properties.isSentCCRT.value",
			
			header: Messages("readsets.properties.isSentCCRT"),
			type :"Boolean",
			order:true,
	    	edit:true
	    	});
		columns.push({	property:"properties.isSentCollaborator.value",
			
			header: Messages("readsets.properties.isSentCollaborator"),
			type :"Boolean",
			order:true,
	    	edit:true
	    	});
		
	}
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
	form.excludes = ["treatments","files"];
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
	
	$scope.init1 = function(){		
		if ($scope.isHomePage('valuation')) {
			//If we want to show the 2 states used to filter the data...
			//$scope.form.stateCodes = ["IW-V","IP-V"];
			//Reduce data to the set of states specific to the valuation
			$scope.states = [{code:"IW-V",name:Codes("state.IW-V")},{code:"IP-V",name:Codes("state.IP-V")}];
		}
		
		$scope.lists.refresh.projects();
		$scope.lists.refresh.states({objectTypeCode:"ReadSet"});
		$scope.lists.refresh.types({objectTypeCode:"Run"});
		$scope.lists.refresh.runs();
		
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
			columns : getColumns('search')
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
			$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("search").url,remove:false});
			$scope.activeTab(0); // desactive le lien !
		}
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
				url:function(line){
					return jsRoutes.controllers.readsets.api.ReadSets.state(line.code).url;
				},
				method:'put',
				value:function(line){return line.state;}
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.get(line.code).url,remove:true});
				}
			},
			columns : getColumns('state')
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
			$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("state").url,remove:false});
			$scope.activeTab(0); // desactive le lien !
		}
		$scope.listsTable.refresh.states({objectTypeCode:"ReadSet"});		
	}	
};

SearchStateCtrl.$inject = ['$scope', 'datatable', 'lists'];


function SearchValuationCtrl($scope, datatable, lists, $routeParams) {

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
				url:function(line){
					return jsRoutes.controllers.readsets.api.ReadSets.valuation(line.code).url;
				},
				method:'put',
				value:function(line){return {productionValuation:line.productionValuation,bioinformaticValuation:line.bioinformaticValuation};}
			},
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:jsRoutes.controllers.readsets.tpl.ReadSets.valuation(line.code).url,remove:true});
				}
			},
			columns : getColumns('valuation')
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
			$scope.datatable.search(updateForm({},'valuation'));
		}
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('valuation');
			$scope.addTabs({label:Messages('readsets.page.tab.validate'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("valuation").url,remove:false});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchValuationCtrl.$inject = ['$scope', 'datatable', 'lists', '$routeParams'];

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
				url:function(line){
					return jsRoutes.controllers.readsets.api.ReadSets.properties(line.code).url;
				},
				method:'put',
				value:function(line){return {properties : line.properties};}
			},
			columns : getColumns('batch')
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
			$scope.addTabs({label:Messages('readsets.menu.search'),href:jsRoutes.controllers.readsets.tpl.ReadSets.home("batch").url,remove:false});
			$scope.activeTab(0); // desactive le lien !
		}
	}	
};

SearchBatchCtrl.$inject = ['$scope', 'datatable'];



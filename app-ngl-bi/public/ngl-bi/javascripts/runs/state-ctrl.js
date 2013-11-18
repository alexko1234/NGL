"use strict";

function SearchStateCtrl($scope, datatable) {

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
				value:function(line){return {};}
			},
			columns : [
			    {  	property:"code",
			    	header: Messages("runs.table.code"),
			    	type :"String",
			    	order:true
				},
				{	property:"typeCode",
					header: Messages("runs.table.typeCode"),
					type :"String",
			    	order:true
				},
				{	property:"traceInformation.creationDate",
					header: Messages("runs.table.creationdate"),
					type :"Date",
			    	order:true
				},
				{	property:"state.code",
					header: Messages("runs.table.stateCode"),
					type :"String",
					edit:true,
					order:true,
			    	choiceInList:true,
			    	listStyle:'bs-select',
			    	possibleValues:[{code:"IW-V",name:"IW-V"},{code:"E",name:"E"}, {code:"F",name:"F"}]				
				},
				{	property:"validation.valid",
					header: Messages("runs.table.validation.valid"),
					type :"String",
			    	order:true
				},       
			]
				
			
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

SearchStateCtrl.$inject = ['$scope', 'datatable'];

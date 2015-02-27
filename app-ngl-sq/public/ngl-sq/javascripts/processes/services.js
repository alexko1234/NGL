 "use strict";
 
 angular.module('ngl-sq.processesServices', []).
	factory('processesSearchService', ['$http', 'mainService', 'lists', 'datatable', function($http, mainService, lists, datatable){
		/*var getColumns = function(){
			var typeCode = "";
			if(this.form.typeCode){
				typeCode = this.form.typeCode;
			}
			
			$http.get(jsRoutes.controllers.processes.tpl.Processes.searchColumns().url,{params:{"typeCode":typeCode}})
			.success(function(data, status, headers, config) {
				if(data!=null){
					searchService.datatable.setColumnsConfig(data);
				}
			})
			.error(function(data, status, headers, config) {
			
			});
		};*/
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				lists.refresh.containerSupportCategories();
				lists.refresh.projects();
				lists.refresh.processCategories();
				lists.refresh.containerSupports();
				lists.refresh.users();
				lists.refresh.states({objectTypeCode:"Process"});
				lists.refresh.processTypes();
				isInit=true;
			}
		};
		
		var searchService = {
				columnsDefault:[
						         {
						        	 "header":Messages("processes.table.containerInputCode"),
						        	 "property":"containerInputCode",
						        	 "position":1,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.sampleCode"),
						        	 "property":"sampleCode",
						        	 "position":2,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.columns"),
						        	 "property":"sampleOnInputContainer.properties.tag.value",
						        	 "position":3,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.properties.tag"),
						        	 "property":"sampleOnInputContainer.properties.tag.value",
						        	 "position":4,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.mesuredVolume"),
						        	 "property":"sampleOnInputContainer.mesuredVolume.value",
						        	 "position":5,
						        	 "type":"text"
						         },  
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.unit.volume"),
						        	 "property":"sampleOnInputContainer.mesuredVolume.unit",
						        	 "position":6,
						        	 "type":"text"
						         },	
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.mesuredConcentration"),
						        	 "property":"sampleOnInputContainer.mesuredConcentration.value",
						        	 "position":7,
						        	 "type":"text"
						         },	 	
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.unit.concentration"),
						        	 "property":"sampleOnInputContainer.mesuredConcentration.unit",
						        	 "position":8,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.typeCode"),
						        	 "property":"typeCode",
						        	 "position":9,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.stateCode"),
						        	 "property":"state.code",
						        	 "position":30,
						        	 "type":"text",
						        	 "filter": "codes:'state'",
						        	 "edit":false,
						        	 "choiceInList": true,
						 			 "possibleValues":"searchService.lists.getStates()", 
						         },
						         {
						        	 "header":Messages("processes.table.resolutionCode"),
						        	 "property":"state.resolutionCodes",
						        	 "position":31,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.currentExperimentTypeCode"),
						        	 "property":"currentExperimentTypeCode",
						        	 "position":32,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.code"),
						        	 "property":"code",
						        	 "position":33,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.creationDate"),
						        	 "property":"traceInformation.creationDate",
						        	 "position":34,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.newContainerSupportCodes"),
						        	 "property":"newContainerSupportCodes",
						        	 "position":35,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.experimentCodes"),
						        	 "property":"experimentCodes",
						        	 "position":36,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.projectCode"),
						        	 "property":"projectCode",
						        	 "position":37,
						        	 "type":"text"
						         }
						 ],
						 columnsDefaultState:[
						         {
						        	 "header":Messages("processes.table.containerInputCode"),
						        	 "property":"containerInputCode",
						        	 "position":1,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.sampleCode"),
						        	 "property":"sampleCode",
						        	 "position":2,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.columns"),
						        	 "property":"sampleOnInputContainer.properties.tag.value",
						        	 "position":3,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.properties.tag"),
						        	 "property":"sampleOnInputContainer.properties.tag.value",
						        	 "position":4,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.mesuredVolume"),
						        	 "property":"sampleOnInputContainer.mesuredVolume.value",
						        	 "position":5,
						        	 "type":"text"
						         },  
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.unit.volume"),
						        	 "property":"sampleOnInputContainer.mesuredVolume.unit",
						        	 "position":6,
						        	 "type":"text"
						         },	
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.mesuredConcentration"),
						        	 "property":"sampleOnInputContainer.mesuredConcentration.value",
						        	 "position":7,
						        	 "type":"text"
						         },	 	
						         {
						        	 "header":Messages("processes.table.sampleOnInputContainer.unit.concentration"),
						        	 "property":"sampleOnInputContainer.mesuredConcentration.unit",
						        	 "position":8,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.typeCode"),
						        	 "property":"typeCode",
						        	 "position":9,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.stateCode"),
						        	 "property":"state.code",
						        	 "position":30,
						        	 "type":"text",
						        	 "filter": "codes:'state'",
						        	 "edit":true,
						        	 "choiceInList": true,
						 			 "possibleValues":"searchService.lists.getStates()", 
						         },
						         {
						        	 "header":Messages("processes.table.resolutionCode"),
						        	 "property":"state.resolutionCodes",
						        	 "position":31,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.currentExperimentTypeCode"),
						        	 "property":"currentExperimentTypeCode",
						        	 "position":32,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.code"),
						        	 "property":"code",
						        	 "position":33,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.creationDate"),
						        	 "property":"traceInformation.creationDate",
						        	 "position":34,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.newContainerSupportCodes"),
						        	 "property":"newContainerSupportCodes",
						        	 "position":35,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.experimentCodes"),
						        	 "property":"experimentCodes",
						        	 "position":36,
						        	 "type":"text"
						         },
						         {
						        	 "header":Messages("processes.table.projectCode"),
						        	 "property":"projectCode",
						        	 "position":37,
						        	 "type":"text"
						         }
						 ],
				datatable:undefined,
				isRouteParam:false,
				lists : lists,
				setRouteParams:function($routeParams){
					var count = 0;
					for(var p in $routeParams){
						count++;
						break;
					}
					if(count > 0){
						this.isRouteParam = true;
						this.form = $routeParams;
					}
				},
				getPropertyColumnType : function(type){
					if(type === "java.lang.String"){
						return "text";
					}else if(type === "java.lang.Double"){
						return "number";
					}else if(type === "java.util.Date"){
						return "date";
					}
					
					return type;
				},
				getColumns : function(){		
					var typeCode = "";		
					var columns = [];
					if(this.form.typeCode){
						typeCode = this.form.typeCode;
					}
					var getPropertyColumnType = this.getPropertyColumnType;
					var datatable = this.datatable;
					var columnsDefault = this.columnsDefault;
					var columnsDefaultState = this.columnsDefaultState;
					return $http.get(jsRoutes.controllers.processes.tpl.Processes.getPropertiesDefinitions(typeCode).url)
					.success(function(data, status, headers, config) {
						if(data!=null){
							console.log(data);
							angular.forEach(data, function(property){					
								var column = {};
								var unit = "";
								if(angular.isDefined(property.displayMeasureValue)){
									unit = "("+property.displayMeasureValue+")";
								}				
									
								column = datatable.newColumn(property.name, "properties."+property.code+".value",property.editable,false,true, getPropertyColumnType(property.valueType),property.choiceInList, property.possibleValues,{});
								
								column.listStyle = "bt-select";
								column.defaultValues = property.defaultValue;
								if(property.displayMeasureValue != undefined && property.displayMeasureValue != null){
									column.convertValue = {"active":true, "displayMeasureValue":property.displayMeasureValue.value, "saveMeasureValue":property.saveMeasureValue.value};
								}
								column.position = (7+property.displayOrder);
								 if(mainService.getHomePage() === 'state'){
									 column.edit = false;
								 }
								columns.push(column);
							});	
							 if(mainService.getHomePage() === 'state'){
								 columns = columnsDefaultState.concat(columns);
							 }else{
								 columns = columnsDefault.concat(columns);
							 }
							datatable.setColumnsConfig(columns);
						}

					})
					.error(function(data, status, headers, config) {
						console.log(data);
					});		

				},	
				updateForm : function(){
					
				},
				convertForm : function(){
					var _form = angular.copy(this.form);	
					if(_form.fromDate)_form.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
					if(_form.toDate)_form.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();
					return _form;
				},
				
				resetForm : function(){
					this.form = {};									
				},
				
				initAdditionalFilters:function(){
					this.additionalFilters=[];
					if(this.form.typeCode !== undefined && lists.get("process-"+this.form.typeCode) && lists.get("process-"+this.form.typeCode).length === 1){
						var formFilters = [];
						var allFilters = angular.copy(lists.get("process-"+this.form.typeCode)[0].filters);
						var nbElementByColumn = Math.ceil(allFilters.length / 5); //5 columns
						for(var i = 0; i  < 5 && allFilters.length > 0 ; i++){
							formFilters.push(allFilters.splice(0, nbElementByColumn));	    								
						}
						//complete to 5 five element to have a great design 
						while(formFilters.length < 5){
							formFilters.push([]);
						}
							
						this.additionalFilters = formFilters;
					}
				},
				
				getAddFiltersToForm : function(){
					if(this.additionalFilters !== undefined && this.additionalFilters.length === 0){
						this.initAdditionalFilters();
					}
					return this.additionalFilters;									
				},
				
				search : function(){
					this.updateForm();
					mainService.setForm(this.form);
					var jsonSearch = this.convertForm();
					if(jsonSearch != undefined){
						searchService.getColumns();
						this.datatable.search(jsonSearch);
					}
				},
				
				refreshSamples : function(){
					if(this.form.projectCodes && this.form.projectCodes.length > 0){
						this.lists.refresh.samples({projectCodes:this.form.projectCodes});
					}
				},
				
				changeProcessCategory : function(){
					this.additionalFilters=[];
					this.form.typeCode = undefined;
					this.lists.clear("processTypes");
					
					if(this.form.categoryCode){
						this.lists.refresh.processTypes({processCategoryCode:this.form.categoryCode});
					}
				},
				
				changeProcessTypeCode : function(){
					if(this.form.categoryCode){
						//searchService.search();
						lists.refresh.filterConfigs({pageCodes:["process-"+this.form.typeCode]}, "process-"+this.form.typeCode);
						this.initAdditionalFilters();
					}else{
						this.form.typeCode = undefined;	
					}
				},
				/**
				 * initialise the service
				 */
				init : function($routeParams, datatableConfig){
					initListService();
					
					//to avoid to lost the previous search
					if(datatableConfig && angular.isUndefined(mainService.getDatatable())){
						searchService.datatable = datatable(datatableConfig);
						 if(mainService.getHomePage() === 'state'){
							 searchService.datatable.setColumnsConfig(this.columnsDefaultState);
						 }else{
							 searchService.datatable.setColumnsConfig(this.columnsDefault);
						 }
						mainService.setDatatable(searchService.datatable);
						//searchService.datatable.setColumnsConfig(getColumns());		
					}else if(angular.isDefined(mainService.getDatatable())){
						searchService.datatable = mainService.getDatatable();			
					}	
					
					if(angular.isDefined(mainService.getForm())){
						searchService.form = mainService.getForm();
					}else{
						searchService.resetForm();						
					}
					
					if(angular.isDefined($routeParams)){
						this.setRouteParams($routeParams);
					}
				}
		};
		
		return searchService;				
	}
]);
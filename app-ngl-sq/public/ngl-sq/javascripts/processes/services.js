"use strict";

angular.module('ngl-sq.processesServices', []).factory('processesSearchService', [ '$http', '$parse', '$filter', 'mainService', 'lists', 'datatable', 
                                                                                   function($http, $parse, $filter, mainService, lists, datatable) {
	var isInit = false;

	var initListService = function() {
		if (!isInit) {
			lists.refresh.processCategories();
			lists.refresh.projects();
			lists.refresh.users();
			lists.refresh.states({
				objectTypeCode : "Process"
			});
			lists.refresh.reportConfigs({
				pageCodes : [ "processes-addcolumns" ]
			}, "processes-addcolumns");
			lists.refresh.filterConfigs({
				pageCodes : [ "processes-search-addfilters" ]
			}, "processes-search-addfilters");
			
			lists.refresh.resolutions({"objectTypeCode":"Process"}, "processResolutions");
			
			isInit = true;
		}
	};

	var getDefaultColumns = function() {
		var columns = [];
		columns.push({
			"header" : Messages("processes.table.inputContainerSupportCode"),
			"property" : "inputContainerSupportCode",
			"order" : true,
			"hide" : false,
			"group" : true,
			"position" : 0.5,
			"type" : "text"
		});
		columns.push({
			"header" : Messages("processes.table.inputContainerCode"),
			"property" : "inputContainerCode",
			"order" : true,
			"hide" : false,
			"group" : true,
			"position" : 1,
			"type" : "text"
		});
		columns.push({
			"header" : Messages("processes.table.projectCode"),
			"property" : "projectCodes",
			"order" : true,
			"hide" : true,
			"group" : true,
			"groupMethod" : "collect",
			"filter":"orderBy",
        	"render" : "<div list-resize='cellValue | unique' list-resize-min-size='3'>",
			"position" : 2,
			"type" : "text"
		});
		columns.push({
			"header" : Messages("processes.table.sampleCode"),
			"property" : "sampleCodes",
			"order" : true,
			"hide" : false,
			"group" : true,
			"groupMethod" : "collect",
			"filter":"orderBy",
        	"render" : "<div list-resize='cellValue | unique' list-resize-min-size='3'>",
			"position" : 3,
			"type" : "text"
		});
		
		columns.push({
			"header" : Messages("containers.table.tags"),
			"property" : "sampleOnInputContainer.properties.tag.value",
			"type" : "string",
			"order" : true,
			"hide" : true,
			"groupMethod" : "collect",
			"render" : "<div list-resize='cellValue | unique' list-resize-min-size='3'>",
			"position" : 4
		});
		columns.push({
			"header" : Messages("processes.table.typeCode"),
			"property" : "typeCode",
			"filter" : "codes:'type'",
			"order" : true,
			"hide" : false,
			"groupMethod" : "unique",
			"position" : 5,
			"type" : "text"
		});
		if (mainService.getHomePage() == 'state') {
			columns.push({
				"header" : Messages("processes.table.stateCode"),
				"property" : "state.code",
				"order" : true,
				"hide" : false,
				"position" : 6,
				"type" : "text",
				"filter" : "codes:'state'",
				"edit" : true,
				"choiceInList" : true,
				"listStyle":"bt-select",
				"possibleValues" : "searchService.lists.getStates()",
			});
		} else {

			columns.push({
				"header" : Messages("processes.table.stateCode"),
				"property" : "state.code",
				"position" : 6,
				"order" : true,
				"hide" : false,
				"edit" : false,
				"type" : "text",
				"filter" : "codes:'state'",
				"groupMethod" : "collect",
				"render" : "<div list-resize='cellValue | unique' list-resize-min-size='3'>",
			});
		}
		columns.push({
			"header" : Messages("processes.table.resolutionCodes"),
			"property" : "state.resolutionCodes",
			"filter":"codes:'resolution'",
			"position" : 7,
			"order" : true,
			"edit" : true,
			"hide" : true,
			"choiceInList":true,
		    "listStyle":"bt-select-multiple",
		    "possibleValues":"searchService.lists.get('processResolutions')",
			"type" : "text"
		});
		columns.push({
			"header" : Messages("processes.table.currentExperimentTypeCode"),
			"property" : "currentExperimentTypeCode",
			"filter" : "codes:'type'",
			"order" : true,
			"hide" : false,
			"groupMethod" : "unique",
			"position" : 8,
			"type" : "text"
		});
		
		columns.push({
			"header" : Messages("processes.table.outputContainerSupportCodes"),
			"property" : "outputContainerSupportCodes",
			"order" : false,
			"hide" : false,
			"position" : 8.5,
			"filter":"unique",
			"groupMethod" : "unique",
			"filter":"orderBy",
			"render" : "<div list-resize='cellValue' list-resize-min-size='2' vertical>",
			"type" : "text"
		});
		
		columns.push({
			"header" : Messages("processes.table.outputContainerCodes"),
			"property" : "outputContainerCodes",
			"order" : false,
			"hide" : false,
			"position" : 9,
			"filter":"unique",
			"groupMethod" : "unique",
			"filter":"orderBy",
			"render" : "<div list-resize='cellValue' list-resize-min-size='2' vertical>",
			"type" : "text"
		});
		
		columns.push({
			"header" : Messages("processes.table.experimentCodes"),
			"property" : "experimentCodes",
			"order" : false,
			"hide" : false,
			"position" : 10,
			"filter" : "unique",
			"groupMethod" : "unique",
			"filter":"orderBy:searchService.extractDate",
			"render" : "<div list-resize='cellValue' list-resize-min-size='2' vertical>",
			"type" : "text"
		});
		columns.push({
			"header" : Messages("processes.table.code"),
			"property" : "code",
			"order" : true,
			"hide" : true,
			"groupMethod" : "collect",
			"render" : "<div list-resize='cellValue | unique' list-resize-min-size='3'>",
			"position" : 11,
			"type" : "text"
		});
		columns.push({
			"header" : Messages("processes.table.creationDate"),
			"property" : "traceInformation.creationDate",
			"position" : 12,
			"order" : true,
			"hide" : true,
			"format" : Messages("datetime.format"),
			"type" : "date"
		});
		columns.push({
			"header" : Messages("processes.table.endDate"),
			"property" : "state.historical",
			"position" : 12,
			"order" : true,
			"hide" : true,
			"filter" : "filter:'F'|get:'date'",
			"format" : Messages("datetime.format"),
			"type" : "date"
		});
		columns.push({
			"header" : Messages("processes.table.createUser"),
			"property" : "traceInformation.createUser",
			"position" : 13,
			"order" : true,
			"hide" : true,
			"type" : "text"
		});
		columns.push({
			"header" : Messages("processes.table.comments"),
			"property" : "comments[0].comment",
			"position" : 500,
			"order" : false,
			"edit" : true,
			"hide" : true,
			"type" : "text"
		});
		return columns;
	};

	
	
	var searchService = {

		datatable : undefined,
		isRouteParam : false,
		lists : lists,
		getDefaultColumns : getDefaultColumns,
		additionalFilters : [],
		additionalProcessFilters : [],
		isProcessFiltered : false,
		additionalColumns : [],
		selectedAddColumns : [],
		processTypesByCategory : {},
		processTypesForCategories : [],
		setRouteParams : function($routeParams) {
			var count = 0;
			for ( var p in $routeParams) {
				count++;
				break;
			}
			if (count > 0) {
				this.isRouteParam = true;
				this.form = $routeParams;
			}
		},
		extractDate: function(value){
			return value.split(/(\d+_\d+)/)[1];
		},
		getPropertyColumnType : function(type) {
			if (type === "java.lang.String") {
				return "text";
			} else if (type === "java.lang.Double") {
				return "number";
			} else if (type === "java.util.Date") {
				return "date";
			}

			return type;
		},
		setColumns : function() {
			var columns = [];
			
			var getPropertyColumnType = this.getPropertyColumnType;
			var datatable = this.datatable;
			var columnsDefault = this.columnsDefault;

			if (this.selectedAddColumns != undefined && this.selectedAddColumns != null) {
				columnsDefault = this.getDefaultColumns().concat(this.selectedAddColumns);

			}
			if (angular.isArray(this.form.typeCodes) && this.form.typeCodes.length === 1) {
				var typeCode = this.form.typeCodes[0];
			
				$http.get(jsRoutes.controllers.processes.api.ProcessTypes.get(typeCode).url).success(function(data, status, headers, config) {
					if (data != null) {
						angular.forEach(data.propertiesDefinitions, function(propertyDefinition) {
							
							var getDisplayUnitFromProperty = function(propertyDefinition){
								var unit = $parse("displayMeasureValue.value")(propertyDefinition);
								if(undefined !== unit && null !== unit) return " ("+unit+")";
								else return "";
							};
							var getPropertyColumnType = function(type){
								if(type === "java.lang.String"){
									return "text";
								}else if(type === "java.lang.Double" || type === "java.lang.Integer" || type === "java.lang.Long"){
									return "number";
								}else if(type === "java.util.Date"){
									return "date";
								}else if(type ==="java.io.File"){
									return "file";
								}else if(type ==="java.awt.Image"){
									return "img";
								}else if(type ==="java.lang.Boolean"){
									return "boolean";	
								}else{
									throw 'not manage : '+type;
								}

								return type;
							};
							
							var column = {};
							column.watch=true;
							column.header = propertyDefinition.name + getDisplayUnitFromProperty(propertyDefinition);
							column.required=propertyDefinition.required;
							    				
							column.property = "properties."+propertyDefinition.code+".value";
							column.edit = (mainService.getHomePage() === 'state')?false:propertyDefinition.editable;
							column.type = getPropertyColumnType(propertyDefinition.valueType);
							column.choiceInList = propertyDefinition.choiceInList;
							column.position = (5+(propertyDefinition.displayOrder/1000));
							column.defaultValues = propertyDefinition.defaultValue;
							column.format = propertyDefinition.displayFormat;
							
							if(column.choiceInList){
								if(propertyDefinition.possibleValues.length > 100){
									column.editTemplate='<input class="form-control" type="text" #ng-model typeahead="v.code as v.name for v in col.possibleValues | filter:$viewValue | limitTo:20" typeahead-min-length="1" udt-change="updatePropertyFromUDT(value,col)"/>';        					
								}else{
									column.listStyle = "bt-select";
								}
								column.possibleValues = propertyDefinition.possibleValues; 
								column.filter = "codes:'value."+propertyDefinition.code+"'";    					
							}
							
							if(propertyDefinition.displayMeasureValue != undefined && propertyDefinition.displayMeasureValue != null){
								column.convertValue = {"active":true, "displayMeasureValue":propertyDefinition.displayMeasureValue.value, 
										"saveMeasureValue":propertyDefinition.saveMeasureValue.value};
							}
							
							columns.push(column);
						});
						columns = columnsDefault.concat(columns);
						datatable.setColumnsConfig(columns);
					}
	
				}).error(function(data, status, headers, config) {
					//console.log(data);
					datatable.setColumnsConfig(columnsDefault);
	
				});
			}else{
				datatable.setColumnsConfig(columnsDefault);
			}
		},

		updateForm : function() {
			this.form.includes = [];
			if (this.reportingConfiguration) {
				for (var i = 0; i < this.reportingConfiguration.columns.length; i++) {
					if (this.reportingConfiguration.columns[i].queryIncludeKeys && this.reportingConfiguration.columns[i].queryIncludeKeys.length > 0) {
						this.form.includes = this.form.includes.concat(this.reportingConfiguration.columns[i].queryIncludeKeys);
					} else {
						this.form.includes.push(this.reportingConfiguration.columns[i].property.replace('.value', '').replace(".unit", ''));
					}
				}
			} else {
				this.form.includes = [ "default" ];
			}

			//this.form.includes = ["default"];
			for (var i = 0; i < this.selectedAddColumns.length; i++) {
				//remove .value if present to manage correctly properties (single, list, etc.)
				if (this.selectedAddColumns[i].queryIncludeKeys && this.selectedAddColumns[i].queryIncludeKeys.length > 0) {
					this.form.includes = this.form.includes.concat(this.selectedAddColumns[i].queryIncludeKeys);
				} else {
					this.form.includes.push(this.selectedAddColumns[i].property.replace('.value', '').replace(".unit", ''));
				}

			}
		},
		convertForm : function() {
			var _form = angular.copy(this.form);
			if (_form.fromDate)
				_form.fromDate = moment(_form.fromDate, Messages("date.format").toUpperCase()).valueOf();
			if (_form.toDate)
				_form.toDate = moment(_form.toDate, Messages("date.format").toUpperCase()).valueOf();
			return _form;
		},

		resetForm : function() {
			this.form = {};
		},

		resetSampleCodes : function() {
			this.form.sampleCodes = [];
		},

		initAdditionalFilters : function() {
			this.additionalFilters = [];
			var formFilters = [];
			var allFilters = undefined;
			var nbElementByColumn = undefined;

			if (lists.get("processes-search-addfilters") && lists.get("processes-search-addfilters").length === 1) {
				allFilters = angular.copy(lists.get("processes-search-addfilters")[0].filters);
			}
			if (angular.isDefined(allFilters)) {
				nbElementByColumn = Math.ceil(allFilters.length / 5); //5 columns
				for (var i = 0; i < 5 && allFilters.length > 0; i++) {
					formFilters.push(allFilters.splice(0, nbElementByColumn));
				}
				//complete to 5 five element to have a great design 
				while (formFilters.length < 5) {
					formFilters.push([]);
				}
			}

			this.additionalFilters = formFilters;
		},

		getAddFiltersToForm : function() {
			if (this.additionalFilters !== undefined && this.additionalFilters.length === 0) {
				this.initAdditionalFilters();
			}
			return this.additionalFilters;

		},

		

		search : function() {
			this.updateForm();
			mainService.setForm(this.form);
			searchService.datatable.setColumnsConfig(this.columnsDefault);
			searchService.setColumns();
			this.datatable.search(this.convertForm());

		},

		refreshSamples : function() {
			if (this.form.projectCodes && this.form.projectCodes.length > 0) {
				this.lists.refresh.samples({
					projectCodes : this.form.projectCodes
				});
			}
		},

		initProcessTypes:function(){
			//load all process cat
			$http.get(jsRoutes.controllers.processes.api.ProcessTypes.list().url, {processTypesByCategory:this.processTypesByCategory})
				.success(function(data, status, headers, config) {
				data.forEach(function(processType){
					if(!config.processTypesByCategory[processType.category.code]){
						config.processTypesByCategory[processType.category.code] = [];
					}
					config.processTypesByCategory[processType.category.code].push(processType);
				})
				
				
			});
		},		
		changeProcessCategories : function() {
			this.additionalProcessFilters = [];
			this.form.typeCodes = undefined;
			this.processTypesForCategories=[];

			if (this.form.categoryCodes && this.form.categoryCodes.length > 0) {
				this.form.categoryCodes.forEach(function(code){
					this.processTypesForCategories = this.processTypesForCategories.concat(this.processTypesByCategory[code])
				},this)
				this.processTypesForCategories = $filter('orderBy')(this.processTypesForCategories, 'displayOrder');
			}else {
				this.form.categoryCodes = undefined;
			}
		},

		changeProcessTypeCode : function() {
			if (angular.isDefined(this.form.categoryCodes)) {
				if(angular.isArray(this.form.typeCodes) && this.form.typeCodes.length === 1){
					lists.refresh.filterConfigs({
						pageCodes : [ "process-" + this.form.typeCodes[0] ]
					}, "process-" + this.form.typeCodes[0]);
				}				
			} else {
				this.form.typeCodes = undefined;
			}
			this.initAdditionalProcessFilters();
		},
		
		initAdditionalProcessFilters : function() {
			this.additionalProcessFilters = [];
			var formFilters = [];
			var allFilters = undefined;
			var nbElementByColumn = undefined;

			if (angular.isArray(this.form.typeCodes) && this.form.typeCodes.length === 1 
					&& lists.get("process-" + this.form.typeCodes[0]) && lists.get("process-" + this.form.typeCodes[0]).length === 1) {
				allFilters = angular.copy(lists.get("process-" + this.form.typeCodes[0])[0].filters);
				this.isProcessFiltered = true;
			} else {
				this.isProcessFiltered = false;
			}
			if (angular.isDefined(allFilters)) {
				nbElementByColumn = Math.ceil(allFilters.length / 5); //5 columns
				for (var i = 0; i < 5 && allFilters.length > 0; i++) {
					formFilters.push(allFilters.splice(0, nbElementByColumn));
				}
				//complete to 5 five element to have a great design 
				while (formFilters.length < 5) {
					formFilters.push([]);
				}
			}

			this.additionalProcessFilters = formFilters;
		},

		getAddProcessFiltersToForm : function() {
			if (this.additionalProcessFilters !== undefined && this.additionalProcessFilters.length === 0) {
				this.initAdditionalProcessFilters();
			}
			return this.additionalProcessFilters;
		},
		
		initAdditionalColumns : function() {
			this.additionalColumns = [];
			this.selectedAddColumns = [];

			if (lists.get("processes-addcolumns") && lists.get("processes-addcolumns").length === 1) {
				var formColumns = [];
				var allColumns = angular.copy(lists.get("processes-addcolumns")[0].columns);
				var nbElementByColumn = Math.ceil(allColumns.length / 5); //5 columns
				for (var i = 0; i < 5 && allColumns.length > 0; i++) {
					formColumns.push(allColumns.splice(0, nbElementByColumn));
				}
				//complete to 5 five element to have a great design 
				while (formColumns.length < 5) {
					formColumns.push([]);
				}
				this.additionalColumns = formColumns;
			}
		},
		getAddColumnsToForm : function() {
			if (this.additionalColumns.length === 0) {
				this.initAdditionalColumns();
			}
			return this.additionalColumns;
		},
		addColumnsToDatatable : function() {

			this.selectedAddColumns = [];
			for (var i = 0; i < this.additionalColumns.length; i++) {
				for (var j = 0; j < this.additionalColumns[i].length; j++) {
					if (this.additionalColumns[i][j].select) {
						this.selectedAddColumns.push(this.additionalColumns[i][j]);
					}
				}
			}
			this.search();
		},
		resetDatatableColumns : function() {
			this.initAdditionalColumns();
			this.datatable.setColumnsConfig(this.getDefaultColumns);
			this.search();
		},
		/**
		 * Update column when change reportingConfiguration
		 */
		updateColumn : function() {
			this.initAdditionalColumns();
			if (this.reportingConfigurationCode) {
				$http.get(jsRoutes.controllers.reporting.api.ReportingConfigurations.get(this.reportingConfigurationCode).url, {
					searchService : this,
					datatable : this.datatable
				}).success(function(data, status, headers, config) {
					config.searchService.reportingConfiguration = data;
					config.searchService.search();
					config.datatable.setColumnsConfig(data.columns);
				});
			} else {
				this.reportingConfiguration = undefined;
				this.datatable.setColumnsConfig(this.getDefaultColumns());
				this.search();
			}

		},
		/**
		 * initialise the service
		 */
		init : function($routeParams, datatableConfig) {
			initListService();
			
			datatableConfig.messages = {
					transformKey: function(key, args) {
                        return Messages(key, args);
                    }
			};
			
			//to avoid to lost the previous search
			if (datatableConfig && angular.isUndefined(mainService.getDatatable())) {
				searchService.datatable = datatable(datatableConfig);
				searchService.datatable.setColumnsConfig(getDefaultColumns());
				mainService.setDatatable(searchService.datatable);
			} else if (angular.isDefined(mainService.getDatatable())) {
				searchService.datatable = mainService.getDatatable();
			}

			if (angular.isDefined(mainService.getForm())) {
				searchService.form = mainService.getForm();
			} else {
				searchService.resetForm();
			}

			if (angular.isDefined($routeParams)) {
				this.setRouteParams($routeParams);
			}
			
			searchService.initProcessTypes();
		}
	};

	return searchService;
} ]);
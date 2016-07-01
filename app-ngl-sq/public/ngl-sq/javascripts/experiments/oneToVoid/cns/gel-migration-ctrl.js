angular.module('home').controller('OneToVoidGelMigrationCNSCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		//nothing
	
	};
	
	$scope.$watch("gel",function(imgNew, imgOld){
		if(imgNew){			
			
			angular.forEach($scope.atmService.data.displayResult, function(dr){
				$parse('inputContainerUsed.experimentProperties.electrophoresisGelPhoto').assign(dr.data, this);
			}, imgNew);
			
		}
		angular.element('#importFile')[0].value = null;
		
	});
	
	$scope.button = {
			isShow:function(){
				return ($scope.isInProgressState() && !$scope.mainService.isEditMode())
				}	
		};
	
	
	var getDefaultDatatableColumn = function() {
		var columns = [];

		columns.push({
			"header" : Messages("containers.table.fromTransformationTypeCodes"),
			"property" : "inputContainer.fromTransformationTypeCodes",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"render" : "<div list-resize='cellValue | unique | codes:\"type\"' list-resize-min-size='3'>",
			"position" : 4,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		columns.push({
			"header" : Messages("containers.table.valuation.valid"),
			"property" : "inputContainer.valuation.valid",
			"filter" : "codes:'valuation'",
			"order" : true,
			"edit" : false,
			"hide" : false,
			"type" : "text",
			"position" : 5,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		columns.push({
			"header" : Messages("containers.table.projectCodes"),
			"property" : "inputContainer.projectCodes",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 6,
			"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		columns.push({
			"header" : Messages("containers.table.sampleCodes"),
			"property" : "inputContainer.sampleCodes",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"position" : 7,
			"render" : "<div list-resize='cellValue' list-resize-min-size='3'>",
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		
		columns.push({
			"header":Messages("containers.table.sampleTypes"),
			"property":"inputContainer.contents",
			"order":false,
			"hide":false,
			"position":7.5,
			"type":"text",
			"filter":"getArray:'sampleTypeCode' | unique | codes:\"type\"",
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		
		columns.push({
        	 "header":Messages("containers.table.concentration"),
        	 "property":"inputContainer.concentration.value (inputContainer.concentration.unit)",
        	 "order":true,
			 "edit":false,
			 "hide":true,
        	 "type":"number",
        	 "position":8,
        	 "extraHeaders":{0:Messages("experiments.inputs")}
         });
        
		columns.push( {
        	 "header":Messages("containers.table.volume") + " (µL)",
        	 "property":"inputContainer.volume.value",
        	 "order":true,
			 "edit":false,
			 "hide":true,
        	 "type":"number",
        	 "position":9,
        	 "extraHeaders":{0:Messages("experiments.inputs")}
         });
		
		
		
		/*
		columns.push({
			"header" : Messages("containers.table.tags"),
			"property" : "inputContainer.contents",
			"order" : false,
			"hide" : true,
			"type" : "text",
			"position" : 9,
			"render" : "<div list-resize='cellValue | getArray:\"properties.tag.value\" | unique' list-resize-min-size='3'>",
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}

		});
		*/
		
		columns.push({
			"header" : Messages("containers.table.stateCode"),
			"property" : "inputContainer.state.code",
			"order" : true,
			"edit" : false,
			"hide" : true,
			"type" : "text",
			"filter" : "codes:'state'",
			"position" : 11,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		columns.push({
			"header" : Messages("containers.table.valuationqc.valid"),
			"property" : "inputContainerUsed.valuation.valid",
			"filter" : "codes:'valuation'",
			"order" : true,
			"edit" : true,
			"hide" : false,
			"type" : "text",
			"choiceInList" : true,
			"listStyle" : 'bt-select',
			"possibleValues" : 'lists.getValuations()',
			"position" : 20,
			"extraHeaders" : {
				0 : Messages("experiments.inputs")
			}
		});
		if ($scope.experiment.instrument.inContainerSupportCategoryCode.indexOf('well') > -1) {
			columns.push({
				"header" : Messages("containers.table.supportCode"),
				"property" : "inputContainer.support.code",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "text",
				"position" : 1,
				"extraHeaders" : {
					0 : Messages("experiments.inputs")
				}
			});
			columns.push({
				"header" : Messages("containers.table.support.line"),
				"property" : "inputContainer.support.line",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "text",
				"position" : 1.1,
				"extraHeaders" : {
					0 : Messages("experiments.inputs")
				}
			});
			columns.push({
				"header" : Messages("containers.table.support.column"),
				"property" : "inputContainer.support.column*1",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "text",
				"position" : 1.2,
				"extraHeaders" : {
					0 : Messages("experiments.inputs")
				}
			});
		} else {
			columns.push({
				"header" : Messages("containers.table.supportCode"),
				"property" : "inputContainer.support.code",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "text",
				"position" : 1,
				"extraHeaders" : {
					0 : Messages("experiments.inputs")
				}
			});
			columns.push({
				"header" : Messages("containers.table.categoryCode"),
				"property" : "inputContainer.support.categoryCode",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "text",
				"position" : 2,
				"filter" : "codes:'container_support_cat'",
				"extraHeaders" : {
					0 : Messages("experiments.inputs")
				}
			});
			columns.push({
				"header" : Messages("containers.table.code"),
				"property" : "inputContainer.code",
				"order" : true,
				"edit" : false,
				"hide" : true,
				"type" : "text",
				"position" : 3,
				"extraHeaders" : {
					0 : Messages("experiments.inputs")
				}
			});

		}
		return columns;
	};
	
	$scope.$parent.atmService.data.setColumnsConfig(getDefaultDatatableColumn());
	
}]);
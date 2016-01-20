angular.module('commonsServices').directive('btInput', [ '$parse', '$filter', function($parse, $filter) {
	return {
		restrict : 'A',
		replace : false,
		scope : true,
		template : ''
			+ '<div class="dropdown" ng-if="isTextarea()">'
			+ '<div class="input-group">'
			+ '<div class="input-group-btn">'
			//textarea mode
			+ '<button tabindex="-1" data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle" type="button" ng-disabled="isDisabled()" ng-click="open()">' 
			+ '<i class="fa fa-list-ul"></i>' + '</button>' + '<ul class="dropdown-menu dropdown-menu-left"  role="menu">' 
			+ '<li>' 
			+ '<textarea ng-class="inputClass" ng-model="textareaValue" ng-change="setTextareaValue(textareaValue)" rows="5"></textarea>' 
			+ '</li>' 
			+ '</ul>' 
			+ '</div>'
			//select mode
			+ '<input type="text" style="background:white" ng-class="inputClass" ng-model="inputValue" ng-change="setInputValue(inputValue)" placeholder="{{placeholder}}" title="{{placeholder}}"/>' 
			+ '</div>' 
			+ '</div>'
			+ '<input type="text" style="background:white" ng-class="inputClass" ng-model="inputValue" ng-change="setInputValue(inputValue)" placeholder="{{placeholder}}" title="{{placeholder}}" ng-if="!isTextarea()"/>' 
			 ,
		require : [ '?ngModelInput' ],
		link : function(scope, element, attr) {
			scope.inputClass = element.attr("class");
			scope.placeholder = attr.placeholder;

			element.attr("class", ''); //remove custom class

			var textarea = false;
			var ngModelTextareaValue = attr.ngModelTextarea;
			if(ngModelTextareaValue){
				textarea = true;
			}
			
			var ngModelValue = attr.ngModelInput;
			if(ngModelValue===undefined || ngModelValue===null){
				return;
			}
			var ngFocus = attr.ngFocus;
			
			
			var inputValue;
			var textareaValue;
			
			scope.isTextarea = function() {
				return (textarea);
			};
			scope.textareaValue = textareaValue;
			scope.setTextareaValue = function(values, $event) {
				var selectedValues = values.split(/\s*[,;\n]\s*/);
				$parse(ngModelTextareaValue).assign(scope, selectedValues);				
			};

			scope.inputValue = inputValue;
			scope.setInputValue = function(value, $event) {
				$parse(ngModelValue).assign(scope, value);	
			};

			scope.open = function() {
				if (ngFocus) {
					$parse(ngFocus)(scope);
				}
			};

			scope.isDisabled = function() {
				return (attr.ngDisabled) ? scope.$parent.$eval(attr.ngDisabled) : false;
			};

			scope.getMessage = function(value) {
				return Messages(value);
			};

		}
	};
} ]);
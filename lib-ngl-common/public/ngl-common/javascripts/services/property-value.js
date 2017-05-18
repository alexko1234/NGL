angular.module('commonsServices').factory('propertyDefinitions', ['$http', function($http){
	
	var datas = new Map();
	
	var promise = $http.get(jsRoutes.controllers.commons.api.PropertyDefinitions.list().url).success(function(data) {
		for(var i=0; i<data.length; i++){
			datas.set(data[i].code,data[i]);
		}
	});
	
	return {
		datas : datas,
		get : function get(type){
			if(datas !== undefined){
				
				return datas.get(type);
			}else
				return null;
		}
	};
	
}]).directive('propertyValue',[function() {
	return {
		restrict : 'EA',
		scope : {
			valueNgModel :'=',
			keyNgModel : '='
		},
		template : '<div ng-if="valueNgModel._type == \'object_list\'">'
						+'<object-list value-ng-model="valueNgModel" key-ng-model="keyNgModel" format-ng-model="paragraph"/>'
					+'</div>'
					+'<div ng-if="valueNgModel._type == \'single\'">'
						+'<single value-ng-model="valueNgModel" key-ng-model="keyNgModel"/>'
					+'</div>'
		};
}]).directive('objectList',[ function() {
	return {
		restrict : 'EA',
		scope : {
			valueNgModel :'=',
			keyNgModel : '=',
			formatNgModel : '=',
		},
			template :'<div ng-if="format == \'line\'" ng-repeat="property in valueNgModel.value">'
						+'<div class="row">'
							+'<label class="col-md-6 col-lg-6 control-label">{{keyNgModel|codes:\'property_definition\'}}</label>'
							+'<div class="col-md-6 col-lg-6">'
							+'<span ng-repeat="(keyProp,valueProp) in property"> <label class="control-label">{{keyProp|codes:\'property_definition.\'+keyNgModel}}</label> : <value value-ng-model=valueProp key-ng-model=keyProp key-prop-def-ng-model=keyNgModel+\'.\'+keyProp/></span>'
							+'</div>'
						+'</div>'
					  +'</div>'
					  +'<div ng-if="format == \'paragraph\'" ng-repeat="property in valueNgModel.value">'
						+'<div class="row">'
							+'<label class="col-md-6 col-lg-6 control-label">{{keyNgModel|codes:\'property_definition\'}}</label>'
							+'<div class="col-md-6 col-lg-6">'
							+'<p ng-repeat="(keyProp,valueProp) in property"> <label class="control-label">{{keyProp|codes:\'property_definition.\'+keyNgModel}}</label> : <value value-ng-model=valueProp key-ng-model=keyProp key-prop-def-ng-model=keyNgModel+\'.\'+keyProp/></p>'
							+'</div>'
						+'</div>'
					  +'</div>',
			link : function(scope, element, attr){
				scope.format="line";
				if(attr.formatNgModel && attr.formatNgModel !== "line"){
					scope.format = attr.formatNgModel;
				}
			}
		};
}]).directive('single',[function() {
	return {
		restrict : 'EA',
		scope : {
			valueNgModel :'=',
			keyNgModel : '='
		},					
		template : '<label class="col-md-6 col-lg-6 control-label" ng-if="valueNgModel.unit !== null"> {{keyNgModel|codes:\'property_definition\'}} ({{valueNgModel.unit}})</label>'
					+'<label class="col-md-6 col-lg-6 control-label" ng-if="valueNgModel.unit === null" ng-bind="keyNgModel|codes:\'property_definition\'"></label>' 
					+'<p class="col-md-6 col-lg-6 form-control-static"><value value-ng-model=valueNgModel.value key-ng-model=keyNgModel key-prop-def-ng-model=keyNgModel/></p>'			
		};
}]).directive('value',['propertyDefinitions', function(propertyDefinitions) {
	return {
		restrict : 'EA',
		scope : {
			valueNgModel :'=',
			//For property type object_list who property key in description table is different because of concatenation of parent key and property key
			keyPropDefNgModel : '=',
			keyNgModel : '='
		},					
		template :'<span ng-switch on="propertyDefinitions.get(keyPropDefNgModel).valueType">'
						+'<span ng-switch-when="java.util.Date">'
							+'<span ng-bind="valueNgModel|codes:\'value.\'+keyNgModel:false|date:\'dd/MM/yyyy\'" />'
						+'</span>'
						+'<span ng-switch-default>'
				   			+'<span ng-bind="valueNgModel|codes:\'value.\'+keyNgModel:false" />'
				   		+'</span>'
				   +'</span>' ,
		link : function(scope, element, attr){
			scope.propertyDefinitions = propertyDefinitions;
		}
		};
}]);

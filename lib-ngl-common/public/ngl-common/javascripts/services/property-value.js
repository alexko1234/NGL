angular.module('commonsServices').factory('propertyDefinitions', ['$http', function($http){
	
	/*var results = {};
	
	var refresh = {
		propDefs : function(){
			load(jsRoutes.controllers.commons.api.PropertyDefinitions.list().url);
		}
	};
	
	function load(url){
			$http.get(url).success(function(data) {
				for(var i=0; i<data.length; i++){
					results[data[i].code]=data[i];
				}
			});
	};

	return {
		refresh : refresh,
		get : function(key){
			return results[key];
		}
	}*/
	var datas =  undefined;
	
	var promise = $http.get(jsRoutes.controllers.commons.api.PropertyDefinitions.list().url);
	
	return {
		datas : undefined,
		promise : promise,
		get : function get(type){
			if(datas == undefined){
				promise.success(function(result){
					load(result);
				})
			}
			return datas.get(type);
		},
		load : function load(data){
			for(var i=0; i<data.length; i++){
				datas.set(data[i].code,data[i]);
			}
		}
		
	};
	
	/*var datas = undefined;
	
	var promise = $http.get(jsRoutes.controllers.commons.api.PropertyDefinitions.list().url).success(function(data) {
		datas = new Map();
		for(var i=0; i<data.length; i++){
			datas.set(data[i].code,data[i]);
		}
	});
	
	return {
		datas : datas,
		get : function get(type){
			if(datas == undefined){
				promise.then(function(result){
					datas = datas;
				})
			}
			return datas.get(type);
		}
	};*/
	/*var factory = {
			propertyDefinitions : new Map()
		};
		
		$http.get(jsRoutes.controllers.commons.api.PropertyDefinitions.list().url).success(function(data) {
			for(var i=0; i<data.length; i++){
				factory.propertyDefinitions.set(data[i].code,data[i]);
			}
		}).then(function(results){
			return factory;
		});*/

		//return factory;
	
}]).directive('propertyValue',[function() {
	return {
		restrict : 'EA',
		scope : {
			valueNgModel :'=',
			keyNgModel : '='
		},
		template : '<div ng-if="propValue._type == \'object_list\'">'
						+'<object-list value-ng-model="propValue" key-ng-model="keyValue"/>'
					+'</div>'
					+'<div ng-if="propValue._type == \'single\'">'
						+'<single value-ng-model="propValue" key-ng-model="keyValue"/>'
					+'</div>',
			link : function(scope, element, attr){
				if(attr.valueNgModel){
					scope.propValue=scope.valueNgModel;
				}
				if(attr.keyNgModel){
					scope.keyValue=scope.keyNgModel;
				}
			}
		};
}]).directive('objectList',[ function() {
	return {
		restrict : 'EA',
		scope : {
			valueNgModel :'=',
			keyNgModel : '='
		},
			template :'<div ng-if="format == \'line\'" ng-repeat="property in propValue.value">'
							+'<div class="row">'
								+'<label class="col-md-6 col-lg-6 control-label">{{keyValue|codes:\'property_definition\'}}</label>'
								+'<div class="col-md-6 col-lg-6">'
									+'<span ng-repeat="(keyProp,valueProp) in property" ng-if="propValue.unit !== null">{{keyProp|codes:\'property_definition.\'+keyValue}} ({{propValue.unit}}) : {{valueProp|codes:\'value.\'+keyProp:false}} </span>'
									+'<span ng-repeat="(keyProp,valueProp) in property" ng-if="propValue.unit === null"> {{keyProp|codes:\'property_definition.\'+keyValue}} : {{valueProp|codes:\'value.\'+keyProp:false}}</span>'
								+'</div>'
							+'</div>'
						+'</div>',
			link : function(scope, element, attr){
				if(attr.valueNgModel){
					scope.propValue=scope.valueNgModel;
				}
				if(attr.keyNgModel){
					scope.keyValue=scope.keyNgModel;
				}
				scope.format="line";
				if(attr.format && attr.format !== "line"){
					scope.format = attr.format;
				}
			}
		};
}]).directive('single',['propertyDefinitions', function(propertyDefinitions) {
	return {
		restrict : 'EA',
		scope : {
			valueNgModel :'=',
			keyNgModel : '='
		},
			template : '<value value-ng-model="propValue" key-ng-model="keyValue" type-ng-model="typeValue"/>',
			link : function(scope, element, attr){
				console.log("Scope "+scope);
				if(attr.valueNgModel){
					scope.propValue=scope.valueNgModel;
				}
				if(attr.keyNgModel){
					scope.keyValue=scope.keyNgModel;
				}
				scope.typeValue=propertyDefinitions.get(scope.keyValue);
				
			}
		};
}]).directive('value',[ function() {
	return {
		restrict : 'EA',
		scope : {
			valueNgModel :'=',
			keyNgModel : '=',
			typeNgModel : '='
		},
		template : 'test',
			/*template : '<div ng-switch-on="typeValue">'
							+'<div ng-switch-when="java.util.Date" class="col-md-6 col-lg-6">'
								+'<label class="control-label" ng-if="propValue.unit === null" ng-bind="keyValue|codes:\'property_definition\'"></label>'
								+'<label class="control-label" ng-if="propValue.unit === null" ng-bind="keyValue|codes:\'property_definition\'"></label>'
								+'<p class="form-control-static" ng-bind="propValue.value|codes:\'value.\'+keyValue:false | date:\'@Messages("date.format")\'" />'
							+'</div>'
							+'<div ng-switch-default class="col-md-6 col-lg-6">'
								+'<label class="control-label" ng-if="propValue.unit === null" ng-bind="keyValue|codes:\'property_definition\'"></label>'
								+'<label class="control-label" ng-if="propValue.unit === null" ng-bind="keyValue|codes:\'property_definition\'"></label>'
								+'<p class="form-control-static" ng-bind="propValue.value|codes:\'value.\'+keyValue:false" />'
							+'</div>'
						+'</div>',*/
			link : function(scope, element, attr){
				if(attr.valueNgModel){
					scope.propValue=scope.valueNgModel;
				}
				if(attr.keyNgModel){
					scope.keyValue=scope.keyNgModel;
				}
				if(attr.typeNgModel){
					scope.typeValue=scope.typeNgModel;
				}
			}
		};
}]);

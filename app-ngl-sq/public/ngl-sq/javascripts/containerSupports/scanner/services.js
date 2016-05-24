"use strict";

angular.module('ngl-sq.barCodeSearchServices', []).
factory('barCodeSearchService', ['$http','$q', function($http,$q){

	var searchService = {
			response: undefined,
			form: undefined,
			search : function() { 
				this.response = {};
            	var that = this;
            	var promise = [];
            	if(!angular.isUndefined(this.form)){
            		
                	promise.push($http.get(jsRoutes.controllers.containers.api.ContainerSupports.get(this.form.code).url));
                	promise.push($http.get(jsRoutes.controllers.containers.api.Containers.list().url, {params:{supportCodeRegex:this.form.code}}));
	            	
                	$q.all(promise).then(function(results) {
	            		that.response.support = results[0].data;
	            		if(that.response.support.categoryCode.indexOf('tube')>=0 || that.response.support.categoryCode.indexOf('mapcard')>=0){
	            			that.response.containers = results[1].data;
	            		}
	            		//=> TODO
	            		that.form = undefined;
	            		angular.element("#scan").focus();
	            	});
            	}else{
            		this.form = undefined;
            		angular.element("#scan").focus();
            	}
            }
	}
	return searchService;
}]);
"use strict";

angular.module('ngl-sq.barCodeSearchServices', []).
factory('barCodeSearchService', ['$http', function($http){

	var searchService = {
			response: undefined,
			form : undefined,
			
			resetForm : function(){
				this.form = undefined;
				this.response = undefined;
			},
			search : function() { 
				this.response = undefined;
            	var that = this;
            	$http({
            		method: 'GET',
            		url: jsRoutes.controllers.containers.api.ContainerSupports.get(this.form.code).url
            	}).then(function success(response) {
            		that.response = response;
            		that.form = undefined;
            		angular.element("#scan").focus();
            	}, function error(response){
            		that.response = response;
            		that.form = undefined;
            		angular.element("#scan").focus();
            	});
            }
	}
	return searchService;
}]);
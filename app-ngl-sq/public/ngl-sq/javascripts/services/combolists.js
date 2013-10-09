"use strict";
/**
 * List of service for combox list
 */
angular.module('comboListsServices', ['ngResource']).
	factory('comboLists', function($resource){
		var functions = {
				/**
				 * Return the list of projects with name and code properties only
				 */
				getProjects : function(){
					return $resource(jsRoutes.controllers.lists.api.Lists.projects().url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				/**
				 * Return the list of samples with name and code for one project
				 */
				getSamples : function(projectCode){
					return $resource(jsRoutes.controllers.lists.api.Lists.samples(projectCode).url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				/**
				 * Return the list of process types with name and code properties only
				 */
				getProcessTypes :  function(){
					return $resource(jsRoutes.controllers.lists.api.Lists.processTypes().url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				/**
				 * Return the list of category codes with name and code properties only
				 */
				getContainerCategoryCodes :  function(){
					return $resource(jsRoutes.controllers.lists.api.Lists.containerCategoryCodes().url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				/**
				 * Return the list of state codes with name and code properties only
				 */
				getContainerStateCodes : function(){
					return $resource(jsRoutes.controllers.lists.api.Lists.containerStates().url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				/**
				 * Return the list of experiment types with name and code properties only
				 */
				getExperimentTypes :  function(){
					return $resource(jsRoutes.controllers.lists.api.Lists.experimentTypes().url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				/**
				 * Return the list of experiment types with name and code properties only
				 */
				getExperimentTypesByCategory :  function(code){
					return $resource(jsRoutes.controllers.lists.api.Lists.experimentTypesByCategory(code).url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				getExperimentCategories :  function(){
					return $resource(jsRoutes.controllers.lists.api.Lists.experimentCategories().url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				getInstrumentUsedTypes : function(experimentTypeCode){
					return $resource(jsRoutes.controllers.lists.api.Lists.instrumentUsedTypes(experimentTypeCode).url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				getInstruments : function(intrumentUsedTypeCode){
					return $resource(jsRoutes.controllers.lists.api.Lists.instruments(intrumentUsedTypeCode).url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				getProtocols : function(ExperimentTypeCode){
					return $resource(jsRoutes.controllers.lists.api.Lists.protocols(ExperimentTypeCode).url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				getResolution : function(){
					return $resource(jsRoutes.controllers.lists.api.Lists.resolutions().url, {}, {
						query: {method:'GET', isArray:true}
					});
				},
				getCategoryCodes : function(instrumentTypeCode){
					return $resource(jsRoutes.controllers.lists.api.Lists.categoryCodes(instrumentTypeCode).url, {}, {
						query: {method:'GET', isArray:true}
					});
				}
		};
		return functions;
});
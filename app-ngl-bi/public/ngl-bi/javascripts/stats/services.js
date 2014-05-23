 "use strict";
 
 angular.module('ngl-bi.StatsServices', []).
	factory('statsConfigReadSetsService', ['$http', 'lists', 'datatable', function($http, lists, datatable){
		var datatableConfig = {
				search : {
					active:false
				},
				pagination:{
					active:false
				},
				remove:{
					active:true,
					mode:'local'
				},
				columns : [
				           {	
				        	   property:"typeCode",
				        	   header: "stats.typeCode",
				        	   type :"text",		    	  	
				        	   order:true
				           },
				           {
				        	   property:"property",
				        	   header: "stats.property",
				        	   type :"text",
				        	   order:true
				           }
				]
		};
		
		var statsService = {
				datatable:undefined,
				select : {
					typeCode:undefined,
					properties:[]
				},
				getStatsTypes : function(){
					return [{code:'z-score', name:'z-score'},{code:'histogram', name:'histogramme'}];
				},
				
				getProperties : function(){
					return [{code:'treatments.ngsrg.default.Q30.value', name:'% >= Q30'}];
				},
				reset : function(){
					this.select =  {
							typeCode:undefined,
							properties:[]
						};
				},
				add : function(){
					var data = [];
					for(var i = 0; i < this.select.properties.length; i++){
						data.push({typeCode : this.select.typeCode, property: this.select.properties[i]});
					}		
					this.datatable.addData(data);
					this.reset();
				},
				getData : function(){
					return this.datatable.getData();
				}
		};
		
		return function(){
			statsService.datatable = datatable(null, datatableConfig);
			statsService.datatable.setData([], 0);
			return statsService;		
		}
	}
		
	
]).
factory('queriesConfigReadSetsService', ['$http', '$q', 'datatable', function($http, $q, datatable){
	var datatableConfig = {
			search : {
				active:false
			},
			pagination:{
				mode:'local'
			},
			remove:{
				active:true,
				mode:'local',
				callback:function(datatable){
					mainService.getBasket().reset();
					mainService.getBasket().add(datatable.getData());
				}
			},
			columns : [
			           {	
			        	   property:"title",
			        	   header: "query.title",
			        	   type :"text",		    	  	
			        	   order:true
			           },
			           {	
			        	   property:"nbResults",
			        	   header: "query.nbResults",
			        	   type :"number",		    	  	
			        	   order:true
			           },
			           {
			        	   property:"form",
			        	   render:function(v){
			        		 return JSON.stringify(v.form);  
			        	   },
			        	   header: "query.form",
			        	   type :"text"
			           }
			]
	};
	
	var url = jsRoutes.controllers.readsets.api.ReadSets.list().url;
	
	var updateResultQueries = function(queries){
		var promises = [];
		for(var i = 0; i < queries.length ; i++){
			var form = angular.copy(queries[i].form);
			form.count = true;
			promises.push($http.get(url,{params:form, query:queries[i]}));			
		}
		return promises;
	}
	
	
	var queriesService = {
			datatable:undefined,
			getData : function(){
				return this.datatable.getData();
			}
	};
	
	return function(queries){
		queriesService.datatable = datatable(null, datatableConfig);
		if(queries && queries.length > 0){
			$q.all(updateResultQueries(queries)).then(function(results){
				angular.forEach(results, function(value, key){
					value.config.query.nbResults = value.data;																
				});	
				queriesService.datatable.setData(queries, queries.length);
			});	
			
			
		} else {
			queriesService.datatable.setData([], 0);
		}
		return queriesService;		
	}
}
	

]);
 
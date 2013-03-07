"use strict";

angular.module('datatableServices', []).
    	factory('datatable', ['$http', function($http){ //service to manage datatable
    		var constructor = function($scope, iConfig){
				var datatable = {
						configDefault:{
							addshow:undefined,			
							edit: false,
							orderReverse:false,
							orderBy:undefined,
							editColumn: {
								all:false	
							},
							updateColumn:{},
							hideColumn: {},
							orderColumn:{},
							url:{
								save:undefined,
								remove:undefined,
								search:undefined
							}
						},
						config:undefined,
    					configMaster:undefined,
    					searchresult:undefined,
    					searchresultMaster:undefined,
    					/**
    					 * Select all the table line or just one
    					 */
						select : function(line){
		    				if(line){
		    					if(!line.selected){
		    						line.selected=true;
		    						line.trClass="row_selected";
		    					}
								else{
									line.selected=false;
		    						line.trClass="";
								}
		    				}else {
		    					for(var i = 0; i < this.searchresult.length; i++){
		    						this.searchresult[i].selected=true;
		    						this.searchresult[i].trClass="row_selected";
		    					}
		    				}
		    			},
		    			/**
		    			 * cancel all actions (edit, hide, order, etc.)
		    			 */
		    			cancel : function(){
		    				this.searchresult = angular.copy(this.searchresultMaster);
		    				this.config = angular.copy(this.configMaster);
		    			},
		    			/**
		    			 * Search function to populate the datatable
		    			 */
		    			search : function(params){
		    				var url = this.getUrl(this.config.url.search);
		    				if(url){
		    					$http.get(url,{params:params}).success(function(data) {
									$scope.datatable.searchresult = data;
									$scope.datatable.searchresultMaster = angular.copy(data);
		    					});
		    				}else{
		    					console.log('no url define for search ! '+this.config.url.search);
		    				}
		    			},
		    			/**
		    			 * Return an url from play js object or string
		    			 */
		    			getUrl : function(url){
		    				if(angular.isObject(url)){
		    					if(angular.isDefined(url.url)){
		    						return url.url;
		    					}
		    				}else if(angular.isString(url)){
		    					return url;
		    				}
		    				return undefined;
		    			},
		    			/**
		    			 * Save the selected table line
		    			 */
		    			save : function(){
		    				for(var i = 0; i < this.searchresult.length; i++){
		    					if(this.searchresult[i].selected){
		    						if(this.config.url.save){
		    							this.saveObject(this.searchresult[i], i);
		    						}else{		    									    		    				
		    							this.searchresult[i].selected = false;
		    							this.searchresult[i].edit=false;
		    							this.searchresult[i].trClass = undefined;
		    							this.searchresultMaster[i] = angular.copy(this.searchresult[i]);
		    							this.searchresult[i].trClass = "success";
		    						}
		    					}						
		    				}		    				
		    			},
		    			saveObject : function(value, i){
		    				var url = this.getUrl(this.config.url.save);
			    			if(url){
			    				$http.post(url, value)
				    				.success(function(data) {
				    					this.searchresult[i].selected = false;
				    					this.searchresult[i].edit=false;
				    					this.searchresult[i].trClass = undefined;
				    					this.searchresultMaster[i] = angular.copy(this.searchresult[i]);
				    					this.searchresult[i].trClass = "success";
				    				})
				    				.error(function(data) {
				    					this.searchresult[i].trClass = "error";
				    				});
		    				}else{
		    					console.log('no url define for save ! '+this.config.url.save);
		    				}
		    				
		    			},
		    			
		    			/**
		    			 *  Remove the selected table line
		    			 */
		    			remove : function(){
		    				for(var i = 0; i < this.searchresult.length; i++){
		    					if(this.searchresult[i].selected){
		    						this.searchresult.splice(i,1);				
		    						this.searchresultMaster.splice(i,1);
		    						i--;
		    						var url = this.getUrl(this.config.url.remove);
					    			if(url){
					    				//TODO must be complete
					    			}else{
				    					console.log('no url define for remove ! '+this.config.url.remove);
				    				}
		    					}						
		    				}
		    				this.config = angular.copy(this.configMaster);
		    			},
		    			/**
		    			 * show one element
		    			 * work only with tab on the left
		    			 */
		    			show : function(){
		    				if(angular.isFunction(this.config.addshow)){
			    				for(var i = 0; i < this.searchresult.length; i++){
			    					if(this.searchresult[i].selected){
			    						this.config.addshow(this.searchresult[i]);
			    					}						
			    				}		    			
		    				}else{
		    					console.log("addshow is not defined !");
		    				}
		    			},
		    			/**
		    			 * set Edit all column or just one
		    			 * @param editColumnName : column name
		    			 */
		    			setEditColumn : function(columnId){		
		    				var find = false;
		    				for(var i = 0; i < this.searchresult.length; i++){
		    					if(this.searchresult[i].selected){
		    						this.searchresult[i].edit=true;
		    						find = true;
		    					
		    					}else{
		    						this.searchresult[i].edit=false;
		    					}
		    				}
		    				if(find){
		    					this.config.edit = true;			
		    					if(columnId){  (new Function("config","config.editColumn."+columnId+"=true"))(this.config);}
		    					else this.config.editColumn.all = true;
		    				}
		    			},
		    			/**
		    			 * Test if a column must be in edition mode
		    			 * @param editColumnName : column name
		    			 * @param line : the line in the table
		    			 */
		    			isEdit : function(columnId, line){
		    				if(columnId && line){
		    					var columnEdit = (new Function("config","return config.editColumn."+columnId))(this.config);
		    					return (line.edit && columnEdit) || (line.edit && this.config.editColumn.all);
		    				}else if(columnId){
		    					var columnEdit =  (new Function("config","return config.editColumn."+columnId))(this.config);
		    					return (columnEdit || this.config.editColumn.all);
		    				}else{
		    					return this.config.edit;
		    				}
		    			},
		    			/**
		    			 * Update all line with the same value
		    			 * @param updateColumnName : column name
		    			 */
		    			updateColumn : function(columnPropertyName, columnId){	
		    				for(var i = 0; i < this.searchresult.length; i++){
		    					if(this.searchresult[i].selected){
		    						var fn = new Function("searchresult", "config","searchresult."+columnPropertyName+"=config.updateColumn."+columnId);
		    						fn(this.searchresult[i], this.config);				
		    					}
		    				}
		    			},
		    			//Hide a column
		    			/**
		    			 * set the hide column
		    			 * @param hideColumnName : column name
		    			 */
		    			setHideColumn : function(columnId){	
		    				var fn = new Function("config", "if(!config.hideColumn."+columnId+"){config.hideColumn."+columnId+"=true;} else{ config.hideColumn."+columnId+"=false;}");
		    				fn(this.config);		
		    			},
		    			/**
		    			 * Test if a column must be hide
		    			 * @param hideColumnName : column name 
		    			 */
		    			isHide : function(columnId){
		    				var fn = new Function("config", "if(config.hideColumn."+columnId+") return config.hideColumn."+columnId+";else return false;");
		    				return fn(this.config);
		    			},
		    			/**
		    			 * set the order column name
		    			 * @param orderColumnName : column name
		    			 */
		    			setOrderColumn : function(columnPropertyName, columnId){
		    				this.config.orderBy = columnPropertyName;
		    				var fn = new Function("config", "if(!config.orderColumn."+columnId+"){config.orderColumn."+columnId+"=true; config.orderReverse=true;} else{ config.orderColumn."+columnId+"=false; config.orderReverse=false;}");
		    				fn(this.config);
		    			}
    			};
			    var settings = $.extend(true, {}, datatable.configDefault, iConfig);
    			datatable.config = angular.copy(settings);
    			datatable.configMaster = angular.copy(settings);    			
    			return datatable;
    		}
    		return constructor;
    	}]);

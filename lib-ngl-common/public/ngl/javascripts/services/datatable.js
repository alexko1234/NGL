"use strict";

angular.module('datatableServices', []).
    	factory('datatable', ['$http','$filter', function($http, $filter){ //service to manage datatable
    		var constructor = function($scope, iConfig){
				var datatable = {
						newConfigDefault:{
							
							remove:{
								active:true,
								mode:'remote', //or local
								url:undefined
							}						
						},
						
						
						configDefault:{
							search : {
								active:true,
								mode:'remote', //or local but not implemented
								url:undefined
							},
							pagination:{
								active:true,
								mode:'remote',
								pageNumber:0,
								numberPageListMax:3,
	    						pageList:[],
								numberRecordsPerPage:10,
	    						numberRecordsPerPageList: [{number:10, clazz:'active'},{number:25, clazz:''},{number:50, clazz:''},{number:100, clazz:''},{number:150, clazz:''},{number:200, clazz:''}]
							},
							order : {
								active:true,
								mode:'remote', //or local
								by : undefined,
								reverse : false,
								columns:{}//columnIndex : true / false
							},
							show :{
								active:true,
								add:function(line){
									console.log("show : add function is not defined in the controller !!!");
								}
							},
							hide:{
								active:true,
								columns : {} //columnIndex : true / false
							},
							edit : {
								active:true,
								started : false,
								all : false,
								columns : {} //columnIndex : {edit : true/false, value:undefined}
							},
							save :{
								active:true,
								mode:'local', //or local
								url:undefined
							},
							url:{
								remove:undefined
							},
							
							showTotalNumberRecords:true,
							showButtons:false
						},
						config:undefined,
    					configMaster:undefined,
    					allResult:undefined,
    					displayResult:undefined,
    					displayResultMaster:undefined,
    					totalNumberRecords:undefined,
    					lastSearchParams : undefined, //used with pagination when length or page change
    					
    					
    					
    					//search functions
    					/**
		    			 * Search function to populate the datatable
		    			 */
		    			search : function(params){
		    				if(this.config.search.active && this.isRemoteMode(this.config.search.mode)){
			    				this.lastSearchParams = params;
			    				var url = this.getUrl(this.config.search.url);
			    				if(url){
			    					$http.get(url,{params:this.getParams(params), datatable:this}).success(function(data, status, headers, config) {		    						
			    						config.datatable.setData(data.data, data.recordsNumber);		    						
			    					});
			    				}else{
			    					console.log('no url define for search ! '+this.config.url.search);
			    				}
		    				}else{
		    					console.log("search is not active !!")
		    				}
		    			},
		    			/**
		    			 * Set all data used by search method or directly when local data
		    			 */
		    			setData:function(data, recordsNumber){
		    				var configPagination = this.config.pagination;
		    				if(configPagination.active && !this.isRemoteMode(this.config.search.mode)){
		    					this.config.pagination.pageNumber=0;
		    				}
		    				this.allResult = data;
		    				this.totalNumberRecords = recordsNumber;
		    				this.sortAllResult();
		    				this.computeDisplayResult();
		    				this.computePaginationList();
		    			},
		    			/**
		    			 * Selected only the records will be displayed.
		    			 * Based on pagination configuration
		    			 */
		    			computeDisplayResult: function(){
		    				//to manage local pagination
		    				var configPagination = this.config.pagination;
		    				if(configPagination.active && !this.isRemoteMode(configPagination.mode)){
		    					this.displayResult = angular.copy(this.allResult.slice((configPagination.pageNumber*configPagination.numberRecordsPerPage), 
		    							(configPagination.pageNumber*configPagination.numberRecordsPerPage+configPagination.numberRecordsPerPage)));
		    				}else{ //to manage all records or server pagination
		    					this.displayResult = angular.copy(this.allResult);		    					
		    				}
		    				this.displayResultMaster = angular.copy(this.displayResult);		    				
		    			},
		    			//pagination functions
		    			/**
		    			 * compute the pagination item list
		    			 */
		    			computePaginationList: function(){
		    				var configPagination = this.config.pagination;		    						    						    				
		    				if(configPagination.active){
		    					configPagination.pageList = [];
			    				var currentPageNumber = configPagination.pageNumber;
	    						var nbPages = Math.ceil(this.totalNumberRecords / configPagination.numberRecordsPerPage);
		    					
	    						if(nbPages > 1 && nbPages <= configPagination.numberPageListMax){
			    					for(var i = 0; i < nbPages; i++){
			    						configPagination.pageList.push({number:i, label:i+1,  clazz:(i!=currentPageNumber)?'':'active'});
			    					}
		    					}else if (nbPages > configPagination.numberPageListMax){
		    						var min = currentPageNumber - ((configPagination.numberPageListMax-1)/2);
		    						var max = currentPageNumber + ((configPagination.numberPageListMax-1)/2)+1;
		    						if(min < 0){
		    							min=0;
		    						}else if(min > nbPages - configPagination.numberPageListMax){
		    							min = nbPages - configPagination.numberPageListMax;
		    						}
		    							
		    						if(max < configPagination.numberPageListMax){
		    							max=configPagination.numberPageListMax
		    						}else if(max > nbPages){
		    							max=nbPages;
		    						}
		    						
		    						configPagination.pageList.push({number:0, label:'<<',  clazz:(currentPageNumber!=min)?'':'disabled'});
		    						configPagination.pageList.push({number:currentPageNumber-1, label:'<',  clazz:(currentPageNumber!=min)?'':'disabled'});
		    						
		    						for(; min < max; min++){
		    							configPagination.pageList.push({number:min, label:min+1,  clazz:(min!=currentPageNumber)?'':'active'});
			    					}
		    						
		    						configPagination.pageList.push({number:currentPageNumber+1, label:'>',  clazz:(currentPageNumber!=max-1)?'':'disabled'});
		    						configPagination.pageList.push({number:nbPages-1, label:'>>',  clazz:(currentPageNumber!=max-1)?'':'disabled'});
		    					}		    					
		    				}else{
		    					console.log("pagination is not active !!!");
		    				}		    				
		    			},
    					/**
    					 * Set the number of records by page
    					 */
    					setNumberRecordsPerPage:function(numberRecordsPerPageElement){
    						if(this.config.pagination.active){
	    						if(angular.isObject(numberRecordsPerPageElement)){
	    							this.config.pagination.numberRecordsPerPage = numberRecordsPerPageElement.number;
	    							numberRecordsPerPageElement.clazz='active';
	    							for(var i = 0; i < this.config.pagination.numberRecordsPerPageList.length; i++){
	    								if(this.config.pagination.numberRecordsPerPageList[i].number != numberRecordsPerPageElement.number){
	    									this.config.pagination.numberRecordsPerPageList[i].clazz='';
	    								}
	    							}
	    							//reinit to first page
	    							if(this.config.edit.active && this.config.edit.start){
	    								//TODO add a warning popup
	    								console.log("edit is active, you lost all modification !!");
	    								this.config.edit = angular.copy(this.configMaster.edit); //reinit edit
	    							}
	    							this.config.pagination.pageNumber=0;
	    							if(this.isRemoteMode(this.config.pagination.mode)){
	    								this.search(this.lastSearchParams);
	    							}else{
	    								this.computeDisplayResult();
	    								this.computePaginationList();
	    							}
	    						}
    						}else{
		    					console.log("pagination is not active !!!");
		    				}	
    					},
    					/**
    					 * Change the page result
    					 */
    					setPageNumber:function(page){
    						if(this.config.pagination.active){
	    						if(angular.isObject(page) && page.clazz == ''){
	    							if(this.config.edit.active && this.config.edit.start){
	    								//TODO add a warning popup
	    								console.log("edit is active, you lost all modification !!");
	    								this.config.edit = angular.copy(this.configMaster.edit); //reinit edit
	    							}
	    							
		    						this.config.pagination.pageNumber=page.number;
		    						if(this.isRemoteMode(this.config.pagination.mode)){
										this.search(this.lastSearchParams);
									}else{
										this.computeDisplayResult();
										this.computePaginationList();
									}
	    						}
    						}else{
		    					console.log("pagination is not active !!!");
		    				}
    					},
    					isPagination: function(){
		    				return (this.config.pagination.active && this.config.pagination.pageList.length > 0);
		    			},	
    					//order functions
    					/**
		    			 * Sort all result
		    			 */
		    			sortAllResult : function(){
		    				if(this.config.order.active && !this.isRemoteMode(this.config.order.mode)){
		    					this.allResult = $filter('orderBy')(this.allResult,this.config.order.by,this.config.order.reverse);		    					
		    				}
		    			},	
		    			/**
		    			 * set the order column name
		    			 * @param orderColumnName : column name
		    			 */
		    			setOrderColumn : function(columnPropertyName, columnId){
		    				if(this.config.order.active){
		    					this.config.order.by = columnPropertyName;
			    				var fn = new Function("config", "if(!config.order.columns."+columnId+")" +
			    						"{config.order.columns."+columnId+"=true; config.order.reverse=true;} " +
			    								"else{ config.order.columns."+columnId+"=false; config.order.reverse=false;}");
			    				fn(this.config);
			    				
		    					if(!this.isRemoteMode(this.config.order.mode)){
		    						this.sortAllResult(); //sort all the result
				    				this.computeDisplayResult(); //redefined the result must be displayed
			    				} else if(this.config.order.active){
			    					this.search(this.lastSearchParams);
			    				}		    					
		    				} else{
		    					console.log("order is not active !!!");
		    				}
		    			},
		    			//show
		    			/**
		    			 * show one element
		    			 * work only with tab on the left
		    			 */
		    			show : function(){
		    				if(this.config.show.active && angular.isFunction(this.config.show.add)){
			    				for(var i = 0; i < this.displayResult.length; i++){
			    					if(this.displayResult[i].selected){
			    						this.config.show.add(this.displayResult[i]);
			    					}						
			    				}		    			
		    				}else{
		    					console.log("show is not active !");
		    				}
		    			},
		    			//Hide a column
		    			/**
		    			 * set the hide column
		    			 * @param hideColumnName : column name
		    			 */
		    			setHideColumn : function(columnId){	
		    				if(this.config.hide.active){
			    				var fn = new Function("config", "if(!config.hide.columns."+columnId+")" +
			    						"{config.hide.columns."+columnId+"=true;} else {config.hide.columns."+columnId+"=false;}");
			    				fn(this.config);		
		    				}else{
		    					console.log("hide is not active !");
		    				}
		    			},
		    			/**
		    			 * Test if a column must be hide
		    			 * @param hideColumnName : column name 
		    			 */
		    			isHide : function(columnId){
		    				if(this.config.hide.active){
				    			var fn = new Function("config", "if(config.hide.columns."+columnId+") return config.hide.columns."+columnId+";else return false;");
				    			return fn(this.config);
		    				}else{
		    					console.log("hide is not active !");
		    					return false;
		    				}
		    			},
		    			//edit
		    			
		    			/**
		    			 * set Edit all column or just one
		    			 * @param editColumnName : column name
		    			 */
		    			setEditColumn : function(columnId){	
		    				if(this.config.edit.active){
			    				var find = false;
			    				for(var i = 0; i < this.displayResult.length; i++){
			    					if(this.displayResult[i].selected){
			    						this.displayResult[i].edit=true;
			    						find = true;
			    					
			    					}else{
			    						this.displayResult[i].edit=false;
			    					}
			    				}
			    				if(find){
			    					this.config.edit.start = true;			
			    					if(columnId){  
			    						(new Function("config","if(angular.isUndefined(config.edit.columns."+columnId+"))config.edit.columns."+columnId+"={}"))(this.config);			    						
			    						(new Function("config","config.edit.columns."+columnId+".edit=true"))(this.config);
			    					}
			    					else this.config.edit.all = true;
			    				}
		    				}else{
		    					console.log("edit is not active !");
		    				}
		    			},
		    			/**
		    			 * Test if a column must be in edition mode
		    			 * @param editColumnName : column name
		    			 * @param line : the line in the table
		    			 */
		    			isEdit : function(columnId, line){
		    				if(this.config.edit.active){
			    				if(columnId && line){
			    					(new Function("config","if(angular.isUndefined(config.edit.columns."+columnId+"))config.edit.columns."+columnId+"={}"))(this.config);			    								    							    					
			    					var columnEdit = (new Function("config","return config.edit.columns."+columnId+".edit"))(this.config);
			    					return (line.edit && columnEdit) || (line.edit && this.config.edit.all);
			    				}else if(columnId){
			    					(new Function("config","if(angular.isUndefined(config.edit.columns."+columnId+"))config.edit.columns."+columnId+"={}"))(this.config);			    								    								    					
			    					var columnEdit = (new Function("config","return config.edit.columns."+columnId+".edit"))(this.config);			    					
			    					return (columnEdit || this.config.edit.all);
			    				}else{
			    					return this.config.edit.start;
			    				}
		    				}else{
		    					console.log("edit is not active !");
		    					return false;
		    				}
		    			},
		    			/**
		    			 * Test if edit mode is start
		    			 */
		    			isEditStart: function(){
		    				if(this.config.edit.active){
		    					return this.config.edit.start;
		    				}else{
		    					console.log("edit is not active !");
		    					return false;
		    				}
		    			},
		    			/**
		    			 * Update all line with the same value
		    			 * @param updateColumnName : column name
		    			 */
		    			updateColumn : function(columnPropertyName, columnId){
		    				if(this.config.edit.active){
			    				for(var i = 0; i < this.displayResult.length; i++){
			    					if(this.displayResult[i].selected){
			    						var fn = new Function("displayResult", "config","displayResult."+columnPropertyName+"=config.edit.columns."+columnId+".value");
			    						fn(this.displayResult[i], this.config);				
			    					}
			    				}
		    				}else{
		    					console.log("edit is not active !");		    				
		    				}
		    			},
		    			//save
		    			/**
		    			 * Save the selected table line
		    			 */
		    			save : function(){
		    				if(this.config.save.active){
			    				for(var i = 0; i < this.displayResult.length; i++){
			    					if(this.displayResult[i].selected){
			    						if(this.isRemoteMode(this.config.save.mode)){
			    							this.saveObject(this.displayResult[i], i);
			    						}else{		    									    		    				
			    							this.updateRecords(i);
			    						}
			    					}						
			    				}
		    				}else{
		    					console.log("save is not active !");		    				
		    				}
		    			},
		    			saveObject : function(value, i){
		    				var url = this.getUrl(this.config.save.url);
			    			if(url){
			    				$http.post(url, value, {datatable:this})
				    				.success(function(data, status, headers, config) {
				    					this.displayResult[i].selected = false;
		    							this.displayResult[i].edit=false;
		    							this.displayResult[i].trClass = undefined;
		    							this.displayResultMaster[i] = angular.copy(this.displayResult[i]);
		    							this.displayResult[i].trClass = "success";
				    				})
				    				.error(function(data, status, headers, config) {
				    					this.displayResult[i].trClass = "error";
				    				});
		    				}else{
		    					console.log('no url define for save ! ');
		    				}
		    				
		    			},
		    			
		    			/**
		    			 * Call after save to update the records property
		    			 */
		    			updateRecords: function(i){
		    				this.displayResult[i].selected = false;
							this.displayResult[i].edit=false;
							this.displayResult[i].trClass = undefined;
							//update in the all result table
							var j = i;
							if(this.config.pagination.active && !this.isRemoteMode(this.config.pagination.mode)){
								j = i + (this.config.pagination.pageNumber*this.config.pagination.numberRecordsPerPage);
							}
							this.allResult[j] = angular.copy(this.displayResult[i]);
							this.displayResult[i].trClass = "success";							
		    			},
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
		    					for(var i = 0; i < this.displayResult.length; i++){
		    						this.displayResult[i].selected=true;
		    						this.displayResult[i].trClass="row_selected";
		    					}
		    				}
		    			},
		    			/**
		    			 * indicate if at least one line is selected
		    			 */
		    			isSelect: function(){
		    				for(var i = 0; this.displayResult && i < this.displayResult.length; i++){
	    						if(this.displayResult[i].selected)return true;	    						
	    					}
		    				return false;
		    			},
		    			/**
		    			 * cancel edit, hide and selected lines only
		    			 */
		    			cancel : function(){
		    				/*cancel only edit and hide mode */
		    				this.config.edit = angular.copy(this.configMaster.edit);
		    				this.config.hide = angular.copy(this.configMaster.hide);
		    				
		    				this.computeDisplayResult();
		    				this.computePaginationList();
		    			},
		    			
		    			/**
		    			 * set if button is present
		    			 */
		    			setShowButtons: function(boolean){
		    				this.config.showButtons=boolean;
		    				this.configMaster = angular.copy(this.config); //in case of cancel to keep showButtons config 
		    				
		    			},
		    			isShowToolbar: function(){
		    				return (this.config.showButtons || this.config.pagination.active ||  this.config.showTotalNumberRecords);
		    			},
		    			
		    			/**
		    			 *  Remove the selected table line
		    			 */
		    			remove : function(){
		    				for(var i = 0; i < this.displayResult.length; i++){
		    					if(this.displayResult[i].selected){
		    						this.displayResult.splice(i,1);				
		    						this.displayResultMaster.splice(i,1);
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
		    			
		    			
		    			//helper functions
		    			/**
		    			 * Add pagination parameters if needed
		    			 */
		    			getParams : function(params){
		    				if(angular.isUndefined(params)){
	    						params = {};
	    					}
		    				if(this.config.pagination.active && this.isRemoteMode(this.config.pagination.mode)){
		    					params.pageNumber = this.config.pagination.pageNumber;
		    					params.numberRecordsPerPage = this.config.pagination.numberRecordsPerPage;		    					
		    				}
		    				if(this.config.order.active && this.isRemoteMode(this.config.order.mode)){
		    					params.orderBy = this.config.order.by;
		    					params.orderSense = (this.config.order.reverse)?"-1":"1";
		    				}
		    				return params;
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
		    			 * test is remote mode
		    			 */
		    			isRemoteMode : function(mode){
		    				if(mode && mode == 'remote'){
		    					return true;
		    				}else{
		    					return false;
		    				}
		    			}
		    			
    			};
			    var settings = $.extend(true, {}, datatable.configDefault, iConfig);
    			datatable.config = angular.copy(settings);
    			datatable.configMaster = angular.copy(settings);    
    			return datatable;
    		}
    		return constructor;
    	}]);

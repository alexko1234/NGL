"use strict";

angular.module('datatableServices', []).
    	factory('datatable', ['$http','$filter', function($http, $filter){ //service to manage datatable
    		var constructor = function($scope, iConfig){
				var datatable = {
						
						configDefault:{
							columns : [], //ex : [{id:'',header:'',order:true,hide:false,edit:true}]
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
								columns:{}//key is the column index
							},
							show :{
								active:false,
								showButton : true,
								add:function(line){
									console.log("show : add function is not defined in the controller !!!");
								}
							},
							hide:{
								active:false,
								columns : {} //columnIndex : true / false
							},
							edit : {
								active:false,
								withoutSelect:false, //edit all line
								showButton : true,
								start : false,
								all : false,
								columns : {}, //columnIndex : {edit : true/false, value:undefined}
							},
							save :{
								active:false,
								withoutEdit:false,
								showButton : true,
								mode:'remote', //or local
								url:undefined,
								callback : undefined, //used to have a callback after save all element. the datatable is pass to callback method
								start : false, //if save started
								number : 0, //number of element in progress
								error : 0
							},
							remove:{
								active:false,
								withEdit:false, //to remove a line in edition mode
								showButton : true,
								mode:'remote', //or local
								url:undefined, //function with object in parameter !!!
								callback : undefined, //used to have a callback after remove all element. the datatable is pass to callback method
								start:false,
								counter:0,
								number:0, //number of element in progress
								error:0								
							},
							select:{
								active:true,
								showButton:true,
								isSelectAll:false
							},
							cancel : {
								active:true,
								showButton:true
							},
							otherButtons:{
								active:false
							},
							messages:{
								active:false,
								errorClass:'alert alert-error',
								successClass: 'alert alert-success',
								errorKey:{save:'datatable.msg.error.save',remove:'datatable.msg.error.remove'},
								successKey:{save:'datatable.msg.success.save',remove:'datatable.msg.success.remove'},
								text:undefined,
								clazz:undefined,
								transformKey : function(key, args){
									return Messages(key, args);
								}
							},
							showTotalNumberRecords:true
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
			    					throw 'no url define for search ! ';
			    				}
		    				}else{
		    					console.log("search is not active !!")
		    				}
		    			},
		    			/**
		    			 * Search with the last parameters
		    			 */
		    			searchWithLastParams : function(){
		    				this.search(this.lastSearchParams);
		    			},
		    			
		    			/**
		    			 * Set all data used by search method or directly when local data
		    			 */
		    			setData:function(data, recordsNumber){
		    				var configPagination = this.config.pagination;
		    				if(configPagination.active && !this.isRemoteMode(configPagination.mode)){
		    					this.config.pagination.pageNumber=0;
		    				}
		    				this.allResult = data;
		    				this.totalNumberRecords = recordsNumber;
		    				this.sortAllResult();
		    				this.computeDisplayResult();
		    				this.computePaginationList();
		    			},
		    			/**
		    			 * Return all the data
		    			 */
		    			getData:function(){
		    				return this.allResult;
		    			},
		    			/**
		    			 * Add data
		    			 */
		    			addData: function(data){
		    				if(!angular.isUndefined(data) && (angular.isArray(data) && data.length > 0)){
			    				var configPagination = this.config.pagination;
			    				if(configPagination.active && !this.isRemoteMode(configPagination.mode)){
			    					this.config.pagination.pageNumber=0;
			    				}
			    				for(var i = 0 ; i < data.length; i++){
			    					this.allResult.push(data[i]);				    				
			    				}
			    				this.totalNumberRecords = this.allResult.length;
			    				this.sortAllResult();
			    				this.computeDisplayResult();
			    				this.computePaginationList();
			    			}
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
	    							if(this.config.edit.active && this.config.edit.start){
	    								//TODO add a warning popup
	    								console.log("edit is active, you lost all modification !!");
	    								this.config.edit = angular.copy(this.configMaster.edit); //reinit edit
	    							}
	    							//reinit to first page	    							
	    							this.config.pagination.pageNumber=0;
	    							if(this.isRemoteMode(this.config.pagination.mode)){
	    								this.searchWithLastParams();
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
	    						if(angular.isObject(page) && page.clazz === ''){
	    							if(this.config.edit.active && this.config.edit.start){
	    								//TODO add a warning popup
	    								console.log("edit is active, you lost all modification !!");
	    								this.config.edit = angular.copy(this.configMaster.edit); //reinit edit
	    							}
	    							
		    						this.config.pagination.pageNumber=page.number;
		    						if(this.isRemoteMode(this.config.pagination.mode)){
										this.searchWithLastParams();
									}else{
										this.computeDisplayResult();
										this.computePaginationList();
									}
	    						}
    						}else{
		    					console.log("pagination is not active !!!");
		    				}
    					},
    					isShowPagination: function(){
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
		    					if(this.config.order.by != columnPropertyName){
		    						this.config.order.by = columnPropertyName;
		    						this.config.order.reverse = false;
		    					}else{
		    						this.config.order.reverse = !this.config.order.reverse;
		    					}
		    					
		    					for(var i = 0; i < this.config.columns.length; i++){
		    						var fn = undefined;
		    						if(this.config.columns[i].id === columnId){
		    							fn = new Function("config", "config.order.columns."+this.config.columns[i].id+"=true;");
		    						}else{
		    							fn = new Function("config", "config.order.columns."+this.config.columns[i].id+"=false;");
		    						}
		    						fn(this.config);		    						
		    					}
		    					if(this.config.edit.active && this.config.edit.start){
    								//TODO add a warning popup
    								console.log("edit is active, you lost all modification !!");
    								this.config.edit = angular.copy(this.configMaster.edit); //reinit edit
    							}
		    					if(!this.isRemoteMode(this.config.order.mode)){
		    						this.sortAllResult(); //sort all the result
				    				this.computeDisplayResult(); //redefined the result must be displayed
			    				} else if(this.config.order.active){
			    					this.searchWithLastParams();
			    				}		    					
		    				} else{
		    					console.log("order is not active !!!");
		    				}
		    			},
		    			getOrderColumnClass : function(columnId){
		    				if(this.config.order.active){
		    					var fn = new Function("config", 
		    							"if(!config.order.columns."+columnId+") {return 'icon-sort';}" +
			    						"else if(config.order.columns."+columnId+" && !config.order.reverse) {return 'icon-sort-up';}" +			    						
			    						"else if(config.order.columns."+columnId+" && config.order.reverse) {return 'icon-sort-down';}");
			    				return fn(this.config);			    						    					    					
		    				} else{
		    					console.log("order is not active !!!");
		    				}
		    			},
		    			/**
		    			 * indicate if we can order the table
		    			 */
		    			canOrder: function(){
		    				return (this.config.edit.active ? !this.config.edit.start : this.config.order.active);
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
		    			 * @param columnId : column id 
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
			    					if(this.displayResult[i].selected || this.config.edit.withoutSelect){
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
		    			 * indicate if at least one line is selected
		    			 */
		    			canEdit: function(){
		    				return (this.config.edit.withoutSelect ? true : this.isSelect());
		    			},
		    			/**
		    			 * Update all line with the same value
		    			 * @param updateColumnName : column name
		    			 */
		    			updateColumn : function(columnPropertyName, columnId){
		    				if(this.config.edit.active){
			    				for(var i = 0; i < this.displayResult.length; i++){
			    					if(this.displayResult[i].edit){
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
		    					this.config.save.number = 0;
		    					this.config.save.error = 0;
		    					this.config.save.start = true;
		    					for(var i = 0; i < this.displayResult.length; i++){
			    					if(this.displayResult[i].edit || this.config.save.withoutEdit){
			    						//remove datatable properties
			    						this.config.save.number++;
			    						this.displayResult[i].selected = undefined;
			    						this.displayResult[i].edit = undefined;
			    						this.displayResult[i].trClass = undefined;					    				
			    						if(this.isRemoteMode(this.config.save.mode)){
			    							this.saveRemote(this.displayResult[i], i);
			    						} else{		    									    		    				
			    							this.saveLocal(i);
			    						}
			    					}						
			    				}
		    					
		    					if(!this.isRemoteMode(this.config.save.mode)){
	    							this.saveFinish();
	    						}
		    				}else{
		    					console.log("save is not active !");		    				
		    				}
		    			},
		    			saveRemote : function(value, i){
		    				var url = this.getUrl(this.config.save.url);
			    			if(url){
			    				//call url
			    				$http.post(url, value, {datatable:this,index:i})
				    				.success(function(data, status, headers, config) {
				    					config.datatable.saveLocal(data, config.index);
				    					config.datatable.saveFinish();
				    				})
				    				.error(function(data, status, headers, config) {
				    					config.datatable.displayResult[config.index].trClass = "error";
				    					config.datatable.displayResult[config.index].edit = true;
				    					config.datatable.config.save.error++;
				    					config.datatable.config.save.number--;
				    					config.datatable.saveFinish();
				    					//TODO add error messages as in datatable jquery
				    				});
		    				}else{
		    					throw 'no url define for save !';
		    				}
		    				
		    			},	
		    			/**
		    			 * Call after save to update the records property
		    			 */
		    			saveLocal: function(data, i){
		    				if(this.config.save.active){
		    					if(data){
		    						this.displayResult[i] = data;
		    					}
			    				//update in the all result table
								var j = i;
								if(this.config.pagination.active && !this.isRemoteMode(this.config.pagination.mode)){
									j = i + (this.config.pagination.pageNumber*this.config.pagination.numberRecordsPerPage);
								}
								this.allResult[j] = angular.copy(this.displayResult[i]);
								this.displayResult[i].trClass = "success";
								this.config.save.number--;
		    				}else{
		    					console.log("save is not active !");		    				
		    				}
		    			},
		    			/**
		    			 * Call when a save local or remote is finish
		    			 */
		    			saveFinish: function(){
		    				if(this.config.save.number === 0){
		    					if(this.config.save.error > 0){
		    						this.config.messages.clazz = this.config.messages.errorClass;
		    						this.config.messages.text = this.config.messages.transformKey(this.config.messages.errorKey.save, this.config.save.error);
		    					}else{
		    						this.config.messages.clazz = this.config.messages.successClass;
		    						this.config.messages.text = this.config.messages.transformKey(this.config.messages.successKey.save);
		    					}
		    					
		    					if(angular.isFunction(this.config.save.callback)){
			    					this.config.save.callback(this);
			    				}
		    					
		    					this.config.save.error = 0;
		    					this.config.save.start = false;
								this.config.edit.start = false;
		    						    					
		    				}
	    					
		    			},		    			
		    			/**
		    			 * Test if save mode can be enable
		    			 */
		    			canSave: function(){
		    				if(this.config.edit.active && !this.config.save.withoutEdit && !this.config.save.start){
		    					return this.config.edit.start;
		    				}else if(this.config.edit.active && this.config.save.withoutEdit && !this.config.save.start){
		    					return true;
		    				}else{
		    					return false;
		    				}
		    			},
		    			//remove
		    			/**
		    			 *  Remove the selected table lines
		    			 */
		    			remove : function(){
		    				if(this.config.remove.active && !this.config.remove.start){
		    					var localDisplayResult = angular.copy(this.displayResult);
		    					this.config.remove.counter = 0;
		    					this.config.remove.start = true;
		    					this.config.remove.number = 0;
		    					this.config.remove.error = 0;
		    					for(var i = 0; i < localDisplayResult.length; i++){
			    					if(localDisplayResult[i].selected && (!localDisplayResult[i].edit || this.config.remove.withEdit)){
			    						this.config.remove.number++;
			    						this.removeLocal(i);			    										    						
			    					}						
			    				}
		    					if(!this.isRemoteMode(this.config.remove.mode)){
		    						this.removeFinish();
		    					}
		    				}else{
		    					console.log("remove is not active !");		    				
		    				}
		    			},
		    			
		    			/**
		    			 * Call after save to update the records property
		    			 */
		    			removeLocal: function(i){
		    				if(this.config.remove.active && this.config.remove.start){
			    				//update in the all result table
								var j ;
								if(this.config.pagination.active && !this.isRemoteMode(this.config.pagination.mode)){
									j = (i + (this.config.pagination.pageNumber*this.config.pagination.numberRecordsPerPage)) - this.config.remove.counter;
								}else{
									j = i - this.config.remove.counter
								}
								var removeArray = this.allResult.splice(j,1);
								this.displayResult.splice((i - this.config.remove.counter),1);
								this.config.remove.counter++;
								this.totalNumberRecords--;
								
								if(this.isRemoteMode(this.config.remove.mode)){
	    							this.removeRemote(removeArray[0]);
	    						}else{
	    							this.config.remove.number--;	    		    				
	    						}
								
		    				} else{
		    					console.log("remove is not active !");		    				
		    				}
		    			},
		    			
		    			removeRemote : function(value){
		    				if(this.config.remove.active && this.config.remove.start){
			    				var url = this.getUrl(this.config.remove.url);
				    			if(url){
				    				$http['delete'](url(value), {datatable:this,value:value})
					    				.success(function(data, status, headers, config) {
					    					config.datatable.config.remove.number--;						    				
					    					config.datatable.removeFinish();
					    				})
					    				.error(function(data, status, headers, config) {
					    					config.datatable.config.remove.error++;
					    					config.datatable.config.remove.number--;						    				
					    					config.datatable.removeFinish();
					    				});
			    				}else{
			    					throw  'no url define for remove ! ';
			    				}
		    				} else{
		    					console.log("remove is not active !");		    				
		    				}		    				
		    			},
		    			/**
		    			 * Call when a remove is done
		    			 */
		    			removeFinish : function(){
		    				if(this.config.remove.number === 0){
		    					if(this.config.remove.error > 0){
		    						this.config.messages.clazz = this.config.messages.errorClass;
		    						this.config.messages.text = this.config.messages.transformKey(this.config.messages.errorKey.remove, this.config.remove.error);
		    					}else{
		    						this.config.messages.clazz = this.config.messages.successClass;
		    						this.config.messages.text = this.config.messages.transformKey(this.config.messages.successKey.remove);
		    					}
		    					
		    					if(angular.isFunction(this.config.remove.callback)){
			    					this.config.remove.callback(this);
			    				}	
		    					
		    					this.computePaginationList();		    					
		    					this.config.remove.error = 0;
		    					this.config.remove.start = false;
		    					this.config.remove.counter = 0;
		    				}
		    			},
		    			
		    			/**
		    			 * indicate if at least one line is selected and not in edit mode
		    			 */
		    			canRemove: function(){
		    				if(this.config.remove.active && !this.config.remove.start){
			    				for(var i = 0; this.displayResult && i < this.displayResult.length; i++){
		    						if(this.displayResult[i].selected && (!this.displayResult[i].edit || this.config.remove.withEdit))return true;	    						
		    					}
		    				}else{
		    					console.log("remove is not active !");
		    					return false;
		    				}
		    			},
		    			//select
    					/**
    					 * Select all the table line or just one
    					 */
						select : function(line){
							if(this.config.select.active){
			    				if(line){
			    					if(!line.selected){
			    						line.selected=true;
			    						line.trClass="info";
			    					}
									else{
										line.selected=false;
			    						line.trClass="";
									}
			    				}
							}else{
								console.log("select is not active");
							}
		    			},
		    			/**
		    			 * Select or unselect all line
		    			 */
		    			selectAll : function(value){
		    				if(this.config.select.active){
			    				this.config.select.isSelectAll = value;
			    				for(var i = 0; i < this.displayResult.length; i++){
			    					if(value){
			    						this.displayResult[i].selected=true;
			    						this.displayResult[i].trClass="info";
			    					}else{
			    						this.displayResult[i].selected=false;
			    						this.displayResult[i].trClass="";
			    					}
		    					}
		    				}else{
								console.log("select is not active");
							}
		    			},	    			
		    			
		    			/**
		    			 * Return all selected element and unselect the data
		    			 */
		    			getSelection : function(unselect){
		    				var selection = [];
		    				for(var i = 0; i < this.displayResult.length; i++){
		    					if(this.displayResult[i].selected){
		    						//unselect selection
		    						if(unselect){
		    							this.displayResult[i].selected = false;
		    							this.displayResult[i].trClass="";
		    						}
		    						selection.push(angular.copy(this.displayResult[i]));
		    					}
		    				}
		    				if(unselect){this.config.select.isSelectAll = false;}
		    				return selection;
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
		    				if(this.config.cancel.active){
			    				/*cancel only edit and hide mode */
			    				this.config.edit = angular.copy(this.configMaster.edit);
			    				this.config.hide = angular.copy(this.configMaster.hide);
			    				this.config.remove = angular.copy(this.configMaster.remove);
			    				this.config.select = angular.copy(this.configMaster.select);
			    				this.config.messages = angular.copy(this.configMaster.messages);
			    				
			    				this.computeDisplayResult();
			    				this.computePaginationList();
		    				}
		    			},
		    			
		    			//helper functions		    			
		    			isShowToolbar: function(){
		    				return (this.isShowToolbarLeft() || this.isShowToolbarRight());
		    			},
		    			isShowToolbarLeft: function(){
		    				return (  this.config.edit.active ||  this.config.save.active || this.config.remove.active || this.config.hide.active || this.config.show.active || this.config.otherButtons.active);
		    			},
		    			isShowToolbarRight: function(){
		    				return (this.isShowPagination() || this.config.showTotalNumberRecords);
		    			},
		    			showButton: function(configParam){
		    				return (this.config[configParam].active && this.config[configParam].showButton);
		    			},
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
		    				} else if(angular.isFunction(url)){
		    					return url;
		    				}
		    				return undefined;
		    			},
		    			/**
		    			 * test is remote mode
		    			 */
		    			isRemoteMode : function(mode){
		    				if(mode && mode === 'remote'){
		    					return true;
		    				}else{
		    					return false;
		    				}
		    			},
		    			/**
		    			 * Set columns configuration
		    			 */
		    			setColumnsConfig: function(columns){
		    				for(var i = 0 ; i < columns.length; i++){
		    					if(columns[i].hide && !this.config.hide.active){
		    						columns[i].hide = false;
		    					}
		    					if(columns[i].order && !this.config.order.active){
		    						columns[i].order = false;
		    					}
		    					if(columns[i].edit && !this.config.edit.active){
		    						columns[i].edit = false;
		    					}
		    				}
		    				
		    				this.config.columns = columns;
		    				this.configMaster.columns = angular.copy(columns);
		    			},
		    			getColumnsConfig: function(){
		    				return this.config.columns;		    				
		    			},
		    			getConfig: function(){
		    				return this.config;		    				
		    			},
		    			setConfig: function(config){
		    				var settings = $.extend(true, {}, this.configDefault, config);
		    	    		this.config = angular.copy(settings);
		    	    		this.configMaster = angular.copy(settings);    
		    			},
		    			/**
		    			 * Return column with hide
		    			 */
		    			getHideColumns: function(){
		    				var c = [];
		    				for(var i = 0 ; i < this.config.columns.length; i++){
		    					if(this.config.columns[i].hide){
		    						c.push(this.config.columns[i]);
		    					}
		    				}
		    				return c;
		    			},
		    			/**
		    			 * Return column with hide
		    			 */
		    			getEditColumns: function(){
		    				var c = [];
		    				for(var i = 0 ; i < this.config.columns.length; i++){
		    					if(this.config.columns[i].edit)c.push(this.config.columns[i]);
		    				}
		    				return c;
		    			}
		    			
    			};
			    var settings = $.extend(true, {}, datatable.configDefault, iConfig);
    			datatable.config = angular.copy(settings);
    			datatable.configMaster = angular.copy(settings);    
    			return datatable;
    		}
    		return constructor;
    	}]);

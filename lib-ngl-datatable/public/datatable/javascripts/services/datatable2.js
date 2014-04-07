"use strict";

angular.module('datatableServices', []).
    	factory('datatable', ['$http','$filter','$parse','$compile', '$sce', '$window', '$q', function($http, $filter,$parse,$compile,$sce,$window, $q){ //service to manage datatable
    		var constructor = function($scope, iConfig){
				var datatable = {
						configDefault:{
							name:"datatable",
							extraHeaders:{
								number:0,// Number of extra headers
								list:{},//if dynamic=false
								dynamic:true //if dynamic=true, the headers will be auto generated
							},//ex: extraHeaders:{number:2,dynamic:false,list:{0:[{"label":"test","colspan":"1"},{"label":"a","colspan":"1"}],1:[{"label":"test2","colspan":"5"}]}}
							columns : [], /*ex : 
												{
													"header":"Code Container", //the title //used by default Messages
													"property":"code", //the property to bind or function used to extract the value
													"filter":"", angular filter to filter the value only used in read mode
													"render" : function() //render the column used to add style around value
													"id":'', //the column id
													"edit":false, //can be edited or not
													"hide":true, //can be hidden or not
													"order":true, //can be ordered or not
													"type":"text"/"number"/"month"/"week"/"time"/"datetime"/"range"/"color"/"mail"/"tel"/"url"/"date", //the column type
													"choiceInList":false, //when the column is in edit mode, the edition is a list of choices or not
													"listStyle":"select"/"radio", //if choiceInList=true, listStyle="select" is a select input, listStyle="radio" is a radio input
													"possibleValues":null, //The list of possible choices
													"format" : null, //number format or date format or datetime format
													"extraHeaders":{"0":"Inputs"}, //the extraHeaders list
													"tdClass : function with data and property as parameter than return css class or just the css class"
													"
												  }*/
							columnsUrl:undefined, //Load columns config
							lines : {
								trClass : undefined // function with data than return css class or just the css class
							},
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
	    						numberRecordsPerPageList: [{number:10, clazz:''},{number:25, clazz:''},{number:50, clazz:''},{number:100, clazz:''}]
							},
							order : {
								active:true,
								showButton : true,
								mode:'remote', //or local
								by : undefined,
								reverse : false,
								columns:{}//key is the column index
							},
							show : {
								active:false,
								showButton : true,
								add:function(line){
									console.log("show : add function is not defined in the controller !!!");
								}
							},
							hide:{
								active:false,
								showButton : true,
								columns : {} //columnIndex : true / false
							},
							edit : {
								active:false,
								withoutSelect:false, //edit all line without selected it
								showButton : true,
								columnMode : false,
								byDefault : false, //put in edit mode when the datatable is build 
								start : false,
								all : false,
								columns : {}, //columnIndex : {edit : true/false, value:undefined}
							},
							save :{
								active:false,
								withoutEdit:false, //usable only for active/inactive save button by default !!!
								keepEdit:false, //keep in edit mode after safe
								showButton : true,
								changeClass : true, //change class to success or error
								mode:'remote', //or local
								url:undefined,
								batch:false, //for batch mode one url with all data
								method:'post',
								value:undefined, //used to transform the value send to the server
								callback:undefined, //used to have a callback after save all element. the datatable is pass to callback method and number of error
								start:false, //if save started
								number:0, //number of element in progress
								error:0
							},
							remove:{
								active:false,
								withEdit:false, //to remove a line in edition mode
								showButton : true,
								mode:'remote', //or local
								url:undefined, //function with object in parameter !!!
								callback : undefined, //used to have a callback after remove all element. the datatable is pass to callback method and number of error
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
								active:false,
								template:undefined
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
							showTotalNumberRecords:true,
							compact:true //mode compact pour le nom des bouttons
						},
						config:undefined,
    					configMaster:undefined,
    					allResult:undefined,
    					displayResult:undefined,
    					//displayResultMaster:undefined,
    					totalNumberRecords:undefined,
    					lastSearchParams : undefined, //used with pagination when length or page change
    					inc:0, //used for unique column ids
    					configColumnDefault:{
								edit:false, //can be edited or not
								hide:true, //can be hidden or not
								order:true, //can be ordered or not
								type:"text", //the column type
								choiceInList:false, //when the column is in edit mode, the edition is a list of choices or not
								extraHeaders:{}
    					},
    					/**
    					 * External search réinit pageNumber to 0
    					 */
    					search : function(params){
    						this.config.edit = angular.copy(this.configMaster.edit);
		    				this.config.remove = angular.copy(this.configMaster.remove);
		    				this.config.select = angular.copy(this.configMaster.select);
		    				this.config.messages = angular.copy(this.configMaster.messages);
		    				this.config.pagination.pageNumber = 0;
							this._search(params);							
    					},
    					
    					//search functions
    					/**
		    			 * Internal Search function to populate the datatable
		    			 */
		    			_search : function(params){
		    				if(this.config.search.active && this.isRemoteMode(this.config.search.mode)){
			    				this.lastSearchParams = params;
			    				var url = this.getUrlFunction(this.config.search.url);
			    				if(url){
			    					$http.get(url(),{params:this.getParams(params), datatable:this}).success(function(data, status, headers, config) {
			    						config.datatable.setData(data.data, data.recordsNumber);		    						
			    					});
			    				}else{
			    					throw 'no url define for search ! ';
			    				}
		    				}else{
		    					//console.log("search is not active !!")
		    				}
		    			},
		    			/**
		    			 * Search with the last parameters
		    			 */
		    			searchWithLastParams : function(){
		    				this._search(this.lastSearchParams);
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
		    				
		    				var _displayResult = [];
		    				
		    				if(configPagination.active && !this.isRemoteMode(configPagination.mode)){
		    					_displayResult = angular.copy(this.allResult.slice((configPagination.pageNumber*configPagination.numberRecordsPerPage), 
		    							(configPagination.pageNumber*configPagination.numberRecordsPerPage+configPagination.numberRecordsPerPage)));
		    				}else{ //to manage all records or server pagination
		    					_displayResult = angular.copy(this.allResult);		    					
		    				}
		    				
		    				this.displayResult = [];
		    				angular.forEach(_displayResult, function(value, key){
		    					 var line = {edit:undefined, selected:undefined, trClass:undefined};
	    						 this.push({data:value, line:line});
	    					}, this.displayResult);
		    				
		    				if(this.config.edit.byDefault){
		    					this.config.edit.withoutSelect = true;
		    					this.setEdit();
		    				}
		    				
		    				//this.displayResultMaster = angular.copy(this.displayResult);		    				
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
		    					//console.log("pagination is not active !!!");
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
		    					//console.log("pagination is not active !!!");
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
		    					//console.log("pagination is not active !!!");
		    				}
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
		    						if(this.config.columns[i].id === columnId){
		    							this.config.order.columns[this.config.columns[i].id] = true;
		    						}else{
		    							this.config.order.columns[this.config.columns[i].id] = false;
		    						}		    							    						
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
		    					//console.log("order is not active !!!");
		    				}
		    			},
		    			getOrderColumnClass : function(columnId){
		    				if(this.config.order.active){
		    					if(!this.config.order.columns[columnId]) {return 'fa fa-sort';}
	    						else if(this.config.order.columns[columnId] && !this.config.order.reverse) {return 'fa fa-sort-up';}		    						
	    						else if(this.config.order.columns[columnId] && this.config.order.reverse) {return 'fa fa-sort-down';}		    							    						    					    					
		    				} else{
		    					//console.log("order is not active !!!");
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
		    					angular.forEach(this.displayResult, function(value, key){
		    						if(value.line.selected){
		    							this.config.show.add(value.data);
		    						}
		    					}, this);
		    							    			
		    				}else{
		    					//console.log("show is not active !");
		    				}
		    			},
		    			//Hide a column
		    			/**
		    			 * set the hide column
		    			 * @param hideColumnName : column name
		    			 */
		    			setHideColumn : function(columnId){	
		    				if(this.config.hide.active){
			    				if(!this.config.hide.columns[columnId]){
			    					this.config.hide.columns[columnId]=true;
			    				}else {
			    					this.config.hide.columns[columnId]=false;
			    				}
			    				this.newExtraHeaderConfig();
		    				}else{
		    					//console.log("hide is not active !");
		    				}
		    				
		    			},
		    			/**
		    			 * Test if a column must be hide
		    			 * @param columnId : column id 
		    			 */
		    			isHide : function(columnId){
		    				if(this.config.hide.active && this.config.hide.columns[columnId]){
				    			return this.config.hide.columns[columnId];				    							    		
		    				}else{
		    					//console.log("hide is not active !");
		    					return false;
		    				}
		    			},		    			
		    			//edit
		    			
		    			/**
		    			 * set Edit all column or just one
		    			 * @param editColumnName : column name
		    			 */
		    			setEdit : function(columnId){	
		    				if(this.config.edit.active){
		    					this.config.edit.columns = {};
			    				var find = false;
			    				for(var i = 0; i < this.displayResult.length; i++){
			    					
			    					if(this.displayResult[i].line.selected || this.config.edit.withoutSelect){
			    						this.displayResult[i].line.edit=true;			    						
			    						find = true;			    					
			    					}else{
			    						this.displayResult[i].line.edit=false;
			    					}			    					   					
			    				}
			    				this.selectAll(false);
			    				if(find){
			    					this.config.edit.start = true;			
			    					if(columnId){
			    						if(angular.isUndefined(this.config.edit.columns[columnId])){
			    							this.config.edit.columns[columnId] = {};
			    						}
			    						this.config.edit.columns[columnId].edit=true;			    						
			    					}
			    					else this.config.edit.all = true;
			    				}
		    				}else{
		    					//console.log("edit is not active !");
		    				}
		    			},		    			
		    			/**
		    			 * Test if a column must be in edition mode
		    			 * @param editColumnName : column name
		    			 * @param line : the line in the table
		    			 */
		    			isEdit : function(columnId, line){
		    				var isEdit = false;
		    				if(this.config.edit.active){
		    					if(columnId && line){
		    						if(angular.isUndefined(this.config.edit.columns[columnId])){
		    							this.config.edit.columns[columnId] = {};
		    						}			    								    							    					
			    					var columnEdit = this.config.edit.columns[columnId].edit;
			    					isEdit = (line.edit && columnEdit) || (line.edit && this.config.edit.all);
			    				}else if(columnId){
			    					if(angular.isUndefined(this.config.edit.columns[columnId])){
		    							this.config.edit.columns[columnId] = {};
		    						}			    								    								    					
			    					var columnEdit = this.config.edit.columns[columnId].edit;			    					
			    					isEdit = (columnEdit || this.config.edit.all);
			    				}else{
			    					isEdit = (this.config.edit.columnMode && this.config.edit.start);
			    				}
		    				}
		    				return isEdit;
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
			    				var getter = $parse(columnPropertyName);
		    					for(var i = 0; i < this.displayResult.length; i++){
			    					if(this.displayResult[i].line.edit){
										getter.assign(this.displayResult[i].data,this.config.edit.columns[columnId].value);
			    					}
			    				}
		    				}else{
		    					//console.log("edit is not active !");		    				
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
		    					this.config.messages.text = undefined;
		    					this.config.messages.clazz = undefined;
		    					var data = [];
		    					var valueFunction = this.getValueFunction(this.config.save.value);
		    					for(var i = 0; i < this.displayResult.length; i++){
			    					if(this.displayResult[i].line.edit || this.config.save.withoutEdit){
			    						//remove datatable properties to avoid this data are retrieve in the json
			    						this.config.save.number++;
			    						this.displayResult[i].line.trClass = undefined;
				    					this.displayResult[i].line.selected = undefined;
				    					
			    						if(this.isRemoteMode(this.config.save.mode) && !this.config.save.batch){
			    							//add the url in table to used $q
			    							data.push(this.getSaveRemoteRequest(this.displayResult[i].data, i));			    							
			    						} else if(this.isRemoteMode(this.config.save.mode) && this.config.save.batch){
			    							//add the data in table to send in once all the result
			    							data.push({index:i, data:valueFunction(this.displayResult[i].data)});			    							
			    						} else{	
			    							this.saveLocal(this.displayResult[i].data,i);
			    						}
			    					}						
			    				}
		    					if(!this.isRemoteMode(this.config.save.mode)){
	    							this.saveFinish();
	    						}else if(this.isRemoteMode(this.config.save.mode) && !this.config.save.batch){
	    							this.saveRemote(data);
	    						} else if(this.isRemoteMode(this.config.save.mode) && this.config.save.batch){
	    							this.saveBatchRemote(data);	    							
	    						}		    					
		    				}else{
		    					//console.log("save is not active !");		    				
		    				}
		    			},
		    			
		    			saveBatchRemote : function(values){
		    				var nbElementByBatch = Math.ceil(values.length / 6); //6 because 6 request max in parrallel with firefox and chrome
		    				var queries = [];
							for(var i = 0; i  < 6 && values.length > 0 ; i++){
								queries.push(this.getSaveRemoteRequest(values.splice(0, nbElementByBatch)));	    								
							}
							$q.all(queries).then(function(results){
								angular.forEach(results, function(result, key){
									if(result.status !== 200){
										console.log("Error for batch save");
									}else{
										angular.forEach(result.data, function(value, key){
											this.datatable.saveRemoteOneElement(value.status, value.data, value.index);	    									
	    								}, result.config);
									}
																									
								});
							});		    							
		    			},
		    			
		    			saveRemote : function(queries){
		    				$q.all(queries).then(function(results){
								angular.forEach(results, function(value, key){
									value.config.datatable.saveRemoteOneElement(value.status, value.data, value.config.index);																
								});	    								
							});				
		    			},
		    			
		    			saveRemoteOneElement : function(status, value, index){
		    				if(status !== 200){
								if(this.config.save.changeClass){
									this.displayResult[index].line.trClass = "danger";
		    					}
								this.displayResult[index].line.edit = true;
								this.config.save.error++;
								this.config.save.number--;
								this.saveFinish();
							}else{
								this.saveLocal(value, index);
								this.saveFinish();
							}  				
		    			},
		    			
		    			getSaveRemoteRequest : function(value, i){
		    				var urlFunction = this.getUrlFunction(this.config.save.url);
		    				if(urlFunction){
			    				if(this.config.save.batch){
			    					return $http[this.config.save.method](urlFunction(value), value, {datatable:this});
			    				}else{
			    					var valueFunction = this.getValueFunction(this.config.save.value);
			    					return $http[this.config.save.method](urlFunction(value), valueFunction(value), {datatable:this,index:i});				    				
			    				
			    				}
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
		    						this.displayResult[i].data = data;
		    					}
		    					
		    					//update in the all result table
								var j = i;
								if(this.config.pagination.active && !this.isRemoteMode(this.config.pagination.mode)){
									j = i + (this.config.pagination.pageNumber*this.config.pagination.numberRecordsPerPage);
								}
								this.allResult[j] = angular.copy(this.displayResult[i].data);
		    					
		    					if(!this.config.save.keepEdit){
		    						this.displayResult[i].line.edit = undefined;
		    					}else{
		    						this.displayResult[i].line.edit = true;		    						
		    					}
			    				
								if(this.config.save.changeClass){
									this.displayResult[i].line.trClass = "success";
								}
								this.config.save.number--;
		    				}else{
		    					//console.log("save is not active !");		    				
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
			    					this.config.save.callback(this, this.config.save.error);
			    				}
		    					if(!this.config.save.keepEdit && this.config.save.error === 0){
		    						this.config.edit.start = false;
		    					}
		    					this.config.save.error = 0;
		    					this.config.save.start = false;	
		    					
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
		    					var r= $window.confirm(Messages("datatable.remove.confirm"));
		    					if(r){		    					
			    					var localDisplayResult = angular.copy(this.displayResult);
			    					this.config.remove.counter = 0;
			    					this.config.remove.start = true;
			    					this.config.remove.number = 0;
			    					this.config.remove.error = 0;
			    					for(var i = 0; i < localDisplayResult.length; i++){
				    					if(localDisplayResult[i].line.selected && (!localDisplayResult[i].line.edit || this.config.remove.withEdit)){
				    						this.config.remove.number++;
				    						this.removeLocal(i);			    										    						
				    					}						
				    				}
			    					if(!this.isRemoteMode(this.config.remove.mode)){
			    						this.removeFinish();
			    					}
		    					}
		    				}else{
		    					//console.log("remove is not active !");		    				
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
		    					//console.log("remove is not active !");		    				
		    				}
		    			},
		    			
		    			removeRemote : function(value){
		    				if(this.config.remove.active && this.config.remove.start){
			    				var url = this.getUrlFunction(this.config.remove.url);
				    			if(url){
				    				$http['delete'](url(value), {datatable:this})
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
		    					//console.log("remove is not active !");		    				
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
			    					this.config.remove.callback(this,this.config.remove.error);
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
		    						if(this.displayResult[i].line.selected && (!this.displayResult[i].line.edit || this.config.remove.withEdit))return true;	    						
		    					}
		    				}else{
		    					//console.log("remove is not active !");
		    					return false;
		    				}
		    			},
		    			//select
    					/**
		    			 * Select or unselect all line
		    			 */
		    			selectAll : function(value){
		    				if(this.config.select.active){
			    				this.config.select.isSelectAll = value;
			    				for(var i = 0; i < this.displayResult.length; i++){
			    					if(value){
			    						this.displayResult[i].line.selected=true;
			    						this.displayResult[i].line.trClass="info";
			    					}else{
			    						this.displayResult[i].line.selected=false;
			    						this.displayResult[i].line.trClass=undefined;
			    					}
		    					}
		    				}else{
								//console.log("select is not active");
							}
		    			},	    			
		    			
		    			/**
		    			 * Return all selected element and unselect the data
		    			 */
		    			getSelection : function(unselect){
		    				var selection = [];
		    				for(var i = 0; i < this.displayResult.length; i++){
		    					if(this.displayResult[i].line.selected){
		    						//unselect selection
		    						if(unselect){
		    							this.displayResult[i].line.selected = false;
		    							this.displayResult[i].line.trClass=undefined;
		    						}
		    						selection.push(angular.copy(this.displayResult[i].data));
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
	    						if(this.displayResult[i].line.selected)return true;	    						
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
		    			
		    			//template helper functions		    			
		    			isShowToolbar: function(){
		    				return (this.isShowToolbarButtons() || this.isShowToolbarPagination() || this.isShowToolbarResults());
		    			},
		    			
		    			isShowToolbarButtons: function(){
		    				return ( this.isShowCRUDButtons()
		    						|| this.isShowHideButtons()  || (this.config.show.active && this.config.show.showButton) 
		    						|| this.isShowOtherButtons());
		    			},
		    			isShowCRUDButtons: function(){
		    				return (  (this.config.edit.active && this.config.edit.showButton) 
		    						||  (this.config.save.active && this.config.save.showButton) || (this.config.remove.active && this.config.remove.showButton));
		    			},
		    			isShowHideButtons: function(){
		    				return (this.config.hide.active && this.config.hide.showButton) ;
		    			},
		    			isShowOtherButtons: function(){
		    				return (this.config.otherButtons.active && this.config.otherButtons.template !== undefined) ;
		    			},
		    			isShowToolbarPagination: function(){
		    				return this.config.pagination.active;
		    			},
		    			isShowPagination: function(){
		    				return (this.config.pagination.active && this.config.pagination.pageList.length > 0);
		    			},
		    			isShowToolbarResults: function(){
		    				return this.config.showTotalNumberRecords;
		    			},
		    			
		    			isCompactMode: function(){
		    				return this.config.compact;
		    			},
		    			
		    			isShowButton: function(configParam, column){
		    				if(column){
		    					return (this.config[configParam].active && this.config[configParam].showButton && column[configParam]);
		    				}else{
		    					return (this.config[configParam].active && this.config[configParam].showButton);
		    				}
		    			},
		    			/**
		    			 * Add pagination parameters if needed
		    			 */
		    			getParams : function(params){
		    				if(angular.isUndefined(params)){
	    						params = {};
	    					}
		    				params.datatable = true;
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
		    			getUrlFunction : function(url){
		    				if(angular.isObject(url)){
		    					if(angular.isDefined(url.url)){
		    						return function(value){return url.url};
		    					}
		    				}else if(angular.isString(url)){
		    					return function(value){return url};
		    				} else if(angular.isFunction(url)){
		    					return url;
		    				}
		    				return undefined;
		    			},
		    			/**
		    			 * Return a function to transform value if exist or the default mode
		    			 */
		    			getValueFunction : function(valueFunction){
		    				if(angular.isFunction(valueFunction)){
		    					return valueFunction;
		    				}
		    				return function(value){return value};
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
		    				if(angular.isDefined(columns)){
		    					for(var i = 0 ; i < columns.length; i++){
		    						
		    						if(!columns[i].type || columns[i].type.toLowerCase() === "string"){
		    							columns[i].type = "text";
		    						}else{
		    							columns[i].type = columns[i].type.toLowerCase();
		    						}
		    						
		    						if(columns[i].type === "img" || columns[i].type === "image"){
		    							if(!columns[i].format)console.log("missing format for "+columns[i].property);
		    							if(!columns[i].width)columns[i].width='100%';
		    						}
		    						
			    					if(columns[i].id == null){
			    						columns[i].id = this.generateColumnId();
			    					}
			    					if(columns[i].hide && !this.config.hide.active){
			    						columns[i].hide = false;
			    					}
			    					if(columns[i].order && !this.config.order.active){
			    						columns[i].order = false;
			    					}
			    					if(columns[i].edit && !this.config.edit.active){
			    						columns[i].edit = false;
			    					}
			    					
			    					if(columns[i].choiceInList && !angular.isDefined(columns[i].listStyle)){
			    						columns[i].listStyle = "select";
			    					}
			    					
			    					if(columns[i].choiceInList && !angular.isDefined(columns[i].possibleValues)){
			    						columns[i].possibleValues = [];
			    					}
			    					
			    					columns[i].cells = [];//Init
			    				}
		    					
		    					var settings = $.extend(true, [], this.configColumnDefault, columns);
			    	    		this.config.columns = angular.copy(settings);
			    	    		this.configMaster.columns = angular.copy(settings);
			    	    		this.newExtraHeaderConfig();
		    			    }
		    			},
		    			setColumnsConfigWithUrl : function(){
		    				$http.get(this.config.columnsUrl,{datatable:this}).success(function(data, status, headers, config) {		    						
	    						config.datatable.setColumnsConfig(data);
	    					});
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
		    	    		if(this.config.columnsUrl){
		    					this.setColumnsConfigWithUrl();
		    				}else{
		    					this.setColumnsConfig(this.config.columns);
		    				}
		    	    		
		    	    		if(this.displayResult && this.displayResult.length > 0){
		    	    			this.computeDisplayResult();
		    	    			this.computePaginationList();
		    	    		}
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
		    			 * Return column with edit
		    			 */
		    			getEditColumns: function(){
		    				var c = [];
		    				for(var i = 0 ; i < this.config.columns.length; i++){
		    					if(this.config.columns[i].edit)c.push(this.config.columns[i]);
		    				}
		    				return c;
		    			},
		    			generateColumnId : function(){
		    				this.inc++;
		    				return "p"+this.inc;
		    			},
		    			newColumn : function(header,property,edit, hide,order,type,choiceInList,possibleValues,extraHeaders){
		    				var column = {};
		    				column.id = this.generateColumnId();
		    				column.header = header;
		    				column.property = property;
		    				column.edit = edit;
		    				column.hide = hide;
		    				column.order = order;
		    				column.type = type;
		    				column.choiceInList = choiceInList;
		    				if(possibleValues!=undefined){
		    					column.possibleValues = possibleValues;
		    				}
		    				
		    				if(extraHeaders!=undefined){
		    					column.extraHeaders = extraHeaders;
		    				}
		    				
		    				return column;
		    			},
		    			/**
		    			 * Add a new column to the table with the <th>title</th>
		    			 * at position
		    			 */
		    			addColumn : function(position, column){
		    				if(position>=0){
		    					this.config.columns.splice(position,0,column);
		    				}else{
		    					this.config.columns.push(column);
		    				}
		    				this.newExtraHeaderConfig();
		    			},
		    			/**
		    			 * Remove a column at position
		    			 */
		    			deleteColumn : function(position){
		    				this.config.columns.splice(position, 1);
		    				this.newExtraHeaderConfig();
		    			},
		    			addToExtraHeaderConfig:function(pos,header){
		    				if(!angular.isDefined(this.config.extraHeaders.list[pos])){
								this.config.extraHeaders.list[pos] = [];
							}
							this.config.extraHeaders.list[pos].push(header);
		    			},
		    			getExtraHeaderConfig : function(){
		    				return this.config.extraHeaders.list;
		    			},
		    			newExtraHeaderConfig : function(){
		    				if(this.config.extraHeaders.dynamic === true){
			    				this.config.extraHeaders.list = {};
			    				var lineUsed = false; // If we don't have label in a line, we don't want to show the line
			    				var count = 0;//Number of undefined extraHeader column beetween two defined ones
			    				//Every level of header
			    				for(var i=0;i<this.config.extraHeaders.number;i++){
			    					lineUsed = false;//re-init because new line
			    					var header = undefined;
			    					//Every column
				    				for(var j=0;j<this.config.columns.length;j++){
				    					if(!this.isHide(this.config.columns[j].id)){
				    					//if the column have a extra header for this level
				    						if(this.config.columns[j].extraHeaders != undefined && this.config.columns[j].extraHeaders[i] != undefined ){
				    							lineUsed = true;
				    							if(count>0){
				    								//adding the empty header of undefined extraHeader columns
				    								this.addToExtraHeaderConfig(i,{"label":"","colspan":count});
				    								count = 0;//Reset the count to 0
				    							}
				    							//The first time the header will be undefined
				    							if(header == undefined){	
				    								//create the new header with colspan 0 (the current column will be counted)
							    					header =  {"label":this.config.columns[j].extraHeaders[i],"colspan":0};
							    				}
				    							
				    							//if two near columns have the same header
				    							if(this.config.columns[j].extraHeaders[i] == header.label){
				    								header.colspan += 1;
				    							}else{
				    								//We have a new header
				    								//adding the current one
				    								this.addToExtraHeaderConfig(i, header);
				    								//and create the new one with colspan 1
				    								//colspan = 1 because we're already on the first column who have this header
				    								header =  {"label":this.config.columns[j].extraHeaders[i],"colspan":1};
				    							}
				    						
				    						}else if(header != undefined){
				    							lineUsed = true;
				    							//If we find a undefined column, we add the old header
				    							this.addToExtraHeaderConfig(i, header);
				    							//and increment the count var
				    							count++;
				    							//The old header is added
			    								header =  undefined;	
				    						}else{
				    							//No header to add, the previous one was a undefined column
				    							//increment the count var
				    							count++;
				    						}
				    					}
				    				}
				    				
				    				//At the end of the level loop
				    				//If we have undefined column left
				    				//And the line have at least one item
				    				if(count>0 && (lineUsed === true || header != undefined)){
				    					this.addToExtraHeaderConfig(i,{"label":"","colspan":count});
				    					count = 0;
				    				}
				    				
				    				//If we have defined column left
				    				if(header != undefined){
		    							this.addToExtraHeaderConfig(i, header);	
				    				}
			    				}
		    				}
		    			},
		    			getNgModel : function(col, header){
		    				if(header){
        			    		return  "dtTable.config.edit.columns."+col.id+".value";
        			    	}else if(angular.isFunction(col.property)){
        			    		return "dtTable.config.columns[$index].property(value.data)";
        			    	}else{
        			    		return "value.data."+col.property;        			    		
        			    	}		    				
				    	},
	  		    		getFormatter : function(col){
		    				var format = "";
		    				if(col.type === "date"){
		    					format += " | date:'"+(col.format?col.format:Messages("date.format"))+"'";
		    				}else if(col.type === "datetime"){
		    					format += " | date:'"+(col.format?col.format:Messages("datetime.format"))+"'";
		    				}else if(col.type === "number"){
								format += " | number"+(col.format?':'+col.format:'');
							}	    				
		    				return format;
		    			},
		    			
		    			getFilter : function(col){
		    				if(col.filter){
		    					return '|'+col.filter;
		    				}
		    				return '';
		    			},
		    			
		    			getValueElement : function(col){  
	    					if(angular.isDefined(col.render) && col.render !== null){
	    						if(angular.isFunction(col.render)){
	    							return '<span dt-compile="dtTable.config.columns[$index].render(value.data, value.line)"></span>';
	    						}else if(angular.isString(col.render)){
	    							return '<span dt-compile="dtTable.config.columns[$index].render"></span>';
	    						}
		    				}else{
		    					if(col.type === "boolean"){
		    						return '<div ng-switch on="'+this.getNgModel(col)+'"><i ng-switch-when="true" class="fa fa-check-square-o"></i><i ng-switch-default class="fa fa-square-o"></i></div>';	    						
		    					}else if(col.type === "img" || col.type === "image"){
		    						if(!col.format)console.log("missing format for img !!");
		    						return '<img ng-src="data:image/'+col.format+';base64,{{'+this.getNgModel(col)+'}}" style="max-width:{{col.width}}"/>';		    					    
		    					} else{
		    						return '<span ng-bind="'+this.getNgModel(col)+this.getFilter(col)+this.getFormatter(col)+'"></span>';
		    					}
		    				}	  					    				
		    			},
		    			
		    			getOptions : function(col){
		    				if(angular.isString(col.possibleValues)){
		    					return col.possibleValues;
		    				}else{ //function
		    					return 'col.possibleValues';
		    				}
		    			},
		    			
		    			getGroupBy : function(col){
		    				if(angular.isString(col.groupBy)){
		    					return 'group by opt.'+col.groupBy;
		    				}else{
		    					return '';
		    				}
		    					
		    			},
		    			
		    			getHeader : function(value){
		    				return Messages(value);
		    			},
		    			
		    			getEditElement : function(col, header){
		    				var editElement = '';
		    				var ngChange = '"';
	    			    	if(header){
	    			    		ngChange = '" ng-change="dtTable.updateColumn(col.property, col.id)"';	    			    		
	    			    	}
		    						    				
		    				if(col.type === "boolean"){
		    					editElement = '<input class="form-control" dt-html-filter="{{col.type}}" type="checkbox" class="input-small" ng-model="'+this.getNgModel(col, header)+ngChange+'/>';
		    				}else if(!col.choiceInList){
		    					editElement = '<input class="form-control" dt-html-filter="{{col.type}}" type="'+col.type+'" class="input-small" ng-model="'+this.getNgModel(col, header)+ngChange+'/>';
		    				}else if(col.choiceInList){
		    					switch (col.listStyle) { 
		    						case "radio":
		    							editElement = '<label ng-repeat="opt in col.possibleValues"  for="radio{{col.id}}"><input id="radio{{col.id}}" dt-html-filter="{{col.type}}" type="radio" ng-model="'+this.getNgModel(col,hearder)+ngChange+' value="{{opt.name}}">{{opt.name}}<br></label>';
		    							break;		    						
		    						case "multiselect":
		    							editElement = '<select class="form-control" multiple="true" ng-options="opt.code as opt.name '+this.getGroupBy(col)+' for opt in '+this.getOptions(col)+this.getFormatter(col)+'" ng-model="'+this.getNgModel(col,header)+ngChange+'></select>';
			    						break;
		    						case "bt-select":
		    							editElement = '<div class="form-control" bt-select placeholder="" bt-dropdown-class="dropdown-menu-right" bt-options="opt.code as opt.name  '+this.getGroupBy(col)+' for opt in '+this.getOptions(col)+this.getFormatter(col)+'" ng-model="'+this.getNgModel(col,header)+ngChange+'></div>';			        		  	    	
		    							break;
		    						case "bt-select-multiple":
		    							editElement = '<div class="form-control" bt-select multiple="true" bt-dropdown-class="dropdown-menu-right" placeholder="" bt-options="opt.code as opt.name  '+this.getGroupBy(col)+' for opt in '+this.getOptions(col)+this.getFormatter(col)+'" ng-model="'+this.getNgModel(col,header)+ngChange+'></div>';			        		  	    	
		    							break;
		    						default:
		    							editElement = '<select class="form-control" ng-options="opt.code as opt.name '+this.getGroupBy(col)+' for opt in '+this.getOptions(col)+this.getFormatter(col)+'" ng-model="'+this.getNgModel(col,header)+ngChange+'></select>';
			    						break;
    		  	    			}		    					
		    				}else{
		    					editElement = "Edit Not Defined for col.type !";
		    				}		    						    				
		    				return '<div class="form-group">'+editElement+'</div>';
		    			}
    			};
				
				datatable.setConfig(iConfig);
    			
				return datatable;
    		}
    		return constructor;
    	}]).directive('datatable',function($parse){
    		return {
  		    	restrict: 'A',
  		    	replace:true,
  		    	//scope:{
  		    	//	dtTable:'=datatable'
  		    	//},
  		    	scope:true,
  		    	transclude:true,
  		    	template:'<div name="datatable" class="datatable">'
  		    		+'<div ng-transclude/>'
  		    		+'<div dt-toolbar ng-if="dtTable.isShowToolbar()"/>'  		    		
  		    		+'<div dt-messages ng-if="dtTable.config.messages.active"/>'
  		    		+'<div dt-table/>'
  		    		+'</div>',
  		    	link: function(scope, element, attr) {
  		    		if(!attr.datatable) return;
  		    		
  		    		scope.$watch(attr.datatable, function(newValue, oldValue) {
  		    			if(newValue && (newValue !== oldValue || !scope.dtTable)){
  		    				//console.log("new datatable")
  		    				scope.dtTable = $parse(attr.datatable)(scope);
  		    			}
		            });
  		    		
  		    		scope.dtTable = $parse(attr.datatable)(scope);
  		    		scope.messagesDatatable = function(message,arg){
			    		if(typeof Messages == 'function'){
			    			if(arg==null || arg==undefined){
			    				return Messages(message);
			    			}else{
			    				return Messages(message,arg);
			    			}
			    		}
			    		
			    		return message;
			    	};
       		    } 		    		
    		};
    	}).directive('dtForm', function(){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	transclude:true,
  		    	template:'<div name="dt-form"  class="row"><div class="col-md-12 col-lg-12" ng-transclude/></div>',
  		    	link: function(scope, element, attr) {
  		    		//console.log("dtForm");
  		    	}
    		};
    	}).directive('dtToolbar', function(){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	//transclude:true,
  		    	template:'<div name="dt-toolbar" class="row margin-bottom-3"><div class="col-md-12 col-lg-12">'
  		    		+'<div class="btn-toolbar pull-left" name="dt-toolbar-buttons" ng-if="dtTable.isShowToolbarButtons()">'
  		    		+'<div class="btn-group"  ng-switch on="dtTable.config.select.isSelectAll">'
  		    		+	'<button class="btn btn-default" ng-click="dtTable.selectAll(true)" ng-show="dtTable.isShowButton(\'select\')" ng-switch-when="false" data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.selectall\')}}">'
  		    		+		'<i class="fa fa-check-square"></i>'
  		    		+		'<span ng-if="!dtTable.isCompactMode()"> {{messagesDatatable(\'datatable.button.selectall\')}}</span>'
  		    		+	'</button>'
  		    		+	'<button class="btn btn-default" ng-click="dtTable.selectAll(false)" ng-show="dtTable.isShowButton(\'select\')" ng-switch-when="true" data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.unselectall\')}}">'
  		    		+		'<i class="fa fa-square"></i>'
    				+		'<span ng-if="!dtTable.isCompactMode()"> {{messagesDatatable(\'datatable.button.unselectall\')}}</span>'
  		    		+	'</button>'
  		    		+	'<button class="btn btn-default" ng-click="dtTable.cancel()"  ng-if="dtTable.isShowButton(\'cancel\')" data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.cancel\')}}">'
  		    		+		'<i class="fa fa-undo"></i>'
  		    		+		'<span ng-if="!dtTable.isCompactMode()"> {{messagesDatatable(\'datatable.button.cancel\')}}</span>'
  		    		+	'</button>'
  		    		+	'<button class="btn btn-default" ng-click="dtTable.show()" ng-disabled="!dtTable.isSelect()" ng-if="dtTable.isShowButton(\'show\')" data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.show\')}}">'
  		    		+		'<i class="fa fa-thumb-tack"></i>'
  		    		+		'<span ng-if="!dtTable.isCompactMode()"> {{messagesDatatable(\'datatable.button.show\')}}</span>'
  		    		+	'</button>'  		    		
  		    		+'</div>'
  		    		+'<div class="btn-group" ng-if="dtTable.isShowCRUDButtons()">'
  		    		+	'<button class="btn btn-default" ng-click="dtTable.setEdit()" ng-disabled="!dtTable.canEdit()"  ng-if="dtTable.isShowButton(\'edit\')" data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.edit\')}}">'
  		    		+		'<i class="fa fa-edit"></i>'
  		    		+		'<span ng-if="!dtTable.isCompactMode()"> {{messagesDatatable(\'datatable.button.edit\')}}</span>'
  		    		+	'</button>'	
  		    		+	'<button class="btn btn-default" ng-click="dtTable.save()" ng-disabled="!dtTable.canSave()" ng-if="dtTable.isShowButton(\'save\')"  data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.save\')}}" >'
  		    		+		'<i class="fa fa-save"></i>'
  		    		+		'<span ng-if="!dtTable.isCompactMode()"> {{messagesDatatable(\'datatable.button.save\')}}</span>'
  		    		+	'</button>'	
  		    		+	'<button class="btn btn-default" ng-click="dtTable.remove()" ng-disabled="!dtTable.canRemove()" ng-if="dtTable.isShowButton(\'remove\')"  data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.remove\')}}">'
  		    		+		'<i class="fa fa-trash-o"></i>'
  		    		+		'<span ng-if="!dtTable.isCompactMode()"> {{messagesDatatable(\'datatable.button.remove\')}}</span>'
  		    		+	'</button>'
  		    		+'</div>'
  		    		+'<div class="btn-group" ng-if="dtTable.isShowHideButtons()">' //todo bt-select
  		    		+	'<button data-toggle="dropdown" class="btn btn-default dropdown-toggle" data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.hide\')}}">'
  		    		+		'<i class="fa fa-eye-slash fa-lg"></i> '
  		    		+		'<span ng-if="!dtTable.isCompactMode()"> {{messagesDatatable(\'datatable.button.hide\')}} </span>'
  		    		+		'<span class="caret"></span>'  		    		
  		    		+	'</button>'
  		    		+	'<ul class="dropdown-menu">'
  		    		+		'<li ng-repeat="column in dtTable.getHideColumns()">'
  		    		+		'<a href="#" ng-click="dtTable.setHideColumn(column.id)" ng-switch on="dtTable.isHide(column.id)"><i class="fa fa-eye" ng-switch-when="true"></i><i class="fa fa-eye-slash" ng-switch-when="false"></i> {{column.header}}</a>'
  		    		+		'</li>'
  		    		+	'</ul>'
  		    		+'</div>'  		    		
  		    		+'<div class="btn-group" ng-if="dtTable.isShowOtherButtons()" dt-compile="dtTable.config.otherButtons.template"></div>'
  		    		+'</div>'
  		    		+'<div class="btn-toolbar pull-right" name="dt-toolbar-results"  ng-if="dtTable.isShowToolbarResults()">'
  		    		+	'<button class="btn btn-info" disabled="disabled" ng-show="dtTable.config.showTotalNumberRecords">{{messagesDatatable(\'datatable.totalNumberRecords\', dtTable.totalNumberRecords)}}</button>'
  		    		+'</div>'
  		    		+'<div class="btn-toolbar pull-right" name="dt-toolbar-pagination"  ng-if="dtTable.isShowToolbarPagination()">'
  		    		+	'<div class="btn-group" ng-if="dtTable.isShowPagination()">'
  		    		+		'<ul class="pagination"><li ng-repeat="page in dtTable.config.pagination.pageList" ng-class="page.clazz"><a href="#" ng-click="dtTable.setPageNumber(page)" ng-bind="page.label"></a></li></ul>'
  		    		+	'</div>'
  		    		+	'<div class="btn-group">'
  		    		+		'<button data-toggle="dropdown" class="btn btn-default dropdown-toggle">'
  		    		+		'{{messagesDatatable(\'datatable.button.length\', dtTable.config.pagination.numberRecordsPerPage)}} <span class="caret"></span>'
  		    		+		'</button>'
  		    		+		'<ul class="dropdown-menu">'
  		    		+			'<li ng-repeat="elt in dtTable.config.pagination.numberRecordsPerPageList" class={{elt.clazz}}>'
  		    		+				'<a href="#" ng-click="dtTable.setNumberRecordsPerPage(elt)">{{elt.number}}</a>' 
  		    		+			'</li>'
  		    		+		'</ul>'
  		    		+	'</div>'
  		    		+'</div>'  		    		  		    	
  		    		+'</div></div>'  		    		
  		    		,
  		    	link: function(scope, element, attr) {
  		    		//console.log("dtToolbar");
  		    	}
    		};
    	}).directive('dtMessages', function(){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	template:
  		    		'<div name="dt-messages" class="row"><div class="col-md-12 col-lg-12">'
  		    		+'<div ng-class="dtTable.config.messages.clazz" ng-show="dtTable.config.messages.text !== undefined"><strong>{{dtTable.config.messages.text}}</strong>'
  		    		+'</div>'
  		    		+'</div></div>'
  		    		,
  		    	link: function(scope, element, attr) {
  		    		//console.log("dtMessages");
  		    	}
    		};
    	}).directive('dtTable', function(){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	template:
  		    		'<div name="dt-table" class="row"><div class="col-md-12 col-lg-12">'
  		    		+'<div class="inProgress" ng-if="dtTable.config.save.start"><button class="btn btn-primary btn-lg"><i class="fa fa-spinner fa-spin fa-5x"></i></button></div>'
  		    		+'<form class="form-inline">'
  		    		+'<table class="table table-condensed table-hover table-bordered">'
  		    		+'<thead>'
  		    		+'<tr ng-repeat="(key,headers) in dtTable.getExtraHeaderConfig()">'
  		    		+	'<th colspan="{{header.colspan}}" ng-repeat="header in headers">{{header.label}}</th>'
  		    		+'</tr>'
  		    		+'<tr>'
  		    		+	'<th id="{{column.id}}" ng-repeat="column in dtTable.getColumnsConfig()" ng-if="!dtTable.isHide(column.id)">'
  		    		+	'<span ng-bind="dtTable.getHeader(column.header)"/>'
  		    		+	'<div class="btn-group pull-right">'
  		    		+	'<button class="btn btn-xs" ng-click="dtTable.setEdit(column.id)" ng-if="dtTable.isShowButton(\'edit\', column)" ng-disabled="!dtTable.canEdit()" data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.edit\')"><i class="fa fa-edit"></i></button>'
  		    		+	'<button class="btn btn-xs" ng-click="dtTable.setOrderColumn(column.property, column.id)" ng-if="dtTable.isShowButton(\'order\', column)" ng-disabled="!dtTable.canOrder()" data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.sort\')"><i ng-class="dtTable.getOrderColumnClass(column.id)"></i></button>'
  		    		+	'<button class="btn btn-xs" ng-click="dtTable.setHideColumn(column.id)" ng-if="dtTable.isShowButton(\'hide\', column)" data-toggle="tooltip" title="{{messagesDatatable(\'datatable.button.hide\')}}"><i class="fa fa-eye-slash"></i></button>'
  		    		+	'</div>'
  		    		+	'</th>'
  		    		+'</tr>'
  		    		+'</thead>'
  		    		+'<tbody>'
  		    		+	'<tr ng-if="dtTable.isEdit()">'
  		    		+		'<td ng-repeat="col in dtTable.config.columns" ng-if="!dtTable.isHide(col.id)">'
  		    		+			'<div dt-cell-header/>'
  		    		+		'</td>'
  		    		+	'</tr>'
  		    		+	'<tr ng-repeat="value in dtTable.displayResult | orderBy:dtTable.config.orderBy:dtTable.config.orderReverse" ng-click="select(value.line)" ng-class="getTrClass(value.data, value.line, this)">'
  		    		+		'<td ng-repeat="col in dtTable.config.columns" rowspan="{{col.cells[$parent.$index].rowSpan}}" ng-hide="dtTable.isHide(col.id)" ng-class="getTdClass(value.data, col, this)">'
  		    		+		'<div dt-cell/>'
  		    		+		'</td>'
  		    		+	'</tr>'
  		    		+'</tbody>'
  		    		+'</table>'
  		    		+'</form>'
  		    		+'</div></div>',
  		    	link: function(scope, element, attr) {
  		    		scope.getTrClass = function(data, line){
  		    			var dtTable = this.dtTable;
	    				if(line.trClass){
	    					return line.trClass; 
	    				}else if(angular.isFunction(dtTable.config.lines.trClass)){
	    					return dtTable.config.lines.trClass(data, line);
	    				} else if(angular.isString(dtTable.config.lines.trClass)){
	    					return this.$eval(dtTable.config.lines.trClass) || dtTable.config.lines.trClass;
	    				}else{
	    					return '';
	    				}		    				
	    			};
	    			scope.getTdClass = function(data, col){
	    				if(angular.isFunction(col.tdClass)){
	    					return col.tdClass(data, col.property, $parse);
	    				} else if(angular.isString(col.tdClass)){
	    					//we try to evaluation the string against the scope
	    					return this.$eval(col.tdClass) || col.tdClass;
	    				}else{
	    					return '';
	    				}
	    			};
	    			/**
					 * Select all the table line or just one
					 */
					scope.select = function(line){
						var dtTable = this.dtTable;
						if(dtTable.config.select.active){
		    				if(line){
		    					if(!line.selected){
		    						line.selected=true;
		    						line.trClass="info";
		    					}
								else{
									line.selected=false;
		    						line.trClass=undefined;
								}
		    				}
						}else{
							//console.log("select is not active");
						}
	    			};
  		    	}
    		};
    	}).directive("dtCell", function(){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	template:	
  		    		'<div>'
	  		    		+'<div ng-if="col.edit" dt-editable-cell></div>'
	  		    		+'<div ng-if="!col.edit" dt-cell-read></div>'		    		
  		    		+'</div>',
	    		link: function(scope, element, attr) {
  		    		//console.log("dtCell");  		    		
  		    	}
    		};
    	}).directive("dtEditableCell", function(){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	template:	
  		    		'<div ng-switch on="dtTable.isEdit(col.id, value.line)">'
	  		    		+'<div ng-switch-when="true" >'
	  		    		+	'<div dt-cell-edit></div>'  		    		
	  		    		+'</div>'
	  		    		+'<div ng-switch-default dt-cell-read></div>'
  		    		+'</div>',
	    		link: function(scope, element, attr) {
  		    		//console.log("dtEditableCell");  		    		
  		    	}
    		};
    	}).directive("dtCellHeader", function(){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	template:	
  		    		'<div ng-if="col.edit" ng-switch on="dtTable.isEdit(col.id)">'  		    			
  		    		+	'<div ng-switch-when="true" dt-compile="dtTable.getEditElement(col, true)"></div><div ng-switch-default></div>'
  		    		+'</div>',
  		    	link: function(scope, element, attr) {
  	  		    	//console.log("dtCellHeader");  		    		
  	  		    }
    		};
    	}).directive("dtCellEdit", function($compile){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	template:'<div dt-compile="dtTable.getEditElement(col)"></div>', 
  		    	link: function(scope, element, attr) {
  		    		//console.log("dtCellEdit")  		    		
  		    	}
  		    	
    		};
    	}).directive("dtCellRead", function($compile){
    		return {
    			restrict: 'A',
  		    	replace:true,
  		    	template:'<div dt-compile="dtTable.getValueElement(col)"></div>'  ,
  		    	link: function(scope, element, attr) {
  		    		//console.log("dtCellRead");
  		    		//element.append($compile(scope.dtTable.getValueElement(scope.col))(scope));  		    		
  		    	}
    		};
    	}).directive('dtCompile', function($compile) {
			// directive factory creates a link function
			return {
				restrict: 'A',
  		    	link: function(scope, element, attrs) {
  					//console.log("dtCompile");
  				    scope.$watch(
  				        function(scope) {
  				             // watch the 'compile' expression for changes
  				            return scope.$eval(attrs.dtCompile);
  				        },
  				        function(value) {
  				            // when the 'compile' expression changes
  				            // assign it into the current DOM
  				            element.html(value);

  				            // compile the new DOM and link it to the current
  				            // scope.
  				            // NOTE: we only compile .childNodes so that
  				            // we don't get into infinite loop compiling ourselves
  				            $compile(element.contents())(scope);
  				        }
  				    );
  				}
			};
						
		}).directive("dtHtmlFilter", function($filter) {
				return {
					  require: 'ngModel',
					  link: function(scope, element, attrs, ngModelController) {
						  //console.log("htmlFilter");
						  ngModelController.$parsers.push(function(data) {
					      //view to model / same algo than model to view ?????
						   var convertedData = data;
					    	
					    	   if(attrs.dtHtmlFilter == "datetime"){
					    		   convertedData = $filter('date')(convertedData, Messages("datetime.format"));
					    	   }else if(attrs.dtHtmlFilter == "date"){
					    		   convertedData = $filter('date')(convertedData, Messages("date.format"));
					    	   }else if(attrs.dtHtmlFilter == "number"){
					    		   convertedData = $filter('number')(convertedData);
					    	   }
					    	
					    	   return convertedData;
					   	});

					    ngModelController.$formatters.push(function(data) {
					      //model to view / same algo than view to model ?????
					    	var convertedData = data;
					    	
					    	  if(attrs.dtHtmlFilter == "datetime"){
					    			convertedData = $filter('date')(convertedData, Messages("datetime.format"));
					    	   }else if(attrs.dtHtmlFilter == "date"){
					    		   	convertedData = $filter('date')(convertedData, Messages("date.format"));
					    	   }else if(attrs.dtHtmlFilter == "number"){
					    		   	convertedData = $filter('number')(convertedData);
					    	   }
					    	
					    	return convertedData;
					    });   
					  }
					}
			});

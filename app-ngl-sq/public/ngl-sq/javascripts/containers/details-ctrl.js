"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$q', '$routeParams', '$filter','$window', 'mainService', 'tabService', 'lists', 'messages', 
                                          function($scope,$http,$q,$routeParams,$filter,$window,mainService,tabService,lists,messages){
	
	$scope.getTabClass = function(value){
		if(value === mainService.get('containerActiveTab')){
			return 'active';
		}
	};
	$scope.setActiveTab = function(value){
		mainService.put('containerActiveTab', value)
	};

	
	var init = function(){
		$scope.messages = messages();
		$scope.lists = lists;
		mainService.stopEditMode();
			
		$http.get(jsRoutes.controllers.containers.api.Containers.get($routeParams.code).url).then(function(response) {
			$scope.container = response.data;
			
			if(tabService.getTabs().length == 0){			
				tabService.addTabs({label:Messages('containers.tabs.search'),href:jsRoutes.controllers.containers.tpl.Containers.home("search").url,remove:true});
				tabService.addTabs({label:$scope.container.code,href:jsRoutes.controllers.containers.tpl.Containers.get($scope.container.code).url,remove:true});
				tabService.activeTab($scope.getTabs(1));
			}
			
			$scope.lists.refresh.containerSupportCategories();
			$scope.lists.refresh.experimentTypes({categoryCodes:["transformation"], withoutOneToVoid:false});
					
			if(undefined === mainService.get('containerActiveTab')){
				mainService.put('containerActiveTab', 'general');
			}else if('treeoflife' ===  mainService.get('containerActiveTab')){
				$scope.initGraph();
			}
		});
	}
	init();
	
	var containerNodes = undefined;
	$scope.initGraph = function(){
		$scope.setActiveTab('treeoflife');
		if(!containerNodes){
			initTreeOfLife($scope.container);
		}
	}
	
	var initCytoscape = function(graphElements){
		var asynchGraph = function() {
			 return $q(function(resolve, reject) {
				 setTimeout(function() {
				 	 var cy = 
						cytoscape({
					          container: document.getElementById('graph'),
					          boxSelectionEnabled: false,
					          autounselectify: true,
					          
					          layout: {
					            name: 'breadthfirst',
					            directed:true,
					            padding:5,
					            spacingFactor:0.5,					           
					          },
					          style: cytoscape.stylesheet()
						          .selector('node')
						            .css({
						              'shape': 'data(faveShape)',
						              'width': '75',
						              'label': 'data(label)',
						              'text-valign': 'center',
						              //'text-outline-width': 2,
						              //'text-outline-color': 'data(faveColor)',
						              'background-color': 'data(faveColor)',
						              'color': '#fff',
						              'font-size':11,  
						            })
						          .selector(':selected')
						            .css({
						              'border-width': 3,
						              'border-color': '#333'
						            })
						          .selector('edge')
						            .css({
						              'opacity': 0.666,
						              'width': '3',
						              'label': 'data(label)',
						              'color': '#000',
						              'font-size':11,
						              'font-weight': 'bold',
						              'target-arrow-shape': 'triangle',
						              'source-arrow-shape': 'circle',
						              'line-color': 'data(faveColor)',
						              'source-arrow-color': 'data(faveColor)',
						              'target-arrow-color': 'data(faveColor)'
						            })
						            /*
						          .selector('edge.questionable')
						            .css({
						              'line-style': 'dotted',
						              'target-arrow-shape': 'diamond'
						            })
						            */
						          .selector('.faded')
						            .css({
						              'opacity': 0.25,
						              'text-opacity': 0
						            })
						            ,
						           
					        
					          elements : graphElements
					      
					        });
				 	cy.on('click', 'node', function(evt){
				 		var data = this.data(); 
				 		$scope.$apply(function(scope){
				 			 tabService.addTabs({label:data.code,href:jsRoutes.controllers.containers.tpl.Containers.get(data.code).url, remove:true});						 		
				 		 });
				 	});
				 	cy.on('click', 'edge', function(evt){
				 		var data = this.data(); 
				 		$scope.$apply(function(scope){
				 			$window.open(jsRoutes.controllers.experiments.tpl.Experiments.get(data.fromExperimentCode).url, 'experiments');
				 		});
				 	});
				});	
			 }, 1);
		};
		asynchGraph();
	}
	
	
	var computeGraphElements = function(containerNodes){
		//nodes
		var graphElements = [];
		containerNodes = $filter('orderBy')(containerNodes,'indexFromCurrent');
		for(var key in containerNodes){
			var currentNode = containerNodes[key];
			var currentContainer = containerNodes[key].container;
			currentContainer.id = currentContainer.code;
			currentContainer.label = currentContainer.code;
			var faveColor = '#6FB1FC';
			if(currentNode.indexFromCurrent < 0){
				faveColor = '#F5A45D'
			}else if(currentNode.indexFromCurrent > 0){
				faveColor = '#86B342';
			}
			
			currentContainer.faveColor = faveColor;
			currentContainer.faveShape="ellipse";
			if(currentContainer.contents.length > 1){
				currentContainer.faveShape="octagon";
			}
			
			graphElements.push({"data":currentContainer,"group":"nodes"});
			
		}
		
		//edges
		
		for(var key in containerNodes){
			
			var currentNode = containerNodes[key];
			var currentContainer = containerNodes[key].container;
			angular.forEach(currentNode.childNodes, function(childNode){
				var childContainer = childNode.container;
				var currentContainer = this.container;
				var edge = {
						"id":currentContainer.code+"-"+childContainer.code,
						"source":currentContainer.code,
						"target":childContainer.code
						
				}
				
				if(childContainer.treeOfLife && childContainer.treeOfLife.from){
					edge.label=$filter('codes')(childContainer.treeOfLife.from.experimentTypeCode,'type');
					edge.fromExperimentCode = childContainer.treeOfLife.from.experimentCode;
				}
				
				var faveColor = '#6FB1FC';
				if(this.indexFromCurrent < 0){
					faveColor = '#F5A45D'
				}else if(this.indexFromCurrent > 0){
					faveColor = '#86B342';
				}
				edge.faveColor=faveColor;
				graphElements.push({"data":edge,"group":"edges"})	
			},currentNode)
			
		}
		
		return graphElements;
	};
	
	
	
	
	var initTreeOfLife = function(currentContainer){
		//extract parent container codes
		var codes = {parentContainerCodes : []};	
		if(!angular.isUndefined(currentContainer.treeOfLife) && (currentContainer.treeOfLife !== null)){
			angular.forEach(currentContainer.treeOfLife.paths, function(path){
				path = path.substring(1);
				this.parentContainerCodes = this.parentContainerCodes.concat(path.split(","));
			}, codes);
		}

		var promises = [];
		if(codes.parentContainerCodes.length > 0){ // Case no paths
			promises.push($http.get(jsRoutes.controllers.containers.api.Containers.list().url, {params : {codes:codes.parentContainerCodes}}));
		}
		promises.push($http.get(jsRoutes.controllers.containers.api.Containers.list().url, {params : {treeOfLifePathRegex:currentContainer.code}}));
		
		
		$q.all(promises).then(function(results){
			containerNodes = {};
			var newNode = function(container){
				return  {container:container, parentNodes:[], childNodes:[],indexFromCurrent:undefined};
			};
			
			containerNodes[$scope.container.code] = newNode($scope.container);


			angular.forEach(results, function(result){
				angular.forEach(result.data, function(container){
					this[container.code] = newNode(container);
				}, this)
			}, containerNodes)

			
			var updateParentNodes = function(currentContainerNode, containerNodes){
				//only if parents
				if(currentContainerNode.parentNodes.length === 0){
					
					if(currentContainerNode.container.treeOfLife){
						var parentContainers = currentContainerNode.container.treeOfLife.from.containers;
						
						angular.forEach(parentContainers, function(parentContainer){
						
							if(containerNodes[parentContainer.code]){
								var parentContainerNode = containerNodes[parentContainer.code];
								this.parentNodes.push(parentContainerNode);
								updateParentNodes(parentContainerNode, containerNodes);								
							}else{
								//when display a branch of a pool
								//throw 'error not found node for '+parentContainer.code;
							}
							
						}, currentContainerNode)
					}
				}			
			};
						
			for(var key in containerNodes){
				updateParentNodes(containerNodes[key], containerNodes);							
			}
			
			//update child
			for(var key in containerNodes){
				var currentNode = containerNodes[key];
				angular.forEach(currentNode.parentNodes, function(parentNode){
					parentNode.childNodes.push(this);
				},currentNode)										
			}
			
			
			var updateIndexForParents = function(parentNodes, childIndex){
				angular.forEach(parentNodes, function(parentNode){
					parentNode.indexFromCurrent = this - 1;
					updateIndexForParents(parentNode.parentNodes, parentNode.indexFromCurrent);
				}, childIndex);
			};
			
			var updateIndexForChildren = function(childNodes, parentIndex){
				angular.forEach(childNodes, function(childNode){
					childNode.indexFromCurrent = this + 1 ;
					updateIndexForChildren(childNode.childNodes, childNode.indexFromCurrent);
				}, parentIndex);
			};
			
			//update index from current container
			var currentContainerNode = containerNodes[$scope.container.code];			
			currentContainerNode.indexFromCurrent = 0;			
			updateIndexForParents(currentContainerNode.parentNodes, currentContainerNode.indexFromCurrent);
			updateIndexForChildren(currentContainerNode.childNodes, currentContainerNode.indexFromCurrent);
			
			
			var graphElements =  computeGraphElements(containerNodes);
			initCytoscape(graphElements);
		});
		
		

	}
	
}]);
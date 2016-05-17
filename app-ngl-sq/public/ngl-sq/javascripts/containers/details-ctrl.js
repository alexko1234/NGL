"use strict";

angular.module('home').controller('DetailsCtrl', ['$scope', '$http', '$q', '$routeParams', 'mainService', 'tabService', 'lists', 'messages', 
                                          function($scope,$http,$q,$routeParams,mainService,tabService,lists,messages){
	
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
			console.info($scope.container);
			// Verification...
			
			if($scope.container){
				initTreeOfLife($scope.container);		
			}else{
				console.info("Aucun container sous le code: " + $routeParams.code);
			}

			if(tabService.getTabs().length == 0){			
				tabService.addTabs({label:Messages('containers.tabs.search'),href:jsRoutes.controllers.containers.tpl.Containers.home("search").url,remove:true});
				tabService.addTabs({label:$scope.container.code,href:jsRoutes.controllers.containers.tpl.Containers.home($scope.container.code).url,remove:true});
				tabService.activeTab($scope.getTabs(1));
			}
			
			$scope.lists.refresh.containerSupportCategories();
			$scope.lists.refresh.experimentTypes({categoryCodes:["transformation"], withoutOneToVoid:false});
					
			if(undefined == mainService.get('containerActiveTab')){
				mainService.put('containerActiveTab', 'general');
			}
		});
	}
	init();
	
	
	var initTreeOfLife = function(currentContainer){
		if(!angular.isUndefined(currentContainer.treeOfLife.paths)){
			//extract parent container codes
			var codes = {parentContainerCodes : []};
			angular.forEach(currentContainer.treeOfLife.paths, function(path){
				path = path.substring(1);
				this.parentContainerCodes = this.parentContainerCodes.concat(path.split(","));
			}, codes);
		}

		
		var promises = [];
		promises.push($http.get(jsRoutes.controllers.containers.api.Containers.list().url, {params : {codes:codes.parentContainerCodes}}));
		promises.push($http.get(jsRoutes.controllers.containers.api.Containers.list().url, {params : {treeOfLifePathRegex:currentContainer.code}}));
		
		
		$q.all(promises).then(function(results){
			var containerNodes = {};
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
								throw 'error not found node for '+parentContainer.code;
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
			
			
			//find the first nodes
			var pathParent=[];
			var findFirstNode = function(currentContainer){
				angular.forEach(containerNodes, function(containerNode){
					// run on roots parents
					angular.forEach(currentContainer.parentNodes, function(parentNode){		
						if(parentNode === containerNode){
							pathParent.push(containerNode);
							if(pathParent[pathParent.length-1].parentNodes.length > 0){
								findFirstNode(containerNode);
							}else{
								this[pathParent[0].container.code] = pathParent;
								pathParent = [];
							}
						}
					}, $scope.pathsParentsCurrent);
				});	
			};
			
			var pathChildren=[];
			var findLastNode = function(currentContainer){
				angular.forEach(containerNodes, function(containerNode){
					// run through roots children
					angular.forEach(currentContainer.childNodes, function(childNode){
						if(childNode === containerNode){
							pathChildren.push(containerNode);
							if(pathChildren[pathChildren.length-1].childNodes.length > 0){
								findLastNode(containerNode)
							}else{
								this[pathChildren[0].container.code] = pathChildren;
								pathChildren = [];
							}
						}
					}, $scope.pathsChildrenCurrent);
				});
			}
			
			
			// Initialisation of $scope.pathsFromCurrent
			$scope.pathsParentsCurrent = {};
			$scope.pathsChildrenCurrent = {};
			findFirstNode(currentContainerNode);
			findLastNode(currentContainerNode);
			
			
			// Add the currentContainer to the map
			$scope.firstNode = currentContainerNode;
			
			
			console.log(containerNodes);
			console.log($scope.pathsParentsCurrent);
			console.log($scope.pathsChildrenCurrent);
			console.log($scope.firstNode);
			//console.log(paths);
			
			console.log("3");
			
		});
		
		console.log("1");
		
	}	
	
}]);
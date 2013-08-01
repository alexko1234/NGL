"use strict";

function SearchCtrl($scope, datatable) {
	$scope.archive = 2; //default only need archive
	
	$scope.datatableConfig = {
			search : { 
				url:jsRoutes.controllers.archives.api.ReadSets.list()
			},
			pagination : {
				mode : 'local'
			},
			order : {
				mode : 'local',
				by:'date'
			}
			
	};
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.search();
		$scope.datatable.search({archive:$scope.archive});
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('home');
			$scope.addTabs({label:Messages('archives.menu.search'),href:jsRoutes.controllers.archives.tpl.ReadSets.home().url,remove:false});
			
			$scope.activeTab(0);
		}
	}
	
	/* 
      call by the init above and the search() to select the type of archives to vizualise (0,1,2)
	 */
	$scope.search = function(){
		$scope.datatable.search({archive:$scope.archive});
	}
	
};

SearchCtrl.$inject = ['$scope', 'datatable'];

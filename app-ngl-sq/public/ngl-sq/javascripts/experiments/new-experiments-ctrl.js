function ListNewCtrl($scope, datatable) {
	
	$scope.datatableConfig = {
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local',
				active:true,
				by:'containerInputCode'
			},
			remove:{
				active:true,
				mode:'local',
				callback : function(datatable){
					$scope.basket.reset();
					$scope.basket.add(datatable.allResult);
				}
			},
			messages:{
				active:true
			}
		};
	
	$scope.newExperiment = function(){
		if(this.basket.length() > 0 && $scope.getTabs().length === 2){
			$scope.addTabs({label:$scope.form.experimentTypes.selected.name+" config",href:"/experiments/create/"+$scope.getForm().experimentTypes.selected.code,remove:false});
		}
	}
	
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.basket = $scope.getBasket();
		$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
		$scope.form = $scope.getForm();
	}
};
ListNewCtrl.$inject = ['$scope', 'datatable'];
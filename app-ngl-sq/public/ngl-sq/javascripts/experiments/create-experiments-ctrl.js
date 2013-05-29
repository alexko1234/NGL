function CreateNewCtrl($scope, datatable) {
	$scope.init = function(){
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.basket = $scope.getBasket();
		$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
	}
}
CreateNewCtrl.$inject = ['$scope', 'datatable'];
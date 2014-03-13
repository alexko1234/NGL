function ListNewCtrl($scope, datatable) {
	
	$scope.datatableConfig = {
			columns:[{
				"header":Messages("experiments.table.code"),
				"property":"code",
				"order":true,
				"type":"text"
			},
			{
				"header":Messages("experiments.table.projectCodes"),
				"property":"projectCodes",
				"order":true,
				"type":"text"
			},
			{
				"header":Messages("experiments.table.sampleCodes"),
				"property":"sampleCodes",
				"order":true,
				"type":"text"
			}
			],
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
			},
			otherButtons:{
				active:true,
				template:'<button class="btn btn-info" ng-disabled="basket.length()==0" ng-click="newExperiment();" data-toggle="tooltip" title="">'
						+'{{form.experimentType.name}}</button>'
			}
		};
	
	$scope.newExperiment = function(){
		if(this.basket.length() > 0 && $scope.getTabs().length === 2){
			$scope.addTabs({label:$scope.form.experimentType.name+" config",href:"/experiments/create/"+$scope.getForm().experimentType.code,remove:false});
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
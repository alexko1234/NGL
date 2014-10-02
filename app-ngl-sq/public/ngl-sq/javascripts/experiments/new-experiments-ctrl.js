angular.module('home').controller('ListNewCtrl', ['$scope', 'datatable','mainService','tabService',function ($scope, datatable,mainService,tabService) {
	
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
			},
			{
				"header":Messages("experiments.table.volume"),
				"property":"volume",
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
						+'{{form.experimentType}}</button>'
			}
	};
	
	$scope.newExperiment = function(){
		if(this.basket.length() > 0 && $scope.getTabs().length === 2){
			tabService.addTabs({label:mainService.getForm().experimentType+" config",href:"/experiments/create/"+mainService.getForm().experimentType,remove:false});
		}
	};
	
	//init
	$scope.datatable = datatable($scope.datatableConfig);
	$scope.basket = $scope.getBasket();
	$scope.datatable.setData($scope.basket.get(),$scope.basket.get().length);
	$scope.form = mainService.getForm();
}]);
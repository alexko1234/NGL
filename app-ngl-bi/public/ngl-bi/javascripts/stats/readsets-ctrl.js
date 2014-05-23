"use strict";

angular.module('home').controller('StatsSearchReadSetsCtrl',['$scope', '$routeParams', 'datatable', 'mainService', 'tabService','readSetsSearchService', 'basket',
                                                              function($scope, $routeParams, datatable, mainService, tabService, readSetsSearchService, basket) { 

	var datatableConfig = {
			order :{by:'runSequencingStartDate', reverse : true},
			search:{
				url:jsRoutes.controllers.readsets.api.ReadSets.list()
			},
			hide:{
				active:true
			},
			otherButtons:{
				active:true,
				template:'<button class="btn btn-default" ng-click="addToBasket()" data-toggle="tooltip" title="'+Messages("button.query.addbasket")+'"><i class="fa fa-shopping-cart"></i> (<span ng-bind="basket.length()"/>)</button>'
			}
	};

	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('stats.page.tab.readsets.select'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets").url});
		tabService.addTabs({label:Messages('stats.page.tab.readsets.config'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-config").url});		
		tabService.addTabs({label:Messages('stats.page.tab.show'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-show").url});		
		
		tabService.activeTab(0); // desactive le lien !
	}
	
	$scope.search = function(){
		$scope.searchService.search($scope.datatable);
	};
	
	$scope.reset = function(){
		$scope.searchService.reset();
	};

	$scope.updateColumn = function(){
		$scope.searchService.updateColumn($scope.datatable);
	};
	
	$scope.addToBasket = function(){
		var query = {form : angular.copy($scope.searchService.form)};
		query.form.includes = undefined;
		query.form.excludes = undefined;
		$scope.basket.add(query);		
	};
	
	$scope.searchService = readSetsSearchService();	
	$scope.searchService.setRouteParams($routeParams);
		
	//to avoid to lost the previous search
	if(angular.isUndefined(mainService.getDatatable())){
		$scope.datatable = datatable($scope, datatableConfig);
		mainService.setDatatable($scope.datatable);
		$scope.datatable.setColumnsConfig($scope.searchService.getColumns());		
	}else{
		$scope.datatable = mainService.getDatatable();			
	}	
	
	if(angular.isUndefined(mainService.getBasket())){
		$scope.basket = basket();			
		mainService.setBasket($scope.basket);
	}else{
		$scope.basket = mainService.getBasket();
	}
	
	$scope.search();
	
	
}]);

angular.module('home').controller('StatsConfigReadSetsCtrl',['$scope', 'mainService', 'tabService', 'datatable', 'basket', 'statsConfigReadSetsService','queriesConfigReadSetsService',
                                                              function($scope, mainService, tabService, datatable, basket, statsConfigReadSetsService, queriesConfigReadSetsService) { 
	
	if(angular.isUndefined(mainService.getBasket())){
		mainService.setBasket(basket());
	}
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('stats.page.tab.readsets.select'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets").url});
		tabService.addTabs({label:Messages('stats.page.tab.readsets.config'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-config").url});		
		tabService.addTabs({label:Messages('stats.page.tab.show'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-show").url});		
		tabService.activeTab(1); // desactive le lien !
	}
	
	if(angular.isUndefined(mainService.get('statsConfigReadSetsService'))){
		$scope.statsConfigService = statsConfigReadSetsService();
		mainService.put('statsConfigReadSetsService', $scope.statsConfigService);
	}else{
		$scope.statsConfigService = mainService.get('statsConfigReadSetsService');
	}
	
	$scope.queriesConfigService = queriesConfigReadSetsService(mainService.getBasket().get());
	mainService.put('queriesConfigReadSetsService', $scope.queriesConfigService);
}]);

angular.module('home').controller('StatsShowReadSetsCtrl',['$scope',  '$http', '$q','$parse', 'mainService', 'tabService', 'statsConfigReadSetsService','queriesConfigReadSetsService',
                                                              function($scope, $http, $q, $parse, mainService, tabService, statsConfigReadSetsService, queriesConfigReadSetsService) { 
	
	if(angular.isUndefined(mainService.getHomePage())){
		mainService.setHomePage('search');
		tabService.addTabs({label:Messages('stats.page.tab.readsets.select'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets").url});
		tabService.addTabs({label:Messages('stats.page.tab.readsets.config'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-config").url});		
		tabService.addTabs({label:Messages('stats.page.tab.show'),href:statsJsRoutes.controllers.stats.tpl.Stats.home("readsets-show").url});		
		
		tabService.activeTab(2); // desactive le lien !
	}
	
	if(angular.isUndefined(mainService.get('statsConfigReadSetsService'))){
		mainService.put('statsConfigReadSetsService', statsConfigReadSetsService());
	}
	$scope.statsConfigService = mainService.get('statsConfigReadSetsService');
	
	
	if(angular.isUndefined(mainService.get('queriesConfigReadSetsService'))){
		mainService.put('queriesConfigReadSetsService', queriesConfigReadSetsService());
	}
	$scope.queriesConfigService = mainService.get('queriesConfigReadSetsService');
		
	
	var statsConfig =  	$scope.statsConfigService.getData();
	var queriesConfig =  $scope.queriesConfigService.getData();
	
	var properties = ["code"];
	for(var i = 0; i < statsConfig.length; i++){
		properties.push(statsConfig[i].property);
	}
	
	var promises = [];
	for(var i = 0; i < queriesConfig.length ; i++){
		var form = angular.copy(queriesConfig[i].form);
		form.list = true;
		form.includes = properties;
		//form.limit=3000;
		promises.push($http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url,{params:form}));			
	}
	
	$q.all(promises).then(function(results){
		var values = {r:[]};
		angular.forEach(results, function(value, key){
			this.r = this.r.concat(value.data);
			
		}, values);	
		
		var data = values.r.map(function(value){return $parse("treatments.ngsrg.default.Q30.value")(value)});
		var mean = ss.mean(data);
		var stdDev = ss.standard_deviation(data);
		
		var t = values.r.map(function(x){return {name:x.code,  y:ss.z_score($parse("treatments.ngsrg.default.Q30.value")(x), mean, stdDev)};});
		
	
	
	
	
	
/*
	var t = [{ name:"BFY_AAAOSN_1_H89E9ADXX.IND5", y:0.6592257288119633}, { name:"BFY_AACOSN_1_H89E9ADXX.IND7", y:0.9226818371293593}, 
	         { name:"BFY_AAEOSN_1_H89E9ADXX.IND9", y:0.33136923846142685}, { name:"BFY_AADOSN_1_H89E9ADXX.IND8", y:0.37235129975524806}, 
	         { name:"BFY_AABOSN_1_H89E9ADXX.IND6", y:0.2376959555041331}, { name:"BFY_AAFOSN_1_H89E9ADXX.IND10", y:0.6065345071484824}, 
	         { name:"BFY_AAHOSN_1_H89E9ADXX.IND12", y:0.9636638984231805}, { name:"BFY_AAGOSN_1_H89E9ADXX.IND11", y:-0.38288954408795256}, 
	         { name:"BFY_AAGHOSF_1_A7D4G.IND28", y:-1.653333444196277}, { name:"BFY_AACAOSF_1_A737Y.IND21", y:-2.0572994769496136},
	         { name:"BFY_AAAOSN_1_H89E9ADXX.IND5", y:0.6592257288119633}, { name:"BFY_AACOSN_1_H89E9ADXX.IND7", y:0.9226818371293593}, 
	         { name:"BFY_AAEOSN_1_H89E9ADXX.IND9", y:0.33136923846142685}, { name:"BFY_AADOSN_1_H89E9ADXX.IND8", y:0.37235129975524806}, 
	         { name:"BFY_AABOSN_1_H89E9ADXX.IND6", y:0.2376959555041331}, { name:"BFY_AAFOSN_1_H89E9ADXX.IND10", y:0.6065345071484824}, 
	         { name:"BFY_AAHOSN_1_H89E9ADXX.IND12", y:0.9636638984231805}, { name:"BFY_AAGOSN_1_H89E9ADXX.IND11", y:-0.38288954408795256}, 
	         { name:"BFY_AAGHOSF_1_A7D4G.IND28", y:-1.653333444196277}, { name:"BFY_AACAOSF_1_A737Y.IND21", y:-2.0572994769496136},
	         { name:"BFY_AAAOSN_1_H89E9ADXX.IND5", y:0.6592257288119633}, { name:"BFY_AACOSN_1_H89E9ADXX.IND7", y:0.9226818371293593}, 
	         { name:"BFY_AAEOSN_1_H89E9ADXX.IND9", y:0.33136923846142685}, { name:"BFY_AADOSN_1_H89E9ADXX.IND8", y:0.37235129975524806}, 
	         { name:"BFY_AABOSN_1_H89E9ADXX.IND6", y:0.2376959555041331}, { name:"BFY_AAFOSN_1_H89E9ADXX.IND10", y:0.6065345071484824}, 
	         { name:"BFY_AAHOSN_1_H89E9ADXX.IND12", y:0.9636638984231805}, { name:"BFY_AAGOSN_1_H89E9ADXX.IND11", y:-0.38288954408795256}, 
	         { name:"BFY_AAGHOSF_1_A7D4G.IND28", y:-1.653333444196277}, { name:"BFY_AACAOSF_1_A737Y.IND21", y:-2.0572994769496136},
	         { name:"BFY_AAAOSN_1_H89E9ADXX.IND5", y:0.6592257288119633}, { name:"BFY_AACOSN_1_H89E9ADXX.IND7", y:0.9226818371293593}, 
	         { name:"BFY_AAEOSN_1_H89E9ADXX.IND9", y:0.33136923846142685}, { name:"BFY_AADOSN_1_H89E9ADXX.IND8", y:0.37235129975524806}, 
	         { name:"BFY_AABOSN_1_H89E9ADXX.IND6", y:0.2376959555041331}, { name:"BFY_AAFOSN_1_H89E9ADXX.IND10", y:0.6065345071484824}, 
	         { name:"BFY_AAHOSN_1_H89E9ADXX.IND12", y:0.9636638984231805}, { name:"BFY_AAGOSN_1_H89E9ADXX.IND11", y:-0.38288954408795256}, 
	         { name:"BFY_AAGHOSF_1_A7D4G.IND28", y:-1.653333444196277}, { name:"BFY_AACAOSF_1_A737Y.IND21", y:-2.0572994769496136},
	         { name:"BFY_AAAOSN_1_H89E9ADXX.IND5", y:0.6592257288119633}, { name:"BFY_AACOSN_1_H89E9ADXX.IND7", y:0.9226818371293593}, 
	         { name:"BFY_AAEOSN_1_H89E9ADXX.IND9", y:0.33136923846142685}, { name:"BFY_AADOSN_1_H89E9ADXX.IND8", y:0.37235129975524806}, 
	         { name:"BFY_AABOSN_1_H89E9ADXX.IND6", y:0.2376959555041331}, { name:"BFY_AAFOSN_1_H89E9ADXX.IND10", y:0.6065345071484824}, 
	         { name:"BFY_AAHOSN_1_H89E9ADXX.IND12", y:0.9636638984231805}, { name:"BFY_AAGOSN_1_H89E9ADXX.IND11", y:-0.38288954408795256}, 
	         { name:"BFY_AAGHOSF_1_A7D4G.IND28", y:-1.653333444196277}, { name:"BFY_AACAOSF_1_A737Y.IND21", y:-2.0572994769496136},
	         { name:"BFY_AAAOSN_1_H89E9ADXX.IND5", y:0.6592257288119633}, { name:"BFY_AACOSN_1_H89E9ADXX.IND7", y:0.9226818371293593}, 
	         { name:"BFY_AAEOSN_1_H89E9ADXX.IND9", y:0.33136923846142685}, { name:"BFY_AADOSN_1_H89E9ADXX.IND8", y:0.37235129975524806}, 
	         { name:"BFY_AABOSN_1_H89E9ADXX.IND6", y:0.2376959555041331}, { name:"BFY_AAFOSN_1_H89E9ADXX.IND10", y:0.6065345071484824}, 
	         { name:"BFY_AAHOSN_1_H89E9ADXX.IND12", y:0.9636638984231805}, { name:"BFY_AAGOSN_1_H89E9ADXX.IND11", y:-0.38288954408795256}, 
	         { name:"BFY_AAGHOSF_1_A7D4G.IND28", y:-1.653333444196277}, { name:"BFY_AACAOSF_1_A737Y.IND21", y:-2.0572994769496136},
	         { name:"BFY_AAAOSN_1_H89E9ADXX.IND5", y:0.6592257288119633}, { name:"BFY_AACOSN_1_H89E9ADXX.IND7", y:0.9226818371293593}, 
	         { name:"BFY_AAEOSN_1_H89E9ADXX.IND9", y:0.33136923846142685}, { name:"BFY_AADOSN_1_H89E9ADXX.IND8", y:0.37235129975524806}, 
	         { name:"BFY_AABOSN_1_H89E9ADXX.IND6", y:0.2376959555041331}, { name:"BFY_AAFOSN_1_H89E9ADXX.IND10", y:0.6065345071484824}, 
	         { name:"BFY_AAHOSN_1_H89E9ADXX.IND12", y:0.9636638984231805}, { name:"BFY_AAGOSN_1_H89E9ADXX.IND11", y:-0.38288954408795256}, 
	         { name:"BFY_AAGHOSF_1_A7D4G.IND28", y:-1.653333444196277}, { name:"BFY_AACAOSF_1_A737Y.IND21", y:-2.0572994769496136}
	         
	         ];
	
	*/
	$scope.chart1 = {
	        chart: {
	        	zoomType : 'x',
	        	height:770
	        },
	        title: {
	        	text : 'Removing contamination : % Coli (PE)'
	        },
	        
	        xAxis: {
	        	title: {
	        		text: 'Readsets',
	        	}, 
	        	labels : {
					enabled : false,
					rotation : -75
				},
				type: "category",
				tickPixelInterval:1
	        },
	        yAxis: {
	            title: {
	                text: 'Z-score'
	            },
	            tickInterval : 2,
	            plotLines : [ {
					value : -2,
					color : 'green',
					dashStyle : 'shortdash',
					width : 2,
					label : {
						text : 'Z-score = -2'
					}
				}, {
					value : 2,
					color : 'red',
					dashStyle : 'shortdash',
					width : 2,
					label : {
						text : 'Z-score = 2'
					}
				} ]
	        },
	        series: [{type : 'column', name:'Z-Score', data:t, turboThreshold:0}]
	    };
	});	
}]);

"use strict";

/**
 * dnoisett, for filter lists of projects
 */

angular.module('commonsProjectServices', [])
	.filter('filterByCodesInStringArray', ['$filter',
	function ($filter) {
	  return function(arr, arr2) {
		if (arr && arr2) {  
			var filtered_array = []; 
			for (var i=0; i<arr.length; i++) {
				for (var j=0; j<arr2.length; j++) {
					if (arr[i].code == arr2[j]) {
						filtered_array.push(arr[i]);
						break;
					}
				}
			}
			return filtered_array;
		}
	  };
	}
	]);


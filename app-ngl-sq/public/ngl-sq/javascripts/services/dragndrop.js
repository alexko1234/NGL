angular.module('dragndropServices', []).factory('dragndropService', function($rootScope) {
	dragndropService = {
		draggedData : {},
		setDraggedData : function(data){
			this.draggedData = data;
		},
		getDraggedData : function(){
			return this.draggedData;
		}
	}

    return dragndropService;
}).directive('draggable',['dragndropService', function(dragndropService) {
    return{ 
		scope:{
			ngModel:'='
		},
    	link: function(scope, element, attrs) {
            // this gives us the native JS object
			var OPTIONS_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?(?:\s+group\s+by\s+(.*))?\s+for\s+(?:([\$\w][\$\w\d]*)|(?:\(\s*([\$\w][\$\w\d]*)\s*,\s*([\$\w][\$\w\d]*)\s*\)))\s+in\s+(.*)$/;
            var REPEAT_REGEXP = /^\s*(.+)\s+in\s+(.*?)\s*(\s+track\s+by\s+(.+)\s*)?$/;
			var el = element[0];
			var getModel = function(){
				if(attrs.ngRepeat){
					var model = attrs.ngRepeat;
					var match = model.match(REPEAT_REGEXP);
					return match[2];
				}else if(attrs.ngOptions){
					var model = attrs.ngOptions;
					var match = model.match(OPTIONS_REGEXP);
					return match[7];
				}
				
				return "";
			};
            el.draggable = true;
            el.addEventListener(
                'dragstart',
                function(e) {
                    e.dataTransfer.effectAllowed = 'move';
                    e.dataTransfer.setData('Text', this.id);// Angular internal system
					e.dataTransfer.setData('Model', getModel());
					
					dragndropService.setDraggedData(scope.ngModel);
                    this.classList.add('drag');
                    return false;
                },
                false
            );

            el.addEventListener(
                'dragend',
                function(e) {
                    this.classList.remove('drag');
                    return false;
                },
                false
            );
        }
    }}]).directive('droppable', ['dragndropService','$filter','$parse', function(dragndropService,$filter,$parse) {
      return {
        scope: {
          drop: '&', // parent
          model: '=ngModel'
        },
        link: function(scope, element) {
          // again we need the native object
          var el = element[0];
          
          el.addEventListener(
            'dragover',
            function(e) {
              e.dataTransfer.dropEffect = 'move';
              // allows us to drop
              if (e.preventDefault) e.preventDefault();
              this.classList.add('over');
              return false;
            },
            false
          );
          
          el.addEventListener(
            'dragenter',
            function(e) {
              this.classList.add('over');
              return false;
            },
            false
          );
          
          el.addEventListener(
            'dragleave',
            function(e) {
              this.classList.remove('over');
              return false;
            },
            false
          );
          
          el.addEventListener(
            'drop',
            function(e) {
              // Stops some browsers from redirecting.
              if (e.stopPropagation) e.stopPropagation();
              this.classList.remove('over');
			  
			  var draggedData = dragndropService.getDraggedData(); 
			
              //push the data to the model and call the drop callback function
				scope.$apply(function(scope) {
				
				//We check that the data is not already in the model
				if(scope.model.indexOf(draggedData) == -1){
					scope.model.push(draggedData);
				
					var fn = scope.$parent.drop;

					if ('undefined' !== typeof fn) {
						fn(e, draggedData);
					}
				}
    		 });
              
              return false;
            },
            false
          );
        }
      }
    }]);
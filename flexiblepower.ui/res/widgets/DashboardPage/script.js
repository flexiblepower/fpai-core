$(document).ready(function() {
	window.royalSlider = {
		slider: $("#royalSlider").royalSlider({"controlNavigation": "bullets",
												"fadeinLoadedSlide": false,
												"navigateByClick": false,
												"sliderDrag": false,
												"sliderTouch": false
												}).data('royalSlider'),
												
		addPage: function() {
			element = $('<div class="slide"><div class="clear"></div></div>');
			this.slider.appendSlide(element);
		},
		
		getSlide: function(ix) {
			return this.slider.slides[ix].content;
		},
		
		getWidgets: function(ix) {
			return this.getSlide(ix).children("div[id^=widget]");
		},
		
		getWidgetIds: function() {
			var currentWidgets = $('div[id^=widget]');
			var result = new Array();
			currentWidgets.each(function() {
				var id = parseInt($(this).attr('id').substring(7), 10);
				result.push(id);
			});
			return result;
		},
		
		getSlides: function() {
			return this.slider.slides;
		},
		
		getLastSlide: function() {
			return this.getSlide(royalSlider.slider.numSlides - 1);
		},
		
		removeSlide: function(e) {
			return this.slider.removeSlide(e);
		},
	
		addWidget: function(widgetId, widgetTitle) {
			var lastSlide = this.getLastSlide();
			if(lastSlide.children("div[id^=widget]").size() >= 6) {
				royalSlider.addPage();
				lastSlide = this.getLastSlide();
			}
			var widget = $('<div id="widget-' + widgetId + '" class="large_tile white"><h3>' + widgetTitle +'</h3><iframe src="/widget/' + widgetId + '/index.html"></iframe></div>');
			widget.insertBefore(lastSlide.children(".clear"));
		},
		
		updateWidgets: function(widgets) {
			var currentWidgets = $('div[id^=widget]');
			
			// Now remove widgets which aren't there anymore
			// and remove ids that are already active
			currentWidgets.each(function() {
				var id = parseInt($(this).attr('id').substring(7), 10);
				if(widgets[id]) {
					delete widgets[id];
				} else {
					$(this).remove();
				}
			});
			
			// Clean up the slides, such that each one contains 6 widgets
			for(var i = 0; i < this.slider.numSlides; i++) {
				var slide = this.getSlide(i);
				var missingWidgets = 6 - this.getWidgets(i).size();
				
				for(var j = i + 1; j < this.slider.numSlides; j++) {
					var nextSlide = this.getSlide(j);
					while(missingWidgets > 0) {
						var widget = this.getWidgets(j).first();
						if(widget.size() > 0) {
							widget.remove();
							widget.insertBefore(slide.children(".clear"));
							missingWidgets--;
						} else {
							break;
						}
					}
				}
			}
			
			// Now add missing widgets
			$.each(widgets, function(id,title) {royalSlider.addWidget(id, title)});
			
			// Clean up empty slides
			for(var i = this.slider.numSlides - 1; i > 0; i--) {
				if(this.getWidgets(i).size() == 0) {
					this.removeSlide(i);
				}
			}
			
			// If there are no slides, then show a temporary widget
			if(this.slider.numSlides == 1 && this.getWidgets(0).size() == 0) {
				var widget = $('<div id="widget--1" class="large_tile white"><h3>No widgets available</h3></div>');
				widget.insertBefore(this.getSlide(0).children(".clear"));
			}
		},
		
		update : function() {
			$.ajax("getWidgets", {
				"type": "POST",
				"data": JSON.stringify(royalSlider.getWidgetIds()),
				"dataType": "json"
			}).done(function(widgets) {
				royalSlider.updateWidgets(widgets);
				window.setTimeout(royalSlider.update, 1000);
			}).error(function(data) {
				console.log(data.responseText);
				window.setTimeout(royalSlider.update, 10000);
			})
		}
	}
	
	royalSlider.addPage();
	royalSlider.update();
});

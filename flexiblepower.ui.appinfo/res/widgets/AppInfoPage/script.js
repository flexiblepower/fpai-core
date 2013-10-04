$(document).ready(function() {
	window.royalSlider = {
		slider: $("#royalSlider").royalSlider({"controlNavigation": "bullets",
												"fadeinLoadedSlide": false,
												"navigateByClick": false,
												"sliderDrag": true,
												"sliderTouch": true
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
		
		getSlides: function() {
			return this.slider.slides;
		},
		
		getLastSlide: function() {
			return this.getSlide(royalSlider.slider.numSlides - 1);
		},
		
		removeSlide: function(e) {
			return this.slider.removeSlide(e);
		},
		
		createWidget: function(widgetId, content) {
			content = '<div id="widget-' + widgetId + '" class="large_tile white">';
			content += '<h3>' + content.appName + '</h3>';
			content += '<div class="overflow">'
			content += '<p class="description">' + content.appDescription + '</p>';
			content += '<br />';
			for(bundle in content.components) {
				content += '<p>' + bundle + '</p>'
			}
			content += '</div></div>';
			return $(content);
		},
	
		addWidget: function(widgetId, content) {
			var lastSlide = this.getLastSlide();
			if(lastSlide.children("div[id^=widget]").size() >= 6) {
				royalSlider.addPage();
				lastSlide = this.getLastSlide();
			}
			var widget = this.createWidget(widgetId, content);
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
			$.each(widgets, function(id, content) {royalSlider.addWidget(id, content)});
			
			// Clean up empty slides
			for(var i = this.slider.numSlides - 1; i > 0; i--) {
				if(this.getWidgets(i).size() == 0) {
					this.removeSlide(i);
				}
			}
			
			// If there are no slides, then show a temporary widget
			if(this.slider.numSlides == 1 && this.getWidgets(0).size() == 0) {
				var widget = $('<div id="widget--1" class="large_tile white"><h3>No apps installed</h3></div>');
				widget.insertBefore(this.getSlide(0).children(".clear"));
			}
		},
		
		update : function() {
			$.ajax("getApps", {
				"type": "POST",
				"data": {},
				"dataType": "json"
			}).done(function(widgets) {
				royalSlider.updateWidgets(widgets);
				window.setTimeout(royalSlider.update, 5000);
			}).error(function() {
				window.setTimeout(royalSlider.update, 15000);
			})
		}
	}
	
	royalSlider.addPage();
	royalSlider.update();
});

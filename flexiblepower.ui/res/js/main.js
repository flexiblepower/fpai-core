$(document).ready(function(){

	// set dynamic width usernav dropdown to length username
	$('header nav').css('width', ($('#usernav a').width()+48) + 'px');
	
	
	// toggle usernav
	$('#usernav > a').click(function(){
	
		if($(this).hasClass('active')){
			$('#usernav nav').hide();
			$(this).removeClass('active');
		} else {
			$('#usernav nav').show();
			$(this).addClass('active');
		}
		
	});
	
	/*$("#slides").slides({
		generatePagination: false
	});*/
	
	// only for dashboard
	if($('#dashboardroot').length > 0){
	
		// http://omnipotent.net/jquery.sparkline
		var myvalues = [10,8,5,7,4,4,1];
        $('#recovery_graph').sparkline(myvalues, {type: 'line', barColor: 'green', width: '290px', height: '82px', lineColor: '24c513', fillColor: false });
	
		// dashboard tiles draggable
		/*$('#dashboard').sortable({
			placeholder: "placeholder",
			cursor: 'move',
			cancel: '.clean',
			stop: function(event, ui) {
				// save positions of the tiles
			}
		});*/
		
		// set countdown begin time
		$("#washer_countdown").countdown({
			date : 'july 30, 2013',
			leadingZero : true,
			htmlTemplate : '<span class="display" id="hours">%h</span><span class="point"></span><span class="display" id="min">%i</span><span class="point"></span><span class="display" id="sec">%s</span>',
			onComplete: function( event ){
				// dishwasher finished..
			}
		});
		
		// slider
		$("#comfort_slider").slider({
			value: 50, // set saved value, get it with ajax..
			stop: function(event, ui) {
				// save slider value ui.value with ajax..
			}
		});
		
		// meter
		$('#meter1').knob();
		
		$('.del').click(function(){
			var q = confirm('Weet u zeker dat u deze app wilt verwijderen?');
			if(q){
				$parent = $(this).parent();
				var app_id = $parent.attr('id').replace('app_', '');	
				
				/*
				// remove app in db
				$.get('somescript.php', { action: 'remove_app', id: app_id }, function(){
					// succes fade app out and remove out dom
					$parent.fadeOut('fast', function(){
						$parent.remove();
					});
				});
				*/
				
				// demo ..
				$parent.fadeOut('fast', function(){
					$parent.remove();
				});
				
			}
		});
	}

	$('#dashboardroot .royalSlider').royalSlider({
		controlNavigation: 'bullets',
		fadeinLoadedSlide: false,
		navigateByClick: false,
		sliderDrag: false
	});
	
	$('footer .royalSlider').royalSlider({
		arrowsNavAutoHide: false,
		navigateByClick: false,
		sliderDrag: false
	});	
	
	// only for devices
	if($('#devices').length > 0){
	
		// devices tiles draggable
		$('#devices').sortable({
			placeholder: "placeholder",
			cursor: 'move',
			stop: function(event, ui) {
				// save positions of the tiles
			}
		});	
		
	}
	
	$('#save_device').click(function(){
		var form_data = $('#device_form').serialize();
		alert('opslaan met ajax');
		$.post('somescript.php?action=save_device_info&'+form_data, function(data) {
			alert('Appraat gegevens opgeslagen');
		});		
		
	});

		
});

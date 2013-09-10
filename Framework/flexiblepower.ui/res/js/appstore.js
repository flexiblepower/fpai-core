$(document).ready(function() {   
	// v11
	
	function getApps() {
        $.ajax({
    	    url: "/storecontrol/index",
    	    cache: false,
    	    dataType: 'json',	    	
    	    error: function(data) {
				alert("error consulting the app store")
	      		// terminate the script
	    	},
    	    success: function(data) {
    	      if (data.status == 'pending') {
    	       	alert("App store catalogue data is pending")
    	      } else {
    	    	  for (i in data)
    	    	  {  
    	    		  if (data[i].type == "group") {
    	    			  $('.apps').append('<div class="app" id="group_'+data[i].appGroup.groupId+'">'
    		  						+ '<div class="left">'
    		  						+	'<img src="/img/apps/miele_logo.png" alt="Miele.com" />'
    								+	'</div>'
    								+	'<div class="right">'
    								+		'<div class="top">'
    								+			'<h3>' + data[i].appGroup.name + '</h3>'
    								+			'<span>&euro; ' + data[i].appGroup.price + '</span>'
    								+		'</div>'
    								+		'<p>' + data[i].appGroup.description + '</p><br>'
    								+		'<a href="javascript: void(0);" type="'+data[i].type+'" id="install_app_' + data[i].appGroup.groupId + '" class="install_app button green">Install App</a>'
    								+		'<a href="/app_detail.html?id=270ca2e4-8b67-49a7-a4c8-5900392328ff" class="button white">More info</a>'
    								+	'</div>'
    								+	'</div>');  
    	    		  }
    	    	  } 
    	    	  initClicks();
    	      }
    	    },
    	    contentType: 'application/json'
	    });
  	}	
	
	getApps();
	
	function initClicks(){
	
		$('.install_app').click(function(){
			
			var item_id = $(this).attr('id').replace('install_app_', '');
			var item_type = $(this).attr('type');
			var item_status = false;
			
			function openInstallViewer() {
				var html = "";	
				html += "<div id=\"installation_container\">\n";
				html += "	<div id=\"installation\">\n";
				html += "		<h3>Installation of  "+item_type+"</h3>\n";
				html += "		<div class=\"progressbar\"><div class=\"complete\" style=\"width: 0px;\"></div><div class=\"failed\" style=\"width: 0px;\"></div></div>\n";
				//html += "		<p>This app is being installed<br />Wait a moment please...</p>\n\n";
				html += "		<p id=\"installation_message\"</p>\n\n";
				html += "		<p id=\"installation_feedback\"></p>\n<br><br>";
				html += "		<a href=\"javascript: void(0);\" id=\"installation_close\" class=\"button green\">Close</a>\n";
				html += "	</div>\n";
				html += "</div>\n";
				
				$('#content').prepend(html);
				
				$('#installation_close').click(function(){
					complete(item_status);
					$('#installation_container').fadeOut('fast', function(){
						$('#installation_container').remove();
					});
				});
				
				$('#installation_message').text("This app is being installed, wait a moment please...");
				$('#installation_close').text("Cancel");
			}
			
			
			function show_progress() {
				// progressbar
				var width = 0;
				var procent = 0;
				var phase = 0;
				var confirm_started = false;
				var installer = setInterval(function(){
					$.ajax({
			    	    url: "/storecontrol/status?type="+item_type+"&id=" + item_id, // added type
			    	    cache: false,
			    	    dataType: 'json',
						error: function(data) {
							// terminate the script
							clearInterval(installer);  
		    	    	},
			    	    success: function(data) {
			    	    	procent = data.step * 100 / data.maxStep;
			    	    	phase = data.phase;
			    	    	$('#installation_feedback').text(data.state).text(data.comment);
			    	    	width = procent * 4;
							if(width >= 400){
								clearInterval(installer); 
								$('body').unbind('click'); 
								if (phase>0) {
									$('#installation .complete').css('width', 400);
									$('#installation_message').text("This app is now correctly installed.");
									$('#installation_close').text("Ok");
									item_status=true;
									//confirm("App installation done");
								}
								else if (phase<0) {
									$('#installation .complete').css('width', 0);
									$('#installation .failed').css('width', 400);
									$('#installation_message').text("This app installation failed.");
									$('#installation_close').text("Cancel");
									item_status=false;
									//confirm("App installation failed");
								}
								
							} else {
								$('#installation .complete').css('width', width + 'px');
							}
			    	    },
			    	    contentType: 'application/json'
				    });
				}, 500); // check every 0,5 second
	
				// clicking on page not allowed
				$('body').bind('click', function(e){ e.preventDefault(); });		
			}
			
			/*
			function confirm_rights() {
				var comborights_answerfromdialog;
				//  request pending rights questions => json ComboRights
				$.ajax({
					url: "/storecontrol/pending/?type="+item_type+"&id=" + item_id,
					cache: false,
					dataType: 'json',
					error: function(comborights) {
						alert("error getting the pending rights: " + comborights)
						// terminate the script
					},
					success: function(comborights) {
						// have a user dialog on the comborights and receive an answer back (again a comborights structure with maybe different answers filled in
						comborights_answerfromdialog = dialog_rights(comborights);
						grant_rights(comborights_answerfromdialog);
						
					},
					contentType: 'application/json'
				});
				
			}
			*/
			
			function complete(status) {
				$.ajax({
					url: "/storecontrol/complete/?type="+item_type+"&id=" + item_id+"&result=" + status,
					cache: false,
					dataType: 'json',
					error: function(data) {
						},
					success: function(data) {	
					},
					contentType: 'application/json'
				});
			}
			
			
			function realpurchase(combouserrights) {
				openInstallViewer();
				$.ajax({
					type: "POST",
					url: "/storecontrol/purchase", 
					cache: false,
					dataType: 'json',
					data : JSON.stringify(combouserrights),
					error: function(data) {
						alert("error purchasing " + data);
					},
					success: function(data) {
						//alert("purchase started");
						show_progress();
					},
					contentType: 'application/json'
				});
				/*
				$.ajax({
					url: "/storecontrol/purchase/?type="+item_type+"&id=" + item_id,
					cache: false,
					dataType: 'json',
					error: function(data) {
						alert("error: " + data);
					},
					success: function(data) {
						show_progress();
					},
					contentType: 'application/json'
				});
				*/
				
				
			}
			
			
			/**
			 * return updated crights to indicate purchase intent, or null of no purchase intent
			 */
			function inspectdialog(productinfo) {
				var confirm_response = true;
				var qs = 0;
				var display = "";
				//alert("inspectdialog starting");	
				for (qs in productinfo.comboRights.questions) {
					//alert("building for question "+display);	
					var q = productinfo.comboRights.questions[qs];
					if (q.show) {
						display+="["+q.question+" ("+q.answer+")] "; 
					}
				}
				//alert("prompting for "+display);	
				// simple prompt now, so yes=purchase and approve all rights, cancel = no purchase
				confirm_response = confirm(display);
				if (confirm_response) {
					//alert("response");
					for (i in productinfo.comboRights.questions) {
						var q = productinfo.comboRights.questions[i];
						if (q.show) {
							q.answer = true;
						}
					}
					//alert("returning comborights from inspectdialog");
					return productinfo.comboRights;
				}
				else {
					//alert("cancel from inspectdialog");
					return null;
				}
			}
			
			function inspectproduct(productinfo) {
				// TODO -- make nice dialog here, for now showing all questions in a prompt to grant/deny all
				var confirm_response = true;
				var answerrights;
				//alert("inspectproduct starting");	
				answerrights = inspectdialog(productinfo);
				//alert("answer rights now known from inspectdialog");
				// null means do not purchase, else we want to purchase with the answers to the rights questions
				if (answerrights!=null) {
				   //alert("purchase ongoing ...");
				   realpurchase(answerrights);	
				}
				else {
				   //alert("no purchase");
				}
			}
			
			$.ajax({
				url: "/storecontrol/productinfo/?type="+item_type+"&id=" + item_id,
				cache: false,
				dataType: 'json',
				error: function(productinfo) {
					alert("error: " + productinfo);
				},
				success: function(productinfo) {
					//alert("product info received");
					inspectproduct(productinfo);
				},
				contentType: 'application/json'
			});
			
		});
	}

});

$(window).load(function() {
	w = new widget("update", 1000, function(data) {
		$("#loading").detach();
		$("p").show();
		$("#simulation_parameters").show();
		$(".error").hide();
		$("#time").text(data.time);
		
		var startBtn = $("#startstop");
		var pauseBtn = $("#pauseunpause");
		
		if(data.state == "RUNNING" || data.state == "STOPPING") {
			startBtn.text("Stop").removeAttr('disabled');
			pauseBtn.text("Pause").removeAttr('disabled');
			$("#period").attr('disabled', 'disabled');
		} else if(data.state == "PAUSED") {
			startBtn.text("Stop").removeAttr('disabled');
			pauseBtn.text("Unpause").removeAttr('disabled');
			$("#period").attr('disabled', 'disabled');
		} else { // data.state == "STOPPED"
			startBtn.text("Start").removeAttr('disabled');
			pauseBtn.text("Pause").attr('disabled', 'disabled');
			$("#period").removeAttr('disabled');
		}
	});
	
	$("input[name='clock_type']").change(function() {
		w.call("setType", this.value, w.callback);
	});

	$("#startstop").click(function() {
		var btn = $(this);
		var parts = $("#datepicker").val().split('-');
		if(parts.length != 3) alert("Invalid date entered. Use dd-mm-yyyy format.");
		var date = new Date(parts[2], parts[1]-1, parts[0]); // months are 0-based
		var startTime = date.getTime()+1000*60*60*10;
		if(isNaN(startTime)) startTime = 1325376000000;
		var stopTime = 0;
		switch($("#period").val()) {
			case "year":  stopTime = startTime + 31556926000; break;
			case "month": stopTime = startTime + 2629743830;  break;
			case "week":  stopTime = startTime + 604800000;   break;
			default:      stopTime = startTime + 86400000;    break;
		}
		if(btn.text() == "Start") {
			w.call("startSimulation", {"startTime":startTime, "stopTime":stopTime, "speedFactor":$("#speed").val()}, w.callback);
		} else {
			w.call("stopSimulation", {}, w.callback);
		}
		btn.attr('disabled', 'disabled');
		$("#pauseunpause").attr('disabled', 'disabled');
	});
	
	$("#pauseunpause").click(function() {
		var btn = $(this);
		if(btn.text() == "Pause") {
			w.call("pauseSimulation", {}, w.callback);
		} else {
			w.call("unpauseSimulation", {}, w.callback);
		}
		btn.attr('disabled', 'disabled');
		$("#startstop").attr('disabled', 'disabled');
	});
	
	$("#speed").change(function () {
		w.call("changeSpeedFactor", {"startTime":0, "stopTime":0, "speedFactor":$("#speed").val()}, w.callback);
	});
});
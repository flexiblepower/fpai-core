function widget(updateMethod, timeout, callback) {
	me = this;
	this.callback = callback;

	this.error = function(msg) { /* Do nothing by default */ };
	this.update = function() {
		$.ajax(updateMethod, {
			dataType: "json",
			success: function(data) {
				me.callback(data);
				window.setTimeout(me.update, timeout);
			},
			error: function(msg) {
				me.error(msg.statusText);
			}
		});
	};
	this.update();

	this.call = function(method, data, callback) {
		$.ajax(method, {
			type: "POST",
			dataType: "json",
			data: JSON.stringify(data),
			success: callback
		});
	};
}

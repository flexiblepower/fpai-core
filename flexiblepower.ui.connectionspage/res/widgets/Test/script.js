jsPlumb.ready(function() {
	jsPlumb.setContainer(document.querySelectorAll("div#main"))
	
	var endpointProps = {
        endpoint: "Dot",
        paintStyle: {
            strokeStyle: "rgba(122, 176, 44, 0.8)",
            fillStyle: "transparent",
            radius: 10,
            lineWidth: 3
        },
        isSource: true,
        isTarget: true,
        connector: [ "Flowchart", { 
        	stub: 20, 
        	gap: 10, 
        	cornerRadius: 5, 
        	alwaysRespectStubs: false } ],
        connectorHoverStyle: {
            lineWidth: 4,
            strokeStyle: "#216477",
            outlineWidth: 2,
            outlineColor: "white"
        },
        dragOptions: {},
        anchor:["Continuous", { faces:[ "bottom", "top" ] } ]
	};
	
	jsPlumb.batch(function() {
		jsPlumb.addEndpoint("powerMatcher", endpointProps, {
			uuid: "powerMatcher:timeshifter",
			label: "timeshifter"
		});
		jsPlumb.addEndpoint("dishwasher-manager", endpointProps, {
			uuid: "dishwasher-manager:driver",
			label: "driver"
		});
		jsPlumb.addEndpoint("dishwasher-manager", endpointProps, {
			uuid: "dishwasher-manager:controller",
			label: "controller"
		});
		jsPlumb.addEndpoint("dishwasher-driver", endpointProps, {
			uuid: "dishwasher-driver:manager",
			label: "manager"
		});
		
		jsPlumb.draggable(document.querySelectorAll(".node"), { grid: [20, 20] });
	})
	
});
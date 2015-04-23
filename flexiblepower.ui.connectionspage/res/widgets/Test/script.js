jsPlumb.ready(function() {
	jsPlumb.setContainer(document.querySelectorAll("div#main"))
	
	jsPlumb.registerEndpointTypes({
		"disabled": { paintStyle: {
			strokeStyle: "red"
		}}
	});
	
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
		var ep = null;
		
		ep = jsPlumb.addEndpoint("powerMatcher", endpointProps, {
			uuid: "powerMatcher:timeshifter",
			label: "timeshifter"
		});
		ep.setParameter("potentialTargets", ["dishwasher-manager:controller"]);
		
		ep = jsPlumb.addEndpoint("dishwasher-manager", endpointProps, {
			uuid: "dishwasher-manager:driver",
			label: "driver"
		});
		ep.setParameter("potentialTargets", ["dishwasher-driver:manager"]);
		
		ep = jsPlumb.addEndpoint("dishwasher-manager", endpointProps, {
			uuid: "dishwasher-manager:controller",
			label: "controller"
		});
		ep.setParameter("potentialTargets", ["powerMatcher:timeshifter"]);
		
		ep = jsPlumb.addEndpoint("dishwasher-driver", endpointProps, {
			uuid: "dishwasher-driver:manager",
			label: "manager"			
		});
		ep.setParameter("potentialTargets", ["dishwasher-manager:driver"]);
		
		jsPlumb.draggable(document.querySelectorAll(".node"), { grid: [20, 20] });
		
		jsPlumb.bind("beforeDrag", function(params) {
			jsPlumb.batch(function() {
				jsPlumb.selectEndpoints().setEnabled(false);
				jsPlumb.selectEndpoints().setType("disabled");
				
				params.endpoint.clearTypes();
				
				var targets = params.endpoint.getParameter("potentialTargets");
				for(ix in targets) {
					var target = jsPlumb.getEndpoint(targets[ix]);
					target.setEnabled(true);
					target.clearTypes();
				}
			});
		});
		
		jsPlumb.bind("connectionDragStop", function(params) {
			jsPlumb.selectEndpoints().setEnabled(true);
			jsPlumb.selectEndpoints().each(function(ep) {ep.clearTypes();});
		});
	});
	
});
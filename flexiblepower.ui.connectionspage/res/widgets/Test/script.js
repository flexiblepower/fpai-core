jsPlumb.ready(function() {
	var instance = jsPlumb.getInstance({
//		ConnectionOverlays: [
//            [ "Label", { label: "FOO", id: "label", cssClass: "connectionLabel", enabled: false } ]
//		],
		Container: "main"
	});
	
	instance.registerEndpointType("disabled", { paintStyle: { strokeStyle: "red" } });
	
	var _createTextNode = function(text, isSmall) {
		var node = document.createElement("p");
		if(isSmall) {
			node.className = "small";
		}
		node.innerHTML = text;
		return node;
	}
	
	var _addEndpoint = function(endpointId, ports) {
		// First check if a node with that ID already exists, then just return
		if(document.getElementById(endpointId) != null) {
			return;
		}
		
		// Parse the id to split it up into a package, a name and an id
		var pkgName = "", epName = endpointId, epId = "";
		
		var data = endpointId.match(/^((?:[a-z]+\.)*)([a-zA-Z]+)(?:\.([^\.]+))$/);
		if(data != null) {
			pkgName = data[1];
			epName = data[2];
			epId = data[3];
		} else {
			data = endpointId.match(/^((?:[^\.]+\.)*)([^\.]+)$/);
			if(data != null) {
				pkgName = data[1];
				epName = data[2];
				epId = "";
			}
		}
		
		// Now create the node
		var node = document.createElement("div");
		node.className = "node";
		node.id = endpointId;
		node.appendChild(_createTextNode(pkgName, true));
		node.appendChild(_createTextNode(epName, false));
		node.appendChild(_createTextNode(epId, true));
		node.style.left = (Math.random() * 1000) + "px";
		node.style.top = (Math.random() * 500) + "px";
		
		// Add the node to the document
		document.getElementById("main").appendChild(node);
				
		// Now create the ports
		for(portId in ports) {			
			var ep = instance.addEndpoint(endpointId, {
		        endpoint: "Dot",
		        isSource: true,
		        isTarget: true,
		        connector: [ "Flowchart", { stub: 20, gap: 10, cornerRadius: 5} ],
		        paintStyle: {
		            strokeStyle: "#216477",
		            fillStyle: "transparent",
		            radius: 10,
		            lineWidth: 3
		        },
		        hoverPaintStyle: {
		            fillStyle: "#216477"
		        },
		        anchor: ["Continuous", { faces:[ "bottom", "top" ] } ],
		        uuid: endpointId + ":" + portId,
		        label: portId,
		        parameters: { potentialTargets: ports[portId] },
			});
		}
	}
	
	instance.batch(function() {
		_addEndpoint("net.powermatcher.fpai.controller.PowerMatcherController.auctioneer", {
			timeshifter: ["org.flexblepower.manager.dishwasher.DishwasherManager.1:controller"],
			buffer: [],
			unconstrained: [],
			uncontrolled: []
		});
		_addEndpoint("org.flexblepower.manager.dishwasher.DishwasherManager.1", {
			controller: ["net.powermatcher.fpai.controller.PowerMatcherController.auctioneer:timeshifter"],
			driver: ["org.flexiblepower.driver.dishwasher.DishwasherDriver.1:manager"]
		});
		_addEndpoint("org.flexiblepower.driver.dishwasher.DishwasherDriver.1", {
			manager: ["org.flexblepower.manager.dishwasher.DishwasherManager.1:driver"]
		});
		
		instance.draggable(document.querySelectorAll(".node"), { grid: [200, 100] });
		
		instance.bind("beforeDrag", function(params) {
			instance.batch(function() {
				instance.selectEndpoints().setEnabled(false);
				instance.selectEndpoints().setType("disabled");
				
				params.endpoint.clearTypes();
				
				var targets = params.endpoint.getParameter("potentialTargets");
				for(ix in targets) {
					var target = instance.getEndpoint(targets[ix]);
					target.setEnabled(true);
					target.clearTypes();
				}
			});
		});
		
		instance.bind("connectionDragStop", function(params) {
			instance.selectEndpoints().setEnabled(true);
			instance.selectEndpoints().each(function(ep) {ep.clearTypes();});
		});
	});
	
});
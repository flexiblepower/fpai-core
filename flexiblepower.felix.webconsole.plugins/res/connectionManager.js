cy = cytoscape({
  container: document.getElementById('cy'),
  selectionType: 'single', // (isTouchDevice ? 'additive' : 'single'),
//  layout: {
//	    name: 'circle',
//	    padding: 10
//	  },
  layout: { name: 'breadthfirst', fit: true },
  zoom: 1,
  minZoom: 0,
  maxZoom: 100,
  zoomingEnabled: true,
  userZoomingEnabled: true,
  pan: { x: 0, y: 0 },
  panningEnabled: true,
  userPanningEnabled: true,
  autoungrabifyNodes: false,
  hideEdgesOnViewport: false,
  hideLabelsOnViewport: false,
  textureOnViewport: false,
  
  style: [
          {
            selector: 'node',
            css: {
                'content': 'data(name)',
                'font-family': 'helvetica',
                'font-size': 14,
                'text-outline-width': 2,
                'text-outline-color': '#000',
                'text-valign': 'center',
                'color': '#fff',
                'background-color': '#0680C1',
                'border-color': '#000'
              }
          },
          {
            selector: ':selected',
            css: {
              'background-color': '#693',
              'line-color': '#693',
              'text-outline-width': 3,
              'text-outline-color': '#693'
            }
          },
          {
            selector: ':parent',
            css: {
              'background-color': '#041B50',
              'line-color': '#000',
              'text-outline-color': '#000'
            }
          },
          {
            selector: 'edge',
            css: {
              'width': 5,
              'target-arrow-shape': 'none'
              
            }
          }
        ],
 
  ready: function(){
  }
} );

$.get("/system/console/fpai-connection-manager/getGraph.json", function(json){
	console.log(json);
	cy.load(json);
	console.log("connected:");
	console.log(cy.edges("[isconnected]"));
}).fail(function(jqXHR, textStatus, errorThrown){
	console.log("error: " + textStatus + ": " + errorThrown);
});

function autoconnect(){
   console.log("Autoconnecting..");
   $.post("/system/console/fpai-connection-manager/autoconnect.json", function(result){
	   console.log(result);
   }).fail(function(jqXHR, textStatus, errorThrown){
		console.log("error: " + textStatus + ": " + errorThrown);
   });
}
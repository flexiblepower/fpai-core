package org.flexiblepower.runtime.ui.connectionspage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.ManagedEndpoint;
import org.flexiblepower.messaging.ConnectionManager.PotentialConnection;
import org.osgi.framework.BundleContext;

public class ConnectionManagerState {
    public static final int SMALL_WIDTH = 160, BIG_WIDTH = 320;
    public static final int HEIGHT = 80;
    public static final int MARGIN_HOR = 80, MARGIN_VER = 120;

    private final Set<EndpointState> endpoints;
    private final Set<String> activeConnections;

    public ConnectionManagerState(ConnectionManager connectionManager, BundleContext context) {
        endpoints = new TreeSet<EndpointState>();
        for (ManagedEndpoint ep : connectionManager.getEndpoints().values()) {
            endpoints.add(new EndpointState(context, ep));
        }

        activeConnections = new TreeSet<String>();
        for (ManagedEndpoint ep : connectionManager.getEndpoints().values()) {
            for (EndpointPort port : ep.getPorts().values()) {
                for (PotentialConnection conn : port.getPotentialConnections().values()) {
                    if (conn.isConnected()) {
                        activeConnections.add(conn.toString());
                    }
                }
            }
        }
    }

    private Set<EndpointState> nextLayer(Set<EndpointState> lastLayer, Set<EndpointState> toLayout) {
        Set<EndpointState> nextLayer = new TreeSet<EndpointState>();

        for (EndpointState eps : lastLayer) {
            for (PortState ps : eps.getPorts()) {
                for (String conn : ps.getPotentialConnections()) {
                    String endpointId = conn.substring(0, conn.lastIndexOf(':'));

                    for (Iterator<EndpointState> it = toLayout.iterator(); it.hasNext();) {
                        EndpointState otherEps = it.next();
                        if (otherEps.getId().equals(endpointId)) {
                            nextLayer.add(otherEps);
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }

        return nextLayer;
    }

    private int[] determineWidths(List<Set<EndpointState>> layout) {
        int[] result = new int[layout.size()];
        int ix = 0;
        for (Set<EndpointState> set : layout) {
            int width = -MARGIN_HOR; // Subtract 1 margin
            for (EndpointState eps : set) {
                width += eps.getWidth() + MARGIN_HOR;
            }
            result[ix++] = width;
        }
        return result;
    }

    public void performLayout() {
        Set<EndpointState> toLayout = new TreeSet<EndpointState>(endpoints);

        int leftSide = 50;
        while (!toLayout.isEmpty()) {
            List<Set<EndpointState>> layout = new ArrayList<Set<EndpointState>>();

            // First find a starting point by the one with the most ports
            int max = -1;
            EndpointState start = null;
            for (EndpointState eps : toLayout) {
                int total = eps.getPorts().size();
                if (total > max) {
                    max = total;
                    start = eps;
                }
            }

            // Determine the first layer by only this node
            Set<EndpointState> firstLayer = Collections.singleton(start);
            toLayout.remove(start);
            layout.add(firstLayer);
            Set<EndpointState> lastLayer = firstLayer;

            // Now determine the next layers
            while (true) {
                Set<EndpointState> nextLayer = nextLayer(lastLayer, toLayout);
                if (nextLayer.isEmpty()) {
                    break;
                }
                layout.add(nextLayer);
                lastLayer = nextLayer;
            }

            // Determine the maximum width and width of each layer
            int maxWidth = 0;
            int[] widths = determineWidths(layout);
            for (int x : widths) {
                if (x > maxWidth) {
                    maxWidth = x;
                }
            }

            // Now perform the real layout
            for (int ix = 0; ix < layout.size(); ix++) {
                Set<EndpointState> layer = layout.get(ix);
                int top = MARGIN_VER / 2 + ix * (MARGIN_VER + HEIGHT);
                int left = leftSide + (maxWidth - widths[ix]) / 2;

                for (EndpointState endpointState : layer) {
                    endpointState.setStyle("top", top + "px");
                    endpointState.setStyle("left", left + "px");

                    left += endpointState.getWidth() + MARGIN_HOR;
                }
            }

            leftSide += maxWidth + MARGIN_HOR;
        }
    }

    public Set<EndpointState> getEndpoints() {
        return endpoints;
    }

    public Set<String> getActiveConnections() {
        return activeConnections;
    }
}

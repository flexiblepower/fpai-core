package org.flexiblepower.runtime.ui.connectionspage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.ManagedEndpoint;
import org.flexiblepower.messaging.ConnectionManager.PotentialConnection;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.ui.Widget;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(properties = { "widget.type=full", "widget.name=connection-manager" })
public class ConnectionManagerPage implements Widget {
    public static final int SMALL_WIDTH = 160, BIG_WIDTH = 320;
    public static final int HEIGHT = 80;
    public static final int MARGIN_HOR = 80, MARGIN_VER = 120;

    public static final Set<String> BANNED_KEYS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("component.id",
                                                                                                                "component.name",
                                                                                                                "objectClass",
                                                                                                                "service.factoryPid",
                                                                                                                "service.pid",
                                                                                                                "service.id")));

    public static class PortState implements Comparable<PortState> {
        private final String id;
        private final Set<String> potentialConnections;
        private final boolean isMultiple;

        public PortState(EndpointPort port) {
            id = port.getName();
            potentialConnections = new TreeSet<String>();
            for (PotentialConnection conn : port.getPotentialConnections().values()) {
                potentialConnections.add(conn.getOtherEnd(port).toString());
            }
            isMultiple = port.getCardinality() == Cardinality.MULTIPLE;
        }

        @Override
        public int compareTo(PortState o) {
            return id.compareTo(o.id);
        }

        public String getId() {
            return id;
        }

        public Set<String> getPotentialConnections() {
            return potentialConnections;
        }

        public boolean isMultiple() {
            return isMultiple;
        }
    }

    public static class EndpointState implements Comparable<EndpointState> {
        private final String id;
        private final Set<PortState> ports;
        private final Set<String> properties;
        private final Map<String, String> style;

        public EndpointState(BundleContext context, ManagedEndpoint endpoint) {
            id = endpoint.getPid();
            ports = new TreeSet<PortState>();
            for (EndpointPort port : endpoint.getPorts().values()) {
                ports.add(new PortState(port));
            }
            properties = new TreeSet<String>();

            try {
                Collection<ServiceReference<Endpoint>> references = context.getServiceReferences(org.flexiblepower.messaging.Endpoint.class,
                                                                                                 "(service.pid=" + id
                                                                                                         + ")");
                if (references != null && references.size() > 0) {
                    ServiceReference<Endpoint> serviceReference = references.iterator().next();
                    for (String key : serviceReference.getPropertyKeys()) {
                        if (!BANNED_KEYS.contains(key)) {
                            properties.add(key + " = " + serviceReference.getProperty(key));
                        }
                    }
                }
            } catch (InvalidSyntaxException e) {
            }

            style = new HashMap<String, String>();
            style.put("width", getWidth() + "px");
            style.put("height", HEIGHT + "px");
        }

        int getWidth() {
            return ports.size() > 2 ? BIG_WIDTH : SMALL_WIDTH;
        }

        public String getId() {
            return id;
        }

        public Set<PortState> getPorts() {
            return ports;
        }

        public Set<String> getProperties() {
            return properties;
        }

        public Map<String, String> getStyle() {
            return style;
        }

        @Override
        public int compareTo(EndpointState o) {
            return id.compareTo(o.id);
        }

        void setStyle(String key, String value) {
            style.put(key, value);
        }
    }

    public static class State {
        private final Set<EndpointState> endpoints;
        private final Set<String> activeConnections;

        public State(ConnectionManager connectionManager, BundleContext context) {
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

    private ConnectionManager connectionManager;

    @Reference
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    private BundleContext bunleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        bunleContext = bundleContext;
    }

    public State currentState() {
        State state = new State(connectionManager, bunleContext);
        state.performLayout();
        return state;
    }

    public void autoconnect() {
        connectionManager.autoConnect();
    }

    @Override
    public String getTitle(Locale locale) {
        return "Connection Manager";
    }

}

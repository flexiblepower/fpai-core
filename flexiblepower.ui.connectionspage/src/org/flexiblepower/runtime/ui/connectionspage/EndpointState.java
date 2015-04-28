package org.flexiblepower.runtime.ui.connectionspage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.ManagedEndpoint;
import org.flexiblepower.messaging.Endpoint;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class EndpointState implements Comparable<EndpointState> {
    public static final Set<String> BANNED_KEYS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("component.id",
                                                                                                                "component.name",
                                                                                                                "objectClass",
                                                                                                                "service.factoryPid",
                                                                                                                "service.pid",
                                                                                                                "service.id")));

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
        style.put("height", ConnectionManagerState.HEIGHT + "px");
    }

    int getWidth() {
        return ports.size() > 2 ? ConnectionManagerState.BIG_WIDTH : ConnectionManagerState.SMALL_WIDTH;
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

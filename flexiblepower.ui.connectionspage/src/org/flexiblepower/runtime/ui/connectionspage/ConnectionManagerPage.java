package org.flexiblepower.runtime.ui.connectionspage;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.ManagedEndpoint;
import org.flexiblepower.ui.Widget;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(properties = { "widget.type=full", "widget.name=connection-manager" })
public class ConnectionManagerPage implements Widget {

    private ConnectionManager connectionManager;

    @Reference
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Map<String, Map<String, Set<String>>> currentState() {
        Map<String, Map<String, Set<String>>> result = new TreeMap<String, Map<String, Set<String>>>();
        for (ManagedEndpoint endpoint : connectionManager.getEndpoints().values()) {
            result.put(endpoint.getPid(), parsePorts(endpoint.getPorts().values()));
        }
        return result;
    }

    private Map<String, Set<String>> parsePorts(Collection<? extends EndpointPort> values) {
        Map<String, Set<String>> result = new TreeMap<String, Set<String>>();
        for (EndpointPort port : values) {
            result.put(port.getName(), port.getPotentialConnections().keySet());
        }
        return result;
    }

    @Override
    public String getTitle(Locale locale) {
        return "Connection Manager";
    }

}

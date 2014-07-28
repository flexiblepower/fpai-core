package org.flexiblepower.runtime.messaging;

import java.util.HashSet;
import java.util.Set;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;

final class EndpointPortImpl implements EndpointPort {
    private final Endpoint endpoint;
    private final Port port;

    private final Set<PortMatchImpl> possibleConnections;

    public EndpointPortImpl(Endpoint endpoint, Port port) {
        this.endpoint = endpoint;
        this.port = port;

        possibleConnections = new HashSet<PortMatchImpl>();
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public Cardinality getCardinality() {
        return port.cardinality();
    }

    @Override
    public String getName() {
        return port.name();
    }

    public Port getPort() {
        return port;
    }

    void addConnection(PortMatchImpl connection) {
        possibleConnections.add(connection);
    }

    void removeConnection(PortMatchImpl connection) {
        possibleConnections.remove(connection);
    }

    @Override
    public Set<PortMatchImpl> getMatchingPorts() {
        return possibleConnections;
    }

    @Override
    public String toString() {
        return "EndpointPort [" + endpoint.getClass().getSimpleName() + ":" + port.name() + "]";
    }
}
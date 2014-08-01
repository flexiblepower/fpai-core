package org.flexiblepower.runtime.messaging;

import java.util.HashSet;
import java.util.Set;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;

public abstract class AbstractEndpointPort implements EndpointPort {
    private final EndpointWrapper endpoint;
    private final Port port;
    private final Set<MatchingPortsImpl> matchingPorts;

    public AbstractEndpointPort(EndpointWrapper endpoint, Port port) {
        this.endpoint = endpoint;
        this.port = port;

        matchingPorts = new HashSet<MatchingPortsImpl>();
    }

    public EndpointWrapper getEndpointWrapper() {
        return endpoint;
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint.getEndpoint();
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

    @Override
    public Set<MatchingPortsImpl> getMatchingPorts() {
        return matchingPorts;
    }

    protected void addMatch(MatchingPortsImpl match) {
        matchingPorts.add(match);
    }

    protected void removeMatch(MatchingPortsImpl match) {
        matchingPorts.remove(match);
    }

    @Override
    public String toString() {
        return endpoint.getEndpoint().getClass().getSimpleName() + ":" + port.name();
    }

    protected abstract void connectTo(MatchingPortsImpl matchingPort);

    protected abstract void disconnect();

    protected abstract boolean isConnected();

    protected abstract void addMessage(Object message);

    protected abstract void handleMessage();
}

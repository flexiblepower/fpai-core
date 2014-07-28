package org.flexiblepower.runtime.messaging;

import java.util.HashSet;
import java.util.Set;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;

final class EndpointPortImpl implements EndpointPort {
    private final Endpoint endpoint;
    private final Port port;

    private final Set<MatchingPortsImpl> matchingPorts;

    public EndpointPortImpl(Endpoint endpoint, Port port) {
        this.endpoint = endpoint;
        this.port = port;

        matchingPorts = new HashSet<MatchingPortsImpl>();
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

    void addMatch(MatchingPortsImpl match) {
        matchingPorts.add(match);
    }

    void removeMatch(MatchingPortsImpl match) {
        matchingPorts.remove(match);
    }

    @Override
    public Set<MatchingPortsImpl> getMatchingPorts() {
        return matchingPorts;
    }

    @Override
    public String toString() {
        return "EndpointPort(" + endpoint.getClass().getSimpleName() + ":" + port.name() + ")";
    }
}

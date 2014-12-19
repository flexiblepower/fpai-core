package org.flexiblepower.runtime.messaging;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.PotentialConnection;
import org.flexiblepower.messaging.Port;

public class EndpointPortImpl implements EndpointPort {
    private final EndpointWrapper endpoint;
    private final Port port;
    private final SortedMap<String, PotentialConnectionImpl> potentialConnections;

    public EndpointPortImpl(EndpointWrapper endpoint, Port port) {
        this.endpoint = endpoint;
        this.port = port;

        potentialConnections = new TreeMap<String, PotentialConnectionImpl>();
    }

    @Override
    public EndpointWrapper getEndpoint() {
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

    @Override
    public PotentialConnection getPotentialConnection(String id) {
        return potentialConnections.get(id);
    }

    @Override
    public PotentialConnection getPotentialConnection(EndpointPort other) {
        return potentialConnections.get(other.toString());
    }

    @Override
    public SortedMap<String, PotentialConnectionImpl> getPotentialConnections() {
        return Collections.unmodifiableSortedMap(potentialConnections);
    }

    protected void addMatch(PotentialConnectionImpl match) {
        potentialConnections.put(getKey(match), match);
    }

    protected void removeMatch(PotentialConnectionImpl match) {
        potentialConnections.remove(getKey(match));
    }

    private String getKey(PotentialConnectionImpl match) {
        return match.getOtherEnd(this).toString();
    }

    @Override
    public String toString() {
        return endpoint.getPid() + ":" + port.name();
    }

    public boolean isConnected() {
        for (PotentialConnectionImpl match : potentialConnections.values()) {
            if (match.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public void close() {
        PotentialConnectionImpl[] conns = getPotentialConnections().values().toArray(new PotentialConnectionImpl[0]);
        for (PotentialConnectionImpl connection : conns) {
            connection.close();
            removeMatch(connection);
            connection.getOtherEnd(this).removeMatch(connection);
        }
    }
}

package org.flexiblepower.runtime.ui.connectionspage;

import java.util.Set;
import java.util.TreeSet;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.PotentialConnection;

public class PortState implements Comparable<PortState> {
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

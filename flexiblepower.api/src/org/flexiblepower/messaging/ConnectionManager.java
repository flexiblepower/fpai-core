package org.flexiblepower.messaging;

import java.util.Set;

public interface ConnectionManager {
    public interface EndpointPort {
        Endpoint getEndpoint();

        String getName();

        Cardinality getCardinality();

        Set<? extends MatchingPorts> getMatchingPorts();
    }

    public interface MatchingPorts {
        EndpointPort getEitherEnd();

        EndpointPort getOtherEnd(EndpointPort either);

        boolean isConnected();

        void connect();

        void disconnect();
    }

    Set<? extends EndpointPort> getEndpointPorts();
}

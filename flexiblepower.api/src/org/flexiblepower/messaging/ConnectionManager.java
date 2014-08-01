package org.flexiblepower.messaging;

import java.io.IOException;
import java.util.Set;

import org.flexiblepower.messaging.ConnectionManager.EndpointPort;

public interface ConnectionManager extends Iterable<EndpointPort> {
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

        void connect() throws IOException;

        void disconnect();
    }
}

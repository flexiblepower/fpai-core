package org.flexiblepower.messaging;

import java.io.IOException;
import java.util.SortedMap;

public interface ConnectionManager {
    public interface ManagedEndpoint {
        String getPid();

        EndpointPort getPort(String name);

        SortedMap<String, ? extends EndpointPort> getPorts();
    }

    public interface EndpointPort {
        String getName();

        Cardinality getCardinality();

        PotentialConnection getPotentialConnection(String id);

        PotentialConnection getPotentialConnection(EndpointPort other);

        SortedMap<String, ? extends PotentialConnection> getPotentialConnections();
    }

    public interface PotentialConnection {
        EndpointPort getEitherEnd();

        EndpointPort getOtherEnd(EndpointPort either);

        boolean isConnected();

        void connect() throws IOException;

        void disconnect();
    }

    ManagedEndpoint getEndpoint(String pid);

    SortedMap<String, ? extends ManagedEndpoint> getEndpoints();

    void autoConnect();
}
